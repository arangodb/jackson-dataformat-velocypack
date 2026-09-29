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

class T32_0486F0 {
private static final byte[] BYTE_MIN = VPackWireFixtureTest.hex("20 80");
private static final byte[] SHORT_MAX = VPackWireFixtureTest.hex("21 ff 7f");
private static final byte[] INT_MIN = VPackWireFixtureTest.hex("23 00 00 00 80");
private static final byte[] INT_MAX = VPackWireFixtureTest.hex("23 ff ff ff 7f");
private static final byte[] LONG_MIN = VPackWireFixtureTest.hex(
            "27 00 00 00 00 00 00 00 80");
private static final byte[] LONG_MAX = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff 7f");
private static final byte[] BCD_LONG_UNDERFLOW = VPackWireFixtureTest.hex(
            "d0 0a 00 00 00 00 09 22 33 72 03 68 54 77 58 09");
private static final byte[] BCD_LONG_OVERFLOW = VPackWireFixtureTest.hex(
            "c8 0a 00 00 00 00 09 22 33 72 03 68 54 77 58 08");
private static final byte[] DOUBLE_ONE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 3f");
private static final byte[] DOUBLE_100_5 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 20 59 40");
private static final byte[] DOUBLE_NEGATIVE_0_25 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 bf");
private static final byte[] DOUBLE_MIN = VPackWireFixtureTest.hex(
            "1b ff ff ff ff ff ff ef ff");
private static final byte[] DOUBLE_MAX = VPackWireFixtureTest.hex(
            "1b ff ff ff ff ff ff ef 7f");
private static final byte[] BCD_100_5 = VPackWireFixtureTest.hex(
            "c8 02 ff ff ff ff 10 05");
private static final byte[] BCD_NEGATIVE_0_25 = VPackWireFixtureTest.hex(
            "d0 01 fe ff ff ff 25");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 01 02 03");
private static final byte[] TEXT_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] ARRAY = VPackWireFixtureTest.hex(
            "02 07 31 32 33 34 35");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] TEXT_TEST = VPackWireFixtureTest.hex(
            "44 74 65 73 74");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeLongValueTest#longValueFromNumberIntOk().
    void longValueFromNumberIntOkVpack() throws Exception {
        assertLongValue(1L, NODES.numberNode(1L));
        assertLongValue(Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertLongValue(Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));

        assertLongValue(1L, NODES.numberNode((byte) 1));
        assertLongValue((long) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertLongValue((long) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertLongValue(1L, NODES.numberNode((short) 1));
        assertLongValue((long) Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertLongValue((long) Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertLongValue(1L, NODES.numberNode(1));
        assertLongValue((long) Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertLongValue((long) Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));

        assertLongValue(1L, NODES.numberNode(BigInteger.ONE));
        assertLongValue(Long.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MIN_VALUE)));
        assertLongValue(Long.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MAX_VALUE)));

        assertLongValue((long) Byte.MIN_VALUE, MAPPER.readTree(BYTE_MIN));
        assertLongValue((long) Short.MAX_VALUE, MAPPER.readTree(SHORT_MAX));
        assertLongValue(Integer.MIN_VALUE, MAPPER.readTree(INT_MIN));
        assertLongValue(Integer.MAX_VALUE, MAPPER.readTree(INT_MAX));
        assertLongValue(Long.MIN_VALUE, MAPPER.readTree(LONG_MIN));
        assertLongValue(Long.MAX_VALUE, MAPPER.readTree(LONG_MAX));
    }

    // Provenance: JsonNodeLongValueTest#longValueFromNumberIntFailRange().
    void longValueFromNumberIntFailRangeVpack() throws Exception {
        BigInteger underflow = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);
        BigInteger overflow = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);

        assertLongValueRangeFailure(NODES.numberNode(underflow));
        assertLongValueRangeFailure(NODES.numberNode(overflow));
        assertLongValueRangeFailure(MAPPER.readTree(BCD_LONG_UNDERFLOW));
        assertLongValueRangeFailure(MAPPER.readTree(BCD_LONG_OVERFLOW));
    }

    // Provenance: JsonNodeLongValueTest#longValueFromNumberFPOk().
    void longValueFromNumberFPOkVpack() throws Exception {
        assertLongValue(1L, NODES.numberNode(1.0f));
        assertLongValue(100_000L, NODES.numberNode(100_000.0f));
        assertLongValue(-100_000L, NODES.numberNode(-100_000.0f));
        assertLongValue(1L, NODES.numberNode(1.0d));
        assertLongValue(100_000L, NODES.numberNode(100_000.0d));
        assertLongValue(-100_000L, NODES.numberNode(-100_000.0d));
        assertLongValue(Long.MIN_VALUE, NODES.numberNode((double) Long.MIN_VALUE));
        assertLongValue(Long.MAX_VALUE, NODES.numberNode((double) Long.MAX_VALUE));
        assertLongValue(1L, NODES.numberNode(BigDecimal.valueOf(1.0d)));
        assertLongValue(Long.MIN_VALUE,
                NODES.numberNode(new BigDecimal(Long.MIN_VALUE + ".0")));
        assertLongValue(Long.MAX_VALUE,
                NODES.numberNode(new BigDecimal(Long.MAX_VALUE + ".0")));

        assertLongValue(1L, MAPPER.readTree(DOUBLE_ONE));
    }

    // Provenance: JsonNodeLongValueTest#longValueFromNumberFPFailRange().
    void longValueFromNumberFPFailRangeVpack() throws Exception {
        assertLongValueRangeFailure(NODES.numberNode(-Double.MAX_VALUE));
        assertLongValueRangeFailure(NODES.numberNode(Double.MAX_VALUE));
        assertLongValueRangeFailure(NODES.numberNode(-Float.MAX_VALUE));
        assertLongValueRangeFailure(NODES.numberNode(Float.MAX_VALUE));

        BigDecimal underflow = BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE);
        BigDecimal overflow = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE);
        assertLongValueRangeFailure(NODES.numberNode(underflow));
        assertLongValueRangeFailure(NODES.numberNode(overflow));

        assertLongValueRangeFailure(MAPPER.readTree(DOUBLE_MIN));
        assertLongValueRangeFailure(MAPPER.readTree(DOUBLE_MAX));
        assertLongValueRangeFailure(MAPPER.readTree(BCD_LONG_UNDERFLOW));
        assertLongValueRangeFailure(MAPPER.readTree(BCD_LONG_OVERFLOW));
    }

    // Provenance: JsonNodeLongValueTest#longValueFromNumberFPFailFraction().
    void longValueFromNumberFPFailFractionVpack() throws Exception {
        assertLongValueFractionFailure(NODES.numberNode(100.5f));
        assertLongValueFractionFailure(NODES.numberNode(-0.25f));
        assertLongValueFractionFailure(NODES.numberNode(100.5d));
        assertLongValueFractionFailure(NODES.numberNode(-0.25d));
        assertLongValueFractionFailure(NODES.numberNode(BigDecimal.valueOf(100.5d)));
        assertLongValueFractionFailure(NODES.numberNode(BigDecimal.valueOf(-0.25d)));

        assertLongValueFractionFailure(MAPPER.readTree(DOUBLE_100_5));
        assertLongValueFractionFailure(MAPPER.readTree(DOUBLE_NEGATIVE_0_25));
        assertLongValueFractionFailure(MAPPER.readTree(BCD_100_5));
        assertLongValueFractionFailure(MAPPER.readTree(BCD_NEGATIVE_0_25));
    }

    // Provenance: JsonNodeLongValueTest#longValueFromNumberFPFailNaN().
    void longValueFromNumberFPFailNaNVpack() throws Exception {
        assertLongValueNaNFailure(NODES.numberNode(Float.NaN));
        assertLongValueNaNFailure(NODES.numberNode(Float.NEGATIVE_INFINITY));
        assertLongValueNaNFailure(NODES.numberNode(Float.POSITIVE_INFINITY));
        assertLongValueNaNFailure(NODES.numberNode(Double.NaN));
        assertLongValueNaNFailure(NODES.numberNode(Double.NEGATIVE_INFINITY));
        assertLongValueNaNFailure(NODES.numberNode(Double.POSITIVE_INFINITY));

        assertLongValueNaNFailure(MAPPER.readTree(NAN));
        assertLongValueNaNFailure(MAPPER.readTree(NEGATIVE_INFINITY));
        assertLongValueNaNFailure(MAPPER.readTree(POSITIVE_INFINITY));
    }

    // Provenance: JsonNodeLongValueTest#longValueFromNonNumberScalarFail().
    void longValueFromNonNumberScalarFailVpack() throws Exception {
        assertLongValueNonNumberFailure(NODES.booleanNode(true));
        assertLongValueNonNumberFailure(NODES.binaryNode(new byte[3]));
        assertLongValueNonNumberFailure(MAPPER.readTree(BINARY));
        assertLongValueNonNumberFailure(NODES.stringNode("123"));
        assertLongValueNonNumberFailure(MAPPER.readTree(TEXT_123));
        assertLongValueNonNumberFailure(NODES.rawValueNode(new RawValue("abc")));
        assertLongValueNonNumberFailure(NODES.pojoNode(Boolean.TRUE));
        assertLongValueNonNumberFailure(NODES.pojoNode(456L));
    }

    // Provenance: JsonNodeLongValueTest#longValueFromStructuralFail().
    void longValueFromStructuralFailVpack() throws Exception {
        assertLongValueNonNumberFailure(NODES.arrayNode(3));
        assertLongValueNonNumberFailure(NODES.objectNode());
        assertLongValueNonNumberFailure(MAPPER.readTree(ARRAY));
        assertLongValueNonNumberFailure(MAPPER.readTree(EMPTY_OBJECT));
    }

    // Provenance: JsonNodeLongValueTest#longValueFromMiscOtherFail().
    void longValueFromMiscOtherFailVpack() throws Exception {
        assertLongValueNonNumberFailure(NODES.nullNode());
        assertLongValueNonNumberFailure(NODES.missingNode());
        assertLongValueNonNumberFailure(MAPPER.readTree(NULL));
    }
private static void assertLongValue(long expected, JsonNode node) {
        assertEquals(expected, node.longValue());
        assertEquals(expected, node.longValue(999999L));
        assertEquals(expected, node.longValueOpt().getAsLong());
    }
private static void assertLongValueRangeFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::longValue);
        assertTrue(exception.getMessage().contains("longValue()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 64-bit `long` range"));
        assertEquals(1L, node.longValue(1L));
        assertFalse(node.longValueOpt().isPresent());
    }
private static void assertLongValueFractionFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::longValue);
        assertTrue(exception.getMessage().contains("longValue()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("to `long`: value has fractional part"));
        assertEquals(1L, node.longValue(1L));
        assertFalse(node.longValueOpt().isPresent());
    }
private static void assertLongValueNonNumberFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::longValue);
        assertTrue(exception.getMessage().contains("longValue()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not numeric"));
        assertEquals(1L, node.longValue(1L));
        assertFalse(node.longValueOpt().isPresent());
    }
private static void assertLongValueNaNFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::longValue);
        assertTrue(exception.getMessage().contains("longValue()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value non-Finite"));
        assertEquals(1L, node.longValue(1L));
        assertFalse(node.longValueOpt().isPresent());
    }

    void __invoke_longValueFromNumberIntOkVpack() throws Exception {
        try {
            longValueFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_longValueFromNumberIntFailRangeVpack() throws Exception {
        try {
            longValueFromNumberIntFailRangeVpack();
        } finally {
        }
    }


    void __invoke_longValueFromNumberFPOkVpack() throws Exception {
        try {
            longValueFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_longValueFromNumberFPFailRangeVpack() throws Exception {
        try {
            longValueFromNumberFPFailRangeVpack();
        } finally {
        }
    }


    void __invoke_longValueFromNumberFPFailFractionVpack() throws Exception {
        try {
            longValueFromNumberFPFailFractionVpack();
        } finally {
        }
    }


    void __invoke_longValueFromNumberFPFailNaNVpack() throws Exception {
        try {
            longValueFromNumberFPFailNaNVpack();
        } finally {
        }
    }


    void __invoke_longValueFromNonNumberScalarFailVpack() throws Exception {
        try {
            longValueFromNonNumberScalarFailVpack();
        } finally {
        }
    }


    void __invoke_longValueFromStructuralFailVpack() throws Exception {
        try {
            longValueFromStructuralFailVpack();
        } finally {
        }
    }


    void __invoke_longValueFromMiscOtherFailVpack() throws Exception {
        try {
            longValueFromMiscOtherFailVpack();
        } finally {
        }
    }

}
