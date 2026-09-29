package tools.jackson.databind.jsontype.ext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonTypeIdResolver;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0444F1 {
private static final byte[] PET_TYPE_FIRST = VPackWireFixtureTest.hex(
            "14 39 44 74 79 70 65 43 63 61 74 "
          + "46 61 6e 69 6d 61 6c 0a "
          + "4d 69 67 6e 6f 72 65 64 4f 62 6a 65 63 74 "
          + "14 17 49 73 6f 6d 65 46 69 65 6c 64 "
          + "49 73 6f 6d 65 56 61 6c 75 65 01 03");
private static final byte[] PET_TYPE_LAST = VPackWireFixtureTest.hex(
            "14 39 46 61 6e 69 6d 61 6c 0a "
          + "4d 69 67 6e 6f 72 65 64 4f 62 6a 65 63 74 "
          + "14 17 49 73 6f 6d 65 46 69 65 6c 64 "
          + "49 73 6f 6d 65 56 61 6c 75 65 01 "
          + "44 74 79 70 65 43 63 61 74 03");
private static final byte[] BIG_DECIMAL_STRING = VPackWireFixtureTest.hex(
            "14 38 4b 6f 62 6a 65 63 74 56 61 6c 75 65 "
          + "57 2d 31 30 30 30 30 30 30 30 30 30 30 2e 30 30 30 30 30 30 30 30 30 31 "
          + "44 74 79 70 65 4b 42 49 47 5f 44 45 43 49 4d 41 4c 02");
private static final byte[] IGNORE_UNKNOWN = VPackWireFixtureTest.hex(
            "13 22 14 1f 44 74 79 70 65 44 74 65 73 74 "
          + "44 64 61 74 61 0a 4a 61 64 64 69 74 69 6f 6e 61 6c 0a 03 01");
private static final byte[] JSON_IGNORE = VPackWireFixtureTest.hex(
            "14 22 45 63 68 69 6c 64 "
          + "14 19 49 63 68 69 6c 64 54 79 70 65 41 41 "
          + "48 73 75 62 43 68 69 6c 64 0a 02 01");
private static final byte[] NULL_BEAN_FIRST = VPackWireFixtureTest.hex(
            "14 17 44 62 65 61 6e 18 47 65 78 74 54 79 70 65 45 76 62 65 61 6e 02");
private static final byte[] NULL_TYPE_FIRST = VPackWireFixtureTest.hex(
            "14 17 47 65 78 74 54 79 70 65 45 76 62 65 61 6e 44 62 65 61 6e 18 02");
private static final byte[] FUNKY_TYPE_FIRST = VPackWireFixtureTest.hex(
            "14 13 47 65 78 74 54 79 70 65 44 66 75 6e 6b 41 69 33 02");
private static final byte[] FUNKY_VALUE_FIRST = VPackWireFixtureTest.hex(
            "14 13 41 69 33 47 65 78 74 54 79 70 65 44 66 75 6e 6b 02");
private final ObjectMapper mapper = new VPackMapper();

    // Provenance: ExternalTypeIdTest#testBigDecimal965().
    void testBigDecimal965Vpack() throws Exception {
        Wrapper965 input = new Wrapper965();
        input.typeEnum = Type965.BIG_DECIMAL;
        input.value = new BigDecimal("-10000000000.0000000001");

        Wrapper965 output = mapper.readerFor(Wrapper965.class)
                .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .readValue(mapper.writeValueAsBytes(input));
        assertEquals(input.typeEnum, output.typeEnum);
        assertEquals(input.value, output.value);
        assertEquals(((BigDecimal) input.value).scale(), ((BigDecimal) output.value).scale());
    }

    // Provenance: ExternalTypeIdTest#testBigDecimal965StringBased().
    void testBigDecimal965StringBasedVpack() throws Exception {
        Wrapper965 output = mapper.readerFor(Wrapper965.class)
                .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .readValue(BIG_DECIMAL_STRING);
        assertEquals(Type965.BIG_DECIMAL, output.typeEnum);
        assertEquals(new BigDecimal("-10000000000.0000000001"), output.value);
    }

    // Provenance: ExternalTypeIdTest#testDecimalMetadata().
    void testDecimalMetadataVpack() throws Exception {
        Map<?, ?> wire = mapper.readValue(mapper.writeValueAsBytes(new DecimalMetadata()), Map.class);
        Map<?, ?> entry = (Map<?, ?>) ((List<?>) wire.get("metadata")).get(0);
        assertEquals("num", entry.get("key"));
        assertEquals(new BigDecimal("111.1"), entry.get("value"));
        assertEquals("decimalValue", entry.get("@type"));
    }

    // Provenance: ExternalTypeIdTest#testDoubleMetadata().
    void testDoubleMetadataVpack() throws Exception {
        Map<?, ?> wire = mapper.readValue(mapper.writeValueAsBytes(new DoubleMetadata()), Map.class);
        Map<?, ?> entry = (Map<?, ?>) ((List<?>) wire.get("metadata")).get(0);
        assertEquals("num", entry.get("key"));
        assertEquals(1234.25, ((Number) entry.get("value")).doubleValue());
        assertEquals("doubleValue", entry.get("@type"));
    }

    // Provenance: ExternalTypeIdTest#testExternalPropertyWithIgnoreUnknown2611().
    void testExternalPropertyWithIgnoreUnknown2611Vpack() throws Exception {
        List<Wrapper2611> result = mapper.readValue(IGNORE_UNKNOWN,
                new TypeReference<List<Wrapper2611>>() { });
        assertEquals(1, result.size());
        assertEquals("test", result.get(0).getType());
        assertNotNull(result.get(0).getData());
    }

    // Provenance: ExternalTypeIdTest#testExternalPropertyWithJsonIgnore4185().
    void testExternalPropertyWithJsonIgnore4185Vpack() throws Exception {
        Parent4185 parent = mapper.readValue(JSON_IGNORE, Parent4185.class);
        assertInstanceOf(SubChildA4185.class, parent.child.subChild);
    }

    // Provenance: ExternalTypeIdTest#testExternalTypeIdWithNull().
    void testExternalTypeIdWithNullVpack() throws Exception {
        ObjectMapper typed = VPackMapper.builder().registerSubtypes(ValueBean.class).build();
        ExternalBean first = typed.readValue(NULL_BEAN_FIRST, ExternalBean.class);
        ExternalBean second = typed.readValue(NULL_TYPE_FIRST, ExternalBean.class);
        assertNotNull(first);
        assertNotNull(second);
        assertEquals(null, first.bean);
        assertEquals(null, second.bean);
    }

    // Provenance: ExternalTypeIdTest#testExternalTypeWithCreator().
    void testExternalTypeWithCreatorVpack() throws Exception {
        ObjectMapper typed = VPackMapper.builder().registerSubtypes(ValueBean.class).build();
        ExternalBeanWithCreator output = typed.readValue(
                typed.writeValueAsBytes(new ExternalBeanWithCreator(7)),
                ExternalBeanWithCreator.class);
        assertNotNull(output);
        assertEquals(7, output.foo);
        assertInstanceOf(ValueBean.class, output.value);
        assertEquals(7, ((ValueBean) output.value).value);
    }

    // Provenance: ExternalTypeIdTest#testExternalTypeWithProp222().
    void testExternalTypeWithProp222Vpack() throws Exception {
        ObjectMapper sorted = VPackMapper.builder()
                .enable(tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .build();
        Map<?, ?> wire = sorted.readValue(sorted.writeValueAsBytes(new Issue222Bean(13)), Map.class);
        assertEquals(List.of("type", "value"), List.copyOf(wire.keySet()));
        assertEquals("foo", wire.get("type"));
        assertEquals(13, ((Number) ((Map<?, ?>) wire.get("value")).get("x")).intValue());
    }

    // Provenance: ExternalTypeIdTest#testImproperExternalIdDeserialization().
    void testImproperExternalIdDeserializationVpack() throws Exception {
        FunkyExternalBean result = mapper.readValue(FUNKY_TYPE_FIRST, FunkyExternalBean.class);
        assertNotNull(result);
        assertEquals(3, result.i);
        result = mapper.readValue(FUNKY_VALUE_FIRST, FunkyExternalBean.class);
        assertNotNull(result);
        assertEquals(3, result.i);
    }
interface Animal { }
static class Cat implements Animal { public int lives = 9; }
static class Dog implements Animal { }
static class Wolf implements Animal { public boolean alive; }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class Pet {
        final String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonTypeIdResolver(AnimalTypeIdResolver.class)
        private final Animal animal;
        @JsonCreator
        Pet(@JsonProperty("type") String type, @JsonProperty("animal") Animal animal) {
            this.type = type;
            this.animal = animal;
        }
    }
static class AnimalTypeIdResolver extends TypeIdResolverBase {
        @Override public String idFromValue(DatabindContext context, Object value) {
            return idFromValueAndType(context, value, value.getClass());
        }
        @Override public String idFromValueAndType(DatabindContext context, Object value,
                Class<?> suggestedType) {
            if (suggestedType.isAssignableFrom(Cat.class)) return "cat";
            if (suggestedType.isAssignableFrom(Dog.class)) return "dog";
            if (suggestedType.isAssignableFrom(Wolf.class)) return "wolf";
            return null;
        }
        @Override public JavaType typeFromId(DatabindContext context, String id) {
            if ("cat".equals(id)) return context.constructType(Cat.class);
            if ("dog".equals(id)) return context.constructType(Dog.class);
            return null;
        }
        @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.NAME; }
    }
static class ExternalBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "extType")
        public Object bean;
    }
@JsonTypeName("vbean")
    static class ValueBean { public int value; ValueBean() { } ValueBean(int v) { value = v; } }
static class ExternalBeanWithCreator {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "extType")
        public Object value;
        public int foo;
        @JsonCreator ExternalBeanWithCreator(@JsonProperty("foo") int foo) {
            this.foo = foo;
            value = new ValueBean(foo);
        }
    }
enum Type965 { BIG_DECIMAL }
static class Wrapper965 {
        protected Type965 typeEnum;
        protected Object value;
        @JsonGetter("type") String getTypeString() { return typeEnum.name(); }
        @JsonSetter("type") void setTypeString(String type) { typeEnum = Type965.valueOf(type); }
        @JsonGetter("objectValue") Object getValue() { return value; }
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type", visible = true)
        @JsonSubTypes(@JsonSubTypes.Type(name = "BIG_DECIMAL", value = BigDecimal.class))
        @JsonSetter("objectValue") private void setValue(Object value) { this.value = value; }
    }
@JsonTypeName("decimalValue")
    static class DecimalValue {
        private BigDecimal value = new BigDecimal("111.1");
        @JsonValue public BigDecimal getValue() { return value; }
    }
@JsonPropertyOrder({"key", "value"})
    static class DecimalEntry {
        public String getKey() { return "num"; }
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
        public DecimalValue getValue() { return new DecimalValue(); }
    }
static class DecimalMetadata {
        @JsonProperty("metadata") public List<DecimalEntry> getMetadata() {
            return List.of(new DecimalEntry());
        }
    }
@JsonTypeName("doubleValue")
    static class DoubleValue {
        private Double value = 1234.25;
        @JsonValue public Double getValue() { return value; }
    }
@JsonPropertyOrder({"key", "value"})
    static class DoubleEntry {
        public String getKey() { return "num"; }
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
        public DoubleValue getValue() { return new DoubleValue(); }
    }
static class DoubleMetadata {
        @JsonProperty("metadata") public List<DoubleEntry> getMetadata() {
            return List.of(new DoubleEntry());
        }
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class Wrapper2611 {
        private String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type", defaultImpl = Default2611.class)
        private Default2611 data;
        @JsonCreator Wrapper2611(@JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "data", required = true) Default2611 data) {
            this.type = type;
            this.data = data;
        }
        String getType() { return type; }
        Default2611 getData() { return data; }
    }
static class Default2611 { }
static class Parent4185 { @JsonIgnoreProperties("parent") public Child4185 child; }
static class Child4185 {
        public Parent4185 parent;
        public String childType;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "childType")
        @JsonSubTypes({ @JsonSubTypes.Type(name = "A", value = SubChildA4185.class),
                @JsonSubTypes.Type(name = "B", value = SubChildB4185.class) })
        public SubChild4185 subChild;
    }
interface SubChild4185 { }
static class SubChildA4185 implements SubChild4185 { }
static class SubChildB4185 implements SubChild4185 { }
static class Issue222Bean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
        public Issue222BeanB value;
        public String type = "foo";
        Issue222Bean() { }
        Issue222Bean(int v) { value = new Issue222BeanB(v); }
    }
@JsonTypeName("222b")
    static class Issue222BeanB { public int x; Issue222BeanB() { } Issue222BeanB(int x) { this.x = x; } }
@JsonTypeName("funk")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "extType")
    static class FunkyExternalBean { public int i = 3; }

    void __invoke_testBigDecimal965Vpack() throws Exception {
        try {
            testBigDecimal965Vpack();
        } finally {
        }
    }


    void __invoke_testBigDecimal965StringBasedVpack() throws Exception {
        try {
            testBigDecimal965StringBasedVpack();
        } finally {
        }
    }


    void __invoke_testDecimalMetadataVpack() throws Exception {
        try {
            testDecimalMetadataVpack();
        } finally {
        }
    }


    void __invoke_testDoubleMetadataVpack() throws Exception {
        try {
            testDoubleMetadataVpack();
        } finally {
        }
    }


    void __invoke_testExternalPropertyWithIgnoreUnknown2611Vpack() throws Exception {
        try {
            testExternalPropertyWithIgnoreUnknown2611Vpack();
        } finally {
        }
    }


    void __invoke_testExternalPropertyWithJsonIgnore4185Vpack() throws Exception {
        try {
            testExternalPropertyWithJsonIgnore4185Vpack();
        } finally {
        }
    }


    void __invoke_testExternalTypeIdWithNullVpack() throws Exception {
        try {
            testExternalTypeIdWithNullVpack();
        } finally {
        }
    }


    void __invoke_testExternalTypeWithCreatorVpack() throws Exception {
        try {
            testExternalTypeWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testExternalTypeWithProp222Vpack() throws Exception {
        try {
            testExternalTypeWithProp222Vpack();
        } finally {
        }
    }


    void __invoke_testImproperExternalIdDeserializationVpack() throws Exception {
        try {
            testImproperExternalIdDeserializationVpack();
        } finally {
        }
    }

}
