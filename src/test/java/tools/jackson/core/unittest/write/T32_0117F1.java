package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0117F1 {

    void emptyArrayWriteVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(output);

        TokenStreamContext context = generator.streamWriteContext();
        assertTrue(context.inRoot());
        assertFalse(context.inArray());
        assertFalse(context.inObject());
        assertEquals(0, context.getEntryCount());
        assertEquals(0, context.getCurrentIndex());

        generator.writeStartArray();
        context = generator.streamWriteContext();
        assertFalse(context.inRoot());
        assertTrue(context.inArray());
        assertFalse(context.inObject());
        assertEquals(0, context.getEntryCount());
        assertEquals(0, context.getCurrentIndex());

        generator.writeEndArray();
        context = generator.streamWriteContext();
        assertTrue(context.inRoot());
        assertFalse(context.inArray());
        assertFalse(context.inObject());
        assertEquals(1, context.getEntryCount());
        assertEquals(0, context.getCurrentIndex());
        generator.close();

        assertArrayEquals(new byte[] { 0x01 }, output.toByteArray());
        assertTokens(output.toByteArray(), JsonToken.START_ARRAY, JsonToken.END_ARRAY);

        output.reset();
        generator = new VPackFactory().createGenerator(output);
        generator.writeStartArray();
        generator.writeStartArray();
        generator.writeEndArray();
        generator.writeEndArray();
        generator.close();

        assertArrayEquals(new byte[] { 0x02, 0x03, 0x01 }, output.toByteArray());
        assertTokens(output.toByteArray(), JsonToken.START_ARRAY, JsonToken.START_ARRAY,
                JsonToken.END_ARRAY, JsonToken.END_ARRAY);
    }

    void invalidArrayWriteVpack() throws Exception {
        JsonGenerator generator = new VPackFactory().createGenerator(new ByteArrayOutputStream());
        generator.writeStartArray();
        assertThrows(StreamWriteException.class, generator::writeEndObject);
        // VPack preserves the failed-generator state, so cleanup reports the
        // same write failure instead of silently auto-closing the array.
        assertThrows(StreamWriteException.class, generator::close);
    }

    void simpleArrayWriteVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartArray();
            generator.writeNumber(13);
            generator.writeBoolean(true);
            generator.writeString("foobar");
            generator.writeEndArray();
        }

        assertArrayEquals(bytes(0x06, 0x10, 0x03, 0x28, 0x0D, 0x1A,
                0x46, 0x66, 0x6F, 0x6F, 0x62, 0x61, 0x72, 0x03, 0x05, 0x06),
                output.toByteArray());
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(13, parser.getIntValue());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("foobar", parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
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

    void __invoke_emptyArrayWriteVpack() throws Exception {
        try {
            emptyArrayWriteVpack();
        } finally {
        }
    }


    void __invoke_invalidArrayWriteVpack() throws Exception {
        try {
            invalidArrayWriteVpack();
        } finally {
        }
    }


    void __invoke_simpleArrayWriteVpack() throws Exception {
        try {
            simpleArrayWriteVpack();
        } finally {
        }
    }

}
