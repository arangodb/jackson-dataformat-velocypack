package tools.jackson.core.unittest.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.io.InputDecorator;
import tools.jackson.core.io.OutputDecorator;
import tools.jackson.core.util.JsonGeneratorDecorator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0043F1 {
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

    void generatorDecorationSurvivesVpackFactoryCopies() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .addDecorator(new TextHider())
                .build();
        byte[] expected = {
                0x0B, 0x11, 0x01,
                0x48, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd',
                0x43, '*', '*', '*',
                0x03
        };

        assertDecorated(factory, expected);
        assertDecorated(factory.copy(), expected);
        assertDecorated(factory.rebuild().build(), expected);
    }

    void binaryInputDecorationSupportsStreamAndByteArraySources() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .inputDecorator(new BinaryInputDecorator())
                .build();

        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                new ByteArrayInputStream(new byte[] { 0x7F }))) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(3, parser.getIntValue());
        }

        byte[] source = { 0x7F };
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                source, 0, source.length)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(4, parser.getIntValue());
        }

        assertThrows(UnsupportedOperationException.class,
                () -> factory.createParser(ObjectReadContext.empty(), new StringReader("3")));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createParser(ObjectReadContext.empty(), new char[] { '3' }, 0, 1));
    }

    void binaryOutputDecorationSupportsStreamTargets() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .outputDecorator(new BinaryOutputDecorator())
                .build();

        ByteArrayOutputStream first = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(first)) {
            generator.writeNumber(9);
        }
        assertArrayEquals(new byte[] { 0x35 }, first.toByteArray());

        ByteArrayOutputStream second = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(ObjectWriteContext.empty(),
                second, tools.jackson.core.JsonEncoding.UTF8)) {
            generator.writeNumber(9);
        }
        assertArrayEquals(new byte[] { 0x35 }, second.toByteArray());

        assertThrows(UnsupportedOperationException.class,
                () -> factory.createGenerator(new StringWriter()));
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

    void __invoke_generatorDecorationSurvivesVpackFactoryCopies() throws Exception {
        try {
            generatorDecorationSurvivesVpackFactoryCopies();
        } finally {
        }
    }


    void __invoke_binaryInputDecorationSupportsStreamAndByteArraySources() throws Exception {
        try {
            binaryInputDecorationSupportsStreamAndByteArraySources();
        } finally {
        }
    }


    void __invoke_binaryOutputDecorationSupportsStreamTargets() throws Exception {
        try {
            binaryOutputDecorationSupportsStreamTargets();
        } finally {
        }
    }

}
