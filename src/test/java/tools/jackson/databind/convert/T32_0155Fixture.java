package tools.jackson.databind.convert;

import java.util.concurrent.atomic.AtomicBoolean;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
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

class T32_0155Fixture {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] STRING_TRUE = VPackWireFixtureTest.hex(
            "44 74 72 75 65");
private static final byte[] STRING_FALSE = VPackWireFixtureTest.hex(
            "45 66 61 6c 73 65");
private static final byte[] STRING_TRUE_MIXED = VPackWireFixtureTest.hex(
            "44 54 72 75 65");
private static final byte[] STRING_FALSE_MIXED = VPackWireFixtureTest.hex(
            "45 46 61 6c 73 65");
private static final byte[] POJO_0 = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 30 01");
private static final byte[] POJO_1 = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 31 01");
private static final byte[] WRAPPER_LARGE_INTEGER = VPackWireFixtureTest.hex(
            "14 29"
            + "49 70 72 69 6d 69 74 69 76 65 2b 00 00 00 80"
            + "47 77 72 61 70 70 65 72 2b 00 00 00 80"
            + "44 63 74 6f 72 2b 00 00 00 80 03");
private static final byte[] BOOLEAN_ARRAY = VPackWireFixtureTest.hex(
            "13 12 30 28 0f 40 45 66 61 6c 73 65 44 54 72 75 65 05");
private static final byte[] INTEGER_0 = VPackWireFixtureTest.hex("30");
private static final byte[] INTEGER_1 = VPackWireFixtureTest.hex("31");
private static final byte[] INTEGER_42 = VPackWireFixtureTest.hex("28 2a");
private static final byte[] INTEGER_NEGATIVE_123 = VPackWireFixtureTest.hex(
            "20 85");
private static final byte[] DOUBLE_1_25 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f4 3f");
private static final byte[] INTEGER_111 = VPackWireFixtureTest.hex("28 6f");
private static final ObjectMapper DEFAULT_MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
private static final ObjectMapper LEGACY_NONCOERCING_MAPPER = VPackMapper.builder()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();
private static final ObjectMapper MAPPER_INT_TO_EMPTY = integerMapper(
            CoercionAction.AsEmpty);
private static final ObjectMapper MAPPER_INT_TRY_CONVERT = integerMapper(
            CoercionAction.TryConvert);
private static final ObjectMapper MAPPER_INT_TO_NULL = integerMapper(
            CoercionAction.AsNull);
private static final ObjectMapper MAPPER_TO_FAIL = integerMapper(
            CoercionAction.Fail);

    void testEmptyStringFailForBooleanPrimitive() throws Exception {
        final ObjectReader reader = DEFAULT_MAPPER.readerFor(BooleanPrimitiveBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(booleanPrimitiveWithEmptyString()));
        assertTrue(failure.getMessage().contains("Cannot coerce"), failure.getMessage());
        assertTrue(failure.getMessage().contains("boolean"), failure.getMessage());
    }

    void testStringToBooleanCoercionOk() throws Exception {
        assertBoolean(DEFAULT_MAPPER, INTEGER_1, Boolean.TYPE, true);
        assertBoolean(DEFAULT_MAPPER, INTEGER_1, Boolean.class, true);
        assertBoolean(DEFAULT_MAPPER, STRING_TRUE, Boolean.TYPE, true);
        assertBoolean(DEFAULT_MAPPER, STRING_TRUE, Boolean.class, true);
        assertBoolean(DEFAULT_MAPPER, STRING_TRUE_MIXED, Boolean.TYPE, true);
        assertBoolean(DEFAULT_MAPPER, STRING_TRUE_MIXED, Boolean.class, true);
        assertBoolean(DEFAULT_MAPPER, INTEGER_0, Boolean.TYPE, false);
        assertBoolean(DEFAULT_MAPPER, INTEGER_0, Boolean.class, false);
        assertBoolean(DEFAULT_MAPPER, STRING_FALSE, Boolean.TYPE, false);
        assertBoolean(DEFAULT_MAPPER, STRING_FALSE, Boolean.class, false);
        assertBoolean(DEFAULT_MAPPER, STRING_FALSE_MIXED, Boolean.TYPE, false);
        assertBoolean(DEFAULT_MAPPER, STRING_FALSE_MIXED, Boolean.class, false);
    }

    void testStringToBooleanCoercionFail() throws Exception {
        assertStringCoercionFails(STRING_TRUE, Boolean.TYPE);
        assertStringCoercionFails(STRING_TRUE, Boolean.class);
        assertStringCoercionFails(STRING_TRUE_MIXED, Boolean.TYPE);
        assertStringCoercionFails(STRING_TRUE_MIXED, Boolean.class);
        assertStringCoercionFails(STRING_FALSE, Boolean.TYPE);
        assertStringCoercionFails(STRING_FALSE, Boolean.class);
    }

    void testIntToBooleanCoercionSuccessPojo() throws Exception {
        BooleanPOJO value = DEFAULT_MAPPER.readValue(POJO_0, BooleanPOJO.class);
        assertFalse(value.value);
        value = DEFAULT_MAPPER.readValue(POJO_1, BooleanPOJO.class);
        assertTrue(value.value);
    }

    void testIntToBooleanCoercionSuccessRoot() throws Exception {
        assertEquals(Boolean.FALSE, DEFAULT_MAPPER.readValue(INTEGER_0, Boolean.class));
        assertEquals(Boolean.TRUE, DEFAULT_MAPPER.readValue(
                VPackWireFixtureTest.hex("3b"), Boolean.class));

        AtomicBoolean value = DEFAULT_MAPPER.readValue(INTEGER_0, AtomicBoolean.class);
        assertFalse(value.get());
        value = DEFAULT_MAPPER.readValue(INTEGER_111, AtomicBoolean.class);
        assertTrue(value.get());
    }

    void testLongToBooleanCoercionOk() throws Exception {
        BooleanWrapper value = DEFAULT_MAPPER.readValue(
                WRAPPER_LARGE_INTEGER, BooleanWrapper.class);
        assertEquals(Boolean.TRUE, value.wrapper);
        assertTrue(value.primitive);
        assertEquals(Boolean.TRUE, value.ctor);

        value = DEFAULT_MAPPER.readValue(BOOLEAN_WRAPPER_ZERO(), BooleanWrapper.class);
        assertEquals(Boolean.FALSE, value.wrapper);
        assertFalse(value.primitive);
        assertEquals(Boolean.FALSE, value.ctor);

        boolean[] values = DEFAULT_MAPPER.readValue(BOOLEAN_ARRAY, boolean[].class);
        assertEquals(5, values.length);
        assertFalse(values[0]);
        assertTrue(values[1]);
        assertFalse(values[2]);
        assertFalse(values[3]);
        assertTrue(values[4]);
    }

    void testIntToBooleanCoercionFailuresUseNativeBinaryInput() throws Exception {
        assertIntegerCoercionFails(POJO_1, BooleanPOJO.class);
        assertIntegerCoercionFails(INTEGER_1, Boolean.TYPE);
        assertIntegerCoercionFails(INTEGER_1, Boolean.class);
        assertNumericCoercionFails(DOUBLE_1_25, Boolean.TYPE);
        assertNumericCoercionFails(DOUBLE_1_25, Boolean.class);
    }

    void testIntToNullCoercion() throws Exception {
        assertNull(MAPPER_INT_TO_NULL.readValue(INTEGER_0, Boolean.class));
        assertNull(MAPPER_INT_TO_NULL.readValue(INTEGER_1, Boolean.class));
        assertEquals(Boolean.FALSE, MAPPER_INT_TO_NULL.readValue(INTEGER_0, Boolean.TYPE));
        assertEquals(Boolean.FALSE, MAPPER_INT_TO_NULL.readValue(INTEGER_1, Boolean.TYPE));
        assertNull(MAPPER_INT_TO_NULL.readValue(INTEGER_0, AtomicBoolean.class));
        assertNull(MAPPER_INT_TO_NULL.readValue(INTEGER_1, AtomicBoolean.class));

        BooleanPOJO value = MAPPER_INT_TO_NULL.readValue(POJO_0, BooleanPOJO.class);
        assertFalse(value.value);
        value = MAPPER_INT_TO_NULL.readValue(POJO_1, BooleanPOJO.class);
        assertFalse(value.value);
    }

    void testIntToEmptyCoercion() throws Exception {
        assertEquals(Boolean.FALSE, MAPPER_INT_TO_EMPTY.readValue(INTEGER_0, Boolean.class));
        assertEquals(Boolean.FALSE, MAPPER_INT_TO_EMPTY.readValue(INTEGER_1, Boolean.class));
        assertEquals(Boolean.FALSE, MAPPER_INT_TO_EMPTY.readValue(INTEGER_0, Boolean.TYPE));
        assertEquals(Boolean.FALSE, MAPPER_INT_TO_EMPTY.readValue(INTEGER_1, Boolean.TYPE));

        AtomicBoolean value = MAPPER_INT_TO_EMPTY.readValue(INTEGER_0, AtomicBoolean.class);
        assertFalse(value.get());
        value = MAPPER_INT_TO_EMPTY.readValue(INTEGER_1, AtomicBoolean.class);
        assertFalse(value.get());

        BooleanPOJO pojo = MAPPER_INT_TO_EMPTY.readValue(POJO_0, BooleanPOJO.class);
        assertFalse(pojo.value);
        pojo = MAPPER_INT_TO_EMPTY.readValue(POJO_1, BooleanPOJO.class);
        assertFalse(pojo.value);
    }

    void testIntToTryCoercion() throws Exception {
        assertEquals(Boolean.FALSE, MAPPER_INT_TRY_CONVERT.readValue(INTEGER_0, Boolean.class));
        assertEquals(Boolean.TRUE, MAPPER_INT_TRY_CONVERT.readValue(INTEGER_1, Boolean.class));
        assertEquals(Boolean.FALSE, MAPPER_INT_TRY_CONVERT.readValue(INTEGER_0, Boolean.TYPE));
        assertEquals(Boolean.TRUE, MAPPER_INT_TRY_CONVERT.readValue(INTEGER_1, Boolean.TYPE));

        AtomicBoolean value = MAPPER_INT_TRY_CONVERT.readValue(INTEGER_0, AtomicBoolean.class);
        assertFalse(value.get());
        value = MAPPER_INT_TRY_CONVERT.readValue(INTEGER_1, AtomicBoolean.class);
        assertTrue(value.get());

        BooleanPOJO pojo = MAPPER_INT_TRY_CONVERT.readValue(POJO_0, BooleanPOJO.class);
        assertFalse(pojo.value);
        pojo = MAPPER_INT_TRY_CONVERT.readValue(POJO_1, BooleanPOJO.class);
        assertTrue(pojo.value);
    }

    void testFailFromInteger() throws Exception {
        assertIntegerCoercionFails(POJO_0, BooleanPOJO.class);
        assertIntegerCoercionFails(POJO_1, BooleanPOJO.class);
        assertIntegerCoercionFails(INTEGER_0, Boolean.class);
        assertIntegerCoercionFails(INTEGER_42, Boolean.class);
        assertIntegerCoercionFails(INTEGER_0, Boolean.TYPE);
        assertIntegerCoercionFails(INTEGER_42, Boolean.TYPE);
        assertIntegerCoercionFails(INTEGER_0, AtomicBoolean.class);
        assertIntegerCoercionFails(INTEGER_NEGATIVE_123, AtomicBoolean.class);
    }
private static ObjectMapper integerMapper(CoercionAction action) {
        return VPackMapper.builder().withCoercionConfig(LogicalType.Boolean,
                cfg -> cfg.setCoercion(CoercionInputShape.Integer, action)).build();
    }
private static void assertBoolean(ObjectMapper mapper, byte[] input,
            Class<?> target, boolean expected) throws Exception {
        assertEquals(expected, mapper.readValue(input, target));
    }
private static void assertStringCoercionFails(byte[] input, Class<?> target) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> LEGACY_NONCOERCING_MAPPER.readValue(input, target));
        assertTrue(failure.getMessage().contains("Cannot coerce"), failure.getMessage());
    }
private static void assertIntegerCoercionFails(byte[] input, Class<?> target) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> MAPPER_TO_FAIL.readValue(input, target));
        assertTrue(failure.getMessage().contains("Cannot coerce"), failure.getMessage());
        assertNotNull(failure.getLocation());
    }
private static void assertNumericCoercionFails(byte[] input, Class<?> target) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> LEGACY_NONCOERCING_MAPPER.readValue(input, target));
        assertNotNull(failure.getLocation());
    }
private static byte[] booleanPrimitiveWithEmptyString() {
        return VPackWireFixtureTest.hex(
                "14 11 4c 62 6f 6f 6c 65 61 6e 56 61 6c 75 65 40 01");
    }
private static byte[] BOOLEAN_WRAPPER_ZERO() {
        return VPackWireFixtureTest.hex(
                "14 1d"
                + "49 70 72 69 6d 69 74 69 76 65 30"
                + "47 77 72 61 70 70 65 72 30"
                + "44 63 74 6f 72 30 03");
    }
static class BooleanPOJO {
        public boolean value;
    }
static class BooleanPrimitiveBean {
        public boolean booleanValue = true;
    }
static class BooleanWrapper {
        public Boolean wrapper;
        public boolean primitive;
        protected Boolean ctor;

        @com.fasterxml.jackson.annotation.JsonCreator
        public BooleanWrapper(
                @com.fasterxml.jackson.annotation.JsonProperty("ctor") Boolean value) {
            ctor = value;
        }
    }

    void __invoke_testEmptyStringFailForBooleanPrimitive() throws Exception {
        try {
            testEmptyStringFailForBooleanPrimitive();
        } finally {
        }
    }


    void __invoke_testStringToBooleanCoercionOk() throws Exception {
        try {
            testStringToBooleanCoercionOk();
        } finally {
        }
    }


    void __invoke_testStringToBooleanCoercionFail() throws Exception {
        try {
            testStringToBooleanCoercionFail();
        } finally {
        }
    }


    void __invoke_testIntToBooleanCoercionSuccessPojo() throws Exception {
        try {
            testIntToBooleanCoercionSuccessPojo();
        } finally {
        }
    }


    void __invoke_testIntToBooleanCoercionSuccessRoot() throws Exception {
        try {
            testIntToBooleanCoercionSuccessRoot();
        } finally {
        }
    }


    void __invoke_testLongToBooleanCoercionOk() throws Exception {
        try {
            testLongToBooleanCoercionOk();
        } finally {
        }
    }


    void __invoke_testIntToBooleanCoercionFailuresUseNativeBinaryInput() throws Exception {
        try {
            testIntToBooleanCoercionFailuresUseNativeBinaryInput();
        } finally {
        }
    }


    void __invoke_testIntToNullCoercion() throws Exception {
        try {
            testIntToNullCoercion();
        } finally {
        }
    }


    void __invoke_testIntToEmptyCoercion() throws Exception {
        try {
            testIntToEmptyCoercion();
        } finally {
        }
    }


    void __invoke_testIntToTryCoercion() throws Exception {
        try {
            testIntToTryCoercion();
        } finally {
        }
    }


    void __invoke_testFailFromInteger() throws Exception {
        try {
            testFailFromInteger();
        } finally {
        }
    }

}
