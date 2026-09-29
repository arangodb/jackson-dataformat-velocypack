package tools.jackson.databind.jsontype;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.NamedType;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0423F1 {
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

    // Provenance: StrictJsonTypeInfoHandling3853Test#testDefaultHasStrictTypeHandling().
    void testDefaultHasStrictTypeHandlingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .registerSubtypes(new NamedType(DoSomethingCommand423.class, "do-something")).build();
        verifyDeserializationWithFullTypeInfo(mapper);
        verifyInvalidTypeIdWithSuperclassTarget(mapper);
        verifyInvalidTypeIdWithConcreteTarget(mapper);
    }

    // Provenance: StrictJsonTypeInfoHandling3853Test#testExplicitNonStrictTypeHandling().
    void testExplicitNonStrictTypeHandlingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES)
                .registerSubtypes(new NamedType(DoSomethingCommand423.class, "do-something")).build();
        verifyDeserializationWithFullTypeInfo(mapper);
        verifyInvalidTypeIdWithSuperclassTarget(mapper);
        verifyDeserializationWithConcreteTarget(mapper);
    }

    // Provenance: StrictJsonTypeInfoHandling3853Test#testMissingTypeId().
    void testMissingTypeIdVpack() throws Exception {
        ObjectMapper enabled = VPackMapper.builder()
                .enable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES).build();
        ObjectMapper disabled = VPackMapper.builder()
                .disable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES).build();
        ObjectMapper defaults = VPackMapper.builder().build();

        for (ObjectMapper mapper : new ObjectMapper[] { enabled, defaults, disabled }) {
            assertMissing(mapper, FalseCommand423.class);
            assertMissing(mapper, TrueCommand423.class);
            assertMissing(mapper, DefaultCommand423.class);
            assertInstanceOf(DoFalseCommand423.class,
                    mapper.readValue(EMPTY_OBJECT, DoFalseCommand423.class));
        }
        for (ObjectMapper mapper : new ObjectMapper[] { enabled, defaults, disabled }) {
            assertMissing(mapper, DoTrueCommand423.class);
        }
        assertMissing(enabled, DoDefaultCommand423.class);
        assertMissing(defaults, DoDefaultCommand423.class);
        assertInstanceOf(DoDefaultCommand423.class,
                disabled.readValue(EMPTY_OBJECT, DoDefaultCommand423.class));
    }

    // Provenance: StrictJsonTypeInfoHandling3853Test#testStrictTypeHandling().
    void testStrictTypeHandlingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES).build();
        verifyDeserializationWithFullTypeInfo(mapper);
        verifyInvalidTypeIdWithSuperclassTarget(mapper);
        verifyInvalidTypeIdWithConcreteTarget(mapper);
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

    void __invoke_testDefaultHasStrictTypeHandlingVpack() throws Exception {
        try {
            testDefaultHasStrictTypeHandlingVpack();
        } finally {
        }
    }


    void __invoke_testExplicitNonStrictTypeHandlingVpack() throws Exception {
        try {
            testExplicitNonStrictTypeHandlingVpack();
        } finally {
        }
    }


    void __invoke_testMissingTypeIdVpack() throws Exception {
        try {
            testMissingTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testStrictTypeHandlingVpack() throws Exception {
        try {
            testStrictTypeHandlingVpack();
        } finally {
        }
    }

}
