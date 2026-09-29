package tools.jackson.databind.node;

import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.JsonNode.OverwriteMode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0507Fixture {
private static final byte[] OBJECT_EXISTING = VPackWireFixtureTest.hex(
            "14 14 41 61 14 0f 41 62 28 2a 41 63 14 06 41 78 31 01 02 01");
private static final byte[] OBJECT_MODIFY = VPackWireFixtureTest.hex(
            "14 0c 41 61 14 07 41 62 28 2a 01 01");
private static final byte[] ARRAY_SIMPLE = VPackWireFixtureTest.hex(
            "14 12 41 61 14 0d 41 62 13 05 31 32 02 41 63 1a 02 01");
private static final byte[] OBJECT_WITH_ARRAY = VPackWireFixtureTest.hex(
            "14 08 43 61 72 72 01 01");
private static final byte[] OBJECT_WITH_NULL = VPackWireFixtureTest.hex(
            "14 06 41 62 18 01");
private static final byte[] OBJECT_WITH_NUMBER = VPackWireFixtureTest.hex(
            "14 07 41 63 28 7b 01");
private final ObjectMapper mapper = new VPackMapper();

    // Provenance: WithPathTest#testValidWithObjectTrivial().
    void testValidWithObjectTrivialVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
        assertSame(root, root.withObject(JsonPointer.empty()));
    }

    // Provenance: WithPathTest#testValidWithObjectSimpleExisting().
    void testValidWithObjectSimpleExistingVpack() throws Exception {
        JsonNode doc = mapper.readTree(OBJECT_EXISTING);
        String before = doc.toString();
        for (boolean compiled : new boolean[] { true, false }) {
            ObjectNode match = compiled
                    ? doc.withObject(JsonPointer.compile("/a"))
                    : doc.withObject("/a");
            assertEquals("{\"b\":42,\"c\":{\"x\":1}}", match.toString());
            assertEquals(before, doc.toString());

            match = compiled
                    ? doc.withObject(JsonPointer.compile("/a/c"))
                    : doc.withObject("/a/c");
            assertEquals("{\"x\":1}", match.toString());
            assertEquals(before, doc.toString());
        }
    }

    // Provenance: WithPathTest#testValidWithObjectSimpleCreate().
    void testValidWithObjectSimpleCreateVpack() throws Exception {
        for (boolean compiled : new boolean[] { true, false }) {
            ObjectNode root = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
            ObjectNode match = compiled
                    ? root.withObject(JsonPointer.compile("/a/b"))
                    : root.withObject("/a/b");
            assertEquals("{}", match.toString());
            match.put("value", 42);
            assertEquals("{\"a\":{\"b\":{\"value\":42}}}", root.toString());

            ObjectNode match2 = compiled
                    ? root.withObject(JsonPointer.compile("/a/b"))
                    : root.withObject("/a/b");
            assertSame(match, match2);
            match.put("value2", true);
            assertEquals("{\"a\":{\"b\":{\"value\":42,\"value2\":true}}}",
                    root.toString());
        }
    }

    // Provenance: WithPathTest#testValidWithObjectSimpleModify().
    void testValidWithObjectSimpleModifyVpack() throws Exception {
        for (boolean compiled : new boolean[] { true, false }) {
            JsonNode doc = mapper.readTree(OBJECT_MODIFY);
            ObjectNode match = compiled
                    ? doc.withObject(JsonPointer.compile("/a/d"))
                    : doc.withObject("/a/d");
            assertEquals("{}", match.toString());
            assertEquals("{\"a\":{\"b\":42,\"d\":{}}}", doc.toString());
        }
    }

    // Provenance: WithPathTest#testValidWithObjectWithArray().
    void testValidWithObjectWithArrayVpack() throws Exception {
        for (boolean compiled : new boolean[] { true, false }) {
            ObjectNode root = (ObjectNode) mapper.readTree(OBJECT_WITH_ARRAY);
            ObjectNode match = compiled
                    ? root.withObject(JsonPointer.compile("/arr/2"))
                    : root.withObject("/arr/2");
            assertTrue(match.isObject());
            match.put("value", 42);
            assertEquals("{\"arr\":[null,null,{\"value\":42}]}", root.toString());

            ObjectNode match2 = compiled
                    ? root.withObject(JsonPointer.compile("/arr/2"))
                    : root.withObject("/arr/2");
            assertSame(match, match2);
            match2.put("value2", true);
            assertEquals("{\"arr\":[null,null,{\"value\":42,\"value2\":true}]}",
                    root.toString());

            ObjectNode match3 = compiled
                    ? root.withObject(JsonPointer.compile("/arr/0"))
                    : root.withObject("/arr/0");
            assertEquals("{}", match3.toString());
            match3.put("value", "bar");
            assertEquals("{\"arr\":[{\"value\":\"bar\"},null,{\"value\":42,\"value2\":true}]}",
                    root.toString());

            JsonNodeException failure = assertThrows(JsonNodeException.class,
                    () -> root.withObject("/arr/1", OverwriteMode.NONE, true));
            assertTrue(failure.getMessage().contains("Cannot replace `JsonNode` of type"));
            assertTrue(failure.getMessage().contains("OverwriteMode.NONE"));
        }
    }

    // Provenance: WithPathTest#testWithObjectProperty().
    void testWithObjectPropertyVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
        ObjectNode match = root.withObjectProperty("a");
        assertTrue(match.isObject());
        assertEquals("{}", match.toString());
        match.put("value", 42);
        assertEquals("{\"a\":{\"value\":42}}", root.toString());

        ObjectNode match2 = root.withObjectProperty("a");
        assertSame(match, match2);
        match.put("value2", true);
        assertEquals("{\"a\":{\"value\":42,\"value2\":true}}", root.toString());

        JsonNode root2 = mapper.readTree(OBJECT_WITH_NULL);
        ObjectNode match3 = root2.withObjectProperty("b");
        assertNotSame(match, match3);
        assertEquals("{\"b\":{}}", root2.toString());

        JsonNode root3 = mapper.readTree(OBJECT_WITH_NUMBER);
        JsonNodeException failure = assertThrows(JsonNodeException.class,
                () -> root3.withObjectProperty("c"));
        assertTrue(failure.getMessage().contains("Cannot replace `JsonNode` of type"));
    }

    // Provenance: WithPathTest#testWithObjectAdnExprOrProp().
    void testWithObjectAdnExprOrPropVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
        ObjectNode match = root.withObject("a");
        assertTrue(match.isObject());
        assertEquals("{}", match.toString());
        match.put("value", 42);
        assertEquals("{\"a\":{\"value\":42}}", root.toString());

        match = root.withObject("/a/b");
        assertEquals("{}", match.toString());
        assertEquals("{\"a\":{\"value\":42,\"b\":{}}}", root.toString());
        assertEquals("{\"value\":42,\"b\":{}}", root.withObject("a").toString());
        assertEquals("{}", root.withObject("/a/b").toString());

        JsonNode root3 = mapper.readTree(OBJECT_WITH_NUMBER);
        for (String path : new String[] { "c", "/c" }) {
            JsonNodeException failure = assertThrows(JsonNodeException.class,
                    () -> root3.withObject(path));
            assertTrue(failure.getMessage().contains("Cannot replace `JsonNode` of type"));
        }
    }

    // Provenance: WithPathTest#testValidWithArrayTrivial().
    void testValidWithArrayTrivialVpack() throws Exception {
        ArrayNode root = (ArrayNode) mapper.readTree(VPackWireFixtureTest.hex("01"));
        assertSame(root, root.withArray(JsonPointer.empty()));

        ObjectNode rootObject = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
        JsonNodeException failure = assertThrows(JsonNodeException.class,
                () -> rootObject.withArray(JsonPointer.empty()));
        assertTrue(failure.getMessage().contains("Can only call `withArray()` with empty"));
        assertTrue(failure.getMessage().contains("on `ArrayNode`"));
    }

    // Provenance: WithPathTest#testValidWithArraySimple().
    void testValidWithArraySimpleVpack() throws Exception {
        for (boolean compiled : new boolean[] { true, false }) {
            JsonNode doc = mapper.readTree(ARRAY_SIMPLE);
            String original = doc.toString();
            ArrayNode match = compiled
                    ? doc.withArray(JsonPointer.compile("/a/b"))
                    : doc.withArray("/a/b");
            assertEquals("[1,2]", match.toString());
            assertEquals(original, doc.toString());

            match = compiled
                    ? doc.withArray(JsonPointer.compile("/a/x"))
                    : doc.withArray("/a/x");
            assertEquals("[]", match.toString());
            assertEquals("{\"a\":{\"b\":[1,2],\"c\":true,\"x\":[]}}", doc.toString());

            JsonNodeException failure = assertThrows(JsonNodeException.class,
                    () -> doc.withArray(JsonPointer.compile("/a/b/0")));
            assertTrue(failure.getMessage().contains("Cannot replace `JsonNode` of type"));
            assertTrue(failure.getMessage().contains("OverwriteMode.NULLS"));

            match = compiled
                    ? doc.withArray(JsonPointer.compile("/a/b/0"), OverwriteMode.ALL, true)
                    : doc.withArray("/a/b/0", OverwriteMode.ALL, true);
            assertEquals("[]", match.toString());
            assertEquals("{\"a\":{\"b\":[[],2],\"c\":true,\"x\":[]}}", doc.toString());
        }
    }

    // Provenance: WithPathTest#testWithArray3882().
    void testWithArray3882Vpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
        ArrayNode array = root.withArray("/key/0/a", OverwriteMode.ALL, true);
        array.add(123);
        assertEquals("{\"key\":[{\"a\":[123]}]}", root.toString());

        root = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
        array = root.withArray(JsonPointer.compile("/key1/array1/0/element1"),
                OverwriteMode.ALL, true);
        array.add("v1");
        assertEquals("{\"key1\":{\"array1\":[{\"element1\":[\"v1\"]}]}}",
                root.toString());
    }

    // Provenance: WithPathTest#testWithArrayProperty().
    void testWithArrayPropertyVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
        ArrayNode match = root.withArrayProperty("a");
        assertTrue(match.isArray());
        assertEquals("[]", match.toString());
        match.add(42);
        assertEquals("{\"a\":[42]}", root.toString());

        ArrayNode match2 = root.withArrayProperty("a");
        assertSame(match, match2);
        match.add(true);
        assertEquals("{\"a\":[42,true]}", root.toString());

        JsonNode root2 = mapper.readTree(OBJECT_WITH_NULL);
        ArrayNode match3 = root2.withArrayProperty("b");
        assertNotSame(match, match3);
        assertEquals("{\"b\":[]}", root2.toString());

        JsonNode root3 = mapper.readTree(OBJECT_WITH_NUMBER);
        JsonNodeException failure = assertThrows(JsonNodeException.class,
                () -> root3.withArrayProperty("c"));
        assertTrue(failure.getMessage().contains("Cannot replace `JsonNode` of type"));
    }

    // Provenance: WithPathTest#testWithArrayAndExprOrProp().
    void testWithArrayAndExprOrPropVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(VPackWireFixtureTest.hex("0a"));
        ArrayNode match = root.withArray("a");
        assertTrue(match.isArray());
        assertEquals("[]", match.toString());
        match.add(42);
        assertEquals("{\"a\":[42]}", root.toString());

        match = root.withArray("/b");
        assertEquals("[]", match.toString());
        assertEquals("{\"a\":[42],\"b\":[]}", root.toString());
        assertEquals("[42]", root.withArray("a").toString());
        assertEquals("[42]", root.withArray("/a").toString());

        JsonNode root3 = mapper.readTree(OBJECT_WITH_NUMBER);
        for (String path : new String[] { "c", "/c" }) {
            JsonNodeException failure = assertThrows(JsonNodeException.class,
                    () -> root3.withArray(path));
            assertTrue(failure.getMessage().contains("Cannot replace `JsonNode` of type"));
        }
    }

    void __invoke_testValidWithObjectTrivialVpack() throws Exception {
        try {
            testValidWithObjectTrivialVpack();
        } finally {
        }
    }


    void __invoke_testValidWithObjectSimpleExistingVpack() throws Exception {
        try {
            testValidWithObjectSimpleExistingVpack();
        } finally {
        }
    }


    void __invoke_testValidWithObjectSimpleCreateVpack() throws Exception {
        try {
            testValidWithObjectSimpleCreateVpack();
        } finally {
        }
    }


    void __invoke_testValidWithObjectSimpleModifyVpack() throws Exception {
        try {
            testValidWithObjectSimpleModifyVpack();
        } finally {
        }
    }


    void __invoke_testValidWithObjectWithArrayVpack() throws Exception {
        try {
            testValidWithObjectWithArrayVpack();
        } finally {
        }
    }


    void __invoke_testWithObjectPropertyVpack() throws Exception {
        try {
            testWithObjectPropertyVpack();
        } finally {
        }
    }


    void __invoke_testWithObjectAdnExprOrPropVpack() throws Exception {
        try {
            testWithObjectAdnExprOrPropVpack();
        } finally {
        }
    }


    void __invoke_testValidWithArrayTrivialVpack() throws Exception {
        try {
            testValidWithArrayTrivialVpack();
        } finally {
        }
    }


    void __invoke_testValidWithArraySimpleVpack() throws Exception {
        try {
            testValidWithArraySimpleVpack();
        } finally {
        }
    }


    void __invoke_testWithArray3882Vpack() throws Exception {
        try {
            testWithArray3882Vpack();
        } finally {
        }
    }


    void __invoke_testWithArrayPropertyVpack() throws Exception {
        try {
            testWithArrayPropertyVpack();
        } finally {
        }
    }


    void __invoke_testWithArrayAndExprOrPropVpack() throws Exception {
        try {
            testWithArrayAndExprOrPropVpack();
        } finally {
        }
    }

}
