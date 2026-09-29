package tools.jackson.dataformat.velocypack;

/** Forward and reverse seven-bit compact structural integers. */
final class VPackVarInts {
    static final int MAX_GROUPS = 8;

    private VPackVarInts() { }

    record Decoded(long value, int start, int end) {
        Decoded {
            if (value < 0L || start < 0 || end < start) {
                throw VPackErrors.malformed("varint", start, "invalid decoded range or value");
            }
        }

        int length() { return end - start; }
    }

    static byte[] encodeForward(long value) {
        checkValue(value, "forward varint");
        byte[] result = new byte[encodedLength(value)];
        int index = 0;
        do {
            int group = (int) (value & 0x7F);
            value >>>= 7;
            result[index++] = (byte) (group | (value == 0L ? 0 : 0x80));
        } while (value != 0L);
        return result;
    }

    static byte[] encodeReverse(long value) {
        byte[] result = encodeForward(value);
        for (int left = 0, right = result.length - 1; left < right; ++left, --right) {
            byte tmp = result[left];
            result[left] = result[right];
            result[right] = tmp;
        }
        return result;
    }

    static int encodedLength(long value) {
        checkValue(value, "varint");
        int groups = 1;
        while ((value >>>= 7) != 0L) {
            ++groups;
        }
        return groups;
    }

    static Decoded readForward(byte[] input, int offset, int end) {
        return readForward(input, offset, end, VPackBounds.MAX_COMPACT_VALUE);
    }

    static Decoded readForward(byte[] input, int offset, int end, long maximum) {
        validateBounds(input, offset, end, "forward varint");
        checkMaximum(maximum, "forward varint");
        long value = 0L;
        int groups = 0;
        int position = offset;
        while (position < end && groups < MAX_GROUPS) {
            int current = input[position++] & 0xFF;
            int group = current & 0x7F;
            value |= (long) group << (groups * 7);
            ++groups;
            if ((current & 0x80) == 0) {
                if (value > maximum) {
                    throw VPackErrors.malformed("forward varint", offset,
                            "value exceeds bounded maximum " + maximum);
                }
                return new Decoded(value, offset, position);
            }
        }
        if (position == end && groups < MAX_GROUPS) {
            throw VPackErrors.malformed("forward varint", offset, "truncated continuation");
        }
        throw VPackErrors.malformed("forward varint", offset,
                groups >= MAX_GROUPS ? "more than eight groups" : "continuation exceeds bound");
    }

    static Decoded readReverse(byte[] input, int start, int end) {
        return readReverse(input, start, end, VPackBounds.MAX_COMPACT_VALUE);
    }

    static Decoded readReverse(byte[] input, int start, int end, long maximum) {
        validateBounds(input, start, end, "reverse varint");
        checkMaximum(maximum, "reverse varint");
        long value = 0L;
        int groups = 0;
        int position = end - 1;
        while (position >= start && groups < MAX_GROUPS) {
            int current = input[position--] & 0xFF;
            int group = current & 0x7F;
            value |= (long) group << (groups * 7);
            ++groups;
            // In reverse order, a continuation bit means that another group
            // exists before this one. The first physical byte is terminal.
            if ((current & 0x80) == 0) {
                if (value > maximum) {
                    throw VPackErrors.malformed("reverse varint", position + 1,
                            "value exceeds bounded maximum " + maximum);
                }
                return new Decoded(value, position + 1, end);
            }
        }
        if (position < start && groups < MAX_GROUPS) {
            throw VPackErrors.malformed("reverse varint", start, "truncated continuation");
        }
        throw VPackErrors.malformed("reverse varint", start, "more than eight groups");
    }

    private static void validateBounds(byte[] input, int start, int end, String context) {
        if (input == null) {
            throw VPackErrors.malformed(context, "byte input is null");
        }
        if (start < 0 || end < start || end > input.length) {
            throw VPackErrors.malformed(context, start, "bounded byte range is invalid");
        }
    }

    private static void checkValue(long value, String context) {
        if (value < 0L || value > VPackBounds.MAX_COMPACT_VALUE) {
            throw VPackErrors.write(context, "value must be in 0..2^56-1");
        }
    }

    private static void checkMaximum(long maximum, String context) {
        if (maximum < 0L || maximum > VPackBounds.MAX_COMPACT_VALUE) {
            throw VPackErrors.malformed(context, "maximum must be in 0..2^56-1");
        }
    }
}
