package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0362Fixture {
private static final ZoneId DEFAULT_TZ = ZoneOffset.UTC;
private static final ZoneId Z2 = ZoneId.of("America/Anchorage");
private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");
private static final ZoneId FIX_OFFSET = ZoneId.of("-08:00");
private static final ZonedDateTime DATE_02 = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L, 183917322L), Z2);
private static final ZonedDateTime DATE_03 = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(1603365380L, 123456789L), Z3);
private static final ZonedDateTime DATE_03_FIXED = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(1603365380L, 123456789L), FIX_OFFSET);
private static final byte[] STRING_02 = VPackWireFixtureTest.hex(
            "63 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 3a 30 30");
private static final byte[] STRING_02_FIXED = VPackWireFixtureTest.hex(
            "63 31 39 37 33 2d 31 31 2d 32 39 54 31 33 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 30 38 3a 30 30");
private static final byte[] STRING_03 = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");
private static final byte[] STRING_03_FIXED = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 31 30 2d 32 32 54 30 33 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 38 3a 30 30");
private static final byte[] STRING_03_ZONE_ID = VPackWireFixtureTest.hex(
            "78 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30 5b 41 6d 65 72 69 63 61 2f 4c 6f 73 5f 41 6e 67 65 6c 65 73 5d");
private static final byte[] TYPE_INFO_01 = VPackWireFixtureTest.hex(
            "06 2c 02 57 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 64 44 61 74 65 54 69 6d 65 "
          + "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22 03 1b");
private static final byte[] TYPE_INFO_02 = VPackWireFixtureTest.hex(
            "06 22 02 57 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 64 44 61 74 65 54 69 6d 65 "
          + "2b 15 cd 5b 07 03 1b");

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString02WithoutTimeZone.
    void testDeserializationAsString02WithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_02);

        assertEquals(DATE_02.toInstant(), value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString02WithTimeZone.
    void testDeserializationAsString02WithTimeZoneVpack() throws Exception {
        ZonedDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_02);

        assertEquals(DATE_02.toInstant(), value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString02WithTimeZoneTurnedOff.
    void testDeserializationAsString02WithTimeZoneTurnedOffVpack() throws Exception {
        ZonedDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_02_FIXED);

        assertEquals(DATE_02.toInstant(), value.toInstant());
        assertEquals(FIX_OFFSET, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString02WithZoneId.
    void testDeserializationAsString02WithZoneIdVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readValue(
                VPackWireFixtureTest.hex(
                        "76 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 3a 30 30 5b 41 6d 65 72 69 63 61 2f 41 6e 63 68 6f 72 61 67 65 5d"),
                ZonedDateTime.class);

        assertEquals(DATE_02.toInstant(), value.toInstant());
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString03WithoutTimeZone.
    void testDeserializationAsString03WithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_03);

        assertEquals(DATE_03.toInstant(), value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString03WithTimeZone.
    void testDeserializationAsString03WithTimeZoneVpack() throws Exception {
        ZonedDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_03);

        assertEquals(DATE_03.toInstant(), value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString03WithTimeZoneTurnedOff.
    void testDeserializationAsString03WithTimeZoneTurnedOffVpack() throws Exception {
        ZonedDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_03_FIXED);

        assertEquals(DATE_03_FIXED.toInstant(), value.toInstant());
        assertEquals(FIX_OFFSET, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString03WithZoneId.
    void testDeserializationAsString03WithZoneIdVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readValue(STRING_03_ZONE_ID,
                ZonedDateTime.class);

        assertEquals(DATE_03.toInstant(), value.toInstant());
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo01WithoutTimeZone.
    void testDeserializationWithTypeInfo01WithoutTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(null).readValue(TYPE_INFO_01, Temporal.class);

        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");
        assertEquals(DATE_02.toInstant(), actual.toInstant());
        assertEquals(DEFAULT_TZ, actual.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo01WithTimeZone.
    void testDeserializationWithTypeInfo01WithTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(TimeZone.getDefault()).readValue(TYPE_INFO_01,
                Temporal.class);

        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");
        assertEquals(DATE_02.toInstant(), actual.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), actual.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo02WithoutTimeZone.
    void testDeserializationWithTypeInfo02WithoutTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(null).readerFor(Temporal.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_02);

        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");
        assertEquals(Instant.ofEpochSecond(123456789L), actual.toInstant());
        assertEquals(DEFAULT_TZ, actual.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationWithTypeInfo02WithTimeZone.
    void testDeserializationWithTypeInfo02WithTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(TimeZone.getDefault()).readerFor(Temporal.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_02);

        ZonedDateTime actual = assertInstanceOf(ZonedDateTime.class, value,
                "The value should be an ZonedDateTime.");
        assertEquals(Instant.ofEpochSecond(123456789L), actual.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), actual.getZone(),
                "The time zone is not correct.");
    }
private static ObjectMapper typeInfoMapper(TimeZone timeZone) {
        VPackMapper.Builder builder = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class);
        if (timeZone != null) {
            builder.defaultTimeZone(timeZone);
        }
        return builder.build();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testDeserializationAsString02WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString02WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString02WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString02WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString02WithTimeZoneTurnedOffVpack() throws Exception {
        try {
            testDeserializationAsString02WithTimeZoneTurnedOffVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString02WithZoneIdVpack() throws Exception {
        try {
            testDeserializationAsString02WithZoneIdVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString03WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString03WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03WithTimeZoneTurnedOffVpack() throws Exception {
        try {
            testDeserializationAsString03WithTimeZoneTurnedOffVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03WithZoneIdVpack() throws Exception {
        try {
            testDeserializationAsString03WithZoneIdVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo02WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo02WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo02WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo02WithTimeZoneVpack();
        } finally {
        }
    }

}
