package com.fasterxml.jackson.databind.exc;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

import static com.fasterxml.jackson.TestUtils.isAtLeastVersion;

/**
 * Unit tests for verifying that simple exceptions can be deserialized.
 */
public class ExceptionDeserializationTest
    extends BaseMapTest
{
    @SuppressWarnings("serial")
    static class MyException extends Exception
    {
        protected int value;

        protected String myMessage;
        protected HashMap<String,Object> stuff = new HashMap<String, Object>();
        
        @JsonCreator
        MyException(@JsonProperty("message") String msg, @JsonProperty("value") int v)
        {
            super(msg);
            myMessage = msg;
            value = v;
        }

        public int getValue() { return value; }
        
        public String getFoo() { return "bar"; }

        @JsonAnySetter public void setter(String key, Object value)
        {
            stuff.put(key, value);
        }
    }

    @SuppressWarnings("serial")
    static class MyNoArgException extends Exception
    {
        @JsonCreator MyNoArgException() { }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new TestVelocypackMapper();
    
    public void testIOException() throws IOException
    {
        IOException ioe = new IOException("TEST");
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(ioe));
        IOException result = MAPPER.readValue(json, IOException.class);
        assertEquals(ioe.getMessage(), result.getMessage());
    }

    public void testWithCreator() throws IOException
    {
        final String MSG = "the message";
        byte[] bytes = MAPPER.writeValueAsBytes(new MyException(MSG, 3));
        MyException result = MAPPER.readValue(bytes, MyException.class);
        assertEquals(MSG, result.getMessage());
        assertEquals(3, result.value);
        assertEquals(result.getFoo(), result.stuff.get("foo"));
    }

    public void testWithNullMessage() throws IOException
    {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(new IOException((String) null)));
        IOException result = mapper.readValue(json, IOException.class);
        assertNotNull(result);
        assertNull(result.getMessage());
    }

    public void testNoArgsException() throws IOException
    {
        MyNoArgException exc = MAPPER.readValue("{}", MyNoArgException.class);
        assertNotNull(exc);
    }

    // try simulating JDK 7 behavior
    public void testJDK7SuppressionProperty() throws IOException
    {
        Exception exc = MAPPER.readValue("{\"suppressed\":[]}", IOException.class);
        assertNotNull(exc);
    }
    
    // [databind#381]
    public void testSingleValueArrayDeserialization() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        final IOException exp;
        try {
            throw new IOException("testing");
        } catch (IOException internal) {
            exp = internal;
        }
        final String value = "[" + com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(exp) )+ "]";
        
        final IOException cloned = mapper.readValue(value, IOException.class);
        assertEquals(exp.getMessage(), cloned.getMessage());    
        
        assertEquals(exp.getStackTrace().length, cloned.getStackTrace().length);
        for (int i = 0; i < exp.getStackTrace().length; i ++) {
            _assertEquality(i, exp.getStackTrace()[i], cloned.getStackTrace()[i]);
        }
    }

    protected void _assertEquality(int ix, StackTraceElement exp, StackTraceElement act)
    {
        _assertEquality(ix, "className", exp.getClassName(), act.getClassName());
        _assertEquality(ix, "methodName", exp.getMethodName(), act.getMethodName());
        _assertEquality(ix, "fileName", exp.getFileName(), act.getFileName());
        _assertEquality(ix, "lineNumber", exp.getLineNumber(), act.getLineNumber());
    }

    protected void _assertEquality(int ix, String prop,
            Object exp, Object act)
    {
        if (exp == null) {
            if (act == null) {
                return;
            }
        } else {
            if (exp.equals(act)) {
                return;
            }
        }
        fail(String.format("StackTraceElement #%d, property '%s' differs: expected %s, actual %s",
                ix, prop, exp, act));
    }

    public void testSingleValueArrayDeserializationException() throws Exception {
        if(!isAtLeastVersion(2, 12)) return;

        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        
        final IOException exp;
        try {
            throw new IOException("testing");
        } catch (IOException internal) {
            exp = internal;
        }
        final String value = "[" + com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(exp) )+ "]";
        
        try {
            mapper.readValue(value, IOException.class);
            fail("Exception not thrown when attempting to deserialize an IOException wrapped in a single value array with UNWRAP_SINGLE_VALUE_ARRAYS disabled");
        } catch (JsonMappingException exp2) {
            verifyException(exp2, "from Array value (token `JsonToken.START_ARRAY`)");
        }
    }

    // mostly to help with XML module (and perhaps CSV)
    public void testLineNumberAsString() throws IOException
    {
        Exception exc = MAPPER.readValue(aposToQuotes(
                "{'message':'Test',\n'stackTrace': "
                +"[ { 'lineNumber':'50' } ] }"
        ), IOException.class);
        assertNotNull(exc);
    }

    // [databind#1842]:
    public void testNullAsMessage() throws IOException
    {
        Exception exc = MAPPER.readValue(aposToQuotes(
                "{'message':null, 'localizedMessage':null }"
        ), IOException.class);
        assertNotNull(exc);
        assertNull(exc.getMessage());
        assertNull(exc.getLocalizedMessage());
    }
}
