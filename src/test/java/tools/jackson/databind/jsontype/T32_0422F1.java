package tools.jackson.databind.jsontype;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0422F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EXTERNAL_DEFAULT = VPackWireFixtureTest.hex(
            "14 20 46 61 6e 69 6d 61 6c 14 16 44 6e 61 6d 65 43 52 65 78 "
          + "45 62 72 65 65 64 43 4c 61 62 02 01");

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testBaseTypeAsDefaultImplSkipped().
    void testBaseTypeAsDefaultImplSkippedVpack() throws Exception {
        AnimalBase422 base = new AnimalBase422();
        base.name = "Generic";
        Map<?, ?> encoded = writeAsMap(MAPPER.writerFor(AnimalBase422.class), base);
        assertFalse(encoded.containsKey("@type"));
        assertEquals("Generic", encoded.get("name"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testBaseTypeAsDefaultImplSubclassHasTypeId().
    void testBaseTypeAsDefaultImplSubclassHasTypeIdVpack() throws Exception {
        DogBase422 dog = new DogBase422();
        dog.name = "Rex";
        dog.breed = "Lab";
        Map<?, ?> encoded = writeAsMap(MAPPER.writerFor(AnimalBase422.class), dog);
        assertEquals("dog", encoded.get("@type"));
        assertEquals("Rex", encoded.get("name"));
        assertEquals("Lab", encoded.get("breed"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testExistingPropertyDefaultImplTypeFieldStillWritten().
    void testExistingPropertyDefaultImplTypeFieldStillWrittenVpack() throws Exception {
        DefaultDog5_422 dog = new DefaultDog5_422();
        dog.name = "Rex";
        dog.breed = "Lab";
        dog.type = "dog";
        Map<?, ?> encoded = writeAsMap(MAPPER.writerFor(Animal5_422.class), dog);
        assertEquals("dog", encoded.get("type"));
        assertEquals("Rex", encoded.get("name"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testExistingPropertyNonDefaultHasTypeId().
    void testExistingPropertyNonDefaultHasTypeIdVpack() throws Exception {
        Cat5_422 cat = new Cat5_422();
        cat.name = "Whiskers";
        cat.lives = 9;
        cat.type = "cat";
        Map<?, ?> encoded = writeAsMap(MAPPER.writerFor(Animal5_422.class), cat);
        assertEquals("cat", encoded.get("type"));
        assertEquals(9, encoded.get("lives"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testExternalPropertyDefaultImplRoundTrip().
    void testExternalPropertyDefaultImplRoundTripVpack() throws Exception {
        AnimalWrapper4_422 wrapper = new AnimalWrapper4_422();
        DefaultDog4_422 dog = new DefaultDog4_422();
        dog.name = "Rex";
        dog.breed = "Lab";
        wrapper.animal = dog;
        Map<?, ?> encoded = writeAsMap(MAPPER, wrapper);
        assertFalse(encoded.containsKey("@type"));

        Map<?, ?> fixture = MAPPER.readValue(EXTERNAL_DEFAULT, Map.class);
        Map<?, ?> fixtureAnimal = assertInstanceOf(Map.class, fixture.get("animal"));
        assertEquals("Rex", fixtureAnimal.get("name"));
        assertEquals("Lab", fixtureAnimal.get("breed"));

        AnimalWrapper4_422 result = MAPPER.readValue(MAPPER.writeValueAsBytes(wrapper),
                AnimalWrapper4_422.class);
        assertInstanceOf(DefaultDog4_422.class, result.animal);
        assertEquals("Rex", result.animal.name);
        assertEquals("Lab", ((DefaultDog4_422) result.animal).breed);
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testExternalPropertyDefaultImplSkipped().
    void testExternalPropertyDefaultImplSkippedVpack() throws Exception {
        AnimalWrapper4_422 wrapper = new AnimalWrapper4_422();
        DefaultDog4_422 dog = new DefaultDog4_422();
        dog.name = "Rex";
        dog.breed = "Lab";
        wrapper.animal = dog;
        Map<?, ?> encoded = writeAsMap(MAPPER, wrapper);
        assertFalse(encoded.containsKey("@type"));
        Map<?, ?> animal = assertInstanceOf(Map.class, encoded.get("animal"));
        assertEquals("Rex", animal.get("name"));
        assertEquals("Lab", animal.get("breed"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testExternalPropertyNonDefaultHasTypeId().
    void testExternalPropertyNonDefaultHasTypeIdVpack() throws Exception {
        AnimalWrapper4_422 wrapper = new AnimalWrapper4_422();
        Cat4_422 cat = new Cat4_422();
        cat.name = "Whiskers";
        cat.lives = 9;
        wrapper.animal = cat;
        Map<?, ?> encoded = writeAsMap(MAPPER, wrapper);
        assertEquals("cat", encoded.get("@type"));
        Map<?, ?> animal = assertInstanceOf(Map.class, encoded.get("animal"));
        assertEquals("Whiskers", animal.get("name"));
        assertEquals(9, animal.get("lives"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testFeatureOffAlwaysWritesTypeId().
    void testFeatureOffAlwaysWritesTypeIdVpack() throws Exception {
        DefaultDogOff422 dog = new DefaultDogOff422();
        dog.name = "Rex";
        dog.breed = "Lab";
        Map<?, ?> encoded = writeAsMap(MAPPER.writerFor(AnimalOff422.class), dog);
        assertEquals("dog", encoded.get("@type"));
        assertEquals("Rex", encoded.get("name"));
        assertEquals("Lab", encoded.get("breed"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testPropertyDefaultImplSkipped().
    void testPropertyDefaultImplSkippedVpack() throws Exception {
        DefaultDog422 dog = new DefaultDog422();
        dog.name = "Rex";
        dog.breed = "Lab";
        Map<?, ?> encoded = writeAsMap(MAPPER, dog);
        assertFalse(encoded.containsKey("@type"));
        assertEquals("Rex", encoded.get("name"));
        assertEquals("Lab", encoded.get("breed"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testPropertyNonDefaultHasTypeId().
    void testPropertyNonDefaultHasTypeIdVpack() throws Exception {
        CatProperty422 cat = new CatProperty422();
        cat.name = "Whiskers";
        cat.lives = 9;
        Map<?, ?> encoded = writeAsMap(MAPPER, cat);
        assertEquals("cat", encoded.get("@type"));
        assertEquals("Whiskers", encoded.get("name"));
        assertEquals(9, encoded.get("lives"));
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

    void __invoke_testBaseTypeAsDefaultImplSkippedVpack() throws Exception {
        try {
            testBaseTypeAsDefaultImplSkippedVpack();
        } finally {
        }
    }


    void __invoke_testBaseTypeAsDefaultImplSubclassHasTypeIdVpack() throws Exception {
        try {
            testBaseTypeAsDefaultImplSubclassHasTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testExistingPropertyDefaultImplTypeFieldStillWrittenVpack() throws Exception {
        try {
            testExistingPropertyDefaultImplTypeFieldStillWrittenVpack();
        } finally {
        }
    }


    void __invoke_testExistingPropertyNonDefaultHasTypeIdVpack() throws Exception {
        try {
            testExistingPropertyNonDefaultHasTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testExternalPropertyDefaultImplRoundTripVpack() throws Exception {
        try {
            testExternalPropertyDefaultImplRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testExternalPropertyDefaultImplSkippedVpack() throws Exception {
        try {
            testExternalPropertyDefaultImplSkippedVpack();
        } finally {
        }
    }


    void __invoke_testExternalPropertyNonDefaultHasTypeIdVpack() throws Exception {
        try {
            testExternalPropertyNonDefaultHasTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testFeatureOffAlwaysWritesTypeIdVpack() throws Exception {
        try {
            testFeatureOffAlwaysWritesTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testPropertyDefaultImplSkippedVpack() throws Exception {
        try {
            testPropertyDefaultImplSkippedVpack();
        } finally {
        }
    }


    void __invoke_testPropertyNonDefaultHasTypeIdVpack() throws Exception {
        try {
            testPropertyNonDefaultHasTypeIdVpack();
        } finally {
        }
    }

}
