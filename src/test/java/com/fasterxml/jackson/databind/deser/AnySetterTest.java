package com.fasterxml.jackson.databind.deser;

import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Unit tests for verifying that {@link JsonAnySetter} annotation
 * works as expected.
 */
public class AnySetterTest
    extends BaseMapTest
{
    static class MapImitator
    {
        HashMap<String,Object> _map;

        public MapImitator() {
            _map = new HashMap<String,Object>();
        }

        @JsonAnySetter
        void addEntry(String key, Object value)
        {
            _map.put(key, value);
        }
    }

    // for [databind#1376]
    static class MapImitatorDisabled extends MapImitator
    {
        @Override
        @JsonAnySetter(enabled=false)
        void addEntry(String key, Object value) {
            throw new RuntimeException("Should not get called");
        }
    }

    /**
     * Let's also verify that it is possible to define different
     * value: not often useful, but possible.
     */
    static class MapImitatorWithValue
    {
        HashMap<String,int[]> _map;

        public MapImitatorWithValue() {
            _map = new HashMap<String,int[]>();
        }

        @JsonAnySetter
        void addEntry(String key, int[] value)
        {
            _map.put(key, value);
        }
    }

    // Bad; 2 "any setters"
    static class Broken
    {
        @JsonAnySetter
        void addEntry1(String key, Object value) { }
        @JsonAnySetter
        void addEntry2(String key, Object value) { }
    }

    @JsonIgnoreProperties("dummy")
    static class Ignored
    {
        HashMap<String,Object> map = new HashMap<String,Object>();
 
        @JsonIgnore
        public String bogus;
        
        @JsonAnySetter
        void addEntry(String key, Object value)
        {
            map.put(key, value);
        }        
    }

    static class Bean744
    {
        protected Map<String,Object> additionalProperties;
        
        @JsonAnySetter
        public void addAdditionalProperty(String key, Object value) {
            if (additionalProperties == null) additionalProperties = new HashMap<String, Object>();
            additionalProperties.put(key,value);
        }
        
        public void setAdditionalProperties(Map<String, Object> additionalProperties) {
            this.additionalProperties = additionalProperties;
        }

        @JsonAnyGetter
        public Map<String,Object> getAdditionalProperties() { return additionalProperties; }

        @JsonIgnore
        public String getName() {
           return (String) additionalProperties.get("name");
        }
    }

    static class Bean797Base
    {
        @JsonAnyGetter
        public Map<String, JsonNode> getUndefinedProperties() {
            throw new IllegalStateException("Should not call parent version!");
        }
    }

    static class Bean797BaseImpl extends Bean797Base
    {
        @Override
        public Map<String, JsonNode> getUndefinedProperties() {
            return new HashMap<String, JsonNode>();
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static abstract class Base { }

    static class Impl extends Base {
        public String value;
        
        public Impl() { }
        public Impl(String v) { value = v; }
    }

    static class PolyAnyBean
    {
        protected Map<String,Base> props = new HashMap<String,Base>();
        
        @JsonAnyGetter
        public Map<String,Base> props() {
            return props;
        }

        @JsonAnySetter
        public void prop(String name, Base value) {
            props.put(name, value);
        }
    }
    
    static class JsonAnySetterOnMap {
        public int id;

        @JsonAnySetter
        protected HashMap<String, String> other = new HashMap<String, String>();

        @JsonAnyGetter
        public Map<String, String> any() {
            return other;
        }
    }

    static class JsonAnySetterOnNullMap {
        public int id;

        @JsonAnySetter
        protected HashMap<String, String> other;

        @JsonAnyGetter
        public Map<String, String> any() {
            return other;
        }
    }

    static class MyGeneric<T>
    {
        private String staticallyMappedProperty;
        private Map<T, Integer> dynamicallyMappedProperties = new HashMap<T, Integer>();
    
        public String getStaticallyMappedProperty() {
            return staticallyMappedProperty;
        }
    
        @JsonAnySetter
        public void addDynamicallyMappedProperty(T key, int value) {
            dynamicallyMappedProperties.put(key, value);
        }

        public void setStaticallyMappedProperty(String staticallyMappedProperty) {
            this.staticallyMappedProperty = staticallyMappedProperty;
        }
    
        @JsonAnyGetter
        public Map<T, Integer> getDynamicallyMappedProperties() {
            return dynamicallyMappedProperties;
        }
    }

    static class MyWrapper
    {
        private MyGeneric<String> myStringGeneric;
        private MyGeneric<Integer> myIntegerGeneric;

        public MyGeneric<String> getMyStringGeneric() {
            return myStringGeneric;
        }

        public void setMyStringGeneric(MyGeneric<String> myStringGeneric) {
            this.myStringGeneric = myStringGeneric;
        }

        public MyGeneric<Integer> getMyIntegerGeneric() {
            return myIntegerGeneric;
        }

        public void setMyIntegerGeneric(MyGeneric<Integer> myIntegerGeneric) {
            this.myIntegerGeneric = myIntegerGeneric;
        }
    }

	/*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new TestVelocypackMapper();
    
    public void testSimpleMapImitation() throws Exception
    {
        MapImitator mapHolder = MAPPER.readValue
            (com.fasterxml.jackson.VPackUtils.toVPack("{ \"a\" : 3, \"b\" : true, \"c\":[1,2,3] }"), MapImitator.class);
        Map<String,Object> result = mapHolder._map;
        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(3), result.get("a"));
        assertEquals(Boolean.TRUE, result.get("b"));
        Object ob = result.get("c");
        assertTrue(ob instanceof List<?>);
        List<?> l = (List<?>)ob;
        assertEquals(3, l.size());
        assertEquals(Integer.valueOf(3), l.get(2));
    }

    public void testAnySetterDisable() throws Exception
    {
        try {
            MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toVPack(aposToQuotes("{'value':3}")),
                    MapImitatorDisabled.class);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Unrecognized field \"value\"");
        }

    }

    public void testSimpleTyped() throws Exception
    {
        MapImitatorWithValue mapHolder = MAPPER.readValue
            (com.fasterxml.jackson.VPackUtils.toVPack("{ \"a\" : [ 3, -1 ], \"b\" : [ ] }"), MapImitatorWithValue.class);
        Map<String,int[]> result = mapHolder._map;
        assertEquals(2, result.size());
        assertEquals(new int[] { 3, -1 }, result.get("a"));
        assertEquals(new int[0], result.get("b"));
    }

    public void testBrokenWithDoubleAnnotations() throws Exception
    {
        try {
            @SuppressWarnings("unused")
            Broken b = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{ \"a\" : 3 }"), Broken.class);
            fail("Should have gotten an exception");
        } catch (JsonMappingException e) {
            verifyException(e, "Multiple 'any-setter' methods");
        }
    }

    public void testIgnored() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        _testIgnorals(mapper);
    }

    public void testIgnoredPart2() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        _testIgnorals(mapper);
    }

    public void testProblem744() throws Exception
    {
        Bean744 bean = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"name\":\"Bob\"}"), Bean744.class);
        assertNotNull(bean.additionalProperties);
        assertEquals(1, bean.additionalProperties.size());
        assertEquals("Bob", bean.additionalProperties.get("name"));
    }

    public void testIssue797() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new Bean797BaseImpl()));
        assertEquals("{}", json);
    }

    // [Issue#337]
    public void testPolymorphic() throws Exception
    {
        PolyAnyBean input = new PolyAnyBean();
        input.props.put("a", new Impl("xyz"));

        byte[] bytes = (MAPPER.writeValueAsBytes(input));
        
//        System.err.println("JSON: "+json);

        PolyAnyBean result = MAPPER.readValue((bytes), PolyAnyBean.class);
        assertEquals(1, result.props.size());
        Base ob = result.props.get("a");
        assertNotNull(ob);
        assertTrue(ob instanceof Impl);
        assertEquals("xyz", ((Impl) ob).value);
    }
    
	public void testJsonAnySetterOnMap() throws Exception {
		JsonAnySetterOnMap result = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"id\":2,\"name\":\"Joe\", \"city\":\"New Jersey\"}"),
		        JsonAnySetterOnMap.class);
		assertEquals(2, result.id);
		assertEquals("Joe", result.other.get("name"));
		assertEquals("New Jersey", result.other.get("city"));
	}

    // [databind#1035]
    public void testGenericAnySetter() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();

        Map<String, Integer> stringGenericMap = new HashMap<String, Integer>();
        stringGenericMap.put("testStringKey", 5);
        Map<Integer, Integer> integerGenericMap = new HashMap<Integer, Integer>();
        integerGenericMap.put(111, 6);

        MyWrapper deserialized = mapper.readValue(com.fasterxml.jackson.VPackUtils.toVPack(aposToQuotes(
                "{'myStringGeneric':{'staticallyMappedProperty':'Test','testStringKey':5},'myIntegerGeneric':{'staticallyMappedProperty':'Test2','111':6}}"
        )), MyWrapper.class);
        MyGeneric<String> stringGeneric = deserialized.getMyStringGeneric();
        MyGeneric<Integer> integerGeneric = deserialized.getMyIntegerGeneric();

        assertNotNull(stringGeneric);
        assertEquals(stringGeneric.getStaticallyMappedProperty(), "Test");
        for(Map.Entry<String, Integer> entry : stringGeneric.getDynamicallyMappedProperties().entrySet()) {
            assertTrue("A key in MyGeneric<String> is not an String.", entry.getKey() instanceof String);
            assertTrue("A value in MyGeneric<Integer> is not an Integer.", entry.getValue() instanceof Integer);
        }
        assertEquals(stringGeneric.getDynamicallyMappedProperties(), stringGenericMap);

        assertNotNull(integerGeneric);
        assertEquals(integerGeneric.getStaticallyMappedProperty(), "Test2");
        for(Map.Entry<Integer, Integer> entry : integerGeneric.getDynamicallyMappedProperties().entrySet()) {
            Object key = entry.getKey();
            assertEquals("A key in MyGeneric<Integer> is not an Integer.", Integer.class, key.getClass());
            Object value = entry.getValue();
            assertEquals("A value in MyGeneric<Integer> is not an Integer.", Integer.class, value.getClass());
        }
        assertEquals(integerGeneric.getDynamicallyMappedProperties(), integerGenericMap);
    }

    /*
    /**********************************************************
    /* Private helper methods
    /**********************************************************
     */

    private void _testIgnorals(ObjectMapper mapper) throws Exception
    {
        Ignored bean = mapper.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"name\":\"Bob\", \"bogus\": [ 1, 2, 3], \"dummy\" : 13 }"), Ignored.class);
        // as of 2.0, @JsonIgnoreProperties does block; @JsonIgnore not
        assertNull(bean.map.get("dummy"));
        assertEquals("[1, 2, 3]", ""+bean.map.get("bogus"));
        assertEquals("Bob", bean.map.get("name"));
        assertEquals(2, bean.map.size());
    }
}
