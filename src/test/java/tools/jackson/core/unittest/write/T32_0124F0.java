package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0124F0 {

    void surrogateByteBackedStringUsesLiteralUtf8() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartArray();
            generator.writeString(new String(Character.toChars(0x1F602)));
            generator.writeEndArray();
        }

        byte[] expected = VPackWireFixtureTest.hex("02 07 44 f0 9f 98 82");
        assertArrayEquals(expected, output.toByteArray());
        assertStringValue(expected, "😂");
    }

    void surrogateCharBackedStringUsesLiteralUtf8() throws Exception {
        char[] value = new String(Character.toChars(0x1F602)).toCharArray();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartArray();
            generator.writeString(value, 0, value.length);
            generator.writeEndArray();
        }

        byte[] expected = VPackWireFixtureTest.hex("02 07 44 f0 9f 98 82");
        assertArrayEquals(expected, output.toByteArray());
        assertStringValue(expected, "😂");
    }

    void nonSurrogateUnicodeAndEmojiPreserveCallOrderAndWireBytes() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartObject();
            generator.writeStringProperty("test_full_width", "foo，bar");
            generator.writeStringProperty("test_small_form", "foo﹪bar");
            generator.writeStringProperty("test_hiragana", "fooあbar");
            generator.writeStringProperty("test_emoji", "😊");
            generator.writeEndObject();
        }

        byte[] expected = VPackWireFixtureTest.hex(
                "0b 63 04 "
                + "4f 74 65 73 74 5f 66 75 6c 6c 5f 77 69 64 74 68 "
                + "49 66 6f 6f ef bc 8c 62 61 72 "
                + "4f 74 65 73 74 5f 73 6d 61 6c 6c 5f 66 6f 72 6d "
                + "49 66 6f 6f ef b9 aa 62 61 72 "
                + "4d 74 65 73 74 5f 68 69 72 61 67 61 6e 61 "
                + "49 66 6f 6f e3 81 82 62 61 72 "
                + "4a 74 65 73 74 5f 65 6d 6f 6a 69 "
                + "44 f0 9f 98 8a "
                + "4f 03 37 1d");
        assertArrayEquals(expected, output.toByteArray());

        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("test_full_width", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("foo，bar", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("test_small_form", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("foo﹪bar", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("test_hiragana", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("fooあbar", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("test_emoji", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("😊", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void surrogateAtSegmentBoundaryRemainsOneUtf8String() throws Exception {
        String value = "x".repeat(1999) + "\uD83E\uDEE1";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartArray();
            generator.writeString(value);
            generator.writeEndArray();
        }

        byte[] actual = output.toByteArray();
        assertEquals(0x03, actual[0] & 0xff);
        assertEquals(0xdf, actual[1] & 0xff);
        assertEquals(0x07, actual[2] & 0xff);
        assertEquals(0xbf, actual[3] & 0xff);
        assertEquals(0xd3, actual[4] & 0xff);
        assertEquals(0x07, actual[5] & 0xff);
        assertEquals(0xf0, actual[actual.length - 4] & 0xff);
        assertEquals(0x9f, actual[actual.length - 3] & 0xff);
        assertEquals(0xab, actual[actual.length - 2] & 0xff);
        assertEquals(0xa1, actual[actual.length - 1] & 0xff);

        try (JsonParser parser = new VPackFactory().createParser(actual)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(value, parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
    }

    void characterEscapesHaveAnExplicitVpackUnsupportedSeam() {
        assertThrows(UnsupportedOperationException.class,
                () -> new VPackFactory().createGenerator(new StringWriter()));
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }
private static void assertStringValue(byte[] bytes, String expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), bytes)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_surrogateByteBackedStringUsesLiteralUtf8() throws Exception {
        try {
            surrogateByteBackedStringUsesLiteralUtf8();
        } finally {
        }
    }


    void __invoke_surrogateCharBackedStringUsesLiteralUtf8() throws Exception {
        try {
            surrogateCharBackedStringUsesLiteralUtf8();
        } finally {
        }
    }


    void __invoke_nonSurrogateUnicodeAndEmojiPreserveCallOrderAndWireBytes() throws Exception {
        try {
            nonSurrogateUnicodeAndEmojiPreserveCallOrderAndWireBytes();
        } finally {
        }
    }


    void __invoke_surrogateAtSegmentBoundaryRemainsOneUtf8String() throws Exception {
        try {
            surrogateAtSegmentBoundaryRemainsOneUtf8String();
        } finally {
        }
    }


    void __invoke_characterEscapesHaveAnExplicitVpackUnsupportedSeam() throws Exception {
        try {
            characterEscapesHaveAnExplicitVpackUnsupportedSeam();
        } finally {
        }
    }

}
