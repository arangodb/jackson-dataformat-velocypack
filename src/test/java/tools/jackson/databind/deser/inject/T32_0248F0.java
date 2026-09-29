package tools.jackson.databind.deser.inject;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0248F0 {
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

    // Provenance: JacksonInject1381WithOptionalDeserializationFeatureDisabledTest#test1.
    void testOptionalFeatureDisabled1() throws Exception {
        assertNull(OPTIONAL_PLAIN_MAPPER.readValue(EMPTY_OBJECT, InputDefault.class).getField());
        assertNull(OPTIONAL_PLAIN_MAPPER.readValue(EMPTY_OBJECT, InputDefaultConstructor.class).getField());
        assertNull(OPTIONAL_PLAIN_MAPPER.readValue(EMPTY_OBJECT, InputTrue.class).getField());
        assertNull(OPTIONAL_PLAIN_MAPPER.readValue(EMPTY_OBJECT, InputTrueConstructor.class).getField());
        assertNull(OPTIONAL_PLAIN_MAPPER.readValue(EMPTY_OBJECT, InputFalse.class).getField());
        assertNull(OPTIONAL_PLAIN_MAPPER.readValue(EMPTY_OBJECT, InputFalseConstructor.class).getField());
    }

    // Provenance: JacksonInject1381WithOptionalDeserializationFeatureDisabledTest#test2.
    void testOptionalFeatureDisabled2() throws Exception {
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputDefault.class).getField());
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputDefaultConstructor.class).getField());
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputTrue.class).getField());
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputTrueConstructor.class).getField());
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputFalse.class).getField());
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputFalseConstructor.class).getField());
    }

    // Provenance: JacksonInject1381WithOptionalDeserializationFeatureDisabledTest#test3.
    void testOptionalFeatureDisabled3() throws Exception {
        assertEquals("input", OPTIONAL_PLAIN_MAPPER.readValue(INPUT_OBJECT, InputDefault.class).getField());
        assertEquals("input", OPTIONAL_PLAIN_MAPPER.readValue(INPUT_OBJECT, InputDefaultConstructor.class).getField());
        assertNull(OPTIONAL_PLAIN_MAPPER.readValue(INPUT_OBJECT, InputFalse.class).getField());
        assertNull(OPTIONAL_PLAIN_MAPPER.readValue(INPUT_OBJECT, InputFalseConstructor.class).getField());
        assertEquals("input", OPTIONAL_PLAIN_MAPPER.readValue(INPUT_OBJECT, InputTrue.class).getField());
        assertEquals("input", OPTIONAL_PLAIN_MAPPER.readValue(INPUT_OBJECT, InputTrueConstructor.class).getField());
    }

    // Provenance: JacksonInject1381WithOptionalDeserializationFeatureDisabledTest#test4.
    void testOptionalFeatureDisabled4() throws Exception {
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputDefault.class).getField());
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputDefaultConstructor.class).getField());
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputFalse.class).getField());
        assertEquals("injected", OPTIONAL_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputFalseConstructor.class).getField());
    }

    // Provenance: JacksonInject1381WithOptionalDeserializationFeatureDisabledTest#test5.
    void testOptionalFeatureDisabled5() throws Exception {
        assertEquals("input", OPTIONAL_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputTrue.class).getField());
        assertEquals("input", OPTIONAL_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputTrueConstructor.class).getField());
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

    void __invoke_testOptionalFeatureDisabled1() throws Exception {
        try {
            testOptionalFeatureDisabled1();
        } finally {
        }
    }


    void __invoke_testOptionalFeatureDisabled2() throws Exception {
        try {
            testOptionalFeatureDisabled2();
        } finally {
        }
    }


    void __invoke_testOptionalFeatureDisabled3() throws Exception {
        try {
            testOptionalFeatureDisabled3();
        } finally {
        }
    }


    void __invoke_testOptionalFeatureDisabled4() throws Exception {
        try {
            testOptionalFeatureDisabled4();
        } finally {
        }
    }


    void __invoke_testOptionalFeatureDisabled5() throws Exception {
        try {
            testOptionalFeatureDisabled5();
        } finally {
        }
    }

}
