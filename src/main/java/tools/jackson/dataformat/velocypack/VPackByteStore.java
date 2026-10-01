package tools.jackson.dataformat.velocypack;

import java.io.IOException;
import java.io.DataInput;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Random-access bytes for one framed root.  Borrowed stores retain a caller
 * slice; owned stores allocate 16 KiB pages only when bytes are appended.
 */
final class VPackByteStore implements AutoCloseable {
    static final int PAGE_SIZE = 16 * 1024;
    private static final int PAGE_SHIFT = Integer.numberOfTrailingZeros(PAGE_SIZE);
    private static final int PAGE_MASK = PAGE_SIZE - 1;
    private static final int INITIAL_PAGE_CAPACITY = 4;
    static {
        if ((PAGE_SIZE & (PAGE_SIZE - 1)) != 0) {
            throw new ExceptionInInitializerError("PAGE_SIZE must be a power of two");
        }
    }
    private static final VarHandle LONG_LE = MethodHandles.byteArrayViewVarHandle(
            long[].class, ByteOrder.LITTLE_ENDIAN);

    private final byte[] borrowed;
    private final int borrowedOffset;
    private byte[][] pages;
    private int pageCount;
    private final VPackPageSupplier pageSupplier;
    private long size;
    private long materializedRanges;
    private long scannedBytes;
    private long copiedBytes;
    private boolean released;

    private VPackByteStore(byte[] input, int offset, int length) {
        borrowed = input;
        borrowedOffset = offset;
        pages = new byte[INITIAL_PAGE_CAPACITY][];
        pageSupplier = null;
        size = length;
    }

    private VPackByteStore(VPackPageSupplier pageSupplier) {
        borrowed = null;
        borrowedOffset = 0;
        pages = new byte[INITIAL_PAGE_CAPACITY][];
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
        return pageCount;
    }

    /** Test-only page identity accessor. */
    byte[] pageForTest(int index) {
        return pages[index];
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
        int page = (int) (index >>> PAGE_SHIFT);
        return pages[page][(int) (index & PAGE_MASK)];
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
            int inPage = (int) (index & PAGE_MASK);
            if (width <= PAGE_SIZE - inPage) {
                data = pages[(int) (index >>> PAGE_SHIFT)];
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
            int page = (int) ((index + i) >>> PAGE_SHIFT);
            int inPage = (int) ((index + i) & PAGE_MASK);
            result |= (long) (pages[page][inPage] & 0xFF) << (8 * i);
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
        int inPage = (int) (index & PAGE_MASK);
        if (length <= PAGE_SIZE - inPage) {
            return new String(pages[(int) (index >>> PAGE_SHIFT)], inPage, length,
                    StandardCharsets.UTF_8);
        }
        byte[] bytes = new byte[length];
        int destination = 0;
        long source = index;
        int remaining = length;
        while (remaining != 0) {
            int sourceOffset = (int) (source & PAGE_MASK);
            int count = Math.min(remaining, PAGE_SIZE - sourceOffset);
            System.arraycopy(pages[(int) (source >>> PAGE_SHIFT)],
                    sourceOffset, bytes, destination, count);
            source += count;
            destination += count;
            remaining -= count;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    void append(byte value) {
        ensureOwned();
        ensureCapacityForAppend(1L);
        int page = (int) (size >>> PAGE_SHIFT);
        int inPage = (int) (size & PAGE_MASK);
        if (inPage == 0) {
            addPage(acquirePage());
        }
        pages[page][inPage] = value;
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
        int page = (int) (size >>> PAGE_SHIFT);
        int inPage = (int) (size & PAGE_MASK);
        byte[] pageData = null;
        while (remaining != 0) {
            if (inPage == 0) {
                page = pageCount;
                pageData = acquirePage();
                addPage(pageData);
            } else {
                pageData = pages[page];
            }
            int count = Math.min(remaining, PAGE_SIZE - inPage);
            System.arraycopy(input, source, pageData, inPage, count);
            source += count;
            remaining -= count;
            size += count;
            page++;
            inPage = 0;
        }
    }

    /** Appends a string marker and UTF-8 payload directly into owned pages. */
    void appendStringFrame(byte[] payload, int offset, int length) {
        ensureOwned();
        if (payload == null || offset < 0 || length < 0
                || offset > payload.length - length) {
            throw VPackErrors.write("owned byte store", "string payload range is invalid");
        }
        int headerLength = length <= 126 ? 1 : 9;
        long totalLength = (long) length + headerLength;
        ensureCapacityForAppend(totalLength);

        if (length <= 126) {
            appendByteUnchecked((byte) (0x40 + length));
        } else {
            appendByteUnchecked((byte) 0xBF);
            long remainingLength = length;
            for (int i = 0; i < 8; ++i) {
                appendByteUnchecked((byte) remainingLength);
                remainingLength >>>= 8;
            }
        }
        append(payload, offset, length);
    }

    void validateRange(long offset, long length, String context) {
        ensureRange(offset, length, context);
    }

    /** Reads directly into the current page's free tail. */
    int appendFrom(InputStream in, int maxLength) throws IOException {
        ensureOwned();
        if (in == null) {
            throw new NullPointerException("input");
        }
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must be non-negative");
        }
        int inPage = (int) (size & PAGE_MASK);
        int requested = Math.min(maxLength, PAGE_SIZE - inPage);
        ensureCapacityForAppend(requested);
        if (requested == 0) {
            return 0;
        }
        int page = (int) (size >>> PAGE_SHIFT);
        if (inPage == 0) {
            addPage(acquirePage());
        }
        // The caller's stream receives our page array, as Jackson's stream
        // parsers do with pooled input buffers. Any damage from a misbehaving
        // stream is confined to this root's owned store and is checked by the
        // parser's normal structural validation.
        int count = in.read(pages[page], inPage, requested);
        if (count > 0 && count <= requested) {
            size += count;
        }
        return count;
    }

    /** Reads exactly {@code length} bytes directly into owned page segments. */
    void appendFullyFrom(DataInput in, int length) throws IOException {
        ensureOwned();
        if (in == null) {
            throw new NullPointerException("input");
        }
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative");
        }
        ensureCapacityForAppend(length);
        int remaining = length;
        while (remaining != 0) {
            int page = (int) (size >>> PAGE_SHIFT);
            int inPage = (int) (size & PAGE_MASK);
            if (inPage == 0) {
                addPage(acquirePage());
            }
            int count = Math.min(remaining, PAGE_SIZE - inPage);
            in.readFully(pages[page], inPage, count);
            size += count;
            remaining -= count;
        }
    }

    void append(VPackByteStore source, long offset, long length) {
        ensureOwned();
        if (source == null) {
            throw VPackErrors.write("owned byte store", "source is null");
        }
        source.ensureRange(offset, length, "byte store append source");
        ensureCapacityForAppend(length);

        long sourceOffset = offset;
        long remaining = length;
        while (remaining != 0L) {
            int destinationPage = (int) (size >>> PAGE_SHIFT);
            int destinationOffset = (int) (size & PAGE_MASK);
            if (destinationOffset == 0) {
                addPage(acquirePage());
            }
            int count = (int) Math.min(remaining, PAGE_SIZE - destinationOffset);
            byte[] sourceArray;
            int sourceArrayOffset;
            if (source.borrowed != null) {
                sourceArray = source.borrowed;
                sourceArrayOffset = source.borrowedOffset + (int) sourceOffset;
                count = Math.min(count, sourceArray.length - sourceArrayOffset);
            } else {
                int sourcePageOffset = (int) (sourceOffset & PAGE_MASK);
                count = Math.min(count, PAGE_SIZE - sourcePageOffset);
                sourceArray = source.pages[(int) (sourceOffset >>> PAGE_SHIFT)];
                sourceArrayOffset = sourcePageOffset;
            }
            System.arraycopy(sourceArray, sourceArrayOffset, pages[destinationPage],
                    destinationOffset, count);
            sourceOffset += count;
            remaining -= count;
            size += count;
        }
    }

    Range range(long offset, long length) {
        ensureRange(offset, length, "byte range");
        return new Range(this, offset, length);
    }

    /**
     * Writes live bytes directly from backing arrays to the caller-supplied stream.
     * The backing arrays are passed to caller-supplied OutputStreams, with off/len
     * limited to live data. This has the same trade-off Jackson's generators make
     * with their pooled output buffers.
     */
    void writeTo(long index, long length, OutputStream out) throws IOException {
        ensureRange(index, length, "byte store write range");
        if (out == null) {
            throw new NullPointerException("output");
        }
        long source = index;
        long remaining = length;
        while (remaining != 0L) {
            byte[] data;
            int offset;
            int count;
            if (borrowed != null) {
                data = borrowed;
                offset = borrowedOffset + (int) source;
                count = (int) Math.min(remaining, data.length - offset);
            } else {
                offset = (int) (source & PAGE_MASK);
                count = (int) Math.min(remaining, PAGE_SIZE - offset);
                data = pages[(int) (source >>> PAGE_SHIFT)];
            }
            out.write(data, offset, count);
            source += count;
            remaining -= count;
        }
    }

    /**
     * Writes a validated range while gathering small backing-page slices into a
     * caller-owned output buffer. Large slices go directly to the target after
     * any buffered tail has been drained.
     */
    int writeTo(long index, long length, OutputStream out, byte[] buffer,
            int buffered) throws IOException {
        ensureRange(index, length, "byte store buffered write range");
        if (out == null) {
            throw new NullPointerException("output");
        }
        if (buffer == null || buffer.length == 0 || buffered < 0
                || buffered > buffer.length) {
            throw new IllegalArgumentException("output buffer state is invalid");
        }
        long source = index;
        long remaining = length;
        while (remaining != 0L) {
            byte[] data;
            int offset;
            int count;
            if (borrowed != null) {
                data = borrowed;
                offset = borrowedOffset + (int) source;
                count = (int) Math.min(remaining, data.length - offset);
            } else {
                offset = (int) (source & PAGE_MASK);
                count = (int) Math.min(remaining, PAGE_SIZE - offset);
                data = pages[(int) (source >>> PAGE_SHIFT)];
            }
            if (count >= buffer.length) {
                if (buffered != 0) {
                    out.write(buffer, 0, buffered);
                    buffered = 0;
                }
                out.write(data, offset, count);
            } else {
                if (count > buffer.length - buffered) {
                    out.write(buffer, 0, buffered);
                    buffered = 0;
                }
                System.arraycopy(data, offset, buffer, buffered, count);
                buffered += count;
                if (buffered == buffer.length) {
                    out.write(buffer, 0, buffered);
                    buffered = 0;
                }
            }
            source += count;
            remaining -= count;
        }
        return buffered;
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
            for (int i = 0; i < pageCount; ++i) {
                pageSupplier.release(pages[i]);
            }
        }
        Arrays.fill(pages, 0, pageCount, null);
        pageCount = 0;
        size = 0L;
    }

    private byte[] acquirePage() {
        byte[] page = pageSupplier == null ? new byte[PAGE_SIZE] : pageSupplier.acquire();
        if (page == null || page.length < PAGE_SIZE) {
            throw new IllegalStateException("page supplier returned a page smaller than PAGE_SIZE");
        }
        return page;
    }

    private void addPage(byte[] page) {
        if (pageCount == pages.length) {
            pages = Arrays.copyOf(pages, pages.length << 1);
        }
        pages[pageCount++] = page;
    }

    private void appendByteUnchecked(byte value) {
        int page = (int) (size >>> PAGE_SHIFT);
        int inPage = (int) (size & PAGE_MASK);
        if (inPage == 0) {
            addPage(acquirePage());
        }
        pages[page][inPage] = value;
        size++;
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

        void recordDecodedBytes(int byteLength) {
            owner.ensureOpen();
            owner.copiedBytes = VPackBounds.checkedAdd(owner.copiedBytes, byteLength,
                    "byte store copy instrumentation");
        }

        /** Return the backing array when the requested range is in one segment. */
        byte[] contiguousArray(long relativeOffset, int byteLength) {
            owner.ensureOpen();
            if (relativeOffset < 0L || relativeOffset > length || byteLength < 0
                    || byteLength > length - relativeOffset) {
                throw VPackErrors.malformed("byte range", relativeOffset,
                        "copy range is outside the range");
            }
            long absolute = VPackBounds.checkedAdd(offset, relativeOffset,
                    "byte range array offset");
            byte[] data;
            if (owner.borrowed != null) {
                data = owner.borrowed;
            } else {
                int inPage = (int) (absolute & PAGE_MASK);
                if (byteLength == 0 && absolute == owner.size) {
                    return null;
                }
                if (byteLength > PAGE_SIZE - inPage) {
                    return null;
                }
                data = owner.pages[(int) (absolute >>> PAGE_SHIFT)];
            }
            owner.scannedBytes = VPackBounds.checkedAdd(owner.scannedBytes, byteLength,
                    "byte store scan instrumentation");
            return data;
        }

        int contiguousOffset(long relativeOffset) {
            owner.ensureOpen();
            if (relativeOffset < 0L || relativeOffset > length) {
                throw VPackErrors.malformed("byte range", relativeOffset,
                        "copy range is outside the range");
            }
            long absolute = VPackBounds.checkedAdd(offset, relativeOffset,
                    "byte range array offset");
            return owner.borrowed != null
                    ? owner.borrowedOffset + (int) absolute
                    : (int) (absolute & PAGE_MASK);
        }

        int compareUnsigned(long relA, int lenA, long relB, int lenB) {
            byte[] a = contiguousArray(relA, lenA);
            byte[] b = contiguousArray(relB, lenB);
            int offA = a == null ? -1 : contiguousOffset(relA);
            int offB = b == null ? -1 : contiguousOffset(relB);
            if (a != null && b != null) {
                return Arrays.compareUnsigned(a, offA, offA + lenA, b, offB, offB + lenB);
            }
            int common = Math.min(lenA, lenB);
            for (int i = 0; i < common; ++i) {
                int va = unsignedByteAt(relA + i, a, offA + i);
                int vb = unsignedByteAt(relB + i, b, offB + i);
                if (va != vb) return va - vb;
            }
            return lenA - lenB;
        }

        int compareUnsigned(long relativeOffset, int byteLength, byte[] other) {
            byte[] data = contiguousArray(relativeOffset, byteLength);
            int offset = data == null ? -1 : contiguousOffset(relativeOffset);
            int common = Math.min(byteLength, other.length);
            for (int i = 0; i < common; ++i) {
                int left = unsignedByteAt(relativeOffset + i, data, offset + i);
                int right = other[i] & 0xFF;
                if (left != right) return left - right;
            }
            return byteLength - other.length;
        }

        private int unsignedByteAt(long relativeOffset, byte[] data, int arrayOffset) {
            if (data != null) return data[arrayOffset] & 0xFF;
            return byteAt(relativeOffset) & 0xFF;
        }

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
            // Both offsets are within a range already bounded by the store size.
            long source = offset + relativeOffset;
            if (owner.borrowed != null) {
                System.arraycopy(owner.borrowed, owner.borrowedOffset + (int) source,
                        output, outputOffset, copyLength);
                return;
            }
            int destination = outputOffset;
            int remaining = copyLength;
            while (remaining != 0) {
                int sourceOffset = (int) (source & PAGE_MASK);
                int count = Math.min(remaining, PAGE_SIZE - sourceOffset);
                int page = (int) (source >>> PAGE_SHIFT);
                System.arraycopy(owner.pages[page], sourceOffset, output, destination, count);
                source += count;
                destination += count;
                remaining -= count;
            }
        }

        void writeTo(long relativeOffset, long writeLength, OutputStream out) throws IOException {
            owner.ensureOpen();
            if (relativeOffset < 0L || relativeOffset > length || writeLength < 0L
                    || writeLength > length - relativeOffset) {
                throw VPackErrors.malformed("byte range", relativeOffset,
                        "write range is outside the range");
            }
            owner.writeTo(VPackBounds.checkedAdd(offset, relativeOffset,
                    "byte range write offset"), writeLength, out);
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
