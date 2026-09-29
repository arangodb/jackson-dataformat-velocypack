package tools.jackson.databind.deser.jdk;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

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

class T32_0267Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
private static final byte[] INT_NEGATIVE_42 = VPackWireFixtureTest.hex("20 d6");
private static final byte[] INT_DOUBLE_39_07 = VPackWireFixtureTest.hex(
            "1b 29 5c 8f c2 f5 88 43 40");
private static final byte[] INT_OBJECT_3 = VPackWireFixtureTest.hex(
            "14 06 41 76 33 01");
private static final byte[] INT_OBJECT_NULL = VPackWireFixtureTest.hex(
            "14 06 41 76 18 01");
private static final byte[] INT_ARRAY_NULL = VPackWireFixtureTest.hex(
            "13 04 18 01");
private static final byte[] INT_OBJECT_ARRAY_3 = VPackWireFixtureTest.hex(
            "14 09 41 76 13 04 33 01 01");
private static final byte[] INT_ROOT_OBJECT_ARRAY_3 = VPackWireFixtureTest.hex(
            "13 0c 14 09 41 76 13 04 33 01 01 01");
private static final byte[] INT_OBJECT_ARRAY_3_3 = VPackWireFixtureTest.hex(
            "14 0a 41 76 13 05 33 33 02 01");
private static final byte[] INT_OBJECT_ARRAY_NULL = VPackWireFixtureTest.hex(
            "14 09 41 76 13 04 18 01 01");
private static final byte[] INT_NESTED_NULL_ARRAY = VPackWireFixtureTest.hex(
            "13 07 13 04 18 01 01");
private static final byte[] INT_OBJECT_8 = VPackWireFixtureTest.hex(
            "14 06 41 76 38 01");
private static final byte[] DOUBLE_1_0 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 3f");
private static final byte[] DOUBLE_0_0 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 00");
private static final byte[] DOUBLE_NEGATIVE_0_3 = VPackWireFixtureTest.hex(
            "1b 33 33 33 33 33 33 d3 bf");
private static final byte[] DOUBLE_0_7 = VPackWireFixtureTest.hex(
            "1b 66 66 66 66 66 66 e6 3f");
private static final byte[] DOUBLE_42_012 = VPackWireFixtureTest.hex(
            "1b a8 c6 4b 37 89 01 45 40");
private static final byte[] DOUBLE_NEGATIVE_999 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 38 8f c0");
private static final byte[] DOUBLE_NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] EMPTY_INTEGER_WRAPPER_BYTE = VPackWireFixtureTest.hex(
            "14 0e 49 62 79 74 65 56 61 6c 75 65 40 01");
private static final byte[] EMPTY_INTEGER_WRAPPER_CHAR = VPackWireFixtureTest.hex(
            "14 0e 49 63 68 61 72 56 61 6c 75 65 40 01");
private static final byte[] EMPTY_INTEGER_WRAPPER_SHORT = VPackWireFixtureTest.hex(
            "14 0f 4a 73 68 6f 72 74 56 61 6c 75 65 40 01");
private static final byte[] EMPTY_INTEGER_WRAPPER_INT = VPackWireFixtureTest.hex(
            "14 0d 48 69 6e 74 56 61 6c 75 65 40 01");
private static final byte[] EMPTY_INTEGER_WRAPPER_LONG = VPackWireFixtureTest.hex(
            "14 0e 49 6c 6f 6e 67 56 61 6c 75 65 40 01");
private static final byte[] EMPTY_FLOAT_WRAPPER_FLOAT = VPackWireFixtureTest.hex(
            "14 0f 4a 66 6c 6f 61 74 56 61 6c 75 65 40 01");
private static final byte[] EMPTY_FLOAT_WRAPPER_DOUBLE = VPackWireFixtureTest.hex(
            "14 10 4b 64 6f 75 62 6c 65 56 61 6c 75 65 40 01");
private static final byte[] EMPTY_BOOLEAN_PRIMITIVE = VPackWireFixtureTest.hex(
            "14 11 4c 62 6f 6f 6c 65 61 6e 56 61 6c 75 65 40 01");
private static final byte[] INVALID_STRING_ARRAY = VPackWireFixtureTest.hex(
            "13 0a 46 66 6f 6f 62 61 72 01");
private static final byte[] SCALAR_OBJECT = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 28 0c 01");

    // Provenance: JDKScalarsDeserTest#testDoubleWrapper().
    void testDoubleWrapper() throws Exception {
        assertDouble(DOUBLE_1_0, "1.0", 1.0);
        assertDouble(DOUBLE_0_0, "0.0", 0.0);
        assertDouble(DOUBLE_NEGATIVE_0_3, "-0.3", -0.3);
        assertDouble(DOUBLE_0_7, "0.7", 0.7);
        assertDouble(DOUBLE_42_012, "42.012", 42.012);
        assertDouble(DOUBLE_NEGATIVE_999, "-999.0", -999.0);
        assertEquals(Double.valueOf(Double.NaN), MAPPER.readValue(text("NaN"), Double.class));
    }

    // Provenance: JDKScalarsDeserTest#testEmptyStringForBooleanPrimitive().
    void testEmptyStringForBooleanPrimitive() throws IOException {
        PrimitivesBean bean = MAPPER.readValue(EMPTY_BOOLEAN_PRIMITIVE, PrimitivesBean.class);
        assertFalse(bean.booleanValue);
    }

    // Provenance: JDKScalarsDeserTest#testEmptyStringForFloatPrimitives().
    void testEmptyStringForFloatPrimitives() throws IOException {
        PrimitivesBean bean = MAPPER.readValue(EMPTY_FLOAT_WRAPPER_FLOAT, PrimitivesBean.class);
        assertEquals(0.0f, bean.floatValue);
        bean = MAPPER.readValue(EMPTY_FLOAT_WRAPPER_DOUBLE, PrimitivesBean.class);
        assertEquals(0.0, bean.doubleValue);
    }

    // Provenance: JDKScalarsDeserTest#testEmptyStringForFloatWrappers().
    void testEmptyStringForFloatWrappers() throws IOException {
        WrappersBean bean = MAPPER.readValue(EMPTY_FLOAT_WRAPPER_FLOAT, WrappersBean.class);
        assertNull(bean.floatValue);
        bean = MAPPER.readValue(EMPTY_FLOAT_WRAPPER_DOUBLE, WrappersBean.class);
        assertNull(bean.doubleValue);
    }

    // Provenance: JDKScalarsDeserTest#testEmptyStringForIntegerPrimitives().
    void testEmptyStringForIntegerPrimitives() throws IOException {
        PrimitivesBean bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_BYTE, PrimitivesBean.class);
        assertEquals((byte) 0, bean.byteValue);
        bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_CHAR, PrimitivesBean.class);
        assertEquals((char) 0, bean.charValue);
        bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_SHORT, PrimitivesBean.class);
        assertEquals((short) 0, bean.shortValue);
        bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_INT, PrimitivesBean.class);
        assertEquals(0, bean.intValue);
        bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_LONG, PrimitivesBean.class);
        assertEquals(0L, bean.longValue);
    }

    // Provenance: JDKScalarsDeserTest#testEmptyStringForIntegerWrappers().
    void testEmptyStringForIntegerWrappers() throws IOException {
        WrappersBean bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_BYTE, WrappersBean.class);
        assertNull(bean.byteValue);
        bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_CHAR, WrappersBean.class);
        assertNull(bean.charValue);
        bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_SHORT, WrappersBean.class);
        assertNull(bean.shortValue);
        bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_INT, WrappersBean.class);
        assertNull(bean.intValue);
        bean = MAPPER.readValue(EMPTY_INTEGER_WRAPPER_LONG, WrappersBean.class);
        assertNull(bean.longValue);
    }

    // Provenance: JDKScalarsDeserTest#testFailForScalarFromObject().
    void testFailForScalarFromObject() throws Exception {
        assertScalarObjectFails(Byte.TYPE);
        assertScalarObjectFails(Short.TYPE);
        assertScalarObjectFails(Long.TYPE);
        assertScalarObjectFails(Float.TYPE);
        assertScalarObjectFails(Double.TYPE);
        assertScalarObjectFails(BigInteger.class);
        assertScalarObjectFails(BigDecimal.class);
    }

    // Provenance: JDKScalarsDeserTest#testFloatWrapper().
    void testFloatWrapper() throws Exception {
        assertFloat(DOUBLE_1_0, "1.0", 1.0f);
        assertFloat(DOUBLE_0_0, "0.0", 0.0f);
        assertFloat(DOUBLE_NEGATIVE_0_3, "-0.3", -0.3f);
        assertFloat(DOUBLE_0_7, "0.7", 0.7f);
        assertFloat(DOUBLE_42_012, "42.012", 42.012f);
        assertFloat(DOUBLE_NEGATIVE_999, "-999.0", -999.0f);
        assertEquals(Float.valueOf(Float.NaN), MAPPER.readValue(text("NaN"), Float.class));
    }

    // Provenance: JDKScalarsDeserTest#testIntPrimitive().
    void testIntPrimitive() throws Exception {
        IntBean result = MAPPER.readValue(INT_OBJECT_3, IntBean.class);
        assertEquals(3, result.value);
        result = MAPPER.readValue(INT_OBJECT_NULL, IntBean.class);
        assertNotNull(result);
        assertEquals(0, result.value);

        int[] array = MAPPER.readValue(INT_ARRAY_NULL, int[].class);
        assertNotNull(array);
        assertArrayEquals(new int[] { 0 }, array);

        ObjectReader noUnwrap = MAPPER.readerFor(IntBean.class)
                .without(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertThrows(MismatchedInputException.class,
                () -> noUnwrap.readValue(INT_OBJECT_ARRAY_3));

        ObjectReader unwrapping = MAPPER.readerFor(IntBean.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        result = unwrapping.readValue(INT_OBJECT_ARRAY_3);
        assertEquals(3, result.value);
        result = unwrapping.readValue(INT_ROOT_OBJECT_ARRAY_3);
        assertEquals(3, result.value);
        assertThrows(MismatchedInputException.class,
                () -> unwrapping.readValue(INT_OBJECT_ARRAY_3_3));

        result = unwrapping.readValue(INT_OBJECT_ARRAY_NULL);
        assertNotNull(result);
        assertEquals(0, result.value);

        array = MAPPER.readerFor(int[].class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(INT_NESTED_NULL_ARRAY);
        assertArrayEquals(new int[] { 0 }, array);
    }

    // Provenance: JDKScalarsDeserTest#testIntWithOverride().
    void testIntWithOverride() throws Exception {
        IntBean2 result = MAPPER.readValue(INT_OBJECT_8, IntBean2.class);
        assertEquals(9, result.value);
    }

    // Provenance: JDKScalarsDeserTest#testIntWrapper().
    void testIntWrapper() throws Exception {
        assertEquals(Integer.valueOf(-42), MAPPER.readValue(INT_NEGATIVE_42, Integer.class));
        assertEquals(Integer.valueOf(-1200), MAPPER.readValue(text("-1200"), Integer.class));
        assertEquals(Integer.valueOf(39), MAPPER.readValue(INT_DOUBLE_39_07, Integer.class));
    }

    // Provenance: JDKScalarsDeserTest#testInvalidStringCoercionFail().
    void testInvalidStringCoercionFail() throws IOException {
        assertInvalidStringFails(boolean[].class);
        assertInvalidStringFails(byte[].class);
        assertInvalidStringFails(short[].class);
        assertInvalidStringFails(int[].class);
        assertInvalidStringFails(long[].class);
        assertInvalidStringFails(float[].class);
        assertInvalidStringFails(double[].class);
    }
private static void assertDouble(byte[] nativeValue, String textValue, double expected)
            throws IOException {
        assertEquals(Double.valueOf(expected), MAPPER.readValue(nativeValue, Double.class));
        assertEquals(Double.valueOf(expected), MAPPER.readValue(text(textValue), Double.class));
    }
private static void assertFloat(byte[] nativeValue, String textValue, float expected)
            throws IOException {
        assertEquals(Float.valueOf(expected), MAPPER.readValue(nativeValue, Float.class));
        assertEquals(Float.valueOf(expected), MAPPER.readValue(text(textValue), Float.class));
    }
private static void assertScalarObjectFails(Class<?> targetType) {
        assertThrows(MismatchedInputException.class, () -> MAPPER.readValue(SCALAR_OBJECT, targetType));
    }
private static void assertInvalidStringFails(Class<?> targetType) {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readerFor(targetType).readValue(INVALID_STRING_ARRAY));
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
static class IntBean {
        public int value;

        public void setV(int value) {
            this.value = value;
        }
    }
static class IntBean2 extends IntBean {
        @Override
        public void setV(int value) {
            super.setV(value + 1);
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
static class WrappersBean {
        public Boolean booleanValue;
        public Byte byteValue;
        public Character charValue;
        public Short shortValue;
        public Integer intValue;
        public Long longValue;
        public Float floatValue;
        public Double doubleValue;
    }

    void __invoke_testDoubleWrapper() throws Exception {
        try {
            testDoubleWrapper();
        } finally {
        }
    }


    void __invoke_testEmptyStringForBooleanPrimitive() throws Exception {
        try {
            testEmptyStringForBooleanPrimitive();
        } finally {
        }
    }


    void __invoke_testEmptyStringForFloatPrimitives() throws Exception {
        try {
            testEmptyStringForFloatPrimitives();
        } finally {
        }
    }


    void __invoke_testEmptyStringForFloatWrappers() throws Exception {
        try {
            testEmptyStringForFloatWrappers();
        } finally {
        }
    }


    void __invoke_testEmptyStringForIntegerPrimitives() throws Exception {
        try {
            testEmptyStringForIntegerPrimitives();
        } finally {
        }
    }


    void __invoke_testEmptyStringForIntegerWrappers() throws Exception {
        try {
            testEmptyStringForIntegerWrappers();
        } finally {
        }
    }


    void __invoke_testFailForScalarFromObject() throws Exception {
        try {
            testFailForScalarFromObject();
        } finally {
        }
    }


    void __invoke_testFloatWrapper() throws Exception {
        try {
            testFloatWrapper();
        } finally {
        }
    }


    void __invoke_testIntPrimitive() throws Exception {
        try {
            testIntPrimitive();
        } finally {
        }
    }


    void __invoke_testIntWithOverride() throws Exception {
        try {
            testIntWithOverride();
        } finally {
        }
    }


    void __invoke_testIntWrapper() throws Exception {
        try {
            testIntWrapper();
        } finally {
        }
    }


    void __invoke_testInvalidStringCoercionFail() throws Exception {
        try {
            testInvalidStringCoercionFail();
        } finally {
        }
    }

}
