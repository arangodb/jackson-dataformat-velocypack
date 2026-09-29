package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0320F1 {
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

    // Provenance: MonthDayDeserTest#testBadDeserializationAsString01.
    void testBadDeserializationAsString01Vpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(INVALID_MONTH_DAY, MonthDay.class));
        assertTrue(failure.getMessage().contains(
                "Cannot deserialize value of type `java.time.MonthDay` from String"),
                failure.getMessage());
    }

    // Provenance: MonthDayDeserTest#testDeserialization01.
    void testDeserialization01Vpack() throws Exception {
        assertEquals(MonthDay.of(Month.JANUARY, 17),
                new VPackMapper().readValue(MONTH_DAY_JANUARY_17, MonthDay.class));
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

    void __invoke_testBadDeserializationAsString01Vpack() throws Exception {
        try {
            testBadDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserialization01Vpack() throws Exception {
        try {
            testDeserialization01Vpack();
        } finally {
        }
    }

}
