package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0319F1 {
private static final byte[] TIME_TIMESTAMP_01 = VPackWireFixtureTest.hex(
            "13 07 28 0f 28 2b 02");
private static final byte[] TIME_TIMESTAMP_02 = VPackWireFixtureTest.hex(
            "13 08 39 28 16 28 39 03");
private static final byte[] TIME_TIMESTAMP_03 = VPackWireFixtureTest.hex(
            "13 09 39 28 16 30 28 39 04");
private static final byte[] TIME_TIMESTAMP_04_NANOS = VPackWireFixtureTest.hex(
            "13 0c 28 16 28 1f 35 2a 8d a9 0c 04");
private static final byte[] TIME_TIMESTAMP_04_MILLIS = VPackWireFixtureTest.hex(
            "13 0b 28 16 28 1f 35 29 3d 03 04");
private static final byte[] SINGLE_TIME_STRING = VPackWireFixtureTest.hex(
            "13 09 45 31 32 3a 30 30 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] INVALID_LOCAL_TIME = VPackWireFixtureTest.hex(
            "4c 6e 6f 74 61 6c 6f 63 61 6c 74 69 6d 65");
private static final byte[] ZULU_2020_10_22_04 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] ZULU_2020_10_25_00 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 30 2d 32 35 54 30 30 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] ZULU_2020_10_25_01 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 30 2d 32 35 54 30 31 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] ZULU_2020_11_01_06 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 31 2d 30 31 54 30 36 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] ZULU_2020_11_01_07 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 31 2d 30 31 54 30 37 3a 31 36 3a 32 30 2e 35 30 34 5a");

    // Provenance: LocalTimeDeserTest#testBadDeserializationFromString.
    void testBadDeserializationFromStringVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(INVALID_LOCAL_TIME, LocalTime.class));
        assertTrue(failure.getMessage().contains(
                "Cannot deserialize value of type `java.time.LocalTime` from String"),
                failure.getMessage());
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsArrayDisabled.
    void testDeserializationAsArrayDisabledVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(SINGLE_TIME_STRING, LocalTime.class));
        assertTrue(failure.getMessage().contains("Unexpected token (VALUE_STRING) within Array"),
                failure.getMessage());
        assertNull(new VPackMapper().readValue(EMPTY_ARRAY, LocalTime.class));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsArrayEnabled.
    void testDeserializationAsArrayEnabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalTime.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertEquals(LocalTime.of(12, 0), reader.readValue(SINGLE_TIME_STRING));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalTime.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        assertNull(reader.readValue(EMPTY_ARRAY));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp01.
    void testDeserializationAsTimestamp01Vpack() throws Exception {
        assertEquals(LocalTime.of(15, 43),
                new VPackMapper().readValue(TIME_TIMESTAMP_01, LocalTime.class));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp02.
    void testDeserializationAsTimestamp02Vpack() throws Exception {
        assertEquals(LocalTime.of(9, 22, 57),
                new VPackMapper().readValue(TIME_TIMESTAMP_02, LocalTime.class));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp03Milliseconds.
    void testDeserializationAsTimestamp03MillisecondsVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalTime.of(9, 22, 0, 57_000_000), reader.readValue(TIME_TIMESTAMP_03));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp03Nanoseconds.
    void testDeserializationAsTimestamp03NanosecondsVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalTime.of(9, 22, 0, 57), reader.readValue(TIME_TIMESTAMP_03));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp04Milliseconds01.
    void testDeserializationAsTimestamp04Milliseconds01Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalTime.of(22, 31, 5, 829837),
                reader.readValue(TIME_TIMESTAMP_04_NANOS));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp04Milliseconds02.
    void testDeserializationAsTimestamp04Milliseconds02Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalTime.of(22, 31, 5, 829_000_000),
                reader.readValue(TIME_TIMESTAMP_04_MILLIS));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp04Nanoseconds.
    void testDeserializationAsTimestamp04NanosecondsVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalTime.of(22, 31, 5, 829837),
                reader.readValue(TIME_TIMESTAMP_04_NANOS));
    }
private static void assertZulu(TimeZone zone, byte[] input, LocalDateTime expected)
            throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .enable(DateTimeFeature.USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING)
                .build()
                .readerFor(LocalDateTime.class)
                .with(zone);
        assertEquals(expected, reader.readValue(input));
    }

    void __invoke_testBadDeserializationFromStringVpack() throws Exception {
        try {
            testBadDeserializationFromStringVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsArrayEnabledVpack();
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

}
