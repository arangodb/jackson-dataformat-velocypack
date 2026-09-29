package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.JsonNodeFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0488F0 {
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] STRING_HELLO = VPackWireFixtureTest.hex(
            "45 68 65 6c 6c 6f");
private static final byte[] INTEGER_42 = VPackWireFixtureTest.hex("28 2a");
private static final byte[] BYTE_127 = VPackWireFixtureTest.hex("28 7f");
private static final byte[] LONG_3456 = VPackWireFixtureTest.hex("29 80 0d");
private static final byte[] DOUBLE_NEGATIVE_2_125 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 01 c0");
private static final byte[] BCD_POINT_ONE = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff ff 01");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 01 02 03");
private static final byte[] BOOLEAN_TRUE = VPackWireFixtureTest.hex("19");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeMapTest#testNullAsWithNullNode().
    void testNullAsWithNullNodeVpack() throws Exception {
        JsonNode defaultNode = NODES.stringNode("default");
        assertSame(defaultNode, NODES.nullNode().nullAs(defaultNode));
        assertSame(defaultNode, MAPPER.readTree(NULL).nullAs(defaultNode));
    }

    // Provenance: JsonNodeMapTest#testNullAsWithNonNullNode().
    void testNullAsWithNonNullNodeVpack() throws Exception {
        JsonNode defaultNode = NODES.stringNode("default");
        JsonNode stringNode = NODES.stringNode("hello");
        assertSame(stringNode, stringNode.nullAs(defaultNode));

        JsonNode wireNode = MAPPER.readTree(STRING_HELLO);
        assertSame(wireNode, wireNode.nullAs(defaultNode));
    }

    // Provenance: JsonNodeMapTest#testNullAsSupplierWithNullNode().
    void testNullAsSupplierWithNullNodeVpack() throws Exception {
        JsonNode defaultNode = NODES.stringNode("supplied");
        assertSame(defaultNode, NODES.nullNode().nullAs(() -> defaultNode));
        assertSame(defaultNode, MAPPER.readTree(NULL).nullAs(() -> defaultNode));
    }

    // Provenance: JsonNodeMapTest#testNullAsSupplierWithNonNullNode().
    void testNullAsSupplierWithNonNullNodeVpack() throws Exception {
        JsonNode defaultNode = NODES.stringNode("supplied");
        JsonNode stringNode = NODES.stringNode("hello");
        boolean[] supplierCalled = {false};
        JsonNode result = stringNode.nullAs(() -> {
            supplierCalled[0] = true;
            return defaultNode;
        });
        assertSame(stringNode, result);
        assertFalse(supplierCalled[0], "Supplier should not be called for non-null node");

        JsonNode wireNode = MAPPER.readTree(STRING_HELLO);
        supplierCalled[0] = false;
        assertSame(wireNode, wireNode.nullAs(() -> {
            supplierCalled[0] = true;
            return defaultNode;
        }));
        assertFalse(supplierCalled[0], "Supplier should not be called for non-null VPack node");
    }

    // Provenance: JsonNodeMapTest#testNullAsWithMissingNode().
    void testNullAsWithMissingNodeVpack() {
        JsonNode missingNode = NODES.missingNode();
        JsonNode defaultNode = NODES.stringNode("default");
        assertSame(missingNode, missingNode.nullAs(defaultNode));
    }
private static void assertNonNumericIntValue(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::intValue);
        assertEquals(true, exception.getMessage().contains("cannot coerce value"));
        assertEquals(true, exception.getMessage().contains("value type not numeric"));
    }
private static void assertNaNFailure(JsonNode node) {
        assertThrows(JsonNodeException.class, node::asShort);
        assertEquals((short) 99, node.asShort((short) 99));
        assertFalse(node.asShortOpt().isPresent());
    }

    void __invoke_testNullAsWithNullNodeVpack() throws Exception {
        try {
            testNullAsWithNullNodeVpack();
        } finally {
        }
    }


    void __invoke_testNullAsWithNonNullNodeVpack() throws Exception {
        try {
            testNullAsWithNonNullNodeVpack();
        } finally {
        }
    }


    void __invoke_testNullAsSupplierWithNullNodeVpack() throws Exception {
        try {
            testNullAsSupplierWithNullNodeVpack();
        } finally {
        }
    }


    void __invoke_testNullAsSupplierWithNonNullNodeVpack() throws Exception {
        try {
            testNullAsSupplierWithNonNullNodeVpack();
        } finally {
        }
    }


    void __invoke_testNullAsWithMissingNodeVpack() throws Exception {
        try {
            testNullAsWithMissingNodeVpack();
        } finally {
        }
    }

}
