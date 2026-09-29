package tools.jackson.dataformat.velocypack;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.StreamReadConstraints;

/** Exact numeric decoding and guards shared by the VPack parser. */
final class VPackNumbers {
    private VPackNumbers() { }

    static byte[] encodeBcd(BigInteger unscaled, int exponent, int maxDigits,
            long maxValueBytes) {
        if (unscaled == null) {
            throw VPackErrors.write("BCD", "unscaled value is null");
        }
        BigInteger magnitude = unscaled.abs();
        // A conservative lower bound: 301029995 / 10^9 < log10(2).
        // The product fits long even at Integer.MAX_VALUE bitLength. Reject
        // grossly oversized values BEFORE BigInteger's decimal conversion.
        long minimumDigits = magnitude.signum() == 0 ? 1L
                : 1L + (magnitude.bitLength() - 1L) * 301029995L / 1_000_000_000L;
        long minimumMantissaBytes = (minimumDigits + 1L) / 2L;
        if (minimumDigits > maxDigits
                || 5L + scalarLengthWidth(minimumMantissaBytes) + minimumMantissaBytes > maxValueBytes) {
            throw VPackErrors.constraint("BCD", -1L,
                    "value cannot fit the configured digit or root byte budget");
        }
        String digits = magnitude.toString();
        int digitCount = digits.length();
        if (digitCount > maxDigits) {
            throw VPackErrors.constraint("number", -1L,
                    "BCD mantissa digit count " + digitCount
                            + " exceeds configured maximum " + maxDigits);
        }
        int mantissaLength = (digitCount + 1) / 2;
        int width = scalarLengthWidth(mantissaLength);
        long total = 1L + width + 4L + mantissaLength;
        if (total > Integer.MAX_VALUE) {
            throw VPackErrors.constraint("BCD", -1L,
                    "encoded value is too large for a Java byte array");
        }
        if (total > maxValueBytes) {
            throw VPackErrors.constraint("BCD", -1L,
                    "encoded value exceeds configured root byte budget");
        }
        byte[] result = new byte[(int) total];
        result[0] = (byte) ((unscaled.signum() < 0 ? 0xD0 : 0xC8) + width - 1);
        VPackBounds.writeUnsigned(result, 1, width, BigInteger.valueOf(mantissaLength));
        VPackBounds.writeBits(result, 1 + width, 4, exponent);

        int mantissaOffset = 1 + width + 4;
        int digitOffset = 0;
        for (int i = 0; i < mantissaLength; ++i) {
            int high;
            if ((digitCount & 1) != 0 && i == 0) {
                high = 0;
            } else {
                high = digits.charAt(digitOffset++) - '0';
            }
            int low = digits.charAt(digitOffset++) - '0';
            result[mantissaOffset + i] = (byte) ((high << 4) | low);
        }
        return result;
    }

    static Number parseTextualNumber(String value, int maxDigits) {
        if (value == null || value.isEmpty()) {
            throw VPackErrors.write("textual number", "value is empty");
        }
        int length = value.length();
        validateTextualLength(length, maxDigits);
        int index = 0;
        if (value.charAt(index) == '-') {
            if (++index == length) {
                throw invalidTextNumber();
            }
        }

        int digits = 0;
        char first = value.charAt(index);
        if (first == '0') {
            ++digits;
            if (++index < length && isDigit(value.charAt(index))) {
                throw invalidTextNumber();
            }
        } else if (first >= '1' && first <= '9') {
            do {
                ++digits;
                ++index;
            } while (index < length && isDigit(value.charAt(index)));
        } else {
            throw invalidTextNumber();
        }

        boolean decimal = false;
        if (index < length && value.charAt(index) == '.') {
            decimal = true;
            ++index;
            int fractionStart = index;
            while (index < length && isDigit(value.charAt(index))) {
                ++digits;
                ++index;
            }
            if (index == fractionStart) {
                throw invalidTextNumber();
            }
        }

        boolean exponent = false;
        if (index < length && (value.charAt(index) == 'e' || value.charAt(index) == 'E')) {
            exponent = true;
            ++index;
            if (index < length && (value.charAt(index) == '+' || value.charAt(index) == '-')) {
                ++index;
            }
            int exponentStart = index;
            while (index < length && isDigit(value.charAt(index))) {
                ++digits;
                ++index;
            }
            if (index == exponentStart) {
                throw invalidTextNumber();
            }
        }
        if (index != length) {
            throw invalidTextNumber();
        }
        if (digits > maxDigits) {
            throw VPackErrors.constraint("textual number", -1L,
                    "digit count " + digits + " exceeds configured maximum " + maxDigits);
        }
        try {
            return (!decimal && !exponent)
                    ? new BigInteger(value) : new BigDecimal(value);
        } catch (RuntimeException e) {
            throw VPackErrors.write("textual number", "value is outside exact Java range");
        }
    }

    static void validateTextualLength(int length, int maxDigits) {
        // A strict JSON number has at most four non-digits: -, ., e/E, +/-.
        if ((long) length > (long) maxDigits + 4L) {
            throw VPackErrors.constraint("textual number", -1L,
                    "text cannot fit the configured digit budget");
        }
    }

    private static boolean isDigit(char value) {
        return value >= '0' && value <= '9';
    }

    private static RuntimeException invalidTextNumber() {
        return VPackErrors.write("textual number", "value is not a strict JSON number");
    }

    static int scalarLengthWidth(long length) {
        if (length <= 0xFFL) return 1;
        if (length <= 0xFFFFL) return 2;
        if (length <= 0xFFFFFFL) return 3;
        if (length <= 0xFFFFFFFFL) return 4;
        if (length <= 0xFFFFFFFFFFL) return 5;
        if (length <= 0xFFFFFFFFFFFFL) return 6;
        if (length <= 0xFFFFFFFFFFFFFFL) return 7;
        return 8;
    }

    static void validateBcdInput(long mantissaLength, int exponent, long errorOffset,
            StreamReadConstraints constraints) {
        long digitCount = VPackBounds.checkedMultiply(mantissaLength, 2L,
                "BCD digit length");
        if (digitCount > Integer.MAX_VALUE) {
            throw VPackErrors.constraint("BCD digit length", errorOffset,
                    "digit count does not fit in a Java number-length constraint");
        }
        if (exponent == 0) {
            constraints.validateIntegerLength((int) digitCount);
        } else {
            constraints.validateFPLength((int) digitCount);
        }
        if (exponent == Integer.MIN_VALUE) {
            throw VPackErrors.malformed("BCD", errorOffset,
                    "exponent -2147483648 cannot be represented as a Java BigDecimal scale");
        }
    }

    static Number decodeBcd(byte[] mantissa, boolean negative, int exponent,
            long errorOffset, StreamReadConstraints constraints) {
        if (mantissa == null || mantissa.length == 0) {
            throw VPackErrors.malformed("BCD", errorOffset, "mantissa must not be empty");
        }
        long digitCount = VPackBounds.checkedMultiply(mantissa.length, 2L,
                "BCD digit length");
        validateBcdInput(mantissa.length, exponent, errorOffset, constraints);

        char[] digits = new char[(int) digitCount];
        int out = 0;
        for (byte value : mantissa) {
            int high = (value >>> 4) & 0x0F;
            int low = value & 0x0F;
            if (high > 9 || low > 9) {
                throw VPackErrors.malformed("BCD", errorOffset,
                        "invalid BCD digit");
            }
            digits[out++] = (char) ('0' + high);
            digits[out++] = (char) ('0' + low);
        }

        BigInteger unscaled = new BigInteger(new String(digits));
        if (negative) {
            unscaled = unscaled.negate();
        }
        if (exponent == 0) {
            return unscaled;
        }
        int scale = -exponent;
        return new BigDecimal(unscaled, scale);
    }

    static void validateBigIntegerScale(BigDecimal value, long errorOffset,
            StreamReadConstraints constraints) {
        int scale = value.scale();
        // StreamReadConstraints uses Math.abs(int); guard MIN_VALUE before delegating.
        if (scale == Integer.MIN_VALUE) {
            throw VPackErrors.constraint("BigInteger conversion", errorOffset,
                    "BigDecimal scale " + scale + " magnitude exceeds the maximum allowed");
        }
        constraints.validateBigIntegerScale(scale);
    }
}
