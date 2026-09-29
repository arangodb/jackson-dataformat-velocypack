package tools.jackson.databind.deser.inject;

import java.util.concurrent.ArrayBlockingQueue;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0250Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] VALUE_OBJECT = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 33 01");
private static final byte[] AGE_OBJECT = VPackWireFixtureTest.hex(
            "14 09 43 61 67 65 28 37 01");
private static final byte[] ISSUE_471_OBJECT = VPackWireFixtureTest.hex(
            "14 4b 41 78 28 0d "
                    + "51 63 6f 6e 73 74 72 75 63 74 6f 72 5f 76 61 6c 75 65 "
                    + "4b 63 6f 6e 73 74 72 75 63 74 6f 72 "
                    + "4c 6d 65 74 68 6f 64 5f 76 61 6c 75 65 "
                    + "46 6d 65 74 68 6f 64 "
                    + "4b 66 69 65 6c 64 5f 76 61 6c 75 65 "
                    + "45 66 69 65 6c 64 04");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] FIVE_INTS = VPackWireFixtureTest.hex(
            "02 07 31 32 33 34 35");
private static final byte[] EMPTY_QUEUE_PROPERTY = VPackWireFixtureTest.hex(
            "14 0a 45 69 74 65 6d 73 01 01");
private static final byte[] STRING_QUEUE_PROPERTY = VPackWireFixtureTest.hex(
            "14 12 45 69 74 65 6d 73 13 09 41 78 41 79 41 7a 03 01");
private static final byte[] INT_QUEUE_PROPERTY = VPackWireFixtureTest.hex(
            "14 14 47 6e 75 6d 62 65 72 73 13 09 28 0a 28 14 28 1e 03 01");
private static final byte[] NULL_QUEUE = VPackWireFixtureTest.hex(
            "13 0b 18 45 68 65 6c 6c 6f 18 03");
private static final byte[] LARGE_QUEUE = VPackWireFixtureTest.hex(
            "13 c2 01 30 31 32 33 34 35 36 37 38 39 "
                    + "28 0a 28 0b 28 0c 28 0d 28 0e 28 0f 28 10 28 11 "
                    + "28 12 28 13 28 14 28 15 28 16 28 17 28 18 28 19 "
                    + "28 1a 28 1b 28 1c 28 1d 28 1e 28 1f 28 20 28 21 "
                    + "28 22 28 23 28 24 28 25 28 26 28 27 28 28 28 29 "
                    + "28 2a 28 2b 28 2c 28 2d 28 2e 28 2f 28 30 28 31 "
                    + "28 32 28 33 28 34 28 35 28 36 28 37 28 38 28 39 "
                    + "28 3a 28 3b 28 3c 28 3d 28 3e 28 3f 28 40 28 41 "
                    + "28 42 28 43 28 44 28 45 28 46 28 47 28 48 28 49 "
                    + "28 4a 28 4b 28 4c 28 4d 28 4e 28 4f 28 50 28 51 "
                    + "28 52 28 53 28 54 28 55 28 56 28 57 28 58 28 59 "
                    + "28 5a 28 5b 28 5c 28 5d 28 5e 28 5f 28 60 28 61 "
                    + "28 62 28 63 64");

    void testSimple() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .injectableValues(new InjectableValues.Std()
                        .addValue(String.class, "stuffValue")
                        .addValue("myId", "xyz")
                        .addValue(Long.TYPE, Long.valueOf(37)))
                .build();

        InjectedBean bean = mapper.readValue(VALUE_OBJECT, InjectedBean.class);
        assertEquals(3, bean.value);
        assertEquals("stuffValue", bean.stuff);
        assertEquals("xyz", bean.otherStuff);
        assertEquals(37L, bean.third);
    }

    void testWithCtors() throws Exception {
        CtorBean bean = MAPPER.readerFor(CtorBean.class)
                .with(new InjectableValues.Std().addValue(String.class, "Bubba"))
                .readValue(AGE_OBJECT);
        assertEquals(55, bean.age);
        assertEquals("Bubba", bean.name);
    }

    void testTwoInjectablesViaCreator() throws Exception {
        CtorBean2 bean = MAPPER.readerFor(CtorBean2.class)
                .with(new InjectableValues.Std()
                        .addValue(String.class, "Bob")
                        .addValue("number", Integer.valueOf(13)))
                .readValue(EMPTY_OBJECT);
        assertEquals(Integer.valueOf(13), bean.age);
        assertEquals("Bob", bean.name);
    }

    void testIssue471() throws Exception {
        final Object constructorInjected = new Object();
        final Object methodInjected = new Object();
        final Object fieldInjected = new Object();
        ObjectMapper mapper = VPackMapper.builder()
                .injectableValues(new InjectableValues.Std()
                        .addValue("constructor_injected", constructorInjected)
                        .addValue("method_injected", methodInjected)
                        .addValue("field_injected", fieldInjected))
                .build();

        Bean471 bean = mapper.readValue(ISSUE_471_OBJECT, Bean471.class);
        assertSame(constructorInjected, bean.constructorInjected);
        assertSame(methodInjected, bean.methodInjected);
        assertSame(fieldInjected, bean.fieldInjected);
        assertEquals("constructor", bean.constructorValue);
        assertEquals("method", bean.methodValue);
        assertEquals("field", bean.fieldValue);
        assertEquals(13, bean.x);
    }

    void testTransientField() throws Exception {
        TransientBean bean = MAPPER.readerFor(TransientBean.class)
                .with(new InjectableValues.Std().addValue("transient", "Injected!"))
                .readValue(VPackWireFixtureTest.hex(
                        "14 0b 45 76 61 6c 75 65 28 1c 01"));
        assertEquals(28, bean.value);
        assertEquals("Injected!", bean.injected);
    }
static class InjectedBean {
        @JacksonInject
        protected String stuff;

        @JacksonInject("myId")
        protected String otherStuff;

        protected long third;

        public int value;

        @JacksonInject
        public void injectThird(long value) {
            third = value;
        }
    }
static class CtorBean {
        protected String name;
        protected int age;

        public CtorBean(@JacksonInject String name, @JsonProperty("age") int age) {
            this.name = name;
            this.age = age;
        }
    }
static class CtorBean2 {
        protected String name;
        protected Integer age;

        public CtorBean2(@JacksonInject String name,
                @JacksonInject("number") Integer age) {
            this.name = name;
            this.age = age;
        }
    }
static class TransientBean {
        @JacksonInject("transient")
        transient Object injected;

        public int value;
    }
static class Bean471 {
        protected final Object constructorInjected;
        protected final String constructorValue;

        @JacksonInject("field_injected")
        protected Object fieldInjected;
        @JsonProperty("field_value")
        protected String fieldValue;

        protected Object methodInjected;
        protected String methodValue;

        public int x;

        @JsonCreator
        private Bean471(@JacksonInject("constructor_injected") Object constructorInjected,
                @JsonProperty("constructor_value") String constructorValue) {
            this.constructorInjected = constructorInjected;
            this.constructorValue = constructorValue;
        }

        @JacksonInject("method_injected")
        private void setMethodInjected(Object methodInjected) {
            this.methodInjected = methodInjected;
        }

        @JsonProperty("method_value")
        public void setMethodValue(String methodValue) {
            this.methodValue = methodValue;
        }
    }
static class StringQueueBean {
        public ArrayBlockingQueue<String> items;
    }
static class IntQueueBean {
        public ArrayBlockingQueue<Integer> numbers;
    }

    void __invoke_testSimple() throws Exception {
        try {
            testSimple();
        } finally {
        }
    }


    void __invoke_testWithCtors() throws Exception {
        try {
            testWithCtors();
        } finally {
        }
    }


    void __invoke_testTwoInjectablesViaCreator() throws Exception {
        try {
            testTwoInjectablesViaCreator();
        } finally {
        }
    }


    void __invoke_testIssue471() throws Exception {
        try {
            testIssue471();
        } finally {
        }
    }


    void __invoke_testTransientField() throws Exception {
        try {
            testTransientField();
        } finally {
        }
    }

}
