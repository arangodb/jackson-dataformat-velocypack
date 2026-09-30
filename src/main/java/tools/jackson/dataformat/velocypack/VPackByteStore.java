package tools.jackson.dataformat.velocypack;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Random-access bytes for one framed root.  Borrowed stores retain a caller
 * slice; owned stores allocate 16 KiB pages only when bytes are appended.
 */
final class VPackByteStore implements AutoCloseable {
    static final int PAGE_SIZE = 16 * 1024;
    private static final VarHandle LONG_LE = MethodHandles.byteArrayViewVarHandle(
            long[].class, ByteOrder.LITTLE_ENDIAN);

    private final byte[] borrowed;
    private final int borrowedOffset;
    private final List<byte[]> pages;
    private final VPackPageSupplier pageSupplier;
    private long size;
    private long materializedRanges;
    private long scannedBytes;
    private long copiedBytes;
    private boolean released;

    private VPackByteStore(byte[] input, int offset, int length) {
        borrowed = input;
        borrowedOffset = offset;
        pages = new ArrayList<>();
        pageSupplier = null;
        size = length;
    }

    private VPackByteStore(VPackPageSupplier pageSupplier) {
        borrowed = null;
        borrowedOffset = 0;
        pages = new ArrayList<>();
        this.pageSupplier = pageSupplier;
    }

    static VPackByteStore borrowed(byte[] input, long offset, long length) {
        int end = VPackBounds.checkedArrayRange(input, offset, length, "borrowed byte store");
        return new VPackByteStore(input, (int) offset, end - (int) offset);
    }

    static VPackByteStore owned() {
        return owned(null);
    }

    static VPackByteStore owned(VPackPageSupplier pageSupplier) {
        return new VPackByteStore(pageSupplier);
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

    long readLE(long index, int width) {
        VPackBounds.requireNumericWidth(width, "little-endian field");
        ensureRange(index, width, "little-endian field");
        scannedBytes = VPackBounds.checkedAdd(scannedBytes, width,
                "byte store scan instrumentation");
        byte[] data;
        int offset;
        if (borrowed != null) {
            data = borrowed;
            offset = borrowedOffset + (int) index;
        } else {
            int inPage = (int) (index % PAGE_SIZE);
            if (width <= PAGE_SIZE - inPage) {
                data = pages.get((int) (index / PAGE_SIZE));
                offset = inPage;
            } else {
                return readLESlow(index, width);
            }
        }
        if (width == 8) {
            return (long) LONG_LE.get(data, offset);
        }
        long result = 0L;
        for (int i = 0; i < width; ++i) {
            result |= (long) (data[offset + i] & 0xFF) << (8 * i);
        }
        return result;
    }

    private long readLESlow(long index, int width) {
        long result = 0L;
        for (int i = 0; i < width; ++i) {
            int page = (int) ((index + i) / PAGE_SIZE);
            int inPage = (int) ((index + i) % PAGE_SIZE);
            result |= (long) (pages.get(page)[inPage] & 0xFF) << (8 * i);
        }
        return result;
    }

    String decodeUtf8(long index, int length) {
        ensureRange(index, length, "UTF-8 value");
        copiedBytes = VPackBounds.checkedAdd(copiedBytes, length,
                "byte store copy instrumentation");
        if (length == 0) {
            return "";
        }
        if (borrowed != null) {
            return new String(borrowed, borrowedOffset + (int) index, length,
                    StandardCharsets.UTF_8);
        }
        int inPage = (int) (index % PAGE_SIZE);
        if (length <= PAGE_SIZE - inPage) {
            return new String(pages.get((int) (index / PAGE_SIZE)), inPage, length,
                    StandardCharsets.UTF_8);
        }
        byte[] bytes = new byte[length];
        int destination = 0;
        long source = index;
        int remaining = length;
        while (remaining != 0) {
            int count = Math.min(remaining, PAGE_SIZE - (int) (source % PAGE_SIZE));
            System.arraycopy(pages.get((int) (source / PAGE_SIZE)),
                    (int) (source % PAGE_SIZE), bytes, destination, count);
            source += count;
            destination += count;
            remaining -= count;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    void append(byte value) {
        ensureOwned();
        ensureCapacityForAppend(1L);
        int page = (int) (size / PAGE_SIZE);
        int inPage = (int) (size % PAGE_SIZE);
        if (inPage == 0) {
            pages.add(acquirePage());
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
                pages.add(acquirePage());
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
        if (pageSupplier != null) {
            for (byte[] page : pages) {
                pageSupplier.release(page);
            }
        }
        pages.clear();
        size = 0L;
    }

    private byte[] acquirePage() {
        byte[] page = pageSupplier == null ? new byte[PAGE_SIZE] : pageSupplier.acquire();
        if (page == null || page.length < PAGE_SIZE) {
            throw new IllegalStateException("page supplier returned a page smaller than PAGE_SIZE");
        }
        return page;
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

        long readLE(long relativeOffset, int width) {
            owner.ensureOpen();
            if (relativeOffset < 0L || relativeOffset > length || width < 0
                    || width > length - relativeOffset) {
                throw VPackErrors.malformed("byte range", relativeOffset,
                        "copy range is outside the range");
            }
            return owner.readLE(VPackBounds.checkedAdd(offset, relativeOffset,
                    "byte range read offset"), width);
        }

        String decodeUtf8(long relativeOffset, int decodeLength) {
            owner.ensureOpen();
            if (relativeOffset < 0L || relativeOffset > length || decodeLength < 0
                    || decodeLength > length - relativeOffset) {
                throw VPackErrors.malformed("byte range", relativeOffset,
                        "copy range is outside the range");
            }
            return owner.decodeUtf8(VPackBounds.checkedAdd(offset, relativeOffset,
                    "byte range decode offset"), decodeLength);
        }

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
