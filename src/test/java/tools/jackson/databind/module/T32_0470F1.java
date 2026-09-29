package tools.jackson.databind.module;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.type.TypeModifier;
import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0470F1 {
private static final byte[] FOO_MAP = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 33 03");
private static final byte[] TYPE_MODIFIER_OUTPUT = VPackWireFixtureTest.hex(
            "0b 2d 01 55 54 33 32 5f 30 34 37 30 46 31 24 4d" +
                "79 54 79 70 65 49 6d 70 6c 0b 13 01 44 64 61 74" +
                "61 49 73 6f 6d 65 74 68 69 6e 67 03 03");

    void testKeyDeserializersVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addKeyDeserializer(Foo.class, new FooKeyDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Map<Foo, Integer> result = mapper.readValue(FOO_MAP,
                new TypeReference<Map<Foo, Integer>>() { });
        assertNotNull(result);
        assertEquals(1, result.size());
        Foo key = result.keySet().iterator().next();
        assertEquals("a", key.value);
    }
static final class CountingModule extends JacksonModule {
        private final AtomicInteger counter;
        private final Object id;

        CountingModule(AtomicInteger counter, Object id) {
            this.counter = counter;
            this.id = id;
        }

        @Override
        public Object getRegistrationId() {
            return id;
        }

        @Override
        public String getModuleName() {
            return "TestModule";
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public void setupModule(SetupContext context) {
            counter.incrementAndGet();
        }
    }
static final class FooKeyDeserializer extends KeyDeserializer {
        @Override
        public Foo deserializeKey(String key, DeserializationContext ctxt) {
            return new Foo(key);
        }
    }
static final class Foo {
        public String value;

        Foo(String value) {
            this.value = value;
        }
    }
interface MyType {
        String getData();
        void setData(String data);
    }
static final class MyTypeImpl implements MyType {
        private String data;

        @Override
        public String getData() {
            return data;
        }

        @Override
        public void setData(String data) {
            this.data = data;
        }
    }
static final class CustomTypeModifier extends TypeModifier {
        @Override
        public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context,
                TypeFactory typeFactory) {
            if (type.hasRawClass(MyTypeImpl.class)) {
                return typeFactory.constructType(MyType.class);
            }
            return type;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    interface Mixin { }

    void __invoke_testKeyDeserializersVpack() throws Exception {
        try {
            testKeyDeserializersVpack();
        } finally {
        }
    }

}
