package tools.jackson.dataformat.velocypack;

import java.util.ArrayDeque;

/** One lazy-page arena for the currently open output root. */
final class VPackOutputArena implements AutoCloseable {
    static final int RETAINED_PAGES = 4;
    private final long maxBytes;
    private final VPackPageSupplier pageSupplier;
    private final ArrayDeque<byte[]> freePages = new ArrayDeque<>();
    private final VPackPageSupplier storePageSupplier = new VPackPageSupplier() {
        @Override
        public byte[] acquire() {
            byte[] page = freePages.pollFirst();
            return page != null ? page : (pageSupplier == null
                    ? new byte[VPackByteStore.PAGE_SIZE] : pageSupplier.acquire());
        }

        @Override
        public void release(byte[] page) {
            if (pageSupplier != null && pageSupplier.ownsPage(page)) {
                pageSupplier.release(page);
                return;
            }
            // Store release invalidates all ranges before these bytes can be reused.
            // Pages are not zeroed; all reads are limited to the new store's size.
            if (freePages.size() < RETAINED_PAGES) {
                freePages.addLast(page);
            } else if (pageSupplier != null) {
                pageSupplier.release(page);
            }
        }
    };
    private VPackByteStore bytes;
    private long retainedBytes;
    private long copiedBytes;
    private boolean released;

    VPackOutputArena(VPackWriteConstraints constraints) {
        this(constraints, null);
    }

    VPackOutputArena(VPackWriteConstraints constraints, VPackPageSupplier pageSupplier) {
        if (constraints == null) {
            throw new NullPointerException("constraints");
        }
        maxBytes = constraints.getMaxRootValueBytes();
        this.pageSupplier = pageSupplier;
        bytes = newStore();
    }

    VPackOutputArena(long maxBytes) {
        this(maxBytes, null);
    }

    VPackOutputArena(long maxBytes, VPackPageSupplier pageSupplier) {
        if (maxBytes < 1L || maxBytes > VPackReadConstraints.MAX_ROOT_VALUE_BYTES) {
            throw new IllegalArgumentException("maxBytes is outside the root storage limit: " + maxBytes);
        }
        this.maxBytes = maxBytes;
        this.pageSupplier = pageSupplier;
        bytes = newStore();
    }

    long size() {
        ensureOpen();
        return retainedBytes;
    }

    long copiedBytes() {
        return copiedBytes;
    }

    boolean isReleased() {
        return released;
    }

    VPackByteStore.Range append(byte value) {
        ensureOpen();
        charge(1L);
        long start = retainedBytes;
        bytes.append(value);
        retainedBytes++;
        return bytes.range(start, 1L);
    }

    void checkCharge(long amount) {
        ensureOpen();
        charge(amount);
    }

    VPackByteStore.Range append(byte[] input) {
        if (input == null) {
            throw VPackErrors.write("output arena", "input is null");
        }
        return append(input, 0, input.length);
    }

    @SuppressWarnings("SameParameterValue") // This slice API intentionally accepts nonzero offsets.
    VPackByteStore.Range append(byte[] input, int offset, int length) {
        ensureOpen();
        if (input == null || offset < 0 || length < 0 || offset > input.length - length) {
            throw VPackErrors.write("output arena", "input range is invalid");
        }
        charge(length);
        long start = retainedBytes;
        bytes.append(input, offset, length);
        retainedBytes = VPackBounds.checkedAdd(retainedBytes, length, "output arena size");
        copiedBytes = VPackBounds.checkedAdd(copiedBytes, length, "output arena copy count");
        return bytes.range(start, length);
    }

    VPackByteStore.Range append(VPackByteStore.Range source) {
        if (source == null) {
            throw VPackErrors.write("output arena", "source range is null");
        }
        source.ensureUsable();
        long length = source.length();
        charge(length);
        long start = retainedBytes;
        if (length == 0L) {
            return bytes.range(start, 0L);
        }
        bytes.append(source.store(), source.offset(), length);
        retainedBytes = VPackBounds.checkedAdd(retainedBytes, length, "output arena size");
        copiedBytes = VPackBounds.checkedAdd(copiedBytes, length, "output arena copy count");
        return bytes.range(start, length);
    }

    VPackByteStore store() {
        ensureOpen();
        return bytes;
    }

    void reset() {
        ensureOpen();
        bytes.release();
        bytes = newStore();
        retainedBytes = 0L;
        copiedBytes = 0L;
    }

    @Override
    public void close() {
        release();
    }

    void release() {
        if (released) {
            return;
        }
        released = true;
        bytes.release();
        freePages.clear();
        retainedBytes = 0L;
        if (pageSupplier != null) {
            pageSupplier.close();
        }
    }

    private void charge(long amount) {
        if (amount < 0L || amount > maxBytes - retainedBytes) {
            throw VPackErrors.constraint("output arena", retainedBytes,
                    "root byte budget exceeded");
        }
    }

    private VPackByteStore newStore() {
        return VPackByteStore.owned(storePageSupplier);
    }

    private void ensureOpen() {
        if (released) {
            throw VPackErrors.write("output arena", "arena has been released");
        }
    }
}
