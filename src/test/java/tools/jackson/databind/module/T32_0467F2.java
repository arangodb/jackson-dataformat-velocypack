package tools.jackson.databind.module;

import java.util.Collection;
import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0467F2 {
private static final byte[] INPUT = VPackWireFixtureTest.hex(
            "47 69 67 6e 6f 72 65 64");
private static final byte[] SERIALIZED_A = VPackWireFixtureTest.hex(
            "48 61 2d 72 65 73 75 6c 74");
private static final byte[] SERIALIZED_B = VPackWireFixtureTest.hex(
            "48 62 2d 72 65 73 75 6c 74");

    // Provenance: SimpleModuleTest#testAccessToMapper().
    void testAccessToMapperVpack() {
        JacksonModule module = new JacksonModule() {
            @Override public String getModuleName() { return "owner-check"; }
            @Override public Version version() { return Version.unknownVersion(); }
            @Override public void setupModule(SetupContext context) {
                assertTrue(context.getOwner() instanceof MapperBuilder<?, ?>);
            }
        };
        assertNotNull(VPackMapper.builder().addModule(module).build());
    }

    // Provenance: SimpleModuleTest#testAddDeserializerTwiceThenOnlyLatestIsKept().
    void testAddDeserializerTwiceThenOnlyLatestIsKeptVpack() throws Exception {
        SimpleModule module = new SimpleModule()
                .addDeserializer(TestBean.class, new DeserializerA())
                .addDeserializer(TestBean.class, new DeserializerB());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertEquals("I am B", mapper.readValue(INPUT, TestBean.class).value);
    }

    // Provenance: SimpleModuleTest#testAddModuleWithSerializerTwiceThenOnlyLatestIsKept().
    void testAddModuleWithSerializerTwiceThenOnlyLatestIsKeptVpack() throws Exception {
        SimpleModule first = new SimpleModule().addSerializer(TestBean.class, new SerializerA());
        SimpleModule second = new SimpleModule().addSerializer(TestBean.class, new SerializerB());
        ObjectMapper mapper = VPackMapper.builder().addModule(first).addModule(second).build();
        assertArrayEquals(SERIALIZED_B, mapper.writeValueAsBytes(new TestBean()));
    }

    // Provenance: SimpleModuleTest#testAddModuleWithSerializerTwiceThenOnlyLatestIsKept_reverseOrder().
    void testAddModuleWithSerializerTwiceThenOnlyLatestIsKeptReverseOrderVpack() throws Exception {
        SimpleModule first = new SimpleModule().addSerializer(TestBean.class, new SerializerA());
        SimpleModule second = new SimpleModule().addSerializer(TestBean.class, new SerializerB());
        ObjectMapper mapper = VPackMapper.builder().addModule(second).addModule(first).build();
        assertArrayEquals(SERIALIZED_A, mapper.writeValueAsBytes(new TestBean()));
    }

    // Provenance: SimpleModuleTest#testAddModuleWithDeserializerTwiceThenOnlyLatestIsKept().
    void testAddModuleWithDeserializerTwiceThenOnlyLatestIsKeptVpack() throws Exception {
        SimpleModule first = new SimpleModule().addDeserializer(TestBean.class, new DeserializerA());
        SimpleModule second = new SimpleModule().addDeserializer(TestBean.class, new DeserializerB());
        ObjectMapper mapper = VPackMapper.builder().addModule(first).addModule(second).build();
        assertEquals("I am B", mapper.readValue(INPUT, TestBean.class).value);
    }

    // Provenance: SimpleModuleTest#testAddModuleWithDeserializerTwiceThenOnlyLatestIsKept_reverseOrder().
    void testAddModuleWithDeserializerTwiceThenOnlyLatestIsKeptReverseOrderVpack() throws Exception {
        SimpleModule first = new SimpleModule().addDeserializer(TestBean.class, new DeserializerA());
        SimpleModule second = new SimpleModule().addDeserializer(TestBean.class, new DeserializerB());
        ObjectMapper mapper = VPackMapper.builder().addModule(second).addModule(first).build();
        assertEquals("I am A", mapper.readValue(INPUT, TestBean.class).value);
    }
private static SimpleModule namedModule(String name) {
        return new SimpleModule(name, Version.unknownVersion());
    }
private static List<String> moduleNames(Collection<JacksonModule> modules) {
        return modules.stream().map(JacksonModule::getModuleName).toList();
    }
static class TestBean {
        public String value;
    }
static class DeserializerA extends ValueDeserializer<TestBean> {
        @Override public TestBean deserialize(JsonParser parser, DeserializationContext ctxt) {
            parser.skipChildren();
            TestBean bean = new TestBean();
            bean.value = "I am A";
            return bean;
        }
    }
static class DeserializerB extends ValueDeserializer<TestBean> {
        @Override public TestBean deserialize(JsonParser parser, DeserializationContext ctxt) {
            parser.skipChildren();
            TestBean bean = new TestBean();
            bean.value = "I am B";
            return bean;
        }
    }
static class SerializerA extends ValueSerializer<TestBean> {
        @Override public void serialize(TestBean value, JsonGenerator generator,
                SerializationContext ctxt) throws JacksonException {
            generator.writeString("a-result");
        }
    }
static class SerializerB extends ValueSerializer<TestBean> {
        @Override public void serialize(TestBean value, JsonGenerator generator,
                SerializationContext ctxt) throws JacksonException {
            generator.writeString("b-result");
        }
    }

    void __invoke_testAccessToMapperVpack() throws Exception {
        try {
            testAccessToMapperVpack();
        } finally {
        }
    }


    void __invoke_testAddDeserializerTwiceThenOnlyLatestIsKeptVpack() throws Exception {
        try {
            testAddDeserializerTwiceThenOnlyLatestIsKeptVpack();
        } finally {
        }
    }


    void __invoke_testAddModuleWithSerializerTwiceThenOnlyLatestIsKeptVpack() throws Exception {
        try {
            testAddModuleWithSerializerTwiceThenOnlyLatestIsKeptVpack();
        } finally {
        }
    }


    void __invoke_testAddModuleWithSerializerTwiceThenOnlyLatestIsKeptReverseOrderVpack() throws Exception {
        try {
            testAddModuleWithSerializerTwiceThenOnlyLatestIsKeptReverseOrderVpack();
        } finally {
        }
    }


    void __invoke_testAddModuleWithDeserializerTwiceThenOnlyLatestIsKeptVpack() throws Exception {
        try {
            testAddModuleWithDeserializerTwiceThenOnlyLatestIsKeptVpack();
        } finally {
        }
    }


    void __invoke_testAddModuleWithDeserializerTwiceThenOnlyLatestIsKeptReverseOrderVpack() throws Exception {
        try {
            testAddModuleWithDeserializerTwiceThenOnlyLatestIsKeptReverseOrderVpack();
        } finally {
        }
    }

}
