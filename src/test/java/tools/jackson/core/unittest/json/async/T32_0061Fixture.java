package tools.jackson.core.unittest.json.async;

import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0061Fixture {
private static final int TOO_DEEP_PATH = 25_000;

    void literalVpackUnicodeSurrogateValuesAndNamesRetainExactTextAndSkip()
            throws Exception {
        assertUnicodeDocument(28);
        assertUnicodeDocument(53);
        assertUnicodeDocument(230);
        assertUnicodeDocument(700);
        assertUnicodeDocument(9_600);
    }
private static void assertUnicodeDocument(int length) throws Exception {
        String expected = _generateUnicode(length);
        byte[] value = string(expected);

        // Literal root string: decode it, then advance past it as the source
        // test does when exercising its skip path.
        try (JsonParser parser = new VPackFactory().createParser(value)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertNull(parser.nextToken());
        }
        try (JsonParser parser = new VPackFactory().createParser(value)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertNull(parser.nextToken());
        }

        // The same literal text as an object key exercises field-name
        // decoding and skipping the associated value.
        byte[] object = indexedObject(expected);
        try (JsonParser parser = new VPackFactory().createParser(object)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(expected, parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
        try (JsonParser parser = new VPackFactory().createParser(object)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
private static String _generateUnicode(int length) {
        final String surrogateChars = "\ud834\udd1e";
        StringBuilder sb = new StringBuilder(length + 200);
        while (sb.length() < length) {
            sb.append(surrogateChars);
            sb.append(sb.length());
            if ((sb.length() & 1) == 1) {
                sb.append('\u00A3');
            } else {
                sb.append('\u3800');
            }
        }
        return sb.toString();
    }
private static byte[] string(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length <= 126) {
            byte[] result = new byte[payload.length + 1];
            result[0] = (byte) (0x40 + payload.length);
            System.arraycopy(payload, 0, result, 1, payload.length);
            return result;
        }
        byte[] result = new byte[payload.length + 9];
        result[0] = (byte) 0xBF;
        putLittleEndian(result, 1, payload.length, 8);
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
private static byte[] indexedObject(String name) {
        byte[] encodedName = string(name);
        int length = 1 + 4 + 4 + encodedName.length + 1 + 4;
        byte[] result = new byte[length];
        result[0] = 0x0D;
        putLittleEndian(result, 1, length, 4);
        putLittleEndian(result, 5, 1, 4);
        System.arraycopy(encodedName, 0, result, 9, encodedName.length);
        result[9 + encodedName.length] = 0x1A;
        putLittleEndian(result, 10 + encodedName.length, 9, 4);
        return result;
    }
private static void putLittleEndian(byte[] target, int offset, long value,
            int width) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
private static void assertDeepPointer(String pathExpr) {
        JsonPointer pointer = JsonPointer.compile(pathExpr);
        assertNotNull(pointer);
        assertEquals(pathExpr, pointer.toString());

        JsonPointer current = pointer;
        while ((current = current.tail()) != null) {
            String actual = current.toString();
            String expected = pathExpr.substring(pathExpr.length() - actual.length());
            assertEquals(expected, actual);
        }
    }
private static String _generatePath(int depth, boolean escaped) {
        StringBuilder sb = new StringBuilder(4 * depth);
        for (int i = 0; i < depth; ++i) {
            sb.append('/')
                    .append((char) ('a' + i % 25))
                    .append(i);
            if (escaped) {
                switch (i & 7) {
                case 1:
                    sb.append("~0x");
                    break;
                case 4:
                    sb.append("~1y");
                    break;
                default:
                    break;
                }
            }
        }
        return sb.toString();
    }

    void __invoke_literalVpackUnicodeSurrogateValuesAndNamesRetainExactTextAndSkip() throws Exception {
        try {
            literalVpackUnicodeSurrogateValuesAndNamesRetainExactTextAndSkip();
        } finally {
        }
    }

}
