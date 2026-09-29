package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0121F1 {

    void testSimpleDupsEagerlyBytesVpack() throws Exception {
        ByteArrayOutputStream simple = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(simple)) {
            writeSimple0(generator, "a");
        }
        assertArrayEquals(hex("0B 0B 02 41 61 31 41 61 32 03 06"),
                simple.toByteArray());

        ByteArrayOutputStream nested = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(nested)) {
            writeSimple1(generator, "x");
        }
        try (JsonParser parser = new VPackFactory().createParser(nested.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(3, parser.getIntValue());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            int properties = 0;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
                ++properties;
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            }
            assertEquals(5, properties);
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }

        VPackFactory strict = VPackFactory.builder()
                .enable(StreamWriteFeature.STRICT_DUPLICATE_DETECTION).build();
        assertThrows(StreamWriteException.class, () -> writeFailingSimple0(strict, "a"));
        assertThrows(StreamWriteException.class, () -> writeFailingSimple1(strict, "x"));
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

    void __invoke_testSimpleDupsEagerlyBytesVpack() throws Exception {
        try {
            testSimpleDupsEagerlyBytesVpack();
        } finally {
        }
    }

}
