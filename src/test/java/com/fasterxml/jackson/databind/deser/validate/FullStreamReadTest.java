package com.fasterxml.jackson.databind.deser.validate;

import com.fasterxml.jackson.databind.*;

import java.util.List;

/**
 * Test for validating {@link com.fasterxml.jackson.databind.DeserializationFeature#FAIL_ON_TRAILING_TOKENS}.
 */
public class FullStreamReadTest extends BaseMapTest
{
    private final static String JSON_OK_ARRAY = " [ 1, 2, 3]    ";
    private final static String JSON_OK_ARRAY_WITH_COMMENT = JSON_OK_ARRAY + " // stuff ";

    private final static String JSON_FAIL_ARRAY = JSON_OK_ARRAY + " [ ]";

    private final static String JSON_OK_NULL = " null  ";
    private final static String JSON_OK_NULL_WITH_COMMENT = " null /* stuff */ ";
    private final static String JSON_FAIL_NULL = JSON_OK_NULL + " false";
    
    /*
    /**********************************************************
    /* Test methods, config
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testMapperAcceptTrailing() throws Exception
    {
        assertFalse(MAPPER.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));

        // by default, should be ok to read, all
        _verifyArray(MAPPER.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_ARRAY)));
        _verifyArray(MAPPER.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_FAIL_ARRAY)));

        // and also via "untyped"
        _verifyCollection(MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_ARRAY), List.class));
        _verifyCollection(MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON_FAIL_ARRAY), List.class));

        // ditto for getting `null` and some other token

        assertTrue(MAPPER.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_NULL)).isNull());
        assertTrue(MAPPER.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_FAIL_NULL)).isNull());

        assertNull(MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_NULL), Object.class));
        assertNull(MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON_FAIL_NULL), Object.class));
    }

    public void testMapperFailOnTrailing() throws Exception
    {
        // but things change if we enforce checks
        ObjectMapper strict = newJsonMapper()
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        assertTrue(strict.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));

        // some still ok
        _verifyArray(strict.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_ARRAY)));
        _verifyCollection(strict.readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_ARRAY), List.class));

    }

    public void testMapperFailOnTrailingWithNull() throws Exception
    {
        final ObjectMapper strict = newJsonMapper()
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

        // some still ok
        JsonNode n = strict.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_NULL));
        assertNotNull(n);
        assertTrue(n.isNull());

    }
    
    public void testReaderAcceptTrailing() throws Exception
    {
        ObjectReader R = MAPPER.reader();
        assertFalse(R.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));

        _verifyArray(R.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_ARRAY)));
        _verifyArray(R.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_FAIL_ARRAY)));
        ObjectReader rColl = R.forType(List.class);
        _verifyCollection((List<?>)rColl.readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_ARRAY)));
        _verifyCollection((List<?>)rColl.readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON_FAIL_ARRAY)));
    }

    public void testReaderFailOnTrailing() throws Exception
    {
        ObjectReader strictR = MAPPER.reader().with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        ObjectReader strictRForList = strictR.forType(List.class);
        _verifyArray(strictR.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_ARRAY)));
        _verifyCollection((List<?>)strictRForList.readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_ARRAY)));
    }

    public void testReaderFailOnTrailingWithNull() throws Exception
    {
        ObjectReader strictR = MAPPER.reader().with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        ObjectReader strictRForList = strictR.forType(List.class);
        JsonNode n = strictR.readTree(com.fasterxml.jackson.VPackUtils.toBytes(JSON_OK_NULL));
        assertTrue(n.isNull());
    }
    
    private void _verifyArray(JsonNode n) throws Exception
    {
        assertTrue(n.isArray());
        assertEquals(3, n.size());
    }

    private void _verifyCollection(List<?> coll) throws Exception
    {
        assertEquals(3, coll.size());
        assertEquals(Integer.valueOf(1), coll.get(0));
        assertEquals(Integer.valueOf(2), coll.get(1));
        assertEquals(Integer.valueOf(3), coll.get(2));
    }
}
