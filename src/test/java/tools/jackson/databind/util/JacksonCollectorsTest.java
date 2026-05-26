package tools.jackson.databind.util;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JacksonCollectorsTest {

    @Test
    public void testToArrayNode()
    {
        final ObjectMapper objectMapper = new VPackMapper();

        final JsonNode jsonNodeResult = IntStream.range(0, 10)
            .mapToObj(i -> {
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("testString", "example");
                objectNode.put("testNumber", i);
                objectNode.put("testBoolean", true);

                return objectNode;
            })
            .collect(JacksonCollectors.toArrayNode());

        assertEquals(10, jsonNodeResult.size());
        jsonNodeResult.forEach(jsonNode -> assertFalse(jsonNode.isEmpty()));
    }
}
