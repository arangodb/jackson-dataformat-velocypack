package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0357F1 {
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

    // Provenance: WriteZoneIdTest#testSerialization01.
    void testZoneIdSerialization01Vpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "4f 41 6d 65 72 69 63 61 2f 43 68 69 63 61 67 6f"),
                TIMESTAMP_MAPPER.writeValueAsBytes(ZoneId.of("America/Chicago")));
    }

    // Provenance: WriteZoneIdTest#testSerialization02.
    void testZoneIdSerialization02Vpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "51 41 6d 65 72 69 63 61 2f 41 6e 63 68 6f 72 61 67 65"),
                TIMESTAMP_MAPPER.writeValueAsBytes(ZoneId.of("America/Anchorage")));
    }

    // Provenance: WriteZoneIdTest#testSerializationWithTypeInfo01.
    void testZoneIdSerializationWithTypeInfo01Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(ZoneId.class, ZoneIdTypeInfo.class)
                .build();
        assertArrayEquals(ZONE_ID_TYPE_INFO,
                mapper.writeValueAsBytes(ZoneId.of("America/Denver")));
    }

    // Provenance: WriteZoneIdTest#testJacksonAnnotatedPOJOWithDateWithTimezoneToJson.
    void testJacksonAnnotatedPOJOWithDateWithTimezoneToJsonVpack() throws Exception {
        assertArrayEquals(ANNOTATED_ZONE_ID,
                TIMESTAMP_MAPPER.writeValueAsBytes(new DummyClassWithDate(
                        ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L),
                                ZoneId.of("Asia/Krasnoyarsk")))));
    }

    // Provenance: WriteZoneIdTest#testMapSerialization.
    void testMapSerializationVpack() throws Exception {
        final ZonedDateTime datetime = ZonedDateTime.parse(
                "2007-12-03T10:15:30+01:00[Europe/Warsaw]");
        final HashMap<ZonedDateTime, String> map = new HashMap<>();
        map.put(datetime, "");
        assertArrayEquals(ZONED_DATE_TIME_MAP,
                TIMESTAMP_MAPPER.writer()
                        .with(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .writeValueAsBytes(map));
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

    void __invoke_testZoneIdSerialization01Vpack() throws Exception {
        try {
            testZoneIdSerialization01Vpack();
        } finally {
        }
    }


    void __invoke_testZoneIdSerialization02Vpack() throws Exception {
        try {
            testZoneIdSerialization02Vpack();
        } finally {
        }
    }


    void __invoke_testZoneIdSerializationWithTypeInfo01Vpack() throws Exception {
        try {
            testZoneIdSerializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testJacksonAnnotatedPOJOWithDateWithTimezoneToJsonVpack() throws Exception {
        try {
            testJacksonAnnotatedPOJOWithDateWithTimezoneToJsonVpack();
        } finally {
        }
    }


    void __invoke_testMapSerializationVpack() throws Exception {
        try {
            testMapSerializationVpack();
        } finally {
        }
    }

}
