package tools.jackson.databind.util;

import java.util.stream.IntStream;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.util.JacksonCollectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import tools.jackson.dataformat.velocypack.*;

class T32_0626F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: JacksonCollectorsTest#testToArrayNode().
    void jacksonCollectorArrayNodeVpack() throws Exception {
        JsonNode collected = IntStream.range(0, 10)
                .mapToObj(i -> {
                    ObjectNode object = MAPPER.createObjectNode();
                    object.put("testString", "example");
                    object.put("testNumber", i);
                    object.put("testBoolean", true);
                    return object;
                })
                .collect(JacksonCollectors.toArrayNode());

        assertEquals(10, collected.size());
        collected.forEach(node -> assertFalse(node.isEmpty()));
        JsonNode decoded = MAPPER.readTree(MAPPER.writeValueAsBytes(collected));
        assertEquals(collected, decoded);
    }

    void __invoke_jacksonCollectorArrayNodeVpack() throws Exception {
        try {
            jacksonCollectorArrayNodeVpack();
        } finally {
        }
    }

}
