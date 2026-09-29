package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.util.JsonParserDelegate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0089F1 {
private static final byte[] TWO_BYTE_UTF8 = {
            0x45, 'c', 'a', 'f', (byte) 0xC3, (byte) 0xA9
    };
private static final byte[] UNICODE_UTF8 = {
            0x4B, 'c', 'a', 'f', (byte) 0xC3, (byte) 0xA9, ' ',
            (byte) 0xE4, (byte) 0xB8, (byte) 0xAD, ' ', 0x00
    };
private static final byte[] FOUR_NUMBER_ARRAY = {
            0x02, 0x06, 0x31, 0x32, 0x33, 0x34
    };
private static final byte[] VALUE_AS_TEXT_OBJECT = {
            0x14, 0x12,
            0x41, 'a', 0x31,
            0x41, 'b', 0x1A,
            0x41, 'c', 0x18,
            0x41, 'd', 0x43, 'f', 'o', 'o',
            0x04
    };
private static final byte[] TEXT_VIA_WRITER_OBJECT = {
            0x14, 0x56,
            0x41, 'a', (byte) 0x80,
            't', 'h', 'i', 's', ' ', 'i', 's', ' ', 'a', ' ', 's', 'a', 'm', 'p', 'l', 'e',
            ' ', 't', 'e', 'x', 't', ' ', 'f', 'o', 'r', ' ', 'j', 's', 'o', 'n', ' ', 'p',
            'a', 'r', 's', 'i', 'n', 'g', ' ', 'u', 's', 'i', 'n', 'g', ' ', 'r', 'e', 'a', 'd',
            'S', 't', 'r', 'i', 'n', 'g', '(', ')', ' ', 'm', 'e', 't', 'h', 'o', 'd',
            0x41, 'b', 0x1A,
            0x41, 'c', 0x18,
            0x41, 'd', 0x47, 'f', 'o', 'o', 'b', 'a', 'r', '!',
            0x04
    };

    void bytesAsSourceHonorsOffsetAndLengthForLiteralVpack() throws Exception {
        int offset = 50;
        byte[] source = new byte[offset + FOUR_NUMBER_ARRAY.length + offset];
        System.arraycopy(FOUR_NUMBER_ARRAY, 0, source, offset, FOUR_NUMBER_ARRAY.length);

        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), source, offset, FOUR_NUMBER_ARRAY.length)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(3, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(4, parser.getIntValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void binaryParserInputSourcesAreReportedWithTheirConcreteTypes() throws Exception {
        VPackFactory factory = new VPackFactory();
        try (JsonParser parser = factory.createParser(
                new ByteArrayInputStream(new byte[] { 0x01 }))) {
            assertInstanceOf(InputStream.class, parser.streamReadInputSource());
        }

        DataInput input = new DataInputStream(new ByteArrayInputStream(new byte[] { 0x01 }));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertInstanceOf(DataInput.class, parser.streamReadInputSource());
        }
    }

    void getTextViaWriterRetainsTokensAndExactTextAcrossBinarySources() throws Exception {
        assertTextViaWriter(new VPackFactory().createParser(TEXT_VIA_WRITER_OBJECT));
        assertTextViaWriter(new VPackFactory().createParser(
                new ByteArrayInputStream(TEXT_VIA_WRITER_OBJECT)));
        DataInput input = new DataInputStream(
                new ByteArrayInputStream(TEXT_VIA_WRITER_OBJECT));
        assertTextViaWriter(new VPackFactory().createParser(ObjectReadContext.empty(), input));
    }

    void getValueAsTextBytesRetainsPortableCoercionsThroughDelegate() throws Exception {
        assertValueAsText(new VPackFactory().createParser(
                new ByteArrayInputStream(VALUE_AS_TEXT_OBJECT)), false);
        assertValueAsText(new VPackFactory().createParser(
                new ByteArrayInputStream(VALUE_AS_TEXT_OBJECT)), true);
    }

    void getValueAsTextDataInputRetainsPortableCoercionsThroughDelegate() throws Exception {
        DataInput first = new DataInputStream(
                new ByteArrayInputStream(VALUE_AS_TEXT_OBJECT));
        assertValueAsText(new VPackFactory().createParser(ObjectReadContext.empty(), first), false);
        DataInput second = new DataInputStream(
                new ByteArrayInputStream(VALUE_AS_TEXT_OBJECT));
        assertValueAsText(new VPackFactory().createParser(ObjectReadContext.empty(), second), true);
    }
private static void assertString(JsonParser parser, String expected) throws Exception {
        try (parser) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.readString(writer));
            assertEquals(expected, writer.toString());
            assertNull(parser.nextToken());
        }
    }
private static void assertTextViaWriter(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertWriterText(parser, "a");
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertWriterText(parser,
                    "this is a sample text for json parsing using readString() method");
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertWriterText(parser, "true");
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("c", parser.currentName());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertWriterText(parser, "null");
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("d", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertWriterText(parser, "foobar!");
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
private static void assertWriterText(JsonParser parser, String expected)
            throws Exception {
        Writer writer = new StringWriter();
        int length = parser.getString(writer);
        assertEquals(expected.length(), length);
        assertEquals(expected, writer.toString());
    }
private static void assertValueAsText(JsonParser input, boolean delegate)
            throws Exception {
        try (JsonParser parser = delegate ? new JsonParserDelegate(input) : input) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertNull(parser.getValueAsString());
            assertEquals("foobar", parser.getValueAsString("foobar"));

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.getString());
            assertEquals("a", parser.getValueAsString());
            assertEquals("a", parser.getValueAsString("default"));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("1", parser.getValueAsString());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.getValueAsString());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals("true", parser.getValueAsString());
            assertEquals("true", parser.getValueAsString("foobar"));

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("c", parser.getValueAsString());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertNull(parser.getValueAsString());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("d", parser.getValueAsString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("foo", parser.getValueAsString("default"));
            assertEquals("foo", parser.getValueAsString());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.getValueAsString());
        }
    }

    void __invoke_bytesAsSourceHonorsOffsetAndLengthForLiteralVpack() throws Exception {
        try {
            bytesAsSourceHonorsOffsetAndLengthForLiteralVpack();
        } finally {
        }
    }


    void __invoke_binaryParserInputSourcesAreReportedWithTheirConcreteTypes() throws Exception {
        try {
            binaryParserInputSourcesAreReportedWithTheirConcreteTypes();
        } finally {
        }
    }


    void __invoke_getTextViaWriterRetainsTokensAndExactTextAcrossBinarySources() throws Exception {
        try {
            getTextViaWriterRetainsTokensAndExactTextAcrossBinarySources();
        } finally {
        }
    }


    void __invoke_getValueAsTextBytesRetainsPortableCoercionsThroughDelegate() throws Exception {
        try {
            getValueAsTextBytesRetainsPortableCoercionsThroughDelegate();
        } finally {
        }
    }


    void __invoke_getValueAsTextDataInputRetainsPortableCoercionsThroughDelegate() throws Exception {
        try {
            getValueAsTextDataInputRetainsPortableCoercionsThroughDelegate();
        } finally {
        }
    }

}
