package tools.jackson.databind.deser.jdk;

import com.fasterxml.jackson.annotation.*;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

public class JDKAtomicTypesDeserTest
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

    static class LowerCasingDeserializer extends StdScalarDeserializer<String>
    {
        public LowerCasingDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) {
            return p.getString().toLowerCase();
        }
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

    // [modules-java8#214]
    static class ListWrapper {
        @JsonMerge
        public AtomicReference<List<String>> list = new AtomicReference<>();
    }

    static class AtomicRefBean {
        protected AtomicReference<String> _atomic;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public AtomicRefBean(@JsonProperty("atomic") AtomicReference<String> ref) {
            _atomic = ref;
        }
    }

    static class AtomicRefBeanWithEmpty {
        protected AtomicReference<String> _atomic;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public AtomicRefBeanWithEmpty(@JsonProperty("atomic")
            @JsonSetter(nulls = Nulls.AS_EMPTY)
            AtomicReference<String> ref) {
            _atomic = ref;
        }
    }

    static class AtomicRefWithNodeBean {
        protected AtomicReference<JsonNode> _atomicNode;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public AtomicRefWithNodeBean(@JsonProperty("atomic") AtomicReference<JsonNode> ref) {
            _atomicNode = ref;
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testAtomicBoolean() throws Exception
    {
        AtomicBoolean b = MAPPER.readValue(VPackUtils.toVPack("true"), AtomicBoolean.class);
        assertTrue(b.get());
    }

    @Test
    public void testAtomicInt() throws Exception
    {
        AtomicInteger value = MAPPER.readValue(VPackUtils.toVPack("13"), AtomicInteger.class);
        assertEquals(13, value.get());
    }

    @Test
    public void testAtomicLong() throws Exception
    {
        AtomicLong value = MAPPER.readValue(VPackUtils.toVPack("12345678901"), AtomicLong.class);
        assertEquals(12345678901L, value.get());
    }

    // Coerced-from-String / coerced-from-Float paths went through
    // `_parseLong` + `Long.intValue()`, silently truncating values above
    // `Integer.MAX_VALUE`.
    @Test
    public void testAtomicLongFromStringAboveIntRange() throws Exception
    {
        AtomicLong value = MAPPER.readValue(VPackUtils.toVPack("\"9999999999\""), AtomicLong.class);
        assertEquals(9999999999L, value.get());
    }

    @Test
    public void testAtomicLongFromFloatAboveIntRange() throws Exception
    {
        AtomicLong value = MAPPER.readValue(VPackUtils.toVPack("12345678901.25"), AtomicLong.class);
        assertEquals(12345678901L, value.get());
    }

    @Test
    public void testAtomicReference() throws Exception
    {
        AtomicReference<long[]> value = MAPPER.readValue(VPackUtils.toVPack("[1,2]"),
                new TypeReference<AtomicReference<long[]>>() { });
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
    @Test
    public void testAbsentExclusion() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertEquals(a2q("{'value':true}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
        assertEquals(a2q("{}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new SimpleWrapper(null))));
    }

    @Test
    public void testSerPropInclusionAlways() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        i -> JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.ALWAYS))
                .build();
        assertEquals(a2q("{'value':true}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
    }

    @Test
    public void testSerPropInclusionNonNull() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        i -> JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_NULL))
                .build();
        assertEquals(a2q("{'value':true}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
    }

    @Test
    public void testSerPropInclusionNonAbsent() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        i -> JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_ABSENT))
                .build();
        assertEquals(a2q("{'value':true}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
    }

    @Test
    public void testSerPropInclusionNonEmpty() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        i -> JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_EMPTY))
                .build();
        assertEquals(a2q("{'value':true}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new SimpleWrapper(Boolean.TRUE))));
    }

    // [databind#340]
    @Test
    public void testPolymorphicAtomicReference() throws Exception
    {
        RefWrapper input = new RefWrapper(13);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));

        RefWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), RefWrapper.class);
        assertNotNull(result.w);
        Object ob = result.w.get();
        assertEquals(Impl.class, ob.getClass());
        assertEquals(13, ((Impl) ob).value);
    }

    // [databind#740]
    @Test
    public void testFilteringOfAtomicReference() throws Exception
    {
        SimpleWrapper input = new SimpleWrapper(null);
        ObjectMapper mapper = MAPPER;

        // by default, include as null
        assertEquals(a2q("{'value':null}"), VPackUtils.toJson(mapper.writeValueAsBytes(input)));

        // ditto with "no nulls"
        mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        assertEquals(a2q("{'value':null}"), VPackUtils.toJson(mapper.writeValueAsBytes(input)));

        // but not with "non empty"
        mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build();
        assertEquals("{}", VPackUtils.toJson(mapper.writeValueAsBytes(input)));
    }

    @Test
    public void testTypeRefinement() throws Exception
    {
        RefiningWrapper input = new RefiningWrapper();
        BigDecimal bd = new BigDecimal("0.25");
        input.value = new AtomicReference<Serializable>(bd);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));

        // so far so good. But does it come back as expected?
        RefiningWrapper result = MAPPER.readValue(VPackUtils.toVPack(json), RefiningWrapper.class);
        assertNotNull(result.value);
        Object ob = result.value.get();
        assertEquals(BigDecimal.class, ob.getClass());
        assertEquals(bd, ob);
    }

    // [databind#882]: verify `@JsonDeserialize(contentAs=)` works with AtomicReference
    @Test
    public void testDeserializeWithContentAs() throws Exception
    {
        AtomicRefReadWrapper result = MAPPER.readValue(VPackUtils.toVPack(a2q("{'value':'abc'}")),
                AtomicRefReadWrapper.class);
         Object v = result.value.get();
         assertNotNull(v);
         assertEquals(WrappedString.class, v.getClass());
         assertEquals("abc", ((WrappedString)v).value);
    }

    // [databind#932]: support unwrapping too
    @Test
    public void testWithUnwrapping() throws Exception
    {
         String jsonExp = a2q("{'XX.name':'Bob'}");
         String jsonAct = VPackUtils.toJson(MAPPER.writeValueAsBytes(new UnwrappingRefParent()));
         assertEquals(jsonExp, jsonAct);
    }

    @Test
    public void testWithCustomDeserializer() throws Exception
    {
        LCStringWrapper w = MAPPER.readValue(VPackUtils.toVPack(a2q("{'value':'FoobaR'}")),
                LCStringWrapper.class);
        assertEquals("foobar", w.value.get());
    }

    @Test
    public void testEmpty1256() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertEquals("{}", VPackUtils.toJson(mapper.writeValueAsBytes(new Issue1256Bean())));
    }

    // [databind#1307]
    @SuppressWarnings("unchecked")
    @Test
    public void testNullValueHandling() throws Exception
    {
        AtomicReference<Double> inputData = new AtomicReference<Double>();
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(inputData));
        AtomicReference<Double> readData = (AtomicReference<Double>) MAPPER.readValue(VPackUtils.toVPack(json), AtomicReference.class);
        assertNotNull(readData);
        assertNull(readData.get());
    }

    // [databind#2303]
    @Test
    public void testNullWithinNested() throws Exception
    {
        final ObjectReader r = MAPPER.readerFor(MyBean2303.class);
        MyBean2303 intRef = r.readValue(VPackUtils.toVPack(" {\"refRef\": 2 } "));
        assertNotNull(intRef.refRef);
        assertNotNull(intRef.refRef.get());
        assertEquals(intRef.refRef.get().get(), Integer.valueOf(2));

        MyBean2303 nullRef = r.readValue(VPackUtils.toVPack(" {\"refRef\": null } "));
        assertNotNull(nullRef.refRef);
        assertNotNull(nullRef.refRef.get());
        assertNull(nullRef.refRef.get().get());
    }

    // for [modules-java8#214]: ReferenceType of List, merge
    @Test
    public void testMergeToListViaRef() throws Exception
    {
        ListWrapper base = MAPPER.readValue(VPackUtils.toVPack(a2q("{'list':['a']}")),
                ListWrapper.class);
        assertNotNull(base.list);
        assertEquals(Arrays.asList("a"), base.list.get());

        ListWrapper merged = MAPPER.readerForUpdating(base)
                .readValue(VPackUtils.toVPack(a2q("{'list':['b']}")));
        assertSame(base, merged);
        assertEquals(Arrays.asList("a", "b"), base.list.get());
    }

    // Verify expected behavior of AtomicReference wrt nulls, absent
    // values.
    //
    // @since 2.14
    @Test
    public void testAbsentAtomicRefViaCreator() throws Exception
    {
        AtomicRefBean bean;

        ObjectReader r = MAPPER.readerFor(AtomicRefBean.class);
        
        // First: null should become empty, non-null reference
        bean = r.readValue(VPackUtils.toVPack(a2q("{'atomic':null}")));
        assertNotNull(bean._atomic);
        assertNull(bean._atomic.get());

        // And then absent (missing), via Creator method, should become actual null
        // 25-Oct-2025, tatu: [databind#53530] Actually, by default should become
        //   empty ref
        bean = r.readValue(VPackUtils.toVPack("{}"));
        assertNotNull(bean._atomic);
        assertNull(bean._atomic.get());

        // 25-Oct-2025, tatu: but can reconfigure to get `null` instead
        bean = r.with(DeserializationFeature.USE_NULL_FOR_MISSING_REFERENCE_VALUES)
                .readValue(VPackUtils.toVPack("{}"));
        assertNull(bean._atomic);

        // Plus can override handling to produce empty
        AtomicRefBeanWithEmpty bean2 = MAPPER.readValue(VPackUtils.toVPack("{}"), AtomicRefBeanWithEmpty.class);
        assertNotNull(bean2._atomic);
        assertNull(bean2._atomic.get());

    }

    // @since 2.14
    @Test
    public void testAtomicRefWithNodeViaCreator() throws Exception
    {
        AtomicRefWithNodeBean bean;

        // Somewhat usual, `null` SHOULD become `NullNode`
        bean = MAPPER.readValue(VPackUtils.toVPack(a2q("{'atomic':null}")), AtomicRefWithNodeBean.class);
        assertNotNull(bean._atomicNode);
        assertNotNull(bean._atomicNode.get());
        JsonNode n = bean._atomicNode.get();
        assertTrue(n.isNull());

        // And then absent (missing), via Creator method, should become actual null
        // 25-Oct-2025, tatu: [databind#5350] Not any longer... (by default)
        bean = MAPPER.readValue(VPackUtils.toVPack("{}"), AtomicRefWithNodeBean.class);
        assertNotNull(bean._atomicNode);
        n = bean._atomicNode.get();
        assertTrue(n.isNull());

        // but can reconfigure to get `null` instead
        bean = MAPPER.readerFor(AtomicRefWithNodeBean.class)
                .with(DeserializationFeature.USE_NULL_FOR_MISSING_REFERENCE_VALUES)
                .readValue(VPackUtils.toVPack("{}"));
        assertNull(bean._atomicNode);
    }
}
