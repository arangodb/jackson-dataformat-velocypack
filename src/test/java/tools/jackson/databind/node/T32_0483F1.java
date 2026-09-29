package tools.jackson.databind.node;

import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0483F1 {
private static final float FLOAT_DEFAULT = -9999.5f;
private static final byte[] BYTE_MIN = VPackWireFixtureTest.hex("20 80");
private static final byte[] SHORT_MAX = VPackWireFixtureTest.hex("29 ff 7f");
private static final byte[] INT_MAX = VPackWireFixtureTest.hex("2b ff ff ff 7f");
private static final byte[] LONG_MIN = VPackWireFixtureTest.hex(
            "27 00 00 00 00 00 00 00 80");
private static final byte[] LONG_MAX = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff 7f");
private static final byte[] DOUBLE_1_25 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f4 3f");
private static final byte[] DOUBLE_TOO_BIG = VPackWireFixtureTest.hex(
            "1b a5 5c c3 f1 29 63 3d 48");
private static final byte[] DOUBLE_TOO_SMALL = VPackWireFixtureTest.hex(
            "1b a5 5c c3 f1 29 63 3d c8");
private static final byte[] BCD_1_25 = VPackWireFixtureTest.hex(
            "c8 02 fe ff ff ff 01 25");
private static final byte[] BCD_FLOAT_TOO_BIG = VPackWireFixtureTest.hex(
            "c8 01 36 01 00 00 01");
private static final byte[] BCD_FLOAT_TOO_SMALL = VPackWireFixtureTest.hex(
            "d0 01 36 01 00 00 01");
private static final byte[] BCD_INT_TOO_BIG = VPackWireFixtureTest.hex(
            "c8 06 00 00 00 00 12 34 56 78 90 00");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 01 02 03");
private static final byte[] TEXT_ABC = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] TEXT_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] ARRAY = VPackWireFixtureTest.hex(
            "13 05 31 32 02");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeIntValueTest#asIntFromNumberFPFailNaN().
    void asIntFromNumberFPFailNaNVpack() throws Exception {
        assertAsIntNaNFailure(NODES.numberNode(Float.NaN));
        assertAsIntNaNFailure(NODES.numberNode(Float.NEGATIVE_INFINITY));
        assertAsIntNaNFailure(NODES.numberNode(Float.POSITIVE_INFINITY));
        assertAsIntNaNFailure(NODES.numberNode(Double.NaN));
        assertAsIntNaNFailure(NODES.numberNode(Double.NEGATIVE_INFINITY));
        assertAsIntNaNFailure(NODES.numberNode(Double.POSITIVE_INFINITY));
        assertAsIntNaNFailure(MAPPER.readTree(NAN));
        assertAsIntNaNFailure(MAPPER.readTree(NEGATIVE_INFINITY));
        assertAsIntNaNFailure(MAPPER.readTree(POSITIVE_INFINITY));
    }

    // Provenance: JsonNodeIntValueTest#asIntFromNonNumberScalar().
    void asIntFromNonNumberScalarVpack() throws Exception {
        assertAsIntNonNumberFailure(NODES.booleanNode(true));
        assertAsIntNonNumberFailure(NODES.binaryNode(new byte[3]));
        assertAsIntNonNumberFailure(MAPPER.readTree(BINARY));
        assertAsIntNonNumberFailure(NODES.rawValueNode(new RawValue("abc")));
        assertAsIntNonNumberFailure(NODES.pojoNode(Boolean.TRUE));
        assertAsIntNonNumberFailure(NODES.stringNode("abc"),
                "value not a valid String representation of `int`");
        assertAsIntNonNumberFailure(MAPPER.readTree(TEXT_ABC),
                "value not a valid String representation of `int`");
        assertAsIntRangeFailure(NODES.pojoNode(123_456_789_000L));
        assertAsIntRangeFailure(MAPPER.readTree(BCD_INT_TOO_BIG));

        assertAsInt(123, NODES.stringNode("123"));
        assertAsInt(123, MAPPER.readTree(TEXT_123));
        assertAsInt(456, NODES.pojoNode(456L));
        assertAsInt(789, NODES.pojoNode(BigInteger.valueOf(789)));
    }

    // Provenance: JsonNodeIntValueTest#asIntFromMiscOther().
    void asIntFromMiscOtherVpack() throws Exception {
        assertAsIntNullLike(NODES.nullNode());
        assertAsIntNullLike(NODES.missingNode());
        assertAsIntNullLike(MAPPER.readTree(NULL));
    }
private static void assertFloatValue(float expected, JsonNode node) {
        assertEquals(expected, node.floatValue());
        assertEquals(expected, node.floatValue(FLOAT_DEFAULT));
        assertEquals(expected, node.floatValueOpt().orElseThrow());
    }
private static void assertFloatValueRangeFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::floatValue);
        assertEquals(true, exception.getMessage().contains("cannot convert value"));
        assertEquals(true, exception.getMessage().contains("value not in 32-bit `float` range"));
        assertEquals(-2.25f, node.floatValue(-2.25f));
        assertFalse(node.floatValueOpt().isPresent());
    }
private static void assertFloatValueNonNumberFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::floatValue);
        assertEquals(true, exception.getMessage().contains("cannot coerce value"));
        assertEquals(true, exception.getMessage().contains("value type not numeric"));
        assertEquals(-2.25f, node.floatValue(-2.25f));
        assertFalse(node.floatValueOpt().isPresent());
    }
private static void assertAsFloat(float expected, JsonNode node) {
        assertEquals(expected, node.asFloat());
        assertEquals(expected, node.asFloat(FLOAT_DEFAULT));
        assertEquals(expected, node.asFloatOpt().orElseThrow());
    }
private static void assertAsFloatNonNumberFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asFloat);
        assertEquals(true, exception.getMessage().contains("asFloat()"));
        assertEquals(true, exception.getMessage().contains("cannot coerce value"));
        assertEquals(true, exception.getMessage().contains("value type not coercible"));
        assertEquals(1.5f, node.asFloat(1.5f));
        assertFalse(node.asFloatOpt().isPresent());
    }
private static void assertAsInt(int expected, JsonNode node) {
        assertEquals(expected, node.asInt());
        assertEquals(expected, node.asInt(999_999));
        assertEquals(expected, node.asIntOpt().getAsInt());
    }
private static void assertAsIntRangeFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asInt);
        assertEquals(true, exception.getMessage().contains("asInt()"));
        assertEquals(true, exception.getMessage().contains("cannot convert value"));
        assertEquals(true, exception.getMessage().contains("value not in 32-bit `int` range"));
        assertEquals(99, node.asInt(99));
        assertFalse(node.asIntOpt().isPresent());
    }
private static void assertAsIntNonNumberFailure(JsonNode node) {
        assertAsIntNonNumberFailure(node, "value type not coercible");
    }
private static void assertAsIntNonNumberFailure(JsonNode node, String detail) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asInt);
        assertEquals(true, exception.getMessage().contains("asInt()"));
        assertEquals(true, exception.getMessage().contains("cannot coerce value"));
        assertEquals(true, exception.getMessage().contains(detail));
        assertEquals(99, node.asInt(99));
        assertFalse(node.asIntOpt().isPresent());
    }
private static void assertAsIntNaNFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asInt);
        assertEquals(true, exception.getMessage().contains("asInt()"));
        assertEquals(true, exception.getMessage().contains("cannot convert value"));
        assertEquals(true, exception.getMessage().contains("value non-Finite"));
        assertEquals(1, node.asInt(1));
        assertFalse(node.asIntOpt().isPresent());
    }
private static void assertAsIntNullLike(JsonNode node) {
        assertEquals(0, node.asInt());
        assertEquals(999_999, node.asInt(999_999));
        assertFalse(node.asIntOpt().isPresent());
    }

    void __invoke_asIntFromNumberFPFailNaNVpack() throws Exception {
        try {
            asIntFromNumberFPFailNaNVpack();
        } finally {
        }
    }


    void __invoke_asIntFromNonNumberScalarVpack() throws Exception {
        try {
            asIntFromNonNumberScalarVpack();
        } finally {
        }
    }


    void __invoke_asIntFromMiscOtherVpack() throws Exception {
        try {
            asIntFromMiscOtherVpack();
        } finally {
        }
    }

}
