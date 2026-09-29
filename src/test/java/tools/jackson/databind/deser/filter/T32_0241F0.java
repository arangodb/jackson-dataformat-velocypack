package tools.jackson.databind.deser.filter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0241F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] VALUE_NULL = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 18 01");
private static final byte[] VALUE_EMPTY_STRING = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 40 01");
private static final byte[] NAME_COMPUTER = VPackWireFixtureTest.hex(
            "14 11 44 6e 61 6d 65 48 43 6f 6d 70 75 74 65 72 01");
private static final byte[] NAME_AND_PRICES_NULL = VPackWireFixtureTest.hex(
            "14 19 44 6e 61 6d 65 48 43 6f 6d 70 75 74 65 72 "
          + "46 70 72 69 63 65 73 18 02");
private static final byte[] NO_NULLS_FOO_NULLS_OK_NULL = VPackWireFixtureTest.hex(
            "14 18 47 6e 6f 4e 75 6c 6c 73 43 66 6f 6f "
          + "47 6e 75 6c 6c 73 4f 6b 18 02");
private static final byte[] NO_NULLS_NULL = VPackWireFixtureTest.hex(
            "14 0c 47 6e 6f 4e 75 6c 6c 73 18 01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NAME_NULL = VPackWireFixtureTest.hex(
            "14 09 44 6e 61 6d 65 18 01");
private static final byte[] NULL_AS_EMPTY_FOO_NULLS_OK_NULL = VPackWireFixtureTest.hex(
            "14 1c 4b 6e 75 6c 6c 41 73 45 6d 70 74 79 43 66 6f 6f "
          + "47 6e 75 6c 6c 73 4f 6b 18 02");
private static final byte[] NULL_AS_EMPTY_NULL = VPackWireFixtureTest.hex(
            "14 10 4b 6e 75 6c 6c 41 73 45 6d 70 74 79 18 01");
private static final byte[] NULLS_OK_NULL = VPackWireFixtureTest.hex(
            "14 0c 47 6e 75 6c 6c 73 4f 6b 18 01");
private static final byte[] NUMBER_THREE = VPackWireFixtureTest.hex(
            "14 10 46 6e 75 6d 62 65 72 45 54 48 52 45 45 01");

    // Provenance: NullConversionsGenericTest#testNullsToEmptyPojo.
    void testNullsToEmptyPojoVpack() throws Exception {
        GeneralEmpty<Point> result = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<Point>>() { });
        assertNotNull(result.value);
        assertEquals(0, result.value.x);
        assertEquals(0, result.value.y);

        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(VALUE_NULL, NoCtorWrapper.class));
        assertTrue(exception.getMessage().contains("empty"));
    }

    // Provenance: NullConversionsGenericTest#testNullsToEmptyCollection.
    void testNullsToEmptyCollectionVpack() throws Exception {
        GeneralEmpty<List<String>> strings = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<List<String>>>() { });
        assertNotNull(strings.value);
        assertEquals(0, strings.value.size());

        GeneralEmpty<List<Integer>> integers = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<List<Integer>>>() { });
        assertNotNull(integers.value);
        assertEquals(0, integers.value.size());
    }

    // Provenance: NullConversionsGenericTest#testNullsToEmptyMap.
    void testNullsToEmptyMapVpack() throws Exception {
        GeneralEmpty<Map<String, String>> result = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<Map<String, String>>>() { });
        assertNotNull(result.value);
        assertEquals(0, result.value.size());
    }

    // Provenance: NullConversionsGenericTest#testNullsToEmptyArrays.
    void testNullsToEmptyArraysVpack() throws Exception {
        GeneralEmpty<Object[]> objects = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<Object[]>>() { });
        assertNotNull(objects.value);
        assertEquals(0, objects.value.length);

        GeneralEmpty<String[]> strings = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<String[]>>() { });
        assertNotNull(strings.value);
        assertEquals(0, strings.value.length);

        GeneralEmpty<int[]> ints = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<int[]>>() { });
        assertNotNull(ints.value);
        assertEquals(0, ints.value.length);

        GeneralEmpty<double[]> doubles = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<double[]>>() { });
        assertNotNull(doubles.value);
        assertEquals(0, doubles.value.length);

        GeneralEmpty<boolean[]> booleans = MAPPER.readValue(VALUE_NULL,
                new TypeReference<GeneralEmpty<boolean[]>>() { });
        assertNotNull(booleans.value);
        assertEquals(0, booleans.value.length);
    }
static class GeneralEmpty<T> {
        T value;

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        public void setValue(T value) {
            this.value = value;
        }
    }
static class NoCtorWrapper {
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        public NoCtorPOJO value;
    }
static class NoCtorPOJO {
        NoCtorPOJO(boolean value) { }
    }
static class Point {
        public int x;
        public int y;
    }
static class Issue3645BeanA {
        String name;
        Collection<Integer> prices;

        @JsonCreator
        Issue3645BeanA(@JsonProperty("name") String name,
                @JsonProperty("prices") @JsonSetter(nulls = Nulls.AS_EMPTY)
                Collection<Integer> prices) {
            this.name = name;
            this.prices = prices;
        }
    }
static class NullFail {
        public String nullsOk = "a";

        @JsonSetter(nulls = Nulls.FAIL)
        public String noNulls = "b";
    }
static class NullFailCtor {
        String value;

        @JsonCreator
        NullFailCtor(@JsonSetter(nulls = Nulls.FAIL) @JsonProperty("noNulls") String value) {
            this.value = value;
        }
    }
static class NullsForString {
        String n = "foo";

        public void setName(String value) { n = value; }
        public String getName() { return n; }
    }
static class NullAsEmpty {
        public String nullsOk = "a";

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        public String nullAsEmpty = "b";
    }
static class NullAsEmptyCtor {
        String nullsOk;
        String nullAsEmpty;

        @JsonCreator
        NullAsEmptyCtor(@JsonProperty("nullsOk") String nullsOk,
                @JsonSetter(nulls = Nulls.AS_EMPTY)
                @JsonProperty("nullAsEmpty") String nullAsEmpty) {
            this.nullsOk = nullsOk;
            this.nullAsEmpty = nullAsEmpty;
        }
    }
static class NullSkipField {
        public String nullsOk = "a";

        @JsonSetter(nulls = Nulls.SKIP)
        public String noNulls = "b";
    }
enum NUMS2015 { ONE, TWO }
static class Pojo2015 {
        @JsonSetter(value = "number", nulls = Nulls.SKIP)
        NUMS2015 number = NUMS2015.TWO;
    }

    void __invoke_testNullsToEmptyPojoVpack() throws Exception {
        try {
            testNullsToEmptyPojoVpack();
        } finally {
        }
    }


    void __invoke_testNullsToEmptyCollectionVpack() throws Exception {
        try {
            testNullsToEmptyCollectionVpack();
        } finally {
        }
    }


    void __invoke_testNullsToEmptyMapVpack() throws Exception {
        try {
            testNullsToEmptyMapVpack();
        } finally {
        }
    }


    void __invoke_testNullsToEmptyArraysVpack() throws Exception {
        try {
            testNullsToEmptyArraysVpack();
        } finally {
        }
    }

}
