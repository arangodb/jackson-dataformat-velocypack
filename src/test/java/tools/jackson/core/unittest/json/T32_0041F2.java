package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.json.JsonWriteFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0041F2 {
private static final String[] SAMPLES = {
            "\"test\"", "\n", "\\n", "\r\n", "a\\b", "tab:\nok?",
            "a\tb\tc\n\fdef\t \tg\"\"\"h\"\\ijklmn\b",
            "\"\"\"", "\\r)'\"",
            "Longer text & other stuff:\twith some\r\n\r\n random linefeeds etc added in to cause some \"special\" handling \\\\ to occur...\n"
    };

    void basicEscaping() throws Exception {
        for (String sample : SAMPLES) {
            assertStringBytes(g -> g.writeString(sample), sample);
            char[] chars = sample.toCharArray();
            assertStringBytes(g -> g.writeString(chars, 0, chars.length), sample);
        }
    }

    void mediumStringsBytes() throws Exception {
        for (int size : new int[] { 1100, 2300, 3800, 7500, 19000 }) {
            String value = mediumText(size);
            assertStringRoundTrip(g -> g.writeString(value), value);
        }
    }

    void longerRandomSingleChunk() throws Exception {
        for (int round = 0; round < 80; ++round) {
            String value = randomText(75000 + round);
            assertStringRoundTrip(g -> g.writeString(value), value);
            char[] chars = value.toCharArray();
            assertStringRoundTrip(g -> g.writeString(chars, 0, chars.length), value);
        }
    }

    void longerRandomMultiChunk() throws Exception {
        for (int round = 0; round < 70; ++round) {
            String value = randomText(73000 + round);
            assertMultiChunk(value, false, false);
            assertMultiChunk(value, false, true);
        }
    }
private static void assertStringBytes(WriterCall call, String expected) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            call.write(generator);
        }
        assertArrayEquals(literalString(expected), output.toByteArray());
        assertScalarString(output.toByteArray(), expected);
    }
private static void assertStringRoundTrip(WriterCall call, String expected) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            call.write(generator);
        }
        assertScalarString(output.toByteArray(), expected);
    }
private static void assertScalarString(byte[] input, String expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertNull(parser.nextToken());
        }
    }
private static void assertArrayStrings(byte[] input, String... expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (String value : expected) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertEquals(value, parser.getString());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertMultiChunk(String value, boolean reader, boolean chars)
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartArray();
            if (reader) {
                generator.writeString(new StringReader(value), -1);
            } else {
                generator.writeString(value);
            }
            generator.writeEndArray();

            generator.writeStartArray();
            Random random = new Random(value.length());
            int offset = 0;
            generator.writeStartArray();
            while (offset < value.length()) {
                int shift = 1 + ((random.nextInt() & 0xFFFFF) % 12);
                int length = (1 << shift) + shift;
                if (offset + length >= value.length()) {
                    length = value.length() - offset;
                } else if (Character.isHighSurrogate(value.charAt(offset + length - 1))) {
                    ++length;
                }
                String part = value.substring(offset, offset + length);
                if (reader) {
                    generator.writeString(new StringReader(part), -1);
                } else if (chars) {
                    char[] partChars = part.toCharArray();
                    generator.writeString(partChars, 0, partChars.length);
                } else {
                    generator.writeString(part);
                }
                offset += length;
            }
            generator.writeEndArray();
            generator.writeEndArray();
        }

        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(value, parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            int offset = 0;
            while (parser.nextToken() == JsonToken.VALUE_STRING) {
                String actual = parser.getString();
                assertEquals(value.substring(offset, offset + actual.length()), actual);
                offset += actual.length();
            }
            assertEquals(value.length(), offset);
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertNumberBytes(VPackFactory factory, ObjectWriteContext context,
            WriterCall call, byte[] expected)
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(context, output)) {
            call.write(generator);
        }
        assertArrayEquals(expected, output.toByteArray());
        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertTrue(parser.nextToken() == JsonToken.VALUE_NUMBER_INT
                    || parser.currentToken() == JsonToken.VALUE_NUMBER_FLOAT);
            assertNull(parser.nextToken());
        }
    }
private static void assertNumber(VPackFactory factory, ObjectWriteContext context,
            WriterCall call, String expected)
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(context, output)) {
            call.write(generator);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            JsonToken token = parser.nextToken();
            assertTrue(token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT);
            assertEquals(expected, parser.getNumberValue().toString());
            assertNull(parser.nextToken());
        }
    }
private static ObjectWriteContext jsonFeatureContext(boolean numbersAsStrings) {
        return new ObjectWriteContext.Base() {
            @Override
            public int getFormatWriteFeatures(int defaults) {
                return numbersAsStrings
                        ? defaults | JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.getMask()
                        : defaults & ~JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.getMask();
            }
        };
    }
private static void assertDecimal(byte[] fixture, BigDecimal expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(expected, parser.getDecimalValue());
            assertEquals(expected.scale(), parser.getDecimalValue().scale());
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
            result[i + 1] = (byte) (payload.length >>> (8 * i));
        }
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
private static String mediumText(int minimumLength) {
        StringBuilder result = new StringBuilder(minimumLength + 1000);
        Random random = new Random(minimumLength);
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
private static String randomText(int length) {
        StringBuilder result = new StringBuilder(length + 1000);
        Random random = new Random(length);
        for (int i = 0; i < length; ++i) {
            if (random.nextBoolean()) {
                int value = random.nextInt() & 0xFFFF;
                if (value >= 0xD800 && value <= 0xDFFF) {
                    int codePoint = random.nextInt() & 0xFFFFF;
                    result.append((char) (0xD800 + (codePoint >> 10)));
                    value = 0xDC00 + (codePoint & 0x3FF);
                }
                result.append((char) value);
            } else {
                result.append((char) (random.nextInt() & 0x7F));
            }
        }
        return result.toString();
    }
@FunctionalInterface
    private interface WriterCall {
        void write(JsonGenerator generator) throws Exception;
    }

    void __invoke_basicEscaping() throws Exception {
        try {
            basicEscaping();
        } finally {
        }
    }


    void __invoke_mediumStringsBytes() throws Exception {
        try {
            mediumStringsBytes();
        } finally {
        }
    }


    void __invoke_longerRandomSingleChunk() throws Exception {
        try {
            longerRandomSingleChunk();
        } finally {
        }
    }


    void __invoke_longerRandomMultiChunk() throws Exception {
        try {
            longerRandomMultiChunk();
        } finally {
        }
    }

}
