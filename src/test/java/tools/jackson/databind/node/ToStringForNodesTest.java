package tools.jackson.databind.node;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

public class ToStringForNodesTest extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = objectMapper();

    @Test
    public void testObjectNode() throws Exception
    {
        _verifyToStrings(MAPPER.readTree(VPackUtils.toVPack("{ \"key\" : 1, \"b\" : \"x\", \"array\" : [ 1, false ] }")));
        final ObjectNode n = MAPPER.createObjectNode().put("msg", "hello world");
        assertEquals(VPackUtils.toJson(MAPPER.writeValueAsBytes(n)), n.toString());
        final String expPretty = VPackUtils.toJson(MAPPER.writer().writeValueAsBytes(n));
        assertEquals(expPretty, n.toString());
    }

    @Test
    public void testArrayNode() throws Exception
    {
        _verifyToStrings(MAPPER.readTree(VPackUtils.toVPack("[ 1, true, null, [ \"abc\",3], { } ]")));
        final ArrayNode n = MAPPER.createArrayNode().add(0.25).add(true);
        assertEquals("[0.25,true]", n.toString());
        assertEquals("[ 0.25, true ]", n.toPrettyString());
    }

    @Test
    public void testBinaryNode() throws Exception
    {
        _verifyToStrings(MAPPER.getNodeFactory().binaryNode(new byte[] { 1, 2, 3, 4, 6 }));
    }

    protected void _verifyToStrings(JsonNode node) throws Exception
    {
        assertEquals(VPackUtils.toJson(MAPPER.writeValueAsBytes(node)), node.toString());
    }
}
