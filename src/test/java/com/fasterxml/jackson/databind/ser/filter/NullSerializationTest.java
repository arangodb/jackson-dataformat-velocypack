package com.fasterxml.jackson.databind.ser.filter;

import java.io.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class NullSerializationTest
    extends BaseMapTest
{
    static class NullSerializer extends JsonSerializer<Object>
    {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
            throws IOException
        {
            gen.writeString("foobar");
        }
    }

    static class Bean1 {
        public String name = null;
    }

    static class Bean2 {
        public String type = null;
    }
    
    @SuppressWarnings("serial")
    static class MyNullProvider extends DefaultSerializerProvider
    {
        public MyNullProvider() { super(); }
        public MyNullProvider(MyNullProvider base, SerializationConfig config, SerializerFactory jsf) {
            super(base, config, jsf);
        }

        // not really a proper impl, but has to do
        @Override
        public DefaultSerializerProvider copy() {
            return this;
        }
        
        @Override
        public DefaultSerializerProvider createInstance(SerializationConfig config, SerializerFactory jsf) {
            return new MyNullProvider(this, config, jsf);
        }

        @Override
        public JsonSerializer<Object> findNullValueSerializer(BeanProperty property)
            throws JsonMappingException
        {
            if ("name".equals(property.getName())) {
                return new NullSerializer();
            }
            return super.findNullValueSerializer(property);
        }
    }

    static class BeanWithNullProps
    {
        @JsonSerialize(nullsUsing=NullSerializer.class)
        public String a = null;
    }

/*
    @JsonSerialize(nullsUsing=NullSerializer.class)
    static class NullValuedType { }
*/
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = objectMapper();
    
    public void testSimple() throws Exception
    {
        assertEquals("null", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(null)));
    }

    public void testOverriddenDefaultNulls() throws Exception
    {
        DefaultSerializerProvider sp = new DefaultSerializerProvider.Impl();
        sp.setNullValueSerializer(new NullSerializer());
        ObjectMapper m = new TestVelocypackMapper();
        m.setSerializerProvider(sp);
        assertEquals("\"foobar\"", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(null)));
    }

    public void testCustomNulls() throws Exception
    {
        ObjectMapper m = new TestVelocypackMapper();
        m.setSerializerProvider(new MyNullProvider());
        assertEquals("{\"name\":\"foobar\"}", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(new Bean1())));
        assertEquals("{\"type\":null}", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(new Bean2())));
    }

    // #281
    public void testCustomNullForTrees() throws Exception
    {
        ObjectNode root = MAPPER.createObjectNode();
        root.putNull("a");

        // by default, null is... well, null
        assertEquals("{\"a\":null}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(root)));

        // but then we can customize it:
        DefaultSerializerProvider prov = new MyNullProvider();
        prov.setNullValueSerializer(new NullSerializer());
        ObjectMapper m = new TestVelocypackMapper();
        m.setSerializerProvider(prov);
        assertEquals("{\"a\":\"foobar\"}", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(root)));
    }

    public void testNullSerializerForProperty() throws Exception
    {
        assertEquals("{\"a\":\"foobar\"}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new BeanWithNullProps())));
    }
}
