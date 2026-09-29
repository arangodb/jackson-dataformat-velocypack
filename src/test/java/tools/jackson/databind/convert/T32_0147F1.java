package tools.jackson.databind.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0147F1 {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] NESTED_EMPTY_ARRAY = VPackWireFixtureTest.hex(
            "13 0a 45 68 65 6c 6c 6f 01 02");
private static final ObjectMapper VANILLA_MAPPER = new VPackMapper();
private static final ObjectReader DEFAULT_READER = VANILLA_MAPPER.reader();
private static final ObjectReader READER_WITH_ARRAYS = DEFAULT_READER
            .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
private static final ObjectMapper EMPTY_STRING_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper EMPTY_ARRAY_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper EMPTY_ARRAY_TO_NULL = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.AsNull))
            .build();
private static final ObjectMapper EMPTY_ARRAY_TO_TRY_CONVERT = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper EMPTY_ARRAY_TO_FAIL = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.Fail))
            .build();

    void testSettings() {
        assertFalse(VANILLA_MAPPER.isEnabled(
                DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT));
        assertFalse(DEFAULT_READER.isEnabled(
                DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT));
        assertTrue(READER_WITH_ARRAYS.isEnabled(
                DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT));
    }

    void testPOJOFromEmptyArray() throws Exception {
        Class<?> targetType = Bean.class;

        assertEmptyArrayFails(DEFAULT_READER, targetType);
        assertEmptyArrayFails(EMPTY_ARRAY_TO_FAIL, targetType);
        assertNull(EMPTY_ARRAY_TO_NULL.readValue(EMPTY_ARRAY, targetType));
        assertNull(EMPTY_ARRAY_TO_TRY_CONVERT.readValue(EMPTY_ARRAY, targetType));

        Bean result = EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Bean.class);
        assertEquals(new Bean(), result);

        ObjectMapper legacyThenFail = VPackMapper.builder()
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .withCoercionConfig(targetType, cfg ->
                        cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.Fail))
                .build();
        assertEmptyArrayFails(legacyThenFail, targetType);

        ObjectMapper typeAsEmpty = VPackMapper.builder()
                .disable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .withCoercionConfig(LogicalType.POJO, cfg ->
                        cfg.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.AsEmpty))
                .build();
        Bean typedResult = typeAsEmpty.readValue(EMPTY_ARRAY, Bean.class);
        assertEquals(new Bean(), typedResult);
    }

    void testMapFromEmptyArray() throws Exception {
        Class<?> targetType = Map.class;

        assertEmptyArrayFails(DEFAULT_READER, targetType);
        assertEmptyArrayFails(EMPTY_ARRAY_TO_FAIL, targetType);
        assertNull(EMPTY_ARRAY_TO_NULL.readValue(EMPTY_ARRAY, targetType));
        assertNull(EMPTY_ARRAY_TO_TRY_CONVERT.readValue(EMPTY_ARRAY, targetType));

        Map<?, ?> result = EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Map.class);
        assertEquals(0, result.size());
    }

    void testEnumMapFromEmptyArray() throws Exception {
        JavaType targetType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<EnumMap<ABC, String>>() { });

        assertNull(EMPTY_ARRAY_TO_NULL.readValue(EMPTY_ARRAY, targetType));
        EnumMap<?, ?> result = EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, targetType);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    void testNumbersFromEmptyArray() throws Exception {
        for (Class<?> targetType : new Class<?>[] {
                Boolean.class, Character.class,
                Byte.class, Short.class, Integer.class, Long.class,
                Float.class, Double.class,
                BigInteger.class, BigDecimal.class
        }) {
            assertEmptyArrayFails(DEFAULT_READER, targetType);
            assertEmptyArrayFails(EMPTY_ARRAY_TO_FAIL, targetType);
            assertNull(EMPTY_ARRAY_TO_NULL.readValue(EMPTY_ARRAY, targetType));
            assertNull(EMPTY_ARRAY_TO_TRY_CONVERT.readValue(EMPTY_ARRAY, targetType));
        }

        assertEquals(Boolean.FALSE, EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Boolean.class));
        assertEquals(Character.valueOf('\0'),
                EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Character.class));
        assertEquals(Byte.valueOf((byte) 0), EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Byte.class));
        assertEquals(Short.valueOf((short) 0), EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Short.class));
        assertEquals(Integer.valueOf(0), EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Integer.class));
        assertEquals(Long.valueOf(0L), EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Long.class));
        assertEquals(Float.valueOf(0f), EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Float.class));
        assertEquals(Double.valueOf(0d), EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, Double.class));
        assertEquals(BigInteger.ZERO, EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, BigInteger.class));
        assertEquals(new BigDecimal(BigInteger.ZERO),
                EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, BigDecimal.class));
    }

    void testOtherScalarsFromEmptyArray() throws Exception {
        for (Class<?> targetType : new Class<?>[] {
                String.class, StringBuilder.class,
                UUID.class, URL.class, URI.class,
                java.util.Date.class, Calendar.class
        }) {
            assertEmptyArrayFails(DEFAULT_READER, targetType);
            assertEmptyArrayFails(EMPTY_ARRAY_TO_FAIL, targetType);
            assertNull(EMPTY_ARRAY_TO_NULL.readValue(EMPTY_ARRAY, targetType));
            assertNull(EMPTY_ARRAY_TO_TRY_CONVERT.readValue(EMPTY_ARRAY, targetType));
        }

        assertEquals("", EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, String.class));
        StringBuilder builder = EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, StringBuilder.class);
        assertEquals(0, builder.length());
        assertEquals(new UUID(0L, 0L), EMPTY_ARRAY_TO_EMPTY.readValue(EMPTY_ARRAY, UUID.class));
    }

    void testStringFromEmptyArrayWithLegacyFeature() throws Exception {
        assertThrows(MismatchedInputException.class,
                () -> VANILLA_MAPPER.readValue(EMPTY_ARRAY, String.class));

        ObjectMapper legacyMapper = VPackMapper.builder()
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .build();
        assertNull(legacyMapper.readValue(EMPTY_ARRAY, String.class));

        List<String> list = legacyMapper.readValue(NESTED_EMPTY_ARRAY,
                new TypeReference<List<String>>() { });
        assertEquals(2, list.size());
        assertEquals("hello", list.get(0));
        assertNull(list.get(1));
    }
private static void assertVanillaEmptyStringFails(JavaType targetType) {
        assertThrows(DatabindException.class,
                () -> VANILLA_MAPPER.readerFor(targetType).readValue(EMPTY_STRING));
    }
private static void assertEmptyArrayFails(ObjectMapper mapper, Class<?> targetType) {
        assertEmptyArrayFails(mapper.reader(), targetType);
    }
private static void assertEmptyArrayFails(ObjectReader reader, Class<?> targetType) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.forType(targetType).readValue(EMPTY_ARRAY));
        assertTrue(failure.getMessage().contains("from Array value"), failure.getMessage());
    }
static class Bean {
        public String a = "foo";

        @Override
        public boolean equals(Object o) {
            return (o instanceof Bean b) && a.equals(b.a);
        }
    }
static class StringWrapper {
        public String str;
    }
enum ABC { A, B, C }

    void __invoke_testSettings() throws Exception {
        try {
            testSettings();
        } finally {
        }
    }


    void __invoke_testPOJOFromEmptyArray() throws Exception {
        try {
            testPOJOFromEmptyArray();
        } finally {
        }
    }


    void __invoke_testMapFromEmptyArray() throws Exception {
        try {
            testMapFromEmptyArray();
        } finally {
        }
    }


    void __invoke_testEnumMapFromEmptyArray() throws Exception {
        try {
            testEnumMapFromEmptyArray();
        } finally {
        }
    }


    void __invoke_testNumbersFromEmptyArray() throws Exception {
        try {
            testNumbersFromEmptyArray();
        } finally {
        }
    }


    void __invoke_testOtherScalarsFromEmptyArray() throws Exception {
        try {
            testOtherScalarsFromEmptyArray();
        } finally {
        }
    }


    void __invoke_testStringFromEmptyArrayWithLegacyFeature() throws Exception {
        try {
            testStringFromEmptyArrayWithLegacyFeature();
        } finally {
        }
    }

}
