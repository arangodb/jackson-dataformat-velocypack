package tools.jackson.databind.ext.javatime.ser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0348F1 {
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

    // Provenance: LocalDateTimeSerTest#testSerializationAsString01.
    void testLocalDateTimeSerializationAsString01Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_STRING_01,
                stringMapper().writeValueAsBytes(LocalDateTime.of(1986, 1, 17, 15, 43, 5)));
    }

    // Provenance: LocalDateTimeSerTest#testSerializationAsString02.
    void testLocalDateTimeSerializationAsString02Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_STRING_02,
                stringMapper().writeValueAsBytes(LocalDateTime.of(2013, 8, 21, 9, 22, 57)));
    }

    // Provenance: LocalDateTimeSerTest#testSerializationAsString03.
    void testLocalDateTimeSerializationAsString03Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_STRING_03,
                stringMapper().writeValueAsBytes(
                        LocalDateTime.of(2005, 11, 5, 22, 31, 5, 829837)));
    }

    // Provenance: LocalDateTimeSerTest#testSerializationAsTimestamp01.
    void testLocalDateTimeSerializationAsTimestamp01Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_TIMESTAMP_01,
                timestampMapper().writeValueAsBytes(LocalDateTime.of(1986, 1, 17, 15, 43)));
    }

    // Provenance: LocalDateTimeSerTest#testSerializationAsTimestamp02.
    void testLocalDateTimeSerializationAsTimestamp02Vpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_TIMESTAMP_02,
                timestampMapper().writeValueAsBytes(LocalDateTime.of(2013, 8, 21, 9, 22, 57)));
    }

    // Provenance: LocalDateTimeSerTest#testSerializationAsTimestamp03Millisecond.
    void testLocalDateTimeSerializationAsTimestamp03MillisecondVpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_TIMESTAMP_03_MILLIS,
                timestampMapper(false)
                        .writeValueAsBytes(LocalDateTime.of(2013, 8, 21, 9, 22, 0, 57)));
    }

    // Provenance: LocalDateTimeSerTest#testSerializationAsTimestamp03Nanosecond.
    void testLocalDateTimeSerializationAsTimestamp03NanosecondVpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_TIMESTAMP_03_NANOS,
                timestampMapper(true)
                        .writeValueAsBytes(LocalDateTime.of(2013, 8, 21, 9, 22, 0, 57)));
    }

    // Provenance: LocalDateTimeSerTest#testSerializationAsTimestamp04Millisecond.
    void testLocalDateTimeSerializationAsTimestamp04MillisecondVpack() throws Exception {
        assertArrayEquals(LOCAL_DATE_TIME_TIMESTAMP_04_MILLIS,
                timestampMapper(false)
                        .writeValueAsBytes(
                                LocalDateTime.of(2005, 11, 5, 22, 31, 5, 422829837)));
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

    void __invoke_testLocalDateTimeSerializationAsString01Vpack() throws Exception {
        try {
            testLocalDateTimeSerializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeSerializationAsString02Vpack() throws Exception {
        try {
            testLocalDateTimeSerializationAsString02Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeSerializationAsString03Vpack() throws Exception {
        try {
            testLocalDateTimeSerializationAsString03Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeSerializationAsTimestamp01Vpack() throws Exception {
        try {
            testLocalDateTimeSerializationAsTimestamp01Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeSerializationAsTimestamp02Vpack() throws Exception {
        try {
            testLocalDateTimeSerializationAsTimestamp02Vpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeSerializationAsTimestamp03MillisecondVpack() throws Exception {
        try {
            testLocalDateTimeSerializationAsTimestamp03MillisecondVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeSerializationAsTimestamp03NanosecondVpack() throws Exception {
        try {
            testLocalDateTimeSerializationAsTimestamp03NanosecondVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeSerializationAsTimestamp04MillisecondVpack() throws Exception {
        try {
            testLocalDateTimeSerializationAsTimestamp04MillisecondVpack();
        } finally {
        }
    }

}
