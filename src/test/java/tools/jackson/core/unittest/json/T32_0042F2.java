package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0042F2 {

    void aboveAsciiCharactersRemainLiteralForReaderAndCharArrayInputs() throws Exception {
        String value = "chars: [\u00A0]-[\u1234]";
        byte[] expectedValue = literalString(value);
        assertStringBytes(g -> g.writeString(new StringReader(value), -1), expectedValue);
        char[] chars = value.toCharArray();
        assertStringBytes(g -> g.writeString(chars, 0, chars.length), expectedValue);

        // Literal indexed object: {"fun:\u0088:\u3456":true}.
        byte[] object = {
                0x0B, 0x10, 0x01,
                0x4A, 'f', 'u', 'n', ':', (byte) 0xC2, (byte) 0x88, ':',
                (byte) 0xE3, (byte) 0x91, (byte) 0x96, 0x1A,
                0x03
        };
        try (JsonParser parser = new VPackFactory().createParser(object)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("fun:\u0088:\u3456", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
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

    void __invoke_aboveAsciiCharactersRemainLiteralForReaderAndCharArrayInputs() throws Exception {
        try {
            aboveAsciiCharactersRemainLiteralForReaderAndCharArrayInputs();
        } finally {
        }
    }

}
