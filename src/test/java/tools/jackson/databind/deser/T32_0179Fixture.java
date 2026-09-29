package tools.jackson.databind.deser;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.std.FunctionalScalarDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0179Fixture {
private static final byte[] JAVA_TYPE = VPackWireFixtureTest.hex(
            "48 6a 61 76 61 74 79 70 65");
private static final byte[] TEST = VPackWireFixtureTest.hex(
            "44 74 65 73 74");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] LIST = VPackWireFixtureTest.hex(
            "02 08 41 61 41 62 41 63");
private static final byte[] NULL_FIELD = VPackWireFixtureTest.hex(
            "14 08 43 62 61 72 18 01");
private static final byte[] ARRAY = VPackWireFixtureTest.hex(
            "02 08 45 68 65 6c 6c 6f");
private static final byte[] OBJECT = VPackWireFixtureTest.hex(
            "14 0f 45 76 61 6c 75 65 45 68 65 6c 6c 6f 01");
private static final byte[] TEXT = VPackWireFixtureTest.hex(
            "4e 65 78 70 65 63 74 65 64 2d 76 61 6c 75 65");
private static final byte[] NUMBER = VPackWireFixtureTest.hex(
            "29 39 30");

    void testJavaTypeWithFunction() {
        ObjectMapper baseMapper = new VPackMapper();
        JavaType barType = baseMapper.constructType(Bar.class);

        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(barType, Bar::of)))
                .build();

        Bar result = mapper.readValue(JAVA_TYPE, Bar.class);
        assertEquals("javatype", result.getValue());
    }

    void testJavaTypeWithBiFunction() {
        ObjectMapper baseMapper = new VPackMapper();
        JavaType barType = baseMapper.constructType(Bar.class);

        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(barType,
                        (p, ctx) -> Bar.of("bi:" + p.getValueAsString()))))
                .build();

        Bar result = mapper.readValue(TEST, Bar.class);
        assertEquals("bi:test", result.getValue());
    }

    void testNullValue() {
        assertNull(barMapper().readValue(NULL, Bar.class));
    }

    void testInList() {
        List<Bar> result = barMapper().readValue(LIST, new TypeReference<List<Bar>>() { });
        assertEquals(3, result.size());
        assertEquals("a", result.get(0).getValue());
        assertEquals("b", result.get(1).getValue());
        assertEquals("c", result.get(2).getValue());
    }

    void testNullFieldInPojo() {
        BarWrapper result = barMapper().readValue(NULL_FIELD, BarWrapper.class);
        assertNull(result.bar);
    }

    void testRejectsJsonArray() {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> barMapper().readValue(ARRAY, Bar.class));
        assertTrue(exception.getMessage().contains("Cannot deserialize"));
    }

    void testRejectsJsonObject() {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> barMapper().readValue(OBJECT, Bar.class));
        assertTrue(exception.getMessage().contains("Cannot deserialize"));
    }

    void testStringFunctionReceivesExtractedText() {
        AtomicReference<String> receivedValue = new AtomicReference<>();
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, text -> {
                    receivedValue.set(text);
                    return Bar.of(text);
                })))
                .build();

        Bar result = mapper.readValue(TEXT, Bar.class);
        assertEquals("expected-value", receivedValue.get());
        assertEquals("expected-value", result.getValue());
    }

    void testStringFunctionReceivesCoercedNumericText() {
        AtomicReference<String> receivedValue = new AtomicReference<>();
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, text -> {
                    receivedValue.set(text);
                    return Bar.of(text);
                })))
                .build();

        Bar result = mapper.readValue(NUMBER, Bar.class);
        assertEquals("12345", receivedValue.get());
        assertEquals("12345", result.getValue());
    }

    void testFunctionThrowsJacksonExceptionPropagatedAsIs() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, value -> {
                    throw DatabindException.from((JsonParser) null, "User JacksonException");
                })))
                .build();

        DatabindException exception = assertThrows(DatabindException.class,
                () -> mapper.readValue(TEST, Bar.class));
        assertEquals("User JacksonException", exception.getOriginalMessage());
    }

    void testWrapExceptionsEnabled() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, value -> {
                    throw new RuntimeException("User error");
                })))
                .enable(DeserializationFeature.WRAP_EXCEPTIONS)
                .build();

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(TEST, Bar.class));
        assertTrue(exception.getMessage().contains("not a valid textual representation"));
        assertTrue(exception.getMessage().contains("User error"));
    }

    void testWrapExceptionsDisabled() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, value -> {
                    throw new RuntimeException("User error");
                })))
                .disable(DeserializationFeature.WRAP_EXCEPTIONS)
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mapper.readValue(TEST, Bar.class));
        assertFalse(exception instanceof MismatchedInputException);
        assertTrue(exception.getMessage().contains("User error"));
    }
private static ObjectMapper barMapper() {
        return VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, Bar::of)))
                .build();
    }
private static SimpleModule barModule(FunctionalScalarDeserializer<Bar> deserializer) {
        SimpleModule module = new SimpleModule("test");
        module.addDeserializer(Bar.class, deserializer);
        return module;
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

    void __invoke_testJavaTypeWithFunction() throws Exception {
        try {
            testJavaTypeWithFunction();
        } finally {
        }
    }


    void __invoke_testJavaTypeWithBiFunction() throws Exception {
        try {
            testJavaTypeWithBiFunction();
        } finally {
        }
    }


    void __invoke_testNullValue() throws Exception {
        try {
            testNullValue();
        } finally {
        }
    }


    void __invoke_testInList() throws Exception {
        try {
            testInList();
        } finally {
        }
    }


    void __invoke_testNullFieldInPojo() throws Exception {
        try {
            testNullFieldInPojo();
        } finally {
        }
    }


    void __invoke_testRejectsJsonArray() throws Exception {
        try {
            testRejectsJsonArray();
        } finally {
        }
    }


    void __invoke_testRejectsJsonObject() throws Exception {
        try {
            testRejectsJsonObject();
        } finally {
        }
    }


    void __invoke_testStringFunctionReceivesExtractedText() throws Exception {
        try {
            testStringFunctionReceivesExtractedText();
        } finally {
        }
    }


    void __invoke_testStringFunctionReceivesCoercedNumericText() throws Exception {
        try {
            testStringFunctionReceivesCoercedNumericText();
        } finally {
        }
    }


    void __invoke_testFunctionThrowsJacksonExceptionPropagatedAsIs() throws Exception {
        try {
            testFunctionThrowsJacksonExceptionPropagatedAsIs();
        } finally {
        }
    }


    void __invoke_testWrapExceptionsEnabled() throws Exception {
        try {
            testWrapExceptionsEnabled();
        } finally {
        }
    }


    void __invoke_testWrapExceptionsDisabled() throws Exception {
        try {
            testWrapExceptionsDisabled();
        } finally {
        }
    }

}
