package tools.jackson.databind.jsontype;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.exc.InvalidTypeIdException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0423F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] DEFAULT_DOG = VPackWireFixtureTest.hex(
            "14 16 44 6e 61 6d 65 43 52 65 78 45 62 72 65 65 64 43 4c 61 62 02");
private static final byte[] CAT = VPackWireFixtureTest.hex(
            "14 22 45 40 74 79 70 65 43 63 61 74 44 6e 61 6d 65 48 57 68 69 73 6b 65 72 73 "
          + "45 6c 69 76 65 73 39 03");
private static final byte[] WRAPPED_DEFAULT = DEFAULT_DOG;
private static final byte[] WRAPPED_ARRAY_CAT = VPackWireFixtureTest.hex(
            "13 1f 43 63 61 74 14 18 44 6e 61 6d 65 48 57 68 69 73 6b 65 72 73 "
          + "45 6c 69 76 65 73 39 02 02");
private static final byte[] WRAPPED_OBJECT_CAT = VPackWireFixtureTest.hex(
            "14 1f 43 63 61 74 14 18 44 6e 61 6d 65 48 57 68 69 73 6b 65 72 73 "
          + "45 6c 69 76 65 73 39 02 01");
private static final byte[] DO_SOMETHING = VPackWireFixtureTest.hex(
            "14 16 45 40 74 79 70 65 4c 64 6f 2d 73 6f 6d 65 74 68 69 6e 67 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testPropertyRoundTrip().
    void testPropertyRoundTripVpack() throws Exception {
        Animal423 defaultDog = MAPPER.readValue(DEFAULT_DOG, Animal423.class);
        assertInstanceOf(DefaultDog423.class, defaultDog);
        assertEquals("Rex", defaultDog.name);
        assertEquals("Lab", ((DefaultDog423) defaultDog).breed);

        Animal423 cat = MAPPER.readValue(CAT, Animal423.class);
        assertInstanceOf(Cat423.class, cat);
        assertEquals("Whiskers", cat.name);
        assertEquals(9, ((Cat423) cat).lives);
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testPropertySubclassOfDefaultImplHasTypeId().
    void testPropertySubclassOfDefaultImplHasTypeIdVpack() throws Exception {
        Puppy423 puppy = new Puppy423();
        puppy.name = "Tiny";
        puppy.breed = "Poodle";
        puppy.isSmall = true;
        MapValue encoded = writeAsMap(MAPPER.writerFor(Animal423.class), puppy);
        assertEquals("puppy", encoded.value("@type"));
        assertEquals("Tiny", encoded.value("name"));
        assertEquals("Poodle", encoded.value("breed"));
        assertEquals(true, encoded.value("isSmall"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testWrapperArrayDefaultImplSkipped().
    void testWrapperArrayDefaultImplSkippedVpack() throws Exception {
        DefaultDog2_423 dog = new DefaultDog2_423();
        dog.name = "Rex";
        dog.breed = "Lab";
        MapValue encoded = writeAsMap(MAPPER.writerFor(Animal2_423.class), dog);
        assertFalse(encoded.containsKey("@type"));
        assertEquals("Rex", encoded.value("name"));
        assertEquals("Lab", encoded.value("breed"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testWrapperArrayNonDefaultHasTypeId().
    void testWrapperArrayNonDefaultHasTypeIdVpack() throws Exception {
        ListValue encoded = writeAsList(MAPPER.writerFor(Animal2_423.class), cat2());
        assertEquals(2, encoded.size());
        assertEquals("cat", encoded.value(0));
        MapValue cat = encoded.map(1);
        assertEquals("Whiskers", cat.value("name"));
        assertEquals(9, cat.value("lives"));

        ListValue literal = new ListValue(MAPPER.readValue(WRAPPED_ARRAY_CAT, List.class));
        assertEquals("cat", literal.value(0));
        assertEquals(9, literal.map(1).value("lives"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testWrapperArrayRoundTrip().
    void testWrapperArrayRoundTripVpack() throws Exception {
        Animal2_423 result = MAPPER.readValue(WRAPPED_DEFAULT, Animal2_423.class);
        assertNotNull(result);
        assertEquals("Rex", result.name);
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testWrapperObjectDefaultImplSkipped().
    void testWrapperObjectDefaultImplSkippedVpack() throws Exception {
        DefaultDog3_423 dog = new DefaultDog3_423();
        dog.name = "Rex";
        dog.breed = "Lab";
        MapValue encoded = writeAsMap(MAPPER.writerFor(Animal3_423.class), dog);
        assertFalse(encoded.containsKey("dog"));
        assertEquals("Rex", encoded.value("name"));
        assertEquals("Lab", encoded.value("breed"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testWrapperObjectNonDefaultHasTypeId().
    void testWrapperObjectNonDefaultHasTypeIdVpack() throws Exception {
        MapValue encoded = writeAsMap(MAPPER.writerFor(Animal3_423.class), cat3());
        MapValue cat = encoded.map("cat");
        assertEquals("Whiskers", cat.value("name"));
        assertEquals(9, cat.value("lives"));
    }

    // Provenance: SkipWriteTypeIdForDefaultImplTest#testWrapperObjectNonDefaultRoundTrip().
    void testWrapperObjectNonDefaultRoundTripVpack() throws Exception {
        Animal3_423 result = MAPPER.readValue(WRAPPED_OBJECT_CAT, Animal3_423.class);
        assertInstanceOf(Cat3_423.class, result);
        assertEquals("Whiskers", result.name);
        assertEquals(9, ((Cat3_423) result).lives);
    }
private static void verifyDeserializationWithFullTypeInfo(ObjectMapper mapper) throws Exception {
        assertInstanceOf(DoSomethingCommand423.class,
                mapper.readValue(DO_SOMETHING, Command423.class));
        assertInstanceOf(DoSomethingCommand423.class,
                mapper.readValue(DO_SOMETHING, DoSomethingCommand423.class));
    }
private static void verifyInvalidTypeIdWithSuperclassTarget(ObjectMapper mapper) {
        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(EMPTY_OBJECT, Command423.class));
    }
private static void verifyInvalidTypeIdWithConcreteTarget(ObjectMapper mapper) {
        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(EMPTY_OBJECT, DoSomethingCommand423.class));
    }
private static void verifyDeserializationWithConcreteTarget(ObjectMapper mapper) throws Exception {
        assertInstanceOf(DoSomethingCommand423.class,
                mapper.readValue(EMPTY_OBJECT, DoSomethingCommand423.class));
    }
private static void assertMissing(ObjectMapper mapper, Class<?> type) {
        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(EMPTY_OBJECT, type));
    }
private static MapValue writeAsMap(ObjectWriter writer, Object value) throws Exception {
        return new MapValue(MAPPER.readValue(writer.writeValueAsBytes(value), Map.class));
    }
private static MapValue writeAsMap(ObjectMapper mapper, Object value) throws Exception {
        return new MapValue(MAPPER.readValue(mapper.writeValueAsBytes(value), Map.class));
    }
private static ListValue writeAsList(ObjectWriter writer, Object value) throws Exception {
        return new ListValue(MAPPER.readValue(writer.writeValueAsBytes(value), List.class));
    }
private static Cat2_423 cat2() {
        Cat2_423 cat = new Cat2_423();
        cat.name = "Whiskers";
        cat.lives = 9;
        return cat;
    }
private static Cat3_423 cat3() {
        Cat3_423 cat = new Cat3_423();
        cat.name = "Whiskers";
        cat.lives = 9;
        return cat;
    }
private record MapValue(Map<?, ?> values) {
        boolean containsKey(String key) { return values.containsKey(key); }
        Object value(String key) { return values.get(key); }
        MapValue map(String key) { return new MapValue(assertInstanceOf(Map.class, values.get(key))); }
    }
private record ListValue(java.util.List<?> values) {
        int size() { return values.size(); }
        Object value(int index) { return values.get(index); }
        MapValue map(int index) { return new MapValue(assertInstanceOf(Map.class, values.get(index))); }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@type", defaultImpl = DefaultDog423.class,
            writeTypeIdForDefaultImpl = OptBoolean.FALSE)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultDog423.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat423.class, name = "cat"),
        @JsonSubTypes.Type(value = Puppy423.class, name = "puppy")
    })
    static class Animal423 { public String name; }
static class DefaultDog423 extends Animal423 { public String breed; }
static class Cat423 extends Animal423 { public int lives; }
static class Puppy423 extends DefaultDog423 { public boolean isSmall; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            defaultImpl = DefaultDog2_423.class, writeTypeIdForDefaultImpl = OptBoolean.FALSE)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultDog2_423.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat2_423.class, name = "cat")
    })
    static class Animal2_423 { public String name; }
static class DefaultDog2_423 extends Animal2_423 { public String breed; }
static class Cat2_423 extends Animal2_423 { public int lives; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT,
            defaultImpl = DefaultDog3_423.class, writeTypeIdForDefaultImpl = OptBoolean.FALSE)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultDog3_423.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat3_423.class, name = "cat")
    })
    static class Animal3_423 { public String name; }
static class DefaultDog3_423 extends Animal3_423 { public String breed; }
static class Cat3_423 extends Animal3_423 { public int lives; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(value = DoSomethingCommand423.class, name = "do-something"))
    interface Command423 { }
@JsonTypeName("do-something")
    static class DoSomethingCommand423 implements Command423 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, requireTypeIdForSubtypes = OptBoolean.DEFAULT)
    @JsonSubTypes(@JsonSubTypes.Type(value = DoDefaultCommand423.class, name = "do-default"))
    interface DefaultCommand423 { }
static class DoDefaultCommand423 implements DefaultCommand423 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, requireTypeIdForSubtypes = OptBoolean.TRUE)
    @JsonSubTypes(@JsonSubTypes.Type(value = DoTrueCommand423.class, name = "do-true"))
    interface TrueCommand423 { }
static class DoTrueCommand423 implements TrueCommand423 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, requireTypeIdForSubtypes = OptBoolean.FALSE)
    @JsonSubTypes(@JsonSubTypes.Type(value = DoFalseCommand423.class, name = "do-false"))
    interface FalseCommand423 { }
static class DoFalseCommand423 implements FalseCommand423 { }

    void __invoke_testPropertyRoundTripVpack() throws Exception {
        try {
            testPropertyRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testPropertySubclassOfDefaultImplHasTypeIdVpack() throws Exception {
        try {
            testPropertySubclassOfDefaultImplHasTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testWrapperArrayDefaultImplSkippedVpack() throws Exception {
        try {
            testWrapperArrayDefaultImplSkippedVpack();
        } finally {
        }
    }


    void __invoke_testWrapperArrayNonDefaultHasTypeIdVpack() throws Exception {
        try {
            testWrapperArrayNonDefaultHasTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testWrapperArrayRoundTripVpack() throws Exception {
        try {
            testWrapperArrayRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testWrapperObjectDefaultImplSkippedVpack() throws Exception {
        try {
            testWrapperObjectDefaultImplSkippedVpack();
        } finally {
        }
    }


    void __invoke_testWrapperObjectNonDefaultHasTypeIdVpack() throws Exception {
        try {
            testWrapperObjectNonDefaultHasTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testWrapperObjectNonDefaultRoundTripVpack() throws Exception {
        try {
            testWrapperObjectNonDefaultRoundTripVpack();
        } finally {
        }
    }

}
