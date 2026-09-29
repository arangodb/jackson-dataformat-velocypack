package tools.jackson.databind.jsontype.deduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidTypeIdException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0433Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] TYPE_RESOLUTION = VPackWireFixtureTest.hex(
            "14 12 41 7a 14 0d 42 7a 7a 14 07 41 61 28 2a 01 01 01");
private static final byte[] LIVE_CAT = VPackWireFixtureTest.hex(
            "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] DEAD_CAT = VPackWireFixtureTest.hex(
            "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] DEAD_CAT_UPPER = VPackWireFixtureTest.hex(
            "14 23 4c 43 41 55 53 45 4f 46 44 45 41 54 48 47 45 4e 54 52 4f 50 59 "
          + "44 4e 41 4d 45 45 46 45 4c 49 58 02");
private static final byte[] CAT_ARRAY = VPackWireFixtureTest.hex(
            "13 3b "
          + "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02 "
          + "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02 02");
private static final byte[] AMBIGUOUS_CAT = VPackWireFixtureTest.hex(
            "14 13 44 6e 61 6d 65 45 46 65 6c 69 78 43 61 67 65 32 02");
private static final byte[] LUCKY_CAT = VPackWireFixtureTest.hex(
            "14 1c 44 6e 61 6d 65 45 46 65 6c 69 78 45 61 6e 67 72 79 1a "
          + "45 6c 69 76 65 73 38 03");
private static final byte[] BOX_LIVE = VPackWireFixtureTest.hex(
            "14 1f 46 66 65 6c 69 6e 65 "
          + "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02 01");
private static final byte[] BOX_DEAD = VPackWireFixtureTest.hex(
            "14 2d 46 66 65 6c 69 6e 65 "
          + "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02 01");
private static final byte[] BOX_EMPTY = VPackWireFixtureTest.hex(
            "14 0b 46 66 65 6c 69 6e 65 0a 01");
private static final byte[] BOX_NULL = VPackWireFixtureTest.hex(
            "14 0b 46 66 65 6c 69 6e 65 18 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");

    
    // Provenance: BasicPolymorphicDeductionTest#testAliasWithPolymorphicDeduction4327(String).
    void testAliasWithPolymorphicDeduction4327Vpack(String field) throws Exception {
        Deduction4327 value = MAPPER.readValue(aliasFixture(field), Deduction4327.class);
        assertNotNull(value);
        assertEquals(2, assertInstanceOf(DeductionBean4327_2.class, value).y);
    }

    // Provenance: BasicPolymorphicDeductionTest#testAmbiguousClasses().
    void testAmbiguousClassesVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .registerSubtypes(AnotherLiveCat433.class).build();
        InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(LIVE_CAT, Cat433.class));
        assertTrue(error.getMessage().contains("cannot be uniquely deduced"),
                error::getMessage);
    }

    // Provenance: BasicPolymorphicDeductionTest#testAmbiguousProperties().
    void testAmbiguousPropertiesVpack() {
        InvalidTypeIdException error = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(AMBIGUOUS_CAT, Cat433.class));
        assertTrue(error.getMessage().contains("Cannot deduce unique subtype"),
                error::getMessage);
    }

    // Provenance: BasicPolymorphicDeductionTest#testArrayInference().
    void testArrayInferenceVpack() throws Exception {
        Cat433[] values = MAPPER.readValue(CAT_ARRAY, Cat433[].class);
        assertEquals(2, values.length);
        assertInstanceOf(LiveCat433.class, values[0]);
        assertInstanceOf(DeadCat433.class, values[1]);
    }

    // Provenance: BasicPolymorphicDeductionTest#testCaseInsensitiveInference().
    void testCaseInsensitiveInferenceVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
        Cat433 cat = mapper.readValue(DEAD_CAT_UPPER, Cat433.class);
        DeadCat433 value = assertInstanceOf(DeadCat433.class, cat);
        assertEquals("FELIX", value.name);
        assertEquals("ENTROPY", value.causeOfDeath);
    }

    // Provenance: BasicPolymorphicDeductionTest#testContainedInference().
    void testContainedInferenceVpack() throws Exception {
        Box433 box = MAPPER.readValue(BOX_LIVE, Box433.class);
        LiveCat433 live = assertInstanceOf(LiveCat433.class, box.feline);
        assertEquals("Felix", live.name);
        assertTrue(live.angry);

        box = MAPPER.readValue(BOX_DEAD, Box433.class);
        DeadCat433 dead = assertInstanceOf(DeadCat433.class, box.feline);
        assertEquals("entropy", dead.causeOfDeath);
    }

    // Provenance: BasicPolymorphicDeductionTest#testContainedInferenceOfEmptySubtype().
    void testContainedInferenceOfEmptySubtypeVpack() throws Exception {
        Box433 box = MAPPER.readValue(BOX_EMPTY, Box433.class);
        assertInstanceOf(Fleabag433.class, box.feline);
        box = MAPPER.readValue(BOX_NULL, Box433.class);
        assertNull(box.feline);
        box = MAPPER.readValue(EMPTY_OBJECT, Box433.class);
        assertNull(box.feline);
    }

    // Provenance: BasicPolymorphicDeductionTest#testDefaultImpl().
    void testDefaultImplVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Cat433.class, CatMixin433.class)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        Cat433 value = mapper.readValue(AMBIGUOUS_CAT, Cat433.class);
        assertSame(Cat433.class, value.getClass());
        assertEquals("Felix", value.name);
    }

    // Provenance: BasicPolymorphicDeductionTest#testFailOnInvalidSubtype().
    void testFailOnInvalidSubtypeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE).build();
        assertNull(mapper.readValue(AMBIGUOUS_CAT, Cat433.class));
    }

    // Provenance: BasicPolymorphicDeductionTest#testIgnoreProperties().
    void testIgnorePropertiesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        LiveCat433 value = assertInstanceOf(LiveCat433.class,
                mapper.readValue(LUCKY_CAT, Cat433.class));
        assertEquals("Felix", value.name);
        assertTrue(value.angry);
    }
private static byte[] aliasFixture(String field) {
        return switch (field) {
        case "y" -> VPackWireFixtureTest.hex("14 06 41 79 32 01");
        case "Y" -> VPackWireFixtureTest.hex("14 06 41 59 32 01");
        case "yy" -> VPackWireFixtureTest.hex("14 07 42 79 79 32 01");
        case "ff" -> VPackWireFixtureTest.hex("14 07 42 66 66 32 01");
        case "X" -> VPackWireFixtureTest.hex("14 06 41 58 32 01");
        default -> throw new IllegalArgumentException(field);
        };
    }
@SuppressWarnings("rawtypes")
    static class A433 {
        private final Map map;

        @JsonCreator
        A433(@JsonProperty("z") Map<String, B433> map) { this.map = map; }
        Map getMap() { return map; }
    }
static class B433 {
        int a;
        @JsonCreator
        B433(@JsonProperty("a") int a) { this.a = a; }
    }
@SuppressWarnings("serial")
    static class MyMap433<K, V> extends java.util.HashMap<K, V> { }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DesktopComputer433.class, name = "desktop"),
            @JsonSubTypes.Type(value = LaptopComputer433.class, name = "laptop")
    })
    static class Computer433 { public String id; }
@JsonTypeName("desktop")
    static class DesktopComputer433 extends Computer433 {
        public String location;
        DesktopComputer433() { }
        DesktopComputer433(String id, String location) { this.id = id; this.location = location; }
    }
@JsonTypeName("laptop")
    static class LaptopComputer433 extends Computer433 {
        public String vendor;
        LaptopComputer433() { }
        LaptopComputer433(String id, String vendor) { this.id = id; this.vendor = vendor; }
    }
static class Company433 {
        public List<Computer433> computers = new ArrayList<>();
        Company433 addComputer(Computer433 computer) { computers.add(computer); return this; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({ @JsonSubTypes.Type(LiveCat433.class), @JsonSubTypes.Type(DeadCat433.class),
            @JsonSubTypes.Type(Fleabag433.class) })
    interface Feline433 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({ @JsonSubTypes.Type(LiveCat433.class), @JsonSubTypes.Type(DeadCat433.class) })
    static class Cat433 implements Feline433 { public String name; }
static class DeadCat433 extends Cat433 { public String causeOfDeath; }
static class LiveCat433 extends Cat433 { public boolean angry; }
static class Fleabag433 implements Feline433 { }
static class Box433 { public Feline433 feline; }
static class AnotherLiveCat433 extends Cat433 { public boolean angry; }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({ @JsonSubTypes.Type(DeductionBean4327_1.class),
            @JsonSubTypes.Type(DeductionBean4327_2.class) })
    interface Deduction4327 { }
static class DeductionBean4327_1 implements Deduction4327 { public int x; }
static class DeductionBean4327_2 implements Deduction4327 {
        @JsonAlias({ "y", "Y", "yy", "ff", "X" })
        public int y;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = Cat433.class)
    abstract static class CatMixin433 { }

    void __invoke_testAliasWithPolymorphicDeduction4327Vpack(String field) throws Exception {
        try {
            testAliasWithPolymorphicDeduction4327Vpack(field);
        } finally {
        }
    }


    void __invoke_testAmbiguousClassesVpack() throws Exception {
        try {
            testAmbiguousClassesVpack();
        } finally {
        }
    }


    void __invoke_testAmbiguousPropertiesVpack() throws Exception {
        try {
            testAmbiguousPropertiesVpack();
        } finally {
        }
    }


    void __invoke_testArrayInferenceVpack() throws Exception {
        try {
            testArrayInferenceVpack();
        } finally {
        }
    }


    void __invoke_testCaseInsensitiveInferenceVpack() throws Exception {
        try {
            testCaseInsensitiveInferenceVpack();
        } finally {
        }
    }


    void __invoke_testContainedInferenceVpack() throws Exception {
        try {
            testContainedInferenceVpack();
        } finally {
        }
    }


    void __invoke_testContainedInferenceOfEmptySubtypeVpack() throws Exception {
        try {
            testContainedInferenceOfEmptySubtypeVpack();
        } finally {
        }
    }


    void __invoke_testDefaultImplVpack() throws Exception {
        try {
            testDefaultImplVpack();
        } finally {
        }
    }


    void __invoke_testFailOnInvalidSubtypeVpack() throws Exception {
        try {
            testFailOnInvalidSubtypeVpack();
        } finally {
        }
    }


    void __invoke_testIgnorePropertiesVpack() throws Exception {
        try {
            testIgnorePropertiesVpack();
        } finally {
        }
    }

}
