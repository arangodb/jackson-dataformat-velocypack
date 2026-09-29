package tools.jackson.databind.deser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonValueInstantiator;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.introspect.AnnotatedWithParams;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0175Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] OBJECT_A_B = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 41 62 03");
private static final byte[] OBJECT_A_3 = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 33 03");
private static final byte[] INTEGER_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] INTEGER_37 = VPackWireFixtureTest.hex("28 25");
private static final byte[] LONG_9876543210 = VPackWireFixtureTest.hex(
            "2f ea 16 b0 4c 02 00 00 00");
private static final byte[] DOUBLE_QUARTER = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 3f");
private static final byte[] BOOLEAN_TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] STRING_ABC = VPackWireFixtureTest.hex("43 61 62 63");

    void testCustomBeanInstantiator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MyBean.class, new MyBeanInstantiator()))
                .build();
        MyBean bean = mapper.readValue(EMPTY_OBJECT, MyBean.class);
        assertNotNull(bean);
        assertEquals("secret!", bean.secret);
    }

    void testCustomListInstantiator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MyList.class, new MyListInstantiator()))
                .build();
        MyList result = mapper.readValue(EMPTY_ARRAY, MyList.class);
        assertNotNull(result);
        assertEquals(MyList.class, result.getClass());
        assertEquals(0, result.size());
    }

    void testCustomMapInstantiator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MyMap.class, new MyMapInstantiator()))
                .build();
        MyMap result = mapper.readValue(OBJECT_A_B, MyMap.class);
        assertNotNull(result);
        assertEquals(MyMap.class, result.getClass());
        assertEquals(1, result.size());
    }

    void testDelegateBeanInstantiator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MyBean.class, new MyDelegateBeanInstantiator()))
                .build();
        MyBean bean = mapper.readValue(INTEGER_123, MyBean.class);
        assertNotNull(bean);
        assertEquals("123", bean.secret);
    }

    void testDelegateListInstantiator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MyList.class, new MyDelegateListInstantiator()))
                .build();
        MyList result = mapper.readValue(INTEGER_123, MyList.class);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(123), result.get(0));
    }

    void testDelegateMapInstantiator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MyMap.class, new MyDelegateMapInstantiator()))
                .build();
        MyMap result = mapper.readValue(INTEGER_123, MyMap.class);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(123), result.values().iterator().next());
    }

    void testCustomDelegateInstantiator() throws Exception {
        AnnotatedBeanDelegating value = MAPPER.readValue(OBJECT_A_3,
                AnnotatedBeanDelegating.class);
        assertNotNull(value);
        Object ob = value.value;
        assertNotNull(ob);
        assertInstanceOf(Map.class, ob);
    }

    void testBeanFromString() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MysteryBean.class, new InstantiatorBase() {
                    @Override
                    public boolean canCreateFromString() { return true; }

                    @Override
                    public Object createFromString(DeserializationContext ctxt, String value) {
                        return new MysteryBean(value);
                    }
                }))
                .build();
        MysteryBean result = mapper.readValue(STRING_ABC, MysteryBean.class);
        assertNotNull(result);
        assertEquals("abc", result.value);
    }

    void testBeanFromInt() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MysteryBean.class, new InstantiatorBase() {
                    @Override
                    public boolean canCreateFromInt() { return true; }

                    @Override
                    public Object createFromInt(DeserializationContext ctxt, int value) {
                        return new MysteryBean(value + 1);
                    }
                }))
                .build();
        MysteryBean result = mapper.readValue(INTEGER_37, MysteryBean.class);
        assertNotNull(result);
        assertEquals(Integer.valueOf(38), result.value);
    }

    void testBeanFromLong() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MysteryBean.class, new InstantiatorBase() {
                    @Override
                    public boolean canCreateFromLong() { return true; }

                    @Override
                    public Object createFromLong(DeserializationContext ctxt, long value) {
                        return new MysteryBean(value + 1L);
                    }
                }))
                .build();
        MysteryBean result = mapper.readValue(LONG_9876543210, MysteryBean.class);
        assertNotNull(result);
        assertEquals(Long.valueOf(9876543211L), result.value);
    }

    void testBeanFromDouble() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MysteryBean.class, new InstantiatorBase() {
                    @Override
                    public boolean canCreateFromDouble() { return true; }

                    @Override
                    public Object createFromDouble(DeserializationContext ctxt, double value) {
                        return new MysteryBean(2.0 * value);
                    }
                }))
                .build();
        MysteryBean result = mapper.readValue(DOUBLE_QUARTER, MysteryBean.class);
        assertNotNull(result);
        assertEquals(Double.valueOf(0.5), result.value);
    }

    void testBeanFromBoolean() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new MyModule(MysteryBean.class, new InstantiatorBase() {
                    @Override
                    public boolean canCreateFromBoolean() { return true; }

                    @Override
                    public Object createFromBoolean(DeserializationContext ctxt, boolean value) {
                        return new MysteryBean(Boolean.valueOf(value));
                    }
                }))
                .build();
        MysteryBean result = mapper.readValue(BOOLEAN_TRUE, MysteryBean.class);
        assertNotNull(result);
        assertEquals(Boolean.TRUE, result.value);
    }
static class MyBean {
        String secret;

        protected MyBean(String value, boolean ignored) {
            secret = value;
        }
    }
static class MysteryBean {
        Object value;

        MysteryBean(Object value) {
            this.value = value;
        }
    }
static class MyList extends ArrayList<Object> {
        MyList(boolean ignored) { }
    }
static class MyMap extends HashMap<String, Object> {
        MyMap(boolean ignored) { }
    }
static abstract class InstantiatorBase extends ValueInstantiator.Base {
        InstantiatorBase() { super(Object.class); }

        @Override
        public String getValueTypeDesc() { return "UNKNOWN"; }

        @Override
        public boolean canCreateUsingDelegate() { return false; }
    }
static class MyBeanInstantiator extends InstantiatorBase {
        @Override
        public boolean canCreateUsingDefault() { return true; }

        @Override
        public MyBean createUsingDefault(DeserializationContext ctxt) {
            return new MyBean("secret!", true);
        }
    }
static class MyListInstantiator extends InstantiatorBase {
        @Override
        public boolean canCreateUsingDefault() { return true; }

        @Override
        public MyList createUsingDefault(DeserializationContext ctxt) {
            return new MyList(true);
        }
    }
static class MyMapInstantiator extends InstantiatorBase {
        @Override
        public boolean canCreateUsingDefault() { return true; }

        @Override
        public MyMap createUsingDefault(DeserializationContext ctxt) {
            return new MyMap(true);
        }
    }
static class MyDelegateBeanInstantiator extends ValueInstantiator.Base {
        MyDelegateBeanInstantiator() { super(Object.class); }

        @Override
        public boolean canCreateUsingDelegate() { return true; }

        @Override
        public JavaType getDelegateType(DeserializationConfig config) {
            return config.constructType(Object.class);
        }

        @Override
        public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) {
            return new MyBean(String.valueOf(delegate), true);
        }
    }
static class MyDelegateListInstantiator extends ValueInstantiator.Base {
        MyDelegateListInstantiator() { super(Object.class); }

        @Override
        public boolean canCreateUsingDelegate() { return true; }

        @Override
        public JavaType getDelegateType(DeserializationConfig config) {
            return config.constructType(Object.class);
        }

        @Override
        public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) {
            MyList list = new MyList(true);
            list.add(delegate);
            return list;
        }
    }
static class MyDelegateMapInstantiator extends ValueInstantiator.Base {
        MyDelegateMapInstantiator() { super(Object.class); }

        @Override
        public boolean canCreateUsingDelegate() { return true; }

        @Override
        public JavaType getDelegateType(DeserializationConfig config) {
            return config.constructType(Object.class);
        }

        @Override
        public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) {
            MyMap map = new MyMap(true);
            map.put("value", delegate);
            return map;
        }
    }
@JsonValueInstantiator(AnnotatedBeanDelegatingInstantiator.class)
    static class AnnotatedBeanDelegating {
        final Object value;

        AnnotatedBeanDelegating(Object value, boolean ignored) {
            this.value = value;
        }
    }
static class AnnotatedBeanDelegatingInstantiator extends InstantiatorBase {
        @Override
        public boolean canCreateUsingDelegate() { return true; }

        @Override
        public JavaType getDelegateType(DeserializationConfig config) {
            return config.constructType(Map.class);
        }

        @Override
        public AnnotatedWithParams getDelegateCreator() { return null; }

        @Override
        public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) {
            return new AnnotatedBeanDelegating(delegate, false);
        }
    }
static class MyModule extends SimpleModule {
        MyModule(Class<?> type, ValueInstantiator instantiator) {
            super("T32-0175", Version.unknownVersion());
            addValueInstantiator(type, instantiator);
        }
    }

    void __invoke_testCustomBeanInstantiator() throws Exception {
        try {
            testCustomBeanInstantiator();
        } finally {
        }
    }


    void __invoke_testCustomListInstantiator() throws Exception {
        try {
            testCustomListInstantiator();
        } finally {
        }
    }


    void __invoke_testCustomMapInstantiator() throws Exception {
        try {
            testCustomMapInstantiator();
        } finally {
        }
    }


    void __invoke_testDelegateBeanInstantiator() throws Exception {
        try {
            testDelegateBeanInstantiator();
        } finally {
        }
    }


    void __invoke_testDelegateListInstantiator() throws Exception {
        try {
            testDelegateListInstantiator();
        } finally {
        }
    }


    void __invoke_testDelegateMapInstantiator() throws Exception {
        try {
            testDelegateMapInstantiator();
        } finally {
        }
    }


    void __invoke_testCustomDelegateInstantiator() throws Exception {
        try {
            testCustomDelegateInstantiator();
        } finally {
        }
    }


    void __invoke_testBeanFromString() throws Exception {
        try {
            testBeanFromString();
        } finally {
        }
    }


    void __invoke_testBeanFromInt() throws Exception {
        try {
            testBeanFromInt();
        } finally {
        }
    }


    void __invoke_testBeanFromLong() throws Exception {
        try {
            testBeanFromLong();
        } finally {
        }
    }


    void __invoke_testBeanFromDouble() throws Exception {
        try {
            testBeanFromDouble();
        } finally {
        }
    }


    void __invoke_testBeanFromBoolean() throws Exception {
        try {
            testBeanFromBoolean();
        } finally {
        }
    }

}
