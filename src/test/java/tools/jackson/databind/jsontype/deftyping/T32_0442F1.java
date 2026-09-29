package tools.jackson.databind.jsontype.deftyping;

import java.io.Serializable;

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
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0442F1 {
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

    // Provenance: TestDefaultWithCreators#testWithCreators().
    void testWithCreatorsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE, DefaultTyping.NON_FINAL)
                .build();
        UrlJob input = new UrlJob(123L, "http://foo", 3);
        Job output = mapper.readValue(mapper.writeValueAsBytes(input), Job.class);
        assertSame(UrlJob.class, output.getClass());
        UrlJob result = (UrlJob) output;
        assertEquals(123L, result.id);
        assertEquals("http://foo", result.getUrl());
        assertEquals(3, result.getCount());
    }

    // Provenance: TestDefaultWithCreators#testWithCreatorAndJsonValue().
    void testWithCreatorAndJsonValueVpack() throws Exception {
        byte[] bytes = new byte[] { 1, 2, 3, 4, 5 };
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE)
                .build();
        Bean1385Wrapper result = mapper.readValue(mapper.writeValueAsBytes(
                new Bean1385Wrapper(new Bean1385(bytes))), Bean1385Wrapper.class);
        assertNotNull(result.value);
        assertEquals(Bean1385.class, result.value.getClass());
        assertArrayEquals(bytes, ((Bean1385) result.value).raw);
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

    void __invoke_testWithCreatorsVpack() throws Exception {
        try {
            testWithCreatorsVpack();
        } finally {
        }
    }


    void __invoke_testWithCreatorAndJsonValueVpack() throws Exception {
        try {
            testWithCreatorAndJsonValueVpack();
        } finally {
        }
    }

}
