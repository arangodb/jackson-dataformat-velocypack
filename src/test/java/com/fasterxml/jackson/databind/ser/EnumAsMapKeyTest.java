package com.fasterxml.jackson.databind.ser;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class EnumAsMapKeyTest extends BaseMapTest
{
    static class MapBean {
        public Map<ABCEnum,Integer> map = new HashMap<>();
        
        public void add(ABCEnum key, int value) {
            map.put(key, Integer.valueOf(value));
        }
    }

    protected enum ABCEnum {
        A, B, C;
        private ABCEnum() { }

        @Override public String toString() { return name().toLowerCase(); }
    }

    // [databind#594]
    static enum MyEnum594 {
        VALUE_WITH_A_REALLY_LONG_NAME_HERE("longValue");

        private final String key;
        private MyEnum594(String k) { key = k; }

        @JsonValue
        public String getKey() { return key; }
    }

    static class MyStuff594 {
        public Map<MyEnum594,String> stuff = new EnumMap<MyEnum594,String>(MyEnum594.class);
        
        public MyStuff594(String value) {
            stuff.put(MyEnum594.VALUE_WITH_A_REALLY_LONG_NAME_HERE, value);
        }
    }

    // [databind#661]
    static class MyBean661 {
        private Map<Foo661, String> foo = new EnumMap<Foo661, String>(Foo661.class);

        public MyBean661(String value) {
            foo.put(Foo661.FOO, value);
        }

        @JsonAnyGetter
        @JsonSerialize(keyUsing = Foo661.Serializer.class)
        public Map<Foo661, String> getFoo() {
            return foo;
        }
    }

    enum Foo661 {
        FOO;
        public static class Serializer extends JsonSerializer<Foo661> {
            @Override
            public void serialize(Foo661 value, JsonGenerator jgen, SerializerProvider provider) 
                    throws IOException {
                jgen.writeFieldName("X-"+value.name());
            }
        }
    }
    
    // [databind#2129]
    public enum Type {
        FIRST,
        SECOND;
    }

    static class TypeContainer {
        public Map<Type, Integer> values;

        public TypeContainer(Type type, int value) {
            values = Collections.singletonMap(type, value);
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */
    
    private final ObjectMapper MAPPER = newJsonMapper();

    public void testMapWithEnumKeys() throws Exception
    {
        MapBean bean = new MapBean();
        bean.add(ABCEnum.B, 3);

        // By default Enums serialized using `name()`
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(bean));
        assertEquals("{\"map\":{\"B\":3}}", json);

        // but can change
        json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writer()
                .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .writeValueAsBytes(bean));
        assertEquals("{\"map\":{\"b\":3}}", json);

        // [databind#1570]

        // 14-Sep-2019, tatu: as per [databind#2129], must NOT use this feature but
        //    instead new `WRITE_ENUM_KEYS_USING_INDEX` added in 2.10
        json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writer()
                .with(SerializationFeature.WRITE_ENUMS_USING_INDEX)
                .writeValueAsBytes(bean));
//        assertEquals(aposToQuotes("{'map':{'"+TestEnum.B.ordinal()+"':3}}"), json);
        assertEquals(aposToQuotes("{'map':{'B':3}}"), json);
    }

    public void testCustomEnumMapKeySerializer() throws Exception {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new MyBean661("abc")));
        assertEquals(aposToQuotes("{'X-FOO':'abc'}"), json);
    }

    // [databind#594]
    public void testJsonValueForEnumMapKey() throws Exception {
        assertEquals(aposToQuotes("{'stuff':{'longValue':'foo'}}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new MyStuff594("foo"))));
    }
    
    // [databind#2129]
    public void testEnumAsIndexForRootMap() throws Exception
    {
        final Map<Type, Integer> input = Collections.singletonMap(Type.FIRST, 3);

        // by default, write using name()
        assertEquals(aposToQuotes("{'FIRST':3}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(input)));

        // but change with setting
        assertEquals(aposToQuotes("{'0':3}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writer()
                .with(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX)
                .writeValueAsBytes(input)));

        // but NOT with value settings
        assertEquals(aposToQuotes("{'FIRST':3}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writer()
                    .with(SerializationFeature.WRITE_ENUMS_USING_INDEX)
                    .writeValueAsBytes(input)));
    }
    
    // [databind#2129]
    public void testEnumAsIndexForValueMap() throws Exception
    {
        final TypeContainer input = new TypeContainer(Type.SECOND, 72);

        // by default, write using name()
        assertEquals(aposToQuotes("{'values':{'SECOND':72}}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(input)));

        // but change with setting
        assertEquals(aposToQuotes("{'values':{'1':72}}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writer()
                .with(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX)
                .writeValueAsBytes(input)));

        // but NOT with value settings
        assertEquals(aposToQuotes("{'values':{'SECOND':72}}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writer()
                    .with(SerializationFeature.WRITE_ENUMS_USING_INDEX)
                    .writeValueAsBytes(input)));
    }
}
