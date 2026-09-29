package tools.jackson.databind.deser.filter;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0246Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper STRICT_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
private static final byte[] UNKNOWN_FIELD = VPackWireFixtureTest.hex(
            "14 12 41 61 31 43 66 6f 6f 02 05 31 32 33 41 62 3f 03");
private static final byte[] CLASS_IGNORES = VPackWireFixtureTest.hex(
            "14 11 41 61 31 41 62 32 41 63 41 78 41 64 41 79 04");
private static final byte[] PROPERTY_IGNORE_CLASS = VPackWireFixtureTest.hex(
            "14 15 45 76 61 6c 75 65 14 0c 41 79 32 41 78 31 41 7a 33 03 01");
private static final byte[] UNWRAPPED_CHILD = VPackWireFixtureTest.hex(
            "14 09 41 61 31 41 62 32 02");
private static final byte[] INJECTED = VPackWireFixtureTest.hex(
            "14 09 41 62 43 62 62 62 01");

    // Provenance: UnknownPropertyDeserTest#testUnknownHandlingDefault.
    void testUnknownHandlingDefault() {
        UnrecognizedPropertyException exception = assertThrows(
                UnrecognizedPropertyException.class,
                () -> STRICT_MAPPER.readValue(UNKNOWN_FIELD, TestBean.class));
        assertTrue(exception.getMessage().contains("foo"));
    }

    // Provenance: UnknownPropertyDeserTest#testUnknownHandlingIgnoreWithHandler.
    void testUnknownHandlingIgnoreWithHandler() throws Exception {
        ObjectMapper mapper = mapperWithUnknownHandler();
        TestBean result = mapper.readValue(UNKNOWN_FIELD, TestBean.class);

        assertNotNull(result);
        assertEquals(1, result._a);
        assertEquals(-1, result._b);
        assertEquals("foo:START_ARRAY", result._unknown);
    }

    // Provenance: UnknownPropertyDeserTest#testUnknownHandlingIgnoreWithHandlerAndObjectReader.
    void testUnknownHandlingIgnoreWithHandlerAndObjectReader() throws Exception {
        TestBean result = MAPPER.readerFor(TestBean.class)
                .withHandler(new UnknownHandler())
                .readValue(UNKNOWN_FIELD);

        assertNotNull(result);
        assertEquals(1, result._a);
        assertEquals(-1, result._b);
        assertEquals("foo:START_ARRAY", result._unknown);
    }

    // Provenance: UnknownPropertyDeserTest#testUnknownHandlingIgnoreWithFeature.
    void testUnknownHandlingIgnoreWithFeature() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        TestBean result = mapper.readValue(UNKNOWN_FIELD, TestBean.class);

        assertNotNull(result);
        assertEquals(1, result._a);
        assertNull(result._unknown);
        assertEquals(-1, result._b);
    }

    // Provenance: UnknownPropertyDeserTest#testWithClassIgnore.
    void testWithClassIgnore() throws Exception {
        IgnoreSome result = MAPPER.readValue(CLASS_IGNORES, IgnoreSome.class);

        assertEquals(1, result.a);
        assertEquals("y", result.d());
        assertEquals(0, result.b);
        assertNull(result.c());
    }

    // Provenance: UnknownPropertyDeserTest#testPropertyIgnoralWithClass.
    void testPropertyIgnoralWithClass() throws Exception {
        XYZWrapper2 result = MAPPER.readValue(PROPERTY_IGNORE_CLASS,
                XYZWrapper2.class);

        assertEquals(1, result.value.x);
    }

    // Provenance: UnknownPropertyDeserTest#testUnwrappedWithFailOnUnknownDisabled.
    void testUnwrappedWithFailOnUnknownDisabled() throws Exception {
        IgnoreUnknownUnwrapped value = MAPPER.readValue(UNWRAPPED_CHILD,
                IgnoreUnknownUnwrapped.class);

        assertNotNull(value);
        assertEquals(1, value.child.a);
        assertEquals(2, value.child.b);
    }
private static ObjectMapper mapperWithUnknownHandler() {
        return VPackMapper.builder().addHandler(new UnknownHandler()).build();
    }
static class UnknownHandler extends DeserializationProblemHandler {
        @Override
        public boolean handleUnknownProperty(DeserializationContext ctxt,
                JsonParser parser, ValueDeserializer<?> deserializer,
                Object bean, String propertyName) {
            ((TestBean) bean).markUnknown(propertyName + ":"
                    + parser.currentToken().toString());
            parser.skipChildren();
            return true;
        }
    }
static class TestBean {
        String _unknown;
        int _a, _b;

        public void setA(int a) { _a = a; }
        public void setB(int b) { _b = b; }
        public void markUnknown(String unknown) { _unknown = unknown; }
    }
@JsonIgnoreProperties({"b", "c"})
    static class IgnoreSome {
        public int a, b;
        private String c, d;

        public String c() { return c; }
        public void setC(String value) { c = value; }
        public String d() { return d; }
        public void setD(String value) { d = value; }
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class IgnoreUnknownUnwrapped {
        @JsonUnwrapped
        UnwrappedChild child;

        static class UnwrappedChild {
            public int a, b;
        }
    }
static class XYZWrapper2 {
        @JsonIgnoreProperties({"y"})
        public X value;
    }
@JsonIgnoreProperties({"z"})
    static class X {
        public int x;
        public int y;
    }
static class InjectMe {
        private String a;

        public InjectMe(boolean dummy) { }

        public void setA(Integer value) { a = value.toString(); }
        public void setA(InjectMe value) { a = String.valueOf(value); }
        public String getA() { return a; }
    }
static class Injectee {
        private String b;

        @JsonCreator
        public Injectee(@JacksonInject(useInput = OptBoolean.FALSE) InjectMe injectMe,
                @JsonProperty("b") String b) {
            this.b = b;
        }

        public String getB() { return b; }
    }
static class BadBean1 {
        @JacksonInject protected String prop1;
        @JacksonInject protected String prop2;
    }
static class BadBean2 {
        @JacksonInject("x") protected String prop1;
        @JacksonInject("x") protected String prop2;
    }

    void __invoke_testUnknownHandlingDefault() throws Exception {
        try {
            testUnknownHandlingDefault();
        } finally {
        }
    }


    void __invoke_testUnknownHandlingIgnoreWithHandler() throws Exception {
        try {
            testUnknownHandlingIgnoreWithHandler();
        } finally {
        }
    }


    void __invoke_testUnknownHandlingIgnoreWithHandlerAndObjectReader() throws Exception {
        try {
            testUnknownHandlingIgnoreWithHandlerAndObjectReader();
        } finally {
        }
    }


    void __invoke_testUnknownHandlingIgnoreWithFeature() throws Exception {
        try {
            testUnknownHandlingIgnoreWithFeature();
        } finally {
        }
    }


    void __invoke_testWithClassIgnore() throws Exception {
        try {
            testWithClassIgnore();
        } finally {
        }
    }


    void __invoke_testPropertyIgnoralWithClass() throws Exception {
        try {
            testPropertyIgnoralWithClass();
        } finally {
        }
    }


    void __invoke_testUnwrappedWithFailOnUnknownDisabled() throws Exception {
        try {
            testUnwrappedWithFailOnUnknownDisabled();
        } finally {
        }
    }

}
