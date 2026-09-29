package tools.jackson.databind.format;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0386F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] ENUM_STRING = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 41 42 03");
private static final byte[] ENUM_NUMBER = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 31 03");
private static final byte[] ENUM_NAMING = VPackWireFixtureTest.hex(
            "0b 13 01 4a 6d 61 69 6e 5f 76 61 6c 75 65 43 42 2d 78 03");
private static final byte[] STATE_17 = VPackWireFixtureTest.hex(
            "0b 0c 01 45 73 74 61 74 65 28 11 03");
private static final byte[] STATE_31 = VPackWireFixtureTest.hex(
            "0b 0c 01 45 73 74 61 74 65 28 1f 03");
private static final byte[] STATE_99 = VPackWireFixtureTest.hex(
            "0b 0c 01 45 73 74 61 74 65 28 63 03");
private static final byte[] STATE_ORDINAL_ZERO = VPackWireFixtureTest.hex(
            "0b 0b 01 45 73 74 61 74 65 30 03");
private static final byte[] ENTRY_FOO_BAR = VPackWireFixtureTest.hex(
            "0b 16 01 45 65 6e 74 72 79 "
          + "0b 0c 01 43 66 6f 6f 43 62 61 72 03 03");
private static final byte[] ENTRY_A_B = VPackWireFixtureTest.hex(
            "0b 12 01 45 65 6e 74 72 79 "
          + "0b 08 01 41 61 41 62 03 03");
private static final byte[] ENTRY_A_EMPTY = VPackWireFixtureTest.hex(
            "0b 11 01 45 65 6e 74 72 79 "
          + "0b 07 01 41 61 40 03 03");
private static final byte[] ENTRY_OBJECT = VPackWireFixtureTest.hex(
            "0b 17 02 43 6b 65 79 43 66 6f 6f "
          + "45 76 61 6c 75 65 43 62 61 72 03 0b");

    void testAsNaturalRoundtripVpack() throws Exception {
        BeanWithMapEntry input = new BeanWithMapEntry("foo", "bar");
        assertArrayEquals(ENTRY_FOO_BAR, MAPPER.writeValueAsBytes(input));
        BeanWithMapEntry result = MAPPER.readValue(ENTRY_FOO_BAR, BeanWithMapEntry.class);
        assertEquals("foo", result.entry.getKey());
        assertEquals("bar", result.entry.getValue());
    }

    void testAsObjectRoundtripVpack() throws Exception {
        MapEntryAsObject input = new MapEntryAsObject("foo", "bar");
        assertArrayEquals(ENTRY_OBJECT, MAPPER.writeValueAsBytes(input));
        MapEntryAsObject result = MAPPER.readValue(ENTRY_OBJECT, MapEntryAsObject.class);
        assertEquals("foo", result.getKey());
        assertEquals("bar", result.getValue());
    }

    void testDefaultPOJOOverrideWithPropertyNaturalVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Map.Entry.class,
                        o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.POJO)))
                .build();
        BeanWithMapEntry result = mapper.readValue(ENTRY_FOO_BAR, BeanWithMapEntry.class);
        assertEquals("foo", result.entry.getKey());
        assertEquals("bar", result.entry.getValue());
    }

    void testDefaultShapeOverrideVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Map.Entry.class,
                        o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.POJO)))
                .configure(tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .build();
        Map.Entry<String, String> input = new BeanWithMapEntry("foo", "bar").entry;
        assertArrayEquals(ENTRY_OBJECT, mapper.writeValueAsBytes(input));
    }

    void testDeserFailWithStructureMismatch1419Vpack() throws Exception {
        ObjectMapper strictMapper = VPackMapper.builder()
                .enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        byte[] invalid = VPackWireFixtureTest.hex(
                "0b 1b 01 45 65 6e 74 72 79 "
              + "0b 11 01 46 6e 6f 74 4b 65 79 45 76 61 6c 75 65 03 03");
        UnrecognizedPropertyException e = assertThrows(UnrecognizedPropertyException.class,
                () -> strictMapper.readValue(invalid, BeanWithMapEntryAsPOJO.class));
        assertEquals("notKey", e.getPropertyName());
    }

    void testInclusionVpack() throws Exception {
        assertArrayEquals(ENTRY_A_B,
                MAPPER.writeValueAsBytes(new EmptyEntryWrapper("a", "b")));
        assertArrayEquals(ENTRY_A_B,
                MAPPER.writeValueAsBytes(new EntryWithDefaultWrapper("a", "b")));
        assertArrayEquals(ENTRY_A_B,
                MAPPER.writeValueAsBytes(new EntryWithNullWrapper("a", "b")));

        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new EmptyEntryWrapper("a", "")));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new EntryWithDefaultWrapper("a", "")));
        assertArrayEquals(ENTRY_A_EMPTY,
                MAPPER.writeValueAsBytes(new EntryWithNullWrapper("a", "")));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new EntryWithNullWrapper("a", null)));
    }

    void testInclusionWithReferenceVpack() throws Exception {
        assertArrayEquals(ENTRY_A_B,
                MAPPER.writeValueAsBytes(new EntryWithNonAbsentWrapper("a", "b")));
        assertArrayEquals(ENTRY_A_EMPTY,
                MAPPER.writeValueAsBytes(new EntryWithNonAbsentWrapper("a", "")));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new EntryWithNonAbsentWrapper("a", null)));
    }
enum PoNUM { A, B }
static class PoOverrideAsString {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public PoNUM value = PoNUM.B;
    }
static class PoOverrideAsNumber {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public PoNUM value = PoNUM.B;
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    enum Enum2365 {
        A, B, C;

        public String getMainValue() { return name() + "-x"; }
    }
static class Pojo3580 {
        public PojoStateNum3580 state;
        public Pojo3580() { }
        Pojo3580(PojoStateNum3580 state) { this.state = state; }
    }
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum PojoStateNum3580 {
        OFF(17), ON(31), UNKNOWN(99);

        private final int value;
        PojoStateNum3580(int value) { this.value = value; }
        @JsonValue public int value() { return value; }
    }
static class PojoInt3580 {
        public PojoStateInt3580 state;
        public PojoInt3580() { }
        PojoInt3580(PojoStateInt3580 state) { this.state = state; }
    }
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    enum PojoStateInt3580 {
        OFF(17), ON(31), UNKNOWN(99);

        private final int value;
        PojoStateInt3580(int value) { this.value = value; }
        @JsonValue public int value() { return value; }
    }
static class BeanWithMapEntry {
        @JsonFormat(shape = JsonFormat.Shape.NATURAL)
        public Map.Entry<String, String> entry;

        BeanWithMapEntry() { }
        BeanWithMapEntry(String key, String value) {
            Map<String, String> map = new HashMap<>();
            map.put(key, value);
            entry = map.entrySet().iterator().next();
        }
    }
@JsonFormat(shape = JsonFormat.Shape.POJO)
    @JsonPropertyOrder({ "key", "value" })
    static class MapEntryAsObject implements Map.Entry<String, String> {
        protected String key, value;

        MapEntryAsObject() { }
        MapEntryAsObject(String key, String value) { this.key = key; this.value = value; }
        @Override public String getKey() { return key; }
        @Override public String getValue() { return value; }
        @Override public String setValue(String value) { this.value = value; return value; }
    }
static class EntryWithNullWrapper {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY,
                content = JsonInclude.Include.NON_NULL)
        public Map.Entry<String, String> entry;

        EntryWithNullWrapper(String key, String value) {
            Map<String, String> map = new HashMap<>();
            map.put(key, value);
            entry = map.entrySet().iterator().next();
        }
    }
static class EntryWithDefaultWrapper {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY,
                content = JsonInclude.Include.NON_DEFAULT)
        public Map.Entry<String, String> entry;

        EntryWithDefaultWrapper(String key, String value) {
            Map<String, String> map = new HashMap<>();
            map.put(key, value);
            entry = map.entrySet().iterator().next();
        }
    }
static class EmptyEntryWrapper {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY,
                content = JsonInclude.Include.NON_EMPTY)
        public Map.Entry<String, String> entry;

        EmptyEntryWrapper(String key, String value) {
            Map<String, String> map = new HashMap<>();
            map.put(key, value);
            entry = map.entrySet().iterator().next();
        }
    }
static class EntryWithNonAbsentWrapper {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY,
                content = JsonInclude.Include.NON_ABSENT)
        public Map.Entry<String, AtomicReference<String>> entry;

        EntryWithNonAbsentWrapper(String key, String value) {
            Map<String, AtomicReference<String>> map = new HashMap<>();
            map.put(key, new AtomicReference<>(value));
            entry = map.entrySet().iterator().next();
        }
    }
static class BeanWithMapEntryAsPOJO {
        @JsonFormat(shape = JsonFormat.Shape.POJO)
        public Map.Entry<String, String> entry;
        BeanWithMapEntryAsPOJO() { }
    }

    void __invoke_testAsNaturalRoundtripVpack() throws Exception {
        try {
            testAsNaturalRoundtripVpack();
        } finally {
        }
    }


    void __invoke_testAsObjectRoundtripVpack() throws Exception {
        try {
            testAsObjectRoundtripVpack();
        } finally {
        }
    }


    void __invoke_testDefaultPOJOOverrideWithPropertyNaturalVpack() throws Exception {
        try {
            testDefaultPOJOOverrideWithPropertyNaturalVpack();
        } finally {
        }
    }


    void __invoke_testDefaultShapeOverrideVpack() throws Exception {
        try {
            testDefaultShapeOverrideVpack();
        } finally {
        }
    }


    void __invoke_testDeserFailWithStructureMismatch1419Vpack() throws Exception {
        try {
            testDeserFailWithStructureMismatch1419Vpack();
        } finally {
        }
    }


    void __invoke_testInclusionVpack() throws Exception {
        try {
            testInclusionVpack();
        } finally {
        }
    }


    void __invoke_testInclusionWithReferenceVpack() throws Exception {
        try {
            testInclusionWithReferenceVpack();
        } finally {
        }
    }

}
