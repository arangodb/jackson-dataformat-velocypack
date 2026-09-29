package tools.jackson.databind.deser.jdk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0268F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
private static final byte[] SHORT_37 = VPackWireFixtureTest.hex("28 25");
private static final byte[] SHORT_STRING = text("-1009");
private static final byte[] SHORT_DOUBLE_NEGATIVE_12_9 = VPackWireFixtureTest.hex(
            "1b cd cc cc cc cc cc 29 c0");
private static final byte[] LONG_12345678901 = VPackWireFixtureTest.hex(
            "c8 06 00 00 00 00 01 23 45 67 89 01");
private static final byte[] LONG_OBJECT_3 = VPackWireFixtureTest.hex(
            "14 06 41 76 33 01");
private static final byte[] LONG_OBJECT_NULL = VPackWireFixtureTest.hex(
            "14 06 41 76 18 01");
private static final byte[] LONG_ARRAY_NULL = VPackWireFixtureTest.hex(
            "13 04 18 01");
private static final byte[] LONG_OBJECT_ARRAY_3 = VPackWireFixtureTest.hex(
            "14 09 41 76 13 04 33 01 01");
private static final byte[] LONG_ROOT_OBJECT_ARRAY_3 = VPackWireFixtureTest.hex(
            "13 0c 14 09 41 76 13 04 33 01 01 01");
private static final byte[] LONG_OBJECT_ARRAY_3_3 = VPackWireFixtureTest.hex(
            "14 0a 41 76 13 05 33 33 02 01");
private static final byte[] LONG_OBJECT_ARRAY_NULL = VPackWireFixtureTest.hex(
            "14 09 41 76 13 04 18 01 01");
private static final byte[] LONG_NESTED_NULL_ARRAY = VPackWireFixtureTest.hex(
            "13 07 13 04 18 01 01");
private static final byte[] NULL_ARRAY = VPackWireFixtureTest.hex("13 04 18 01");
private static final byte[] EMPTY_STRING_ARRAY = VPackWireFixtureTest.hex("13 04 40 01");
private static final byte[] NULL_DEFAULTS_FIRST = VPackWireFixtureTest.hex(
            "14 28 48 69 6e 74 56 61 6c 75 65 18 "
          + "4c 62 6f 6f 6c 65 61 6e 56 61 6c 75 65 18 "
          + "4b 64 6f 75 62 6c 65 56 61 6c 75 65 18 03");
private static final byte[] NULL_DEFAULTS_SECOND = VPackWireFixtureTest.hex(
            "14 25 49 62 79 74 65 56 61 6c 75 65 18 "
          + "49 6c 6f 6e 67 56 61 6c 75 65 18 "
          + "4a 66 6c 6f 61 74 56 61 6c 75 65 18 03");
private static final byte[] NULL_BYTE = VPackWireFixtureTest.hex(
            "14 0e 49 62 79 74 65 56 61 6c 75 65 18 01");
private static final byte[] NULL_SHORT = VPackWireFixtureTest.hex(
            "14 0f 4a 73 68 6f 72 74 56 61 6c 75 65 18 01");
private static final byte[] NULL_INT = VPackWireFixtureTest.hex(
            "14 0d 48 69 6e 74 56 61 6c 75 65 18 01");
private static final byte[] NULL_LONG = VPackWireFixtureTest.hex(
            "14 0e 49 6c 6f 6e 67 56 61 6c 75 65 18 01");
private static final byte[] NULL_FLOAT = VPackWireFixtureTest.hex(
            "14 0f 4a 66 6c 6f 61 74 56 61 6c 75 65 18 01");
private static final byte[] NULL_DOUBLE = VPackWireFixtureTest.hex(
            "14 10 4b 64 6f 75 62 6c 65 56 61 6c 75 65 18 01");
private static final byte[] NULL_BOOLEAN = VPackWireFixtureTest.hex(
            "14 11 4c 62 6f 6f 6c 65 61 6e 56 61 6c 75 65 18 01");
private static final byte[] NULL_CHAR = VPackWireFixtureTest.hex(
            "14 0e 49 63 68 61 72 56 61 6c 75 65 18 01");
private static final byte[] NULL_CREATOR = VPackWireFixtureTest.hex(
            "14 06 41 61 18 01");
private static final byte[] VOID_PROPERTY = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 28 7b 01");
private static final byte[] VOID_ARRAY = VPackWireFixtureTest.hex(
            "13 05 31 1a 02");
private static final byte[] ROOT_SEQUENCE = VPackWireFixtureTest.hex(
            "30 31 32 33 34 35 36 37 38 39 "
          + "28 0a 28 0b 28 0c 28 0d 28 0e 28 0f 28 10 28 11 28 12 28 13 28 14 28 15 "
          + "28 16 28 17 28 18 28 19 28 1a 28 1b 28 1c 28 1d 28 1e 28 1f 28 20 28 21 28 22 "
          + "28 23 28 24 28 25 28 26 28 27 28 28 28 29 28 2a 28 2b 28 2c 28 2d 28 2e 28 2f "
          + "28 30 28 31 28 32 28 33 28 34 28 35 28 36 28 37 28 38 28 39 28 3a 28 3b 28 3c "
          + "28 3d 28 3e 28 3f 28 40 28 41 28 42 28 43 28 44 28 45 28 46 28 47 28 48 28 49 "
          + "28 4a 28 4b 28 4c 28 4d 28 4e 28 4f 28 50 28 51 28 52 28 53 28 54 28 55 28 56 "
          + "28 57 28 58 28 59 28 5a 28 5b 28 5c 28 5d 28 5e 28 5f 28 60 28 61 28 62 28 63");

    // Provenance: JDKScalarsDeserTest#testLongPrimitive().
    void testLongPrimitive() throws Exception {
        LongBean result = MAPPER.readValue(LONG_OBJECT_3, LongBean.class);
        assertEquals(3L, result.value);
        result = MAPPER.readValue(LONG_OBJECT_NULL, LongBean.class);
        assertNotNull(result);
        assertEquals(0L, result.value);

        assertArrayEquals(new long[] { 0L }, MAPPER.readValue(LONG_ARRAY_NULL, long[].class));

        ObjectReader noUnwrap = MAPPER.readerFor(LongBean.class)
                .without(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertThrows(MismatchedInputException.class,
                () -> noUnwrap.readValue(LONG_OBJECT_ARRAY_3));

        ObjectReader unwrapping = MAPPER.readerFor(LongBean.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        result = unwrapping.readValue(LONG_OBJECT_ARRAY_3);
        assertEquals(3L, result.value);
        result = unwrapping.readValue(LONG_ROOT_OBJECT_ARRAY_3);
        assertEquals(3L, result.value);
        assertThrows(MismatchedInputException.class,
                () -> unwrapping.readValue(LONG_OBJECT_ARRAY_3_3));

        result = unwrapping.readValue(LONG_OBJECT_ARRAY_NULL);
        assertNotNull(result);
        assertEquals(0L, result.value);
        assertArrayEquals(new long[] { 0L }, MAPPER.readerFor(long[].class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(LONG_NESTED_NULL_ARRAY));
    }

    // Provenance: JDKScalarsDeserTest#testLongWrapper().
    void testLongWrapper() throws Exception {
        assertEquals(Long.valueOf(12345678901L), MAPPER.readValue(LONG_12345678901, Long.class));
        assertEquals(Long.valueOf(-9876L), MAPPER.readValue(text("-9876"), Long.class));
        assertEquals(Long.valueOf(1918L), MAPPER.readValue(
                VPackWireFixtureTest.hex("1b 33 33 33 33 33 f9 9d 40"), Long.class));
    }

    // Provenance: JDKScalarsDeserTest#testNullForPrimitiveArrays().
    void testNullForPrimitiveArrays() throws IOException {
        assertPrimitiveArrayNull(boolean[].class, Boolean.FALSE, true);
        assertPrimitiveArrayNull(byte[].class, Byte.valueOf((byte) 0), true);
        assertPrimitiveArrayNull(char[].class, Character.valueOf((char) 0), false);
        assertPrimitiveArrayNull(short[].class, Short.valueOf((short) 0), true);
        assertPrimitiveArrayNull(int[].class, Integer.valueOf(0), true);
        assertPrimitiveArrayNull(long[].class, Long.valueOf(0L), true);
        assertPrimitiveArrayNull(float[].class, Float.valueOf(0f), true);
        assertPrimitiveArrayNull(double[].class, Double.valueOf(0d), true);
    }

    // Provenance: JDKScalarsDeserTest#testNullForPrimitivesDefault().
    void testNullForPrimitivesDefault() throws IOException {
        PrimitivesBean bean = MAPPER.readValue(NULL_DEFAULTS_FIRST, PrimitivesBean.class);
        assertNotNull(bean);
        assertEquals(0, bean.intValue);
        assertFalse(bean.booleanValue);
        assertEquals(0.0, bean.doubleValue);

        bean = MAPPER.readValue(NULL_DEFAULTS_SECOND, PrimitivesBean.class);
        assertEquals((byte) 0, bean.byteValue);
        assertEquals(0L, bean.longValue);
        assertEquals(0.0f, bean.floatValue);
    }

    // Provenance: JDKScalarsDeserTest#testNullForPrimitivesNotAllowedInts().
    void testNullForPrimitivesNotAllowedInts() throws IOException {
        ObjectReader reader = MAPPER.readerFor(PrimitivesBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        assertNullPrimitiveFails(reader, NULL_BYTE, "byteValue");
        assertNullPrimitiveFails(reader, NULL_SHORT, "shortValue");
        assertNullPrimitiveFails(reader, NULL_INT, "intValue");
        assertNullPrimitiveFails(reader, NULL_LONG, "longValue");
    }

    // Provenance: JDKScalarsDeserTest#testNullForPrimitivesNotAllowedFP().
    void testNullForPrimitivesNotAllowedFP() throws IOException {
        ObjectReader reader = MAPPER.readerFor(PrimitivesBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        assertNullPrimitiveFails(reader, NULL_FLOAT, "floatValue");
        assertNullPrimitiveFails(reader, NULL_DOUBLE, "doubleValue");
    }

    // Provenance: JDKScalarsDeserTest#testNullForPrimitivesNotAllowedMisc().
    void testNullForPrimitivesNotAllowedMisc() throws IOException {
        ObjectReader reader = MAPPER.readerFor(PrimitivesBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        assertNullPrimitiveFails(reader, NULL_BOOLEAN, "booleanValue");
        assertNullPrimitiveFails(reader, NULL_CHAR, "charValue");
    }

    // Provenance: JDKScalarsDeserTest#testNullForPrimitivesViaCreator().
    void testNullForPrimitivesViaCreator() throws IOException {
        ObjectReader reader = MAPPER.readerFor(PrimitiveCreatorBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(NULL_CREATOR));
        assertEquals("a", failure.getPath().get(0).getPropertyName());
    }

    // Provenance: JDKScalarsDeserTest#testSequenceOfInts().
    void testSequenceOfInts() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .build();
        try (JsonParser parser = mapper.createParser(ROOT_SEQUENCE)) {
            for (int i = 0; i < 100; ++i) {
                assertEquals(Integer.valueOf(i), mapper.readValue(parser, Integer.class));
            }
        }
    }

    // Provenance: JDKScalarsDeserTest#testShortWrapper().
    void testShortWrapper() throws Exception {
        assertEquals(Short.valueOf((short) 37), MAPPER.readValue(SHORT_37, Short.class));
        assertEquals(Short.valueOf((short) -1009), MAPPER.readValue(SHORT_STRING, Short.class));
        assertEquals(Short.valueOf((short) -12), MAPPER.readValue(
                SHORT_DOUBLE_NEGATIVE_12_9, Short.class));
    }

    // Provenance: JDKScalarsDeserTest#testVoidDeser().
    void testVoidDeser() throws Exception {
        VoidBean bean = MAPPER.readValue(VOID_PROPERTY, VoidBean.class);
        assertNull(bean.value);

        assertNull(MAPPER.readValue(VPackWireFixtureTest.hex("0a"), Void.class));
        assertNull(MAPPER.readValue(VPackWireFixtureTest.hex("29 d2 04"), Void.class));
        assertNull(MAPPER.readValue(VOID_ARRAY, Void.class));
        assertNull(MAPPER.readValue(VPackWireFixtureTest.hex("0a"), Void.TYPE));
        assertNull(MAPPER.readValue(VPackWireFixtureTest.hex("29 d2 04"), Void.TYPE));
        assertNull(MAPPER.readValue(VOID_ARRAY, Void.TYPE));
    }
private static void assertPrimitiveArrayNull(Class<?> type, Object expected,
            boolean testEmptyString) throws IOException {
        ObjectReader reader = MAPPER.readerFor(type);
        Object value = reader.readValue(NULL_ARRAY);
        assertEquals(1, java.lang.reflect.Array.getLength(value));
        assertEquals(expected, java.lang.reflect.Array.get(value, 0));

        ObjectReader strict = reader.with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        assertThrows(MismatchedInputException.class, () -> strict.readValue(NULL_ARRAY));
        if (testEmptyString) {
            value = reader.readValue(EMPTY_STRING_ARRAY);
            assertEquals(1, java.lang.reflect.Array.getLength(value));
            assertEquals(expected, java.lang.reflect.Array.get(value, 0));
        }
    }
private static void assertNullPrimitiveFails(ObjectReader reader, byte[] input,
            String property) throws IOException {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(input));
        assertEquals(property, failure.getPath().get(0).getPropertyName());
    }
private static byte[] text(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length > 126) {
            throw new IllegalArgumentException("fixture is not a short string");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
static class LongBean {
        public long value;

        public void setV(long value) {
            this.value = value;
        }
    }
static class PrimitivesBean {
        public boolean booleanValue = true;
        public byte byteValue = 3;
        public char charValue = 'a';
        public short shortValue = 37;
        public int intValue = 1;
        public long longValue = 100L;
        public float floatValue = 0.25f;
        public double doubleValue = -1.0;
    }
static class PrimitiveCreatorBean {
        @JsonCreator
        PrimitiveCreatorBean(@JsonProperty(value = "a", required = true) int a,
                @JsonProperty(value = "b", required = true) int b) { }
    }
static class VoidBean {
        public Void value;
    }

    void __invoke_testLongPrimitive() throws Exception {
        try {
            testLongPrimitive();
        } finally {
        }
    }


    void __invoke_testLongWrapper() throws Exception {
        try {
            testLongWrapper();
        } finally {
        }
    }


    void __invoke_testNullForPrimitiveArrays() throws Exception {
        try {
            testNullForPrimitiveArrays();
        } finally {
        }
    }


    void __invoke_testNullForPrimitivesDefault() throws Exception {
        try {
            testNullForPrimitivesDefault();
        } finally {
        }
    }


    void __invoke_testNullForPrimitivesNotAllowedInts() throws Exception {
        try {
            testNullForPrimitivesNotAllowedInts();
        } finally {
        }
    }


    void __invoke_testNullForPrimitivesNotAllowedFP() throws Exception {
        try {
            testNullForPrimitivesNotAllowedFP();
        } finally {
        }
    }


    void __invoke_testNullForPrimitivesNotAllowedMisc() throws Exception {
        try {
            testNullForPrimitivesNotAllowedMisc();
        } finally {
        }
    }


    void __invoke_testNullForPrimitivesViaCreator() throws Exception {
        try {
            testNullForPrimitivesViaCreator();
        } finally {
        }
    }


    void __invoke_testSequenceOfInts() throws Exception {
        try {
            testSequenceOfInts();
        } finally {
        }
    }


    void __invoke_testShortWrapper() throws Exception {
        try {
            testShortWrapper();
        } finally {
        }
    }


    void __invoke_testVoidDeser() throws Exception {
        try {
            testVoidDeser();
        } finally {
        }
    }

}
