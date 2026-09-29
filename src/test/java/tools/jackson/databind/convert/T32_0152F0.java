package tools.jackson.databind.convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0152F0 {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] STRING_123 = VPackWireFixtureTest.hex("43 31 32 33");
private static final byte[] STRING_123_POINT_5 = VPackWireFixtureTest.hex(
            "45 31 32 33 2e 35");
private static final byte[] STRING_123_POINT_0 = VPackWireFixtureTest.hex(
            "45 31 32 33 2e 30");
private static final byte[] STRING_TRUE = VPackWireFixtureTest.hex(
            "44 74 72 75 65");
private static final byte[] INTEGER_65 = VPackWireFixtureTest.hex("28 41");
private static final ObjectMapper COERCING_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();
private static final ObjectMapper NOT_COERCING_MAPPER = VPackMapper.builder()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_EMPTY_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                    CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper MAPPER_EMPTY_TO_TRY_CONVERT = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                    CoercionInputShape.EmptyString, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper MAPPER_EMPTY_TO_NULL = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                    CoercionInputShape.EmptyString, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_EMPTY_TO_FAIL = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                    CoercionInputShape.EmptyString, CoercionAction.Fail))
            .build();

    void testNullValueFromEmpty() throws Exception {
        verifyNullOkFromEmpty(Boolean.class, null);
        verifyNullOkFromEmpty(Boolean.TYPE, Boolean.FALSE);
        verifyNullOkFromEmpty(Byte.class, null);
        verifyNullOkFromEmpty(Byte.TYPE, Byte.valueOf((byte) 0));
        verifyNullOkFromEmpty(Short.class, null);
        verifyNullOkFromEmpty(Short.TYPE, Short.valueOf((short) 0));
        verifyNullOkFromEmpty(Character.class, null);
        verifyNullOkFromEmpty(Character.TYPE, Character.valueOf((char) 0));
        verifyNullOkFromEmpty(Integer.class, null);
        verifyNullOkFromEmpty(Integer.TYPE, Integer.valueOf(0));
        verifyNullOkFromEmpty(Long.class, null);
        verifyNullOkFromEmpty(Long.TYPE, Long.valueOf(0L));
        verifyNullOkFromEmpty(Float.class, null);
        verifyNullOkFromEmpty(Float.TYPE, Float.valueOf(0.0f));
        verifyNullOkFromEmpty(Double.class, null);
        verifyNullOkFromEmpty(Double.TYPE, Double.valueOf(0.0));
        verifyNullOkFromEmpty(BigInteger.class, null);
        verifyNullOkFromEmpty(BigDecimal.class, null);
        verifyNullOkFromEmpty(AtomicBoolean.class, null);
    }

    void testNullFailFromEmpty() {
        for (Class<?> target : new Class<?>[] {
                Boolean.class, Boolean.TYPE, Byte.class, Byte.TYPE,
                Short.class, Short.TYPE, Character.class, Character.TYPE,
                Integer.class, Integer.TYPE, Long.class, Long.TYPE,
                Float.class, Float.TYPE, Double.class, Double.TYPE,
                BigInteger.class, BigDecimal.class, AtomicBoolean.class
        }) {
            verifyNullFail(target);
        }
    }

    void testStringToNumbersCoercionOk() throws Exception {
        assertEquals(Byte.valueOf((byte) 123), COERCING_MAPPER.readValue(STRING_123, Byte.TYPE));
        assertEquals(Byte.valueOf((byte) 123), COERCING_MAPPER.readValue(STRING_123, Byte.class));
        assertEquals(Short.valueOf((short) 123), COERCING_MAPPER.readValue(STRING_123, Short.TYPE));
        assertEquals(Short.valueOf((short) 123), COERCING_MAPPER.readValue(STRING_123, Short.class));
        assertEquals(Integer.valueOf(123), COERCING_MAPPER.readValue(STRING_123, Integer.TYPE));
        assertEquals(Integer.valueOf(123), COERCING_MAPPER.readValue(STRING_123, Integer.class));
        assertEquals(Long.valueOf(123), COERCING_MAPPER.readValue(STRING_123, Long.TYPE));
        assertEquals(Long.valueOf(123), COERCING_MAPPER.readValue(STRING_123, Long.class));
        assertEquals(Float.valueOf(123.5f),
                COERCING_MAPPER.readValue(STRING_123_POINT_5, Float.TYPE));
        assertEquals(Float.valueOf(123.5f),
                COERCING_MAPPER.readValue(STRING_123_POINT_5, Float.class));
        assertEquals(Double.valueOf(123.5),
                COERCING_MAPPER.readValue(STRING_123_POINT_5, Double.TYPE));
        assertEquals(Double.valueOf(123.5),
                COERCING_MAPPER.readValue(STRING_123_POINT_5, Double.class));
        assertEquals(BigInteger.valueOf(123),
                COERCING_MAPPER.readValue(STRING_123, BigInteger.class));
        assertEquals(new BigDecimal("123.0"),
                COERCING_MAPPER.readValue(STRING_123_POINT_0, BigDecimal.class));

        AtomicBoolean value = COERCING_MAPPER.readValue(STRING_TRUE, AtomicBoolean.class);
        assertTrue(value.get());
    }

    void testStringCoercionFailInteger() throws Exception {
        for (Class<?> target : new Class<?>[] {
                Byte.TYPE, Byte.class, Short.TYPE, Short.class,
                Integer.TYPE, Integer.class, Long.TYPE, Long.class
        }) {
            verifyRootStringCoerceFail(STRING_123, "123", target);
        }
    }

    void testStringCoercionFailFloat() throws Exception {
        for (Class<?> target : new Class<?>[] {
                Float.TYPE, Float.class, Double.TYPE, Double.class
        }) {
            verifyRootStringCoerceFail(STRING_123_POINT_5, "123.5", target);
        }
        verifyRootStringCoerceFail(STRING_123, "123", BigInteger.class);
        verifyRootStringCoerceFail(STRING_123_POINT_0, "123.0", BigDecimal.class);
    }

    void testMiscCoercionFail() {
        verifyIntegerToCharacterFail(Character.class,
                "Cannot coerce Integer value (65) to `java.lang.Character` value");
        verifyIntegerToCharacterFail(Character.TYPE,
                "Cannot coerce Integer value (65) to `char` value");
    }
private static void verifyNullOkFromEmpty(Class<?> target, Object expected) throws Exception {
        Object result = COERCING_MAPPER.readerFor(target)
                .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(EMPTY_STRING);
        assertEquals(expected, result);
    }
private static void verifyNullFail(Class<?> target) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> NOT_COERCING_MAPPER.readerFor(target).readValue(EMPTY_STRING));
        assertTrue(failure.getMessage().contains("Cannot coerce empty String"),
                failure.getMessage());
    }
private static void verifyRootStringCoerceFail(byte[] input, String expectedValue,
            Class<?> target) throws Exception {
        try (JsonParser parser = NOT_COERCING_MAPPER.createParser(input)) {
            MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                    () -> NOT_COERCING_MAPPER.readerFor(target).readValue(parser));
            assertTrue(failure.getMessage().contains("Cannot coerce"), failure.getMessage());
            assertSame(parser, failure.processor());
            assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
            assertEquals(expectedValue, parser.getString());
        }
    }
private static void verifyIntegerToCharacterFail(Class<?> target, String expectedMessage) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> NOT_COERCING_MAPPER.readerFor(target).readValue(INTEGER_65));
        assertTrue(failure.getMessage().contains(expectedMessage), failure.getMessage());
    }
private static void verifyScalarEmptyToNull(ObjectMapper mapper, Class<?> target)
            throws Exception {
        assertNull(mapper.readerFor(target).readValue(EMPTY_STRING));
    }
private static void verifyScalarEmptyToEmpty(ObjectMapper mapper, Class<?> target,
            Object expected) throws Exception {
        Object result = mapper.readerFor(target).readValue(EMPTY_STRING);
        assertEquals(expected, result);
    }
private static void verifyScalarToFail(ObjectMapper mapper, Class<?> target) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readerFor(target).readValue(EMPTY_STRING));
        assertTrue(failure.getMessage().contains("Cannot coerce empty String"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains(" to `" + target.getName()),
                failure.getMessage());
    }
private static void verifyAllMiscScalarsNull(ObjectMapper mapper) throws Exception {
        for (Class<?> target : miscScalarTypes()) {
            verifyScalarEmptyToNull(mapper, target);
        }
    }
private static Class<?>[] miscScalarTypes() {
        return new Class<?>[] {
                File.class, URL.class, URI.class, Class.class, JavaType.class,
                Currency.class, Pattern.class, Locale.class, Charset.class,
                TimeZone.class, InetAddress.class, InetSocketAddress.class
        };
    }

    void __invoke_testNullValueFromEmpty() throws Exception {
        try {
            testNullValueFromEmpty();
        } finally {
        }
    }


    void __invoke_testNullFailFromEmpty() throws Exception {
        try {
            testNullFailFromEmpty();
        } finally {
        }
    }


    void __invoke_testStringToNumbersCoercionOk() throws Exception {
        try {
            testStringToNumbersCoercionOk();
        } finally {
        }
    }


    void __invoke_testStringCoercionFailInteger() throws Exception {
        try {
            testStringCoercionFailInteger();
        } finally {
        }
    }


    void __invoke_testStringCoercionFailFloat() throws Exception {
        try {
            testStringCoercionFailFloat();
        } finally {
        }
    }


    void __invoke_testMiscCoercionFail() throws Exception {
        try {
            testMiscCoercionFail();
        } finally {
        }
    }

}
