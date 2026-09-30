package tools.jackson.dataformat.velocypack;

import java.math.BigInteger;

/** Checked integer, range, and little-endian wire primitives. */
final class VPackBounds {
    static final long MAX_COMPACT_VALUE = 0x00FFFFFFFFFFFFFFL;
    static final BigInteger UINT64_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    private VPackBounds() { }

    static long checkedAdd(long left, long right) {
        return checkedAdd(left, right, "wire arithmetic");
    }

    static long checkedAdd(long left, long right, String context) {
        nonNegative(left, context, "left operand is negative");
        nonNegative(right, context, "right operand is negative");
        if (right > Long.MAX_VALUE - left) {
            throw VPackErrors.malformed(context, "addition overflow");
        }
        return left + right;
    }

    static long checkedAdd(long left, long right, String context, String suffix) {
        if (left < 0L || right < 0L || right > Long.MAX_VALUE - left) {
            return checkedAdd(left, right, context + suffix);
        }
        return left + right;
    }

    static long checkedAdd(long left, long right, String context, long errorOffset) {
        if (left < 0L || right < 0L) {
            throw VPackErrors.malformed(context, errorOffset, "arithmetic operand is negative");
        }
        if (right > Long.MAX_VALUE - left) {
            throw VPackErrors.malformed(context, errorOffset, "addition overflow");
        }
        return left + right;
    }

    static long checkedSubtract(long left, long right) {
        return checkedSubtract(left, right, "wire arithmetic");
    }

    static long checkedSubtract(long left, long right, String context) {
        nonNegative(left, context, "left operand is negative");
        nonNegative(right, context, "right operand is negative");
        if (right > left) {
            throw VPackErrors.malformed(context, "subtraction would produce a negative result");
        }
        return left - right;
    }

    static long checkedMultiply(long left, long right) {
        return checkedMultiply(left, right, "wire arithmetic");
    }

    static long checkedMultiply(long left, long right, String context) {
        nonNegative(left, context, "left operand is negative");
        nonNegative(right, context, "right operand is negative");
        if (left != 0L && right > Long.MAX_VALUE / left) {
            throw VPackErrors.malformed(context, "multiplication overflow");
        }
        return left * right;
    }

    static long checkedRange(long start, long length, long limit, String context) {
        nonNegative(start, context, "range start is negative");
        nonNegative(length, context, "range length is negative");
        nonNegative(limit, context, "range limit is negative");
        long end = checkedAdd(start, length, context);
        if (end > limit) {
            throw VPackErrors.malformed(context, "range exceeds enclosing limit");
        }
        return end;
    }

    static long requireBudget(long value, long budget, String context) {
        nonNegative(value, context, "value is negative");
        nonNegative(budget, context, "budget is negative");
        if (value > budget) {
            throw VPackErrors.constraint(context, -1L,
                    "value exceeds configured budget " + budget);
        }
        return value;
    }

    static int checkedInt(long value, String context) {
        nonNegative(value, context, "value is negative");
        if (value > Integer.MAX_VALUE) {
            throw VPackErrors.constraint(context, -1L, "value does not fit in an int");
        }
        return (int) value;
    }

    static int checkedInt(long value, String context, String suffix) {
        if (value < 0L || value > Integer.MAX_VALUE) {
            return checkedInt(value, context + suffix);
        }
        return (int) value;
    }

    static int checkedArrayRange(byte[] input, long offset, long length, String context) {
        if (input == null) {
            throw VPackErrors.malformed(context, "byte input is null");
        }
        long end = checkedRange(offset, length, input.length, context);
        if (offset > Integer.MAX_VALUE || end > Integer.MAX_VALUE) {
            throw VPackErrors.malformed(context, offset, "byte range does not fit in an array index");
        }
        return (int) end;
    }

    static int checkedWriteArrayRange(byte[] output, long offset, long length,
            String context) {
        if (output == null) {
            throw VPackErrors.write(context, "byte output is null");
        }
        if (offset < 0L) {
            throw VPackErrors.write(context, "range start is negative");
        }
        if (length < 0L) {
            throw VPackErrors.write(context, "range length is negative");
        }
        if (offset > Long.MAX_VALUE - length) {
            throw VPackErrors.write(context, "range end overflows");
        }
        long end = offset + length;
        if (end > output.length) {
            throw VPackErrors.write(context, "range exceeds output length");
        }
        if (offset > Integer.MAX_VALUE || end > Integer.MAX_VALUE) {
            throw VPackErrors.write(context, "byte range does not fit in an array index");
        }
        return (int) end;
    }

    static int requireWidth(int width, String context) {
        if (width != 1 && width != 2 && width != 4 && width != 8) {
            throw VPackErrors.malformed(context, "field width must be 1, 2, 4, or 8");
        }
        return width;
    }

    /** Widths used by structural length, count, and offset fields. */
    static int requireStructuralWidth(int width, String context) {
        return requireWidth(width, context);
    }

    static int requireWriteStructuralWidth(int width, String context) {
        if (width != 1 && width != 2 && width != 4 && width != 8) {
            throw VPackErrors.write(context, "field width must be 1, 2, 4, or 8");
        }
        return width;
    }

    /** Numeric scalar fields use every byte width from one through eight. */
    static int requireNumericWidth(int width, String context) {
        if (width < 1 || width > 8) {
            throw VPackErrors.malformed(context, "numeric field width must be 1 through 8");
        }
        return width;
    }

    static int requireWriteNumericWidth(int width, String context) {
        if (width < 1 || width > 8) {
            throw VPackErrors.write(context, "numeric field width must be 1 through 8");
        }
        return width;
    }

    /** Read an unsigned field as its exact uint64 value. */
    static BigInteger readUnsigned(byte[] input, int offset, int width) {
        requireNumericWidth(width, "unsigned field");
        long bits = readNumericBits(input, offset, width);
        return readUnsigned(bits, width);
    }

    static BigInteger readUnsigned(long bits, int width) {
        requireNumericWidth(width, "unsigned field");
        return width == 8 && bits < 0L ? unsignedLong(bits) : BigInteger.valueOf(bits);
    }

    /** Read a signed two's-complement field, sign-extending widths below eight. */
    static long readSigned(byte[] input, int offset, int width) {
        requireNumericWidth(width, "signed field");
        long bits = readNumericBits(input, offset, width);
        return readSigned(bits, width);
    }

    static long readSigned(long bits, int width) {
        requireNumericWidth(width, "signed field");
        if (width < 8 && (bits & (1L << ((width * 8) - 1))) != 0L) {
            bits |= -1L << (width * 8);
        }
        return bits;
    }

    /**
     * Read a structural unsigned field. A uint64 with bit 63 set is a value,
     * but cannot be a Java long structural position or length.
     */
    static long readStructural(byte[] input, int offset, int width) {
        requireStructuralWidth(width, "structural field");
        long bits = readBits(input, offset, width);
        return readStructural(bits, width, offset);
    }

    static long readStructural(long bits, int width, long errorOffset) {
        requireStructuralWidth(width, "structural field");
        if (width == 8 && bits < 0L) {
            throw VPackErrors.malformed("structural field", errorOffset,
                    "uint64 value has its high bit set");
        }
        return bits;
    }

    static long readStructural(byte[] input, int offset, int width, long errorOffset) {
        requireStructuralWidth(width, "structural field");
        return readStructural(readLengthBits(input, offset, width, errorOffset,
                "structural field"), width, errorOffset);
    }

    /**
     * Read a scalar length prefix. Unlike structural container fields, scalar
     * length prefixes may use every width from one through eight.
     */
    static long readScalarLength(byte[] input, int offset, int width, long errorOffset) {
        requireNumericWidth(width, "scalar length");
        return readScalarLength(readLengthBits(input, offset, width, errorOffset,
                "scalar length"), width, errorOffset);
    }

    static long readScalarLength(long bits, int width, long errorOffset) {
        requireNumericWidth(width, "scalar length");
        if (width == 8 && bits < 0L) {
            throw VPackErrors.malformed("scalar length", errorOffset,
                    "uint64 value has its high bit set");
        }
        return bits;
    }

    private static long readLengthBits(byte[] input, int offset, int width, long errorOffset,
            String context) {
        if (input == null) {
            throw VPackErrors.malformed(context, errorOffset, "byte input is null");
        }
        long end = (long) offset + width;
        if (offset < 0 || end < 0L || end > input.length) {
            throw VPackErrors.malformed(context, errorOffset, "field is truncated");
        }
        long bits = readBitsUnchecked(input, offset, width, context);
        if (width == 8 && bits < 0L) {
            throw VPackErrors.malformed(context, errorOffset,
                    "uint64 value has its high bit set");
        }
        return bits;
    }

    /** Raw little-endian bits; for width eight the result is an unsigned bit pattern. */
    static long readBits(byte[] input, int offset, int width) {
        requireStructuralWidth(width, "little-endian field");
        return readBitsUnchecked(input, offset, width, "little-endian field");
    }

    private static long readNumericBits(byte[] input, int offset, int width) {
        requireNumericWidth(width, "little-endian numeric field");
        return readBitsUnchecked(input, offset, width, "little-endian numeric field");
    }

    private static long readBitsUnchecked(byte[] input, int offset, int width, String context) {
        checkedArrayRange(input, offset, width, context);
        long result = 0L;
        for (int i = 0; i < width; ++i) {
            result |= (long) (input[offset + i] & 0xFF) << (8 * i);
        }
        return result;
    }

    static void writeBits(byte[] output, int offset, int width, long bits) {
        requireWriteStructuralWidth(width, "little-endian field");
        writeBitsUnchecked(output, offset, width, bits, "little-endian field");
    }

    private static void writeNumericBits(byte[] output, int offset, int width, long bits) {
        requireWriteNumericWidth(width, "little-endian numeric field");
        writeBitsUnchecked(output, offset, width, bits, "little-endian numeric field");
    }

    private static void writeBitsUnchecked(byte[] output, int offset, int width, long bits,
            String context) {
        checkedWriteArrayRange(output, offset, width, context);
        for (int i = 0; i < width; ++i) {
            output[offset + i] = (byte) (bits >>> (8 * i));
        }
    }

    static void writeUnsigned(byte[] output, int offset, int width, BigInteger value) {
        requireWriteNumericWidth(width, "unsigned field");
        if (value == null || value.signum() < 0 || value.bitLength() > width * 8) {
            throw VPackErrors.write("unsigned field", "value does not fit in " + width + " bytes");
        }
        checkedWriteArrayRange(output, offset, width, "unsigned field");
        BigInteger remaining = value;
        for (int i = 0; i < width; ++i) {
            output[offset + i] = remaining.byteValue();
            remaining = remaining.shiftRight(8);
        }
    }

    /** Write a signed two's-complement scalar after checking its exact width. */
    static void writeSigned(byte[] output, int offset, int width, long value) {
        requireWriteNumericWidth(width, "signed field");
        if (width < 8) {
            int bits = width * 8;
            long minimum = -(1L << (bits - 1));
            long maximum = (1L << (bits - 1)) - 1L;
            if (value < minimum || value > maximum) {
                throw VPackErrors.write("signed field",
                        "value does not fit in " + width + " bytes");
            }
        }
        writeNumericBits(output, offset, width, value);
    }

    static BigInteger unsignedLong(long bits) {
        if (bits >= 0L) {
            return BigInteger.valueOf(bits);
        }
        return BigInteger.valueOf(bits & Long.MAX_VALUE).setBit(63);
    }

    static long requireUint64(BigInteger value, String context) {
        if (value == null || value.signum() < 0 || value.compareTo(UINT64_MAX) > 0) {
            throw VPackErrors.write(context, "value is outside uint64 range");
        }
        return value.longValue();
    }

    static void nonNegative(long value, String context, String detail) {
        if (value < 0L) {
            throw VPackErrors.malformed(context, detail);
        }
    }

}
