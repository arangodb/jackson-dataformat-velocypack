package tools.jackson.core.unittest.read;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0082Fixture {
private static final VPackFactory FACTORY = new VPackFactory();

    void longRangeUsesLiteralIndexedArray() throws Exception {
        // Six fixed-width signed integers in an indexed array. The index is
        // relative to the marker: 3, 12, 21, 30, 39, and 48.
        byte[] input = {
                0x06, 0x3F, 0x06,
                0x27, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F,
                0x27, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80,
                0x27, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, 0x00, 0x00,
                0x27, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                0x27, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F,
                0x27, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80,
                0x03, 0x0C, 0x15, 0x1E, 0x27, 0x30
        };
        long[] expected = {
                Long.MAX_VALUE, Long.MIN_VALUE, (long) Integer.MAX_VALUE + 1L,
                (long) Integer.MIN_VALUE - 1L, Long.MAX_VALUE - 1L,
                Long.MIN_VALUE + 1L
        };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (long value : expected) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(JsonParser.NumberType.LONG, parser.getNumberType());
                assertEquals(value, parser.getLongValue());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void longWithENotationUsesLiteralBcdExponent() throws Exception {
        // Positive BCD: 1 * 10^5, the VPack equivalent of JSON 1e5.
        byte[] input = { (byte) 0xC8, 0x01, 0x05, 0x00, 0x00, 0x00, 0x01 };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(100_000L, parser.getLongValue());
            assertNull(parser.nextToken());
        }
    }

    void negativeMaxNumberLengthIsRejectedForVpackConfiguration() {
        assertThrows(IllegalArgumentException.class, () ->
                VPackFactory.builder()
                        .streamReadConstraints(StreamReadConstraints.builder()
                                .maxNumberLength(-1).build())
                        .build());
    }

    void __invoke_longRangeUsesLiteralIndexedArray() throws Exception {
        try {
            longRangeUsesLiteralIndexedArray();
        } finally {
        }
    }


    void __invoke_longWithENotationUsesLiteralBcdExponent() throws Exception {
        try {
            longWithENotationUsesLiteralBcdExponent();
        } finally {
        }
    }


    void __invoke_negativeMaxNumberLengthIsRejectedForVpackConfiguration() throws Exception {
        try {
            negativeMaxNumberLengthIsRejectedForVpackConfiguration();
        } finally {
        }
    }

}
