package tools.jackson.databind.ext.javatime.deser;

import java.time.OffsetTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0330Fixture {
private static final byte[] TIMESTAMP_01 = VPackWireFixtureTest.hex(
            "13 0d 28 0f 28 2b 45 2b 30 33 30 30 03");
private static final byte[] TIMESTAMP_02 = VPackWireFixtureTest.hex(
            "13 0f 39 28 16 28 39 46 2d 30 36 3a 33 30 04");
private static final byte[] TIMESTAMP_03 = VPackWireFixtureTest.hex(
            "13 10 39 28 16 30 28 39 46 2d 30 36 3a 33 30 05");
private static final byte[] TIMESTAMP_04_NANOS = VPackWireFixtureTest.hex(
            "13 13 28 16 28 1f 35 2a 8d a9 0c 46 2b 31 31 3a 30 30 05");
private static final byte[] TIMESTAMP_04_MILLIS_02 = VPackWireFixtureTest.hex(
            "13 12 28 16 28 1f 35 29 3d 03 46 2b 31 31 3a 30 30 05");
private static final byte[] TIMESTAMP_05 = VPackWireFixtureTest.hex(
            "14 19 45 76 61 6c 75 65 13 10 39 28 16 30 28 39 46 2d 30 36 3a 33 30 05 01");
private static final byte[] TIMESTAMP_05_MILLIS_02 = VPackWireFixtureTest.hex(
            "14 1a 45 76 61 6c 75 65 13 11 39 28 16 30 29 a1 10 46 2d 30 36 3a 33 30 05 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");

    // Provenance: OffsetTimeDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        assertNull(new VPackMapper().readerFor(OffsetTime.class).readValue(EMPTY_ARRAY));
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetTime.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        assertNull(reader.readValue(EMPTY_ARRAY));
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp01.
    void testDeserializationAsTimestamp01Vpack() throws Exception {
        OffsetTime expected = OffsetTime.of(15, 43, 0, 0, ZoneOffset.of("+0300"));
        OffsetTime actual = new VPackMapper().readerFor(OffsetTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_01);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp02.
    void testDeserializationAsTimestamp02Vpack() throws Exception {
        OffsetTime expected = OffsetTime.of(9, 22, 57, 0, ZoneOffset.of("-06:30"));
        OffsetTime actual = new VPackMapper().readerFor(OffsetTime.class)
                .readValue(TIMESTAMP_02);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp03Milliseconds.
    void testDeserializationAsTimestamp03MillisecondsVpack() throws Exception {
        OffsetTime expected = OffsetTime.of(9, 22, 0, 57_000_000, ZoneOffset.of("-06:30"));
        OffsetTime actual = new VPackMapper().readerFor(OffsetTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_03);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp03Nanoseconds.
    void testDeserializationAsTimestamp03NanosecondsVpack() throws Exception {
        OffsetTime expected = OffsetTime.of(9, 22, 0, 57, ZoneOffset.of("-06:30"));
        OffsetTime actual = new VPackMapper().readerFor(OffsetTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_03);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp04Milliseconds01.
    void testDeserializationAsTimestamp04Milliseconds01Vpack() throws Exception {
        OffsetTime expected = OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+11:00"));
        OffsetTime actual = new VPackMapper().readerFor(OffsetTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_04_NANOS);
        assertEquals(expected, actual);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp04Milliseconds02.
    void testDeserializationAsTimestamp04Milliseconds02Vpack() throws Exception {
        OffsetTime expected = OffsetTime.of(22, 31, 5, 829_000_000, ZoneOffset.of("+11:00"));
        OffsetTime actual = new VPackMapper().readerFor(OffsetTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_04_MILLIS_02);
        assertEquals(expected, actual);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp04Nanoseconds.
    void testDeserializationAsTimestamp04NanosecondsVpack() throws Exception {
        OffsetTime expected = OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+11:00"));
        OffsetTime actual = new VPackMapper().readerFor(OffsetTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_04_NANOS);
        assertEquals(expected, actual);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp05Milliseconds01.
    void testDeserializationAsTimestamp05Milliseconds01Vpack() throws Exception {
        WrapperWithReadTimestampsAsNanosDisabled actual = new VPackMapper()
                .readerFor(WrapperWithReadTimestampsAsNanosDisabled.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_05);
        assertNotNull(actual);
        assertEquals(OffsetTime.of(9, 22, 0, 57_000_000, ZoneOffset.of("-06:30")), actual.value);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp05Milliseconds02.
    void testDeserializationAsTimestamp05Milliseconds02Vpack() throws Exception {
        WrapperWithReadTimestampsAsNanosDisabled actual = new VPackMapper()
                .readerFor(WrapperWithReadTimestampsAsNanosDisabled.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_05_MILLIS_02);
        assertNotNull(actual);
        assertEquals(OffsetTime.of(9, 22, 0, 4_257, ZoneOffset.of("-06:30")), actual.value);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsTimestamp05Nanoseconds.
    void testDeserializationAsTimestamp05NanosecondsVpack() throws Exception {
        WrapperWithReadTimestampsAsNanosEnabled actual = new VPackMapper()
                .readerFor(WrapperWithReadTimestampsAsNanosEnabled.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_05);
        assertNotNull(actual);
        assertEquals(OffsetTime.of(9, 22, 0, 57, ZoneOffset.of("-06:30")), actual.value);
    }
static class WrapperWithReadTimestampsAsNanosDisabled {
        @JsonFormat(without = Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public OffsetTime value;
    }
static class WrapperWithReadTimestampsAsNanosEnabled {
        @JsonFormat(with = Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public OffsetTime value;
    }

    void __invoke_testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp01Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp02Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp03MillisecondsVpack() throws Exception {
        try {
            testDeserializationAsTimestamp03MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp03NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsTimestamp03NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp04Milliseconds01Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp04Milliseconds01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp04Milliseconds02Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp04Milliseconds02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp04NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsTimestamp04NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp05Milliseconds01Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp05Milliseconds01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp05Milliseconds02Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp05Milliseconds02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp05NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsTimestamp05NanosecondsVpack();
        } finally {
        }
    }

}
