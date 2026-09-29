package tools.jackson.databind.deser.inject;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MissingInjectableValueException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0247F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] INPUT_OBJECT = VPackWireFixtureTest.hex(
            "14 0f 45 66 69 65 6c 64 45 69 6e 70 75 74 01");
private static final ObjectMapper PLAIN_MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE)
            .build();
private static final ObjectMapper INJECTED_MAPPER = VPackMapper.builder()
            .injectableValues(new InjectableValues.Std().addValue("key", "injected"))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE)
            .build();
private static final ObjectMapper DEFAULT_MAPPER = VPackMapper.builder().build();
private static final ObjectMapper DEFAULT_INJECTED_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE)
            .injectableValues(new InjectableValues.Std().addValue("key", "injected"))
            .build();

    // Provenance: JacksonInject1381Test#test1.
    void test1() {
        assertMissingInjectable(InputDefault.class);
        assertMissingInjectable(InputDefaultConstructor.class);
        assertMissingInjectable(InputTrue.class);
        assertMissingInjectable(InputTrueConstructor.class);
        assertMissingInjectable(InputFalse.class);
        assertMissingInjectable(InputFalseConstructor.class);
    }

    // Provenance: JacksonInject1381Test#test2.
    void test2() throws Exception {
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputDefault.class).getField());
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputDefaultConstructor.class).getField());
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputTrue.class).getField());
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputTrueConstructor.class).getField());
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputFalse.class).getField());
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(EMPTY_OBJECT, InputFalseConstructor.class).getField());
    }

    // Provenance: JacksonInject1381Test#test3.
    void test3() {
        assertMissingInjectable(InputDefault.class, INPUT_OBJECT);
        assertMissingInjectable(InputDefaultConstructor.class, INPUT_OBJECT);
        assertMissingInjectable(InputFalse.class, INPUT_OBJECT);
        assertMissingInjectable(InputFalseConstructor.class, INPUT_OBJECT);
    }

    // Provenance: JacksonInject1381Test#test4.
    void test4() throws Exception {
        assertEquals("input", DEFAULT_MAPPER.readValue(INPUT_OBJECT, InputTrue.class).getField());
        assertEquals("input", DEFAULT_MAPPER.readValue(INPUT_OBJECT, InputTrueConstructor.class).getField());
    }

    // Provenance: JacksonInject1381Test#test5.
    void test5() throws Exception {
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputDefault.class).getField());
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputDefaultConstructor.class).getField());
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputFalse.class).getField());
        assertEquals("injected", DEFAULT_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputFalseConstructor.class).getField());
    }

    // Provenance: JacksonInject1381Test#test6.
    void test6() throws Exception {
        assertEquals("input", DEFAULT_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputTrue.class).getField());
        assertEquals("input", DEFAULT_INJECTED_MAPPER.readValue(INPUT_OBJECT, InputTrueConstructor.class).getField());
    }
private static void assertMissingInjectable(Class<?> type) {
        assertMissingInjectable(type, EMPTY_OBJECT);
    }
private static void assertMissingInjectable(Class<?> type, byte[] input) {
        assertThrows(
                MissingInjectableValueException.class,
                () -> DEFAULT_MAPPER.readValue(input, type));
    }
static class InputDefault {
        @JacksonInject(value = "key")
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
        public InputDefaultConstructor(@JacksonInject(value = "key")
                                       @JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }
static class InputTrue {
        @JacksonInject(value = "key", useInput = OptBoolean.TRUE)
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
        public InputTrueConstructor(@JacksonInject(value = "key", useInput = OptBoolean.TRUE)
                                    @JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }
static class InputFalse {
        @JacksonInject(value = "key", useInput = OptBoolean.FALSE)
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
        public InputFalseConstructor(@JacksonInject(value = "key", useInput = OptBoolean.FALSE)
                                     @JsonProperty("field") final String field) {
            _field = field;
        }

        public String getField() { return _field; }
    }

    void __invoke_test1() throws Exception {
        try {
            test1();
        } finally {
        }
    }


    void __invoke_test2() throws Exception {
        try {
            test2();
        } finally {
        }
    }


    void __invoke_test3() throws Exception {
        try {
            test3();
        } finally {
        }
    }


    void __invoke_test4() throws Exception {
        try {
            test4();
        } finally {
        }
    }


    void __invoke_test5() throws Exception {
        try {
            test5();
        } finally {
        }
    }


    void __invoke_test6() throws Exception {
        try {
            test6();
        } finally {
        }
    }

}
