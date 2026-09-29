package tools.jackson.databind.module;

import java.util.Collection;
import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0467F0 {
private static final byte[] INPUT = VPackWireFixtureTest.hex(
            "47 69 67 6e 6f 72 65 64");
private static final byte[] SERIALIZED_A = VPackWireFixtureTest.hex(
            "48 61 2d 72 65 73 75 6c 74");
private static final byte[] SERIALIZED_B = VPackWireFixtureTest.hex(
            "48 62 2d 72 65 73 75 6c 74");

    // Provenance: BuilderModuleReuse5481Test#testBuilderReuseWithDifferentModules().
    void testBuilderReuseWithDifferentModulesVpack() {
        SimpleModule moduleA = namedModule("ModuleA");
        SimpleModule moduleB = namedModule("ModuleB");
        SimpleModule moduleC = namedModule("ModuleC");

        VPackMapper.Builder builder = VPackMapper.builder()
                .addModule(moduleA)
                .addModule(moduleB);
        VPackMapper mapper1 = builder.build();
        assertEquals(List.of("VPackModule", "ModuleA", "ModuleB"),
                moduleNames(mapper1.registeredModules()));

        builder.addModule(moduleC);
        VPackMapper mapper2 = builder.build();
        assertEquals(List.of("VPackModule", "ModuleA", "ModuleB", "ModuleC"),
                moduleNames(mapper2.registeredModules()));
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

    void __invoke_testBuilderReuseWithDifferentModulesVpack() throws Exception {
        try {
            testBuilderReuseWithDifferentModulesVpack();
        } finally {
        }
    }

}
