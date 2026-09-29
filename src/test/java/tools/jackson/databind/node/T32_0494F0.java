package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0494F0 {
private static final byte[] NULL_ROOT = VPackWireFixtureTest.hex("18");
private static final byte[] ARRAY_ROOT = VPackWireFixtureTest.hex(
            "13 12 19 14 0c 46 61 6e 73 77 65 72 28 2a 01 28 89 03");
private static final byte[] OBJECT_ROOT = VPackWireFixtureTest.hex(
            "14 39 46 61 6e 73 77 65 72 28 2a"
          + "46 6d 61 74 72 69 78 13 0f 31 2c 35 1c dc df 02 1a 43 2e 2e 2e 04"
          + "44 6d 69 73 63 14 12 45 76 61 6c 75 65 1b 00 00 00 00 00 00 d0 3f 01 03");
private static final byte[] NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 06 41 78 18 01");
private static final byte[] NULL_COVARIANCE = VPackWireFixtureTest.hex(
            "14 12 46 6f 62 6a 65 63 74 18 45 61 72 72 61 79 18 02");
private static final byte[] SCALED_DECIMAL = VPackWireFixtureTest.hex(
            "c8 04 fc ff ff ff 01 23 45 00");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: NodeJDKSerializationTest#testObjectNodeSerialization().
    void testObjectNodeSerializationVpack() throws Exception {
        ObjectNode expected = mapper.createObjectNode();
        expected.put("answer", 42);
        ArrayNode matrix = expected.withArray("matrix");
        matrix.add(1).add(12345678901L).add(true).add("...");
        expected.withObject("misc").put("value", 0.25);

        assertEquals(expected, mapper.readTree(OBJECT_ROOT));
        assertEquals(expected, roundTrip(expected));
    }

    // Provenance: NodeJDKSerializationTest#testArrayNodeSerialization().
    void testArrayNodeSerializationVpack() throws Exception {
        ArrayNode expected = mapper.createArrayNode();
        expected.add(false);
        expected.addObject().put("answer", 42);
        expected.add(137);

        assertEquals(expected, mapper.readTree(ARRAY_ROOT));
        assertEquals(expected, roundTrip(expected));
    }

    // Provenance: NodeJDKSerializationTest#testBigArrayNodeSerialization().
    void testBigArrayNodeSerializationVpack() throws Exception {
        // The source test crosses NodeSerialization's JDK framing allocation
        // threshold. VPack has no such framing seam, so retain its large-tree
        // round-trip assertion with bounded, deterministic sizes.
        for (int size : new int[] { 39, 101, 997, 4096 }) {
            ArrayNode root = mapper.createArrayNode();
            for (int ix = 0; ix < size; ++ix) {
                root.addObject().put("index", ix).put("extra", "none#" + (ix + 1));
            }
            assertEquals(root, roundTrip(root), "size=" + size);
        }
    }

    // Provenance: NodeJDKSerializationTest#testScalarSerialization().
    void testScalarSerializationVpack() throws Exception {
        assertEquals(NullNode.instance, roundTrip(mapper.nullNode()));
        assertEquals(mapper.getNodeFactory().textNode("Foobar"),
                roundTrip(mapper.getNodeFactory().textNode("Foobar")));
        assertEquals(mapper.getNodeFactory().booleanNode(true),
                roundTrip(mapper.getNodeFactory().booleanNode(true)));
        assertEquals(mapper.getNodeFactory().booleanNode(false),
                roundTrip(mapper.getNodeFactory().booleanNode(false)));
        assertEquals(mapper.getNodeFactory().numberNode(123),
                roundTrip(mapper.getNodeFactory().numberNode(123)));
        assertEquals(mapper.getNodeFactory().numberNode(-12345678901234L),
                roundTrip(mapper.getNodeFactory().numberNode(-12345678901234L)));
    }
private JsonNode roundTrip(JsonNode input) throws Exception {
        return mapper.readTree(mapper.writeValueAsBytes(input));
    }
public static class CovarianceBean {
        public ObjectNode object;
        public ArrayNode array;
    }
@SuppressWarnings("serial")
    public static class MyNull extends NullNode { }

    void __invoke_testObjectNodeSerializationVpack() throws Exception {
        try {
            testObjectNodeSerializationVpack();
        } finally {
        }
    }


    void __invoke_testArrayNodeSerializationVpack() throws Exception {
        try {
            testArrayNodeSerializationVpack();
        } finally {
        }
    }


    void __invoke_testBigArrayNodeSerializationVpack() throws Exception {
        try {
            testBigArrayNodeSerializationVpack();
        } finally {
        }
    }


    void __invoke_testScalarSerializationVpack() throws Exception {
        try {
            testScalarSerializationVpack();
        } finally {
        }
    }

}
