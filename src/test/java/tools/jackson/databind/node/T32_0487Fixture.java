package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0487Fixture {
private static final byte[] STRING_HELLO = VPackWireFixtureTest.hex(
            "45 68 65 6c 6c 6f");
private static final byte[] NUMBER_42 = VPackWireFixtureTest.hex(
            "28 2a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] OBJECT_NAME_AGE = VPackWireFixtureTest.hex(
            "14 13 44 6e 61 6d 65 44 4a 6f 68 6e 43 61 67 65 28 1e 02");
private static final byte[] OBJECT_X_Y = VPackWireFixtureTest.hex(
            "14 0b 41 78 28 0a 41 79 28 14 02");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeMapTest#testMapWithStringNode().
    void testMapWithStringNodeVpack() throws Exception {
        JsonNode node = NODES.stringNode("hello");

        assertEquals("HELLO", node.map(n -> n.asString().toUpperCase()));
        Integer length = node.map(n -> n.asString().length());
        assertEquals(5, length);

        JsonNode wireNode = MAPPER.readTree(STRING_HELLO);
        assertEquals("HELLO", wireNode.map(n -> n.asString().toUpperCase()));
        length = wireNode.map(n -> n.asString().length());
        assertEquals(5, length);
    }

    // Provenance: JsonNodeMapTest#testMapWithNumberNode().
    void testMapWithNumberNodeVpack() throws Exception {
        JsonNode node = NODES.numberNode(42);

        Integer doubled = node.map(n -> n.asInt() * 2);
        assertEquals(84, doubled);
        JsonNode result = node.map(n -> NODES.stringNode(n.asString()));
        assertTrue(result.isString());
        assertEquals("42", result.stringValue());

        JsonNode wireNode = MAPPER.readTree(NUMBER_42);
        doubled = wireNode.map(n -> n.asInt() * 2);
        assertEquals(84, doubled);
        result = wireNode.map(n -> NODES.stringNode(n.asString()));
        assertTrue(result.isString());
        assertEquals("42", result.stringValue());
    }

    // Provenance: JsonNodeMapTest#testMapWithNullNode().
    void testMapWithNullNodeVpack() throws Exception {
        assertEquals("default", NODES.nullNode().map(
                n -> n.isNull() ? "default" : n.asString()));
        assertEquals("default", MAPPER.readTree(NULL).map(
                n -> n.isNull() ? "default" : n.asString()));
    }

    // Provenance: JsonNodeMapTest#testMapWithObjectNode().
    void testMapWithObjectNodeVpack() throws Exception {
        JsonNode node = MAPPER.readTree(OBJECT_NAME_AGE);
        assertEquals("John", node.map(n -> n.get("name").asString()));
    }

    // Provenance: JsonNodeMapTest#testMapWithComplexTransformation().
    void testMapWithComplexTransformationVpack() throws Exception {
        JsonNode node = MAPPER.readTree(OBJECT_X_Y);
        Point point = node.map(n -> new Point(
                n.get("x").asInt(),
                n.get("y").asInt()));

        assertEquals(10, point.x());
        assertEquals(20, point.y());
    }

    // Provenance: JsonNodeMapTest#testMapWithMissingNode().
    void testMapWithMissingNodeVpack() {
        JsonNode missingNode = NODES.missingNode();
        assertEquals("not found",
                missingNode.map(n -> n.isMissingNode() ? "not found" : n.asString()));
    }

    // Provenance: JsonNodeMapTest#testMissingAsWithMissingNode().
    void testMissingAsWithMissingNodeVpack() {
        JsonNode defaultNode = NODES.stringNode("default");
        JsonNode result = NODES.missingNode().missingAs(defaultNode);
        assertSame(defaultNode, result);
    }

    // Provenance: JsonNodeMapTest#testMissingAsWithNonMissingNode().
    void testMissingAsWithNonMissingNodeVpack() {
        JsonNode stringNode = NODES.stringNode("hello");
        JsonNode defaultNode = NODES.stringNode("default");

        assertSame(stringNode, stringNode.missingAs(defaultNode));
    }

    // Provenance: JsonNodeMapTest#testMissingAsSupplierWithMissingNode().
    void testMissingAsSupplierWithMissingNodeVpack() {
        JsonNode defaultNode = NODES.stringNode("supplied");
        JsonNode result = NODES.missingNode().missingAs(() -> defaultNode);
        assertSame(defaultNode, result);
    }

    // Provenance: JsonNodeMapTest#testMissingAsSupplierWithNonMissingNode().
    void testMissingAsSupplierWithNonMissingNodeVpack() {
        JsonNode stringNode = NODES.stringNode("hello");
        boolean[] supplierCalled = {false};

        JsonNode result = stringNode.missingAs(() -> {
            supplierCalled[0] = true;
            return NODES.stringNode("supplied");
        });

        assertSame(stringNode, result);
        assertFalse(supplierCalled[0], "Supplier should not be called for non-missing node");
    }

    // Provenance: JsonNodeMapTest#testMissingAsWithNullNode().
    void testMissingAsWithNullNodeVpack() {
        JsonNode nullNode = NODES.nullNode();
        JsonNode defaultNode = NODES.stringNode("default");

        JsonNode result = nullNode.missingAs(defaultNode);
        assertSame(nullNode, result);
    }

    // Provenance: JsonNodeMapTest#testNullAsAndMissingAsCombined().
    void testNullAsAndMissingAsCombinedVpack() {
        JsonNode defaultNode = NODES.stringNode("default");

        JsonNode nullNode = NODES.nullNode();
        JsonNode result1 = nullNode.nullAs(defaultNode).missingAs(NODES.stringNode("other"));
        assertSame(defaultNode, result1);

        JsonNode missingNode = NODES.missingNode();
        JsonNode result2 = missingNode.nullAs(NODES.stringNode("other")).missingAs(defaultNode);
        assertSame(defaultNode, result2);

        JsonNode regularNode = NODES.stringNode("regular");
        JsonNode result3 = regularNode.nullAs(NODES.stringNode("a")).missingAs(NODES.stringNode("b"));
        assertSame(regularNode, result3);
    }
private record Point(int x, int y) { }

    void __invoke_testMapWithStringNodeVpack() throws Exception {
        try {
            testMapWithStringNodeVpack();
        } finally {
        }
    }


    void __invoke_testMapWithNumberNodeVpack() throws Exception {
        try {
            testMapWithNumberNodeVpack();
        } finally {
        }
    }


    void __invoke_testMapWithNullNodeVpack() throws Exception {
        try {
            testMapWithNullNodeVpack();
        } finally {
        }
    }


    void __invoke_testMapWithObjectNodeVpack() throws Exception {
        try {
            testMapWithObjectNodeVpack();
        } finally {
        }
    }


    void __invoke_testMapWithComplexTransformationVpack() throws Exception {
        try {
            testMapWithComplexTransformationVpack();
        } finally {
        }
    }


    void __invoke_testMapWithMissingNodeVpack() throws Exception {
        try {
            testMapWithMissingNodeVpack();
        } finally {
        }
    }


    void __invoke_testMissingAsWithMissingNodeVpack() throws Exception {
        try {
            testMissingAsWithMissingNodeVpack();
        } finally {
        }
    }


    void __invoke_testMissingAsWithNonMissingNodeVpack() throws Exception {
        try {
            testMissingAsWithNonMissingNodeVpack();
        } finally {
        }
    }


    void __invoke_testMissingAsSupplierWithMissingNodeVpack() throws Exception {
        try {
            testMissingAsSupplierWithMissingNodeVpack();
        } finally {
        }
    }


    void __invoke_testMissingAsSupplierWithNonMissingNodeVpack() throws Exception {
        try {
            testMissingAsSupplierWithNonMissingNodeVpack();
        } finally {
        }
    }


    void __invoke_testMissingAsWithNullNodeVpack() throws Exception {
        try {
            testMissingAsWithNullNodeVpack();
        } finally {
        }
    }


    void __invoke_testNullAsAndMissingAsCombinedVpack() throws Exception {
        try {
            testNullAsAndMissingAsCombinedVpack();
        } finally {
        }
    }

}
