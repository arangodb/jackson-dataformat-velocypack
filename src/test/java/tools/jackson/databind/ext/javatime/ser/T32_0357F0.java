package tools.jackson.databind.ext.javatime.ser;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0357F0 {
private static final ZoneId UTC = ZoneId.of("UTC");
private static final byte[] SCALAR_NANOS = VPackWireFixtureTest.hex(
            "0b 28 02 4b 6e 61 6e 6f 73 65 63 6f 6e 64 73 "
          + "c8 01 ff ff ff ff 00 "
          + "4e 6e 6f 74 4e 61 6e 6f 73 65 63 6f 6e 64 73 30 03 16");
private static final byte[] LOCAL_DATE_TIME_NANOS = VPackWireFixtureTest.hex(
            "0b 46 02 4b 6e 61 6e 6f 73 65 63 6f 6e 64 73 "
          + "06 13 07 29 b2 07 31 31 30 30 30 31 03 06 07 08 09 0a 0b "
          + "4e 6e 6f 74 4e 61 6e 6f 73 65 63 6f 6e 64 73 "
          + "06 13 07 29 b2 07 31 31 30 30 30 30 03 06 07 08 09 0a 0b "
          + "03 22");
private static final byte[] LOCAL_TIME_NANOS = VPackWireFixtureTest.hex(
            "0b 2c 02 4b 6e 61 6e 6f 73 65 63 6f 6e 64 73 "
          + "02 06 30 30 30 31 "
          + "4e 6e 6f 74 4e 61 6e 6f 73 65 63 6f 6e 64 73 "
          + "02 06 30 30 30 30 03 15");
private static final byte[] OFFSET_TIME_NANOS = VPackWireFixtureTest.hex(
            "0b 3c 02 4b 6e 61 6e 6f 73 65 63 6f 6e 64 73 "
          + "06 0e 05 30 30 30 31 41 5a 03 04 05 06 07 "
          + "4e 6e 6f 74 4e 61 6e 6f 73 65 63 6f 6e 64 73 "
          + "06 0e 05 30 30 30 30 41 5a 03 04 05 06 07 "
          + "03 1d");
private static final byte[] ZONE_ID_TYPE_INFO = VPackWireFixtureTest.hex(
            "06 25 02 50 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 49 64 "
          + "4e 41 6d 65 72 69 63 61 2f 44 65 6e 76 65 72 03 14");
private static final byte[] ANNOTATED_ZONE_ID = VPackWireFixtureTest.hex(
            "0b 35 01 44 64 61 74 65 "
          + "6b 30 31 2d 30 31 2d 31 39 37 30 54 30 37 3a 30 30 3a 30 30 20 2b 30 37 30 30 "
          + "5b 41 73 69 61 2f 4b 72 61 73 6e 6f 79 61 72 73 6b 5d 03");
private static final byte[] ZONED_DATE_TIME_MAP = VPackWireFixtureTest.hex(
            "0b 2e 01 68 32 30 30 37 2d 31 32 2d 30 33 54 31 30 3a 31 35 3a 33 30 "
          + "2b 30 31 3a 30 30 5b 45 75 72 6f 70 65 2f 57 61 72 73 61 77 5d 40 03");
private static final ObjectMapper TIMESTAMP_MAPPER = VPackMapper.builder()
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .build();

    // Provenance: WriteNanosecondsTest#testSerializeDurationWithAndWithoutNanoseconds.
    void testSerializeDurationWithAndWithoutNanosecondsVpack() throws Exception {
        assertArrayEquals(SCALAR_NANOS,
                TIMESTAMP_MAPPER.writer()
                        .with(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                        .writeValueAsBytes(new DummyClass<>(Duration.ZERO)));
    }

    // Provenance: WriteNanosecondsTest#testSerializeInstantWithAndWithoutNanoseconds.
    void testSerializeInstantWithAndWithoutNanosecondsVpack() throws Exception {
        assertArrayEquals(SCALAR_NANOS,
                TIMESTAMP_MAPPER.writeValueAsBytes(new DummyClass<>(Instant.EPOCH)));
    }

    // Provenance: WriteNanosecondsTest#testSerializeLocalDateTimeWithAndWithoutNanoseconds.
    void testSerializeLocalDateTimeWithAndWithoutNanosecondsVpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_NANOS,
                TIMESTAMP_MAPPER.writeValueAsBytes(new DummyClass<>(
                        LocalDateTime.of(1970, 1, 1, 0, 0, 0, 1))));
    }

    // Provenance: WriteNanosecondsTest#testSerializeLocalTimeWithAndWithoutNanoseconds.
    void testSerializeLocalTimeWithAndWithoutNanosecondsVpack() throws Exception {
        assertArrayEquals(LOCAL_TIME_NANOS,
                TIMESTAMP_MAPPER.writeValueAsBytes(new DummyClass<>(LocalTime.of(0, 0, 0, 1))));
    }

    // Provenance: WriteNanosecondsTest#testSerializeOffsetDateTimeWithAndWithoutNanoseconds.
    void testSerializeOffsetDateTimeWithAndWithoutNanosecondsVpack() throws Exception {
        assertArrayEquals(SCALAR_NANOS,
                TIMESTAMP_MAPPER.writeValueAsBytes(new DummyClass<>(
                        OffsetDateTime.ofInstant(Instant.EPOCH, UTC))));
    }

    // Provenance: WriteNanosecondsTest#testSerializeOffsetTimeWithAndWithoutNanoseconds.
    void testSerializeOffsetTimeWithAndWithoutNanosecondsVpack() throws Exception {
        assertArrayEquals(OFFSET_TIME_NANOS,
                TIMESTAMP_MAPPER.writeValueAsBytes(new DummyClass<>(
                        OffsetTime.of(0, 0, 0, 1, ZoneOffset.UTC))));
    }

    // Provenance: WriteNanosecondsTest#testSerializeZonedDateTimeWithAndWithoutNanoseconds.
    void testSerializeZonedDateTimeWithAndWithoutNanosecondsVpack() throws Exception {
        assertArrayEquals(SCALAR_NANOS,
                TIMESTAMP_MAPPER.writeValueAsBytes(new DummyClass<>(
                        ZonedDateTime.ofInstant(Instant.EPOCH, UTC))));
    }
static class DummyClass<T> {
        @JsonFormat(with = JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        private final T nanoseconds;

        @JsonFormat(without = JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        private final T notNanoseconds;

        DummyClass(T value) {
            nanoseconds = value;
            notNanoseconds = value;
        }
    }
static class DummyClassWithDate {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "dd-MM-yyyy'T'hh:mm:ss Z",
                with = JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID)
        public ZonedDateTime date;

        DummyClassWithDate(ZonedDateTime date) {
            this.date = date;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface ZoneIdTypeInfo { }

    void __invoke_testSerializeDurationWithAndWithoutNanosecondsVpack() throws Exception {
        try {
            testSerializeDurationWithAndWithoutNanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializeInstantWithAndWithoutNanosecondsVpack() throws Exception {
        try {
            testSerializeInstantWithAndWithoutNanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializeLocalDateTimeWithAndWithoutNanosecondsVpack() throws Exception {
        try {
            testSerializeLocalDateTimeWithAndWithoutNanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializeLocalTimeWithAndWithoutNanosecondsVpack() throws Exception {
        try {
            testSerializeLocalTimeWithAndWithoutNanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializeOffsetDateTimeWithAndWithoutNanosecondsVpack() throws Exception {
        try {
            testSerializeOffsetDateTimeWithAndWithoutNanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializeOffsetTimeWithAndWithoutNanosecondsVpack() throws Exception {
        try {
            testSerializeOffsetTimeWithAndWithoutNanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializeZonedDateTimeWithAndWithoutNanosecondsVpack() throws Exception {
        try {
            testSerializeZonedDateTimeWithAndWithoutNanosecondsVpack();
        } finally {
        }
    }

}
