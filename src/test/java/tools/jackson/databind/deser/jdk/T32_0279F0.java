package tools.jackson.databind.deser.jdk;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0279F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] SIMPLE_STRING_INT_ENTRY = VPackWireFixtureTest.hex(
            "14 09 43 66 6f 6f 28 2a 01");
private static final byte[] STRING_STRING_ENTRY = VPackWireFixtureTest.hex(
            "14 0d 43 6b 65 79 45 76 61 6c 75 65 01");
private static final byte[] ROUND_TRIP_ENTRY = VPackWireFixtureTest.hex(
            "14 0a 44 74 65 73 74 28 7b 01");
private static final byte[] BOOLEAN_TRUE_MAP = VPackWireFixtureTest.hex(
            "14 16 43 6d 61 70 14 0f 44 74 72 75 65 46 66 6f 6f 62 61 72 01 01");
private static final byte[] BOOLEAN_FALSE_MAP = VPackWireFixtureTest.hex(
            "14 17 43 6d 61 70 14 10 45 66 61 6c 73 65 46 66 6f 6f 62 61 72 01 01");
private static final byte[] BYTE_MAP = VPackWireFixtureTest.hex(
            "14 14 43 6d 61 70 14 0d 42 31 33 46 66 6f 6f 62 61 72 01 01");
private static final byte[] INTEGER_MAP = VPackWireFixtureTest.hex(
            "14 14 43 6d 61 70 14 0d 42 2d 33 46 66 6f 6f 62 61 72 01 01");
private static final byte[] FLOAT_MAP = VPackWireFixtureTest.hex(
            "14 15 43 6d 61 70 14 0e 43 33 2e 35 46 66 6f 6f 62 61 72 01 01");
private static final byte[] DOUBLE_MAP = VPackWireFixtureTest.hex(
            "14 16 43 6d 61 70 14 0f 44 30 2e 32 35 46 66 6f 6f 62 61 72 01 01");
private static final byte[] FACTORY_MAP = VPackWireFixtureTest.hex(
            "14 10 4a 66 69 72 73 74 2e 6c 61 73 74 28 2a 01");
private static final byte[] BYTE_ARRAY_MAP = VPackWireFixtureTest.hex(
            "14 1e 43 6d 61 70 14 17 4c 41 51 49 45 43 42 41 68 54 77 3d 3d "
          + "46 66 6f 6f 62 61 72 01 01");
private static final byte[] ENUM_MAP = VPackWireFixtureTest.hex(
            "14 0b 41 31 45 48 65 6c 6c 6f 01");
private static final byte[] INVALID_KEY_MAP = VPackWireFixtureTest.hex(
            "14 05 40 30 01");

    // Provenance: MapEntryDeserializationTest#testSimpleStringIntEntry().
    void testSimpleStringIntEntryVpack() throws Exception {
        Map.Entry<String, Integer> result = MAPPER.readValue(SIMPLE_STRING_INT_ENTRY,
                new TypeReference<Map.Entry<String, Integer>>() { });
        assertNotNull(result);
        assertEquals("foo", result.getKey());
        assertEquals(Integer.valueOf(42), result.getValue());
    }

    // Provenance: MapEntryDeserializationTest#testStringStringEntry().
    void testStringStringEntryVpack() throws Exception {
        Map.Entry<String, String> result = MAPPER.readValue(STRING_STRING_ENTRY,
                new TypeReference<Map.Entry<String, String>>() { });
        assertNotNull(result);
        assertEquals("key", result.getKey());
        assertEquals("value", result.getValue());
    }

    // Provenance: MapEntryDeserializationTest#testRoundTrip().
    void testRoundTripVpack() throws Exception {
        Map.Entry<String, Integer> literal = MAPPER.readValue(ROUND_TRIP_ENTRY,
                new TypeReference<Map.Entry<String, Integer>>() { });
        assertEquals("test", literal.getKey());
        assertEquals(Integer.valueOf(123), literal.getValue());

        Map.Entry<String, Integer> orig = new java.util.AbstractMap.SimpleEntry<>("test", 123);
        Map.Entry<String, Integer> result = MAPPER.readValue(MAPPER.writeValueAsBytes(orig),
                new TypeReference<Map.Entry<String, Integer>>() { });
        assertNotNull(result);
        assertEquals(orig.getKey(), result.getKey());
        assertEquals(orig.getValue(), result.getValue());
    }
private static final TypeReference<Map<DummyDto2158, Integer>> MAP_TYPE_2158 =
            new TypeReference<Map<DummyDto2158, Integer>>() { };
static class MapWrapper<K, V> {
        public Map<K, V> map;
    }
static class FullName {
        String _firstname, _lastname;

        private FullName(String firstname, String lastname) {
            _firstname = firstname;
            _lastname = lastname;
        }

        @JsonCreator
        public static FullName valueOf(String value) {
            String[] split = value.split("\\.");
            return new FullName(split[0], split[1]);
        }

        @JsonValue
        @Override
        public String toString() {
            return _firstname + "." + _lastname;
        }
    }
enum TestEnum2725 {
        FOO(1);

        private final int i;

        TestEnum2725(int i) {
            this.i = i;
        }

        @JsonValue
        public int getI() {
            return i;
        }

        @JsonCreator
        public static TestEnum2725 getByIntegerId(final Integer id) {
            return id == FOO.i ? FOO : null;
        }

        @JsonCreator
        public static TestEnum2725 getByStringId(final String id) {
            return Integer.parseInt(id) == FOO.i ? FOO : null;
        }
    }
private static final class DummyDto2158 {
        @JsonValue
        private final String value;

        private DummyDto2158(String value) {
            this.value = value;
        }

        @JsonCreator
        static DummyDto2158 fromValue(String value) {
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Value must be nonempty");
            }
            return new DummyDto2158(value.toLowerCase(java.util.Locale.ROOT));
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof DummyDto2158 other && other.value.equals(value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    void __invoke_testSimpleStringIntEntryVpack() throws Exception {
        try {
            testSimpleStringIntEntryVpack();
        } finally {
        }
    }


    void __invoke_testStringStringEntryVpack() throws Exception {
        try {
            testStringStringEntryVpack();
        } finally {
        }
    }


    void __invoke_testRoundTripVpack() throws Exception {
        try {
            testRoundTripVpack();
        } finally {
        }
    }

}
