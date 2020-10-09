package com.fasterxml.jackson.databind.ser;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Unit tests for verifying serialization of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class TestEnumSerialization
    extends BaseMapTest
{
    /**
     * Test enumeration for verifying Enum serialization functionality.
     */
    protected enum TestEnum {
        A, B, C;
        private TestEnum() { }

        @Override public String toString() { return name().toLowerCase(); }
    }

    /**
     * Alternative version that forces use of "toString-serializer".
     */
    @JsonSerialize(using=ToStringSerializer.class)
    protected enum AnnotatedTestEnum {
        A2, B2, C2;
        private AnnotatedTestEnum() { }

        @Override public String toString() { return name().toLowerCase(); }
    }

    protected enum EnumWithJsonValue {
        A("foo"), B("bar");
        private final String name;
        private EnumWithJsonValue(String n) {
            name = n;
        }

        @Override
        public String toString() { return name; }

        @JsonValue
        public String external() { return "value:"+name; }
    }

    protected static interface ToStringMixin {
        @Override
        @JsonValue public String toString();
    }

    protected static enum SerializableEnum implements JsonSerializable
    {
        A, B, C;

        private SerializableEnum() { }

        @Override
        public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
                throws IOException, JsonProcessingException
        {
            serialize(jgen, provider);
        }

        @Override
        public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException
        {
            jgen.writeString("foo");
        }
    }

    protected static enum LowerCaseEnum {
        A, B, C;
        private LowerCaseEnum() { }
        @Override
        public String toString() { return name().toLowerCase(); }
    }

    static class MapBean {
        public Map<TestEnum,Integer> map = new HashMap<TestEnum,Integer>();
        
        public void add(TestEnum key, int value) {
            map.put(key, Integer.valueOf(value));
        }
    }

    static enum NOT_OK {
        V1("v1"); 
        protected String key;
        // any runtime-persistent annotation is fine
        NOT_OK(@JsonProperty String key) { this.key = key; }
    }

    static enum OK {
        V1("v1");
        protected String key;
        OK(String key) { this.key = key; }
    }

    @SuppressWarnings({ "rawtypes", "serial" })
    static class LowerCasingEnumSerializer extends StdSerializer<Enum>
    {
        public LowerCasingEnumSerializer() { super(Enum.class); }
        @Override
        public void serialize(Enum value, JsonGenerator jgen,
                SerializerProvider provider) throws IOException {
            jgen.writeString(value.name().toLowerCase());
        }
    }

    protected static enum LC749Enum {
        A, B, C;
        private LC749Enum() { }
        @Override
        public String toString() { return name().toLowerCase(); }
    }

    // for [databind#1322]
    protected enum EnumWithJsonProperty {
        @JsonProperty("aleph")
        A;
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();
    
    public void testSimple() throws Exception
    {
        assertEquals("\"B\"", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(TestEnum.B)));
    }

    public void testEnumSet() throws Exception
    {
        final EnumSet<TestEnum> value = EnumSet.of(TestEnum.B);
        assertEquals("[\"B\"]", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(value)));
    }

    /**
     * Whereas regular Enum serializer uses enum names, some users
     * prefer calling toString() instead. So let's verify that
     * this can be done using annotation for enum class.
     */
    public void testEnumUsingToString() throws Exception
    {
        assertEquals("\"c2\"", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(AnnotatedTestEnum.C2)));
    }

    public void testSubclassedEnums() throws Exception
    {
        assertEquals("\"B\"", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(EnumWithSubClass.B)));
    }

    public void testEnumsWithJsonValue() throws Exception {
        assertEquals("\"value:bar\"", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(EnumWithJsonValue.B)));
    }

    public void testEnumsWithJsonValueUsingMixin() throws Exception
    {
        // can't share, as new mix-ins are added
        ObjectMapper m = new TestVelocypackMapper();
        m.addMixIn(TestEnum.class, ToStringMixin.class);
        assertEquals("\"b\"", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(TestEnum.B)));
    }

    // [databind#601]
    public void testEnumsWithJsonValueInMap() throws Exception
    {
        EnumMap<EnumWithJsonValue,String> input = new EnumMap<EnumWithJsonValue,String>(EnumWithJsonValue.class);
        input.put(EnumWithJsonValue.B, "x");
        // 24-Sep-2015, tatu: SHOULD actually use annotated method, as per:
        assertEquals("{\"value:bar\":\"x\"}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input)));
    }

    /**
     * Test for ensuring that @JsonSerializable is used with Enum types as well
     * as with any other types.
     */
    public void testSerializableEnum() throws Exception
    {
        assertEquals("\"foo\"", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(SerializableEnum.A)));
    }

    public void testToStringEnum() throws Exception
    {
        ObjectMapper m = new TestVelocypackMapper();
        m.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        assertEquals("\"b\"", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(LowerCaseEnum.B)));

        // [databind#749] but should also be able to dynamically disable
        assertEquals("\"B\"", com.fasterxml.jackson.VPackUtils.toJson(
                m.writer().without(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                    .writeValueAsBytes(LowerCaseEnum.B)));
    }

    public void testToStringEnumWithEnumMap() throws Exception
    {
        ObjectMapper m = new TestVelocypackMapper();
        m.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        EnumMap<LowerCaseEnum,String> enums = new EnumMap<LowerCaseEnum,String>(LowerCaseEnum.class);
        enums.put(LowerCaseEnum.C, "value");
        assertEquals("{\"c\":\"value\"}", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(enums)));
    }

    public void testAsIndex() throws Exception
    {
        // By default, serialize using name
        ObjectMapper m = new TestVelocypackMapper();
        assertFalse(m.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX));
        assertEquals(quote("B"), com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(TestEnum.B)));

        // but we can change (dynamically, too!) it to be number-based
        m.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
        assertEquals("1", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(TestEnum.B)));
    }

    public void testAnnotationsOnEnumCtor() throws Exception
    {
        assertEquals(quote("V1"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(OK.V1)));
        assertEquals(quote("V1"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(NOT_OK.V1)));
        assertEquals(quote("V2"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(NOT_OK2.V2)));
    }

    // [databind#227]
    public void testGenericEnumSerializer() throws Exception
    {
        // By default, serialize using name
        ObjectMapper m = new TestVelocypackMapper();
        SimpleModule module = new SimpleModule("foobar");
        module.addSerializer(Enum.class, new LowerCasingEnumSerializer());
        m.registerModule(module);
        assertEquals(quote("b"), com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(TestEnum.B)));
    }

    // [databind#749]

    public void testEnumMapSerDefault() throws Exception {
        final ObjectMapper mapper = newJsonMapper();
        EnumMap<LC749Enum, String> m = new EnumMap<LC749Enum, String>(LC749Enum.class);
        m.put(LC749Enum.A, "value");
        assertEquals("{\"A\":\"value\"}", com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(m)));
    }
    
    public void testEnumMapSerDisableToString() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        ObjectWriter w = mapper.writer().without(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        EnumMap<LC749Enum, String> m = new EnumMap<LC749Enum, String>(LC749Enum.class);
        m.put(LC749Enum.A, "value");
        assertEquals("{\"A\":\"value\"}", com.fasterxml.jackson.VPackUtils.toJson( w.writeValueAsBytes(m)));
    }

    public void testEnumMapSerEnableToString() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        ObjectWriter w = mapper.writer().with(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        EnumMap<LC749Enum, String> m = new EnumMap<LC749Enum, String>(LC749Enum.class);
        m.put(LC749Enum.A, "value");
        assertEquals("{\"a\":\"value\"}", com.fasterxml.jackson.VPackUtils.toJson( w.writeValueAsBytes(m)));
    }

    // [databind#1322]
    public void testEnumsWithJsonProperty() throws Exception {
        assertEquals(quote("aleph"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(EnumWithJsonProperty.A)));
    }

    // [databind#1535]
    public void testEnumKeysWithJsonProperty() throws Exception {
        Map<EnumWithJsonProperty,Integer> input = new HashMap<EnumWithJsonProperty,Integer>();
        input.put(EnumWithJsonProperty.A, 13);
        assertEquals(aposToQuotes("{'aleph':13}"), com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input)));
    }

    // [databind#1322]
    public void testEnumsWithJsonPropertyInSet() throws Exception
    {
        assertEquals("[\"aleph\"]", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(EnumSet.of(EnumWithJsonProperty.A))));
    }

    // [databind#1322]
    public void testEnumsWithJsonPropertyAsKey() throws Exception
    {
        EnumMap<EnumWithJsonProperty,String> input = new EnumMap<EnumWithJsonProperty,String>(EnumWithJsonProperty.class);
        input.put(EnumWithJsonProperty.A, "b");
        assertEquals("{\"aleph\":\"b\"}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input)));
    }
}

// [JACKSON-757], non-inner enum
enum NOT_OK2 {
    V2("v2"); 
    protected String key;
    // any runtime-persistent annotation is fine
    NOT_OK2(@JsonProperty String key) { this.key = key; }
}
