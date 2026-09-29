package tools.jackson.databind.ext.cglib;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0297Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] ISSUE_905 = VPackWireFixtureTest.hex(
            "14 09 41 79 33 41 78 32 02");
private static final byte[] NAMING_CONFLICT = VPackWireFixtureTest.hex(
            "14 08 43 62 61 72 33 01");
private static final byte[] INFERENCE = VPackWireFixtureTest.hex(
            "14 09 41 78 33 41 79 35 02");
private static final byte[] VALUE_ABC = VPackWireFixtureTest.hex(
            "14 0d 45 76 61 6c 75 65 43 61 62 63 01");
private static final byte[] IGNORED_NONSCALAR = VPackWireFixtureTest.hex(
            "13 4c "
          + "14 14 44 6e 61 6d 65 4b 66 69 72 73 74 20 65 6e 74 72 79 01 "
          + "14 21 44 6e 61 6d 65 4c 73 65 63 6f 6e 64 20 65 6e 74 72 79 "
          + "47 62 72 65 61 6b 65 72 13 04 40 01 02 "
          + "14 14 44 6e 61 6d 65 4b 74 68 69 72 64 20 65 6e 74 72 79 01 03");
private static final byte[] VALUE_FOO = VPackWireFixtureTest.hex(
            "14 0d 45 76 61 6c 75 65 43 66 6f 6f 01");
private static final byte[] READ_ONLY_ANNOTATIONS_ONE = VPackWireFixtureTest.hex(
            "14 10 48 74 65 73 74 45 6e 75 6d 43 61 62 63 01");
private static final byte[] READ_ONLY_ANNOTATIONS_TWO = VPackWireFixtureTest.hex(
            "14 1f 48 74 65 73 74 45 6e 75 6d 43 78 79 7a 44 6e 61 6d 65 "
          + "49 63 68 61 6e 67 79 6f 6e 67 02");
private static final byte[] READ_ONLY_ONE = VPackWireFixtureTest.hex(
            "14 0d 48 74 65 73 74 45 6e 75 6d 40 01");

    // Provenance: CglibFiltering5354Test#testWriteWithSpringCglibProxyDoesNotIncludeCallbacksProperty.
    void testWriteWithSpringCglibProxyDoesNotIncludeCallbacksPropertyVpack() throws Exception {
        assertOnlyProperty(new SpringProxyEquivalent("hello"));
    }

    // Provenance: CglibFiltering5354Test#testWriteWithHibernateCglibProxyDoesNotIncludeCallbacksProperty.
    void testWriteWithHibernateCglibProxyDoesNotIncludeCallbacksPropertyVpack() throws Exception {
        assertOnlyProperty(new HibernateProxyEquivalent("hello"));
    }

    // Provenance: CglibFiltering5354Test#testWriteWithNetCglibProxyDoesNotIncludeCallbacksProperty.
    void testWriteWithNetCglibProxyDoesNotIncludeCallbacksPropertyVpack() throws Exception {
        assertOnlyProperty(new NetProxyEquivalent("hello"));
    }
private static void assertOnlyProperty(Object proxy) throws Exception {
        Map<?, ?> properties = MAPPER.readValue(MAPPER.writeValueAsBytes(proxy), Map.class);
        assertEquals(Collections.singleton("propertyName"), properties.keySet());
    }
@JsonIgnoreProperties("callbacks")
    static class SpringProxyEquivalent {
        private final String propertyName;
        SpringProxyEquivalent(String propertyName) { this.propertyName = propertyName; }
        public String getPropertyName() { return propertyName; }
        public Object[] getCallbacks() { return new Object[] { new Object() }; }
    }
@JsonIgnoreProperties("callbacks")
    static class HibernateProxyEquivalent {
        private final String propertyName;
        HibernateProxyEquivalent(String propertyName) { this.propertyName = propertyName; }
        public String getPropertyName() { return propertyName; }
        public Object[] getCallbacks() { return new Object[] { new Object() }; }
    }
@JsonIgnoreProperties("callbacks")
    static class NetProxyEquivalent {
        private final String propertyName;
        NetProxyEquivalent(String propertyName) { this.propertyName = propertyName; }
        public String getPropertyName() { return propertyName; }
        public Object[] getCallbacks() { return new Object[] { new Object() }; }
    }
static class Issue905Bean {
        public int x;
        public int y;

        @ConstructorProperties({ "x", "y" })
        public Issue905Bean(int first, int second) {
            x = first;
            y = second;
        }
    }
static class Ambiguity {
        @JsonProperty("bar")
        private int foo;

        protected Ambiguity() { }

        @ConstructorProperties("foo")
        public Ambiguity(int foo) { this.foo = foo; }

        public int getFoo() { return foo; }
    }
static class Lombok1371Bean {
        public int x, y;

        protected Lombok1371Bean() { }

        @ConstructorProperties({ "x", "y" })
        protected Lombok1371Bean(int x, int y) {
            this.x = x + 1;
            this.y = y + 1;
        }
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class Value3252 {
        @JsonProperty("name")
        private final String name;
        @JsonProperty("dumbMap")
        private final Map<String, String> dumbMap;

        @JsonCreator
        Value3252(@JsonProperty("name") String name,
                @JsonProperty("dumbMap") Map<String, String> dumbMap) {
            this.name = name;
            this.dumbMap = dumbMap == null ? Collections.emptyMap() : dumbMap;
        }
    }
static class Test4310 {
        private final String value;

        @ConstructorProperties("value")
        public Test4310(String v) {
            if (v == null) {
                throw new IllegalArgumentException("Constructor called with null value");
            }
            value = v;
        }

        public String getValue() { return value; }
    }
static class Something4908 {
        @JsonProperty("value")
        private final String value;

        @JsonCreator
        @ConstructorProperties({ "value" })
        Something4908(String parameter) { value = parameter; }

        String getValue() { return value; }
    }
static class PersonAnnotations {
        public String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private TestEnum testEnum = TestEnum.DEFAULT;

        PersonAnnotations() { }

        @ConstructorProperties({ "testEnum", "name" })
        public PersonAnnotations(TestEnum testEnum, String name) {
            this.testEnum = testEnum;
            this.name = name;
        }

        public TestEnum getTestEnum() { return testEnum; }
        public void setTestEnum(TestEnum testEnum) { this.testEnum = testEnum; }
    }
static class Person {
        public String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private TestEnum testEnum = TestEnum.DEFAULT;

        Person() { }
        protected Person(TestEnum testEnum, String name) {
            this.testEnum = testEnum;
            this.name = name;
        }

        public TestEnum getTestEnum() { return testEnum; }
        public void setTestEnum(TestEnum testEnum) { this.testEnum = testEnum; }
    }
enum TestEnum { DEFAULT, TEST }

    void __invoke_testWriteWithSpringCglibProxyDoesNotIncludeCallbacksPropertyVpack() throws Exception {
        try {
            testWriteWithSpringCglibProxyDoesNotIncludeCallbacksPropertyVpack();
        } finally {
        }
    }


    void __invoke_testWriteWithHibernateCglibProxyDoesNotIncludeCallbacksPropertyVpack() throws Exception {
        try {
            testWriteWithHibernateCglibProxyDoesNotIncludeCallbacksPropertyVpack();
        } finally {
        }
    }


    void __invoke_testWriteWithNetCglibProxyDoesNotIncludeCallbacksPropertyVpack() throws Exception {
        try {
            testWriteWithNetCglibProxyDoesNotIncludeCallbacksPropertyVpack();
        } finally {
        }
    }

}
