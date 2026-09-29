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

class T32_0042F0 {

    void mediumStringsCharsPreserveCharactersThroughCharArrayWriting() throws Exception {
        for (int size : new int[] { 1100, 2300, 3800, 7500, 19000 }) {
            String value = mediumText(size);
            char[] chars = value.toCharArray();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
                generator.writeString(chars, 0, chars.length);
            }
            assertArrayEquals(literalString(value), output.toByteArray());
            assertScalarString(output.toByteArray(), value);
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

    void __invoke_mediumStringsCharsPreserveCharactersThroughCharArrayWriting() throws Exception {
        try {
            mediumStringsCharsPreserveCharactersThroughCharArrayWriting();
        } finally {
        }
    }

}
