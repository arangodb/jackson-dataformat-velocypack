package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Tests to verify that Type Id may be exposed during deserialization,
 */
public class TestVisibleTypeId extends BaseMapTest
{
    // type id as property, exposed
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY,
            property="type", visible=true)
    @JsonTypeName("BaseType")
    static class PropertyBean {
        public int a = 3;

        protected String type;

        public void setType(String t) { type = t; }
    }

    // as wrapper-array
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_ARRAY,
            property="type", visible=true)
    @JsonTypeName("ArrayType")
    static class WrapperArrayBean {
        public int a = 1;

        protected String type;

        public void setType(String t) { type = t; }
    }

    // as wrapper-object
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT,
            property="type", visible=true)
    @JsonTypeName("ObjectType")
    static class WrapperObjectBean {
        public int a = 2;

        protected String type;

        public void setType(String t) { type = t; }
    }

    @JsonTypeName("ExternalType")
    static class ExternalIdBean {
        public int a = 2;

        protected String type;

        public void setType(String t) { type = t; }
    }

    // // // [JACKSON-762]: type id from property
    
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY,
            property="type")
    static class TypeIdFromFieldProperty {
        public int a = 3;

        @JsonTypeId
        public String type = "SomeType";
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_ARRAY,
            property="type")
    static class TypeIdFromFieldArray {
        public int a = 3;
        @JsonTypeId
        public String type = "SomeType";
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT,
            property="type")
    static class TypeIdFromMethodObject {
        public int a = 3;
        
        @JsonTypeId
        public String getType() { return "SomeType"; }
    }

    static class ExternalIdWrapper2 {
        @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property="type", visible=true)
        public ExternalIdBean2 bean = new ExternalIdBean2();
    }

    static class ExternalIdBean2 {
        public int a = 2;

        /* Type id property itself cannot be external, as it is conceptually
         * part of the bean for which info is written:
         */
        @JsonTypeId
        public String getType() { return "SomeType"; }
    }

    // Invalid definition: multiple type ids
    static class MultipleIds {
        @JsonTypeId
        public String type1 = "type1";

        @JsonTypeId
        public String getType2() { return "type2"; };
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name")
    @JsonSubTypes({ @JsonSubTypes.Type(value=I263Impl.class) })
    public static abstract class I263Base {
        @JsonTypeId
        public abstract String getName();
    }

    @JsonPropertyOrder({ "age", "name" })
    @JsonTypeName("bob")
    public static class I263Impl extends I263Base
    {
        @Override
        public String getName() { return "bob"; }
        
        public int age = 41;
    }

    // [databind#408]
    static class ExternalBeanWithId
    {
        protected String _type;
        
        @JsonTypeInfo(use=Id.NAME, include=As.EXTERNAL_PROPERTY, property="type", visible=true)
        public ValueBean bean;

        public ExternalBeanWithId() { }
        public ExternalBeanWithId(int v) {
            bean = new ValueBean(v);
        }

        public void setType(String t) {
            _type = t;
        }
    }

    @JsonTypeName("vbean")
    static class ValueBean {
        public int value;
        
        public ValueBean() { }
        public ValueBean(int v) { value = v; }
    }
    
    /*
    /**********************************************************
    /* Unit tests, success
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new TestVelocypackMapper();
    
    public void testVisibleWithProperty() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new PropertyBean()));
        // just default behavior:
        assertEquals("{\"type\":\"BaseType\",\"a\":3}", json);
        // but then expect to read it back
        PropertyBean result = MAPPER.readValue(json, PropertyBean.class);
        assertEquals("BaseType", result.type);

        // also, should work with order reversed
        result = MAPPER.readValue("{\"a\":7, \"type\":\"BaseType\"}", PropertyBean.class);
        assertEquals(7, result.a);
        assertEquals("BaseType", result.type);
    }

    public void testVisibleWithWrapperArray() throws Exception {
        byte[] bytes = MAPPER.writeValueAsBytes(new WrapperArrayBean());
        assertEquals("[\"ArrayType\",{\"a\":1}]", com.fasterxml.jackson.VPackUtils.toJson(bytes));
        WrapperArrayBean result = MAPPER.readValue(bytes, WrapperArrayBean.class);
        assertEquals("ArrayType", result.type);
        assertEquals(1, result.a);
    }

    public void testVisibleWithWrapperObject() throws Exception {
        byte[] bytes = MAPPER.writeValueAsBytes(new WrapperObjectBean());
        assertEquals("{\"ObjectType\":{\"a\":2}}", com.fasterxml.jackson.VPackUtils.toJson(bytes));
        WrapperObjectBean result = MAPPER.readValue(bytes, WrapperObjectBean.class);
        assertEquals("ObjectType", result.type);
        assertEquals(2, result.a);

    }

    public void testTypeIdFromProperty() throws Exception
    {
        assertEquals("{\"type\":\"SomeType\",\"a\":3}", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new TypeIdFromFieldProperty())));
    }

    public void testTypeIdFromArray() throws Exception
    {
        assertEquals("[\"SomeType\",{\"a\":3}]", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new TypeIdFromFieldArray())));
    }

    public void testTypeIdFromObject() throws Exception
    {
        assertEquals("{\"SomeType\":{\"a\":3}}", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new TypeIdFromMethodObject())));
    }

    public void testTypeIdFromExternal() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new ExternalIdWrapper2()));
        // Implementation detail: type id written AFTER value, due to constraints
        assertEquals("{\"bean\":{\"a\":2},\"type\":\"SomeType\"}", json);
        
    }
    
    public void testIssue263() throws Exception
    {
        // first, serialize:
        assertEquals("{\"name\":\"bob\",\"age\":41}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new I263Impl())));
        
        // then bring back:
        I263Base result = MAPPER.readValue("{\"age\":19,\"name\":\"bob\"}", I263Base.class);
        assertTrue(result instanceof I263Impl);
        assertEquals(19, ((I263Impl) result).age);
    }

    // [databind#408]
    /* NOTE: Handling changed between 2.4 and 2.5; earlier, type id was 'injected'
     *  inside POJO; but with 2.5 this was fixed so it would remain outside, similar
     *  to how JSON structure is.
     */
    public void testVisibleTypeId408() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new ExternalBeanWithId(3)));
        ExternalBeanWithId result = MAPPER.readValue(json, ExternalBeanWithId.class);
        assertNotNull(result);
        assertNotNull(result.bean);
        assertEquals(3, result.bean.value);
        assertEquals("vbean", result._type);
    }
    
    /*
    /**********************************************************
    /* Unit tests, fails
    /**********************************************************
     */

    public void testInvalidMultipleTypeIds() throws Exception
    {
        try {
            MAPPER.writeValueAsBytes(new MultipleIds());
            fail("Should have failed");
        } catch (JsonMappingException e) {
            verifyException(e, "multiple type ids");
        }
    }
}
