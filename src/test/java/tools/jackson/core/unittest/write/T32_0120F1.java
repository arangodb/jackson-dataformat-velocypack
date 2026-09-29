package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0120F1 {
private static void assertStringWrite(String value, byte[] expected,
            boolean chars, int offset) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            if (chars) {
                char[] buffer = new char[value.length() + 20];
                value.getChars(0, value.length(), buffer, offset);
                generator.writeString(buffer, offset, value.length());
            } else {
                generator.writeString(value);
            }
        }
        assertArrayEquals(expected, output.toByteArray());
        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(value, parser.getString());
            assertNull(parser.nextToken());
        }
    }

    void noAutoCloseTargetVpack() throws Exception {
        TrackingOutputStream output = new TrackingOutputStream();
        JsonGenerator generator = VPackFactory.builder()
                .disable(StreamWriteFeature.AUTO_CLOSE_TARGET)
                .build().createGenerator(ObjectWriteContext.empty(), output);
        generator.writeNumber(39);
        generator.close();
        assertFalse(output.closed);
        assertArrayEquals(bytes(0x28, 0x27), output.bytes.toByteArray());
    }

    void closeGeneratorVpack() throws Exception {
        TrackingOutputStream output = new TrackingOutputStream();
        JsonGenerator generator = VPackFactory.builder()
                .enable(StreamWriteFeature.AUTO_CLOSE_TARGET)
                .build().createGenerator(ObjectWriteContext.empty(), output);
        generator.writeNumber(39);
        generator.close();
        assertTrue(output.closed);
        assertArrayEquals(bytes(0x28, 0x27), output.bytes.toByteArray());
    }

    void autoCloseArraysAndObjectsVpack() throws Exception {
        ByteArrayOutputStream arrays = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(arrays);
        generator.writeStartArray();
        generator.close();
        assertArrayEquals(bytes(0x01), arrays.toByteArray());

        ByteArrayOutputStream objects = new ByteArrayOutputStream();
        generator = new VPackFactory().createGenerator(objects);
        generator.writeStartObject();
        generator.close();
        assertArrayEquals(bytes(0x0A), objects.toByteArray());
    }

    void noAutoCloseObjectVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = VPackFactory.builder()
                .disable(StreamWriteFeature.AUTO_CLOSE_CONTENT)
                .build().createGenerator(output);
        generator.writeStartObject();
        assertThrows(StreamWriteException.class, generator::close);
        assertEquals(0, output.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertTrue(generator.isClosed());
    }

    void autoFlushOrNotVpack() throws Exception {
        TrackingOutputStream defaultOutput = new TrackingOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(defaultOutput);
        generator.writeStartArray();
        generator.writeEndArray();
        assertEquals(0, defaultOutput.flushes);
        generator.flush();
        assertEquals(1, defaultOutput.flushes);
        assertArrayEquals(bytes(0x01), defaultOutput.bytes.toByteArray());
        generator.close();

        TrackingOutputStream noFlushOutput = new TrackingOutputStream();
        generator = VPackFactory.builder()
                .disable(StreamWriteFeature.FLUSH_PASSED_TO_STREAM)
                .build().createGenerator(noFlushOutput);
        generator.writeStartArray();
        generator.writeEndArray();
        generator.flush();
        assertEquals(0, noFlushOutput.flushes);
        generator.close();
        assertEquals(0, noFlushOutput.flushes);
        assertArrayEquals(bytes(0x01), noFlushOutput.bytes.toByteArray());
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = (byte) values[i];
        }
        return result;
    }
private static byte[] hex(String value) {
        String[] parts = value.trim().split("\\s+");
        byte[] result = new byte[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            result[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return result;
    }
private static final class TrackingOutputStream extends OutputStream {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int flushes;
        boolean closed;

        @Override
        public void write(int value) {
            bytes.write(value);
        }

        @Override
        public void write(byte[] value, int offset, int length) {
            bytes.write(value, offset, length);
        }

        @Override
        public void flush() throws IOException {
            ++flushes;
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    void __invoke_noAutoCloseTargetVpack() throws Exception {
        try {
            noAutoCloseTargetVpack();
        } finally {
        }
    }


    void __invoke_closeGeneratorVpack() throws Exception {
        try {
            closeGeneratorVpack();
        } finally {
        }
    }


    void __invoke_autoCloseArraysAndObjectsVpack() throws Exception {
        try {
            autoCloseArraysAndObjectsVpack();
        } finally {
        }
    }


    void __invoke_noAutoCloseObjectVpack() throws Exception {
        try {
            noAutoCloseObjectVpack();
        } finally {
        }
    }


    void __invoke_autoFlushOrNotVpack() throws Exception {
        try {
            autoFlushOrNotVpack();
        } finally {
        }
    }

}
