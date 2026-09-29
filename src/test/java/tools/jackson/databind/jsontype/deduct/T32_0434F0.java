package tools.jackson.databind.jsontype.deduct;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0434F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();
private static final byte[] LIVE_CAT = VPackWireFixtureTest.hex(
            "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] DEAD_CAT = VPackWireFixtureTest.hex(
            "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02");
private static final byte[] CAT_ARRAY = VPackWireFixtureTest.hex(
            "13 3b "
          + "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02 "
          + "14 23 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 02 02");
private static final byte[] CAT_ARRAY_SERIALIZED = VPackWireFixtureTest.hex(
            "06 41 02 "
          + "0b 17 02 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 03 0a "
          + "0b 25 02 4c 63 61 75 73 65 4f 66 44 65 61 74 68 47 65 6e 74 72 6f 70 79 "
          + "44 6e 61 6d 65 45 46 65 6c 69 78 03 18 03 1a");
private static final byte[] LIVE_CAT_SERIALIZED = VPackWireFixtureTest.hex(
            "0b 17 02 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 03 0a");
private static final byte[] CAT_MAP = VPackWireFixtureTest.hex(
            "14 1d 44 6c 69 76 65 "
          + "14 15 45 61 6e 67 72 79 1a 44 6e 61 6d 65 45 46 65 6c 69 78 02 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] LOCAL_DATE = VPackWireFixtureTest.hex(
            "0b 15 01 45 76 61 6c 75 65 4a 31 39 38 36 2d 30 31 2d 31 37 03");
private static final byte[] LOCAL_DATE_TIME = VPackWireFixtureTest.hex(
            "0b 28 01 45 76 61 6c 75 65 5d 32 30 31 33 2d 30 38 2d 32 31 54 30 39 3a 32 32 3a "
          + "30 30 2e 30 30 30 30 30 30 30 35 37 03");
private static final byte[] LOCAL_TIME = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 48 30 39 3a 32 32 3a 35 37 03");

    // Provenance: BasicPolymorphicDeductionTest#testListInference().
    void testListInferenceVpack() throws Exception {
        JavaType listOfCats = MAPPER.getTypeFactory().constructParametricType(List.class, Cat434.class);
        List<Cat434> cats = MAPPER.readValue(CAT_ARRAY, listOfCats);
        assertInstanceOf(LiveCat434.class, cats.get(0));
        assertInstanceOf(DeadCat434.class, cats.get(1));
    }

    // Provenance: BasicPolymorphicDeductionTest#testListSerialization().
    void testListSerializationVpack() throws Exception {
        JavaType listOfCats = MAPPER.getTypeFactory().constructParametricType(List.class, Cat434.class);
        List<Cat434> cats = MAPPER.readValue(CAT_ARRAY, listOfCats);
        assertArrayEquals(CAT_ARRAY_SERIALIZED, MAPPER.writeValueAsBytes(cats));
    }

    // Provenance: BasicPolymorphicDeductionTest#testMapInference().
    void testMapInferenceVpack() throws Exception {
        JavaType mapOfCats = MAPPER.getTypeFactory().constructParametricType(
                Map.class, String.class, Cat434.class);
        Map<String, Cat434> cats = MAPPER.readValue(CAT_MAP, mapOfCats);
        assertEquals(1, cats.size());
        assertInstanceOf(LiveCat434.class, cats.entrySet().iterator().next().getValue());
    }

    // Provenance: BasicPolymorphicDeductionTest#testSimpleInference().
    void testSimpleInferenceVpack() throws Exception {
        Cat434 cat = MAPPER.readValue(LIVE_CAT, Cat434.class);
        assertInstanceOf(LiveCat434.class, cat);
        assertSame(LiveCat434.class, cat.getClass());
        assertEquals("Felix", cat.name);
        assertTrue(((LiveCat434) cat).angry);

        cat = MAPPER.readValue(DEAD_CAT, Cat434.class);
        assertInstanceOf(DeadCat434.class, cat);
        assertSame(DeadCat434.class, cat.getClass());
        assertEquals("Felix", cat.name);
        assertEquals("entropy", ((DeadCat434) cat).causeOfDeath);
    }

    // Provenance: BasicPolymorphicDeductionTest#testSimpleInferenceOfEmptySubtype().
    void testSimpleInferenceOfEmptySubtypeVpack() throws Exception {
        assertInstanceOf(Fleabag434.class, MAPPER.readValue(EMPTY_OBJECT, Feline434.class));
    }

    // Provenance: BasicPolymorphicDeductionTest#testSimpleInferenceOfEmptySubtypeDoesntMatchNull().
    void testSimpleInferenceOfEmptySubtypeDoesntMatchNullVpack() throws Exception {
        assertEquals(null, MAPPER.readValue(NULL, Feline434.class));
    }

    // Provenance: BasicPolymorphicDeductionTest#testSimpleSerialization().
    void testSimpleSerializationVpack() throws Exception {
        JavaType listOfCats = MAPPER.getTypeFactory().constructParametricType(List.class, Cat434.class);
        List<Cat434> cats = MAPPER.readValue(CAT_ARRAY, listOfCats);
        assertArrayEquals(LIVE_CAT_SERIALIZED, MAPPER.writeValueAsBytes(cats.get(0)));
    }

    // Provenance: BasicPolymorphicDeductionTest#testWithEnum().
    void testWithEnumVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("41 42"), MAPPER.writeValueAsBytes(Enum434.B));
    }

    // Provenance: BasicPolymorphicDeductionTest#testWithPojoAsJsonValue().
    void testWithPojoAsJsonValueVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("45 76 61 6c 75 65"),
                MAPPER.writeValueAsBytes(new Bean434()));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({ @JsonSubTypes.Type(LiveCat434.class), @JsonSubTypes.Type(DeadCat434.class),
            @JsonSubTypes.Type(Fleabag434.class) })
    interface Feline434 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({ @JsonSubTypes.Type(LiveCat434.class), @JsonSubTypes.Type(DeadCat434.class) })
    static class Cat434 implements Feline434 { public String name; }
static class DeadCat434 extends Cat434 { public String causeOfDeath; }
static class LiveCat434 extends Cat434 { public boolean angry; }
static class Fleabag434 implements Feline434 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static enum Enum434 { A, B }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static class Bean434 {
        @JsonValue
        public String ser = "value";
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    static class Wrapper434 {
        public Object value;

        Wrapper434(Object value) { this.value = value; }
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


    void __invoke_testSimpleInferenceVpack() throws Exception {
        try {
            testSimpleInferenceVpack();
        } finally {
        }
    }


    void __invoke_testSimpleInferenceOfEmptySubtypeVpack() throws Exception {
        try {
            testSimpleInferenceOfEmptySubtypeVpack();
        } finally {
        }
    }


    void __invoke_testSimpleInferenceOfEmptySubtypeDoesntMatchNullVpack() throws Exception {
        try {
            testSimpleInferenceOfEmptySubtypeDoesntMatchNullVpack();
        } finally {
        }
    }


    void __invoke_testSimpleSerializationVpack() throws Exception {
        try {
            testSimpleSerializationVpack();
        } finally {
        }
    }


    void __invoke_testWithEnumVpack() throws Exception {
        try {
            testWithEnumVpack();
        } finally {
        }
    }


    void __invoke_testWithPojoAsJsonValueVpack() throws Exception {
        try {
            testWithPojoAsJsonValueVpack();
        } finally {
        }
    }

}
