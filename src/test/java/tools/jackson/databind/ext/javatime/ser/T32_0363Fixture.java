package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.ser.ZonedDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0363Fixture {
private static final ZoneId DEFAULT_TZ = ZoneOffset.UTC;
private static final ZoneId Z1 = ZoneId.of("America/Chicago");
private static final ZoneId Z2 = ZoneId.of("America/Anchorage");
private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");
private static final ZoneId FIX_OFFSET = ZoneId.of("-08:00");
private static final ZonedDateTime DATE_01 = ZonedDateTime.ofInstant(
            Instant.EPOCH, Z1);
private static final ZonedDateTime DATE_02 = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L, 183917322L), Z2);
private static final Instant DATE_03_MILLIS_INSTANT =
            Instant.ofEpochSecond(123456789L, 422000000L);
private static final ZonedDateTime DATE_03 = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(1603365380L, 123456789L), Z3);
private static final ZonedDateTime DATE_03_FIXED = ZonedDateTime.ofInstant(
            DATE_03.toInstant(), FIX_OFFSET);
private static final byte[] STRING_01 = VPackWireFixtureTest.hex(
            "59 31 39 36 39 2d 31 32 2d 33 31 54 31 38 3a 30 30 3a 30 30 2d 30 36 3a 30 30");
private static final byte[] STRING_02 = VPackWireFixtureTest.hex(
            "63 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 3a 30 30");
private static final byte[] STRING_03 = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");
private static final byte[] STRING_CONTEXT_OFF = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");
private static final byte[] TYPE_INFO_03_MILLIS = VPackWireFixtureTest.hex(
            "06 23 02 57 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 64 44 61 74 65 54 69 6d 65 "
          + "2c ae 1b 99 be 1c 03 1b");
private static final byte[] TYPE_INFO_04 = VPackWireFixtureTest.hex(
            "06 41 02 57 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 64 44 61 74 65 54 69 6d 65 "
          + "63 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30 "
          + "03 1b");
private static final byte[] TYPE_INFO_04_FIXED = VPackWireFixtureTest.hex(
            "06 41 02 57 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 64 44 61 74 65 54 69 6d 65 "
          + "63 32 30 32 30 2d 31 30 2d 32 32 54 30 33 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 38 3a 30 30 "
          + "03 1b");
private static final byte[] CUSTOM_FORMATTER_CONTEXT_OFF = VPackWireFixtureTest.hex(
            "53 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30");
private static final byte[] NUMERIC_CUSTOM_PATTERN = VPackWireFixtureTest.hex(
            "0b 19 01 45 76 61 6c 75 65 4e 31 39 37 30 30 31 30 31 30 30 30 30 30 30 03");
private static final byte[] INSTANT_PRIOR_TO_EPOCH = VPackWireFixtureTest.hex(
            "58 31 39 36 39 2d 31 32 2d 33 31 54 32 33 3a 35 39 3a 35 39 2e 39 39 39 5a");

    // Provenance: ZonedDateTimeSerTest#testSerializationAsString01.
    void testSerializationAsString01Vpack() throws Exception {
        assertArrayEquals(STRING_01, stringMapper().writeValueAsBytes(DATE_01));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsString02.
    void testSerializationAsString02Vpack() throws Exception {
        assertArrayEquals(STRING_02, stringMapper().writeValueAsBytes(DATE_02));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsString03.
    void testSerializationAsString03Vpack() throws Exception {
        assertArrayEquals(STRING_03, stringMapper().writeValueAsBytes(DATE_03));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOff.
    void testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOffVpack()
            throws Exception {
        ObjectMapper mapper = stringMapper();

        assertArrayEquals(STRING_CONTEXT_OFF,
                mapper.writer()
                        .with(TimeZone.getTimeZone(Z2))
                        .without(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .without(DateTimeFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                        .writeValueAsBytes(DATE_03));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOffAndACustomFormatter.
    void testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOffAndACustomFormatterVpack()
            throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().addSerializer(
                        new ZonedDateTimeSerializer(DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd'T'HH:mm:ss"))))
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        assertArrayEquals(CUSTOM_FORMATTER_CONTEXT_OFF,
                mapper.writer()
                        .with(TimeZone.getTimeZone(Z2))
                        .without(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .without(DateTimeFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                        .writeValueAsBytes(DATE_03));
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo03WithoutTimeZone.
    void testDeserializationWithTypeInfo03WithoutTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(null).readerFor(Temporal.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_03_MILLIS);
        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");

        assertEquals(DATE_03_MILLIS_INSTANT, actual.toInstant());
        assertEquals(DEFAULT_TZ, actual.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo03WithTimeZone.
    void testDeserializationWithTypeInfo03WithTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(TimeZone.getDefault()).readerFor(Temporal.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_03_MILLIS);
        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");

        assertEquals(DATE_03_MILLIS_INSTANT, actual.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), actual.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo04WithoutTimeZone.
    void testDeserializationWithTypeInfo04WithoutTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(null).readerFor(Temporal.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(TYPE_INFO_04);
        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");

        assertEquals(DATE_03.toInstant(), actual.toInstant());
        assertEquals(DEFAULT_TZ, actual.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo04WithTimeZone.
    void testDeserializationWithTypeInfo04WithTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(TimeZone.getDefault()).readerFor(Temporal.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(TYPE_INFO_04);
        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");

        assertEquals(DATE_03.toInstant(), actual.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), actual.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo04WithTimeZoneTurnedOff.
    void testDeserializationWithTypeInfo04WithTimeZoneTurnedOffVpack() throws Exception {
        Temporal value = typeInfoMapper(TimeZone.getDefault()).readerFor(Temporal.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(TYPE_INFO_04_FIXED);
        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");

        assertEquals(DATE_03_FIXED.toInstant(), actual.toInstant());
        assertEquals(FIX_OFFSET, actual.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testNumericCustomPatternWithAnnotations.
    void testNumericCustomPatternWithAnnotationsVpack() throws Exception {
        ZonedDateTime inputValue = ZonedDateTime.ofInstant(Instant.EPOCH, DEFAULT_TZ);
        ObjectMapper mapper = new VPackMapper();

        assertArrayEquals(NUMERIC_CUSTOM_PATTERN,
                mapper.writeValueAsBytes(new WrapperNumeric(inputValue)));
        WrapperNumeric result = mapper.readValue(NUMERIC_CUSTOM_PATTERN, WrapperNumeric.class);
        assertEquals(inputValue.toInstant(), result.value.toInstant());
    }

    // Provenance: ZonedDateTimeSerTest#testInstantPriorToEpochIsEqual.
    void testInstantPriorToEpochIsEqualVpack() throws Exception {
        Instant original = Instant.ofEpochMilli(-1L);
        ObjectMapper mapper = new VPackMapper();

        assertArrayEquals(INSTANT_PRIOR_TO_EPOCH, mapper.writeValueAsBytes(original));
        assertEquals(original, mapper.readValue(INSTANT_PRIOR_TO_EPOCH, Instant.class));
    }
private static ObjectMapper stringMapper() {
        return VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
private static ObjectMapper typeInfoMapper(TimeZone timeZone) {
        VPackMapper.Builder builder = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class);
        if (timeZone != null) {
            builder.defaultTimeZone(timeZone);
        }
        return builder.build();
    }
static final class WrapperNumeric {
        @JsonFormat(pattern = "yyyyMMddHHmmss", shape = JsonFormat.Shape.STRING,
                timezone = "UTC")
        public ZonedDateTime value;

        public WrapperNumeric() { }

        WrapperNumeric(ZonedDateTime value) {
            this.value = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testSerializationAsString01Vpack() throws Exception {
        try {
            testSerializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsString02Vpack() throws Exception {
        try {
            testSerializationAsString02Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsString03Vpack() throws Exception {
        try {
            testSerializationAsString03Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOffVpack() throws Exception {
        try {
            testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOffVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOffAndACustomFormatterVpack() throws Exception {
        try {
            testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOffAndACustomFormatterVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo03WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo03WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo03WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo03WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo04WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo04WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo04WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo04WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo04WithTimeZoneTurnedOffVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo04WithTimeZoneTurnedOffVpack();
        } finally {
        }
    }


    void __invoke_testNumericCustomPatternWithAnnotationsVpack() throws Exception {
        try {
            testNumericCustomPatternWithAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testInstantPriorToEpochIsEqualVpack() throws Exception {
        try {
            testInstantPriorToEpochIsEqualVpack();
        } finally {
        }
    }

}
