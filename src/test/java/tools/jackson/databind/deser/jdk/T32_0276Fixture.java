package tools.jackson.databind.deser.jdk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Currency;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0276Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] UNTYPED_MAP = VPackWireFixtureTest.hex(
            "14 3e "
          + "46 64 6f 75 62 6c 65 1b 00 00 00 00 00 00 45 40 "
          + "46 73 74 72 69 6e 67 46 73 74 72 69 6e 67 "
          + "47 62 6f 6f 6c 65 61 6e 1a "
          + "44 6c 69 73 74 13 09 45 6c 69 73 74 30 01 "
          + "44 6e 75 6c 6c 18 05");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] EXACT_STRING_INT_MAP = VPackWireFixtureTest.hex(
            "14 11 43 66 6f 6f 28 0d 43 62 61 72 20 d9 40 30 03");
private static final byte[] INT_BOOLEAN_MAP = VPackWireFixtureTest.hex(
            "14 0a 41 31 1a 42 2d 31 19 02");
private static final byte[] EXACT_STRING_STRING_MAP = VPackWireFixtureTest.hex(
            "14 07 41 61 41 62 01");
private static final byte[] GENERIC_STRING_INT_MAP = VPackWireFixtureTest.hex(
            "14 0d 41 61 31 41 62 32 41 63 20 9d 03");
private static final byte[] ENUM_MAP = VPackWireFixtureTest.hex(
            "14 13 44 4b 45 59 31 40 48 57 48 41 54 45 56 45 52 18 02");
private static final byte[] DATE_MAP = VPackWireFixtureTest.hex(
            "14 25 5d 46 72 69 2c 20 30 32 20 4a 61 6e 20 31 39 37 30 "
          + "20 31 30 3a 31 37 3a 33 36 20 55 54 43 40 41 30 18 02");
private static final byte[] CURRENCY_KEY_MAP = VPackWireFixtureTest.hex(
            "14 08 43 55 53 44 34 01");
private static final byte[] CLASS_KEY_MAP = VPackWireFixtureTest.hex(
            "14 18 50 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67 "
          + "43 66 6f 6f 01");
private static final byte[] CONFLICTING_MAP = VPackWireFixtureTest.hex(
            "14 07 41 61 41 62 01");

    // Provenance: MapDeserializationTest#testGenericMap().
    void testGenericMapVpack() throws Exception {
        Map<String, ObjectWrapper> result = MAPPER.readValue(UNTYPED_MAP,
                new TypeReference<Map<String, ObjectWrapper>>() { });
        assertUntyped(result);
    }

    // Provenance: MapDeserializationTest#testFromEmptyString().
    void testFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .build();
        assertNull(mapper.readValue(EMPTY_STRING, Map.class));
    }

    // Provenance: MapDeserializationTest#testExactStringIntMap().
    void testExactStringIntMapVpack() throws Exception {
        Map<String, Integer> result = MAPPER.readValue(EXACT_STRING_INT_MAP,
                new TypeReference<HashMap<String, Integer>>() { });
        assertNotNull(result);
        assertEquals(HashMap.class, result.getClass());
        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(13), result.get("foo"));
        assertEquals(Integer.valueOf(-39), result.get("bar"));
        assertEquals(Integer.valueOf(0), result.get(""));
        assertNull(result.get("foobar"));
        assertNull(result.get(" "));
    }

    // Provenance: MapDeserializationTest#testIntBooleanMap().
    void testIntBooleanMapVpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(INT_BOOLEAN_MAP,
                new TypeReference<HashMap<Integer, Boolean>>() { });
        assertNotNull(result);
        assertEquals(HashMap.class, result.getClass());
        assertEquals(2, result.size());
        assertEquals(Boolean.TRUE, result.get(Integer.valueOf(1)));
        assertEquals(Boolean.FALSE, result.get(Integer.valueOf(-1)));
        assertNull(result.get("foobar"));
        assertNull(result.get(0));
    }

    // Provenance: MapDeserializationTest#testExactStringStringMap().
    void testExactStringStringMapVpack() throws Exception {
        Map<String, String> result = MAPPER.readValue(EXACT_STRING_STRING_MAP,
                new TypeReference<TreeMap<String, String>>() { });
        assertNotNull(result);
        assertEquals(TreeMap.class, result.getClass());
        assertEquals(1, result.size());
        assertEquals("b", result.get("a"));
        assertNull(result.get("b"));
    }

    // Provenance: MapDeserializationTest#testGenericStringIntMap().
    void testGenericStringIntMapVpack() throws Exception {
        Map<String, Integer> result = MAPPER.readValue(GENERIC_STRING_INT_MAP,
                new TypeReference<Map<String, Integer>>() { });
        assertNotNull(result);
        assertInstanceOf(Map.class, result);
        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(-99), result.get("c"));
        assertEquals(Integer.valueOf(2), result.get("b"));
        assertEquals(Integer.valueOf(1), result.get("a"));
        assertNull(result.get(""));
    }

    // Provenance: MapDeserializationTest#testEnumMap().
    void testEnumMapVpack() throws Exception {
        EnumMap<EnumMapKey, String> result = MAPPER.readValue(ENUM_MAP,
                new TypeReference<EnumMap<EnumMapKey, String>>() { });
        assertNotNull(result);
        assertEquals(EnumMap.class, result.getClass());
        assertEquals(2, result.size());
        assertEquals("", result.get(EnumMapKey.KEY1));
        assertTrue(result.containsKey(EnumMapKey.WHATEVER));
        assertNull(result.get(EnumMapKey.WHATEVER));
        assertFalse(result.containsKey(EnumMapKey.KEY2));
        assertNull(result.get(EnumMapKey.KEY2));
    }

    // Provenance: MapDeserializationTest#testDateMap().
    void testDateMapVpack() throws Exception {
        Date date1 = new Date(123456000L);
        DateFormat format = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        HashMap<Date, String> result = MAPPER.readValue(DATE_MAP,
                new TypeReference<HashMap<Date, String>>() { });
        assertNotNull(result);
        assertEquals(HashMap.class, result.getClass());
        assertEquals(2, result.size());
        assertTrue(result.containsKey(date1));
        assertEquals("", result.get(new Date(123456000L)));
        assertTrue(result.containsKey(new Date(0)));
        assertNull(result.get(new Date(0)));
        assertEquals("Fri, 02 Jan 1970 10:17:36 UTC", format.format(date1));
    }

    // Provenance: MapDeserializationTest#testCurrencyKeyMap().
    void testCurrencyKeyMapVpack() throws Exception {
        Map<Currency, Object> result = MAPPER.readValue(CURRENCY_KEY_MAP,
                new TypeReference<Map<Currency, Object>>() { });
        assertNotNull(result);
        assertEquals(1, result.size());
        Object key = result.keySet().iterator().next();
        assertNotNull(key);
        assertEquals(Currency.class, key.getClass());
        assertEquals(Currency.getInstance("USD"), key);
        assertEquals(4, result.get(Currency.getInstance("USD")));
    }

    // Provenance: MapDeserializationTest#testClassKeyMap().
    void testClassKeyMapVpack() throws Exception {
        ClassStringMap result = MAPPER.readValue(CLASS_KEY_MAP, ClassStringMap.class);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("foo", result.get(String.class));
    }

    // Provenance: MapDeserializationTest#testCanDeserializeMap2757().
    void testCanDeserializeMap2757Vpack() throws Exception {
        MyMap2757 input = new MyMap2757();
        input.put("a", "b");
        MyMap2757 result = MAPPER.readValue(CONFLICTING_MAP, MyMap2757.class);
        assertEquals(1, result.size());
        assertEquals("b", result.get("a"));
        assertEquals("b", input.get("a"));
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
enum EnumMapKey {
        KEY1, KEY2, WHATEVER
    }
static class ClassStringMap extends HashMap<Class<?>, String> { }
static class MyMap2757 extends LinkedHashMap<String, String> {
        public MyMap2757() { }

        public void setValue(StringWrapper value) { }
        public void setValue(IntWrapper value) { }

        public long getValue() { return 0L; }
    }
static class StringWrapper { }
static class IntWrapper { }
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

    void __invoke_testGenericMapVpack() throws Exception {
        try {
            testGenericMapVpack();
        } finally {
        }
    }


    void __invoke_testFromEmptyStringVpack() throws Exception {
        try {
            testFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testExactStringIntMapVpack() throws Exception {
        try {
            testExactStringIntMapVpack();
        } finally {
        }
    }


    void __invoke_testIntBooleanMapVpack() throws Exception {
        try {
            testIntBooleanMapVpack();
        } finally {
        }
    }


    void __invoke_testExactStringStringMapVpack() throws Exception {
        try {
            testExactStringStringMapVpack();
        } finally {
        }
    }


    void __invoke_testGenericStringIntMapVpack() throws Exception {
        try {
            testGenericStringIntMapVpack();
        } finally {
        }
    }


    void __invoke_testEnumMapVpack() throws Exception {
        try {
            testEnumMapVpack();
        } finally {
        }
    }


    void __invoke_testDateMapVpack() throws Exception {
        try {
            testDateMapVpack();
        } finally {
        }
    }


    void __invoke_testCurrencyKeyMapVpack() throws Exception {
        try {
            testCurrencyKeyMapVpack();
        } finally {
        }
    }


    void __invoke_testClassKeyMapVpack() throws Exception {
        try {
            testClassKeyMapVpack();
        } finally {
        }
    }


    void __invoke_testCanDeserializeMap2757Vpack() throws Exception {
        try {
            testCanDeserializeMap2757Vpack();
        } finally {
        }
    }

}
