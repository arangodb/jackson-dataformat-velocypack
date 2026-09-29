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

class T32_0353F0 {
private static final ZoneId Z2 = ZoneId.of("America/Anchorage");
private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");
private static final Instant FRACTIONAL_INSTANT =
            Instant.ofEpochSecond(123456789L, 183917322L);
private static final OffsetDateTime DETERMINISTIC_DATE = OffsetDateTime.of(
            2020, 6, 7, 8, 9, 10, 123456789,
            Z3.getRules().getOffset(Instant.parse("2020-06-07T15:09:10Z")));
private static final byte[] OFFSET_DATE_TIME_TYPE_INFO_MILLIS = VPackWireFixtureTest.hex(
            "06 24 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "2c bf 1a 99 be 1c 03 1c");
private static final byte[] OFFSET_DATE_TIME_TYPE_INFO_STRING = VPackWireFixtureTest.hex(
            "06 42 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "63 32 30 32 30 2d 30 36 2d 30 37 54 30 38 3a 30 39 3a 31 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30 "
          + "03 1c");
private static final byte[] OFFSET_DATE_TIME_SHAPE_INT = VPackWireFixtureTest.hex(
            "0b 22 02 42 74 31 2d 00 9d 76 6a 80 01 42 74 32 "
          + "c8 0a f7 ff ff ff 01 65 10 53 60 00 00 00 00 00 03 0d");
private static final byte[] OFFSET_TIME_TIMESTAMP_01 = VPackWireFixtureTest.hex(
            "06 11 03 28 0f 28 2b 46 2b 30 33 3a 30 30 03 05 07");
private static final byte[] OFFSET_TIME_TIMESTAMP_02 = VPackWireFixtureTest.hex(
            "06 13 04 39 28 16 28 39 46 2d 30 36 3a 33 30 03 04 06 08");
private static final byte[] OFFSET_TIME_TIMESTAMP_03_NANOS = VPackWireFixtureTest.hex(
            "06 15 05 39 28 16 30 28 39 46 2d 30 36 3a 33 30 03 04 06 07 09");
private static final byte[] OFFSET_TIME_TIMESTAMP_03_MILLIS = VPackWireFixtureTest.hex(
            "06 14 05 39 28 16 30 30 46 2d 30 36 3a 33 30 03 04 06 07 08");
private static final byte[] OFFSET_TIME_TIMESTAMP_04_MILLIS = VPackWireFixtureTest.hex(
            "06 17 05 28 16 28 1f 35 29 a6 01 46 2b 31 31 3a 30 30 03 05 07 08 0b");
private static final byte[] OFFSET_TIME_STRING_01 = VPackWireFixtureTest.hex(
            "4b 31 35 3a 34 33 2b 30 33 3a 30 30");
private static final byte[] OFFSET_TIME_STRING_02 = VPackWireFixtureTest.hex(
            "4e 30 39 3a 32 32 3a 35 37 2d 30 36 3a 33 30");
private static final byte[] OFFSET_TIME_STRING_03 = VPackWireFixtureTest.hex(
            "58 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37 2b 31 31 3a 30 30");

    // Provenance: OffsetDateTimeSerTest#testSerializationWithTypeInfo02.
    void testOffsetDateTimeSerializationWithTypeInfo02Vpack() throws Exception {
        assertArrayEquals(OFFSET_DATE_TIME_TYPE_INFO_MILLIS,
                VPackMapper.builder()
                        .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                        .addMixIn(Temporal.class, TemporalTypeInfo.class)
                        .build()
                        .writeValueAsBytes(OffsetDateTime.ofInstant(FRACTIONAL_INSTANT, Z2)));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationWithTypeInfo03.
    void testOffsetDateTimeSerializationWithTypeInfo03Vpack() throws Exception {
        assertArrayEquals(OFFSET_DATE_TIME_TYPE_INFO_STRING,
                VPackMapper.builder()
                        .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .addMixIn(Temporal.class, TemporalTypeInfo.class)
                        .build()
                        .writeValueAsBytes(DETERMINISTIC_DATE));
    }

    // Provenance: OffsetDateTimeSerTest#testSerializationWithTypeInfoAndMapperTimeZone.
    void testOffsetDateTimeSerializationWithTypeInfoAndMapperTimeZoneVpack() throws Exception {
        assertArrayEquals(OFFSET_DATE_TIME_TYPE_INFO_STRING,
                VPackMapper.builder()
                        .addMixIn(Temporal.class, TemporalTypeInfo.class)
                        .build()
                        .writer()
                        .with(TimeZone.getTimeZone(Z3))
                        .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .writeValueAsBytes(DETERMINISTIC_DATE));
    }

    // Provenance: OffsetDateTimeSerTest#testShapeInt.
    void testOffsetDateTimeShapeIntVpack() throws Exception {
        assertArrayEquals(OFFSET_DATE_TIME_SHAPE_INT,
                VPackMapper.builder()
                        .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .build()
                        .writeValueAsBytes(new ShapeIntPojo()));
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
static class ShapeIntPojo {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public OffsetDateTime t1 = OffsetDateTime.parse("2022-04-27T12:00:00+02:00");
        public OffsetDateTime t2 = t1;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testOffsetDateTimeSerializationWithTypeInfo02Vpack() throws Exception {
        try {
            testOffsetDateTimeSerializationWithTypeInfo02Vpack();
        } finally {
        }
    }


    void __invoke_testOffsetDateTimeSerializationWithTypeInfo03Vpack() throws Exception {
        try {
            testOffsetDateTimeSerializationWithTypeInfo03Vpack();
        } finally {
        }
    }


    void __invoke_testOffsetDateTimeSerializationWithTypeInfoAndMapperTimeZoneVpack() throws Exception {
        try {
            testOffsetDateTimeSerializationWithTypeInfoAndMapperTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testOffsetDateTimeShapeIntVpack() throws Exception {
        try {
            testOffsetDateTimeShapeIntVpack();
        } finally {
        }
    }

}
