package tools.jackson.databind.ext.javatime.ser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0349F1 {
private static final byte[] LOCAL_DATE_TIME_TIMESTAMP_04_NANOS = VPackWireFixtureTest.hex(
            "06 19 07 29 d5 07 28 0b 35 28 16 28 1f 35 2a 8d a9 0c "
          + "03 06 08 09 0b 0d 0e");
private static final byte[] LOCAL_DATE_TIME_FORMAT_FIELD = VPackWireFixtureTest.hex(
            "0b 1e 01 45 76 61 6c 75 65 53 32 30 30 35 2d 31 31 2d 30 35 "
          + "41 32 32 3a 33 31 3a 30 35 03");
private static final byte[] LOCAL_DATE_TIME_FORMAT_OVERRIDE = VPackWireFixtureTest.hex(
            "50 32 30 30 35 2d 31 31 2d 30 35 58 32 32 3a 33 31");
private static final byte[] LOCAL_DATE_TIME_TYPE_INFO_NANOS = VPackWireFixtureTest.hex(
            "06 36 02 57 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 54 69 6d 65 "
          + "06 19 07 29 d5 07 28 0b 35 28 16 28 1f 35 2a 8d a9 0c 03 06 08 09 0b 0d 0e "
          + "03 1b");
private static final byte[] LOCAL_DATE_TIME_TYPE_INFO_MILLIS = VPackWireFixtureTest.hex(
            "02 32 57 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 54 69 6d 65 "
          + "06 18 07 29 d5 07 28 0b 35 28 16 28 1f 35 29 a6 01 03 06 08 09 0b 0d 0e");
private static final byte[] LOCAL_DATE_TIME_TYPE_INFO_STRING = VPackWireFixtureTest.hex(
            "06 3b 02 57 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 54 69 6d 65 "
          + "5d 32 30 30 35 2d 31 31 2d 30 35 54 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37 "
          + "03 1b");
private static final byte[] LOCAL_TIME_STRING_01 = VPackWireFixtureTest.hex(
            "48 31 35 3a 34 33 3a 32 30");
private static final byte[] LOCAL_TIME_STRING_02 = VPackWireFixtureTest.hex(
            "48 30 39 3a 32 32 3a 35 37");
private static final byte[] LOCAL_TIME_STRING_03 = VPackWireFixtureTest.hex(
            "52 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37");
private static final byte[] LOCAL_TIME_TIMESTAMP_01 = VPackWireFixtureTest.hex(
            "02 06 28 0f 28 2b");
private static final byte[] LOCAL_TIME_TIMESTAMP_02 = VPackWireFixtureTest.hex(
            "06 0b 03 39 28 16 28 39 03 04 06");
private static final byte[] LOCAL_TIME_TIMESTAMP_03_MILLIS = VPackWireFixtureTest.hex(
            "06 0c 04 39 28 16 30 30 03 04 06 07");
private static final byte[] LOCAL_TIME_TIMESTAMP_03_NANOS = VPackWireFixtureTest.hex(
            "06 0d 04 39 28 16 30 28 39 03 04 06 07");

    // Provenance: LocalTimeSerTest#testSerializationAsString01.
    void testLocalTimeSerializationAsString01Vpack() throws Exception {
        assertArrayEquals(LOCAL_TIME_STRING_01,
                stringMapper().writeValueAsBytes(LocalTime.of(15, 43, 20)));
    }

    // Provenance: LocalTimeSerTest#testSerializationAsString02.
    void testLocalTimeSerializationAsString02Vpack() throws Exception {
        assertArrayEquals(LOCAL_TIME_STRING_02,
                stringMapper().writeValueAsBytes(LocalTime.of(9, 22, 57)));
    }

    // Provenance: LocalTimeSerTest#testSerializationAsString03.
    void testLocalTimeSerializationAsString03Vpack() throws Exception {
        assertArrayEquals(LOCAL_TIME_STRING_03,
                stringMapper().writeValueAsBytes(LocalTime.of(22, 31, 5, 829837)));
    }

    // Provenance: LocalTimeSerTest#testSerializationAsTimestamp01.
    void testLocalTimeSerializationAsTimestamp01Vpack() throws Exception {
        assertArrayEquals(LOCAL_TIME_TIMESTAMP_01,
                timestampMapper(true).writeValueAsBytes(LocalTime.of(15, 43)));
    }

    // Provenance: LocalTimeSerTest#testSerializationAsTimestamp02.
    void testLocalTimeSerializationAsTimestamp02Vpack() throws Exception {
        assertArrayEquals(LOCAL_TIME_TIMESTAMP_02,
                timestampMapper(true).writeValueAsBytes(LocalTime.of(9, 22, 57)));
    }

    // Provenance: LocalTimeSerTest#testSerializationAsTimestamp03Milliseconds.
    void testLocalTimeSerializationAsTimestamp03MillisecondsVpack() throws Exception {
        assertArrayEquals(LOCAL_TIME_TIMESTAMP_03_MILLIS,
                timestampMapper(false).writeValueAsBytes(LocalTime.of(9, 22, 0, 57)));
    }

    // Provenance: LocalTimeSerTest#testSerializationAsTimestamp03Nanoseconds.
    void testLocalTimeSerializationAsTimestamp03NanosecondsVpack() throws Exception {
        assertArrayEquals(LOCAL_TIME_TIMESTAMP_03_NANOS,
                timestampMapper(true).writeValueAsBytes(LocalTime.of(9, 22, 0, 57)));
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
private static ObjectMapper typeInfoMapper(boolean timestamps, boolean nanos) {
        var builder = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, timestamps)
                .configure(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, nanos);
        return builder.build();
    }
static class LDTWrapper {
        @JsonFormat(pattern = "yyyy-MM-dd'A'HH:mm:ss")
        public LocalDateTime value;

        LDTWrapper(LocalDateTime value) {
            this.value = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testLocalTimeSerializationAsString01Vpack() throws Exception {
        try {
            testLocalTimeSerializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeSerializationAsString02Vpack() throws Exception {
        try {
            testLocalTimeSerializationAsString02Vpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeSerializationAsString03Vpack() throws Exception {
        try {
            testLocalTimeSerializationAsString03Vpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeSerializationAsTimestamp01Vpack() throws Exception {
        try {
            testLocalTimeSerializationAsTimestamp01Vpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeSerializationAsTimestamp02Vpack() throws Exception {
        try {
            testLocalTimeSerializationAsTimestamp02Vpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeSerializationAsTimestamp03MillisecondsVpack() throws Exception {
        try {
            testLocalTimeSerializationAsTimestamp03MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeSerializationAsTimestamp03NanosecondsVpack() throws Exception {
        try {
            testLocalTimeSerializationAsTimestamp03NanosecondsVpack();
        } finally {
        }
    }

}
