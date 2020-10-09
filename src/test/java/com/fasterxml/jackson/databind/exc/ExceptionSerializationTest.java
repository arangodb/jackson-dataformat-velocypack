package com.fasterxml.jackson.databind.exc;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Unit tests for verifying that simple exceptions can be serialized.
 */
public class ExceptionSerializationTest
    extends BaseMapTest
{
    @SuppressWarnings("serial")
    @JsonIgnoreProperties({ "bogus1" })
    static class ExceptionWithIgnoral extends RuntimeException
    {
        public int bogus1 = 3;

        public int bogus2 = 5;

        protected ExceptionWithIgnoral() { }
        public ExceptionWithIgnoral(String msg) {
            super(msg);
        }
    }

    // [databind#1368]
    static class NoSerdeConstructor {
        private String strVal;
        public String getVal() { return strVal; }
        public NoSerdeConstructor( String strVal ) {
            this.strVal = strVal;
        }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testSimple() throws Exception
    {
        String TEST = "test exception";
        Map<String,Object> result = writeAndMap(MAPPER, new Exception(TEST));
        // JDK 7 has introduced a new property 'suppressed' to Throwable
        Object ob = result.get("suppressed");
        if (ob != null) {
            assertEquals(5, result.size());
        } else {
            assertEquals(4, result.size());
        }

        assertEquals(TEST, result.get("message"));
        assertNull(result.get("cause"));
        assertEquals(TEST, result.get("localizedMessage"));

        // hmmh. what should we get for stack traces?
        Object traces = result.get("stackTrace");
        if (!(traces instanceof List<?>)) {
            fail("Expected a List for exception member 'stackTrace', got: "+traces);
        }
    }

    // to double-check [databind#1413]
    public void testSimpleOther() throws Exception
    {
        JsonParser p = MAPPER.getFactory().createParser(com.fasterxml.jackson.VPackUtils.toBytes("{ }"));
        InvalidFormatException exc = InvalidFormatException.from(p, "Test", getClass(), String.class);
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(exc));
        p.close();
        assertNotNull(json);
    }
    
    // for [databind#877]
    @SuppressWarnings("unchecked")
    public void testIgnorals() throws Exception
    {
        ExceptionWithIgnoral input = new ExceptionWithIgnoral("foobar");
        input.initCause(new IOException("surprise!"));

        // First, should ignore anything with class annotations
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER
                .writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(input));

        Map<String,Object> result = MAPPER.readValue(json, Map.class);
        assertEquals("foobar", result.get("message"));

        assertNull(result.get("bogus1"));
        assertNotNull(result.get("bogus2"));

        // and then also remova second property with config overrides
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.configOverride(ExceptionWithIgnoral.class)
            .setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("bogus2"));
        String json2 = com.fasterxml.jackson.VPackUtils.toJson( mapper
                .writeValueAsBytes(new ExceptionWithIgnoral("foobar")));

        Map<String,Object> result2 = mapper.readValue(json2, Map.class);
        assertNull(result2.get("bogus1"));
        assertNull(result2.get("bogus2"));

        // and try to deserialize as well
        ExceptionWithIgnoral output = mapper.readValue(json2, ExceptionWithIgnoral.class);
        assertNotNull(output);
        assertEquals("foobar", output.getMessage());
    }

    // [databind#1368]
    public void testJsonMappingExceptionSerialization() throws IOException {
        Exception e = null;
        // cant deserialize due to unexpected constructor
        try {
            MAPPER.readValue( "{ \"val\": \"foo\" }", NoSerdeConstructor.class );
            fail("Should not pass");
        } catch (JsonMappingException e0) {
            verifyException(e0, "cannot deserialize from Object");
            e = e0;
        }
        // but should be able to serialize new exception we got
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(e));
        JsonNode root = MAPPER.readTree(com.fasterxml.jackson.VPackUtils.toBytes(json));
        String msg = root.path("message").asText();
        String MATCH = "cannot construct instance";
        if (!msg.toLowerCase().contains(MATCH)) {
            fail("Exception should contain '"+MATCH+"', does not: '"+msg+"'");
        }
    }
}
