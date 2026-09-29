package tools.jackson.databind.ser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0555Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] STRING_123 = VPackWireFixtureTest.hex("43 31 32 33");
private static final byte[] XYZ = VPackWireFixtureTest.hex("43 78 79 7a");
private static final byte[] MAP_A1 = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 41 31 03");
private static final byte[] MAP_B2 = VPackWireFixtureTest.hex(
            "0b 08 01 41 62 41 32 03");
private static final byte[] DYNAMIC_VALUE = VPackWireFixtureTest.hex(
            "0b 0d 02 41 61 41 61 41 62 41 62 03 07");
private static final byte[] STATIC_VALUE = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 41 61 03");
private static final byte[] NUMBER_13 = VPackWireFixtureTest.hex("28 0d");
private static final byte[] POLYMORPHIC = VPackWireFixtureTest.hex(
            "0b 1a 02 46 62 6f 69 6e 67 6f 46 62 6f 6f 70 73 79 "
          + "45 74 6f 41 64 64 31 03 11");

    void testSimpleMethodJsonValueVpack() throws Exception {
        assertArrayEquals(ABC, MAPPER.writeValueAsBytes(new ValueClass<>("abc")));
        assertArrayEquals(new byte[] { 0x18 },
                MAPPER.writeValueAsBytes(new ValueClass<>(null)));
    }

    void testSimpleFieldJsonValueVpack() throws Exception {
        assertArrayEquals(ABC, MAPPER.writeValueAsBytes(new FieldValueClass<>("abc")));
        assertArrayEquals(new byte[] { 0x18 },
                MAPPER.writeValueAsBytes(new FieldValueClass<>(null)));
    }

    void testJsonValueWithUseSerializerVpack() throws Exception {
        assertArrayEquals(STRING_123,
                MAPPER.writeValueAsBytes(new ToStringValueClass<>(Integer.valueOf(123))));
    }

    void testMixedJsonValueVpack() throws Exception {
        assertArrayEquals(XYZ, MAPPER.writeValueAsBytes(new ToStringValueClass2("xyz")));
    }

    void testValueWithStaticTypeVpack() throws Exception {
        assertArrayEquals(DYNAMIC_VALUE, MAPPER.writeValueAsBytes(new ValueWrapper()));
        ObjectMapper staticMapper = VPackMapper.builder()
                .enable(tools.jackson.databind.MapperFeature.USE_STATIC_TYPING)
                .build();
        assertArrayEquals(STATIC_VALUE, staticMapper.writeValueAsBytes(new ValueWrapper()));
    }

    void testMapWithJsonValueVpack() throws Exception {
        assertArrayEquals(MAP_A1, MAPPER.writeValueAsBytes(new MapBean()));
        assertArrayEquals(MAP_B2, MAPPER.writeValueAsBytes(new MapFieldBean()));
    }

    void testWithListVpack() throws Exception {
        assertArrayEquals(NUMBER_13, MAPPER.writeValueAsBytes(new ListAsNumber()));
    }

    void testPolymorphicSerdeWithDelegateVpack() throws Exception {
        AdditionInterface adder = new AdditionInterfaceImpl(1);
        assertEquals(2, adder.add(1));
        byte[] encoded = MAPPER.writeValueAsBytes(adder);
        assertArrayEquals(POLYMORPHIC, encoded);
        assertEquals(2, MAPPER.readValue(encoded, AdditionInterface.class).add(1));
    }

    void testSimpleStringGetterVpack() {
        assertRawTextUnsupported(new ClassGetter<>("abc"));
    }

    void testSimpleNonStringGetterVpack() {
        assertRawTextUnsupported(new ClassGetter<>(Integer.valueOf(123)));
    }

    void testNullStringGetterVpack() throws Exception {
        Map<String, Object> expected = new HashMap<>();
        expected.put("nonRaw", null);
        expected.put("raw", null);
        expected.put("value", null);
        assertEquals(expected,
                MAPPER.readValue(MAPPER.writeValueAsBytes(new ClassGetter<String>(null)), Map.class));
    }

    void testRawFromMapToTreeVpack() {
        RawValue myType = new RawValue("Jackson");
        Map<String, Object> object = new HashMap<>();
        object.put("key", myType);
        assertThrows(UnsupportedOperationException.class, () -> {
            JsonNode jsonNode = MAPPER.valueToTree(object);
            MAPPER.writeValueAsBytes(jsonNode);
        });
    }
private static void assertRawTextUnsupported(Object value) {
        assertThrows(tools.jackson.databind.DatabindException.class,
                () -> MAPPER.writeValueAsBytes(value));
    }
static class ValueClass<T> {
        final T value;
        ValueClass(T value) { this.value = value; }
        @JsonValue T value() { return value; }
    }
static class FieldValueClass<T> {
        @JsonValue(true) final T value;
        FieldValueClass(T value) { this.value = value; }
    }
static class ToStringValueClass<T> extends ValueClass<T> {
        ToStringValueClass(T value) { super(value); }
        @JsonSerialize(using = ToStringSerializer.class)
        @Override @JsonValue T value() { return super.value(); }
    }
static class ToStringValueClass2 extends ValueClass<String> {
        ToStringValueClass2(String value) { super(value); }
        @JsonProperty int getFoobar() { return 4; }
        public String[] getSomethingElse() { return new String[] { "1", "a" }; }
    }
static class ValueBase { public String a = "a"; }
static class ValueType extends ValueBase { public String b = "b"; }
static class ValueWrapper {
        @JsonValue public ValueBase getX() { return new ValueType(); }
    }
static class MapBean {
        @JsonValue public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("a", "1");
            return map;
        }
    }
static class MapFieldBean {
        @JsonValue Map<String, String> stuff = new HashMap<>();
        { stuff.put("b", "2"); }
    }
static class ListAsNumber extends ArrayList<Integer> {
        @JsonValue public int value() { return 13; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "boingo")
    @JsonSubTypes(@JsonSubTypes.Type(name = "boopsy", value = AdditionInterfaceImpl.class))
    interface AdditionInterface { int add(int in); }
static class AdditionInterfaceImpl implements AdditionInterface {
        private final int toAdd;
        @com.fasterxml.jackson.annotation.JsonCreator
        AdditionInterfaceImpl(@JsonProperty("toAdd") int toAdd) { this.toAdd = toAdd; }
        @JsonProperty public int getToAdd() { return toAdd; }
        @Override public int add(int in) { return in + toAdd; }
    }
@JsonPropertyOrder(alphabetic = true)
    static class ClassGetter<T> {
        protected final T value;
        ClassGetter(T value) { this.value = value; }
        public T getNonRaw() { return value; }
        @JsonProperty("raw") @JsonRawValue public T foobar() { return value; }
        @JsonProperty @JsonRawValue protected T value() { return value; }
    }

    void __invoke_testSimpleMethodJsonValueVpack() throws Exception {
        try {
            testSimpleMethodJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testSimpleFieldJsonValueVpack() throws Exception {
        try {
            testSimpleFieldJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testJsonValueWithUseSerializerVpack() throws Exception {
        try {
            testJsonValueWithUseSerializerVpack();
        } finally {
        }
    }


    void __invoke_testMixedJsonValueVpack() throws Exception {
        try {
            testMixedJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testValueWithStaticTypeVpack() throws Exception {
        try {
            testValueWithStaticTypeVpack();
        } finally {
        }
    }


    void __invoke_testMapWithJsonValueVpack() throws Exception {
        try {
            testMapWithJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testWithListVpack() throws Exception {
        try {
            testWithListVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicSerdeWithDelegateVpack() throws Exception {
        try {
            testPolymorphicSerdeWithDelegateVpack();
        } finally {
        }
    }


    void __invoke_testSimpleStringGetterVpack() throws Exception {
        try {
            testSimpleStringGetterVpack();
        } finally {
        }
    }


    void __invoke_testSimpleNonStringGetterVpack() throws Exception {
        try {
            testSimpleNonStringGetterVpack();
        } finally {
        }
    }


    void __invoke_testNullStringGetterVpack() throws Exception {
        try {
            testNullStringGetterVpack();
        } finally {
        }
    }


    void __invoke_testRawFromMapToTreeVpack() throws Exception {
        try {
            testRawFromMapToTreeVpack();
        } finally {
        }
    }

}
