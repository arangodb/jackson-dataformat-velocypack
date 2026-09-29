package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0326Fixture {
private static final byte[] ZERO = VPackWireFixtureTest.hex("30");
private static final byte[] SECONDS_123456789 = VPackWireFixtureTest.hex(
            "2b 15 cd 5b 07");
private static final byte[] MILLIS_123456789422 = VPackWireFixtureTest.hex(
            "2f ae 1b 99 be 1c 00 00 00");
private static final byte[] FLOAT_03 = VPackWireFixtureTest.hex(
            "c8 07 fd ff ff ff 01 60 33 65 38 05 04");
private static final byte[] SECONDS_03 = VPackWireFixtureTest.hex(
            "2b 04 6a 91 5f");
private static final byte[] MILLIS_03 = VPackWireFixtureTest.hex(
            "2f 98 21 06 50 75 01 00 00");
private static final Instant EPOCH = Instant.EPOCH;
private static final Instant INSTANT_02 = Instant.ofEpochSecond(123456789L);
private static final Instant INSTANT_03 = Instant.ofEpochSecond(1603365380L, 504_000_000L);

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsFloat03WithoutTimeZone.
    void testDeserializationAsFloat03WithoutTimeZoneVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class);
        OffsetDateTime value = reader.readValue(FLOAT_03);

        assertTimestamp(INSTANT_03, ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt01MillisecondsWithTimeZone.
    void testDeserializationAsInt01MillisecondsWithTimeZoneVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(ZERO);

        assertTimestamp(EPOCH, systemOffset(EPOCH), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt01MillisecondsWithoutTimeZone.
    void testDeserializationAsInt01MillisecondsWithoutTimeZoneVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(ZERO);

        assertTimestamp(EPOCH, ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt01NanosecondsWithTimeZone.
    void testDeserializationAsInt01NanosecondsWithTimeZoneVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(ZERO);

        assertTimestamp(EPOCH, systemOffset(EPOCH), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt01NanosecondsWithoutTimeZone.
    void testDeserializationAsInt01NanosecondsWithoutTimeZoneVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(ZERO);

        assertTimestamp(EPOCH, ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt02MillisecondsWithTimeZone.
    void testDeserializationAsInt02MillisecondsWithTimeZoneVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(MILLIS_123456789422);

        Instant instant = Instant.ofEpochSecond(123456789L, 422_000_000L);
        assertTimestamp(instant, systemOffset(instant), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt02MillisecondsWithoutTimeZone.
    void testDeserializationAsInt02MillisecondsWithoutTimeZoneVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(MILLIS_123456789422);

        assertTimestamp(Instant.ofEpochSecond(123456789L, 422_000_000L),
                ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt02NanosecondsWithTimeZone.
    void testDeserializationAsInt02NanosecondsWithTimeZoneVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(SECONDS_123456789);

        assertTimestamp(INSTANT_02, systemOffset(INSTANT_02), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt02NanosecondsWithoutTimeZone.
    void testDeserializationAsInt02NanosecondsWithoutTimeZoneVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(SECONDS_123456789);

        assertTimestamp(INSTANT_02, ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt03MillisecondsWithoutTimeZone.
    void testDeserializationAsInt03MillisecondsWithoutTimeZoneVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(MILLIS_03);

        assertTimestamp(INSTANT_03, ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt03MillisecondsWithTimeZone.
    void testDeserializationAsInt03MillisecondsWithTimeZoneVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(MILLIS_03);

        assertTimestamp(INSTANT_03, systemOffset(INSTANT_03), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsInt03NanosecondsWithTimeZone.
    void testDeserializationAsInt03NanosecondsWithTimeZoneVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        OffsetDateTime value = reader.readValue(SECONDS_03);

        assertTimestamp(INSTANT_03.truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                systemOffset(INSTANT_03), value);
    }
private static void assertTimestamp(Instant expected, ZoneOffset expectedOffset,
            OffsetDateTime actual) {
        assertTrue(expected.equals(actual.toInstant()),
                "The value is not correct. Expected instant <" + expected
                        + ">, actual <" + actual + ">");
        assertEquals(expectedOffset, actual.getOffset(), "The time zone is not correct.");
    }
private static ZoneOffset systemOffset(Instant instant) {
        return ZoneId.systemDefault().getRules().getOffset(instant);
    }

    void __invoke_testDeserializationAsFloat03WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat03WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01MillisecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt01MillisecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01MillisecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt01MillisecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01NanosecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt01NanosecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01NanosecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt01NanosecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02MillisecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt02MillisecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02MillisecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt02MillisecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02NanosecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt02NanosecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02NanosecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt02NanosecondsWithoutTimeZoneVpack();
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


    void __invoke_testDeserializationAsInt03NanosecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt03NanosecondsWithTimeZoneVpack();
        } finally {
        }
    }

}
