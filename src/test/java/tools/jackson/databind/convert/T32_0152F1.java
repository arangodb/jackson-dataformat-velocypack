package tools.jackson.databind.convert;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
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

class T32_0152F1 {
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

    void testLegacyDateTimeCoercions() throws Exception {
        assertNull(DEFAULT_MAPPER.readValue(EMPTY_STRING, Calendar.class));
        assertNull(DEFAULT_MAPPER.readValue(EMPTY_STRING, Date.class));
        assertNull(MAPPER_EMPTY_TO_NULL.readValue(EMPTY_STRING, Calendar.class));
        assertNull(MAPPER_EMPTY_TO_NULL.readValue(EMPTY_STRING, Date.class));
        assertNull(MAPPER_EMPTY_TO_TRY_CONVERT.readValue(EMPTY_STRING, Calendar.class));
        assertNull(MAPPER_EMPTY_TO_TRY_CONVERT.readValue(EMPTY_STRING, Date.class));

        // The upstream Calendar AsEmpty assertion is disabled; Date remains active.
        assertEquals(new Date(0L), MAPPER_EMPTY_TO_EMPTY.readValue(EMPTY_STRING, Date.class));

        verifyScalarToFail(MAPPER_EMPTY_TO_FAIL, Calendar.class);
        verifyScalarToFail(MAPPER_EMPTY_TO_FAIL, Date.class);
    }

    void testScalarDefaultsFromEmpty() throws Exception {
        verifyScalarEmptyToNull(DEFAULT_MAPPER, File.class);
        verifyScalarEmptyToNull(DEFAULT_MAPPER, URL.class);
        verifyScalarEmptyToEmpty(DEFAULT_MAPPER, URI.class, URI.create(""));
        verifyScalarEmptyToNull(DEFAULT_MAPPER, Class.class);
        verifyScalarEmptyToNull(DEFAULT_MAPPER, JavaType.class);
        verifyScalarEmptyToNull(DEFAULT_MAPPER, Currency.class);
        verifyScalarEmptyToNull(DEFAULT_MAPPER, Pattern.class);
        verifyScalarEmptyToEmpty(DEFAULT_MAPPER, Locale.class, Locale.ROOT);
        verifyScalarEmptyToNull(DEFAULT_MAPPER, Charset.class);
        verifyScalarEmptyToNull(DEFAULT_MAPPER, TimeZone.class);
        verifyScalarEmptyToNull(DEFAULT_MAPPER, InetAddress.class);
        verifyScalarEmptyToNull(DEFAULT_MAPPER, InetSocketAddress.class);
    }

    void testScalarEmptyToEmpty() throws Exception {
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, File.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, URL.class);
        verifyScalarEmptyToEmpty(MAPPER_EMPTY_TO_EMPTY, URI.class, URI.create(""));
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, Class.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, JavaType.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, Currency.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, Pattern.class);
        verifyScalarEmptyToEmpty(MAPPER_EMPTY_TO_EMPTY, Locale.class, Locale.ROOT);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, Charset.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, TimeZone.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, InetAddress.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_EMPTY, InetSocketAddress.class);
    }

    void testScalarEmptyToNull() throws Exception {
        verifyAllMiscScalarsNull(MAPPER_EMPTY_TO_NULL);
    }

    void testScalarEmptyToTryConvert() throws Exception {
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, File.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, URL.class);
        verifyScalarEmptyToEmpty(MAPPER_EMPTY_TO_TRY_CONVERT, URI.class, URI.create(""));
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, Class.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, JavaType.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, Currency.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, Pattern.class);
        verifyScalarEmptyToEmpty(MAPPER_EMPTY_TO_TRY_CONVERT, Locale.class, Locale.ROOT);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, Charset.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, TimeZone.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, InetAddress.class);
        verifyScalarEmptyToNull(MAPPER_EMPTY_TO_TRY_CONVERT, InetSocketAddress.class);
    }

    void testScalarsFailFromEmpty() {
        for (Class<?> target : miscScalarTypes()) {
            verifyScalarToFail(MAPPER_EMPTY_TO_FAIL, target);
        }
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

    void __invoke_testLegacyDateTimeCoercions() throws Exception {
        try {
            testLegacyDateTimeCoercions();
        } finally {
        }
    }


    void __invoke_testScalarDefaultsFromEmpty() throws Exception {
        try {
            testScalarDefaultsFromEmpty();
        } finally {
        }
    }


    void __invoke_testScalarEmptyToEmpty() throws Exception {
        try {
            testScalarEmptyToEmpty();
        } finally {
        }
    }


    void __invoke_testScalarEmptyToNull() throws Exception {
        try {
            testScalarEmptyToNull();
        } finally {
        }
    }


    void __invoke_testScalarEmptyToTryConvert() throws Exception {
        try {
            testScalarEmptyToTryConvert();
        } finally {
        }
    }


    void __invoke_testScalarsFailFromEmpty() throws Exception {
        try {
            testScalarsFailFromEmpty();
        } finally {
        }
    }

}
