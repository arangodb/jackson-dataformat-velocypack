package tools.jackson.databind.ext.javatime.deser;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0325F1 {
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

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsFloat01WithoutTimeZone.
    void testDeserializationAsFloat01WithoutTimeZoneVpack() throws Exception {
        OffsetDateTime value = new VPackMapper().readValue(ZERO_SECONDS,
                OffsetDateTime.class);
        assertIsEqual(EPOCH, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsFloat01WithTimeZone.
    void testDeserializationAsFloat01WithTimeZoneVpack() throws Exception {
        OffsetDateTime source = OffsetDateTime.ofInstant(Instant.EPOCH,
                ZoneId.of("America/Chicago"));
        ObjectMapper mapper = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build();
        OffsetDateTime value = mapper.readValue(ZERO_SECONDS, OffsetDateTime.class);
        assertIsEqual(source, value);
        assertEquals(defaultOffset(source), value.getOffset(),
                "The time zone is not correct.");
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsFloat02WithoutTimeZone.
    void testDeserializationAsFloat02WithoutTimeZoneVpack() throws Exception {
        OffsetDateTime value = new VPackMapper().readValue(
                ONE_HUNDRED_TWENTY_THREE_MILLION_SECONDS, OffsetDateTime.class);
        assertIsEqual(ONE_HUNDRED_TWENTY_THREE_MILLION, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsFloat02WithTimeZone.
    void testDeserializationAsFloat02WithTimeZoneVpack() throws Exception {
        OffsetDateTime source = OffsetDateTime.ofInstant(
                Instant.ofEpochSecond(123456789L, 183917322L),
                ZoneId.of("America/Anchorage"));
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class)
                .with(TimeZone.getDefault());
        OffsetDateTime value = reader.readValue(ONE_HUNDRED_TWENTY_THREE_MILLION_SECONDS);
        assertIsEqual(source, value);
        assertEquals(defaultOffset(source), value.getOffset(),
                "The time zone is not correct.");
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsFloat03WithTimeZone.
    void testDeserializationAsFloat03WithTimeZoneVpack() throws Exception {
        ZoneId sourceZone = ZoneId.of("America/Los_Angeles");
        Instant instant = Instant.parse("2024-07-15T19:34:56.123456789Z");
        OffsetDateTime source = OffsetDateTime.ofInstant(instant, sourceZone);
        ObjectMapper mapper = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getTimeZone(sourceZone))
                .build();

        OffsetDateTime value = mapper.readerFor(OffsetDateTime.class)
                .readValue(FIXED_NANOSECOND_TIMESTAMP);

        assertIsEqual(source, value);
        assertEquals(sourceZone.getRules().getOffset(source.toLocalDateTime()),
                value.getOffset(), "The time zone is not correct.");
    }

    // Provenance: OffsetDateTimeDeserTest#testCustomPatternWithAnnotations.
    void testCustomPatternWithAnnotationsVpack() throws Exception {
        OffsetDateTime inputValue = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        CustomPatternWrapper input = new CustomPatternWrapper(inputValue);
        ObjectMapper mapper = new VPackMapper();

        assertArrayEquals(CUSTOM_PATTERN_WRAPPER, mapper.writeValueAsBytes(input));
        CustomPatternWrapper result = mapper.readValue(CUSTOM_PATTERN_WRAPPER,
                CustomPatternWrapper.class);
        assertEquals(input.value, result.value);
    }

    // Provenance: OffsetDateTimeDeserTest#OffsetDateTime_with_offset_can_be_deserialized.
    void offsetDateTimeWithOffsetCanBeDeserializedVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        String base = "2015-07-24T12:23:34.184";

        for (String offset : new String[] { "+00", "-00" }) {
            String time = base + offset;
            OffsetDateTime zeroExpected = OffsetDateTime.parse(base + "Z");
            assertIsEqual(zeroExpected, reader.readValue(shortString(time)));
            assertIsEqual(zeroExpected, reader.readValue(shortString(time + "00")));
            assertIsEqual(zeroExpected, reader.readValue(shortString(time + ":00")));

            OffsetDateTime halfHourExpected = OffsetDateTime.parse(base + offset + ":30");
            assertIsEqual(halfHourExpected, reader.readValue(shortString(time + "30")));
            assertIsEqual(halfHourExpected, reader.readValue(shortString(time + ":30")));
        }

        for (String prefix : new String[] { "-", "+" }) {
            for (String hours : new String[] { "00", "01", "02", "03", "11", "12" }) {
                String time = base + prefix + hours;
                OffsetDateTime expectedHour = OffsetDateTime.parse(time + ":00");
                assertIsEqual(expectedHour, reader.readValue(shortString(time)));
                assertIsEqual(expectedHour, reader.readValue(shortString(time + "00")));
                assertIsEqual(expectedHour, reader.readValue(shortString(time + ":00")));
                assertIsEqual(OffsetDateTime.parse(time + ":30"),
                        reader.readValue(shortString(time + "30")));
                assertIsEqual(OffsetDateTime.parse(time + ":30"),
                        reader.readValue(shortString(time + ":30")));
            }
        }
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

    void __invoke_testDeserializationAsFloat01WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat01WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat01WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat01WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat02WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat02WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat02WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat02WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat03WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat03WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testCustomPatternWithAnnotationsVpack() throws Exception {
        try {
            testCustomPatternWithAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_offsetDateTimeWithOffsetCanBeDeserializedVpack() throws Exception {
        try {
            offsetDateTimeWithOffsetCanBeDeserializedVpack();
        } finally {
        }
    }

}
