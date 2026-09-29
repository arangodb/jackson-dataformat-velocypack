package tools.jackson.core.unittest.base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0008F1 {
private static final byte[] HELLO = "hello".getBytes(StandardCharsets.US_ASCII);
private static final String LONG_BASE64 =
            "1sPEAASBOGM6XGFwYWNoZV9yb290X2Rldlx0bXBcX3N0YXBsZXJcNHEydHJhY3ZcYXZhc3RfZnJlZV9hbnRpdmlydXNfc2V0dXBfb25saW5lLmV4ZS8vYzpcYXBhY2hlX3Jvb3RfZGV2XHN0b3JhZ2VcY1w3XDFcYzcxZmViMTA2NDA5MTE4NzIwOGI4MGNkM2Q0NWE0YThcYXZhc3RfZnJlZV9hbnRpdmlydXNfc2V0dXBfb25saW5lLmV4ZS8FkK0pAKA2kLFgAJsXgyyBZfkKWXg6OZiYBgBYCQCASAAAgAMAAAC4AACABgEAgAoAAABYCACADgAAAJgIAIAQAAAA2AgAgBgAAAAYCWgAAIDJAAAAkHgJAwAqDwCoAAAAqBgDAIwOAAUAAQAAAPAAAIACAUABAIAEAQCABQEIAQAAOCcDAEAhADABAAB4SAMAKFgBAACgigMAqCUAAQAASLADAKgBAADwwAMAaAQAFQA=";

    void nativeBinaryEmbeddedObjectWriteUsesRawBytes() throws Exception {
        byte[] payload = { 0, 1, 2, 3 };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), output)) {
            generator.writeEmbeddedObject(payload);
        }
        assertArrayEquals(new byte[] { (byte) 0xC0, 4, 0, 1, 2, 3 }, output.toByteArray());
    }

    void nativeBinaryWriteRetainsRootArrayAndObjectContexts() throws Exception {
        VPackFactory factory = new VPackFactory();

        ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(arrayOutput)) {
            generator.writeStartArray();
            generator.writeBinary(Base64Variants.MIME, HELLO, 0, HELLO.length);
            generator.writeEndArray();
        }
        try (VPackParser parser = (VPackParser) factory.createParser(arrayOutput.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertArrayEquals(HELLO, parser.getBinaryValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }

        ByteArrayOutputStream objectOutput = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(objectOutput)) {
            generator.writeStartObject();
            generator.writeName("field");
            generator.writeBinary(Base64Variants.PEM, HELLO, 0, HELLO.length);
            generator.writeEndObject();
        }
        try (VPackParser parser = (VPackParser) factory.createParser(objectOutput.toByteArray())) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals("field", parser.nextName());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertArrayEquals(HELLO, parser.getBinaryValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    void nativeBinaryStreamingWritesUseExactBytesForVariantsAndChunkSizes() throws Exception {
        byte[] payload = new byte[37];
        for (int i = 0; i < payload.length; ++i) {
            payload[i] = (byte) (i * 3);
        }
        Base64Variant[] variants = {
                Base64Variants.MIME, Base64Variants.MIME_NO_LINEFEEDS,
                Base64Variants.MODIFIED_FOR_URL, Base64Variants.PEM
        };
        for (Base64Variant variant : variants) {
            for (boolean passLength : new boolean[] { true, false }) {
                for (int chunkSize : new int[] { 1, 2, 3, 7, 64 }) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
                        int length = passLength ? payload.length : -1;
                        assertEquals(payload.length, generator.writeBinary(variant,
                                new ChunkedInputStream(payload, chunkSize), length));
                    }
                    assertArrayEquals(binaryEnvelope(payload), output.toByteArray());
                }
            }
        }
    }
private static byte[] decode(byte[] input, Base64Variant variant) throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            return parser.getBinaryValue(variant);
        }
    }
private static byte[] shortString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.US_ASCII);
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static byte[] longString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.US_ASCII);
        byte[] result = new byte[payload.length + 9];
        result[0] = (byte) 0xBF;
        for (int i = 0; i < 8; ++i) {
            result[1 + i] = (byte) (((long) payload.length) >>> (8 * i));
        }
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
private static byte[] binaryEnvelope(byte[] payload) {
        byte[] result = new byte[payload.length + 2];
        result[0] = (byte) 0xC0;
        result[1] = (byte) payload.length;
        System.arraycopy(payload, 0, result, 2, payload.length);
        return result;
    }
private static final class ChunkedInputStream extends InputStream {
        private final byte[] input;
        private final int chunkSize;
        private int offset;

        private ChunkedInputStream(byte[] input, int chunkSize) {
            this.input = input;
            this.chunkSize = chunkSize;
        }

        @Override
        public int read() {
            return offset == input.length ? -1 : input[offset++] & 0xFF;
        }

        @Override
        public int read(byte[] target, int targetOffset, int length) throws IOException {
            if (offset == input.length) {
                return -1;
            }
            int count = Math.min(Math.min(length, chunkSize), input.length - offset);
            System.arraycopy(input, offset, target, targetOffset, count);
            offset += count;
            return count;
        }
    }

    void __invoke_nativeBinaryEmbeddedObjectWriteUsesRawBytes() throws Exception {
        try {
            nativeBinaryEmbeddedObjectWriteUsesRawBytes();
        } finally {
        }
    }


    void __invoke_nativeBinaryWriteRetainsRootArrayAndObjectContexts() throws Exception {
        try {
            nativeBinaryWriteRetainsRootArrayAndObjectContexts();
        } finally {
        }
    }


    void __invoke_nativeBinaryStreamingWritesUseExactBytesForVariantsAndChunkSizes() throws Exception {
        try {
            nativeBinaryStreamingWritesUseExactBytesForVariantsAndChunkSizes();
        } finally {
        }
    }

}
