package tools.jackson.databind.ser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.util.NameTransformer;
import tools.jackson.databind.type.MapType;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0545F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] NUMBER_OBJECT = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 28 2a 03");
private static final byte[] CURRENT_VALUE = VPackWireFixtureTest.hex(
            "0b 19 01 44 70 72 6f 70 4f 49 73 73 75 65 36 33 31 42 65 61 6e 2f 34 32 03");
private static final byte[] ONLY_B = VPackWireFixtureTest.hex(
            "0b 08 01 41 62 41 62 03");
private static final byte[] A_THEN_B = VPackWireFixtureTest.hex(
            "0b 0d 02 41 61 41 61 41 62 41 62 03 07");
private static final byte[] NUMBER_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] FOO_THREE = VPackWireFixtureTest.hex(
            "0b 09 01 43 66 6f 6f 33 03");
private static final byte[] QUOTED_FOO = VPackWireFixtureTest.hex(
            "45 27 66 6f 6f 27");
private static final byte[] CUSTOM_LIST = VPackWireFixtureTest.hex(
            "0b 14 01 44 6c 69 73 74 06 0b 03 41 41 18 41 42 03 05 06 03");
private static final byte[] FOO_BAR = VPackWireFixtureTest.hex(
            "46 46 4f 4f 42 41 52");
private static final byte[] FOO_NULL = VPackWireFixtureTest.hex(
            "06 0a 02 43 46 4f 4f 18 03 07");

    void testNumberSubclassVpack() throws Exception {
        assertVpack(NUMBER_OBJECT, MAPPER.writeValueAsBytes(new LikeNumber(42)));
    }

    void testWithCurrentValueVpack() throws Exception {
        assertVpack(CURRENT_VALUE, MAPPER.writeValueAsBytes(new Issue631Bean(42)));
    }

    void testWithCustomElementsVpack() throws Exception {
        assertVpack(CUSTOM_LIST,
                MAPPER.writeValueAsBytes(new StringListWrapper("a", null, "b")));

        SimpleModule module = new SimpleModule("custom-elements")
                .addSerializer(String.class, new UCStringSerializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertVpack(FOO_BAR, mapper.writeValueAsBytes("foobar"));
        assertVpack(FOO_NULL, mapper.writeValueAsBytes(new String[] { "foo", null }));
        assertVpack(FOO_NULL, mapper.writeValueAsBytes(Arrays.asList("foo", null)));
        Set<String> set = new LinkedHashSet<>(Arrays.asList("foo", null));
        assertVpack(FOO_NULL, mapper.writeValueAsBytes(set));
    }

    void testRegisteredDelegatingSerializerVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().addSerializer(new DelegatingSerializer5630Impl()))
                .build();
        assertVpack(QUOTED_FOO, mapper.writeValueAsBytes("foo"));
    }

    void testPropertyRemovalVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SerializerModifierModule(new RemovingModifier("a"))).build();
        assertVpack(ONLY_B, mapper.writeValueAsBytes(new ModifierBean()));
    }

    void testPropertyReorderVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SerializerModifierModule(new ReorderingModifier())).build();
        assertVpack(A_THEN_B, mapper.writeValueAsBytes(new ModifierBean()));
    }

    void testSerializerReplacementVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SerializerModifierModule(new ReplacingModifier(
                        new BogusBeanSerializer(123)))).build();
        assertVpack(NUMBER_123, mapper.writeValueAsBytes(new ModifierBean()));
    }

    void testModifyMapSerializerVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().setSerializerModifier(new MapSerializerModifier()))
                .build();
        assertVpack(NUMBER_123, mapper.writeValueAsBytes(new HashMap<String, String>()));
    }

    void testModifyEnumSerializerVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().setSerializerModifier(new EnumSerializerModifier()))
                .build();
        assertVpack(NUMBER_123, mapper.writeValueAsBytes(ABC.C));
    }

    void testModifyKeySerializerVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().setSerializerModifier(new KeySerializerModifier()))
                .build();
        Map<String, Integer> map = new HashMap<>();
        map.put("x", 3);
        assertVpack(FOO_THREE, mapper.writeValueAsBytes(map));
    }
private static void assertVpack(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual, "actual=" + toHex(actual));
    }
private static String toHex(byte[] value) {
        StringBuilder result = new StringBuilder();
        for (byte b : value) {
            if (!result.isEmpty()) result.append(' ');
            result.append(String.format("%02x", b & 0xff));
        }
        return result.toString();
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    static class LikeNumber extends Number {
        private static final long serialVersionUID = 1L;
        public int x;
        LikeNumber(int value) { x = value; }
        @Override public double doubleValue() { return x; }
        @Override public float floatValue() { return x; }
        @Override public int intValue() { return x; }
        @Override public long longValue() { return x; }
    }
static class Issue631Bean {
        @JsonSerialize(using = ParentClassSerializer.class)
        public Object prop;
        Issue631Bean(Object value) { prop = value; }
    }
static class ParentClassSerializer extends StdScalarSerializer<Object> {
        ParentClassSerializer() { super(Object.class); }
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContext provider) {
            Object parent = generator.currentValue();
            String description = (parent == null) ? "NULL" : parent.getClass().getSimpleName();
            generator.writeString(description + "/" + value);
        }
    }
static class StringListWrapper {
        @JsonSerialize(contentUsing = UCStringSerializer.class)
        public List<String> list;
        StringListWrapper(String... values) {
            list = new ArrayList<>();
            for (String value : values) list.add(value);
        }
    }
static class UCStringSerializer extends StdScalarSerializer<String> {
        UCStringSerializer() { super(String.class); }
        @Override
        public void serialize(String value, JsonGenerator generator, SerializationContext provider) {
            generator.writeString(value.toUpperCase());
        }
    }
static class DelegatingSerializer5630Impl extends tools.jackson.databind.ser.std.DelegatingSerializer {
        DelegatingSerializer5630Impl() { this(new QuotingStringSerializer5630Impl()); }
        DelegatingSerializer5630Impl(ValueSerializer<?> serializer) { super(serializer); }
        @Override
        protected ValueSerializer<Object> newDelegatingInstance(ValueSerializer<?> delegatee) {
            return new DelegatingSerializer5630Impl(delegatee);
        }
    }
static class QuotingStringSerializer5630Impl extends StdSerializer<String> {
        QuotingStringSerializer5630Impl() { super(String.class); }
        @Override
        public void serialize(String value, JsonGenerator generator, SerializationContext provider) {
            generator.writeString("'" + value + "'");
        }
        @Override
        public boolean isEmpty(SerializationContext provider, String value) { return value.isEmpty(); }
        @Override
        public ValueSerializer<String> unwrappingSerializer(NameTransformer unwrapper) {
            return new QuotingStringSerializer5630Impl();
        }
    }
@JsonPropertyOrder({ "b", "a" })
    static class ModifierBean { public String b = "b"; public String a = "a"; }
static class SerializerModifierModule extends SimpleModule {
        SerializerModifierModule(ValueSerializerModifier modifier) {
            super("serializer-modifier");
            setSerializerModifier(modifier);
        }
    }
static class RemovingModifier extends ValueSerializerModifier {
        private final String removed;
        RemovingModifier(String value) { removed = value; }
        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                BeanDescription.Supplier beanDesc, List<BeanPropertyWriter> properties) {
            properties.removeIf(property -> property.getName().equals(removed));
            return properties;
        }
    }
static class ReorderingModifier extends ValueSerializerModifier {
        @Override
        public List<BeanPropertyWriter> orderProperties(SerializationConfig config,
                BeanDescription.Supplier beanDesc, List<BeanPropertyWriter> properties) {
            properties.sort((first, second) -> first.getName().compareTo(second.getName()));
            return properties;
        }
    }
static class ReplacingModifier extends ValueSerializerModifier {
        private final ValueSerializer<?> serializer;
        ReplacingModifier(ValueSerializer<?> value) { serializer = value; }
        @Override
        public ValueSerializer<?> modifySerializer(SerializationConfig config,
                BeanDescription.Supplier beanDesc, ValueSerializer<?> original) {
            return serializer;
        }
    }
static class BogusBeanSerializer extends StdSerializer<Object> {
        private final int value;
        BogusBeanSerializer(int value) { super(Object.class); this.value = value; }
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContext provider) {
            generator.writeNumber(this.value);
        }
    }
static class MapSerializerModifier extends ValueSerializerModifier {
        @Override
        public ValueSerializer<?> modifyMapSerializer(SerializationConfig config, MapType valueType,
                BeanDescription.Supplier beanDesc, ValueSerializer<?> serializer) {
            return new BogusBeanSerializer(123);
        }
    }
static class EnumSerializerModifier extends ValueSerializerModifier {
        @Override
        public ValueSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType,
                BeanDescription.Supplier beanDesc, ValueSerializer<?> serializer) {
            return new BogusBeanSerializer(123);
        }
    }
static class KeySerializerModifier extends ValueSerializerModifier {
        @Override
        public ValueSerializer<?> modifyKeySerializer(SerializationConfig config, JavaType valueType,
                BeanDescription.Supplier beanDesc, ValueSerializer<?> serializer) {
            return new StdSerializer<Object>(Object.class) {
                @Override
                public void serialize(Object value, JsonGenerator generator, SerializationContext provider) {
                    generator.writeName("foo");
                }
            };
        }
    }
enum ABC { A, B, C }

    void __invoke_testNumberSubclassVpack() throws Exception {
        try {
            testNumberSubclassVpack();
        } finally {
        }
    }


    void __invoke_testWithCurrentValueVpack() throws Exception {
        try {
            testWithCurrentValueVpack();
        } finally {
        }
    }


    void __invoke_testWithCustomElementsVpack() throws Exception {
        try {
            testWithCustomElementsVpack();
        } finally {
        }
    }


    void __invoke_testRegisteredDelegatingSerializerVpack() throws Exception {
        try {
            testRegisteredDelegatingSerializerVpack();
        } finally {
        }
    }


    void __invoke_testPropertyRemovalVpack() throws Exception {
        try {
            testPropertyRemovalVpack();
        } finally {
        }
    }


    void __invoke_testPropertyReorderVpack() throws Exception {
        try {
            testPropertyReorderVpack();
        } finally {
        }
    }


    void __invoke_testSerializerReplacementVpack() throws Exception {
        try {
            testSerializerReplacementVpack();
        } finally {
        }
    }


    void __invoke_testModifyMapSerializerVpack() throws Exception {
        try {
            testModifyMapSerializerVpack();
        } finally {
        }
    }


    void __invoke_testModifyEnumSerializerVpack() throws Exception {
        try {
            testModifyEnumSerializerVpack();
        } finally {
        }
    }


    void __invoke_testModifyKeySerializerVpack() throws Exception {
        try {
            testModifyKeySerializerVpack();
        } finally {
        }
    }

}
