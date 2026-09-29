package tools.jackson.databind.deser;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0165F1 {
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

    void testAnySetterDisable() throws Exception {
        UnrecognizedPropertyException exception = assertThrows(
                UnrecognizedPropertyException.class,
                () -> MAPPER.readerFor(MapImitatorDisabled.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(UNKNOWN_VALUE));
        assertTrue(exception.getMessage().contains("Unrecognized property \"value\""));
    }

    void testAnySetterFieldWithCreator4639() throws Exception {
        AnySetterCreatorBean4639 bean = MAPPER.readValue(
                CREATOR_PROPERTIES, AnySetterCreatorBean4639.class);
        assertEquals(2, bean.b);
        assertEquals(4, bean.d);
        assertEquals(Map.of("a", 1, "c", 3, "e", 5, "f", 6), bean.any);
    }

    void testAnySetterMethodWithCreator4639() throws Exception {
        AnySetterMethodCreatorBean4639 bean = MAPPER.readValue(
                CREATOR_PROPERTIES, AnySetterMethodCreatorBean4639.class);
        assertEquals(2, bean.b);
        assertEquals(4, bean.d);
        assertEquals(Map.of("a", 1, "c", 3, "e", 5, "f", 6), bean.any);
    }

    void testAnySetterWithJsonNode() throws Exception {
        AnySetter3394Bean bean = MAPPER.readValue(JSON_NODE_PROPERTIES,
                AnySetter3394Bean.class);
        assertEquals(42, bean.id);
        assertNotNull(bean.extraData);
        assertEquals(3, bean.extraData.get("test").asInt());
        assertTrue(bean.extraData.get("nullable").isNull());
        assertTrue(bean.extraData.get("value").asBoolean());
    }

    void testBrokenWithDoubleAnnotations() {
        InvalidDefinitionException exception = assertThrows(
                InvalidDefinitionException.class,
                () -> MAPPER.readValue(UNKNOWN_VALUE, Broken.class));
        assertTrue(exception.getMessage().contains("Multiple 'any-setter' methods"));
    }

    void testGenericAnySetter() throws Exception {
        MyWrapper deserialized = MAPPER.readValue(GENERIC_WRAPPER, MyWrapper.class);
        MyGeneric<String> strings = deserialized.getMyStringGeneric();
        MyGeneric<Integer> integers = deserialized.getMyIntegerGeneric();

        assertNotNull(strings);
        assertEquals("Test", strings.getStaticallyMappedProperty());
        assertEquals(Map.of("testStringKey", 5), strings.getDynamicallyMappedProperties());
        assertEquals(String.class, strings.getDynamicallyMappedProperties().keySet()
                .iterator().next().getClass());

        assertNotNull(integers);
        assertEquals("Test2", integers.getStaticallyMappedProperty());
        assertEquals(Map.of(111, 6), integers.getDynamicallyMappedProperties());
        assertEquals(Integer.class, integers.getDynamicallyMappedProperties().keySet()
                .iterator().next().getClass());
    }

    void testIgnored() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        assertIgnored(mapper);
    }

    void testIgnoredPart2() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        assertIgnored(mapper);
    }

    void testIssue797() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new Bean797BaseImpl()));
    }

    void testJsonAnySetterOnMap() throws Exception {
        JsonAnySetterOnMap result = MAPPER.readValue(ANY_SETTER_MAP,
                JsonAnySetterOnMap.class);
        assertEquals(2, result.id);
        assertEquals("Joe", result.other.get("name"));
        assertEquals("New Jersey", result.other.get("city"));
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

    void __invoke_testAnySetterDisable() throws Exception {
        try {
            testAnySetterDisable();
        } finally {
        }
    }


    void __invoke_testAnySetterFieldWithCreator4639() throws Exception {
        try {
            testAnySetterFieldWithCreator4639();
        } finally {
        }
    }


    void __invoke_testAnySetterMethodWithCreator4639() throws Exception {
        try {
            testAnySetterMethodWithCreator4639();
        } finally {
        }
    }


    void __invoke_testAnySetterWithJsonNode() throws Exception {
        try {
            testAnySetterWithJsonNode();
        } finally {
        }
    }


    void __invoke_testBrokenWithDoubleAnnotations() throws Exception {
        try {
            testBrokenWithDoubleAnnotations();
        } finally {
        }
    }


    void __invoke_testGenericAnySetter() throws Exception {
        try {
            testGenericAnySetter();
        } finally {
        }
    }


    void __invoke_testIgnored() throws Exception {
        try {
            testIgnored();
        } finally {
        }
    }


    void __invoke_testIgnoredPart2() throws Exception {
        try {
            testIgnoredPart2();
        } finally {
        }
    }


    void __invoke_testIssue797() throws Exception {
        try {
            testIssue797();
        } finally {
        }
    }


    void __invoke_testJsonAnySetterOnMap() throws Exception {
        try {
            testJsonAnySetterOnMap();
        } finally {
        }
    }

}
