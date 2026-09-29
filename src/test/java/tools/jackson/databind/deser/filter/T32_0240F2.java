package tools.jackson.databind.deser.filter;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0240F2 {
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

    // Provenance: NullConversionsGenericTest#testEmptyStringToNullToEmptyPojo.
    void testEmptyStringToNullToEmptyPojoVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(new TypeReference<GeneralEmpty<Point>>() { })
                .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        GeneralEmpty<Point> result = reader.readValue(VALUE_EMPTY_STRING);
        assertNotNull(result.value);
        assertEquals(0, result.value.x);
        assertEquals(0, result.value.y);
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

    void __invoke_testEmptyStringToNullToEmptyPojoVpack() throws Exception {
        try {
            testEmptyStringToNullToEmptyPojoVpack();
        } finally {
        }
    }

}
