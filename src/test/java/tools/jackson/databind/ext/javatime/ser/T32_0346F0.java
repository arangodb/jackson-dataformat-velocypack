package tools.jackson.databind.ext.javatime.ser;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0346F0 {
private static final byte[] DURATION_60_NANOS = VPackWireFixtureTest.hex(
            "c8 06 f7 ff ff ff 06 00 00 00 00 00");
private static final byte[] DURATION_FRACTIONAL_NANOS = VPackWireFixtureTest.hex(
            "c8 07 f7 ff ff ff 13 49 80 00 00 83 74");
private static final byte[] POSITIVE_MILLISECOND_NANOS = VPackWireFixtureTest.hex(
            "c8 04 f7 ff ff ff 01 00 00 00");
private static final byte[] NEGATIVE_MILLISECOND_NANOS = VPackWireFixtureTest.hex(
            "d0 04 f7 ff ff ff 01 00 00 00");
private static final byte[] INSTANT_ZERO_NANOS = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff ff 00");
private static final byte[] ZERO_MILLISECONDS = VPackWireFixtureTest.hex("30");
private static final byte[] INSTANT_MILLISECONDS = VPackWireFixtureTest.hex(
            "2c bf 1a 99 be 1c");
private static final byte[] INSTANT_ZERO_STRING = VPackWireFixtureTest.hex(
            "54 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 5a");
private static final byte[] INSTANT_FRACTIONAL_STRING = VPackWireFixtureTest.hex(
            "5e 31 39 37 33 2d 31 31 2d 32 39 54 32 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 5a");
private static final byte[] INSTANT_DETERMINISTIC_STRING = VPackWireFixtureTest.hex(
            "5e 32 30 32 30 2d 31 30 2d 32 30 54 31 32 3a 33 34 3a 35 36 2e 31 32 33 34 35 36 37 38 39 5a");
private static final byte[] TYPED_DURATION_NANOS = VPackWireFixtureTest.hex(
            "06 25 02 52 6a 61 76 61 2e 74 69 6d 65 2e 44 75 72 61 74 69 6f 6e "
          + "c8 07 f7 ff ff ff 13 49 80 00 00 83 74 03 16");
private static final byte[] TYPED_DURATION_MILLISECONDS = VPackWireFixtureTest.hex(
            "06 1c 02 52 6a 61 76 61 2e 74 69 6d 65 2e 44 75 72 61 74 69 6f 6e "
          + "2a d5 f9 cd 03 16");
private static final byte[] TYPED_DURATION_STRING = VPackWireFixtureTest.hex(
            "06 2d 02 52 6a 61 76 61 2e 74 69 6d 65 2e 44 75 72 61 74 69 6f 6e "
          + "54 50 54 33 48 34 34 4d 35 38 2e 30 30 30 30 30 38 33 37 34 53 "
          + "03 16");

    // Provenance: DurationSerTest#testSerializationAsTimestampNanoseconds01.
    void testSerializationAsTimestampNanoseconds01Vpack() throws Exception {
        assertArrayEquals(DURATION_60_NANOS,
                durationNanosecondsMapper().writeValueAsBytes(Duration.ofSeconds(60L, 0)));
    }

    // Provenance: DurationSerTest#testSerializationAsTimestampNanoseconds02.
    void testSerializationAsTimestampNanoseconds02Vpack() throws Exception {
        assertArrayEquals(DURATION_FRACTIONAL_NANOS,
                durationNanosecondsMapper().writeValueAsBytes(Duration.ofSeconds(13498L, 8374)));
    }

    // Provenance: DurationSerTest#testSerializationAsTimestampNanoseconds03.
    void testSerializationAsTimestampNanoseconds03Vpack() throws Exception {
        ObjectMapper mapper = durationNanosecondsMapper();
        assertArrayEquals(POSITIVE_MILLISECOND_NANOS,
                mapper.writeValueAsBytes(Duration.ofMillis(1L)));
        assertArrayEquals(NEGATIVE_MILLISECOND_NANOS,
                mapper.writeValueAsBytes(Duration.ofMillis(-1L)));
    }

    // Provenance: DurationSerTest#testSerializationWithTypeInfo01.
    void testSerializationWithTypeInfo01Vpack() throws Exception {
        assertArrayEquals(TYPED_DURATION_NANOS,
                typedDurationMapper(true, true).writeValueAsBytes(
                        Duration.ofSeconds(13498L, 8374)));
    }

    // Provenance: DurationSerTest#testSerializationWithTypeInfo02.
    void testSerializationWithTypeInfo02Vpack() throws Exception {
        assertArrayEquals(TYPED_DURATION_MILLISECONDS,
                typedDurationMapper(true, false).writeValueAsBytes(
                        Duration.ofSeconds(13498L, 837481723)));
    }

    // Provenance: DurationSerTest#testSerializationWithTypeInfo03.
    void testSerializationWithTypeInfo03Vpack() throws Exception {
        assertArrayEquals(TYPED_DURATION_STRING,
                typedDurationMapper(false, true).writeValueAsBytes(
                        Duration.ofSeconds(13498L, 8374)));
    }
private static ObjectMapper durationNanosecondsMapper() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .enable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
private static ObjectMapper typedDurationMapper(boolean asTimestamp, boolean nanos) {
        var builder = VPackMapper.builder()
                .addMixIn(TemporalAmount.class, DurationTypeInfo.class);
        if (asTimestamp) {
            builder.enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        } else {
            builder.disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        }
        if (nanos) {
            builder.enable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        } else {
            builder.disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        }
        return builder.build();
    }
private static ObjectMapper stringInstantMapper() {
        return VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
private static ObjectMapper instantMillisecondsMapper() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
private static ObjectMapper instantNanosecondsMapper() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface DurationTypeInfo { }

    void __invoke_testSerializationAsTimestampNanoseconds01Vpack() throws Exception {
        try {
            testSerializationAsTimestampNanoseconds01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestampNanoseconds02Vpack() throws Exception {
        try {
            testSerializationAsTimestampNanoseconds02Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestampNanoseconds03Vpack() throws Exception {
        try {
            testSerializationAsTimestampNanoseconds03Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithTypeInfo01Vpack() throws Exception {
        try {
            testSerializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithTypeInfo02Vpack() throws Exception {
        try {
            testSerializationWithTypeInfo02Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithTypeInfo03Vpack() throws Exception {
        try {
            testSerializationWithTypeInfo03Vpack();
        } finally {
        }
    }

}
