package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0365F1 {
private static final ZoneId Z2 = ZoneId.of("America/Anchorage");
private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");
private static final Instant FRACTIONAL_INSTANT =
            Instant.ofEpochSecond(123456789L, 183917322L);
private static final Instant DETERMINISTIC_INSTANT =
            Instant.parse("2023-01-15T10:30:45.123456789Z");
private static final ZonedDateTime DATE_02 = ZonedDateTime.ofInstant(
            FRACTIONAL_INSTANT, Z2);
private static final ZonedDateTime DATE_03 = ZonedDateTime.ofInstant(
            DETERMINISTIC_INSTANT, Z3);
private static final byte[] TIMESTAMP_02_NANOS = VPackWireFixtureTest.hex(
            "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22");
private static final byte[] TIMESTAMP_02_MILLIS = VPackWireFixtureTest.hex(
            "2c bf 1a 99 be 1c");
private static final byte[] TIMESTAMP_03_NANOS = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 01 67 37 78 64 51 23 45 67 89");
private static final byte[] TIMESTAMP_03_MILLIS = VPackWireFixtureTest.hex(
            "2d 83 48 fb b4 85 01");
private static final byte[] TYPE_INFO_01 = VPackWireFixtureTest.hex(
            "06 2c 02 57 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 64 44 61 74 65 54 69 6d 65 "
          + "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22 03 1b");
private static final byte[] TYPE_INFO_02 = VPackWireFixtureTest.hex(
            "06 23 02 57 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 64 44 61 74 65 54 69 6d 65 "
          + "2c bf 1a 99 be 1c 03 1b");
private static final byte[] TYPE_INFO_03 = VPackWireFixtureTest.hex(
            "06 41 02 57 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 64 44 61 74 65 54 69 6d 65 "
          + "63 32 30 32 33 2d 30 31 2d 31 35 54 30 32 3a 33 30 3a 34 35 2e 31 32 33 34 35 36 37 38 39 2d 30 38 3a 30 30 "
          + "03 1b");
private static final byte[] JSON_FORMAT_OVERRIDE = VPackWireFixtureTest.hex(
            "0b 22 01 45 76 61 6c 75 65 57 32 30 32 34 2d 31 31 2d 31 35 20 31 38 3a 32 37 3a 30 36 20 43 45 54 03");
private static final byte[] SHAPE_INT = VPackWireFixtureTest.hex(
            "0b 22 02 42 74 31 2d 00 9d 76 6a 80 01 42 74 32 "
          + "c8 0a f7 ff ff ff 01 65 10 53 60 00 00 00 00 00 03 0d");

    // Provenance: ZonedDateTimeSerWithJsonFormat333Test#testJsonFormatOverridesSerialization.
    void testJsonFormatOverridesSerializationVpack() throws Exception {
        ContainerWithPattern333 input = new ContainerWithPattern333();
        input.value = ZonedDateTime.parse("2024-11-15T18:27:06.921054+01:00[Europe/Berlin]");

        assertArrayEquals(JSON_FORMAT_OVERRIDE,
                VPackMapper.builder()
                        .enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .build()
                        .writeValueAsBytes(input));
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
private static ObjectMapper typeInfoMapper(boolean timestamps, boolean nanos) {
        var builder = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class);
        if (timestamps) {
            builder.enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        } else {
            builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        if (nanos) {
            builder.enable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        } else {
            builder.disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        }
        return builder.build();
    }
static class ContainerWithPattern333 {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
        public ZonedDateTime value;
    }
static class ShapeIntPojo {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public ZonedDateTime t1 = ZonedDateTime.parse("2022-04-27T12:00:00+02:00[Europe/Paris]");
        public ZonedDateTime t2 = t1;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testJsonFormatOverridesSerializationVpack() throws Exception {
        try {
            testJsonFormatOverridesSerializationVpack();
        } finally {
        }
    }

}
