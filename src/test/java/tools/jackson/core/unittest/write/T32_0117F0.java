package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0117F0 {

    void intArrayVpack() throws Exception {
        assertIntArray(0, 0, 0);
        assertIntArray(0, 1, 1);
        assertIntArray(1, 0, 0);
        assertIntArray(1, 1, 1);
        assertIntArray(15, 0, 0);
        assertIntArray(15, 2, 3);
        assertIntArray(39, 0, 0);
        assertIntArray(39, 4, 0);
        assertIntArray(271, 0, 0);
        assertIntArray(271, 0, 4);
        assertIntArray(5009, 0, 0);
        assertIntArray(5009, 0, 1);
    }

    void longArrayVpack() throws Exception {
        assertLongArray(0, 0, 0);
        assertLongArray(0, 1, 1);
        assertLongArray(1, 0, 0);
        assertLongArray(1, 1, 1);
        assertLongArray(15, 0, 0);
        assertLongArray(15, 2, 3);
        assertLongArray(39, 0, 0);
        assertLongArray(39, 4, 0);
        assertLongArray(271, 0, 0);
        assertLongArray(271, 0, 4);
        assertLongArray(5009, 0, 0);
        assertLongArray(5009, 0, 1);
    }

    void doubleArrayVpack() throws Exception {
        assertDoubleArray(0, 0, 0);
        assertDoubleArray(0, 1, 1);
        assertDoubleArray(1, 0, 0);
        assertDoubleArray(1, 1, 1);
        assertDoubleArray(15, 0, 0);
        assertDoubleArray(15, 2, 3);
        assertDoubleArray(39, 0, 0);
        assertDoubleArray(39, 4, 0);
        assertDoubleArray(271, 0, 0);
        assertDoubleArray(271, 0, 4);
        assertDoubleArray(5009, 0, 0);
        assertDoubleArray(5009, 0, 1);
    }

    void stringArrayVpack() throws Exception {
        assertStringArray(0, 0, 0);
        assertStringArray(0, 1, 1);
        assertStringArray(1, 0, 0);
        assertStringArray(1, 1, 1);
        assertStringArray(15, 0, 0);
        assertStringArray(15, 2, 3);
        assertStringArray(39, 0, 0);
        assertStringArray(39, 4, 0);
        assertStringArray(271, 0, 0);
        assertStringArray(271, 0, 4);
        assertStringArray(5009, 0, 0);
        assertStringArray(5009, 0, 1);
    }
private static void assertIntArray(int elements, int pre, int post) throws Exception {
        int[] values = new int[elements + pre + post];
        for (int i = pre; i < pre + elements; ++i) values[i] = i - pre;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeArray(values, pre, elements);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertArrayHeader(parser, elements);
            for (int i = 0; i < elements; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(i, parser.getIntValue());
            }
            assertArrayEnd(parser);
        }
    }
private static void assertLongArray(int elements, int pre, int post) throws Exception {
        long[] values = new long[elements + pre + post];
        for (int i = pre; i < pre + elements; ++i) values[i] = i - pre;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeArray(values, pre, elements);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertArrayHeader(parser, elements);
            for (int i = 0; i < elements; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(i, parser.getLongValue());
            }
            assertArrayEnd(parser);
        }
    }
private static void assertDoubleArray(int elements, int pre, int post) throws Exception {
        double[] values = new double[elements + pre + post];
        for (int i = pre; i < pre + elements; ++i) values[i] = i - pre;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeArray(values, pre, elements);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertArrayHeader(parser, elements);
            for (int i = 0; i < elements; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                assertEquals((double) i, parser.getDoubleValue());
            }
            assertArrayEnd(parser);
        }
    }
private static void assertStringArray(int elements, int pre, int post) throws Exception {
        String[] values = new String[elements + pre + post];
        for (int i = pre; i < pre + elements; ++i) {
            int value = i - pre;
            values[i] = (value & 1) == 0 ? "value-" + value : "é-" + value;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeArray(values, pre, elements);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertArrayHeader(parser, elements);
            for (int i = 0; i < elements; ++i) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertEquals(values[pre + i], parser.getString());
            }
            assertArrayEnd(parser);
        }
    }
private static void assertArrayHeader(JsonParser parser, int elements) throws Exception {
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        if (elements == 0) return;
    }
private static void assertArrayEnd(JsonParser parser) throws Exception {
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertNull(parser.nextToken());
    }
private static void assertTokens(byte[] input, JsonToken... expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            for (JsonToken token : expected) assertEquals(token, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }

    void __invoke_intArrayVpack() throws Exception {
        try {
            intArrayVpack();
        } finally {
        }
    }


    void __invoke_longArrayVpack() throws Exception {
        try {
            longArrayVpack();
        } finally {
        }
    }


    void __invoke_doubleArrayVpack() throws Exception {
        try {
            doubleArrayVpack();
        } finally {
        }
    }


    void __invoke_stringArrayVpack() throws Exception {
        try {
            stringArrayVpack();
        } finally {
        }
    }

}
