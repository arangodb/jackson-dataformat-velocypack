package tools.jackson.databind.jsontype.ext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0448F1 {
private static final byte[] LOCATION_TYPE_FIRST_SCALAR = VPackWireFixtureTest.hex(
            "14 1a 44 74 79 70 65 48 6c 6f 63 61 74 69 6f 6e "
          + "47 77 72 61 70 70 65 64 31 02");
private static final byte[] LOCATION_TYPE_FIRST_OBJECT = VPackWireFixtureTest.hex(
            "14 1a 44 74 79 70 65 48 6c 6f 63 61 74 69 6f 6e "
          + "47 77 72 61 70 70 65 64 0a 02");
private static final byte[] LOCATION_VALUE_FIRST_SCALAR = VPackWireFixtureTest.hex(
            "14 1a 47 77 72 61 70 70 65 64 31 44 74 79 70 65 "
          + "48 6c 6f 63 61 74 69 6f 6e 02");
private static final byte[] LOCATION_VALUE_FIRST_OBJECT = VPackWireFixtureTest.hex(
            "14 1a 47 77 72 61 70 70 65 64 0a 44 74 79 70 65 "
          + "48 6c 6f 63 61 74 69 6f 6e 02");
private static final byte[] LOCATION_DEFAULT = VPackWireFixtureTest.hex(
            "14 0c 47 77 72 61 70 70 65 64 0a 01");
private static final byte[] APPLE_PRESENT = VPackWireFixtureTest.hex(
            "14 2e 44 74 79 70 65 45 61 70 70 6c 65 45 66 72 75 69 74 "
          + "14 1a 44 6e 61 6d 65 45 41 70 70 6c 65 49 73 65 65 64 43 6f 75 6e 74 "
          + "28 10 02 02");
private static final byte[] ORANGE_PRESENT = VPackWireFixtureTest.hex(
            "14 31 44 74 79 70 65 46 6f 72 61 6e 67 65 45 66 72 75 69 74 "
          + "14 1c 44 6e 61 6d 65 46 4f 72 61 6e 67 65 45 63 6f 6c 6f 72 "
          + "46 6f 72 61 6e 67 65 02 02");
private static final byte[] APPLE_EMPTY = VPackWireFixtureTest.hex(
            "14 15 44 74 79 70 65 45 61 70 70 6c 65 45 66 72 75 69 74 0a 02");
private static final byte[] ORANGE_EMPTY = VPackWireFixtureTest.hex(
            "14 16 44 74 79 70 65 46 6f 72 61 6e 67 65 45 66 72 75 69 74 0a 02");
private static final byte[] APPLE_NULL = VPackWireFixtureTest.hex(
            "14 15 44 74 79 70 65 45 61 70 70 6c 65 45 66 72 75 69 74 18 02");
private static final byte[] ORANGE_NULL = VPackWireFixtureTest.hex(
            "14 16 44 74 79 70 65 46 6f 72 61 6e 67 65 45 66 72 75 69 74 18 02");
private static final byte[] APPLE_MISSING = VPackWireFixtureTest.hex(
            "14 0e 44 74 79 70 65 45 61 70 70 6c 65 01");
private static final byte[] ORANGE_MISSING = VPackWireFixtureTest.hex(
            "14 0f 44 74 79 70 65 46 6f 72 61 6e 67 65 01");
private final ObjectMapper mapper = new VPackMapper();

    void testPropertyCreatorDeserializationPresentVpack() throws Exception {
        checkCreatorBox(creatorReader(false), ORANGE_PRESENT, orangeBox());
        checkCreatorBox(creatorReader(false), APPLE_PRESENT, appleBox());
        checkCreatorBox(creatorReader(true), ORANGE_PRESENT, orangeBox());
        checkCreatorBox(creatorReader(true), APPLE_PRESENT, appleBox());
    }

    void testPropertyCreatorDeserializationNullVpack() throws Exception {
        checkCreatorNull(creatorReader(false), ORANGE_NULL);
        checkCreatorNull(creatorReader(false), APPLE_NULL);
        checkCreatorNull(creatorReader(true), ORANGE_NULL);
        checkCreatorNull(creatorReader(true), APPLE_NULL);
    }

    void testPropertyCreatorDeserializationEmptyVpack() throws Exception {
        checkCreatorEmpty(creatorReader(false), ORANGE_EMPTY);
        checkCreatorEmpty(creatorReader(false), APPLE_EMPTY);
        checkCreatorEmpty(creatorReader(true), ORANGE_EMPTY);
        checkCreatorEmpty(creatorReader(true), APPLE_EMPTY);
    }

    void testPropertyCreatorDeserializationMissingVpack() throws Exception {
        checkCreatorNull(creatorReader(false), ORANGE_MISSING);
        checkCreatorNull(creatorReader(false), APPLE_MISSING);
        assertMissing(creatorReader(true), ORANGE_MISSING);
        assertMissing(creatorReader(true), APPLE_MISSING);
    }
private static void assertLocation(Tag value) {
        assertEquals(Location.class, value.getClass());
        assertEquals("/wrapped", ((Location) value).value);
    }
private ObjectReader creatorReader(boolean failOnMissing) {
        ObjectReader reader = mapper.readerFor(CreatorBox.class)
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        return failOnMissing
                ? reader.with(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
                : reader.without(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
    }
private ObjectReader subtypeReader(boolean failOnMissing) {
        ObjectReader reader = mapper.readerFor(Box.class)
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        return failOnMissing
                ? reader.with(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
                : reader.without(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);
    }
private static CreatorBox creatorBox(ObjectReader reader, byte[] input) throws Exception {
        return reader.readValue(input);
    }
private static void checkCreatorBox(ObjectReader reader, byte[] input, Box expected) throws Exception {
        CreatorBox result = creatorBox(reader, input);
        assertEquals(expected.type, result.type);
        assertFruit(result.fruit, expected.fruit);
    }
private static void checkCreatorNull(ObjectReader reader, byte[] input) throws Exception {
        CreatorBox result = reader.readValue(input);
        assertEquals("orange".equals(result.type) || "apple".equals(result.type), true);
        assertNull(result.fruit);
    }
private static void checkCreatorEmpty(ObjectReader reader, byte[] input) throws Exception {
        CreatorBox result = reader.readValue(input);
        assertInstanceOf(Fruit.class, result.fruit);
        assertNull(result.fruit.name);
        if (result.fruit instanceof Apple apple) assertEquals(0, apple.seedCount);
        if (result.fruit instanceof Orange orange) assertNull(orange.color);
    }
private static void checkBox(ObjectReader reader, byte[] input, Box expected) throws Exception {
        Box result = reader.readValue(input);
        assertEquals(expected.type, result.type);
        assertFruit(result.fruit, expected.fruit);
    }
private static void checkBoxNull(ObjectReader reader, byte[] input) throws Exception {
        Box result = reader.readValue(input);
        assertEquals("orange".equals(result.type) || "apple".equals(result.type), true);
        assertNull(result.fruit);
    }
private static void checkBoxEmpty(ObjectReader reader, byte[] input) throws Exception {
        Box result = reader.readValue(input);
        assertInstanceOf(Fruit.class, result.fruit);
        assertNull(result.fruit.name);
        if (result.fruit instanceof Apple apple) assertEquals(0, apple.seedCount);
        if (result.fruit instanceof Orange orange) assertNull(orange.color);
    }
private static void assertFruit(Fruit actual, Fruit expected) {
        assertSame(expected.getClass(), actual.getClass());
        assertEquals(expected.name, actual.name);
        if (actual instanceof Apple apple) assertEquals(((Apple) expected).seedCount, apple.seedCount);
        if (actual instanceof Orange orange) assertEquals(((Orange) expected).color, orange.color);
    }
private static void assertMissing(ObjectReader reader, byte[] input) {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(input));
        assertEquals(true, exception.getMessage().contains("Missing property 'fruit'"));
    }
private static void assertMissingRequired(ObjectReader reader, byte[] input) {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> reader.forType(ReqBox.class).readValue(input));
        assertEquals(true, exception.getMessage().contains("Missing property 'fruit'"));
    }
private static Box orangeBox() { return new Box("orange", new Orange("Orange", "orange")); }
private static Box appleBox() { return new Box("apple", new Apple("Apple", 16)); }
static class Wrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
        public Tag wrapped;
        public String type;
    }
static class CreatorWrapper {
        final String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
        @JsonSubTypes(@JsonSubTypes.Type(Location.class))
        final Tag wrapped;

        @JsonCreator
        CreatorWrapper(@JsonProperty("type") String type, @JsonProperty("wrapped") Tag wrapped) {
            this.type = type;
            this.wrapped = wrapped;
        }
    }
static class DefaultImplWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, defaultImpl = Location.class)
        public Tag wrapped;
        public String type;
    }
@JsonSubTypes(@JsonSubTypes.Type(Location.class))
    interface Tag { }
@JsonTypeName("location")
    @JsonDeserialize(using = LocationDeserializer.class)
    static class Location implements Tag {
        String value;
        Location() { }
        Location(String value) { this.value = value; }
    }
static class LocationDeserializer extends ValueDeserializer<Location> {
        @Override
        public Location deserialize(JsonParser parser, DeserializationContext context) {
            parser.skipChildren();
            return new Location(parser.streamReadContext().pathAsPointer().toString());
        }
    }
static class CreatorBox {
        private String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes({ @JsonSubTypes.Type(value = Apple.class, name = "apple"),
                @JsonSubTypes.Type(value = Orange.class, name = "orange") })
        private Fruit fruit;

        @JsonCreator
        CreatorBox(@JsonProperty("type") String type, @JsonProperty("fruit") Fruit fruit) {
            this.type = type;
            this.fruit = fruit;
        }
    }
static class Box {
        public String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type", visible = true)
        public Fruit fruit;
        public Box() { }
        Box(String type, Fruit fruit) { this.type = type; this.fruit = fruit; }
    }
static class ReqBox {
        public String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonProperty(required = true)
        public Fruit fruit;
        public ReqBox() { }
    }
@JsonSubTypes({ @JsonSubTypes.Type(value = Apple.class, name = "apple"),
            @JsonSubTypes.Type(value = Orange.class, name = "orange") })
    static abstract class Fruit {
        public String name;
        public Fruit() { }
        Fruit(String name) { this.name = name; }
    }
static class Apple extends Fruit {
        public int seedCount;
        public Apple() { }
        Apple(String name, int seedCount) { super(name); this.seedCount = seedCount; }
    }
static class Orange extends Fruit {
        public String color;
        public Orange() { }
        Orange(String name, String color) { super(name); this.color = color; }
    }

    void __invoke_testPropertyCreatorDeserializationPresentVpack() throws Exception {
        try {
            testPropertyCreatorDeserializationPresentVpack();
        } finally {
        }
    }


    void __invoke_testPropertyCreatorDeserializationNullVpack() throws Exception {
        try {
            testPropertyCreatorDeserializationNullVpack();
        } finally {
        }
    }


    void __invoke_testPropertyCreatorDeserializationEmptyVpack() throws Exception {
        try {
            testPropertyCreatorDeserializationEmptyVpack();
        } finally {
        }
    }


    void __invoke_testPropertyCreatorDeserializationMissingVpack() throws Exception {
        try {
            testPropertyCreatorDeserializationMissingVpack();
        } finally {
        }
    }

}
