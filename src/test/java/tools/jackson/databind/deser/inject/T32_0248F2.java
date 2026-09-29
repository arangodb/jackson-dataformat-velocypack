package tools.jackson.databind.deser.inject;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0248F2 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] INPUT_OBJECT = VPackWireFixtureTest.hex(
            "14 0f 45 66 69 65 6c 64 45 69 6e 70 75 74 01");
private static final byte[] ID_OBJECT = VPackWireFixtureTest.hex(
            "14 07 42 69 64 33 01");
private static final ObjectMapper OPTIONAL_PLAIN_MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE)
            .build();
private static final ObjectMapper OPTIONAL_INJECTED_MAPPER = VPackMapper.builder()
            .injectableValues(new InjectableValues.Std().addValue("key", "injected"))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE)
            .build();
private static final ObjectMapper DEFAULT_OPTIONAL_PLAIN_MAPPER = VPackMapper.builder().build();
private static final ObjectMapper DEFAULT_OPTIONAL_INJECTED_MAPPER = VPackMapper.builder()
            .injectableValues(new InjectableValues.Std().addValue("key", "injected"))
            .build();

    // Provenance: JacksonInject2465Test#injectWithCreator.
    void injectWithCreator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(h -> JsonSetter.Value.construct(Nulls.AS_EMPTY, Nulls.AS_EMPTY))
                .changeDefaultVisibility(h -> h.withVisibility(PropertyAccessor.FIELD,
                        JsonAutoDetect.Visibility.ANY))
                .build();

        final Internal2465 injected = new Internal2465("test");
        TestCase2465 o = mapper.readerFor(TestCase2465.class)
                .with(new InjectableValues.Std().addValue(Internal2465.class, injected))
                .readValue(ID_OBJECT);
        assertEquals(3, o.fetchId());
        assertNotNull(o.fetchInternal());
    }
static class InputDefault {
        @JacksonInject(value = "key", optional = OptBoolean.TRUE)
        @JsonProperty("field")
        private final String _field;

        @JsonCreator
        public InputDefault(@JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }
static class InputDefaultConstructor {
        private final String _field;

        @JsonCreator
        public InputDefaultConstructor(@JacksonInject(value = "key", optional = OptBoolean.TRUE)
                                       @JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }
static class InputTrue {
        @JacksonInject(value = "key", useInput = OptBoolean.TRUE, optional = OptBoolean.TRUE)
        @JsonProperty("field")
        private final String _field;

        @JsonCreator
        public InputTrue(@JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }
static class InputTrueConstructor {
        private final String _field;

        @JsonCreator
        public InputTrueConstructor(@JacksonInject(value = "key", useInput = OptBoolean.TRUE,
                optional = OptBoolean.TRUE) @JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }
static class InputFalse {
        @JacksonInject(value = "key", useInput = OptBoolean.FALSE, optional = OptBoolean.TRUE)
        @JsonProperty("field")
        private final String _field;

        @JsonCreator
        public InputFalse(@JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }
static class InputFalseConstructor {
        private final String _field;

        @JsonCreator
        public InputFalseConstructor(@JacksonInject(value = "key", useInput = OptBoolean.FALSE,
                optional = OptBoolean.TRUE) @JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }
static final class TestCase2465 {
        private final Internal2465 str;
        private final int id;

        @JsonCreator
        public TestCase2465(@JacksonInject(useInput = OptBoolean.FALSE) Internal2465 str,
                            @JsonProperty("id") int id) {
            this.str = str;
            this.id = id;
        }

        public int fetchId() { return id; }

        public Internal2465 fetchInternal() { return str; }
    }
static final class Internal2465 {
        final String val;

        public Internal2465(String val) { this.val = val; }
    }

    void __invoke_injectWithCreator() throws Exception {
        try {
            injectWithCreator();
        } finally {
        }
    }

}
