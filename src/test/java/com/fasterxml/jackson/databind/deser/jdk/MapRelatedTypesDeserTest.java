package com.fasterxml.jackson.databind.deser.jdk;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

public class MapRelatedTypesDeserTest
    extends BaseMapTest
{
    private final ObjectMapper MAPPER = newJsonMapper();

    /*
    /**********************************************************
    /* Test methods, Map.Entry
    /**********************************************************
     */

    public void testMapEntrySimpleTypes() throws Exception
    {
        List<Map.Entry<String,Long>> stuff = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(aposToQuotes("[{'a':15},{'b':42}]")),
                new TypeReference<List<Map.Entry<String,Long>>>() { });
        assertNotNull(stuff);
        assertEquals(2, stuff.size());
        assertNotNull(stuff.get(1));
        assertEquals("b", stuff.get(1).getKey());
        assertEquals(Long.valueOf(42), stuff.get(1).getValue());
    }

    public void testMapEntryWithStringBean() throws Exception
    {
        List<Map.Entry<Integer,StringWrapper>> stuff = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(aposToQuotes("[{'28':'Foo'},{'13':'Bar'}]")),
                new TypeReference<List<Map.Entry<Integer,StringWrapper>>>() { });
        assertNotNull(stuff);
        assertEquals(2, stuff.size());
        assertNotNull(stuff.get(1));
        assertEquals(Integer.valueOf(13), stuff.get(1).getKey());
        
        StringWrapper sw = stuff.get(1).getValue();
        assertEquals("Bar", sw.str);
    }

    public void testMapEntryFail() throws Exception
    {
        try {
            /*List<Map.Entry<Integer,StringWrapper>> stuff =*/ MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(aposToQuotes("[{'28':'Foo','13':'Bar'}]")),
                    new TypeReference<List<Map.Entry<Integer,StringWrapper>>>() { });
            fail("Should not have passed");
        } catch (Exception e) {
            verifyException(e, "more than one entry in JSON");
        }
    }

    /*
    /**********************************************************
    /* Test methods, other exotic Map types
    /**********************************************************
     */
    
    // [databind#810]
    public void testReadProperties() throws Exception
    {
        Properties props = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toBytes(aposToQuotes("{'a':'foo', 'b':123}")),
                Properties.class);
        assertEquals(2, props.size());
        assertEquals("foo", props.getProperty("a"));
        assertEquals("123", props.getProperty("b"));
    }

    // JDK singletonMap
    public void testSingletonMapRoundtrip() throws Exception
    {
        final TypeReference<Map<String,IntWrapper>> type = new TypeReference<Map<String,IntWrapper>>() { };

        byte[] bytes = MAPPER.writeValueAsBytes(Collections.singletonMap("value", new IntWrapper(5)));
        Map<String,IntWrapper> result = MAPPER.readValue(bytes, type);
        assertNotNull(result);
        assertEquals(1, result.size());
        IntWrapper w = result.get("value");
        assertNotNull(w);
        assertEquals(5, w.i);
    }
}
