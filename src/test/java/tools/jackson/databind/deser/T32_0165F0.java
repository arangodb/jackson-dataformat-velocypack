package tools.jackson.databind.deser;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0165F0 {
private static final byte[] METHOD_BEAN = VPackWireFixtureTest.hex(
            "0b 0a 01 44 69 6e 74 73 33 03");
private static final byte[] ROOT_CUSTOM_KEY = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 28 0d 03");
private static final byte[] UNKNOWN_VALUE = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 33 03");
private static final byte[] CREATOR_PROPERTIES = VPackWireFixtureTest.hex(
            "0b 1b 06 41 61 31 41 62 32 41 63 33 41 64 34 41 65 35 41 66 36 "
          + "03 06 09 0c 0f 12");
private static final byte[] JSON_NODE_PROPERTIES = VPackWireFixtureTest.hex(
            "0b 23 04 44 74 65 73 74 33 48 6e 75 6c 6c 61 62 6c 65 18 "
          + "42 69 64 28 2a 45 76 61 6c 75 65 1a 13 09 03 18");
private static final byte[] IGNORED_PROPERTIES = VPackWireFixtureTest.hex(
            "0b 22 03 44 6e 61 6d 65 43 42 6f 62 45 62 6f 67 75 73 "
          + "02 05 31 32 33 45 64 75 6d 6d 79 28 0d 0c 17 03");
private static final byte[] ANY_SETTER_MAP = VPackWireFixtureTest.hex(
            "0b 23 03 42 69 64 32 44 6e 61 6d 65 43 4a 6f 65 "
          + "44 63 69 74 79 4a 4e 65 77 20 4a 65 72 73 65 79 10 03 07");
private static final byte[] GENERIC_WRAPPER = VPackWireFixtureTest.hex(
            "0b 81 02 4f 6d 79 53 74 72 69 6e 67 47 65 6e 65 72 69 63 "
          + "0b 32 02 58 73 74 61 74 69 63 61 6c 6c 79 4d 61 70 70 65 64 50 72 6f 70 65 72 74 79 "
          + "44 54 65 73 74 4d 74 65 73 74 53 74 72 69 6e 67 4b 65 79 35 03 21 "
          + "50 6d 79 49 6e 74 65 67 65 72 47 65 6e 65 72 69 63 "
          + "0b 29 02 58 73 74 61 74 69 63 61 6c 6c 79 4d 61 70 70 65 64 50 72 6f 70 65 72 74 79 "
          + "45 54 65 73 74 32 43 31 31 31 36 22 03 45 03");
private static final ObjectMapper MAPPER = new VPackMapper();

    void testMethodDeserializer() throws Exception {
        MethodBean result = MAPPER.readValue(METHOD_BEAN, MethodBean.class);
        assertNotNull(result);
        assertNotNull(result.ints);
        assertEquals(1, result.ints.length);
        assertEquals(3, result.ints[0]);
    }

    void testRootValueWithCustomKey() throws Exception {
        MapKeyMap result = MAPPER.readValue(ROOT_CUSTOM_KEY, MapKeyMap.class);
        assertNotNull(result);
        assertEquals(1, result.size());
        Map.Entry<Object, Object> entry = result.entrySet().iterator().next();
        assertEquals(ValueClass.class, entry.getValue().getClass());
        assertEquals(13, ((ValueClass) entry.getValue()).value);
        assertEquals(String[].class, entry.getKey().getClass());
    }
private static void assertIgnored(ObjectMapper mapper) throws Exception {
        Ignored bean = mapper.readValue(IGNORED_PROPERTIES, Ignored.class);
        assertNull(bean.map.get("dummy"));
        assertNull(bean.map.get("bogus"));
        assertEquals("Bob", bean.map.get("name"));
        assertEquals(1, bean.map.size());
    }
@JsonDeserialize(using = ValueDeserializer.class)
    static class ValueClass {
        int value;

        ValueClass(int value, int ignored) {
            this.value = value;
        }
    }
static class MethodBean {
        int[] ints;

        @JsonDeserialize(using = IntsDeserializer.class)
        public void setInts(int[] value) {
            ints = value;
        }
    }
@JsonDeserialize(keyUsing = MapKeyDeserializer.class,
            contentUsing = ValueDeserializer.class)
    static class MapKeyMap extends HashMap<Object, Object> {
        private static final long serialVersionUID = 1L;
    }
static class MapImitatorDisabled extends MapImitator {
        @Override
        @JsonAnySetter(enabled = false)
        void addEntry(String key, Object value) {
            throw new IllegalStateException("Should not get called");
        }
    }
static class MapImitator {
        @JsonAnySetter
        void addEntry(String key, Object value) { }
    }
static class Broken {
        @JsonAnySetter
        void addEntry1(String key, Object value) { }

        @JsonAnySetter
        void addEntry2(String key, Object value) { }
    }
static class AnySetterCreatorBean4639 {
        int b;
        int d;

        @JsonAnySetter
        Map<String, ?> any;

        @JsonCreator
        public AnySetterCreatorBean4639(@JsonProperty("b") int b,
                @JsonProperty("d") int d) {
            this.b = b;
            this.d = d;
        }
    }
static class AnySetterMethodCreatorBean4639 {
        final int b;
        final int d;
        final Map<String, Object> any = new HashMap<>();

        @JsonCreator
        public AnySetterMethodCreatorBean4639(@JsonProperty("b") int b,
                @JsonProperty("d") int d) {
            this.b = b;
            this.d = d;
        }

        @JsonAnySetter
        public void setAny(String name, Object value) {
            any.put(name, value);
        }
    }
@JsonIgnoreProperties("dummy")
    static class Ignored {
        HashMap<String, Object> map = new HashMap<>();

        @JsonIgnore
        public String bogus;

        @JsonAnySetter
        void addEntry(String key, Object value) {
            map.put(key, value);
        }
    }
static class Bean797Base {
        @JsonAnyGetter
        public Map<String, JsonNode> getUndefinedProperties() {
            throw new IllegalStateException("Should not call parent version!");
        }
    }
static class Bean797BaseImpl extends Bean797Base {
        @Override
        public Map<String, JsonNode> getUndefinedProperties() {
            return new HashMap<>();
        }
    }
static class AnySetter3394Bean {
        public int id;

        @JsonAnySetter
        public JsonNode extraData = new ObjectNode(null);
    }
static class JsonAnySetterOnMap {
        public int id;

        @JsonAnySetter
        protected HashMap<String, String> other = new HashMap<>();

        @JsonAnyGetter
        public Map<String, String> any() {
            return other;
        }
    }
static class MyGeneric<T> {
        private String staticallyMappedProperty;
        private Map<T, Integer> dynamicallyMappedProperties = new HashMap<>();

        public String getStaticallyMappedProperty() {
            return staticallyMappedProperty;
        }

        @JsonAnySetter
        public void addDynamicallyMappedProperty(T key, int value) {
            dynamicallyMappedProperties.put(key, value);
        }

        public void setStaticallyMappedProperty(String value) {
            staticallyMappedProperty = value;
        }

        @JsonAnyGetter
        public Map<T, Integer> getDynamicallyMappedProperties() {
            return dynamicallyMappedProperties;
        }
    }
static class MyWrapper {
        private MyGeneric<String> myStringGeneric;
        private MyGeneric<Integer> myIntegerGeneric;

        public MyGeneric<String> getMyStringGeneric() {
            return myStringGeneric;
        }

        public void setMyStringGeneric(MyGeneric<String> value) {
            myStringGeneric = value;
        }

        public MyGeneric<Integer> getMyIntegerGeneric() {
            return myIntegerGeneric;
        }

        public void setMyIntegerGeneric(MyGeneric<Integer> value) {
            myIntegerGeneric = value;
        }
    }
static class ValueDeserializer extends StdDeserializer<ValueClass> {
        ValueDeserializer() {
            super(ValueClass.class);
        }

        @Override
        public ValueClass deserialize(tools.jackson.core.JsonParser parser,
                tools.jackson.databind.DeserializationContext ctxt) {
            int value = parser.getIntValue();
            return new ValueClass(value, value);
        }
    }
static class IntsDeserializer extends StdDeserializer<int[]> {
        IntsDeserializer() {
            super(int[].class);
        }

        @Override
        public int[] deserialize(tools.jackson.core.JsonParser parser,
                tools.jackson.databind.DeserializationContext ctxt) {
            return new int[] { parser.getIntValue() };
        }
    }
static class MapKeyDeserializer extends tools.jackson.databind.KeyDeserializer {
        @Override
        public Object deserializeKey(String key,
                tools.jackson.databind.DeserializationContext ctxt) {
            return new String[] { key };
        }
    }

    void __invoke_testMethodDeserializer() throws Exception {
        try {
            testMethodDeserializer();
        } finally {
        }
    }


    void __invoke_testRootValueWithCustomKey() throws Exception {
        try {
            testRootValueWithCustomKey();
        } finally {
        }
    }

}
