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

class T32_0489Fixture {
private static final byte[] SHORT_MIN = VPackWireFixtureTest.hex("21 00 80");
private static final byte[] SHORT_MAX = VPackWireFixtureTest.hex("21 ff 7f");
private static final byte[] INT_SHORT_MIN = VPackWireFixtureTest.hex(
            "23 00 80 ff ff");
private static final byte[] INT_SHORT_MAX = VPackWireFixtureTest.hex(
            "23 ff 7f 00 00");
private static final byte[] LONG_SHORT_MIN = VPackWireFixtureTest.hex(
            "27 00 80 ff ff ff ff ff ff");
private static final byte[] LONG_SHORT_MAX = VPackWireFixtureTest.hex(
            "2f ff 7f 00 00 00 00 00 00");
private static final byte[] BCD_SHORT_UNDERFLOW = VPackWireFixtureTest.hex(
            "d0 03 00 00 00 00 03 27 69");
private static final byte[] BCD_SHORT_OVERFLOW = VPackWireFixtureTest.hex(
            "c8 03 00 00 00 00 03 27 68");
private static final byte[] BCD_ONE = VPackWireFixtureTest.hex(
            "c8 01 00 00 00 00 01");
private static final byte[] BCD_100_5 = VPackWireFixtureTest.hex(
            "c8 02 ff ff ff ff 10 05");
private static final byte[] BCD_NEGATIVE_0_25 = VPackWireFixtureTest.hex(
            "d0 01 fe ff ff ff 25");
private static final byte[] DOUBLE_ONE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 3f");
private static final byte[] DOUBLE_10_000 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 88 c3 40");
private static final byte[] DOUBLE_NEGATIVE_10_000 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 88 c3 c0");
private static final byte[] DOUBLE_SHORT_MIN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 e0 c0");
private static final byte[] DOUBLE_SHORT_MAX = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 c0 ff df 40");
private static final byte[] DOUBLE_UNDERFLOW = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 80 00 e0 c0");
private static final byte[] DOUBLE_OVERFLOW = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 e0 40");
private static final byte[] DOUBLE_100_75 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 30 59 40");
private static final byte[] DOUBLE_NEGATIVE_1_25 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f4 bf");
private static final byte[] BOOLEAN_TRUE = VPackWireFixtureTest.hex("19");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 01 02 03");
private static final byte[] TEXT_ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] TEXT_123 = VPackWireFixtureTest.hex("43 31 32 33");
private static final byte[] ARRAY = VPackWireFixtureTest.hex(
            "02 07 31 32 33 34 35");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeShortValueTest#shortValueFromNumberIntFailRange().
    void shortValueFromNumberIntFailRangeVpack() throws Exception {
        int underflow = Short.MIN_VALUE - 1;
        int overflow = Short.MAX_VALUE + 1;
        assertShortValueRangeFailure(NODES.numberNode(underflow));
        assertShortValueRangeFailure(NODES.numberNode(overflow));
        assertShortValueRangeFailure(NODES.numberNode(BigInteger.valueOf(underflow)));
        assertShortValueRangeFailure(NODES.numberNode(BigInteger.valueOf(overflow)));
        assertShortValueRangeFailure(MAPPER.readTree(BCD_SHORT_UNDERFLOW));
        assertShortValueRangeFailure(MAPPER.readTree(BCD_SHORT_OVERFLOW));
    }

    // Provenance: JsonNodeShortValueTest#shortValueFromNumberFPOk().
    void shortValueFromNumberFPOkVpack() throws Exception {
        assertShortValue((short) 1, NODES.numberNode(1.0f));
        assertShortValue((short) 10_000, NODES.numberNode(10_000.0f));
        assertShortValue((short) -10_000, NODES.numberNode(-10_000.0f));
        assertShortValue((short) 1, NODES.numberNode(1.0d));
        assertShortValue((short) 10_000, NODES.numberNode(10_000.0d));
        assertShortValue((short) -10_000, NODES.numberNode(-10_000.0d));
        assertShortValue(Short.MIN_VALUE, NODES.numberNode((double) Short.MIN_VALUE));
        assertShortValue(Short.MAX_VALUE, NODES.numberNode((double) Short.MAX_VALUE));
        assertShortValue((short) 1, NODES.numberNode(BigDecimal.valueOf(1.0d)));
        assertShortValue(Short.MIN_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Short.MIN_VALUE)));
        assertShortValue(Short.MAX_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Short.MAX_VALUE)));

        assertShortValue((short) 1, MAPPER.readTree(DOUBLE_ONE));
        assertShortValue((short) 10_000, MAPPER.readTree(DOUBLE_10_000));
        assertShortValue((short) -10_000, MAPPER.readTree(DOUBLE_NEGATIVE_10_000));
        assertShortValue(Short.MIN_VALUE, MAPPER.readTree(DOUBLE_SHORT_MIN));
        assertShortValue(Short.MAX_VALUE, MAPPER.readTree(DOUBLE_SHORT_MAX));
        assertShortValue((short) 1, MAPPER.readTree(BCD_ONE));
    }

    // Provenance: JsonNodeShortValueTest#shortValueFromNumberFPFailRange().
    void shortValueFromNumberFPFailRangeVpack() throws Exception {
        assertShortValueRangeFailure(NODES.numberNode((double) (Short.MIN_VALUE - 1L)));
        assertShortValueRangeFailure(NODES.numberNode((double) (Short.MAX_VALUE + 1L)));
        assertShortValueRangeFailure(NODES.numberNode(-Float.MAX_VALUE));
        assertShortValueRangeFailure(NODES.numberNode(Float.MAX_VALUE));
        assertShortValueRangeFailure(NODES.numberNode(
                BigDecimal.valueOf(Short.MIN_VALUE).subtract(BigDecimal.ONE)));
        assertShortValueRangeFailure(NODES.numberNode(
                BigDecimal.valueOf(Short.MAX_VALUE).add(BigDecimal.ONE)));

        assertShortValueRangeFailure(MAPPER.readTree(DOUBLE_UNDERFLOW));
        assertShortValueRangeFailure(MAPPER.readTree(DOUBLE_OVERFLOW));
        assertShortValueRangeFailure(MAPPER.readTree(BCD_SHORT_UNDERFLOW));
        assertShortValueRangeFailure(MAPPER.readTree(BCD_SHORT_OVERFLOW));
    }

    // Provenance: JsonNodeShortValueTest#shortValueFromNumberFPFailFraction().
    void shortValueFromNumberFPFailFractionVpack() throws Exception {
        assertShortValueFractionFailure(NODES.numberNode(100.5f));
        assertShortValueFractionFailure(NODES.numberNode(-0.25f));
        assertShortValueFractionFailure(NODES.numberNode(100.5d));
        assertShortValueFractionFailure(NODES.numberNode(-0.25d));
        assertShortValueFractionFailure(NODES.numberNode(BigDecimal.valueOf(100.5d)));
        assertShortValueFractionFailure(NODES.numberNode(BigDecimal.valueOf(-0.25d)));

        assertShortValueFractionFailure(MAPPER.readTree(DOUBLE_100_75));
        assertShortValueFractionFailure(MAPPER.readTree(DOUBLE_NEGATIVE_1_25));
        assertShortValueFractionFailure(MAPPER.readTree(BCD_100_5));
        assertShortValueFractionFailure(MAPPER.readTree(BCD_NEGATIVE_0_25));
    }

    // Provenance: JsonNodeShortValueTest#shortValueFromNonNumberFail().
    void shortValueFromNonNumberFailVpack() throws Exception {
        assertShortValueNonNumberFailure(NODES.booleanNode(true));
        assertShortValueNonNumberFailure(NODES.binaryNode(new byte[3]));
        assertShortValueNonNumberFailure(NODES.stringNode("123"));
        assertShortValueNonNumberFailure(NODES.rawValueNode(new RawValue("abc")));
        assertShortValueNonNumberFailure(NODES.pojoNode(Boolean.TRUE));
        assertShortValueNonNumberFailure(NODES.arrayNode(3));
        assertShortValueNonNumberFailure(NODES.objectNode());
        assertShortValueNonNumberFailure(NODES.nullNode());
        assertShortValueNonNumberFailure(NODES.missingNode());
        assertShortValueNonNumberFailure(NODES.pojoNode((short) 456));

        assertShortValueNonNumberFailure(MAPPER.readTree(BOOLEAN_TRUE));
        assertShortValueNonNumberFailure(MAPPER.readTree(BINARY));
        assertShortValueNonNumberFailure(MAPPER.readTree(TEXT_123));
        assertShortValueNonNumberFailure(MAPPER.readTree(ARRAY));
        assertShortValueNonNumberFailure(MAPPER.readTree(EMPTY_OBJECT));
    }

    // Provenance: JsonNodeShortValueTest#asShortFromNumberIntOk().
    void asShortFromNumberIntOkVpack() throws Exception {
        assertAsShort((short) 1, NODES.numberNode((short) 1));
        assertAsShort(Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertAsShort(Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertAsShort((short) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertAsShort((short) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertAsShort(Short.MIN_VALUE, NODES.numberNode((int) Short.MIN_VALUE));
        assertAsShort(Short.MAX_VALUE, NODES.numberNode((int) Short.MAX_VALUE));
        assertAsShort(Short.MIN_VALUE, NODES.numberNode((long) Short.MIN_VALUE));
        assertAsShort(Short.MAX_VALUE, NODES.numberNode((long) Short.MAX_VALUE));
        assertAsShort(Short.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Short.MIN_VALUE)));
        assertAsShort(Short.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Short.MAX_VALUE)));

        assertAsShort(Short.MIN_VALUE, MAPPER.readTree(SHORT_MIN));
        assertAsShort(Short.MAX_VALUE, MAPPER.readTree(SHORT_MAX));
        assertAsShort(Short.MIN_VALUE, MAPPER.readTree(INT_SHORT_MIN));
        assertAsShort(Short.MAX_VALUE, MAPPER.readTree(INT_SHORT_MAX));
        assertAsShort(Short.MIN_VALUE, MAPPER.readTree(LONG_SHORT_MIN));
        assertAsShort(Short.MAX_VALUE, MAPPER.readTree(LONG_SHORT_MAX));
    }

    // Provenance: JsonNodeShortValueTest#asShortFromNumberIntFailRange().
    void asShortFromNumberIntFailRangeVpack() throws Exception {
        int underflow = Short.MIN_VALUE - 1;
        long overflow = Short.MAX_VALUE + 1L;
        assertAsShortRangeFailure(NODES.numberNode(underflow));
        assertAsShortRangeFailure(NODES.numberNode(overflow));
        assertAsShortRangeFailure(NODES.numberNode(BigInteger.valueOf(underflow)));
        assertAsShortRangeFailure(NODES.numberNode(BigInteger.valueOf(overflow)));
        assertAsShortRangeFailure(MAPPER.readTree(BCD_SHORT_UNDERFLOW));
        assertAsShortRangeFailure(MAPPER.readTree(BCD_SHORT_OVERFLOW));
    }

    // Provenance: JsonNodeShortValueTest#asShortFromNumberFPOk().
    void asShortFromNumberFPOkVpack() throws Exception {
        assertAsShort((short) 1, NODES.numberNode(1.0f));
        assertAsShort((short) 100, NODES.numberNode(100.0f));
        assertAsShort((short) -100, NODES.numberNode(-100.0f));
        assertAsShort((short) 1, NODES.numberNode(1.0d));
        assertAsShort((short) 100, NODES.numberNode(100.0d));
        assertAsShort((short) -100, NODES.numberNode(-100.0d));
        assertAsShort(Short.MIN_VALUE, NODES.numberNode((double) Short.MIN_VALUE));
        assertAsShort(Short.MAX_VALUE, NODES.numberNode((double) Short.MAX_VALUE));
        assertAsShort((short) 1, NODES.numberNode(BigDecimal.valueOf(1.0d)));
        assertAsShort(Short.MIN_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Short.MIN_VALUE)));
        assertAsShort(Short.MAX_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Short.MAX_VALUE)));

        assertAsShort((short) 1, MAPPER.readTree(DOUBLE_ONE));
        assertAsShort((short) 100, MAPPER.readTree(VPackWireFixtureTest.hex(
                "1b 00 00 00 00 00 00 59 40")));
        assertAsShort((short) -100, MAPPER.readTree(VPackWireFixtureTest.hex(
                "1b 00 00 00 00 00 00 59 c0")));
        assertAsShort(Short.MIN_VALUE, MAPPER.readTree(DOUBLE_SHORT_MIN));
        assertAsShort(Short.MAX_VALUE, MAPPER.readTree(DOUBLE_SHORT_MAX));
    }

    // Provenance: JsonNodeShortValueTest#asShortFromNumberFPFailRange().
    void asShortFromNumberFPFailRangeVpack() throws Exception {
        assertAsShortRangeFailure(NODES.numberNode((float) (Short.MIN_VALUE - 1L)));
        assertAsShortRangeFailure(NODES.numberNode((double) (Short.MAX_VALUE + 1L)));
        assertAsShortRangeFailure(NODES.numberNode(-Float.MAX_VALUE));
        assertAsShortRangeFailure(NODES.numberNode(Float.MAX_VALUE));
        assertAsShortRangeFailure(NODES.numberNode(
                BigDecimal.valueOf(Short.MIN_VALUE).subtract(BigDecimal.ONE)));
        assertAsShortRangeFailure(NODES.numberNode(
                BigDecimal.valueOf(Short.MAX_VALUE).add(BigDecimal.ONE)));

        assertAsShortRangeFailure(MAPPER.readTree(DOUBLE_UNDERFLOW));
        assertAsShortRangeFailure(MAPPER.readTree(DOUBLE_OVERFLOW));
        assertAsShortRangeFailure(MAPPER.readTree(BCD_SHORT_UNDERFLOW));
        assertAsShortRangeFailure(MAPPER.readTree(BCD_SHORT_OVERFLOW));
    }

    // Provenance: JsonNodeShortValueTest#asShortFromNumberFPWithFraction().
    void asShortFromNumberFPWithFractionVpack() throws Exception {
        assertAsShort((short) 100, NODES.numberNode(100.75f));
        assertAsShort((short) -1, NODES.numberNode(-1.25f));
        assertAsShort((short) 100, NODES.numberNode(100.75d));
        assertAsShort((short) -1, NODES.numberNode(-1.25d));
        assertAsShort((short) 100, NODES.numberNode(BigDecimal.valueOf(100.75d)));
        assertAsShort((short) -1, NODES.numberNode(BigDecimal.valueOf(-1.25d)));

        assertAsShort((short) 100, MAPPER.readTree(DOUBLE_100_75));
        assertAsShort((short) -1, MAPPER.readTree(DOUBLE_NEGATIVE_1_25));
        assertAsShort((short) 100, MAPPER.readTree(VPackWireFixtureTest.hex(
                "c8 03 fe ff ff ff 01 00 75")));
        assertAsShort((short) -1, MAPPER.readTree(VPackWireFixtureTest.hex(
                "d0 02 fe ff ff ff 01 25")));
    }

    // Provenance: JsonNodeShortValueTest#asShortFromNonNumberScalar().
    void asShortFromNonNumberScalarVpack() throws Exception {
        assertAsShortNonNumberFailure(NODES.booleanNode(true));
        assertAsShortNonNumberFailure(NODES.binaryNode(new byte[3]));
        assertAsShortNonNumberFailure(NODES.rawValueNode(new RawValue("abc")));
        assertAsShortNonNumberFailure(NODES.pojoNode(Boolean.TRUE));
        assertAsShortNonNumberFailure(NODES.stringNode("abc"),
                "not a valid String representation of `short`");
        assertAsShortNonNumberFailure(NODES.pojoNode("123456"));

        assertAsShort((short) 123, NODES.stringNode("123"));
        assertAsShort((short) 456, NODES.pojoNode(456));
        assertAsShort((short) 789, NODES.pojoNode(BigInteger.valueOf(789)));

        assertAsShortNonNumberFailure(MAPPER.readTree(BOOLEAN_TRUE));
        assertAsShortNonNumberFailure(MAPPER.readTree(BINARY));
        assertAsShortNonNumberFailure(MAPPER.readTree(TEXT_ABC),
                "not a valid String representation of `short`");
        assertAsShort((short) 123, MAPPER.readTree(TEXT_123));
    }

    // Provenance: JsonNodeShortValueTest#asIntFromStructuralFail().
    void asIntFromStructuralFailVpack() throws Exception {
        assertAsShortNonNumberFailure(NODES.arrayNode(3));
        assertAsShortNonNumberFailure(NODES.objectNode());
        assertAsShortNonNumberFailure(MAPPER.readTree(ARRAY));
        assertAsShortNonNumberFailure(MAPPER.readTree(EMPTY_OBJECT));
    }
private static void assertShortValue(short expected, JsonNode node) {
        assertEquals(expected, node.shortValue());
        assertEquals(expected, node.shortValue((short) 99));
        assertEquals(expected, node.shortValueOpt().get());
    }
private static void assertShortValueRangeFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::shortValue);
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 16-bit `short` range"));
        assertEquals((short) 99, node.shortValue((short) 99));
        assertFalse(node.shortValueOpt().isPresent());
    }
private static void assertShortValueFractionFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::shortValue);
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("to `short`: value has fractional part"));
        assertEquals((short) 99, node.shortValue((short) 99));
        assertFalse(node.shortValueOpt().isPresent());
    }
private static void assertShortValueNonNumberFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::shortValue);
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not numeric"));
        assertEquals((short) 99, node.shortValue((short) 99));
        assertFalse(node.shortValueOpt().isPresent());
    }
private static void assertAsShort(short expected, JsonNode node) {
        assertEquals(expected, node.asShort());
        assertEquals(expected, node.asShort((short) 99));
        assertEquals(expected, node.asShortOpt().get());
    }
private static void assertAsShortRangeFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asShort);
        assertTrue(exception.getMessage().contains("asShort()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 16-bit `short` range"));
        assertEquals((short) 99, node.asShort((short) 99));
        assertFalse(node.asShortOpt().isPresent());
    }
private static void assertAsShortNonNumberFailure(JsonNode node) {
        assertAsShortNonNumberFailure(node, "value type not coercible");
    }
private static void assertAsShortNonNumberFailure(JsonNode node, String extraMessage) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asShort);
        assertTrue(exception.getMessage().contains("asShort()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains(extraMessage));
        assertEquals((short) 99, node.asShort((short) 99));
        assertFalse(node.asShortOpt().isPresent());
    }

    void __invoke_shortValueFromNumberIntFailRangeVpack() throws Exception {
        try {
            shortValueFromNumberIntFailRangeVpack();
        } finally {
        }
    }


    void __invoke_shortValueFromNumberFPOkVpack() throws Exception {
        try {
            shortValueFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_shortValueFromNumberFPFailRangeVpack() throws Exception {
        try {
            shortValueFromNumberFPFailRangeVpack();
        } finally {
        }
    }


    void __invoke_shortValueFromNumberFPFailFractionVpack() throws Exception {
        try {
            shortValueFromNumberFPFailFractionVpack();
        } finally {
        }
    }


    void __invoke_shortValueFromNonNumberFailVpack() throws Exception {
        try {
            shortValueFromNonNumberFailVpack();
        } finally {
        }
    }


    void __invoke_asShortFromNumberIntOkVpack() throws Exception {
        try {
            asShortFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_asShortFromNumberIntFailRangeVpack() throws Exception {
        try {
            asShortFromNumberIntFailRangeVpack();
        } finally {
        }
    }


    void __invoke_asShortFromNumberFPOkVpack() throws Exception {
        try {
            asShortFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_asShortFromNumberFPFailRangeVpack() throws Exception {
        try {
            asShortFromNumberFPFailRangeVpack();
        } finally {
        }
    }


    void __invoke_asShortFromNumberFPWithFractionVpack() throws Exception {
        try {
            asShortFromNumberFPWithFractionVpack();
        } finally {
        }
    }


    void __invoke_asShortFromNonNumberScalarVpack() throws Exception {
        try {
            asShortFromNonNumberScalarVpack();
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
