package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0042F1 {

    void nonLatin1CharactersAreLiteralUtf8ForStringCharAndUtf8Overloads() throws Exception {
        String value = "Line\u2028feed, \u00D6l!";
        byte[] expected = {
                0x51, 'L', 'i', 'n', 'e', (byte) 0xE2, (byte) 0x80, (byte) 0xA8,
                'f', 'e', 'e', 'd', ',', ' ', (byte) 0xC3, (byte) 0x96, 'l', '!'
        };
        byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
        assertStringBytes(g -> g.writeString(value), expected);
        char[] chars = value.toCharArray();
        assertStringBytes(g -> g.writeString(chars, 0, chars.length), expected);
        assertStringBytes(g -> g.writeUTF8String(utf8, 0, utf8.length), expected);
    }

    void charArrayWritingPreservesAnEmbeddedNul() throws Exception {
        char[] value = { '\0' };
        byte[] expected = { 0x41, 0x00 };
        assertStringBytes(g -> g.writeString(value, 0, value.length), expected);
    }

    void validTextEscapeSpellingsBecomeLiteralVpackCharacters() throws Exception {
        // An indexed array assembled independently from four literal strings:
        // "LF=\n", "NULL:\0!", U+0123, and "AC".
        byte[] input = {
                0x06, 0x1A, 0x04,
                0x44, 'L', 'F', '=', 0x0A,
                0x47, 'N', 'U', 'L', 'L', ':', 0x00, '!',
                0x42, (byte) 0xC4, (byte) 0xA3,
                0x42, 'A', 'C',
                0x03, 0x08, 0x10, 0x13
        };
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("LF=\n", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("NULL:\0!", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("\u0123", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("AC", parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void jsonEscapeSyntaxIsNotInterpretedByVpack() throws Exception {
        assertLiteralString(new byte[] {
                0x4C, 'L', 'i', 'n', 'e', 'f', 'e', 'e', 'd', ':', ' ', 0x0A, '.'
        }, "Linefeed: \n.");
        assertLiteralString(new byte[] { 0x46, '\\', 'u', '4', '1', '=', 'A' }, "\\u41=A");
        assertLiteralString(new byte[] {
                0x47, '\\', 'u', (byte) 0xC2, (byte) 0x80, '.', '.', '.'
        }, "\\u\u0080...");
        assertLiteralString(new byte[] {
                0x4A, '\\', 'u', '0', '0', '4', '1', '1', '2', '3', '4'
        }, "\\u00411234");
    }

    void quotedPropertyNamesAreLiteralAndTheNumericValueRemainsExact() throws Exception {
        // Sorted indexed object with the literal property name ".
        byte[] input = {
                0x0B, 0x0B, 0x01,
                0x41, '"', 0x2B, 0x15, (byte) 0xCD, 0x5B, 0x07,
                0x03
        };
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("\"", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(123456789, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void longUnicodeStringUsesLiteralLongStringEncoding() throws Exception {
        StringBuilder builder = new StringBuilder();
        while (builder.length() < 2000) {
            builder.append("\u65E5\u672C\u8A9E");
        }
        String value = builder.toString();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeString(value);
        }
        byte[] expected = literalString(value);
        assertEquals(0xBF, expected[0] & 0xFF);
        assertArrayEquals(expected, output.toByteArray());
    }
private static void assertStringBytes(WriterCall call, byte[] expected) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            call.write(generator);
        }
        assertArrayEquals(expected, output.toByteArray());
    }
private static void assertLiteralString(byte[] input, String expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertNull(parser.nextToken());
        }
    }
private static void assertScalarString(byte[] input, String expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertNull(parser.nextToken());
        }
    }
private static byte[] literalString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length <= 126) {
            byte[] result = new byte[payload.length + 1];
            result[0] = (byte) (0x40 + payload.length);
            System.arraycopy(payload, 0, result, 1, payload.length);
            return result;
        }
        byte[] result = new byte[payload.length + 9];
        result[0] = (byte) 0xBF;
        for (int i = 0; i < 8; ++i) {
            result[i + 1] = (byte) (((long) payload.length) >>> (8 * i));
        }
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
private static String mediumText(int minimumLength) {
        StringBuilder result = new StringBuilder(minimumLength + 1000);
        java.util.Random random = new java.util.Random(minimumLength);
        do {
            switch (random.nextInt(4)) {
            case 0 -> result.append(" foo");
            case 1 -> result.append(" bar");
            case 2 -> result.append(result.length());
            default -> result.append(" \"stuff\"");
            }
        } while (result.length() < minimumLength);
        return result.toString();
    }
@FunctionalInterface
    private interface WriterCall {
        void write(JsonGenerator generator) throws Exception;
    }

    void __invoke_nonLatin1CharactersAreLiteralUtf8ForStringCharAndUtf8Overloads() throws Exception {
        try {
            nonLatin1CharactersAreLiteralUtf8ForStringCharAndUtf8Overloads();
        } finally {
        }
    }


    void __invoke_charArrayWritingPreservesAnEmbeddedNul() throws Exception {
        try {
            charArrayWritingPreservesAnEmbeddedNul();
        } finally {
        }
    }


    void __invoke_validTextEscapeSpellingsBecomeLiteralVpackCharacters() throws Exception {
        try {
            validTextEscapeSpellingsBecomeLiteralVpackCharacters();
        } finally {
        }
    }


    void __invoke_jsonEscapeSyntaxIsNotInterpretedByVpack() throws Exception {
        try {
            jsonEscapeSyntaxIsNotInterpretedByVpack();
        } finally {
        }
    }


    void __invoke_quotedPropertyNamesAreLiteralAndTheNumericValueRemainsExact() throws Exception {
        try {
            quotedPropertyNamesAreLiteralAndTheNumericValueRemainsExact();
        } finally {
        }
    }


    void __invoke_longUnicodeStringUsesLiteralLongStringEncoding() throws Exception {
        try {
            longUnicodeStringUsesLiteralLongStringEncoding();
        } finally {
        }
    }

}
