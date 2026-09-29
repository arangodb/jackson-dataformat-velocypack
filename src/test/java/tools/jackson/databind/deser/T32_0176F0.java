package tools.jackson.databind.deser;

import java.util.HashMap;

import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonValueInstantiator;
import tools.jackson.databind.deser.CreatorProperty;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0176F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] PROPERTY_BEAN = VPackWireFixtureTest.hex(
            "0b 16 02 46 73 65 63 72 65 74 28 7b 45 76 61 6c 75 65 28 25 03 0c");
private static final byte[] PROPERTY_MAP = VPackWireFixtureTest.hex(
            "0b 12 02 44 6e 61 6d 65 43 62 6f 62 41 78 41 79 03 0c");
private static final byte[] POLYMORPHIC = VPackWireFixtureTest.hex(
            "0b 4c 02 44 74 79 70 65 77 74 6f 6f 6c 73 2e 6a" +
                "61 63 6b 73 6f 6e 2e 64 61 74 61 62 69 6e 64 2e" +
                "64 65 73 65 72 2e 54 33 32 5f 30 31 37 36 46 30" +
                "24 50 6f 6c 79 6d 6f 72 70 68 69 63 42 65 61 6e" +
                "44 6e 61 6d 65 44 41 78 65 6c 40 03");
private static final byte[] STRING_FOO = VPackWireFixtureTest.hex(
            "43 66 6f 6f");
private static final byte[] CONCURRENCY_OBJECT = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 2a 03");

    void testPropertyBasedBeanInstantiator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(CreatorBean.class, new InstantiatorBase() {
                    @Override
                    public boolean canCreateFromObjectWith() { return true; }

                    @Override
                    public CreatorProperty[] getFromObjectArguments(DeserializationConfig config) {
                        return new CreatorProperty[] {
                                CreatorProperty.construct(new PropertyName("secret"),
                                        config.constructType(String.class), null, null, null,
                                        null, 0, null, PropertyMetadata.STD_REQUIRED)
                        };
                    }

                    @Override
                    public Object createFromObjectWith(DeserializationContext ctxt,
                            Object[] args) {
                        return new CreatorBean((String) args[0]);
                    }
                }))
                .build();
        CreatorBean bean = mapper.readValue(PROPERTY_BEAN, CreatorBean.class);
        assertNotNull(bean);
        assertEquals("123", bean.secret);
    }

    void testPropertyBasedMapInstantiator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MyMap.class, new CreatorMapInstantiator()))
                .build();
        MyMap result = mapper.readValue(PROPERTY_MAP, MyMap.class);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("bob", result.get("bob"));
        assertEquals("y", result.get("x"));
    }

    void testPolymorphicCreatorBean() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(PolymorphicBeanBase.class,
                        new PolymorphicBeanInstantiator()))
                .build();
        PolymorphicBeanBase result = mapper.readValue(POLYMORPHIC,
                PolymorphicBeanBase.class);
        assertNotNull(result);
        assertSame(PolymorphicBean.class, result.getClass());
        assertEquals("Axel", ((PolymorphicBean) result).name);
    }

    void testEmptyBean() throws Exception {
        AnnotatedBean bean = MAPPER.readValue(EMPTY_OBJECT, AnnotatedBean.class);
        assertNotNull(bean);
        assertEquals("foo", bean.a);
        assertEquals(3, bean.b);
    }

    void testErrorMessageForMissingCtor() {
        InvalidDefinitionException exception = assertThrows(
                InvalidDefinitionException.class,
                () -> MAPPER.readValue(EMPTY_OBJECT, MyBean.class));
        assertTrue(exception.getMessage().contains("Cannot construct instance of"));
        assertTrue(exception.getMessage().contains("no Creators"));
    }

    void testErrorMessageForMissingStringCtor() {
        InvalidDefinitionException exception = assertThrows(
                InvalidDefinitionException.class,
                () -> MAPPER.readValue(STRING_FOO, MyBean.class));
        assertTrue(exception.getMessage().contains("Cannot construct instance of"));
        assertTrue(exception.getMessage().contains(
                "no String-argument constructor/factory"));
    }
static class MyBean {
        String secret;

        protected MyBean(String value, boolean ignored) {
            secret = value;
        }
    }
static class CreatorBean {
        String secret;
        public String value;

        protected CreatorBean(String value) {
            secret = value;
        }
    }
static class MyMap extends HashMap<String, Object> {
        MyMap(String name) {
            put(name, name);
        }
    }
static class CreatorMapInstantiator extends InstantiatorBase {
        @Override
        public boolean canCreateFromObjectWith() { return true; }

        @Override
        public CreatorProperty[] getFromObjectArguments(DeserializationConfig config) {
            return new CreatorProperty[] {
                    CreatorProperty.construct(new PropertyName("name"),
                            config.constructType(String.class), null, null, null, null,
                            0, null, PropertyMetadata.STD_REQUIRED)
            };
        }

        @Override
        public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
            return new MyMap((String) args[0]);
        }
    }
static abstract class InstantiatorBase extends ValueInstantiator.Base {
        InstantiatorBase() { super(Object.class); }

        @Override
        public String getValueTypeDesc() { return "UNKNOWN"; }

        @Override
        public boolean canCreateUsingDelegate() { return false; }
    }
static abstract class PolymorphicBeanBase { }
static class PolymorphicBean extends PolymorphicBeanBase {
        public String name;
    }
static class PolymorphicBeanInstantiator extends InstantiatorBase {
        @Override
        public boolean canCreateFromObjectWith() { return true; }

        @Override
        public CreatorProperty[] getFromObjectArguments(DeserializationConfig config) {
            return new CreatorProperty[] {
                    CreatorProperty.construct(new PropertyName("type"),
                            config.constructType(Class.class), null, null, null, null,
                            0, null, PropertyMetadata.STD_REQUIRED)
            };
        }

        @Override
        public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
            try {
                return ((Class<?>) args[0]).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
@JsonValueInstantiator(AnnotatedBeanInstantiator.class)
    static class AnnotatedBean {
        final String a;
        final int b;

        AnnotatedBean(String a, int b) {
            this.a = a;
            this.b = b;
        }
    }
static class AnnotatedBeanInstantiator extends InstantiatorBase {
        @Override
        public boolean canCreateUsingDefault() { return true; }

        @Override
        public Object createUsingDefault(DeserializationContext ctxt) {
            return new AnnotatedBean("foo", 3);
        }
    }
@JsonDeserialize(using = TestBeanDeserializer.class)
    static class Bean {
        public int value = 42;
    }
static class TestBeanDeserializer extends ValueDeserializer<Bean> {
        protected volatile boolean resolved;

        @Override
        public Bean deserialize(JsonParser p, DeserializationContext ctxt) {
            if (!resolved) {
                ctxt.reportInputMismatch(Bean.class,
                        "Deserializer not yet completely resolved");
            }
            p.skipChildren();
            Bean bean = new Bean();
            bean.value = 13;
            return bean;
        }

        @Override
        public void resolve(DeserializationContext ctxt) {
            try {
                Thread.sleep(100L);
            } catch (Exception e) {
                // Preserve the source test's deliberately simple race setup.
            }
            resolved = true;
        }
    }
static class MyModule extends SimpleModule {
        MyModule(Class<?> type, ValueInstantiator instantiator) {
            super("T32-0176", Version.unknownVersion());
            addValueInstantiator(type, instantiator);
        }
    }

    void __invoke_testPropertyBasedBeanInstantiator() throws Exception {
        try {
            testPropertyBasedBeanInstantiator();
        } finally {
        }
    }


    void __invoke_testPropertyBasedMapInstantiator() throws Exception {
        try {
            testPropertyBasedMapInstantiator();
        } finally {
        }
    }


    void __invoke_testPolymorphicCreatorBean() throws Exception {
        try {
            testPolymorphicCreatorBean();
        } finally {
        }
    }


    void __invoke_testEmptyBean() throws Exception {
        try {
            testEmptyBean();
        } finally {
        }
    }


    void __invoke_testErrorMessageForMissingCtor() throws Exception {
        try {
            testErrorMessageForMissingCtor();
        } finally {
        }
    }


    void __invoke_testErrorMessageForMissingStringCtor() throws Exception {
        try {
            testErrorMessageForMissingStringCtor();
        } finally {
        }
    }

}
