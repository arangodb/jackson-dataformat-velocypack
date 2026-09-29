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
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.IgnoredPropertyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0233F0 {
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

    // Provenance: AnySetterIgnoreProperties6115Test#recordCreateFiltersIgnored.
    void recordCreateFiltersIgnoredVpack() throws Exception {
        AnyRecord result = MAPPER.readValue(RECORD_INPUT, AnyRecord.class);
        assertEquals(Map.of("other", "ok"), result.extras());
    }

    // Provenance: AnySetterIgnoreProperties6115Test#recordUpdateDoesNotLeakIgnoredToAnySetter.
    void recordUpdateDoesNotLeakIgnoredToAnySetterVpack() throws Exception {
        AnyRecord result = MAPPER.readerForUpdating(
                new AnyRecord("alice", new HashMap<>(Map.of("kept", "from-original"))))
                .readValue(RECORD_INPUT);
        assertFalse(result.extras().containsKey("secret"));
        assertEquals(Map.of("other", "ok"), result.extras());
    }

    // Provenance: AnySetterIgnoreProperties6115Test#unwrappedCreatorDoesNotLeakIgnoredToAnySetter.
    void unwrappedCreatorDoesNotLeakIgnoredToAnySetterVpack() throws Exception {
        UnwrapCreatorBean result = MAPPER.readValue(UNWRAPPED_INPUT, UnwrapCreatorBean.class);
        assertFalse(result.extras.containsKey("secret"));
        assertEquals(Map.of("other", "ok"), result.extras);
        assertNotNull(result.name);
        assertEquals("a", result.name.first);
    }

    // Provenance: AnySetterIgnoreProperties6115Test#unwrappedAcceptAllCreatorDoesNotLeakIgnoredToOuterAnySetter.
    void unwrappedAcceptAllCreatorDoesNotLeakIgnoredToOuterAnySetterVpack() throws Exception {
        UnwrapAllCreatorBean result = MAPPER.readValue(
                UNWRAPPED_ACCEPT_ALL_INPUT, UnwrapAllCreatorBean.class);
        assertNotNull(result.name);
        assertFalse(result.extras.containsKey("secret"));
        assertEquals(Map.of("other", "ok", "secret", "nope"), result.name.rest);
        assertEquals("a", result.name.first);
    }

    // Provenance: AnySetterIgnoreProperties6115Test#unwrappedSubPropertyBindsConsistently.
    void unwrappedSubPropertyBindsConsistentlyVpack() throws Exception {
        UnwrapFieldIgnoringSubProp field = MAPPER.readValue(
                UNWRAPPED_SUB_PROPERTY_INPUT, UnwrapFieldIgnoringSubProp.class);
        UnwrapCreatorIgnoringSubProp creator = MAPPER.readValue(
                UNWRAPPED_SUB_PROPERTY_INPUT, UnwrapCreatorIgnoringSubProp.class);
        assertEquals("a", field.name.first);
        assertEquals("b", field.name.last);
        assertEquals("a", creator.name.first);
        assertEquals("b", creator.name.last);
    }

    // Provenance: AnySetterIgnoreProperties6115Test#recordUpdateHonorsIncludeProperties.
    void recordUpdateHonorsIncludePropertiesVpack() throws Exception {
        IncludeOnlyRecord result = MAPPER.readerForUpdating(
                new IncludeOnlyRecord("alice", new HashMap<>())).readValue(RECORD_INPUT);
        assertEquals(Map.of("other", "ok"), result.extras());
    }

    // Provenance: AnySetterIgnoreProperties6115Test#explicitIgnoralBeatsIgnoreUnknown.
    void explicitIgnoralBeatsIgnoreUnknownVpack() throws Exception {
        PlainCreatorIgnoreUnknownBean plain = MAPPER.readValue(
                PLAIN_IGNORAL_INPUT, PlainCreatorIgnoreUnknownBean.class);
        assertEquals(Map.of("other", "ok"), plain.extras);

        UnwrapCreatorIgnoreUnknownBean result = MAPPER.readValue(
                UNWRAPPED_INPUT, UnwrapCreatorIgnoreUnknownBean.class);
        assertFalse(result.extras.containsKey("secret"));
        assertEquals(Map.of("other", "ok"), result.extras);
        assertEquals("a", result.name.first);
    }

    // Provenance: AnySetterIgnoreProperties6115Test#failOnIgnoredPropertiesAppliesOnUpdatePath.
    void failOnIgnoredPropertiesAppliesOnUpdatePathVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES).build();
        assertThrows(IgnoredPropertyException.class,
                () -> mapper.readerForUpdating(new AnyRecord("alice", new HashMap<>()))
                        .readValue(SECRET_ONLY));
    }

    // Provenance: AnySetterIgnoreProperties6115Test#failOnIgnoredPropertiesAppliesOnUnwrappedCreatorPath.
    void failOnIgnoredPropertiesAppliesOnUnwrappedCreatorPathVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES).build();
        assertThrows(IgnoredPropertyException.class,
                () -> mapper.readValue(UNWRAPPED_FAILURE_INPUT, UnwrapCreatorBean.class));
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

    void __invoke_recordCreateFiltersIgnoredVpack() throws Exception {
        try {
            recordCreateFiltersIgnoredVpack();
        } finally {
        }
    }


    void __invoke_recordUpdateDoesNotLeakIgnoredToAnySetterVpack() throws Exception {
        try {
            recordUpdateDoesNotLeakIgnoredToAnySetterVpack();
        } finally {
        }
    }


    void __invoke_unwrappedCreatorDoesNotLeakIgnoredToAnySetterVpack() throws Exception {
        try {
            unwrappedCreatorDoesNotLeakIgnoredToAnySetterVpack();
        } finally {
        }
    }


    void __invoke_unwrappedAcceptAllCreatorDoesNotLeakIgnoredToOuterAnySetterVpack() throws Exception {
        try {
            unwrappedAcceptAllCreatorDoesNotLeakIgnoredToOuterAnySetterVpack();
        } finally {
        }
    }


    void __invoke_unwrappedSubPropertyBindsConsistentlyVpack() throws Exception {
        try {
            unwrappedSubPropertyBindsConsistentlyVpack();
        } finally {
        }
    }


    void __invoke_recordUpdateHonorsIncludePropertiesVpack() throws Exception {
        try {
            recordUpdateHonorsIncludePropertiesVpack();
        } finally {
        }
    }


    void __invoke_explicitIgnoralBeatsIgnoreUnknownVpack() throws Exception {
        try {
            explicitIgnoralBeatsIgnoreUnknownVpack();
        } finally {
        }
    }


    void __invoke_failOnIgnoredPropertiesAppliesOnUpdatePathVpack() throws Exception {
        try {
            failOnIgnoredPropertiesAppliesOnUpdatePathVpack();
        } finally {
        }
    }


    void __invoke_failOnIgnoredPropertiesAppliesOnUnwrappedCreatorPathVpack() throws Exception {
        try {
            failOnIgnoredPropertiesAppliesOnUnwrappedCreatorPathVpack();
        } finally {
        }
    }

}
