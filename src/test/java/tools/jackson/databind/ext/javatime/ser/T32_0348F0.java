package tools.jackson.databind.ext.javatime.ser;

import java.time.LocalDate;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0348F0 {
private static final byte[] LOCAL_DATE_TIMESTAMP_1986 = VPackWireFixtureTest.hex(
            "06 0c 03 29 c2 07 31 28 11 03 06 07");
private static final byte[] LOCAL_DATE_TIMESTAMP_2013 = VPackWireFixtureTest.hex(
            "06 0c 03 29 dd 07 38 28 15 03 06 07");
private static final byte[] LOCAL_DATE_TYPED_STRING = VPackWireFixtureTest.hex(
            "06 24 02 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 "
          + "4a 32 30 30 35 2d 31 31 2d 30 35 03 17");
private static final byte[] LOCAL_DATE_TYPED_TIMESTAMP = VPackWireFixtureTest.hex(
            "0b 46 02 49 6c 6f 63 61 6c 44 61 74 65 "
          + "06 0c 03 29 e1 07 28 0c 35 03 06 08 "
          + "46 6f 62 6a 65 63 74 "
          + "0b 24 01 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 "
          + "06 0c 03 29 e1 07 28 0c 35 03 06 08 03 "
          + "03 19");
private static final byte[] LOCAL_DATE_TIME_STRING_01 = VPackWireFixtureTest.hex(
            "53 31 39 38 36 2d 30 31 2d 31 37 54 31 35 3a 34 33 3a 30 35");
private static final byte[] LOCAL_DATE_TIME_STRING_02 = VPackWireFixtureTest.hex(
            "53 32 30 31 33 2d 30 38 2d 32 31 54 30 39 3a 32 32 3a 35 37");
private static final byte[] LOCAL_DATE_TIME_STRING_03 = VPackWireFixtureTest.hex(
            "5d 32 30 30 35 2d 31 31 2d 30 35 54 32 32 3a 33 31 3a 30 35 2e "
          + "30 30 30 38 32 39 38 33 37");
private static final byte[] LOCAL_DATE_TIME_TIMESTAMP_01 = VPackWireFixtureTest.hex(
            "06 12 05 29 c2 07 31 28 11 28 0f 28 2b 03 06 07 09 0b");
private static final byte[] LOCAL_DATE_TIME_TIMESTAMP_02 = VPackWireFixtureTest.hex(
            "06 14 06 29 dd 07 38 28 15 39 28 16 28 39 03 06 07 09 0a 0c");
private static final byte[] LOCAL_DATE_TIME_TIMESTAMP_03_NANOS = VPackWireFixtureTest.hex(
            "06 16 07 29 dd 07 38 28 15 39 28 16 30 28 39 03 06 07 09 0a 0c 0d");
private static final byte[] LOCAL_DATE_TIME_TIMESTAMP_03_MILLIS = VPackWireFixtureTest.hex(
            "06 15 07 29 dd 07 38 28 15 39 28 16 30 30 03 06 07 09 0a 0c 0d");
private static final byte[] LOCAL_DATE_TIME_TIMESTAMP_04_MILLIS = VPackWireFixtureTest.hex(
            "06 18 07 29 d5 07 28 0b 35 28 16 28 1f 35 29 a6 01 "
          + "03 06 08 09 0b 0d 0e");

    // Provenance: LocalDateSerTest#testSerializationAsTimestamp01.
    void testLocalDateSerializationAsTimestamp01Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIMESTAMP_1986,
                timestampMapper().writeValueAsBytes(LocalDate.of(1986, 1, 17)));
    }

    // Provenance: LocalDateSerTest#testSerializationAsTimestamp02.
    void testLocalDateSerializationAsTimestamp02Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIMESTAMP_2013,
                timestampMapper().writeValueAsBytes(LocalDate.of(2013, 8, 21)));
    }

    // Provenance: LocalDateSerTest#testSerializationWithTypeInfo01.
    void testLocalDateSerializationWithTypeInfo01Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TYPED_STRING,
                typedLocalDateMapper(false).writeValueAsBytes(LocalDate.of(2005, 11, 5)));
    }

    // Provenance: LocalDateSerTest#testSerializationWithTypeInfo02.
    void testLocalDateSerializationWithTypeInfo02Vpack() throws Exception {
        LocalDate date = LocalDate.of(2017, 12, 5);
        assertArrayEquals(LOCAL_DATE_TYPED_TIMESTAMP,
                timestampMapper().writeValueAsBytes(new Holder46(date, date)));
    }
private static ObjectMapper timestampMapper() {
        return timestampMapper(true);
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
private static ObjectMapper typedLocalDateMapper(boolean timestamps) {
        var builder = VPackMapper.builder()
                .addMixIn(Temporal.class, LocalDateTypeInfo.class);
        if (timestamps) {
            builder.enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        } else {
            builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        return builder.build();
    }
static class Holder46 {
        public LocalDate localDate;

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
        public Object object;

        public Holder46() { }

        Holder46(LocalDate localDate, Object object) {
            this.localDate = localDate;
            this.object = object;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface LocalDateTypeInfo { }

    void __invoke_testLocalDateSerializationAsTimestamp01Vpack() throws Exception {
        try {
            testLocalDateSerializationAsTimestamp01Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateSerializationAsTimestamp02Vpack() throws Exception {
        try {
            testLocalDateSerializationAsTimestamp02Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateSerializationWithTypeInfo01Vpack() throws Exception {
        try {
            testLocalDateSerializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateSerializationWithTypeInfo02Vpack() throws Exception {
        try {
            testLocalDateSerializationWithTypeInfo02Vpack();
        } finally {
        }
    }

}
