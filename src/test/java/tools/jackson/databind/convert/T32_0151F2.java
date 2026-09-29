package tools.jackson.databind.convert;

import java.math.BigDecimal;

import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0151F2 {
private static final byte[] ONE = VPackWireFixtureTest.hex("31");
private static final byte[] TWO = VPackWireFixtureTest.hex("32");
private static final byte[] THREE = VPackWireFixtureTest.hex("33");
private static final byte[] FOUR = VPackWireFixtureTest.hex("34");
private static final byte[] NEG_ONE = VPackWireFixtureTest.hex("3f");
private static final byte[] NEG_TWO = VPackWireFixtureTest.hex("3e");
private static final byte[] NEG_FIVE = VPackWireFixtureTest.hex("3b");
private static final byte[] NEG_SEVEN = VPackWireFixtureTest.hex("20 f9");
private static final byte[] FOUR_HUNDRED_TWENTY = VPackWireFixtureTest.hex(
            "29 a4 01");
private static final byte[] THREE_THOUSAND_SIX_HUNDRED_FORTY_THREE =
            VPackWireFixtureTest.hex("29 3b 0e");
private static final byte[] LARGE_INTEGER = VPackWireFixtureTest.hex(
                    "2b ee d6 60 04");
private static final byte[] BIG_INTEGER = VPackWireFixtureTest.hex(
                    "2b 61 5a 3d 19");
private static final byte[] OBJECT_F = VPackWireFixtureTest.hex(
            "14 06 41 66 3b 01");
private static final byte[] OBJECT_D = VPackWireFixtureTest.hex(
            "14 06 41 64 32 01");
private static final byte[] OBJECT_STR_NEG_FIVE = VPackWireFixtureTest.hex(
            "14 08 43 73 74 72 3b 01");
private static final byte[] ARRAY_TWO = VPackWireFixtureTest.hex(
            "13 04 32 01");
private static final byte[] ARRAY_NEG_TWO = VPackWireFixtureTest.hex(
            "13 04 3e 01");
private static final byte[] ARRAY_NEG_SEVEN = VPackWireFixtureTest.hex(
            "13 05 20 f9 01");
private static final byte[] ARRAY_FOUR_HUNDRED_TWENTY = VPackWireFixtureTest.hex(
            "13 06 29 a4 01 01");
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_TO_FAIL_FLOAT = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Float, cfg -> cfg.setCoercion(
                    CoercionInputShape.Integer, CoercionAction.Fail))
            .build();
private static final ObjectMapper MAPPER_TRY_CONVERT_FLOAT = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Float, cfg -> cfg.setCoercion(
                    CoercionInputShape.Integer, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper MAPPER_TO_NULL_FLOAT = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Float, cfg -> cfg.setCoercion(
                    CoercionInputShape.Integer, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_TO_EMPTY_FLOAT = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Float, cfg -> cfg.setCoercion(
                    CoercionInputShape.Integer, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper LEGACY_SCALAR_COERCION_FAIL = VPackMapper.builder()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();
private static final ObjectMapper MAPPER_TO_FAIL_STRING = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg -> cfg.setCoercion(
                    CoercionInputShape.Integer, CoercionAction.Fail))
            .build();
private static final ObjectMapper MAPPER_TRY_CONVERT_STRING = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg -> cfg.setCoercion(
                    CoercionInputShape.Integer, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper MAPPER_TO_NULL_STRING = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg -> cfg.setCoercion(
                    CoercionInputShape.Integer, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_TO_EMPTY_STRING = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg -> cfg.setCoercion(
                    CoercionInputShape.Integer, CoercionAction.AsEmpty))
            .build();

    void testDefaultIntToStringCoercion() throws Exception {
        assertSuccessfulIntToStringCoercionWith(DEFAULT_MAPPER);
    }

    void testCoerceIntToStringConfigToConvert() throws Exception {
        assertSuccessfulIntToStringCoercionWith(MAPPER_TRY_CONVERT_STRING);
    }

    void testCoerceIntToStringConfigToNull() throws Exception {
        assertNull(MAPPER_TO_NULL_STRING.readValue(ONE, String.class));
        StringWrapper wrapper = MAPPER_TO_NULL_STRING.readValue(OBJECT_STR_NEG_FIVE,
                StringWrapper.class);
        assertNull(wrapper.str);
        String[] array = MAPPER_TO_NULL_STRING.readValue(ARRAY_TWO, String[].class);
        assertEquals(1, array.length);
        assertNull(array[0]);
    }

    void testCoerceIntToStringConfigToEmpty() throws Exception {
        assertEquals("", MAPPER_TO_EMPTY_STRING.readValue(THREE, String.class));
        StringWrapper wrapper = MAPPER_TO_EMPTY_STRING.readValue(OBJECT_STR_NEG_FIVE,
                StringWrapper.class);
        assertEquals("", wrapper.str);
        String[] array = MAPPER_TO_EMPTY_STRING.readValue(ARRAY_TWO, String[].class);
        assertEquals(1, array.length);
        assertEquals("", array[0]);
    }

    void testCoerceIntToStringConfigToFail() {
        assertIntegerToStringCoerceFails(String.class, THREE, "java.lang.String");
        assertIntegerToStringCoerceFails(StringWrapper.class, OBJECT_STR_NEG_FIVE, "StringWrapper");
        assertIntegerToStringCoerceFails(String[].class, ARRAY_TWO, "java.lang.String");
    }
private static void assertSuccessfulFloatToStringCoercionWith(ObjectMapper mapper)
            throws Exception {
        assertEquals("3.0", mapper.readValue(
                VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 08 40"), String.class));
        assertEquals("-2.0", mapper.readValue(
                VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 00 c0"), String.class));
        StringWrapper wrapper = mapper.readValue(
                VPackWireFixtureTest.hex("14 10 43 73 74 72 1b 00 00 00 00 00 00 14 c0 01"),
                StringWrapper.class);
        assertEquals("-5.0", wrapper.str);
        String[] array = mapper.readValue(
                VPackWireFixtureTest.hex("13 0c 1b 00 00 00 00 00 00 00 40 01"),
                String[].class);
        assertEquals("2.0", array[0]);
    }
private static void assertSuccessfulIntToFloatConversionsWith(ObjectMapper mapper)
            throws Exception {
        assertEquals(3.0f, mapper.readValue(THREE, Float.class));
        assertEquals(-2.0f, mapper.readValue(NEG_TWO, Float.TYPE));
        assertEquals(-5.0f, mapper.readValue(OBJECT_F, FloatWrapper.class).f);
        assertEquals(2.0f, mapper.readValue(ARRAY_TWO, float[].class)[0]);

        assertEquals(-1.0d, mapper.readValue(NEG_ONE, Double.class));
        assertEquals(4.0d, mapper.readValue(FOUR, Double.TYPE));
        assertEquals(2.0d, mapper.readValue(OBJECT_D, DoubleWrapper.class).d);
        assertEquals(-2.0d, mapper.readValue(ARRAY_NEG_TWO, double[].class)[0]);

        BigDecimal biggie = mapper.readValue(
                BIG_INTEGER,
                BigDecimal.class);
        assertEquals(new BigDecimal("423451233"), biggie);
    }
private static void assertSuccessfulIntToStringCoercionWith(ObjectMapper mapper)
            throws Exception {
        assertEquals("3", mapper.readValue(THREE, String.class));
        assertEquals("-2", mapper.readValue(NEG_TWO, String.class));
        StringWrapper wrapper = mapper.readValue(OBJECT_STR_NEG_FIVE, StringWrapper.class);
        assertEquals("-5", wrapper.str);
        String[] array = mapper.readValue(ARRAY_TWO, String[].class);
        assertEquals("2", array[0]);
    }
private static void assertIntegerCoerceFails(ObjectMapper mapper, Class<?> targetType,
            byte[] input, String targetDescription) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(input, targetType));
        assertTrue(failure.getMessage().contains("Cannot coerce Integer"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains(targetDescription), failure.getMessage());
    }
private static void assertIntegerToStringCoerceFails(Class<?> targetType, byte[] input,
            String targetDescription) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> MAPPER_TO_FAIL_STRING.readValue(input, targetType));
        assertTrue(failure.getMessage().contains("Cannot coerce Integer"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains(targetDescription), failure.getMessage());
    }
static class FloatWrapper {
        public float f;
    }
static class DoubleWrapper {
        public double d;
    }
static class StringWrapper {
        public String str;
    }

    void __invoke_testDefaultIntToStringCoercion() throws Exception {
        try {
            testDefaultIntToStringCoercion();
        } finally {
        }
    }


    void __invoke_testCoerceIntToStringConfigToConvert() throws Exception {
        try {
            testCoerceIntToStringConfigToConvert();
        } finally {
        }
    }


    void __invoke_testCoerceIntToStringConfigToNull() throws Exception {
        try {
            testCoerceIntToStringConfigToNull();
        } finally {
        }
    }


    void __invoke_testCoerceIntToStringConfigToEmpty() throws Exception {
        try {
            testCoerceIntToStringConfigToEmpty();
        } finally {
        }
    }


    void __invoke_testCoerceIntToStringConfigToFail() throws Exception {
        try {
            testCoerceIntToStringConfigToFail();
        } finally {
        }
    }

}
