package tools.jackson.databind.deser;

import java.util.concurrent.atomic.AtomicReference;

import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.DeserializationContextExt;
import tools.jackson.databind.deser.std.FunctionalScalarDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0177F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] BAR_FIELD = VPackWireFixtureTest.hex(
            "0b 13 01 43 62 61 72 4a 66 69 65 6c 64 56 61 6c 75 65 03");
private static final byte[] STRING_TEST_VALUE = VPackWireFixtureTest.hex(
            "4a 74 65 73 74 2d 76 61 6c 75 65");
private static final byte[] STRING_INVALID = VPackWireFixtureTest.hex(
            "47 69 6e 76 61 6c 69 64");
private static final byte[] STRING_BAD = VPackWireFixtureTest.hex(
            "43 62 61 64");
private static final byte[] STRING_TEST = VPackWireFixtureTest.hex(
            "44 74 65 73 74");
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 31 03");

    void testAsPojoField() {
        SimpleModule module = barModule(new FunctionalScalarDeserializer<>(Bar.class, Bar::of));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        BarWrapper result = mapper.readValue(BAR_FIELD, BarWrapper.class);
        assertNotNull(result.bar);
        assertEquals("fieldValue", result.bar.getValue());
    }

    void testClassWithBiFunction() {
        SimpleModule module = barModule(new FunctionalScalarDeserializer<>(Bar.class,
                (parser, context) -> Bar.of("prefix:" + parser.getValueAsString())));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Bar result = mapper.readValue(STRING_TEST, Bar.class);
        assertEquals("prefix:test", result.getValue());
    }

    void testBiFunctionReceivesParserDirectly() {
        AtomicReference<String> parserState = new AtomicReference<>();
        SimpleModule module = barModule(new FunctionalScalarDeserializer<>(Bar.class,
                (parser, context) -> {
                    parserState.set(parser.currentToken().toString());
                    return Bar.of(parser.getValueAsString());
                }));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Bar result = mapper.readValue(STRING_TEST_VALUE, Bar.class);
        assertEquals("VALUE_STRING", parserState.get());
        assertEquals("test-value", result.getValue());
    }

    void testBiFunctionThrowsIllegalArgumentException() {
        SimpleModule module = barModule(new FunctionalScalarDeserializer<>(Bar.class,
                (parser, context) -> {
                    throw new IllegalArgumentException(
                            "BiFunction error: " + parser.getValueAsString());
                }));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(STRING_INVALID, Bar.class));
        assertTrue(exception.getMessage().contains("not a valid textual representation"));
        assertTrue(exception.getMessage().contains("BiFunction error"));
    }

    void testBiFunctionThrowsCustomException() {
        SimpleModule module = barModule(new FunctionalScalarDeserializer<>(Bar.class,
                (parser, context) -> {
                    throw new RuntimeException(
                            "BiFunction custom error: " + parser.getValueAsString());
                }));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(STRING_INVALID, Bar.class));
        assertTrue(exception.getMessage().contains("not a valid textual representation"));
        assertTrue(exception.getMessage().contains("BiFunction custom error"));
    }

    void testBiFunctionThrowsJacksonExceptionPropagatedAsIs() {
        SimpleModule module = barModule(new FunctionalScalarDeserializer<>(Bar.class,
                (parser, context) -> {
                    throw DatabindException.from(parser,
                            "User BiFunction JacksonException");
                }));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        DatabindException exception = assertThrows(DatabindException.class,
                () -> mapper.readValue(STRING_TEST, Bar.class));
        assertEquals("User BiFunction JacksonException", exception.getOriginalMessage());
    }
private static SimpleModule barModule(ValueDeserializer<Bar> deserializer) {
        SimpleModule module = new SimpleModule("test");
        module.addDeserializer(Bar.class, deserializer);
        return module;
    }
private static void verifyIsFound(Class<?> rawType) {
        if (!verifyDeserializerExistence(rawType)) {
            fail("Should have explicit deserializer for " + rawType.getName());
        }
    }
private static void verifyNotFound(Class<?> rawType) {
        if (verifyDeserializerExistence(rawType)) {
            fail("Should NOT have explicit deserializer for " + rawType.getName());
        }
    }
private static boolean verifyDeserializerExistence(Class<?> rawType) {
        DeserializationContextExt context = MAPPER._deserializationContext();
        return context.hasExplicitDeserializerFor(rawType);
    }
static class POJO2539 { }
static class MyValue {
        public int x;
    }
static class Bar {
        private final String value;

        private Bar(String value) {
            this.value = value;
        }

        static Bar of(String value) {
            return new Bar(value);
        }

        String getValue() {
            return value;
        }
    }
static class BarWrapper {
        public Bar bar;
    }

    void __invoke_testAsPojoField() throws Exception {
        try {
            testAsPojoField();
        } finally {
        }
    }


    void __invoke_testClassWithBiFunction() throws Exception {
        try {
            testClassWithBiFunction();
        } finally {
        }
    }


    void __invoke_testBiFunctionReceivesParserDirectly() throws Exception {
        try {
            testBiFunctionReceivesParserDirectly();
        } finally {
        }
    }


    void __invoke_testBiFunctionThrowsIllegalArgumentException() throws Exception {
        try {
            testBiFunctionThrowsIllegalArgumentException();
        } finally {
        }
    }


    void __invoke_testBiFunctionThrowsCustomException() throws Exception {
        try {
            testBiFunctionThrowsCustomException();
        } finally {
        }
    }


    void __invoke_testBiFunctionThrowsJacksonExceptionPropagatedAsIs() throws Exception {
        try {
            testBiFunctionThrowsJacksonExceptionPropagatedAsIs();
        } finally {
        }
    }

}
