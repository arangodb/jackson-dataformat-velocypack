package tools.jackson.databind.ext.javatime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0301Fixture {
private static final VPackMapper MAPPER = new VPackMapper();
private static final VPackMapper MAPPER_TRUNCATE_WRITE = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_WRITE)
            .build();
private static final VPackMapper MAPPER_TRUNCATE_READ = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_READ)
            .build();
private static final VPackMapper MAPPER_TRUNCATE_BOTH = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_WRITE,
                    DateTimeFeature.TRUNCATE_TO_MSECS_ON_READ)
            .build();
private static final byte[] INSTANT_FULL = VPackWireFixtureTest.hex(
            "5e 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33 34 35 36 37 38 39 5a");
private static final byte[] INSTANT_MILLIS = VPackWireFixtureTest.hex(
            "58 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33 5a");
private static final byte[] INSTANT_ZERO_NANOS = VPackWireFixtureTest.hex(
            "54 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 5a");
private static final byte[] LOCAL_DATE_TIME_MILLIS = VPackWireFixtureTest.hex(
            "57 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33");
private static final byte[] OFFSET_DATE_TIME_FULL = VPackWireFixtureTest.hex(
            "63 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33 34 35 36 37 38 39 2b 30 31 3a 30 30");
private static final byte[] OFFSET_DATE_TIME_MILLIS = VPackWireFixtureTest.hex(
            "5d 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33 2b 30 31 3a 30 30");
private static final byte[] LOCAL_DATE_INVALID_DATE = VPackWireFixtureTest.hex(
            "06 0c 03 29 e7 07 32 28 1e 03 06 07");
private static final byte[] LOCAL_DATE_INVALID_MONTH = VPackWireFixtureTest.hex(
            "06 0c 03 29 e7 07 30 28 0f 03 06 07");
private static final byte[] LOCAL_DATE_TIME_INVALID_DATE = VPackWireFixtureTest.hex(
            "06 12 05 29 e7 07 32 28 1e 28 0c 28 1e 03 06 07 09 0b");
private static final byte[] LOCAL_DATE_INVALID_DATE_STRING = VPackWireFixtureTest.hex(
            "4a 32 30 32 35 2d 30 32 2d 33 30");
private static final byte[] EXTREME_DECIMAL = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff 7f 07");

    // Provenance: TruncateToMillisecondsTest#testRoundTripWithBothFeaturesEnabled.
    void testRoundTripWithBothFeaturesEnabledVpack() throws Exception {
        Instant original = Instant.parse("2023-01-15T10:30:45.123456789Z");

        byte[] encoded = MAPPER_TRUNCATE_BOTH.writeValueAsBytes(original);
        assertArrayEquals(INSTANT_MILLIS, encoded);

        Instant result = MAPPER_TRUNCATE_BOTH.readValue(encoded, Instant.class);
        assertEquals(123000000, result.getNano());
        assertEquals(original.getEpochSecond(), result.getEpochSecond());
    }

    // Provenance: TruncateToMillisecondsTest#testRoundTripSerializeTruncateDeserializeWithout.
    void testRoundTripSerializeTruncateDeserializeWithoutVpack() throws Exception {
        LocalDateTime original = LocalDateTime.of(2023, 1, 15, 10, 30, 45, 123456789);

        byte[] encoded = MAPPER_TRUNCATE_WRITE.writeValueAsBytes(original);
        assertArrayEquals(LOCAL_DATE_TIME_MILLIS, encoded);

        LocalDateTime result = MAPPER.readValue(encoded, LocalDateTime.class);
        assertEquals(123000000, result.getNano());
    }

    // Provenance: TruncateToMillisecondsTest#testZeroNanosecondsRemains.
    void testZeroNanosecondsRemainsVpack() throws Exception {
        Instant instant = Instant.parse("2023-01-15T10:30:45Z");
        assertEquals(0, instant.getNano());

        byte[] encoded = MAPPER_TRUNCATE_WRITE.writeValueAsBytes(instant);
        assertArrayEquals(INSTANT_ZERO_NANOS, encoded);

        Instant result = MAPPER_TRUNCATE_READ.readValue(encoded, Instant.class);
        assertEquals(0, result.getNano());
        assertEquals(instant, result);
    }

    // Provenance: TruncateToMillisecondsTest#testTruncationIndependentOfNanosecondFeature.
    void testTruncationIndependentOfNanosecondFeatureVpack() throws Exception {
        ObjectMapper mapperNanosOff = VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_WRITE,
                        DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        Instant instant = Instant.parse("2023-01-15T10:30:45.123456789Z");

        byte[] encoded = mapperNanosOff.writeValueAsBytes(instant);
        // 1673778645123 as the minimal six-byte unsigned VPack integer.
        assertArrayEquals(VPackWireFixtureTest.hex("2d 83 48 fb b4 85 01"), encoded);

        Instant result = MAPPER.readValue(encoded, Instant.class);
        assertTrue(result.getNano() % 1_000_000 == 0,
                "Expected millisecond precision: " + result.getNano());
    }

    // Provenance: TruncateToMillisecondsTest#testOffsetDateTimeTruncation.
    void testOffsetDateTimeTruncationVpack() throws Exception {
        OffsetDateTime value = OffsetDateTime.parse(
                "2023-01-15T10:30:45.123456789+01:00");

        assertArrayEquals(OFFSET_DATE_TIME_FULL,
                MAPPER.writeValueAsBytes(value));
        byte[] encoded = MAPPER_TRUNCATE_WRITE.writeValueAsBytes(value);
        assertArrayEquals(OFFSET_DATE_TIME_MILLIS, encoded);

        OffsetDateTime result = MAPPER_TRUNCATE_READ.readValue(
                encoded, OffsetDateTime.class);
        assertEquals(123000000, result.getNano());
    }

    // Provenance: TruncateToMillisecondsTest#testZonedDateTimeTruncation.
    void testZonedDateTimeTruncationVpack() throws Exception {
        ZonedDateTime value = ZonedDateTime.parse(
                "2023-01-15T10:30:45.123456789+01:00[Europe/Paris]");

        assertArrayEquals(OFFSET_DATE_TIME_FULL,
                MAPPER.writeValueAsBytes(value));
        byte[] encoded = MAPPER_TRUNCATE_WRITE.writeValueAsBytes(value);
        assertArrayEquals(OFFSET_DATE_TIME_MILLIS, encoded);

        ZonedDateTime result = MAPPER_TRUNCATE_READ.readValue(
                encoded, ZonedDateTime.class);
        assertEquals(123000000, result.getNano());
    }

    void __invoke_testRoundTripWithBothFeaturesEnabledVpack() throws Exception {
        try {
            testRoundTripWithBothFeaturesEnabledVpack();
        } finally {
        }
    }


    void __invoke_testRoundTripSerializeTruncateDeserializeWithoutVpack() throws Exception {
        try {
            testRoundTripSerializeTruncateDeserializeWithoutVpack();
        } finally {
        }
    }


    void __invoke_testZeroNanosecondsRemainsVpack() throws Exception {
        try {
            testZeroNanosecondsRemainsVpack();
        } finally {
        }
    }


    void __invoke_testTruncationIndependentOfNanosecondFeatureVpack() throws Exception {
        try {
            testTruncationIndependentOfNanosecondFeatureVpack();
        } finally {
        }
    }


    void __invoke_testOffsetDateTimeTruncationVpack() throws Exception {
        try {
            testOffsetDateTimeTruncationVpack();
        } finally {
        }
    }


    void __invoke_testZonedDateTimeTruncationVpack() throws Exception {
        try {
            testZonedDateTimeTruncationVpack();
        } finally {
        }
    }

}
