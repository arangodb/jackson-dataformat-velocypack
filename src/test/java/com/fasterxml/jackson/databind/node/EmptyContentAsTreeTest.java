package com.fasterxml.jackson.databind.node;

import com.arangodb.velocypack.VPackBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests to verify handling of empty content with "readTree()"
 */
public class EmptyContentAsTreeTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = objectMapper();

    private final byte[] EMPTY0 = new VPackBuilder().slice().toByteArray();

    // [databind#1406]: when passing `JsonParser`, indicate lack of content
    // by returning `null`

    public void testNullFromEOFWithParserAndMapper() throws Exception
    {
        try (JsonParser p = MAPPER.getFactory().createParser(EMPTY0)) {
            _assertNullTree(MAPPER.readTree(p));
        }
    }

    // [databind#1406]
    public void testNullFromEOFWithParserAndReader() throws Exception
    {
        try (JsonParser p = MAPPER.getFactory().createParser(EMPTY0)) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }
    }

    // [databind#2211]: when passing content sources OTHER than `JsonParser`,
    // return "missing node" instead of alternate (return `null`, throw exception).
    public void testMissingNodeForEOFOtherMapper() throws Exception
    {
        _assertMissing(MAPPER.readTree(EMPTY0));
        // Assume File, URL, etc are fine. Note: `DataInput` probably can't be made to
        // work since it can not easily/gracefully handle unexpected end-of-input
    }

    public void testMissingNodeViaObjectReader() throws Exception
    {
        _assertMissing(MAPPER.reader().readTree(EMPTY0));
    }

    private void _assertNullTree(TreeNode n) {
        if (n != null) {
            fail("Should get `null` for reads with `JsonParser`, instead got: "+n.getClass().getName());
        }
    }

    private void _assertMissing(JsonNode n) {
        assertNotNull("Should not get `null` but `MissingNode`", n);
        if (!n.isMissingNode()) {
            fail("Should get `MissingNode` but got: "+n.getClass().getName());
        }
    }
}
