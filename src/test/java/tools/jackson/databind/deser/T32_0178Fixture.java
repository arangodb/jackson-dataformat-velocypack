package tools.jackson.databind.deser;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.deser.std.FunctionalScalarDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0178Fixture {
private static final byte[] HELLO = VPackWireFixtureTest.hex(
            "45 68 65 6c 6c 6f");
private static final byte[] INTEGER_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] DECIMAL_3_14159 = VPackWireFixtureTest.hex(
            "c8 03 fb ff ff ff 31 41 59");
private static final byte[] NEGATIVE_42 = VPackWireFixtureTest.hex("20 d6");
private static final byte[] BOOLEAN_TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] BOOLEAN_FALSE = VPackWireFixtureTest.hex("19");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] BAD = VPackWireFixtureTest.hex(
            "43 62 61 64");

    void testClassWithFunction() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, Bar::of)))
                .build();

        Bar result = mapper.readValue(HELLO, Bar.class);
        assertEquals("hello", result.getValue());
    }

    void testFromIntegerNumber() {
        Bar result = barMapper().readValue(INTEGER_123, Bar.class);
        assertEquals("123", result.getValue());
    }

    void testFromDecimalNumber() {
        Bar result = barMapper().readValue(DECIMAL_3_14159, Bar.class);
        assertEquals("3.14159", result.getValue());
    }

    void testFromNegativeNumber() {
        Bar result = barMapper().readValue(NEGATIVE_42, Bar.class);
        assertEquals("-42", result.getValue());
    }

    void testFromBooleanTrue() {
        Bar result = barMapper().readValue(BOOLEAN_TRUE, Bar.class);
        assertEquals("true", result.getValue());
    }

    void testFromBooleanFalse() {
        Bar result = barMapper().readValue(BOOLEAN_FALSE, Bar.class);
        assertEquals("false", result.getValue());
    }

    void testEmptyStringDefault() {
        Bar result = barMapper().readValue(EMPTY_STRING, Bar.class);
        assertNull(result);
    }

    void testEmptyStringCoercionFail() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, Bar::of)))
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.Fail))
                .build();

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(EMPTY_STRING, Bar.class));
        assertTrue(exception.getMessage().contains("Cannot coerce empty String"));
    }

    void testEmptyStringCoercionAsNull() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, Bar::of)))
                .withCoercionConfig(LogicalType.OtherScalar, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsNull))
                .build();

        assertNull(mapper.readValue(EMPTY_STRING, Bar.class));
    }

    void testEmptyStringCoercionAsEmpty() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, Bar::of)))
                .withCoercionConfig(LogicalType.OtherScalar, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
                .build();

        assertNull(mapper.readValue(EMPTY_STRING, Bar.class));
    }

    void testFunctionThrowsIllegalArgumentException() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, value -> {
                    throw new IllegalArgumentException("Invalid format: " + value);
                })))
                .build();

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(BAD, Bar.class));
        assertTrue(exception.getMessage().contains("not a valid textual representation"));
        assertTrue(exception.getMessage().contains("Invalid format"));
    }

    void testFunctionThrowsCustomException() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, value -> {
                    throw new RuntimeException("Custom error: " + value);
                })))
                .build();

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(BAD, Bar.class));
        assertTrue(exception.getMessage().contains("not a valid textual representation"));
        assertTrue(exception.getMessage().contains("Custom error"));
    }
private static ObjectMapper barMapper() {
        return VPackMapper.builder()
                .addModule(barModule(new FunctionalScalarDeserializer<>(Bar.class, Bar::of)))
                .build();
    }
private static SimpleModule barModule(
            FunctionalScalarDeserializer<Bar> deserializer) {
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

    void __invoke_testClassWithFunction() throws Exception {
        try {
            testClassWithFunction();
        } finally {
        }
    }


    void __invoke_testFromIntegerNumber() throws Exception {
        try {
            testFromIntegerNumber();
        } finally {
        }
    }


    void __invoke_testFromDecimalNumber() throws Exception {
        try {
            testFromDecimalNumber();
        } finally {
        }
    }


    void __invoke_testFromNegativeNumber() throws Exception {
        try {
            testFromNegativeNumber();
        } finally {
        }
    }


    void __invoke_testFromBooleanTrue() throws Exception {
        try {
            testFromBooleanTrue();
        } finally {
        }
    }


    void __invoke_testFromBooleanFalse() throws Exception {
        try {
            testFromBooleanFalse();
        } finally {
        }
    }


    void __invoke_testEmptyStringDefault() throws Exception {
        try {
            testEmptyStringDefault();
        } finally {
        }
    }


    void __invoke_testEmptyStringCoercionFail() throws Exception {
        try {
            testEmptyStringCoercionFail();
        } finally {
        }
    }


    void __invoke_testEmptyStringCoercionAsNull() throws Exception {
        try {
            testEmptyStringCoercionAsNull();
        } finally {
        }
    }


    void __invoke_testEmptyStringCoercionAsEmpty() throws Exception {
        try {
            testEmptyStringCoercionAsEmpty();
        } finally {
        }
    }


    void __invoke_testFunctionThrowsIllegalArgumentException() throws Exception {
        try {
            testFunctionThrowsIllegalArgumentException();
        } finally {
        }
    }


    void __invoke_testFunctionThrowsCustomException() throws Exception {
        try {
            testFunctionThrowsCustomException();
        } finally {
        }
    }

}
