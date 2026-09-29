package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.StringNode;
import tools.jackson.databind.node.TreeBuildingGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0503F0 {
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

    // Provenance: StringNodeTest#testEquals().
    void testEqualsVpack() throws Exception {
        StringNode first = (StringNode) mapper.readTree(ABC);
        StringNode sameValue = (StringNode) mapper.readTree(ABC);
        StringNode different = (StringNode) mapper.readTree(DEF);

        assertEquals(first, sameValue);
        assertNotEquals(first, different);
    }

    // Provenance: StringNodeTest#testHashCode().
    void testHashCodeVpack() throws Exception {
        StringNode node = (StringNode) mapper.readTree(ABC);

        assertEquals("abc".hashCode(), node.hashCode());
    }
private TreeBuildingGenerator generator() {
        return TreeBuildingGenerator.forSerialization(null, mapper.getNodeFactory());
    }
private void verifyToStrings(JsonNode node) throws Exception {
        assertEquals(jsonMapper.writeValueAsString(node), node.toString());
        assertEquals(jsonMapper.writer().withDefaultPrettyPrinter()
                .writeValueAsString(node), node.toPrettyString());
    }

    void __invoke_testEqualsVpack() throws Exception {
        try {
            testEqualsVpack();
        } finally {
        }
    }


    void __invoke_testHashCodeVpack() throws Exception {
        try {
            testHashCodeVpack();
        } finally {
        }
    }

}
