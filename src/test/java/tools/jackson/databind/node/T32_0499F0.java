package tools.jackson.databind.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0499F0 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] STRING_OBJECT_ABC = VPackWireFixtureTest.hex(
            "14 0f 41 61 41 61 41 62 41 62 41 63 41 63 03");
private static final byte[] NULLS_AND_NUMBER = VPackWireFixtureTest.hex(
            "14 0c 41 61 18 41 62 32 41 63 18 03");
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "14 0c 43 6b 65 79 31 41 62 41 78 02");
private static final byte[] SIMPLE_PATH = VPackWireFixtureTest.hex(
            "14 11 47 72 65 73 75 6c 74 73 14 06 41 61 33 01 01");
private static final byte[] ARRAY_123 = VPackWireFixtureTest.hex(
            "02 05 31 32 33");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: ObjectNodeTest#testRemoveNulls().
    void testRemoveNullsVpack() throws Exception {
        ObjectNode expected = mapper.createObjectNode().put("b", 2);
        ObjectNode actual = (ObjectNode) mapper.readTree(NULLS_AND_NUMBER);

        assertEquals(expected, actual.removeNulls());
    }

    // Provenance: ObjectNodeTest#testRetain().
    void testRetainVpack() throws Exception {
        ObjectNode object = (ObjectNode) mapper.readTree(STRING_OBJECT_ABC);

        assertEquals(3, object.size());
        assertSame(object, object.retain("a", "c"));
        assertEquals(2, object.size());
        assertEquals("a", object.get("a").stringValue());
        assertNull(object.get("b"));
        assertEquals("c", object.get("c").stringValue());
    }

    // Provenance: ObjectNodeTest#testSetAll().
    void testSetAllVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        assertEquals(0, root.size());

        Map<String, JsonNode> values = new java.util.HashMap<>();
        values.put("a", root.numberNode(1));
        root.setAll(values);
        assertEquals(1, root.size());
        assertTrue(root.has("a"));
        assertFalse(root.has("b"));

        values.put("b", root.numberNode(2));
        root.setAll(values);
        assertEquals(2, root.size());
        assertTrue(root.has("a"));
        assertTrue(root.has("b"));
        assertEquals(2, root.path("b").intValue());

        ObjectNode root2 = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root2.setAll(root);
        assertEquals(2, root.size());
        assertEquals(2, root2.size());

        root2.setAll(root);
        assertEquals(2, root.size());
        assertEquals(2, root2.size());

        ObjectNode root3 = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root3.put("a", 2);
        root3.put("c", 3);
        assertEquals(2, root3.path("a").intValue());
        root3.setAll(root2);
        assertEquals(3, root3.size());
        assertEquals(1, root3.path("a").intValue());
    }

    // Provenance: ObjectNodeTest#testSimpleMismatch().
    void testSimpleMismatchVpack() {
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(ARRAY_123, ObjectNode.class));
    }

    // Provenance: ObjectNodeTest#testSimpleObject().
    void testSimpleObjectVpack() throws Exception {
        JsonNode root = mapper.readTree(SIMPLE_OBJECT);

        assertFalse(root.isValueNode());
        assertTrue(root.isContainer());
        assertFalse(root.isArray());
        assertTrue(root.isObject());
        assertEquals(2, root.size());
        assertFalse(root.isEmpty());

        assertFalse(root.isBoolean());
        assertFalse(root.isString());
        assertFalse(root.isNumber());
        assertFalse(root.canConvertToInt());
        assertFalse(root.canConvertToLong());
        assertFalse(root.canConvertToExactIntegral());

        java.util.Iterator<JsonNode> values = root.iterator();
        assertNotNull(values);
        assertTrue(values.hasNext());
        assertEquals(1, values.next().intValue());
        assertTrue(values.hasNext());
        assertEquals("x", values.next().stringValue());
        assertFalse(values.hasNext());

        ObjectNode object = (ObjectNode) root;
        java.util.Iterator<Map.Entry<String, JsonNode>> properties = object.properties().iterator();
        assertTrue(properties.hasNext());
        Map.Entry<String, JsonNode> first = properties.next();
        assertEquals("key", first.getKey());
        assertEquals(1, first.getValue().intValue());
        assertTrue(properties.hasNext());
        Map.Entry<String, JsonNode> second = properties.next();
        assertEquals("b", second.getKey());
        assertEquals("x", second.getValue().stringValue());
        assertFalse(properties.hasNext());
    }

    // Provenance: ObjectNodeTest#testSimplePath().
    void testSimplePathVpack() throws Exception {
        JsonNode root = mapper.readTree(SIMPLE_PATH);
        assertTrue(root.isObject());
        JsonNode result = root.path("results");
        assertNotNull(result);
        assertTrue(result.isObject());
        assertEquals(3, result.path("a").intValue());
    }

    // Provenance: ObjectNodeTest#testStreamMethods().
    void testStreamMethodsVpack() throws Exception {
        ObjectNode object = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        JsonNode n1 = object.numberNode(42);
        JsonNode n2 = object.stringNode("foo");
        object.set("a", n1);
        object.set("b", n2);

        assertEquals(2, object.valueStream().count());
        assertEquals(Arrays.asList(n1, n2), object.valueStream().toList());
        assertEquals(2, object.propertyStream().count());
        assertEquals(new ArrayList<>(object.properties()),
                object.propertyStream().toList());

        LinkedHashMap<String, JsonNode> map = new LinkedHashMap<>();
        object.forEachEntry(map::put);
        assertEquals(object.properties(), map.entrySet());
    }

    // Provenance: ObjectNodeTest#testValidWithArray().
    void testValidWithArrayVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        ArrayNode child = root.withArray("/arr");

        assertInstanceOf(ArrayNode.class, child);
        assertEquals(1, root.size());
        assertTrue(root.get("arr").isArray());
        assertEquals(0, child.size());
    }

    // Provenance: ObjectNodeTest#testValidWithObject().
    void testValidWithObjectVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        JsonNode child = root.withObject("/prop");

        assertInstanceOf(ObjectNode.class, child);
        assertEquals(1, root.size());
        assertTrue(root.get("prop").isObject());
        assertEquals(0, child.size());
    }

    void __invoke_testRemoveNullsVpack() throws Exception {
        try {
            testRemoveNullsVpack();
        } finally {
        }
    }


    void __invoke_testRetainVpack() throws Exception {
        try {
            testRetainVpack();
        } finally {
        }
    }


    void __invoke_testSetAllVpack() throws Exception {
        try {
            testSetAllVpack();
        } finally {
        }
    }


    void __invoke_testSimpleMismatchVpack() throws Exception {
        try {
            testSimpleMismatchVpack();
        } finally {
        }
    }


    void __invoke_testSimpleObjectVpack() throws Exception {
        try {
            testSimpleObjectVpack();
        } finally {
        }
    }


    void __invoke_testSimplePathVpack() throws Exception {
        try {
            testSimplePathVpack();
        } finally {
        }
    }


    void __invoke_testStreamMethodsVpack() throws Exception {
        try {
            testStreamMethodsVpack();
        } finally {
        }
    }


    void __invoke_testValidWithArrayVpack() throws Exception {
        try {
            testValidWithArrayVpack();
        } finally {
        }
    }


    void __invoke_testValidWithObjectVpack() throws Exception {
        try {
            testValidWithObjectVpack();
        } finally {
        }
    }

}
