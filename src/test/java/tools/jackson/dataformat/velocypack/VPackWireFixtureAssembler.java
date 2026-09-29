package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;

/** Test-only assembler. Its fields and varints are deliberately independent of production code. */
public final class VPackWireFixtureAssembler {
    private VPackWireFixtureAssembler() { }

    public static byte[] le(int width, long value) {
        if (width != 1 && width != 2 && width != 4 && width != 8) {
            throw new IllegalArgumentException("width must be 1, 2, 4, or 8");
        }
        byte[] result = new byte[width];
        for (int i = 0; i < width; ++i) {
            result[i] = (byte) (value >>> (8 * i));
        }
        return result;
    }

    public static byte[] forwardVarint(long value) {
        return varint(value, false);
    }

    public static byte[] reverseVarint(long value) {
        return varint(value, true);
    }

    private static byte[] varint(long value, boolean reverse) {
        if (value < 0 || value > 0x00FFFFFFFFFFFFFFL) {
            throw new IllegalArgumentException("value must fit in eight 7-bit groups");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        do {
            int group = (int) (value & 0x7f);
            value >>>= 7;
            out.write(group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        byte[] result = out.toByteArray();
        if (reverse) {
            for (int i = 0, j = result.length - 1; i < j; ++i, --j) {
                byte b = result[i]; result[i] = result[j]; result[j] = b;
            }
        }
        return result;
    }
}
