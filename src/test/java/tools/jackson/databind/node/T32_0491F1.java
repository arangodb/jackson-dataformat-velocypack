package tools.jackson.databind.node;

import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0491F1 {
private static final byte[] POINTER_ROOT = VPackWireFixtureTest.hex(
            "0b 10 01 45 6e 75 6d 73 7e 02 06 28 2a 20 9d 03");
private static final byte[] ARRAY_NUMBERS = VPackWireFixtureTest.hex(
            "02 0a 28 0a 28 14 28 1e 28 28");
private static final byte[] CHAINED_OBJECT = VPackWireFixtureTest.hex(
            "0b 13 04 41 61 31 41 62 32 41 63 33 41 64 34 03 06 09 0c");
private static final byte[] ONE_PROPERTY = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 31 03");
private static final byte[] NESTED_VALUES_ARRAY = VPackWireFixtureTest.hex(
            "0b 1b 01 44 64 61 74 61 0b 12 01 46 76 61 6c 75 65 73 "
            + "02 07 31 32 33 34 35 03 03");
private static final byte[] NESTED_OBJECT = VPackWireFixtureTest.hex(
            "0b 1f 01 45 6f 75 74 65 72 0b 15 01 45 69 6e 6e 65 72 "
            + "0b 0b 01 44 64 65 65 70 28 2a 03 03 03");
private static final byte[] FIRST_LAST = VPackWireFixtureTest.hex(
            "06 18 03 45 66 69 72 73 74 46 6d 69 64 64 6c 65 "
            + "44 6c 61 73 74 03 09 10");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NESTED_ARRAY = VPackWireFixtureTest.hex(
            "0b 11 01 45 61 72 72 61 79 02 07 31 32 33 34 35 03");
private static final byte[] VALUE_NODE_ROOT = VPackWireFixtureTest.hex(
            "0b 0e 01 41 61 0b 08 01 41 62 28 7b 03 03");
private static final byte[] MIXED_ROOT = VPackWireFixtureTest.hex(
            "0b 39 01 45 49 6d 61 67 65 0b 2f 03 45 57 69 64 74 68 "
            + "29 20 03 46 48 65 69 67 68 74 29 58 02 43 49 44 73 "
            + "06 12 04 28 74 29 af 03 29 ea 00 29 89 97 03 05 08 0b "
            + "0c 16 03 03");
private final VPackMapper MAPPER = new VPackMapper();

    // Provenance: JsonPointerRemoval1981Test#testRemoveArrayElement().
    void testRemoveArrayElementVpack() throws Exception {
        ArrayNode array = (ArrayNode) MAPPER.readTree(ARRAY_NUMBERS);

        JsonNode removed = array.remove(JsonPointer.compile("/1"));
        assertEquals(20, removed.asInt());

        ArrayNode expected = MAPPER.createArrayNode().add(10).add(30).add(40);
        assertEquals(expected, array);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveChainedOperations().
    void testRemoveChainedOperationsVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(CHAINED_OBJECT);

        assertTrue(root.remove(JsonPointer.compile("/a")).isNumber());
        assertTrue(root.remove(JsonPointer.compile("/c")).isNumber());
        assertTrue(root.remove(JsonPointer.compile("/d")).isNumber());

        ObjectNode expected = MAPPER.createObjectNode().put("b", 2);
        assertEquals(expected, root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveEmptyPointer().
    void testRemoveEmptyPointerVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(ONE_PROPERTY);

        JsonNode removed = root.remove(JsonPointer.compile(""));
        assertTrue(removed.isMissingNode());
        assertEquals(MAPPER.createObjectNode().put("a", 1), root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveEntireArray().
    void testRemoveEntireArrayVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(NESTED_VALUES_ARRAY);

        JsonNode removed = root.remove(JsonPointer.compile("/data/values"));
        assertTrue(removed.isArray());
        assertEquals(5, removed.size());

        ObjectNode expected = MAPPER.createObjectNode();
        expected.set("data", MAPPER.createObjectNode());
        assertEquals(expected, root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveEntireObject().
    void testRemoveEntireObjectVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(NESTED_OBJECT);

        JsonNode removed = root.remove(JsonPointer.compile("/outer/inner"));
        assertTrue(removed.isObject());
        assertEquals(42, removed.path("deep").asInt());

        ObjectNode expected = MAPPER.createObjectNode();
        expected.set("outer", MAPPER.createObjectNode());
        assertEquals(expected, root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveFirstAndLastArrayElements().
    void testRemoveFirstAndLastArrayElementsVpack() throws Exception {
        ArrayNode array = (ArrayNode) MAPPER.readTree(FIRST_LAST);

        JsonNode removed = array.remove(JsonPointer.compile("/0"));
        assertEquals("first", removed.asString());
        assertEquals(2, array.size());
        assertEquals("middle", array.get(0).asString());

        removed = array.remove(JsonPointer.compile("/1"));
        assertEquals("last", removed.asString());
        assertEquals(1, array.size());
        assertEquals("middle", array.get(0).asString());
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveFromEmptyArray().
    void testRemoveFromEmptyArrayVpack() throws Exception {
        ArrayNode array = (ArrayNode) MAPPER.readTree(EMPTY_ARRAY);

        assertTrue(array.remove(JsonPointer.compile("/0")).isMissingNode());
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveFromEmptyObject().
    void testRemoveFromEmptyObjectVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(EMPTY_OBJECT);

        assertTrue(root.remove(JsonPointer.compile("/anything")).isMissingNode());
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveFromNestedArray().
    void testRemoveFromNestedArrayVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(NESTED_ARRAY);

        JsonNode removed = root.remove(JsonPointer.compile("/array/2"));
        assertEquals(3, removed.asInt());

        ObjectNode expected = MAPPER.createObjectNode();
        expected.set("array", MAPPER.createArrayNode().add(1).add(2).add(4).add(5));
        assertEquals(expected, root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveFromValueNode().
    void testRemoveFromValueNodeVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(VALUE_NODE_ROOT);

        JsonNode removed = root.remove(JsonPointer.compile("/a/b/c"));
        assertTrue(removed.isMissingNode());

        ObjectNode expected = MAPPER.createObjectNode();
        expected.set("a", MAPPER.createObjectNode().put("b", 123));
        assertEquals(expected, root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveMixedNestedStructure().
    void testRemoveMixedNestedStructureVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(MIXED_ROOT);

        JsonNode removed = root.remove(JsonPointer.compile("/Image/IDs/2"));
        assertEquals(234, removed.asInt());

        ObjectNode image = MAPPER.createObjectNode();
        image.put("Width", 800);
        image.put("Height", 600);
        image.set("IDs", MAPPER.createArrayNode().add(116).add(943).add(38793));
        ObjectNode expected = MAPPER.createObjectNode();
        expected.set("Image", image);
        assertEquals(expected, root);
    }

    void __invoke_testRemoveArrayElementVpack() throws Exception {
        try {
            testRemoveArrayElementVpack();
        } finally {
        }
    }


    void __invoke_testRemoveChainedOperationsVpack() throws Exception {
        try {
            testRemoveChainedOperationsVpack();
        } finally {
        }
    }


    void __invoke_testRemoveEmptyPointerVpack() throws Exception {
        try {
            testRemoveEmptyPointerVpack();
        } finally {
        }
    }


    void __invoke_testRemoveEntireArrayVpack() throws Exception {
        try {
            testRemoveEntireArrayVpack();
        } finally {
        }
    }


    void __invoke_testRemoveEntireObjectVpack() throws Exception {
        try {
            testRemoveEntireObjectVpack();
        } finally {
        }
    }


    void __invoke_testRemoveFirstAndLastArrayElementsVpack() throws Exception {
        try {
            testRemoveFirstAndLastArrayElementsVpack();
        } finally {
        }
    }


    void __invoke_testRemoveFromEmptyArrayVpack() throws Exception {
        try {
            testRemoveFromEmptyArrayVpack();
        } finally {
        }
    }


    void __invoke_testRemoveFromEmptyObjectVpack() throws Exception {
        try {
            testRemoveFromEmptyObjectVpack();
        } finally {
        }
    }


    void __invoke_testRemoveFromNestedArrayVpack() throws Exception {
        try {
            testRemoveFromNestedArrayVpack();
        } finally {
        }
    }


    void __invoke_testRemoveFromValueNodeVpack() throws Exception {
        try {
            testRemoveFromValueNodeVpack();
        } finally {
        }
    }


    void __invoke_testRemoveMixedNestedStructureVpack() throws Exception {
        try {
            testRemoveMixedNestedStructureVpack();
        } finally {
        }
    }

}
