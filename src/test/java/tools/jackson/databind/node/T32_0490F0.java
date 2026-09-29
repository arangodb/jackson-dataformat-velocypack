package tools.jackson.databind.node;

import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.exc.JsonNodeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0490F0 {
private static final byte[] SHORT_MIN = VPackWireFixtureTest.hex("21 00 80");
private static final byte[] SHORT_MAX = VPackWireFixtureTest.hex("21 ff 7f");
private static final byte[] INT_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] INT_TWO = VPackWireFixtureTest.hex("32");
private static final byte[] INT_THREE = VPackWireFixtureTest.hex("33");
private static final byte[] INT_FOUR = VPackWireFixtureTest.hex("34");
private static final byte[] INT_TEN = VPackWireFixtureTest.hex("28 0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] BINARY_TWO_ZEROES = VPackWireFixtureTest.hex(
            "c0 02 00 00");
private static final byte[] TEXT_ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] ARRAY_ONE = VPackWireFixtureTest.hex("02 03 31");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] DOUBLE_QUARTER = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 3f");
private static final byte[] DOUBLE_NEGATIVE_2_125 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 01 c0");
private static final byte[] BCD_POINT_ONE = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff ff 01");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeShortValueTest#shortValueFromNumberIntOk().
    void shortValueFromNumberIntOkVpack() throws Exception {
        assertShortValue((short) 1, NODES.numberNode((short) 1));
        assertShortValue(Short.MIN_VALUE, NODES.numberNode(Short.MIN_VALUE));
        assertShortValue(Short.MAX_VALUE, NODES.numberNode(Short.MAX_VALUE));
        assertShortValue((short) 1, NODES.numberNode((byte) 1));
        assertShortValue(Byte.MIN_VALUE, NODES.numberNode(Byte.MIN_VALUE));
        assertShortValue(Byte.MAX_VALUE, NODES.numberNode(Byte.MAX_VALUE));
        assertShortValue((short) 1, NODES.numberNode(1));
        assertShortValue(Short.MIN_VALUE, NODES.numberNode((int) Short.MIN_VALUE));
        assertShortValue(Short.MAX_VALUE, NODES.numberNode((int) Short.MAX_VALUE));
        assertShortValue((short) 1, NODES.numberNode(1L));
        assertShortValue(Short.MIN_VALUE, NODES.numberNode((long) Short.MIN_VALUE));
        assertShortValue(Short.MAX_VALUE, NODES.numberNode((long) Short.MAX_VALUE));
        assertShortValue((short) 1, NODES.numberNode(BigInteger.ONE));
        assertShortValue(Short.MIN_VALUE,
                NODES.numberNode(BigInteger.valueOf(Short.MIN_VALUE)));
        assertShortValue(Short.MAX_VALUE,
                NODES.numberNode(BigInteger.valueOf(Short.MAX_VALUE)));

        assertShortValue(Short.MIN_VALUE, MAPPER.readTree(SHORT_MIN));
        assertShortValue(Short.MAX_VALUE, MAPPER.readTree(SHORT_MAX));
    }
private static void assertShortValue(short expected, JsonNode node) {
        assertEquals(expected, node.shortValue());
    }
private static void assertStringValueSuccess(JsonNode node) {
        assertEquals("abc", node.stringValue());
        assertEquals("abc", node.stringValue("xyz"));
        assertEquals("abc", node.stringValueOpt().get());
    }
private static void assertStringValueFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::stringValue);
        assertEquals(true, exception.getMessage().contains("cannot convert value"));
        assertEquals(true, exception.getMessage().contains("value type not String"));
        assertEquals("foo", node.stringValue("foo"));
        assertFalse(node.stringValueOpt().isPresent());
    }
private static void assertAsStringSuccess(String expected, JsonNode node) {
        assertEquals(expected, node.asString());
        assertEquals(expected, node.asString("fallback"));
        assertEquals(expected, node.asStringOpt().get());
    }
private static void assertAsStringFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asString);
        assertEquals(true, exception.getMessage().contains("cannot coerce value"));
        assertEquals(true, exception.getMessage().contains("value type not coercible"));
        assertEquals("foo", node.asString("foo"));
        assertFalse(node.asStringOpt().isPresent());
    }

    void __invoke_shortValueFromNumberIntOkVpack() throws Exception {
        try {
            shortValueFromNumberIntOkVpack();
        } finally {
        }
    }

}
