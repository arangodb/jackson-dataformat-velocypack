package com.fasterxml.jackson.databind.deser;

import java.util.*;

import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.ValueType;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.TokenBuffer;

/**
 * Unit tests for those Jackson types we want to ensure can be deserialized.
 */
public class TestJacksonTypes
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    private final ObjectMapper MAPPER = sharedMapper();

    public void testJsonLocation() throws Exception
    {
        // note: source reference is untyped, only String guaranteed to work
        JsonLocation loc = new JsonLocation("whatever",  -1, -1, 100, 13);
        // Let's use serializer here; goal is round-tripping
        JsonLocation result = MAPPER.readValue(MAPPER.writeValueAsBytes(loc), JsonLocation.class);
        assertNotNull(result);
        assertEquals(loc.getSourceRef(), result.getSourceRef());
        assertEquals(loc.getByteOffset(), result.getByteOffset());
        assertEquals(loc.getCharOffset(), result.getCharOffset());
        assertEquals(loc.getColumnNr(), result.getColumnNr());
        assertEquals(loc.getLineNr(), result.getLineNr());
    }

    // doesn't really belong here but...
    public void testJsonLocationProps()
    {
        JsonLocation loc = new JsonLocation(null,  -1, -1, 100, 13);
        assertTrue(loc.equals(loc));
        assertFalse(loc.equals(null));
        final Object value = "abx";
        assertFalse(loc.equals(value));

        // should we check it's not 0?
        loc.hashCode();
    }

    public void testJavaType() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        // first simple type:
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(tf.constructType(String.class)));
        assertEquals(quote(java.lang.String.class.getName()), json);
        // and back
        JavaType t = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toVPack(json), JavaType.class);
        assertNotNull(t);
        assertEquals(String.class, t.getRawClass());
    }

    /**
     * Verify that {@link TokenBuffer} can be properly deserialized
     * automatically, using the "standard" JSON sample document
     */
    public void testTokenBufferWithSample() throws Exception
    {
        // First, try standard sample doc:
        TokenBuffer result = MAPPER.readValue(com.fasterxml.jackson.VPackUtils.toVPack(SAMPLE_DOC_JSON_SPEC), TokenBuffer.class);
        verifyJsonSpecSampleDoc(result.asParser(), true);
        result.close();
    }

    @SuppressWarnings("resource")
    public void testTokenBufferWithSequence() throws Exception
    {
        // and then sequence of other things
        JsonParser jp = MAPPER.getFactory().createParser(com.fasterxml.jackson.VPackUtils.toVPack("[ 32, [ 1 ], \"abc\", { \"a\" : true } ]"));
        assertToken(JsonToken.START_ARRAY, jp.nextToken());

        assertToken(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
        TokenBuffer buf = MAPPER.readValue(jp, TokenBuffer.class);

        // check manually...
        JsonParser bufParser = buf.asParser();
        assertToken(JsonToken.VALUE_NUMBER_INT, bufParser.nextToken());
        assertEquals(32, bufParser.getIntValue());
        assertNull(bufParser.nextToken());

        // then bind to another
        buf = MAPPER.readValue(jp, TokenBuffer.class);
        bufParser = buf.asParser();
        assertToken(JsonToken.START_ARRAY, bufParser.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, bufParser.nextToken());
        assertEquals(1, bufParser.getIntValue());
        assertToken(JsonToken.END_ARRAY, bufParser.nextToken());
        assertNull(bufParser.nextToken());

        // third one, with automatic binding
        buf = MAPPER.readValue(jp, TokenBuffer.class);
        String str = MAPPER.readValue(buf.asParser(), String.class);
        assertEquals("abc", str);

        // and ditto for last one
        buf = MAPPER.readValue(jp, TokenBuffer.class);
        Map<?,?> map = MAPPER.readValue(buf.asParser(), Map.class);
        assertEquals(1, map.size());
        assertEquals(Boolean.TRUE, map.get("a"));
        
        assertEquals(JsonToken.END_ARRAY, jp.nextToken());
        assertNull(jp.nextToken());
    }

    // 10k does it, 5k not, but use bit higher values just in case
    private final static int RECURSION_2398 = 25000;

    // [databind#2398]
    public void testDeeplyNestedArrays() throws Exception
    {
        try (JsonParser p = MAPPER.tokenStreamFactory().createParser(_createNestedArray(RECURSION_2398 * 2, 123))) {
            p.nextToken();
            TokenBuffer b = new TokenBuffer(p);
            b.copyCurrentStructure(p);
            b.close();
        }
    }

    public void testDeeplyNestedObjects() throws Exception
    {
        try (JsonParser p = MAPPER.tokenStreamFactory().createParser(_createNestedObject(RECURSION_2398, "a", 42))) {
            p.nextToken();
            TokenBuffer b = new TokenBuffer(p);
            b.copyCurrentStructure(p);
            b.close();
        }
    }

    private byte[] _createNestedArray(int nesting, int middle) {
        VPackBuilder builder = new VPackBuilder();
        for (int i = 0; i < nesting; i++) {
            builder.add(ValueType.ARRAY);
        }
        builder.add(middle);
        for (int i = 0; i < nesting; i++) {
            builder.close();
        }
        return builder.slice().toByteArray();
    }

    private byte[] _createNestedObject(int nesting, String key, int value) {
        VPackBuilder builder = new VPackBuilder();
        builder.add(ValueType.OBJECT);
        for (int i = 0; i < nesting - 1; i++) {
            builder.add(key, ValueType.OBJECT);
        }
        builder.add(key, value);
        for (int i = 0; i < nesting; i++) {
            builder.close();
        }
        return builder.slice().toByteArray();
    }

}
