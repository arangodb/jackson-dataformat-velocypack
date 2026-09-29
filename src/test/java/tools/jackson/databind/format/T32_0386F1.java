package tools.jackson.databind.format;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0386F1 {
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

    void enumNumberFormatShape3580Vpack() throws Exception {
        assertArrayEquals(STATE_17,
                MAPPER.writeValueAsBytes(new Pojo3580(PojoStateNum3580.OFF)));
        assertArrayEquals(STATE_31,
                MAPPER.writeValueAsBytes(new Pojo3580(PojoStateNum3580.ON)));
        assertArrayEquals(STATE_99,
                MAPPER.writeValueAsBytes(new Pojo3580(PojoStateNum3580.UNKNOWN)));

        assertEquals(PojoStateNum3580.OFF,
                MAPPER.readValue(STATE_17, Pojo3580.class).state);
        assertEquals(PojoStateNum3580.ON,
                MAPPER.readValue(STATE_31, Pojo3580.class).state);
        assertEquals(PojoStateNum3580.UNKNOWN,
                MAPPER.readValue(STATE_99, Pojo3580.class).state);
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(STATE_ORDINAL_ZERO, PojoStateNum3580.class));
    }

    void enumNumberIntFormatShape3580Vpack() throws Exception {
        assertArrayEquals(STATE_17,
                MAPPER.writeValueAsBytes(new PojoInt3580(PojoStateInt3580.OFF)));
        assertArrayEquals(STATE_31,
                MAPPER.writeValueAsBytes(new PojoInt3580(PojoStateInt3580.ON)));
        assertArrayEquals(STATE_99,
                MAPPER.writeValueAsBytes(new PojoInt3580(PojoStateInt3580.UNKNOWN)));

        assertEquals(PojoStateInt3580.OFF,
                MAPPER.readValue(STATE_17, PojoInt3580.class).state);
        assertEquals(PojoStateInt3580.ON,
                MAPPER.readValue(STATE_31, PojoInt3580.class).state);
        assertEquals(PojoStateInt3580.UNKNOWN,
                MAPPER.readValue(STATE_99, PojoInt3580.class).state);
        assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(STATE_ORDINAL_ZERO, PojoInt3580.class));
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

    void __invoke_enumNumberFormatShape3580Vpack() throws Exception {
        try {
            enumNumberFormatShape3580Vpack();
        } finally {
        }
    }


    void __invoke_enumNumberIntFormatShape3580Vpack() throws Exception {
        try {
            enumNumberIntFormatShape3580Vpack();
        } finally {
        }
    }

}
