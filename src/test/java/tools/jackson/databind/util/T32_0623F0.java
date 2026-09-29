package tools.jackson.databind.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.databind.util.ByteBufferBackedInputStream;
import tools.jackson.databind.util.ByteBufferBackedOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0623F0 {

    // Provenance: ByteBufferUtilsTest#testByteBufferInputStreamParameterValidation().
    void byteBufferInputStreamParameterValidationVpack() throws Exception {
        try (ByteBufferBackedInputStream stream = new ByteBufferBackedInputStream(
                ByteBuffer.wrap(new byte[] { 1, 2, 3 }))) {
            byte[] buffer = new byte[10];
            assertThrows(NullPointerException.class, () -> stream.read(null, 0, 5));
            assertThrows(IndexOutOfBoundsException.class, () -> stream.read(buffer, -1, 5));
            assertThrows(IndexOutOfBoundsException.class, () -> stream.read(buffer, 0, -1));
            assertThrows(IndexOutOfBoundsException.class, () -> stream.read(buffer, 5, 10));
        }
    }

    // Provenance: ByteBufferUtilsTest#testByteBufferOutput().
    void byteBufferOutputVpack() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[10]);
        try (ByteBufferBackedOutputStream output = new ByteBufferBackedOutputStream(buffer)) {
            output.write(1);
            output.write(new byte[] { 2, 3 });
            assertEquals(3, buffer.position());
            assertEquals(7, buffer.remaining());
        }

        ByteBuffer vpack = ByteBuffer.allocate(3);
        try (ByteBufferBackedOutputStream output = new ByteBufferBackedOutputStream(vpack)) {
            output.write(VPackWireFixtureTest.hex("41 61"));
        }
        assertEquals(2, vpack.position());
        assertEquals(1, vpack.remaining());
        assertArrayEquals(VPackWireFixtureTest.hex("41 61"),
                new byte[] { vpack.get(0), vpack.get(1) });
    }

    // Provenance: ByteBufferUtilsTest#testReadFromByteBuffer().
    void readFromByteBufferVpack() throws Exception {
        byte[] object = VPackWireFixtureTest.hex("0b 0b 02 41 61 31 41 62 32 03 06");
        try (InputStream input = new ByteBufferBackedInputStream(
                ByteBuffer.wrap(object));
             JsonParser parser = new VPackFactory().createParser(ObjectReadContext.empty(), input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    // Provenance: ByteBufferUtilsTest#testWriteToByteBuffer().
    void writeToByteBufferVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex("0b 0b 02 41 61 31 41 62 32 03 06");
        ByteBuffer buffer = ByteBuffer.allocate(expected.length);
        try (OutputStream output = new ByteBufferBackedOutputStream(buffer);
             JsonGenerator generator = new VPackFactory().createGenerator(ObjectWriteContext.empty(), output)) {
            generator.writeStartObject();
            generator.writeNumberProperty("a", 1);
            generator.writeNumberProperty("b", 2);
            generator.writeEndObject();
        }
        assertEquals(expected.length, buffer.position());
        assertEquals(0, buffer.remaining());
        assertArrayEquals(expected, buffer.array());
    }
static void throwsException() {
        throw new IllegalArgumentException("A custom message");
    }
enum TestEnum { A }
class InnerNonStatic { }

    void __invoke_byteBufferInputStreamParameterValidationVpack() throws Exception {
        try {
            byteBufferInputStreamParameterValidationVpack();
        } finally {
        }
    }


    void __invoke_byteBufferOutputVpack() throws Exception {
        try {
            byteBufferOutputVpack();
        } finally {
        }
    }


    void __invoke_readFromByteBufferVpack() throws Exception {
        try {
            readFromByteBufferVpack();
        } finally {
        }
    }


    void __invoke_writeToByteBufferVpack() throws Exception {
        try {
            writeToByteBufferVpack();
        } finally {
        }
    }

}
