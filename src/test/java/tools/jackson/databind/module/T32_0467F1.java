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
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0467F1 {
private static final byte[] INPUT = VPackWireFixtureTest.hex(
            "47 69 67 6e 6f 72 65 64");
private static final byte[] SERIALIZED_A = VPackWireFixtureTest.hex(
            "48 61 2d 72 65 73 75 6c 74");
private static final byte[] SERIALIZED_B = VPackWireFixtureTest.hex(
            "48 62 2d 72 65 73 75 6c 74");

    // Provenance: SimpleModuleArgCheckTest#testInvalidForDeserializers().
    void testInvalidForDeserializersVpack() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        IllegalArgumentException deserializer = assertThrows(IllegalArgumentException.class,
                () -> module.addDeserializer(String.class, null));
        assertTrue(deserializer.getMessage().contains("Cannot pass `null` as deserializer"));

        IllegalArgumentException keyDeserializer = assertThrows(IllegalArgumentException.class,
                () -> module.addKeyDeserializer(String.class, null));
        assertTrue(keyDeserializer.getMessage().contains("Cannot pass `null` as key deserializer"));
    }

    // Provenance: SimpleModuleArgCheckTest#testInvalidAbstractTypeMapping().
    void testInvalidAbstractTypeMappingVpack() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        IllegalArgumentException abstractType = assertThrows(IllegalArgumentException.class,
                () -> module.addAbstractTypeMapping(null, String.class));
        assertTrue(abstractType.getMessage().contains("Cannot pass `null` as abstract type to map"));

        IllegalArgumentException concreteType = assertThrows(IllegalArgumentException.class,
                () -> module.addAbstractTypeMapping(String.class, null));
        assertTrue(concreteType.getMessage().contains("Cannot pass `null` as concrete type to map to"));
    }

    // Provenance: SimpleModuleArgCheckTest#testInvalidSubtypeMappings().
    void testInvalidSubtypeMappingsVpack() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        IllegalArgumentException classSubtype = assertThrows(IllegalArgumentException.class,
                () -> module.registerSubtypes(String.class, null));
        assertTrue(classSubtype.getMessage().contains("Cannot pass `null` as subtype to register"));

        IllegalArgumentException namedSubtype = assertThrows(IllegalArgumentException.class,
                () -> module.registerSubtypes(new NamedType(Integer.class), (NamedType) null));
        assertTrue(namedSubtype.getMessage().contains("Cannot pass `null` as subtype to register"));
    }

    // Provenance: SimpleModuleArgCheckTest#testInvalidValueInstantiator().
    void testInvalidValueInstantiatorVpack() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        IllegalArgumentException type = assertThrows(IllegalArgumentException.class,
                () -> module.addValueInstantiator(null, null));
        assertTrue(type.getMessage().contains(
                "Cannot pass `null` as class to register value instantiator for"));

        IllegalArgumentException instantiator = assertThrows(IllegalArgumentException.class,
                () -> module.addValueInstantiator(CharSequence.class, null));
        assertTrue(instantiator.getMessage().contains(
                "Cannot pass `null` as value instantiator"));
    }

    // Provenance: SimpleModuleArgCheckTest#testInvalidMixIn().
    void testInvalidMixInVpack() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        IllegalArgumentException target = assertThrows(IllegalArgumentException.class,
                () -> module.setMixInAnnotation(null, String.class));
        assertTrue(target.getMessage().contains("Cannot pass `null` as target type"));

        IllegalArgumentException mixin = assertThrows(IllegalArgumentException.class,
                () -> module.setMixInAnnotation(String.class, null));
        assertTrue(mixin.getMessage().contains("Cannot pass `null` as mixin class"));
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

    void __invoke_testInvalidForDeserializersVpack() throws Exception {
        try {
            testInvalidForDeserializersVpack();
        } finally {
        }
    }


    void __invoke_testInvalidAbstractTypeMappingVpack() throws Exception {
        try {
            testInvalidAbstractTypeMappingVpack();
        } finally {
        }
    }


    void __invoke_testInvalidSubtypeMappingsVpack() throws Exception {
        try {
            testInvalidSubtypeMappingsVpack();
        } finally {
        }
    }


    void __invoke_testInvalidValueInstantiatorVpack() throws Exception {
        try {
            testInvalidValueInstantiatorVpack();
        } finally {
        }
    }


    void __invoke_testInvalidMixInVpack() throws Exception {
        try {
            testInvalidMixInVpack();
        } finally {
        }
    }

}
