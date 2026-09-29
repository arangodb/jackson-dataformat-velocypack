package tools.jackson.databind.ext.javatime.deser;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.ext.javatime.deser.MonthDeserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0325F0 {
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] MONTH_VALUE_11 = VPackWireFixtureTest.hex(
            "14 0c 45 76 61 6c 75 65 42 31 31 01");
private static final byte[] ZERO_SECONDS = VPackWireFixtureTest.hex(
            "c8 01 f7 ff ff ff 00");
private static final byte[] ONE_HUNDRED_TWENTY_THREE_MILLION_SECONDS =
            VPackWireFixtureTest.hex(
                    "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22");
private static final byte[] FIXED_NANOSECOND_TIMESTAMP = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 01 72 10 72 09 61 23 45 67 89");
private static final byte[] CUSTOM_PATTERN_WRAPPER = VPackWireFixtureTest.hex(
            "0b 23 01 45 76 61 6c 75 65 58 31 39 37 30 5f 30 31 5f 30 31 54 30 30 3a "
          + "30 30 3a 30 30 2b 30 30 30 30 03");
private static final OffsetDateTime EPOCH = OffsetDateTime.ofInstant(
            Instant.EPOCH, ZoneOffset.UTC);
private static final OffsetDateTime ONE_HUNDRED_TWENTY_THREE_MILLION =
            OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322L),
                    ZoneOffset.UTC);

    // Provenance: MonthDeserializerTest#testDeserializeFromEmptyString.
    void testDeserializeFromEmptyStringVpack() throws Exception {
        assertNull(new VPackMapper().readValue(NULL, Month.class));

        ObjectMapper strictMapper = VPackMapper.builder()
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .build();
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> strictMapper.readValue(EMPTY_STRING, Month.class));
        assertTrue(failure.getMessage().contains("Cannot coerce empty String"),
                failure.getMessage());

        ObjectMapper emptyStringMapper = VPackMapper.builder()
                .withCoercionConfig(Month.class,
                        h -> h.setCoercion(CoercionInputShape.EmptyString,
                                CoercionAction.AsNull))
                .build();
        assertNull(emptyStringMapper.readValue(EMPTY_STRING, Month.class));
    }

    // Provenance: MonthDeserializerTest#testFormatAnnotation_oneBased.
    void testFormatAnnotationOneBasedVpack() throws Exception {
        Wrapper output = VPackMapper.builder()
                .enable(DateTimeFeature.ONE_BASED_MONTHS)
                .build()
                .readValue(MONTH_VALUE_11, Wrapper.class);
        assertEquals(Month.NOVEMBER, output.value);
    }

    // Provenance: MonthDeserializerTest#testFormatAnnotation_zeroBased.
    void testFormatAnnotationZeroBasedVpack() throws Exception {
        Wrapper output = VPackMapper.builder()
                .disable(DateTimeFeature.ONE_BASED_MONTHS)
                .build()
                .readValue(MONTH_VALUE_11, Wrapper.class);
        assertEquals(Month.DECEMBER, output.value);
    }

    // Provenance: MonthDeserializerTest#testWithDateFormatCreatesNewInstance.
    void testWithDateFormatCreatesNewInstanceVpack() throws Exception {
        MonthDeserializer original = MonthDeserializer.INSTANCE;
        MonthDeserializer withFormatter = (MonthDeserializer) invokeProtected(
                original, "withDateFormat", DateTimeFormatter.class,
                DateTimeFormatter.ofPattern("MMM"));
        assertNotSame(original, withFormatter);
    }

    // Provenance: MonthDeserializerTest#testWithLeniencyCreatesNewInstance.
    void testWithLeniencyCreatesNewInstanceVpack() throws Exception {
        MonthDeserializer original = MonthDeserializer.INSTANCE;
        MonthDeserializer strict = (MonthDeserializer) invokeProtected(
                original, "withLeniency", Boolean.class, Boolean.FALSE);
        assertNotSame(original, strict);
        assertFalse((Boolean) invokeProtected(strict, "isLenient"));
    }
private static void assertIsEqual(OffsetDateTime expected, OffsetDateTime actual) {
        org.junit.jupiter.api.Assertions.assertTrue(expected.isEqual(actual),
                "The value is not correct. Expected timezone-adjusted <" + expected
                        + ">, actual <" + actual + ">.");
    }
private static ZoneOffset defaultOffset(OffsetDateTime date) {
        return ZoneId.systemDefault().getRules().getOffset(date.toLocalDateTime());
    }
private static byte[] shortString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.US_ASCII);
        if (payload.length > 126) {
            throw new IllegalArgumentException("fixture is not a short VPack string");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static Object invokeProtected(Object target, String name,
            Class<?> parameterType, Object argument) throws Exception {
        return invokeProtected(target, name, new Class<?>[] { parameterType },
                new Object[] { argument });
    }
private static Object invokeProtected(Object target, String name) throws Exception {
        return invokeProtected(target, name, new Class<?>[0], new Object[0]);
    }
private static Object invokeProtected(Object target, String name,
            Class<?>[] parameterTypes, Object[] arguments) throws Exception {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Method method = type.getDeclaredMethod(name, parameterTypes);
                method.setAccessible(true);
                return method.invoke(target, arguments);
            } catch (NoSuchMethodException e) {
                // Search the package-private JSR-310 base classes as well.
            }
        }
        throw new NoSuchMethodException(name);
    }
static class Wrapper {
        public Month value;
    }
static class CustomPatternWrapper {
        @JsonFormat(pattern = "yyyy_MM_dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
        public OffsetDateTime value;

        public CustomPatternWrapper() { }

        CustomPatternWrapper(OffsetDateTime value) {
            this.value = value;
        }
    }

    void __invoke_testDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testFormatAnnotationOneBasedVpack() throws Exception {
        try {
            testFormatAnnotationOneBasedVpack();
        } finally {
        }
    }


    void __invoke_testFormatAnnotationZeroBasedVpack() throws Exception {
        try {
            testFormatAnnotationZeroBasedVpack();
        } finally {
        }
    }


    void __invoke_testWithDateFormatCreatesNewInstanceVpack() throws Exception {
        try {
            testWithDateFormatCreatesNewInstanceVpack();
        } finally {
        }
    }


    void __invoke_testWithLeniencyCreatesNewInstanceVpack() throws Exception {
        try {
            testWithLeniencyCreatesNewInstanceVpack();
        } finally {
        }
    }

}
