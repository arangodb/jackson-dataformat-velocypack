package tools.jackson.databind.deser.dos;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0217F2 {
private static final int DEEP_ARRAY_NESTING = StreamReadConstraints.DEFAULT_MAX_DEPTH * 10;
private static final int DEEP_SERIALIZATION_NESTING =
            StreamWriteConstraints.DEFAULT_MAX_DEPTH + 100;
private static final ObjectMapper NO_LIMITS_MAPPER = VPackMapper.builder(
            VPackFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxNestingDepth(Integer.MAX_VALUE).build())
                    .streamWriteConstraints(StreamWriteConstraints.builder()
                            .maxNestingDepth(Integer.MAX_VALUE).build())
                    .build()).build();
private static final byte[] DEEP_ARRAY_WITH_VALUE = nestedEqualArrays(
            DEEP_ARRAY_NESTING, VPackWireFixtureTest.hex("28 7b"));

    // Provenance: DeepJsonNodeSerTest#testDeepNodeSerNoStreamingLimits.
    void testDeepNodeSerNoStreamingLimits() throws Exception {
        JsonNode jsonNode = NO_LIMITS_MAPPER.readTree(
                nestedObjects(DEEP_SERIALIZATION_NESTING));
        byte[] vpack = NO_LIMITS_MAPPER.writeValueAsBytes(jsonNode);
        assertNotNull(vpack);
    }

    // Provenance: DeepJsonNodeSerTest#testDeepNodeSerWithStreamingLimits.
    void testDeepNodeSerWithStreamingLimits() throws Exception {
        JsonNode jsonNode = NO_LIMITS_MAPPER.readTree(
                nestedObjects(DEEP_SERIALIZATION_NESTING));
        ObjectMapper defaultMapper = new VPackMapper();
        StreamConstraintsException exception = assertThrows(StreamConstraintsException.class,
                () -> defaultMapper.writeValueAsBytes(jsonNode));
        assertTrue(exception.getMessage().contains("Document nesting depth"));
        assertTrue(exception.getMessage().contains("exceeds the maximum allowed"));
    }
private static void assertNestedArrayWrappingRejected(Class<?> valueType) {
        ObjectMapper mapper = VPackMapper.builder(
                VPackFactory.builder()
                        .streamReadConstraints(StreamReadConstraints.builder()
                                .maxNestingDepth(Integer.MAX_VALUE).build())
                        .build())
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS).build();
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(DEEP_ARRAY_WITH_VALUE, valueType));
        assertTrue(exception.getMessage().contains("Cannot deserialize"));
        assertTrue(exception.getMessage().contains("nested Array"));
    }
private static byte[] nestedEqualArrays(int depth, byte[] leaf) {
        int length = Math.addExact(Math.multiplyExact(depth, 9), leaf.length);
        byte[] result = new byte[length];
        int offset = 0;
        int containerLength = length;
        for (int i = 0; i < depth; ++i) {
            result[offset++] = 0x05;
            putLittleEndian(result, offset, containerLength);
            offset += 8;
            containerLength -= 9;
        }
        System.arraycopy(leaf, 0, result, offset, leaf.length);
        return result;
    }
private static byte[] nestedObjects(int depth) {
        int length = Math.addExact(Math.multiplyExact(depth, 27), 1);
        byte[] result = new byte[length];
        int offset = 0;
        for (int i = 0; i < depth; ++i) {
            int containerLength = 1 + 27 * (depth - i);
            result[offset] = 0x0e;
            putLittleEndian(result, offset + 1, containerLength);
            putLittleEndian(result, offset + 9, 1);
            result[offset + 17] = 0x41;
            result[offset + 18] = 0x78;
            putLittleEndian(result, offset + containerLength - 8, 17);
            offset += 19;
        }
        result[offset] = 0x0a;
        return result;
    }
private static void putLittleEndian(byte[] target, int offset, long value) {
        for (int i = 0; i < 8; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }

    void __invoke_testDeepNodeSerNoStreamingLimits() throws Exception {
        try {
            testDeepNodeSerNoStreamingLimits();
        } finally {
        }
    }


    void __invoke_testDeepNodeSerWithStreamingLimits() throws Exception {
        try {
            testDeepNodeSerWithStreamingLimits();
        } finally {
        }
    }

}
