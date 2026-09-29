package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0352Fixture {
private static final ZoneId Z1 = ZoneId.of("America/Chicago");
private static final ZoneId Z2 = ZoneId.of("America/Anchorage");
private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");
private static final Instant FRACTIONAL_INSTANT =
            Instant.ofEpochSecond(123456789L, 183917322L);
private static final Instant DETERMINISTIC_INSTANT =
            Instant.parse("2023-01-15T10:30:45.123456789Z");
private static final byte[] TIMESTAMP_01_NANOS = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff ff 00");
private static final byte[] TIMESTAMP_01_MILLIS = VPackWireFixtureTest.hex("30");
private static final byte[] TIMESTAMP_02_NANOS = VPackWireFixtureTest.hex(
            "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22");
private static final byte[] TIMESTAMP_02_MILLIS = VPackWireFixtureTest.hex(
            "2c bf 1a 99 be 1c");
private static final byte[] TIMESTAMP_03_NANOS = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 01 67 37 78 64 51 23 45 67 89");
private static final byte[] TIMESTAMP_03_MILLIS = VPackWireFixtureTest.hex(
            "2d 83 48 fb b4 85 01");
private static final byte[] JSON_FORMAT_FIELD = VPackWireFixtureTest.hex(
            "0b 23 01 45 76 61 6c 75 65 58 32 30 32 32 5f 30 34 5f 32 37 54 31 32 3a 30 30 3a 30 30 2b 30 32 30 30 03");
private static final byte[] JSON_FORMAT_OVERRIDE = VPackWireFixtureTest.hex(
            "53 32 30 32 32 2e 30 34 2e 32 37 78 31 32 3a 30 30 3a 30 30");
private static final byte[] TYPE_INFO_NANOS = VPackWireFixtureTest.hex(
            "06 2d 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22 03 1c");
private static final byte[] MAPPER_TIME_ZONE_01 = VPackWireFixtureTest.hex(
            "59 31 39 36 39 2d 31 32 2d 33 31 54 31 38 3a 30 30 3a 30 30 2d 30 36 3a 30 30");
private static final byte[] MAPPER_TIME_ZONE_02 = VPackWireFixtureTest.hex(
            "63 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 3a 30 30");
private static final byte[] MAPPER_TIME_ZONE_03 = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 30 36 2d 30 37 54 30 38 3a 30 39 3a 31 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");
private static final byte[] CONTEXT_TIME_ZONE_ON = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 30 36 2d 30 37 54 30 37 3a 30 39 3a 31 30 2e 31 32 33 34 35 36 37 38 39 2d 30 38 3a 30 30");

    // Provenance: OffsetDateTimeSerTest#testSerializationAsTimestamp01Nanoseconds.
    void testSerializationAsTimestamp01NanosecondsVpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        assertArrayEquals(TIMESTAMP_01_NANOS, timestampMapper(true).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsTimestamp01Milliseconds.
    void testSerializationAsTimestamp01MillisecondsVpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        assertArrayEquals(TIMESTAMP_01_MILLIS, timestampMapper(false).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsTimestamp02Nanoseconds.
    void testSerializationAsTimestamp02NanosecondsVpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.ofInstant(FRACTIONAL_INSTANT, Z2);
        assertArrayEquals(TIMESTAMP_02_NANOS, timestampMapper(true).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsTimestamp02Milliseconds.
    void testSerializationAsTimestamp02MillisecondsVpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.ofInstant(FRACTIONAL_INSTANT, Z2);
        assertArrayEquals(TIMESTAMP_02_MILLIS, timestampMapper(false).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsTimestamp03Nanoseconds.
    void testSerializationAsTimestamp03NanosecondsVpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.ofInstant(DETERMINISTIC_INSTANT, Z3);
        assertArrayEquals(TIMESTAMP_03_NANOS, timestampMapper(true).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsTimestamp03Milliseconds.
    void testSerializationAsTimestamp03MillisecondsVpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.ofInstant(DETERMINISTIC_INSTANT, Z3);
        assertArrayEquals(TIMESTAMP_03_MILLIS, timestampMapper(false).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationWithJsonFormat.
    void testSerializationWithJsonFormatVpack() throws Exception {
        OffsetDateTime value = OffsetDateTime.parse("2022-04-27T12:00:00+02:00");
        assertArrayEquals(JSON_FORMAT_FIELD,
                VPackMapper.builder().build().writeValueAsBytes(new Wrapper(value)));

        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(OffsetDateTime.class,
                        cfg -> cfg.setFormat(JsonFormat.Value.forPattern("yyyy.MM.dd'x'HH:mm:ss")))
                .build();
        assertArrayEquals(JSON_FORMAT_OVERRIDE, mapper.writeValueAsBytes(value));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationWithTypeInfo01.
    void testSerializationWithTypeInfo01Vpack() throws Exception {
        assertArrayEquals(TYPE_INFO_NANOS,
                VPackMapper.builder()
                        .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                                DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                        .addMixIn(Temporal.class, TemporalTypeInfo.class)
                        .build()
                        .writeValueAsBytes(OffsetDateTime.ofInstant(FRACTIONAL_INSTANT, Z2)));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsStringWithMapperTimeZone01.
    void testSerializationAsStringWithMapperTimeZone01Vpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        assertArrayEquals(MAPPER_TIME_ZONE_01,
                stringMapper().writer().with(TimeZone.getTimeZone(Z1)).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsStringWithMapperTimeZone02.
    void testSerializationAsStringWithMapperTimeZone02Vpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.ofInstant(FRACTIONAL_INSTANT, Z2);
        assertArrayEquals(MAPPER_TIME_ZONE_02,
                stringMapper().writer().with(TimeZone.getTimeZone(Z2)).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsStringWithMapperTimeZone03.
    void testSerializationAsStringWithMapperTimeZone03Vpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.of(2020, 6, 7, 8, 9, 10, 123456789,
                Z3.getRules().getOffset(Instant.parse("2020-06-07T15:09:10Z")));
        assertArrayEquals(MAPPER_TIME_ZONE_03,
                stringMapper().writer().with(TimeZone.getTimeZone(Z3)).writeValueAsBytes(date));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOn.
    void testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnVpack() throws Exception {
        OffsetDateTime date = OffsetDateTime.of(2020, 6, 7, 8, 9, 10, 123456789,
                Z3.getRules().getOffset(Instant.parse("2020-06-07T15:09:10Z")));
        assertArrayEquals(CONTEXT_TIME_ZONE_ON,
                stringMapper().writer()
                        .with(TimeZone.getTimeZone(Z2))
                        .without(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .with(DateTimeFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                        .writeValueAsBytes(date));
    }
private static ObjectMapper timestampMapper(boolean nanos) {
        var builder = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (nanos) {
            builder.enable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        } else {
            builder.disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        }
        return builder.build();
    }
private static ObjectMapper stringMapper() {
        return VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
static class Wrapper {
        @JsonFormat(pattern = "yyyy_MM_dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
        public OffsetDateTime value;

        Wrapper(OffsetDateTime value) {
            this.value = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testSerializationAsTimestamp01NanosecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp01NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestamp01MillisecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp01MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestamp02NanosecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp02NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestamp02MillisecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp02MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestamp03NanosecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp03NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestamp03MillisecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp03MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithJsonFormatVpack() throws Exception {
        try {
            testSerializationWithJsonFormatVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithTypeInfo01Vpack() throws Exception {
        try {
            testSerializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithMapperTimeZone01Vpack() throws Exception {
        try {
            testSerializationAsStringWithMapperTimeZone01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithMapperTimeZone02Vpack() throws Exception {
        try {
            testSerializationAsStringWithMapperTimeZone02Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithMapperTimeZone03Vpack() throws Exception {
        try {
            testSerializationAsStringWithMapperTimeZone03Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnVpack() throws Exception {
        try {
            testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnVpack();
        } finally {
        }
    }

}
