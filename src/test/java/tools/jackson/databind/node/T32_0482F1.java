package tools.jackson.databind.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.exc.JsonNodeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0482F1 {
private static final float FLOAT_DEFAULT = -9999.5f;
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "14 09 41 62 32 41 61 31 02");
private static final byte[] BIGGER_ARRAY = VPackWireFixtureTest.hex(
            "13 12 41 78 14 0c 41 62 31 41 63 1a 41 61 33 03 19 03");
private static final byte[] JSON_SAMPLE = VPackWireFixtureTest.hex(
            "14 33 41 61 14 0a 45 76 61 6c 75 65 33 01"
          + "45 61 72 72 61 79 13 1e"
          + "14 06 41 62 33 01"
          + "14 0b 45 76 61 6c 75 65 28 2a 01"
          + "14 0a 45 6f 74 68 65 72 1a 01 03 02");
private static final byte[] JSON_4229 = VPackWireFixtureTest.hex(
            "0b 66 03"
          + "46 74 61 72 67 65 74 47 74 61 72 67 65 74 31"
          + "47 6f 62 6a 65 63 74 31"
          + "0b 13 01 46 74 61 72 67 65 74 47 74 61 72 67 65 74 32 03"
          + "47 6f 62 6a 65 63 74 32"
          + "0b 2e 01 46 74 61 72 67 65 74"
          + "0b 23 01 46 74 61 72 67 65 74"
          + "57 69 67 6e 6f 72 65 64 41 73 50 61 72 65 6e 74 49 73 54 61 72 67 65 74 03"
          + "03 12 2d 03");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 01 02 03");
private static final byte[] TEXT_ABC = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] TEXT_HALF = VPackWireFixtureTest.hex(
            "43 30 2e 35");
private static final byte[] DOUBLE_1_5 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] HUGE_BCD_POSITIVE = VPackWireFixtureTest.hex(
            "c8 01 36 01 00 00 01");
private static final byte[] HUGE_BCD_NEGATIVE = VPackWireFixtureTest.hex(
            "d0 01 36 01 00 00 01");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeFindMethodsTest#testFindParents4229().
    void testFindParents4229Vpack() throws Exception {
        JsonNode rootNode = MAPPER.readTree(JSON_4229);
        assertEquals(List.of(
                rootNode,
                rootNode.at("/object1"),
                rootNode.at("/object2")),
                rootNode.findParents("target"));
    }

    // Provenance: JsonNodeFindMethodsTest#testFindValues4229().
    void testFindValues4229Vpack() throws Exception {
        JsonNode rootNode = MAPPER.readTree(JSON_4229);
        assertEquals(List.of(
                rootNode.at("/target"),
                rootNode.at("/object1/target"),
                rootNode.at("/object2/target")),
                rootNode.findValues("target"));
    }

    // Provenance: JsonNodeFindMethodsTest#testMatchingMultiple().
    void testMatchingMultipleVpack() throws Exception {
        JsonNode root = MAPPER.readTree(JSON_SAMPLE);

        List<JsonNode> nodes = root.findValues("value");
        assertEquals(2, nodes.size());
        assertEquals(3, nodes.get(0).intValue());
        assertEquals(42, nodes.get(1).intValue());

        nodes = root.findParents("value");
        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0).isObject());
        assertTrue(nodes.get(1).isObject());
        assertEquals(3, nodes.get(0).path("value").intValue());
        assertEquals(42, nodes.get(1).path("value").intValue());

        List<String> values = root.findValuesAsString("value");
        assertEquals(List.of("3", "42"), values);
    }

    // Provenance: JsonNodeFindMethodsTest#testMatchingSingle().
    void testMatchingSingleVpack() throws Exception {
        JsonNode root = MAPPER.readTree(JSON_SAMPLE);

        JsonNode node = root.findValue("b");
        assertNotNull(node);
        assertEquals(3, node.intValue());
        node = root.findParent("b");
        assertNotNull(node);
        assertTrue(node.isObject());
        assertEquals(1, ((ObjectNode) node).size());
        assertEquals(3, node.path("b").intValue());
    }

    // Provenance: JsonNodeFindMethodsTest#testNonMatching().
    void testNonMatchingVpack() throws Exception {
        JsonNode root = MAPPER.readTree(JSON_SAMPLE);

        assertNull(root.findValue("boogaboo"));
        assertNull(root.findParent("boogaboo"));
        JsonNode n = root.findPath("boogaboo");
        assertNotNull(n);
        assertTrue(n.isMissingNode());
        assertTrue(root.findValues("boogaboo").isEmpty());
        assertTrue(root.findParents("boogaboo").isEmpty());
    }
private static List<String> propertyNames(ObjectNode object) {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, JsonNode> entry : object.properties()) {
            names.add(entry.getKey());
        }
        return names;
    }
private static void assertAsFloat(float expected, JsonNode node) {
        assertEquals(expected, node.asFloat());
        assertEquals(expected, node.asFloat(FLOAT_DEFAULT));
        assertEquals(expected, node.asFloatOpt().orElseThrow());
    }
private static void assertAsFloatNullLike(JsonNode node) {
        assertEquals(0.0f, node.asFloat());
        assertEquals(FLOAT_DEFAULT, node.asFloat(FLOAT_DEFAULT));
        assertFalse(node.asFloatOpt().isPresent());
    }
private static void assertAsFloatFailForValueRange(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asFloat);
        assertTrue(exception.getMessage().contains("asFloat()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 32-bit `float` range"));
        assertEquals(-2.25f, node.asFloat(-2.25f));
        assertFalse(node.asFloatOpt().isPresent());
    }
private static void assertAsFloatFailForNonNumber(JsonNode node) {
        assertAsFloatFailForNonNumber(node, "value type not coercible");
    }
private static void assertAsFloatFailForNonNumber(JsonNode node, String extraMatch) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asFloat);
        assertTrue(exception.getMessage().contains("asFloat()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains(extraMatch));
        assertEquals(1.5f, node.asFloat(1.5f));
        assertFalse(node.asFloatOpt().isPresent());
    }
private static class SortingNodeFactory extends JsonNodeFactory {
        private static final long serialVersionUID = 1L;

        @Override
        public ObjectNode objectNode() {
            return new ObjectNode(this, new TreeMap<String, JsonNode>());
        }
    }

    void __invoke_testFindParents4229Vpack() throws Exception {
        try {
            testFindParents4229Vpack();
        } finally {
        }
    }


    void __invoke_testFindValues4229Vpack() throws Exception {
        try {
            testFindValues4229Vpack();
        } finally {
        }
    }


    void __invoke_testMatchingMultipleVpack() throws Exception {
        try {
            testMatchingMultipleVpack();
        } finally {
        }
    }


    void __invoke_testMatchingSingleVpack() throws Exception {
        try {
            testMatchingSingleVpack();
        } finally {
        }
    }


    void __invoke_testNonMatchingVpack() throws Exception {
        try {
            testNonMatchingVpack();
        } finally {
        }
    }

}
