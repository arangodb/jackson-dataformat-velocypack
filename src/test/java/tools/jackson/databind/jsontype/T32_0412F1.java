package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidTypeIdException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0412F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] APPLE = VPackWireFixtureTest.hex(
            "0b 2e 03 44 6e 61 6d 65 4b 41 70 70 6c 65 2d 41 2d 44 61 79 "
          + "49 73 65 65 64 43 6f 75 6e 74 28 10 44 74 79 70 65 45 61 70 70 6c 65 "
          + "03 14 20");
private static final byte[] ORANGE = VPackWireFixtureTest.hex(
            "0b 34 03 44 6e 61 6d 65 4f 4d 61 6e 64 61 72 69 6e 20 4f 72 61 6e 67 65 "
          + "45 63 6f 6c 6f 72 46 6f 72 61 6e 67 65 44 74 79 70 65 46 6f 72 61 6e 67 65 "
          + "18 03 25");
private static final byte[] CAT = VPackWireFixtureTest.hex(
            "0b 2f 03 48 66 75 72 43 6f 6c 6f 72 45 74 61 62 62 79 "
          + "44 6e 61 6d 65 49 42 65 65 6c 7a 65 62 75 62 "
          + "44 74 79 70 65 45 6b 69 74 74 79 03 12 21");
private static final byte[] CAMRY = VPackWireFixtureTest.hex(
            "0b 3f 03 4d 65 78 74 65 72 69 6f 72 43 6f 6c 6f 72 4f 63 61 6e 64 79 2d 61 70 70 6c 65 2d 72 65 64 "
          + "44 6e 61 6d 65 4a 53 77 65 65 74 20 52 69 64 65 "
          + "44 74 79 70 65 45 63 61 6d 72 79 03 21 31");
private static final byte[] ACCORD = VPackWireFixtureTest.hex(
            "0b 2f 03 44 6e 61 6d 65 49 52 6f 61 64 20 52 61 67 65 "
          + "4c 73 70 65 61 6b 65 72 43 6f 75 6e 74 36 "
          + "44 74 79 70 65 46 61 63 63 6f 72 64 03 12 20");
private static final byte[] ENUM_A = VPackWireFixtureTest.hex(
            "0b 13 02 44 74 79 70 65 41 41 45 76 61 6c 75 65 33 03 0a");
private static final byte[] ENUM_C = VPackWireFixtureTest.hex(
            "0b 0b 01 44 74 79 70 65 41 43 03");
private static final byte[] SHAPE_SQUARE = VPackWireFixtureTest.hex(
            "0b 10 01 44 74 79 70 65 46 73 71 75 61 72 65 03");
private static final byte[] SHAPE_INVALID = VPackWireFixtureTest.hex(
            "0b 11 01 44 74 79 70 65 47 69 6e 76 61 6c 69 64 03");
private static final byte[] SHAPE_NULL = VPackWireFixtureTest.hex(
            "0b 0a 01 44 74 79 70 65 18 03");
private static final byte[] MIXED_CASE_PROPERTY = VPackWireFixtureTest.hex(
            "0b 14 01 49 6f 50 65 52 61 54 69 6f 4e 45 6e 6f 74 45 71 03");

    // Provenance: JsonTypeInfoCaseInsensitive1983Test#readMixedCasePropertyName().
    void readMixedCasePropertyNameVpack() throws Exception {
        assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(MIXED_CASE_PROPERTY, Filter.class));
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES).build();
        assertEquals(NotEqual.class, mapper.readValue(MIXED_CASE_PROPERTY, Filter.class).getClass());
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type", visible = true)
    @JsonSubTypes({ @JsonSubTypes.Type(value = Apple.class, name = "apple"),
            @JsonSubTypes.Type(value = Orange.class, name = "orange") })
    static abstract class Fruit { public String name; Fruit() { } Fruit(String name) { this.name = name; } }
@JsonTypeName("apple") @JsonPropertyOrder({ "name", "seedCount", "type" })
    static class Apple extends Fruit { public int seedCount; public String type; Apple() { } Apple(String n, int count) { super(n); seedCount = count; type = "apple"; } }
@JsonTypeName("orange") @JsonPropertyOrder({ "name", "color", "type" })
    static class Orange extends Fruit { public String color; public String type; Orange() { } Orange(String n, String color) { super(n); this.color = color; type = "orange"; } }
static class FruitWrapper { public Fruit fruit; FruitWrapper() { } FruitWrapper(Fruit fruit) { this.fruit = fruit; } }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(value = Dog.class, name = "doggie"), @JsonSubTypes.Type(value = Cat.class, name = "kitty") })
    static abstract class Animal { public String name; Animal() { } Animal(String name) { this.name = name; } public abstract String getType(); }
static class Dog extends Animal { public int boneCount; Dog() { } Dog(String n, int count) { super(n); boneCount = count; } public String getType() { return "doggie"; } }
static class Cat extends Animal { public String furColor; Cat() { } Cat(String n, String color) { super(n); furColor = color; } public String getType() { return "kitty"; } }
static class AnimalWrapper { public Animal animal; AnimalWrapper() { } AnimalWrapper(Animal animal) { this.animal = animal; } }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(value = Accord.class, name = "accord"), @JsonSubTypes.Type(value = Camry.class, name = "camry") })
    static abstract class Car { public String name; Car() { } Car(String name) { this.name = name; } }
static class Accord extends Car { public int speakerCount; Accord() { } Accord(String n, int count) { super(n); speakerCount = count; } public String getType() { return "accord"; } }
static class Camry extends Car { public String exteriorColor; Camry() { } Camry(String n, String color) { super(n); exteriorColor = color; } public String getType() { return "camry"; } }
static class CarWrapper { public Car car; CarWrapper() { } CarWrapper(Car car) { this.car = car; } }
enum ABC { A, B, C }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            visible = true, property = "type", defaultImpl = Bean1635Default.class)
    @JsonSubTypes(@JsonSubTypes.Type(value = Bean1635A.class, name = "A"))
    static class Bean1635 { public ABC type; }
@JsonTypeName("A") static class Bean1635A extends Bean1635 { public int value; }
static class Bean1635Default extends Bean1635 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            visible = true, property = "type", defaultImpl = DefaultShape3271.class)
    @JsonSubTypes(@JsonSubTypes.Type(value = Square3271.class, name = "square"))
    static abstract class Shape3271 { public String type; public String getType() { return type; } }
static class Square3271 extends Shape3271 { }
static class DefaultShape3271 extends Shape3271 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type", visible = true, defaultImpl = Default1528.class)
    @JsonSubTypes(@JsonSubTypes.Type(value = Child1528.class, name = "child"))
    static class Parent1528 { @JsonProperty("type") public String type; }
static class Default1528 extends Parent1528 { }
static class Child1528 extends Parent1528 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "Operation")
    @JsonSubTypes({ @JsonSubTypes.Type(value = Equal.class, name = "eq"), @JsonSubTypes.Type(value = NotEqual.class, name = "notEq") })
    static abstract class Filter { }
static class Equal extends Filter { }
static class NotEqual extends Filter { }

    void __invoke_readMixedCasePropertyNameVpack() throws Exception {
        try {
            readMixedCasePropertyNameVpack();
        } finally {
        }
    }

}
