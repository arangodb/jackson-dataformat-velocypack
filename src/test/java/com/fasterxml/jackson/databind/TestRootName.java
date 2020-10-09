package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Unit tests dealing with handling of "root element wrapping",
 * including configuration of root name to use.
 */
public class TestRootName extends BaseMapTest
{
    @JsonRootName("rudy")
    static class Bean {
        public int a = 3;
    }
    
    @JsonRootName("")
    static class RootBeanWithEmpty {
        public int a = 2;
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    public void testRootViaMapper() throws Exception
    {
        ObjectMapper mapper = rootMapper();
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(new Bean()));
        assertEquals("{\"rudy\":{\"a\":3}}", json);
        Bean bean = mapper.readValue(json, Bean.class);
        assertNotNull(bean);

        // also same with explicitly "not defined"...
        json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(new RootBeanWithEmpty()));
        assertEquals("{\"RootBeanWithEmpty\":{\"a\":2}}", json);
        RootBeanWithEmpty bean2 = mapper.readValue(json, RootBeanWithEmpty.class);
        assertNotNull(bean2);
        assertEquals(2, bean2.a);
    }

    public void testRootViaWriterAndReader() throws Exception
    {
        ObjectMapper mapper = rootMapper();
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writer().writeValueAsBytes(new Bean()));
        assertEquals("{\"rudy\":{\"a\":3}}", json);
        Bean bean = mapper.readerFor(Bean.class).readValue(com.fasterxml.jackson.VPackUtils.toBytes(json));
        assertNotNull(bean);
    }

    public void testReconfiguringOfWrapping() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        // default: no wrapping
        final Bean input = new Bean();
        String jsonUnwrapped = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input));
        assertEquals("{\"a\":3}", jsonUnwrapped);
        // secondary: wrapping
        String jsonWrapped = com.fasterxml.jackson.VPackUtils.toJson( mapper.writer(SerializationFeature.WRAP_ROOT_VALUE)
            .writeValueAsBytes(input));
        assertEquals("{\"rudy\":{\"a\":3}}", jsonWrapped);

        // and then similarly for readers:
        Bean result = mapper.readValue(jsonUnwrapped, Bean.class);
        assertNotNull(result);
        try { // must not have extra wrapping
            result = mapper.readerFor(Bean.class).with(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .readValue(com.fasterxml.jackson.VPackUtils.toBytes(jsonUnwrapped));
            fail("Should have failed");
        } catch (JsonMappingException e) {
            verifyException(e, "Root name 'a'");
        }
        // except wrapping may be expected:
        result = mapper.readerFor(Bean.class).with(DeserializationFeature.UNWRAP_ROOT_VALUE)
            .readValue(com.fasterxml.jackson.VPackUtils.toBytes(jsonWrapped));
        assertNotNull(result);
    }
    
    // [JACKSON-764]
    public void testRootUsingExplicitConfig() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        ObjectWriter writer = mapper.writer().withRootName("wrapper");
        String json = com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new Bean()));
        assertEquals("{\"wrapper\":{\"a\":3}}", json);

        ObjectReader reader = mapper.readerFor(Bean.class).withRootName("wrapper");
        Bean bean = reader.readValue(com.fasterxml.jackson.VPackUtils.toBytes(json));
        assertNotNull(bean);

        // also: verify that we can override SerializationFeature as well:
        ObjectMapper wrapping = rootMapper();
        json = com.fasterxml.jackson.VPackUtils.toJson( wrapping.writer().withRootName("something").writeValueAsBytes(new Bean()));
        assertEquals("{\"something\":{\"a\":3}}", json);
        json = com.fasterxml.jackson.VPackUtils.toJson( wrapping.writer().withRootName("").writeValueAsBytes(new Bean()));
        assertEquals("{\"a\":3}", json);

        // 21-Apr-2015, tatu: Alternative available with 2.6 as well:
        json = com.fasterxml.jackson.VPackUtils.toJson( wrapping.writer().withoutRootName().writeValueAsBytes(new Bean()));
        assertEquals("{\"a\":3}", json);

        bean = wrapping.readerFor(Bean.class).withRootName("").readValue(com.fasterxml.jackson.VPackUtils.toBytes(json));
        assertNotNull(bean);
        assertEquals(3, bean.a);

        bean = wrapping.readerFor(Bean.class).withoutRootName().readValue(com.fasterxml.jackson.VPackUtils.toBytes("{\"a\":4}"));
        assertNotNull(bean);
        assertEquals(4, bean.a);

        // and back to defaults
        bean = wrapping.readerFor(Bean.class).readValue(com.fasterxml.jackson.VPackUtils.toBytes("{\"rudy\":{\"a\":7}}"));
        assertNotNull(bean);
        assertEquals(7, bean.a);
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private ObjectMapper rootMapper()
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        return mapper;
    }
}
