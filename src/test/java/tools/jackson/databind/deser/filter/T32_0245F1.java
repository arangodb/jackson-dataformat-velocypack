package tools.jackson.databind.deser.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0245F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper STRICT_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
private static final byte[] PERSON = VPackWireFixtureTest.hex(
            "14 25 44 6e 61 6d 65 45 61 64 6d 69 6e "
          + "48 70 65 72 73 6f 6e 5f 7a 14 0e 44 6e 61 6d 65 45 77 79 61 74 74 01 02");
private static final byte[] PERSONS = VPackWireFixtureTest.hex(
            "14 33 44 6e 61 6d 65 45 61 64 6d 69 6e "
          + "48 70 65 72 73 6f 6e 5f 7a 13 1c "
          + "14 0d 44 6e 61 6d 65 44 46 6f 6f 72 01 "
          + "14 0c 44 6e 61 6d 65 43 42 61 72 01 02 02");
private static final byte[] JACK_EXT_1755 = VPackWireFixtureTest.hex(
            "14 27 42 69 64 41 31 46 6c 69 6e 6b 65 64 13 18 "
          + "14 15 42 69 64 41 31 48 69 67 6e 6f 72 65 4d 65 43 79 7a 78 02 01 02");
private static final byte[] ITEMS_4417 = VPackWireFixtureTest.hex(
            "14 16 45 69 74 65 6d 73 13 0d "
          + "14 0a 45 69 74 65 6d 73 01 01 01 01");
private static final byte[] ANY_SETTER = VPackWireFixtureTest.hex(
            "14 0b 41 78 41 79 41 61 41 62 02");
private static final byte[] IGNORED_MAP = VPackWireFixtureTest.hex(
            "14 13 41 61 13 04 31 01 41 62 32 41 63 41 78 41 64 19 04");
private static final byte[] IGNORE_UNKNOWN = VPackWireFixtureTest.hex(
            "14 13 41 62 33 41 63 13 05 31 32 02 41 78 0a 41 61 3d 04");
private static final byte[] IMPLICIT_IGNORES = VPackWireFixtureTest.hex(
            "14 0c 41 61 31 41 62 32 41 63 33 03");
private static final byte[] IMPLICIT_IGNORES_UNKNOWN = VPackWireFixtureTest.hex(
            "14 0f 41 61 31 41 62 32 41 63 33 41 64 34 04");
private static final byte[] ISSUE_987 = VPackWireFixtureTest.hex(
            "13 1e 14 1b "
          + "49 61 50 72 6f 70 65 72 74 79 41 78 "
          + "47 75 6e 6b 6e 6f 77 6e 13 04 0a 01 02 01");
private static final byte[] PROPERTY_IGNORAL = VPackWireFixtureTest.hex(
            "14 15 45 76 61 6c 75 65 14 0c 41 79 32 41 78 31 41 7a 33 03 01");
private static final byte[] PROPERTY_IGNORAL_MAP = VPackWireFixtureTest.hex(
            "14 13 46 76 61 6c 75 65 73 14 09 41 78 31 41 79 32 02 01" );

    // Provenance: UnknownPropertyDeserTest#testAnySetterWithFailOnUnknownDisabled.
    void testAnySetterWithFailOnUnknownDisabled() throws Exception {
        IgnoreUnknownAnySetter value = MAPPER.readValue(ANY_SETTER,
                IgnoreUnknownAnySetter.class);
        assertNotNull(value);
        assertEquals(2, value.props.size());
    }

    // Provenance: UnknownPropertyDeserTest#testClassIgnoreWithMap.
    void testClassIgnoreWithMap() throws Exception {
        IgnoreMap result = MAPPER.readValue(IGNORED_MAP, IgnoreMap.class);
        assertEquals(2, result.size());
        Object value = result.get("b");
        assertEquals(Integer.class, value.getClass());
        assertEquals(Integer.valueOf(2), value);
        assertEquals("x", result.get("c"));
        assertFalse(result.containsKey("a"));
        assertFalse(result.containsKey("d"));
    }

    // Provenance: UnknownPropertyDeserTest#testClassWithIgnoreUnknown.
    void testClassWithIgnoreUnknown() throws Exception {
        IgnoreUnknown result = MAPPER.readValue(IGNORE_UNKNOWN, IgnoreUnknown.class);
        assertEquals(-3, result.a);
    }

    // Provenance: UnknownPropertyDeserTest#testClassWithUnknownAndIgnore.
    void testClassWithUnknownAndIgnore() throws Exception {
        ImplicitIgnores result = STRICT_MAPPER.readValue(IMPLICIT_IGNORES, ImplicitIgnores.class);
        assertEquals(3, result.c);
        assertThrows(UnrecognizedPropertyException.class,
                () -> STRICT_MAPPER.readValue(IMPLICIT_IGNORES_UNKNOWN, ImplicitIgnores.class));
    }

    // Provenance: UnknownPropertyDeserTest#testIssue987.
    void testIssue987() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public boolean handleUnknownProperty(DeserializationContext ctxt,
                            JsonParser parser, ValueDeserializer<?> deserializer,
                            Object beanOrClass, String propertyName) {
                        parser.skipChildren();
                        return true;
                    }
                })
                .build();
        List<Bean987> result = mapper.readValue(ISSUE_987,
                mapper.getTypeFactory().constructCollectionType(List.class, Bean987.class));
        assertEquals(1, result.size());
    }

    // Provenance: UnknownPropertyDeserTest#testPropertyIgnoral.
    void testPropertyIgnoral() throws Exception {
        XYZWrapper1 result = MAPPER.readValue(PROPERTY_IGNORAL, XYZWrapper1.class);
        assertEquals(2, result.value.y);
        assertEquals(3, result.value.z);
    }

    // Provenance: UnknownPropertyDeserTest#testPropertyIgnoralForMap.
    void testPropertyIgnoralForMap() throws Exception {
        MapWithoutX result = MAPPER.readValue(PROPERTY_IGNORAL_MAP, MapWithoutX.class);
        assertNotNull(result.values);
        assertEquals(1, result.values.size());
        assertEquals(Integer.valueOf(2), result.values.get("y"));
    }
static class Person {
        public String name;

        @JsonProperty("person_z")
        @JsonIgnoreProperties({"person_z"})
        public Person personZ;
    }
static class Persons {
        public String name;

        @JsonProperty("person_z")
        @JsonIgnoreProperties({"person_z"})
        public java.util.Set<Persons> personZ;
    }
static class JackBase1755 {
        public String id;
    }
static class JackExt extends JackBase1755 {
        public java.math.BigDecimal quantity;
        public String ignoreMe;

        @JsonIgnoreProperties({"ignoreMe"})
        public List<JackExt> linked;

        public List<KeyValue> metadata;
    }
static class KeyValue {
        public String key;
        public String value;
    }
static class Item4417 {
        @JsonIgnoreProperties({"whatever"})
        public List<Item4417> items;
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class IgnoreUnknownAnySetter {
        Map<String, Object> props = new HashMap<>();

        @JsonAnySetter
        public void addProperty(String key, Object value) {
            props.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, Object> getProperties() {
            return props;
        }
    }
@JsonIgnoreProperties({"a", "d"})
    static class IgnoreMap extends HashMap<String, Object> { }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class IgnoreUnknown {
        public int a;
    }
static class ImplicitIgnores {
        @JsonIgnore
        public int a;

        @JsonIgnore
        public void setB(int b) { }

        public int c;
    }
static class XYZWrapper1 {
        @JsonIgnoreProperties({"x"})
        public YZ value;
    }
static class YZ {
        public int y, z;
    }
static class MapWithoutX {
        @JsonIgnoreProperties("x")
        public Map<String, Integer> values;
    }
static class Bean987 {
        public String aProperty;
    }

    void __invoke_testAnySetterWithFailOnUnknownDisabled() throws Exception {
        try {
            testAnySetterWithFailOnUnknownDisabled();
        } finally {
        }
    }


    void __invoke_testClassIgnoreWithMap() throws Exception {
        try {
            testClassIgnoreWithMap();
        } finally {
        }
    }


    void __invoke_testClassWithIgnoreUnknown() throws Exception {
        try {
            testClassWithIgnoreUnknown();
        } finally {
        }
    }


    void __invoke_testClassWithUnknownAndIgnore() throws Exception {
        try {
            testClassWithUnknownAndIgnore();
        } finally {
        }
    }


    void __invoke_testIssue987() throws Exception {
        try {
            testIssue987();
        } finally {
        }
    }


    void __invoke_testPropertyIgnoral() throws Exception {
        try {
            testPropertyIgnoral();
        } finally {
        }
    }


    void __invoke_testPropertyIgnoralForMap() throws Exception {
        try {
            testPropertyIgnoralForMap();
        } finally {
        }
    }

}
