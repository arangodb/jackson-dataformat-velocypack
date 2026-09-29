package tools.jackson.databind.deser.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.DeserializationProblemHandler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0233F1 {
private static final byte[] RECORD_INPUT = VPackWireFixtureTest.hex(
            "14 23 44 6e 61 6d 65 45 61 6c 69 63 65 "
          + "46 73 65 63 72 65 74 44 6e 6f 70 65 "
          + "45 6f 74 68 65 72 42 6f 6b 03");
private static final byte[] UNWRAPPED_INPUT = VPackWireFixtureTest.hex(
            "14 2b 42 69 64 31 45 66 69 72 73 74 41 61 "
          + "44 6c 61 73 74 41 62 46 73 65 63 72 65 74 44 6e 6f 70 65 "
          + "45 6f 74 68 65 72 42 6f 6b 05");
private static final byte[] UNWRAPPED_ACCEPT_ALL_INPUT = VPackWireFixtureTest.hex(
            "14 24 42 69 64 31 45 66 69 72 73 74 41 61 "
          + "46 73 65 63 72 65 74 44 6e 6f 70 65 "
          + "45 6f 74 68 65 72 42 6f 6b 04");
private static final byte[] UNWRAPPED_SUB_PROPERTY_INPUT = VPackWireFixtureTest.hex(
            "14 16 42 69 64 31 45 66 69 72 73 74 41 61 "
          + "44 6c 61 73 74 41 62 03");
private static final byte[] PLAIN_IGNORAL_INPUT = VPackWireFixtureTest.hex(
            "14 1c 42 69 64 31 46 73 65 63 72 65 74 44 6e 6f 70 65 "
          + "45 6f 74 68 65 72 42 6f 6b 03");
private static final byte[] SECRET_ONLY = VPackWireFixtureTest.hex(
            "14 0f 46 73 65 63 72 65 74 44 6e 6f 70 65 01");
private static final byte[] UNWRAPPED_FAILURE_INPUT = VPackWireFixtureTest.hex(
            "14 1b 42 69 64 31 45 66 69 72 73 74 41 61 "
          + "46 73 65 63 72 65 74 44 6e 6f 70 65 03");
private static final byte[] COLLECTION_PROPERTY_STRING = VPackWireFixtureTest.hex(
            "14 13 44 70 72 6f 70 4a 73 6f 6d 65 53 74 72 69 6e 67 01");
private static final byte[] STRING_VALUE = VPackWireFixtureTest.hex(
            "4a 73 6f 6d 65 53 74 72 69 6e 67");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForCollectionProp3349.
    void testHandleUnexpectedTokenForCollectionProp3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        VPackMapper mapper = VPackMapper.builder().addHandler(handler).build();
        mapper.readValue(COLLECTION_PROPERTY_STRING, ArrayHolder3349.class);
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForIntArray3349.
    void testHandleUnexpectedTokenForIntArray3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        VPackMapper mapper = VPackMapper.builder().addHandler(handler).build();
        mapper.readValue(STRING_VALUE, int[].class);
        verifyUnexpectedTokenCalled(handler);
    }

    // Provenance: DeserializationProblemHandlerTest#testHandleUnexpectedTokenForBooleanArray3349.
    void testHandleUnexpectedTokenForBooleanArray3349Vpack() throws Exception {
        TrackingProblemHandler handler = new TrackingProblemHandler();
        VPackMapper mapper = VPackMapper.builder().addHandler(handler).build();
        mapper.readValue(STRING_VALUE, boolean[].class);
        verifyUnexpectedTokenCalled(handler);
    }
private static void verifyUnexpectedTokenCalled(TrackingProblemHandler handler) {
        assertTrue(handler.handleUnexpectedTokenCalled,
                "handleUnexpectedToken should have been called");
        assertFalse(handler.handleInstantiationProblemCalled,
                "handleInstantiationProblem should NOT have been called");
        assertFalse(handler.handleMissingInstantiatorCalled,
                "handleMissingInstantiator should NOT have been called");
    }
@JsonIgnoreProperties({"secret"})
    public record AnyRecord(String name, @JsonAnySetter Map<String, Object> extras) { }
static class Name {
        public String first;
        public String last;
    }
@JsonIgnoreProperties({"secret"})
    static class UnwrapCreatorBean {
        final int id;
        @JsonUnwrapped public Name name;
        final Map<String, Object> extras = new HashMap<>();

        @JsonCreator
        UnwrapCreatorBean(@JsonProperty("id") int id) { this.id = id; }

        @JsonAnySetter
        public void any(String key, Object value) { extras.put(key, value); }
    }
static class NameWithAny {
        public String first;
        @JsonAnySetter public Map<String, Object> rest = new HashMap<>();
    }
@JsonIgnoreProperties({"secret"})
    static class UnwrapAllCreatorBean {
        final int id;
        @JsonUnwrapped public NameWithAny name;
        final Map<String, Object> extras = new HashMap<>();

        @JsonCreator
        UnwrapAllCreatorBean(@JsonProperty("id") int id) { this.id = id; }

        @JsonAnySetter
        public void any(String key, Object value) { extras.put(key, value); }
    }
@JsonIgnoreProperties({"last"})
    static class UnwrapCreatorIgnoringSubProp {
        final int id;
        @JsonUnwrapped public Name name;

        @JsonCreator
        UnwrapCreatorIgnoringSubProp(@JsonProperty("id") int id) { this.id = id; }
    }
@JsonIgnoreProperties({"last"})
    static class UnwrapFieldIgnoringSubProp {
        public int id;
        @JsonUnwrapped public Name name;
    }
@JsonIncludeProperties({"name", "other"})
    public record IncludeOnlyRecord(String name, @JsonAnySetter Map<String, Object> extras) { }
@JsonIgnoreProperties(value = {"secret"}, ignoreUnknown = true)
    static class UnwrapCreatorIgnoreUnknownBean {
        final int id;
        @JsonUnwrapped public Name name;
        final Map<String, Object> extras = new HashMap<>();

        @JsonCreator
        UnwrapCreatorIgnoreUnknownBean(@JsonProperty("id") int id) { this.id = id; }

        @JsonAnySetter
        public void any(String key, Object value) { extras.put(key, value); }
    }
@JsonIgnoreProperties(value = {"secret"}, ignoreUnknown = true)
    static class PlainCreatorIgnoreUnknownBean {
        final int id;
        final Map<String, Object> extras = new HashMap<>();

        @JsonCreator
        PlainCreatorIgnoreUnknownBean(@JsonProperty("id") int id) { this.id = id; }

        @JsonAnySetter
        public void any(String key, Object value) { extras.put(key, value); }
    }
static class TrackingProblemHandler extends DeserializationProblemHandler {
        boolean handleUnexpectedTokenCalled;
        boolean handleInstantiationProblemCalled;
        boolean handleMissingInstantiatorCalled;

        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType,
                JsonToken token, JsonParser parser, String failureMsg) {
            handleUnexpectedTokenCalled = true;
            if (targetType.isMapLikeType()) return new HashMap<>();
            if (targetType.isCollectionLikeType()) return new ArrayList<>();
            if (targetType.isArrayType()) {
                return java.lang.reflect.Array.newInstance(
                        targetType.getContentType().getRawClass(), 0);
            }
            return NOT_HANDLED;
        }

        @Override
        public Object handleInstantiationProblem(DeserializationContext ctxt, Class<?> instClass,
                Object argument, Throwable throwable) {
            handleInstantiationProblemCalled = true;
            return NOT_HANDLED;
        }

        @Override
        public Object handleMissingInstantiator(DeserializationContext ctxt, Class<?> instClass,
                tools.jackson.databind.deser.ValueInstantiator inst, JsonParser parser,
                String message) {
            handleMissingInstantiatorCalled = true;
            return NOT_HANDLED;
        }
    }
static class ArrayHolder3349 {
        private final java.util.Collection<String> prop;

        private ArrayHolder3349(java.util.Collection<String> prop) { this.prop = prop; }

        @JsonCreator
        static ArrayHolder3349 create(@JsonProperty("prop") Iterable<String> prop) {
            ArrayList<String> list = new ArrayList<>();
            prop.forEach(list::add);
            return new ArrayHolder3349(list);
        }
    }

    void __invoke_testHandleUnexpectedTokenForCollectionProp3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForCollectionProp3349Vpack();
        } finally {
        }
    }


    void __invoke_testHandleUnexpectedTokenForIntArray3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForIntArray3349Vpack();
        } finally {
        }
    }


    void __invoke_testHandleUnexpectedTokenForBooleanArray3349Vpack() throws Exception {
        try {
            testHandleUnexpectedTokenForBooleanArray3349Vpack();
        } finally {
        }
    }

}
