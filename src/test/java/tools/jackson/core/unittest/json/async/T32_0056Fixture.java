package tools.jackson.core.unittest.json.async;

import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0056Fixture {

    void literalLongUtf8NamesRetainExactNamesAndValues() throws Exception {
        assertLongName(generateName(5_000));

        StringBuilder nameBuilder = new StringBuilder("longString");
        for (int i = 1; nameBuilder.length() < 9_000; ++i) {
            nameBuilder.append('.').append(i);
        }
        assertLongName(nameBuilder.toString());
    }
private static void assertLongName(String name) throws Exception {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] input = singleLongNameObject(nameBytes);

        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(name, parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(13, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static byte[] singleLongNameObject(byte[] nameBytes) {
        int bodyLength = 9 + nameBytes.length + 5;
        int totalLength = 1 + 2 + 2 + bodyLength + 2;
        if (totalLength > 0xFFFF) {
            throw new IllegalArgumentException("test fixture exceeds two-byte length");
        }

        byte[] result = new byte[totalLength];
        result[0] = 0x0C;
        putLittleEndian(result, 1, 2, totalLength);
        putLittleEndian(result, 3, 2, 1);
        int bodyStart = 5;
        result[bodyStart] = (byte) 0xBF;
        putLittleEndian(result, bodyStart + 1, 8, nameBytes.length);
        System.arraycopy(nameBytes, 0, result, bodyStart + 9, nameBytes.length);
        result[bodyStart + 9 + nameBytes.length] = 0x23;
        putLittleEndian(result, bodyStart + 10 + nameBytes.length, 4, 13);
        putLittleEndian(result, totalLength - 2, 2, bodyStart);
        return result;
    }
private static String generateName(int minLength) {
        StringBuilder result = new StringBuilder();
        java.util.Random random = new java.util.Random(123);
        while (result.length() < minLength) {
            int ch = random.nextInt(96);
            if (ch < 32) {
                result.append((char) (48 + ch));
            } else if (ch < 64) {
                result.append((char) (128 + ch));
            } else {
                result.append((char) (4000 + ch));
            }
        }
        return result.toString();
    }
private static void putLittleEndian(byte[] target, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }

    void __invoke_literalLongUtf8NamesRetainExactNamesAndValues() throws Exception {
        try {
            literalLongUtf8NamesRetainExactNamesAndValues();
        } finally {
        }
    }

}
