package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamContext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0120F0 {

    void stringWriteVpack() throws Exception {
        String[] values = { "", "X", "1234567890" };
        byte[][] expected = {
            bytes(0x40),
            bytes(0x41, 'X'),
            bytes(0x4A, '1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
        };

        for (int i = 0; i < values.length; ++i) {
            assertStringWrite(values[i], expected[i], false, i);
            assertStringWrite(values[i], expected[i], true, i);
        }
    }
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

    void rootIntsWriteVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeNumber(1);
            generator.writeNumber((short) 2);
            generator.writeNumber(-13);
        }

        byte[] expected = bytes(0x31, 0x32, 0x20, 0xF3);
        assertArrayEquals(expected, output.toByteArray());
        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-13, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }

    void outputContextVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(output);
        TokenStreamContext context = generator.streamWriteContext();
        assertTrue(context.inRoot());

        generator.writeStartObject();
        assertTrue(generator.streamWriteContext().inObject());
        generator.writeName("a");
        assertEquals("a", generator.streamWriteContext().currentName());
        generator.writeStartArray();
        assertTrue(generator.streamWriteContext().inArray());
        generator.writeStartObject();
        assertTrue(generator.streamWriteContext().inObject());

        generator.writeName("b");
        context = generator.streamWriteContext();
        assertEquals("b", context.currentName());
        generator.writeNumber(123);
        assertEquals("b", context.currentName());
        generator.writeName("c");
        assertEquals("c", generator.streamWriteContext().currentName());
        generator.writeNumber(5);
        generator.writeName("d");
        assertEquals("d", generator.streamWriteContext().currentName());
        generator.writeStartArray();
        context = generator.streamWriteContext();
        assertTrue(context.inArray());
        assertEquals(0, context.getCurrentIndex());
        assertEquals(0, context.getEntryCount());

        generator.writeBoolean(true);
        context = generator.streamWriteContext();
        assertEquals(0, context.getCurrentIndex());
        assertEquals(1, context.getEntryCount());
        generator.writeNumber(3);
        context = generator.streamWriteContext();
        assertEquals(1, context.getCurrentIndex());
        assertEquals(2, context.getEntryCount());

        generator.writeEndArray();
        assertTrue(generator.streamWriteContext().inObject());
        generator.writeEndObject();
        assertTrue(generator.streamWriteContext().inArray());
        generator.writeEndArray();
        assertTrue(generator.streamWriteContext().inObject());
        generator.writeEndObject();
        assertTrue(generator.streamWriteContext().inRoot());
        generator.close();

        assertArrayEquals(bytes(
                0x0B, 0x1B, 0x01, 0x41, 'a', 0x02, 0x15,
                0x0B, 0x13, 0x03, 0x41, 'b', 0x28, 0x7B,
                0x41, 'c', 0x35, 0x41, 'd', 0x02, 0x04, 0x1A, 0x33,
                0x03, 0x07, 0x0A, 0x03), output.toByteArray());
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

    void __invoke_stringWriteVpack() throws Exception {
        try {
            stringWriteVpack();
        } finally {
        }
    }


    void __invoke_rootIntsWriteVpack() throws Exception {
        try {
            rootIntsWriteVpack();
        } finally {
        }
    }


    void __invoke_outputContextVpack() throws Exception {
        try {
            outputContextVpack();
        } finally {
        }
    }

}
