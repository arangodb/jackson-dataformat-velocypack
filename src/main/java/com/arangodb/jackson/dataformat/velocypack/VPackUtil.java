package com.arangodb.jackson.dataformat.velocypack;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Miscellaneous helper methods for VelocyPack encoding and decoding.
 */
public class VPackUtil
{
    /**
     * Return the byte width (1, 2, 4, or 8) needed to represent the given
     * unsigned value.
     */
    public static int unsignedByteWidth(long value) {
        if (value <= 0xFFL) {
            return 1;
        }
        if (value <= 0xFFFFL) {
            return 2;
        }
        if (value <= 0xFFFFFFFFL) {
            return 4;
        }
        return 8;
    }

    /**
     * Return the byte width (1, 2, 4, or 8) needed to represent the given
     * unsigned value using the widths 1, 2, 4, 8 only (no 3/5/6/7).
     */
    public static int signedByteWidth(long value) {
        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            return 1;
        }
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            return 2;
        }
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            return 4;
        }
        return 8;
    }

    /**
     * Write a little-endian unsigned integer of exactly {@code width} bytes into
     * {@code buf} at {@code offset}.
     */
    public static void writeLeUnsigned(byte[] buf, int offset, long value, int width) {
        for (int i = 0; i < width; i++) {
            buf[offset + i] = (byte) (value & 0xFF);
            value >>>= 8;
        }
    }

    /**
     * Read a little-endian unsigned long from {@code buf} at {@code offset},
     * using exactly {@code width} bytes.
     */
    public static long readLeUnsigned(byte[] buf, int offset, int width) {
        long result = 0L;
        for (int i = width - 1; i >= 0; i--) {
            result = (result << 8) | (buf[offset + i] & 0xFFL);
        }
        return result;
    }

    /**
     * Read a little-endian signed long from {@code buf} at {@code offset},
     * using exactly {@code width} bytes (sign-extends).
     */
    public static long readLeSigned(byte[] buf, int offset, int width) {
        long result = readLeUnsigned(buf, offset, width);
        // Sign-extend if width < 8
        if (width < 8) {
            int shiftBits = (8 - width) * 8;
            result = (result << shiftBits) >> shiftBits;
        }
        return result;
    }

    /**
     * Encode a non-negative long value using VByte (variable-length, 7 bits per byte,
     * all bytes except the last have high bit set). Returns the bytes in little-endian
     * order (least-significant 7 bits first), as used in compact arrays/objects.
     */
    public static byte[] encodeVByte(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("VByte encoding requires non-negative value");
        }
        // Maximum 8 bytes for up to 56-bit values
        byte[] tmp = new byte[8];
        int len = 0;
        do {
            tmp[len++] = (byte) ((value & 0x7FL) | (value >= 0x80L ? 0x80 : 0x00));
            value >>>= 7;
        } while (value > 0L);
        // All bytes except the last have their high bit set; the last has it clear
        // (already set correctly in the loop above)
        byte[] result = new byte[len];
        System.arraycopy(tmp, 0, result, 0, len);
        return result;
    }

    /**
     * Decode a VByte-encoded value from {@code data} starting at {@code offset}.
     * Returns the decoded value; the number of bytes consumed is
     * {@code [0] = bytes consumed} stored in {@code outLen[0]}.
     */
    public static long decodeVByte(byte[] data, int offset, int[] outLen) {
        long result = 0L;
        int shift = 0;
        int i = offset;
        while (i < data.length) {
            int b = data[i++] & 0xFF;
            result |= (long)(b & 0x7F) << shift;
            shift += 7;
            if ((b & 0x80) == 0) {
                break;
            }
        }
        outLen[0] = i - offset;
        return result;
    }

    /**
     * Decode reverse-VByte (used for NRITEMS at end of compact array/object).
     * In reverse VByte, the LAST byte (highest address) contains the LEAST significant
     * 7 bits; walk backwards from {@code endOffset - 1} until you find a byte
     * without the high bit set.
     *
     * @param data       byte array
     * @param endOffset  exclusive end of the value (i.e., the byte just past the last byte)
     * @param outLen     outLen[0] will be set to the number of bytes consumed (walking backwards)
     * @return decoded value
     */
    public static long decodeVByteReverse(byte[] data, int endOffset, int[] outLen) {
        long result = 0L;
        int shift = 0;
        int i = endOffset - 1;
        while (i >= 0) {
            int b = data[i--] & 0xFF;
            result |= (long)(b & 0x7F) << shift;
            shift += 7;
            if ((b & 0x80) == 0) {
                break;
            }
        }
        outLen[0] = endOffset - 1 - i;
        return result;
    }

    /**
     * Encode a BigDecimal to packed BCD bytes (big-endian, 2 digits per byte).
     * Returns the mantissa bytes and sets {@code outExponent[0]} to the exponent.
     *
     * <p>The "unholy nibble problem" is resolved by always writing an even number
     * of decimal digits in the mantissa. If the unscaled value has an odd number of
     * digits (in decimal), we prepend a zero digit, which means the exponent must
     * be adjusted by -1.
     */
    public static byte[] encodeBcd(BigDecimal value, int[] outExponent) {
        // Work with the absolute value; sign is handled by type byte
        BigDecimal absVal = value.abs();
        // unscaledValue = mantissa * 10^(-scale)
        // So decimal = unscaled * 10^(-scale)
        // We want: mantissa_bcd * 10^exponent = decimal
        // Use unscaled as the mantissa digits, and scale as the negative exponent
        BigInteger unscaled = absVal.unscaledValue();
        int scale = absVal.scale();
        String digits = unscaled.toString();
        // Remove trailing zeros from digits (they are captured in the exponent instead)
        int trailingZeros = 0;
        for (int i = digits.length() - 1; i >= 0 && digits.charAt(i) == '0'; i--) {
            trailingZeros++;
        }
        if (trailingZeros > 0 && digits.length() > trailingZeros) {
            digits = digits.substring(0, digits.length() - trailingZeros);
            scale -= trailingZeros;
        }
        // exponent = -scale
        int exponent = -scale;
        // Pad to even length. We use the trailing-zero approach (append zero, decrement exponent)
        // to align with the spec's second example: "12345" → "123450" with exponent -1.
        // This avoids ambiguity and ensures the mantissa integer directly maps to the value.
        if ((digits.length() % 2) != 0) {
            digits = digits + "0";
            exponent--;
        }
        // Pack BCD: 2 digits per byte, big-endian
        int numBytes = digits.length() / 2;
        byte[] bcd = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            int hi = digits.charAt(2 * i) - '0';
            int lo = digits.charAt(2 * i + 1) - '0';
            bcd[i] = (byte)((hi << 4) | lo);
        }
        outExponent[0] = exponent;
        return bcd;
    }

    /**
     * Decode packed BCD bytes (big-endian, 2 digits per byte) with a given exponent
     * into a BigDecimal. The mantissa bytes represent an even number of decimal digits.
     *
     * @param bcd        packed BCD bytes
     * @param exponent   the power-of-10 exponent
     * @param negative   true if the value is negative
     * @return decoded BigDecimal
     */
    public static BigDecimal decodeBcd(byte[] bcd, int exponent, boolean negative) {
        if (bcd.length == 0) {
            return negative ? BigDecimal.ZERO.negate() : BigDecimal.ZERO;
        }
        String digits = getDigits(bcd);
        // BigDecimal: unscaledValue * 10^(scale) = unscaledValue * 10^(-(-exponent))
        // exponent = power of 10 to multiply mantissa
        // scale = -exponent
        BigInteger unscaled = new BigInteger(digits);
        BigDecimal result = new BigDecimal(unscaled, -exponent);
        return negative ? result.negate() : result;
    }

    private static String getDigits(byte[] bcd) {
        StringBuilder sb = new StringBuilder(bcd.length * 2);
        for (byte b : bcd) {
            sb.append((b >> 4) & 0xF);
            sb.append(b & 0xF);
        }
        // Remove leading zeros from digit string (but keep at least one)
        String digits = sb.toString();
        int leadZeros = 0;
        while (leadZeros < digits.length() - 1 && digits.charAt(leadZeros) == '0') {
            leadZeros++;
        }
        if (leadZeros > 0) {
            digits = digits.substring(leadZeros);
        }
        return digits;
    }

    /**
     * Return the byte size needed to represent the given little-endian length value.
     * Used for binary and BCD lengths.
     */
    public static int bytesNeededForUnsigned(long len) {
        return unsignedByteWidth(len);
    }
}
