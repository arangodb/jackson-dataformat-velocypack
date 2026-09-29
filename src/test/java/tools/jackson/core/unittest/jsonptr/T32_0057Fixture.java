package tools.jackson.core.unittest.jsonptr;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0057Fixture {
private static final byte[] POINTER_DOCUMENT = {
            0x14, 0x48,
            0x41, 'a', 0x28, 0x7B,
            0x45, 'a', 'r', 'r', 'a', 'y',
            0x13, 0x18,
            0x31, 0x32,
            0x13, 0x04, 0x33, 0x01,
            0x35,
            0x14, 0x0E, 0x49, 'o', 'b', 'I', 'n', 'A', 'r', 'r', 'a', 'y',
            0x34, 0x01,
            0x05,
            0x42, 'o', 'b',
            0x14, 0x1D,
            0x45, 'f', 'i', 'r', 's', 't',
            0x13, 0x05, 0x19, 0x1A, 0x02,
            0x46, 's', 'e', 'c', 'o', 'n', 'd',
            0x14, 0x08, 0x43, 's', 'u', 'b', 0x37, 0x01,
            0x02,
            0x41, 'b', 0x1A,
            0x04
    };

    void literalVpackNestedContextPointersFollowTraversal() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(POINTER_DOCUMENT)) {
            assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
            assertTokenAndPath(parser, JsonToken.START_OBJECT, "");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/a");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/a");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/array");
            assertTokenAndPath(parser, JsonToken.START_ARRAY, "/array");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/array/0");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/array/1");
            assertTokenAndPath(parser, JsonToken.START_ARRAY, "/array/2");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/array/2/0");
            assertTokenAndPath(parser, JsonToken.END_ARRAY, "/array/2");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/array/3");
            assertTokenAndPath(parser, JsonToken.START_OBJECT, "/array/4");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/array/4/obInArray");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/array/4/obInArray");
            assertTokenAndPath(parser, JsonToken.END_OBJECT, "/array/4");
            assertTokenAndPath(parser, JsonToken.END_ARRAY, "/array");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/ob");
            assertTokenAndPath(parser, JsonToken.START_OBJECT, "/ob");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/ob/first");
            assertTokenAndPath(parser, JsonToken.START_ARRAY, "/ob/first");
            assertTokenAndPath(parser, JsonToken.VALUE_FALSE, "/ob/first/0");
            assertTokenAndPath(parser, JsonToken.VALUE_TRUE, "/ob/first/1");
            assertTokenAndPath(parser, JsonToken.END_ARRAY, "/ob/first");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/ob/second");
            assertTokenAndPath(parser, JsonToken.START_OBJECT, "/ob/second");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/ob/second/sub");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/ob/second/sub");
            assertTokenAndPath(parser, JsonToken.END_OBJECT, "/ob/second");
            assertTokenAndPath(parser, JsonToken.END_OBJECT, "/ob");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/b");
            assertTokenAndPath(parser, JsonToken.VALUE_TRUE, "/b");
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals("", parser.streamReadContext().pathAsPointer().toString());
            assertNull(parser.nextToken());
        }
    }
private static void assertTokenAndPath(JsonParser parser, JsonToken token,
            String path) throws Exception {
        assertEquals(token, parser.nextToken());
        assertEquals(path, parser.streamReadContext().pathAsPointer().toString());
    }
private static void assertSingleName(String name, String expected) throws Exception {
        byte[] key = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] input = new byte[key.length + 5];
        input[0] = 0x14;
        input[1] = (byte) input.length;
        input[2] = (byte) (0x40 + key.length);
        System.arraycopy(key, 0, input, 3, key.length);
        input[3 + key.length] = 0x1A;
        input[input.length - 1] = 0x01;
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(expected, parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertInteger(byte[] input, int expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }
private static void assertDouble(byte[] input, double expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(expected, parser.getDoubleValue());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_literalVpackNestedContextPointersFollowTraversal() throws Exception {
        try {
            literalVpackNestedContextPointersFollowTraversal();
        } finally {
        }
    }

}
