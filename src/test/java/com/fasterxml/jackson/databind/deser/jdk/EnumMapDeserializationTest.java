package com.fasterxml.jackson.databind.deser.jdk;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

@SuppressWarnings("serial")
public class EnumMapDeserializationTest extends BaseMapTest
{
    enum TestEnum { JACKSON, RULES, OK; }

    enum TestEnumWithDefault {
        JACKSON, RULES,
        @JsonEnumDefaultValue
        OK; 
    }
    
    protected enum LowerCaseEnum {
        A, B, C;
        private LowerCaseEnum() { }
        @Override
        public String toString() { return name().toLowerCase(); }
    }

    static class MySimpleEnumMap extends EnumMap<TestEnum,String> { 
        public MySimpleEnumMap() {
            super(TestEnum.class);
        }
    }

    static class FromStringEnumMap extends EnumMap<TestEnum,String> { 
        @JsonCreator
        public FromStringEnumMap(String value) {
            super(TestEnum.class);
            put(TestEnum.JACKSON, value);
        }
    }

    static class FromDelegateEnumMap extends EnumMap<TestEnum,String> { 
        @JsonCreator
        public FromDelegateEnumMap(Map<Object,Object> stuff) {
            super(TestEnum.class);
            put(TestEnum.OK, String.valueOf(stuff));
        }
    }

    static class FromPropertiesEnumMap extends EnumMap<TestEnum,String> { 
        int a0, b0;

        @JsonCreator
        public FromPropertiesEnumMap(@JsonProperty("a") int a,
                @JsonProperty("b") int b) {
            super(TestEnum.class);
            a0 = a;
            b0 = b;
        }
    }

    // [databind#1859]
    public enum Enum1859 {
        A, B, C;
    }

    static class Pojo1859
    {
        public EnumMap<Enum1859, String> values;

        public Pojo1859() { }
        public Pojo1859(EnumMap<Enum1859, String> v) {
            values = v;
        }
    }

    // [databind#2457]
    enum MyEnum2457 {
        A,
        B() {
            // just to ensure subclass construction
            @Override
            public void foo() { }
        };

        // needed to force subclassing
        public void foo() { }

        @Override
        public String toString() { return name() + " as string"; }
    }

    /*
    /**********************************************************
    /* Test methods, basic
    /**********************************************************
     */

    protected final ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testEnumMaps() throws Exception
    {
        EnumMap<TestEnum,String> value = MAPPER.readValue("{\"OK\":\"value\"}",
                new TypeReference<EnumMap<TestEnum,String>>() { });
        assertEquals("value", value.get(TestEnum.OK));
    }

    public void testToStringEnumMaps() throws Exception
    {
        // can't reuse global one due to reconfig
        ObjectReader r = MAPPER.reader()
                .with(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        EnumMap<LowerCaseEnum,String> value = r.forType(
            new TypeReference<EnumMap<LowerCaseEnum,String>>() { })
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"a\":\"value\"}"));
        assertEquals("value", value.get(LowerCaseEnum.A));
    }

    /*
    /**********************************************************
    /* Test methods: custom enum maps
    /**********************************************************
     */

    public void testCustomEnumMapWithDefaultCtor() throws Exception
    {
        MySimpleEnumMap map = MAPPER.readValue(aposToQuotes("{'RULES':'waves'}"),
                MySimpleEnumMap.class);   
        assertEquals(1, map.size());
        assertEquals("waves", map.get(TestEnum.RULES));
    }

    public void testCustomEnumMapFromString() throws Exception
    {
        FromStringEnumMap map = MAPPER.readValue(quote("kewl"), FromStringEnumMap.class);   
        assertEquals(1, map.size());
        assertEquals("kewl", map.get(TestEnum.JACKSON));
    }

    public void testCustomEnumMapWithDelegate() throws Exception
    {
        FromDelegateEnumMap map = MAPPER.readValue(aposToQuotes("{'foo':'bar'}"), FromDelegateEnumMap.class);   
        assertEquals(1, map.size());
        assertEquals("{foo=bar}", map.get(TestEnum.OK));
    }

    public void testCustomEnumMapFromProps() throws Exception
    {
        FromPropertiesEnumMap map = MAPPER.readValue(aposToQuotes(
                "{'a':13,'RULES':'jackson','b':-731,'OK':'yes'}"),
                FromPropertiesEnumMap.class);

        assertEquals(13, map.a0);
        assertEquals(-731, map.b0);

        assertEquals("jackson", map.get(TestEnum.RULES));
        assertEquals("yes", map.get(TestEnum.OK));
        assertEquals(2, map.size());
    }

    /*
    /**********************************************************
    /* Test methods: polymorphic
    /**********************************************************
     */

    // [databind#1859]
    public void testEnumMapAsPolymorphic() throws Exception
    {
        EnumMap<Enum1859, String> enumMap = new EnumMap<>(Enum1859.class);
        enumMap.put(Enum1859.A, "Test");
        enumMap.put(Enum1859.B, "stuff");
        Pojo1859 input = new Pojo1859(enumMap);

        ObjectMapper mapper = com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper.testBuilder()
                .activateDefaultTypingAsProperty(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL, "@type")
                .build();

        // 05-Mar-2018, tatu: Original issue had this; should not make difference:
         /*
        TypeResolverBuilder<?> mapTyperAsPropertyType = new com.fasterxml.jackson.dataformat.velocypack.TestVelocyPackMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL);
        mapTyperAsPropertyType.init(JsonTypeInfo.Id.CLASS, null);
        mapTyperAsPropertyType.inclusion(JsonTypeInfo.As.PROPERTY);
        mapper.setDefaultTyping(mapTyperAsPropertyType);
         */

        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input));
        Pojo1859 result = mapper.readValue(json, Pojo1859.class);
        assertNotNull(result);
        assertNotNull(result.values);
        assertEquals(2, result.values.size());
    }

    /*
    /**********************************************************
    /* Test methods: handling of invalid values
    /**********************************************************
     */

    // [databind#1859]
    public void testUnknownKeyAsDefault() throws Exception
    {
        // first, via EnumMap
        EnumMap<TestEnumWithDefault,String> value = MAPPER
                .readerFor(new TypeReference<EnumMap<TestEnumWithDefault,String>>() { })
                .with(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"unknown\":\"value\"}"));
        assertEquals(1, value.size());
        assertEquals("value", value.get(TestEnumWithDefault.OK));

        Map<TestEnumWithDefault,String> value2 = MAPPER
                .readerFor(new TypeReference<Map<TestEnumWithDefault,String>>() { })
                .with(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"unknown\":\"value\"}"));
        assertEquals(1, value2.size());
        assertEquals("value", value2.get(TestEnumWithDefault.OK));
    }

    // [databind#1859]
    public void testUnknownKeyAsNull() throws Exception
    {
        // first, via EnumMap
        EnumMap<TestEnumWithDefault,String> value = MAPPER
                .readerFor(new TypeReference<EnumMap<TestEnumWithDefault,String>>() { })
                .with(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"unknown\":\"value\"}"));
        assertEquals(0, value.size());

        // then regular Map
        Map<TestEnumWithDefault,String> value2 = MAPPER
                .readerFor(new TypeReference<Map<TestEnumWithDefault,String>>() { })
                .with(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"unknown\":\"value\"}"));
        // 04-Jan-2017, tatu: Not sure if this is weird or not, but since `null`s are typically
        //    ok for "regular" JDK Maps...
        assertEquals(1, value2.size());
        assertEquals("value", value2.get(null));
    }

    // [databind#2457]
    public void testCustomEnumAsRootMapKey() throws Exception
    {
        final ObjectMapper mapper = newJsonMapper();
        final Map<MyEnum2457, String> map = new LinkedHashMap<>();
        map.put(MyEnum2457.A, "1");
        map.put(MyEnum2457.B, "2");
        assertEquals(aposToQuotes("{'A':'1','B':'2'}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(map)));

        // But should be able to override
        assertEquals(aposToQuotes("{'"+MyEnum2457.A.toString()+"':'1','"+MyEnum2457.B.toString()+"':'2'}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writer()
                    .with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                    .writeValueAsBytes(map)));
    }
}
