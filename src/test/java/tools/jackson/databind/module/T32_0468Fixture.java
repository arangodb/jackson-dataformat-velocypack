package tools.jackson.databind.module;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.module.SimpleDeserializers;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.module.SimpleSerializers;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0468Fixture {
private static final byte[] UNKNOWN_PROPERTY_OBJECT = VPackWireFixtureTest.hex(
            "0b 11 02 43 6e 75 6d 32 43 73 74 72 42 61 62 03 08");
private static final byte[] CUSTOM_BEAN_STRING = VPackWireFixtureTest.hex(
            "45 78 79 7a 7c 33");
private static final byte[] ENUM_A = VPackWireFixtureTest.hex("41 61");
private static final byte[] ENUM_B = VPackWireFixtureTest.hex("41 62");
private static final byte[] CUSTOM_BEAN_OUTPUT = VPackWireFixtureTest.hex(
            "47 61 62 63 64 65 7c 35");
private static final byte[] MIXIN_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0f 03 41 63 33 41 61 31 41 62 32 06 09 03");

    void testDeserializationWithoutModuleVpack() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        assertUnknownProperty(() -> mapper.readValue(UNKNOWN_PROPERTY_OBJECT, CustomBean.class));
        assertUnknownProperty(() -> mapper.readValue(new ByteArrayInputStream(
                UNKNOWN_PROPERTY_OBJECT), CustomBean.class));
    }
private static void assertUnknownProperty(ThrowingRead read) throws Exception {
        DatabindException exception = assertThrows(DatabindException.class, read::read);
        assertTrue(exception.getMessage().contains("Unrecognized"),
                () -> "unexpected exception message: " + exception.getMessage());
    }

    void testSerializationWithoutModuleVpack() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
        try {
            mapper.writeValueAsBytes(new CustomBean("foo", 3));
            fail("Should have caused an exception");
        } catch (DatabindException e) {
            assertTrue(e.getMessage().contains("No serializer found"));
        }
        try {
            mapper.writeValueAsBytes(new CustomBean("foo", 3));
            fail("Should have caused an exception");
        } catch (DatabindException e) {
            assertTrue(e.getMessage().contains("No serializer found"));
        }
    }

    void testSimpleBeanSerializerVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addSerializer(new CustomBeanSerializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertArrayEquals(CUSTOM_BEAN_OUTPUT,
                mapper.writeValueAsBytes(new CustomBean("abcde", 5)));
    }

    void testSimpleBeanDeserializerVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(CustomBean.class, new CustomBeanDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        CustomBean bean = mapper.readValue(CUSTOM_BEAN_STRING, CustomBean.class);
        assertEquals("xyz", bean.str);
        assertEquals(3, bean.num);
    }

    void testSimpleEnumDeserializerVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(SimpleEnum.class, new SimpleEnumDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertSame(SimpleEnum.A, mapper.readValue(ENUM_A, SimpleEnum.class));
    }

    void testMultipleModulesVpack() throws Exception {
        MySimpleModule first = new MySimpleModule("test1", Version.unknownVersion());
        SimpleModule second = new SimpleModule("test2", Version.unknownVersion());
        first.addSerializer(SimpleEnum.class, new SimpleEnumSerializer());
        first.addDeserializer(CustomBean.class, new CustomBeanDeserializer());

        Map<Class<?>, ValueDeserializer<?>> deserializers = new HashMap<>();
        deserializers.put(SimpleEnum.class, new SimpleEnumDeserializer());
        second.setDeserializers(new SimpleDeserializers(deserializers));
        second.addSerializer(CustomBean.class, new CustomBeanSerializer());

        VPackMapper mapper = VPackMapper.builder().addModule(first).addModule(second).build();
        assertArrayEquals(ENUM_B, mapper.writeValueAsBytes(SimpleEnum.B));
        assertSame(SimpleEnum.A, mapper.readValue(ENUM_A, SimpleEnum.class));

        mapper = VPackMapper.builder().addModule(second).addModule(first).build();
        assertArrayEquals(ENUM_B, mapper.writeValueAsBytes(SimpleEnum.B));
        assertSame(SimpleEnum.A, mapper.readValue(ENUM_A, SimpleEnum.class));
    }

    void testGetRegisteredModulesVpack() {
        MySimpleModule first = new MySimpleModule("test1", Version.unknownVersion());
        AnotherSimpleModule second = new AnotherSimpleModule("test2", Version.unknownVersion());
        VPackMapper mapper = VPackMapper.builder().addModule(first).addModule(second).build();
        List<JacksonModule> modules = registeredModules(mapper);
        assertEquals(List.of("VPackModule", "test1", "test2"),
                modules.stream().map(JacksonModule::getModuleName).toList());

        mapper = VPackMapper.builder().build();
        assertEquals(1, registeredModules(mapper).size());

        mapper = VPackMapper.builder().addModule(new SimpleModule()).build();
        assertEquals(2, registeredModules(mapper).size());
        Object id = mapper.registeredModules().stream()
                .map(JacksonModule::getRegistrationId)
                .filter(value -> value.toString().startsWith("SimpleModule-"))
                .findFirst().orElseThrow();
        assertTrue(id.toString().startsWith("SimpleModule-"));

        JacksonModule named = new SimpleModule("VerySpecialModule");
        mapper = VPackMapper.builder().addModule(named).build();
        assertSame(named, registeredModules(mapper).stream()
                .filter(module -> module.getModuleName().equals("VerySpecialModule"))
                .findFirst().orElseThrow());
    }

    void testMultipleSimpleModulesVpack() {
        SimpleModule first = new SimpleModule();
        SimpleModule second = new SimpleModule();
        assertEquals(3, registeredModules(VPackMapper.builder()
                .addModule(first).addModule(second).build()).size());
        assertEquals(2, registeredModules(VPackMapper.builder()
                .addModule(first).addModule(first).build()).size());

        SimpleModule subFirst = new SimpleModule() { };
        SimpleModule subSecond = new SimpleModule() { };
        assertEquals(3, registeredModules(VPackMapper.builder()
                .addModule(subFirst).addModule(subSecond).build()).size());
        assertEquals(2, registeredModules(VPackMapper.builder()
                .addModule(subFirst).addModule(subFirst).build()).size());
    }

    void testMixInsVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.setMixInAnnotation(MixableBean.class, MixInForOrder.class);
        VPackMapper mapper = VPackMapper.builder().addModule(module).build();
        assertArrayEquals(MIXIN_OUTPUT, mapper.writeValueAsBytes(new MixableBean()));
    }

    void testAutoDiscoveryVpack() {
        assertEquals(0, MapperBuilder.findModules().size());
    }

    void testAddSerializerTwiceThenOnlyLatestIsKeptVpack() throws Exception {
        SimpleModule module = new SimpleModule()
                .addSerializer(Test3787Bean.class, new Serializer3787A())
                .addSerializer(Test3787Bean.class, new Serializer3787B());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertArrayEquals(VPackWireFixtureTest.hex("48 62 2d 72 65 73 75 6c 74"),
                mapper.writeValueAsBytes(new Test3787Bean()));
    }

    void testDuplicateModules5063Vpack() {
        VPackMapper mapper = VPackMapper.builder()
                .addModule(new Module5063A()).addModule(new Module5063B()).build();
        assertEquals(3, registeredModules(mapper).size());
    }
private static List<JacksonModule> registeredModules(ObjectMapper mapper) {
        return new ArrayList<>(mapper.registeredModules());
    }
@FunctionalInterface
    interface ThrowingRead { Object read() throws Exception; }
static final class CustomBean {
        protected String str;
        protected Integer num;
        public CustomBean() { }
        public CustomBean(String str, Integer num) { this.str = str; this.num = num; }
    }
enum SimpleEnum { A, B }
static class CustomBeanSerializer extends tools.jackson.databind.ser.std.StdSerializer<CustomBean> {
        CustomBeanSerializer() { super(CustomBean.class); }
        @Override public void serialize(CustomBean value, JsonGenerator generator,
                SerializationContext provider) throws JacksonException {
            generator.writeString(value.str + "|" + value.num);
        }
    }
static class CustomBeanDeserializer extends ValueDeserializer<CustomBean> {
        @Override public CustomBean deserialize(JsonParser parser, DeserializationContext ctxt) {
            String text = parser.getString();
            int split = text.indexOf('|');
            if (split < 0) throw new IllegalArgumentException("missing separator");
            return new CustomBean(text.substring(0, split),
                    Integer.parseInt(text.substring(split + 1)));
        }
    }
static class SimpleEnumSerializer extends tools.jackson.databind.ser.std.StdSerializer<SimpleEnum> {
        SimpleEnumSerializer() { super(SimpleEnum.class); }
        @Override public void serialize(SimpleEnum value, JsonGenerator generator,
                SerializationContext provider) throws JacksonException {
            generator.writeString(value.name().toLowerCase());
        }
    }
static class SimpleEnumDeserializer extends ValueDeserializer<SimpleEnum> {
        @Override public SimpleEnum deserialize(JsonParser parser, DeserializationContext ctxt) {
            return SimpleEnum.valueOf(parser.getString().toUpperCase());
        }
    }
static class MySimpleSerializers extends SimpleSerializers { }
static class MySimpleDeserializers extends SimpleDeserializers { }
static class MySimpleModule extends SimpleModule {
        MySimpleModule(String name, Version version) {
            super(name, version);
            _deserializers = new MySimpleDeserializers();
            _serializers = new MySimpleSerializers();
        }
    }
static class AnotherSimpleModule extends SimpleModule {
        AnotherSimpleModule(String name, Version version) { super(name, version); }
    }
@JsonPropertyOrder({"c", "a", "b"})
    static class MixInForOrder { }
static class MixableBean {
        public int a = 1;
        public int b = 2;
        public int c = 3;
    }
static class Test3787Bean { }
static class Serializer3787A extends ValueSerializer<Test3787Bean> {
        @Override public void serialize(Test3787Bean value, JsonGenerator generator,
                SerializationContext provider) throws JacksonException {
            generator.writeString("a-result");
        }
    }
static class Serializer3787B extends ValueSerializer<Test3787Bean> {
        @Override public void serialize(Test3787Bean value, JsonGenerator generator,
                SerializationContext provider) throws JacksonException {
            generator.writeString("b-result");
        }
    }
static class Module5063A extends SimpleModule {
        Module5063A() { super(Version.unknownVersion()); }
    }
static class Module5063B extends SimpleModule {
        Module5063B() { super(Version.unknownVersion()); }
    }

    void __invoke_testDeserializationWithoutModuleVpack() throws Exception {
        try {
            testDeserializationWithoutModuleVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithoutModuleVpack() throws Exception {
        try {
            testSerializationWithoutModuleVpack();
        } finally {
        }
    }


    void __invoke_testSimpleBeanSerializerVpack() throws Exception {
        try {
            testSimpleBeanSerializerVpack();
        } finally {
        }
    }


    void __invoke_testSimpleBeanDeserializerVpack() throws Exception {
        try {
            testSimpleBeanDeserializerVpack();
        } finally {
        }
    }


    void __invoke_testSimpleEnumDeserializerVpack() throws Exception {
        try {
            testSimpleEnumDeserializerVpack();
        } finally {
        }
    }


    void __invoke_testMultipleModulesVpack() throws Exception {
        try {
            testMultipleModulesVpack();
        } finally {
        }
    }


    void __invoke_testGetRegisteredModulesVpack() throws Exception {
        try {
            testGetRegisteredModulesVpack();
        } finally {
        }
    }


    void __invoke_testMultipleSimpleModulesVpack() throws Exception {
        try {
            testMultipleSimpleModulesVpack();
        } finally {
        }
    }


    void __invoke_testMixInsVpack() throws Exception {
        try {
            testMixInsVpack();
        } finally {
        }
    }


    void __invoke_testAutoDiscoveryVpack() throws Exception {
        try {
            testAutoDiscoveryVpack();
        } finally {
        }
    }


    void __invoke_testAddSerializerTwiceThenOnlyLatestIsKeptVpack() throws Exception {
        try {
            testAddSerializerTwiceThenOnlyLatestIsKeptVpack();
        } finally {
        }
    }


    void __invoke_testDuplicateModules5063Vpack() throws Exception {
        try {
            testDuplicateModules5063Vpack();
        } finally {
        }
    }

}
