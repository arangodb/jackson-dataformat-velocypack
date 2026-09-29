package tools.jackson.databind.jsontype.deduct;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0436Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();
private static final byte[] LIVE_CAT = VPackWireFixtureTest.hex(
            "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] DEAD_CAT = VPackWireFixtureTest.hex(
            "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] DEAD_CAT_UPPER = VPackWireFixtureTest.hex(
            "14 23 4c 43 41 55 53 45 4f 46 44 45 41 54 48 47 45 4e 54 52 4f 50 59 "
          + "44 4e 41 4d 45 45 46 45 4c 49 58 02");
private static final byte[] AMBIGUOUS_CAT = VPackWireFixtureTest.hex(
            "14 13 44 6e 61 6d 65 45 46 65 6c 69 78 43 61 67 65 32 02");
private static final byte[] LUCKY_CAT = VPackWireFixtureTest.hex(
            "14 1c 44 6e 61 6d 65 45 46 65 6c 69 78 45 61 6e 67 72 79 1a "
          + "45 6c 69 76 65 73 38 03");
private static final byte[] CAT_ARRAY = VPackWireFixtureTest.hex(
            "13 3b "
          + "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02 "
          + "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02 02");
private static final byte[] CAT_MAP = VPackWireFixtureTest.hex(
            "14 1d 44 6c 69 76 65 "
          + "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02 01");
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

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testAmbiguousClasses().
    void testAmbiguousClassesVpack() {
        InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
                () -> VPackMapper.builder().build().readValue(LIVE_CAT, AmbiguousCat436.class));
        assertTrue(error.getMessage().contains("Subtypes"), error::getMessage);
        assertTrue(error.getMessage().contains("have the same signature"), error::getMessage);
        assertTrue(error.getMessage().contains("cannot be uniquely deduced"), error::getMessage);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testAmbiguousProperties().
    void testAmbiguousPropertiesVpack() {
        InvalidTypeIdException error = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(AMBIGUOUS_CAT, Cat436.class));
        assertTrue(error.getMessage().contains("Cannot deduce unique subtype"), error::getMessage);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testArrayInference().
    void testArrayInferenceVpack() throws Exception {
        Cat436[] cats = MAPPER.readValue(CAT_ARRAY, Cat436[].class);
        assertEquals(2, cats.length);
        assertInstanceOf(LiveCat436.class, cats[0]);
        assertInstanceOf(DeadCat436.class, cats[1]);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testCaseInsensitiveInference().
    void testCaseInsensitiveInferenceVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).build();
        DeadCat436 cat = assertInstanceOf(DeadCat436.class,
                mapper.readValue(DEAD_CAT_UPPER, Cat436.class));
        assertEquals("FELIX", cat.name);
        assertEquals("ENTROPY", cat.causeOfDeath);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testContainedInference().
    void testContainedInferenceVpack() throws Exception {
        Box436 box = MAPPER.readValue(BOX_LIVE, Box436.class);
        LiveCat436 live = assertInstanceOf(LiveCat436.class, box.feline);
        assertEquals("Felix", live.name);
        assertTrue(live.angry);

        box = MAPPER.readValue(BOX_DEAD, Box436.class);
        DeadCat436 dead = assertInstanceOf(DeadCat436.class, box.feline);
        assertEquals("entropy", dead.causeOfDeath);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testContainedInferenceOfEmptySubtype().
    void testContainedInferenceOfEmptySubtypeVpack() throws Exception {
        Box436 box = MAPPER.readValue(BOX_EMPTY, Box436.class);
        assertInstanceOf(Fleabag436.class, box.feline);
        box = MAPPER.readValue(BOX_NULL, Box436.class);
        assertNull(box.feline);
        box = MAPPER.readValue(EMPTY_OBJECT, Box436.class);
        assertNull(box.feline);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testDefaultImpl().
    void testDefaultImplVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Cat436.class, CatMixin436.class)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        Cat436 cat = mapper.readValue(AMBIGUOUS_CAT, Cat436.class);
        assertSame(Cat436.class, cat.getClass());
        assertEquals("Felix", cat.name);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testFailOnInvalidSubtype().
    void testFailOnInvalidSubtypeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE).build();
        assertNull(mapper.readValue(AMBIGUOUS_CAT, Cat436.class));
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testIgnoreProperties().
    void testIgnorePropertiesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        LiveCat436 cat = assertInstanceOf(LiveCat436.class,
                mapper.readerFor(Cat436.class).readValue(LUCKY_CAT));
        assertEquals("Felix", cat.name);
        assertTrue(cat.angry);
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testListInference().
    void testListInferenceVpack() throws Exception {
        JavaType listType = MAPPER.getTypeFactory().constructParametricType(List.class, Cat436.class);
        List<Cat436> cats = MAPPER.readValue(CAT_ARRAY, listType);
        assertInstanceOf(LiveCat436.class, cats.get(0));
        assertInstanceOf(DeadCat436.class, cats.get(1));
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testListSerialization().
    void testListSerializationVpack() throws Exception {
        JavaType listType = MAPPER.getTypeFactory().constructParametricType(List.class, Cat436.class);
        List<Cat436> cats = MAPPER.readValue(CAT_ARRAY, listType);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 41 02 "
              + "0b 17 02 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 03 0a "
              + "0b 25 02 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
              + "44 6e 61 6d 65 45 46 65 6c 69 78 03 18 03 1a"),
                MAPPER.writeValueAsBytes(cats));
    }

    // Provenance: SealedTypesWithPolymorphicDeductionTest#testMapInference().
    void testMapInferenceVpack() throws Exception {
        JavaType mapType = MAPPER.getTypeFactory().constructParametricType(
                Map.class, String.class, Cat436.class);
        Map<String, Cat436> cats = MAPPER.readValue(CAT_MAP, mapType);
        assertEquals(1, cats.size());
        assertInstanceOf(LiveCat436.class, cats.entrySet().iterator().next().getValue());
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    sealed interface Feline436 permits Cat436, Fleabag436 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static sealed class Cat436 implements Feline436 permits DeadCat436, LiveCat436 {
        public String name;
    }
static final class DeadCat436 extends Cat436 {
        public String causeOfDeath;
    }
static final class LiveCat436 extends Cat436 {
        public boolean angry;
    }
static final class Fleabag436 implements Feline436 { }
static class Box436 {
        public Feline436 feline;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    sealed interface AmbiguousFeline436 permits AmbiguousCat436, AmbiguousFleabag436 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static sealed class AmbiguousCat436 implements AmbiguousFeline436
            permits AmbiguousDeadCat436, AmbiguousLiveCat436, AmbiguousAnotherLiveCat436 {
        public String name;
    }
static final class AmbiguousDeadCat436 extends AmbiguousCat436 {
        public String causeOfDeath;
    }
static final class AmbiguousLiveCat436 extends AmbiguousCat436 {
        public boolean angry;
    }
static final class AmbiguousAnotherLiveCat436 extends AmbiguousCat436 {
        public boolean angry;
    }
static final class AmbiguousFleabag436 implements AmbiguousFeline436 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = Cat436.class)
    abstract static class CatMixin436 { }

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


    void __invoke_testListInferenceVpack() throws Exception {
        try {
            testListInferenceVpack();
        } finally {
        }
    }


    void __invoke_testListSerializationVpack() throws Exception {
        try {
            testListSerializationVpack();
        } finally {
        }
    }


    void __invoke_testMapInferenceVpack() throws Exception {
        try {
            testMapInferenceVpack();
        } finally {
        }
    }

}
