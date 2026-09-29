package tools.jackson.databind.deser.inject;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationProblemHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0246F0 {
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

    // Provenance: InjectableWithoutDeser962Test#testInjected.
    void testInjected() throws Exception {
        InjectMe im = new InjectMe(true);
        ObjectMapper mapper = VPackMapper.builder()
                .injectableValues(new InjectableValues.Std()
                        .addValue(InjectMe.class, im))
                .build();

        Injectee actual = mapper.readValue(INJECTED, Injectee.class);

        assertEquals("bbb", actual.getB());
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

    void __invoke_testInjected() throws Exception {
        try {
            testInjected();
        } finally {
        }
    }

}
