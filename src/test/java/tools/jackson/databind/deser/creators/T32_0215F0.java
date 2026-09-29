package tools.jackson.databind.deser.creators;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.Version;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.deser.ValueInstantiators;
import tools.jackson.databind.deser.bean.PropertyValueBuffer;
import tools.jackson.databind.deser.std.StdValueInstantiator;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0215F0 {
private static final byte[] CREATOR_541 = VPackWireFixtureTest.hex(
            "14 43 43 66 6f 6f 14 2f 41 30 14 13 41 70 30 45 73 74 75 66 66 "
          + "13 07 41 61 41 62 02 02 41 31 14 15 41 70 29 e8 03 45 73 74 75 66 66 "
          + "13 07 41 63 41 64 02 02 02 47 61 6e 75 6d 62 65 72 2b 92 5b 83 01 02");
private static final byte[] MULTI_CTOR_421 = VPackWireFixtureTest.hex(
            "14 0f 41 61 43 31 32 33 41 62 43 66 6f 6f 02");
private static final byte[] SERIALIZED_PRODUCT = VPackWireFixtureTest.hex(
            "4b 74 65 73 74 50 72 6f 64 75 63 74");
private static final byte[] PRODUCT_OBJECT = VPackWireFixtureTest.hex(
            "14 1d 44 6e 61 6d 65 45 64 75 6d 6d 79 45 6f 74 68 65 72 0a "
          + "46 65 72 72 6f 72 73 0a 03");
private static final byte[] PRODUCT_STRING = VPackWireFixtureTest.hex(
            "4b 74 65 73 74 50 72 6f 64 75 63 74");
private static final byte[] PRODUCT_WRAPPED_STRING = VPackWireFixtureTest.hex(
            "13 0f 4b 74 65 73 74 50 72 6f 64 75 63 74 01");
private static final byte[] SIMPLE_VALUE_5008 = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 46 61 62 63 31 32 33 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] ALL_PRESENT = VPackWireFixtureTest.hex(
            "14 13 41 61 28 08 41 62 28 09 41 63 41 79 41 64 41 7a 04");
private static final byte[] A_ABSENT = VPackWireFixtureTest.hex(
            "14 0f 41 62 28 09 41 63 41 79 41 64 41 7a 03");
private static final byte[] B_ABSENT = VPackWireFixtureTest.hex(
            "14 0f 41 61 28 08 41 63 41 79 41 64 41 7a 03");
private static final byte[] C_ABSENT = VPackWireFixtureTest.hex(
            "14 0f 41 61 28 08 41 62 28 09 41 64 41 7a 03");
private static final byte[] D_ABSENT = VPackWireFixtureTest.hex(
            "14 0f 41 61 28 08 41 62 28 09 41 63 41 79 03");
private static final byte[] BIG_BUCKET_VALUES = VPackWireFixtureTest.hex(
            "14 18 43 69 30 33 30 43 69 31 31 31 43 73 30 35 18 "
          + "43 73 30 38 41 78 04");
private static final byte[] THIRTY_TWO_PROPERTIES = VPackWireFixtureTest.hex(
            "14 b2 03 42 70 31 48 4e 6f 74 4e 75 6c 6c 31 42"
          + "70 32 48 4e 6f 74 4e 75 6c 6c 32 42 70 33 48 4e"
          + "6f 74 4e 75 6c 6c 33 42 70 34 48 4e 6f 74 4e 75"
          + "6c 6c 34 42 70 35 48 4e 6f 74 4e 75 6c 6c 35 42"
          + "70 36 48 4e 6f 74 4e 75 6c 6c 36 42 70 37 48 4e"
          + "6f 74 4e 75 6c 6c 37 42 70 38 48 4e 6f 74 4e 75"
          + "6c 6c 38 42 70 39 48 4e 6f 74 4e 75 6c 6c 39 43"
          + "70 31 30 49 4e 6f 74 4e 75 6c 6c 31 30 43 70 31"
          + "31 49 4e 6f 74 4e 75 6c 6c 31 31 43 70 31 32 49"
          + "4e 6f 74 4e 75 6c 6c 31 32 43 70 31 33 49 4e 6f"
          + "74 4e 75 6c 6c 31 33 43 70 31 34 49 4e 6f 74 4e"
          + "75 6c 6c 31 34 43 70 31 35 49 4e 6f 74 4e 75 6c"
          + "6c 31 35 43 70 31 36 49 4e 6f 74 4e 75 6c 6c 31"
          + "36 43 70 31 37 49 4e 6f 74 4e 75 6c 6c 31 37 43"
          + "70 31 38 49 4e 6f 74 4e 75 6c 6c 31 38 43 70 31"
          + "39 49 4e 6f 74 4e 75 6c 6c 31 39 43 70 32 30 49"
          + "4e 6f 74 4e 75 6c 6c 32 30 43 70 32 31 49 4e 6f"
          + "74 4e 75 6c 6c 32 31 43 70 32 32 49 4e 6f 74 4e"
          + "75 6c 6c 32 32 43 70 32 33 49 4e 6f 74 4e 75 6c"
          + "6c 32 33 43 70 32 34 49 4e 6f 74 4e 75 6c 6c 32"
          + "34 43 70 32 35 49 4e 6f 74 4e 75 6c 6c 32 35 43"
          + "70 32 36 49 4e 6f 74 4e 75 6c 6c 32 36 43 70 32"
          + "37 49 4e 6f 74 4e 75 6c 6c 32 37 43 70 32 38 49"
          + "4e 6f 74 4e 75 6c 6c 32 38 43 70 32 39 49 4e 6f"
          + "74 4e 75 6c 6c 32 39 43 70 33 30 49 4e 6f 74 4e"
          + "75 6c 6c 33 30 43 70 33 31 49 4e 6f 74 4e 75 6c"
          + "6c 33 31 43 70 33 32 49 4e 6f 74 4e 75 6c 6c 33"
          + "32 20");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: TestCreators3#testCreator541.
    void testCreator541() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(tools.jackson.databind.MapperFeature.USE_GETTERS_AS_SETTERS)
                .build();
        Value541 value = mapper.readValue(CREATOR_541, Value541.class);
        assertNotNull(value);
        assertNotNull(value.foo);
        assertEquals(2, value.foo.size());
        assertEquals(25385874L, value.getAnumber());
        assertEquals(List.of("a", "b"), value.foo.get(0).getStuff());
        assertEquals(1000L, value.foo.get(1).getP());
    }

    // Provenance: TestCreators3#testMultiCtor421.
    void testMultiCtor421() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new MyParamIntrospector())
                .build();
        MultiCtor value = mapper.readValue(MULTI_CTOR_421, MultiCtor.class);
        assertNotNull(value);
        assertEquals("123", value.a);
        assertEquals("foo", value.b);
    }

    // Provenance: TestCreators3#testSerialization.
    void testSerialization() throws Exception {
        assertArrayEquals(SERIALIZED_PRODUCT,
                MAPPER.writeValueAsBytes(new Product1853(false, "testProduct")));
    }

    // Provenance: TestCreators3#testDeserializationFromObject.
    void testDeserializationFromObject() throws Exception {
        assertEquals("PROP:dummy", MAPPER.readValue(PRODUCT_OBJECT, Product1853.class).getName());
    }

    // Provenance: TestCreators3#testDeserializationFromString.
    void testDeserializationFromString() throws Exception {
        assertEquals("DELEG:testProduct", MAPPER.readValue(PRODUCT_STRING, Product1853.class).getName());
    }

    // Provenance: TestCreators3#testDeserializationFromWrappedString.
    void testDeserializationFromWrappedString() throws Exception {
        Product1853 value = MAPPER.readerFor(Product1853.class)
                .with(tools.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(PRODUCT_WRAPPED_STRING);
        assertEquals("DELEG:testProduct", value.getName());
    }

    // Provenance: TestCreators3#testSimpleValue5008.
    void testSimpleValue5008() throws Exception {
        SimpleValue5008 value = MAPPER.readValue(SIMPLE_VALUE_5008, SimpleValue5008.class);
        assertEquals("abc123", value.value);
    }
private static void assertBucket(Bucket value, int a, int b, String c, String d) {
        assertEquals(a, value.a);
        assertEquals(b, value.b);
        assertEquals(c, value.c);
        assertEquals(d, value.d);
    }
static final class Value541 {
        @JsonProperty("foo") protected Map<Integer, Bar> foo;
        @JsonProperty("anumber") protected long anumber;
        public Map<Integer, Bar> getFoo() { return foo; }
        public long getAnumber() { return anumber; }
    }
static final class Bar {
        private final long p;
        private final List<String> stuff;
        @JsonCreator Bar(@JsonProperty("p") long p, @JsonProperty("stuff") List<String> stuff) {
            this.p = p; this.stuff = stuff;
        }
        @JsonProperty("s") public List<String> getStuff() { return stuff; }
        public long getP() { return p; }
    }
static class MultiCtor {
        String a, b;
        private MultiCtor() { }
        private MultiCtor(String a, String b, Boolean ignored) { this.a = a; this.b = b; }
        @JsonCreator static MultiCtor factory(@JsonProperty("a") String a, @JsonProperty("b") String b) {
            return new MultiCtor(a, b, Boolean.TRUE);
        }
    }
@SuppressWarnings("serial")
    static class MyParamIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                return switch (parameter.getIndex()) {
                case 0 -> "a";
                case 1 -> "b";
                case 2 -> "c";
                default -> "param" + parameter.getIndex();
                };
            }
            return super.findImplicitPropertyName(config, member);
        }
    }
public static class Product1853 {
        String name;
        public Object other, errors;
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public Product1853(@JsonProperty("name") String name) { this.name = "PROP:" + name; }
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Product1853 from(String name) { return new Product1853(false, "DELEG:" + name); }
        Product1853(boolean ignored, String name) { this.name = name; }
        @JsonValue public String getName() { return name; }
    }
static class SimpleValue5008 {
        final String value;
        @JsonCreator public SimpleValue5008(@JsonProperty("value") String value) { this.value = value; }
    }
static class Bucket {
        static final int DEFAULT_A = 111, DEFAULT_B = 222;
        static final String DEFAULT_C = "defaultC", DEFAULT_D = "defaultD";
        final int a, b; final String c, d;
        @JsonCreator Bucket(@JsonProperty("a") int a, @JsonProperty("b") int b,
                @JsonProperty("c") String c, @JsonProperty("d") String d) {
            this.a = a; this.b = b; this.c = c; this.d = d;
        }
    }
static class BucketInstantiator extends StdValueInstantiator {
        BucketInstantiator(StdValueInstantiator source) { super(source); }
        @Override public Object createFromObjectWith(DeserializationContext ctxt,
                SettableBeanProperty[] props, PropertyValueBuffer buffer) {
            int a = Bucket.DEFAULT_A, b = Bucket.DEFAULT_B;
            String c = Bucket.DEFAULT_C, d = Bucket.DEFAULT_D;
            for (SettableBeanProperty prop : props) {
                if (!buffer.hasParameter(prop)) continue;
                Object value = buffer.getParameter(ctxt, prop);
                switch (prop.getName()) {
                case "a" -> a = (Integer) value;
                case "b" -> b = (Integer) value;
                case "c" -> c = (String) value;
                case "d" -> d = (String) value;
                default -> { }
                }
            }
            return new Bucket(a, b, c, d);
        }
    }
static class BigBucket {
        static final int DEFAULT_I = 555; static final String DEFAULT_S = "defaultS";
        final int i01, i02, i03, i04, i05, i06, i07, i08, i09, i10, i11, i12, i13, i14, i15, i16;
        final String s01, s02, s03, s04, s05, s06, s07, s08, s09, s10, s11, s12, s13, s14, s15, s16;
        @JsonCreator BigBucket(
                @JsonProperty("i01") int i01, @JsonProperty("i02") int i02, @JsonProperty("i03") int i03,
                @JsonProperty("i04") int i04, @JsonProperty("i05") int i05, @JsonProperty("i06") int i06,
                @JsonProperty("i07") int i07, @JsonProperty("i08") int i08, @JsonProperty("i09") int i09,
                @JsonProperty("i10") int i10, @JsonProperty("i11") int i11, @JsonProperty("i12") int i12,
                @JsonProperty("i13") int i13, @JsonProperty("i14") int i14, @JsonProperty("i15") int i15,
                @JsonProperty("i16") int i16, @JsonProperty("s01") String s01, @JsonProperty("s02") String s02,
                @JsonProperty("s03") String s03, @JsonProperty("s04") String s04, @JsonProperty("s05") String s05,
                @JsonProperty("s06") String s06, @JsonProperty("s07") String s07, @JsonProperty("s08") String s08,
                @JsonProperty("s09") String s09, @JsonProperty("s10") String s10, @JsonProperty("s11") String s11,
                @JsonProperty("s12") String s12, @JsonProperty("s13") String s13, @JsonProperty("s14") String s14,
                @JsonProperty("s15") String s15, @JsonProperty("s16") String s16, @JsonProperty("dummy") boolean dummy) {
            this.i01=i01; this.i02=i02; this.i03=i03; this.i04=i04; this.i05=i05; this.i06=i06; this.i07=i07; this.i08=i08;
            this.i09=i09; this.i10=i10; this.i11=i11; this.i12=i12; this.i13=i13; this.i14=i14; this.i15=i15; this.i16=i16;
            this.s01=s01; this.s02=s02; this.s03=s03; this.s04=s04; this.s05=s05; this.s06=s06; this.s07=s07; this.s08=s08;
            this.s09=s09; this.s10=s10; this.s11=s11; this.s12=s12; this.s13=s13; this.s14=s14; this.s15=s15; this.s16=s16;
        }
    }
static class BigBucketInstantiator extends StdValueInstantiator {
        BigBucketInstantiator(StdValueInstantiator source) { super(source); }
        @Override public Object createFromObjectWith(DeserializationContext ctxt,
                SettableBeanProperty[] props, PropertyValueBuffer buffer) {
            int[] ints = new int[16];
            String[] strings = new String[16];
            java.util.Arrays.fill(ints, BigBucket.DEFAULT_I);
            java.util.Arrays.fill(strings, BigBucket.DEFAULT_S);
            for (SettableBeanProperty prop : props) {
                if (!buffer.hasParameter(prop)) continue;
                Object value = buffer.getParameter(ctxt, prop);
                String name = prop.getName();
                if (name.startsWith("i")) ints[Integer.parseInt(name.substring(1)) - 1] = (Integer) value;
                else if (name.startsWith("s")) strings[Integer.parseInt(name.substring(1)) - 1] = (String) value;
            }
            return new BigBucket(ints[0],ints[1],ints[2],ints[3],ints[4],ints[5],ints[6],ints[7],
                    ints[8],ints[9],ints[10],ints[11],ints[12],ints[13],ints[14],ints[15],
                    strings[0],strings[1],strings[2],strings[3],strings[4],strings[5],strings[6],strings[7],
                    strings[8],strings[9],strings[10],strings[11],strings[12],strings[13],strings[14],strings[15],false);
        }
    }
static class BucketModule extends SimpleModule {
        @Override public void setupModule(SetupContext context) {
            context.addValueInstantiators(new ValueInstantiators.Base() {
                @Override public ValueInstantiator modifyValueInstantiator(DeserializationConfig config,
                        BeanDescription.Supplier beanDescRef, ValueInstantiator defaultInstantiator) {
                    if (defaultInstantiator instanceof StdValueInstantiator source) {
                        if (beanDescRef.getBeanClass() == Bucket.class) return new BucketInstantiator(source);
                        if (beanDescRef.getBeanClass() == BigBucket.class) return new BigBucketInstantiator(source);
                    }
                    return defaultInstantiator;
                }
            });
        }
    }
static class ClassWith32Props {
        final String p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16,
                p17,p18,p19,p20,p21,p22,p23,p24,p25,p26,p27,p28,p29,p30,p31,p32;
        @JsonCreator ClassWith32Props(@JsonProperty("p1") String p1,@JsonProperty("p2") String p2,@JsonProperty("p3") String p3,@JsonProperty("p4") String p4,
                @JsonProperty("p5") String p5,@JsonProperty("p6") String p6,@JsonProperty("p7") String p7,@JsonProperty("p8") String p8,
                @JsonProperty("p9") String p9,@JsonProperty("p10") String p10,@JsonProperty("p11") String p11,@JsonProperty("p12") String p12,
                @JsonProperty("p13") String p13,@JsonProperty("p14") String p14,@JsonProperty("p15") String p15,@JsonProperty("p16") String p16,
                @JsonProperty("p17") String p17,@JsonProperty("p18") String p18,@JsonProperty("p19") String p19,@JsonProperty("p20") String p20,
                @JsonProperty("p21") String p21,@JsonProperty("p22") String p22,@JsonProperty("p23") String p23,@JsonProperty("p24") String p24,
                @JsonProperty("p25") String p25,@JsonProperty("p26") String p26,@JsonProperty("p27") String p27,@JsonProperty("p28") String p28,
                @JsonProperty("p29") String p29,@JsonProperty("p30") String p30,@JsonProperty("p31") String p31,@JsonProperty("p32") String p32) {
            this.p1=p1;this.p2=p2;this.p3=p3;this.p4=p4;this.p5=p5;this.p6=p6;this.p7=p7;this.p8=p8;
            this.p9=p9;this.p10=p10;this.p11=p11;this.p12=p12;this.p13=p13;this.p14=p14;this.p15=p15;this.p16=p16;
            this.p17=p17;this.p18=p18;this.p19=p19;this.p20=p20;this.p21=p21;this.p22=p22;this.p23=p23;this.p24=p24;
            this.p25=p25;this.p26=p26;this.p27=p27;this.p28=p28;this.p29=p29;this.p30=p30;this.p31=p31;this.p32=p32;
        }
    }
static class ClassWith32Module extends SimpleModule {
        ClassWith32Module() { super("test", Version.unknownVersion()); }
        @Override public void setupModule(SetupContext context) {
            context.addValueInstantiators(new ValueInstantiators.Base() {
                @Override public ValueInstantiator modifyValueInstantiator(DeserializationConfig config,
                        BeanDescription.Supplier beanDescRef, ValueInstantiator defaultInstantiator) {
                    if (beanDescRef.getBeanClass() == ClassWith32Props.class) {
                        return new VerifyingValueInstantiator((StdValueInstantiator) defaultInstantiator);
                    }
                    return defaultInstantiator;
                }
            });
        }
    }
static class VerifyingValueInstantiator extends StdValueInstantiator {
        VerifyingValueInstantiator(StdValueInstantiator source) { super(source); }
        @Override public Object createFromObjectWith(DeserializationContext ctxt,
                SettableBeanProperty[] props, PropertyValueBuffer buffer) {
            for (SettableBeanProperty prop : props) {
                assertTrue(buffer.hasParameter(prop), "prop " + prop.getName() + " was not present");
            }
            return super.createFromObjectWith(ctxt, props, buffer);
        }
    }

    void __invoke_testCreator541() throws Exception {
        try {
            testCreator541();
        } finally {
        }
    }


    void __invoke_testMultiCtor421() throws Exception {
        try {
            testMultiCtor421();
        } finally {
        }
    }


    void __invoke_testSerialization() throws Exception {
        try {
            testSerialization();
        } finally {
        }
    }


    void __invoke_testDeserializationFromObject() throws Exception {
        try {
            testDeserializationFromObject();
        } finally {
        }
    }


    void __invoke_testDeserializationFromString() throws Exception {
        try {
            testDeserializationFromString();
        } finally {
        }
    }


    void __invoke_testDeserializationFromWrappedString() throws Exception {
        try {
            testDeserializationFromWrappedString();
        } finally {
        }
    }


    void __invoke_testSimpleValue5008() throws Exception {
        try {
            testSimpleValue5008();
        } finally {
        }
    }

}
