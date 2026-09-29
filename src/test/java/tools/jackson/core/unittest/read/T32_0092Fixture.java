package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0092Fixture {
private static final byte[] EMPTY_NAME_AND_VALUE = {
            0x14, 0x05, 0x40, 0x40, 0x01
    };

    void emptyFieldNameAndValueAreRetainedAcrossBinarySources() throws Exception {
        forEachBinarySource(EMPTY_NAME_AND_VALUE, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("", parser.getString());
            assertEquals("", parser.currentName());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        });
    }

    void twoByteUtf8FieldNamesAreDecodedExactly() throws Exception {
        NameCase[] cases = {
                name(ascii("b"), "b"),
                name(bytes(0xC3, 0x98), "\u00D8"),
                name(concat(ascii("A"), bytes(0xC3, 0x98)), "A\u00D8"),
                name(concat(ascii("ab"), bytes(0xC3, 0x98), ascii("d")), "ab\u00D8d"),
                name(concat(ascii("abc"), bytes(0xC3, 0x98)), "abc\u00D8"),
                name(ascii("c3p0"), "c3p0"),
                name(concat(ascii("1234"), bytes(0xC3, 0x87), ascii("5")), "1234\u00C75"),
                name(ascii("......"), "......"),
                name(concat(ascii("Long"), bytes(0xC3, 0xBA), ascii("er")), "Long\u00FAer"),
                name(concat(ascii("Latin1-fully-"), bytes(0xC2, 0xBE),
                        ascii("-develop"), bytes(0xC2, 0xA8), ascii("d")),
                        "Latin1-fully-\u00BE-develop\u00A8d"),
                name(concat(ascii("Some very long name, ridiculously long actually to see that "
                                + "buffer expansion works: "), bytes(0xC2, 0xBF), ascii("?")),
                        "Some very long name, ridiculously long actually to see that "
                                + "buffer expansion works: \u00BF?")
        };

        for (NameCase testCase : cases) {
            assertFieldNameAcrossSources(testCase, 0x30);
        }
    }

    void threeByteUtf8FieldNamesAreDecodedExactly() throws Exception {
        NameCase[] cases = {
                name(concat(bytes(0xEC, 0xA0, 0xA3), ascii("?")), "\uC823?"),
                name(concat(ascii("A"), bytes(0xE4, 0x80, 0x8F)), "A\u400F"),
                name(concat(ascii("1"), bytes(0xE1, 0x88, 0xB4), ascii("?")), "1\u1234?"),
                name(concat(ascii("ab"), bytes(0xE1, 0x88, 0xB4), ascii("d")), "ab\u1234d"),
                name(concat(ascii("Ab123"), bytes(0xE4, 0x80, 0xB4)), "Ab123\u4034"),
                name(concat(ascii("Long "), bytes(0xEC, 0x80, 0xA3), ascii(" ish")),
                        "Long \uC023 ish"),
                name(concat(ascii("Bit longer:"), bytes(0xEC, 0x80, 0xA3)),
                        "Bit longer:\uC023"),
                name(concat(ascii("Even-longer:"), bytes(0xE3, 0x91, 0x96)),
                        "Even-longer:\u3456"),
                name(concat(ascii("Yet bit longer "), bytes(0xEC, 0x80, 0xA3)),
                        "Yet bit longer \uC023"),
                name(concat(ascii("Even more "), bytes(0xE3, 0x91, 0x96),
                        ascii(" longer")), "Even more \u3456 longer"),
                name(concat(bytes(0xEC, 0x80, 0xA3), ascii(" Possibly ridiculous")),
                        "\uC023 Possibly ridiculous"),
                name(concat(ascii("But "), bytes(0xEC, 0x80, 0xA3),
                        ascii(" this takes the cake")), "But \uC023 this takes the cake")
        };

        for (NameCase testCase : cases) {
            assertFieldNameAcrossSources(testCase, 0x1A);
        }
    }
private static void assertFieldNameAcrossSources(NameCase testCase, int valueMarker)
            throws Exception {
        byte[] document = singlePairObject(testCase.encoded(), valueMarker);
        forEachBinarySource(document, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(testCase.decoded(), parser.currentName());
            assertEquals(testCase.decoded(), parser.getString());
            assertEquals(valueMarker == 0x1A
                    ? JsonToken.VALUE_TRUE : JsonToken.VALUE_NUMBER_INT,
                    parser.nextToken());
            assertEquals(testCase.decoded(), parser.currentName());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        });
    }
private static byte[] singlePairObject(byte[] key, int valueMarker) {
        if (key.length > 126) {
            throw new IllegalArgumentException("short test key");
        }
        byte[] result = new byte[key.length + 5];
        result[0] = 0x14;
        result[1] = (byte) result.length;
        result[2] = (byte) (0x40 + key.length);
        System.arraycopy(key, 0, result, 3, key.length);
        result[3 + key.length] = (byte) valueMarker;
        result[result.length - 1] = 0x01;
        return result;
    }
private static NameCase name(byte[] encoded, String decoded) {
        return new NameCase(encoded, decoded);
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
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = (byte) values[i];
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

    void __invoke_emptyFieldNameAndValueAreRetainedAcrossBinarySources() throws Exception {
        try {
            emptyFieldNameAndValueAreRetainedAcrossBinarySources();
        } finally {
        }
    }


    void __invoke_twoByteUtf8FieldNamesAreDecodedExactly() throws Exception {
        try {
            twoByteUtf8FieldNamesAreDecodedExactly();
        } finally {
        }
    }


    void __invoke_threeByteUtf8FieldNamesAreDecodedExactly() throws Exception {
        try {
            threeByteUtf8FieldNamesAreDecodedExactly();
        } finally {
        }
    }

}
