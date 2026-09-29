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

class T32_0480F0 {
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

    // Provenance: JsonNodeDecimalValueTest#decimalValueFromNumberIntOk().
    void decimalValueFromNumberIntOkVpack() throws Exception {
        assertDecimalValue(BigDecimal.ONE, NODES.numberNode((byte) 1));
        assertDecimalValue(BigDecimal.valueOf(Byte.MIN_VALUE), NODES.numberNode(Byte.MIN_VALUE));
        assertDecimalValue(BigDecimal.valueOf(Byte.MAX_VALUE), NODES.numberNode(Byte.MAX_VALUE));
        assertDecimalValue(BigDecimal.ONE, NODES.numberNode((short) 1));
        assertDecimalValue(BigDecimal.valueOf(Short.MIN_VALUE), NODES.numberNode(Short.MIN_VALUE));
        assertDecimalValue(BigDecimal.valueOf(Short.MAX_VALUE), NODES.numberNode(Short.MAX_VALUE));
        assertDecimalValue(BigDecimal.ONE, NODES.numberNode(1));
        assertDecimalValue(BigDecimal.valueOf(Integer.MIN_VALUE), NODES.numberNode(Integer.MIN_VALUE));
        assertDecimalValue(BigDecimal.valueOf(Integer.MAX_VALUE), NODES.numberNode(Integer.MAX_VALUE));
        assertDecimalValue(BigDecimal.ONE, NODES.numberNode(1L));
        assertDecimalValue(BigDecimal.valueOf(Long.MIN_VALUE), NODES.numberNode(Long.MIN_VALUE));
        assertDecimalValue(BigDecimal.valueOf(Long.MAX_VALUE), NODES.numberNode(Long.MAX_VALUE));
        assertDecimalValue(BigDecimal.ONE, NODES.numberNode(BigInteger.ONE));
        assertDecimalValue(BigDecimal.valueOf(Long.MIN_VALUE),
                NODES.numberNode(BigInteger.valueOf(Long.MIN_VALUE)));
        assertDecimalValue(BigDecimal.valueOf(Long.MAX_VALUE),
                NODES.numberNode(BigInteger.valueOf(Long.MAX_VALUE)));

        assertDecimalValue(BigDecimal.ONE, MAPPER.readTree(INT_ONE));
        assertDecimalValue(new BigDecimal("18446744073709551615"),
                MAPPER.readTree(MAX_UNSIGNED));
    }

    // Provenance: JsonNodeDecimalValueTest#decimalValueFromNumberFPOk().
    void decimalValueFromNumberFPOkVpack() throws Exception {
        assertDecimalValue(new BigDecimal("1.0"), NODES.numberNode(1.0f));
        assertDecimalValue(new BigDecimal("1.0"), NODES.numberNode(1.0f));
        assertDecimalValue(new BigDecimal("100000.0"), NODES.numberNode(100_000.0f));
        assertDecimalValue(new BigDecimal("-100000.0"), NODES.numberNode(-100_000.0f));
        assertDecimalValue(new BigDecimal("1.0"), NODES.numberNode(1.0d));
        assertDecimalValue(new BigDecimal("100000.0"), NODES.numberNode(100_000.0d));
        assertDecimalValue(new BigDecimal("-100000.0"), NODES.numberNode(-100_000.0d));
        assertDecimalValue(new BigDecimal("100000.0"), MAPPER.readTree(DOUBLE_100000));
        assertDecimalValue(new BigDecimal("100.001"),
                NODES.numberNode(new BigDecimal("100.001")));
    }

    // Provenance: JsonNodeDecimalValueTest#failBigDecimalFromNonNumberScalar().
    void failBigDecimalFromNonNumberScalarVpack() throws Exception {
        assertFailDecimalValueForNonNumber(NODES.booleanNode(true));
        assertFailDecimalValueForNonNumber(NODES.binaryNode(new byte[3]));
        assertFailDecimalValueForNonNumber(MAPPER.readTree(BINARY));
        assertFailDecimalValueForNonNumber(NODES.stringNode("123"));
        assertFailDecimalValueForNonNumber(MAPPER.readTree(TEXT_123));
        assertFailDecimalValueForNonNumber(NODES.rawValueNode(new RawValue("abc")));
        assertFailDecimalValueForNonNumber(NODES.pojoNode(Boolean.TRUE));
    }

    // Provenance: JsonNodeDecimalValueTest#failBigDecimalValueFromMiscOther().
    void failBigDecimalValueFromMiscOtherVpack() throws Exception {
        assertFailDecimalValueForNonNumber(NODES.nullNode());
        assertFailDecimalValueForNonNumber(NODES.missingNode());
        assertFailDecimalValueForNonNumber(MAPPER.readTree(NULL));
    }

    // Provenance: JsonNodeDecimalValueTest#failBigDecimalValueFromStructural().
    void failBigDecimalValueFromStructuralVpack() throws Exception {
        assertFailDecimalValueForNonNumber(NODES.arrayNode(3));
        assertFailDecimalValueForNonNumber(NODES.objectNode());
        assertFailDecimalValueForNonNumber(MAPPER.readTree(EMPTY_ARRAY));
        assertFailDecimalValueForNonNumber(MAPPER.readTree(EMPTY_OBJECT));
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

    void __invoke_decimalValueFromNumberIntOkVpack() throws Exception {
        try {
            decimalValueFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_decimalValueFromNumberFPOkVpack() throws Exception {
        try {
            decimalValueFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_failBigDecimalFromNonNumberScalarVpack() throws Exception {
        try {
            failBigDecimalFromNonNumberScalarVpack();
        } finally {
        }
    }


    void __invoke_failBigDecimalValueFromMiscOtherVpack() throws Exception {
        try {
            failBigDecimalValueFromMiscOtherVpack();
        } finally {
        }
    }


    void __invoke_failBigDecimalValueFromStructuralVpack() throws Exception {
        try {
            failBigDecimalValueFromStructuralVpack();
        } finally {
        }
    }

}
