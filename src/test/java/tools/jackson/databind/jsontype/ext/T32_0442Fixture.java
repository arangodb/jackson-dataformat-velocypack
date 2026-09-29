package tools.jackson.databind.jsontype.ext;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0442Fixture {
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

    // Provenance: ExternalPropertyWithArrayShape4277Test#testArrayShapeWithPropertyInclusion().
    void testArrayShapeWithPropertyInclusionVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().registerSubtypes(Cat.class, Dog.class).build();
        WrapperWithPropertyInclusion input = new WrapperWithPropertyInclusion("id123",
                new Cat("Fluffy"));
        WrapperWithPropertyInclusion result = mapper.readValue(
                mapper.writeValueAsBytes(input), WrapperWithPropertyInclusion.class);
        assertEquals("id123", result.uniqueId);
        assertInstanceOf(Cat.class, result.animal);
        assertEquals("Fluffy", result.animal.name);
    }

    // Provenance: ExternalPropertyWithArrayShape4277Test#testArrayShapeWithWrapperArrayInclusion().
    void testArrayShapeWithWrapperArrayInclusionVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().registerSubtypes(Cat.class, Dog.class).build();
        WrapperWithWrapperArrayInclusion input = new WrapperWithWrapperArrayInclusion("id123",
                new Cat("Fluffy"));
        WrapperWithWrapperArrayInclusion result = mapper.readValue(
                mapper.writeValueAsBytes(input), WrapperWithWrapperArrayInclusion.class);
        assertEquals("id123", result.uniqueId);
        assertInstanceOf(Cat.class, result.animal);
        assertEquals("Fluffy", result.animal.name);
    }

    // Provenance: ExternalPropertyWithArrayShape4277Test#testErrorMessageContainsAlternatives().
    void testErrorMessageContainsAlternativesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().registerSubtypes(Cat.class, Dog.class).build();
        InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(EXTERNAL_ARRAY, WrapperWithExternalProperty.class));
        String message = error.getMessage();
        assertTrue(message.contains("ARRAY") || message.contains("array"), message);
        assertTrue(message.contains("EXTERNAL_PROPERTY") || message.contains("external"), message);
        assertTrue(message.contains("PROPERTY") || message.contains("WRAPPER_ARRAY")
                || message.contains("alternative") || message.contains("custom deserializer"), message);
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

    void __invoke_testArrayShapeWithPropertyInclusionVpack() throws Exception {
        try {
            testArrayShapeWithPropertyInclusionVpack();
        } finally {
        }
    }


    void __invoke_testArrayShapeWithWrapperArrayInclusionVpack() throws Exception {
        try {
            testArrayShapeWithWrapperArrayInclusionVpack();
        } finally {
        }
    }


    void __invoke_testErrorMessageContainsAlternativesVpack() throws Exception {
        try {
            testErrorMessageContainsAlternativesVpack();
        } finally {
        }
    }

}
