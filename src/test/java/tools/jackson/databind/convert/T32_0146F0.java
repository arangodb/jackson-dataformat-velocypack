package tools.jackson.databind.convert;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0146F0 {
private static final byte[] FALSE = VPackWireFixtureTest.hex("19");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] OBJECT_FALSE = VPackWireFixtureTest.hex(
            "14 08 43 73 74 72 19 01");
private static final byte[] ARRAY_TRUE = VPackWireFixtureTest.hex(
            "13 04 1a 01");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_TO_FAIL = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail))
            .build();
private static final ObjectMapper MAPPER_TRY_CONVERT = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Boolean, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper MAPPER_TO_NULL = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Boolean, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Boolean, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper MAPPER_EMPTY_STRING_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
            .build();

    void testDefaultBooleanToStringCoercion() throws Exception {
        assertSuccessfulBooleanToStringCoercionWith(DEFAULT_MAPPER);
    }

    void testCoerceConfigToConvert() throws Exception {
        assertSuccessfulBooleanToStringCoercionWith(MAPPER_TRY_CONVERT);
    }

    void testCoerceConfigToNull() throws Exception {
        assertNull(MAPPER_TO_NULL.readValue(TRUE, String.class));
        StringWrapper wrapper = MAPPER_TO_NULL.readValue(OBJECT_FALSE, StringWrapper.class);
        assertNull(wrapper.str);
        String[] array = MAPPER_TO_NULL.readValue(ARRAY_TRUE, String[].class);
        assertEquals(1, array.length);
        assertNull(array[0]);
    }

    void testCoerceConfigToEmpty() throws Exception {
        assertEquals("", MAPPER_TO_EMPTY.readValue(TRUE, String.class));
        StringWrapper wrapper = MAPPER_TO_EMPTY.readValue(OBJECT_FALSE, StringWrapper.class);
        assertEquals("", wrapper.str);
        String[] array = MAPPER_TO_EMPTY.readValue(ARRAY_TRUE, String[].class);
        assertEquals(1, array.length);
        assertEquals("", array[0]);
    }

    void testCoerceConfigToFail() throws Exception {
        verifyBooleanCoerceFail(MAPPER_TO_FAIL, String.class, TRUE, "java.lang.String");
        verifyBooleanCoerceFail(MAPPER_TO_FAIL, StringWrapper.class, OBJECT_FALSE,
                "StringWrapper");
        verifyBooleanCoerceFail(MAPPER_TO_FAIL, String[].class, ARRAY_TRUE,
                "java.lang.String");
    }
private static void assertSuccessfulBooleanToStringCoercionWith(ObjectMapper mapper)
            throws Exception {
        assertEquals("false", mapper.readValue(FALSE, String.class));
        assertEquals("true", mapper.readValue(TRUE, String.class));

        StringWrapper wrapper = mapper.readValue(OBJECT_FALSE, StringWrapper.class);
        assertEquals("false", wrapper.str);
        String[] array = mapper.readValue(ARRAY_TRUE, String[].class);
        assertEquals("true", array[0]);
    }
private static void verifyBooleanCoerceFail(ObjectMapper mapper, Class<?> targetType,
            byte[] input, String targetDescription) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(input, targetType));
        assertEquals(true, failure.getMessage().contains("Cannot coerce Boolean"),
                failure.getMessage());
        assertEquals(true, failure.getMessage().contains(targetDescription),
                failure.getMessage());
    }
private static void assertEmptyAfterStringCoercion(Class<?> targetType,
            boolean verifyVanillaFailure) throws Exception {
        if (verifyVanillaFailure) {
            assertVanillaEmptyStringFails(DEFAULT_MAPPER.constructType(targetType));
        }
        Object result = MAPPER_EMPTY_STRING_TO_EMPTY.readValue(EMPTY_STRING, targetType);
        assertNotNull(result);
        assertEquals(0, java.lang.reflect.Array.getLength(result));
    }
private static void assertVanillaEmptyStringFails(JavaType targetType) {
        assertThrows(DatabindException.class,
                () -> DEFAULT_MAPPER.readerFor(targetType).readValue(EMPTY_STRING));
    }
static class StringWrapper {
        public String str;
    }
enum ABC { A, B, C }

    void __invoke_testDefaultBooleanToStringCoercion() throws Exception {
        try {
            testDefaultBooleanToStringCoercion();
        } finally {
        }
    }


    void __invoke_testCoerceConfigToConvert() throws Exception {
        try {
            testCoerceConfigToConvert();
        } finally {
        }
    }


    void __invoke_testCoerceConfigToNull() throws Exception {
        try {
            testCoerceConfigToNull();
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

}
