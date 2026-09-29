package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0044F1 {
private static final byte[] ROOT_NUMBERS = {
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37
    };
private static final byte[] ROOT_BOOLEANS = {
            0x1A, 0x19, 0x1A, 0x19, 0x1A, 0x19, 0x1A
    };

    void repeatedStreamParsersRemainStableAcrossFiveThousandLiteralNames() throws Exception {
        byte[] document = literalSymbolDocument(5000);
        VPackFactory factory = new VPackFactory();
        for (int round = 0; round < 3; ++round) {
            try (JsonParser parser = factory.createParser(new ChunkedInputStream(document))) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                for (int i = 0; i < 5000; ++i) {
                    assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                    assertEquals(fieldName(i), parser.currentName());
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(i, parser.getIntValue());
                }
                assertEquals(JsonToken.END_OBJECT, parser.nextToken());
                assertNull(parser.nextToken());
            }
        }
    }
private static void assertNumbers(JsonParser parser) throws Exception {
        for (int i = 1; i <= 7; ++i) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(i, parser.getIntValue());
        }
        assertNull(parser.nextToken());
    }
private static void assertBooleans(JsonParser parser) throws Exception {
        boolean expected = true;
        for (int i = 0; i < 7; ++i) {
            assertEquals(expected ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE,
                    parser.nextToken());
            expected = !expected;
        }
        assertNull(parser.nextToken());
    }
private static byte[] literalSymbolDocument(int count) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        long[] offsets = new long[count];
        for (int i = 0; i < count; ++i) {
            offsets[i] = 17L + body.size();
            body.write(0x45); // five ASCII bytes in the key
            body.write('f');
            body.write('0' + (i / 1000) % 10);
            body.write('0' + (i / 100) % 10);
            body.write('0' + (i / 10) % 10);
            body.write('0' + i % 10);
            writeIndependentUnsigned(body, i);
        }

        byte[] bodyBytes = body.toByteArray();
        byte[] result = new byte[17 + bodyBytes.length + count * 8];
        result[0] = 0x0E;
        writeLittleEndian(result, 1, result.length, 8);
        writeLittleEndian(result, 9, count, 8);
        System.arraycopy(bodyBytes, 0, result, 17, bodyBytes.length);
        int indexOffset = 17 + bodyBytes.length;
        for (int i = 0; i < count; ++i) {
            writeLittleEndian(result, indexOffset + i * 8, offsets[i], 8);
        }
        return result;
    }
private static void writeIndependentUnsigned(ByteArrayOutputStream output, int value) {
        if (value <= 9) {
            output.write(0x30 + value);
        } else if (value <= 0xFF) {
            output.write(0x28);
            output.write(value);
        } else {
            output.write(0x29);
            output.write(value);
            output.write(value >>> 8);
        }
    }
private static void writeLittleEndian(byte[] target, int offset, long value, int width) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
private static String fieldName(int index) {
        return "f" + String.format("%04d", index);
    }
private static final class ChunkedInputStream extends InputStream {
        private final byte[] input;
        private int position;

        private ChunkedInputStream(byte[] input) {
            this.input = input;
        }

        @Override
        public int read() {
            return position == input.length ? -1 : input[position++] & 0xFF;
        }

        @Override
        public int read(byte[] target, int offset, int length) throws IOException {
            if (position == input.length) {
                return -1;
            }
            if (length == 0) {
                return 0;
            }
            int count = Math.min(length, Math.min(2, input.length - position));
            System.arraycopy(input, position, target, offset, count);
            position += count;
            return count;
        }
    }

    void __invoke_repeatedStreamParsersRemainStableAcrossFiveThousandLiteralNames() throws Exception {
        try {
            repeatedStreamParsersRemainStableAcrossFiveThousandLiteralNames();
        } finally {
        }
    }

}
