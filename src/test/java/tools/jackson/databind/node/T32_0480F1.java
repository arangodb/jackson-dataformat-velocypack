package tools.jackson.databind.node;

import java.math.BigDecimal;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.exc.JsonNodeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0480F1 {
private static final BigDecimal BD_DEFAULT = new BigDecimal("12.125");
private static final double DOUBLE_DEFAULT = -9999.5d;
private static final byte[] INT_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] MAX_UNSIGNED = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff ff");
private static final byte[] DOUBLE_100000 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 6a f8 40");
private static final byte[] HUGE_BCD = VPackWireFixtureTest.hex(
            "c8 01 35 01 00 00 02");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 00 03 03");
private static final byte[] TEXT_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] TEXT_HALF = VPackWireFixtureTest.hex(
            "43 30 2e 35");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeDeepCopyTest#testWithObjectSimple().
    void testWithObjectSimpleVpack() {
        ObjectNode root = NODES.objectNode();
        root.put("a", 3);
        assertEquals(1, root.size());

        ObjectNode copy = root.deepCopy();
        assertEquals(1, copy.size());

        root.put("b", 7);
        assertEquals(2, root.size());
        assertEquals(1, copy.size());

        copy.put("c", 3);
        assertEquals(2, root.size());
        assertEquals(2, copy.size());
    }

    // Provenance: JsonNodeDeepCopyTest#testWithArraySimple().
    void testWithArraySimpleVpack() {
        ArrayNode root = NODES.arrayNode();
        root.add("a");
        assertEquals(1, root.size());

        ArrayNode copy = root.deepCopy();
        assertEquals(1, copy.size());

        root.add(7);
        assertEquals(2, root.size());
        assertEquals(1, copy.size());

        copy.add(3);
        assertEquals(2, root.size());
        assertEquals(2, copy.size());
    }

    // Provenance: JsonNodeDeepCopyTest#testWithNested().
    void testWithNestedVpack() {
        ObjectNode root = NODES.objectNode();
        ObjectNode leafObject = root.putObject("ob");
        ArrayNode leafArray = root.putArray("arr");
        assertEquals(2, root.size());

        leafObject.put("a", 3);
        assertEquals(1, leafObject.size());
        leafArray.add(true);
        assertEquals(1, leafArray.size());

        ObjectNode copy = root.deepCopy();
        assertNotSame(copy, root);
        assertEquals(2, copy.size());

        leafObject.put("x", 9);
        assertEquals(2, leafObject.size());
        assertEquals(1, copy.get("ob").size());

        leafArray.add("foobar");
        assertEquals(2, leafArray.size());
        assertEquals(1, copy.get("arr").size());

        ((ObjectNode) copy.get("ob")).put("c", 3);
        assertEquals(2, leafObject.size());
        assertEquals(2, copy.get("ob").size());

        ((ArrayNode) copy.get("arr")).add(13);
        assertEquals(2, leafArray.size());
        assertEquals(2, copy.get("arr").size());
    }
private static void assertDecimalValue(BigDecimal expected, JsonNode node) {
        assertEquals(expected, node.decimalValue());
        assertEquals(expected, node.decimalValue(BD_DEFAULT));
        assertEquals(expected, node.decimalValueOpt().orElseThrow());
    }
private static void assertFailDecimalValueForNonNumber(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::decimalValue);
        assertTrue(exception.getMessage().contains("decimalValue()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not numeric"));
        assertEquals(BD_DEFAULT, node.decimalValue(BD_DEFAULT));
        assertFalse(node.decimalValueOpt().isPresent());
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
        assertFailAsDoubleForNonNumber(node, "value type not coercible");
    }
private static void assertFailAsDoubleForNonNumber(JsonNode node, String expectedMessage) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asDouble);
        assertTrue(exception.getMessage().contains("asDouble()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains(expectedMessage));
        assertEquals(1.5d, node.asDouble(1.5d));
        assertFalse(node.asDoubleOpt().isPresent());
    }

    void __invoke_testWithObjectSimpleVpack() throws Exception {
        try {
            testWithObjectSimpleVpack();
        } finally {
        }
    }


    void __invoke_testWithArraySimpleVpack() throws Exception {
        try {
            testWithArraySimpleVpack();
        } finally {
        }
    }


    void __invoke_testWithNestedVpack() throws Exception {
        try {
            testWithNestedVpack();
        } finally {
        }
    }

}
