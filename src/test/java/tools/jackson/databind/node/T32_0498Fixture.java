package tools.jackson.databind.node;

import java.util.Arrays;

import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0498Fixture {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NUMERIC_OBJECT_ABC = VPackWireFixtureTest.hex(
            "14 0c 41 61 31 41 62 32 41 63 33 03");
private static final byte[] STRING_OBJECT_ABC = VPackWireFixtureTest.hex(
            "14 0f 41 61 41 61 41 62 41 62 41 63 41 63 03");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: ObjectNodeTest#testPutNumericSegmentOnExistingObject().
    void testPutNumericSegmentOnExistingObjectVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.putObject("obj");

        root.put(JsonPointer.compile("/obj/0"),
                mapper.getNodeFactory().stringNode("value"));

        assertTrue(root.get("obj").isObject());
        assertEquals("value", root.at("/obj/0").asString());
    }

    // Provenance: ObjectNodeTest#testPutWithJsonPointer().
    void testPutWithJsonPointerVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        JsonNode value = mapper.getNodeFactory().stringNode("test");

        ObjectNode result = root.put(JsonPointer.compile("/a/b/c"), value);

        assertSame(root, result);
        assertEquals("test", root.at("/a/b/c").asString());
    }

    // Provenance: ObjectNodeTest#testPutWithJsonPointerArrayIndex().
    void testPutWithJsonPointerArrayIndexVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.withArray("/arr").add("placeholder");

        root.put(JsonPointer.compile("/arr/0"),
                mapper.getNodeFactory().stringNode("replaced"));

        assertEquals("replaced", root.at("/arr/0").asString());
        assertEquals(1, root.at("/arr").size());
        assertEquals("replaced", root.at("/arr").get(0).asString());
    }

    // Provenance: ObjectNodeTest#testPutWithJsonPointerChaining().
    void testPutWithJsonPointerChainingVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);

        root.put(JsonPointer.compile("/a"), mapper.getNodeFactory().stringNode("A"))
                .put(JsonPointer.compile("/b"), mapper.getNodeFactory().stringNode("B"));

        assertEquals("A", root.get("a").asString());
        assertEquals("B", root.get("b").asString());
    }

    // Provenance: ObjectNodeTest#testPutWithJsonPointerNestedArrayIndex().
    void testPutWithJsonPointerNestedArrayIndexVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.withArray("/data/items").add("x").add("y");

        root.put(JsonPointer.compile("/data/items/1"),
                mapper.getNodeFactory().stringNode("updated"));

        assertEquals("updated", root.at("/data/items/1").asString());
        assertEquals("x", root.at("/data/items/0").asString());
        assertEquals(2, root.at("/data/items").size());
    }

    // Provenance: ObjectNodeTest#testPutWithJsonPointerNullValue().
    void testPutWithJsonPointerNullValueVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);

        root.put(JsonPointer.compile("/nullVal"), null);

        assertTrue(root.has("nullVal"));
        assertTrue(root.get("nullVal").isNull());
    }

    // Provenance: ObjectNodeTest#testPutWithJsonPointerOverwrite().
    void testPutWithJsonPointerOverwriteVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.put("key", "old");

        root.put(JsonPointer.compile("/key"),
                mapper.getNodeFactory().stringNode("new"));

        assertEquals("new", root.get("key").asString());
    }

    // Provenance: ObjectNodeTest#testPutWithJsonPointerRootFails().
    void testPutWithJsonPointerRootFailsVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);

        JsonNodeException failure = assertThrows(JsonNodeException.class,
                () -> root.put(JsonPointer.empty(), mapper.createObjectNode()));
        assertTrue(failure.getMessage().contains("empty `JsonPointer`"));
    }

    // Provenance: ObjectNodeTest#testPutWithNumericPropertyName().
    void testPutWithNumericPropertyNameVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);

        root.put(JsonPointer.compile("/0"),
                mapper.getNodeFactory().stringNode("value"));

        assertTrue(root.has("0"));
        assertEquals("value", root.get("0").asString());
    }

    // Provenance: ObjectNodeTest#testRemove().
    void testRemoveVpack() throws Exception {
        ObjectNode object = (ObjectNode) mapper.readTree(STRING_OBJECT_ABC);

        assertEquals(3, object.size());
        assertSame(object, object.without(Arrays.asList("a", "c")));
        assertEquals(1, object.size());
        assertEquals("b", object.get("b").asString());
    }

    // Provenance: ObjectNodeTest#testRemoveAll().
    void testRemoveAllVpack() throws Exception {
        ObjectNode object = (ObjectNode) mapper.readTree(NUMERIC_OBJECT_ABC);

        assertEquals(mapper.createObjectNode(), object.removeAll());
    }

    // Provenance: ObjectNodeTest#testRemoveIf().
    void testRemoveIfVpack() throws Exception {
        ObjectNode first = (ObjectNode) mapper.readTree(NUMERIC_OBJECT_ABC);
        assertEquals(mapper.createObjectNode().put("c", 3),
                first.removeIf(value -> value.asInt() <= 2));

        ObjectNode second = (ObjectNode) mapper.readTree(NUMERIC_OBJECT_ABC);
        assertEquals(mapper.createObjectNode().put("a", 1),
                second.removeIf(value -> value.asInt() > 1));
    }

    void __invoke_testPutNumericSegmentOnExistingObjectVpack() throws Exception {
        try {
            testPutNumericSegmentOnExistingObjectVpack();
        } finally {
        }
    }


    void __invoke_testPutWithJsonPointerVpack() throws Exception {
        try {
            testPutWithJsonPointerVpack();
        } finally {
        }
    }


    void __invoke_testPutWithJsonPointerArrayIndexVpack() throws Exception {
        try {
            testPutWithJsonPointerArrayIndexVpack();
        } finally {
        }
    }


    void __invoke_testPutWithJsonPointerChainingVpack() throws Exception {
        try {
            testPutWithJsonPointerChainingVpack();
        } finally {
        }
    }


    void __invoke_testPutWithJsonPointerNestedArrayIndexVpack() throws Exception {
        try {
            testPutWithJsonPointerNestedArrayIndexVpack();
        } finally {
        }
    }


    void __invoke_testPutWithJsonPointerNullValueVpack() throws Exception {
        try {
            testPutWithJsonPointerNullValueVpack();
        } finally {
        }
    }


    void __invoke_testPutWithJsonPointerOverwriteVpack() throws Exception {
        try {
            testPutWithJsonPointerOverwriteVpack();
        } finally {
        }
    }


    void __invoke_testPutWithJsonPointerRootFailsVpack() throws Exception {
        try {
            testPutWithJsonPointerRootFailsVpack();
        } finally {
        }
    }


    void __invoke_testPutWithNumericPropertyNameVpack() throws Exception {
        try {
            testPutWithNumericPropertyNameVpack();
        } finally {
        }
    }


    void __invoke_testRemoveVpack() throws Exception {
        try {
            testRemoveVpack();
        } finally {
        }
    }


    void __invoke_testRemoveAllVpack() throws Exception {
        try {
            testRemoveAllVpack();
        } finally {
        }
    }


    void __invoke_testRemoveIfVpack() throws Exception {
        try {
            testRemoveIfVpack();
        } finally {
        }
    }

}
