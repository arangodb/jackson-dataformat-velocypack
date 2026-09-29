package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0481F0 {
private static final double DOUBLE_DEFAULT = -9999.5d;
private static final byte[] HUGE_BCD_POSITIVE = VPackWireFixtureTest.hex(
            "c8 01 35 01 00 00 01");
private static final byte[] HUGE_BCD_NEGATIVE = VPackWireFixtureTest.hex(
            "d0 01 35 01 00 00 01");
private static final byte[] UNSIGNED_128 = VPackWireFixtureTest.hex("28 80");
private static final byte[] SIGNED_NEGATIVE_128 = VPackWireFixtureTest.hex("20 80");
private static final byte[] DOUBLE_1_5 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] BINARY_ONE_BYTE = VPackWireFixtureTest.hex("c0 01 07");
private static final byte[] TEXT_X = VPackWireFixtureTest.hex("41 78");
private static final byte[] TEXT_123 = VPackWireFixtureTest.hex("43 31 32 33");
private static final byte[] ARRAY_ONE = VPackWireFixtureTest.hex("13 04 31 01");
private static final byte[] ARRAY_TWO = VPackWireFixtureTest.hex("13 04 32 01");
private static final byte[] OBJECT_A_ONE = VPackWireFixtureTest.hex(
            "14 06 41 61 31 01");
private static final byte[] OBJECT_B_TWO = VPackWireFixtureTest.hex(
            "14 06 41 62 32 01");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] DECIMAL_12_5000 = VPackWireFixtureTest.hex(
            "c8 03 fc ff ff ff 12 50 00");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeDoubleValueTest#asDoubleFromNumberFPRangeFail().
    void asDoubleFromNumberFPRangeFailVpack() throws Exception {
        BigDecimal tooBig = new BigDecimal(BigInteger.TEN.pow(310))
                .add(BigDecimal.valueOf(0.125));
        assertFailAsDoubleForValueRange(NODES.numberNode(tooBig));
        assertFailAsDoubleForValueRange(NODES.numberNode(tooBig.negate()));
        assertFailAsDoubleForValueRange(MAPPER.readTree(HUGE_BCD_POSITIVE));
        assertFailAsDoubleForValueRange(MAPPER.readTree(HUGE_BCD_NEGATIVE));
    }

    // Provenance: JsonNodeDoubleValueTest#asDoubleFromNumberIntOk().
    void asDoubleFromNumberIntOkVpack() throws Exception {
        final double ONE_D = 1.0d;

        assertAsDouble(ONE_D, NODES.numberNode((byte) 1));
        assertAsDouble((double) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertAsDouble((double) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertAsDouble(ONE_D, NODES.numberNode((short) 1));
        assertAsDouble((double) Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertAsDouble((double) Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertAsDouble(ONE_D, NODES.numberNode(1));
        assertAsDouble((double) Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertAsDouble((double) Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));
        assertAsDouble(ONE_D, NODES.numberNode(1L));
        assertAsDouble((double) Long.MIN_VALUE, NODES.numberNode(Long.MIN_VALUE));
        assertAsDouble((double) Long.MAX_VALUE, NODES.numberNode(Long.MAX_VALUE));
        assertAsDouble(ONE_D, NODES.numberNode(BigInteger.ONE));
        assertAsDouble((double) Long.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MIN_VALUE)));
        assertAsDouble((double) Long.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MAX_VALUE)));

        assertAsDouble(128.0d, MAPPER.readTree(UNSIGNED_128));
    }

    // Provenance: JsonNodeDoubleValueTest#asDoubleFromStructuralFail().
    void asDoubleFromStructuralFailVpack() throws Exception {
        assertFailAsDoubleForNonNumber(NODES.arrayNode(3));
        assertFailAsDoubleForNonNumber(NODES.objectNode());
        assertFailAsDoubleForNonNumber(MAPPER.readTree(ARRAY_ONE));
        assertFailAsDoubleForNonNumber(MAPPER.readTree(OBJECT_A_ONE));
    }

    // Provenance: JsonNodeDoubleValueTest#doubleValueFromNumberFPOk().
    void doubleValueFromNumberFPOkVpack() throws Exception {
        assertDoubleValue(1.0d, NODES.numberNode(1.0f));
        assertDoubleValue(100_000.0d, NODES.numberNode(100_000.0f));
        assertDoubleValue(-100_000.0d, NODES.numberNode(-100_000.0f));
        assertDoubleValue(1.0d, NODES.numberNode(1.0d));
        assertDoubleValue(100_000.0d, NODES.numberNode(100_000.0d));
        assertDoubleValue(-100_000.0d, NODES.numberNode(-100_000.0d));
        assertDoubleValue(1.0d, NODES.numberNode(BigDecimal.valueOf(1.0d)));
        assertDoubleValue((double) Long.MIN_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Long.MIN_VALUE)));
        assertDoubleValue((double) Long.MAX_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Long.MAX_VALUE)));

        assertDoubleValue(1.5d, MAPPER.readTree(DOUBLE_1_5));
    }

    // Provenance: JsonNodeDoubleValueTest#doubleValueFromNumberIntOk().
    void doubleValueFromNumberIntOkVpack() throws Exception {
        final double ONE_D = 1.0d;

        assertDoubleValue(ONE_D, NODES.numberNode((byte) 1));
        assertDoubleValue((double) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertDoubleValue((double) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertDoubleValue(ONE_D, NODES.numberNode((short) 1));
        assertDoubleValue((double) Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertDoubleValue((double) Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertDoubleValue(ONE_D, NODES.numberNode(1));
        assertDoubleValue((double) Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertDoubleValue((double) Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));
        assertDoubleValue(ONE_D, NODES.numberNode(1L));
        assertDoubleValue((double) Long.MIN_VALUE, NODES.numberNode(Long.MIN_VALUE));
        assertDoubleValue((double) Long.MAX_VALUE, NODES.numberNode(Long.MAX_VALUE));
        assertDoubleValue(ONE_D, NODES.numberNode(BigInteger.ONE));
        assertDoubleValue((double) Long.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MIN_VALUE)));
        assertDoubleValue((double) Long.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MAX_VALUE)));

        assertDoubleValue(-128.0d, MAPPER.readTree(SIGNED_NEGATIVE_128));
    }

    // Provenance: JsonNodeDoubleValueTest#failDoubleValueFromMiscOther().
    void failDoubleValueFromMiscOtherVpack() throws Exception {
        assertFailDoubleValueForNonNumber(NODES.nullNode());
        assertFailDoubleValueForNonNumber(NODES.missingNode());
        assertFailDoubleValueForNonNumber(MAPPER.readTree(NULL));
    }

    // Provenance: JsonNodeDoubleValueTest#failDoubleValueFromNonNumberScalar().
    void failDoubleValueFromNonNumberScalarVpack() throws Exception {
        assertFailDoubleValueForNonNumber(NODES.booleanNode(true));
        assertFailDoubleValueForNonNumber(NODES.binaryNode(new byte[3]));
        assertFailDoubleValueForNonNumber(NODES.stringNode("123"));
        assertFailDoubleValueForNonNumber(NODES.rawValueNode(new RawValue("abc")));
        assertFailDoubleValueForNonNumber(NODES.pojoNode(Boolean.TRUE));
        assertFailDoubleValueForNonNumber(NODES.pojoNode(3.8d));
        assertFailDoubleValueForNonNumber(MAPPER.readTree(BINARY_ONE_BYTE));
        assertFailDoubleValueForNonNumber(MAPPER.readTree(TEXT_123));
    }

    // Provenance: JsonNodeDoubleValueTest#failDoubleValueFromNumberFPRange().
    void failDoubleValueFromNumberFPRangeVpack() throws Exception {
        BigDecimal tooBig = new BigDecimal(BigInteger.TEN.pow(310))
                .add(BigDecimal.valueOf(0.125));
        assertFailDoubleValueForValueRange(NODES.numberNode(tooBig));
        assertFailDoubleValueForValueRange(NODES.numberNode(tooBig.negate()));
        assertFailDoubleValueForValueRange(MAPPER.readTree(HUGE_BCD_POSITIVE));
        assertFailDoubleValueForValueRange(MAPPER.readTree(HUGE_BCD_NEGATIVE));
    }

    // Provenance: JsonNodeDoubleValueTest#failDoubleValueFromNumberIntRange().
    void failDoubleValueFromNumberIntRangeVpack() {
        BigInteger tooBig = BigInteger.TEN.pow(310);
        assertFailDoubleValueForValueRange(NODES.numberNode(tooBig));
        assertFailDoubleValueForValueRange(NODES.numberNode(tooBig.negate()));
    }

    // Provenance: JsonNodeDoubleValueTest#failDoubleValueFromStructural().
    void failDoubleValueFromStructuralVpack() throws Exception {
        assertFailDoubleValueForNonNumber(NODES.arrayNode(3));
        assertFailDoubleValueForNonNumber(NODES.objectNode());
        assertFailDoubleValueForNonNumber(MAPPER.readTree(ARRAY_TWO));
        assertFailDoubleValueForNonNumber(MAPPER.readTree(OBJECT_B_TWO));
    }
private static void assertDoubleValue(double expected, JsonNode node) {
        assertEquals(expected, node.doubleValue());
        assertEquals(expected, node.doubleValue(DOUBLE_DEFAULT));
        assertEquals(expected, node.doubleValueOpt().orElseThrow());
    }
private static void assertFailDoubleValueForValueRange(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::doubleValue);
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 64-bit `double` range"));
        assertEquals(-2.25d, node.doubleValue(-2.25d));
        assertFalse(node.doubleValueOpt().isPresent());
    }
private static void assertFailDoubleValueForNonNumber(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::doubleValue);
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not numeric"));
        assertEquals(1.5d, node.doubleValue(1.5d));
        assertFalse(node.doubleValueOpt().isPresent());
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
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asDouble);
        assertTrue(exception.getMessage().contains("asDouble()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not coercible"));
        assertEquals(1.5d, node.asDouble(1.5d));
        assertFalse(node.asDoubleOpt().isPresent());
    }

    void __invoke_asDoubleFromNumberFPRangeFailVpack() throws Exception {
        try {
            asDoubleFromNumberFPRangeFailVpack();
        } finally {
        }
    }


    void __invoke_asDoubleFromNumberIntOkVpack() throws Exception {
        try {
            asDoubleFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_asDoubleFromStructuralFailVpack() throws Exception {
        try {
            asDoubleFromStructuralFailVpack();
        } finally {
        }
    }


    void __invoke_doubleValueFromNumberFPOkVpack() throws Exception {
        try {
            doubleValueFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_doubleValueFromNumberIntOkVpack() throws Exception {
        try {
            doubleValueFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_failDoubleValueFromMiscOtherVpack() throws Exception {
        try {
            failDoubleValueFromMiscOtherVpack();
        } finally {
        }
    }


    void __invoke_failDoubleValueFromNonNumberScalarVpack() throws Exception {
        try {
            failDoubleValueFromNonNumberScalarVpack();
        } finally {
        }
    }


    void __invoke_failDoubleValueFromNumberFPRangeVpack() throws Exception {
        try {
            failDoubleValueFromNumberFPRangeVpack();
        } finally {
        }
    }


    void __invoke_failDoubleValueFromNumberIntRangeVpack() throws Exception {
        try {
            failDoubleValueFromNumberIntRangeVpack();
        } finally {
        }
    }


    void __invoke_failDoubleValueFromStructuralVpack() throws Exception {
        try {
            failDoubleValueFromStructuralVpack();
        } finally {
        }
    }

}
