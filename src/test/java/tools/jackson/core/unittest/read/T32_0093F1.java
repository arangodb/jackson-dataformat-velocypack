package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0093F1 {
private static final NameCase[] TWO_BYTE_VALUES = {
            name(bytes(0x62), "b"),
            name(bytes(0xC3, 0x98), "\u00D8"),
            name(bytes(0x41, 0xC3, 0x98), "A\u00D8"),
            name(bytes(0x61, 0x62, 0xC3, 0x98, 0x64), "ab\u00D8d"),
            name(bytes(0x61, 0x62, 0x63, 0xC3, 0x98), "abc\u00D8"),
            name(bytes(0x63, 0x33, 0x70, 0x30), "c3p0"),
            name(bytes(0x31, 0x32, 0x33, 0x34, 0xC3, 0x87, 0x35), "1234\u00C75"),
            name(bytes(0x2E, 0x2E, 0x2E, 0x2E, 0x2E, 0x2E), "......"),
            name(bytes(0x4C, 0x6F, 0x6E, 0x67, 0xC3, 0xBA, 0x65, 0x72), "Long\u00FAer"),
            name(bytes(0x4C, 0x61, 0x74, 0x69, 0x6E, 0x31, 0x2D, 0x66, 0x75, 0x6C, 0x6C, 0x79,
                    0x2D, 0xC2, 0xBE, 0x2D, 0x64, 0x65, 0x76, 0x65, 0x6C, 0x6F, 0x70,
                    0xC2, 0xA8, 0x64), "Latin1-fully-\u00BE-develop\u00A8d"),
            name(bytes(0x53, 0x6F, 0x6D, 0x65, 0x20, 0x76, 0x65, 0x72, 0x79, 0x20, 0x6C, 0x6F,
                    0x6E, 0x67, 0x20, 0x6E, 0x61, 0x6D, 0x65, 0x2C, 0x20, 0x72, 0x69, 0x64,
                    0x69, 0x63, 0x75, 0x6C, 0x6F, 0x75, 0x73, 0x6C, 0x79, 0x20, 0x6C, 0x6F,
                    0x6E, 0x67, 0x20, 0x61, 0x63, 0x74, 0x75, 0x61, 0x6C, 0x6C, 0x79, 0x20,
                    0x74, 0x6F, 0x20, 0x73, 0x65, 0x65, 0x20, 0x74, 0x68, 0x61, 0x74, 0x20,
                    0x62, 0x75, 0x66, 0x66, 0x65, 0x72, 0x20, 0x65, 0x78, 0x70, 0x61, 0x6E,
                    0x73, 0x69, 0x6F, 0x6E, 0x20, 0x77, 0x6F, 0x72, 0x6B, 0x73, 0x3A, 0x20,
                    0xC2, 0xBF, 0x3F),
                    "Some very long name, ridiculously long actually to see that buffer expansion works: \u00BF?")
    };
private static final NameCase[] THREE_BYTE_VALUES = {
            name(bytes(0xEC, 0xA0, 0xA3, 0x3F), "\uC823?"),
            name(bytes(0x41, 0xE4, 0x80, 0x8F), "A\u400F"),
            name(bytes(0x31, 0xE1, 0x88, 0xB4, 0x3F), "1\u1234?"),
            name(bytes(0x61, 0x62, 0xE1, 0x88, 0xB4, 0x64), "ab\u1234d"),
            name(bytes(0x41, 0x62, 0x31, 0x32, 0x33, 0xE4, 0x80, 0xB4), "Ab123\u4034"),
            name(bytes(0x4C, 0x6F, 0x6E, 0x67, 0x20, 0xEC, 0x80, 0xA3, 0x20, 0x69, 0x73, 0x68),
                    "Long \uC023 ish"),
            name(bytes(0x42, 0x69, 0x74, 0x20, 0x6C, 0x6F, 0x6E, 0x67, 0x65, 0x72, 0x3A,
                    0xEC, 0x80, 0xA3), "Bit longer:\uC023"),
            name(bytes(0x45, 0x76, 0x65, 0x6E, 0x2D, 0x6C, 0x6F, 0x6E, 0x67, 0x65, 0x72, 0x3A,
                    0xE3, 0x91, 0x96), "Even-longer:\u3456"),
            name(bytes(0x59, 0x65, 0x74, 0x20, 0x62, 0x69, 0x74, 0x20, 0x6C, 0x6F, 0x6E, 0x67,
                    0x65, 0x72, 0x20, 0xEC, 0x80, 0xA3), "Yet bit longer \uC023"),
            name(bytes(0x45, 0x76, 0x65, 0x6E, 0x20, 0x6D, 0x6F, 0x72, 0x65, 0x20, 0xE3, 0x91,
                    0x96, 0x20, 0x6C, 0x6F, 0x6E, 0x67, 0x65, 0x72), "Even more \u3456 longer"),
            name(bytes(0xEC, 0x80, 0xA3, 0x20, 0x50, 0x6F, 0x73, 0x73, 0x69, 0x62, 0x6C, 0x79,
                    0x20, 0x72, 0x69, 0x64, 0x69, 0x63, 0x75, 0x6C, 0x6F, 0x75, 0x73),
                    "\uC023 Possibly ridiculous"),
            name(bytes(0x42, 0x75, 0x74, 0x20, 0xEC, 0x80, 0xA3, 0x20, 0x74, 0x68, 0x69, 0x73,
                    0x20, 0x74, 0x61, 0x6B, 0x65, 0x73, 0x20, 0x74, 0x68, 0x65, 0x20, 0x63,
                    0x61, 0x6B, 0x65), "But \uC023 this takes the cake")
    };

    void utf8Char3Bytes() throws Exception {
        String expected = "a".repeat(4000) + "\u5496";
        byte[] payload = longString(expected);
        forEachBinarySource(singlePairObject(shortString(ascii("value")), payload), parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        });
    }
private static void assertStringInSingleElementArray(byte[] encoded, String expected)
            throws Exception {
        forEachBinarySource(singleElementArray(shortString(encoded)), parser -> {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        });
    }
private static void assertStringAcrossBinarySources(byte[] encoded, String expected)
            throws Exception {
        forEachBinarySource(encoded, parser -> {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertNull(parser.nextToken());
        });
    }
private static void assertObjectNameAcrossBinarySources(byte[] encoded, String expected)
            throws Exception {
        forEachBinarySource(encoded, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(expected, parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(42, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        });
    }
private static void assertValidThreeByteValue(byte[] payload, String expected) throws Exception {
        byte[] document = singlePairObject(shortString(ascii("value")), shortString(payload));
        forEachBinarySource(document, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        });
    }
private static void assertInvalidString(byte[] payload) {
        StreamReadException failure;
        try (JsonParser parser = new VPackFactory().createParser(shortString(payload))) {
            failure = assertThrows(StreamReadException.class, parser::nextToken);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        assertTrue(failure.getMessage().contains("surrogate"));
    }
private static String randomUtf8Value(int length) {
        Random random = new Random(13);
        StringBuilder result = new StringBuilder(length + 20);
        while (result.length() < length) {
            int c;
            if (random.nextBoolean()) {
                c = 32 + (random.nextInt() & 0x3F);
                if (c == '"' || c == '\\') {
                    c = ' ';
                }
            } else if (random.nextBoolean()) {
                c = 160 + (random.nextInt() & 0x3FF);
            } else if (random.nextBoolean()) {
                c = 8000 + (random.nextInt() & 0x7FFF);
            } else {
                int value = random.nextInt() & 0x3FFFF;
                result.append((char) (0xD800 + (value >> 10)));
                c = 0xDC00 + (value & 0x3FF);
            }
            result.append((char) c);
        }
        return result.toString();
    }
private static byte[] singleElementArray(byte[] value) {
        byte[] result = new byte[value.length + 2];
        result[0] = 0x02;
        result[1] = (byte) result.length;
        System.arraycopy(value, 0, result, 2, value.length);
        return result;
    }
private static byte[] singlePairObject(byte[] key, byte[] value) {
        byte[] result = new byte[1 + 2 + 2 + key.length + value.length + 2];
        result[0] = 0x0C;
        putLittleEndian(result, 1, 2, result.length);
        putLittleEndian(result, 3, 2, 1);
        System.arraycopy(key, 0, result, 5, key.length);
        System.arraycopy(value, 0, result, 5 + key.length, value.length);
        putLittleEndian(result, result.length - 2, 2, 5);
        return result;
    }
private static byte[] longNameObject(String name) {
        byte[] key = longString(name);
        return singlePairObject(key, bytes(0x28, 0x2A));
    }
private static byte[] shortString(byte[] payload) {
        if (payload.length > 126) {
            throw new IllegalArgumentException("short test string");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static byte[] longString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[payload.length + 9];
        result[0] = (byte) 0xBF;
        putLittleEndian(result, 1, 8, payload.length);
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
private static byte[] ascii(String value) {
        return value.getBytes(StandardCharsets.US_ASCII);
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = (byte) values[i];
        }
        return result;
    }
private static void putLittleEndian(byte[] target, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
private static void forEachBinarySource(byte[] document, ParserAssertions assertions)
            throws Exception {
        VPackFactory factory = new VPackFactory();
        try (JsonParser parser = factory.createParser(document)) {
            assertions.check(parser);
        }
        try (JsonParser parser = factory.createParser(new OneByteInputStream(document))) {
            assertions.check(parser);
        }
        DataInput input = new DataInputStream(new ByteArrayInputStream(document));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertions.check(parser);
        }
    }
private static NameCase name(byte[] encoded, String decoded) {
        return new NameCase(encoded, decoded);
    }
@FunctionalInterface
    private interface ParserAssertions {
        void check(JsonParser parser) throws Exception;
    }
private record NameCase(byte[] encoded, String decoded) { }
private static final class OneByteInputStream extends InputStream {
        private final ByteArrayInputStream delegate;

        private OneByteInputStream(byte[] input) {
            delegate = new ByteArrayInputStream(input);
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public int read(byte[] target, int offset, int length) {
            if (length == 0) {
                return 0;
            }
            int value = delegate.read();
            if (value < 0) {
                return -1;
            }
            target[offset] = (byte) value;
            return 1;
        }
    }

    void __invoke_utf8Char3Bytes() throws Exception {
        try {
            utf8Char3Bytes();
        } finally {
        }
    }

}
