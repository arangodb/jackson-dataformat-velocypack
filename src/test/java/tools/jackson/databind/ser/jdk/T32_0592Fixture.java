package tools.jackson.databind.ser.jdk;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0592Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: MapSerializationTest#testMapSerializer().
    void testMapSerializerVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "4a 7b 61 3d 62 2c 20 63 3d 64 7d"),
                MAPPER.writeValueAsBytes(new PseudoMap("a", "b", "c", "d")));
    }

    // Provenance: MapSerializationTest#testMapKeySetValuesSerialization().
    void testMapKeySetValuesSerializationVpack() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        assertArrayEquals(VPackWireFixtureTest.hex("02 04 41 61"),
                MAPPER.writeValueAsBytes(map.keySet()));
        assertArrayEquals(VPackWireFixtureTest.hex("02 04 41 62"),
                MAPPER.writeValueAsBytes(map.values()));

        map = new TreeMap<>();
        map.put("c", "d");
        assertArrayEquals(VPackWireFixtureTest.hex("02 04 41 63"),
                MAPPER.writeValueAsBytes(map.keySet()));
        assertArrayEquals(VPackWireFixtureTest.hex("02 04 41 64"),
                MAPPER.writeValueAsBytes(map.values()));

        map = new ConcurrentHashMap<>();
        map.put("e", "f");
        assertArrayEquals(VPackWireFixtureTest.hex("02 04 41 65"),
                MAPPER.writeValueAsBytes(map.keySet()));
        assertArrayEquals(VPackWireFixtureTest.hex("02 04 41 66"),
                MAPPER.writeValueAsBytes(map.values()));
    }

    // Provenance: MapSerializationTest#testConcurrentMaps().
    void testConcurrentMapsVpack() throws Exception {
        ObjectMapper sorting = new VPackMapper();
        Map<String, String> input = new ConcurrentSkipListMap<>();
        input.put("x", "y");
        input.put("a", "b");
        Map<?, ?> expected = MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 0d 02 41 61 41 62 41 78 41 79 03 07"), Map.class);
        assertEquals(expected, sorting.readValue(sorting.writer(
                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS).writeValueAsBytes(input), Map.class));

        input = new ConcurrentHashMap<>();
        input.put("x", "y");
        input.put("a", "b");
        assertEquals(expected, sorting.readValue(sorting.writer(
                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS).writeValueAsBytes(input), Map.class));

        input = new Hashtable<>();
        input.put("x", "y");
        input.put("a", "b");
        assertEquals(expected, sorting.readValue(sorting.writer(
                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS).writeValueAsBytes(input), Map.class));
    }

    // Provenance: MapSerializationTest#testMapJsonValueKey47().
    void testMapJsonValueKey47Vpack() throws Exception {
        WatMap input = new WatMap();
        input.put(new Wat("3"), true);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 07 01 41 33 1a 03"), MAPPER.writeValueAsBytes(input));
    }

    // Provenance: MapSerializationTest#testDynamicMapKeys().
    void testDynamicMapKeysVpack() throws Exception {
        Map<Object, Integer> stuff = new LinkedHashMap<>();
        stuff.put(AbcLC.B, 3);
        stuff.put(new UCString("foo"), 4);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 02 41 62 33 43 46 4f 4f 34 06 03"),
                MAPPER.writeValueAsBytes(stuff));
    }

    // Provenance: MapSerializationTest#testMapKeyWithJsonValue().
    void testMapKeyWithJsonValueVpack() throws Exception {
        Map<JsonValue2306Key, String> map = Map.of(
                new JsonValue2306Key("myId"), "value");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 44 6d 79 49 64 45 76 61 6c 75 65 03"),
                MAPPER.writeValueAsBytes(map));
    }

    // Provenance: MapSerializationTest#testKarl().
    void testKarlVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 43 6d 61 70 0b 0a 01 44 4b 61 72 6c 31 03 03"),
                MAPPER.writeValueAsBytes(new KarlBean()));
    }

    // Provenance: MapSerializationTest#testCustomForEnum().
    void testCustomForEnumVpack() throws Exception {
        SimpleModule mod = new SimpleModule("test");
        mod.addKeySerializer(ABCKey.class, new ABCKeySerializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(mod).build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 17 01 45 73 74 75 66 66 0b 0d 01 44 78 78 78 42 43 62 61 72 03 03"),
                mapper.writeValueAsBytes(new ABCMapWrapper()));
    }

    // Provenance: MapSerializationTest#testCustomNullSerializers().
    void testCustomNullSerializersVpack() throws Exception {
        SimpleModule mod = new SimpleModule()
                .setDefaultNullKeySerializer(new NullKeySerializer("NULL-KEY"))
                .setDefaultNullValueSerializer(new NullValueSerializer("NULL"));
        ObjectMapper mapper = VPackMapper.builder().addModule(mod).build();
        Map<String, Integer> input = new HashMap<>();
        input.put(null, 3);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0e 01 48 4e 55 4c 4c 2d 4b 45 59 33 03"),
                mapper.writeValueAsBytes(input));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 0d 03 31 44 4e 55 4c 4c 1a 03 04 09"),
                mapper.writeValueAsBytes(new Object[] { 1, null, true }));
    }

    // Provenance: MapSerializationTest#testCustomEnumInnerMapKey().
    void testCustomEnumInnerMapKeyVpack() throws Exception {
        Map<OuterEnum, Object> outerMap = new HashMap<>();
        Map<ABCKey, Map<String, String>> map = new EnumMap<>(ABCKey.class);
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put("one", "1");
        map.put(ABCKey.A, innerMap);
        outerMap.put(OuterEnum.inner, map);
        SimpleModule mod = new SimpleModule("test")
                .setMixInAnnotation(ABCKey.class, ABCMixin.class)
                .addKeySerializer(ABCKey.class, new ABCKeySerializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(mod).build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1d 01 45 69 6e 6e 65 72 0b 13 01 44 78 78 78 41"
              + "0b 0a 01 43 6f 6e 65 41 31 03 03 03"),
                mapper.writeValueAsBytes(outerMap));
    }

    // Provenance: MapSerializationTest#testClassKey().
    void testClassKeyVpack() throws Exception {
        Map<Class<?>, Integer> map = new LinkedHashMap<>();
        map.put(String.class, 2);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 16 01 50 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67 32 03"),
                MAPPER.writeValueAsBytes(map));
    }

    // Provenance: MapSerializationTest#testMapKeyRecursion1679().
    void testMapKeyRecursion1679Vpack() throws Exception {
        Map<Object, Object> objectMap = new HashMap<>();
        objectMap.put(new Object(), "foo");
        byte[] encoded = MAPPER.writeValueAsBytes(objectMap);
        assertNotNull(encoded);
        assertEquals("foo", MAPPER.readValue(encoded, Map.class).values().iterator().next());
    }
@JsonSerialize(using = PseudoMapSerializer.class)
    static class PseudoMap extends LinkedHashMap<String, String> {
        PseudoMap(String... values) {
            for (int i = 0; i < values.length; i += 2) {
                put(values[i], values[i + 1]);
            }
        }
    }
static class PseudoMapSerializer extends ValueSerializer<Map<String, String>> {
        @Override
        public void serialize(Map<String, String> value, tools.jackson.core.JsonGenerator g,
                tools.jackson.databind.SerializationContext ctxt) {
            g.writeString(value.toString());
        }
    }
static class Wat {
        private final String wat;

        @JsonCreator
        Wat(String wat) { this.wat = wat; }

        @JsonValue
        public String getWat() { return wat; }
    }
static class WatMap extends HashMap<Wat, Boolean> { }
static class UCString {
        private final String value;

        UCString(String value) { this.value = value.toUpperCase(); }

        @JsonValue
        public String asString() { return value; }
    }
enum AbcLC {
        A, B, C;

        @JsonValue
        public String toLC() { return name().toLowerCase(); }
    }
static class JsonValue2306Key {
        @JsonValue
        private final String id;

        JsonValue2306Key(String id) { this.id = id; }
    }
static class KarlSerializer extends ValueSerializer<String> {
        @Override
        public void serialize(String value, tools.jackson.core.JsonGenerator g,
                tools.jackson.databind.SerializationContext ctxt) {
            g.writeName("Karl");
        }
    }
static class KarlBean {
        @JsonSerialize(keyUsing = KarlSerializer.class)
        public Map<String, Integer> map = new HashMap<>();

        KarlBean() { map.put("Not Karl", 1); }
    }
enum OuterEnum { inner }
enum ABCKey { A, B, C }
static class ABCMapWrapper {
        public Map<ABCKey, String> stuff = new HashMap<>();

        ABCMapWrapper() { stuff.put(ABCKey.B, "bar"); }
    }
@JsonSerialize(keyUsing = ABCKeySerializer.class)
    static enum ABCMixin { }
static class ABCKeySerializer extends ValueSerializer<ABCKey> {
        @Override
        public void serialize(ABCKey value, tools.jackson.core.JsonGenerator g,
                tools.jackson.databind.SerializationContext ctxt) {
            g.writeName("xxx" + value);
        }
    }
static class NullKeySerializer extends ValueSerializer<Object> {
        private final String value;

        NullKeySerializer(String value) { this.value = value; }

        @Override
        public void serialize(Object ignored, tools.jackson.core.JsonGenerator g,
                tools.jackson.databind.SerializationContext ctxt) {
            g.writeName(value);
        }
    }
static class NullValueSerializer extends ValueSerializer<Object> {
        private final String value;

        NullValueSerializer(String value) { this.value = value; }

        @Override
        public void serialize(Object ignored, tools.jackson.core.JsonGenerator g,
                tools.jackson.databind.SerializationContext ctxt) {
            g.writeString(value);
        }
    }

    void __invoke_testMapSerializerVpack() throws Exception {
        try {
            testMapSerializerVpack();
        } finally {
        }
    }


    void __invoke_testMapKeySetValuesSerializationVpack() throws Exception {
        try {
            testMapKeySetValuesSerializationVpack();
        } finally {
        }
    }


    void __invoke_testConcurrentMapsVpack() throws Exception {
        try {
            testConcurrentMapsVpack();
        } finally {
        }
    }


    void __invoke_testMapJsonValueKey47Vpack() throws Exception {
        try {
            testMapJsonValueKey47Vpack();
        } finally {
        }
    }


    void __invoke_testDynamicMapKeysVpack() throws Exception {
        try {
            testDynamicMapKeysVpack();
        } finally {
        }
    }


    void __invoke_testMapKeyWithJsonValueVpack() throws Exception {
        try {
            testMapKeyWithJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testKarlVpack() throws Exception {
        try {
            testKarlVpack();
        } finally {
        }
    }


    void __invoke_testCustomForEnumVpack() throws Exception {
        try {
            testCustomForEnumVpack();
        } finally {
        }
    }


    void __invoke_testCustomNullSerializersVpack() throws Exception {
        try {
            testCustomNullSerializersVpack();
        } finally {
        }
    }


    void __invoke_testCustomEnumInnerMapKeyVpack() throws Exception {
        try {
            testCustomEnumInnerMapKeyVpack();
        } finally {
        }
    }


    void __invoke_testClassKeyVpack() throws Exception {
        try {
            testClassKeyVpack();
        } finally {
        }
    }


    void __invoke_testMapKeyRecursion1679Vpack() throws Exception {
        try {
            testMapKeyRecursion1679Vpack();
        } finally {
        }
    }

}
