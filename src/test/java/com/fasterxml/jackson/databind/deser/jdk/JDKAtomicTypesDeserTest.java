package com.fasterxml.jackson.databind.deser.jdk;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.atomic.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class JDKAtomicTypesDeserTest
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({ @JsonSubTypes.Type(Impl.class) })
    static abstract class Base { }

    @JsonTypeName("I")
    static class Impl extends Base {
        public int value;

        public Impl() { }
        public Impl(int v) { value = v; }
    }

    static class RefWrapper
    {
        public AtomicReference<Base> w;

        public RefWrapper() { }
        public RefWrapper(Base b) {
            w = new AtomicReference<Base>(b);
        }
        public RefWrapper(int i) {
            w = new AtomicReference<Base>(new Impl(i));
        }
    }

    static class SimpleWrapper {
        public AtomicReference<Object> value;

        public SimpleWrapper(Object o) { value = new AtomicReference<Object>(o); }
    }

    static class RefiningWrapper {
        @JsonDeserialize(contentAs=BigDecimal.class)
        public AtomicReference<Serializable> value;
    }

    // Additional tests for improvements with [databind#932]

    static class UnwrappingRefParent {
        @JsonUnwrapped(prefix = "XX.")
        public AtomicReference<Child> child = new AtomicReference<Child>(new Child());
    }

    static class Child {
        public String name = "Bob";
    }

    static class Parent {
        private Child child = new Child();

        @JsonUnwrapped
        public Child getChild() {
             return child;
        }
    }

    static class WrappedString {
        String value;

        public WrappedString(String s) { value = s; }
    }

    static class AtomicRefReadWrapper {
        @JsonDeserialize(contentAs=WrappedString.class)
        public AtomicReference<Object> value;
    }

    static class LCStringWrapper {
        @JsonDeserialize(contentUsing=LowerCasingDeserializer.class)
        public AtomicReference<String> value;

        public LCStringWrapper() { }
    }

    @JsonPropertyOrder({ "a", "b" })
    static class Issue1256Bean {
        @JsonSerialize(as=AtomicReference.class)
        public Object a = new AtomicReference<Object>();
        public AtomicReference<Object> b = new AtomicReference<Object>();
    }

    // [databind#2303]
    static class MyBean2303 {
        public AtomicReference<AtomicReference<Integer>> refRef;
    }    

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = objectMapper();
    
    public void testAtomicBoolean() throws Exception
    {
        AtomicBoolean b = MAPPER.readValue("true", AtomicBoolean.class);
        assertTrue(b.get());
    }

    public void testAtomicInt() throws Exception
    {
        AtomicInteger value = MAPPER.readValue("13", AtomicInteger.class);
        assertEquals(13, value.get());
    }

    public void testAtomicLong() throws Exception
    {
        AtomicLong value = MAPPER.readValue("12345678901", AtomicLong.class);
        assertEquals(12345678901L, value.get());
    }

    public void testAtomicReference() throws Exception
    {
        AtomicReference<long[]> value = MAPPER.readValue("[1,2]",
                new com.fasterxml.jackson.core.type.TypeReference<AtomicReference<long[]>>() { });
        Object ob = value.get();
        assertNotNull(ob);
        assertEquals(long[].class, ob.getClass());
        long[] longs = (long[]) ob;
        assertNotNull(longs);
        assertEquals(2, longs.length);
        assertEquals(1, longs[0]);
        assertEquals(2, longs[1]);
    }

    // for [databind#811]
    public void testAbsentExclusion() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        assertEquals(aposToQuotes("{'value':true}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
        assertEquals(aposToQuotes("{}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new SimpleWrapper(null))));
    }

    public void testSerPropInclusionAlways() throws Exception
    {
        JsonInclude.Value incl =
                JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.ALWAYS);
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.setDefaultPropertyInclusion(incl);
        assertEquals(aposToQuotes("{'value':true}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
    }

    public void testSerPropInclusionNonNull() throws Exception
    {
        JsonInclude.Value incl =
                JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_NULL);
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.setDefaultPropertyInclusion(incl);
        assertEquals(aposToQuotes("{'value':true}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
    }

    public void testSerPropInclusionNonAbsent() throws Exception
    {
        JsonInclude.Value incl =
                JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_ABSENT);
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.setDefaultPropertyInclusion(incl);
        assertEquals(aposToQuotes("{'value':true}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
    }

    public void testSerPropInclusionNonEmpty() throws Exception
    {
        JsonInclude.Value incl =
                JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_EMPTY);
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.setDefaultPropertyInclusion(incl);
        assertEquals(aposToQuotes("{'value':true}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
    }

    // [databind#340]
    public void testPolymorphicAtomicReference() throws Exception
    {
        RefWrapper input = new RefWrapper(13);
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input));
        
        RefWrapper result = MAPPER.readValue(json, RefWrapper.class);
        assertNotNull(result.w);
        Object ob = result.w.get();
        assertEquals(Impl.class, ob.getClass());
        assertEquals(13, ((Impl) ob).value);
    }

    // [databind#740]
    public void testFilteringOfAtomicReference() throws Exception
    {
        SimpleWrapper input = new SimpleWrapper(null);
        ObjectMapper mapper = MAPPER;

        // by default, include as null
        assertEquals(aposToQuotes("{'value':null}"), com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input)));

        // ditto with "no nulls"
        mapper = new TestVelocypackMapper().setSerializationInclusion(JsonInclude
                .Include.NON_NULL);
        assertEquals(aposToQuotes("{'value':null}"), com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input)));

        // but not with "non empty"
        mapper = new TestVelocypackMapper().setSerializationInclusion(JsonInclude
                .Include.NON_EMPTY);
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input)));
    }

    public void testTypeRefinement() throws Exception
    {
        RefiningWrapper input = new RefiningWrapper();
        BigDecimal bd = new BigDecimal("0.25");
        input.value = new AtomicReference<Serializable>(bd);
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input));

        // so far so good. But does it come back as expected?
        RefiningWrapper result = MAPPER.readValue(json, RefiningWrapper.class);
        assertNotNull(result.value);
        Object ob = result.value.get();
        assertEquals(BigDecimal.class, ob.getClass());
        assertEquals(bd, ob);
    }

    // [databind#882]: verify `@JsonDeserialize(contentAs=)` works with AtomicReference
    public void testDeserializeWithContentAs() throws Exception
    {
        AtomicRefReadWrapper result = MAPPER.readValue(aposToQuotes("{'value':'abc'}"),
                AtomicRefReadWrapper.class);
         Object v = result.value.get();
         assertNotNull(v);
         assertEquals(WrappedString.class, v.getClass());
         assertEquals("abc", ((WrappedString)v).value);
    }
    
    // [databind#932]: support unwrapping too
    public void testWithUnwrapping() throws Exception
    {
         String jsonExp = aposToQuotes("{'XX.name':'Bob'}");
         String jsonAct = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new UnwrappingRefParent()));
         assertEquals(jsonExp, jsonAct);
    }

    public void testWithCustomDeserializer() throws Exception
    {
        LCStringWrapper w = MAPPER.readValue(aposToQuotes("{'value':'FoobaR'}"),
                LCStringWrapper.class);
        assertEquals("foobar", w.value.get());
    }

    public void testEmpty1256() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(new Issue1256Bean()));
        assertEquals("{}", json);
    }

    // [databind#1307]
    @SuppressWarnings("unchecked")
    public void testNullValueHandling() throws Exception
    {
        AtomicReference<Double> inputData = new AtomicReference<Double>();
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(inputData));
        AtomicReference<Double> readData = (AtomicReference<Double>) MAPPER.readValue(json, AtomicReference.class);
        assertNotNull(readData);
        assertNull(readData.get());
    }

    // [databind#2303]
    public void testNullWithinNested() throws Exception
    {
        final ObjectReader r = MAPPER.readerFor(MyBean2303.class);
        MyBean2303 intRef = r.readValue(com.fasterxml.jackson.VPackUtils.toVPack(" {\"refRef\": 2 } "));
        assertNotNull(intRef.refRef);
        assertNotNull(intRef.refRef.get());
        assertEquals(intRef.refRef.get().get(), new Integer(2));

        MyBean2303 nullRef = r.readValue(com.fasterxml.jackson.VPackUtils.toVPack(" {\"refRef\": null } "));
        assertNotNull(nullRef.refRef);
        assertNotNull(nullRef.refRef.get());
        assertNull(nullRef.refRef.get().get());
    }
}
