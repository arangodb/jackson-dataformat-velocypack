package tools.jackson.databind.deser;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationContextExt;
import tools.jackson.databind.deser.impl.ErrorThrowingDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0177F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] BAR_FIELD = VPackWireFixtureTest.hex(
            "0b 13 01 43 62 61 72 4a 66 69 65 6c 64 56 61 6c 75 65 03");
private static final byte[] STRING_TEST_VALUE = VPackWireFixtureTest.hex(
            "4a 74 65 73 74 2d 76 61 6c 75 65");
private static final byte[] STRING_INVALID = VPackWireFixtureTest.hex(
            "47 69 6e 76 61 6c 69 64");
private static final byte[] STRING_BAD = VPackWireFixtureTest.hex(
            "43 62 61 64");
private static final byte[] STRING_TEST = VPackWireFixtureTest.hex(
            "44 74 65 73 74");
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 31 03");

    void testDeserializeThrowsError() throws Exception {
        NoClassDefFoundError error = new NoClassDefFoundError("com.example.Missing");
        ErrorThrowingDeserializer deser = new ErrorThrowingDeserializer(error);

        try (JsonParser parser = new VPackFactory().createParser(SIMPLE_OBJECT)) {
            NoClassDefFoundError thrown = assertThrows(NoClassDefFoundError.class,
                    () -> deser.deserialize(parser, null));
            assertSame(error, thrown);
            assertEquals("com.example.Missing", thrown.getMessage());
        }
    }

    
    void testViaModuleRegistration() {
        NoClassDefFoundError error = new NoClassDefFoundError("some.missing.Class");
        ErrorThrowingDeserializer deser = new ErrorThrowingDeserializer(error);
        SimpleModule module = new SimpleModule("test");
        module.addDeserializer(MyValue.class,
                (ValueDeserializer<MyValue>) (ValueDeserializer<?>) deser);

        ObjectMapper mapper = VPackMapper.builder()
                .addModule(module)
                .build();
        NoClassDefFoundError thrown = assertThrows(NoClassDefFoundError.class,
                () -> mapper.readValue(SIMPLE_OBJECT, MyValue.class));
        assertSame(error, thrown);
    }
private static SimpleModule barModule(ValueDeserializer<Bar> deserializer) {
        SimpleModule module = new SimpleModule("test");
        module.addDeserializer(Bar.class, deserializer);
        return module;
    }
private static void verifyIsFound(Class<?> rawType) {
        if (!verifyDeserializerExistence(rawType)) {
            fail("Should have explicit deserializer for " + rawType.getName());
        }
    }
private static void verifyNotFound(Class<?> rawType) {
        if (verifyDeserializerExistence(rawType)) {
            fail("Should NOT have explicit deserializer for " + rawType.getName());
        }
    }
private static boolean verifyDeserializerExistence(Class<?> rawType) {
        DeserializationContextExt context = MAPPER._deserializationContext();
        return context.hasExplicitDeserializerFor(rawType);
    }
static class POJO2539 { }
static class MyValue {
        public int x;
    }
static class Bar {
        private final String value;

        private Bar(String value) {
            this.value = value;
        }

        static Bar of(String value) {
            return new Bar(value);
        }

        String getValue() {
            return value;
        }
    }
static class BarWrapper {
        public Bar bar;
    }

    void __invoke_testDeserializeThrowsError() throws Exception {
        try {
            testDeserializeThrowsError();
        } finally {
        }
    }


    void __invoke_testViaModuleRegistration() throws Exception {
        try {
            testViaModuleRegistration();
        } finally {
        }
    }

}
