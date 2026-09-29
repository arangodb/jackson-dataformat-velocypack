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

class T32_0484Fixture {
private static final byte[] BYTE_MIN = VPackWireFixtureTest.hex("20 80");
private static final byte[] SHORT_MAX = VPackWireFixtureTest.hex("21 ff 7f");
private static final byte[] INT_MIN = VPackWireFixtureTest.hex("23 00 00 00 80");
private static final byte[] INT_MAX = VPackWireFixtureTest.hex("23 ff ff ff 7f");
private static final byte[] DOUBLE_ONE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 3f");
private static final byte[] DOUBLE_100_5 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 20 59 40");
private static final byte[] DOUBLE_INT_OVERFLOW = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 e0 41");
private static final byte[] BCD_ONE = VPackWireFixtureTest.hex(
            "c8 01 00 00 00 00 01");
private static final byte[] BCD_100_5 = VPackWireFixtureTest.hex(
            "c8 02 ff ff ff ff 10 05");
private static final byte[] BCD_NEGATIVE_1_25 = VPackWireFixtureTest.hex(
            "d0 02 fe ff ff ff 01 25");
private static final byte[] BCD_INT_OVERFLOW = VPackWireFixtureTest.hex(
            "c8 05 00 00 00 00 21 47 48 36 48");
private static final byte[] BCD_INT_UNDERFLOW = VPackWireFixtureTest.hex(
            "d0 05 00 00 00 00 21 47 48 36 49");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 01 02 03");
private static final byte[] TEXT_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] ARRAY = VPackWireFixtureTest.hex(
            "13 05 31 32 02");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeIntValueTest#intValueFromNumberFPOk().
    void intValueFromNumberFPOkVpack() throws Exception {
        assertIntValue(1, NODES.numberNode(1.0f));
        assertIntValue(100_000, NODES.numberNode(100_000.0f));
        assertIntValue(-100_000, NODES.numberNode(-100_000.0f));
        assertIntValue(1, NODES.numberNode(1.0d));
        assertIntValue(100_000, NODES.numberNode(100_000.0d));
        assertIntValue(-100_000, NODES.numberNode(-100_000.0d));
        assertIntValue(Integer.MIN_VALUE, NODES.numberNode((double) Integer.MIN_VALUE));
        assertIntValue(Integer.MAX_VALUE, NODES.numberNode((double) Integer.MAX_VALUE));
        assertIntValue(1, NODES.numberNode(BigDecimal.valueOf(1.0d)));
        assertIntValue(Integer.MIN_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Integer.MIN_VALUE)));
        assertIntValue(Integer.MAX_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Integer.MAX_VALUE)));

        assertIntValue(1, MAPPER.readTree(DOUBLE_ONE));
        assertIntValue(1, MAPPER.readTree(BCD_ONE));
    }

    // Provenance: JsonNodeIntValueTest#intValueFromNumberFPFailRange().
    void intValueFromNumberFPFailRangeVpack() throws Exception {
        final long underflow = Integer.MIN_VALUE - 1L;
        final long overflow = Integer.MAX_VALUE + 1L;

        assertIntValueRangeFailure(NODES.numberNode((double) underflow));
        assertIntValueRangeFailure(NODES.numberNode((double) overflow));
        assertIntValueRangeFailure(NODES.numberNode(-Float.MAX_VALUE));
        assertIntValueRangeFailure(NODES.numberNode(Float.MAX_VALUE));
        assertIntValueRangeFailure(NODES.numberNode(BigDecimal.valueOf(underflow)));
        assertIntValueRangeFailure(NODES.numberNode(BigDecimal.valueOf(overflow)));

        assertIntValueRangeFailure(MAPPER.readTree(DOUBLE_INT_OVERFLOW));
        assertIntValueRangeFailure(MAPPER.readTree(BCD_INT_OVERFLOW));
    }

    // Provenance: JsonNodeIntValueTest#intValueFromNumberFPFailFraction().
    void intValueFromNumberFPFailFractionVpack() throws Exception {
        assertIntValueFractionFailure(NODES.numberNode(100.5f));
        assertIntValueFractionFailure(NODES.numberNode(-0.25f));
        assertIntValueFractionFailure(NODES.numberNode(100.5d));
        assertIntValueFractionFailure(NODES.numberNode(-0.25d));
        assertIntValueFractionFailure(NODES.numberNode(BigDecimal.valueOf(100.5d)));
        assertIntValueFractionFailure(NODES.numberNode(BigDecimal.valueOf(-0.25d)));

        assertIntValueFractionFailure(MAPPER.readTree(DOUBLE_100_5));
        assertIntValueFractionFailure(MAPPER.readTree(BCD_100_5));
    }

    // Provenance: JsonNodeIntValueTest#intValueFromNumberFPFailNaN().
    void intValueFromNumberFPFailNaNVpack() throws Exception {
        assertIntValueNaNFailure(NODES.numberNode(Float.NaN));
        assertIntValueNaNFailure(NODES.numberNode(Float.NEGATIVE_INFINITY));
        assertIntValueNaNFailure(NODES.numberNode(Float.POSITIVE_INFINITY));
        assertIntValueNaNFailure(NODES.numberNode(Double.NaN));
        assertIntValueNaNFailure(NODES.numberNode(Double.NEGATIVE_INFINITY));
        assertIntValueNaNFailure(NODES.numberNode(Double.POSITIVE_INFINITY));
        assertIntValueNaNFailure(MAPPER.readTree(NAN));
        assertIntValueNaNFailure(MAPPER.readTree(NEGATIVE_INFINITY));
        assertIntValueNaNFailure(MAPPER.readTree(POSITIVE_INFINITY));
    }

    // Provenance: JsonNodeIntValueTest#intValueFromNonNumberScalarFail().
    void intValueFromNonNumberScalarFailVpack() throws Exception {
        assertIntValueNonNumberFailure(NODES.booleanNode(true));
        assertIntValueNonNumberFailure(NODES.binaryNode(new byte[3]));
        assertIntValueNonNumberFailure(MAPPER.readTree(BINARY));
        assertIntValueNonNumberFailure(NODES.stringNode("123"));
        assertIntValueNonNumberFailure(MAPPER.readTree(TEXT_123));
        assertIntValueNonNumberFailure(NODES.rawValueNode(new RawValue("abc")));
        assertIntValueNonNumberFailure(NODES.pojoNode(Boolean.TRUE));
        assertIntValueNonNumberFailure(NODES.pojoNode(456));
    }

    // Provenance: JsonNodeIntValueTest#intValueFromMiscOtherFail().
    void intValueFromMiscOtherFailVpack() throws Exception {
        assertIntValueNonNumberFailure(NODES.nullNode());
        assertIntValueNonNumberFailure(NODES.missingNode());
        assertIntValueNonNumberFailure(MAPPER.readTree(NULL));
    }

    // Provenance: JsonNodeIntValueTest#asIntFromNumberIntOk().
    void asIntFromNumberIntOkVpack() throws Exception {
        assertAsInt(1, NODES.numberNode(1));
        assertAsInt(Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertAsInt(Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));
        assertAsInt(1, NODES.numberNode((byte) 1));
        assertAsInt((int) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertAsInt((int) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertAsInt(1, NODES.numberNode((short) 1));
        assertAsInt((int) Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertAsInt((int) Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertAsInt(1, NODES.numberNode(1L));
        assertAsInt(Integer.MIN_VALUE, NODES.numberNode((long) Integer.MIN_VALUE));
        assertAsInt(Integer.MAX_VALUE, NODES.numberNode((long) Integer.MAX_VALUE));
        assertAsInt(1, NODES.numberNode(BigInteger.ONE));
        assertAsInt(Integer.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Integer.MIN_VALUE)));
        assertAsInt(Integer.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Integer.MAX_VALUE)));

        assertAsInt((int) Byte.MIN_VALUE, MAPPER.readTree(BYTE_MIN));
        assertAsInt((int) Short.MAX_VALUE, MAPPER.readTree(SHORT_MAX));
        assertAsInt(Integer.MIN_VALUE, MAPPER.readTree(INT_MIN));
        assertAsInt(Integer.MAX_VALUE, MAPPER.readTree(INT_MAX));
    }

    // Provenance: JsonNodeIntValueTest#asIntFromNumberIntFailRange().
    void asIntFromNumberIntFailRangeVpack() throws Exception {
        final long underflow = Integer.MIN_VALUE - 1L;
        final long overflow = Integer.MAX_VALUE + 1L;

        assertAsIntRangeFailure(NODES.numberNode(underflow));
        assertAsIntRangeFailure(NODES.numberNode(overflow));
        assertAsIntRangeFailure(NODES.numberNode(BigInteger.valueOf(underflow)));
        assertAsIntRangeFailure(NODES.numberNode(BigInteger.valueOf(overflow)));
        assertAsIntRangeFailure(MAPPER.readTree(BCD_INT_UNDERFLOW));
        assertAsIntRangeFailure(MAPPER.readTree(BCD_INT_OVERFLOW));
    }

    // Provenance: JsonNodeIntValueTest#asIntFromNumberFPOk().
    void asIntFromNumberFPOkVpack() throws Exception {
        assertAsInt(1, NODES.numberNode(1.0f));
        assertAsInt(100_000, NODES.numberNode(100_000.0f));
        assertAsInt(-100_000, NODES.numberNode(-100_000.0f));
        assertAsInt(1, NODES.numberNode(1.0d));
        assertAsInt(100_000, NODES.numberNode(100_000.0d));
        assertAsInt(-100_000, NODES.numberNode(-100_000.0d));
        assertAsInt(Integer.MIN_VALUE, NODES.numberNode((double) Integer.MIN_VALUE));
        assertAsInt(Integer.MAX_VALUE, NODES.numberNode((double) Integer.MAX_VALUE));
        assertAsInt(1, NODES.numberNode(BigDecimal.valueOf(1.0d)));
        assertAsInt(Integer.MIN_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Integer.MIN_VALUE)));
        assertAsInt(Integer.MAX_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Integer.MAX_VALUE)));

        assertAsInt(1, MAPPER.readTree(DOUBLE_ONE));
        assertAsInt(1, MAPPER.readTree(BCD_ONE));
    }

    // Provenance: JsonNodeIntValueTest#asIntFromNumberFPFailRange().
    void asIntFromNumberFPFailRangeVpack() throws Exception {
        final long underflow = Integer.MIN_VALUE - 1L;
        final long overflow = Integer.MAX_VALUE + 1L;

        assertAsIntRangeFailure(NODES.numberNode((double) underflow));
        assertAsIntRangeFailure(NODES.numberNode((double) overflow));
        assertAsIntRangeFailure(NODES.numberNode(-Float.MAX_VALUE));
        assertAsIntRangeFailure(NODES.numberNode(Float.MAX_VALUE));
        assertAsIntRangeFailure(NODES.numberNode(BigDecimal.valueOf(underflow)));
        assertAsIntRangeFailure(NODES.numberNode(BigDecimal.valueOf(overflow)));

        assertAsIntRangeFailure(MAPPER.readTree(DOUBLE_INT_OVERFLOW));
        assertAsIntRangeFailure(MAPPER.readTree(BCD_INT_OVERFLOW));
    }

    // Provenance: JsonNodeIntValueTest#asIntFromNumberFPWithFraction().
    void asIntFromNumberFPWithFractionVpack() throws Exception {
        assertAsInt(100, NODES.numberNode(100.75f));
        assertAsInt(-1, NODES.numberNode(-1.25f));
        assertAsInt(100, NODES.numberNode(100.75d));
        assertAsInt(-1, NODES.numberNode(-1.25d));
        assertAsInt(100, NODES.numberNode(BigDecimal.valueOf(100.75d)));
        assertAsInt(-1, NODES.numberNode(BigDecimal.valueOf(-1.25d)));

        assertAsInt(100, MAPPER.readTree(BCD_100_5));
        assertAsInt(-1, MAPPER.readTree(BCD_NEGATIVE_1_25));
    }

    // Provenance: JsonNodeIntValueTest#asIntFromStructuralFail().
    void asIntFromStructuralFailVpack() throws Exception {
        assertAsIntNonNumberFailure(NODES.arrayNode(3));
        assertAsIntNonNumberFailure(NODES.objectNode());
        assertAsIntNonNumberFailure(MAPPER.readTree(ARRAY));
        assertAsIntNonNumberFailure(MAPPER.readTree(EMPTY_OBJECT));
    }
private static void assertIntValue(int expected, JsonNode node) {
        assertEquals(expected, node.intValue());
        assertEquals(expected, node.intValue(999_999));
        assertEquals(expected, node.intValueOpt().getAsInt());
    }
private static void assertIntValueRangeFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::intValue);
        assertTrue(exception.getMessage().contains("intValue()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 32-bit `int` range"));
        assertEquals(99, node.intValue(99));
        assertFalse(node.intValueOpt().isPresent());
    }
private static void assertIntValueFractionFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::intValue);
        assertTrue(exception.getMessage().contains("intValue()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("to `int`: value has fractional part"));
        assertEquals(99, node.intValue(99));
        assertFalse(node.intValueOpt().isPresent());
    }
private static void assertIntValueNonNumberFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::intValue);
        assertTrue(exception.getMessage().contains("intValue()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not numeric"));
        assertEquals(99, node.intValue(99));
        assertFalse(node.intValueOpt().isPresent());
    }
private static void assertIntValueNaNFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::intValue);
        assertTrue(exception.getMessage().contains("intValue()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value non-Finite"));
        assertEquals(1, node.intValue(1));
        assertFalse(node.intValueOpt().isPresent());
    }
private static void assertAsInt(int expected, JsonNode node) {
        assertEquals(expected, node.asInt());
        assertEquals(expected, node.asInt(999_999));
        assertEquals(expected, node.asIntOpt().getAsInt());
    }
private static void assertAsIntRangeFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asInt);
        assertTrue(exception.getMessage().contains("asInt()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 32-bit `int` range"));
        assertEquals(99, node.asInt(99));
        assertFalse(node.asIntOpt().isPresent());
    }
private static void assertAsIntNonNumberFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asInt);
        assertTrue(exception.getMessage().contains("asInt()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not coercible"));
        assertEquals(99, node.asInt(99));
        assertFalse(node.asIntOpt().isPresent());
    }

    void __invoke_intValueFromNumberFPOkVpack() throws Exception {
        try {
            intValueFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_intValueFromNumberFPFailRangeVpack() throws Exception {
        try {
            intValueFromNumberFPFailRangeVpack();
        } finally {
        }
    }


    void __invoke_intValueFromNumberFPFailFractionVpack() throws Exception {
        try {
            intValueFromNumberFPFailFractionVpack();
        } finally {
        }
    }


    void __invoke_intValueFromNumberFPFailNaNVpack() throws Exception {
        try {
            intValueFromNumberFPFailNaNVpack();
        } finally {
        }
    }


    void __invoke_intValueFromNonNumberScalarFailVpack() throws Exception {
        try {
            intValueFromNonNumberScalarFailVpack();
        } finally {
        }
    }


    void __invoke_intValueFromMiscOtherFailVpack() throws Exception {
        try {
            intValueFromMiscOtherFailVpack();
        } finally {
        }
    }


    void __invoke_asIntFromNumberIntOkVpack() throws Exception {
        try {
            asIntFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_asIntFromNumberIntFailRangeVpack() throws Exception {
        try {
            asIntFromNumberIntFailRangeVpack();
        } finally {
        }
    }


    void __invoke_asIntFromNumberFPOkVpack() throws Exception {
        try {
            asIntFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_asIntFromNumberFPFailRangeVpack() throws Exception {
        try {
            asIntFromNumberFPFailRangeVpack();
        } finally {
        }
    }


    void __invoke_asIntFromNumberFPWithFractionVpack() throws Exception {
        try {
            asIntFromNumberFPWithFractionVpack();
        } finally {
        }
    }


    void __invoke_asIntFromStructuralFailVpack() throws Exception {
        try {
            asIntFromStructuralFailVpack();
        } finally {
        }
    }

}
