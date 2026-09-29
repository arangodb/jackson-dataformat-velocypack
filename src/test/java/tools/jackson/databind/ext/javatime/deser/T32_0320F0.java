package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0320F0 {
private static final TypeReference<Map<String, LocalTime>> LOCAL_TIME_MAP_TYPE =
            new TypeReference<Map<String, LocalTime>>() { };
private static final byte[] TIME_TIMESTAMP_05_NANOS = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 13 09 39 28 16 30 28 39 04 01");
private static final byte[] TIME_TIMESTAMP_05_MILLIS_02 = VPackWireFixtureTest.hex(
            "14 13 45 76 61 6c 75 65 13 0a 39 28 16 30 29 a1 10 04 01");
private static final byte[] TIME_STRING_15_43 = VPackWireFixtureTest.hex(
            "45 31 35 3a 34 33");
private static final byte[] TIME_STRING_12_00 = VPackWireFixtureTest.hex(
            "45 31 32 3a 30 30");
private static final byte[] TIME_STRING_09_22_57 = VPackWireFixtureTest.hex(
            "48 30 39 3a 32 32 3a 35 37");
private static final byte[] TIME_STRING_22_31_05 = VPackWireFixtureTest.hex(
            "52 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37");
private static final byte[] TIME_TYPE_INFO_NANOS = VPackWireFixtureTest.hex(
            "13 23 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 54 69 6d 65 "
          + "13 0c 28 16 28 1f 35 2a 8d a9 0c 04 02");
private static final byte[] TIME_TYPE_INFO_MILLIS = VPackWireFixtureTest.hex(
            "13 22 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 54 69 6d 65 "
          + "13 0b 28 16 28 1f 35 29 a6 01 04 02");
private static final byte[] TIME_TYPE_INFO_STRING = VPackWireFixtureTest.hex(
            "13 2a 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 54 69 6d 65 "
          + "52 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37 02");
private static final byte[] LOCAL_TIME_NULL = VPackWireFixtureTest.hex(
            "14 0e 49 6c 6f 63 61 6c 54 69 6d 65 18 01");
private static final byte[] LOCAL_TIME_EMPTY = VPackWireFixtureTest.hex(
            "14 0e 49 6c 6f 63 61 6c 54 69 6d 65 40 01");
private static final byte[] DATE_EMPTY = VPackWireFixtureTest.hex(
            "14 09 44 64 61 74 65 40 01");
private static final byte[] INVALID_FORMATTED_TIME = VPackWireFixtureTest.hex(
            "14 0f 45 76 61 6c 75 65 45 32 35 3a 34 35 01");
private static final byte[] INVALID_MONTH_DAY = VPackWireFixtureTest.hex(
            "4c 6e 6f 74 61 6d 6f 6e 74 68 64 61 79");
private static final byte[] MONTH_DAY_JANUARY_17 = VPackWireFixtureTest.hex(
            "47 2d 2d 30 31 2d 31 37");

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp05Milliseconds01.
    void testDeserializationAsTimestamp05Milliseconds01Vpack() throws Exception {
        NanosDisabledWrapper actual = new VPackMapper().readValue(
                TIME_TIMESTAMP_05_NANOS, NanosDisabledWrapper.class);
        assertEquals(LocalTime.of(9, 22, 0, 57_000_000), actual.value);
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp05Milliseconds02.
    void testDeserializationAsTimestamp05Milliseconds02Vpack() throws Exception {
        NanosDisabledWrapper actual = new VPackMapper().readValue(
                TIME_TIMESTAMP_05_MILLIS_02, NanosDisabledWrapper.class);
        assertEquals(LocalTime.of(9, 22, 0, 4257), actual.value);
    }

    // Provenance: LocalTimeDeserTest#testDeserializationAsTimestamp05Nanoseconds.
    void testDeserializationAsTimestamp05NanosecondsVpack() throws Exception {
        NanosEnabledWrapper actual = new VPackMapper().readValue(
                TIME_TIMESTAMP_05_NANOS, NanosEnabledWrapper.class);
        assertEquals(LocalTime.of(9, 22, 0, 57), actual.value);
    }

    // Provenance: LocalTimeDeserTest#testDeserializationFromString.
    void testDeserializationFromStringVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalTime.class);
        assertEquals(LocalTime.of(15, 43), reader.readValue(TIME_STRING_15_43));
        assertEquals(LocalTime.of(12, 0), reader.readValue(TIME_STRING_12_00));
        assertEquals(LocalTime.of(9, 22, 57), reader.readValue(TIME_STRING_09_22_57));
        assertEquals(LocalTime.of(22, 31, 5, 829837), reader.readValue(TIME_STRING_22_31_05));
    }

    // Provenance: LocalTimeDeserTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
        Temporal value = mapper.readerFor(Temporal.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIME_TYPE_INFO_NANOS);
        assertNotNull(value);
        assertInstanceOf(LocalTime.class, value);
        assertEquals(LocalTime.of(22, 31, 5, 829837), value);
    }

    // Provenance: LocalTimeDeserTest#testDeserializationWithTypeInfo02.
    void testDeserializationWithTypeInfo02Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
        Temporal value = mapper.readerFor(Temporal.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIME_TYPE_INFO_MILLIS);
        assertInstanceOf(LocalTime.class, value);
        assertEquals(LocalTime.of(22, 31, 5, 422_000_000), value);
    }

    // Provenance: LocalTimeDeserTest#testDeserializationWithTypeInfo03.
    void testDeserializationWithTypeInfo03Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
        Temporal value = mapper.readValue(TIME_TYPE_INFO_STRING, Temporal.class);
        assertInstanceOf(LocalTime.class, value);
        assertEquals(LocalTime.of(22, 31, 5, 829837), value);
    }

    // Provenance: LocalTimeDeserTest#testLenientDeserializeFromEmptyString.
    void testLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LOCAL_TIME_MAP_TYPE);
        Map<String, LocalTime> fromNull = reader.readValue(LOCAL_TIME_NULL);
        Map<String, LocalTime> fromEmpty = reader.readValue(LOCAL_TIME_EMPTY);
        assertNull(fromNull.get("localTime"));
        assertNull(fromEmpty.get("localTime"));
    }

    // Provenance: LocalTimeDeserTest#testStrictCustomFormatInvalidTime.
    void testStrictCustomFormatInvalidTimeVpack() {
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_FORMATTED_TIME, StrictWrapper.class));
        assertTrue(failure.getMessage().contains(
                "Cannot deserialize value of type `java.time.LocalTime` from String"),
                failure.getMessage());
    }

    // Provenance: LocalTimeDeserTest#testStrictDeserializeFromEmptyString.
    void testStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalTime.class,
                        c -> c.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        ObjectReader reader = mapper.readerFor(LOCAL_TIME_MAP_TYPE);
        Map<String, LocalTime> fromNull = reader.readValue(LOCAL_TIME_NULL);
        assertNull(fromNull.get("localTime"));
        assertThrows(MismatchedInputException.class, () -> reader.readValue(DATE_EMPTY));
    }
static final class NanosDisabledWrapper {
        @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public LocalTime value;

        public NanosDisabledWrapper() { }
    }
static final class NanosEnabledWrapper {
        @JsonFormat(with = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public LocalTime value;

        public NanosEnabledWrapper() { }
    }
static final class StrictWrapper {
        @JsonFormat(pattern = "HH:mm", lenient = OptBoolean.FALSE)
        public LocalTime value;

        public StrictWrapper() { }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

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


    void __invoke_testDeserializationFromStringVpack() throws Exception {
        try {
            testDeserializationFromStringVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo02Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo03Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo03Vpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testLenientDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatInvalidTimeVpack() throws Exception {
        try {
            testStrictCustomFormatInvalidTimeVpack();
        } finally {
        }
    }


    void __invoke_testStrictDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testStrictDeserializeFromEmptyStringVpack();
        } finally {
        }
    }

}
