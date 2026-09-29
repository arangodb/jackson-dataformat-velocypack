package tools.jackson.databind.convert;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0154F1 {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] STRING_28 = VPackWireFixtureTest.hex("42 32 38");
private static final byte[] STRING_37 = VPackWireFixtureTest.hex("42 33 37");
private static final byte[] STRING_39 = VPackWireFixtureTest.hex("42 33 39");
private static final byte[] STRING_42 = VPackWireFixtureTest.hex("42 34 32");
private static final byte[] STRING_50 = VPackWireFixtureTest.hex("42 35 30");
private static final byte[] STRING_52 = VPackWireFixtureTest.hex("42 35 32");
private static final byte[] STRING_55 = VPackWireFixtureTest.hex("42 35 35");
private static final byte[] STRING_60 = VPackWireFixtureTest.hex("42 36 30");
private static final byte[] STRING_77 = VPackWireFixtureTest.hex("42 37 37");
private static final byte[] STRING_99 = VPackWireFixtureTest.hex("42 39 39");
private static final byte[] STRING_12 = VPackWireFixtureTest.hex("42 31 32");
private static final byte[] STRING_15 = VPackWireFixtureTest.hex("42 31 35");
private static final byte[] STRING_19 = VPackWireFixtureTest.hex("42 31 39");
private static final byte[] STRING_25 = VPackWireFixtureTest.hex("42 32 35");
private static final byte[] STRING_29 = VPackWireFixtureTest.hex("42 32 39");
private static final byte[] STRING_34 = VPackWireFixtureTest.hex("42 33 34");
private static final byte[] STRING_123 = VPackWireFixtureTest.hex("43 31 32 33");
private static final byte[] STRING_344 = VPackWireFixtureTest.hex("43 33 34 34");
private static final byte[] STRING_534 = VPackWireFixtureTest.hex("43 35 33 34");
private static final byte[] STRING_999 = VPackWireFixtureTest.hex("43 39 39 39");
private static final byte[] STRING_1234 = VPackWireFixtureTest.hex("44 31 32 33 34");
private static final byte[] STRING_1242 = VPackWireFixtureTest.hex("44 31 32 34 32");
private static final byte[] STRING_190 = VPackWireFixtureTest.hex("43 31 39 30");
private static final byte[] STRING_136 = VPackWireFixtureTest.hex("43 31 33 36");
private static final byte[] STRING_256 = VPackWireFixtureTest.hex("43 32 35 36");
private static final byte[] STRING_1379 = VPackWireFixtureTest.hex("44 31 33 37 39");
private static final byte[] STRING_25236256 = VPackWireFixtureTest.hex(
            "48 32 35 32 33 36 32 35 36");
private static final byte[] STRING_NEG19 = VPackWireFixtureTest.hex("43 2d 31 39");
private static final byte[] STRING_NEG255 = VPackWireFixtureTest.hex("44 2d 32 35 35");
private static final byte[] STRING_NEG126 = VPackWireFixtureTest.hex("44 2d 31 32 36");
private static final byte[] STRING_22 = VPackWireFixtureTest.hex("42 32 32");
private static final byte[] STRING_155 = VPackWireFixtureTest.hex("43 31 35 35");
private static final byte[] STRING_225 = VPackWireFixtureTest.hex("44 2d 32 32 35");
private static final byte[] STRING_425 = VPackWireFixtureTest.hex("44 2d 34 32 35");
private static final byte[] STRING_738 = VPackWireFixtureTest.hex("43 37 33 38");
private static final byte[] STRING_95007 = VPackWireFixtureTest.hex(
            "45 39 35 30 30 37");
private static final byte[] STRING_NEG13 = VPackWireFixtureTest.hex(
            "43 2d 31 33");
private static final byte[] STRING_NEG25 = VPackWireFixtureTest.hex(
            "43 2d 32 35");
private static final byte[] STRING_NEG99 = VPackWireFixtureTest.hex(
            "43 2d 39 39");
private static final byte[] STRING_NEG128 = VPackWireFixtureTest.hex(
            "44 2d 31 32 38");
private static final byte[] STRING_NEG135 = VPackWireFixtureTest.hex(
            "44 2d 31 33 35");
private static final byte[] STRING_NEG178 = VPackWireFixtureTest.hex(
            "44 2d 31 37 38");
private static final byte[] STRING_NEG425 = VPackWireFixtureTest.hex(
            "44 2d 34 32 35");
private static final byte[] STRING_NEG225 = STRING_225;
private static final byte[] STRING_2210 = VPackWireFixtureTest.hex(
            "44 32 32 31 30");
private static final byte[] STRING_25236 = VPackWireFixtureTest.hex(
            "45 32 35 32 33 36");
private static final byte[] STRING_25000000 = VPackWireFixtureTest.hex(
            "48 32 35 30 30 30 30 30 30");
private static final byte[] INT_WRAPPER_37 = VPackWireFixtureTest.hex(
            "14 08 41 69 42 33 37 01");
private static final byte[] INT_WRAPPER_NEG225 = VPackWireFixtureTest.hex(
            "14 0a 41 69 44 2d 32 32 35 01");
private static final byte[] LONG_WRAPPER_NEG13 = VPackWireFixtureTest.hex(
            "14 09 41 6c 43 2d 31 33 01");
private static final byte[] LONG_WRAPPER_NEG225 = VPackWireFixtureTest.hex(
            "14 0a 41 6c 44 2d 32 32 35 01");
private static final byte[] LONG_WRAPPER_77 = VPackWireFixtureTest.hex(
            "14 08 41 6c 42 37 37 01");
private static final byte[] INT_ARRAY_42 = VPackWireFixtureTest.hex(
            "13 06 42 34 32 01");
private static final byte[] INT_ARRAY_26 = VPackWireFixtureTest.hex(
            "13 06 42 32 36 01");
private static final byte[] INT_ARRAY_25 = VPackWireFixtureTest.hex(
            "13 06 42 32 35 01");
private static final byte[] INT_ARRAY_256 = VPackWireFixtureTest.hex(
            "13 07 43 32 35 36 01");
private static final byte[] INT_ARRAY_2210 = VPackWireFixtureTest.hex(
            "13 08 44 32 32 31 30 01");
private static final byte[] INT_ARRAY_NEG128 = VPackWireFixtureTest.hex(
            "13 08 44 2d 31 32 38 01");
private static final byte[] LONG_ARRAY_0 = VPackWireFixtureTest.hex(
            "13 05 41 30 01");
private static final byte[] LONG_ARRAY_26 = VPackWireFixtureTest.hex(
            "13 06 42 32 36 01");
private static final byte[] LONG_ARRAY_190 = VPackWireFixtureTest.hex(
            "13 07 43 31 39 30 01");
private static final byte[] LONG_ARRAY_136 = VPackWireFixtureTest.hex(
            "13 07 43 31 33 36 01");
private static final byte[] LONG_ARRAY_NEG135 = VPackWireFixtureTest.hex(
            "13 08 44 2d 31 33 35 01");
private static final byte[] LONG_ARRAY_22 = VPackWireFixtureTest.hex(
            "13 06 42 32 32 01");
private static final byte[] SHORT_ARRAY_25 = VPackWireFixtureTest.hex(
            "13 06 42 32 35 01");
private static final byte[] SHORT_ARRAY_NEG135 = VPackWireFixtureTest.hex(
            "13 08 44 2d 31 33 35 01");
private static final byte[] SHORT_ARRAY_NEG126 = VPackWireFixtureTest.hex(
            "13 08 44 2d 31 32 36 01");
private static final byte[] BYTE_ARRAY_25 = SHORT_ARRAY_25;
private static final byte[] BYTE_ARRAY_NEG13 = VPackWireFixtureTest.hex(
            "13 07 43 2d 31 33 01");
private static final byte[] BYTE_ARRAY_NEG1379 = VPackWireFixtureTest.hex(
            "13 09 44 2d 31 33 37 39 01");
private static final byte[] BIG_INTEGER_ARRAY_25 = VPackWireFixtureTest.hex(
            "13 06 42 32 35 01");
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectReader READER_LEGACY_FAIL = VPackMapper.builder()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build().reader();
private static final ObjectMapper MAPPER_TO_EMPTY = integerMapper(CoercionAction.AsEmpty);
private static final ObjectMapper MAPPER_TRY_CONVERT = integerMapper(CoercionAction.TryConvert);
private static final ObjectMapper MAPPER_TO_NULL = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.String, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_TO_FAIL = integerMapper(CoercionAction.Fail);

    void testLegacyStringToIntCoercion() throws Exception {
        assertEquals(28, DEFAULT_MAPPER.readValue(STRING_28, Integer.class));
        assertEquals(37, DEFAULT_MAPPER.readValue(INT_WRAPPER_37, IntWrapper.class).i);
        assertEquals(42, DEFAULT_MAPPER.readValue(INT_ARRAY_42, int[].class)[0]);
        assertEquals(39L, DEFAULT_MAPPER.readValue(STRING_39, Long.class));
        assertEquals(-13L, DEFAULT_MAPPER.readValue(LONG_WRAPPER_NEG13, LongWrapper.class).l);
        assertEquals(0L, DEFAULT_MAPPER.readValue(LONG_ARRAY_0, long[].class)[0]);
        assertEquals(42, DEFAULT_MAPPER.readValue(STRING_42, Short.class).intValue());
        assertEquals(95007, DEFAULT_MAPPER.readValue(STRING_95007, BigInteger.class).intValue());
    }

    void testLegacyFailStringToInt() throws Exception {
        verifyFail(READER_LEGACY_FAIL, Integer.class, STRING_52, "java.lang.Integer");
        verifyFail(READER_LEGACY_FAIL, int.class, STRING_37, "int");
        verifyFail(READER_LEGACY_FAIL, IntWrapper.class, INT_WRAPPER_37, "int");
        verifyFail(READER_LEGACY_FAIL, int[].class, INT_ARRAY_NEG128, "element of `int[]`");
    }

    void testLegacyFailStringToLong() throws Exception {
        verifyFail(READER_LEGACY_FAIL, Long.class, STRING_55, "java.lang.Long");
        verifyFail(READER_LEGACY_FAIL, long.class, STRING_NEG25, "long");
        verifyFail(READER_LEGACY_FAIL, LongWrapper.class, LONG_WRAPPER_77, "long");
        verifyFail(READER_LEGACY_FAIL, long[].class, LONG_ARRAY_136, "element of `long[]`");
    }

    void testLegacyFailStringToOther() throws Exception {
        verifyFail(READER_LEGACY_FAIL, Short.class, STRING_50, "java.lang.Short");
        verifyFail(READER_LEGACY_FAIL, short.class, STRING_NEG255, "short");
        verifyFail(READER_LEGACY_FAIL, short[].class, SHORT_ARRAY_NEG126, "element of `short[]`");
        verifyFail(READER_LEGACY_FAIL, Byte.class, STRING_60, "java.lang.Byte");
        verifyFail(READER_LEGACY_FAIL, byte.class, STRING_NEG25, "byte");
        verifyFail(READER_LEGACY_FAIL, byte[].class, BYTE_ARRAY_NEG13, "element of `byte[]`");
        verifyFail(READER_LEGACY_FAIL, BigInteger.class, STRING_25236, "java.math.BigInteger");
        verifyFail(READER_LEGACY_FAIL, AtomicLong.class, STRING_25236, "java.util.concurrent.atomic.AtomicLong");
    }

    void testCoerceConfigStringToNull() throws Exception {
        assertNull(MAPPER_TO_NULL.readValue(STRING_155, Integer.class));
        assertEquals(0, MAPPER_TO_NULL.readValue(STRING_NEG178, int.class));
        assertEquals(0, MAPPER_TO_NULL.readValue(INT_WRAPPER_NEG225, IntWrapper.class).i);
        assertEquals(0, MAPPER_TO_NULL.readValue(INT_ARRAY_26, int[].class)[0]);
        assertNull(MAPPER_TO_NULL.readValue(STRING_25, Long.class));
        assertEquals(0L, MAPPER_TO_NULL.readValue(STRING_NEG425, long.class));
        assertEquals(0L, MAPPER_TO_NULL.readValue(LONG_WRAPPER_NEG225, LongWrapper.class).l);
        assertEquals(0L, MAPPER_TO_NULL.readValue(LONG_ARRAY_190, long[].class)[0]);
        assertNull(MAPPER_TO_NULL.readValue(STRING_29, Short.class));
        assertEquals(0, MAPPER_TO_NULL.readValue(STRING_NEG425, short.class).intValue());
        assertNull(MAPPER_TO_NULL.readValue(STRING_29, Byte.class));
        assertEquals(0, MAPPER_TO_NULL.readValue(STRING_NEG425, byte.class).intValue());
        assertNull(MAPPER_TO_NULL.readValue(STRING_25000000, BigInteger.class));
        assertNull(MAPPER_TO_NULL.readValue(BIG_INTEGER_ARRAY_25, BigInteger[].class)[0]);
    }

    void testCoerceConfigStringToEmpty() throws Exception {
        assertEquals(0, MAPPER_TO_EMPTY.readValue(STRING_12, Integer.class));
        assertEquals(0, MAPPER_TO_EMPTY.readValue(STRING_15, int.class));
        assertEquals(0, MAPPER_TO_EMPTY.readValue(INT_WRAPPER_NEG225, IntWrapper.class).i);
        assertEquals(0, MAPPER_TO_EMPTY.readValue(INT_ARRAY_25, int[].class)[0]);
        assertEquals(0L, MAPPER_TO_EMPTY.readValue(STRING_12, Long.class));
        assertEquals(0L, MAPPER_TO_EMPTY.readValue(STRING_99, long.class));
        assertEquals(0L, MAPPER_TO_EMPTY.readValue(LONG_WRAPPER_NEG225, LongWrapper.class).l);
        assertEquals(0L, MAPPER_TO_EMPTY.readValue(LONG_ARRAY_26, long[].class)[0]);
        assertEquals((short) 0, MAPPER_TO_EMPTY.readValue(STRING_12, Short.class));
        assertEquals((short) 0, MAPPER_TO_EMPTY.readValue(STRING_999, short.class));
        assertEquals((byte) 0, MAPPER_TO_EMPTY.readValue(STRING_12, Byte.class));
        assertEquals((byte) 0, MAPPER_TO_EMPTY.readValue(STRING_123, byte.class));
        assertEquals(BigInteger.ZERO, MAPPER_TO_EMPTY.readValue(STRING_1234, BigInteger.class));
    }

    void testCoerceConfigStringConvert() throws Exception {
        assertEquals(12, MAPPER_TRY_CONVERT.readValue(STRING_12, Integer.class));
        assertEquals(34, MAPPER_TRY_CONVERT.readValue(STRING_34, int.class));
        assertEquals(-225, MAPPER_TRY_CONVERT.readValue(INT_WRAPPER_NEG225, IntWrapper.class).i);
        assertEquals(2210, MAPPER_TRY_CONVERT.readValue(INT_ARRAY_2210, int[].class)[0]);
        assertEquals(34L, MAPPER_TRY_CONVERT.readValue(STRING_34, Long.class));
        assertEquals(534L, MAPPER_TRY_CONVERT.readValue(STRING_534, long.class));
        assertEquals(-225L, MAPPER_TRY_CONVERT.readValue(LONG_WRAPPER_NEG225, LongWrapper.class).l);
        assertEquals(22L, MAPPER_TRY_CONVERT.readValue(LONG_ARRAY_22, long[].class)[0]);
        assertEquals((short) 12, MAPPER_TRY_CONVERT.readValue(STRING_12, Short.class));
        assertEquals((short) 344, MAPPER_TRY_CONVERT.readValue(STRING_344, short.class));
        assertEquals((byte) 12, MAPPER_TRY_CONVERT.readValue(STRING_12, Byte.class));
        assertEquals((byte) -99, MAPPER_TRY_CONVERT.readValue(STRING_NEG99, byte.class));
        assertEquals(BigInteger.valueOf(1242), MAPPER_TRY_CONVERT.readValue(STRING_1242, BigInteger.class));
    }

    void testCoerceConfigFailFromString() throws Exception {
        verifyFail(MAPPER_TO_FAIL, Integer.class, STRING_15, "java.lang.Integer");
        verifyFail(MAPPER_TO_FAIL, int.class, STRING_15, "int");
        verifyFail(MAPPER_TO_FAIL, IntWrapper.class, INT_WRAPPER_NEG225, "int");
        verifyFail(MAPPER_TO_FAIL, int[].class, INT_ARRAY_256, "element of `int[]`");
        verifyFail(MAPPER_TO_FAIL, Long.class, STRING_738, "java.lang.Long");
        verifyFail(MAPPER_TO_FAIL, long.class, STRING_NEG99, "long");
        verifyFail(MAPPER_TO_FAIL, LongWrapper.class, LONG_WRAPPER_77, "long");
        verifyFail(MAPPER_TO_FAIL, long[].class, LONG_ARRAY_NEG135, "element of `long[]`");
        verifyFail(MAPPER_TO_FAIL, Short.class, STRING_NEG19, "java.lang.Short");
        verifyFail(MAPPER_TO_FAIL, short.class, STRING_25, "short");
        verifyFail(MAPPER_TO_FAIL, short[].class, SHORT_ARRAY_NEG135, "element of `short[]`");
        verifyFail(MAPPER_TO_FAIL, Byte.class, STRING_15, "java.lang.Byte");
        verifyFail(MAPPER_TO_FAIL, byte.class, STRING_NEG25, "byte");
        verifyFail(MAPPER_TO_FAIL, byte[].class, BYTE_ARRAY_NEG1379, "element of `byte[]`");
        verifyFail(MAPPER_TO_FAIL, BigInteger.class, STRING_25236256, "java.math.BigInteger");
    }
private static ObjectMapper integerMapper(CoercionAction action) {
        return VPackMapper.builder().withCoercionConfig(LogicalType.Integer,
                cfg -> cfg.setCoercion(CoercionInputShape.String, action)).build();
    }
private static void verifyFail(ObjectReader reader, Class<?> target, byte[] input,
            String targetDescription) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.forType(target).readValue(input));
        assertEquals(true, failure.getMessage().contains("Cannot coerce String"));
        assertEquals(true, failure.getMessage().contains(targetDescription));
    }
private static void verifyFail(ObjectMapper mapper, Class<?> target, byte[] input,
            String targetDescription) {
        verifyFail(mapper.reader(), target, input, targetDescription);
    }
static class Bean {
        public String a;
    }
static class BeanWithProp3676 {
        @JsonCreator
        public BeanWithProp3676(@JsonProperty("a") String a) {
            this.a = a;
        }

        public String a;
    }
static class IntWrapper {
        public int i;
    }
static class LongWrapper {
        public long l;
    }

    void __invoke_testLegacyStringToIntCoercion() throws Exception {
        try {
            testLegacyStringToIntCoercion();
        } finally {
        }
    }


    void __invoke_testLegacyFailStringToInt() throws Exception {
        try {
            testLegacyFailStringToInt();
        } finally {
        }
    }


    void __invoke_testLegacyFailStringToLong() throws Exception {
        try {
            testLegacyFailStringToLong();
        } finally {
        }
    }


    void __invoke_testLegacyFailStringToOther() throws Exception {
        try {
            testLegacyFailStringToOther();
        } finally {
        }
    }


    void __invoke_testCoerceConfigStringToNull() throws Exception {
        try {
            testCoerceConfigStringToNull();
        } finally {
        }
    }


    void __invoke_testCoerceConfigStringToEmpty() throws Exception {
        try {
            testCoerceConfigStringToEmpty();
        } finally {
        }
    }


    void __invoke_testCoerceConfigStringConvert() throws Exception {
        try {
            testCoerceConfigStringConvert();
        } finally {
        }
    }


    void __invoke_testCoerceConfigFailFromString() throws Exception {
        try {
            testCoerceConfigFailFromString();
        } finally {
        }
    }

}
