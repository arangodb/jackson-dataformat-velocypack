package tools.jackson.core.unittest.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.io.InputDecorator;
import tools.jackson.core.io.OutputDecorator;
import tools.jackson.core.util.JsonGeneratorDecorator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0043F0 {
private static final byte[] ABOVE_ASCII_VALUE = {
            0x51, 'c', 'h', 'a', 'r', 's', ':', ' ', '[',
            (byte) 0xC2, (byte) 0xA0, ']', '-', '[',
            (byte) 0xE1, (byte) 0x88, (byte) 0xB4, ']'
    };
private static final byte[] ABOVE_ASCII_OBJECT = {
            0x0B, 0x10, 0x01,
            0x4A, 'f', 'u', 'n', ':', (byte) 0xC2, (byte) 0x88, ':',
            (byte) 0xE3, (byte) 0x91, (byte) 0x96, 0x1A,
            0x03
    };
private static final byte[] CUSTOM_STRING = {
            0x4D, '[', 'a', 'b', 'c', 'd', '-', (byte) 0xC4, (byte) 0x91,
            '-', (byte) 0xE1, (byte) 0x84, (byte) 0x91, ']'
    };
private static final byte[] CUSTOM_OBJECT = {
            0x0B, 0x20, 0x01,
            0x4D, '[', 'a', 'b', 'c', 'd', '-', (byte) 0xC4, (byte) 0x91,
            '-', (byte) 0xE1, (byte) 0x84, (byte) 0x91, ']',
            0x4D, '[', 'a', 'b', 'c', 'd', '-', (byte) 0xC4, (byte) 0x91,
            '-', (byte) 0xE1, (byte) 0x84, (byte) 0x91, ']',
            0x03
    };

    void aboveAsciiCharactersRemainLiteralForUtf8Output() throws Exception {
        String value = "chars: [\u00A0]-[\u1234]";
        assertStringBytes(g -> g.writeString(value), ABOVE_ASCII_VALUE);
        char[] chars = value.toCharArray();
        assertStringBytes(g -> g.writeString(chars, 0, chars.length), ABOVE_ASCII_VALUE);

        try (JsonParser parser = new VPackFactory().createParser(ABOVE_ASCII_OBJECT)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("fun:\u0088:\u3456", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void customEscapesAreTextOnlyAndVpackKeepsInputCharacters() throws Exception {
        String value = "[abcd-\u0111-\u1111]";
        assertStringBytes(g -> g.writeString(value), CUSTOM_STRING);
        char[] chars = value.toCharArray();
        assertStringBytes(g -> g.writeString(chars, 0, chars.length), CUSTOM_STRING);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartObject();
            generator.writeName(value);
            generator.writeString(value);
            generator.writeEndObject();
        }
        assertArrayEquals(CUSTOM_OBJECT, output.toByteArray());
    }

    void jsonpLineSeparatorsAreLiteralUtf8Characters() throws Exception {
        byte[] expected = {
                0x02, 0x0A,
                0x43, (byte) 0xE2, (byte) 0x80, (byte) 0xA8,
                0x43, (byte) 0xE2, (byte) 0x80, (byte) 0xA9
        };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeStartArray();
            generator.writeString("\u2028");
            generator.writeString("\u2029");
            generator.writeEndArray();
        }
        assertArrayEquals(expected, output.toByteArray());

        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("\u2028", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("\u2029", parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertDecorated(VPackFactory factory, byte[] expected) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartObject();
            generator.writeStringProperty("password", "s3cr37x!!");
            generator.writeEndObject();
        }
        assertArrayEquals(expected, output.toByteArray());
    }
private static void assertStringBytes(WriterCall call, byte[] expected) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            call.write(generator);
        }
        assertArrayEquals(expected, output.toByteArray());
    }
private static final class TextHider implements JsonGeneratorDecorator {
        @Override
        public JsonGenerator decorate(TokenStreamFactory factory, JsonGenerator generator) {
            return new JsonGeneratorDecoratorDelegate(generator);
        }
    }
private static final class JsonGeneratorDecoratorDelegate
            extends tools.jackson.core.util.JsonGeneratorDelegate {
        private JsonGeneratorDecoratorDelegate(JsonGenerator generator) {
            super(generator);
        }

        @Override
        public JsonGenerator writeString(String text) {
            delegate.writeString("***");
            return this;
        }
    }
private static final class BinaryInputDecorator extends InputDecorator {
        @Override
        public InputStream decorate(IOContext ctxt, InputStream in) {
            return new ByteArrayInputStream(new byte[] { 0x33 });
        }

        @Override
        public InputStream decorate(IOContext ctxt, byte[] src, int offset, int length) {
            return new ByteArrayInputStream(new byte[] { 0x34 });
        }

        @Override
        public Reader decorate(IOContext ctxt, Reader src) {
            return src;
        }
    }
private static final class BinaryOutputDecorator extends OutputDecorator {
        @Override
        public OutputStream decorate(IOContext ctxt, OutputStream out) throws JacksonException {
            try {
                out.write(0x35);
            } catch (java.io.IOException e) {
                throw tools.jackson.core.exc.JacksonIOException.construct(e, null);
            }
            return new ByteArrayOutputStream();
        }

        @Override
        public Writer decorate(IOContext ctxt, Writer out) {
            return out;
        }
    }
@FunctionalInterface
    private interface WriterCall {
        void write(JsonGenerator generator) throws Exception;
    }

    void __invoke_aboveAsciiCharactersRemainLiteralForUtf8Output() throws Exception {
        try {
            aboveAsciiCharactersRemainLiteralForUtf8Output();
        } finally {
        }
    }


    void __invoke_customEscapesAreTextOnlyAndVpackKeepsInputCharacters() throws Exception {
        try {
            customEscapesAreTextOnlyAndVpackKeepsInputCharacters();
        } finally {
        }
    }


    void __invoke_jsonpLineSeparatorsAreLiteralUtf8Characters() throws Exception {
        try {
            jsonpLineSeparatorsAreLiteralUtf8Characters();
        } finally {
        }
    }

}
