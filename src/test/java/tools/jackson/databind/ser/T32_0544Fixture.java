package tools.jackson.databind.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.POJOPropertyBuilder;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.BeanSerializerBuilder;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import tools.jackson.databind.ser.std.StdConvertingSerializer;
import tools.jackson.databind.ser.std.StdDelegatingSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.type.ArrayType;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.Converter;
import tools.jackson.databind.util.StdConverter;
import tools.jackson.core.JsonGenerator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0544Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] NULL_VALUE = VPackWireFixtureTest.hex("18");
private static final byte[] IMMUTABLE = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 33 41 79 37 03 06");
private static final byte[] OTHER_A = VPackWireFixtureTest.hex(
            "0b 0c 01 45 6f 74 68 65 72 41 61 03");
private static final byte[] ITEM = VPackWireFixtureTest.hex(
            "0b 12 02 42 69 64 44 49 44 2d 31 43 73 65 74 01 03 0b");
private static final byte[] EMPTY_BEAN = VPackWireFixtureTest.hex(
            "0b 0e 01 45 62 6f 67 75 73 43 66 6f 6f 03");
private static final byte[] FORTY_TWO = VPackWireFixtureTest.hex("28 2a");
private static final byte[] ONE_TWO_THREE = VPackWireFixtureTest.hex("28 7b");

    void testCustomListsVpack() throws Exception {
        SimpleModule module = new SimpleModule("custom-lists")
                .addSerializer(Collection.class, new StdSerializer<Collection>(Collection.class) {
                    @Override
                    public void serialize(Collection value, JsonGenerator generator,
                            SerializationContext provider) {
                        if (value.isEmpty()) {
                            generator.writeNull();
                        } else {
                            generator.writeStartArray();
                            generator.writeEndArray();
                        }
                    }
                });
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertVpack(NULL_VALUE, mapper.writeValueAsBytes(new ArrayList<>()));
    }

    void testDelegatingVpack() throws Exception {
        SimpleModule module = new SimpleModule("delegating");
        module.addSerializer(new StdConvertingSerializer(Immutable.class,
                new StdConverter<Immutable, Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> convert(Immutable value) {
                        Map<String, Integer> result = new java.util.LinkedHashMap<>();
                        result.put("x", value.x());
                        result.put("y", value.y());
                        return result;
                    }
                }));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertVpack(IMMUTABLE, mapper.writeValueAsBytes(new Immutable()));
    }

    
    void testDelegatingWithDeprecatedVpack() throws Exception {
        SimpleModule module = new SimpleModule("deprecated-delegating");
        module.addSerializer(new StdDelegatingSerializer(Immutable.class,
                new StdConverter<Immutable, Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> convert(Immutable value) {
                        Map<String, Integer> result = new java.util.LinkedHashMap<>();
                        result.put("x", value.x());
                        result.put("y", value.y());
                        return result;
                    }
                }));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertVpack(IMMUTABLE, mapper.writeValueAsBytes(new Immutable()));
    }

    void testConvertingSerializerIsEmptyVpack() throws Exception {
        assertVpack(OTHER_A, MAPPER.writeValueAsBytes(new ConvertingIsEmptyBean("NULL", "a")));
        assertEquals(Map.of("other", "b"),
                MAPPER.readValue(MAPPER.writeValueAsBytes(
                        new ConvertingIsEmptyBean("EMPTY", "b")), Map.class));
        Map<?, ?> value = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new ConvertingIsEmptyBean("hello", "c")), Map.class);
        assertEquals(Map.of("text", "hello", "other", "c"), value);
    }

    void testIssue2475Vpack() throws Exception {
        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter("myFilter", new CurrentValueFilter2475());
        assertVpack(ITEM, MAPPER.writer(filters).writeValueAsBytes(
                new Item2475(new ArrayList<String>(), "ID-1")));
    }

    void testIssue4575Vpack() throws Exception {
        // The actual behavior under test is the converting serializer's null
        // result in the presence of name-based type metadata.
        SimpleModule module = new SimpleModule("issue-4575")
                .setSerializerModifier(new NullSerializerModifier4575());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertEquals(Map.of("@type", "Super"),
                mapper.readValue(mapper.writeValueAsBytes(new Super4575()), Map.class));
        assertEquals(Map.of("@type", "Sub"),
                mapper.readValue(mapper.writeValueAsBytes(new Sub4575()), Map.class));
        assertEquals(null, mapper.readValue(mapper.writeValueAsBytes(Super4575.NULL), Object.class));
    }

    void testBuilderReplacementVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SerializerModifierModule(new BuilderModifier(new BogusBeanSerializer(17))))
                .build();
        assertVpack(VPackWireFixtureTest.hex("28 11"), mapper.writeValueAsBytes(new ModifierBean()));
    }

    void testEmptyBeanVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SerializerModifierModule(new EmptyBeanModifier()))
                .build();
        assertVpack(EMPTY_BEAN, mapper.writeValueAsBytes(new EmptyBean()));
    }

    void testEmptyBean539Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SerializerModifierModule(new EmptyBeanModifier539()))
                .build();
        assertVpack(FORTY_TWO, mapper.writeValueAsBytes(new EmptyBean()));
    }

    void testModifyArraySerializerVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().setSerializerModifier(new ArraySerializerModifier()))
                .build();
        assertVpack(ONE_TWO_THREE, mapper.writeValueAsBytes(new Integer[] { 1, 2 }));
    }

    void testModifyCollectionSerializerVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().setSerializerModifier(new CollectionSerializerModifier()))
                .build();
        assertVpack(ONE_TWO_THREE, mapper.writeValueAsBytes(new ArrayList<Integer>()));
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
private static tools.jackson.databind.ValueSerializer<?> numberSerializer() {
        return new StdSerializer<Object>(Object.class) {
            @Override public void serialize(Object value, JsonGenerator generator, SerializationContext provider) {
                generator.writeNumber(123);
            }
        };
    }
static class Immutable {
        int x() { return 3; }
        int y() { return 7; }
    }
@JsonPropertyOrder({ "text", "other" })
    static class ConvertingIsEmptyBean {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonSerialize(converter = MaybeEmptyConverter.class)
        public String text;
        public String other;
        ConvertingIsEmptyBean(String text, String other) { this.text = text; this.other = other; }
    }
static class MaybeEmptyConverter extends StdConverter<String, String> {
        @Override public String convert(String value) {
            if ("NULL".equals(value)) return null;
            if ("EMPTY".equals(value)) return "";
            return value;
        }
    }
@JsonFilter("myFilter")
    @JsonPropertyOrder({ "id", "set" })
    static class Item2475 {
        private final Collection<String> set;
        private final String id;
        Item2475(Collection<String> set, String id) { this.set = set; this.id = id; }
        public Collection<String> getSet() { return set; }
        public String getId() { return id; }
    }
static class CurrentValueFilter2475 extends SimpleBeanPropertyFilter {
        @Override
        public void serializeAsProperty(Object pojo, JsonGenerator generator,
                SerializationContext provider, PropertyWriter writer)
                throws Exception {
            if (!(generator.streamWriteContext().currentValue() instanceof Item2475)) {
                throw new AssertionError("current value was not Item2475");
            }
            super.serializeAsProperty(pojo, generator, provider, writer);
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(Sub4575.class))
    @JsonTypeName("Super")
    static class Super4575 {
        static final Super4575 NULL = new Super4575();
    }
@JsonTypeName("Sub")
    static class Sub4575 extends Super4575 { }
static class NullSerializerModifier4575 extends ValueSerializerModifier {
        @Override
        public tools.jackson.databind.ValueSerializer<?> modifySerializer(SerializationConfig config,
                BeanDescription.Supplier beanDesc, tools.jackson.databind.ValueSerializer<?> serializer) {
            if (serializer.handledType() == Super4575.class) {
                return new NullSerializer4575(config.getTypeFactory(), serializer, null);
            }
            return serializer;
        }
    }
static class NullSerializer4575 extends StdConvertingSerializer {
        NullSerializer4575(TypeFactory factory, tools.jackson.databind.ValueSerializer<?> delegate,
                BeanProperty property) {
            this(new StdConverter<Object, Object>() {
                @Override public Object convert(Object value) { return value == Super4575.NULL ? null : value; }
                @Override public JavaType getInputType(TypeFactory typeFactory) {
                    return typeFactory.constructType(delegate.handledType());
                }
                @Override public JavaType getOutputType(TypeFactory typeFactory) {
                    return typeFactory.constructType(delegate.handledType());
                }
            }, factory.constructType(delegate.handledType()), delegate, property);
        }

        NullSerializer4575(Converter<Object, ?> converter, JavaType delegateType,
                tools.jackson.databind.ValueSerializer<?> delegate, BeanProperty property) {
            super(converter, delegateType, delegate, property);
        }

        @Override
        protected StdConvertingSerializer withDelegate(Converter<Object, ?> converter,
                JavaType delegateType, tools.jackson.databind.ValueSerializer<?> delegate,
                BeanProperty property) {
            return new NullSerializer4575(converter, delegateType, delegate, property);
        }
    }
static class SerializerModifierModule extends SimpleModule {
        SerializerModifierModule(ValueSerializerModifier modifier) {
            super("serializer-modifier");
            setSerializerModifier(modifier);
        }
    }
@JsonPropertyOrder({ "b", "a" })
    static class ModifierBean { public String b = "b"; public String a = "a"; }
static class BuilderModifier extends ValueSerializerModifier {
        private final tools.jackson.databind.ValueSerializer<?> serializer;
        BuilderModifier(tools.jackson.databind.ValueSerializer<?> serializer) { this.serializer = serializer; }
        @Override
        public BeanSerializerBuilder updateBuilder(SerializationConfig config,
                BeanDescription.Supplier beanDesc, BeanSerializerBuilder builder) {
            return new BogusSerializerBuilder(builder, serializer);
        }
    }
static class BogusSerializerBuilder extends BeanSerializerBuilder {
        private final tools.jackson.databind.ValueSerializer<?> serializer;
        BogusSerializerBuilder(BeanSerializerBuilder source,
                tools.jackson.databind.ValueSerializer<?> serializer) {
            super(source); this.serializer = serializer;
        }
        @Override public tools.jackson.databind.ValueSerializer<?> build() { return serializer; }
    }
static class BogusBeanSerializer extends StdSerializer<Object> {
        private final int value;
        BogusBeanSerializer(int value) { super(Object.class); this.value = value; }
        @Override public void serialize(Object value, JsonGenerator generator, SerializationContext provider) {
            generator.writeNumber(this.value);
        }
    }
static class EmptyBean { @com.fasterxml.jackson.annotation.JsonIgnore public String name = "foo"; }
static class EmptyBeanModifier extends ValueSerializerModifier {
        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                BeanDescription.Supplier beanDesc, List<BeanPropertyWriter> properties) {
            JavaType stringType = config.constructType(String.class);
            POJOPropertyBuilder prop = new POJOPropertyBuilder(config, null, true,
                    new tools.jackson.databind.PropertyName("bogus"));
            try {
                AnnotatedField field = new AnnotatedField(null,
                        EmptyBean.class.getDeclaredField("name"), null);
                properties.add(new BeanPropertyWriter(prop, field, null, stringType,
                        null, null, stringType, false, null, null));
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
            return properties;
        }
    }
static class EmptyBeanModifier539 extends ValueSerializerModifier {
        @Override public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                BeanDescription.Supplier beanDesc, List<BeanPropertyWriter> properties) { return properties; }
        @Override public tools.jackson.databind.ValueSerializer<?> modifySerializer(SerializationConfig config,
                BeanDescription.Supplier beanDesc, tools.jackson.databind.ValueSerializer<?> serializer) {
            return new BogusBeanSerializer(42);
        }
    }
static class ArraySerializerModifier extends ValueSerializerModifier {
        @Override public tools.jackson.databind.ValueSerializer<?> modifyArraySerializer(SerializationConfig config,
                ArrayType valueType, BeanDescription.Supplier beanDesc,
                tools.jackson.databind.ValueSerializer<?> serializer) {
            return numberSerializer();
        }
    }
static class CollectionSerializerModifier extends ValueSerializerModifier {
        @Override public tools.jackson.databind.ValueSerializer<?> modifyCollectionSerializer(SerializationConfig config,
                CollectionType valueType, BeanDescription.Supplier beanDesc,
                tools.jackson.databind.ValueSerializer<?> serializer) {
            return numberSerializer();
        }
    }

    void __invoke_testCustomListsVpack() throws Exception {
        try {
            testCustomListsVpack();
        } finally {
        }
    }


    void __invoke_testDelegatingVpack() throws Exception {
        try {
            testDelegatingVpack();
        } finally {
        }
    }


    void __invoke_testDelegatingWithDeprecatedVpack() throws Exception {
        try {
            testDelegatingWithDeprecatedVpack();
        } finally {
        }
    }


    void __invoke_testConvertingSerializerIsEmptyVpack() throws Exception {
        try {
            testConvertingSerializerIsEmptyVpack();
        } finally {
        }
    }


    void __invoke_testIssue2475Vpack() throws Exception {
        try {
            testIssue2475Vpack();
        } finally {
        }
    }


    void __invoke_testIssue4575Vpack() throws Exception {
        try {
            testIssue4575Vpack();
        } finally {
        }
    }


    void __invoke_testBuilderReplacementVpack() throws Exception {
        try {
            testBuilderReplacementVpack();
        } finally {
        }
    }


    void __invoke_testEmptyBeanVpack() throws Exception {
        try {
            testEmptyBeanVpack();
        } finally {
        }
    }


    void __invoke_testEmptyBean539Vpack() throws Exception {
        try {
            testEmptyBean539Vpack();
        } finally {
        }
    }


    void __invoke_testModifyArraySerializerVpack() throws Exception {
        try {
            testModifyArraySerializerVpack();
        } finally {
        }
    }


    void __invoke_testModifyCollectionSerializerVpack() throws Exception {
        try {
            testModifyCollectionSerializerVpack();
        } finally {
        }
    }

}
