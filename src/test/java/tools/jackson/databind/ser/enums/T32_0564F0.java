package tools.jackson.databind.ser.enums;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.EnumFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0564F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();
private static final byte[] LEVELS = VPackWireFixtureTest.hex(
            "06 54 03 "
          + "0b 11 01 45 6c 61 62 65 6c 46 6c 65 76 65 6c 31 03 "
          + "0b 11 01 45 6c 61 62 65 6c 46 6c 65 76 65 6c 32 03 "
          + "0b 2c 02 45 6c 61 62 65 6c 46 6c 65 76 65 6c 33 "
          + "48 73 75 62 6c 65 76 65 6c "
          + "0b 11 01 45 6c 61 62 65 6c 46 6c 65 76 65 6c 31 03 03 "
          + "10 03 14 25");
private static final byte[] MAP_B = VPackWireFixtureTest.hex(
            "0b 0f 01 43 6d 61 70 0b 07 01 41 42 33 03 03");
private static final byte[] MAP_b = VPackWireFixtureTest.hex(
            "0b 0f 01 43 6d 61 70 0b 07 01 41 62 33 03 03");
private static final byte[] CUSTOM_KEY = VPackWireFixtureTest.hex(
            "0b 0e 01 45 58 2d 46 4f 4f 43 61 62 63 03");
private static final byte[] JSON_VALUE_KEY = VPackWireFixtureTest.hex(
            "0b 1c 01 45 73 74 75 66 66 0b 12 01 49 6c 6f 6e 67 56 61 6c 75 65 "
          + "43 66 6f 6f 03 03");
private static final byte[] ROOT_FIRST = VPackWireFixtureTest.hex(
            "0b 0b 01 45 46 49 52 53 54 33 03");
private static final byte[] VALUE_SECOND = VPackWireFixtureTest.hex(
            "0b 18 01 46 76 61 6c 75 65 73 0b 0d 01 46 53 45 43 4f 4e 44 28 48 03 03");
private static final byte[] ROOT_CUSTOM = VPackWireFixtureTest.hex(
            "0b 0d 02 41 41 41 31 41 42 41 32 03 07");
private static final byte[] ROOT_CUSTOM_TOSTRING = VPackWireFixtureTest.hex(
            "0b 21 02 4b 41 20 61 73 20 73 74 72 69 6e 67 41 31 "
          + "4b 42 20 61 73 20 73 74 72 69 6e 67 41 32 03 11");
private static final byte[] ROOT_MIXIN = VPackWireFixtureTest.hex(
            "0b 1e 03 47 61 5f 6d 69 78 69 6e 41 31 "
          + "47 62 5f 6d 69 78 69 6e 41 32 41 43 41 33 17 03 0d");
private static final byte[] ROOT_MIXIN_TOSTRING = VPackWireFixtureTest.hex(
            "0b 28 03 47 61 5f 6d 69 78 69 6e 41 31 "
          + "47 62 5f 6d 69 78 69 6e 41 32 "
          + "4b 43 20 61 73 20 73 74 72 69 6e 67 41 33 17 03 0d");
private static final byte[] COLOR_KEY = VPackWireFixtureTest.hex(
            "0b 10 01 43 72 65 64 47 23 66 66 30 30 30 30 03");

    // Provenance: EnumAsFormatObject4564Test#testEnumAsFormatObject().
    void testEnumAsFormatObjectVpack() throws Exception {
        List<Level> levels = List.of(Level.LEVEL1, Level.LEVEL2, Level.LEVEL3);

        assertArrayEquals(LEVELS, MAPPER.writeValueAsBytes(levels));
        List<?> decoded = MAPPER.readValue(LEVELS, List.class);
        assertEquals(3, decoded.size());
        assertEquals(Map.of("label", "level1"), decoded.get(0));
        assertEquals(Map.of("label", "level2"), decoded.get(1));
        assertEquals(Map.of("label", "level3", "sublevel", Map.of("label", "level1")),
                decoded.get(2));
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"label", "sublevel"})
    enum Level {
        LEVEL1("level1"),
        LEVEL2("level2"),
        LEVEL3("level3", LEVEL1);

        public String label;
        public Level sublevel;

        Level(String label) { this.label = label; }
        Level(String label, Level sublevel) {
            this.label = label;
            this.sublevel = sublevel;
        }
    }
static class MapBean {
        public Map<ABCEnum, Integer> map = new LinkedHashMap<>();
        void add(ABCEnum key, int value) { map.put(key, value); }
    }
enum ABCEnum {
        A, B, C;
        @Override public String toString() { return name().toLowerCase(); }
    }
enum MyEnum594 {
        VALUE_WITH_A_REALLY_LONG_NAME_HERE("longValue");
        private final String key;
        MyEnum594(String key) { this.key = key; }
        @JsonValue public String getKey() { return key; }
    }
static class MyStuff594 {
        public Map<MyEnum594, String> stuff = new LinkedHashMap<>();
        MyStuff594() { }
        MyStuff594(String value) { stuff.put(MyEnum594.VALUE_WITH_A_REALLY_LONG_NAME_HERE, value); }
    }
static class MyBean661 {
        private final Map<Foo661, String> foo = new LinkedHashMap<>();
        MyBean661(String value) { foo.put(Foo661.FOO, value); }
        @JsonAnyGetter
        @JsonSerialize(keyUsing = Foo661.Serializer.class)
        public Map<Foo661, String> getFoo() { return foo; }
    }
enum Foo661 {
        FOO;
        static class Serializer extends tools.jackson.databind.ValueSerializer<Foo661> {
            @Override
            public void serialize(Foo661 value, JsonGenerator g,
                    tools.jackson.databind.SerializationContext provider) {
                g.writeName("X-" + value.name());
            }
        }
    }
enum Type { FIRST, SECOND }
static class TypeContainer {
        public Map<Type, Integer> values;
        TypeContainer(Type type, int value) {
            values = new LinkedHashMap<>();
            values.put(type, value);
        }
    }
enum MyEnum2457 {
        A, B() { @Override public void foo() { } };
        public void foo() { }
        @Override public String toString() { return name() + " as string"; }
    }
enum MyEnum2457Base {
        @JsonProperty("a_base") A,
        @JsonProperty("b_base") B() { @Override public void foo() { } },
        C;
        public void foo() { }
        @Override public String toString() { return name() + " as string"; }
    }
enum MyEnum2457Mixin {
        @JsonProperty("a_mixin") A,
        @JsonProperty("b_mixin") B() { @Override public void foo() { } },
        C;
        public void foo() { }
    }
enum Color5432 {
        @JsonProperty("red") RED
    }

    void __invoke_testEnumAsFormatObjectVpack() throws Exception {
        try {
            testEnumAsFormatObjectVpack();
        } finally {
        }
    }

}
