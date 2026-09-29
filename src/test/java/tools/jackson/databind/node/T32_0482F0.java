package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0482F0 {
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

    // Provenance: JsonNodeFactoryTest#testSimpleCreation().
    void testSimpleCreationVpack() {
        JsonNodeFactory f = NODES;
        JsonNode n = f.numberNode((byte) 4);
        assertTrue(n.isInt());
        assertEquals(4, n.intValue());

        assertTrue(f.numberNode((Byte) null).isNull());
        assertTrue(f.numberNode((Short) null).isNull());
        assertTrue(f.numberNode((Integer) null).isNull());
        assertTrue(f.numberNode((Long) null).isNull());
        assertTrue(f.numberNode((Float) null).isNull());
        assertTrue(f.numberNode((Double) null).isNull());
        assertTrue(f.numberNode((BigDecimal) null).isNull());
        assertTrue(f.numberNode((BigInteger) null).isNull());
        assertTrue(f.missingNode().isMissingNode());
    }

    // Provenance: JsonNodeFactoryTest#testSortingObjectNode().
    void testSortingObjectNodeVpack() throws Exception {
        ObjectNode ordinary = (ObjectNode) MAPPER.readTree(SIMPLE_OBJECT);
        assertEquals(List.of("b", "a"), propertyNames(ordinary));

        VPackMapper sortingMapper = VPackMapper.builder()
                .nodeFactory(new SortingNodeFactory())
                .build();
        ObjectNode sorted = (ObjectNode) sortingMapper.readTree(SIMPLE_OBJECT);
        assertEquals(List.of("a", "b"), propertyNames(sorted));
        assertEquals(1, sorted.get("a").intValue());
        assertEquals(2, sorted.get("b").intValue());

        JsonNode ordinaryBigger = MAPPER.readTree(BIGGER_ARRAY);
        ObjectNode ordinaryNested = (ObjectNode) ordinaryBigger.get(1);
        assertEquals(List.of("b", "c", "a"), propertyNames(ordinaryNested));

        JsonNode sortedBigger = sortingMapper.readTree(BIGGER_ARRAY);
        ObjectNode sortedNested = (ObjectNode) sortedBigger.get(1);
        assertEquals(List.of("a", "b", "c"), propertyNames(sortedNested));
        assertEquals(3, sortedNested.get("a").intValue());
        assertTrue(sortedNested.get("c").isBoolean());
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

    void __invoke_testSimpleCreationVpack() throws Exception {
        try {
            testSimpleCreationVpack();
        } finally {
        }
    }


    void __invoke_testSortingObjectNodeVpack() throws Exception {
        try {
            testSortingObjectNodeVpack();
        } finally {
        }
    }

}
