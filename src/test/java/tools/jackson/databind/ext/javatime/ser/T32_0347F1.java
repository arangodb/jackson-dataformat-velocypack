package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0347F1 {
private static final Instant FRACTIONAL_INSTANT =
            Instant.ofEpochSecond(123456789L, 183917322L);
private static final Instant DETERMINISTIC_INSTANT =
            Instant.parse("2023-01-15T10:30:45.123456789Z");
private static final byte[] INSTANT_TIMESTAMP_NANOS = VPackWireFixtureTest.hex(
            "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22");
private static final byte[] INSTANT_TIMESTAMP_MILLIS = VPackWireFixtureTest.hex(
            "2d 83 48 fb b4 85 01");
private static final byte[] INSTANT_TIMESTAMP_NANOS_NOW = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 01 67 37 78 64 51 23 45 67 89");
private static final byte[] INSTANT_TYPED_NANOS = VPackWireFixtureTest.hex(
            "06 26 02 51 6a 61 76 61 2e 74 69 6d 65 2e 49 6e 73 74 61 6e 74 "
          + "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22 03 15");
private static final byte[] INSTANT_TYPED_MILLIS = VPackWireFixtureTest.hex(
            "06 1d 02 51 6a 61 76 61 2e 74 69 6d 65 2e 49 6e 73 74 61 6e 74 "
          + "2c bf 1a 99 be 1c 03 15");
private static final byte[] INSTANT_TYPED_STRING = VPackWireFixtureTest.hex(
            "06 36 02 51 6a 61 76 61 2e 74 69 6d 65 2e 49 6e 73 74 61 6e 74 "
          + "5e 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 2e "
          + "31 32 33 34 35 36 37 38 39 5a 03 15");
private static final byte[] INSTANT_SHAPE_INT = VPackWireFixtureTest.hex(
            "0b 22 02 42 74 31 2d 00 7a e4 6a 80 01 42 74 32 "
          + "c8 0a f7 ff ff ff 01 65 10 60 80 00 00 00 00 00 03 0d");
private static final byte[] LOCAL_DATE_1986 = VPackWireFixtureTest.hex(
            "4a 31 39 38 36 2d 30 31 2d 31 37");
private static final byte[] LOCAL_DATE_2013 = VPackWireFixtureTest.hex(
            "4a 32 30 31 33 2d 30 38 2d 32 31");
private static final byte[] LOCAL_DATE_PATTERN_OBJECT = VPackWireFixtureTest.hex(
            "0b 15 01 45 76 61 6c 75 65 4a 32 30 30 35 5f 31 31 5f 30 35 03");
private static final byte[] LOCAL_DATE_PATTERN = VPackWireFixtureTest.hex(
            "4a 32 30 30 35 5f 31 31 5f 30 35");
private static final byte[] LOCAL_DATE_EPOCH_OBJECT = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 29 e8 03 03");
private static final byte[] LOCAL_DATE_EPOCH = VPackWireFixtureTest.hex(
            "29 e8 03");

    // Provenance: LocalDateSerTest#testConfigOverrides.
    void testLocalDateConfigOverridesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class,
                        o -> o.setFormat(JsonFormat.Value.forPattern("yyyy_MM_dd")))
                .build();
        LocalDate date = LocalDate.of(2005, 11, 5);

        assertArrayEquals(LOCAL_DATE_PATTERN_OBJECT,
                mapper.writeValueAsBytes(new LocalDateWrapper(date)));
        assertArrayEquals(LOCAL_DATE_PATTERN, mapper.writeValueAsBytes(date));
        assertEquals(date, mapper.readValue(LOCAL_DATE_PATTERN_OBJECT,
                LocalDateWrapper.class).value);
        assertEquals(date, mapper.readValue(LOCAL_DATE_PATTERN, LocalDate.class));
    }

    // Provenance: LocalDateSerTest#testConfigOverridesToEpochDay.
    void testLocalDateConfigOverridesToEpochDayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class,
                        o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER_INT)))
                .build();
        LocalDate date = LocalDate.ofEpochDay(1000);

        assertArrayEquals(LOCAL_DATE_EPOCH_OBJECT,
                mapper.writeValueAsBytes(new LocalDateWrapper(date)));
        assertArrayEquals(LOCAL_DATE_EPOCH, mapper.writeValueAsBytes(date));
        assertEquals(date, mapper.readValue(LOCAL_DATE_EPOCH_OBJECT,
                LocalDateWrapper.class).value);
        assertEquals(date, mapper.readValue(LOCAL_DATE_EPOCH, LocalDate.class));
    }

    // Provenance: LocalDateSerTest#testCustomFormatToEpochDay.
    void testLocalDateCustomFormatToEpochDayVpack() throws Exception {
        LocalDateWrapper value = new VPackMapper().readValue(
                LOCAL_DATE_EPOCH_OBJECT, LocalDateWrapper.class);
        assertEquals(LocalDate.ofEpochDay(1000), value.value);
    }

    // Provenance: LocalDateSerTest#testSerializationAsString01.
    void testLocalDateSerializationAsString01Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_1986,
                new VPackMapper().writeValueAsBytes(LocalDate.of(1986, 1, 17)));
    }

    // Provenance: LocalDateSerTest#testSerializationAsString02.
    void testLocalDateSerializationAsString02Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_2013,
                new VPackMapper().writeValueAsBytes(LocalDate.of(2013, 8, 21)));
    }
private static ObjectMapper instantNanosecondsMapper() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
private static ObjectMapper instantMillisecondsMapper() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
private static ObjectMapper typedInstantMapper(boolean asTimestamp, boolean nanos) {
        var builder = VPackMapper.builder()
                .addMixIn(Temporal.class, InstantTypeInfo.class);
        if (asTimestamp) {
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
static class InstantShapePojo {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Instant t1 = Instant.parse("2022-04-27T12:00:00Z");
        public Instant t2 = t1;
    }
static class LocalDateWrapper {
        public LocalDate value;

        public LocalDateWrapper() { }

        LocalDateWrapper(LocalDate value) {
            this.value = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface InstantTypeInfo { }

    void __invoke_testLocalDateConfigOverridesVpack() throws Exception {
        try {
            testLocalDateConfigOverridesVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateConfigOverridesToEpochDayVpack() throws Exception {
        try {
            testLocalDateConfigOverridesToEpochDayVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateCustomFormatToEpochDayVpack() throws Exception {
        try {
            testLocalDateCustomFormatToEpochDayVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateSerializationAsString01Vpack() throws Exception {
        try {
            testLocalDateSerializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateSerializationAsString02Vpack() throws Exception {
        try {
            testLocalDateSerializationAsString02Vpack();
        } finally {
        }
    }

}
