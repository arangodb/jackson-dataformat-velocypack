package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0068F2 {
private static final byte[] SUPPLEMENTARY_FIELD_NAME = {
            0x14, 0x0E,
            0x44, (byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8D,
            0x45, 'v', 'a', 'l', 'u', 'e',
            0x01
    };
private static final byte[] SUPPLEMENTARY_STRING_VALUE = {
            0x14, 0x0E,
            0x45, 'f', 'i', 'e', 'l', 'd',
            0x44, (byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8D,
            0x01
    };
private static final byte[] FLOATS = {
            0x13, 0x30,
            0x1B, 0x00, 0x00, 0x00, (byte) 0xA0, 0x7F, (byte) 0xC8, (byte) 0xB5, 0x3A,
            0x1B, 0x00, 0x00, 0x00, 0x20, 0x33, 0x33, (byte) 0xF3, 0x3F,
            0x1B, 0x00, 0x00, 0x00, (byte) 0xE0, (byte) 0xFF, (byte) 0xFF, (byte) 0xEF, 0x47,
            0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xA0, 0x36,
            0x1B, 0x00, 0x00, 0x00, (byte) 0xE0, 0x04, 0x2F, (byte) 0xC3, 0x3F,
            0x05
    };

private static void assertFieldName(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("\uD83D\uDC4D", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertStringValue(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("field", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("\uD83D\uDC4D", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertFloatArray(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(7.038531e-26f, parser.getFloatValue());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(1.199999988079071f, parser.getFloatValue());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(3.4028235677973366e38f, parser.getFloatValue());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(7.006492321624086e-46f, parser.getFloatValue());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static final class OneByteInputStream extends InputStream {
        private final byte[] input;
        private int position;

        private OneByteInputStream(byte[] input) {
            this.input = input;
        }

        @Override
        public int read() {
            return position == input.length ? -1 : input[position++] & 0xFF;
        }

        @Override
        public int read(byte[] target, int offset, int length) throws IOException {
            if (position == input.length) {
                return -1;
            }
            if (length == 0) {
                return 0;
            }
            target[offset] = input[position++];
            return 1;
        }
    }

}
