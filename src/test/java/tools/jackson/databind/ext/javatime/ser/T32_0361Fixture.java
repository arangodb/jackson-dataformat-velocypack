package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0361Fixture {
private static final ZoneId DEFAULT_TZ = ZoneOffset.UTC;
private static final ZoneId FIX_OFFSET = ZoneId.of("-08:00");
private static final byte[] INT_02_NANOSECONDS = VPackWireFixtureTest.hex(
            "2b 15 cd 5b 07");
private static final byte[] INT_02_MILLISECONDS = VPackWireFixtureTest.hex(
            "2c ae 1b 99 be 1c");
private static final byte[] INT_03_NANOSECONDS = VPackWireFixtureTest.hex(
            "2b b1 68 de 3a");
private static final byte[] INT_03_MILLISECONDS = VPackWireFixtureTest.hex(
            "2c e3 f3 c8 f4 e5");
private static final byte[] STRING_OFFSET = VPackWireFixtureTest.hex(
            "59 31 39 36 39 2d 31 32 2d 33 31 54 31 38 3a 30 30 3a 30 30 2d 30 36 3a 30 30");
private static final byte[] STRING_FIXED_OFFSET = VPackWireFixtureTest.hex(
            "59 31 39 36 39 2d 31 32 2d 33 31 54 31 36 3a 30 30 3a 30 30 2d 30 38 3a 30 30");
private static final byte[] STRING_ZONE_ID = VPackWireFixtureTest.hex(
            "6a 31 39 36 39 2d 31 32 2d 33 31 54 31 38 3a 30 30 3a 30 30 2d 30 36 3a 30 30 5b 41 6d 65 72 69 63 61 2f 43 68 69 63 61 67 6f 5d");

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt02NanosecondsWithoutTimeZone.
    void testDeserializationAsInt02NanosecondsWithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(INT_02_NANOSECONDS);

        assertEquals(Instant.ofEpochSecond(123456789L), value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt02NanosecondsWithTimeZone.
    void testDeserializationAsInt02NanosecondsWithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .with(TimeZone.getDefault())
                .readValue(INT_02_NANOSECONDS);

        assertEquals(Instant.ofEpochSecond(123456789L), value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt02MillisecondsWithoutTimeZone.
    void testDeserializationAsInt02MillisecondsWithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(INT_02_MILLISECONDS);

        assertEquals(Instant.ofEpochSecond(123456789L, 422000000L), value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt02MillisecondsWithTimeZone.
    void testDeserializationAsInt02MillisecondsWithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .with(TimeZone.getDefault())
                .readValue(INT_02_MILLISECONDS);

        assertEquals(Instant.ofEpochSecond(123456789L, 422000000L), value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt03NanosecondsWithoutTimeZone.
    void testDeserializationAsInt03NanosecondsWithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(INT_03_NANOSECONDS);

        assertEquals(Instant.ofEpochSecond(987654321L), value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt03NanosecondsWithTimeZone.
    void testDeserializationAsInt03NanosecondsWithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .with(TimeZone.getDefault())
                .readValue(INT_03_NANOSECONDS);

        assertEquals(Instant.ofEpochSecond(987654321L), value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt03MillisecondsWithoutTimeZone.
    void testDeserializationAsInt03MillisecondsWithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(INT_03_MILLISECONDS);

        assertEquals(Instant.ofEpochSecond(987654321L, 123000000L), value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt03MillisecondsWithTimeZone.
    void testDeserializationAsInt03MillisecondsWithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .with(TimeZone.getDefault())
                .readValue(INT_03_MILLISECONDS);

        assertEquals(Instant.ofEpochSecond(987654321L, 123000000L), value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString01WithoutTimeZone.
    void testDeserializationAsString01WithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_OFFSET);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString01WithTimeZone.
    void testDeserializationAsString01WithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .with(TimeZone.getDefault())
                .readValue(STRING_OFFSET);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString01WithTimeZoneTurnedOff.
    void testDeserializationAsString01WithTimeZoneTurnedOffVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .with(TimeZone.getDefault())
                .readValue(STRING_FIXED_OFFSET);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(FIX_OFFSET, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsString01WithZoneId.
    void testDeserializationAsString01WithZoneIdVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readValue(STRING_ZONE_ID, ZonedDateTime.class);

        assertEquals(Instant.EPOCH, value.toInstant());
    }

    void __invoke_testDeserializationAsInt02NanosecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt02NanosecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02NanosecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt02NanosecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02MillisecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt02MillisecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02MillisecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt02MillisecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt03NanosecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt03NanosecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt03NanosecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt03NanosecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt03MillisecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt03MillisecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt03MillisecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt03MillisecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString01WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString01WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01WithTimeZoneTurnedOffVpack() throws Exception {
        try {
            testDeserializationAsString01WithTimeZoneTurnedOffVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01WithZoneIdVpack() throws Exception {
        try {
            testDeserializationAsString01WithZoneIdVpack();
        } finally {
        }
    }

}
