package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0047Fixture {
private static final byte[] THUMBS_UP_UTF8 = {
            (byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8D
    };
private static final byte[] G_CLEF_UTF8 = {
            (byte) 0xF0, (byte) 0x9D, (byte) 0x84, (byte) 0x9E
    };
private static final String THUMBS_UP = "\uD83D\uDC4D";
private static final String G_CLEF = "\uD834\uDD1E";

    void literalUtf8FieldNameVariationsRemainExact() throws Exception {
        // Prefix lengths 1-5; the empty-prefix case is covered by T32-0046.
        assertLiteralFieldName(concat(ascii("a"), THUMBS_UP_UTF8),
                "a" + THUMBS_UP);
        assertLiteralFieldName(concat(ascii("ab"), THUMBS_UP_UTF8),
                "ab" + THUMBS_UP);
        assertLiteralFieldName(concat(ascii("abc"), THUMBS_UP_UTF8),
                "abc" + THUMBS_UP);
        assertLiteralFieldName(concat(ascii("abcd"), THUMBS_UP_UTF8),
                "abcd" + THUMBS_UP);
        assertLiteralFieldName(concat(ascii("abcde"), THUMBS_UP_UTF8),
                "abcde" + THUMBS_UP);

        assertLiteralFieldName(concat(THUMBS_UP_UTF8, ascii("z")),
                THUMBS_UP + "z");
        assertLiteralFieldName(concat(ascii("x"), THUMBS_UP_UTF8, ascii("y")),
                "x" + THUMBS_UP + "y");
        assertLiteralFieldName(concat(THUMBS_UP_UTF8, THUMBS_UP_UTF8),
                THUMBS_UP + THUMBS_UP);
        assertLiteralFieldName(concat(THUMBS_UP_UTF8, G_CLEF_UTF8),
                THUMBS_UP + G_CLEF);
        assertLiteralFieldName(G_CLEF_UTF8, G_CLEF);

        assertLiteralFieldName(concat(ascii("abcdefghijklm"), THUMBS_UP_UTF8),
                "abcdefghijklm" + THUMBS_UP);
        assertLiteralFieldName(concat(ascii("abcdefghijklm"), THUMBS_UP_UTF8,
                        ascii("n")),
                "abcdefghijklm" + THUMBS_UP + "n");
        assertLiteralFieldName(concat(ascii("abcdefgh"), THUMBS_UP_UTF8,
                        ascii("ijklmnop")),
                "abcdefgh" + THUMBS_UP + "ijklmnop");
        assertLiteralFieldName(concat(ascii("abcdefgh"), THUMBS_UP_UTF8,
                        ascii("ij"), G_CLEF_UTF8, ascii("klmn")),
                "abcdefgh" + THUMBS_UP + "ij" + G_CLEF + "klmn");
    }
private static void assertLiteralFieldName(byte[] keyBytes, String expected)
            throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(singlePairObject(keyBytes))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(expected, parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static byte[] singlePairObject(byte[] keyBytes) {
        byte[] result = new byte[keyBytes.length + 10];
        result[0] = 0x14;
        result[1] = (byte) result.length;
        result[2] = (byte) (0x40 + keyBytes.length);
        System.arraycopy(keyBytes, 0, result, 3, keyBytes.length);
        int valueOffset = 3 + keyBytes.length;
        result[valueOffset] = 0x45;
        result[valueOffset + 1] = 'v';
        result[valueOffset + 2] = 'a';
        result[valueOffset + 3] = 'l';
        result[valueOffset + 4] = 'u';
        result[valueOffset + 5] = 'e';
        result[result.length - 1] = 0x01;
        return result;
    }
private static byte[] ascii(String value) {
        byte[] result = new byte[value.length()];
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c > 0x7F) {
                throw new IllegalArgumentException("not ASCII: " + value);
            }
            result[i] = (byte) c;
        }
        return result;
    }
private static byte[] concat(byte[]... parts) {
        int length = 0;
        for (byte[] part : parts) {
            length += part.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }

    void __invoke_literalUtf8FieldNameVariationsRemainExact() throws Exception {
        try {
            literalUtf8FieldNameVariationsRemainExact();
        } finally {
        }
    }

}
