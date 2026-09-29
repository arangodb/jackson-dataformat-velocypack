package tools.jackson.databind.deser.filter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidNullException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0240F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] VALUES_NULL = VPackWireFixtureTest.hex(
            "14 0e 46 76 61 6c 75 65 73 13 04 18 01 01");
private static final byte[] VALUES_INTS = VPackWireFixtureTest.hex(
            "14 10 46 76 61 6c 75 65 73 13 06 31 18 32 03 01");
private static final byte[] VALUES_STRINGS = VPackWireFixtureTest.hex(
            "14 13 46 76 61 6c 75 65 73 13 09 41 61 18 42 78 79 03 01");
private static final byte[] VALUES_LONGS = VPackWireFixtureTest.hex(
            "14 13 46 76 61 6c 75 65 73 13 09 20 f3 18 29 e7 03 03 01");
private static final byte[] VALUES_BOOLEANS = VPackWireFixtureTest.hex(
            "14 10 46 76 61 6c 75 65 73 13 06 1a 18 1a 03 01");
private static final byte[] VALUES_MAP = VPackWireFixtureTest.hex(
            "14 1c 46 76 61 6c 75 65 73 14 12 41 41 43 66 6f 6f "
          + "41 42 18 41 43 43 62 61 72 03 01");
private static final byte[] VALUE_NULL = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 18 01");
private static final byte[] VALUES_ENUM_MAP_NULL = VPackWireFixtureTest.hex(
            "14 10 46 76 61 6c 75 65 73 14 06 41 42 18 01 01");
private static final byte[] VALUE_EMPTY_STRING = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 40 01");
private static final byte[] SETTER_NULL = VPackWireFixtureTest.hex(
            "14 11 45 76 61 6c 75 65 14 08 43 66 6f 6f 18 01 01");

    // Provenance: NullConversionsForContentTest#testNullsSkipWithArrays.
    void testNullsSkipWithArraysVpack() throws Exception {
        NullContentSkip<Object[]> objects = MAPPER.readValue(VALUES_STRINGS,
                new TypeReference<NullContentSkip<Object[]>>() { });
        assertEquals(2, objects.values.length);
        assertEquals("a", objects.values[0]);
        assertEquals("xy", objects.values[1]);

        NullContentSkip<String[]> strings = MAPPER.readValue(VALUES_STRINGS,
                new TypeReference<NullContentSkip<String[]>>() { });
        assertEquals(2, strings.values.length);
        assertEquals("a", strings.values[0]);
        assertEquals("xy", strings.values[1]);
    }

    // Provenance: NullConversionsForContentTest#testNullsSkipWithCollections.
    void testNullsSkipWithCollectionsVpack() throws Exception {
        NullContentSkip<List<Integer>> integers = MAPPER.readValue(VALUES_INTS,
                new TypeReference<NullContentSkip<List<Integer>>>() { });
        assertEquals(List.of(1, 2), integers.values);

        NullContentSkip<List<String>> strings = MAPPER.readValue(VALUES_STRINGS,
                new TypeReference<NullContentSkip<List<String>>>() { });
        assertEquals(List.of("a", "xy"), strings.values);
    }

    // Provenance: NullConversionsForContentTest#testNullsSkipWithMaps.
    void testNullsSkipWithMapsVpack() throws Exception {
        NullContentSkip<Map<String, String>> map = MAPPER.readValue(VALUES_MAP,
                new TypeReference<NullContentSkip<Map<String, String>>>() { });
        assertEquals(Map.of("A", "foo", "C", "bar"), map.values);

        NullContentSkip<EnumMap<ABC, String>> enumMap = MAPPER.readValue(VALUES_MAP,
                new TypeReference<NullContentSkip<EnumMap<ABC, String>>>() { });
        assertEquals(2, enumMap.values.size());
        assertEquals("foo", enumMap.values.get(ABC.A));
        assertEquals("bar", enumMap.values.get(ABC.C));
    }

    // Provenance: NullConversionsForContentTest#testNullsSkipWithOverrides.
    void testNullsSkipWithOverridesVpack() throws Exception {
        TypeReference<NullContentSkip<List<Long>>> listType =
                new TypeReference<NullContentSkip<List<Long>>>() { };

        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> n.withContentNulls(Nulls.FAIL))
                .build();
        NullContentSkip<List<Long>> result = mapper.readValue(VALUES_NULL, listType);
        assertEquals(0, result.values.size());

        mapper = VPackMapper.builder()
                .withConfigOverride(List.class,
                        o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.FAIL)))
                .build();
        result = mapper.readValue(VALUES_NULL, listType);
        assertEquals(0, result.values.size());
    }

    // Provenance: NullConversionsForContentTest#testNullsSkipWithPrimitiveArrays.
    void testNullsSkipWithPrimitiveArraysVpack() throws Exception {
        NullContentSkip<int[]> ints = MAPPER.readValue(VALUES_INTS,
                new TypeReference<NullContentSkip<int[]>>() { });
        assertEquals(2, ints.values.length);
        assertEquals(1, ints.values[0]);
        assertEquals(2, ints.values[1]);

        NullContentSkip<long[]> longs = MAPPER.readValue(VALUES_LONGS,
                new TypeReference<NullContentSkip<long[]>>() { });
        assertEquals(2, longs.values.length);
        assertEquals(-13L, longs.values[0]);
        assertEquals(999L, longs.values[1]);

        NullContentSkip<boolean[]> booleans = MAPPER.readValue(VALUES_BOOLEANS,
                new TypeReference<NullContentSkip<boolean[]>>() { });
        assertEquals(2, booleans.values.length);
        assertTrue(booleans.values[0]);
        assertTrue(booleans.values[1]);
    }

    // Provenance: NullConversionsForContentTest#testSetterNulls4200.
    void testSetterNulls4200Vpack() {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(SETTER_NULL, SetterWrapper4200.class));
        assertTrue(exception.getMessage().contains("Invalid `null` value"));
    }
static class NullContentSkip<T> {
        @JsonSetter(contentNulls = Nulls.SKIP)
        public T values;
    }
static class NullValueAsEmpty<T> {
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        public T value;
    }
static class NullContentAsEmpty<T> {
        @JsonSetter(contentNulls = Nulls.AS_EMPTY)
        public T values;
    }
static class SetterWrapper4200 {
        private Map<String, String> value;

        @JsonSetter(contentNulls = Nulls.FAIL)
        public void setValue(Map<String, String> value) {
            this.value = value;
        }

        public Map<String, String> getValue() {
            return value;
        }
    }
static class GeneralEmpty<T> {
        T value;

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        public void setValue(T value) {
            this.value = value;
        }
    }
static class Point {
        public int x;
        public int y;
    }
enum ABC { A, B, C }

    void __invoke_testNullsSkipWithArraysVpack() throws Exception {
        try {
            testNullsSkipWithArraysVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkipWithCollectionsVpack() throws Exception {
        try {
            testNullsSkipWithCollectionsVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkipWithMapsVpack() throws Exception {
        try {
            testNullsSkipWithMapsVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkipWithOverridesVpack() throws Exception {
        try {
            testNullsSkipWithOverridesVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkipWithPrimitiveArraysVpack() throws Exception {
        try {
            testNullsSkipWithPrimitiveArraysVpack();
        } finally {
        }
    }


    void __invoke_testSetterNulls4200Vpack() throws Exception {
        try {
            testSetterNulls4200Vpack();
        } finally {
        }
    }

}
