package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0120F2 {
private static void assertStringWrite(String value, byte[] expected,
            boolean chars, int offset) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            if (chars) {
                char[] buffer = new char[value.length() + 20];
                value.getChars(0, value.length(), buffer, offset);
                generator.writeString(buffer, offset, value.length());
            } else {
                generator.writeString(value);
            }
        }
        assertArrayEquals(expected, output.toByteArray());
        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(value, parser.getString());
            assertNull(parser.nextToken());
        }
    }

    void copyArrayTokensVpack() throws Exception {
        // Independent literal: root 123 followed by [1, null, [false, 1234567890124]].
        byte[] input = hex("28 7B 06 15 03 31 18 06 0D 02 18 2D CC 04 FB 71 1F 01 03 04 03 04 05");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = new VPackFactory().createParser(input);
                JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            generator.copyCurrentEvent(parser);
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.currentToken());
            assertEquals(123, parser.getIntValue());

            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            generator.copyCurrentStructure(parser);
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
        }
        assertArrayEquals(input, output.toByteArray());
    }

    void copyNumericTokensExactlyVpack() throws Exception {
        // Independent literal: the same exact decimal is present at a and b[0].d.
        byte[] input = hex(
                "0B 3D 02 41 61 C8 0E E5 FF FF FF 01 23 45 67 89 12 34 56 78 91 23 45 67 89 "
                + "41 62 02 20 0B 1E 02 41 63 18 41 64 C8 0E E5 FF FF FF "
                + "01 23 45 67 89 12 34 56 78 91 23 45 67 89 03 06 03 19");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = new VPackFactory().createParser(input);
                JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            generator.copyCurrentStructureExact(parser);
            assertEquals(JsonToken.END_OBJECT, parser.currentToken());
        }
        assertArrayEquals(input, output.toByteArray());

        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals("a", parser.nextName());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals("0.123456789123456789123456789", parser.getString());
            assertEquals(27, parser.getDecimalValue().scale());
            assertEquals("b", parser.nextName());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals("c", parser.nextName());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals("d", parser.nextName());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(27, parser.getDecimalValue().scale());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void copyObjectTokensVpack() throws Exception {
        // Independent literal: {"a":1,"b":[{"c":null,"d":0.25}]}.
        byte[] input = hex(
                "0B 1F 02 41 61 31 41 62 02 15 0B 13 02 41 63 18 41 64 "
                + "1B 00 00 00 00 00 00 D0 3F 03 06 03 06");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = new VPackFactory().createParser(input);
                JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            generator.copyCurrentStructure(parser);
            assertEquals(JsonToken.END_OBJECT, parser.currentToken());
        }
        assertArrayEquals(input, output.toByteArray());

        byte[] fieldInput = hex("0B 0B 02 41 61 31 41 62 18 03 06");
        ByteArrayOutputStream fieldOutput = new ByteArrayOutputStream();
        try (JsonParser parser = new VPackFactory().createParser(fieldInput);
                JsonGenerator generator = new VPackFactory().createGenerator(fieldOutput)) {
            generator.writeStartObject();
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            generator.copyCurrentStructure(parser);
            generator.writeEndObject();
        }
        assertArrayEquals(hex("0B 07 01 41 61 31 03"), fieldOutput.toByteArray());
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = (byte) values[i];
        }
        return result;
    }
private static byte[] hex(String value) {
        String[] parts = value.trim().split("\\s+");
        byte[] result = new byte[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            result[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return result;
    }
private static final class TrackingOutputStream extends OutputStream {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int flushes;
        boolean closed;

        @Override
        public void write(int value) {
            bytes.write(value);
        }

        @Override
        public void write(byte[] value, int offset, int length) {
            bytes.write(value, offset, length);
        }

        @Override
        public void flush() throws IOException {
            ++flushes;
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    void __invoke_copyArrayTokensVpack() throws Exception {
        try {
            copyArrayTokensVpack();
        } finally {
        }
    }


    void __invoke_copyNumericTokensExactlyVpack() throws Exception {
        try {
            copyNumericTokensExactlyVpack();
        } finally {
        }
    }


    void __invoke_copyObjectTokensVpack() throws Exception {
        try {
            copyObjectTokensVpack();
        } finally {
        }
    }

}
