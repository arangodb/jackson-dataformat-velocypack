package tools.jackson.databind.deser.jdk;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.Base64Variants;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0279F1 {
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

    // Provenance: MapKeyDeserializationTest#testBooleanMapKeyDeserialization().
    void testBooleanMapKeyDeserializationVpack() throws Exception {
        MapWrapper<Boolean, String> trueResult = MAPPER.readValue(BOOLEAN_TRUE_MAP,
                new TypeReference<MapWrapper<Boolean, String>>() { });
        assertEquals(Boolean.TRUE, trueResult.map.keySet().iterator().next());

        MapWrapper<Boolean, String> falseResult = MAPPER.readValue(BOOLEAN_FALSE_MAP,
                new TypeReference<MapWrapper<Boolean, String>>() { });
        assertEquals(Boolean.FALSE, falseResult.map.keySet().iterator().next());
    }

    // Provenance: MapKeyDeserializationTest#testByteMapKeyDeserialization().
    void testByteMapKeyDeserializationVpack() throws Exception {
        MapWrapper<Byte, String> result = MAPPER.readValue(BYTE_MAP,
                new TypeReference<MapWrapper<Byte, String>>() { });
        assertEquals(Byte.valueOf((byte) 13), result.map.keySet().iterator().next());
    }

    // Provenance: MapKeyDeserializationTest#testIntegerMapKeyDeserialization().
    void testIntegerMapKeyDeserializationVpack() throws Exception {
        MapWrapper<Integer, String> result = MAPPER.readValue(INTEGER_MAP,
                new TypeReference<MapWrapper<Integer, String>>() { });
        assertEquals(Integer.valueOf(-3), result.map.keySet().iterator().next());
    }

    // Provenance: MapKeyDeserializationTest#testFloatMapKeyDeserialization().
    void testFloatMapKeyDeserializationVpack() throws Exception {
        MapWrapper<Float, String> result = MAPPER.readValue(FLOAT_MAP,
                new TypeReference<MapWrapper<Float, String>>() { });
        assertEquals(Float.valueOf(3.5f), result.map.keySet().iterator().next());
    }

    // Provenance: MapKeyDeserializationTest#testDoubleMapKeyDeserialization().
    void testDoubleMapKeyDeserializationVpack() throws Exception {
        MapWrapper<Double, String> result = MAPPER.readValue(DOUBLE_MAP,
                new TypeReference<MapWrapper<Double, String>>() { });
        assertEquals(Double.valueOf(0.25), result.map.keySet().iterator().next());
    }

    // Provenance: MapKeyDeserializationTest#testDeserializeKeyViaFactory().
    void testDeserializeKeyViaFactoryVpack() throws Exception {
        Map<FullName, Double> map = MAPPER.readValue(FACTORY_MAP,
                new TypeReference<Map<FullName, Double>>() { });
        Map.Entry<FullName, Double> entry = map.entrySet().iterator().next();
        assertEquals("first", entry.getKey()._firstname);
        assertEquals("last", entry.getKey()._lastname);
        assertEquals(42, entry.getValue().doubleValue(), 0);
    }

    // Provenance: MapKeyDeserializationTest#testByteArrayMapKeyDeserialization().
    void testByteArrayMapKeyDeserializationVpack() throws Exception {
        byte[] expected = new byte[] { 1, 2, 4, 8, 16, 33, 79 };
        assertEquals("AQIECBAhTw==", Base64Variants.MIME.encode(expected));

        MapWrapper<byte[], String> result = MAPPER.readValue(BYTE_ARRAY_MAP,
                new TypeReference<MapWrapper<byte[], String>>() { });
        Map.Entry<byte[], String> entry = result.map.entrySet().iterator().next();
        assertEquals("foobar", entry.getValue());
        assertArrayEquals(expected, entry.getKey());
    }

    // Provenance: MapKeyDeserializationTest#testEnumWithCreatorMapKeyDeserialization().
    void testEnumWithCreatorMapKeyDeserializationVpack() throws Exception {
        Map<TestEnum2725, String> output = MAPPER.readValue(ENUM_MAP,
                new TypeReference<Map<TestEnum2725, String>>() { });
        assertNotNull(output);
        assertEquals(Collections.singletonMap(TestEnum2725.FOO, "Hello"), output);
    }

    // Provenance: MapKeyDeserializationTest#testDeserializeInvalidKey().
    void testDeserializeInvalidKeyVpack() throws Exception {
        InvalidFormatException exception = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(INVALID_KEY_MAP, MAP_TYPE_2158));
        assertTrue(exception.getMessage().contains("Value must be nonempty"));
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

    void __invoke_testBooleanMapKeyDeserializationVpack() throws Exception {
        try {
            testBooleanMapKeyDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testByteMapKeyDeserializationVpack() throws Exception {
        try {
            testByteMapKeyDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testIntegerMapKeyDeserializationVpack() throws Exception {
        try {
            testIntegerMapKeyDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testFloatMapKeyDeserializationVpack() throws Exception {
        try {
            testFloatMapKeyDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testDoubleMapKeyDeserializationVpack() throws Exception {
        try {
            testDoubleMapKeyDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeKeyViaFactoryVpack() throws Exception {
        try {
            testDeserializeKeyViaFactoryVpack();
        } finally {
        }
    }


    void __invoke_testByteArrayMapKeyDeserializationVpack() throws Exception {
        try {
            testByteArrayMapKeyDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithCreatorMapKeyDeserializationVpack() throws Exception {
        try {
            testEnumWithCreatorMapKeyDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeInvalidKeyVpack() throws Exception {
        try {
            testDeserializeInvalidKeyVpack();
        } finally {
        }
    }

}
