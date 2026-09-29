package tools.jackson.databind.node;

import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.JsonNodeFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0485F0 {
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

    // Provenance: JsonNodeIntValueTest#intValueFromNumberIntOk().
    void intValueFromNumberIntOkVpack() throws Exception {
        assertIntValue(1, NODES.numberNode(1));
        assertIntValue(Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertIntValue(Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));
        assertIntValue(1, NODES.numberNode((byte) 1));
        assertIntValue((int) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertIntValue((int) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertIntValue(1, NODES.numberNode((short) 1));
        assertIntValue((int) Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertIntValue((int) Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertIntValue(1, NODES.numberNode(1L));
        assertIntValue(Integer.MIN_VALUE, NODES.numberNode((long) Integer.MIN_VALUE));
        assertIntValue(Integer.MAX_VALUE, NODES.numberNode((long) Integer.MAX_VALUE));
        assertIntValue(1, NODES.numberNode(BigInteger.ONE));
        assertIntValue(Integer.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Integer.MIN_VALUE)));
        assertIntValue(Integer.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Integer.MAX_VALUE)));

        assertIntValue((int) Byte.MIN_VALUE, MAPPER.readTree(BYTE_MIN));
        assertIntValue((int) Short.MAX_VALUE, MAPPER.readTree(SHORT_MAX));
        assertIntValue(Integer.MIN_VALUE, MAPPER.readTree(INT_MIN));
        assertIntValue(Integer.MAX_VALUE, MAPPER.readTree(INT_MAX));
    }

    // Provenance: JsonNodeIntValueTest#intValueFromNumberIntFailRange().
    void intValueFromNumberIntFailRangeVpack() throws Exception {
        long underflow = (long) Integer.MIN_VALUE - 1L;
        long overflow = (long) Integer.MAX_VALUE + 1L;

        assertIntValueRangeFailure(NODES.numberNode(underflow));
        assertIntValueRangeFailure(NODES.numberNode(overflow));
        assertIntValueRangeFailure(NODES.numberNode(BigInteger.valueOf(underflow)));
        assertIntValueRangeFailure(NODES.numberNode(BigInteger.valueOf(overflow)));
        assertIntValueRangeFailure(MAPPER.readTree(BCD_INT_UNDERFLOW));
        assertIntValueRangeFailure(MAPPER.readTree(BCD_INT_OVERFLOW));
    }

    // Provenance: JsonNodeIntValueTest#intValueFromStructuralFail().
    void intValueFromStructuralFailVpack() throws Exception {
        assertIntValueNonNumberFailure(NODES.arrayNode(3));
        assertIntValueNonNumberFailure(NODES.objectNode());
        assertIntValueNonNumberFailure(MAPPER.readTree(ARRAY));
        assertIntValueNonNumberFailure(MAPPER.readTree(EMPTY_OBJECT));
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

    void __invoke_intValueFromNumberIntOkVpack() throws Exception {
        try {
            intValueFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_intValueFromNumberIntFailRangeVpack() throws Exception {
        try {
            intValueFromNumberIntFailRangeVpack();
        } finally {
        }
    }


    void __invoke_intValueFromStructuralFailVpack() throws Exception {
        try {
            intValueFromStructuralFailVpack();
        } finally {
        }
    }

}
