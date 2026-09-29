package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0122F1 {

    void emptyObjectWriteVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(output);
        assertTrue(generator.streamWriteContext().inRoot());
        assertFalse(generator.streamWriteContext().inObject());
        assertEquals(0, generator.streamWriteContext().getEntryCount());
        generator.writeStartObject();
        assertTrue(generator.streamWriteContext().inObject());
        assertEquals(0, generator.streamWriteContext().getEntryCount());
        generator.writeEndObject();
        assertTrue(generator.streamWriteContext().inRoot());
        assertEquals(1, generator.streamWriteContext().getEntryCount());
        generator.close();

        assertArrayEquals(bytes(0x0A), output.toByteArray());
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void invalidObjectWriteVpack() throws Exception {
        JsonGenerator generator = new VPackFactory().createGenerator(
                new ByteArrayOutputStream());
        generator.writeStartObject();
        StreamWriteException exception = assertThrows(StreamWriteException.class,
                generator::writeEndArray);
        assertTrue(exception.getMessage().contains("no array is open"));
        assertThrows(StreamWriteException.class, generator::close);
    }

    void simpleObjectWriteVpack() throws Exception {
        byte[] expected = bytes(
                0x0B, 0x21, 0x03,
                0x45, 'f', 'i', 'r', 's', 't', 0x21, 0x7B, (byte) 0xFC,
                0x43, 's', 'e', 'c', 0x19,
                0x44, '3', 'r', 'd', '!', 0x47, 'y', 'e', 'e', '-', 'h', 'a', 'w',
                0x11, 0x03, 0x0C);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartObject();
            generator.writeName("first");
            generator.writeNumber(-901);
            generator.writeName("sec");
            generator.writeBoolean(false);
            generator.writeName("3rd!");
            generator.writeString("yee-haw");
            generator.writeEndObject();
        }
        assertArrayEquals(expected, output.toByteArray());

        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertProperty(parser, "first");
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-901, parser.getIntValue());
            assertProperty(parser, "sec");
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertProperty(parser, "3rd!");
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("yee-haw", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void convenienceMethodsVpack() throws Exception {
        final String text = "\"some\nString!\"";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartObject();
            generator.writeNullProperty("null");
            generator.writeBooleanProperty("bt", true);
            generator.writeBooleanProperty("bf", false);
            generator.writeNumberProperty("short", (short) -12345);
            generator.writeNumberProperty("int", Integer.MIN_VALUE + 1707);
            generator.writeNumberProperty("long", Integer.MIN_VALUE - 1707L);
            generator.writeNumberProperty("big", BigInteger.valueOf(Long.MIN_VALUE)
                    .subtract(BigInteger.valueOf(1707)));
            generator.writeNumberProperty("float", 17.07F);
            generator.writeNumberProperty("double", 17.07);
            generator.writeNumberProperty("dec", new BigDecimal("0.1"));
            generator.writeObjectPropertyStart("ob");
            generator.writeStringProperty("str", text);
            generator.writeEndObject();
            generator.writeArrayPropertyStart("arr");
            generator.writeEndArray();
            generator.writeEndObject();
        }

        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertProperty(parser, "null");
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertProperty(parser, "bt");
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertProperty(parser, "bf");
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertInteger(parser, "short", -12345);
            assertInteger(parser, "int", Integer.MIN_VALUE + 1707L);
            assertInteger(parser, "long", Integer.MIN_VALUE - 1707L);
            assertProperty(parser, "big");
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.valueOf(1707)),
                    parser.getBigIntegerValue());
            assertProperty(parser, "float");
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals((double) 17.07F, parser.getDoubleValue());
            assertProperty(parser, "double");
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(17.07, parser.getDoubleValue());
            assertProperty(parser, "dec");
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(new BigDecimal("0.1"), parser.getDecimalValue());
            assertProperty(parser, "ob");
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertProperty(parser, "str");
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(text, parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertProperty(parser, "arr");
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void convenienceMethodsWithNullsVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartObject();
            generator.writeStringProperty("str", null);
            generator.writeNumberProperty("big", (BigInteger) null);
            generator.writeNumberProperty("dec", (BigDecimal) null);
            generator.writePOJOProperty("obj", null);
            generator.writeBinaryProperty("bin", new byte[] { 1, 2 });
            generator.writeEndObject();
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertProperty(parser, "str");
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertProperty(parser, "big");
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertProperty(parser, "dec");
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertProperty(parser, "obj");
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertProperty(parser, "bin");
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertArrayEquals(new byte[] { 1, 2 }, parser.getBinaryValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertInteger(JsonParser parser, String name, long value)
            throws Exception {
        assertProperty(parser, name);
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(value, parser.getLongValue());
    }
private static void assertProperty(JsonParser parser, String name) throws Exception {
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(name, parser.getString());
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }

    void __invoke_emptyObjectWriteVpack() throws Exception {
        try {
            emptyObjectWriteVpack();
        } finally {
        }
    }


    void __invoke_invalidObjectWriteVpack() throws Exception {
        try {
            invalidObjectWriteVpack();
        } finally {
        }
    }


    void __invoke_simpleObjectWriteVpack() throws Exception {
        try {
            simpleObjectWriteVpack();
        } finally {
        }
    }


    void __invoke_convenienceMethodsVpack() throws Exception {
        try {
            convenienceMethodsVpack();
        } finally {
        }
    }


    void __invoke_convenienceMethodsWithNullsVpack() throws Exception {
        try {
            convenienceMethodsWithNullsVpack();
        } finally {
        }
    }

}
