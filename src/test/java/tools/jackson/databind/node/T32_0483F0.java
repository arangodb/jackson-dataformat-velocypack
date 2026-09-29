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

import tools.jackson.dataformat.velocypack.*;

class T32_0483F0 {
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

    // Provenance: JsonNodeFloatValueTest#floatValueFromNumberIntOk().
    void floatValueFromNumberIntOkVpack() throws Exception {
        assertFloatValue(1.0f, NODES.numberNode((byte) 1));
        assertFloatValue((float) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertFloatValue((float) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertFloatValue(1.0f, NODES.numberNode((short) 1));
        assertFloatValue((float) Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertFloatValue((float) Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertFloatValue(1.0f, NODES.numberNode(1));
        assertFloatValue((float) Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertFloatValue((float) Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));
        assertFloatValue(1.0f, NODES.numberNode(1L));
        assertFloatValue((float) Long.MIN_VALUE, NODES.numberNode(Long.MIN_VALUE));
        assertFloatValue((float) Long.MAX_VALUE, NODES.numberNode(Long.MAX_VALUE));
        assertFloatValue(1.0f, NODES.numberNode(BigInteger.ONE));
        assertFloatValue((float) Long.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MIN_VALUE)));
        assertFloatValue((float) Long.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MAX_VALUE)));

        assertFloatValue((float) Byte.MIN_VALUE, MAPPER.readTree(BYTE_MIN));
        assertFloatValue((float) Short.MAX_VALUE, MAPPER.readTree(SHORT_MAX));
        assertFloatValue((float) Integer.MAX_VALUE, MAPPER.readTree(INT_MAX));
        assertFloatValue((float) Long.MIN_VALUE, MAPPER.readTree(LONG_MIN));
        assertFloatValue((float) Long.MAX_VALUE, MAPPER.readTree(LONG_MAX));
    }

    // Provenance: JsonNodeFloatValueTest#failfloatValueFromNumberIntRange().
    void failfloatValueFromNumberIntRangeVpack() throws Exception {
        BigInteger tooBig = BigInteger.TEN.pow(310);
        assertFloatValueRangeFailure(NODES.numberNode(tooBig));
        assertFloatValueRangeFailure(NODES.numberNode(tooBig.negate()));
        assertFloatValueRangeFailure(MAPPER.readTree(BCD_FLOAT_TOO_BIG));
        assertFloatValueRangeFailure(MAPPER.readTree(BCD_FLOAT_TOO_SMALL));
    }

    // Provenance: JsonNodeFloatValueTest#floatValueFromNumberFPOk().
    void floatValueFromNumberFPOkVpack() throws Exception {
        assertFloatValue(1.0f, NODES.numberNode(1.0f));
        assertFloatValue(100_000.25f, NODES.numberNode(100_000.25f));
        assertFloatValue(-100_000.25f, NODES.numberNode(-100_000.25f));
        assertFloatValue(1.0f, NODES.numberNode(1.0d));
        assertFloatValue(100_000.25f, NODES.numberNode(100_000.25d));
        assertFloatValue(-100_000.25f, NODES.numberNode(-100_000.25d));
        assertFloatValue(1.25f, NODES.numberNode(BigDecimal.valueOf(1.25d)));
        assertFloatValue((float) Long.MIN_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Long.MIN_VALUE)));
        assertFloatValue((float) Long.MAX_VALUE,
                NODES.numberNode(BigDecimal.valueOf((double) Long.MAX_VALUE)));

        assertFloatValue(1.25f, MAPPER.readTree(DOUBLE_1_25));
        assertFloatValue(1.25f, MAPPER.readTree(BCD_1_25));
    }

    // Provenance: JsonNodeFloatValueTest#failFloatValueFromNumberFPRange().
    void failFloatValueFromNumberFPRangeVpack() throws Exception {
        double tooBig = 1e40;
        assertFloatValueRangeFailure(NODES.numberNode(tooBig));
        assertFloatValueRangeFailure(NODES.numberNode(-tooBig));

        BigDecimal tooBigDecimal = new BigDecimal(BigInteger.TEN.pow(50))
                .add(BigDecimal.valueOf(0.125));
        assertFloatValueRangeFailure(NODES.numberNode(tooBigDecimal));
        assertFloatValueRangeFailure(NODES.numberNode(tooBigDecimal.negate()));
        assertFloatValueRangeFailure(MAPPER.readTree(DOUBLE_TOO_BIG));
        assertFloatValueRangeFailure(MAPPER.readTree(DOUBLE_TOO_SMALL));
        assertFloatValueRangeFailure(MAPPER.readTree(BCD_FLOAT_TOO_BIG));
        assertFloatValueRangeFailure(MAPPER.readTree(BCD_FLOAT_TOO_SMALL));
    }

    // Provenance: JsonNodeFloatValueTest#failFloatValueFromNonNumberScalar().
    void failFloatValueFromNonNumberScalarVpack() throws Exception {
        assertFloatValueNonNumberFailure(NODES.booleanNode(true));
        assertFloatValueNonNumberFailure(NODES.binaryNode(new byte[3]));
        assertFloatValueNonNumberFailure(MAPPER.readTree(BINARY));
        assertFloatValueNonNumberFailure(NODES.stringNode("123"));
        assertFloatValueNonNumberFailure(NODES.rawValueNode(new RawValue("abc")));
        assertFloatValueNonNumberFailure(NODES.pojoNode(Boolean.TRUE));
        assertFloatValueNonNumberFailure(NODES.pojoNode(3.8f));
        assertFloatValueNonNumberFailure(MAPPER.readTree(TEXT_ABC));
    }

    // Provenance: JsonNodeFloatValueTest#failFloatValueFromStructural().
    void failFloatValueFromStructuralVpack() throws Exception {
        assertFloatValueNonNumberFailure(NODES.arrayNode(3));
        assertFloatValueNonNumberFailure(NODES.objectNode());
        assertFloatValueNonNumberFailure(MAPPER.readTree(ARRAY));
    }

    // Provenance: JsonNodeFloatValueTest#failFloatValueFromMiscOther().
    void failFloatValueFromMiscOtherVpack() throws Exception {
        assertFloatValueNonNumberFailure(NODES.nullNode());
        assertFloatValueNonNumberFailure(NODES.missingNode());
        assertFloatValueNonNumberFailure(MAPPER.readTree(NULL));
    }

    // Provenance: JsonNodeFloatValueTest#asFloatFromNumberIntOk().
    void asFloatFromNumberIntOkVpack() throws Exception {
        assertAsFloat(1.0f, NODES.numberNode((byte) 1));
        assertAsFloat((float) Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertAsFloat((float) Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertAsFloat(1.0f, NODES.numberNode((short) 1));
        assertAsFloat((float) Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertAsFloat((float) Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertAsFloat(1.0f, NODES.numberNode(1));
        assertAsFloat((float) Integer.MIN_VALUE, NODES.numberNode(Integer.MIN_VALUE));
        assertAsFloat((float) Integer.MAX_VALUE, NODES.numberNode(Integer.MAX_VALUE));
        assertAsFloat(1.0f, NODES.numberNode(1L));
        assertAsFloat((float) Long.MIN_VALUE, NODES.numberNode(Long.MIN_VALUE));
        assertAsFloat((float) Long.MAX_VALUE, NODES.numberNode(Long.MAX_VALUE));
        assertAsFloat(1.0f, NODES.numberNode(BigInteger.ONE));
        assertAsFloat((float) Long.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MIN_VALUE)));
        assertAsFloat((float) Long.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Long.MAX_VALUE)));

        assertAsFloat((float) Byte.MIN_VALUE, MAPPER.readTree(BYTE_MIN));
        assertAsFloat((float) Short.MAX_VALUE, MAPPER.readTree(SHORT_MAX));
        assertAsFloat((float) Integer.MAX_VALUE, MAPPER.readTree(INT_MAX));
        assertAsFloat((float) Long.MIN_VALUE, MAPPER.readTree(LONG_MIN));
        assertAsFloat((float) Long.MAX_VALUE, MAPPER.readTree(LONG_MAX));
    }

    // Provenance: JsonNodeFloatValueTest#asFloatFromStructuralFail().
    void asFloatFromStructuralFailVpack() throws Exception {
        assertAsFloatNonNumberFailure(NODES.arrayNode(3));
        assertAsFloatNonNumberFailure(NODES.objectNode());
        assertAsFloatNonNumberFailure(MAPPER.readTree(ARRAY));
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

    void __invoke_floatValueFromNumberIntOkVpack() throws Exception {
        try {
            floatValueFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_failfloatValueFromNumberIntRangeVpack() throws Exception {
        try {
            failfloatValueFromNumberIntRangeVpack();
        } finally {
        }
    }


    void __invoke_floatValueFromNumberFPOkVpack() throws Exception {
        try {
            floatValueFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_failFloatValueFromNumberFPRangeVpack() throws Exception {
        try {
            failFloatValueFromNumberFPRangeVpack();
        } finally {
        }
    }


    void __invoke_failFloatValueFromNonNumberScalarVpack() throws Exception {
        try {
            failFloatValueFromNonNumberScalarVpack();
        } finally {
        }
    }


    void __invoke_failFloatValueFromStructuralVpack() throws Exception {
        try {
            failFloatValueFromStructuralVpack();
        } finally {
        }
    }


    void __invoke_failFloatValueFromMiscOtherVpack() throws Exception {
        try {
            failFloatValueFromMiscOtherVpack();
        } finally {
        }
    }


    void __invoke_asFloatFromNumberIntOkVpack() throws Exception {
        try {
            asFloatFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_asFloatFromStructuralFailVpack() throws Exception {
        try {
            asFloatFromStructuralFailVpack();
        } finally {
        }
    }

}
