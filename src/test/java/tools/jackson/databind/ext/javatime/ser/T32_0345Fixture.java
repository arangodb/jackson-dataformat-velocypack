package tools.jackson.databind.ext.javatime.ser;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0345Fixture {
private static final byte[] NANOS_IN_ONE_HOUR = VPackWireFixtureTest.hex(
            "2d 00 a0 b8 30 46 03");
private static final byte[] SIXTY = VPackWireFixtureTest.hex("28 3c");
private static final byte[] ONE = VPackWireFixtureTest.hex("31");
private static final byte[] FORTY_FIVE_SECONDS_MILLIS = VPackWireFixtureTest.hex(
            "29 c8 af");
private static final byte[] NEGATIVE_THIRTY_TWO_SECONDS_MILLIS = VPackWireFixtureTest.hex(
            "21 00 83");
private static final byte[] THIRTEEN_MILLION_FOUR_HUNDRED_NINETY_EIGHT_THOUSAND_MILLIS =
            VPackWireFixtureTest.hex("2a 90 f6 cd");
private static final byte[] THIRTEEN_MILLION_FOUR_HUNDRED_NINETY_EIGHT_THOUSAND_EIGHT_HUNDRED_THIRTY_SEVEN_MILLIS =
            VPackWireFixtureTest.hex("2a d5 f9 cd");
private static final byte[] PT_ONE_MINUTE = VPackWireFixtureTest.hex(
            "44 50 54 31 4d");
private static final byte[] PT_THREE_HOURS = VPackWireFixtureTest.hex(
            "54 50 54 33 48 34 34 4d 35 38 2e 30 30 30 30 30 38 33 37 34 53");
private static final byte[] MINUTES_BEAN = VPackWireFixtureTest.hex(
            "0b 0b 01 44 6d 69 6e 73 28 78 03");
private static final byte[] MIN_VALUE_SECONDS_BEAN = VPackWireFixtureTest.hex(
            "0b 16 01 48 64 75 72 61 74 69 6f 6e 27 00 00 00 00 00 00 00 80 03");

    // Provenance: DurationSerTest#shouldSerializeInNanos_whenSetAsPattern.
    void shouldSerializeInNanos_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(NANOS_IN_ONE_HOUR,
                mapperForPattern("NANOS").writeValueAsBytes(Duration.ofHours(1)));
    }

    // Provenance: DurationSerTest#shouldSerializeInSeconds_whenSetAsPattern.
    void shouldSerializeInSeconds_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(SIXTY,
                mapperForPattern("SECONDS").writeValueAsBytes(Duration.ofMinutes(1)));
    }

    // Provenance: DurationSerTest#shouldSerializeInSecondsDiscardingFractions_whenSetAsPattern.
    void shouldSerializeInSecondsDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE,
                mapperForPattern("SECONDS").writeValueAsBytes(Duration.ofMillis(1500)));
    }

    // Provenance: DurationSerTest#shouldSerializeInMinutes_whenSetAsPattern.
    void shouldSerializeInMinutes_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(SIXTY,
                mapperForPattern("MINUTES").writeValueAsBytes(Duration.ofHours(1)));
    }

    // Provenance: DurationSerTest#shouldSerializeInMinutesDiscardingFractions_whenSetAsPattern.
    void shouldSerializeInMinutesDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE,
                mapperForPattern("MINUTES").writeValueAsBytes(Duration.ofSeconds(90)));
    }

    // Provenance: DurationSerTest#testDurationFormatOverrideMinutes.
    void testDurationFormatOverrideMinutesVpack() throws Exception {
        assertArrayEquals(MINUTES_BEAN,
                mapperForPatternBean().writeValueAsBytes(new MyDto224(Duration.ofHours(2))));
    }

    // Provenance: DurationSerTest#testDurationFormatOverrideSeconds.
    void testDurationFormatOverrideSecondsVpack() throws Exception {
        assertArrayEquals(MIN_VALUE_SECONDS_BEAN,
                mapperForPatternBean().writeValueAsBytes(
                        new Bean282(Duration.ofSeconds(Long.MIN_VALUE))));
    }

    // Provenance: DurationSerTest#testSerializationAsString01.
    void testSerializationAsString01Vpack() throws Exception {
        assertArrayEquals(PT_ONE_MINUTE,
                new VPackMapper().writeValueAsBytes(Duration.ofSeconds(60L, 0)));
    }

    // Provenance: DurationSerTest#testSerializationAsString02.
    void testSerializationAsString02Vpack() throws Exception {
        assertArrayEquals(PT_THREE_HOURS,
                new VPackMapper().writeValueAsBytes(Duration.ofSeconds(13498L, 8374)));
    }

    // Provenance: DurationSerTest#testSerializationAsTimestampMilliseconds01.
    void testSerializationAsTimestampMilliseconds01Vpack() throws Exception {
        ObjectMapper mapper = timestampMillisecondsMapper();
        assertArrayEquals(FORTY_FIVE_SECONDS_MILLIS,
                mapper.writeValueAsBytes(Duration.ofSeconds(45L, 0)));
        assertArrayEquals(NEGATIVE_THIRTY_TWO_SECONDS_MILLIS,
                mapper.writeValueAsBytes(Duration.ofSeconds(-32L, 0)));
    }

    // Provenance: DurationSerTest#testSerializationAsTimestampMilliseconds02.
    void testSerializationAsTimestampMilliseconds02Vpack() throws Exception {
        assertArrayEquals(THIRTEEN_MILLION_FOUR_HUNDRED_NINETY_EIGHT_THOUSAND_MILLIS,
                timestampMillisecondsMapper().writeValueAsBytes(
                        Duration.ofSeconds(13498L, 8374)));
    }

    // Provenance: DurationSerTest#testSerializationAsTimestampMilliseconds03.
    void testSerializationAsTimestampMilliseconds03Vpack() throws Exception {
        assertArrayEquals(THIRTEEN_MILLION_FOUR_HUNDRED_NINETY_EIGHT_THOUSAND_EIGHT_HUNDRED_THIRTY_SEVEN_MILLIS,
                timestampMillisecondsMapper().writeValueAsBytes(
                        Duration.ofSeconds(13498L, 837481723)));
    }
private static ObjectMapper mapperForPattern(String pattern) {
        return VPackMapper.builder()
                .withConfigOverride(Duration.class,
                        override -> override.setFormat(JsonFormat.Value.forPattern(pattern)))
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
private static ObjectMapper timestampMillisecondsMapper() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
private static ObjectMapper mapperForPatternBean() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
static class MyDto224 {
        @JsonFormat(pattern = "MINUTES")
        @JsonProperty("mins")
        final Duration duration;

        public MyDto224(Duration duration) {
            this.duration = duration;
        }

        public Duration getDuration() {
            return duration;
        }
    }
static class Bean282 {
        @JsonFormat(pattern = "SECONDS")
        public Duration duration;

        public Bean282(Duration duration) {
            this.duration = duration;
        }
    }

    void __invoke_shouldSerializeInNanos_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInNanos_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInSeconds_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInSeconds_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInSecondsDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInSecondsDiscardingFractions_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInMinutes_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInMinutes_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInMinutesDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInMinutesDiscardingFractions_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_testDurationFormatOverrideMinutesVpack() throws Exception {
        try {
            testDurationFormatOverrideMinutesVpack();
        } finally {
        }
    }


    void __invoke_testDurationFormatOverrideSecondsVpack() throws Exception {
        try {
            testDurationFormatOverrideSecondsVpack();
        } finally {
        }
    }


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


    void __invoke_testSerializationAsTimestampMilliseconds01Vpack() throws Exception {
        try {
            testSerializationAsTimestampMilliseconds01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestampMilliseconds02Vpack() throws Exception {
        try {
            testSerializationAsTimestampMilliseconds02Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestampMilliseconds03Vpack() throws Exception {
        try {
            testSerializationAsTimestampMilliseconds03Vpack();
        } finally {
        }
    }

}
