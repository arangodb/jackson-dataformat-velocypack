package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackMarkerTest {
    @Test
    void everyByteIsClassifiedExactlyOnce() {
        VPackMarker[] expected = new VPackMarker[256];
        expected[0x00] = VPackMarker.PADDING;
        expected[0x01] = VPackMarker.EMPTY_ARRAY;
        fill(expected, 0x02, 0x05, VPackMarker.EQUAL_ARRAY);
        fill(expected, 0x06, 0x09, VPackMarker.INDEXED_ARRAY);
        expected[0x0A] = VPackMarker.EMPTY_OBJECT;
        fill(expected, 0x0B, 0x0E, VPackMarker.SORTED_OBJECT);
        fill(expected, 0x0F, 0x12, VPackMarker.UNSORTED_OBJECT);
        expected[0x13] = VPackMarker.COMPACT_ARRAY;
        expected[0x14] = VPackMarker.COMPACT_OBJECT;
        fill(expected, 0x15, 0x17, VPackMarker.RESERVED);
        expected[0x18] = VPackMarker.NULL;
        expected[0x19] = VPackMarker.FALSE;
        expected[0x1A] = VPackMarker.TRUE;
        expected[0x1B] = VPackMarker.DOUBLE;
        expected[0x1C] = VPackMarker.UTC_DATE;
        expected[0x1D] = VPackMarker.EXTERNAL_POINTER;
        expected[0x1E] = VPackMarker.MIN_KEY;
        expected[0x1F] = VPackMarker.MAX_KEY;
        fill(expected, 0x20, 0x27, VPackMarker.SIGNED_INTEGER);
        fill(expected, 0x28, 0x2F, VPackMarker.UNSIGNED_INTEGER);
        fill(expected, 0x30, 0x39, VPackMarker.SMALL_POSITIVE);
        fill(expected, 0x3A, 0x3F, VPackMarker.SMALL_NEGATIVE);
        fill(expected, 0x40, 0xBE, VPackMarker.SHORT_STRING);
        expected[0xBF] = VPackMarker.LONG_STRING;
        fill(expected, 0xC0, 0xC7, VPackMarker.BINARY);
        fill(expected, 0xC8, 0xCF, VPackMarker.POSITIVE_BCD);
        fill(expected, 0xD0, 0xD7, VPackMarker.NEGATIVE_BCD);
        fill(expected, 0xD8, 0xFF, VPackMarker.RESERVED);

        for (int marker = 0; marker <= 0xFF; ++marker) {
            assertEquals(expected[marker], VPackMarker.classify(marker),
                    Integer.toHexString(marker));
        }
        assertThrows(StreamReadException.class, () -> VPackMarker.classify(-1));
        assertThrows(StreamReadException.class, () -> VPackMarker.classify(0x100));
    }

    @Test
    void markerFamiliesExposeIndependentContainerAndScalarWidths() {
        int[] containerStarts = { 0x02, 0x06, 0x0B, 0x0F };
        for (int base : containerStarts) {
            for (int index = 0; index < 4; ++index) {
                assertEquals(new int[] { 1, 2, 4, 8 }[index],
                        VPackMarker.width(base + index), Integer.toHexString(base + index));
            }
        }
        int[] scalarStarts = { 0x20, 0x28, 0xC0, 0xC8, 0xD0 };
        for (int base : scalarStarts) {
            for (int index = 0; index < 8; ++index) {
                assertEquals(index + 1, VPackMarker.width(base + index),
                        Integer.toHexString(base + index));
            }
        }
        for (int marker = 0; marker <= 0xFF; ++marker) {
            if (!isWidthMarker(marker)) {
                final int invalid = marker;
                assertThrows(StreamReadException.class, () -> VPackMarker.width(invalid),
                        Integer.toHexString(marker));
            }
        }
    }

    @Test
    void supportedAndUnsupportedMarkersAreDistinguishedWithoutPayloadReads() {
        assertTrue(VPackMarker.requireSupported(0x01, "marker") == VPackMarker.EMPTY_ARRAY);
        assertThrows(StreamReadException.class, () -> VPackMarker.requireSupported(0x00, "marker"));
        assertThrows(StreamReadException.class, () -> VPackMarker.requireSupported(0x1D, "marker"));
        assertThrows(StreamReadException.class, () -> VPackMarker.requireSupported(0xFF, "marker"));
    }

    @Test
    void containerClassificationIsCentralized() {
        for (int marker : new int[] { 0x01, 0x02, 0x06, 0x0A, 0x0B, 0x0F, 0x13, 0x14 }) {
            assertTrue(VPackMarker.isContainer(marker), Integer.toHexString(marker));
        }
        for (int marker : new int[] { 0x00, 0x18, 0x1B, 0x20, 0x40, 0xFF }) {
            assertFalse(VPackMarker.isContainer(marker), Integer.toHexString(marker));
        }
    }

    private static void fill(VPackMarker[] expected, int first, int last, VPackMarker kind) {
        for (int marker = first; marker <= last; ++marker) {
            expected[marker] = kind;
        }
    }

    private static boolean isWidthMarker(int marker) {
        return marker >= 0x02 && marker <= 0x09
                || marker >= 0x0B && marker <= 0x12
                || marker >= 0x20 && marker <= 0x2F
                || marker >= 0xC0 && marker <= 0xD7;
    }
}
