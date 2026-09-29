package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackLayoutTest {
    @Test
    void equalArrayRegionsHaveExactBoundariesAtEveryWidth() {
        int[][] cases = { { 0x02, 1, 7 }, { 0x03, 2, 6 }, { 0x04, 4, 4 },
                { 0x05, 8, 0 } };
        for (int[] row : cases) {
            byte[] bytes = equal(row[0], row[1], row[2], new byte[] { 0x31 });
            VPackLayout.FixedLayout layout = VPackLayout.analyzeFixed(bytes, 0, bytes.length);
            assertEquals(row[0], layout.marker());
            assertEquals(row[1], layout.width());
            assertEquals(row[2], layout.padding());
            assertEquals(bytes.length, layout.length());
            assertEquals(bytes.length, layout.end());
            assertEquals(layout.bodyStart(), layout.paddingEnd());
            assertEquals(layout.bodyEnd(), layout.indexStart());
            assertEquals(layout.bodyEnd(), layout.indexEnd());
            assertFalse(layout.hasCount());
            assertEquals(1L, layout.bodyLength());
        }
    }

    @Test
    void indexedHeaderAndTrailingLayoutsExposeCountIndexAndBodySeparately() {
        VPackLayout.FixedLayout one = VPackLayout.analyzeFixed(
                indexed(0x06, 1, 6, 1, new byte[] { 0x31 }, new long[] { 9L }), 0, 11);
        assertEquals(3L, one.headerEnd());
        assertEquals(2L, one.countStart());
        assertEquals(3L, one.countEnd());
        assertEquals(9L, one.bodyStart());
        assertEquals(10L, one.bodyEnd());
        assertEquals(10L, one.indexStart());
        assertEquals(11L, one.indexEnd());
        assertEquals(1L, one.count());

        byte[] trailing = literal("09 1a 00 00 00 00 00 00 00 31 09 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00");
        VPackLayout.FixedLayout two = VPackLayout.analyzeFixed(trailing, 0, trailing.length);
        assertEquals(8, two.width());
        assertEquals(9L, two.bodyStart());
        assertEquals(10L, two.bodyEnd());
        assertEquals(10L, two.indexStart());
        assertEquals(18L, two.indexEnd());
        assertEquals(18L, two.countStart());
        assertEquals(26L, two.countEnd());
        assertEquals(1L, two.count());
    }

    @Test
    void widthEightHeaderCountObjectDoesNotUseTrailingCountLayout() {
        byte[] bytes = literal("0e 1c 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 41 61 31 11 00 00 00 00 00 00 00");
        VPackLayout.FixedLayout layout = VPackLayout.analyzeFixed(bytes, 0, bytes.length);
        assertEquals(VPackMarker.SORTED_OBJECT, layout.classification());
        assertEquals(17L, layout.headerEnd());
        assertEquals(9L, layout.countStart());
        assertEquals(17L, layout.countEnd());
        assertEquals(17L, layout.bodyStart());
        assertEquals(20L, layout.bodyEnd());
        assertEquals(20L, layout.indexStart());
        assertEquals(28L, layout.indexEnd());
        assertEquals(1L, layout.count());
    }

    @Test
    void everyHeaderCountMarkerUsesItsDeclaredWidthAndKind() {
        int[] markers = { 0x06, 0x07, 0x08, 0x0b, 0x0c, 0x0d,
                0x0f, 0x10, 0x11 };
        for (int marker : markers) {
            int width = switch (marker) {
            case 0x06, 0x0b, 0x0f -> 1;
            case 0x07, 0x0c, 0x10 -> 2;
            case 0x08, 0x0d, 0x11 -> 4;
            default -> throw new AssertionError("unexpected marker");
            };
            boolean object = marker >= 0x0b;
            byte[] body = object ? new byte[] { 0x41, 0x61, 0x31 } : new byte[] { 0x31 };
            byte[] bytes = indexed(marker, width, 0, 1, body,
                    new long[] { 1L + 2L * width });
            VPackLayout.FixedLayout layout = VPackLayout.analyzeFixed(bytes, 0, bytes.length);
            assertEquals(width, layout.width(), "marker 0x" + Integer.toHexString(marker));
            assertEquals(object, layout.classification() == VPackMarker.SORTED_OBJECT
                    || layout.classification() == VPackMarker.UNSORTED_OBJECT);
            assertEquals(1L, layout.count());
        }

        VPackLayout.FixedLayout obsoleteTrailing = VPackLayout.analyzeFixed(
                trailingObject(), 0, trailingObject().length);
        assertEquals(VPackMarker.UNSORTED_OBJECT, obsoleteTrailing.classification());
        assertEquals(obsoleteTrailing.indexEnd(), obsoleteTrailing.countStart());
    }

    @Test
    void underflowAndCountFeasibilityAreRejectedBeforeRegionsEscape() {
        byte[] underflow = literal("09 09 00 00 00 00 00 00 00 31 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00");
        assertThrows(StreamReadException.class,
                () -> VPackLayout.analyzeFixed(underflow, 0, underflow.length));

        byte[] impossible = literal("06 05 02 00 31 00 00");
        assertThrows(StreamReadException.class,
                () -> VPackLayout.analyzeFixed(impossible, 0, impossible.length));
    }

    @Test
    void structuralUint64HighBitIsNeverConvertedToALong() {
        byte[] bytes = literal("05 00 00 00 00 00 00 80 00");
        assertThrows(StreamReadException.class,
                () -> VPackLayout.analyzeFixed(bytes, 0, bytes.length));
    }

    private static byte[] equal(int marker, int width, int padding, byte[] body) {
        long length = 1L + width + padding + body.length;
        byte[] result = new byte[(int) length];
        result[0] = (byte) marker;
        putLittleEndian(result, 1, width, length);
        System.arraycopy(body, 0, result, 1 + width + padding, body.length);
        return result;
    }

    private static byte[] indexed(int marker, int width, int padding, long count,
            byte[] body, long[] indexes) {
        long length = 1L + 2L * width + padding + body.length + (long) width * count;
        byte[] result = new byte[(int) length];
        result[0] = (byte) marker;
        putLittleEndian(result, 1, width, length);
        putLittleEndian(result, 1 + width, width, count);
        int bodyStart = 1 + 2 * width + padding;
        System.arraycopy(body, 0, result, bodyStart, body.length);
        for (int i = 0; i < indexes.length; ++i) {
            putLittleEndian(result, bodyStart + body.length + i * width, width, indexes[i]);
        }
        return result;
    }

    private static void putLittleEndian(byte[] output, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) output[offset + i] = (byte) (value >>> (8 * i));
    }

    private static byte[] trailingObject() {
        byte[] result = new byte[1 + 8 + 3 + 8 + 8];
        result[0] = 0x12;
        putLittleEndian(result, 1, 8, result.length);
        result[9] = 0x41;
        result[10] = 0x61;
        result[11] = 0x31;
        putLittleEndian(result, 12, 8, 9L);
        putLittleEndian(result, 20, 8, 1L);
        return result;
    }

    private static byte[] literal(String text) {
        String compact = text.replace(" ", "");
        byte[] result = new byte[compact.length() / 2];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte) Integer.parseInt(compact.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }
}
