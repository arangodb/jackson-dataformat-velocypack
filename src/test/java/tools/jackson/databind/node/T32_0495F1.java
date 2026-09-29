package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.DoubleNode;
import tools.jackson.databind.node.FloatNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.BigIntegerNode;
import tools.jackson.databind.node.JsonNodeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0495F1 {
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

    // Provenance: NumberNodesTest#testBigDecimalAsPlain().
    void testBigDecimalAsPlainVpack() throws Exception {
        // JSON's WRITE_BIGDECIMAL_AS_PLAIN assertion is textual and therefore
        // has no VPack wire spelling. The corresponding native VPack contract
        // is exact value and scale preservation for the exponent-bearing BCD.
        JsonNode object = mapper.readTree(OBJECT_ONE_HUNDRED_EXPONENT);
        JsonNode node = object.get("x");
        assertEquals(new BigDecimal("1e2"), node.decimalValue());
        assertEquals(-2, node.decimalValue().scale());
        assertEquals(object, mapper.readTree(mapper.writeValueAsBytes(object)));

        JsonNode tree = mapper.valueToTree(new BigDecimal(100));
        assertNotNull(tree);
        assertEquals(new BigDecimal(100), tree.decimalValue());
    }

    // Provenance: NumberNodesTest#testBigIntegerNode().
    void testBigIntegerNodeVpack() throws Exception {
        BigIntegerNode n = BigIntegerNode.valueOf(BigInteger.ONE);
        assertTrue(n.equals(new BigIntegerNode(BigInteger.ONE)));
        assertEquals(JsonToken.VALUE_NUMBER_INT, n.asToken());
        assertEquals(JsonParser.NumberType.BIG_INTEGER, n.numberType());
        assertTrue(n.isNumber());
        assertTrue(n.isIntegralNumber());
        assertTrue(n.isBigInteger());
        assertEquals(BigInteger.ONE, n.numberValue());
        assertEquals(1, n.intValue());
        assertEquals(1L, n.longValue());
        assertEquals(BigInteger.ONE, n.bigIntegerValue());
        assertEquals("1", n.asString());

        JsonNode wire = mapper.readTree(BIG_INTEGER_OVER_LONG);
        BigInteger expected = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
        assertEquals(expected, wire.bigIntegerValue());
        assertTrue(wire.isBigInteger());
        assertEquals(wire, mapper.readTree(mapper.writeValueAsBytes(wire)));
    }

    // Provenance: NumberNodesTest#testCanonicalNumbers().
    void testCanonicalNumbersVpack() throws Exception {
        JsonNodeFactory f = mapper.getNodeFactory();
        JsonNode n = f.numberNode(123);
        assertTrue(n.isInt());
        n = f.numberNode(1L + Integer.MAX_VALUE);
        assertFalse(n.isInt());
        assertTrue(n.isLong());
        n = f.numberNode(123L);
        assertTrue(n.isLong());
    }

    // Provenance: NumberNodesTest#testDecimalNode().
    void testDecimalNodeVpack() throws Exception {
        DecimalNode n = DecimalNode.valueOf(BigDecimal.ONE);
        assertEquals(JsonToken.VALUE_NUMBER_FLOAT, n.asToken());
        assertEquals(JsonParser.NumberType.BIG_DECIMAL, n.numberType());
        assertTrue(n.isNumber());
        assertFalse(n.isIntegralNumber());
        assertTrue(n.isBigDecimal());
        assertEquals(BigDecimal.ONE, n.numberValue());
        assertEquals(1, n.intValue());
        assertEquals(1L, n.longValue());
        assertEquals(BigDecimal.ONE, n.decimalValue());
        assertEquals("1", n.asString());

        BigDecimal value = new BigDecimal("0.1");
        JsonNode result = DecimalNode.valueOf(value);
        assertFalse(result.isObject());
        assertTrue(result.isNumber());
        assertFalse(result.isIntegralNumber());
        assertTrue(result.isBigDecimal());
        assertEquals(value, result.numberValue());
        assertEquals(value.toString(), result.asString());
        assertEquals(result, DecimalNode.valueOf(value));
        assertEquals(value, mapper.readTree(mapper.writeValueAsBytes(result)).decimalValue());
    }

    // Provenance: NumberNodesTest#testDecimalNodeEqualsHashCode().
    void testDecimalNodeEqualsHashCodeVpack() {
        BigDecimal b1 = BigDecimal.ONE;
        BigDecimal b2 = new BigDecimal("1");
        BigDecimal b3 = new BigDecimal("0.01e2");

        DecimalNode node1 = new DecimalNode(b1);
        DecimalNode node2 = new DecimalNode(b2);
        DecimalNode node3 = new DecimalNode(b3);
        assertEquals(node1.hashCode(), node2.hashCode());
        assertEquals(node2.hashCode(), node3.hashCode());
        assertEquals(node1, node2);
        assertEquals(node2, node1);
        assertEquals(node2, node3);
    }

    // Provenance: NumberNodesTest#testDouble().
    void testDoubleVpack() throws Exception {
        DoubleNode n = DoubleNode.valueOf(0.25);
        assertEquals(JsonToken.VALUE_NUMBER_FLOAT, n.asToken());
        assertEquals(JsonParser.NumberType.DOUBLE, n.numberType());
        assertEquals(0.25, n.doubleValue());
        assertNotNull(n.decimalValue());
        assertEquals("0.25", n.asString());
        assertTrue(DoubleNode.valueOf(0).canConvertToInt());
        assertTrue(DoubleNode.valueOf(Integer.MAX_VALUE).canConvertToInt());
        assertTrue(DoubleNode.valueOf(Integer.MIN_VALUE).canConvertToInt());
        assertFalse(DoubleNode.valueOf(1L + Integer.MAX_VALUE).canConvertToInt());
        assertTrue(DoubleNode.valueOf(Long.MAX_VALUE).canConvertToLong());

        JsonNode negativeZero = mapper.readTree(NEGATIVE_ZERO);
        assertTrue(negativeZero.isDouble());
        assertEquals("-0.0", String.valueOf(negativeZero.doubleValue()));
    }

    // Provenance: NumberNodesTest#testDoubleViaMapper().
    void testDoubleViaMapperVpack() throws Exception {
        double value = 3.04;
        JsonNode result = mapper.readTree(DOUBLE_304);
        assertTrue(result.isNumber());
        assertTrue(result.isFloatingPointNumber());
        assertTrue(result.isDouble());
        assertFalse(result.isInt());
        assertFalse(result.isLong());
        assertFalse(result.isIntegralNumber());
        assertFalse(result.isNull());
        assertFalse(result.isString());
        assertFalse(result.isMissingNode());
        assertEquals(value, result.doubleValue());
        assertEquals(value, result.numberValue().doubleValue());
        assertEquals(String.valueOf(value), result.asString());
        assertEquals(result, DoubleNode.valueOf(value));
    }

    // Provenance: NumberNodesTest#testFloat().
    void testFloatVpack() throws Exception {
        FloatNode n = FloatNode.valueOf(0.45f);
        assertEquals(JsonToken.VALUE_NUMBER_FLOAT, n.asToken());
        assertEquals(JsonParser.NumberType.FLOAT, n.numberType());
        assertEquals(0, n.intValue(0));
        assertTrue(n.isFloatingPointNumber());
        assertFalse(n.isIntegralNumber());
        assertFalse(n.canConvertToExactIntegral());
        assertEquals(0.45f, n.floatValue());
        assertEquals("0.45", n.asString());
        assertEquals("0.45", String.valueOf((float) n.doubleValue()));
        assertNotNull(n.decimalValue());
        assertTrue(FloatNode.valueOf(0).canConvertToInt());
        assertTrue(FloatNode.valueOf(Integer.MAX_VALUE).canConvertToInt());
        assertTrue(FloatNode.valueOf(Integer.MIN_VALUE).canConvertToInt());
        assertTrue(FloatNode.valueOf(0L).canConvertToLong());
        assertTrue(FloatNode.valueOf(Integer.MAX_VALUE).canConvertToLong());
        assertTrue(FloatNode.valueOf(Integer.MIN_VALUE).canConvertToLong());

        // VPack's wire FP family is double; the portable JsonNode assertion
        // remains the widened numeric value.
        JsonNode wire = mapper.readTree(DOUBLE_QUARTER);
        assertEquals(0.25d, wire.doubleValue());
    }

    // Provenance: NumberNodesTest#testInt().
    void testIntVpack() throws Exception {
        IntNode n = IntNode.valueOf(1);
        assertEquals(JsonToken.VALUE_NUMBER_INT, n.asToken());
        assertEquals(JsonParser.NumberType.INT, n.numberType());
        assertEquals(1, n.intValue());
        assertEquals(1L, n.longValue());
        assertEquals(BigDecimal.ONE, n.decimalValue());
        assertEquals(BigInteger.ONE, n.bigIntegerValue());
        assertEquals("1", n.asString());
        assertTrue(n.canConvertToInt());
        assertTrue(n.canConvertToLong());
        assertTrue(n.canConvertToExactIntegral());
        assertEquals(n, mapper.readTree(VPackWireFixtureTest.hex("31")));
    }

    void __invoke_testBigDecimalAsPlainVpack() throws Exception {
        try {
            testBigDecimalAsPlainVpack();
        } finally {
        }
    }


    void __invoke_testBigIntegerNodeVpack() throws Exception {
        try {
            testBigIntegerNodeVpack();
        } finally {
        }
    }


    void __invoke_testCanonicalNumbersVpack() throws Exception {
        try {
            testCanonicalNumbersVpack();
        } finally {
        }
    }


    void __invoke_testDecimalNodeVpack() throws Exception {
        try {
            testDecimalNodeVpack();
        } finally {
        }
    }


    void __invoke_testDecimalNodeEqualsHashCodeVpack() throws Exception {
        try {
            testDecimalNodeEqualsHashCodeVpack();
        } finally {
        }
    }


    void __invoke_testDoubleVpack() throws Exception {
        try {
            testDoubleVpack();
        } finally {
        }
    }


    void __invoke_testDoubleViaMapperVpack() throws Exception {
        try {
            testDoubleViaMapperVpack();
        } finally {
        }
    }


    void __invoke_testFloatVpack() throws Exception {
        try {
            testFloatVpack();
        } finally {
        }
    }


    void __invoke_testIntVpack() throws Exception {
        try {
            testIntVpack();
        } finally {
        }
    }

}
