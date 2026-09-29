package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0327Fixture {
private static final byte[] SECONDS_03 = VPackWireFixtureTest.hex(
            "2b 04 6a 91 5f");
private static final byte[] NANOS_04 = VPackWireFixtureTest.hex(
            "14 0e 45 76 61 6c 75 65 2b 15 cd 5b 07 01");
private static final byte[] MILLIS_04 = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 2f ae 1b 99 be 1c 00 00 00 01");
private static final byte[] STRING_01 = VPackWireFixtureTest.hex(
            "59 31 39 36 39 2d 31 32 2d 33 31 54 31 38 3a 30 30 3a 30 30 2d 30 36 3a 30 30");
private static final byte[] STRING_01_COLONLESS = VPackWireFixtureTest.hex(
            "58 31 39 36 39 2d 31 32 2d 33 31 54 31 38 3a 30 30 3a 30 30 2d 30 36 30 30");
private static final byte[] STRING_02 = VPackWireFixtureTest.hex(
            "63 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 3a 30 30");
private static final byte[] STRING_02_COLONLESS = VPackWireFixtureTest.hex(
            "62 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 30 30");
private static final OffsetDateTime DATE_03 = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(1603365380L), ZoneId.of("America/Los_Angeles"));
private static final OffsetDateTime DATE_01 = OffsetDateTime.ofInstant(
            Instant.EPOCH, ZoneId.of("America/Chicago"));
private static final OffsetDateTime DATE_02 = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L, 183917322L),
            ZoneId.of("America/Anchorage"));
private static final OffsetDateTime DATE_04_NANOS = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L), ZoneId.of("America/Chicago"));
private static final OffsetDateTime DATE_04_MILLIS = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L, 422_000_000L),
            ZoneId.of("America/Chicago"));

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt03NanosecondsWithoutTimeZone.
    void testDeserializationAsInt03NanosecondsWithoutTimeZoneVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(SECONDS_03);

        assertTimestamp(DATE_03.toInstant().truncatedTo(ChronoUnit.SECONDS),
                ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt04NanosecondsWithoutTimeZone.
    void testDeserializationAsInt04NanosecondsWithoutTimeZoneVpack() throws Exception {
        NanosEnabledWrapper actual = new VPackMapper().readValue(NANOS_04,
                NanosEnabledWrapper.class);

        assertNotNull(actual);
        assertNotNull(actual.value);
        assertTimestamp(Instant.ofEpochSecond(123456789L), ZoneOffset.UTC, actual.value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt04NanosecondsWithTimeZone.
    void testDeserializationAsInt04NanosecondsWithTimeZoneVpack() throws Exception {
        NanosEnabledWrapper actual = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readValue(NANOS_04, NanosEnabledWrapper.class);

        assertNotNull(actual);
        assertNotNull(actual.value);
        assertTimestamp(Instant.ofEpochSecond(123456789L),
                defaultOffset(DATE_04_NANOS), actual.value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt04MillisecondsWithoutTimeZone.
    void testDeserializationAsInt04MillisecondsWithoutTimeZoneVpack() throws Exception {
        MillisDisabledWrapper actual = new VPackMapper().readValue(MILLIS_04,
                MillisDisabledWrapper.class);

        assertNotNull(actual);
        assertNotNull(actual.value);
        assertTimestamp(Instant.ofEpochSecond(123456789L, 422_000_000L),
                ZoneOffset.UTC, actual.value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt04MillisecondsWithTimeZone.
    void testDeserializationAsInt04MillisecondsWithTimeZoneVpack() throws Exception {
        MillisDisabledWrapper actual = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readValue(MILLIS_04, MillisDisabledWrapper.class);

        assertNotNull(actual);
        assertNotNull(actual.value);
        assertTimestamp(Instant.ofEpochSecond(123456789L, 422_000_000L),
                defaultOffset(DATE_04_MILLIS), actual.value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString01WithoutTimeZone.
    void testDeserializationAsString01WithoutTimeZoneVpack() throws Exception {
        OffsetDateTime value = new VPackMapper().readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_01);

        assertTimestamp(DATE_01.toInstant(), ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString01WithTimeZone.
    void testDeserializationAsString01WithTimeZoneVpack() throws Exception {
        OffsetDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_01);

        assertTimestamp(DATE_01.toInstant(), defaultOffset(DATE_01), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString01WithTimeZoneTurnedOff.
    void testDeserializationAsString01WithTimeZoneTurnedOffVpack() throws Exception {
        OffsetDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_01);

        assertTimestamp(DATE_01.toInstant(), DATE_01.getOffset(), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString01WithTimeZoneColonless.
    void testDeserializationAsString01WithTimeZoneColonlessVpack() throws Exception {
        OffsetDateTime value = new VPackMapper().readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_01_COLONLESS);

        assertTimestamp(DATE_01.toInstant(), DATE_01.getOffset(), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString02WithTimeZone.
    void testDeserializationAsString02WithTimeZoneVpack() throws Exception {
        OffsetDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_02);

        assertTimestamp(DATE_02.toInstant(), defaultOffset(DATE_02), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString02WithTimeZoneTurnedOff.
    void testDeserializationAsString02WithTimeZoneTurnedOffVpack() throws Exception {
        OffsetDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_02);

        assertTimestamp(DATE_02.toInstant(), DATE_02.getOffset(), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString02WithTimeZoneColonless.
    void testDeserializationAsString02WithTimeZoneColonlessVpack() throws Exception {
        OffsetDateTime value = new VPackMapper().readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_02_COLONLESS);

        assertTimestamp(DATE_02.toInstant(), DATE_02.getOffset(), value);
    }
private static void assertTimestamp(Instant expected, ZoneOffset expectedOffset,
            OffsetDateTime actual) {
        assertTrue(expected.equals(actual.toInstant()),
                "The value is not correct. Expected instant <" + expected
                        + ">, actual <" + actual + ">");
        assertEquals(expectedOffset, actual.getOffset(), "The time zone is not correct.");
    }
private static ZoneOffset defaultOffset(OffsetDateTime date) {
        return ZoneId.systemDefault().getRules().getOffset(date.toLocalDateTime());
    }
static final class NanosEnabledWrapper {
        @JsonFormat(with = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public OffsetDateTime value;

        public NanosEnabledWrapper() { }
    }
static final class MillisDisabledWrapper {
        @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public OffsetDateTime value;

        public MillisDisabledWrapper() { }
    }

    void __invoke_testDeserializationAsInt03NanosecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt03NanosecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt04NanosecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt04NanosecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt04NanosecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt04NanosecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt04MillisecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt04MillisecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt04MillisecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt04MillisecondsWithTimeZoneVpack();
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


    void __invoke_testDeserializationAsString01WithTimeZoneColonlessVpack() throws Exception {
        try {
            testDeserializationAsString01WithTimeZoneColonlessVpack();
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


    void __invoke_testDeserializationAsString02WithTimeZoneColonlessVpack() throws Exception {
        try {
            testDeserializationAsString02WithTimeZoneColonlessVpack();
        } finally {
        }
    }

}
