package tools.jackson.dataformat.velocypack;

import java.util.ArrayList;
import java.util.List;

/**
 * Random-access bytes for one framed root.  Borrowed stores retain a caller
 * slice; owned stores allocate 16 KiB pages only when bytes are appended.
 */
final class VPackByteStore implements AutoCloseable {
    static final int PAGE_SIZE = 16 * 1024;

    private final byte[] borrowed;
    private final int borrowedOffset;
    private final List<byte[]> pages;
    private long size;
    private long materializedRanges;
    private long scannedBytes;
    private long copiedBytes;
    private boolean released;

    private VPackByteStore(byte[] input, int offset, int length) {
        borrowed = input;
        borrowedOffset = offset;
        pages = new ArrayList<>();
        size = length;
    }

    private VPackByteStore() {
        borrowed = null;
        borrowedOffset = 0;
        pages = new ArrayList<>();
    }

    static VPackByteStore borrowed(byte[] input, long offset, long length) {
        int end = VPackBounds.checkedArrayRange(input, offset, length, "borrowed byte store");
        return new VPackByteStore(input, (int) offset, end - (int) offset);
    }

    static VPackByteStore owned() {
        return new VPackByteStore();
    }

    boolean isReleased() {
        return released;
    }

    /** Test-only ownership evidence; production callers do not depend on pages. */
    int pageCount() {
        return pages.size();
    }

    /** Test-only evidence that a complete range was materialized. */
    long materializedRanges() {
        return materializedRanges;
    }

    /** Test evidence for actual random-access bytes inspected. */
    long scannedBytes() { return scannedBytes; }

    /** Test evidence for bytes copied from this store into materialized values. */
    long copiedBytes() { return copiedBytes; }

    long size() {
        ensureOpen();
        return size;
    }

    byte byteAt(long index) {
        ensureRange(index, 1L, "byte access");
        scannedBytes++;
        if (borrowed != null) {
            return borrowed[borrowedOffset + (int) index];
        }
        int page = (int) (index / PAGE_SIZE);
        return pages.get(page)[(int) (index % PAGE_SIZE)];
    }

    void append(byte value) {
        ensureOwned();
        ensureCapacityForAppend(1L);
        int page = (int) (size / PAGE_SIZE);
        int inPage = (int) (size % PAGE_SIZE);
        if (inPage == 0) {
            pages.add(new byte[PAGE_SIZE]);
        }
        pages.get(page)[inPage] = value;
        size++;
    }

    void append(byte[] input, int offset, int length) {
        ensureOwned();
        if (input == null) {
            throw VPackErrors.write("owned byte store", "input is null");
        }
        if (offset < 0 || length < 0 || offset > input.length - length) {
            throw VPackErrors.write("owned byte store", "input range is invalid");
        }
        ensureCapacityForAppend(length);
        int source = offset;
        int remaining = length;
        while (remaining != 0) {
            int page = (int) (size / PAGE_SIZE);
            int inPage = (int) (size % PAGE_SIZE);
            if (inPage == 0) {
                pages.add(new byte[PAGE_SIZE]);
            }
            int count = Math.min(remaining, PAGE_SIZE - inPage);
            System.arraycopy(input, source, pages.get(page), inPage, count);
            source += count;
            remaining -= count;
            size += count;
        }
    }

    Range range(long offset, long length) {
        ensureRange(offset, length, "byte range");
        return new Range(this, offset, length);
    }

    void copyTo(long offset, byte[] output, int outputOffset, int length) {
        range(offset, length).copyTo(output, outputOffset);
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
        pages.clear();
        size = 0L;
    }

    private void ensureOwned() {
        ensureOpen();
        if (borrowed != null) {
            throw VPackErrors.write("owned byte store", "borrowed store is immutable");
        }
    }

    private void ensureCapacityForAppend(long length) {
        if (length < 0L || size > Long.MAX_VALUE - length) {
            throw VPackErrors.write("owned byte store", "size overflow");
        }
        if (size + length > Integer.MAX_VALUE - 8L) {
            throw VPackErrors.constraint("owned byte store", size,
                    "store exceeds the supported root storage limit");
        }
    }

    private void ensureRange(long offset, long length, String context) {
        ensureOpen();
        if (offset < 0L || length < 0L || offset > size || length > size - offset) {
            throw VPackErrors.malformed(context, offset, "range is outside the store");
        }
    }

    private void ensureOpen() {
        if (released) {
            throw VPackErrors.malformed("byte store", "store has been released");
        }
    }

    /** An immutable logical range; it owns no page and is safe to splice once. */
    static final class Range {
        private final VPackByteStore owner;
        private final long offset;
        private final long length;

        private Range(VPackByteStore owner, long offset, long length) {
            this.owner = owner;
            this.offset = offset;
            this.length = length;
        }

        VPackByteStore store() {
            return owner;
        }

        long offset() {
            return offset;
        }

        long length() {
            return length;
        }

        long scannedBytes() { return owner.scannedBytes(); }

        long copiedBytes() { return owner.copiedBytes(); }

        byte byteAt(long relativeOffset) {
            if (relativeOffset < 0L || relativeOffset >= length) {
                throw VPackErrors.malformed("byte range", relativeOffset,
                        "relative offset is outside the range");
            }
            return owner.byteAt(offset + relativeOffset);
        }

        void copyTo(byte[] output, int outputOffset) {
            owner.ensureOpen();
            if (output == null || outputOffset < 0
                    || length > output.length - (long) outputOffset) {
                throw VPackErrors.write("byte range", "output range is invalid");
            }
            copyTo(0L, output, outputOffset, VPackBounds.checkedInt(length, "byte range copy"));
        }

        void copyTo(long relativeOffset, byte[] output, int outputOffset, int copyLength) {
            owner.ensureOpen();
            if (relativeOffset < 0L || relativeOffset > length || copyLength < 0
                    || copyLength > length - relativeOffset) {
                throw VPackErrors.malformed("byte range", relativeOffset,
                        "copy range is outside the range");
            }
            if (output == null || outputOffset < 0
                    || copyLength > output.length - (long) outputOffset) {
                throw VPackErrors.write("byte range", "output range is invalid");
            }
            owner.copiedBytes = VPackBounds.checkedAdd(owner.copiedBytes, copyLength,
                    "byte store copy instrumentation");
            long source = VPackBounds.checkedAdd(offset, relativeOffset, "byte range copy offset");
            int destination = outputOffset;
            int remaining = copyLength;
            while (remaining != 0) {
                int count = Math.min(remaining, PAGE_SIZE - (int) (source % PAGE_SIZE));
                if (owner.borrowed != null) {
                    count = Math.min(count, owner.borrowed.length
                            - owner.borrowedOffset - (int) source);
                    System.arraycopy(owner.borrowed, owner.borrowedOffset + (int) source,
                            output, destination, count);
                } else {
                    int page = (int) (source / PAGE_SIZE);
                    System.arraycopy(owner.pages.get(page), (int) (source % PAGE_SIZE),
                            output, destination, count);
                }
                source += count;
                destination += count;
                remaining -= count;
            }
        }

        void ensureUsable() {
            owner.ensureRange(offset, length, "byte range");
        }

        byte[] toByteArray() {
            owner.materializedRanges++;
            byte[] result = new byte[VPackBounds.checkedInt(length, "byte range materialization")];
            copyTo(result, 0);
            return result;
        }
    }
}
