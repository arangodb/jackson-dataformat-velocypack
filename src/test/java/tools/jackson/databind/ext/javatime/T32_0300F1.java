package tools.jackson.databind.ext.javatime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;

import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0300F1 {
private static final byte[] INSTANT_FULL = VPackWireFixtureTest.hex(
            "5e 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e 31 32 33 34 35 36 37 38 39 5a");
private static final byte[] INSTANT_MILLIS = VPackWireFixtureTest.hex(
            "58 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e 31 32 33 5a");
private static final byte[] LOCAL_DATE_TIME_FULL = VPackWireFixtureTest.hex(
            "5d 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e 31 32 33 34 35 36 37 38 39");
private static final byte[] LOCAL_DATE_TIME_MILLIS = VPackWireFixtureTest.hex(
            "57 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e 31 32 33");
private static final byte[] LOCAL_TIME_FULL = VPackWireFixtureTest.hex(
            "52 31 30 3a 33 30 3a 34 35 2e 31 32 33 34 35 36 37 38 39");
private static final byte[] LOCAL_TIME_MILLIS = VPackWireFixtureTest.hex(
            "4c 31 30 3a 33 30 3a 34 35 2e 31 32 33");
private static final byte[] DURATION_FULL = VPackWireFixtureTest.hex(
            "50 50 54 32 4d 33 2e 34 35 36 37 38 39 30 31 32 53");
private static final byte[] DURATION_MILLIS = VPackWireFixtureTest.hex(
            "4a 50 54 32 4d 33 2e 34 35 36 53");
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

    // Provenance: TruncateToMillisecondsTest#testInstantSerializationTruncation.
    void testInstantSerializationTruncationVpack() throws Exception {
        Instant instant = Instant.parse("2023-01-15T10:30:45.123456789Z");

        assertArrayEquals(INSTANT_FULL, MAPPER.writeValueAsBytes(instant));
        assertArrayEquals(INSTANT_MILLIS,
                MAPPER_TRUNCATE_WRITE.writeValueAsBytes(instant));
    }

    // Provenance: TruncateToMillisecondsTest#testLocalDateTimeSerializationTruncation.
    void testLocalDateTimeSerializationTruncationVpack() throws Exception {
        LocalDateTime value = LocalDateTime.of(2023, 1, 15, 10, 30, 45, 123456789);

        assertArrayEquals(LOCAL_DATE_TIME_FULL, MAPPER.writeValueAsBytes(value));
        assertArrayEquals(LOCAL_DATE_TIME_MILLIS,
                MAPPER_TRUNCATE_WRITE.writeValueAsBytes(value));
    }

    // Provenance: TruncateToMillisecondsTest#testLocalTimeSerializationTruncation.
    void testLocalTimeSerializationTruncationVpack() throws Exception {
        LocalTime value = LocalTime.of(10, 30, 45, 123456789);

        assertArrayEquals(LOCAL_TIME_FULL, MAPPER.writeValueAsBytes(value));
        assertArrayEquals(LOCAL_TIME_MILLIS,
                MAPPER_TRUNCATE_WRITE.writeValueAsBytes(value));
    }

    // Provenance: TruncateToMillisecondsTest#testDurationSerializationTruncation.
    void testDurationSerializationTruncationVpack() throws Exception {
        Duration value = Duration.ofSeconds(123, 456789012);

        assertArrayEquals(DURATION_FULL, MAPPER.writeValueAsBytes(value));
        assertArrayEquals(DURATION_MILLIS,
                MAPPER_TRUNCATE_WRITE.writeValueAsBytes(value));
    }

    // Provenance: TruncateToMillisecondsTest#testInstantDeserializationTruncation.
    void testInstantDeserializationTruncationVpack() throws Exception {
        Instant full = MAPPER.readValue(INSTANT_FULL, Instant.class);
        Instant truncated = MAPPER_TRUNCATE_READ.readValue(INSTANT_FULL, Instant.class);

        assertEquals(123456789, full.getNano());
        assertEquals(123000000, truncated.getNano());
    }

    // Provenance: TruncateToMillisecondsTest#testLocalDateTimeDeserializationTruncation.
    void testLocalDateTimeDeserializationTruncationVpack() throws Exception {
        LocalDateTime full = MAPPER.readValue(LOCAL_DATE_TIME_FULL, LocalDateTime.class);
        LocalDateTime truncated = MAPPER_TRUNCATE_READ.readValue(
                LOCAL_DATE_TIME_FULL, LocalDateTime.class);

        assertEquals(123456789, full.getNano());
        assertEquals(123000000, truncated.getNano());
    }

    // Provenance: TruncateToMillisecondsTest#testLocalTimeDeserializationTruncation.
    void testLocalTimeDeserializationTruncationVpack() throws Exception {
        LocalTime full = MAPPER.readValue(LOCAL_TIME_FULL, LocalTime.class);
        LocalTime truncated = MAPPER_TRUNCATE_READ.readValue(LOCAL_TIME_FULL, LocalTime.class);

        assertEquals(123456789, full.getNano());
        assertEquals(123000000, truncated.getNano());
    }

    // Provenance: TruncateToMillisecondsTest#testDurationDeserializationTruncation.
    void testDurationDeserializationTruncationVpack() throws Exception {
        Duration full = MAPPER.readValue(DURATION_FULL, Duration.class);
        Duration truncated = MAPPER_TRUNCATE_READ.readValue(DURATION_FULL, Duration.class);

        assertEquals(456789012, full.getNano());
        assertEquals(456000000, truncated.getNano());
    }

    // Provenance: TruncateToMillisecondsTest#testAlreadyTruncatedValueRemains.
    void testAlreadyTruncatedValueRemainsVpack() throws Exception {
        Instant instant = Instant.parse("2023-01-15T10:30:45.123Z");
        assertEquals(123000000, instant.getNano());

        byte[] encoded = MAPPER_TRUNCATE_WRITE.writeValueAsBytes(instant);
        assertArrayEquals(INSTANT_MILLIS, encoded);
        assertEquals(instant, MAPPER_TRUNCATE_READ.readValue(encoded, Instant.class));
    }

    // Provenance: TruncateToMillisecondsTest#testInstantNumericTimestampTruncation.
    void testInstantNumericTimestampTruncationVpack() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DateTimeFeature.TRUNCATE_TO_MSECS_ON_WRITE)
                .build();
        Instant instant = Instant.parse("2023-01-15T10:30:45.123456789Z");

        Instant result = MAPPER.readValue(mapper.writeValueAsBytes(instant), Instant.class);
        assertEquals(Instant.parse("2023-01-15T10:30:45.123Z"), result);
        assertEquals(0, result.getNano() % 1_000_000);
    }

    // Provenance: TruncateToMillisecondsTest#testNegativeDurationTruncation.
    void testNegativeDurationTruncationVpack() throws Exception {
        Duration whole = Duration.ofSeconds(-5);
        Duration wholeResult = MAPPER_TRUNCATE_BOTH.readValue(
                MAPPER_TRUNCATE_WRITE.writeValueAsBytes(whole), Duration.class);
        assertEquals(-5, wholeResult.getSeconds());
        assertEquals(0, wholeResult.getNano());

        Duration fractional = Duration.ofMillis(-5123).plusNanos(456789);
        Duration fractionalResult = MAPPER_TRUNCATE_BOTH.readValue(
                MAPPER_TRUNCATE_WRITE.writeValueAsBytes(fractional), Duration.class);
        assertEquals(0, fractionalResult.toNanos() % 1_000_000);
    }

    void __invoke_testInstantSerializationTruncationVpack() throws Exception {
        try {
            testInstantSerializationTruncationVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeSerializationTruncationVpack() throws Exception {
        try {
            testLocalDateTimeSerializationTruncationVpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeSerializationTruncationVpack() throws Exception {
        try {
            testLocalTimeSerializationTruncationVpack();
        } finally {
        }
    }


    void __invoke_testDurationSerializationTruncationVpack() throws Exception {
        try {
            testDurationSerializationTruncationVpack();
        } finally {
        }
    }


    void __invoke_testInstantDeserializationTruncationVpack() throws Exception {
        try {
            testInstantDeserializationTruncationVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeDeserializationTruncationVpack() throws Exception {
        try {
            testLocalDateTimeDeserializationTruncationVpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeDeserializationTruncationVpack() throws Exception {
        try {
            testLocalTimeDeserializationTruncationVpack();
        } finally {
        }
    }


    void __invoke_testDurationDeserializationTruncationVpack() throws Exception {
        try {
            testDurationDeserializationTruncationVpack();
        } finally {
        }
    }


    void __invoke_testAlreadyTruncatedValueRemainsVpack() throws Exception {
        try {
            testAlreadyTruncatedValueRemainsVpack();
        } finally {
        }
    }


    void __invoke_testInstantNumericTimestampTruncationVpack() throws Exception {
        try {
            testInstantNumericTimestampTruncationVpack();
        } finally {
        }
    }


    void __invoke_testNegativeDurationTruncationVpack() throws Exception {
        try {
            testNegativeDurationTruncationVpack();
        } finally {
        }
    }

}
