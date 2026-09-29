package tools.jackson.databind.node;

import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0492F0 {
private static final byte[] NESTED_PROPERTY = VPackWireFixtureTest.hex(
            "14 1d 41 61 14 14 41 62 14 0b 41 63 28 0d 41 64 28 0e 02 "
            + "41 65 28 0f 02 41 66 28 10 02");
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "14 0c 41 61 31 41 62 32 41 63 33 03");
private static final byte[] TWO_ELEMENT_ARRAY = VPackWireFixtureTest.hex(
            "02 04 31 32");
private static final byte[] NESTED_PATH = VPackWireFixtureTest.hex(
            "14 0b 41 61 14 06 41 62 31 01 01");
private static final byte[] NULL_PROPERTY_OBJECT = VPackWireFixtureTest.hex(
            "14 1a 48 6e 75 6c 6c 50 72 6f 70 18 4a 6e 6f 72 6d 61 6c 50 72 6f 70 "
            + "28 2a 02");
private static final byte[] SPECIAL_PROPERTY_OBJECT = VPackWireFixtureTest.hex(
            "14 15 43 61 2f 62 31 43 63 7e 64 32 46 6e 6f 72 6d 61 6c 33 03");
private static final byte[] EMPTY_NAME_OBJECT = VPackWireFixtureTest.hex(
            "14 20 40 49 65 6d 70 74 79 20 6b 65 79 46 6e 6f 72 6d 61 6c "
            + "4a 6e 6f 72 6d 61 6c 20 6b 65 79 02");
private static final byte[] POINTER_TREE = VPackWireFixtureTest.hex(
            "0b 38 01 45 49 6d 61 67 65 0b 2e 03 45 57 69 64 74 68 29 20 03 "
            + "46 48 65 69 67 68 74 29 58 02 43 49 44 73 06 11 04 28 74 29 af 03 "
            + "28 ea 29 89 97 03 05 08 0a 0c 16 03 03");
private static final byte[] LONG_KEY_SMALL = VPackWireFixtureTest.hex(
            "14 0a 43 31 32 33 29 c8 01 01");
private static final byte[] LONG_KEY_LARGE = VPackWireFixtureTest.hex(
            "14 12 4b 33 35 33 36 31 37 30 36 30 34 35 29 d2 04 01");
private static final byte[] EMPTY_NAME_VALUE = VPackWireFixtureTest.hex(
            "14 06 40 28 7b 01");
private final VPackMapper MAPPER = new VPackMapper();

    // Provenance: JsonPointerRemoval1981Test#testRemoveNestedProperty().
    void testRemoveNestedPropertyVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(NESTED_PROPERTY);

        JsonNode removed = root.remove(JsonPointer.compile("/a/b/c"));
        assertEquals(13, removed.asInt());

        ObjectNode b = MAPPER.createObjectNode().put("d", 14);
        ObjectNode a = MAPPER.createObjectNode().set("b", b).put("e", 15);
        ObjectNode expected = MAPPER.createObjectNode().set("a", a).put("f", 16);
        assertEquals(expected, root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveNonExistentArrayIndex().
    void testRemoveNonExistentArrayIndexVpack() throws Exception {
        ArrayNode array = (ArrayNode) MAPPER.readTree(TWO_ELEMENT_ARRAY);

        assertTrue(array.remove(JsonPointer.compile("/10")).isMissingNode());
        assertTrue(array.remove(JsonPointer.compile("/a")).isMissingNode());
        assertEquals(MAPPER.createArrayNode().add(1).add(2), array);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveNonExistentNestedPath().
    void testRemoveNonExistentNestedPathVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(NESTED_PATH);

        assertTrue(root.remove(JsonPointer.compile("/a/x/y")).isMissingNode());
        assertEquals(MAPPER.createObjectNode().set("a",
                MAPPER.createObjectNode().put("b", 1)), root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveNonExistentProperty().
    void testRemoveNonExistentPropertyVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(SIMPLE_OBJECT);

        assertTrue(root.remove(JsonPointer.compile("/nonexistent")).isMissingNode());
        assertTrue(root.remove(JsonPointer.compile("/0")).isMissingNode());
        assertEquals(MAPPER.createObjectNode().put("a", 1).put("b", 2).put("c", 3), root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveNullValueProperty().
    void testRemoveNullValuePropertyVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(NULL_PROPERTY_OBJECT);

        JsonNode removed = root.remove(JsonPointer.compile("/nullProp"));
        assertTrue(removed.isNull());
        assertEquals(1, root.size());
        assertFalse(root.has("nullProp"));
        assertEquals(42, root.path("normalProp").asInt());
    }

    // Provenance: JsonPointerRemoval1981Test#testRemovePropertyWithSpecialCharacters().
    void testRemovePropertyWithSpecialCharactersVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(SPECIAL_PROPERTY_OBJECT);

        assertEquals(1, root.remove(JsonPointer.compile("/a~1b")).asInt());
        assertFalse(root.has("a/b"));
        assertEquals(2, root.remove(JsonPointer.compile("/c~0d")).asInt());
        assertFalse(root.has("c~d"));
        assertEquals(MAPPER.createObjectNode().put("normal", 3), root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveSimpleProperty().
    void testRemoveSimplePropertyVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(SIMPLE_OBJECT);

        assertEquals(2, root.remove(JsonPointer.compile("/b")).asInt());
        assertEquals(MAPPER.createObjectNode().put("a", 1).put("c", 3), root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveWithCompiledPointer().
    void testRemoveWithCompiledPointerVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(
                VPackWireFixtureTest.hex("14 0c 41 78 28 64 41 79 29 c8 00 02"));
        JsonPointer pointer = JsonPointer.compile("/x");

        assertEquals(100, root.remove(pointer).asInt());
        assertEquals(MAPPER.createObjectNode().put("y", 200), root);
    }

    // Provenance: JsonPointerRemoval1981Test#testRemoveWithEmptyStringPropertyName().
    void testRemoveWithEmptyStringPropertyNameVpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(EMPTY_NAME_OBJECT);

        assertEquals("empty key", root.remove(JsonPointer.compile("/")).asString());
        assertEquals(MAPPER.createObjectNode().put("normal", "normal key"), root);
    }

    void __invoke_testRemoveNestedPropertyVpack() throws Exception {
        try {
            testRemoveNestedPropertyVpack();
        } finally {
        }
    }


    void __invoke_testRemoveNonExistentArrayIndexVpack() throws Exception {
        try {
            testRemoveNonExistentArrayIndexVpack();
        } finally {
        }
    }


    void __invoke_testRemoveNonExistentNestedPathVpack() throws Exception {
        try {
            testRemoveNonExistentNestedPathVpack();
        } finally {
        }
    }


    void __invoke_testRemoveNonExistentPropertyVpack() throws Exception {
        try {
            testRemoveNonExistentPropertyVpack();
        } finally {
        }
    }


    void __invoke_testRemoveNullValuePropertyVpack() throws Exception {
        try {
            testRemoveNullValuePropertyVpack();
        } finally {
        }
    }


    void __invoke_testRemovePropertyWithSpecialCharactersVpack() throws Exception {
        try {
            testRemovePropertyWithSpecialCharactersVpack();
        } finally {
        }
    }


    void __invoke_testRemoveSimplePropertyVpack() throws Exception {
        try {
            testRemoveSimplePropertyVpack();
        } finally {
        }
    }


    void __invoke_testRemoveWithCompiledPointerVpack() throws Exception {
        try {
            testRemoveWithCompiledPointerVpack();
        } finally {
        }
    }


    void __invoke_testRemoveWithEmptyStringPropertyNameVpack() throws Exception {
        try {
            testRemoveWithEmptyStringPropertyNameVpack();
        } finally {
        }
    }

}
