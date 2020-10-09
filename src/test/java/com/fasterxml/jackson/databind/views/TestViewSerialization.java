package com.fasterxml.jackson.databind.views;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Unit tests for verifying JSON view functionality: ability to declaratively
 * suppress subset of properties from being serialized.
 */
public class TestViewSerialization
    extends BaseMapTest
{
    // Classes that represent views
    static class ViewA { }
    static class ViewAA extends ViewA { }
    static class ViewB { }
    static class ViewBB extends ViewB { }
    
    static class Bean
    {
        @JsonView(ViewA.class)
        public String a = "1";

        @JsonView({ViewAA.class, ViewB.class})
        public String aa = "2";

        @JsonView(ViewB.class)
        public String getB() { return "3"; }
    }

    /**
     * Bean with mix of explicitly annotated
     * properties, and implicit ones that may or may
     * not be included in views.
     */
    static class MixedBean
    {
        @JsonView(ViewA.class)
        public String a = "1";

        public String getB() { return "2"; }
    }

    /**
     * As indicated by [JACKSON-261], @JsonView should imply
     * that associated element (method, field) is to be considered
     * a property
     */
    static class ImplicitBean {
        @JsonView(ViewA.class)
        private int a = 1;
    }

    static class VisibilityBean {
        @JsonProperty protected String id = "id";
    
        @JsonView(ViewA.class)
        public String value = "x";
    }   

    public static class WebView { }
    public static class OtherView { }
    public static class Foo {
        @JsonView(WebView.class)
        public int getFoo() { return 3; }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */    

    private final ObjectMapper MAPPER = objectMapper();

    @SuppressWarnings("unchecked")
    public void testSimple() throws IOException
    {
        // Ok, first, using no view whatsoever; all 3
        Bean bean = new Bean();
        Map<String,Object> map = writeAndMap(MAPPER, bean);
        assertEquals(3, map.size());

        // Then with "ViewA", just one property
        byte[] bytes = MAPPER.writerWithView(ViewA.class).writeValueAsBytes(bean);
        map = MAPPER.readValue(bytes, Map.class);
        assertEquals(1, map.size());
        assertEquals("1", map.get("a"));

        // "ViewAA", 2 properties
        bytes = MAPPER.writerWithView(ViewAA.class).writeValueAsBytes( bean);
        map = MAPPER.readValue(bytes, Map.class);
        assertEquals(2, map.size());
        assertEquals("1", map.get("a"));
        assertEquals("2", map.get("aa"));

        // "ViewB", 2 prop2
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writerWithView(ViewB.class).writeValueAsBytes(bean));
        map = MAPPER.readValue(json, Map.class);
        assertEquals(2, map.size());
        assertEquals("2", map.get("aa"));
        assertEquals("3", map.get("b"));

        // and "ViewBB", 2 as well
        json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writerWithView(ViewBB.class).writeValueAsBytes(bean));
        map = MAPPER.readValue(json, Map.class);
        assertEquals(2, map.size());
        assertEquals("2", map.get("aa"));
        assertEquals("3", map.get("b"));

        // and finally, without view.
        json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writerWithView(null).writeValueAsBytes(bean));
        map = MAPPER.readValue(json, Map.class);
        assertEquals(3, map.size());
    }

    /**
     * Unit test to verify implementation of [JACKSON-232], to
     * allow "opt-in" handling for JSON Views: that is, that
     * default for properties is to exclude unless included in
     * a view.
     */
    @SuppressWarnings("unchecked")
    public void testDefaultExclusion() throws IOException
    {
        MixedBean bean = new MixedBean();

        // default setting: both fields will get included
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writerWithView(ViewA.class).writeValueAsBytes(bean));
        Map<String,Object> map = MAPPER.readValue(json, Map.class);
        assertEquals(2, map.size());
        assertEquals("1", map.get("a"));
        assertEquals("2", map.get("b"));

        // but can also change (but not necessarily on the fly...)
        ObjectMapper mapper = jsonMapperBuilder()
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .build();

        // with this setting, only explicit inclusions count:
        json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writerWithView(ViewA.class).writeValueAsBytes(bean));
        map = mapper.readValue(json, Map.class);
        assertEquals(1, map.size());
        assertEquals("1", map.get("a"));
        assertNull(map.get("b"));

        // but without view, view processing disabled:
        json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writer().withView(null).writeValueAsBytes(bean));
        map = mapper.readValue(json, Map.class);
        assertEquals(2, map.size());
        assertEquals("1", map.get("a"));
        assertEquals("2", map.get("b"));
    }

    /**
     * As per [JACKSON-261], @JsonView annotation should imply that associated
     * method/field does indicate a property.
     */
    public void testImplicitAutoDetection() throws Exception
    {
        assertEquals("{\"a\":1}", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new ImplicitBean())));
    }

    public void testVisibility() throws Exception
    {
        VisibilityBean bean = new VisibilityBean();
        // Without view setting, should only see "id"
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writerWithView(Object.class).writeValueAsBytes(bean));
        //json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(bean));
        assertEquals("{\"id\":\"id\"}", json);
    }

    // [JACKSON-868]
    public void test868() throws IOException
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writerWithView(OtherView.class).writeValueAsBytes(new Foo()));
        assertEquals(json, "{}");
    }    
}
