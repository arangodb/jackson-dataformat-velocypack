package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0497Fixture {
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] OBJECT_ABC = VPackWireFixtureTest.hex(
            "14 0c 41 61 31 41 62 32 41 63 33 03");
private static final byte[] OBJECT_BCA = VPackWireFixtureTest.hex(
            "14 0c 41 62 32 41 63 33 41 61 31 03");
private static final byte[] DUPLICATE_OBJECT = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 31 41 61 32 03 06");
private static final byte[] NESTED_DUPLICATE_OBJECT = VPackWireFixtureTest.hex(
            "14 2d 44 6e 6f 64 65 14 25 44 64 61 74 61 13 1d 31 32 "
          + "14 06 41 61 33 01 14 12 43 66 6f 6f 31 43 62 61 72 32 "
          + "43 66 6f 6f 33 03 04 01 01");
private static final byte[] OBJECT_A_TRUE_C_STUFF = VPackWireFixtureTest.hex(
            "14 11 41 61 31 41 62 1a 41 63 45 73 74 75 66 66 03");
private static final byte[] OBJECT_A_STRING_B = VPackWireFixtureTest.hex(
            "14 07 41 61 41 62 01");
private static final byte[] WRAPPED_A_OBJECT = VPackWireFixtureTest.hex(
            "0b 10 01 44 6e 6f 64 65 0b 07 01 41 61 33 03 03");
private static final byte[] EMPTY_WRAPPER = VPackWireFixtureTest.hex("0a");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: ObjectNodeTest#testEqualityWrtOrder().
    void testEqualityWrtOrderVpack() throws Exception {
        ObjectNode first = (ObjectNode) mapper.readTree(OBJECT_ABC);
        ObjectNode second = (ObjectNode) mapper.readTree(OBJECT_BCA);

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    // Provenance: ObjectNodeTest#testFailOnDupKeys().
    void testFailOnDupKeysVpack() throws Exception {
        assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY));
        ObjectNode defaultRead = (ObjectNode) mapper.readTree(DUPLICATE_OBJECT);
        assertEquals(2, defaultRead.path("a").asInt());
        ObjectNode readerRead = (ObjectNode) mapper.reader().readTree(DUPLICATE_OBJECT);
        assertEquals(2, readerRead.path("a").asInt());

        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.reader(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
                        .readTree(DUPLICATE_OBJECT));
        assertTrue(failure.getMessage().contains("Duplicate property \"a\""));
    }

    // Provenance: ObjectNodeTest#testFailOnDupNestedKeys().
    void testFailOnDupNestedKeysVpack() throws Exception {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readerFor(ObNodeWrapper.class)
                        .with(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
                        .readValue(NESTED_DUPLICATE_OBJECT));
        assertTrue(failure.getMessage().contains("Duplicate property"));
        assertTrue(failure.getMessage().contains("foo"));
    }

    // Provenance: ObjectNodeTest#testInvalidWithArray().
    void testInvalidWithArrayVpack() throws Exception {
        JsonNode arrayRoot = mapper.readTree(EMPTY_ARRAY);
        JsonNodeException arrayFailure = assertThrows(JsonNodeException.class,
                () -> arrayRoot.withArray("/prop"));
        assertTrue(arrayFailure.getMessage().contains("not of type `ObjectNode`"));
        assertTrue(arrayFailure.getMessage().contains("ArrayNode"));

        ObjectNode objectRoot = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        objectRoot.put("prop", 13);
        JsonNodeException propertyFailure = assertThrows(JsonNodeException.class,
                () -> objectRoot.withArray("/prop"));
        assertTrue(propertyFailure.getMessage().contains("Cannot replace `JsonNode` of type"));
        assertTrue(propertyFailure.getMessage().contains("IntNode"));
    }

    // Provenance: ObjectNodeTest#testInvalidWithObject().
    void testInvalidWithObjectVpack() throws Exception {
        JsonNode arrayRoot = mapper.readTree(EMPTY_ARRAY);
        JsonNodeException arrayFailure = assertThrows(JsonNodeException.class,
                () -> arrayRoot.withObject("/prop"));
        assertTrue(arrayFailure.getMessage().contains("not of type `ObjectNode`"));
        assertTrue(arrayFailure.getMessage().contains("ArrayNode"));

        ObjectNode objectRoot = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        objectRoot.put("prop", 13);
        JsonNodeException propertyFailure = assertThrows(JsonNodeException.class,
                () -> objectRoot.withObject("/prop"));
        assertTrue(propertyFailure.getMessage().contains("Cannot replace `JsonNode` of type"));
        assertTrue(propertyFailure.getMessage().contains("IntNode"));
    }

    // Provenance: ObjectNodeTest#testIssue941().
    void testIssue941Vpack() throws Exception {
        byte[] encoded = mapper.writeValueAsBytes(mapper.createObjectNode());
        assertEquals(EMPTY_OBJECT[0], encoded[0]);
        assertNotNull(mapper.readValue(encoded, ObjectNode.class));
        assertNotNull(mapper.readValue(encoded, MyValue.class));
    }

    // Provenance: ObjectNodeTest#testNonEmptySerialization().
    void testNonEmptySerializationVpack() throws Exception {
        ObNodeWrapper wrapper = new ObNodeWrapper(mapper.createObjectNode().put("a", 3));
        assertArrayEquals(WRAPPED_A_OBJECT, mapper.writeValueAsBytes(wrapper));

        wrapper = new ObNodeWrapper(mapper.createObjectNode());
        assertArrayEquals(EMPTY_WRAPPER, mapper.writeValueAsBytes(wrapper));
    }

    // Provenance: ObjectNodeTest#testNullChecking().
    void testNullCheckingVpack() throws Exception {
        ObjectNode first = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        ObjectNode second = mapper.createObjectNode();
        first.setAll(second);
        assertEquals(0, first.size());
        assertEquals(0, second.size());

        first.set("x", null);
        JsonNode node = first.get("x");
        assertNotNull(node);
        assertSame(node, tools.jackson.databind.node.NullNode.instance);
        assertEquals(tools.jackson.databind.node.NullNode.instance, first.optional("x").get());

        first.put("str", (String) null);
        node = first.get("str");
        assertNotNull(node);
        assertSame(node, tools.jackson.databind.node.NullNode.instance);

        first.put("d", (BigDecimal) null);
        node = first.get("d");
        assertNotNull(node);
        assertSame(node, tools.jackson.databind.node.NullNode.instance);

        first.put("3", (BigInteger) null);
        node = first.get("3");
        assertNotNull(node);
        assertSame(node, tools.jackson.databind.node.NullNode.instance);
        assertEquals(4, first.size());
    }

    // Provenance: ObjectNodeTest#testNullChecking2().
    void testNullChecking2Vpack() throws Exception {
        ObjectNode source = (ObjectNode) mapper.readTree(OBJECT_A_STRING_B);
        ObjectNode destination = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        destination.setAll(source);
        assertEquals("b", destination.path("a").asString());
    }

    // Provenance: ObjectNodeTest#testNullKeyChecking().
    void testNullKeyCheckingVpack() {
        ObjectNode source = mapper.createObjectNode();
        assertThrows(NullPointerException.class, () -> source.put(null, "a"));
        assertThrows(NullPointerException.class, () -> source.put(null, 123));
        assertThrows(NullPointerException.class, () -> source.put(null, 123L));
        assertThrows(NullPointerException.class, () -> source.putNull(null));
        assertThrows(NullPointerException.class, () -> source.set(null, BooleanNode.TRUE));
        assertThrows(NullPointerException.class, () -> source.replace(null, BooleanNode.TRUE));
        assertThrows(NullPointerException.class, () -> source.setAll(Collections.singletonMap(
                null, mapper.createArrayNode())));
    }

    // Provenance: ObjectNodeTest#testPropertiesTraversal().
    void testPropertiesTraversalVpack() throws Exception {
        assertEquals("", toString(mapper.readTree(EMPTY_ARRAY)));
        assertEquals("", toString(mapper.readTree(VPackWireFixtureTest.hex(
                "43 66 6f 6f"))));
        assertEquals("a/1,b/true,c/\"stuff\"",
                toString(mapper.readTree(OBJECT_A_TRUE_C_STUFF)));
    }

    // Provenance: ObjectNodeTest#testPutArrayIndexWhenParentMissing().
    void testPutArrayIndexWhenParentMissingVpack() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.put(JsonPointer.compile("/arr/0"),
                mapper.getNodeFactory().stringNode("value"));

        assertTrue(root.has("arr"));
        assertTrue(root.get("arr").isObject());
        assertEquals("value", root.at("/arr/0").asString());
    }
private String toString(JsonNode node) {
        StringBuilder result = new StringBuilder();
        node.properties().forEach(entry -> {
            if (result.length() > 0) {
                result.append(',');
            }
            result.append(entry.getKey()).append('/').append(entry.getValue());
        });
        return result.toString();
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class ObNodeWrapper {
        public ObjectNode node;

        ObNodeWrapper() { }

        ObNodeWrapper(ObjectNode node) {
            this.node = node;
        }
    }
static class MyValue {
        private final ObjectNode object;

        @JsonCreator
        MyValue(ObjectNode object) {
            this.object = object;
        }

        @JsonValue
        ObjectNode getObject() {
            return object;
        }
    }

    void __invoke_testEqualityWrtOrderVpack() throws Exception {
        try {
            testEqualityWrtOrderVpack();
        } finally {
        }
    }


    void __invoke_testFailOnDupKeysVpack() throws Exception {
        try {
            testFailOnDupKeysVpack();
        } finally {
        }
    }


    void __invoke_testFailOnDupNestedKeysVpack() throws Exception {
        try {
            testFailOnDupNestedKeysVpack();
        } finally {
        }
    }


    void __invoke_testInvalidWithArrayVpack() throws Exception {
        try {
            testInvalidWithArrayVpack();
        } finally {
        }
    }


    void __invoke_testInvalidWithObjectVpack() throws Exception {
        try {
            testInvalidWithObjectVpack();
        } finally {
        }
    }


    void __invoke_testIssue941Vpack() throws Exception {
        try {
            testIssue941Vpack();
        } finally {
        }
    }


    void __invoke_testNonEmptySerializationVpack() throws Exception {
        try {
            testNonEmptySerializationVpack();
        } finally {
        }
    }


    void __invoke_testNullCheckingVpack() throws Exception {
        try {
            testNullCheckingVpack();
        } finally {
        }
    }


    void __invoke_testNullChecking2Vpack() throws Exception {
        try {
            testNullChecking2Vpack();
        } finally {
        }
    }


    void __invoke_testNullKeyCheckingVpack() throws Exception {
        try {
            testNullKeyCheckingVpack();
        } finally {
        }
    }


    void __invoke_testPropertiesTraversalVpack() throws Exception {
        try {
            testPropertiesTraversalVpack();
        } finally {
        }
    }


    void __invoke_testPutArrayIndexWhenParentMissingVpack() throws Exception {
        try {
            testPutArrayIndexWhenParentMissingVpack();
        } finally {
        }
    }

}
