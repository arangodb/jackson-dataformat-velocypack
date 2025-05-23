package com.fasterxml.jackson.databind.node;

import java.util.*;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonNode;

public class TestFindMethods
    extends BaseMapTest
{
    public void testNonMatching() throws Exception
    {
        JsonNode root = _buildTree();

        assertNull(root.findValue("boogaboo"));
        assertNull(root.findParent("boogaboo"));
        JsonNode n = root.findPath("boogaboo");
        assertNotNull(n);
        assertTrue(n.isMissingNode());

        assertTrue(root.findValues("boogaboo").isEmpty());
        assertTrue(root.findParents("boogaboo").isEmpty());
    }

    public void testMatchingSingle() throws Exception
    {
        JsonNode root = _buildTree();

        JsonNode node = root.findValue("b");
        assertNotNull(node);
        assertEquals(3, node.intValue());
        node = root.findParent("b");
        assertNotNull(node);
        assertTrue(node.isObject());
        assertEquals(1, ((ObjectNode) node).size());
        assertEquals(3, node.path("b").intValue());
    }

    public void testMatchingMultiple() throws Exception
    {
        JsonNode root = _buildTree();

        List<JsonNode> nodes = root.findValues("value");
        assertEquals(2, nodes.size());
        // here we count on nodes being returned in order; true with Jackson:
        assertEquals(3, nodes.get(0).intValue());
        assertEquals(42, nodes.get(1).intValue());

        nodes = root.findParents("value");
        assertEquals(2, nodes.size());
        // should only return JSON Object nodes:
        assertTrue(nodes.get(0).isObject());
        assertTrue(nodes.get(1).isObject());
        assertEquals(3, nodes.get(0).path("value").intValue());
        assertEquals(42, nodes.get(1).path("value").intValue());

        // and finally, convenience conversion method
        List<String> values = root.findValuesAsText("value");
        assertEquals(2, values.size());
        assertEquals("3", values.get(0));
        assertEquals("42", values.get(1));
    }
    
    private JsonNode _buildTree() throws Exception
    {
        final String SAMPLE = "{ \"a\" : { \"value\" : 3 },"
            +"\"array\" : [ { \"b\" : 3 }, {\"value\" : 42}, { \"other\" : true } ]"
            +"}";
        return objectMapper().readTree(com.fasterxml.jackson.VPackUtils.toVPack(SAMPLE));
    }
}
