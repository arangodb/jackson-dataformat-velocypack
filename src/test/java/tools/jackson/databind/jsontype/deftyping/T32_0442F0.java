package tools.jackson.databind.jsontype.deftyping;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0442F0 {
private static final byte[] PLAIN_SCALARS = VPackWireFixtureTest.hex(
            "06 0e 04 43 61 62 63 1a 18 19 03 07 08 09");
private static final byte[] NUMERIC_SCALARS = VPackWireFixtureTest.hex(
            "06 1d 04 28 7b 28 25 1b 00 00 00 00 00 00 d0 3f "
          + "1b 00 00 00 00 00 00 e0 3f 03 05 07 10");
private static final byte[] DATE_SCALAR = VPackWireFixtureTest.hex(
            "1c 4e 61 bc 00 00 00 00 00");
private static final byte[] EXTERNAL_ARRAY = VPackWireFixtureTest.hex(
            "06 1f 03 43 63 61 74 45 69 64 31 32 33 "
          + "14 0f 44 6e 61 6d 65 46 46 6c 75 66 66 79 01 03 07 0d");
private static final ObjectMapper DEFAULT_TYPING_MAPPER = VPackMapper.builder()
            .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE)
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    // Provenance: TestDefaultForScalars#testNumericScalars().
    void testNumericScalarsVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        Object[] literal = plain.readValue(NUMERIC_SCALARS, Object[].class);
        assertEquals(123, literal[0]);
        assertEquals(37, literal[1]);
        assertEquals(0.25d, ((Number) literal[2]).doubleValue());
        assertEquals(0.5d, ((Number) literal[3]).doubleValue());

        Object[] input = new Object[] {
                Integer.valueOf(123), Long.valueOf(37),
                Double.valueOf(0.25), Float.valueOf(0.5f)
        };
        Object[] output = DEFAULT_TYPING_MAPPER.readValue(
                DEFAULT_TYPING_MAPPER.writeValueAsBytes(input), Object[].class);
        assertEquals(Integer.class, output[0].getClass());
        assertEquals(Long.class, output[1].getClass());
        assertEquals(Double.class, output[2].getClass());
        assertEquals(Float.class, output[3].getClass());
        assertEquals(input[0], output[0]);
        assertEquals(input[1], output[1]);
        assertEquals(input[2], output[2]);
        assertEquals(input[3], output[3]);
    }

    // Provenance: TestDefaultForScalars#testDateScalars().
    void testDateScalarsVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        Date literal = plain.readValue(DATE_SCALAR, Date.class);
        assertEquals(12345678L, literal.getTime());

        long ts = 12345678L;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ROOT);
        calendar.setTimeInMillis(ts);
        Object[] input = new Object[] { new Date(ts), calendar };
        Object[] output = DEFAULT_TYPING_MAPPER.readValue(
                DEFAULT_TYPING_MAPPER.writeValueAsBytes(input), Object[].class);
        assertEquals(2, output.length);
        assertInstanceOf(Date.class, output[0]);
        assertEquals(ts, ((Date) output[0]).getTime());
        assertInstanceOf(Calendar.class, output[1]);
        assertEquals(ts, ((Calendar) output[1]).getTimeInMillis());
    }

    // Provenance: TestDefaultForScalars#testMiscScalars().
    void testMiscScalarsVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        Object[] literal = plain.readValue(PLAIN_SCALARS, Object[].class);
        assertArrayEquals(new Object[] { "abc", Boolean.TRUE, null, Boolean.FALSE }, literal);

        Object[] output = DEFAULT_TYPING_MAPPER.readValue(
                DEFAULT_TYPING_MAPPER.writeValueAsBytes(
                        new Object[] { "abc", Boolean.TRUE, null, Boolean.FALSE }),
                Object[].class);
        assertArrayEquals(new Object[] { "abc", Boolean.TRUE, null, Boolean.FALSE }, output);
    }

    // Provenance: TestDefaultForScalars#testScalarArrays().
    void testScalarArraysVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.JAVA_LANG_OBJECT)
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        Object[] input = new Object[] { "abc", new Date(1234567L), null, Integer.valueOf(456) };
        Object[] output = mapper.readValue(mapper.writeValueAsBytes(input), Object[].class);
        assertEquals(4, output.length);
        assertEquals("abc", output[0]);
        assertInstanceOf(Date.class, output[1]);
        assertEquals(1234567L, ((Date) output[1]).getTime());
        assertEquals(null, output[2]);
        assertEquals(Integer.valueOf(456), output[3]);
    }

    // Provenance: TestDefaultForScalars#test417().
    void test417Vpack() throws Exception {
        Jackson417Bean input = new Jackson417Bean();
        Jackson417Bean result = DEFAULT_TYPING_MAPPER.readValue(
                DEFAULT_TYPING_MAPPER.writeValueAsBytes(input), Jackson417Bean.class);
        assertEquals(input.foo, result.foo);
        assertEquals(input.bar, result.bar);
        assertEquals(Integer.class, result.bar.getClass());
    }

    // Provenance: TestDefaultForScalars#testDefaultTypingWithLong().
    void testDefaultTypingWithLongVpack() throws Exception {
        Data data = new Data();
        data.key = 1L;
        Map<String, Object> input = new HashMap<>();
        input.put("longInMap", 2L);
        input.put("longAsField", data);

        StdTypeResolverBuilder resolver = new StdTypeResolverBuilder(
                JsonTypeInfo.Id.CLASS, JsonTypeInfo.As.PROPERTY, "__t", null);
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .polymorphicTypeValidator(NoCheckSubTypeValidator.INSTANCE)
                .setDefaultTyping(resolver)
                .build();
        Map<?, ?> result = mapper.readValue(mapper.writeValueAsBytes(input), Map.class);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // Provenance: TestDefaultForScalars#testDefaultTypingWithNaN().
    void testDefaultTypingWithNaNVpack() throws Exception {
        ObjectWrapperForPoly input = new ObjectWrapperForPoly(Double.POSITIVE_INFINITY);
        ObjectWrapperForPoly result = DEFAULT_TYPING_MAPPER.readValue(
                DEFAULT_TYPING_MAPPER.writeValueAsBytes(input), ObjectWrapperForPoly.class);
        assertEquals(Double.class, result.getObject().getClass());
        assertEquals(input.getObject().toString(), result.getObject().toString());
        assertTrue(((Double) result.getObject()).isInfinite());
    }
static class Jackson417Bean {
        public String foo = "bar";
        public Serializable bar = Integer.valueOf(13);
    }
static class Data {
        public long key;
    }
static class ObjectWrapperForPoly {
        Object object;
        protected ObjectWrapperForPoly() { }
        public ObjectWrapperForPoly(Object object) { this.object = object; }
        public Object getObject() { return object; }
    }
static abstract class Job {
        public long id;
    }
static class UrlJob extends Job {
        private final String url;
        private final int count;

        @JsonCreator
        UrlJob(@JsonProperty("id") long id, @JsonProperty("url") String url,
                @JsonProperty("count") int count) {
            this.id = id;
            this.url = url;
            this.count = count;
        }
        public String getUrl() { return url; }
        public int getCount() { return count; }
    }
static class Bean1385Wrapper {
        public Object value;
        Bean1385Wrapper() { }
        Bean1385Wrapper(Object value) { this.value = value; }
    }
static class Bean1385 {
        byte[] raw;
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        Bean1385(byte[] raw) { this.raw = raw.clone(); }
        @JsonValue
        public byte[] getBytes() { return raw; }
    }
static class Animal {
        public String name;
        Animal() { }
        Animal(String name) { this.name = name; }
    }
@JsonTypeName("cat")
    static class Cat extends Animal {
        Cat() { }
        Cat(String name) { super(name); }
    }
@JsonTypeName("dog")
    static class Dog extends Animal {
        Dog() { }
        Dog(String name) { super(name); }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"type", "uniqueId", "animal"})
    static class WrapperWithExternalProperty {
        public String type;
        public String uniqueId;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes({
            @JsonSubTypes.Type(value = Cat.class, name = "cat"),
            @JsonSubTypes.Type(value = Dog.class, name = "dog")
        })
        public Animal animal;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"uniqueId", "animal"})
    static class WrapperWithPropertyInclusion {
        public String uniqueId;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
                property = "type")
        @JsonSubTypes({
            @JsonSubTypes.Type(value = Cat.class, name = "cat"),
            @JsonSubTypes.Type(value = Dog.class, name = "dog")
        })
        public Animal animal;
        WrapperWithPropertyInclusion() { }
        WrapperWithPropertyInclusion(String id, Animal animal) {
            this.uniqueId = id;
            this.animal = animal;
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"uniqueId", "animal"})
    static class WrapperWithWrapperArrayInclusion {
        public String uniqueId;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        @JsonSubTypes({
            @JsonSubTypes.Type(value = Cat.class, name = "cat"),
            @JsonSubTypes.Type(value = Dog.class, name = "dog")
        })
        public Animal animal;
        WrapperWithWrapperArrayInclusion() { }
        WrapperWithWrapperArrayInclusion(String id, Animal animal) {
            this.uniqueId = id;
            this.animal = animal;
        }
    }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator INSTANCE = new NoCheckSubTypeValidator();
        @Override
        public Validity validateBaseType(tools.jackson.databind.DatabindContext ctxt,
                tools.jackson.databind.JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testNumericScalarsVpack() throws Exception {
        try {
            testNumericScalarsVpack();
        } finally {
        }
    }


    void __invoke_testDateScalarsVpack() throws Exception {
        try {
            testDateScalarsVpack();
        } finally {
        }
    }


    void __invoke_testMiscScalarsVpack() throws Exception {
        try {
            testMiscScalarsVpack();
        } finally {
        }
    }


    void __invoke_testScalarArraysVpack() throws Exception {
        try {
            testScalarArraysVpack();
        } finally {
        }
    }


    void __invoke_test417Vpack() throws Exception {
        try {
            test417Vpack();
        } finally {
        }
    }


    void __invoke_testDefaultTypingWithLongVpack() throws Exception {
        try {
            testDefaultTypingWithLongVpack();
        } finally {
        }
    }


    void __invoke_testDefaultTypingWithNaNVpack() throws Exception {
        try {
            testDefaultTypingWithNaNVpack();
        } finally {
        }
    }

}
