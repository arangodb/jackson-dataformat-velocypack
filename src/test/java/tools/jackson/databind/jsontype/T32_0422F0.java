package tools.jackson.databind.jsontype;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0422F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EXTERNAL_DEFAULT = VPackWireFixtureTest.hex(
            "14 20 46 61 6e 69 6d 61 6c 14 16 44 6e 61 6d 65 43 52 65 78 "
          + "45 62 72 65 65 64 43 4c 61 62 02 01");

    // Provenance: SealedTypesWithTypedSerializationTest#testTypeAsArray().
    void testTypeAsArrayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Animal422.class, TypeWithArray422.class).build();
        Map<?, ?> result = MAPPER.readValue(mapper.writeValueAsBytes(
                new AnimalWrapper422(new Dog422("Amadeus", 7))), Map.class);

        List<?> animal = assertInstanceOf(List.class, result.get("animal"));
        assertEquals(2, animal.size());
        assertEquals(Dog422.class.getName(), animal.get(0));
        Map<?, ?> dog = assertInstanceOf(Map.class, animal.get(1));
        assertEquals(2, dog.size());
        assertEquals("Amadeus", dog.get("name"));
        assertEquals(7, dog.get("boneCount"));
    }

    // Provenance: SealedTypesWithTypedSerializationTest#testTypeAsWrapper().
    void testTypeAsWrapperVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Animal422.class, TypeWithWrapper422.class).build();
        Map<?, ?> result = MAPPER.readValue(mapper.writeValueAsBytes(
                new AnimalWrapper422(new Cat422("Venla", "black"))), Map.class);

        Map<?, ?> animal = assertInstanceOf(Map.class, result.get("animal"));
        assertEquals(1, animal.size());
        Map<?, ?> cat = assertInstanceOf(Map.class,
                animal.get(".T32_0422F0$Cat422"));
        assertEquals(2, cat.size());
        assertEquals("Venla", cat.get("name"));
        assertEquals("black", cat.get("furColor"));
    }
private static Map<?, ?> writeAsMap(ObjectMapper mapper, Object value) throws Exception {
        return mapper.readValue(mapper.writeValueAsBytes(value), Map.class);
    }
private static Map<?, ?> writeAsMap(ObjectWriter writer, Object value) throws Exception {
        return MAPPER.readValue(writer.writeValueAsBytes(value), Map.class);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    static abstract class Animal422 {
        public String name;
        Animal422() { }
        Animal422(String name) { this.name = name; }
    }
static final class Dog422 extends Animal422 {
        public int boneCount;
        Dog422() { }
        Dog422(String name, int boneCount) { super(name); this.boneCount = boneCount; }
    }
static final class Cat422 extends Animal422 {
        public String furColor;
        Cat422() { }
        Cat422(String name, String furColor) { super(name); this.furColor = furColor; }
    }
static class AnimalWrapper422 {
        public Animal422 animal;
        AnimalWrapper422(Animal422 animal) { this.animal = animal; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    interface TypeWithWrapper422 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    interface TypeWithArray422 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@type", defaultImpl = DefaultDog422.class,
            writeTypeIdForDefaultImpl = OptBoolean.FALSE)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultDog422.class, name = "dog"),
        @JsonSubTypes.Type(value = CatProperty422.class, name = "cat")
    })
    static class AnimalProperty422 {
        public String name;
    }
static class DefaultDog422 extends AnimalProperty422 {
        public String breed;
    }
static class CatProperty422 extends AnimalProperty422 {
        public int lives;
    }
static class Animal4_422 {
        public String name;
    }
static class DefaultDog4_422 extends Animal4_422 {
        public String breed;
    }
static class Cat4_422 extends Animal4_422 {
        public int lives;
    }
static class AnimalWrapper4_422 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "@type", defaultImpl = DefaultDog4_422.class,
                writeTypeIdForDefaultImpl = OptBoolean.FALSE)
        @JsonSubTypes({
            @JsonSubTypes.Type(value = DefaultDog4_422.class, name = "dog"),
            @JsonSubTypes.Type(value = Cat4_422.class, name = "cat")
        })
        public Animal4_422 animal;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type", defaultImpl = DefaultDog5_422.class,
            writeTypeIdForDefaultImpl = OptBoolean.FALSE)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultDog5_422.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat5_422.class, name = "cat")
    })
    static class Animal5_422 {
        public String name;
        public String type;
    }
static class DefaultDog5_422 extends Animal5_422 {
        public String breed;
    }
static class Cat5_422 extends Animal5_422 {
        public int lives;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@type", defaultImpl = AnimalBase422.class,
            writeTypeIdForDefaultImpl = OptBoolean.FALSE)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = AnimalBase422.class, name = "base"),
        @JsonSubTypes.Type(value = DogBase422.class, name = "dog")
    })
    static class AnimalBase422 {
        public String name;
    }
static class DogBase422 extends AnimalBase422 {
        public String breed;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@type", defaultImpl = DefaultDogOff422.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultDogOff422.class, name = "dog"),
        @JsonSubTypes.Type(value = CatOff422.class, name = "cat")
    })
    static class AnimalOff422 {
        public String name;
    }
static class DefaultDogOff422 extends AnimalOff422 {
        public String breed;
    }
static class CatOff422 extends AnimalOff422 {
        public int lives;
    }

    void __invoke_testTypeAsArrayVpack() throws Exception {
        try {
            testTypeAsArrayVpack();
        } finally {
        }
    }


    void __invoke_testTypeAsWrapperVpack() throws Exception {
        try {
            testTypeAsWrapperVpack();
        } finally {
        }
    }

}
