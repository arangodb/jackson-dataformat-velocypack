package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0118F0 {

    void boundsWithByteArrayInputFromBytesVpack() {
        assertInvalidByteRanges((generator, data, offset, length) ->
                generator.writeBinary(null, data, offset, length));
        assertInvalidByteRanges((generator, data, offset, length) ->
                generator.writeUTF8String(data, offset, length));
        assertUnsupported(generator -> generator.writeRawUTF8String(new byte[] { 'x' }, 0, 1));
    }

    void boundsWithByteArrayInputFromCharsVpack() {
        assertThrows(UnsupportedOperationException.class,
                () -> new VPackFactory().createGenerator(new StringWriter()));
    }

    void boundsWithCharArrayInputFromBytesVpack() {
        assertInvalidCharRanges((generator, data, offset, length) ->
                generator.writeNumber(data, offset, length));
        assertInvalidCharRanges((generator, data, offset, length) ->
                generator.writeString(data, offset, length));
        assertUnsupported(generator -> generator.writeRaw(new char[] { 'x' }, 0, 1));
        assertUnsupported(generator -> generator.writeRawValue(new char[] { 'x' }, 0, 1));
    }

    void boundsWithCharArrayInputFromCharsVpack() {
        assertThrows(UnsupportedOperationException.class,
                () -> new VPackFactory().createGenerator(new StringWriter()));
    }

    void boundsWithStringInputFromBytesVpack() {
        assertUnsupported(generator -> generator.writeRaw("x", 0, 1));
        assertUnsupported(generator -> generator.writeRawValue("x", 0, 1));
    }

    void boundsWithStringInputFromCharVpack() {
        assertThrows(UnsupportedOperationException.class,
                () -> new VPackFactory().createGenerator(new StringWriter()));
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

    void __invoke_boundsWithByteArrayInputFromBytesVpack() throws Exception {
        try {
            boundsWithByteArrayInputFromBytesVpack();
        } finally {
        }
    }


    void __invoke_boundsWithByteArrayInputFromCharsVpack() throws Exception {
        try {
            boundsWithByteArrayInputFromCharsVpack();
        } finally {
        }
    }


    void __invoke_boundsWithCharArrayInputFromBytesVpack() throws Exception {
        try {
            boundsWithCharArrayInputFromBytesVpack();
        } finally {
        }
    }


    void __invoke_boundsWithCharArrayInputFromCharsVpack() throws Exception {
        try {
            boundsWithCharArrayInputFromCharsVpack();
        } finally {
        }
    }


    void __invoke_boundsWithStringInputFromBytesVpack() throws Exception {
        try {
            boundsWithStringInputFromBytesVpack();
        } finally {
        }
    }


    void __invoke_boundsWithStringInputFromCharVpack() throws Exception {
        try {
            boundsWithStringInputFromCharVpack();
        } finally {
        }
    }

}
