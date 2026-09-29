package tools.jackson.dataformat.velocypack;

import tools.jackson.core.StreamReadConstraints;

/** Strict UTF-8 validation and decoding for a retained VPack byte range. */
final class VPackUtf8 {
    private VPackUtf8() { }

    /**
     * Return the exact UTF-8 byte count for a UTF-16 value. Java's UTF-8
     * encoder replaces lone surrogates; that is not legal for VPack, so this
     * scan is deliberately separate from any JDK charset encoder.
     */
    static long encodedLength(String value, String context) {
        if (value == null) {
            throw VPackErrors.write(context, "string is null");
        }
        long result = 0L;
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (ch <= 0x7F) {
                result = checkedEncodedLength(result, 1L, context);
            } else if (ch <= 0x7FF) {
                result = checkedEncodedLength(result, 2L, context);
            } else if (Character.isHighSurrogate(ch)) {
                if (i + 1 >= value.length()
                        || !Character.isLowSurrogate(value.charAt(i + 1))) {
                    throw VPackErrors.write(context, "unpaired high UTF-16 surrogate");
                }
                result = checkedEncodedLength(result, 4L, context);
                ++i;
            } else if (Character.isLowSurrogate(ch)) {
                throw VPackErrors.write(context, "unpaired low UTF-16 surrogate");
            } else {
                result = checkedEncodedLength(result, 3L, context);
            }
        }
        return result;
    }

    static long encodedLength(char[] value, int offset, int length, String context) {
        checkChars(value, offset, length, context);
        long result = 0L;
        int end = offset + length;
        for (int i = offset; i < end; ++i) {
            char ch = value[i];
            if (ch <= 0x7F) {
                result = checkedEncodedLength(result, 1L, context);
            } else if (ch <= 0x7FF) {
                result = checkedEncodedLength(result, 2L, context);
            } else if (Character.isHighSurrogate(ch)) {
                if (i + 1 >= end || !Character.isLowSurrogate(value[i + 1])) {
                    throw VPackErrors.write(context, "unpaired high UTF-16 surrogate");
                }
                result = checkedEncodedLength(result, 4L, context);
                ++i;
            } else if (Character.isLowSurrogate(ch)) {
                throw VPackErrors.write(context, "unpaired low UTF-16 surrogate");
            } else {
                result = checkedEncodedLength(result, 3L, context);
            }
        }
        return result;
    }

    @SuppressWarnings("SameParameterValue") // Preserve the caller's label in UTF-8 write errors.
    static byte[] encode(String value, String context) {
        if (value == null) {
            throw VPackErrors.write(context, "string is null");
        }
        long byteLength = encodedLength(value, context);
        byte[] result = new byte[VPackBounds.checkedInt(byteLength, context + " UTF-8 length")];
        encodeInto(value, result, 0);
        return result;
    }

    /** Encode a value after the caller has already completed a strict length scan. */
    @SuppressWarnings("DuplicatedCode") // Keep the String path allocation-free while it encodes characters.
    static void encodeInto(String value, byte[] result, int offset) {
        int out = 0;
        for (int i = 0; i < value.length(); ++i) {
            int ch = value.charAt(i);
            if (ch <= 0x7F) {
                result[offset + out++] = (byte) ch;
            } else if (ch <= 0x7FF) {
                result[offset + out++] = (byte) (0xC0 | (ch >> 6));
                result[offset + out++] = (byte) (0x80 | (ch & 0x3F));
            } else if (Character.isHighSurrogate((char) ch)) {
                int codePoint = Character.toCodePoint((char) ch, value.charAt(++i));
                result[offset + out++] = (byte) (0xF0 | (codePoint >> 18));
                result[offset + out++] = (byte) (0x80 | ((codePoint >> 12) & 0x3F));
                result[offset + out++] = (byte) (0x80 | ((codePoint >> 6) & 0x3F));
                result[offset + out++] = (byte) (0x80 | (codePoint & 0x3F));
            } else {
                result[offset + out++] = (byte) (0xE0 | (ch >> 12));
                result[offset + out++] = (byte) (0x80 | ((ch >> 6) & 0x3F));
                result[offset + out++] = (byte) (0x80 | (ch & 0x3F));
            }
        }
    }

    /** Encode a value after the caller has already completed a strict length scan. */
    @SuppressWarnings("DuplicatedCode") // The Reader path encodes a char slice without creating a String.
    static void encodeInto(char[] value, int offset, int length, byte[] result, int resultOffset) {
        int out = 0;
        int end = offset + length;
        for (int i = offset; i < end; ++i) {
            int ch = value[i];
            if (ch <= 0x7F) {
                result[resultOffset + out++] = (byte) ch;
            } else if (ch <= 0x7FF) {
                result[resultOffset + out++] = (byte) (0xC0 | (ch >> 6));
                result[resultOffset + out++] = (byte) (0x80 | (ch & 0x3F));
            } else if (Character.isHighSurrogate((char) ch)) {
                int codePoint = Character.toCodePoint((char) ch, value[++i]);
                result[resultOffset + out++] = (byte) (0xF0 | (codePoint >> 18));
                result[resultOffset + out++] = (byte) (0x80 | ((codePoint >> 12) & 0x3F));
                result[resultOffset + out++] = (byte) (0x80 | ((codePoint >> 6) & 0x3F));
                result[resultOffset + out++] = (byte) (0x80 | (codePoint & 0x3F));
            } else {
                result[resultOffset + out++] = (byte) (0xE0 | (ch >> 12));
                result[resultOffset + out++] = (byte) (0x80 | ((ch >> 6) & 0x3F));
                result[resultOffset + out++] = (byte) (0x80 | (ch & 0x3F));
            }
        }
    }

    /** Validate a caller supplied UTF-8 slice without materializing UTF-16. */
    @SuppressWarnings("SameParameterValue") // Preserve the caller's label in UTF-8 validation errors.
    static void validate(byte[] input, int offset, int length, String context) {
        VPackBounds.checkedWriteArrayRange(input, offset, length, context);
        int end = offset + length;
        for (int i = offset; i < end;) {
            int first = input[i] & 0xFF;
            if (first <= 0x7F) {
                ++i;
            } else if (first >= 0xC2 && first <= 0xDF) {
                i = validateContinuation(input, i, end, 2, context);
            } else if (first >= 0xE0 && first <= 0xEF) {
                i = validateContinuation(input, i, end, 3, context);
                int second = input[i - 2] & 0xFF;
                if ((first == 0xE0 && second < 0xA0)
                        || (first == 0xED && second > 0x9F)) {
                    throw VPackErrors.write(context, "invalid UTF-8 sequence");
                }
            } else if (first >= 0xF0 && first <= 0xF4) {
                i = validateContinuation(input, i, end, 4, context);
                int second = input[i - 3] & 0xFF;
                if ((first == 0xF0 && second < 0x90)
                        || (first == 0xF4 && second > 0x8F)) {
                    throw VPackErrors.write(context, "invalid UTF-8 sequence");
                }
            } else {
                throw VPackErrors.write(context, "invalid UTF-8 leading byte");
            }
        }
    }

    static int compareUnsigned(byte[] left, byte[] right) {
        int length = Math.min(left.length, right.length);
        for (int i = 0; i < length; ++i) {
            int a = left[i] & 0xFF;
            int b = right[i] & 0xFF;
            if (a != b) return Integer.compare(a, b);
        }
        return Integer.compare(left.length, right.length);
    }

    private static int validateContinuation(byte[] input, int start, int end, int width,
            String context) {
        if (end - start < width) {
            throw VPackErrors.write(context, "truncated UTF-8 sequence");
        }
        for (int i = start + 1; i < start + width; ++i) {
            if ((input[i] & 0xC0) != 0x80) {
                throw VPackErrors.write(context, "invalid UTF-8 continuation byte");
            }
        }
        return start + width;
    }

    private static void checkChars(char[] value, int offset, int length, String context) {
        if (value == null || offset < 0 || length < 0
                || offset > value.length - length) {
            throw VPackErrors.write(context, "character range is invalid");
        }
    }

    private static long checkedEncodedLength(long current, long addition, String context) {
        if (current > Long.MAX_VALUE - addition) {
            throw VPackErrors.write(context, "length overflows");
        }
        return current + addition;
    }

    static Decoded decode(VPackByteStore.Range range, long payloadOffset, long byteLength,
            long absoluteOffset, StreamReadConstraints constraints) {
        return decode(range, payloadOffset, byteLength, absoluteOffset, constraints, false);
    }

    static Decoded decodeName(VPackByteStore.Range range, long payloadOffset, long byteLength,
            long absoluteOffset, StreamReadConstraints constraints) {
        return decode(range, payloadOffset, byteLength, absoluteOffset, constraints, true);
    }

    private static Decoded decode(VPackByteStore.Range range, long payloadOffset, long byteLength,
            long absoluteOffset, StreamReadConstraints constraints, boolean propertyName) {
        VPackBounds.checkedRange(payloadOffset, byteLength, range.length(), "UTF-8 payload");
        long chars = 0L;
        long index = 0L;
        while (index < byteLength) {
            int first = unsigned(range.byteAt(payloadOffset + index));
            int width;
            int codePoint;
            if (first <= 0x7F) {
                width = 1;
                codePoint = first;
            } else if (first >= 0xC2 && first <= 0xDF) {
                width = 2;
                int second = continuation(range, payloadOffset, byteLength, index + 1L,
                        absoluteOffset);
                codePoint = ((first & 0x1F) << 6) | (second & 0x3F);
            } else if (first >= 0xE0 && first <= 0xEF) {
                width = 3;
                int second = continuation(range, payloadOffset, byteLength, index + 1L,
                        absoluteOffset);
                if (first == 0xE0 && second < 0xA0) {
                    invalid(absoluteOffset + index, "overlong UTF-8 sequence");
                }
                if (first == 0xED && second > 0x9F) {
                    invalid(absoluteOffset + index, "UTF-8 encodes a surrogate");
                }
                int third = continuation(range, payloadOffset, byteLength, index + 2L,
                        absoluteOffset);
                codePoint = ((first & 0x0F) << 12) | ((second & 0x3F) << 6)
                        | (third & 0x3F);
            } else if (first >= 0xF0 && first <= 0xF4) {
                width = 4;
                int second = continuation(range, payloadOffset, byteLength, index + 1L,
                        absoluteOffset);
                if (first == 0xF0 && second < 0x90) {
                    invalid(absoluteOffset + index, "overlong UTF-8 sequence");
                }
                if (first == 0xF4 && second > 0x8F) {
                    invalid(absoluteOffset + index, "UTF-8 code point exceeds U+10FFFF");
                }
                int third = continuation(range, payloadOffset, byteLength, index + 2L,
                        absoluteOffset);
                int fourth = continuation(range, payloadOffset, byteLength, index + 3L,
                        absoluteOffset);
                codePoint = ((first & 0x07) << 18) | ((second & 0x3F) << 12)
                        | ((third & 0x3F) << 6) | (fourth & 0x3F);
            } else {
                invalid(absoluteOffset + index,
                        first <= 0xBF
                                ? "unexpected UTF-8 continuation byte"
                                : "invalid UTF-8 leading byte");
                return null;
            }
            chars = VPackBounds.checkedAdd(chars, codePoint > 0xFFFF ? 2L : 1L,
                    "decoded UTF-8 character count", absoluteOffset + index);
            index += width;
        }
        int charCount = VPackBounds.checkedInt(chars, "decoded UTF-8 character count");
        if (propertyName) {
            constraints.validateNameLength(charCount);
        } else {
            constraints.validateStringLengthLong(chars);
        }
        char[] output = new char[charCount];
        index = 0L;
        int out = 0;
        while (index < byteLength) {
            int first = unsigned(range.byteAt(payloadOffset + index));
            if (first <= 0x7F) {
                output[out++] = (char) first;
                index++;
            } else if (first <= 0xDF) {
                int second = unsigned(range.byteAt(payloadOffset + index + 1L));
                output[out++] = (char) (((first & 0x1F) << 6) | (second & 0x3F));
                index += 2L;
            } else if (first <= 0xEF) {
                int second = unsigned(range.byteAt(payloadOffset + index + 1L));
                int third = unsigned(range.byteAt(payloadOffset + index + 2L));
                output[out++] = (char) (((first & 0x0F) << 12) | ((second & 0x3F) << 6)
                        | (third & 0x3F));
                index += 3L;
            } else {
                int second = unsigned(range.byteAt(payloadOffset + index + 1L));
                int third = unsigned(range.byteAt(payloadOffset + index + 2L));
                int fourth = unsigned(range.byteAt(payloadOffset + index + 3L));
                int codePoint = ((first & 0x07) << 18) | ((second & 0x3F) << 12)
                        | ((third & 0x3F) << 6) | (fourth & 0x3F);
                int value = codePoint - 0x10000;
                output[out++] = (char) (0xD800 | (value >>> 10));
                output[out++] = (char) (0xDC00 | (value & 0x3FF));
                index += 4L;
            }
        }
        return new Decoded(output);
    }

    private static int continuation(VPackByteStore.Range range, long payloadOffset,
            long byteLength, long index, long absoluteOffset) {
        if (index >= byteLength) {
            invalid(absoluteOffset + index, "truncated UTF-8 sequence");
        }
        int value = unsigned(range.byteAt(payloadOffset + index));
        if ((value & 0xC0) != 0x80) {
            invalid(absoluteOffset + index, "invalid UTF-8 continuation byte");
        }
        return value;
    }

    private static int unsigned(byte value) {
        return value & 0xFF;
    }

    private static void invalid(long offset, String detail) {
        throw VPackErrors.malformed("UTF-8 string", offset, detail);
    }

    record Decoded(char[] chars) { }
}
