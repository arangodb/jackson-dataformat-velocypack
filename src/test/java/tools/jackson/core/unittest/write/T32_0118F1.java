package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0118F1 {

    void testDoubleValuesVpack() throws Exception {
        WireDouble[] cases = {
            new WireDouble(0.0, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0, 0)),
            new WireDouble(-0.0, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0, 0x80)),
            new WireDouble(1.0, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF0, 0x3F)),
            new WireDouble(-1.0, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF0, 0xBF)),
            new WireDouble(1.5, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F)),
            new WireDouble(-1.5, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0xBF)),
            new WireDouble(0.25, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0x3F)),
            new WireDouble(-0.25, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0xBF)),
            new WireDouble(123.456, bytes(0x1B, 0x77, 0xBE, 0x9F, 0x1A, 0x2F,
                    0xDD, 0x5E, 0x40)),
            new WireDouble(-123.456, bytes(0x1B, 0x77, 0xBE, 0x9F, 0x1A, 0x2F,
                    0xDD, 0x5E, 0xC0)),
            new WireDouble(1.0E10, bytes(0x1B, 0, 0, 0, 0x20, 0x5F, 0xA0, 0x02, 0x42)),
            new WireDouble(1.0E-10, bytes(0x1B, 0xBB, 0xBD, 0xD7, 0xD9, 0xDF,
                    0x7C, 0xDB, 0x3D)),
            new WireDouble(-1.0E10, bytes(0x1B, 0, 0, 0, 0x20, 0x5F, 0xA0, 0x02, 0xC2)),
            new WireDouble(-1.0E-10, bytes(0x1B, 0xBB, 0xBD, 0xD7, 0xD9, 0xDF,
                    0x7C, 0xDB, 0xBD)),
            new WireDouble(Double.MAX_VALUE, bytes(0x1B, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
                    0xFF, 0xEF, 0x7F)),
            new WireDouble(Double.MIN_VALUE, bytes(0x1B, 0x01, 0, 0, 0, 0, 0, 0, 0)),
            new WireDouble(Double.MIN_NORMAL, bytes(0x1B, 0, 0, 0, 0, 0, 0, 0x10, 0x00))
        };
        for (WireDouble test : cases) {
            assertArrayEquals(test.wire(), writeDouble(test.value()));
            assertDoubleFixture(test.wire(), test.value());
        }
    }

    void testDoubleSpecialValuesVpack() throws Exception {
        assertSpecialDouble(bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x7F), Double.NaN);
        assertSpecialDouble(bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF0, 0x7F),
                Double.POSITIVE_INFINITY);
        assertSpecialDouble(bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF0, 0xFF),
                Double.NEGATIVE_INFINITY);
    }

    void testDoubleWriteInObjectVpack() throws Exception {
        byte[] expected = bytes(0x0B, 0x13, 0x01, 0x45, 'v', 'a', 'l', 'u', 'e',
                0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F, 0x03);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartObject();
            generator.writeName("value");
            generator.writeNumber(1.5);
            generator.writeEndObject();
        }
        assertArrayEquals(expected, output.toByteArray());
        assertObjectDouble(expected, "value", 1.5);
    }

    void testDoubleWriteInArrayVpack() throws Exception {
        byte[] expected = bytes(
                0x02, 0x1D,
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0x3F,
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0xBF,
                0x1B, 0x52, 0xB8, 0x1E, 0x85, 0xEB, 0x11, 0x31, 0x40);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartArray();
            generator.writeNumber(0.25);
            generator.writeNumber(-0.25);
            generator.writeNumber(17.07);
            generator.writeEndArray();
        }
        assertArrayEquals(expected, output.toByteArray());
        assertArrayDoubles(expected, new double[] { 0.25, -0.25, 17.07 });
    }

    void testDoubleWriteToOutputStreamVpack() throws Exception {
        byte[] expected = bytes(
                0x02, 0x1D,
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0x3F,
                0x1B, 0, 0, 0, 0, 0, 0, 0xD0, 0xBF,
                0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartArray();
            generator.writeNumber(0.25);
            generator.writeNumber(-0.25);
            generator.writeNumber(1.5);
            generator.writeEndArray();
        }
        assertArrayEquals(expected, output.toByteArray());
        assertArrayDoubles(expected, new double[] { 0.25, -0.25, 1.5 });
    }

    void testDoubleWithNumbersAsStringsVpack() throws Exception {
        // The pinned source method does not enable WRITE_NUMBERS_AS_STRINGS;
        // VPack's applicable equivalent is native binary64 output.
        byte[] expected = bytes(0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F);
        assertArrayEquals(expected, writeDouble(1.5));
        assertDoubleFixture(expected, 1.5);
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }
private static byte[] writeDouble(double value) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeNumber(value);
        }
        return output.toByteArray();
    }
private static void assertDoubleFixture(byte[] wire, double expected) throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(wire)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(Double.doubleToRawLongBits(expected), VPackTestAccess.currentDoubleBits(parser));
            assertEquals(expected, parser.getDoubleValue());
            assertEquals(null, parser.nextToken());
        }
    }
private static void assertSpecialDouble(byte[] wire, double expected) throws Exception {
        assertArrayEquals(wire, writeDouble(expected));
        assertDoubleFixture(wire, expected);
    }
private static void assertObjectDouble(byte[] wire, String name, double expected)
            throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(wire)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(name, parser.nextName());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(expected, parser.getDoubleValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(null, parser.nextToken());
        }
    }
private static void assertArrayDoubles(byte[] wire, double[] expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(wire)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (double value : expected) {
                assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                assertEquals(value, parser.getDoubleValue());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(null, parser.nextToken());
        }
    }
private static void assertInvalidByteRanges(ByteCall call) {
        byte[] data = new byte[10];
        int[][] cases = { { -1, 1 }, { 4, -1 }, { 4, -6 }, { 9, 5 },
                { Integer.MAX_VALUE, 4 }, { Integer.MAX_VALUE, Integer.MAX_VALUE } };
        for (int[] range : cases) {
            assertWriteFailure((generator) -> call.call(generator, data, range[0], range[1]));
        }
        assertWriteFailure((generator) -> call.call(generator, null, 0, 3));
    }
private static void assertInvalidCharRanges(CharCall call) {
        char[] data = new char[10];
        int[][] cases = { { -1, 1 }, { 4, -1 }, { 4, -6 }, { 9, 5 },
                { Integer.MAX_VALUE, 4 }, { Integer.MAX_VALUE, Integer.MAX_VALUE } };
        for (int[] range : cases) {
            assertWriteFailure((generator) -> call.call(generator, data, range[0], range[1]));
        }
        assertWriteFailure((generator) -> call.call(generator, null, 0, 3));
    }
private static void assertWriteFailure(GeneratorCall call) {
        JsonGenerator generator = generator(new ByteArrayOutputStream());
        try {
            assertThrows(StreamWriteException.class, () -> call.call(generator));
        } finally {
            closeQuietly(generator);
        }
    }
private static void assertUnsupported(GeneratorCall call) {
        JsonGenerator generator = generator(new ByteArrayOutputStream());
        try {
            assertThrows(UnsupportedOperationException.class, () -> call.call(generator));
        } finally {
            closeQuietly(generator);
        }
    }
private static void closeQuietly(JsonGenerator generator) {
        try {
            generator.close();
        } catch (RuntimeException ignored) {
            // Failed generators retain their primary exception for close().
        }
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }
private record WireDouble(double value, byte[] wire) { }
@FunctionalInterface
    private interface ByteCall {
        void call(JsonGenerator generator, byte[] data, int offset, int length) throws Exception;
    }
@FunctionalInterface
    private interface CharCall {
        void call(JsonGenerator generator, char[] data, int offset, int length) throws Exception;
    }
@FunctionalInterface
    private interface GeneratorCall {
        void call(JsonGenerator generator) throws Exception;
    }

    void __invoke_testDoubleValuesVpack() throws Exception {
        try {
            testDoubleValuesVpack();
        } finally {
        }
    }


    void __invoke_testDoubleSpecialValuesVpack() throws Exception {
        try {
            testDoubleSpecialValuesVpack();
        } finally {
        }
    }


    void __invoke_testDoubleWriteInObjectVpack() throws Exception {
        try {
            testDoubleWriteInObjectVpack();
        } finally {
        }
    }


    void __invoke_testDoubleWriteInArrayVpack() throws Exception {
        try {
            testDoubleWriteInArrayVpack();
        } finally {
        }
    }


    void __invoke_testDoubleWriteToOutputStreamVpack() throws Exception {
        try {
            testDoubleWriteToOutputStreamVpack();
        } finally {
        }
    }


    void __invoke_testDoubleWithNumbersAsStringsVpack() throws Exception {
        try {
            testDoubleWithNumbersAsStringsVpack();
        } finally {
        }
    }

}
