package tools.jackson.databind.ext.javatime.ser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.temporal.TemporalAdjuster;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0344Fixture {
private static final byte[] YEAR_1986 = VPackWireFixtureTest.hex("29 c2 07");
private static final byte[] TEMPORAL_ADJUSTER = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 0a 03");
private static final byte[] ONE = VPackWireFixtureTest.hex("31");
private static final byte[] TWO = VPackWireFixtureTest.hex("32");
private static final byte[] TWENTY_FOUR = VPackWireFixtureTest.hex("28 18");
private static final byte[] ONE_THOUSAND = VPackWireFixtureTest.hex("29 e8 03");

    // Provenance: DurationSerTest#shouldSerializeInMicros_whenSetAsPattern.
    void shouldSerializeInMicros_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE_THOUSAND,
                mapperForPattern("MICROS").writeValueAsBytes(Duration.ofMillis(1)));
    }

    // Provenance: DurationSerTest#shouldSerializeInMicrosDiscardingFractions_whenSetAsPattern.
    void shouldSerializeInMicrosDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE,
                mapperForPattern("MICROS").writeValueAsBytes(Duration.ofNanos(1500)));
    }

    // Provenance: DurationSerTest#shouldSerializeInMillis_whenSetAsPattern.
    void shouldSerializeInMillis_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE_THOUSAND,
                mapperForPattern("MILLIS").writeValueAsBytes(Duration.ofSeconds(1)));
    }

    // Provenance: DurationSerTest#shouldSerializeInMillisDiscardingFractions_whenSetAsPattern.
    void shouldSerializeInMillisDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE,
                mapperForPattern("MILLIS").writeValueAsBytes(Duration.ofNanos(1500000)));
    }

    // Provenance: DurationSerTest#shouldSerializeInHours_whenSetAsPattern.
    void shouldSerializeInHours_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(TWENTY_FOUR,
                mapperForPattern("HOURS").writeValueAsBytes(Duration.ofDays(1)));
    }

    // Provenance: DurationSerTest#shouldSerializeInHoursDiscardingFractions_whenSetAsPattern.
    void shouldSerializeInHoursDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE,
                mapperForPattern("HOURS").writeValueAsBytes(Duration.ofMinutes(90)));
    }

    // Provenance: DurationSerTest#shouldSerializeInHalfDays_whenSetAsPattern.
    void shouldSerializeInHalfDays_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(TWO,
                mapperForPattern("HALF_DAYS").writeValueAsBytes(Duration.ofDays(1)));
    }

    // Provenance: DurationSerTest#shouldSerializeInHalfDaysDiscardingFractions_whenSetAsPattern.
    void shouldSerializeInHalfDaysDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE,
                mapperForPattern("DAYS").writeValueAsBytes(Duration.ofHours(30)));
    }

    // Provenance: DurationSerTest#shouldSerializeInDays_whenSetAsPattern.
    void shouldSerializeInDays_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE,
                mapperForPattern("DAYS").writeValueAsBytes(Duration.ofDays(1)));
    }

    // Provenance: DurationSerTest#shouldSerializeInDaysDiscardingFractions_whenSetAsPattern.
    void shouldSerializeInDaysDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        assertArrayEquals(ONE,
                mapperForPattern("DAYS").writeValueAsBytes(Duration.ofHours(36)));
    }
private static ObjectMapper mapperForPattern(String pattern) {
        return VPackMapper.builder()
                .withConfigOverride(Duration.class,
                        override -> override.setFormat(JsonFormat.Value.forPattern(pattern)))
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
private static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        return bytes.toByteArray();
    }
private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return input.readObject();
        }
    }
static class TAWrapper {
        public TemporalAdjuster a;

        TAWrapper(TemporalAdjuster a) {
            this.a = a;
        }
    }

    void __invoke_shouldSerializeInMicros_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInMicros_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInMicrosDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInMicrosDiscardingFractions_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInMillis_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInMillis_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInMillisDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInMillisDiscardingFractions_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInHours_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInHours_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInHoursDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInHoursDiscardingFractions_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInHalfDays_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInHalfDays_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInHalfDaysDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInHalfDaysDiscardingFractions_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInDays_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInDays_whenSetAsPatternVpack();
        } finally {
        }
    }


    void __invoke_shouldSerializeInDaysDiscardingFractions_whenSetAsPatternVpack() throws Exception {
        try {
            shouldSerializeInDaysDiscardingFractions_whenSetAsPatternVpack();
        } finally {
        }
    }

}
