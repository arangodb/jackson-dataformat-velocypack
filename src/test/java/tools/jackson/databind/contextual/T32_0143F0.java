package tools.jackson.databind.contextual;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.ContextualKeyDeserializer;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0143F0 {
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "14 0b 41 61 41 31 41 62 41 32 02");
private static final byte[] SIMPLE_OBJECT_3_4 = VPackWireFixtureTest.hex(
            "14 0b 41 61 41 33 41 62 41 34 02");
private static final byte[] SIMPLE_OBJECT_X_Y = VPackWireFixtureTest.hex(
            "14 0b 41 61 41 78 41 62 41 79 02");
private static final byte[] SIMPLE_OBJECT_123_345 = VPackWireFixtureTest.hex(
            "14 0f 41 61 43 31 32 33 41 62 43 33 34 35 02");
private static final byte[] MAP_SINGLE = VPackWireFixtureTest.hex(
            "14 10 45 62 65 61 6e 73 14 07 41 61 41 62 01 01");
private static final byte[] MAP_TWO = VPackWireFixtureTest.hex(
            "14 14 45 62 65 61 6e 73 14 0b 41 78 41 79 41 31 41 32 02 01");
private static final byte[] CONTEXTUAL_TYPE = VPackWireFixtureTest.hex(
            "14 10 45 73 74 75 66 66 14 07 41 31 41 62 01 01");
private static final byte[] EXPECTED_CLASS = VPackWireFixtureTest.hex(
            "14 14 45 76 61 6c 75 65 4a 56 6f 69 6c 61 2d 3e 78 79 7a 01");
private static final byte[] EXPECTED_ANNOTATED = VPackWireFixtureTest.hex(
            "14 15 45 76 61 6c 75 65 4b 70 72 65 66 69 78 2d 3e 61 62 63 01");
private static final byte[] EXPECTED_ARRAY = VPackWireFixtureTest.hex(
            "14 17 45 62 65 61 6e 73 13 0e 4a 61 72 72 61 79 2d 3e 31 32 33 01 01");
private static final byte[] EXPECTED_ARRAY_ELEMENT = VPackWireFixtureTest.hex(
            "14 16 45 62 65 61 6e 73 13 0d 49 65 6c 65 6d 2d 3e 34 35 36 01 01");
private static final byte[] EXPECTED_LIST = VPackWireFixtureTest.hex(
            "14 16 45 62 65 61 6e 73 13 0d 49 6c 69 73 74 2d 3e 61 62 63 01 01");
private static final byte[] EXPECTED_KEY = VPackWireFixtureTest.hex(
            "14 0d 48 70 72 65 66 69 78 3a 61 33 01");
private static final ObjectMapper MAPPER = new VPackMapper();

    void contextualDeserSimple() throws Exception {
        ObjectMapper mapper = contextualValueMapper(new PropertyNameDeserializer());
        ContextualBean bean = mapper.readValue(SIMPLE_OBJECT, ContextualBean.class);
        assertEquals("a=1", bean.a.value);
        assertEquals("b=2", bean.b.value);

        bean = mapper.readValue(SIMPLE_OBJECT_3_4, ContextualBean.class);
        assertEquals("a=3", bean.a.value);
        assertEquals("b=4", bean.b.value);
    }

    void contextualDeserSimpleWithAnnotations() throws Exception {
        ObjectMapper mapper = annotatedValueMapper();
        ContextualBean bean = mapper.readValue(SIMPLE_OBJECT, ContextualBean.class);
        assertEquals("NameA=1", bean.a.value);
        assertEquals("NameB=2", bean.b.value);

        bean = mapper.readValue(SIMPLE_OBJECT_X_Y, ContextualBean.class);
        assertEquals("NameA=x", bean.a.value);
        assertEquals("NameB=y", bean.b.value);
    }

    void contextualDeserSimpleWithClassAnnotations() throws Exception {
        ObjectMapper mapper = annotatedValueMapper();
        ContextualClassBean bean = mapper
                .readValue(SIMPLE_OBJECT, ContextualClassBean.class);
        assertEquals("Class=1", bean.a.value);
        assertEquals("NameB=2", bean.b.value);

        bean = mapper.readValue(SIMPLE_OBJECT_123_345, ContextualClassBean.class);
        assertEquals("Class=123", bean.a.value);
        assertEquals("NameB=345", bean.b.value);
    }

    void contextualDeserAnnotatedMap() throws Exception {
        ObjectMapper mapper = annotatedValueMapper();
        ContextualMapBean bean = mapper.readValue(MAP_SINGLE, ContextualMapBean.class);
        assertEquals(1, bean.beans.size());
        Map.Entry<String, StringValue> entry = bean.beans.entrySet().iterator().next();
        assertEquals("a", entry.getKey());
        assertEquals("map=b", entry.getValue().value);

        bean = mapper.readValue(MAP_TWO, ContextualMapBean.class);
        assertEquals(2, bean.beans.size());
        Iterator<Map.Entry<String, StringValue>> it = bean.beans.entrySet().iterator();
        entry = it.next();
        assertEquals("x", entry.getKey());
        assertEquals("map=y", entry.getValue().value);
        entry = it.next();
        assertEquals("1", entry.getKey());
        assertEquals("map=2", entry.getValue().value);
    }

    void contextualDeserContextualType() throws Exception {
        GenericBean bean = MAPPER.readValue(CONTEXTUAL_TYPE, GenericBean.class);
        assertEquals(1, bean.stuff.size());
        assertEquals("String", bean.stuff.get(Integer.valueOf(1)));
    }
private static ObjectMapper contextualValueMapper(ValueDeserializer<StringValue> deserializer) {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion())
                .addDeserializer(StringValue.class, deserializer);
        return VPackMapper.builder().addModule(module).build();
    }
private static ObjectMapper annotatedValueMapper() {
        return contextualValueMapper(new AnnotatedContextualDeserializer());
    }
private static ObjectMapper annotatedStringMapper() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion())
                .addSerializer(String.class, new AnnotatedContextualSerializer());
        return VPackMapper.builder().addModule(module).build();
    }
private static void assertWrittenTreeEquals(byte[] expected, ObjectMapper mapper,
            Object value) throws Exception {
        assertEquals(MAPPER.readTree(expected), MAPPER.readTree(mapper.writeValueAsBytes(value)));
    }
private static void assertWrittenTreeEquals(byte[] expected, ObjectWriter writer,
            Object value) throws Exception {
        assertEquals(MAPPER.readTree(expected), MAPPER.readTree(writer.writeValueAsBytes(value)));
    }
@java.lang.annotation.Target({ java.lang.annotation.ElementType.FIELD,
            java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.TYPE })
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @JacksonAnnotation
    public @interface Name {
        String value();
    }
static class StringValue {
        protected String value;

        StringValue(String value) {
            this.value = value;
        }
    }
static class ContextualBean {
        @Name("NameA")
        public StringValue a;
        @Name("NameB")
        public StringValue b;
    }
@Name("Class")
    static class ContextualClassBean {
        public StringValue a;

        @Name("NameB")
        public StringValue b;
    }
static class ContextualMapBean {
        @Name("map")
        public Map<String, StringValue> beans;
    }
static class ContextualCtorBean {
        protected String a;
        protected String b;

        @JsonCreator
        ContextualCtorBean(@Name("CtorA") @JsonProperty("a") StringValue a,
                @Name("CtorB") @JsonProperty("b") StringValue b) {
            this.a = a.value;
            this.b = b.value;
        }
    }
static class GenericBean {
        @JsonDeserialize(contentUsing = GenericStringDeserializer.class)
        public Map<Integer, String> stuff;
    }
static class GenericStringDeserializer extends StdScalarDeserializer<Object> {
        private final String value;

        GenericStringDeserializer() {
            this("N/A");
        }

        GenericStringDeserializer(String value) {
            super(String.class);
            this.value = value;
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context,
                BeanProperty property) {
            return new GenericStringDeserializer(
                    context.getContextualType().getRawClass().getSimpleName());
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) {
            return value;
        }
    }
static class PropertyNameDeserializer extends ValueDeserializer<StringValue> {
        private final String fieldName;

        PropertyNameDeserializer() {
            this("");
        }

        PropertyNameDeserializer(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public StringValue deserialize(JsonParser parser, DeserializationContext context) {
            return new StringValue(fieldName + "=" + parser.getString());
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context,
                BeanProperty property) {
            return new PropertyNameDeserializer(property == null ? "NULL" : property.getName());
        }
    }
static class AnnotatedContextualDeserializer extends ValueDeserializer<StringValue> {
        @Override
        public StringValue deserialize(JsonParser parser, DeserializationContext context) {
            return new StringValue("=" + parser.getString());
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context,
                BeanProperty property) {
            Name annotation = property.getAnnotation(Name.class);
            if (annotation == null) {
                annotation = property.getContextAnnotation(Name.class);
            }
            String name = annotation == null ? "UNKNOWN" : annotation.value();
            return new NamedValueDeserializer(name);
        }
    }
static class NamedValueDeserializer extends ValueDeserializer<StringValue> {
        private final String name;

        NamedValueDeserializer(String name) {
            this.name = name;
        }

        @Override
        public StringValue deserialize(JsonParser parser, DeserializationContext context) {
            return new StringValue(name + "=" + parser.getString());
        }
    }
static class MapBean {
        public Map<String, Integer> map;
    }
static class ContextualKeySerializer extends ValueSerializer<String> {
        private final String prefix;

        ContextualKeySerializer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void serialize(String value, JsonGenerator generator, SerializationContext context) {
            generator.writeName(prefix + value);
        }

        @Override
        public ValueSerializer<?> createContextual(SerializationContext context,
                BeanProperty property) {
            return new ContextualKeySerializer(prefix + ":");
        }
    }
static class ContextualKeyDeser extends KeyDeserializer implements ContextualKeyDeserializer {
        private final String prefix;

        ContextualKeyDeser(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Object deserializeKey(String key, DeserializationContext context) {
            return prefix + ":" + key;
        }

        @Override
        public KeyDeserializer createContextual(DeserializationContext context,
                BeanProperty property) {
            return new ContextualKeyDeser(property == null ? "ROOT" : property.getName());
        }
    }
@java.lang.annotation.Target({ java.lang.annotation.ElementType.FIELD,
            java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD })
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @JacksonAnnotation
    public @interface Prefix {
        String value();
    }
@Prefix("Voila->")
    static class BeanWithClassConfig {
        public String value;

        BeanWithClassConfig(String value) {
            this.value = value;
        }
    }
static class AnnotatedContextualBean {
        @Prefix("prefix->")
        @JsonSerialize(using = AnnotatedContextualSerializer.class)
        protected final String value;

        AnnotatedContextualBean(String value) {
            this.value = value;
        }
    }
static class ContextualArrayBean {
        @Prefix("array->")
        public final String[] beans;

        ContextualArrayBean(String... values) {
            beans = values;
        }
    }
static class ContextualArrayElementBean {
        @Prefix("elem->")
        @JsonSerialize(contentUsing = AnnotatedContextualSerializer.class)
        public final String[] beans;

        ContextualArrayElementBean(String... values) {
            beans = values;
        }
    }
static class ContextualListBean {
        @Prefix("list->")
        public final List<String> beans = new java.util.ArrayList<>();

        ContextualListBean(String... values) {
            for (String value : values) {
                beans.add(value);
            }
        }
    }
static class AnnotatedContextualSerializer extends ValueSerializer<String> {
        private final String prefix;

        AnnotatedContextualSerializer() {
            this("");
        }

        AnnotatedContextualSerializer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void serialize(String value, JsonGenerator generator, SerializationContext context) {
            generator.writeString(prefix + value);
        }

        @Override
        public ValueSerializer<?> createContextual(SerializationContext context,
                BeanProperty property) {
            String resolved = "UNKNOWN";
            Prefix annotation = null;
            if (property != null) {
                annotation = property.getAnnotation(Prefix.class);
                if (annotation == null) {
                    annotation = property.getContextAnnotation(Prefix.class);
                }
            }
            if (annotation != null) {
                resolved = annotation.value();
            }
            return new AnnotatedContextualSerializer(resolved);
        }
    }

    void __invoke_contextualDeserSimple() throws Exception {
        try {
            contextualDeserSimple();
        } finally {
        }
    }


    void __invoke_contextualDeserSimpleWithAnnotations() throws Exception {
        try {
            contextualDeserSimpleWithAnnotations();
        } finally {
        }
    }


    void __invoke_contextualDeserSimpleWithClassAnnotations() throws Exception {
        try {
            contextualDeserSimpleWithClassAnnotations();
        } finally {
        }
    }


    void __invoke_contextualDeserAnnotatedMap() throws Exception {
        try {
            contextualDeserAnnotatedMap();
        } finally {
        }
    }


    void __invoke_contextualDeserContextualType() throws Exception {
        try {
            contextualDeserContextualType();
        } finally {
        }
    }

}
