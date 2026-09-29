package tools.jackson.databind.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0150F0 {
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

    void testLegacyDoubleToIntCoercionJsonNodeToInteger() throws Exception {
        JsonNodeFactory nodeFactory = INTEGER_DEFAULT_MAPPER.getNodeFactory();
        assertEquals(1, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(1.25), Integer.class));
        assertEquals(-2, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(-2.5f), Integer.class));
        assertEquals(3, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(BigDecimal.valueOf(3.75)), Integer.class));

        assertEquals(1, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(1.25), Integer.TYPE));
        assertEquals(-2, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(-2.5f), Integer.TYPE));
        assertEquals(3, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(BigDecimal.valueOf(3.75)), Integer.TYPE));
    }

    void testLegacyDoubleToIntCoercionJsonNodeToLong() throws Exception {
        JsonNodeFactory nodeFactory = INTEGER_DEFAULT_MAPPER.getNodeFactory();
        assertEquals(1L, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(1.25), Long.class));
        assertEquals(-2L, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(-2.5f), Long.class));
        assertEquals(3L, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(BigDecimal.valueOf(3.75)), Long.class));

        assertEquals(1L, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(1.25), Long.TYPE));
        assertEquals(-2L, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(-2.5f), Long.TYPE));
        assertEquals(3L, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(BigDecimal.valueOf(3.75)), Long.TYPE));
    }

    void testLegacyFPToIntCoercionJsonNodeToByte() throws Exception {
        JsonNodeFactory nodeFactory = INTEGER_DEFAULT_MAPPER.getNodeFactory();
        assertEquals((byte) 1, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(1.25), Byte.class));
        assertEquals((byte) -2, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(-2.5f), Byte.class));
        assertEquals((byte) 3, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(BigDecimal.valueOf(3.75)), Byte.class));

        assertEquals((byte) 1, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(1.25), Byte.TYPE));
        assertEquals((byte) -2, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(-2.5f), Byte.TYPE));
        assertEquals((byte) 3, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(BigDecimal.valueOf(3.75)), Byte.TYPE));
    }

    void testLegacyFPToIntCoercionJsonNodeToShort() throws Exception {
        JsonNodeFactory nodeFactory = INTEGER_DEFAULT_MAPPER.getNodeFactory();
        assertEquals((short) 1, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(1.25), Short.class));
        assertEquals((short) -2, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(-2.5f), Short.class));
        assertEquals((short) 3, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(BigDecimal.valueOf(3.75)), Short.class));

        assertEquals((short) 1, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(1.25), Short.TYPE));
        assertEquals((short) -2, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(-2.5f), Short.TYPE));
        assertEquals((short) 3, INTEGER_DEFAULT_MAPPER.treeToValue(
                nodeFactory.numberNode(BigDecimal.valueOf(3.75)), Short.TYPE));
    }

    void testLegacyFail2804() throws Exception {
        assertLegacyFail(Integer.class, FIVE_POINT_FIVE, "5.5");
        assertLegacyFail(Long.class, FIVE_POINT_ZERO, "5.0");
        assertLegacyFail(BigInteger.class, LARGE_POINT_ZERO, null);
        assertLegacyFail(new TypeReference<List<Integer>>() { }, FIVE_POINT_FIVE_LIST, "5.5");
        assertLegacyFail(new TypeReference<Map<String, Integer>>() { }, FIVE_POINT_FIVE_MAP, "5.5");
    }

    void testLegacyFailDoubleToInt() throws Exception {
        assertLegacyFail(Integer.class, ONE_POINT_FIVE, "java.lang.Integer");
        assertLegacyFail(Integer.TYPE, ONE_POINT_FIVE, "int");
        assertLegacyFail(IntWrapper.class, NEG_TWO_POINT_FIVE_I, "int");
        assertLegacyFail(int[].class, ONE_POINT_FIVE_ARRAY, "to `int` value");
    }

    void testLegacyFailDoubleToLong() throws Exception {
        assertLegacyFail(Long.class, ZERO_POINT_FIVE, "java.lang.Long");
        assertLegacyFail(Long.TYPE, NEG_TWO_POINT_FIVE, "long");
        assertLegacyFail(LongWrapper.class, NEG_TWO_POINT_FIVE_L, "long");
        assertLegacyFail(long[].class, NEG_TWO_POINT_FIVE_ARRAY, "to `long` value");
    }

    void testLegacyFailDoubleToOther() throws Exception {
        assertLegacyFail(Byte.class, ZERO_POINT_FIVE, "java.lang.Byte");
        assertLegacyFail(Byte.TYPE, NEG_TWO_POINT_FIVE, "byte");
        assertLegacyFail(byte[].class, NEG_TWO_POINT_FIVE_ARRAY, "to `byte` value");
        assertLegacyFail(Short.class, ZERO_POINT_FIVE, "java.lang.Short");
        assertLegacyFail(Short.TYPE, NEG_TWO_POINT_FIVE, "short");
        assertLegacyFail(short[].class, NEG_TWO_POINT_FIVE_ARRAY, "to `short` value");
        assertLegacyFail(BigInteger.class, ONE_POINT_FIVE, "java.math.BigInteger");
        assertLegacyFail(AtomicLong.class, ONE_POINT_FIVE, "java.util.concurrent.atomic.AtomicLong");
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

    void __invoke_testLegacyDoubleToIntCoercionJsonNodeToInteger() throws Exception {
        try {
            testLegacyDoubleToIntCoercionJsonNodeToInteger();
        } finally {
        }
    }


    void __invoke_testLegacyDoubleToIntCoercionJsonNodeToLong() throws Exception {
        try {
            testLegacyDoubleToIntCoercionJsonNodeToLong();
        } finally {
        }
    }


    void __invoke_testLegacyFPToIntCoercionJsonNodeToByte() throws Exception {
        try {
            testLegacyFPToIntCoercionJsonNodeToByte();
        } finally {
        }
    }


    void __invoke_testLegacyFPToIntCoercionJsonNodeToShort() throws Exception {
        try {
            testLegacyFPToIntCoercionJsonNodeToShort();
        } finally {
        }
    }


    void __invoke_testLegacyFail2804() throws Exception {
        try {
            testLegacyFail2804();
        } finally {
        }
    }


    void __invoke_testLegacyFailDoubleToInt() throws Exception {
        try {
            testLegacyFailDoubleToInt();
        } finally {
        }
    }


    void __invoke_testLegacyFailDoubleToLong() throws Exception {
        try {
            testLegacyFailDoubleToLong();
        } finally {
        }
    }


    void __invoke_testLegacyFailDoubleToOther() throws Exception {
        try {
            testLegacyFailDoubleToOther();
        } finally {
        }
    }

}
