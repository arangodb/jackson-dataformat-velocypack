package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.StringNode;

import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0472F0 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NESTED_ARRAY = VPackWireFixtureTest.hex(
            "02 14 02 12 02 04 30 30 02 04 30 30 02 04 30 30 02 04 30 30");
private final ObjectMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODE_F = MAPPER.getNodeFactory();

    void testNullFromMissingNodeParameterVpack() throws Exception {
        Pojo3214 result = MAPPER.readValue(EMPTY_OBJECT, Pojo3214.class);
        assertNull(result.fromCtor);
    }
static class Pojo3214 {
        JsonNode fromCtor = StringNode.valueOf("x");

        @com.fasterxml.jackson.annotation.JsonCreator
        public Pojo3214(@com.fasterxml.jackson.annotation.JsonProperty("node") JsonNode node) {
            fromCtor = node;
        }
    }

    void __invoke_testNullFromMissingNodeParameterVpack() throws Exception {
        try {
            testNullFromMissingNodeParameterVpack();
        } finally {
        }
    }

}
