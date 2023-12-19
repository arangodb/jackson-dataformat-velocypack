package com.fasterxml.jackson.databind.ser.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

import java.io.IOException;

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

    public void testNullSerializerForProperty() throws Exception
    {
        assertEquals("{\"a\":\"foobar\"}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new BeanWithNullProps())));
    }
}
