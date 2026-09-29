package tools.jackson.core.unittest.base64;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0008F0 {
private static final byte[] HELLO = "hello".getBytes(StandardCharsets.US_ASCII);
private static final String LONG_BASE64 =
            "1sPEAASBOGM6XGFwYWNoZV9yb290X2Rldlx0bXBcX3N0YXBsZXJcNHEydHJhY3ZcYXZhc3RfZnJlZV9hbnRpdmlydXNfc2V0dXBfb25saW5lLmV4ZS8vYzpcYXBhY2hlX3Jvb3RfZGV2XHN0b3JhZ2VcY1w3XDFcYzcxZmViMTA2NDA5MTE4NzIwOGI4MGNkM2Q0NWE0YThcYXZhc3RfZnJlZV9hbnRpdmlydXNfc2V0dXBfb25saW5lLmV4ZS8FkK0pAKA2kLFgAJsXgyyBZfkKWXg6OZiYBgBYCQCASAAAgAMAAAC4AACABgEAgAoAAABYCACADgAAAJgIAIAQAAAA2AgAgBgAAAAYCWgAAIDJAAAAkHgJAwAqDwCoAAAAqBgDAIwOAAUAAQAAAPAAAIACAUABAIAEAQCABQEIAQAAOCcDAEAhADABAAB4SAMAKFgBAACgigMAqCUAAQAASLADAKgBAADwwAMAaAQAFQA=";

    void textBinaryAccessorRetainsPaddingModes() throws Exception {
        for (Base64Variant variant : new Base64Variant[] {
                Base64Variants.MIME, Base64Variants.MIME_NO_LINEFEEDS, Base64Variants.PEM
        }) {
            assertArrayEquals(HELLO, decode(shortString("aGVsbG8="), variant.withPaddingAllowed()));
            assertArrayEquals(HELLO, decode(shortString("aGVsbG8="), variant.withPaddingRequired()));
            assertThrows(StreamReadException.class,
                    () -> decode(shortString("aGVsbG8="), variant.withPaddingForbidden()));

            assertArrayEquals(HELLO, decode(shortString("aGVsbG8"), variant.withPaddingAllowed()));
            assertArrayEquals(HELLO, decode(shortString("aGVsbG8"), variant.withPaddingForbidden()));
            assertThrows(StreamReadException.class,
                    () -> decode(shortString("aGVsbG8"), variant.withPaddingRequired()));
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

    void __invoke_textBinaryAccessorRetainsPaddingModes() throws Exception {
        try {
            textBinaryAccessorRetainsPaddingModes();
        } finally {
        }
    }

}
