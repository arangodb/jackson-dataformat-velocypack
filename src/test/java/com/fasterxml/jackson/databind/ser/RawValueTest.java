package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This unit test suite tests functioning of {@link JsonRawValue}
 * annotation with bean serialization.
 */
public class RawValueTest
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    /*
    /*********************************************************
    /* Helper bean classes
    /*********************************************************
     */

    /// Class for testing {@link JsonRawValue} annotations with getters returning String
    @JsonPropertyOrder(alphabetic=true)
    final static class ClassGetter<T>
    {
        protected final T _value;
    	
        protected ClassGetter(T value) { _value = value;}
 
        public T getNonRaw() { return _value; }

        @JsonProperty("raw") @JsonRawValue public T foobar() { return _value; }
        
        @JsonProperty @JsonRawValue protected T value() { return _value; }
    }

    // [databind#348]
    static class RawWrapped
    {
        @JsonRawValue
        private final String json;

        public RawWrapped(String str) {
            json = str;
        }
    }

    /*
    /*********************************************************
    /* Test cases
    /*********************************************************
     */

    private final ObjectMapper MAPPER = objectMapper();
    

    public void testNullStringGetter() throws Exception
    {
        String result = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new ClassGetter<String>(null)));
        String expected = "{\"nonRaw\":null,\"raw\":null,\"value\":null}";
        assertEquals(expected, result);
    }

}
