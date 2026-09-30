package tools.jackson.dataformat.velocypack;

/** One lazy-page arena for the currently open output root. */
final class VPackOutputArena implements AutoCloseable {
    private final long maxBytes;
    private final VPackPageSupplier pageSupplier;
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
        bytes = VPackByteStore.owned(pageSupplier);
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
        bytes = VPackByteStore.owned(pageSupplier);
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

        int chunkSize = (int) Math.min(VPackByteStore.PAGE_SIZE, length);
        byte[] chunk = new byte[chunkSize];
        long sourceOffset = 0L;
        while (sourceOffset < length) {
            int count = (int) Math.min(chunk.length, length - sourceOffset);
            source.copyTo(sourceOffset, chunk, 0, count);
            append(chunk, 0, count);
            sourceOffset += count;
        }
        return bytes.range(start, length);
    }

    VPackByteStore store() {
        ensureOpen();
        return bytes;
    }

    void reset() {
        ensureOpen();
        bytes.release();
        bytes = VPackByteStore.owned(pageSupplier);
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

    private void ensureOpen() {
        if (released) {
            throw VPackErrors.write("output arena", "arena has been released");
        }
    }
}
