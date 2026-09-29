package tools.jackson.core.unittest.io;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0028F1 {

    void binaryNumberWritingPreservesUpstreamValueSets() throws Exception {
        int[] ints = new int[251000 + 18];
        int intOffset = 0;
        int[] intEdges = {
                0, -3, 1234, -1234, 56789, -56789, 999999, -999999,
                1000000, -1000000, 10000001, -10000001, -100000012,
                100000012, 1999888777, -1999888777, Integer.MAX_VALUE,
                Integer.MIN_VALUE
        };
        for (int value : intEdges) {
            ints[intOffset++] = value;
        }
        Random intRandom = new Random(12345L);
        for (int i = 0; i < 251000; ++i) {
            ints[intOffset++] = intRandom.nextInt();
        }

        long[] longs = new long[678000 + 7];
        int longOffset = 0;
        long[] longEdges = {
                0L, 1L, -1L, Long.MAX_VALUE, Long.MIN_VALUE,
                Long.MAX_VALUE - 1L, Long.MIN_VALUE + 1L
        };
        for (long value : longEdges) {
            longs[longOffset++] = value;
        }
        Random longRandom = new Random(12345L);
        for (int i = 0; i < 678000; ++i) {
            longs[longOffset++] = ((long) longRandom.nextInt() << 32)
                    | longRandom.nextInt();
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            for (int value : ints) {
                generator.writeNumber(value);
            }
            for (long value : longs) {
                generator.writeNumber(value);
            }
        }

        // Pin representative width choices to independent wire bytes before
        // checking the complete deterministic source value families.
        byte[] encoded = output.toByteArray();
        assertArrayEquals(bytes(0x30), slice(encoded, 0, 1));
        assertArrayEquals(bytes(0x3D), slice(encoded, 1, 1));
        assertArrayEquals(bytes(0x29, 0xD2, 0x04), slice(encoded, 2, 3));
        assertArrayEquals(bytes(0x21, 0x2E, 0xFB), slice(encoded, 5, 3));

        try (JsonParser parser = new VPackFactory().createParser(encoded)) {
            for (int expected : ints) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(expected, parser.getIntValue());
            }
            for (long expected : longs) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(expected, parser.getLongValue());
            }
            assertNull(parser.nextToken());
        }
    }
private static void assertLongFixture(byte[] fixture, long expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getLongValue());
            assertNull(parser.nextToken());
        }
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = (byte) values[i];
        }
        return result;
    }
private static byte[] slice(byte[] input, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(input, offset, result, 0, length);
        return result;
    }

    void __invoke_binaryNumberWritingPreservesUpstreamValueSets() throws Exception {
        try {
            binaryNumberWritingPreservesUpstreamValueSets();
        } finally {
        }
    }

}
