package tools.jackson.databind.deser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdConvertingDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.deser.std.StdNodeBasedDeserializer;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.databind.util.NameTransformer;
import tools.jackson.databind.util.StdConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0174Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] CUSTOM_BEANS = VPackWireFixtureTest.hex(
            "0b 3a 01 45 62 65 61 6e 73 06 30 01 0b 2c 02 41 63 0b 17 02 41 61 "
          + "c8 01 00 00 00 00 10 41 62 c8 01 00 00 00 00 20 03 0c "
          + "41 64 4b 68 65 6c 6c 6f 2c 20 74 61 74 75 03 1c 03 03");
private static final byte[] CUSTOM_BEANS_REORDERED = VPackWireFixtureTest.hex(
            "0b 39 01 45 62 65 61 6e 73 06 2f 02 0b 15 02 41 63 0f 0b 02 41 62 33 41 61 3c 03 06 "
          + "41 64 40 03 10 0f 15 02 41 64 43 61 62 63 41 63 0b 08 01 41 62 28 0f 03 03 09 03 18 03");
private static final byte[] IMMUTABLE = VPackWireFixtureTest.hex(
            "14 09 41 78 33 41 79 37 02");
private static final byte[] IMMUTABLE_NEGATIVE = VPackWireFixtureTest.hex(
            "14 0a 41 78 20 f6 41 79 33 02");
private static final byte[] CUSTOM_KEY_MODEL = VPackWireFixtureTest.hex(
            "14 13 43 6d 61 70 14 0c 43 31 32 33 44 74 65 73 74 01 01");
private static final byte[] CONTEXT_ROOT = VPackWireFixtureTest.hex("28 0d");
private static final byte[] CONTEXT_PROPERTY = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 28 0d 01");
private static final byte[] CURRENT_VALUE = VPackWireFixtureTest.hex(
            "14 0e 44 70 72 6f 70 45 73 74 75 66 66 01");
private static final byte[] FOO = VPackWireFixtureTest.hex("43 66 6f 6f");
private static final byte[] FOO_WRAPPER = VPackWireFixtureTest.hex(
            "14 0b 43 73 74 72 43 66 6f 6f 01");
private static final byte[] READ_TREE = VPackWireFixtureTest.hex(
            "13 0c 31 14 06 41 61 33 01 28 7b 03");
private static final byte[] NAMED_POINT = VPackWireFixtureTest.hex(
            "0b 24 02 45 70 6f 69 6e 74 14 0a 41 78 28 0d 41 79 3c 02 "
          + "44 6e 61 6d 65 49 46 6f 6f 7a 69 62 61 6c 64 13 03");
private static final byte[] NAMED_POINT_NUMBER_NAME = VPackWireFixtureTest.hex(
            "14 09 44 6e 61 6d 65 34 01");

    void testCustomBeanDeserializer() throws Exception {
        TestBeans beans = MAPPER.readValue(CUSTOM_BEANS, TestBeans.class);
        assertNotNull(beans);
        assertEquals(1, beans.beans.size());
        TestBean bean = beans.beans.get(0);
        assertEquals("hello, tatu", bean.d);
        assertEquals(10, bean.c.a);
        assertEquals(20, bean.c.b);

        beans = MAPPER.readValue(CUSTOM_BEANS_REORDERED, TestBeans.class);
        assertNotNull(beans);
        assertEquals(2, beans.beans.size());
        bean = beans.beans.get(0);
        assertEquals("", bean.d);
        assertEquals(-4, bean.c.a);
        assertEquals(3, bean.c.b);
        bean = beans.beans.get(1);
        assertEquals("abc", bean.d);
        assertEquals(0, bean.c.a);
        assertEquals(15, bean.c.b);
    }

    void testDelegating() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(Immutable.class,
                new StdConvertingDeserializer<Immutable>(
                        new StdConverter<JsonNode, Immutable>() {
                            @Override
                            public Immutable convert(JsonNode value) {
                                return new Immutable(value.path("x").asInt(),
                                        value.path("y").asInt());
                            }
                        }));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        Immutable value = mapper.readValue(IMMUTABLE, Immutable.class);
        assertEquals(3, value.x);
        assertEquals(7, value.y);
    }

    void testJsonNodeDelegating() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(Immutable.class,
                new StdNodeBasedDeserializer<Immutable>(Immutable.class) {
                    @Override
                    public Immutable convert(JsonNode root, DeserializationContext ctxt) {
                        return new Immutable(root.path("x").asInt(),
                                root.path("y").asInt());
                    }
                });
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        Immutable value = mapper.readValue(IMMUTABLE_NEGATIVE, Immutable.class);
        assertEquals(-10, value.x);
        assertEquals(3, value.y);
    }

    void testIssue882() throws Exception {
        Model model = MAPPER.readValue(CUSTOM_KEY_MODEL, Model.class);
        assertNotNull(model);
        assertNotNull(model.map);
        assertEquals(1, model.map.size());
        assertEquals("test", model.map.get(new CustomKey(123)));
    }

    void testContextReadValue() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(Bean375Outer.class, new Bean375OuterDeserializer());
        module.addDeserializer(Bean375Inner.class, new Bean375InnerDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Bean375Outer outer = mapper.readValue(CONTEXT_ROOT, Bean375Outer.class);
        assertEquals(26, outer.inner.x);

        Bean375Wrapper wrapper = mapper.readValue(CONTEXT_PROPERTY, Bean375Wrapper.class);
        assertNotNull(wrapper.value);
        assertNotNull(wrapper.value.inner);
        assertEquals(-13, wrapper.value.inner.x);
    }

    void testCurrentValueAccess() throws Exception {
        Issue631Bean bean = MAPPER.readValue(CURRENT_VALUE, Issue631Bean.class);
        assertNotNull(bean);
        assertEquals("prop/Issue631Bean", bean.prop);
    }

    void testCustomStringDeser() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(String.class, new UCStringDeserializer()))
                .build();
        assertEquals("FOO", mapper.readValue(FOO, String.class));
        StringWrapper wrapper = mapper.readValue(FOO_WRAPPER, StringWrapper.class);
        assertNotNull(wrapper);
        assertEquals("FOO", wrapper.str);
    }

    void testDelegatingDeserializer() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new DelegatingModuleImpl())
                .build();
        assertEquals("MY:foo", mapper.readValue(FOO, String.class));
    }

    void testModifyingCustomDeserializer() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .setDeserializerModifier(new ValueDeserializerModifier() {
                            @Override
                            public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                    BeanDescription.Supplier beanDescRef,
                                    ValueDeserializer<?> deserializer) {
                                if (deserializer instanceof DummyDeserializer<?>) {
                                    return new DummyDeserializer<String>("FOOBAR", String.class);
                                }
                                return deserializer;
                            }
                        })
                        .addDeserializer(String.class,
                                new DummyDeserializer<String>("dummy", String.class)))
                .build();
        assertEquals("FOOBAR", mapper.readValue(FOO, String.class));
    }

    void testCustomDeserializerWithReadTree() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().addDeserializer(Object.class,
                        new MyNodeDeserializer()))
                .build();
        ObjectWrapper wrapper = mapper.readValue(READ_TREE, ObjectWrapper.class);
        assertEquals(ArrayNode.class, wrapper.getObject().getClass());
        JsonNode node = (JsonNode) wrapper.getObject();
        assertEquals(3, node.size());
        assertEquals(123, node.get(2).intValue());
    }

    void testCustomDeserializerWithReadTreeAsValue() throws Exception {
        NamedPoint result = MAPPER.readValue(NAMED_POINT, NamedPoint.class);
        assertNotNull(result);
        assertEquals("Foozibald", result.name);
        assertEquals(new Point(13, -4), result.point);

        result = MAPPER.readValue(NAMED_POINT,
                MAPPER.constructType(NamedPoint.class));
        assertNotNull(result);
        assertEquals("Foozibald", result.name);
        assertEquals(new Point(13, -4), result.point);

        result = MAPPER.readValue(NAMED_POINT_NUMBER_NAME, NamedPoint.class);
        assertNotNull(result);
        assertEquals("4", result.name);
        assertNull(result.point);
    }

    void testBasicDelegatingDeser() throws Exception {
        Delegating3748 deser = new Delegating3748();
        assertEquals("absent", deser.getAbsentValue(null));
        assertEquals("empty", deser.getEmptyValue(null));
        assertEquals(AccessPattern.ALWAYS_NULL, deser.getEmptyAccessPattern());
        ValueDeserializer<?> unwrapping = deser.unwrappingDeserializer(null, null);
        assertNotNull(unwrapping);
        assertNotSame(deser, unwrapping);
    }
static class ObjectWrapper {
        final Object object;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        ObjectWrapper(Object object) {
            this.object = object;
        }

        Object getObject() { return object; }
    }
static class DummyDeserializer<T> extends StdDeserializer<T> {
        final T value;

        DummyDeserializer(T value, Class<T> cls) {
            super(cls);
            this.value = value;
        }

        @Override
        public T deserialize(JsonParser parser, DeserializationContext ctxt) {
            parser.skipChildren();
            return value;
        }
    }
static class TestBeans { public List<TestBean> beans; }
static class TestBean { public CustomBean c; public String d; }
@JsonDeserialize(using = CustomBeanDeserializer.class)
    static class CustomBean {
        final int a, b;

        CustomBean(int a, int b) { this.a = a; this.b = b; }
    }
static class CustomBeanDeserializer extends ValueDeserializer<CustomBean> {
        @Override
        public CustomBean deserialize(JsonParser parser, DeserializationContext ctxt) {
            JsonNode tree = ctxt.readTree(parser);
            return new CustomBean(tree.path("a").asInt(), tree.path("b").asInt());
        }
    }
static class Immutable {
        final int x, y;

        Immutable(int x, int y) { this.x = x; this.y = y; }
    }
static class CustomKey {
        final int id;

        CustomKey(int id) { this.id = id; }

        @Override
        public boolean equals(Object other) {
            return other instanceof CustomKey key && id == key.id;
        }

        @Override
        public int hashCode() { return id; }
    }
static class Model {
        final Map<CustomKey, String> map;

        @JsonCreator
        Model(@JsonProperty("map") @JsonDeserialize(keyUsing = CustomKeyDeserializer.class)
                Map<CustomKey, String> map) {
            this.map = map;
        }
    }
static class CustomKeyDeserializer extends tools.jackson.databind.KeyDeserializer {
        @Override
        public CustomKey deserializeKey(String key, DeserializationContext ctxt) {
            return new CustomKey(Integer.parseInt(key));
        }
    }
@Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Negative { }
static class Bean375Wrapper {
        @Negative
        public Bean375Outer value;
    }
static class Bean375Outer {
        final Bean375Inner inner;

        Bean375Outer(Bean375Inner inner) { this.inner = inner; }
    }
static class Bean375Inner {
        final int x;

        Bean375Inner(int x) { this.x = x; }
    }
static class Bean375OuterDeserializer extends StdDeserializer<Bean375Outer> {
        final BeanProperty property;

        Bean375OuterDeserializer() { this(null); }
        Bean375OuterDeserializer(BeanProperty property) {
            super(Bean375Outer.class);
            this.property = property;
        }

        @Override
        public Bean375Outer deserialize(JsonParser parser, DeserializationContext ctxt) {
            Object value = ctxt.readPropertyValue(parser, property, Bean375Inner.class);
            return new Bean375Outer((Bean375Inner) value);
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
                BeanProperty property) {
            return new Bean375OuterDeserializer(property);
        }
    }
static class Bean375InnerDeserializer extends StdDeserializer<Bean375Inner> {
        final boolean negative;

        Bean375InnerDeserializer() { this(false); }
        Bean375InnerDeserializer(boolean negative) {
            super(Bean375Inner.class);
            this.negative = negative;
        }

        @Override
        public Bean375Inner deserialize(JsonParser parser, DeserializationContext ctxt) {
            int x = parser.getIntValue();
            return new Bean375Inner(negative ? -x : x + x);
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
                BeanProperty property) {
            if (property != null && property.getAnnotation(Negative.class) != null) {
                return new Bean375InnerDeserializer(true);
            }
            return this;
        }
    }
static class Issue631Bean {
        @JsonDeserialize(using = ParentClassDeserializer.class)
        public Object prop;
    }
static class ParentClassDeserializer extends StdScalarDeserializer<Object> {
        ParentClassDeserializer() { super(Object.class); }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext ctxt) {
            Object parent = parser.currentValue();
            return "prop/" + ((parent == null) ? "NULL" : parent.getClass().getSimpleName());
        }
    }
static class UCStringDeserializer extends StdDeserializer<String> {
        UCStringDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext ctxt) {
            return parser.getString().toUpperCase();
        }
    }
static class StringWrapper { public String str; }
static class DelegatingModuleImpl extends SimpleModule {
        DelegatingModuleImpl() { super("test", Version.unknownVersion()); }

        @Override
        public void setupModule(SetupContext context) {
            super.setupModule(context);
            context.addDeserializerModifier(new ValueDeserializerModifier() {
                @Override
                public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                        BeanDescription.Supplier beanDescRef,
                        ValueDeserializer<?> deserializer) {
                    if (deserializer.handledType() == String.class) {
                        return new MyStringDeserializer(deserializer);
                    }
                    return deserializer;
                }
            });
        }
    }
static class MyStringDeserializer extends DelegatingDeserializer {
        MyStringDeserializer(ValueDeserializer<?> delegate) { super(delegate); }

        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> delegate) {
            return new MyStringDeserializer(delegate);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext ctxt) {
            return "MY:" + _delegatee.deserialize(parser, ctxt);
        }
    }
static class MyNodeDeserializer extends StdDeserializer<Object> {
        MyNodeDeserializer() { super(Object.class); }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext ctxt) {
            return ctxt.readTree(parser);
        }
    }
@JsonDeserialize(using = NamedPointDeserializer.class)
    static class NamedPoint {
        public Point point;
        public String name;

        NamedPoint(String name, Point point) {
            this.name = name;
            this.point = point;
        }
    }
static class NamedPointDeserializer extends StdDeserializer<NamedPoint> {
        NamedPointDeserializer() { super(NamedPoint.class); }

        @Override
        public NamedPoint deserialize(JsonParser parser, DeserializationContext ctxt) {
            JsonNode tree = ctxt.readTree(parser);
            String name = tree.path("name").asString(null);
            Point point = ctxt.readTreeAsValue(tree.get("point"), Point.class);
            return new NamedPoint(name, point);
        }
    }
static class Point {
        final int x, y;

        @JsonCreator
        Point(@JsonProperty("x") int x, @JsonProperty("y") int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Point point && x == point.x && y == point.y;
        }

        @Override
        public int hashCode() { return 31 * x + y; }
    }
static class Delegating3748 extends DelegatingDeserializer {
        Delegating3748() { this(new BaseDeserializer3748()); }
        Delegating3748(ValueDeserializer<?> delegate) { super(delegate); }

        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> delegate) {
            return new Delegating3748(delegate);
        }
    }
static class BaseDeserializer3748 extends StdDeserializer<String> {
        BaseDeserializer3748() { super(String.class); }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext ctxt) { return null; }

        @Override
        public Object getEmptyValue(DeserializationContext ctxt) { return "empty"; }

        @Override
        public AccessPattern getEmptyAccessPattern() { return AccessPattern.ALWAYS_NULL; }

        @Override
        public Object getAbsentValue(DeserializationContext ctxt) { return "absent"; }

        @Override
        public ValueDeserializer<String> unwrappingDeserializer(DeserializationContext ctxt,
                NameTransformer unwrapper) {
            return new BaseDeserializer3748();
        }
    }

    void __invoke_testCustomBeanDeserializer() throws Exception {
        try {
            testCustomBeanDeserializer();
        } finally {
        }
    }


    void __invoke_testDelegating() throws Exception {
        try {
            testDelegating();
        } finally {
        }
    }


    void __invoke_testJsonNodeDelegating() throws Exception {
        try {
            testJsonNodeDelegating();
        } finally {
        }
    }


    void __invoke_testIssue882() throws Exception {
        try {
            testIssue882();
        } finally {
        }
    }


    void __invoke_testContextReadValue() throws Exception {
        try {
            testContextReadValue();
        } finally {
        }
    }


    void __invoke_testCurrentValueAccess() throws Exception {
        try {
            testCurrentValueAccess();
        } finally {
        }
    }


    void __invoke_testCustomStringDeser() throws Exception {
        try {
            testCustomStringDeser();
        } finally {
        }
    }


    void __invoke_testDelegatingDeserializer() throws Exception {
        try {
            testDelegatingDeserializer();
        } finally {
        }
    }


    void __invoke_testModifyingCustomDeserializer() throws Exception {
        try {
            testModifyingCustomDeserializer();
        } finally {
        }
    }


    void __invoke_testCustomDeserializerWithReadTree() throws Exception {
        try {
            testCustomDeserializerWithReadTree();
        } finally {
        }
    }


    void __invoke_testCustomDeserializerWithReadTreeAsValue() throws Exception {
        try {
            testCustomDeserializerWithReadTreeAsValue();
        } finally {
        }
    }


    void __invoke_testBasicDelegatingDeser() throws Exception {
        try {
            testBasicDelegatingDeser();
        } finally {
        }
    }

}
