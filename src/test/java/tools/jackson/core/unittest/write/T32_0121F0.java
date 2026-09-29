package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0121F0 {

    void copyRootTokensVpack() throws Exception {
        // Independent literal roots: string, true, false, double 2.0, null,
        // and unsigned integer 1234567890123.
        byte[] input = hex(
                "51 74 65 78 74 0A 6F 6E 20 74 77 6F 20 6C 69 6E 65 73 "
                + "1A 19 1B 00 00 00 00 00 00 00 40 18 "
                + "2F CB 04 FB 71 1F 01 00 00");
        byte[] expected = hex(
                "51 74 65 78 74 0A 6F 6E 20 74 77 6F 20 6C 69 6E 65 73 "
                + "1A 19 1B 00 00 00 00 00 00 00 40 18 "
                + "2D CB 04 FB 71 1F 01");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = new VPackFactory().createParser(input);
                JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            generator.copyCurrentEvent(parser);
            assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
            assertEquals("text\non two lines", parser.getString());

            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            generator.copyCurrentEvent(parser);
            assertEquals(JsonToken.VALUE_TRUE, parser.currentToken());

            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            generator.copyCurrentEvent(parser);
            assertEquals(JsonToken.VALUE_FALSE, parser.currentToken());

            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            generator.copyCurrentEvent(parser);
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.currentToken());
            assertEquals(2.0, parser.getDoubleValue());

            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            generator.copyCurrentEvent(parser);
            assertEquals(JsonToken.VALUE_NULL, parser.currentToken());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            generator.copyCurrentEvent(parser);
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.currentToken());
            assertEquals(1234567890123L, parser.getLongValue());
            assertNull(parser.nextToken());
        }
        assertArrayEquals(expected, output.toByteArray());
    }
private static final int LONG_OBJECT_ENTRIES = 6_000;
private static void writeLongObject(JsonGenerator generator) {
        generator.writeStartObject(LONG_OBJECT_ENTRIES);
        for (int i = 0; i < LONG_OBJECT_ENTRIES; ++i) {
            generator.writeName(longObjectName(i));
            generator.writeNumber(i % 20 - 1);
        }
        generator.writeEndObject();
    }
private static String longObjectName(int index) {
        return "field" + index;
    }
private static void writeSimple0(JsonGenerator generator, String name) {
        generator.writeStartObject();
        generator.writeNumberProperty(name, 1);
        generator.writeNumberProperty(name, 2);
        generator.writeEndObject();
    }
private static void writeSimple1(JsonGenerator generator, String name) {
        generator.writeStartArray();
        generator.writeNumber(3);
        generator.writeStartObject();
        generator.writeNumberProperty("foo", 1);
        generator.writeNumberProperty("bar", 1);
        generator.writeNumberProperty(name, 1);
        generator.writeNumberProperty("bar2", 1);
        generator.writeNumberProperty(name, 2);
        generator.writeEndObject();
        generator.writeEndArray();
    }
private static void writeFailingSimple0(VPackFactory factory, String name) {
        try (JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream())) {
            writeSimple0(generator, name);
        }
    }
private static void writeFailingSimple1(VPackFactory factory, String name) {
        try (JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream())) {
            writeSimple1(generator, name);
        }
    }
private static byte[] hex(String value) {
        String[] parts = value.trim().split("\\s+");
        byte[] result = new byte[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            result[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return result;
    }

    void __invoke_copyRootTokensVpack() throws Exception {
        try {
            copyRootTokensVpack();
        } finally {
        }
    }

}
