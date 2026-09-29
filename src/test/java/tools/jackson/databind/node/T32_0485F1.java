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

class T32_0485F1 {
private static final byte[] BYTE_MIN = VPackWireFixtureTest.hex("20 80");
private static final byte[] SHORT_MAX = VPackWireFixtureTest.hex("21 ff 7f");
private static final byte[] INT_MIN = VPackWireFixtureTest.hex("23 00 00 00 80");
private static final byte[] INT_MAX = VPackWireFixtureTest.hex("23 ff ff ff 7f");
private static final byte[] LONG_MIN = VPackWireFixtureTest.hex(
            "27 00 00 00 00 00 00 00 80");
private static final byte[] LONG_MAX = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff 7f");
private static final byte[] BCD_INT_UNDERFLOW = VPackWireFixtureTest.hex(
            "d0 05 00 00 00 00 21 47 48 36 49");
private static final byte[] BCD_INT_OVERFLOW = VPackWireFixtureTest.hex(
            "c8 05 00 00 00 00 21 47 48 36 48");
private static final byte[] BCD_LONG_UNDERFLOW = VPackWireFixtureTest.hex(
            "d0 0a 00 00 00 00 09 22 33 72 03 68 54 77 58 09");
private static final byte[] BCD_LONG_OVERFLOW = VPackWireFixtureTest.hex(
            "c8 0a 00 00 00 00 09 22 33 72 03 68 54 77 58 08");
private static final byte[] DOUBLE_ONE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 3f");
private static final byte[] DOUBLE_100_75 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 20 59 40");
private static final byte[] DOUBLE_MAX = VPackWireFixtureTest.hex(
            "1b ff ff ff ff ff ff ef 7f");
private static final byte[] DOUBLE_MIN = VPackWireFixtureTest.hex(
            "1b ff ff ff ff ff ff ef ff");
private static final byte[] BCD_100_75 = VPackWireFixtureTest.hex(
            "c8 03 fe ff ff ff 01 00 75");
private static final byte[] BCD_NEGATIVE_1_25 = VPackWireFixtureTest.hex(
            "d0 02 fe ff ff ff 01 25");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 01 02 03");
private static final byte[] TEXT_ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] TEXT_1234 = VPackWireFixtureTest.hex(
            "44 31 32 33 34");
private static final byte[] ARRAY = VPackWireFixtureTest.hex("13 05 31 32 02");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeLongValueTest#asLongFromNumberIntOk().
    void asLongFromNumberIntOkVpack() throws Exception {
        assertAsLong(1L, NODES.numberNode(1L));
        assertAsLong(Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertAsLong(Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));
        assertAsLong(1L, NODES.numberNode((byte) 1));
        assertAsLong((long) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertAsLong((long) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertAsLong(1L, NODES.numberNode((short) 1));
        assertAsLong((long) Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertAsLong((long) Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertAsLong(1L, NODES.numberNode(1));
        assertAsLong((long) Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertAsLong((long) Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));
        assertAsLong(1L, NODES.numberNode(BigInteger.ONE));
        assertAsLong(Long.MIN_VALUE, NODES.numberNode(BigInteger.valueOf(Long.MIN_VALUE)));
        assertAsLong(Long.MAX_VALUE, NODES.numberNode(BigInteger.valueOf(Long.MAX_VALUE)));

        assertAsLong((long) Byte.MIN_VALUE, MAPPER.readTree(BYTE_MIN));
        assertAsLong((long) Short.MAX_VALUE, MAPPER.readTree(SHORT_MAX));
        assertAsLong((long) Integer.MIN_VALUE, MAPPER.readTree(INT_MIN));
        assertAsLong((long) Integer.MAX_VALUE, MAPPER.readTree(INT_MAX));
        assertAsLong(Long.MIN_VALUE, MAPPER.readTree(LONG_MIN));
        assertAsLong(Long.MAX_VALUE, MAPPER.readTree(LONG_MAX));
    }

    // Provenance: JsonNodeLongValueTest#asLongFromNumberIntFailRange().
    void asLongFromNumberIntFailRangeVpack() throws Exception {
        BigInteger underflow = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);
        BigInteger overflow = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);

        assertAsLongRangeFailure(NODES.numberNode(underflow));
        assertAsLongRangeFailure(NODES.numberNode(overflow));
        assertAsLongRangeFailure(MAPPER.readTree(BCD_LONG_UNDERFLOW));
        assertAsLongRangeFailure(MAPPER.readTree(BCD_LONG_OVERFLOW));
    }

    // Provenance: JsonNodeLongValueTest#asLongFromNumberFPOk().
    void asLongFromNumberFPOkVpack() throws Exception {
        assertAsLong(1L, NODES.numberNode(1.0f));
        assertAsLong(100_000L, NODES.numberNode(100_000.0f));
        assertAsLong(-100_000L, NODES.numberNode(-100_000.0f));
        assertAsLong(1L, NODES.numberNode(1.0d));
        assertAsLong(100_000L, NODES.numberNode(100_000.0d));
        assertAsLong(-100_000L, NODES.numberNode(-100_000.0d));
        assertAsLong(Long.MIN_VALUE, NODES.numberNode((double) Long.MIN_VALUE));
        assertAsLong(Long.MAX_VALUE, NODES.numberNode((double) Long.MAX_VALUE));
        assertAsLong(1L, NODES.numberNode(BigDecimal.valueOf(1.0d)));
        assertAsLong(Long.MIN_VALUE,
                NODES.numberNode(new BigDecimal(Long.MIN_VALUE + ".0")));
        assertAsLong(Long.MAX_VALUE,
                NODES.numberNode(new BigDecimal(Long.MAX_VALUE + ".0")));

        assertAsLong(1L, MAPPER.readTree(DOUBLE_ONE));
        assertAsLong(1L, MAPPER.readTree(VPackWireFixtureTest.hex(
                "c8 01 00 00 00 00 01")));
    }

    // Provenance: JsonNodeLongValueTest#asLongFromNumberFPFailRange().
    void asLongFromNumberFPFailRangeVpack() throws Exception {
        assertAsLongRangeFailure(NODES.numberNode(-Double.MAX_VALUE));
        assertAsLongRangeFailure(NODES.numberNode(Double.MAX_VALUE));
        assertAsLongRangeFailure(NODES.numberNode(-Float.MAX_VALUE));
        assertAsLongRangeFailure(NODES.numberNode(Float.MAX_VALUE));

        BigDecimal underflow = BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE);
        BigDecimal overflow = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE);
        assertAsLongRangeFailure(NODES.numberNode(underflow));
        assertAsLongRangeFailure(NODES.numberNode(overflow));

        assertAsLongRangeFailure(MAPPER.readTree(DOUBLE_MIN));
        assertAsLongRangeFailure(MAPPER.readTree(DOUBLE_MAX));
        assertAsLongRangeFailure(MAPPER.readTree(BCD_LONG_UNDERFLOW));
        assertAsLongRangeFailure(MAPPER.readTree(BCD_LONG_OVERFLOW));
    }

    // Provenance: JsonNodeLongValueTest#asLongFromNumberFPWithFraction().
    void asLongFromNumberFPWithFractionVpack() throws Exception {
        assertAsLong(100L, NODES.numberNode(100.75f));
        assertAsLong(-1L, NODES.numberNode(-1.25f));
        assertAsLong(100L, NODES.numberNode(100.75d));
        assertAsLong(-1L, NODES.numberNode(-1.25d));
        assertAsLong(100L, NODES.numberNode(BigDecimal.valueOf(100.75d)));
        assertAsLong(-1L, NODES.numberNode(BigDecimal.valueOf(-1.25d)));

        assertAsLong(100L, MAPPER.readTree(DOUBLE_100_75));
        assertAsLong(100L, MAPPER.readTree(BCD_100_75));
        assertAsLong(-1L, MAPPER.readTree(BCD_NEGATIVE_1_25));
    }

    // Provenance: JsonNodeLongValueTest#asLongFromNumberFPFailNaN().
    void asLongFromNumberFPFailNaNVpack() throws Exception {
        assertAsLongNaNFailure(NODES.numberNode(Float.NaN));
        assertAsLongNaNFailure(NODES.numberNode(Float.NEGATIVE_INFINITY));
        assertAsLongNaNFailure(NODES.numberNode(Float.POSITIVE_INFINITY));
        assertAsLongNaNFailure(NODES.numberNode(Double.NaN));
        assertAsLongNaNFailure(NODES.numberNode(Double.NEGATIVE_INFINITY));
        assertAsLongNaNFailure(NODES.numberNode(Double.POSITIVE_INFINITY));
        assertAsLongNaNFailure(MAPPER.readTree(NAN));
        assertAsLongNaNFailure(MAPPER.readTree(NEGATIVE_INFINITY));
        assertAsLongNaNFailure(MAPPER.readTree(POSITIVE_INFINITY));
    }

    // Provenance: JsonNodeLongValueTest#asLongFromNonNumberScalarFail().
    void asLongFromNonNumberScalarFailVpack() throws Exception {
        assertAsLongNonNumberFailure(NODES.booleanNode(true));
        assertAsLongNonNumberFailure(NODES.binaryNode(new byte[3]));
        assertAsLongNonNumberFailure(MAPPER.readTree(BINARY));
        assertAsLongNonNumberFailure(NODES.rawValueNode(new RawValue("abc")));
        assertAsLongNonNumberFailure(NODES.pojoNode(Boolean.TRUE));
        assertAsLongNonNumberFailure(NODES.stringNode("abcdef"),
                "not a valid String representation of `long`");
        assertAsLongNonNumberFailure(MAPPER.readTree(TEXT_ABC),
                "not a valid String representation of `long`");
        assertAsLongNonNumberFailure(NODES.pojoNode(true));

        assertAsLong(1234L, NODES.stringNode("1234"));
        assertAsLong(123456L, NODES.pojoNode(123456L));
        assertAsLong(789L, NODES.pojoNode(BigInteger.valueOf(789)));
        assertAsLong(1234L, MAPPER.readTree(TEXT_1234));
    }

    // Provenance: JsonNodeLongValueTest#asLongFromStructuralFail().
    void asLongFromStructuralFailVpack() throws Exception {
        assertAsLongNonNumberFailure(NODES.arrayNode(3));
        assertAsLongNonNumberFailure(NODES.objectNode());
        assertAsLongNonNumberFailure(MAPPER.readTree(ARRAY));
        assertAsLongNonNumberFailure(MAPPER.readTree(EMPTY_OBJECT));
    }

    // Provenance: JsonNodeLongValueTest#asLongFromMiscOther().
    void asLongFromMiscOtherVpack() throws Exception {
        assertEquals(0L, NODES.nullNode().asLong());
        assertEquals(999999L, NODES.nullNode().asLong(999999L));
        assertFalse(NODES.nullNode().asLongOpt().isPresent());
        assertEquals(0L, NODES.missingNode().asLong());
        assertEquals(999999L, NODES.missingNode().asLong(999999L));
        assertFalse(NODES.missingNode().asLongOpt().isPresent());

        JsonNode parsedNull = MAPPER.readTree(NULL);
        assertEquals(0L, parsedNull.asLong());
        assertEquals(999999L, parsedNull.asLong(999999L));
        assertFalse(parsedNull.asLongOpt().isPresent());
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
private static void assertIntValueNonNumberFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::intValue);
        assertTrue(exception.getMessage().contains("intValue()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not numeric"));
        assertEquals(99, node.intValue(99));
        assertFalse(node.intValueOpt().isPresent());
    }
private static void assertAsLong(long expected, JsonNode node) {
        assertEquals(expected, node.asLong());
        assertEquals(expected, node.asLong(999999L));
        assertEquals(expected, node.asLongOpt().getAsLong());
    }
private static void assertAsLongRangeFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asLong);
        assertTrue(exception.getMessage().contains("asLong()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 64-bit `long` range"));
        assertEquals(1L, node.asLong(1L));
        assertFalse(node.asLongOpt().isPresent());
    }
private static void assertAsLongNonNumberFailure(JsonNode node) {
        assertAsLongNonNumberFailure(node, "value type not coercible");
    }
private static void assertAsLongNonNumberFailure(JsonNode node, String extraMessage) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asLong);
        assertTrue(exception.getMessage().contains("asLong()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains(extraMessage));
        assertEquals(1L, node.asLong(1L));
        assertFalse(node.asLongOpt().isPresent());
    }
private static void assertAsLongNaNFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asLong);
        assertTrue(exception.getMessage().contains("asLong()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value non-Finite"));
        assertEquals(1L, node.asLong(1L));
        assertFalse(node.asLongOpt().isPresent());
    }

    void __invoke_asLongFromNumberIntOkVpack() throws Exception {
        try {
            asLongFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_asLongFromNumberIntFailRangeVpack() throws Exception {
        try {
            asLongFromNumberIntFailRangeVpack();
        } finally {
        }
    }


    void __invoke_asLongFromNumberFPOkVpack() throws Exception {
        try {
            asLongFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_asLongFromNumberFPFailRangeVpack() throws Exception {
        try {
            asLongFromNumberFPFailRangeVpack();
        } finally {
        }
    }


    void __invoke_asLongFromNumberFPWithFractionVpack() throws Exception {
        try {
            asLongFromNumberFPWithFractionVpack();
        } finally {
        }
    }


    void __invoke_asLongFromNumberFPFailNaNVpack() throws Exception {
        try {
            asLongFromNumberFPFailNaNVpack();
        } finally {
        }
    }


    void __invoke_asLongFromNonNumberScalarFailVpack() throws Exception {
        try {
            asLongFromNonNumberScalarFailVpack();
        } finally {
        }
    }


    void __invoke_asLongFromStructuralFailVpack() throws Exception {
        try {
            asLongFromStructuralFailVpack();
        } finally {
        }
    }


    void __invoke_asLongFromMiscOtherVpack() throws Exception {
        try {
            asLongFromMiscOtherVpack();
        } finally {
        }
    }

}
