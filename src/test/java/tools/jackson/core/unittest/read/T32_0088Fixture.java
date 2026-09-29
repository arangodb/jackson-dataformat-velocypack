package tools.jackson.core.unittest.read;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0088Fixture {
private static final byte[] SINGLE_CHAR_ARRAY = {
            0x02, 0x04, 0x41, 'x'
    };
private static final byte[] THREE_BYTE_UTF8 = {
            0x46, (byte) 0xE4, (byte) 0xB8, (byte) 0xAD,
            (byte) 0xE6, (byte) 0x96, (byte) 0x87
    };
private static final byte[] SURROGATE_UTF8 = {
            0x4F, (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x80,
            ' ', 'e', 'm', 'o', 'j', 'i', ' ',
            (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x80
    };
private static final byte[] PROPERTY_OBJECT = {
            0x14, 0x11,
            0x45, 'm', 'y', 'K', 'e', 'y',
            0x47, 'm', 'y', 'V', 'a', 'l', 'u', 'e',
            0x01
    };
private static final byte[] NUMBER_ARRAY = {
            0x06, 0x10, 0x02,
            0x28, 0x2A,
            0x1B, 0x1F, (byte) 0x85, (byte) 0xEB, 0x51,
            (byte) 0xB8, 0x1E, 0x09, 0x40,
            0x03, 0x05
    };
private static final byte[] NULL_ARRAY = { 0x02, 0x03, 0x18 };

    void singleCharacterStringCanBeReadIntoWriter() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(SINGLE_CHAR_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(1L, parser.readString(writer));
            assertEquals("x", writer.toString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }

    void threeByteUtf8StringIsDecodedExactly() throws Exception {
        String expected = "\u4e2d\u6587";
        try (JsonParser parser = new VPackFactory().createParser(THREE_BYTE_UTF8)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
            assertNull(parser.nextToken());
        }
    }

    void surrogatePairStringIsDecodedExactly() throws Exception {
        String expected = "\uD83D\uDE00 emoji \uD83D\uDE00";
        try (JsonParser parser = new VPackFactory().createParser(SURROGATE_UTF8)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }

    void surrogatePairAtOutputBufferBoundaryIsDecodedExactly() throws Exception {
        String expected = "x".repeat(1023) + "\uD83D\uDE00" + "tail";
        try (JsonParser parser = new VPackFactory().createParser(longString(expected))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }

    void stringExactlyAtOutputBufferSizeIsWrittenExactly() throws Exception {
        assertLongStringOfLength(1024);
    }

    void stringOneOverOutputBufferSizeIsWrittenExactly() throws Exception {
        assertLongStringOfLength(1025);
    }

    void stringOfTwoFullOutputBuffersIsWrittenExactly() throws Exception {
        assertLongStringOfLength(2048);
    }

    void stringOfTwoFullOutputBuffersPlusOneIsWrittenExactly() throws Exception {
        assertLongStringOfLength(2049);
    }

    void readStringOnPropertyNameWritesTheCurrentName() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(PROPERTY_OBJECT)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(5L, parser.readString(writer));
            assertEquals("myKey", writer.toString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    void readStringOnNumberTokensWritesTheirText() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(NUMBER_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(2L, parser.readString(writer));
            assertEquals("42", writer.toString());

            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            writer = new StringWriter();
            assertEquals(4L, parser.readString(writer));
            assertEquals("3.14", writer.toString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }

    void readStringOnNullTokenWritesNull() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(NULL_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(4L, parser.readString(writer));
            assertEquals("null", writer.toString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }

    void readStringBeforeFirstTokenWritesNothing() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(SINGLE_CHAR_ARRAY)) {
            StringWriter writer = new StringWriter();
            assertEquals(0L, parser.readString(writer));
            assertEquals("", writer.toString());
        }
    }
private static void assertLongStringOfLength(int length) throws Exception {
        String expected = "a".repeat(length);
        try (JsonParser parser = new VPackFactory().createParser(longString(expected))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(length, parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }
private static byte[] longString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[payload.length + 9];
        result[0] = (byte) 0xBF;
        long length = payload.length;
        for (int i = 0; i < 8; ++i) {
            result[1 + i] = (byte) (length >>> (8 * i));
        }
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }

    void __invoke_singleCharacterStringCanBeReadIntoWriter() throws Exception {
        try {
            singleCharacterStringCanBeReadIntoWriter();
        } finally {
        }
    }


    void __invoke_threeByteUtf8StringIsDecodedExactly() throws Exception {
        try {
            threeByteUtf8StringIsDecodedExactly();
        } finally {
        }
    }


    void __invoke_surrogatePairStringIsDecodedExactly() throws Exception {
        try {
            surrogatePairStringIsDecodedExactly();
        } finally {
        }
    }


    void __invoke_surrogatePairAtOutputBufferBoundaryIsDecodedExactly() throws Exception {
        try {
            surrogatePairAtOutputBufferBoundaryIsDecodedExactly();
        } finally {
        }
    }


    void __invoke_stringExactlyAtOutputBufferSizeIsWrittenExactly() throws Exception {
        try {
            stringExactlyAtOutputBufferSizeIsWrittenExactly();
        } finally {
        }
    }


    void __invoke_stringOneOverOutputBufferSizeIsWrittenExactly() throws Exception {
        try {
            stringOneOverOutputBufferSizeIsWrittenExactly();
        } finally {
        }
    }


    void __invoke_stringOfTwoFullOutputBuffersIsWrittenExactly() throws Exception {
        try {
            stringOfTwoFullOutputBuffersIsWrittenExactly();
        } finally {
        }
    }


    void __invoke_stringOfTwoFullOutputBuffersPlusOneIsWrittenExactly() throws Exception {
        try {
            stringOfTwoFullOutputBuffersPlusOneIsWrittenExactly();
        } finally {
        }
    }


    void __invoke_readStringOnPropertyNameWritesTheCurrentName() throws Exception {
        try {
            readStringOnPropertyNameWritesTheCurrentName();
        } finally {
        }
    }


    void __invoke_readStringOnNumberTokensWritesTheirText() throws Exception {
        try {
            readStringOnNumberTokensWritesTheirText();
        } finally {
        }
    }


    void __invoke_readStringOnNullTokenWritesNull() throws Exception {
        try {
            readStringOnNullTokenWritesNull();
        } finally {
        }
    }


    void __invoke_readStringBeforeFirstTokenWritesNothing() throws Exception {
        try {
            readStringBeforeFirstTokenWritesNothing();
        } finally {
        }
    }

}
