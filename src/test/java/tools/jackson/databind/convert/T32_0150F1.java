package tools.jackson.databind.convert;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0150F1 {
private static final byte[] ONE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] NEG_TWO_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 04 c0");
private static final byte[] THREE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 0c 40");
private static final byte[] FIVE_POINT_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 14 40");
private static final byte[] FIVE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 16 40");
private static final byte[] ZERO_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 e0 3f");
private static final byte[] LARGE_POINT_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 80 b5 43");
private static final byte[] NEG_TWO_POINT_FIVE_I = VPackWireFixtureTest.hex(
            "14 0e 41 69 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] NEG_TWO_POINT_FIVE_L = VPackWireFixtureTest.hex(
            "14 0e 41 6c 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] ONE_POINT_FIVE_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 00 f8 3f 01");
private static final byte[] NEG_TWO_POINT_FIVE_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] FIVE_POINT_FIVE_LIST = VPackWireFixtureTest.hex(
            "13 0e 34 1b 00 00 00 00 00 00 16 40 36 03");
private static final byte[] FIVE_POINT_FIVE_MAP = VPackWireFixtureTest.hex(
            "14 17 44 6b 65 79 31 34 44 6b 65 79 32 1b 00 00 00 00 00 00 16 40 02");
private static final byte[] THREE_POINT_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 08 40");
private static final byte[] NEG_TWO_POINT_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 c0");
private static final byte[] NEG_FIVE_POINT_ZERO_OBJECT = VPackWireFixtureTest.hex(
            "14 10 43 73 74 72 1b 00 00 00 00 00 00 14 c0 01");
private static final byte[] TWO_POINT_ZERO_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 00 00 40 01");
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectReader READER_LEGACY_FAIL = DEFAULT_MAPPER.reader()
            .without(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
private static final ObjectMapper MAPPER_TO_FAIL = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.Fail))
            .build();
private static final ObjectMapper MAPPER_TRY_CONVERT = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper MAPPER_TO_NULL = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper INTEGER_DEFAULT_MAPPER = new VPackMapper();

    void testCoerceConfigToConvert() throws Exception {
        assertSuccessfulFloatToStringCoercionWith(MAPPER_TRY_CONVERT);
    }

    void testCoerceConfigToEmpty() throws Exception {
        assertEquals("", MAPPER_TO_EMPTY.readValue(THREE_POINT_ZERO, String.class));
        StringWrapper wrapper = MAPPER_TO_EMPTY.readValue(NEG_FIVE_POINT_ZERO_OBJECT,
                StringWrapper.class);
        assertEquals("", wrapper.str);
        String[] array = MAPPER_TO_EMPTY.readValue(TWO_POINT_ZERO_ARRAY, String[].class);
        assertEquals(1, array.length);
        assertEquals("", array[0]);
    }

    void testCoerceConfigToFail() {
        assertStringCoerceFails(String.class, THREE_POINT_FIVE, "java.lang.String");
        assertStringCoerceFails(StringWrapper.class, NEG_FIVE_POINT_ZERO_OBJECT, "StringWrapper");
        assertStringCoerceFails(String[].class, TWO_POINT_ZERO_ARRAY, "java.lang.String");
    }

    void testCoerceConfigToNull() throws Exception {
        assertNull(MAPPER_TO_NULL.readValue(THREE_POINT_ZERO, String.class));
        StringWrapper wrapper = MAPPER_TO_NULL.readValue(NEG_FIVE_POINT_ZERO_OBJECT,
                StringWrapper.class);
        assertNull(wrapper.str);
        String[] array = MAPPER_TO_NULL.readValue(TWO_POINT_ZERO_ARRAY, String[].class);
        assertEquals(1, array.length);
        assertNull(array[0]);
    }
private static void assertSuccessfulFloatToStringCoercionWith(ObjectMapper mapper)
            throws Exception {
        assertEquals("3.0", mapper.readValue(THREE_POINT_ZERO, String.class));
        assertEquals("-2.0", mapper.readValue(NEG_TWO_POINT_ZERO, String.class));
        StringWrapper wrapper = mapper.readValue(NEG_FIVE_POINT_ZERO_OBJECT,
                StringWrapper.class);
        assertEquals("-5.0", wrapper.str);
        String[] array = mapper.readValue(TWO_POINT_ZERO_ARRAY, String[].class);
        assertEquals("2.0", array[0]);
    }
private static void assertLegacyFail(Class<?> targetType, byte[] input,
            String expectedMessagePart) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> READER_LEGACY_FAIL.forType(targetType).readValue(input));
        assertTrue(failure instanceof InvalidFormatException, failure.getMessage());
        assertTrue(failure.getMessage().contains("Cannot coerce Floating-point"),
                failure.getMessage());
        if (expectedMessagePart != null) {
            assertTrue(failure.getMessage().contains(expectedMessagePart), failure.getMessage());
        }
    }
private static void assertLegacyFail(TypeReference<?> targetType, byte[] input,
            String expectedValue) throws Exception {
        JavaType javaType = DEFAULT_MAPPER.constructType(targetType);
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> READER_LEGACY_FAIL.forType(javaType).readValue(input));
        if (expectedValue != null) {
            assertTrue(failure.getMessage().contains(expectedValue), failure.getMessage());
        }
    }
private static void assertStringCoerceFails(Class<?> targetType, byte[] input,
            String expectedMessagePart) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> MAPPER_TO_FAIL.readValue(input, targetType));
        assertTrue(failure.getMessage().contains("Cannot coerce Float"), failure.getMessage());
        assertTrue(failure.getMessage().contains(expectedMessagePart), failure.getMessage());
    }
static class IntWrapper {
        public int i;
    }
static class LongWrapper {
        public long l;
    }
static class StringWrapper {
        public String str;
    }

    void __invoke_testCoerceConfigToConvert() throws Exception {
        try {
            testCoerceConfigToConvert();
        } finally {
        }
    }


    void __invoke_testCoerceConfigToEmpty() throws Exception {
        try {
            testCoerceConfigToEmpty();
        } finally {
        }
    }


    void __invoke_testCoerceConfigToFail() throws Exception {
        try {
            testCoerceConfigToFail();
        } finally {
        }
    }


    void __invoke_testCoerceConfigToNull() throws Exception {
        try {
            testCoerceConfigToNull();
        } finally {
        }
    }

}
