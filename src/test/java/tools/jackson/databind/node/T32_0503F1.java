package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.TreeBuildingGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0503F1 {
private static final byte[] ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] DEF = VPackWireFixtureTest.hex("43 64 65 66");
private static final byte[] OBJECT = VPackWireFixtureTest.hex(
            "14 17 "
            + "43 6b 65 79 31 "
            + "41 62 41 78 "
            + "45 61 72 72 61 79 13 05 31 19 02 "
            + "03");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 05 01 02 03 04 06");
private final VPackMapper mapper = new VPackMapper();
private final JsonMapper jsonMapper = JsonMapper.builder().build();

    // Provenance: ToStringForNodesTest#testArrayNode().
    void testArrayNodeVpack() throws Exception {
        verifyToStrings(mapper.readTree(VPackWireFixtureTest.hex(
                "13 0f 31 1a 18 13 08 43 61 62 63 33 02 0a 05")));

        ArrayNode node = mapper.createArrayNode().add(0.25).add(true);
        assertEquals("[0.25,true]", node.toString());
        assertEquals("[ 0.25, true ]", node.toPrettyString());
    }

    // Provenance: ToStringForNodesTest#testBinaryNode().
    void testBinaryNodeVpack() throws Exception {
        verifyToStrings(mapper.readTree(BINARY));
    }

    // Provenance: ToStringForNodesTest#testObjectNode().
    void testObjectNodeVpack() throws Exception {
        verifyToStrings(mapper.readTree(OBJECT));

        ObjectNode node = mapper.createObjectNode().put("msg", "hello world");
        assertEquals(jsonMapper.writeValueAsString(node), node.toString());
        assertEquals(jsonMapper.writer().withDefaultPrettyPrinter()
                .writeValueAsString(node), node.toPrettyString());
    }
private TreeBuildingGenerator generator() {
        return TreeBuildingGenerator.forSerialization(null, mapper.getNodeFactory());
    }
private void verifyToStrings(JsonNode node) throws Exception {
        assertEquals(jsonMapper.writeValueAsString(node), node.toString());
        assertEquals(jsonMapper.writer().withDefaultPrettyPrinter()
                .writeValueAsString(node), node.toPrettyString());
    }

    void __invoke_testArrayNodeVpack() throws Exception {
        try {
            testArrayNodeVpack();
        } finally {
        }
    }


    void __invoke_testBinaryNodeVpack() throws Exception {
        try {
            testBinaryNodeVpack();
        } finally {
        }
    }


    void __invoke_testObjectNodeVpack() throws Exception {
        try {
            testObjectNodeVpack();
        } finally {
        }
    }

}
