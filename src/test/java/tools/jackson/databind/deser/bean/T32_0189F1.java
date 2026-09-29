package tools.jackson.databind.deser.bean;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.BeanDeserializerBuilder;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.bean.BeanDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.ArrayType;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0189F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .addModule(new BeanModule(new ValueDeserializerModifier() {
                @Override
                public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                        BeanDescription.Supplier beanDescRef, BeanDeserializerBuilder builder) {
                    Iterator<SettableBeanProperty> properties = builder.getProperties();
                    while (properties.hasNext()) {
                        SettableBeanProperty property = properties.next();
                        if (property.getName().equals("a")) {
                            builder.addOrReplaceProperty(property.withValueDeserializer(
                                    new ConvertingStringDeserializer()), true);
                        }
                    }
                    return builder;
                }
            }))
            .build();
private static final byte[] CUSTOM_STRING_PROPERTY = VPackWireFixtureTest.hex(
            "14 10 41 61 4a 53 6f 6d 65 20 76 61 6c 75 65 01");
private static final byte[] ABSTRACT_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 78 33 01");
private static final byte[] BEAN_OBJECT = VPackWireFixtureTest.hex(
            "14 09 41 61 43 78 79 7a 01");
private static final byte[] ISSUE476_OBJECT = VPackWireFixtureTest.hex(
            "14 43 46 76 61 6c 75 65 31 "
          + "14 1a 44 6e 61 6d 65 45 66 72 75 69 74 45 76 61 6c 75 65 45 61 70 70 6c 65 02 "
          + "46 76 61 6c 75 65 32 "
          + "14 18 44 6e 61 6d 65 45 63 6f 6c 6f 72 45 76 61 6c 75 65 43 72 65 64 02 "
          + "02");
private static final byte[] TWO_VALUES = VPackWireFixtureTest.hex(
            "13 05 31 32 02");
private static final byte[] TWO_PROPERTIES = VPackWireFixtureTest.hex(
            "14 09 41 61 31 41 62 32 02");
private static final byte[] ONE_PROPERTY = VPackWireFixtureTest.hex(
            "14 06 41 61 31 01");
private static final byte[] ISSUE1912_OBJECT = VPackWireFixtureTest.hex(
            "14 14 47 73 75 62 42 65 61 6e "
          + "14 09 41 61 43 66 6f 6f 01 01");
private static final byte[] ENUM_VALUE = VPackWireFixtureTest.hex("41 42");
private static final byte[] STRING_VALUE = VPackWireFixtureTest.hex(
            "46 61 62 63 44 45 46");

    void testAbstractFailure() {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(ABSTRACT_OBJECT, Abstract.class));

        assertTrue(exception.getMessage().contains("Cannot construct instance of"));
    }

    void testDeserializerReplacement() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new BeanModule(new ReplacingModifier(new BogusBeanDeserializer("foo", "bar"))))
                .build();

        Bean value = mapper.readValue(BEAN_OBJECT, Bean.class);

        assertEquals("foo", value.a);
        assertEquals("bar", value.b);
    }

    void testIssue476() throws Exception {
        Issue476Deserializer.propCount = 0;
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new Issue476Module())
                .build();

        mapper.readValue(ISSUE476_OBJECT, Issue476Bean.class);

        assertEquals(2, Issue476Deserializer.propCount);
    }

    void testModifyArrayDeserializer() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new BeanModule(new ArrayDeserializerModifier()))
                .build();

        Object[] result = mapper.readValue(TWO_VALUES, Object[].class);

        assertEquals(1, result.length);
        assertEquals("foo", result[0]);
    }

    void testModifyCollectionDeserializer() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new BeanModule(new CollectionDeserializerModifier()))
                .build();

        List<?> result = mapper.readValue(TWO_VALUES, List.class);

        assertEquals(1, result.size());
        assertEquals("foo", result.get(0));
    }

    void testModifyEnumDeserializer() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new BeanModule(new EnumDeserializerModifier()))
                .build();

        Object result = mapper.readValue(ENUM_VALUE, EnumABC.class);

        assertEquals("foo", result);
    }

    void testModifyKeyDeserializer() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new BeanModule(new KeyDeserializerModifier()))
                .build();

        Map<?, ?> result = mapper.readValue(ONE_PROPERTY, Map.class);

        assertEquals(1, result.size());
        assertEquals("foo", result.entrySet().iterator().next().getKey());
    }

    void testModifyMapDeserializer() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new BeanModule(new MapDeserializerModifier()))
                .build();

        Map<?, ?> result = mapper.readValue(TWO_PROPERTIES, Map.class);

        assertEquals(1, result.size());
        assertEquals("foo", result.get("a"));
    }

    void testModifyStdScalarDeserializer() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new BeanModule(new ValueDeserializerModifier() {
                    @Override
                    public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                            BeanDescription.Supplier beanDescRef, ValueDeserializer<?> deser) {
                        if (beanDescRef.getBeanClass() == String.class) {
                            return new UCStringDeserializer(deser);
                        }
                        return deser;
                    }
                }))
                .build();

        Object result = mapper.readValue(STRING_VALUE, String.class);

        assertEquals("ABCDEF", result);
    }

    void testAddOrReplacePropertyIsUsedOnDeserialization() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new Issue1912Module())
                .build();

        Issue1912Bean result = mapper.readValue(ISSUE1912_OBJECT, Issue1912Bean.class);

        assertEquals("foo_custom", result.subBean.a);
    }
static class MutableBean4356 {
        String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }
static class ImmutableBean4356 {
        final String a;

        @JsonCreator
        public ImmutableBean4356(@JsonProperty("a") String a) {
            this.a = a;
        }

        public String getA() {
            return a;
        }
    }
static class BeanDeserializerModifier4356Support {
        static final String CUSTOM_DESERIALIZER_VALUE = "Custom deserializer value";
    }
static class BeanModule extends SimpleModule {
        BeanModule(ValueDeserializerModifier modifier) {
            setDeserializerModifier(modifier);
        }
    }
static class ConvertingStringDeserializer extends ValueDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, tools.jackson.databind.DeserializationContext ctxt) {
            p.skipChildren();
            return BeanDeserializerModifier4356Support.CUSTOM_DESERIALIZER_VALUE;
        }
    }
static abstract class Abstract {
        public int x;
    }
static class Bean {
        public String b = "b";
        public String a = "a";

        Bean() { }

        Bean(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }
static class BogusBeanDeserializer extends ValueDeserializer<Object> {
        private final String a;
        private final String b;

        BogusBeanDeserializer(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public Object deserialize(JsonParser p, tools.jackson.databind.DeserializationContext ctxt) {
            p.skipChildren();
            return new Bean(a, b);
        }
    }
static class ReplacingModifier extends ValueDeserializerModifier {
        private final ValueDeserializer<?> deserializer;

        ReplacingModifier(ValueDeserializer<?> deserializer) {
            this.deserializer = deserializer;
        }

        @Override
        public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                BeanDescription.Supplier beanDescRef, ValueDeserializer<?> deserializer) {
            return this.deserializer;
        }
    }
enum EnumABC { A, B, C }
static class ArrayDeserializerModifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyArrayDeserializer(DeserializationConfig config,
                ArrayType valueType, BeanDescription.Supplier beanDescRef,
                ValueDeserializer<?> deserializer) {
            return new StdDeserializer<Object>(Object.class) {
                @Override
                public Object deserialize(JsonParser p,
                        tools.jackson.databind.DeserializationContext ctxt) {
                    p.skipChildren();
                    return new String[] { "foo" };
                }
            };
        }
    }
static class CollectionDeserializerModifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyCollectionDeserializer(DeserializationConfig config,
                CollectionType valueType, BeanDescription.Supplier beanDescRef,
                ValueDeserializer<?> deserializer) {
            return new StdDeserializer<Object>(Object.class) {
                @Override
                public Object deserialize(JsonParser p,
                        tools.jackson.databind.DeserializationContext ctxt) {
                    p.skipChildren();
                    return List.of("foo");
                }
            };
        }
    }
static class MapDeserializerModifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyMapDeserializer(DeserializationConfig config,
                MapType valueType, BeanDescription.Supplier beanDescRef,
                ValueDeserializer<?> deserializer) {
            return new StdDeserializer<Object>(Object.class) {
                @Override
                public Object deserialize(JsonParser p,
                        tools.jackson.databind.DeserializationContext ctxt) {
                    p.skipChildren();
                    return Map.of("a", "foo");
                }
            };
        }
    }
static class EnumDeserializerModifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyEnumDeserializer(DeserializationConfig config,
                JavaType valueType, BeanDescription.Supplier beanDescRef,
                ValueDeserializer<?> deserializer) {
            return new StdDeserializer<Object>(Object.class) {
                @Override
                public Object deserialize(JsonParser p,
                        tools.jackson.databind.DeserializationContext ctxt) {
                    return "foo";
                }
            };
        }
    }
static class KeyDeserializerModifier extends ValueDeserializerModifier {
        @Override
        public KeyDeserializer modifyKeyDeserializer(DeserializationConfig config,
                JavaType valueType, KeyDeserializer kd) {
            return new KeyDeserializer() {
                @Override
                public Object deserializeKey(String key,
                        tools.jackson.databind.DeserializationContext ctxt) {
                    return "foo";
                }
            };
        }
    }
static class UCStringDeserializer extends StdScalarDeserializer<String> {
        private final ValueDeserializer<?> deserializer;

        UCStringDeserializer(ValueDeserializer<?> deserializer) {
            super(String.class);
            this.deserializer = deserializer;
        }

        @Override
        public String deserialize(JsonParser p, tools.jackson.databind.DeserializationContext ctxt) {
            Object value = deserializer.deserialize(p, ctxt);
            return String.valueOf(value).toUpperCase();
        }
    }
static class Issue476Bean {
        public Issue476Type value1;
        public Issue476Type value2;
    }
static class Issue476Type {
        public String name;
        public String value;
    }
static class Issue476Deserializer extends BeanDeserializer {
        static int propCount;

        Issue476Deserializer(BeanDeserializer source) {
            super(source);
        }

        @Override
        public ValueDeserializer<?> createContextual(
                tools.jackson.databind.DeserializationContext ctxt,
                tools.jackson.databind.BeanProperty property) {
            super.createContextual(ctxt, property);
            propCount++;
            return this;
        }
    }
static class Issue476DeserializerModifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                BeanDescription.Supplier beanDescRef, ValueDeserializer<?> deserializer) {
            if (Issue476Type.class == beanDescRef.getBeanClass()) {
                return new Issue476Deserializer((BeanDeserializer) deserializer);
            }
            return super.modifyDeserializer(config, beanDescRef, deserializer);
        }
    }
static class Issue476Module extends SimpleModule {
        Issue476Module() {
            setDeserializerModifier(new Issue476DeserializerModifier());
        }
    }
static class Issue1912Bean {
        public Issue1912SubBean subBean;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public Issue1912Bean(@JsonProperty("subBean") Issue1912SubBean subBean) {
            this.subBean = subBean;
        }
    }
static class Issue1912SubBean {
        public String a;

        public Issue1912SubBean() { }

        public Issue1912SubBean(String a) {
            this.a = a;
        }
    }
static class Issue1912CustomBeanDeserializer extends ValueDeserializer<Issue1912Bean> {
        private final BeanDeserializer defaultDeserializer;

        Issue1912CustomBeanDeserializer(BeanDeserializer defaultDeserializer) {
            this.defaultDeserializer = defaultDeserializer;
        }

        @Override
        public Issue1912Bean deserialize(JsonParser p,
                tools.jackson.databind.DeserializationContext ctxt) {
            p.nextName();
            if (p.nextToken() != JsonToken.START_OBJECT) {
                throw new IllegalArgumentException("Unexpected token " + p.currentToken());
            }

            Issue1912SubBean subBean = (Issue1912SubBean) defaultDeserializer
                    .findProperty(new PropertyName("subBean")).deserialize(p, ctxt);
            if (p.nextToken() != JsonToken.END_OBJECT) {
                throw new IllegalArgumentException("Unexpected token " + p.currentToken());
            }

            return new Issue1912Bean(subBean);
        }

        @Override
        public ValueDeserializer<?> createContextual(
                tools.jackson.databind.DeserializationContext ctxt,
                tools.jackson.databind.BeanProperty property) {
            return new Issue1912CustomBeanDeserializer(
                    (BeanDeserializer) defaultDeserializer.createContextual(ctxt, property));
        }
    }
static class Issue1912CustomPropertyDeserializer extends ValueDeserializer<Issue1912SubBean> {
        @Override
        public Issue1912SubBean deserialize(JsonParser p,
                tools.jackson.databind.DeserializationContext ctxt) {
            p.nextName();
            Issue1912SubBean object = new Issue1912SubBean(p.nextStringValue() + "_custom");
            p.nextToken();
            return object;
        }
    }
static class Issue1912Modifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                BeanDescription.Supplier beanDescRef, ValueDeserializer<?> deserializer) {
            if (beanDescRef.getBeanClass() == Issue1912Bean.class) {
                return new Issue1912CustomBeanDeserializer((BeanDeserializer) deserializer);
            }
            return super.modifyDeserializer(config, beanDescRef, deserializer);
        }

        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                BeanDescription.Supplier beanDescRef, BeanDeserializerBuilder builder) {
            if (beanDescRef.getBeanClass() == Issue1912Bean.class) {
                Iterator<SettableBeanProperty> properties = builder.getProperties();
                while (properties.hasNext()) {
                    SettableBeanProperty property = properties.next();
                    builder.addOrReplaceProperty(property.withValueDeserializer(
                            new Issue1912CustomPropertyDeserializer()), true);
                }
            }
            return builder;
        }
    }
static class Issue1912Module extends SimpleModule {
        Issue1912Module() {
            setDeserializerModifier(new Issue1912Modifier());
        }
    }

    void __invoke_testAbstractFailure() throws Exception {
        try {
            testAbstractFailure();
        } finally {
        }
    }


    void __invoke_testDeserializerReplacement() throws Exception {
        try {
            testDeserializerReplacement();
        } finally {
        }
    }


    void __invoke_testIssue476() throws Exception {
        try {
            testIssue476();
        } finally {
        }
    }


    void __invoke_testModifyArrayDeserializer() throws Exception {
        try {
            testModifyArrayDeserializer();
        } finally {
        }
    }


    void __invoke_testModifyCollectionDeserializer() throws Exception {
        try {
            testModifyCollectionDeserializer();
        } finally {
        }
    }


    void __invoke_testModifyEnumDeserializer() throws Exception {
        try {
            testModifyEnumDeserializer();
        } finally {
        }
    }


    void __invoke_testModifyKeyDeserializer() throws Exception {
        try {
            testModifyKeyDeserializer();
        } finally {
        }
    }


    void __invoke_testModifyMapDeserializer() throws Exception {
        try {
            testModifyMapDeserializer();
        } finally {
        }
    }


    void __invoke_testModifyStdScalarDeserializer() throws Exception {
        try {
            testModifyStdScalarDeserializer();
        } finally {
        }
    }


    void __invoke_testAddOrReplacePropertyIsUsedOnDeserialization() throws Exception {
        try {
            testAddOrReplacePropertyIsUsedOnDeserialization();
        } finally {
        }
    }

}
