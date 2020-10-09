package com.fasterxml.jackson.databind.ser;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

@SuppressWarnings("serial")
public class TestJsonSerialize2
    extends BaseMapTest
{
    static class SimpleKey {
        protected final String key;
        
        public SimpleKey(String str) { key = str; }
        
        @Override public String toString() { return "toString:"+key; }
    }

    static class SimpleValue {
        public final String value;
        
        public SimpleValue(String str) { value = str; }
    }

    @JsonPropertyOrder({"value", "value2"})
    static class ActualValue extends SimpleValue
    {
        public final String other = "123";
        
        public ActualValue(String str) { super(str); }
    }

    static class SimpleKeySerializer extends JsonSerializer<SimpleKey> {
        @Override
        public void serialize(SimpleKey key, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
            jgen.writeFieldName("key "+key.key);
        }
    }

    static class SimpleValueSerializer extends JsonSerializer<SimpleValue> {
        @Override
        public void serialize(SimpleValue value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
            jgen.writeString("value "+value.value);
        }
    }

    @JsonSerialize(contentAs=SimpleValue.class)
    static class SimpleValueList extends ArrayList<ActualValue> { }

    @JsonSerialize(contentAs=SimpleValue.class)
    static class SimpleValueMap extends HashMap<SimpleKey, ActualValue> { }

    @JsonSerialize(contentUsing=SimpleValueSerializer.class)
    static class SimpleValueListWithSerializer extends ArrayList<ActualValue> { }

    @JsonSerialize(keyUsing=SimpleKeySerializer.class, contentUsing=SimpleValueSerializer.class)
    static class SimpleValueMapWithSerializer extends HashMap<SimpleKey, ActualValue> { }
    
    static class ListWrapperSimple
    {
        @JsonSerialize(contentAs=SimpleValue.class)
        public final ArrayList<ActualValue> values = new ArrayList<ActualValue>();
        
        public ListWrapperSimple(String value) {
            values.add(new ActualValue(value));
        }
    }

    static class ListWrapperWithSerializer
    {
        @JsonSerialize(contentUsing=SimpleValueSerializer.class)
        public final ArrayList<ActualValue> values = new ArrayList<ActualValue>();
        
        public ListWrapperWithSerializer(String value) {
            values.add(new ActualValue(value));
        }
    }
    
    static class MapWrapperSimple
    {
        @JsonSerialize(contentAs=SimpleValue.class)
        public final HashMap<SimpleKey, ActualValue> values = new HashMap<SimpleKey, ActualValue>();
        
        public MapWrapperSimple(String key, String value) {
            values.put(new SimpleKey(key), new ActualValue(value));
        }
    }

    static class MapWrapperWithSerializer
    {
        @JsonSerialize(keyUsing=SimpleKeySerializer.class, contentUsing=SimpleValueSerializer.class)
        public final HashMap<SimpleKey, ActualValue> values = new HashMap<SimpleKey, ActualValue>();
        
        public MapWrapperWithSerializer(String key, String value) {
            values.put(new SimpleKey(key), new ActualValue(value));
        }
    }

    static class NullBean
    {
        @JsonSerialize(using=NullSerializer.class)
        public String value = "abc";
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new TestVelocypackMapper();
    
    // test value annotation applied to List value class
    public void testSerializedAsListWithClassAnnotations() throws IOException
    {
        SimpleValueList list = new SimpleValueList();
        list.add(new ActualValue("foo"));
        assertEquals("[{\"value\":\"foo\"}]", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(list)));
    }

    // test value annotation applied to Map value class
    public void testSerializedAsMapWithClassAnnotations() throws IOException
    {
        SimpleValueMap map = new SimpleValueMap();
        map.put(new SimpleKey("x"), new ActualValue("y"));
        assertEquals("{\"toString:x\":{\"value\":\"y\"}}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(map)));
    }

    // test Serialization annotation with List
    public void testSerializedAsListWithClassSerializer() throws IOException
    {
        ObjectMapper m = new TestVelocypackMapper();
        SimpleValueListWithSerializer list = new SimpleValueListWithSerializer();
        list.add(new ActualValue("foo"));
        assertEquals("[\"value foo\"]", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(list)));
    }

    public void testSerializedAsListWithPropertyAnnotations() throws IOException
    {
        ListWrapperSimple input = new ListWrapperSimple("bar");
        assertEquals("{\"values\":[{\"value\":\"bar\"}]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input)));
    }
    
    public void testSerializedAsMapWithClassSerializer() throws IOException
    {
        SimpleValueMapWithSerializer map = new SimpleValueMapWithSerializer();
        map.put(new SimpleKey("abc"), new ActualValue("123"));
        assertEquals("{\"key abc\":\"value 123\"}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(map)));
    }

    public void testSerializedAsMapWithPropertyAnnotations() throws IOException
    {
        MapWrapperSimple input = new MapWrapperSimple("a", "b");
        assertEquals("{\"values\":{\"toString:a\":{\"value\":\"b\"}}}", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(input)));
    }
    
    public void testSerializedAsListWithPropertyAnnotations2() throws IOException
    {
        ListWrapperWithSerializer input = new ListWrapperWithSerializer("abc");
        assertEquals("{\"values\":[\"value abc\"]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input)));
    }

    public void testSerializedAsMapWithPropertyAnnotations2() throws IOException
    {
        MapWrapperWithSerializer input = new MapWrapperWithSerializer("foo", "b");
        assertEquals("{\"values\":{\"key foo\":\"value b\"}}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input)));
    }

    public void testEmptyInclusionContainers() throws IOException
    {
        ObjectMapper defMapper = MAPPER;
        ObjectMapper inclMapper = new TestVelocypackMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        ListWrapper<String> list = new ListWrapper<String>();
        assertEquals("{\"list\":[]}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(list)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(list)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(new ListWrapper<String>())));

        MapWrapper<String,Integer> map = new MapWrapper<String,Integer>(new HashMap<String,Integer>());
        assertEquals("{\"map\":{}}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(map)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(map)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(new MapWrapper<String,Integer>(null))));

        ArrayWrapper<Integer> array = new ArrayWrapper<Integer>(new Integer[0]);
        assertEquals("{\"array\":[]}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(array)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(array)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(new ArrayWrapper<Integer>(null))));
    }

    public void testNullSerializer() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NullBean()));
        assertEquals("{\"value\":null}", json);
    }
}
