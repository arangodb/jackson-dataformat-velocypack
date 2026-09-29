package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.JsonNodeFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0486F1 {
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

    // Provenance: JsonNodeMapTest#testMapWithBooleanNode().
    void testMapWithBooleanNodeVpack() throws Exception {
        assertEquals("yes",
                NODES.booleanNode(true).map(n -> n.asBoolean() ? "yes" : "no"));
        assertEquals("no",
                NODES.booleanNode(false).map(n -> n.asBoolean() ? "yes" : "no"));
        assertEquals("yes",
                MAPPER.readTree(VPackWireFixtureTest.hex("1a"))
                        .map(n -> n.asBoolean() ? "yes" : "no"));
        assertEquals("no",
                MAPPER.readTree(VPackWireFixtureTest.hex("19"))
                        .map(n -> n.asBoolean() ? "yes" : "no"));
    }

    // Provenance: JsonNodeMapTest#testMapWithArrayNode().
    void testMapWithArrayNodeVpack() throws Exception {
        JsonNode node = MAPPER.readTree(ARRAY);
        Integer size = node.map(n -> n.size());
        assertEquals(5, size);
    }

    // Provenance: JsonNodeMapTest#testMapReturningNull().
    void testMapReturningNullVpack() throws Exception {
        String result = NODES.stringNode("test").map(n -> null);
        assertNull(result);

        String parsedResult = MAPPER.readTree(TEXT_TEST).map(n -> null);
        assertNull(parsedResult);
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

    void __invoke_testMapWithBooleanNodeVpack() throws Exception {
        try {
            testMapWithBooleanNodeVpack();
        } finally {
        }
    }


    void __invoke_testMapWithArrayNodeVpack() throws Exception {
        try {
            testMapWithArrayNodeVpack();
        } finally {
        }
    }


    void __invoke_testMapReturningNullVpack() throws Exception {
        try {
            testMapReturningNullVpack();
        } finally {
        }
    }

}
