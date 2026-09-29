package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JsonGenerator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0628F1 {
private static final int[] SIZES = { 3, 19, 99, 1007, 19999, 99001 };
private final ObjectMapper mapper = VPackMapper.builder().build();

    // Provenance: ObjectBufferTest#testUntyped(). Retains every source size and value assertion.
    void untypedVpackArraysPreserveObjectBufferSizedSequences() throws Exception {
        for (int size : SIZES) {
            Integer[] values = values(size);
            Object[] result = mapper.readValue(mapper.writeValueAsBytes(values), Object[].class);
            assertEquals(size, result.length);
            Random expected = new Random(size);
            for (Object value : result) {
                assertEquals(expected.nextInt(), ((Number) value).intValue());
            }
        }
    }

    // Provenance: ObjectBufferTest#testTyped(). Retains every source size and value assertion.
    void typedVpackArraysPreserveObjectBufferSizedSequences() throws Exception {
        for (int size : SIZES) {
            Integer[] values = values(size);
            Integer[] result = mapper.readValue(mapper.writeValueAsBytes(values), Integer[].class);
            assertEquals(size, result.length);
            Random expected = new Random(size);
            for (int i = 0; i < size; ++i) {
                assertEquals(expected.nextInt(), result[i].intValue());
            }
        }
    }
private static Integer[] values(int size) {
        Integer[] values = new Integer[size];
        Random random = new Random(size);
        for (int i = 0; i < size; ++i) {
            values[i] = random.nextInt();
        }
        return values;
    }
private static void assertTextNumber(String text, int... expected) throws Exception {
        assertArrayEquals(bytes(expected), writeTextNumber(text), text);
    }
private static byte[] writeTextNumber(String text) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeNumber(text);
        }
        return output.toByteArray();
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }

    void __invoke_untypedVpackArraysPreserveObjectBufferSizedSequences() throws Exception {
        try {
            untypedVpackArraysPreserveObjectBufferSizedSequences();
        } finally {
        }
    }


    void __invoke_typedVpackArraysPreserveObjectBufferSizedSequences() throws Exception {
        try {
            typedVpackArraysPreserveObjectBufferSizedSequences();
        } finally {
        }
    }

}
