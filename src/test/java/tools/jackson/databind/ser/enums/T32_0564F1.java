package tools.jackson.databind.ser.enums;

import java.util.LinkedHashMap;
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

class T32_0564F1 {
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

    // Provenance: EnumAsMapKeySerializationTest#testMapWithEnumKeys().
    void testMapWithEnumKeysVpack() throws Exception {
        MapBean bean = new MapBean();
        bean.add(ABCEnum.B, 3);

        assertArrayEquals(MAP_B, MAPPER.writeValueAsBytes(bean));
        assertArrayEquals(MAP_b, MAPPER.writer()
                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .writeValueAsBytes(bean));
        assertArrayEquals(MAP_B, MAPPER.writer()
                .with(EnumFeature.WRITE_ENUMS_USING_INDEX)
                .writeValueAsBytes(bean));
    }

    // Provenance: EnumAsMapKeySerializationTest#testCustomEnumMapKeySerializer().
    void testCustomEnumMapKeySerializerVpack() throws Exception {
        assertArrayEquals(CUSTOM_KEY, MAPPER.writeValueAsBytes(new MyBean661("abc")));
    }

    // Provenance: EnumAsMapKeySerializationTest#testJsonValueForEnumMapKeySer().
    void testJsonValueForEnumMapKeySerVpack() throws Exception {
        assertArrayEquals(JSON_VALUE_KEY, MAPPER.writeValueAsBytes(new MyStuff594("foo")));
    }

    // Provenance: EnumAsMapKeySerializationTest#testJsonValueForEnumMapKeyDeser().
    void testJsonValueForEnumMapKeyDeserVpack() throws Exception {
        MyStuff594 result = MAPPER.readerFor(MyStuff594.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(JSON_VALUE_KEY);
        assertEquals("foo", result.stuff.get(MyEnum594.VALUE_WITH_A_REALLY_LONG_NAME_HERE));

        result = MAPPER.readerFor(MyStuff594.class)
                .without(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(JSON_VALUE_KEY);
        assertEquals("foo", result.stuff.get(MyEnum594.VALUE_WITH_A_REALLY_LONG_NAME_HERE));
    }

    // Provenance: EnumAsMapKeySerializationTest#testEnumAsIndexForRootMap().
    void testEnumAsIndexForRootMapVpack() throws Exception {
        Map<Type, Integer> input = Map.of(Type.FIRST, 3);

        assertArrayEquals(ROOT_FIRST, MAPPER.writeValueAsBytes(input));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 07 01 41 30 33 03"),
                MAPPER.writer().with(EnumFeature.WRITE_ENUM_KEYS_USING_INDEX)
                        .writeValueAsBytes(input));
        assertArrayEquals(ROOT_FIRST, MAPPER.writer()
                .with(EnumFeature.WRITE_ENUMS_USING_INDEX)
                .writeValueAsBytes(input));
    }

    // Provenance: EnumAsMapKeySerializationTest#testEnumAsIndexForValueMap().
    void testEnumAsIndexForValueMapVpack() throws Exception {
        TypeContainer input = new TypeContainer(Type.SECOND, 72);

        assertArrayEquals(VALUE_SECOND, MAPPER.writeValueAsBytes(input));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 46 76 61 6c 75 65 73 0b 08 01 41 31 28 48 03 03"),
                MAPPER.writer().with(EnumFeature.WRITE_ENUM_KEYS_USING_INDEX)
                        .writeValueAsBytes(input));
        assertArrayEquals(VALUE_SECOND, MAPPER.writer()
                .with(EnumFeature.WRITE_ENUMS_USING_INDEX)
                .writeValueAsBytes(input));
    }

    // Provenance: EnumAsMapKeySerializationTest#testCustomEnumAsRootMapKey().
    void testCustomEnumAsRootMapKeyVpack() throws Exception {
        Map<MyEnum2457, String> map = new LinkedHashMap<>();
        map.put(MyEnum2457.A, "1");
        map.put(MyEnum2457.B, "2");

        assertArrayEquals(ROOT_CUSTOM, MAPPER.writeValueAsBytes(map));
        assertArrayEquals(ROOT_CUSTOM_TOSTRING, MAPPER.writer()
                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .writeValueAsBytes(map));
    }

    // Provenance: EnumAsMapKeySerializationTest#testCustomEnumAsRootMapKeyMixin().
    void testCustomEnumAsRootMapKeyMixinVpack() throws Exception {
        ObjectMapper mixinMapper = VPackMapper.builder()
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .addMixIn(MyEnum2457Base.class, MyEnum2457Mixin.class)
                .build();
        Map<MyEnum2457Base, String> map = new LinkedHashMap<>();
        map.put(MyEnum2457Base.A, "1");
        map.put(MyEnum2457Base.B, "2");
        map.put(MyEnum2457Base.C, "3");

        assertArrayEquals(ROOT_MIXIN, mixinMapper.writeValueAsBytes(map));
        assertArrayEquals(ROOT_MIXIN_TOSTRING, mixinMapper.writer()
                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .writeValueAsBytes(map));
    }

    // Provenance: EnumAsMapKeySerializationTest#enumKeyShouldSerializeUsingJsonPropertyAndToString().
    void enumKeyShouldSerializeUsingJsonPropertyAndToStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .build();
        Map<Color5432, String> map = Map.of(Color5432.RED, "#ff0000");

        assertArrayEquals(COLOR_KEY, mapper.writeValueAsBytes(map));
    }

    // Provenance: EnumAsMapKeySerializationTest#enumKeyShouldSerializeUsingJsonPropertyAndName().
    void enumKeyShouldSerializeUsingJsonPropertyAndNameVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .build();
        Map<Color5432, String> map = Map.of(Color5432.RED, "#ff0000");

        assertArrayEquals(COLOR_KEY, mapper.writeValueAsBytes(map));
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

    void __invoke_testMapWithEnumKeysVpack() throws Exception {
        try {
            testMapWithEnumKeysVpack();
        } finally {
        }
    }


    void __invoke_testCustomEnumMapKeySerializerVpack() throws Exception {
        try {
            testCustomEnumMapKeySerializerVpack();
        } finally {
        }
    }


    void __invoke_testJsonValueForEnumMapKeySerVpack() throws Exception {
        try {
            testJsonValueForEnumMapKeySerVpack();
        } finally {
        }
    }


    void __invoke_testJsonValueForEnumMapKeyDeserVpack() throws Exception {
        try {
            testJsonValueForEnumMapKeyDeserVpack();
        } finally {
        }
    }


    void __invoke_testEnumAsIndexForRootMapVpack() throws Exception {
        try {
            testEnumAsIndexForRootMapVpack();
        } finally {
        }
    }


    void __invoke_testEnumAsIndexForValueMapVpack() throws Exception {
        try {
            testEnumAsIndexForValueMapVpack();
        } finally {
        }
    }


    void __invoke_testCustomEnumAsRootMapKeyVpack() throws Exception {
        try {
            testCustomEnumAsRootMapKeyVpack();
        } finally {
        }
    }


    void __invoke_testCustomEnumAsRootMapKeyMixinVpack() throws Exception {
        try {
            testCustomEnumAsRootMapKeyMixinVpack();
        } finally {
        }
    }


    void __invoke_enumKeyShouldSerializeUsingJsonPropertyAndToStringVpack() throws Exception {
        try {
            enumKeyShouldSerializeUsingJsonPropertyAndToStringVpack();
        } finally {
        }
    }


    void __invoke_enumKeyShouldSerializeUsingJsonPropertyAndNameVpack() throws Exception {
        try {
            enumKeyShouldSerializeUsingJsonPropertyAndNameVpack();
        } finally {
        }
    }

}
