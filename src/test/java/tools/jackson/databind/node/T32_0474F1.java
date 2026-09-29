package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0474F1 {
private static final byte[] DOUBLE_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 00");
private static final byte[] DOUBLE_9999 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 80 87 c3 40");
private static final byte[] DOUBLE_NEGATIVE_28 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 3c c0");
private static final byte[] DOUBLE_15 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] DOUBLE_NEGATIVE_625 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 19 c0");
private static final byte[] DOUBLE_425 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 40 45 40");
private static final byte[] ARRAY = VPackWireFixtureTest.hex(
            "13 08 31 43 74 77 6f 02");
private static final byte[] OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 61 31 01");
private static final byte[] TWO_ARRAY = VPackWireFixtureTest.hex(
            "02 04 31 32");
private static final byte[] ARRAY_OF_OBJECTS = VPackWireFixtureTest.hex(
            "13 20 14 0e 44 6e 61 6d 65 45 66 69 72 73 74 01"
          + "14 0f 44 6e 61 6d 65 46 73 65 63 6f 6e 64 01 02");
private static final byte[] ARRAY_OF_ARRAYS = VPackWireFixtureTest.hex(
            "13 0b 02 04 31 32 02 04 33 34 02");
private final VPackMapper MAPPER = VPackMapper.builder().build();
private final VPackMapper STRICT_MAPPER = VPackMapper.builder()
            .disable(tools.jackson.databind.DeserializationFeature.ACCEPT_FLOAT_AS_INT)
            .build();

    // Provenance: JsonNodeAsContainerTest#asArrayOkFromArrayNode().
    void asArrayOkFromArrayNodeVpack() throws Exception {
        ArrayNode array = (ArrayNode) MAPPER.readTree(ARRAY);
        assertSame(array, array.asArray());
        assertTrue(array.asArrayOpt().isPresent());
        assertSame(array, array.asArrayOpt().get());
    }

    // Provenance: JsonNodeAsContainerTest#asArrayFailFromObjectNode().
    void asArrayFailFromObjectNodeVpack() throws Exception {
        assertFailAsArrayFor(MAPPER.readTree(OBJECT));
    }

    // Provenance: JsonNodeAsContainerTest#asArrayFailFromScalars().
    void asArrayFailFromScalarsVpack() throws Exception {
        for (JsonNode node : scalarNodes()) {
            assertFailAsArrayFor(node);
        }
    }

    // Provenance: JsonNodeAsContainerTest#asObjectOkFromObjectNode().
    void asObjectOkFromObjectNodeVpack() throws Exception {
        ObjectNode object = (ObjectNode) MAPPER.readTree(OBJECT);
        assertSame(object, object.asObject());
        assertTrue(object.asObjectOpt().isPresent());
        assertSame(object, object.asObjectOpt().get());
    }

    // Provenance: JsonNodeAsContainerTest#asObjectFailFromArrayNode().
    void asObjectFailFromArrayNodeVpack() throws Exception {
        assertFailAsObjectFor(MAPPER.readTree(ARRAY));
    }

    // Provenance: JsonNodeAsContainerTest#asObjectFailFromScalars().
    void asObjectFailFromScalarsVpack() throws Exception {
        for (JsonNode node : scalarNodes()) {
            assertFailAsObjectFor(node);
        }
    }

    // Provenance: JsonNodeAsContainerTest#iterateArrayElementsAsObjects().
    void iterateArrayElementsAsObjectsVpack() throws Exception {
        ArrayNode array = (ArrayNode) MAPPER.readTree(ARRAY_OF_OBJECTS);
        for (JsonNode element : array) {
            element.asObject().put("added", true);
        }
        assertTrue(array.get(0).get("added").booleanValue());
        assertTrue(array.get(1).get("added").booleanValue());
    }

    // Provenance: JsonNodeAsContainerTest#iterateArrayElementsAsArrays().
    void iterateArrayElementsAsArraysVpack() throws Exception {
        ArrayNode outer = (ArrayNode) MAPPER.readTree(ARRAY_OF_ARRAYS);
        for (JsonNode element : outer) {
            element.asArray().add(99);
        }
        assertEquals(3, outer.get(0).size());
        assertEquals(3, outer.get(1).size());
        assertEquals(99, outer.get(0).get(2).intValue());
        assertEquals(99, outer.get(1).get(2).intValue());
    }
private JsonNode readDouble(byte[] bytes) throws Exception {
        return MAPPER.readTree(bytes);
    }
private JsonNode[] scalarNodes() throws Exception {
        return new JsonNode[] {
            MAPPER.readTree(VPackWireFixtureTest.hex("1a")),
            MAPPER.readTree(VPackWireFixtureTest.hex("19")),
            MAPPER.readTree(VPackWireFixtureTest.hex("32")),
            MAPPER.readTree(VPackWireFixtureTest.hex("28 2a")),
            MAPPER.readTree(DOUBLE_425),
            MAPPER.getNodeFactory().numberNode(BigInteger.TEN),
            MAPPER.getNodeFactory().numberNode(BigDecimal.valueOf(12.5)),
            MAPPER.readTree(VPackWireFixtureTest.hex("44 74 65 73 74")),
            MAPPER.readTree(VPackWireFixtureTest.hex("c0 03 00 00 00")),
            MAPPER.readTree(VPackWireFixtureTest.hex("18")),
            MAPPER.readTree(VPackWireFixtureTest.hex("40")),
            MAPPER.getNodeFactory().missingNode(),
            MAPPER.getNodeFactory().rawValueNode(new RawValue("abc")),
            MAPPER.getNodeFactory().pojoNode(new AtomicInteger(1))
        };
    }
private void assertFailAsArrayFor(JsonNode node) {
        JsonNodeException e = assertThrows(JsonNodeException.class, node::asArray);
        assertTrue(e.getMessage().contains("asArray()"));
        assertTrue(e.getMessage().contains("ArrayNode"));
        assertFalse(node.asArrayOpt().isPresent());
    }
private void assertFailAsObjectFor(JsonNode node) {
        JsonNodeException e = assertThrows(JsonNodeException.class, node::asObject);
        assertTrue(e.getMessage().contains("asObject()"));
        assertTrue(e.getMessage().contains("ObjectNode"));
        assertFalse(node.asObjectOpt().isPresent());
    }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        static final NoCheckSubTypeValidator instance = new NoCheckSubTypeValidator();
    }

    void __invoke_asArrayOkFromArrayNodeVpack() throws Exception {
        try {
            asArrayOkFromArrayNodeVpack();
        } finally {
        }
    }


    void __invoke_asArrayFailFromObjectNodeVpack() throws Exception {
        try {
            asArrayFailFromObjectNodeVpack();
        } finally {
        }
    }


    void __invoke_asArrayFailFromScalarsVpack() throws Exception {
        try {
            asArrayFailFromScalarsVpack();
        } finally {
        }
    }


    void __invoke_asObjectOkFromObjectNodeVpack() throws Exception {
        try {
            asObjectOkFromObjectNodeVpack();
        } finally {
        }
    }


    void __invoke_asObjectFailFromArrayNodeVpack() throws Exception {
        try {
            asObjectFailFromArrayNodeVpack();
        } finally {
        }
    }


    void __invoke_asObjectFailFromScalarsVpack() throws Exception {
        try {
            asObjectFailFromScalarsVpack();
        } finally {
        }
    }


    void __invoke_iterateArrayElementsAsObjectsVpack() throws Exception {
        try {
            iterateArrayElementsAsObjectsVpack();
        } finally {
        }
    }


    void __invoke_iterateArrayElementsAsArraysVpack() throws Exception {
        try {
            iterateArrayElementsAsArraysVpack();
        } finally {
        }
    }

}
