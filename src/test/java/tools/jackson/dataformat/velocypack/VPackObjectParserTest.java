package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VPackObjectParserTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void preservesPhysicalOrderWhileSortedIndexUsesNameOrder() throws Exception {
        byte[] body = body(pair("b", new byte[] { 0x31 }), pair("a", new byte[] { 0x32 }));
        int bodyStart = 3;
        try (JsonParser parser = factory.createParser(object(1, true, body,
                new long[] { bodyStart + 3, bodyStart }))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    @Test
    void parsesEmptyNestedAndEveryRegularObjectWidth() throws Exception {
        assertTokens(new byte[] { 0x0A }, JsonToken.START_OBJECT, JsonToken.END_OBJECT);
        for (int width : new int[] { 1, 2, 4, 8 }) {
            byte[] bytes = object(width, true, pair("x", new byte[] { 0x31 }),
                    new long[] { 1 + 2L * width });
            assertTokens(bytes, JsonToken.START_OBJECT, JsonToken.PROPERTY_NAME,
                    JsonToken.VALUE_NUMBER_INT, JsonToken.END_OBJECT);
        }
    }

    @Test
    void traversesMixedNestedArraysAndObjects() throws Exception {
        byte[] nestedArray = { 0x02, 0x04, 0x31, 0x32 };
        byte[] body = body(pair("a", nestedArray), pair("b", new byte[] { 0x0A }));
        try (JsonParser parser = factory.createParser(object(1, true, body,
                new long[] { 3, 9 }))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    @Test
    void acceptsTrailingCountSortedAndObsoleteFixtures() throws Exception {
        byte[] body = pair("x", new byte[] { 0x31 });
        assertTokens(object(8, true, body, new long[] { 17 }),
                JsonToken.START_OBJECT, JsonToken.PROPERTY_NAME,
                JsonToken.VALUE_NUMBER_INT, JsonToken.END_OBJECT);
        assertTokens(object(8, false, body, new long[] { 9 }),
                JsonToken.START_OBJECT, JsonToken.PROPERTY_NAME,
                JsonToken.VALUE_NUMBER_INT, JsonToken.END_OBJECT);
    }

    public static byte[] object(int width, boolean sorted, byte[] body, long[] indexes) {
        int marker = (sorted ? 0x0B : 0x0F) + widthIndex(width);
        boolean trailing = width == 8 && !sorted;
        int bodyStart = 1 + (trailing ? width : 2 * width);
        int indexStart = bodyStart + body.length;
        int length = indexStart + indexes.length * width + (trailing ? width : 0);
        byte[] result = new byte[length];
        result[0] = (byte) marker;
        put(result, 1, width, length);
        if (!trailing) put(result, 1 + width, width, indexes.length);
        System.arraycopy(body, 0, result, bodyStart, body.length);
        for (int i = 0; i < indexes.length; ++i) {
            put(result, indexStart + i * width, width, indexes[i]);
        }
        if (trailing) put(result, indexStart + indexes.length * width, width, indexes.length);
        return result;
    }

    public static byte[] body(byte[]... parts) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] part : parts) out.writeBytes(part);
        return out.toByteArray();
    }

    public static byte[] pair(String name, byte[] value) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        if (nameBytes.length >= 0x7F) throw new IllegalArgumentException("test name too long");
        byte[] result = new byte[1 + nameBytes.length + value.length];
        result[0] = (byte) (0x40 + nameBytes.length);
        System.arraycopy(nameBytes, 0, result, 1, nameBytes.length);
        System.arraycopy(value, 0, result, 1 + nameBytes.length, value.length);
        return result;
    }

    private static int widthIndex(int width) {
        return switch (width) {
        case 1 -> 0;
        case 2 -> 1;
        case 4 -> 2;
        case 8 -> 3;
        default -> throw new IllegalArgumentException("width");
        };
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
