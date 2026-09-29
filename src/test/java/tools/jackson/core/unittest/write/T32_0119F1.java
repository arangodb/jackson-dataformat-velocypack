package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0119F1 {

    void fieldValueWritesVpack() throws Exception {
        byte[] expected = bytes(
                0x0B, 0x52, 0x07,
                0x45, 's', 'h', 'o', 'r', 't', 0x33,
                0x43, 'i', 'n', 't', 0x33,
                0x44, 'l', 'o', 'n', 'g', 0x33,
                0x43, 'b', 'i', 'g', 0x29, 0xAB, 0x06,
                0x46, 'd', 'o', 'u', 'b', 'l', 'e',
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0x3F,
                0x45, 'f', 'l', 'o', 'a', 't',
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0xBF,
                0x47, 'd', 'e', 'c', 'i', 'm', 'a', 'l',
                0xC8, 0x02, 0xFE, 0xFF, 0xFF, 0xFF, 0x17, 0x07,
                0x15, 0x3B, 0x1C, 0x2C, 0x0A, 0x0F, 0x03);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartObject();
            generator.writeNumberProperty("short", (short) 3);
            generator.writeNumberProperty("int", 3);
            generator.writeNumberProperty("long", 3L);
            generator.writeNumberProperty("big", java.math.BigInteger.valueOf(1707));
            generator.writeNumberProperty("double", 0.25);
            generator.writeNumberProperty("float", -0.25f);
            generator.writeNumberProperty("decimal", new BigDecimal("17.07"));
            generator.writeEndObject();
        }
        assertArrayEquals(expected, output.toByteArray());
        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertField(parser, "short", "3");
            assertField(parser, "int", "3");
            assertField(parser, "long", "3");
            assertField(parser, "big", "1707");
            assertField(parser, "double", "0.25", JsonToken.VALUE_NUMBER_FLOAT);
            assertField(parser, "float", "-0.25", JsonToken.VALUE_NUMBER_FLOAT);
            assertField(parser, "decimal", "17.07", JsonToken.VALUE_NUMBER_FLOAT);
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void intValueWriteVpack() throws Exception {
        int[] values = { 0, 1, -9, 32, -32, 57, 189, 2017, -9999, 13240,
                123456, 1111111, 22222222, 123456789, 7300999, -7300999,
                99300999, -99300999, 999300999, -999300999, 1000300999,
                2000500126, -1000300999, -2000500126, Integer.MIN_VALUE,
                Integer.MAX_VALUE };
        for (int value : values) assertInteger(value);
    }

    void longValueWriteVpack() throws Exception {
        long[] values = { 0L, 1L, -1L, 2000100345L, -12005002294L,
                5111222333L, -5111222333L, 65111222333L, -65111222333L,
                123456789012L, -123456789012L, 123456789012345L,
                -123456789012345L, 123456789012345789L,
                -123456789012345789L, Long.MIN_VALUE, Long.MAX_VALUE };
        for (long value : values) assertLong(value);
    }

    void getOutputTargetVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            assertSame(output, generator.streamWriteOutputTarget());
        }
    }
private static byte[] writeFloat(float value) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeNumber(value);
        }
        return output.toByteArray();
    }
private static void assertFloatFixture(byte[] wire, float expected) throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(wire)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(Double.doubleToRawLongBits((double) expected), VPackTestAccess.currentDoubleBits(parser));
            assertEquals((double) expected, parser.getDoubleValue());
            assertNull(parser.nextToken());
        }
    }
private static void assertSpecialFloat(byte[] wire, float expected) throws Exception {
        assertArrayEquals(wire, writeFloat(expected));
        assertFloatFixture(wire, expected);
    }
private static void assertArrayDoubles(byte[] wire, double[] expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(wire)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (double value : expected) {
                assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                assertEquals(value, parser.getDoubleValue());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertField(JsonParser parser, String name, String value) throws Exception {
        assertField(parser, name, value, JsonToken.VALUE_NUMBER_INT);
    }
private static void assertField(JsonParser parser, String name, String value,
            JsonToken token) throws Exception {
        assertEquals(name, parser.nextName());
        assertEquals(token, parser.nextToken());
        assertEquals(value, parser.getString());
    }
private static void assertInteger(int value) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeNumber(value);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(Integer.toString(value), parser.getString());
            assertEquals(value, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }
private static void assertLong(long value) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeNumber(value);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(Long.toString(value), parser.getString());
            assertEquals(value, parser.getLongValue());
            assertNull(parser.nextToken());
        }
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }
private record FloatCase(float value, byte[] wire) { }

    void __invoke_fieldValueWritesVpack() throws Exception {
        try {
            fieldValueWritesVpack();
        } finally {
        }
    }


    void __invoke_intValueWriteVpack() throws Exception {
        try {
            intValueWriteVpack();
        } finally {
        }
    }


    void __invoke_longValueWriteVpack() throws Exception {
        try {
            longValueWriteVpack();
        } finally {
        }
    }


    void __invoke_getOutputTargetVpack() throws Exception {
        try {
            getOutputTargetVpack();
        } finally {
        }
    }

}
