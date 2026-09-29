package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackPaddingTest {
    @Test
    void everyProfilePaddingStartIsAcceptedWithoutCandidateParsing() {
        int[][] equal = { { 0x02, 1, 0 }, { 0x02, 1, 1 }, { 0x02, 1, 3 },
                { 0x02, 1, 7 }, { 0x03, 2, 0 }, { 0x03, 2, 2 }, { 0x03, 2, 6 },
                { 0x04, 4, 0 }, { 0x04, 4, 4 }, { 0x05, 8, 0 } };
        for (int[] row : equal) {
            byte[] bytes = equal(row[0], row[1], row[2], new byte[] { 0x31 });
            assertEquals(row[2], VPackLayout.analyzeFixed(bytes, 0, bytes.length).padding());
        }

        int[][] indexed = { { 0x06, 1, 0 }, { 0x06, 1, 6 },
                { 0x07, 2, 0 }, { 0x07, 2, 4 }, { 0x08, 4, 0 }, { 0x09, 8, 0 } };
        for (int[] row : indexed) {
            byte[] bytes = row[0] == 0x09
                    ? trailingArray(row[2])
                    : indexed(row[0], row[1], row[2], new byte[] { 0x31 });
            assertEquals(row[2], VPackLayout.analyzeFixed(bytes, 0, bytes.length).padding());
        }
    }

    @Test
    void nonzeroAtUnlistedStartAndExcessLeadingZerosAreRejected() {
        byte[] equalAtTwo = equal(0x02, 1, 2, new byte[] { 0x31 });
        assertThrows(StreamReadException.class,
                () -> VPackLayout.analyzeFixed(equalAtTwo, 0, equalAtTwo.length));

        byte[] indexedAtTwo = indexed(0x06, 1, 2, new byte[] { 0x31 });
        assertThrows(StreamReadException.class,
                () -> VPackLayout.analyzeFixed(indexedAtTwo, 0, indexedAtTwo.length));

        byte[] sevenZerosNoChild = equal(0x02, 1, 7, new byte[] { 0x00 });
        assertThrows(StreamReadException.class,
                () -> VPackLayout.analyzeFixed(sevenZerosNoChild, 0, sevenZerosNoChild.length));
    }

    private static byte[] equal(int marker, int width, int padding, byte[] body) {
        int length = 1 + width + padding + body.length;
        byte[] result = new byte[length];
        result[0] = (byte) marker;
        put(result, 1, width, length);
        System.arraycopy(body, 0, result, 1 + width + padding, body.length);
        return result;
    }

    private static byte[] indexed(int marker, int width, int padding, byte[] body) {
        int length = 1 + 2 * width + padding + body.length + width;
        byte[] result = new byte[length];
        result[0] = (byte) marker;
        put(result, 1, width, length);
        put(result, 1 + width, width, 1);
        int bodyStart = 1 + 2 * width + padding;
        System.arraycopy(body, 0, result, bodyStart, body.length);
        put(result, bodyStart + body.length, width, bodyStart);
        return result;
    }

    private static byte[] trailingArray(int padding) {
        int length = 1 + 8 + padding + 1 + 8 + 8;
        byte[] result = new byte[length];
        result[0] = 0x09;
        put(result, 1, 8, length);
        int bodyStart = 9 + padding;
        result[bodyStart] = 0x31;
        put(result, bodyStart + 1, 8, bodyStart);
        put(result, bodyStart + 1 + 8, 8, 1);
        return result;
    }

    private static void put(byte[] output, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) output[offset + i] = (byte) (value >>> (8 * i));
    }
}
