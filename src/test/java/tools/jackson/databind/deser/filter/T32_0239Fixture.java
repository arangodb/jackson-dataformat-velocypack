package tools.jackson.databind.deser.filter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidNullException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0239Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] VALUES_NULL = VPackWireFixtureTest.hex(
            "14 0e 46 76 61 6c 75 65 73 13 04 18 01 01");
private static final byte[] NULLS_OK_LIST_NULL = VPackWireFixtureTest.hex(
            "14 0f 47 6e 75 6c 6c 73 4f 6b 13 04 18 01 01");
private static final byte[] NO_NULLS_LIST_NULL = VPackWireFixtureTest.hex(
            "14 0f 47 6e 6f 4e 75 6c 6c 73 13 04 18 01 01");
private static final byte[] NO_NULLS_MAP_NULL = VPackWireFixtureTest.hex(
            "14 11 47 6e 6f 4e 75 6c 6c 73 14 06 41 41 18 01 01");
private static final byte[] NO_NULLS_ENUM_MAP_NULL = VPackWireFixtureTest.hex(
            "14 11 47 6e 6f 4e 75 6c 6c 73 14 06 41 41 18 01 01");
private static final byte[] VALUES_ENUM_MAP_NULL = VPackWireFixtureTest.hex(
            "14 10 46 76 61 6c 75 65 73 14 06 41 41 18 01 01");
private static final byte[] DELEGATING_NULL = VPackWireFixtureTest.hex(
            "14 08 43 66 6f 6f 18 01");

    // Provenance: NullConversionsForContentTest#testFailOnNullFromDefaults.
    void testFailOnNullFromDefaultsVpack() throws Exception {
        TypeReference<NullContentUndefined<List<String>>> listType =
                new TypeReference<NullContentUndefined<List<String>>>() { };

        NullContentUndefined<List<String>> result = MAPPER.readValue(VALUES_NULL, listType);
        assertNotNull(result.values);
        assertEquals(1, result.values.size());
        assertNull(result.values.get(0));

        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> n.withContentNulls(Nulls.FAIL))
                .build();
        ObjectMapper failByDefault = mapper;
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> failByDefault.readValue(VALUES_NULL, listType));
        assertTrue(exception.getMessage().contains("property \"values\""));
        assertEquals(String.class, exception.getTargetType());

        mapper = VPackMapper.builder()
                .withConfigOverride(List.class,
                        o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.FAIL)))
                .build();
        ObjectMapper failByType = mapper;
        exception = assertThrows(InvalidNullException.class,
                () -> failByType.readValue(VALUES_NULL, listType));
        assertTrue(exception.getMessage().contains("property \"values\""));
        assertEquals(String.class, exception.getTargetType());
    }

    // Provenance: NullConversionsForContentTest#testFailOnNullWithCollections.
    void testFailOnNullWithCollectionsVpack() throws Exception {
        TypeReference<NullContentFail<List<Integer>>> integerType =
                new TypeReference<NullContentFail<List<Integer>>>() { };
        NullContentFail<List<Integer>> nullable = MAPPER.readValue(
                NULLS_OK_LIST_NULL, integerType);
        assertNotNull(nullable.nullsOk);
        assertEquals(1, nullable.nullsOk.size());
        assertNull(nullable.nullsOk.get(0));

        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_LIST_NULL, integerType));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(Integer.class, exception.getTargetType());

        TypeReference<NullContentFail<List<String>>> stringType =
                new TypeReference<NullContentFail<List<String>>>() { };
        exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_LIST_NULL, stringType));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(String.class, exception.getTargetType());
    }

    // Provenance: NullConversionsForContentTest#testFailOnNullWithArrays.
    void testFailOnNullWithArraysVpack() throws Exception {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_LIST_NULL,
                        new TypeReference<NullContentFail<Object[]>>() { }));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(Object.class, exception.getTargetType());

        exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_LIST_NULL,
                        new TypeReference<NullContentFail<String[]>>() { }));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(String.class, exception.getTargetType());
    }

    // Provenance: NullConversionsForContentTest#testFailOnNullWithPrimitiveArrays.
    void testFailOnNullWithPrimitiveArraysVpack() throws Exception {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_LIST_NULL,
                        new TypeReference<NullContentFail<boolean[]>>() { }));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(Boolean.TYPE, exception.getTargetType());

        exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_LIST_NULL,
                        new TypeReference<NullContentFail<int[]>>() { }));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(Integer.TYPE, exception.getTargetType());

        exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_LIST_NULL,
                        new TypeReference<NullContentFail<double[]>>() { }));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(Double.TYPE, exception.getTargetType());
    }

    // Provenance: NullConversionsForContentTest#testDelegatingCreatorNulls4200.
    void testDelegatingCreatorNulls4200Vpack() {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(DELEGATING_NULL, DelegatingWrapper4200.class));
        assertTrue(exception.getMessage().contains("Invalid `null` value"));
    }

    // Provenance: NullConversionsForContentTest#testFailOnNullWithMaps.
    void testFailOnNullWithMapsVpack() throws Exception {
        TypeReference<NullContentFail<Map<String, String>>> mapType =
                new TypeReference<NullContentFail<Map<String, String>>>() { };
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_MAP_NULL, mapType));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(String.class, exception.getTargetType());

        TypeReference<NullContentFail<EnumMap<ABC, String>>> enumMapType =
                new TypeReference<NullContentFail<EnumMap<ABC, String>>>() { };
        exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(NO_NULLS_ENUM_MAP_NULL, enumMapType));
        assertTrue(exception.getMessage().contains("property \"noNulls\""));
        assertEquals(String.class, exception.getTargetType());
    }

    // Provenance: NullConversionsForContentTest#testNullsAsEmptyWithCollections.
    void testNullsAsEmptyWithCollectionsVpack() throws Exception {
        TypeReference<NullContentAsEmpty<List<Integer>>> integerType =
                new TypeReference<NullContentAsEmpty<List<Integer>>>() { };
        NullContentAsEmpty<List<Integer>> integers = MAPPER.readValue(VALUES_NULL, integerType);
        assertEquals(1, integers.values.size());
        assertEquals(Integer.valueOf(0), integers.values.get(0));

        TypeReference<NullContentAsEmpty<List<String>>> stringType =
                new TypeReference<NullContentAsEmpty<List<String>>>() { };
        NullContentAsEmpty<List<String>> strings = MAPPER.readValue(VALUES_NULL, stringType);
        assertEquals(1, strings.values.size());
        assertEquals("", strings.values.get(0));
    }

    // Provenance: NullConversionsForContentTest#testNullsAsEmptyUsingDefaults.
    void testNullsAsEmptyUsingDefaultsVpack() throws Exception {
        TypeReference<NullContentUndefined<List<Integer>>> listType =
                new TypeReference<NullContentUndefined<List<Integer>>>() { };

        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> n.withContentNulls(Nulls.AS_EMPTY))
                .build();
        NullContentUndefined<List<Integer>> result = mapper.readValue(VALUES_NULL, listType);
        assertEquals(1, result.values.size());
        assertEquals(Integer.valueOf(0), result.values.get(0));

        mapper = VPackMapper.builder()
                .withConfigOverride(List.class,
                        o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY)))
                .build();
        result = mapper.readValue(VALUES_NULL, listType);
        assertEquals(1, result.values.size());
        assertEquals(Integer.valueOf(0), result.values.get(0));
    }

    // Provenance: NullConversionsForContentTest#testNullsAsEmptyWithArrays.
    void testNullsAsEmptyWithArraysVpack() throws Exception {
        NullContentAsEmpty<String[]> result = MAPPER.readValue(VALUES_NULL,
                new TypeReference<NullContentAsEmpty<String[]>>() { });
        assertEquals(1, result.values.length);
        assertEquals("", result.values[0]);
    }

    // Provenance: NullConversionsForContentTest#testNullsAsEmptyWithMaps.
    void testNullsAsEmptyWithMapsVpack() throws Exception {
        TypeReference<NullContentAsEmpty<Map<String, String>>> mapType =
                new TypeReference<NullContentAsEmpty<Map<String, String>>>() { };
        NullContentAsEmpty<Map<String, String>> map = MAPPER.readValue(
                VPackWireFixtureTest.hex("14 10 46 76 61 6c 75 65 73 14 06 41 41 18 01 01"),
                mapType);
        assertEquals(1, map.values.size());
        assertEquals("A", map.values.entrySet().iterator().next().getKey());
        assertEquals("", map.values.entrySet().iterator().next().getValue());

        TypeReference<NullContentAsEmpty<EnumMap<ABC, String>>> enumMapType =
                new TypeReference<NullContentAsEmpty<EnumMap<ABC, String>>>() { };
        NullContentAsEmpty<EnumMap<ABC, String>> enumMap = MAPPER.readValue(
                VALUES_ENUM_MAP_NULL, enumMapType);
        assertEquals(1, enumMap.values.size());
        assertEquals(ABC.A, enumMap.values.entrySet().iterator().next().getKey());
        assertEquals("", enumMap.values.entrySet().iterator().next().getValue());
    }

    // Provenance: NullConversionsForContentTest#testNullsAsEmptyWithPrimitiveArrays.
    void testNullsAsEmptyWithPrimitiveArraysVpack() throws Exception {
        ObjectReader reader = MAPPER.reader()
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

        NullContentAsEmpty<int[]> ints = reader.forType(
                new TypeReference<NullContentAsEmpty<int[]>>() { }).readValue(VALUES_NULL);
        assertEquals(1, ints.values.length);
        assertEquals(0, ints.values[0]);

        NullContentAsEmpty<long[]> longs = reader.forType(
                new TypeReference<NullContentAsEmpty<long[]>>() { }).readValue(VALUES_NULL);
        assertEquals(1, longs.values.length);
        assertEquals(0L, longs.values[0]);

        NullContentAsEmpty<boolean[]> booleans = reader.forType(
                new TypeReference<NullContentAsEmpty<boolean[]>>() { }).readValue(VALUES_NULL);
        assertEquals(1, booleans.values.length);
        assertFalse(booleans.values[0]);
    }

    // Provenance: NullConversionsForContentTest#testNullsSkipUsingDefaults.
    void testNullsSkipUsingDefaultsVpack() throws Exception {
        TypeReference<NullContentUndefined<List<Long>>> listType =
                new TypeReference<NullContentUndefined<List<Long>>>() { };

        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> n.withContentNulls(Nulls.SKIP))
                .build();
        NullContentUndefined<List<Long>> result = mapper.readValue(VALUES_NULL, listType);
        assertEquals(0, result.values.size());

        mapper = VPackMapper.builder()
                .withConfigOverride(List.class,
                        o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.SKIP)))
                .build();
        result = mapper.readValue(VALUES_NULL, listType);
        assertEquals(0, result.values.size());
    }
static class NullContentFail<T> {
        public T nullsOk;

        @JsonSetter(contentNulls = Nulls.FAIL)
        public T noNulls;
    }
static class NullContentAsEmpty<T> {
        @JsonSetter(contentNulls = Nulls.AS_EMPTY)
        public T values;
    }
static class NullContentUndefined<T> {
        @JsonSetter
        public T values;
    }
static class DelegatingWrapper4200 {
        private final Map<String, String> value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        DelegatingWrapper4200(@JsonSetter(contentNulls = Nulls.FAIL)
                Map<String, String> value) {
            this.value = value;
        }

        public Map<String, String> getValue() {
            return value;
        }
    }
enum ABC { A }

    void __invoke_testFailOnNullFromDefaultsVpack() throws Exception {
        try {
            testFailOnNullFromDefaultsVpack();
        } finally {
        }
    }


    void __invoke_testFailOnNullWithCollectionsVpack() throws Exception {
        try {
            testFailOnNullWithCollectionsVpack();
        } finally {
        }
    }


    void __invoke_testFailOnNullWithArraysVpack() throws Exception {
        try {
            testFailOnNullWithArraysVpack();
        } finally {
        }
    }


    void __invoke_testFailOnNullWithPrimitiveArraysVpack() throws Exception {
        try {
            testFailOnNullWithPrimitiveArraysVpack();
        } finally {
        }
    }


    void __invoke_testDelegatingCreatorNulls4200Vpack() throws Exception {
        try {
            testDelegatingCreatorNulls4200Vpack();
        } finally {
        }
    }


    void __invoke_testFailOnNullWithMapsVpack() throws Exception {
        try {
            testFailOnNullWithMapsVpack();
        } finally {
        }
    }


    void __invoke_testNullsAsEmptyWithCollectionsVpack() throws Exception {
        try {
            testNullsAsEmptyWithCollectionsVpack();
        } finally {
        }
    }


    void __invoke_testNullsAsEmptyUsingDefaultsVpack() throws Exception {
        try {
            testNullsAsEmptyUsingDefaultsVpack();
        } finally {
        }
    }


    void __invoke_testNullsAsEmptyWithArraysVpack() throws Exception {
        try {
            testNullsAsEmptyWithArraysVpack();
        } finally {
        }
    }


    void __invoke_testNullsAsEmptyWithMapsVpack() throws Exception {
        try {
            testNullsAsEmptyWithMapsVpack();
        } finally {
        }
    }


    void __invoke_testNullsAsEmptyWithPrimitiveArraysVpack() throws Exception {
        try {
            testNullsAsEmptyWithPrimitiveArraysVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkipUsingDefaultsVpack() throws Exception {
        try {
            testNullsSkipUsingDefaultsVpack();
        } finally {
        }
    }

}
