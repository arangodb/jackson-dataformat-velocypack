package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0087Fixture {
private static final byte[] EMPTY_STRING = { 0x40 };
private static final byte[] COMMON_CHARACTERS = {
            0x56, '"', ' ', ' ', '\\', ' ', ' ', '/', ' ', ' ',
            '\b', ' ', ' ', '\f', ' ', ' ', '\n', ' ', ' ', '\r', ' ', ' ', '\t'
    };
private static final byte[] TWO_STRINGS = {
            0x02, 0x0E,
            0x45, 'h', 'e', 'l', 'l', 'o',
            0x45, 'w', 'o', 'r', 'l', 'd'
    };
private static final byte[] THREE_STRINGS = {
            0x13, 0x16,
            0x45, 'f', 'i', 'r', 's', 't',
            0x46, 's', 'e', 'c', 'o', 'n', 'd',
            0x45, 't', 'h', 'i', 'r', 'd',
            0x03
    };
private static final String MIXED_CONTENT = "Hello \n\t\u00e9 \u4e2d\u6587 \uD83D\uDE00 end";

    void emptyStringCanBeReadIntoWriter() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(EMPTY_STRING)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(0L, parser.readString(writer));
            assertEquals("", writer.toString());
        }
    }

    void commonStringCharactersAreWrittenVerbatim() throws Exception {
        String expected = "\"  \\  /  \b  \f  \n  \r  \t";
        try (JsonParser parser = new VPackFactory().createParser(COMMON_CHARACTERS)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }

    void contentAtOutputBufferBoundaryIsWrittenExactly() throws Exception {
        String expected = "x".repeat(1023) + "\n";
        try (JsonParser parser = new VPackFactory().createParser(longString(expected))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }

    void configuredStringLimitAcceptsExactLength() throws Exception {
        final int maxLength = 1000;
        String expected = "x".repeat(maxLength);
        VPackFactory factory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxStringLength(maxLength).build())
                .build();
        try (JsonParser parser = factory.createParser(longString(expected))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(maxLength, parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }

    void configuredStringLimitRejectsOneBeyondFlushBoundary() {
        assertOverLimit(1023, 1124);
    }

    void configuredStringLimitRejectsOneBeyondExactBoundary() {
        assertOverLimit(1024, 1025);
    }

    void readStringConsumesTheCurrentString() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(TWO_STRINGS)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(5L, parser.readString(writer));
            assertEquals("hello", writer.toString());
            assertEquals("", parser.getString());

            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            writer = new StringWriter();
            assertEquals(5L, parser.readString(writer));
            assertEquals("world", writer.toString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }

    void multipleStringsInArrayCanBeReadSequentially() throws Exception {
        String[] values = { "first", "second", "third" };
        try (JsonParser parser = new VPackFactory().createParser(THREE_STRINGS)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (String expected : values) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                StringWriter writer = new StringWriter();
                assertEquals(expected.length(), parser.readString(writer));
                assertEquals(expected, writer.toString());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }

    void mixedUtf8ContentIsWrittenExactly() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(shortString(MIXED_CONTENT))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(MIXED_CONTENT.length(), parser.readString(writer));
            assertEquals(MIXED_CONTENT, writer.toString());
        }
    }

    void longMixedUtf8ContentIsWrittenExactly() throws Exception {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 50; ++i) {
            builder.append("ASCII_block_");
            builder.append('\u00e9');
            builder.append('\u4e2d');
            builder.append("\uD83D\uDE00");
        }
        String expected = builder.toString();
        try (JsonParser parser = new VPackFactory().createParser(longString(expected))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }

    void rawUtf8StringSurvivesOneByteInputChunks() throws Exception {
        String expected = "\n\t\r\"\\\u00e9";
        try (JsonParser parser = new VPackFactory().createParser(
                new OneByteInputStream(shortString(expected)))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }

    void longStringSurvivesOneByteInputChunks() throws Exception {
        String expected = "abcdefghij".repeat(200);
        try (JsonParser parser = new VPackFactory().createParser(
                new OneByteInputStream(longString(expected)))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
        }
    }
private static void assertOverLimit(int maxLength, int actualLength) {
        VPackFactory factory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxStringLength(maxLength).build())
                .build();
        try (JsonParser parser = factory.createParser(
                longString("x".repeat(actualLength)))) {
            // VPack decodes a complete, self-framed string before exposing its
            // token, so the constraint failure is detected at nextToken(),
            // earlier than JSON's streaming readString() flush boundary.
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
private static byte[] shortString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length > 126) {
            throw new IllegalArgumentException("short fixture too large");
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
        long length = payload.length;
        for (int i = 0; i < 8; ++i) {
            result[1 + i] = (byte) (length >>> (8 * i));
        }
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
private static final class OneByteInputStream extends ByteArrayInputStream {
        private OneByteInputStream(byte[] input) {
            super(input);
        }

        @Override
        public synchronized int read(byte[] target, int offset, int length) {
            return super.read(target, offset, Math.min(length, 1));
        }
    }

    void __invoke_emptyStringCanBeReadIntoWriter() throws Exception {
        try {
            emptyStringCanBeReadIntoWriter();
        } finally {
        }
    }


    void __invoke_commonStringCharactersAreWrittenVerbatim() throws Exception {
        try {
            commonStringCharactersAreWrittenVerbatim();
        } finally {
        }
    }


    void __invoke_contentAtOutputBufferBoundaryIsWrittenExactly() throws Exception {
        try {
            contentAtOutputBufferBoundaryIsWrittenExactly();
        } finally {
        }
    }


    void __invoke_configuredStringLimitAcceptsExactLength() throws Exception {
        try {
            configuredStringLimitAcceptsExactLength();
        } finally {
        }
    }


    void __invoke_configuredStringLimitRejectsOneBeyondFlushBoundary() throws Exception {
        try {
            configuredStringLimitRejectsOneBeyondFlushBoundary();
        } finally {
        }
    }


    void __invoke_configuredStringLimitRejectsOneBeyondExactBoundary() throws Exception {
        try {
            configuredStringLimitRejectsOneBeyondExactBoundary();
        } finally {
        }
    }


    void __invoke_readStringConsumesTheCurrentString() throws Exception {
        try {
            readStringConsumesTheCurrentString();
        } finally {
        }
    }


    void __invoke_multipleStringsInArrayCanBeReadSequentially() throws Exception {
        try {
            multipleStringsInArrayCanBeReadSequentially();
        } finally {
        }
    }


    void __invoke_mixedUtf8ContentIsWrittenExactly() throws Exception {
        try {
            mixedUtf8ContentIsWrittenExactly();
        } finally {
        }
    }


    void __invoke_longMixedUtf8ContentIsWrittenExactly() throws Exception {
        try {
            longMixedUtf8ContentIsWrittenExactly();
        } finally {
        }
    }


    void __invoke_rawUtf8StringSurvivesOneByteInputChunks() throws Exception {
        try {
            rawUtf8StringSurvivesOneByteInputChunks();
        } finally {
        }
    }


    void __invoke_longStringSurvivesOneByteInputChunks() throws Exception {
        try {
            longStringSurvivesOneByteInputChunks();
        } finally {
        }
    }

}
