package tools.jackson.dataformat.velocypack;

import java.util.Arrays;

/**
 * Checked, side-effect-free layout arithmetic for fixed and compact
 * containers. This class deliberately knows nothing about parser tokens or
 * observed entries; those checks belong to container traversal. Package-level
 * probes are retained for malformed-input tests.
 */
@SuppressWarnings("unused")
final class VPackLayout {
    private static final int[] STRUCTURAL_WIDTHS = { 1, 2, 4, 8 };
    private static final long UINT8_MAX = 0xFFL;
    private static final long UINT16_MAX = 0xFFFFL;
    private static final long UINT32_MAX = 0xFFFFFFFFL;

    private VPackLayout() { }

    private interface Bytes {
        long length();
        int get(long offset);
        default long readLE(long offset, int width) {
            long value = 0L;
            for (int i = 0; i < width; ++i) value |= (long) get(offset + i) << (8 * i);
            return value;
        }
    }

    /**
     * Regions in logical (usually source) coordinates.  A missing count has
     * {@code hasCount()==false} and both count boundaries are {@code -1}.
     * Index boundaries are equal for an unindexed form.
     */
    record FixedLayout(int marker, VPackMarker classification, int width,
            long address, long length, long end,
            long lengthStart, long lengthEnd,
            long countStart, long countEnd,
            long paddingStart, long paddingEnd,
            long bodyStart, long bodyEnd,
            long indexStart, long indexEnd,
            long count, boolean hasCount) {
        long headerEnd() { return paddingStart; }
        long padding() { return paddingEnd - paddingStart; }
        long bodyLength() { return bodyEnd - bodyStart; }
        boolean indexed() { return indexStart != indexEnd; }
    }

    /** A complete fixed-width writer candidate, with all boundaries resolved. */
    record FixedCandidate(int marker, int width, long length,
            long bodyStart, long bodyEnd, long indexStart, long indexEnd,
            long countStart, long countEnd, boolean trailingCount,
            boolean equalArray) {
        long indexLength() { return indexEnd - indexStart; }
    }

    /** A complete compact writer candidate after the length fixed point. */
    record CompactCandidate(int marker, long length, int lengthWidth,
            long bodyStart, long bodyEnd, long countStart, long countEnd,
            int countWidth) {
    }

    /**
     * Analyze one fixed container in a byte-array range.  {@code limit} is
     * the enclosing body boundary, not an invitation to inspect beyond the
     * declared container length.
     */
    static FixedLayout analyzeFixed(byte[] input, int offset, int limit) {
        return analyzeFixed(input, offset, limit, offset, limit);
    }

    @SuppressWarnings("DuplicatedCode") // Byte arrays and store ranges share the same layout algorithm.
    static FixedLayout analyzeFixed(byte[] input, int offset, int limit,
            long address, long enclosingEnd) {
        if (input == null) {
            throw VPackErrors.malformed("container layout", address, "byte input is null");
        }
        return analyzeFixed(new Bytes() {
            @Override public long length() { return input.length; }
            @Override public int get(long index) { return input[(int) index] & 0xFF; }
        }, offset, limit, address, enclosingEnd);
    }

    static FixedLayout analyzeFixed(VPackByteStore.Range input, long offset, long limit,
            long address, long enclosingEnd) {
        if (input == null) {
            throw VPackErrors.malformed("container layout", address, "byte range is null");
        }
        return analyzeFixed(new Bytes() {
            @Override public long length() { return input.length(); }
            @Override public int get(long index) { return input.byteAt(index) & 0xFF; }
            @Override public long readLE(long index, int width) { return input.readLE(index, width); }
        }, offset, limit, address, enclosingEnd);
    }

    /** Analyze a physical byte range while reporting logical source offsets. */
    private static FixedLayout analyzeFixed(Bytes input, long offset, long limit,
            long address, long enclosingEnd) {
        if (offset < 0L || limit < offset || limit > input.length()) {
            throw VPackErrors.malformed("container layout", address,
                    "byte range is invalid");
        }
        if (address < 0L || enclosingEnd < address) {
            throw VPackErrors.malformed("container layout", address,
                    "logical range is invalid");
        }
        if (offset == limit) {
            throw VPackErrors.malformed("container layout", address, "missing marker");
        }
        int marker = input.get(offset);
        VPackMarker classification = VPackMarker.requireSupported(marker,
                "container layout", address);
        if (!isFixedContainer(classification)) {
            throw VPackErrors.malformed("container layout", address,
                    "marker is not a fixed container");
        }

        if (classification == VPackMarker.EMPTY_ARRAY
                || classification == VPackMarker.EMPTY_OBJECT) {
            if (offset + 1L > limit) {
                throw VPackErrors.malformed("empty container", address, "container is truncated");
            }
            return new FixedLayout(marker, classification, 0, address, 1L,
                    checkedLogicalEnd(address, 1L, enclosingEnd),
                    -1L, -1L, -1L, -1L, address + 1L, address + 1L,
                    address + 1L, address + 1L, address + 1L, address + 1L,
                    -1L, false);
        }

        int width = VPackMarker.width(marker);
        int fieldEnd = checkedPhysicalRange(offset + 1L, offset + 1L + width, limit,
                "container length field", address + 1L);
        long length = readStructural(input, offset + 1L, width, address + 1L);
        if (length < 1L + width) {
            throw VPackErrors.malformed("container length", address + 1L,
                    "declared length is smaller than its consumed header");
        }
        long end = checkedLogicalEnd(address, length, enclosingEnd);
        int physicalEnd = checkedPhysicalRange(offset, offset + length, limit,
                "container range", address);
        long lengthStart = address + 1L;
        long lengthEnd = address + 1L + width;

        if (classification == VPackMarker.EQUAL_ARRAY) {
            int[] allowed = allowedPadding(width, false);
            int padding = inspectPadding(input, offset + 1L + width,
                    physicalEnd, allowed, address + 1L + width, end);
            long bodyStart = checkedAddRead(lengthEnd, padding, "equal-array body start",
                    address);
            requireNonemptyBody(bodyStart, end, address);
            return new FixedLayout(marker, classification, width, address, length, end,
                    lengthStart, lengthEnd, -1L, -1L,
                    lengthEnd, bodyStart, bodyStart, end,
                    end, end, -1L, false);
        }

        boolean trailingCount = marker == 0x09 || marker == 0x12;
        boolean object = classification == VPackMarker.SORTED_OBJECT
                || classification == VPackMarker.UNSORTED_OBJECT;
        long count;
        long countStart;
        long countEnd;
        long bodyStartBase;
        long bodyEnd;
        long indexStart;
        long indexEnd;
        if (trailingCount) {
            // W3: validate the suffix before deriving the index region.
            countStart = end - width;
            countEnd = end;
            if (countStart < lengthEnd) {
                throw VPackErrors.malformed("container count", countStart,
                        "trailing count overlaps the length header");
            }
            count = readStructural(input, physicalEnd - width, width, countStart);
            long indexBytes = checkedMultiplyRead(count, width, "container index length", address);
            bodyEnd = checkedSubtractRead(countStart, indexBytes, address);
            bodyStartBase = lengthEnd;
            indexStart = bodyEnd;
            indexEnd = countStart;
        } else {
            countStart = lengthEnd;
            countEnd = checkedAddRead(countStart, width, "container count boundary", address);
            count = readStructural(input, fieldEnd, width, countStart);
            long indexBytes = checkedMultiplyRead(count, width, "container index length", address);
            bodyEnd = checkedSubtractRead(end, indexBytes, address);
            bodyStartBase = countEnd;
            indexStart = bodyEnd;
            indexEnd = end;
        }
        if (count <= 0L) {
            throw VPackErrors.malformed("container count", countStart,
                    "regular fixed container count must be positive");
        }
        long minimumBody = checkedMultiplyRead(count, object ? 2L : 1L,
                "minimum container body", address);
        if (bodyEnd < bodyStartBase || bodyEnd - bodyStartBase < minimumBody) {
            throw VPackErrors.malformed("container body", bodyStartBase,
                    "body cannot contain the declared count");
        }
        if (indexEnd < indexStart || indexEnd > end) {
            throw VPackErrors.malformed("container index", indexStart,
                    "index overlaps the header or container boundary");
        }

        int[] allowed = allowedPadding(width, true);
        int padding = inspectPadding(input, offset + 1L +
                (trailingCount ? width : 2L * width),
                physicalEnd - (trailingCount ? width : 0L), allowed,
                bodyStartBase, bodyEnd);
        long bodyStart = checkedAddRead(bodyStartBase, padding, "container body start", address);
        if (bodyStart > bodyEnd || bodyEnd - bodyStart < minimumBody) {
            throw VPackErrors.malformed("container body", bodyStart,
                    "padding leaves no feasible body for the declared count");
        }
        return new FixedLayout(marker, classification, width, address, length, end,
                lengthStart, lengthEnd, countStart, countEnd,
                bodyStartBase, bodyStart, bodyStart, bodyEnd,
                indexStart, indexEnd, count, true);
    }

    /** Build one no-padding equal-array candidate at a specified width. */
    static FixedCandidate equalCandidate(int width, long bodyLength) {
        VPackBounds.requireWriteStructuralWidth(width, "equal-array width");
        requireWriteNonnegative(bodyLength, "equal-array body length");
        if (bodyLength == 0L) {
            throw VPackErrors.write("equal-array body", "regular equal array must be nonempty");
        }
        long bodyStart = writeAdd(1L, width, "equal-array body start");
        long length = writeAdd(bodyStart, bodyLength, "equal-array length");
        requireFits(length, width, "equal-array length");
        return buildEqualCandidate(width, bodyStart, length);
    }

    private static FixedCandidate buildEqualCandidate(int width, long bodyStart, long length) {
        int marker = 0x02 + widthIndex(width);
        return new FixedCandidate(marker, width, length, bodyStart, length,
                length, length, -1L, -1L, false, true);
    }

    private static FixedCandidate tryEqualCandidate(int width, long bodyLength) {
        if (!isStructuralWidth(width) || bodyLength < 0L || bodyLength == 0L) return null;
        long bodyStart = tryWriteAdd(1L, width);
        if (bodyStart < 0L) return null;
        long length = tryWriteAdd(bodyStart, bodyLength);
        if (length < 0L || !fits(length, width)) return null;
        return buildEqualCandidate(width, bodyStart, length);
    }

    static FixedCandidate selectEqualWidth(long bodyLength) {
        // Keep width probing allocation-free and exception-free on ordinary misses.
        for (int width : STRUCTURAL_WIDTHS) {
            FixedCandidate candidate = tryEqualCandidate(width, bodyLength);
            if (candidate != null) return candidate;
        }
        throw VPackErrors.write("equal-array width", "body does not fit any structural width");
    }

    /**
     * Build an indexed array/object candidate at a specified width.
     * {@code bodyOffsets}, when supplied, are starts relative to the body;
     * the candidate adds its width-dependent body start before checking the
     * values written to the index.  Passing null is useful for synthetic
     * count/length boundary tests that cannot materialize all entries.
     */
    static FixedCandidate indexedCandidate(boolean object, int width, long bodyLength,
            long entryCount, long[] bodyOffsets) {
        VPackBounds.requireWriteStructuralWidth(width, "indexed width");
        requireWriteNonnegative(bodyLength, "indexed body length");
        requireWriteNonnegative(entryCount, "indexed entry count");
        if (entryCount == 0L) {
            throw VPackErrors.write("indexed entry count", "regular fixed container count must be positive");
        }
        if (bodyOffsets != null && bodyOffsets.length != entryCount) {
            throw VPackErrors.write("indexed offsets", "offset count does not match entry count");
        }
        long minimumBody = writeMultiply(entryCount, object ? 2L : 1L, "minimum indexed body");
        if (bodyLength < minimumBody) {
            throw VPackErrors.write("indexed body", "body cannot contain the declared count");
        }
        long indexBytes = writeMultiply(entryCount, width, "indexed length");
        long header;
        long bodyStart;
        long indexStart;
        long indexEnd;
        long countStart;
        long countEnd;
        boolean trailing = !object && width == 8;
        if (trailing) {
            header = writeAdd(1L, width, "indexed header");
            bodyStart = header;
            indexStart = writeAdd(bodyStart, bodyLength, "indexed body end");
            indexEnd = writeAdd(indexStart, indexBytes, "indexed index end");
            countStart = indexEnd;
            countEnd = writeAdd(countStart, width, "indexed count end");
        } else {
            header = writeAdd(1L, writeMultiply(2L, width, "indexed header"), "indexed header");
            countStart = 1L + width;
            countEnd = header;
            bodyStart = header;
            indexStart = writeAdd(bodyStart, bodyLength, "indexed body end");
            indexEnd = writeAdd(indexStart, indexBytes, "indexed index end");
        }
        long length = trailing ? countEnd : indexEnd;
        requireFits(length, width, "indexed length");
        requireFits(entryCount, width, "indexed count");
        validateOffsets(bodyOffsets, bodyStart, bodyLength, width);
        return buildIndexedCandidate(object, width, length, bodyStart, indexStart,
                indexEnd, countStart, countEnd, trailing);
    }

    private static FixedCandidate buildIndexedCandidate(boolean object, int width,
            long length, long bodyStart, long indexStart, long indexEnd,
            long countStart, long countEnd, boolean trailing) {
        int marker = object ? 0x0B + widthIndex(width) : 0x06 + widthIndex(width);
        return new FixedCandidate(marker, width, length, bodyStart, indexStart,
                indexStart, indexEnd, countStart, countEnd, trailing, false);
    }

    private static FixedCandidate tryIndexedCandidate(boolean object, int width,
            long bodyLength, long entryCount, long[] bodyOffsets) {
        if (!isStructuralWidth(width) || bodyLength < 0L || entryCount < 0L
                || entryCount == 0L) return null;
        if (bodyOffsets != null && bodyOffsets.length != entryCount) return null;

        long minimumBody = tryWriteMultiply(entryCount, object ? 2L : 1L);
        if (minimumBody < 0L || bodyLength < minimumBody) return null;
        long indexBytes = tryWriteMultiply(entryCount, width);
        if (indexBytes < 0L) return null;

        long header;
        long bodyStart;
        long indexStart;
        long indexEnd;
        long countStart;
        long countEnd;
        boolean trailing = !object && width == 8;
        if (trailing) {
            header = tryWriteAdd(1L, width);
            if (header < 0L) return null;
            bodyStart = header;
            indexStart = tryWriteAdd(bodyStart, bodyLength);
            if (indexStart < 0L) return null;
            indexEnd = tryWriteAdd(indexStart, indexBytes);
            if (indexEnd < 0L) return null;
            countStart = indexEnd;
            countEnd = tryWriteAdd(countStart, width);
            if (countEnd < 0L) return null;
        } else {
            long twiceWidth = tryWriteMultiply(2L, width);
            if (twiceWidth < 0L) return null;
            header = tryWriteAdd(1L, twiceWidth);
            if (header < 0L) return null;
            countStart = 1L + width;
            countEnd = header;
            bodyStart = header;
            indexStart = tryWriteAdd(bodyStart, bodyLength);
            if (indexStart < 0L) return null;
            indexEnd = tryWriteAdd(indexStart, indexBytes);
            if (indexEnd < 0L) return null;
        }
        long length = trailing ? countEnd : indexEnd;
        if (!fits(length, width) || !fits(entryCount, width)) return null;
        if (!tryValidateOffsets(bodyOffsets, bodyStart, bodyLength, width)) return null;
        return buildIndexedCandidate(object, width, length, bodyStart, indexStart,
                indexEnd, countStart, countEnd, trailing);
    }

    /** Select the smallest complete indexed candidate, including its index bytes. */
    static FixedCandidate selectIndexedWidth(boolean object, long bodyLength,
            long entryCount, long[] bodyOffsets) {
        for (int i = 0; i < STRUCTURAL_WIDTHS.length - 1; ++i) {
            int width = STRUCTURAL_WIDTHS[i];
            FixedCandidate candidate = tryIndexedCandidate(object, width, bodyLength,
                    entryCount, bodyOffsets);
            if (candidate != null) return candidate;
        }
        return indexedCandidate(object, 8, bodyLength, entryCount, bodyOffsets);
    }

    /**
     * Compute compact output using W4's fixed point.  A null offsets concept
     * is intentional here: compact values have no index to validate.
     */
    static CompactCandidate compactCandidate(boolean object, long bodyLength, long entryCount) {
        requireWriteNonnegative(bodyLength, "compact body length");
        requireWriteNonnegative(entryCount, "compact entry count");
        long minimumBody = writeMultiply(entryCount, object ? 2L : 1L, "minimum compact body");
        if (bodyLength < minimumBody) {
            throw VPackErrors.write("compact body", "body cannot contain the declared count");
        }
        int countWidth = compactWidth(entryCount);
        int lengthWidth = 1;
        long length;
        for (int attempt = 0; attempt < 16; ++attempt) {
            length = writeAdd(1L, lengthWidth, "compact length");
            length = writeAdd(length, bodyLength, "compact length");
            length = writeAdd(length, countWidth, "compact length");
            int required = compactWidth(length);
            if (required == lengthWidth) {
                int marker = object ? 0x14 : 0x13;
                long bodyStart = 1L + lengthWidth;
                long bodyEnd = writeAdd(bodyStart, bodyLength, "compact body end");
                long countEnd = writeAdd(bodyEnd, countWidth, "compact count end");
                if (countEnd != length) {
                    throw VPackErrors.write("compact length", "fixed point boundaries disagree");
                }
                return new CompactCandidate(marker, length, lengthWidth, bodyStart, bodyEnd,
                        bodyEnd, countEnd, countWidth);
            }
            lengthWidth = required;
        }
        throw VPackErrors.write("compact length", "length width did not converge");
    }

    private static boolean isFixedContainer(VPackMarker classification) {
        return classification == VPackMarker.EQUAL_ARRAY
                || classification == VPackMarker.INDEXED_ARRAY
                || classification == VPackMarker.SORTED_OBJECT
                || classification == VPackMarker.UNSORTED_OBJECT
                || classification == VPackMarker.EMPTY_ARRAY
                || classification == VPackMarker.EMPTY_OBJECT;
    }

    private static int[] allowedPadding(int width, boolean indexed) {
        if (!indexed) {
            return switch (width) {
            case 1 -> new int[] { 0, 1, 3, 7 };
            case 2 -> new int[] { 0, 2, 6 };
            case 4 -> new int[] { 0, 4 };
            case 8 -> new int[] { 0 };
            default -> throw VPackErrors.malformed("container padding", "invalid width");
            };
        }
        return switch (width) {
        case 1 -> new int[] { 0, 6 };
        case 2 -> new int[] { 0, 4 };
        case 4, 8 -> new int[] { 0 };
        default -> throw VPackErrors.malformed("container padding", "invalid width");
        };
    }

    private static int inspectPadding(Bytes input, long base, long bodyEnd, int[] allowed,
            long logicalBase, long logicalBodyEnd) {
        int maximum = allowed[allowed.length - 1];
        for (int padding = 0; padding <= maximum; ++padding) {
            if (base + padding >= bodyEnd) {
                break;
            }
            if (input.get(base + padding) == 0) {
                continue;
            }
            if (Arrays.binarySearch(allowed, padding) < 0) {
                throw VPackErrors.malformed("container padding", logicalBase + padding,
                        "first nonzero byte is not at an allowed body start");
            }
            if (logicalBase + padding >= logicalBodyEnd) {
                throw VPackErrors.malformed("container padding", logicalBase + padding,
                        "padding leaves no child before the index or count region");
            }
            VPackMarker.requireSupported(input.get(base + padding),
                    "container child marker", logicalBase + padding);
            return padding;
        }
        throw VPackErrors.malformed("container padding", logicalBase,
                "no child begins at an allowed body start");
    }

    private static long readStructural(Bytes input, long offset, int width, long errorOffset) {
        if (width != 1 && width != 2 && width != 4 && width != 8) {
            throw VPackErrors.malformed("structural field", errorOffset,
                    "field width must be 1, 2, 4, or 8");
        }
        if (offset < 0L || offset > input.length() - width) {
            throw VPackErrors.malformed("structural field", errorOffset, "field is truncated");
        }
        long value = input.readLE(offset, width);
        if (width == 8 && value < 0L) {
            throw VPackErrors.malformed("structural field", errorOffset,
                    "unsigned field does not fit in a signed long");
        }
        return value;
    }

    private static void requireNonemptyBody(long start, long end, long address) {
        if (end <= start) {
            throw VPackErrors.malformed("equal array body", address,
                    "regular fixed container body must be nonempty");
        }
    }

    private static int checkedPhysicalRange(long start, long lengthOrEnd,
            long limit, String context, long logicalOffset) {
        if (start < 0L || start > Integer.MAX_VALUE) {
            throw VPackErrors.malformed(context, logicalOffset, "physical offset is invalid");
        }
        if (lengthOrEnd < start || lengthOrEnd > limit) {
            throw VPackErrors.malformed(context, logicalOffset, "field or range is truncated");
        }
        return (int) lengthOrEnd;
    }

    private static long checkedLogicalEnd(long address, long length, long enclosingEnd) {
        long end = VPackBounds.checkedAdd(address, length, "container end", address);
        if (end > enclosingEnd) {
            throw VPackErrors.malformed("container range", address,
                    "declared length exceeds enclosing body boundary");
        }
        return end;
    }

    private static long checkedAddRead(long left, long right, String context, long address) {
        return VPackBounds.checkedAdd(left, right, context, address);
    }

    private static long checkedSubtractRead(long left, long right, long address) {
        if (right < 0L || right > left) {
            throw VPackErrors.malformed("container body end", address, "region underflow");
        }
        return left - right;
    }

    private static long checkedMultiplyRead(long left, long right, String context, long address) {
        if (left < 0L || right < 0L || (left != 0L && right > Long.MAX_VALUE / left)) {
            throw VPackErrors.malformed(context, address, "region multiplication overflow");
        }
        return left * right;
    }

    private static int widthIndex(int width) {
        return switch (width) {
        case 1 -> 0;
        case 2 -> 1;
        case 4 -> 2;
        case 8 -> 3;
        default -> throw VPackErrors.write("container width", "invalid structural width");
        };
    }

    private static long widthMaximum(int width) {
        return switch (width) {
        case 1 -> UINT8_MAX;
        case 2 -> UINT16_MAX;
        case 4 -> UINT32_MAX;
        case 8 -> Long.MAX_VALUE;
        default -> throw VPackErrors.write("container width", "invalid structural width");
        };
    }

    private static void requireFits(long value, int width, String context) {
        if (value < 0L || value > widthMaximum(width)) {
            throw VPackErrors.write(context, "value does not fit in " + width + " bytes");
        }
    }

    private static boolean fits(long value, int width) {
        return value >= 0L && value <= widthMaximum(width);
    }

    private static boolean isStructuralWidth(int width) {
        return width == 1 || width == 2 || width == 4 || width == 8;
    }

    // A negative result marks invalid nonnegative arithmetic; valid layout values are nonnegative.
    private static long tryWriteAdd(long left, long right) {
        if (left < 0L || right < 0L || right > Long.MAX_VALUE - left) return -1L;
        return left + right;
    }

    private static long tryWriteMultiply(long left, long right) {
        if (left < 0L || right < 0L
                || (left != 0L && right > Long.MAX_VALUE / left)) return -1L;
        return left * right;
    }

    private static void validateOffsets(long[] bodyOffsets, long bodyStart,
            long bodyLength, int width) {
        if (bodyOffsets == null) return;
        for (long bodyOffset : bodyOffsets) {
            if (bodyOffset < 0L || bodyOffset >= bodyLength) {
                throw VPackErrors.write("indexed offset", "offset is outside the body");
            }
            requireFits(writeAdd(bodyStart, bodyOffset, "indexed offset"), width,
                    "indexed offset");
        }
    }

    private static boolean tryValidateOffsets(long[] bodyOffsets, long bodyStart,
            long bodyLength, int width) {
        if (bodyOffsets == null) return true;
        for (long bodyOffset : bodyOffsets) {
            if (bodyOffset < 0L || bodyOffset >= bodyLength) return false;
            long offset = tryWriteAdd(bodyStart, bodyOffset);
            if (offset < 0L || !fits(offset, width)) return false;
        }
        return true;
    }

    private static int compactWidth(long value) {
        if (value < 0L || value > VPackBounds.MAX_COMPACT_VALUE) {
            throw VPackErrors.write("compact width", "value must be in 0..2^56-1");
        }
        int width = 1;
        while ((value >>>= 7) != 0L) ++width;
        return width;
    }

    private static void requireWriteNonnegative(long value, String context) {
        if (value < 0L) throw VPackErrors.write(context, "value is negative");
    }

    private static long writeAdd(long left, long right, String context) {
        requireWriteNonnegative(left, context);
        requireWriteNonnegative(right, context);
        if (right > Long.MAX_VALUE - left) throw VPackErrors.write(context, "addition overflow");
        return left + right;
    }

    private static long writeMultiply(long left, long right, String context) {
        requireWriteNonnegative(left, context);
        requireWriteNonnegative(right, context);
        if (left != 0L && right > Long.MAX_VALUE / left) {
            throw VPackErrors.write(context, "multiplication overflow");
        }
        return left * right;
    }
}
