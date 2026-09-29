package tools.jackson.databind.jsontype;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0412F0 {
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

    // Provenance: ExistingPropertyTest#testExistingPropertySerializationFruits().
    void testExistingPropertySerializationFruitsVpack() throws Exception {
        Map<?, ?> apple = MAPPER.readValue(MAPPER.writeValueAsBytes(new Apple("Apple-A-Day", 16)), Map.class);
        assertEquals(Map.of("name", "Apple-A-Day", "seedCount", 16, "type", "apple"), apple);
        Map<?, ?> orange = MAPPER.readValue(MAPPER.writeValueAsBytes(new Orange("Mandarin Orange", "orange")), Map.class);
        assertEquals(Map.of("name", "Mandarin Orange", "color", "orange", "type", "orange"), orange);
        FruitWrapper wrapper = MAPPER.readValue(MAPPER.writeValueAsBytes(new FruitWrapper(new Apple("Apple-A-Day", 16))), FruitWrapper.class);
        assertInstanceOf(Apple.class, wrapper.fruit);
        assertEquals(2, MAPPER.readValue(MAPPER.writeValueAsBytes(List.of(new Apple("Apple-A-Day", 16), new Orange("Mandarin Orange", "orange"))), List.class).size());
    }

    // Provenance: ExistingPropertyTest#testSimpleClassAsExistingPropertyDeserializationFruits().
    void testSimpleClassAsExistingPropertyDeserializationFruitsVpack() throws Exception {
        Fruit apple = MAPPER.readValue(APPLE, Fruit.class);
        assertInstanceOf(Apple.class, apple);
        assertEquals("Apple-A-Day", apple.name);
        assertEquals(16, ((Apple) apple).seedCount);
        assertEquals("apple", ((Apple) apple).type);
        Fruit orange = MAPPER.readValue(ORANGE, Fruit.class);
        assertInstanceOf(Orange.class, orange);
        assertEquals("Mandarin Orange", orange.name);
        assertEquals("orange", ((Orange) orange).color);
    }

    // Provenance: ExistingPropertyTest#testExistingPropertySerializationAnimals().
    void testExistingPropertySerializationAnimalsVpack() throws Exception {
        Map<?, ?> cat = MAPPER.readValue(MAPPER.writeValueAsBytes(new Cat("Beelzebub", "tabby")), Map.class);
        assertEquals(Map.of("name", "Beelzebub", "furColor", "tabby", "type", "kitty"), cat);
        Map<?, ?> dog = MAPPER.readValue(MAPPER.writeValueAsBytes(new Dog("Rover", 42)), Map.class);
        assertEquals(Map.of("name", "Rover", "boneCount", 42, "type", "doggie"), dog);
        AnimalWrapper wrapper = MAPPER.readValue(MAPPER.writeValueAsBytes(new AnimalWrapper(new Cat("Beelzebub", "tabby"))), AnimalWrapper.class);
        assertInstanceOf(Cat.class, wrapper.animal);
    }

    // Provenance: ExistingPropertyTest#testSimpleClassAsExistingPropertyDeserializationAnimals().
    void testSimpleClassAsExistingPropertyDeserializationAnimalsVpack() throws Exception {
        Animal cat = MAPPER.readValue(CAT, Animal.class);
        assertInstanceOf(Cat.class, cat);
        assertEquals("Beelzebub", cat.name);
        assertEquals("tabby", ((Cat) cat).furColor);
        assertEquals("kitty", cat.getType());
    }

    // Provenance: ExistingPropertyTest#testExistingPropertySerializationCars().
    void testExistingPropertySerializationCarsVpack() throws Exception {
        Map<?, ?> camry = MAPPER.readValue(MAPPER.writeValueAsBytes(new Camry("Sweet Ride", "candy-apple-red")), Map.class);
        assertEquals(Map.of("name", "Sweet Ride", "exteriorColor", "candy-apple-red", "type", "camry"), camry);
        Map<?, ?> accord = MAPPER.readValue(MAPPER.writeValueAsBytes(new Accord("Road Rage", 6)), Map.class);
        assertEquals(Map.of("name", "Road Rage", "speakerCount", 6, "type", "accord"), accord);
        CarWrapper wrapper = MAPPER.readValue(MAPPER.writeValueAsBytes(new CarWrapper(new Camry("Sweet Ride", "candy-apple-red"))), CarWrapper.class);
        assertInstanceOf(Camry.class, wrapper.car);
    }

    // Provenance: ExistingPropertyTest#testSimpleClassAsExistingPropertyDeserializationCars().
    void testSimpleClassAsExistingPropertyDeserializationCarsVpack() throws Exception {
        Car camry = MAPPER.readValue(CAMRY, Car.class);
        assertInstanceOf(Camry.class, camry);
        assertEquals("Sweet Ride", camry.name);
        assertEquals("candy-apple-red", ((Camry) camry).exteriorColor);
        Car accord = MAPPER.readValue(ACCORD, Car.class);
        assertInstanceOf(Accord.class, accord);
        assertEquals(6, ((Accord) accord).speakerCount);
    }

    // Provenance: ExistingPropertyTest#testExistingEnumTypeId().
    void testExistingEnumTypeIdVpack() throws Exception {
        Bean1635 result = MAPPER.readValue(ENUM_A, Bean1635.class);
        assertEquals(Bean1635A.class, result.getClass());
        assertEquals(3, ((Bean1635A) result).value);
        assertEquals(ABC.A, result.type);
    }

    // Provenance: ExistingPropertyTest#testExistingEnumTypeIdViaDefault().
    void testExistingEnumTypeIdViaDefaultVpack() throws Exception {
        Bean1635 result = MAPPER.readValue(ENUM_C, Bean1635.class);
        assertEquals(Bean1635Default.class, result.getClass());
        assertEquals(ABC.C, result.type);
    }

    // Provenance: ExistingPropertyTest#testDeserializationWithValidType().
    void testDeserializationWithValidTypeVpack() throws Exception {
        Shape3271 result = MAPPER.readValue(SHAPE_SQUARE, Shape3271.class);
        assertInstanceOf(Square3271.class, result);
        assertEquals("square", result.getType());
    }

    // Provenance: ExistingPropertyTest#testDeserializationWithInvalidType().
    void testDeserializationWithInvalidTypeVpack() throws Exception {
        Shape3271 result = MAPPER.readValue(SHAPE_INVALID, Shape3271.class);
        assertInstanceOf(DefaultShape3271.class, result);
        assertEquals("invalid", result.getType());
    }

    // Provenance: ExistingPropertyTest#testNullTypePreservedWithDefaultImpl1528().
    void testNullTypePreservedWithDefaultImpl1528Vpack() throws Exception {
        Parent1528 result = MAPPER.readValue(SHAPE_NULL, Parent1528.class);
        assertNull(result.type);
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

    void __invoke_testExistingPropertySerializationFruitsVpack() throws Exception {
        try {
            testExistingPropertySerializationFruitsVpack();
        } finally {
        }
    }


    void __invoke_testSimpleClassAsExistingPropertyDeserializationFruitsVpack() throws Exception {
        try {
            testSimpleClassAsExistingPropertyDeserializationFruitsVpack();
        } finally {
        }
    }


    void __invoke_testExistingPropertySerializationAnimalsVpack() throws Exception {
        try {
            testExistingPropertySerializationAnimalsVpack();
        } finally {
        }
    }


    void __invoke_testSimpleClassAsExistingPropertyDeserializationAnimalsVpack() throws Exception {
        try {
            testSimpleClassAsExistingPropertyDeserializationAnimalsVpack();
        } finally {
        }
    }


    void __invoke_testExistingPropertySerializationCarsVpack() throws Exception {
        try {
            testExistingPropertySerializationCarsVpack();
        } finally {
        }
    }


    void __invoke_testSimpleClassAsExistingPropertyDeserializationCarsVpack() throws Exception {
        try {
            testSimpleClassAsExistingPropertyDeserializationCarsVpack();
        } finally {
        }
    }


    void __invoke_testExistingEnumTypeIdVpack() throws Exception {
        try {
            testExistingEnumTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testExistingEnumTypeIdViaDefaultVpack() throws Exception {
        try {
            testExistingEnumTypeIdViaDefaultVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithValidTypeVpack() throws Exception {
        try {
            testDeserializationWithValidTypeVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithInvalidTypeVpack() throws Exception {
        try {
            testDeserializationWithInvalidTypeVpack();
        } finally {
        }
    }


    void __invoke_testNullTypePreservedWithDefaultImpl1528Vpack() throws Exception {
        try {
            testNullTypePreservedWithDefaultImpl1528Vpack();
        } finally {
        }
    }

}
