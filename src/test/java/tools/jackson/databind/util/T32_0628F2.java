package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.RawValue;
import tools.jackson.core.JsonGenerator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0628F2 {
private static final int[] SIZES = { 3, 19, 99, 1007, 19999, 99001 };
private final ObjectMapper mapper = VPackMapper.builder().build();

    // Provenance: RawValueTest#testEquality(). RawValue equality is format-independent.
    void rawValueEqualityAndDescription() {
        RawValue raw1 = new RawValue("foo");
        RawValue raw1b = new RawValue("foo");
        RawValue raw2 = new RawValue("bar");

        assertTrue(raw1.equals(raw1));
        assertTrue(raw1.equals(raw1b));
        assertFalse(raw1.equals(raw2));
        assertFalse(raw1.equals(null));
        assertFalse(new RawValue((tools.jackson.databind.JacksonSerializable) null).equals(raw1));
        assertNotNull(raw1.toString());
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

    void __invoke_rawValueEqualityAndDescription() throws Exception {
        try {
            rawValueEqualityAndDescription();
        } finally {
        }
    }

}
