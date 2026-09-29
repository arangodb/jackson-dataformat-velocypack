package tools.jackson.databind.deser.jdk;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0277Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] UNTYPED_MAP = VPackWireFixtureTest.hex(
            "14 07 41 61 41 78 01");
private static final byte[] UNTYPED_NESTED_MAP = VPackWireFixtureTest.hex(
            "14 15 41 61 13 10 14 07 41 61 41 62 01 45 76 61 6c 75 65 02 01");
private static final byte[] UNTYPED_THREE_FIELD_MAP = VPackWireFixtureTest.hex(
            "14 3f 44 76 61 72 31 44 76 61 6c 31 "
          + "44 76 61 72 32 44 76 61 6c 32 "
          + "47 73 75 62 76 61 72 73 13 20 "
          + "14 17 47 73 75 62 76 61 72 31 47 73 75 62 76 61 72 32 "
          + "41 78 41 79 02 14 06 41 61 31 01 02 03");
private static final byte[] SPECIAL_MAP = VPackWireFixtureTest.hex(
            "14 3e "
          + "46 64 6f 75 62 6c 65 1b 00 00 00 00 00 00 45 40 "
          + "46 73 74 72 69 6e 67 46 73 74 72 69 6e 67 "
          + "47 62 6f 6f 6c 65 61 6e 1a "
          + "44 6c 69 73 74 13 09 45 6c 69 73 74 30 01 "
          + "44 6e 75 6c 6c 18 05");
private static final byte[] MAP_WITH_NULL = VPackWireFixtureTest.hex(
            "14 08 43 6b 65 79 18 01");
private static final byte[] MAP_WITH_FALSE = VPackWireFixtureTest.hex(
            "14 08 43 6b 65 79 19 01");
private static final byte[] ARRAY_AS_MAP = VPackWireFixtureTest.hex(
            "13 05 31 32 02");
private static final byte[] BOOLEAN = VPackWireFixtureTest.hex("1a");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] ENUM_KEY_MAP = VPackWireFixtureTest.hex(
            "14 11 44 4b 45 59 32 48 57 48 41 54 45 56 45 52 01");
private static final byte[] UUID_KEY_MAP = VPackWireFixtureTest.hex(
            "14 29 64 33 38 35 38 66 36 32 32 2d 33 30 61 63 2d 33 63 39 31 "
          + "2d 39 66 33 30 2d 30 63 36 36 34 33 31 32 63 36 33 66 34 01");
private static final byte[] CREATOR_KEY = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] CREATOR_KEY_MAP = VPackWireFixtureTest.hex(
            "14 08 43 66 6f 6f 33 01");
private static final byte[] CHAR_SEQUENCE_KEY_MAP = VPackWireFixtureTest.hex(
            "14 07 41 61 41 62 01");
private static final byte[] NO_CTOR_MAP = VPackWireFixtureTest.hex(
            "14 06 41 61 33 01");
private static final byte[] CUSTOM_MAP_VALUE = VPackWireFixtureTest.hex(
            "43 78 79 7a");

    // Provenance: MapDeserializationTest#testUntypedMap2().
    void testUntypedMap2Vpack() throws Exception {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> result = MAPPER.readValue(UNTYPED_MAP, HashMap.class);
        assertNotNull(result);
        assertInstanceOf(Map.class, result);
        assertEquals(1, result.size());
        assertEquals("x", result.get("a"));
    }

    // Provenance: MapDeserializationTest#testUntypedMap3().
    void testUntypedMap3Vpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(UNTYPED_NESTED_MAP, Map.class);
        assertInstanceOf(Map.class, result);
        assertEquals(1, result.size());
        Object value = result.get("a");
        assertNotNull(value);
        Collection<?> list = (Collection<?>) value;
        assertEquals(2, list.size());

        result = MAPPER.readValue(UNTYPED_THREE_FIELD_MAP, Map.class);
        assertInstanceOf(Map.class, result);
        assertEquals(3, result.size());
    }

    // Provenance: MapDeserializationTest#testSpecialMap().
    void testSpecialMapVpack() throws Exception {
        ObjectWrapperMap result = MAPPER.readValue(SPECIAL_MAP, ObjectWrapperMap.class);
        assertNotNull(result);
        assertUntyped(result);
    }

    // Provenance: MapDeserializationTest#testMapUpdate().
    void testMapUpdateVpack() throws Exception {
        Map<String, String> map = new HashMap<>();
        Object result = MAPPER.readerFor(Map.class)
                .withValueToUpdate(map)
                .readValue(EMPTY_OBJECT);
        assertSame(map, result);
        assertEquals(0, map.size());

        result = MAPPER.readerFor(Map.class)
                .withValueToUpdate(map)
                .readValue(MAP_WITH_NULL);
        assertSame(map, result);
        assertEquals(1, map.size());
        assertEquals(java.util.Collections.singletonMap("key", null), map);

        result = MAPPER.readerFor(Map.class)
                .withValueToUpdate(map)
                .readValue(MAP_WITH_FALSE);
        assertSame(map, result);
        assertEquals(1, map.size());
        assertEquals(java.util.Collections.singletonMap("key", Boolean.FALSE), map);
    }

    // Provenance: MapDeserializationTest#testMapWithEnums().
    void testMapWithEnumsVpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(ENUM_KEY_MAP,
                new TypeReference<Map<EnumKey, EnumKey>>() { });
        assertNotNull(result);
        assertInstanceOf(Map.class, result);
        assertEquals(1, result.size());
        assertEquals(EnumKey.WHATEVER, result.get(EnumKey.KEY2));
        assertNull(result.get(EnumKey.WHATEVER));
        assertNull(result.get(EnumKey.KEY1));
    }

    // Provenance: MapDeserializationTest#testUUIDKeyMap().
    void testUUIDKeyMapVpack() throws Exception {
        UUID key = UUID.nameUUIDFromBytes("foobar".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Map<UUID, Object> result = MAPPER.readValue(UUID_KEY_MAP,
                new TypeReference<Map<UUID, Object>>() { });
        assertNotNull(result);
        assertEquals(1, result.size());
        Object actualKey = result.keySet().iterator().next();
        assertNotNull(actualKey);
        assertEquals(UUID.class, actualKey.getClass());
        assertEquals(key, actualKey);
        assertEquals(4, result.get(key));
    }

    // Provenance: MapDeserializationTest#testKeyWithCreator().
    void testKeyWithCreatorVpack() throws Exception {
        KeyType key = MAPPER.readValue(CREATOR_KEY, KeyType.class);
        assertEquals("abc", key.value);

        Map<KeyType, Integer> map = MAPPER.readValue(CREATOR_KEY_MAP,
                new TypeReference<Map<KeyType, Integer>>() { });
        assertEquals(1, map.size());
        key = map.keySet().iterator().next();
        assertEquals("foo", key.value);
    }

    // Provenance: MapDeserializationTest#testcharSequenceKeyMap().
    void testCharSequenceKeyMapVpack() throws Exception {
        Map<CharSequence, String> result = MAPPER.readValue(CHAR_SEQUENCE_KEY_MAP,
                new TypeReference<Map<CharSequence, String>>() { });
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("b", result.get("a"));
    }

    // Provenance: MapDeserializationTest#testMapWithDeserializer().
    void testMapWithDeserializerVpack() throws Exception {
        CustomMap result = MAPPER.readValue(CUSTOM_MAP_VALUE, CustomMap.class);
        assertEquals(1, result.size());
        assertEquals("xyz", result.get("x"));
    }

    // Provenance: MapDeserializationTest#testMapError().
    void testMapErrorVpack() throws Exception {
        TypeReference<?> type = new TypeReference<Map<String, String>>() { };
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(ARRAY_AS_MAP, type));
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(BOOLEAN, type));

        Map<String, String> map = new HashMap<>();
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readerFor(type).withValueToUpdate(map).readValue(BOOLEAN));
    }

    // Provenance: MapDeserializationTest#testNoCtorMap().
    void testNoCtorMapVpack() throws Exception {
        assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(NO_CTOR_MAP, BrokenMap.class));
    }
private static void assertUntyped(Map<String, ObjectWrapper> map) {
        ObjectWrapper value = map.get("double");
        assertNotNull(value);
        assertEquals(Double.valueOf(42), value.getObject());
        assertEquals("string", map.get("string").getObject());
        assertEquals(Boolean.TRUE, map.get("boolean").getObject());
        assertEquals(List.of("list0"), map.get("list").getObject());
        assertTrue(map.containsKey("null"));
        assertNull(map.get("null"));
        assertEquals(5, map.size());
    }
enum EnumKey {
        KEY1, KEY2, WHATEVER
    }
static class ObjectWrapperMap extends HashMap<String, ObjectWrapper> { }
static class ObjectWrapper {
        final Object object;

        protected ObjectWrapper(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static ObjectWrapper jsonValue(Object object) {
            return new ObjectWrapper(object);
        }
    }
@JsonDeserialize(using = CustomMapDeserializer.class)
    static class CustomMap extends LinkedHashMap<String, String> { }
static class CustomMapDeserializer extends StdDeserializer<CustomMap> {
        CustomMapDeserializer() {
            super(CustomMap.class);
        }

        @Override
        public CustomMap deserialize(JsonParser parser, DeserializationContext ctxt) {
            CustomMap result = new CustomMap();
            result.put("x", parser.getString());
            return result;
        }
    }
static class BrokenMap extends HashMap<Object, Object> {
        BrokenMap(Object ignored) {
            super();
        }
    }
static class KeyType {
        protected String value;

        private KeyType(String value, boolean ignored) {
            this.value = value;
        }

        @JsonCreator
        public static KeyType create(String value) {
            return new KeyType(value, true);
        }
    }

    void __invoke_testUntypedMap2Vpack() throws Exception {
        try {
            testUntypedMap2Vpack();
        } finally {
        }
    }


    void __invoke_testUntypedMap3Vpack() throws Exception {
        try {
            testUntypedMap3Vpack();
        } finally {
        }
    }


    void __invoke_testSpecialMapVpack() throws Exception {
        try {
            testSpecialMapVpack();
        } finally {
        }
    }


    void __invoke_testMapUpdateVpack() throws Exception {
        try {
            testMapUpdateVpack();
        } finally {
        }
    }


    void __invoke_testMapWithEnumsVpack() throws Exception {
        try {
            testMapWithEnumsVpack();
        } finally {
        }
    }


    void __invoke_testUUIDKeyMapVpack() throws Exception {
        try {
            testUUIDKeyMapVpack();
        } finally {
        }
    }


    void __invoke_testKeyWithCreatorVpack() throws Exception {
        try {
            testKeyWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testCharSequenceKeyMapVpack() throws Exception {
        try {
            testCharSequenceKeyMapVpack();
        } finally {
        }
    }


    void __invoke_testMapWithDeserializerVpack() throws Exception {
        try {
            testMapWithDeserializerVpack();
        } finally {
        }
    }


    void __invoke_testMapErrorVpack() throws Exception {
        try {
            testMapErrorVpack();
        } finally {
        }
    }


    void __invoke_testNoCtorMapVpack() throws Exception {
        try {
            testNoCtorMapVpack();
        } finally {
        }
    }

}
