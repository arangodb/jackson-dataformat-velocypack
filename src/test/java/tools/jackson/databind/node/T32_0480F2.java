package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0480F2 {
private static final BigDecimal BD_DEFAULT = new BigDecimal("12.125");
private static final double DOUBLE_DEFAULT = -9999.5d;
private static final byte[] INT_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] MAX_UNSIGNED = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff ff");
private static final byte[] DOUBLE_100000 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 6a f8 40");
private static final byte[] HUGE_BCD = VPackWireFixtureTest.hex(
            "c8 01 35 01 00 00 02");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 00 03 03");
private static final byte[] TEXT_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] TEXT_HALF = VPackWireFixtureTest.hex(
            "43 30 2e 35");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeDoubleValueTest#asDoubleFailFromNumberIntRange().
    void asDoubleFailFromNumberIntRangeVpack() throws Exception {
        BigInteger tooBig = BigInteger.TEN.pow(310);
        BigInteger tooSmall = tooBig.negate();
        assertFailAsDoubleForValueRange(NODES.numberNode(tooBig));
        assertFailAsDoubleForValueRange(NODES.numberNode(tooSmall));
        assertFailAsDoubleForValueRange(MAPPER.readTree(HUGE_BCD));
    }

    // Provenance: JsonNodeDoubleValueTest#asDoubleFromMiscOther().
    void asDoubleFromMiscOtherVpack() throws Exception {
        assertEquals(0.0d, NODES.nullNode().asDouble());
        assertEquals(DOUBLE_DEFAULT, NODES.nullNode().asDouble(DOUBLE_DEFAULT));
        assertFalse(NODES.nullNode().asDoubleOpt().isPresent());
        assertEquals(0.0d, NODES.missingNode().asDouble());
        assertEquals(DOUBLE_DEFAULT, NODES.missingNode().asDouble(DOUBLE_DEFAULT));
        assertFalse(NODES.missingNode().asDoubleOpt().isPresent());

        JsonNode wireNull = MAPPER.readTree(NULL);
        assertEquals(0.0d, wireNull.asDouble());
        assertEquals(DOUBLE_DEFAULT, wireNull.asDouble(DOUBLE_DEFAULT));
        assertFalse(wireNull.asDoubleOpt().isPresent());
    }

    // Provenance: JsonNodeDoubleValueTest#asDoubleFromNonNumberScalar().
    void asDoubleFromNonNumberScalarVpack() throws Exception {
        assertFailAsDoubleForNonNumber(NODES.booleanNode(true));
        assertFailAsDoubleForNonNumber(NODES.binaryNode(new byte[3]));
        assertFailAsDoubleForNonNumber(MAPPER.readTree(BINARY));
        assertFailAsDoubleForNonNumber(NODES.rawValueNode(new RawValue("abc")));
        assertFailAsDoubleForNonNumber(NODES.pojoNode(Boolean.TRUE));
        assertFailAsDoubleForNonNumber(NODES.stringNode("abc"),
                "not a valid String representation of `double`");
        assertFailAsDoubleForNonNumber(NODES.pojoNode(new String[0]));
        assertFailAsDoubleForValueRange(NODES.pojoNode(
                new BigDecimal(BigInteger.TEN.pow(310))));

        assertAsDouble(0.5d, NODES.stringNode("0.5"));
        assertAsDouble(0.5d, MAPPER.readTree(TEXT_HALF));
        assertAsDouble(2.5d, NODES.pojoNode(2.5d));
        assertAsDouble(1e40, NODES.pojoNode(1e40));
    }

    // Provenance: JsonNodeDoubleValueTest#asDoubleFromNumberFPOk().
    void asDoubleFromNumberFPOkVpack() throws Exception {
        assertAsDouble(1.0d, NODES.numberNode(1.0f));
        assertAsDouble(100_000.0d, NODES.numberNode(100_000.0f));
        assertAsDouble(-100_000.0d, NODES.numberNode(-100_000.0f));
        assertAsDouble(1.0d, NODES.numberNode(1.0d));
        assertAsDouble(100_000.0d, NODES.numberNode(100_000.0d));
        assertAsDouble(-100_000.0d, NODES.numberNode(-100_000.0d));
        assertAsDouble(100_000.0d, MAPPER.readTree(DOUBLE_100000));
        assertAsDouble(1.0d, NODES.numberNode(BigDecimal.valueOf(1.0d)));
        assertAsDouble((double) Long.MIN_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Long.MIN_VALUE)));
        assertAsDouble((double) Long.MAX_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Long.MAX_VALUE)));
    }
private static void assertDecimalValue(BigDecimal expected, JsonNode node) {
        assertEquals(expected, node.decimalValue());
        assertEquals(expected, node.decimalValue(BD_DEFAULT));
        assertEquals(expected, node.decimalValueOpt().orElseThrow());
    }
private static void assertFailDecimalValueForNonNumber(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::decimalValue);
        assertTrue(exception.getMessage().contains("decimalValue()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not numeric"));
        assertEquals(BD_DEFAULT, node.decimalValue(BD_DEFAULT));
        assertFalse(node.decimalValueOpt().isPresent());
    }
private static void assertAsDouble(double expected, JsonNode node) {
        assertEquals(expected, node.asDouble());
        assertEquals(expected, node.asDouble(DOUBLE_DEFAULT));
        assertEquals(expected, node.asDoubleOpt().orElseThrow());
    }
private static void assertFailAsDoubleForValueRange(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asDouble);
        assertTrue(exception.getMessage().contains("asDouble()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 64-bit `double` range"));
        assertEquals(-2.25d, node.asDouble(-2.25d));
        assertFalse(node.asDoubleOpt().isPresent());
    }
private static void assertFailAsDoubleForNonNumber(JsonNode node) {
        assertFailAsDoubleForNonNumber(node, "value type not coercible");
    }
private static void assertFailAsDoubleForNonNumber(JsonNode node, String expectedMessage) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asDouble);
        assertTrue(exception.getMessage().contains("asDouble()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains(expectedMessage));
        assertEquals(1.5d, node.asDouble(1.5d));
        assertFalse(node.asDoubleOpt().isPresent());
    }

    void __invoke_asDoubleFailFromNumberIntRangeVpack() throws Exception {
        try {
            asDoubleFailFromNumberIntRangeVpack();
        } finally {
        }
    }


    void __invoke_asDoubleFromMiscOtherVpack() throws Exception {
        try {
            asDoubleFromMiscOtherVpack();
        } finally {
        }
    }


    void __invoke_asDoubleFromNonNumberScalarVpack() throws Exception {
        try {
            asDoubleFromNonNumberScalarVpack();
        } finally {
        }
    }


    void __invoke_asDoubleFromNumberFPOkVpack() throws Exception {
        try {
            asDoubleFromNumberFPOkVpack();
        } finally {
        }
    }

}
