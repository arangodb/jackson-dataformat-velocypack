package com.fasterxml.jackson.databind.deser.jdk;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

@SuppressWarnings("serial")
public class EnumDeserializationTest
    extends BaseMapTest
{
    enum TestEnum { JACKSON, RULES, OK; }

    /**
     * Alternative version that annotates which deserializer to use
     */
    @JsonDeserialize(using=DummyDeserializer.class)
    enum AnnotatedTestEnum {
        JACKSON, RULES, OK;
    }

    public static class DummyDeserializer extends StdDeserializer<Object>
    {
        public DummyDeserializer() { super(Object.class); }
        @Override
        public Object deserialize(JsonParser jp, DeserializationContext ctxt)
        {
            return AnnotatedTestEnum.OK;
        }
    }

    public static class LcEnumDeserializer extends StdDeserializer<TestEnum>
    {
        public LcEnumDeserializer() { super(TestEnum.class); }
        @Override
        public TestEnum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
        {
            return TestEnum.valueOf(jp.getText().toUpperCase());
        }
    }

    protected enum LowerCaseEnum {
        A, B, C;
        private LowerCaseEnum() { }
        @Override
        public String toString() { return name().toLowerCase(); }
    }

    protected enum EnumWithJsonValue {
        A("foo"), B("bar");
        private final String name;
        private EnumWithJsonValue(String n) {
            name = n;
        }
        @JsonValue
        @Override
        public String toString() { return name; }
    }

    static class ClassWithEnumMapKey {
        @JsonProperty Map<TestEnum, String> map;
    }

    // [databind#677]
    static enum EnumWithPropertyAnno {
        @JsonProperty("a")
        A,

        // For this value, force use of anonymous sub-class, to ensure things still work
        @JsonProperty("b")
        B {
            @Override
            public String toString() {
                return "bb";
            }
        }
        ;
    }

    // [databind#1161]
    enum Enum1161 {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        };
    }

    static enum EnumWithDefaultAnno {
        A, B,

        @JsonEnumDefaultValue
        OTHER;
    }

    static enum EnumWithDefaultAnnoAndConstructor {
        A, B,

        @JsonEnumDefaultValue
        OTHER;

        @JsonCreator public static EnumWithDefaultAnnoAndConstructor fromId(String value) {
            for (EnumWithDefaultAnnoAndConstructor e: values()) {
                if (e.name().toLowerCase().equals(value)) return e;
            }
            return null;
        }
    }

    static enum StrictEnumCreator {
        A, B;

        @JsonCreator public static StrictEnumCreator fromId(String value) {
            for (StrictEnumCreator e: values()) {
                if (e.name().toLowerCase().equals(value)) return e;
            }
            throw new IllegalArgumentException(value);
        }
    }

    // // 
    
    public enum AnEnum {
        ZERO,
        ONE
    }

    public static class AnEnumDeserializer extends FromStringDeserializer<AnEnum> {

        public AnEnumDeserializer() {
            super(AnEnum.class);
        }

        @Override
        protected AnEnum _deserialize(String value, DeserializationContext ctxt) throws IOException {
            try {
                return AnEnum.valueOf(value);
            } catch (IllegalArgumentException e) {
                return (AnEnum) ctxt.handleWeirdStringValue(AnEnum.class, value,
                        "Undefined AnEnum code");
            }
        }
    }

    public static class AnEnumKeyDeserializer extends KeyDeserializer {

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
            try {
                return AnEnum.valueOf(key);
            } catch (IllegalArgumentException e) {
                return ctxt.handleWeirdKey(AnEnum.class, key, "Undefined AnEnum code");
            }
        }
    }


    @JsonDeserialize(using = AnEnumDeserializer.class, keyUsing = AnEnumKeyDeserializer.class)
    public enum LanguageCodeMixin {
    }

    public static class EnumModule extends SimpleModule {
        @Override
        public void setupModule(SetupContext context) {
            context.setMixInAnnotations(AnEnum.class, LanguageCodeMixin.class);
        }

        public static ObjectMapper setupObjectMapper(ObjectMapper mapper) {
            final EnumModule module = new EnumModule();
            mapper.registerModule(module);
            return mapper;
        }
    }

    // for [databind#2164]
    public enum TestEnum2164 {
        A, B;

        @JsonCreator
        public static TestEnum2164 fromString(String input) {
            throw new IllegalArgumentException("2164");
        }
    }

    // for [databind#2309]
    static enum Enum2309 {
        NON_NULL("NON_NULL"),
        NULL(null),
        OTHER("OTHER")
        ;

        private String value;

        private Enum2309(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }        

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    protected final ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testSimple() throws Exception
    {
        // Then error case: unrecognized value
        try {
            /*Object result =*/ MAPPER.readValue("\"NO-SUCH-VALUE\"", TestEnum.class);
            fail("Expected an exception for bogus enum value...");
        } catch (MismatchedInputException jex) {
            verifyException(jex, "not one of the values accepted for Enum class");
        }
    }

    /**
     * Enums are considered complex if they have code (and hence sub-classes)... an
     * example is TimeUnit
     */
    public void testComplexEnum() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(TimeUnit.SECONDS));
        assertEquals(quote("SECONDS"), json);
        TimeUnit result = MAPPER.readValue(json, TimeUnit.class);
        assertSame(TimeUnit.SECONDS, result);
    }
    
    /**
     * Testing to see that annotation override works
     */
    public void testAnnotated() throws Exception
    {
        AnnotatedTestEnum e = MAPPER.readValue("\"JACKSON\"", AnnotatedTestEnum.class);
        /* dummy deser always returns value OK, independent of input;
         * only works if annotation is used
         */
        assertEquals(AnnotatedTestEnum.OK, e);
    }

    public void testSubclassedEnums() throws Exception
    {
        EnumWithSubClass value = MAPPER.readValue("\"A\"", EnumWithSubClass.class);
        assertEquals(EnumWithSubClass.A, value);
    }

    public void testToStringEnums() throws Exception
    {
        // can't reuse global one due to reconfig
        ObjectMapper m = new TestVelocypackMapper();
        m.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        LowerCaseEnum value = m.readValue("\"c\"", LowerCaseEnum.class);
        assertEquals(LowerCaseEnum.C, value);
    }

    public void testNumbersToEnums() throws Exception
    {
        // by default numbers are fine:
        assertFalse(MAPPER.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS));
        TestEnum value = MAPPER.readValue("1", TestEnum.class);
        assertSame(TestEnum.RULES, value);

        // but can also be changed to errors:
        ObjectReader r = MAPPER.readerFor(TestEnum.class)
                .with(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        try {
            value = r.readValue(com.fasterxml.jackson.VPackUtils.toVPack("1"));
            fail("Expected an error");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "not allowed to deserialize Enum value out of number: disable");
        }

        // and [databind#684]
        try {
            value = r.readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("1")));
            fail("Expected an error");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
            // 26-Jan-2017, tatu: as per [databind#1505], should fail bit differently
            verifyException(e, "not one of the values accepted for Enum class");
        }
    }

    public void testEnumsWithIndex() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writer()
                .with(SerializationFeature.WRITE_ENUMS_USING_INDEX)
                .writeValueAsBytes(TestEnum.RULES));
        assertEquals(String.valueOf(TestEnum.RULES.ordinal()), json);
        TestEnum result = MAPPER.readValue(json, TestEnum.class);
        assertSame(TestEnum.RULES, result);
    }

    public void testEnumsWithJsonValue() throws Exception
    {
        // first, enum as is
        EnumWithJsonValue e = MAPPER.readValue(quote("foo"), EnumWithJsonValue.class);
        assertSame(EnumWithJsonValue.A, e);
        e = MAPPER.readValue(quote("bar"), EnumWithJsonValue.class);
        assertSame(EnumWithJsonValue.B, e);

        // then in EnumSet
        EnumSet<EnumWithJsonValue> set = MAPPER.readValue("[\"bar\"]",
                new TypeReference<EnumSet<EnumWithJsonValue>>() { });
        assertNotNull(set);
        assertEquals(1, set.size());
        assertTrue(set.contains(EnumWithJsonValue.B));
        assertFalse(set.contains(EnumWithJsonValue.A));

        // and finally EnumMap
        EnumMap<EnumWithJsonValue,Integer> map = MAPPER.readValue("{\"foo\":13}",
                new TypeReference<EnumMap<EnumWithJsonValue, Integer>>() { });
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals(Integer.valueOf(13), map.get(EnumWithJsonValue.A));
    }

    // Ability to ignore unknown Enum values:

    public void testAllowUnknownEnumValuesReadAsNull() throws Exception
    {
        // cannot use shared mapper when changing configs...
        ObjectReader reader = MAPPER.reader(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        assertNull(reader.forType(TestEnum.class).readValue(com.fasterxml.jackson.VPackUtils.toVPack("\"NO-SUCH-VALUE\"")));
        assertNull(reader.forType(TestEnum.class).readValue(com.fasterxml.jackson.VPackUtils.toVPack(" 4343 ")));
    }

    // Ability to ignore unknown Enum values:

    // [databind#1642]
    public void testAllowUnknownEnumValuesReadAsNullWithCreatorMethod() throws Exception
    {
        // cannot use shared mapper when changing configs...
        ObjectReader reader = MAPPER.reader(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        assertNull(reader.forType(StrictEnumCreator.class).readValue(com.fasterxml.jackson.VPackUtils.toVPack("\"NO-SUCH-VALUE\"")));
        assertNull(reader.forType(StrictEnumCreator.class).readValue(com.fasterxml.jackson.VPackUtils.toVPack(" 4343 ")));
    }

    public void testAllowUnknownEnumValuesForEnumSets() throws Exception
    {
        ObjectReader reader = MAPPER.reader(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        EnumSet<TestEnum> result = reader.forType(new TypeReference<EnumSet<TestEnum>>() { })
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("[\"NO-SUCH-VALUE\"]"));
        assertEquals(0, result.size());
    }
    
    public void testAllowUnknownEnumValuesAsMapKeysReadAsNull() throws Exception
    {
        ObjectReader reader = MAPPER.reader(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        ClassWithEnumMapKey result = reader.forType(ClassWithEnumMapKey.class)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"map\":{\"NO-SUCH-VALUE\":\"val\"}}"));
        assertTrue(result.map.containsKey(null));
    }
    
    public void testDoNotAllowUnknownEnumValuesAsMapKeysWhenReadAsNullDisabled() throws Exception
    {
        assertFalse(MAPPER.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
         try {
             MAPPER.readValue("{\"map\":{\"NO-SUCH-VALUE\":\"val\"}}", ClassWithEnumMapKey.class);
             fail("Expected an exception for bogus enum value...");
         } catch (InvalidFormatException jex) {
             verifyException(jex, "Cannot deserialize Map key of type `com.fasterxml.jackson.databind.deser.jdk.EnumDeserializationTest$TestEnum`");
         }
    }

    // [databind#141]: allow mapping of empty String into null
    public void testEnumsWithEmpty() throws Exception
    {
       final ObjectMapper mapper = new TestVelocypackMapper();
       mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
       TestEnum result = mapper.readValue("\"\"", TestEnum.class);
       assertNull(result);
    }

    public void testGenericEnumDeserialization() throws Exception
    {
       final ObjectMapper mapper = new TestVelocypackMapper();
       SimpleModule module = new SimpleModule("foobar");
       module.addDeserializer(Enum.class, new LcEnumDeserializer());
       mapper.registerModule(module);
       // not sure this is totally safe but...
       assertEquals(TestEnum.JACKSON, mapper.readValue(quote("jackson"), TestEnum.class));
    }

    // [databind#381]
    public void testUnwrappedEnum() throws Exception {
        assertEquals(TestEnum.JACKSON,
                MAPPER.readerFor(TestEnum.class)
                    .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                    .readValue(com.fasterxml.jackson.VPackUtils.toVPack("[" + quote("JACKSON") + "]")));
    }
    
    public void testUnwrappedEnumException() throws Exception {
        final ObjectMapper mapper = newJsonMapper();
        mapper.disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        try {
            Object v = mapper.readValue("[" + quote("JACKSON") + "]",
                    TestEnum.class);
            fail("Exception was not thrown on deserializing a single array element of type enum; instead got: "+v);
        } catch (MismatchedInputException exp) {
            //exception as thrown correctly
            verifyException(exp, "Cannot deserialize");
        }
    }

    // [databind#149]: 'stringified' indexes for enums
    public void testIndexAsString() throws Exception
    {
        // first, regular index ought to work fine
        TestEnum en = MAPPER.readValue("2", TestEnum.class);
        assertSame(TestEnum.values()[2], en);

        // but also with quoted Strings
        en = MAPPER.readValue(quote("1"), TestEnum.class);
        assertSame(TestEnum.values()[1], en);

        // [databind#1690]: unless prevented
        try {
            en = com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper.testBuilder()
                    .configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false)
                    .build()
                    .readerFor(TestEnum.class)
                    .readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("1")));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type");
            verifyException(e, "EnumDeserializationTest$TestEnum");
            verifyException(e, "value looks like quoted Enum index");
        }
    }

    public void testEnumWithJsonPropertyRename() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new EnumWithPropertyAnno[] {
                EnumWithPropertyAnno.B, EnumWithPropertyAnno.A
        }));
        assertEquals("[\"b\",\"a\"]", json);

        // and while not really proper place, let's also verify deser while we're at it
        EnumWithPropertyAnno[] result = MAPPER.readValue(json, EnumWithPropertyAnno[].class);
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(EnumWithPropertyAnno.B, result[0]);
        assertSame(EnumWithPropertyAnno.A, result[1]);
    }

    // [databind#1161], unable to switch READ_ENUMS_USING_TO_STRING
    public void testDeserWithToString1161() throws Exception
    {
        Enum1161 result = MAPPER.readerFor(Enum1161.class)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("A")));
        assertSame(Enum1161.A, result);

        result = MAPPER.readerFor(Enum1161.class)
                .with(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("a")));
        assertSame(Enum1161.A, result);

        // and once again, going back to defaults
        result = MAPPER.readerFor(Enum1161.class)
                .without(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("A")));
        assertSame(Enum1161.A, result);
    }
    
    public void testEnumWithDefaultAnnotation() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        EnumWithDefaultAnno myEnum = mapper.readValue("\"foo\"", EnumWithDefaultAnno.class);
        assertSame(EnumWithDefaultAnno.OTHER, myEnum);
    }

    public void testEnumWithDefaultAnnotationUsingIndexInBound1() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        EnumWithDefaultAnno myEnum = mapper.readValue("1", EnumWithDefaultAnno.class);
        assertSame(EnumWithDefaultAnno.B, myEnum);
    }

    public void testEnumWithDefaultAnnotationUsingIndexInBound2() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        EnumWithDefaultAnno myEnum = mapper.readValue("2", EnumWithDefaultAnno.class);
        assertSame(EnumWithDefaultAnno.OTHER, myEnum);
    }

    public void testEnumWithDefaultAnnotationUsingIndexSameAsLength() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        EnumWithDefaultAnno myEnum = mapper.readValue("3", EnumWithDefaultAnno.class);
        assertSame(EnumWithDefaultAnno.OTHER, myEnum);
    }

    public void testEnumWithDefaultAnnotationUsingIndexOutOfBound() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        EnumWithDefaultAnno myEnum = mapper.readValue("4", EnumWithDefaultAnno.class);
        assertSame(EnumWithDefaultAnno.OTHER, myEnum);
    }

    public void testEnumWithDefaultAnnotationWithConstructor() throws Exception {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        EnumWithDefaultAnnoAndConstructor myEnum = mapper.readValue("\"foo\"", EnumWithDefaultAnnoAndConstructor.class);
        assertNull("When using a constructor, the default value annotation shouldn't be used.", myEnum);
    }

    public void testExceptionFromCustomEnumKeyDeserializer() throws Exception {
        ObjectMapper mapper = newJsonMapper()
                .registerModule(new EnumModule());
        try {
            mapper.readValue("{\"TWO\": \"dumpling\"}",
                    new TypeReference<Map<AnEnum, String>>() {});
            fail("No exception");
        } catch (MismatchedInputException e) {
            assertTrue(e.getMessage().contains("Undefined AnEnum"));
        }
    }

    // [databind#2164]
    public void testWrapExceptions() throws Exception
    {
        // By default, wrap:
        try {
            MAPPER.readerFor(TestEnum2164.class)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("B")));
            fail("Should not pass");
        } catch (ValueInstantiationException e) {
            verifyException(e, "2164");
        }

        // But can disable:
        try {
            MAPPER.readerFor(TestEnum2164.class)
                .without(DeserializationFeature.WRAP_EXCEPTIONS)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("B")));
            fail("Should not pass");
        } catch (JsonMappingException e) {
            fail("Wrong exception, should not wrap, got: "+e);
        } catch (IllegalArgumentException e) {
            verifyException(e, "2164");
        }
    }

    // [databind#2309]
    public void testEnumToStringNull2309() throws Exception
    {
        Enum2309 value = MAPPER.readerFor(Enum2309.class)
                .with(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack(quote("NON_NULL")));
        assertEquals(Enum2309.NON_NULL, value);
    }

}
