package com.fasterxml.jackson.databind;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.LRUMap;

import static com.fasterxml.jackson.VPackUtils.toVPack;

/**
 * Tests to verify that most core Jackson components can be serialized
 * using default JDK serialization: this feature is useful for some
 * platforms, such as Android, where memory management is handled
 * much more aggressively.
 */
public class TestJDKSerialization extends BaseMapTest
{
    static class MyPojo {
        public int x;
        protected int y;
        
        public MyPojo() { }
        public MyPojo(int x0, int y0) {
            x = x0;
            y = y0;
        }
        
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
    }

    // for [databind#899]
    static class EnumPOJO {
        public ABC abc = ABC.B;

        public Map<String,ABC> stuff = new LinkedHashMap<String,ABC>();
    }

    static class AnyBean {
        HashMap<String,Object> _map;

        public AnyBean() {
            _map = new HashMap<String,Object>();
        }

        @JsonAnySetter
        AnyBean addEntry(String key, Object value) {
            _map.put(key, value);
            return this;
        }

        @JsonAnyGetter
        public Map<String,Object> properties() {
            return _map;
        }
    }

    /*
    /**********************************************************
    /* Tests for individual objects
    /**********************************************************
     */

    /* 18-Oct-2013, tatu: Not sure why, but looks like sharing the default
     *   ObjectMapper here can lead to strange unit test suite failures, so
     *   let's create a private copy for this class only.
     */
    private final ObjectMapper MAPPER = newJsonMapper();
    
    public void testConfigs() throws IOException
    {
        byte[] base = jdkSerialize(MAPPER.getDeserializationConfig().getBaseSettings());
        assertNotNull(jdkDeserialize(base));

        // first things first: underlying BaseSettings
        
        DeserializationConfig origDC = MAPPER.getDeserializationConfig();
        SerializationConfig origSC = MAPPER.getSerializationConfig();
        byte[] dcBytes = jdkSerialize(origDC);
        byte[] scBytes = jdkSerialize(origSC);

        DeserializationConfig dc = jdkDeserialize(dcBytes);
        assertNotNull(dc);
        assertEquals(dc._deserFeatures, origDC._deserFeatures);
        SerializationConfig sc = jdkDeserialize(scBytes);
        assertNotNull(sc);
        assertEquals(sc._serFeatures, origSC._serFeatures);
    }

    // for [databind#899]
    public void testEnumHandlers() throws IOException
    {
        ObjectMapper mapper = newJsonMapper();
        // ensure we have serializers and/or deserializers, first
        byte[] vpackBytes = mapper.writerFor(EnumPOJO.class)
                .writeValueAsBytes(new EnumPOJO());
        EnumPOJO result = mapper.readerFor(EnumPOJO.class)
                .readValue(vpackBytes);
        assertNotNull(result);

        // and then use JDK serialization to freeze/thaw objects
        byte[] bytes = jdkSerialize(mapper);
        ObjectMapper mapper2 = jdkDeserialize(bytes);
        assertNotNull(mapper2);

        bytes = jdkSerialize(mapper.readerFor(EnumPOJO.class));
        ObjectReader r = jdkDeserialize(bytes);
        assertNotNull(r);

        /* 14-Aug-2015, tatu: Looks like pre-loading JsonSerializer is problematic
         *    at this point; comment out for now. Try to fix later on.
         */
        bytes = jdkSerialize(mapper.writerFor(EnumPOJO.class));
        ObjectWriter w = jdkDeserialize(bytes);
        assertNotNull(w);

        // plus, ensure objects are usable:
        byte[] vpackBytes2 = w.writeValueAsBytes(new EnumPOJO());
        assertEquals(vpackBytes, vpackBytes2);
        EnumPOJO result2 = r.readValue(vpackBytes2);
        assertNotNull(result2);
    }

    public void testObjectWriter() throws IOException
    {
        ObjectWriter origWriter = MAPPER.writer();
        final String EXP_JSON = "{\"x\":2,\"y\":3}";
        final MyPojo p = new MyPojo(2, 3);
        assertEquals(EXP_JSON, com.fasterxml.jackson.VPackUtils.toJson( origWriter.writeValueAsBytes(p)));
        String json = com.fasterxml.jackson.VPackUtils.toJson( origWriter.writeValueAsBytes(new AnyBean()
                .addEntry("a", "b")));
        assertNotNull(json);
        byte[] bytes = jdkSerialize(origWriter);
        ObjectWriter writer2 = jdkDeserialize(bytes);
        assertEquals(EXP_JSON, com.fasterxml.jackson.VPackUtils.toJson( writer2.writeValueAsBytes(p)));
    }
    
    public void testObjectReader() throws IOException
    {
        ObjectReader origReader = MAPPER.readerFor(MyPojo.class);
        byte[] bytes = toVPack("{\"x\":1,\"y\":2}");
        MyPojo p1 = origReader.readValue(bytes);
        assertEquals(2, p1.y);
        ObjectReader anyReader = MAPPER.readerFor(AnyBean.class);
        AnyBean any = anyReader.readValue(bytes);
        assertEquals(Integer.valueOf(2), any.properties().get("y"));
        
        byte[] readerBytes = jdkSerialize(origReader);
        ObjectReader reader2 = jdkDeserialize(readerBytes);
        MyPojo p2 = reader2.readValue(bytes);
        assertEquals(2, p2.y);

        ObjectReader anyReader2 = jdkDeserialize(jdkSerialize(anyReader));
        AnyBean any2 = anyReader2.readValue(bytes);
        assertEquals(Integer.valueOf(2), any2.properties().get("y"));
    }

    public void testObjectMapper() throws IOException
    {
        final String EXP_JSON = "{\"x\":2,\"y\":3}";
        final MyPojo p = new MyPojo(2, 3);
        assertEquals(EXP_JSON, com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(p)));
        assertNotNull(MAPPER.getFactory());
        assertNotNull(MAPPER.getFactory().getCodec());

        byte[] bytes = jdkSerialize(MAPPER);
        ObjectMapper mapper2 = jdkDeserialize(bytes);
        assertEquals(EXP_JSON, com.fasterxml.jackson.VPackUtils.toJson( mapper2.writeValueAsBytes(p)));
        MyPojo p2 = mapper2.readValue(toVPack(EXP_JSON), MyPojo.class);
        assertEquals(p.x, p2.x);
        assertEquals(p.y, p2.y);

        // [databind#2038]: verify that codec is not lost
        assertNotNull(mapper2.getFactory());
        assertNotNull(mapper2.getFactory().getCodec());
    }

    public void testTypeFactory() throws Exception
    {
        TypeFactory orig = TypeFactory.defaultInstance();
        JavaType t = orig.constructType(JavaType.class);
        assertNotNull(t);

        byte[] bytes = jdkSerialize(orig);
        TypeFactory result = jdkDeserialize(bytes);
        assertNotNull(result);
        t = orig.constructType(JavaType.class);
        assertEquals(JavaType.class, t.getRawClass());
    }

    public void testLRUMap() throws Exception
    {
        LRUMap<String,Integer> map = new LRUMap<String,Integer>(32, 32);
        map.put("a", 1);

        byte[] bytes = jdkSerialize(map);
        LRUMap<String,Integer> result = jdkDeserialize(bytes);
        // transient implementation, will be read as empty
        assertEquals(0, result.size());

        // but should be possible to re-populate
        result.put("a", 2);
        assertEquals(1, result.size());
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    protected byte[] jdkSerialize(Object o) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(2000);
        ObjectOutputStream obOut = new ObjectOutputStream(bytes);
        obOut.writeObject(o);
        obOut.close();
        return bytes.toByteArray();
    }

    @SuppressWarnings("unchecked")
    protected <T> T jdkDeserialize(byte[] raw) throws IOException
    {
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(raw));
        try {
            return (T) objIn.readObject();
        } catch (ClassNotFoundException e) {
            fail("Missing class: "+e.getMessage());
            return null;
        } finally {
            objIn.close();
        }
    }
}
