package tools.jackson.databind.node;

import java.math.BigDecimal;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.cfg.JsonNodeFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0495F0 {
private static final byte[] HUGE_DECIMAL = VPackWireFixtureTest.hex(
            "c8 08 35 01 00 00 79 76 93 13 48 62 31 57");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] OBJECT_ONE_HUNDRED_EXPONENT = VPackWireFixtureTest.hex(
            "14 0c 41 78 c8 01 02 00 00 00 01 01");
private static final byte[] BIG_INTEGER_OVER_LONG = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff ff");
private static final byte[] DOUBLE_304 = VPackWireFixtureTest.hex(
            "1b 52 b8 1e 85 eb 51 08 40");
private static final byte[] NEGATIVE_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 80");
private static final byte[] DOUBLE_QUARTER = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 3f");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: NumberNodes1770Test#testBigDecimalCoercion().
    void testBigDecimalCoercionVpack() throws Exception {
        JsonNode jsonNode = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build().readTree(HUGE_DECIMAL);

        assertTrue(jsonNode.isBigDecimal(), "Expected DecimalNode, got: "
                + jsonNode.getClass().getName() + ": " + jsonNode);
        assertEquals(new BigDecimal("7976931348623157e309"), jsonNode.decimalValue());
    }

    // Provenance: NumberNodes1770Test#testBigDecimalCoercionInf().
    void testBigDecimalCoercionInfVpack() throws Exception {
        JsonNode jsonNode = mapper.readTree(POSITIVE_INFINITY);

        assertTrue(jsonNode.isDouble(), "Expected DoubleNode, got: "
                + jsonNode.getClass().getName() + ": " + jsonNode);
        assertEquals(Double.POSITIVE_INFINITY, jsonNode.doubleValue());
    }

    // Provenance: NumberNodes1770Test#testBigDecimalCoercionNaN().
    void testBigDecimalCoercionNaNVpack() throws Exception {
        VPackMapper permissive = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build();
        JsonNode n = permissive.readTree(NAN);
        assertTrue(n.isDouble());
        assertEquals(Double.NaN, n.doubleValue());

        VPackMapper strict = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(JsonNodeFeature.FAIL_ON_NAN_TO_BIG_DECIMAL_COERCION)
                .build();
        try {
            strict.readTree(NAN);
            throw new AssertionError("Should not pass without allowing coercion");
        } catch (tools.jackson.databind.exc.InvalidFormatException e) {
            assertTrue(e.getMessage().contains("Cannot convert NaN"));
        }
    }

    void __invoke_testBigDecimalCoercionVpack() throws Exception {
        try {
            testBigDecimalCoercionVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalCoercionInfVpack() throws Exception {
        try {
            testBigDecimalCoercionInfVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalCoercionNaNVpack() throws Exception {
        try {
            testBigDecimalCoercionNaNVpack();
        } finally {
        }
    }

}
