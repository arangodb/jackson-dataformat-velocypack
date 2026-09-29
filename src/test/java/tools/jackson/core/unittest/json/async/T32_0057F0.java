package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0057F0 {
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

    void literalVpackSimpleFieldNamesRemainExact() throws Exception {
        String[] names = { "", "a", "ab", "abc", "abcd", "abcd1", "abcd12",
                "abcd123", "abcd1234", "abcd1234a", "abcd1234ab", "abcd1234abc",
                "abcd1234abcd", "abcd1234abcd1" };
        for (String name : names) {
            assertSingleName(name, name);
        }
    }

    void literalVpackDecodedFieldNamesReplaceJsonEscapeSpellings() throws Exception {
        assertSingleName("'foo'", "'foo'");
        assertSingleName("'foobar'", "'foobar'");
        assertSingleName("'foo & bar'", "'foo & bar'");
        assertSingleName("Something 'longer'?", "Something 'longer'?");
        assertSingleName("\u00A7", "\u00A7");
        assertSingleName("\u4567", "\u4567");
        assertSingleName("Unicode: \u00A7 and \u4567?", "Unicode: \u00A7 and \u4567?");
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

    void __invoke_literalVpackSimpleFieldNamesRemainExact() throws Exception {
        try {
            literalVpackSimpleFieldNamesRemainExact();
        } finally {
        }
    }


    void __invoke_literalVpackDecodedFieldNamesReplaceJsonEscapeSpellings() throws Exception {
        try {
            literalVpackDecodedFieldNamesReplaceJsonEscapeSpellings();
        } finally {
        }
    }

}
