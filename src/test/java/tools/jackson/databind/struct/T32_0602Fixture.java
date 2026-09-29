package tools.jackson.databind.struct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0602Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper UNWRAPPING = VPackMapper.builder()
            .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
private static final byte[] ARRAY_INT = VPackWireFixtureTest.hex("13 07 2a e0 3b 0e 01");
private static final byte[] ARRAY_LONG = VPackWireFixtureTest.hex(
            "13 0c 2f af cb af a2 5c fc f2 20 01");
private static final byte[] ARRAY_SHORT = VPackWireFixtureTest.hex("13 06 29 e0 3b 01");
private static final byte[] ARRAY_BYTE = VPackWireFixtureTest.hex("13 05 28 2b 01");
private static final byte[] ARRAY_DOUBLE = VPackWireFixtureTest.hex(
            "13 0c 1b 61 c3 d3 2b 65 29 40 40 01");
private static final byte[] ARRAY_FLOAT = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 80 f4 17 55 40 01");
private static final byte[] ARRAY_CHAR = VPackWireFixtureTest.hex("13 05 41 63 01");
private static final byte[] ARRAY_TRUE = VPackWireFixtureTest.hex("13 04 1a 01");
private static final byte[] ARRAY_FALSE = VPackWireFixtureTest.hex("13 04 19 01");
private static final byte[] ARRAY_TWO_INTS = VPackWireFixtureTest.hex(
            "13 07 28 2a 28 2a 02");
private static final byte[] OBJECT_WITH_NULL_ARRAY = VPackWireFixtureTest.hex(
            "14 0d 45 76 61 6c 75 65 13 04 18 01 01");
private static final byte[] BOOLEAN_BEAN_TRUE_ARRAY = VPackWireFixtureTest.hex(
            "14 09 41 76 13 04 1a 01 01");
private static final byte[] BOOLEAN_BEAN_NULL_ARRAY = VPackWireFixtureTest.hex(
            "14 09 41 76 13 04 18 01 01");
private static final byte[] BOOLEAN_BEAN_NULL_ARRAY_IN_ROOT = VPackWireFixtureTest.hex(
            "13 0c 14 09 41 76 13 04 18 01 01 01");
private static final byte[] BOOLEAN_ARRAY_NULL = VPackWireFixtureTest.hex(
            "13 07 13 04 18 01 01");
private static final byte[] CLASS_STRING = VPackWireFixtureTest.hex(
            "50 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67");
private static final byte[] CLASS_STRING_ARRAY = VPackWireFixtureTest.hex(
            "13 14 50 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67 01");
private static final byte[] SINGLE_POJO = VPackWireFixtureTest.hex(
            "13 0a 14 07 41 69 28 2a 01 01");
private static final byte[] TWO_POJOS = VPackWireFixtureTest.hex(
            "13 11 14 07 41 69 28 2a 01 14 07 41 69 28 10 01 02");
private static final byte[] SINGLE_MAP = VPackWireFixtureTest.hex(
            "13 0e 14 0b 45 73 74 75 66 66 28 2a 01 01");
private static final byte[] TWO_MAPS = VPackWireFixtureTest.hex(
            "13 11 14 07 41 69 28 2a 01 14 07 41 69 28 10 01 02");
private static final byte[] SINGLE_ENUM_MAP = VPackWireFixtureTest.hex(
            "13 0a 14 07 41 41 28 2a 01 01");
private static final byte[] TWO_ENUM_MAPS = VPackWireFixtureTest.hex(
            "13 11 14 07 41 41 28 2a 01 14 07 41 42 28 10 01 02");
private static final byte[] BIG_DECIMAL = VPackWireFixtureTest.hex(
            "c8 01 fd ff ff ff 01");
private static final byte[] BIG_INTEGER = VPackWireFixtureTest.hex(
            "d0 10 00 00 00 00 01 23 45 67 89 01 23 45 67 89 01 23 45 56 78 09");
private static final byte[] ORDERED_MAP = VPackWireFixtureTest.hex(
            "0b 16 01 46 76 61 6c 75 65 73 0b 0b 02 41 61 31 41 62 32 03 06 03");

    // Provenance: UnwrapSingleArrayTest#testSingleElementScalarArrays().
    void testSingleElementScalarArraysVpack() throws Exception {
        assertEquals(932832, UNWRAPPING.readValue(ARRAY_INT, int.class));
        assertEquals(Integer.valueOf(932832), UNWRAPPING.readValue(ARRAY_INT, Integer.class));
        assertEquals(32.3234d, UNWRAPPING.readValue(ARRAY_DOUBLE, Double.class));
        assertEquals(2374237428374293423L, UNWRAPPING.readValue(ARRAY_LONG, long.class));
        assertEquals(Long.valueOf(2374237428374293423L), UNWRAPPING.readValue(ARRAY_LONG, Long.class));
        assertEquals((short) 0x3be0, UNWRAPPING.readValue(ARRAY_SHORT, short.class));
        assertEquals(Short.valueOf((short) 0x3be0), UNWRAPPING.readValue(ARRAY_SHORT, Short.class));
        assertEquals(84.3743f, UNWRAPPING.readValue(ARRAY_FLOAT, Float.class));
        assertEquals((byte) 43, UNWRAPPING.readValue(ARRAY_BYTE, byte.class));
        assertEquals(Byte.valueOf((byte) 43), UNWRAPPING.readValue(ARRAY_BYTE, Byte.class));
        assertEquals('c', UNWRAPPING.readValue(ARRAY_CHAR, char.class));
        assertEquals(Character.valueOf('c'), UNWRAPPING.readValue(ARRAY_CHAR, Character.class));
        assertTrue(UNWRAPPING.readValue(ARRAY_TRUE, boolean.class));
        assertFalse(UNWRAPPING.readValue(ARRAY_FALSE, boolean.class));
        assertEquals(Boolean.TRUE, UNWRAPPING.readValue(ARRAY_TRUE, Boolean.class));
    }

    // Provenance: UnwrapSingleArrayTest#testSingleElementArrayDisabled().
    void testSingleElementArrayDisabledVpack() {
        ObjectMapper mapper = MAPPER;
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_INT, Integer.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_INT, int.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_LONG, Long.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_LONG, long.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_SHORT, Short.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_SHORT, short.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_FLOAT, Float.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_FLOAT, float.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_BYTE, Byte.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_BYTE, byte.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_CHAR, Character.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_CHAR, char.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TRUE, Boolean.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TRUE, boolean.class));
    }

    // Provenance: UnwrapSingleArrayTest#testMultiValueArrayException().
    void testMultiValueArrayExceptionVpack() {
        ObjectMapper mapper = UNWRAPPING;
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, Integer.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, int.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, Long.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, long.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, Short.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, short.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, Byte.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, byte.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, Float.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, float.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, Double.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, double.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, Character.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, char.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, Boolean.class));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(ARRAY_TWO_INTS, boolean.class));
    }

    // Provenance: UnwrapSingleArrayTest#testBooleanPrimitiveArrayUnwrap().
    void testBooleanPrimitiveArrayUnwrapVpack() throws Exception {
        BooleanBean bean = UNWRAPPING.readValue(BOOLEAN_BEAN_TRUE_ARRAY, BooleanBean.class);
        assertTrue(bean.value);
        assertThrows(MismatchedInputException.class,
                () -> UNWRAPPING.readValue(BOOLEAN_BEAN_TWO_VALUES, BooleanBean.class));
        bean = UNWRAPPING.readValue(BOOLEAN_BEAN_NULL_ARRAY, BooleanBean.class);
        assertFalse(bean.value);
        bean = UNWRAPPING.readValue(BOOLEAN_BEAN_NULL_ARRAY_IN_ROOT, BooleanBean.class);
        assertFalse(bean.value);
        boolean[] values = UNWRAPPING.readValue(BOOLEAN_ARRAY_NULL, boolean[].class);
        assertEquals(1, values.length);
        assertFalse(values[0]);
    }

    // Provenance: UnwrapSingleArrayTest#testBigDecimal().
    void testBigDecimalVpack() throws Exception {
        BigDecimal expected = new BigDecimal("0.001");
        assertEquals(expected, MAPPER.readValue(BIG_DECIMAL, BigDecimal.class));
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(ARRAY_BIG_DECIMAL, BigDecimal.class));
        assertEquals(expected, UNWRAPPING.readValue(ARRAY_BIG_DECIMAL, BigDecimal.class));
        assertThrows(MismatchedInputException.class,
                () -> UNWRAPPING.readValue(TWO_BIG_DECIMALS, BigDecimal.class));
    }

    // Provenance: UnwrapSingleArrayTest#testBigInteger().
    void testBigIntegerVpack() throws Exception {
        BigInteger expected = new BigInteger("-1234567890123456789012345567809");
        assertEquals(expected, MAPPER.readValue(BIG_INTEGER, BigInteger.class));
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(ARRAY_BIG_INTEGER, BigInteger.class));
        assertEquals(expected, UNWRAPPING.readValue(ARRAY_BIG_INTEGER, BigInteger.class));
        assertThrows(MismatchedInputException.class,
                () -> UNWRAPPING.readValue(TWO_BIG_INTEGERS, BigInteger.class));
    }

    // Provenance: UnwrapSingleArrayTest#testClassAsArray().
    void testClassAsArrayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS).build();
        assertEquals(String.class, mapper.readValue(CLASS_STRING, Class.class));
        assertEquals(String.class, mapper.readValue(CLASS_STRING_ARRAY, Class.class));
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(CLASS_STRING_ARRAY, Class.class));
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(TWO_CLASS_NAMES, Class.class));
    }

    // Provenance: UnwrapSingleArrayTest#testSimplePOJOUnwrapping().
    void testSimplePOJOUnwrappingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS).build();
        IntWrapper result = mapper.readValue(SINGLE_POJO, IntWrapper.class);
        assertEquals(42, result.i);
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(TWO_POJOS, IntWrapper.class));
    }

    // Provenance: UnwrapSingleArrayTest#testSimpleMapUnwrapping().
    void testSimpleMapUnwrappingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS).build();
        Map<?, ?> result = mapper.readValue(SINGLE_MAP, Map.class);
        assertEquals(Map.of("stuff", 42), result);
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(TWO_MAPS, Map.class));
    }

    // Provenance: UnwrapSingleArrayTest#testEnumMapUnwrapping().
    void testEnumMapUnwrappingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS).build();
        EnumMap<ABC, Integer> result = mapper.readValue(SINGLE_ENUM_MAP,
                new TypeReference<EnumMap<ABC, Integer>>() { });
        EnumMap<ABC, Integer> expected = new EnumMap<>(ABC.class);
        expected.put(ABC.A, 42);
        assertEquals(expected, result);
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(TWO_ENUM_MAPS, new TypeReference<EnumMap<ABC, Integer>>() { }));
    }

    // Provenance: UnwrapSingleArrayTest#testDeserializeArrayWithNullElement().
    void testDeserializeArrayWithNullElementVpack() throws Exception {
        StringWrapper result = UNWRAPPING.readValue(OBJECT_WITH_NULL_ARRAY, StringWrapper.class);
        assertNotNull(result);
        assertNull(result.value);
    }

    // Provenance: UnwrapSingleArrayTest#testOrderedMaps().
    void testOrderedMapsVpack() throws Exception {
        SortedKeysMap value = new SortedKeysMap().put("b", 2).put("a", 1);
        assertArrayEquals(ORDERED_MAP, MAPPER.writeValueAsBytes(value));
        Map<?, ?> result = MAPPER.readValue(ORDERED_MAP, Map.class);
        assertEquals(Map.of("values", Map.of("a", 1, "b", 2)), result);
    }
private static final byte[] BOOLEAN_BEAN_TWO_VALUES = VPackWireFixtureTest.hex(
            "14 0a 41 76 13 05 1a 1a 02 01");
private static final byte[] ARRAY_BIG_DECIMAL = VPackWireFixtureTest.hex(
            "13 0a c8 01 fd ff ff ff 01 01");
private static final byte[] TWO_BIG_DECIMALS = VPackWireFixtureTest.hex(
            "13 11 c8 01 fd ff ff ff 01 c8 01 fd ff ff ff 01 02");
private static final byte[] ARRAY_BIG_INTEGER = VPackWireFixtureTest.hex(
            "13 19 d0 10 00 00 00 00 01 23 45 67 89 01 23 45 67 89 01 23 45 56 78 09 01");
private static final byte[] TWO_BIG_INTEGERS = VPackWireFixtureTest.hex(
            "13 2f d0 10 00 00 00 00 01 23 45 67 89 01 23 45 67 89 01 23 45 56 78 09 d0 10 00 00 00 00 01 23 45 67 89 01 23 45 67 89 01 23 45 56 78 09 02");
private static final byte[] TWO_CLASS_NAMES = VPackWireFixtureTest.hex(
            "13 25 50 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67 50 6a 61 76 61 2e 6c 61 6e 67 2e 4f 62 6a 65 63 74 02");
static class BooleanBean {
        boolean value;
        public void setV(boolean v) { value = v; }
    }
static class StringWrapper {
        public String value;
    }
static class IntWrapper {
        public int i;
    }
enum ABC { A, B, C }
static class SortedKeysMap {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES)
        public Map<String, Integer> values = new LinkedHashMap<>();

        SortedKeysMap put(String key, int number) {
            values.put(key, number);
            return this;
        }
    }

    void __invoke_testSingleElementScalarArraysVpack() throws Exception {
        try {
            testSingleElementScalarArraysVpack();
        } finally {
        }
    }


    void __invoke_testSingleElementArrayDisabledVpack() throws Exception {
        try {
            testSingleElementArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testMultiValueArrayExceptionVpack() throws Exception {
        try {
            testMultiValueArrayExceptionVpack();
        } finally {
        }
    }


    void __invoke_testBooleanPrimitiveArrayUnwrapVpack() throws Exception {
        try {
            testBooleanPrimitiveArrayUnwrapVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalVpack() throws Exception {
        try {
            testBigDecimalVpack();
        } finally {
        }
    }


    void __invoke_testBigIntegerVpack() throws Exception {
        try {
            testBigIntegerVpack();
        } finally {
        }
    }


    void __invoke_testClassAsArrayVpack() throws Exception {
        try {
            testClassAsArrayVpack();
        } finally {
        }
    }


    void __invoke_testSimplePOJOUnwrappingVpack() throws Exception {
        try {
            testSimplePOJOUnwrappingVpack();
        } finally {
        }
    }


    void __invoke_testSimpleMapUnwrappingVpack() throws Exception {
        try {
            testSimpleMapUnwrappingVpack();
        } finally {
        }
    }


    void __invoke_testEnumMapUnwrappingVpack() throws Exception {
        try {
            testEnumMapUnwrappingVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeArrayWithNullElementVpack() throws Exception {
        try {
            testDeserializeArrayWithNullElementVpack();
        } finally {
        }
    }


    void __invoke_testOrderedMapsVpack() throws Exception {
        try {
            testOrderedMapsVpack();
        } finally {
        }
    }

}
