package tools.jackson.databind.deser.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0245F0 {
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

    // Provenance: RecursiveIgnorePropertiesTest#testRecursiveForDeser.
    void testRecursiveForDeser() throws Exception {
        Person result = MAPPER.readValue(PERSON, Person.class);
        assertEquals("admin", result.name);
        assertNotNull(result.personZ);
        assertEquals("wyatt", result.personZ.name);
    }

    // Provenance: RecursiveIgnorePropertiesTest#testRecursiveWithCollectionDeser.
    void testRecursiveWithCollectionDeser() throws Exception {
        Persons result = MAPPER.readValue(PERSONS, Persons.class);
        assertEquals("admin", result.name);
        assertNotNull(result.personZ);
        assertEquals(2, result.personZ.size());
    }

    // Provenance: RecursiveIgnorePropertiesTest#testRecursiveForSer.
    void testRecursiveForSer() throws Exception {
        Person input = new Person();
        input.name = "Bob";
        Person nested = new Person();
        nested.name = "Bill";
        input.personZ = nested;
        nested.personZ = input;

        assertNotNull(MAPPER.writeValueAsBytes(input));
    }

    // Provenance: RecursiveIgnorePropertiesTest#testRecursiveIgnore1755.
    void testRecursiveIgnore1755() throws Exception {
        JackExt value = MAPPER.readValue(JACK_EXT_1755, JackExt.class);
        assertNotNull(value);
        assertNotNull(value.linked);
        assertEquals(1, value.linked.size());
        assertNull(value.linked.get(0).ignoreMe);
    }

    // Provenance: RecursiveIgnorePropertiesTest#testRecursiveIgnore4417.
    void testRecursiveIgnore4417() throws Exception {
        Item4417 result = MAPPER.readValue(ITEMS_4417, Item4417.class);
        assertEquals(1, result.items.size());
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

    void __invoke_testRecursiveForDeser() throws Exception {
        try {
            testRecursiveForDeser();
        } finally {
        }
    }


    void __invoke_testRecursiveWithCollectionDeser() throws Exception {
        try {
            testRecursiveWithCollectionDeser();
        } finally {
        }
    }


    void __invoke_testRecursiveForSer() throws Exception {
        try {
            testRecursiveForSer();
        } finally {
        }
    }


    void __invoke_testRecursiveIgnore1755() throws Exception {
        try {
            testRecursiveIgnore1755();
        } finally {
        }
    }


    void __invoke_testRecursiveIgnore4417() throws Exception {
        try {
            testRecursiveIgnore4417();
        } finally {
        }
    }

}
