package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VPackArrayParserTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void parsesEmptyEqualAndIndexedArraysFromLiteralBytes() throws Exception {
        assertTokens(new byte[] { 0x01 }, JsonToken.START_ARRAY, JsonToken.END_ARRAY);
        assertTokens(new byte[] { 0x02, 0x04, 0x31, 0x32 },
                JsonToken.START_ARRAY, JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_ARRAY);
        assertTokens(new byte[] { 0x06, 0x07, 0x02, 0x31, 0x32, 0x03, 0x04 },
                JsonToken.START_ARRAY, JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_ARRAY);
    }

    @Test
    void nextValueAndSkipChildrenUseTheValidatedTraversal() throws Exception {
        byte[] nested = { 0x02, 0x06, 0x02, 0x04, 0x31, 0x32 };
        try (JsonParser parser = factory.createParser(new ByteArrayInputStream(nested))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextValue());
            parser.skipChildren();
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    @Test
    void traversesEveryRegularArrayWidth() throws Exception {
        assertTwoValues(equal(0x02, 1, false));
        assertTwoValues(equal(0x03, 2, false));
        assertTwoValues(equal(0x04, 4, false));
        assertTwoValues(equal(0x05, 8, false));
        assertTwoValues(indexed(0x06, 1));
        assertTwoValues(indexed(0x07, 2));
        assertTwoValues(indexed(0x08, 4));
        assertTwoValues(indexed(0x09, 8));
    }

    private void assertTwoValues(byte[] input) throws Exception {
        try (JsonParser parser = factory.createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    private static byte[] equal(int marker, int width, boolean padding) {
        int start = 1 + width + (padding ? width == 1 ? 1 : width == 2 ? 2 : 0 : 0);
        int length = start + 2;
        byte[] result = new byte[length];
        result[0] = (byte) marker;
        put(result, 1, width, length);
        result[start] = 0x31;
        result[start + 1] = 0x32;
        return result;
    }

    private static byte[] indexed(int marker, int width) {
        boolean trailing = width == 8;
        int bodyStart = 1 + (trailing ? width : 2 * width);
        int indexStart = bodyStart + 2;
        int length = indexStart + 2 * width + (trailing ? width : 0);
        byte[] result = new byte[length];
        result[0] = (byte) marker;
        put(result, 1, width, length);
        if (!trailing) put(result, 1 + width, width, 2);
        result[bodyStart] = 0x31;
        result[bodyStart + 1] = 0x32;
        put(result, indexStart, width, bodyStart);
        put(result, indexStart + width, width, bodyStart + 1L);
        if (trailing) put(result, indexStart + 2 * width, width, 2);
        return result;
    }

    private static void put(byte[] result, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) result[offset + i] = (byte) (value >>> (8 * i));
    }

    private void assertTokens(byte[] input, JsonToken... expected) throws Exception {
        try (JsonParser parser = factory.createParser(input)) {
            for (JsonToken token : expected) assertEquals(token, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
}
