package tools.jackson.databind.convert;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0149F1 {
private static final byte[] INTEGER_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] ONE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] NEG_TWO_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 04 c0");
private static final byte[] THREE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 0c 40");
private static final byte[] TWENTY_TWO_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 80 36 40");
private static final byte[] NINETEEN_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 80 33 40");
private static final byte[] ONE_TWENTY_FOUR_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 20 5f 40");
private static final byte[] NINETY_FIVE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 e0 57 40");
private static final byte[] NEG_TWO_POINT_FIVE_I = VPackWireFixtureTest.hex(
            "14 0e 41 69 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] NEG_TWO_POINT_FIVE_L = VPackWireFixtureTest.hex(
            "14 0e 41 6c 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] ONE_POINT_FIVE_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 00 f8 3f 01");
private static final byte[] NEG_TWO_POINT_FIVE_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] TWENTY_TWO_POINT_FIVE_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 80 36 40 01");
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper MAPPER_TRY_CONVERT = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper MAPPER_TO_NULL = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_TO_FAIL = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.Fail))
            .build();
private static final EnumCoerce ENUM_DEFAULT = EnumCoerce.DEFAULT;

    void testLegacyDoubleToIntCoercion() throws Exception {
        assertEquals(Integer.valueOf(1), DEFAULT_MAPPER.readValue(ONE_POINT_FIVE, Integer.class));
        assertEquals(Integer.valueOf(-2),
                DEFAULT_MAPPER.readValue(NEG_TWO_POINT_FIVE, Integer.TYPE));
        assertEquals(-2, DEFAULT_MAPPER.readValue(NEG_TWO_POINT_FIVE_I, IntWrapper.class).i);
        assertEquals(1, DEFAULT_MAPPER.readValue(ONE_POINT_FIVE_ARRAY, int[].class)[0]);

        assertEquals(Long.valueOf(3), DEFAULT_MAPPER.readValue(THREE_POINT_FIVE, Long.class));
        assertEquals(-2L, DEFAULT_MAPPER.readValue(NEG_TWO_POINT_FIVE_L, LongWrapper.class).l);
        assertEquals(1L, DEFAULT_MAPPER.readValue(ONE_POINT_FIVE_ARRAY, long[].class)[0]);

        assertEquals(Short.valueOf((short) 42), DEFAULT_MAPPER.readValue(
                VPackWireFixtureTest.hex("1b 00 00 00 00 00 40 45 40"), Short.class));
        assertEquals(BigInteger.valueOf(95),
                DEFAULT_MAPPER.readValue(NINETY_FIVE_POINT_FIVE, BigInteger.class));
    }

    void testLegacyDoubleToIntCoercionJsonNodeToBigInteger() throws Exception {
        JsonNodeFactory nodeFactory = DEFAULT_MAPPER.getNodeFactory();
        assertEquals(BigInteger.ONE,
                DEFAULT_MAPPER.treeToValue(nodeFactory.numberNode(1.5), BigInteger.class));
        assertEquals(BigInteger.valueOf(-2),
                DEFAULT_MAPPER.treeToValue(nodeFactory.numberNode(-2.5f), BigInteger.class));
        assertEquals(BigInteger.valueOf(3),
                DEFAULT_MAPPER.treeToValue(nodeFactory.numberNode(java.math.BigDecimal.valueOf(3.75)),
                        BigInteger.class));
    }

    void testCoerceConfigFloatToNull() throws Exception {
        assertNull(MAPPER_TO_NULL.readValue(ONE_POINT_FIVE, Integer.class));
        assertEquals(Integer.valueOf(0), MAPPER_TO_NULL.readValue(ONE_POINT_FIVE, Integer.TYPE));
        assertEquals(0, MAPPER_TO_NULL.readValue(NEG_TWO_POINT_FIVE_I, IntWrapper.class).i);
        assertEquals(0, MAPPER_TO_NULL.readValue(ONE_POINT_FIVE_ARRAY, int[].class)[0]);

        assertNull(MAPPER_TO_NULL.readValue(THREE_POINT_FIVE, Long.class));
        assertEquals(0L, MAPPER_TO_NULL.readValue(NEG_TWO_POINT_FIVE_L, LongWrapper.class).l);
        assertEquals(0L, MAPPER_TO_NULL.readValue(ONE_POINT_FIVE_ARRAY, long[].class)[0]);
        assertNull(MAPPER_TO_NULL.readValue(THREE_POINT_FIVE, Short.class));
        assertEquals(Short.valueOf((short) 0),
                MAPPER_TO_NULL.readValue(THREE_POINT_FIVE, Short.TYPE));
        assertEquals((short) 0,
                MAPPER_TO_NULL.readValue(NEG_TWO_POINT_FIVE_ARRAY, short[].class)[0]);
        assertNull(MAPPER_TO_NULL.readValue(THREE_POINT_FIVE, Byte.class));
        assertEquals(Byte.valueOf((byte) 0),
                MAPPER_TO_NULL.readValue(THREE_POINT_FIVE, Byte.TYPE));
        assertEquals((byte) 0,
                MAPPER_TO_NULL.readValue(ONE_POINT_FIVE_ARRAY, byte[].class)[0]);
        assertNull(MAPPER_TO_NULL.readValue(THREE_POINT_FIVE, BigInteger.class));
        assertNull(MAPPER_TO_NULL.readValue(ONE_POINT_FIVE_ARRAY, BigInteger[].class)[0]);
    }

    void testCoerceConfigFloatToEmpty() throws Exception {
        assertEquals(Integer.valueOf(0), MAPPER_TO_EMPTY.readValue(ONE_POINT_FIVE, Integer.class));
        assertEquals(Integer.valueOf(0), MAPPER_TO_EMPTY.readValue(ONE_POINT_FIVE, Integer.TYPE));
        assertEquals(0, MAPPER_TO_EMPTY.readValue(NEG_TWO_POINT_FIVE_I, IntWrapper.class).i);
        assertEquals(0, MAPPER_TO_EMPTY.readValue(ONE_POINT_FIVE_ARRAY, int[].class)[0]);

        assertEquals(Long.valueOf(0), MAPPER_TO_EMPTY.readValue(THREE_POINT_FIVE, Long.class));
        assertEquals(Long.valueOf(0), MAPPER_TO_EMPTY.readValue(THREE_POINT_FIVE, Long.TYPE));
        assertEquals(0L, MAPPER_TO_EMPTY.readValue(NEG_TWO_POINT_FIVE_L, LongWrapper.class).l);
        assertEquals(0L, MAPPER_TO_EMPTY.readValue(ONE_POINT_FIVE_ARRAY, long[].class)[0]);
        assertEquals(Short.valueOf((short) 0),
                MAPPER_TO_EMPTY.readValue(THREE_POINT_FIVE, Short.class));
        assertEquals(Byte.valueOf((byte) 0),
                MAPPER_TO_EMPTY.readValue(THREE_POINT_FIVE, Byte.class));
        assertEquals(Byte.valueOf((byte) 0),
                MAPPER_TO_EMPTY.readValue(THREE_POINT_FIVE, Byte.TYPE));
        assertEquals(Short.valueOf((short) 0),
                MAPPER_TO_EMPTY.readValue(THREE_POINT_FIVE, Short.TYPE));
        assertEquals(BigInteger.ZERO, MAPPER_TO_EMPTY.readValue(THREE_POINT_FIVE, BigInteger.class));
    }

    void testCoerceConfigFloatSuccess() throws Exception {
        assertEquals(Integer.valueOf(1), MAPPER_TRY_CONVERT.readValue(ONE_POINT_FIVE, Integer.class));
        assertEquals(Integer.valueOf(3), MAPPER_TRY_CONVERT.readValue(THREE_POINT_FIVE, Integer.TYPE));
        assertEquals(-2, MAPPER_TRY_CONVERT.readValue(NEG_TWO_POINT_FIVE_I, IntWrapper.class).i);
        assertEquals(22, MAPPER_TRY_CONVERT.readValue(TWENTY_TWO_POINT_FIVE_ARRAY, int[].class)[0]);

        assertEquals(Long.valueOf(1), MAPPER_TRY_CONVERT.readValue(ONE_POINT_FIVE, Long.class));
        assertEquals(Long.valueOf(1),
                MAPPER_TRY_CONVERT.readValue(ONE_POINT_FIVE, Long.TYPE));
        assertEquals(-2L,
                MAPPER_TRY_CONVERT.readValue(NEG_TWO_POINT_FIVE_L, LongWrapper.class).l);
        assertEquals(2L, MAPPER_TRY_CONVERT.readValue(
                VPackWireFixtureTest.hex("13 0c 1b 00 00 00 00 00 00 04 40 01"), long[].class)[0]);
        assertEquals(Short.valueOf((short) 1),
                MAPPER_TRY_CONVERT.readValue(ONE_POINT_FIVE, Short.class));
        assertEquals(Short.valueOf((short) 19),
                MAPPER_TRY_CONVERT.readValue(NINETEEN_POINT_FIVE, Short.TYPE));
        assertEquals(Byte.valueOf((byte) 1),
                MAPPER_TRY_CONVERT.readValue(ONE_POINT_FIVE, Byte.class));
        assertEquals(Byte.valueOf((byte) 1),
                MAPPER_TRY_CONVERT.readValue(ONE_POINT_FIVE, Byte.TYPE));
        assertEquals(BigInteger.valueOf(124),
                MAPPER_TRY_CONVERT.readValue(ONE_TWENTY_FOUR_POINT_FIVE, BigInteger.class));
    }

    void testCoerceConfigFailFromFloat() {
        assertFloatFails(Integer.class, ONE_POINT_FIVE);
        assertFloatFails(Integer.TYPE, ONE_POINT_FIVE);
        assertFloatFails(IntWrapper.class, NEG_TWO_POINT_FIVE_I);
        assertFloatFails(int[].class, ONE_POINT_FIVE_ARRAY);
        assertFloatFails(Long.class, THREE_POINT_FIVE);
        assertFloatFails(Long.TYPE, THREE_POINT_FIVE);
        assertFloatFails(LongWrapper.class, NEG_TWO_POINT_FIVE_L);
        assertFloatFails(long[].class, ONE_POINT_FIVE_ARRAY);
        assertFloatFails(Short.class, THREE_POINT_FIVE);
        assertFloatFails(Short.TYPE, THREE_POINT_FIVE);
        assertFloatFails(short[].class, ONE_POINT_FIVE_ARRAY);
        assertFloatFails(Byte.class, THREE_POINT_FIVE);
        assertFloatFails(Byte.TYPE, THREE_POINT_FIVE);
        assertFloatFails(byte[].class, ONE_POINT_FIVE_ARRAY);
        assertFloatFails(BigInteger.class, THREE_POINT_FIVE);
    }
private static EnumCoerce readEnum(ObjectMapper mapper, byte[] input) throws Exception {
        return mapper.readValue(input, EnumCoerce.class);
    }
private static ObjectMapper enumMapper(CoercionAction action) {
        return VPackMapper.builder().withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                CoercionInputShape.Integer, action)).build();
    }
private static ObjectMapper enumMapper(LogicalType type, CoercionAction action) {
        return VPackMapper.builder().withCoercionConfig(type, cfg -> cfg.setCoercion(
                CoercionInputShape.Integer, action)).build();
    }
private static ObjectMapper enumMapper(Class<?> type, CoercionAction action) {
        return VPackMapper.builder().withCoercionConfig(type, cfg -> cfg.setCoercion(
                CoercionInputShape.Integer, action)).build();
    }
private static void assertEnumIntegerFails(ObjectMapper mapper) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> readEnum(mapper, INTEGER_ONE));
        assertNotNull(failure.getLocation());
        assertTrue(failure.getMessage().contains("Cannot coerce Integer value"));
        assertTrue(failure.getMessage().contains("but could if coercion was enabled"));
    }
private static void assertFloatFails(Class<?> type, byte[] input) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> MAPPER_TO_FAIL.readValue(input, type));
        assertNotNull(failure.getLocation());
        assertTrue(failure.getMessage().contains("Cannot coerce Floating-point"));
    }
static class IntWrapper {
        public int i;
    }
static class LongWrapper {
        public long l;
    }
enum EnumCoerce {
        A, B, C,

        @JsonEnumDefaultValue
        DEFAULT
    }

    void __invoke_testLegacyDoubleToIntCoercion() throws Exception {
        try {
            testLegacyDoubleToIntCoercion();
        } finally {
        }
    }


    void __invoke_testLegacyDoubleToIntCoercionJsonNodeToBigInteger() throws Exception {
        try {
            testLegacyDoubleToIntCoercionJsonNodeToBigInteger();
        } finally {
        }
    }


    void __invoke_testCoerceConfigFloatToNull() throws Exception {
        try {
            testCoerceConfigFloatToNull();
        } finally {
        }
    }


    void __invoke_testCoerceConfigFloatToEmpty() throws Exception {
        try {
            testCoerceConfigFloatToEmpty();
        } finally {
        }
    }


    void __invoke_testCoerceConfigFloatSuccess() throws Exception {
        try {
            testCoerceConfigFloatSuccess();
        } finally {
        }
    }


    void __invoke_testCoerceConfigFailFromFloat() throws Exception {
        try {
            testCoerceConfigFailFromFloat();
        } finally {
        }
    }

}
