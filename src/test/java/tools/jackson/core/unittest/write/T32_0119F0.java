package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0119F0 {

    void testFloatValuesVpack() throws Exception {
        FloatCase[] cases = {
            new FloatCase(0.0f, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0, 0)),
            new FloatCase(-0.0f, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0, 0x80)),
            new FloatCase(1.0f, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF0, 0x3F)),
            new FloatCase(-1.0f, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF0, 0xBF)),
            new FloatCase(1.5f, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F)),
            new FloatCase(-1.5f, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0xBF)),
            new FloatCase(0.25f, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0x3F)),
            new FloatCase(-0.25f, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0xBF)),
            new FloatCase(123.456f, bytes(0x1B, 0, 0, 0, 0x20, 0x2F, 0xDD, 0x5E, 0x40)),
            new FloatCase(-123.456f, bytes(0x1B, 0, 0, 0, 0x20, 0x2F, 0xDD, 0x5E, 0xC0)),
            new FloatCase(1.0E10f, bytes(0x1B, 0, 0, 0, 0x20, 0x5F, 0xA0, 0x02, 0x42)),
            new FloatCase(1.0E-10f, bytes(0x1B, 0, 0, 0, 0xE0, 0xDF, 0x7C, 0xDB, 0x3D)),
            new FloatCase(-1.0E10f, bytes(0x1B, 0, 0, 0, 0x20, 0x5F, 0xA0, 0x02, 0xC2)),
            new FloatCase(-1.0E-10f, bytes(0x1B, 0, 0, 0, 0xE0, 0xDF, 0x7C, 0xDB, 0xBD)),
            new FloatCase(Float.MAX_VALUE, bytes(0x1B, 0, 0, 0, 0xE0, 0xFF, 0xFF, 0xEF, 0x47)),
            new FloatCase(Float.MIN_VALUE, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xA0, 0x36)),
            new FloatCase(Float.MIN_NORMAL, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0x10, 0x38))
        };
        for (FloatCase test : cases) {
            assertArrayEquals(test.wire(), writeFloat(test.value()));
            assertFloatFixture(test.wire(), test.value());
        }
    }

    void testFloatSpecialValuesVpack() throws Exception {
        assertSpecialFloat(bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x7F), Float.NaN);
        assertSpecialFloat(bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF0, 0x7F),
                Float.POSITIVE_INFINITY);
        assertSpecialFloat(bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF0, 0xFF),
                Float.NEGATIVE_INFINITY);
    }

    void testFloatWriteInArrayVpack() throws Exception {
        byte[] expected = bytes(
                0x02, 0x1D,
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0x3F,
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0xBF,
                0x1B, 0, 0, 0, 0x80, 0xEB, 0x11, 0x31, 0x40);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartArray();
            generator.writeNumber(0.25f);
            generator.writeNumber(-0.25f);
            generator.writeNumber(17.07f);
            generator.writeEndArray();
        }
        assertArrayEquals(expected, output.toByteArray());
        assertArrayDoubles(expected, new double[] { 0.25, -0.25, (double) 17.07f });
    }

    void testFloatWriteInObjectVpack() throws Exception {
        byte[] expected = bytes(0x0B, 0x13, 0x01, 0x45, 'v', 'a', 'l', 'u', 'e',
                0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F, 0x03);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartObject();
            generator.writeName("value");
            generator.writeNumber(1.5f);
            generator.writeEndObject();
        }
        assertArrayEquals(expected, output.toByteArray());
        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals("value", parser.nextName());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(1.5, parser.getDoubleValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void testFloatWriteToOutputStreamVpack() throws Exception {
        byte[] expected = bytes(
                0x02, 0x1D,
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0x3F,
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0xBF,
                0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartArray();
            generator.writeNumber(0.25f);
            generator.writeNumber(-0.25f);
            generator.writeNumber(1.5f);
            generator.writeEndArray();
        }
        assertArrayEquals(expected, output.toByteArray());
        assertArrayDoubles(expected, new double[] { 0.25, -0.25, 1.5 });
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

    void __invoke_testFloatValuesVpack() throws Exception {
        try {
            testFloatValuesVpack();
        } finally {
        }
    }


    void __invoke_testFloatSpecialValuesVpack() throws Exception {
        try {
            testFloatSpecialValuesVpack();
        } finally {
        }
    }


    void __invoke_testFloatWriteInArrayVpack() throws Exception {
        try {
            testFloatWriteInArrayVpack();
        } finally {
        }
    }


    void __invoke_testFloatWriteInObjectVpack() throws Exception {
        try {
            testFloatWriteInObjectVpack();
        } finally {
        }
    }


    void __invoke_testFloatWriteToOutputStreamVpack() throws Exception {
        try {
            testFloatWriteToOutputStreamVpack();
        } finally {
        }
    }

}
