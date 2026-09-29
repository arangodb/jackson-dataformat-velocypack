package tools.jackson.databind.jsontype.ext;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0445Fixture {
private static final byte[] SIMPLE = VPackWireFixtureTest.hex(
            "14 21 44 62 65 61 6e 14 0b 45 76 61 6c 75 65 28 0b 01 "
          + "47 65 78 74 54 79 70 65 45 76 62 65 61 6e 02");
private static final byte[] ISSUE_831 = VPackWireFixtureTest.hex(
            "14 21 47 70 65 74 54 79 70 65 43 64 6f 67 43 70 65 74 "
          + "14 0e 44 6e 61 6d 65 45 50 6c 75 74 6f 01 02");
private static final byte[] ISSUE_3008 = VPackWireFixtureTest.hex(
            "14 10 44 74 79 70 65 18 45 66 72 75 69 74 18 02");
private static final byte[] MULTIPLE_TYPE_IDS = VPackWireFixtureTest.hex(
            "14 68 46 76 61 6c 75 65 31 14 0a 45 76 61 6c 75 65 33 01 "
          + "48 65 78 74 54 79 70 65 31 45 76 62 65 61 6e 46 76 61 6c 75 65 32 "
          + "14 0a 45 76 61 6c 75 65 34 01 43 66 6f 6f 33 48 65 78 74 54 79 70 65 32 "
          + "45 76 62 65 61 6e 46 76 61 6c 75 65 33 14 0a 45 76 61 6c 75 65 35 01 "
          + "48 65 78 74 54 79 70 65 33 45 76 62 65 61 6e 07");
private static final byte[] MULTIPLE_VALUES_FIRST = VPackWireFixtureTest.hex(
            "14 2a 44 74 79 70 65 41 31 46 66 69 65 6c 64 31 14 09 41 61 43 41 41 41 01 "
          + "46 66 69 65 6c 64 32 14 09 41 63 43 43 43 43 01 03");
private static final byte[] MULTIPLE_VALUES_LAST = VPackWireFixtureTest.hex(
            "14 2a 46 66 69 65 6c 64 31 14 09 41 61 43 41 41 41 01 "
          + "46 66 69 65 6c 64 32 14 09 41 63 43 43 43 43 01 44 74 79 70 65 41 31 03");
private static final byte[] MULTIPLE_VALUES_MIDDLE = VPackWireFixtureTest.hex(
            "14 2a 46 66 69 65 6c 64 31 14 09 41 61 43 41 41 41 01 "
          + "44 74 79 70 65 41 31 46 66 69 65 6c 64 32 14 09 41 63 43 43 43 43 01 03");
private static final byte[] DEFAULT_WITHOUT_TYPE = VPackWireFixtureTest.hex(
            "14 13 44 62 65 61 6e 14 0b 45 76 61 6c 75 65 28 0d 01 01");
private static final byte[] NATURAL_INT = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 28 0d 01");
private static final byte[] NATURAL_BOOLEAN = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 1a 01");
private static final byte[] NATURAL_STRING = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 46 66 6f 6f 62 61 72 01");
private static final byte[] AS_VALUE = VPackWireFixtureTest.hex(
            "14 18 45 76 61 6c 75 65 29 39 30 44 74 79 70 65 46 74 68 69 6e 67 79 02");
private static final byte[] ISSUE_798 = VPackWireFixtureTest.hex(
            "14 73 55 62 61 73 65 43 6f 6e 74 61 69 6e 65 72 50 72 6f 70 65 72 74 79 "
          + "4b 62 63 20 70 72 6f 70 20 76 61 6c 44 62 61 73 65 14 41 4c 62 61 73 65 50 72 6f 70 65 72 74 79 "
          + "4d 62 61 73 65 20 70 72 6f 70 20 76 61 6c 50 64 65 72 69 76 65 64 31 50 72 6f 70 65 72 74 79 "
          + "51 64 65 72 69 76 65 64 31 20 70 72 6f 70 20 76 61 6c 02 44 74 79 70 65 42 64 31 03");
private static final byte[] INVERSE_FIRST = VPackWireFixtureTest.hex(
            "0b 65 02 47 70 61 79 6c 6f 61 64 0b 13 01 49 73" +
                "6f 6d 65 74 68 69 6e 67 44 74 65 73 74 03 45 63" +
                "6c 61 73 73 7e 74 6f 6f 6c 73 2e 6a 61 63 6b 73" +
                "6f 6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f 6e" +
                "74 79 70 65 2e 65 78 74 2e 54 33 32 5f 30 34 34" +
                "35 46 69 78 74 75 72 65 24 50 61 79 6c 6f 61 64" +
                "39 32 38 1e 03");
private static final byte[] INVERSE_LAST = VPackWireFixtureTest.hex(
            "0b 65 02 45 63 6c 61 73 73 7e 74 6f 6f 6c 73 2e" +
                "6a 61 63 6b 73 6f 6e 2e 64 61 74 61 62 69 6e 64" +
                "2e 6a 73 6f 6e 74 79 70 65 2e 65 78 74 2e 54 33" +
                "32 5f 30 34 34 35 46 69 78 74 75 72 65 24 50 61" +
                "79 6c 6f 61 64 39 32 38 47 70 61 79 6c 6f 61 64" +
                "0b 13 01 49 73 6f 6d 65 74 68 69 6e 67 44 74 65" +
                "73 74 03 03 48");
private final ObjectMapper mapper = new VPackMapper();

    // Provenance: ExternalTypeIdTest#testSimpleSerialization().
    void testSimpleSerializationVpack() throws Exception {
        ObjectMapper typed = VPackMapper.builder().registerSubtypes(ValueBean.class).build();
        Map<?, ?> wire = typed.readValue(typed.writeValueAsBytes(new ExternalBean(11)), Map.class);
        assertEquals(List.of("bean", "extType"), List.copyOf(wire.keySet()));
        assertEquals(11, ((Number) ((Map<?, ?>) wire.get("bean")).get("value")).intValue());
        assertEquals("vbean", wire.get("extType"));
    }

    // Provenance: ExternalTypeIdTest#testImproperExternalIdSerialization().
    void testImproperExternalIdSerializationVpack() throws Exception {
        Map<?, ?> wire = mapper.readValue(mapper.writeValueAsBytes(new FunkyExternalBean()), Map.class);
        assertEquals(List.of("extType", "i"), List.copyOf(wire.keySet()));
        assertEquals("funk", wire.get("extType"));
        assertEquals(3, ((Number) wire.get("i")).intValue());
    }

    // Provenance: ExternalTypeIdTest#testSimpleDeserialization().
    void testSimpleDeserializationVpack() throws Exception {
        ObjectMapper typed = VPackMapper.builder().registerSubtypes(ValueBean.class).build();
        ExternalBean result = typed.readValue(SIMPLE, ExternalBean.class);
        assertNotNull(result);
        assertInstanceOf(ValueBean.class, result.bean);
        assertEquals(11, ((ValueBean) result.bean).value);
        result = typed.readValue(VPackWireFixtureTest.hex(
                "14 21 47 65 78 74 54 79 70 65 45 76 62 65 61 6e 44 62 65 61 6e "
              + "14 0b 45 76 61 6c 75 65 28 0d 01 02"), ExternalBean.class);
        assertEquals(13, ((ValueBean) result.bean).value);
    }

    // Provenance: ExternalTypeIdTest#testMultipleTypeIdsDeserialization().
    void testMultipleTypeIdsDeserializationVpack() throws Exception {
        ObjectMapper typed = VPackMapper.builder().registerSubtypes(ValueBean.class).build();
        ExternalBean3 result = typed.readValue(MULTIPLE_TYPE_IDS, ExternalBean3.class);
        assertNotNull(result);
        assertEquals(3, ((ValueBean) result.value1).value);
        assertEquals(4, ((ValueBean) result.value2).value);
        assertEquals(5, ((ValueBean) result.value3).value);
        assertEquals(3, result.foo);
    }

    // Provenance: ExternalTypeIdTest#testIssue798().
    void testIssue798Vpack() throws Exception {
        BaseContainer result = mapper.readValue(ISSUE_798, BaseContainer.class);
        assertEquals("bc prop val", result.getBaseContainerProperty());
        assertInstanceOf(Derived1.class, result.getBase());
        Derived1 derived = (Derived1) result.getBase();
        assertEquals("base prop val", derived.getBaseProperty());
        assertEquals("derived1 prop val", derived.getDerived1Property());
    }

    // Provenance: ExternalTypeIdTest#testIssue831().
    void testIssue831Vpack() throws Exception {
        House831 result = mapper.readValue(ISSUE_831, House831.class);
        assertNotNull(result);
        assertSame(Dog.class, result.pet.getClass());
        assertEquals("dog", result.getPetType());
        assertEquals("Pluto", ((Dog) result.pet).name);
    }

    // Provenance: ExternalTypeIdTest#testInverseExternalId928().
    void testInverseExternalId928Vpack() throws Exception {
        ObjectMapper typed = VPackMapper.builder()
                .polymorphicTypeValidator(NoCheckSubTypeValidator.INSTANCE).build();
        Envelope928 envelope = typed.readValue(INVERSE_FIRST, Envelope928.class);
        assertNotNull(envelope);
        assertEquals(Payload928.class, envelope.payload.getClass());
        envelope = typed.readValue(INVERSE_LAST, Envelope928.class);
        assertNotNull(envelope);
        assertEquals(Payload928.class, envelope.payload.getClass());
    }

    // Provenance: ExternalTypeIdTest#testWithAsValue().
    void testWithAsValueVpack() throws Exception {
        ObjectMapper typed = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS).build();
        ExternalTypeWithNonPOJO result = typed.readValue(AS_VALUE, ExternalTypeWithNonPOJO.class);
        assertInstanceOf(AsValueThingy.class, result.value);
        assertEquals(12345L, ((AsValueThingy) result.value).rawDate);
        Map<?, ?> wire = typed.readValue(typed.writeValueAsBytes(
                new ExternalTypeWithNonPOJO(new AsValueThingy(12345L))), Map.class);
        assertEquals(12345L, ((Number) wire.get("value")).longValue());
        assertEquals("thingy", wire.get("type"));
    }

    // Provenance: ExternalTypeIdTest#testWithDefaultAndMissing96().
    void testWithDefaultAndMissing96Vpack() throws Exception {
        ObjectMapper typed = VPackMapper.builder()
                .polymorphicTypeValidator(NoCheckSubTypeValidator.INSTANCE).build();
        ExternalBeanWithDefault defaulted = typed.readValue(DEFAULT_WITHOUT_TYPE,
                ExternalBeanWithDefault.class);
        assertNotNull(defaulted);
        assertSame(ValueBean.class, defaulted.bean.getClass());
        assertEquals(13, ((ValueBean) defaulted.bean).value);
        ExternalBeanWithDefault input = new ExternalBeanWithDefault(13);
        ExternalBeanWithDefault output = typed.readValue(typed.writeValueAsBytes(input),
                ExternalBeanWithDefault.class);
        assertNotNull(output);
        assertSame(ValueBean.class, output.bean.getClass());
    }

    // Provenance: ExternalTypeIdTest#testWithNaturalScalar118().
    void testWithNaturalScalar118Vpack() throws Exception {
        ExternalTypeWithNonPOJO result = mapper.readValue(NATURAL_INT, ExternalTypeWithNonPOJO.class);
        assertInstanceOf(Integer.class, result.value);
        result = mapper.readValue(NATURAL_BOOLEAN, ExternalTypeWithNonPOJO.class);
        assertInstanceOf(Boolean.class, result.value);
        result = mapper.readValue(NATURAL_STRING, ExternalTypeWithNonPOJO.class);
        assertInstanceOf(String.class, result.value);
        assertEquals("foobar", result.value);
    }

    // Provenance: ExternalTypeIdTest#testIssue3008().
    void testIssue3008Vpack() throws Exception {
        Box3008 result = mapper.readValue(ISSUE_3008, Box3008.class);
        assertEquals(null, result.fruit);
        assertEquals(null, result.type);
    }

    // Provenance: ExternalTypeIdTest#testMultipleValuesSingleExtId291().
    void testMultipleValuesSingleExtId291Vpack() throws Exception {
        assert291(MULTIPLE_VALUES_FIRST, "1");
        assert291(MULTIPLE_VALUES_LAST, "1");
        assert291(MULTIPLE_VALUES_MIDDLE, "1");
    }
private void assert291(byte[] input, String expectedType) throws Exception {
        Container291 result = mapper.readValue(input, Container291.class);
        assertInstanceOf(A291.class, result.field1);
        assertEquals("AAA", ((A291) result.field1).a);
        assertInstanceOf(C291.class, result.field2);
        assertEquals("CCC", ((C291) result.field2).c);
        ContainerWithExtra291 extra = mapper.readValue(input, ContainerWithExtra291.class);
        assertEquals(expectedType, extra.type);
        assertInstanceOf(A291.class, extra.field1);
        assertInstanceOf(C291.class, extra.field2);
    }
interface Animal { }
static class Cat implements Animal { public int lives = 9; }
static class Dog implements Animal { public String name; }
@JsonTypeName("vbean")
    static class ValueBean { public int value; ValueBean() { } ValueBean(int v) { value = v; } }
static class ExternalBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "extType")
        public Object bean;
        ExternalBean() { }
        ExternalBean(int value) { bean = new ValueBean(value); }
    }
static class ExternalBean3 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "extType1")
        public Object value1;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "extType2")
        public Object value2;
        public int foo;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "extType3")
        public Object value3;
    }
@JsonTypeName("funk")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "extType")
    static class FunkyExternalBean { public int i = 3; }
@JsonSubTypes({ @JsonSubTypes.Type(value = Derived1.class, name = "d1") })
    interface Base { String getBaseProperty(); }
static class Derived1 implements Base {
        private String derived1Property;
        private String baseProperty;
        protected Derived1() { throw new IllegalStateException("wrong constructor called"); }
        @JsonCreator Derived1(@JsonProperty("derived1Property") String d1p,
                @JsonProperty("baseProperty") String bp) {
            derived1Property = d1p; baseProperty = bp;
        }
        @JsonProperty public String getBaseProperty() { return baseProperty; }
        @JsonProperty public String getDerived1Property() { return derived1Property; }
    }
static class BaseContainer {
        protected final Base base;
        protected final String baseContainerProperty;
        protected BaseContainer() { throw new IllegalStateException("wrong constructor called"); }
        @JsonCreator BaseContainer(@JsonProperty("baseContainerProperty") String p,
                @JsonProperty("base") Base b) { baseContainerProperty = p; base = b; }
        @JsonProperty public String getBaseContainerProperty() { return baseContainerProperty; }
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
        @JsonSubTypes(@JsonSubTypes.Type(value = Derived1.class, name = "d1"))
        @JsonProperty public Base getBase() { return base; }
    }
static class House831 {
        protected String petType;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "petType", visible = true)
        @JsonSubTypes(@JsonSubTypes.Type(name = "dog", value = Dog.class))
        public Animal pet;
        public String getPetType() { return petType; }
        public void setPetType(String type) { petType = type; }
    }
static class ExternalTypeWithNonPOJO {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, defaultImpl = String.class)
        @JsonSubTypes({ @JsonSubTypes.Type(value = AsValueThingy.class, name = "thingy") })
        public Object value;
        ExternalTypeWithNonPOJO() { }
        ExternalTypeWithNonPOJO(Object value) { this.value = value; }
    }
static class AsValueThingy {
        public long rawDate;
        AsValueThingy() { }
        AsValueThingy(long value) { rawDate = value; }
        @JsonValue public java.util.Date serialization() { return new java.util.Date(rawDate); }
    }
static class Envelope928 {
        Object payload;
        @JsonCreator Envelope928(@JsonProperty("payload")
                @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                        property = "class") Object payload) { this.payload = payload; }
    }
static class Payload928 { public String something; }
static class Box3008 {
        public String type;
        public Fruit3008 fruit;
        @JsonCreator Box3008(@JsonProperty("type") String type,
                @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                        property = "type") @JsonSubTypes(@JsonSubTypes.Type(value = Orange.class, name = "orange"))
                @JsonProperty("fruit") Fruit3008 fruit) { this.type = type; this.fruit = fruit; }
    }
interface Fruit3008 { }
static class Orange implements Fruit3008 { public String name; public String color; }
static class ExternalBeanWithDefault {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "extType", defaultImpl = ValueBean.class)
        public Object bean;
        ExternalBeanWithDefault() { }
        ExternalBeanWithDefault(int value) { bean = new ValueBean(value); }
    }
interface F1_291 { }
static class A291 implements F1_291 { public String a; }
static class B291 implements F1_291 { public String b; }
interface F2_291 { }
static class C291 implements F2_291 { public String c; }
static class D291 implements F2_291 { public String d; }
static class Container291 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXTERNAL_PROPERTY, visible = true)
        @JsonSubTypes({ @JsonSubTypes.Type(value = A291.class, name = "1"), @JsonSubTypes.Type(value = B291.class, name = "2") })
        public F1_291 field1;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXTERNAL_PROPERTY, visible = true)
        @JsonSubTypes({ @JsonSubTypes.Type(value = C291.class, name = "1"), @JsonSubTypes.Type(value = D291.class, name = "2") })
        public F2_291 field2;
    }
static class ContainerWithExtra291 extends Container291 { public String type; }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator INSTANCE = new NoCheckSubTypeValidator();
        @Override public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testSimpleSerializationVpack() throws Exception {
        try {
            testSimpleSerializationVpack();
        } finally {
        }
    }


    void __invoke_testImproperExternalIdSerializationVpack() throws Exception {
        try {
            testImproperExternalIdSerializationVpack();
        } finally {
        }
    }


    void __invoke_testSimpleDeserializationVpack() throws Exception {
        try {
            testSimpleDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testMultipleTypeIdsDeserializationVpack() throws Exception {
        try {
            testMultipleTypeIdsDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testIssue798Vpack() throws Exception {
        try {
            testIssue798Vpack();
        } finally {
        }
    }


    void __invoke_testIssue831Vpack() throws Exception {
        try {
            testIssue831Vpack();
        } finally {
        }
    }


    void __invoke_testInverseExternalId928Vpack() throws Exception {
        try {
            testInverseExternalId928Vpack();
        } finally {
        }
    }


    void __invoke_testWithAsValueVpack() throws Exception {
        try {
            testWithAsValueVpack();
        } finally {
        }
    }


    void __invoke_testWithDefaultAndMissing96Vpack() throws Exception {
        try {
            testWithDefaultAndMissing96Vpack();
        } finally {
        }
    }


    void __invoke_testWithNaturalScalar118Vpack() throws Exception {
        try {
            testWithNaturalScalar118Vpack();
        } finally {
        }
    }


    void __invoke_testIssue3008Vpack() throws Exception {
        try {
            testIssue3008Vpack();
        } finally {
        }
    }


    void __invoke_testMultipleValuesSingleExtId291Vpack() throws Exception {
        try {
            testMultipleValuesSingleExtId291Vpack();
        } finally {
        }
    }

}
