package tools.jackson.dataformat.velocypack;

import tools.jackson.core.io.IOContext;

/** Keeps one IOContext page available for reuse across sequential roots. */
final class VPackRecyclerPageSupplier implements VPackPageSupplier {
    private final IOContext ioContext;
    private final boolean readBuffer;
    private byte[] pooledPage;
    private boolean leased;
    private boolean closed;

    private VPackRecyclerPageSupplier(IOContext ioContext, boolean readBuffer) {
        this.ioContext = ioContext;
        this.readBuffer = readBuffer;
    }

    static VPackPageSupplier forRead(IOContext ioContext) {
        return usable(ioContext) ? new VPackRecyclerPageSupplier(ioContext, true) : null;
    }

    static VPackPageSupplier forWrite(IOContext ioContext) {
        return usable(ioContext) ? new VPackRecyclerPageSupplier(ioContext, false) : null;
    }

    private static boolean usable(IOContext context) {
        return context != null && context.bufferRecycler() != null;
    }

    @Override
    public byte[] acquire() {
        if (closed) {
            throw new IllegalStateException("page supplier has been released");
        }
        if (pooledPage == null) {
            pooledPage = readBuffer
                    ? ioContext.allocReadIOBuffer(VPackByteStore.PAGE_SIZE)
                    : ioContext.allocWriteEncodingBuffer(VPackByteStore.PAGE_SIZE);
            leased = true;
            return pooledPage;
        }
        if (!leased) {
            leased = true;
            return pooledPage;
        }
        return new byte[VPackByteStore.PAGE_SIZE];
    }

    @Override
    public void release(byte[] page) {
        if (page == pooledPage && leased) {
            leased = false;
        }
    }

    @Override
    public boolean ownsPage(byte[] page) {
        return page == pooledPage;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        if (pooledPage != null) {
            if (leased) {
                throw new IllegalStateException("pooled page is still in use");
            }
            if (readBuffer) {
                ioContext.releaseReadIOBuffer(pooledPage);
            } else {
                ioContext.releaseWriteEncodingBuffer(pooledPage);
            }
            pooledPage = null;
        }
    }
}
