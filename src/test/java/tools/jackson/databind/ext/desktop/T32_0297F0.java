package tools.jackson.databind.ext.desktop;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0297F0 {
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
private static void assertOnlyProperty(Object proxy) throws Exception {
        Map<?, ?> properties = MAPPER.readValue(MAPPER.writeValueAsBytes(proxy), Map.class);
        assertEquals(Collections.singleton("propertyName"), properties.keySet());
    }

    // Provenance: ConstructorPropertiesAnnotationTest#testConstructorPropertiesAnnotation.
    void testConstructorPropertiesAnnotationVpack() throws Exception {
        Issue905Bean value = MAPPER.readValue(ISSUE_905, Issue905Bean.class);
        assertEquals(2, value.x);
        assertEquals(3, value.y);
    }

    // Provenance: ConstructorPropertiesAnnotationTest#testPossibleNamingConflict.
    void testPossibleNamingConflictVpack() throws Exception {
        Ambiguity value = MAPPER.readValue(NAMING_CONFLICT, Ambiguity.class);
        assertEquals(3, value.getFoo());
    }

    // Provenance: ConstructorPropertiesAnnotationTest#testConstructorPropertiesInference.
    void testConstructorPropertiesInferenceVpack() throws Exception {
        Lombok1371Bean value = MAPPER.readValue(INFERENCE, Lombok1371Bean.class);
        assertEquals(4, value.x);
        assertEquals(6, value.y);

        ObjectMapper mapper = VPackMapper.builder()
                .disable(tools.jackson.databind.MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES)
                .build();
        value = mapper.readValue(INFERENCE, Lombok1371Bean.class);
        assertEquals(3, value.x);
        assertEquals(5, value.y);
    }

    // Provenance: ConstructorPropertiesAnnotationTest#testConstructorPropertyAndJsonCreator.
    void testConstructorPropertyAndJsonCreatorVpack() throws Exception {
        Something4908 value = MAPPER.readValue(VALUE_ABC, Something4908.class);
        assertEquals("abc", value.getValue());
    }

    // Provenance: ConstructorPropertiesAnnotationTest#testSkipNonScalar3252.
    void testSkipNonScalar3252Vpack() throws Exception {
        List<Value3252> values = MAPPER.readValue(IGNORED_NONSCALAR,
                MAPPER.getTypeFactory().constructCollectionType(List.class, Value3252.class));
        assertEquals(3, values.size());
    }

    // Provenance: ConstructorPropertiesAnnotationTest#issue4310.
    void issue4310Vpack() throws Exception {
        Test4310 value = MAPPER.readValue(VALUE_FOO, Test4310.class);
        assertEquals("foo", value.getValue());
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

    void __invoke_testConstructorPropertiesAnnotationVpack() throws Exception {
        try {
            testConstructorPropertiesAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testPossibleNamingConflictVpack() throws Exception {
        try {
            testPossibleNamingConflictVpack();
        } finally {
        }
    }


    void __invoke_testConstructorPropertiesInferenceVpack() throws Exception {
        try {
            testConstructorPropertiesInferenceVpack();
        } finally {
        }
    }


    void __invoke_testConstructorPropertyAndJsonCreatorVpack() throws Exception {
        try {
            testConstructorPropertyAndJsonCreatorVpack();
        } finally {
        }
    }


    void __invoke_testSkipNonScalar3252Vpack() throws Exception {
        try {
            testSkipNonScalar3252Vpack();
        } finally {
        }
    }


    void __invoke_issue4310Vpack() throws Exception {
        try {
            issue4310Vpack();
        } finally {
        }
    }

}
