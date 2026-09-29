package tools.jackson.core.unittest.jsonptr;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0063F0 {
private static final JsonPointer EMPTY_PTR = JsonPointer.empty();
private static final int DEEP_ARRAY_DEPTH = 120_000;

    void deepJsonPointerFromLiteralVpack() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxNestingDepth(Integer.MAX_VALUE).build())
                .build();
        byte[] input = nestedEqualArrays(DEEP_ARRAY_DEPTH);

        try (JsonParser parser = factory.createParser(input)) {
            for (int i = 0; i < DEEP_ARRAY_DEPTH; ++i) {
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            }

            JsonPointer pointer = parser.streamReadContext().pathAsPointer();
            assertEquals(repeat("/0", DEEP_ARRAY_DEPTH - 1), pointer.toString());
        }
    }
private static byte[] nestedEqualArrays(int depth) {
        int length = Math.addExact(Math.multiplyExact(depth, 9), 1);
        byte[] result = new byte[length];
        int offset = 0;
        int containerLength = length;
        for (int i = 0; i < depth; ++i) {
            result[offset++] = 0x05;
            putLittleEndian(result, offset, containerLength, 8);
            offset += 8;
            containerLength -= 9;
        }
        result[offset] = 0x31;
        return result;
    }
private static void putLittleEndian(byte[] target, int offset, long value, int width) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
private static String repeat(String value, int count) {
        StringBuilder result = new StringBuilder(value.length() * count);
        for (int i = 0; i < count; ++i) {
            result.append(value);
        }
        return result.toString();
    }

    void __invoke_deepJsonPointerFromLiteralVpack() throws Exception {
        try {
            deepJsonPointerFromLiteralVpack();
        } finally {
        }
    }

}
