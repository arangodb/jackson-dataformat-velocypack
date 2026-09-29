package tools.jackson.databind.ext.javatime.deser;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0331Fixture {
private static final byte[] BAD_OFFSET = VPackWireFixtureTest.hex(
            "14 1a 45 76 61 6c 75 65 13 11 39 28 16 30 29 a1 10 46 2d 32 35 3a 33 30 05 01");
private static final byte[] CORRUPT_OFFSET = VPackWireFixtureTest.hex(
            "14 22 45 76 61 6c 75 65 13 19 39 28 16 30 29 a1 10 4e 63 6f 72 72 75 70 74 2d "
          + "6f 66 66 73 65 74 05 01");
private static final byte[] ARRAY_MISSING_TIME_ZONE = VPackWireFixtureTest.hex(
            "13 0b 28 0a 28 1e 28 2d 28 7b 04");
private static final byte[] ARRAY_MISSING_TIME_ZONE_MINIMAL = VPackWireFixtureTest.hex(
            "13 07 28 0a 28 1e 02");
private static final byte[] INTEGER = VPackWireFixtureTest.hex("29 39 30");
private static final byte[] STRING_01 = VPackWireFixtureTest.hex(
            "4e 31 35 3a 34 33 3a 30 30 2b 30 33 3a 30 30");
private static final byte[] STRING_02 = VPackWireFixtureTest.hex(
            "4e 30 39 3a 32 32 3a 35 37 2d 30 36 3a 33 30");
private static final byte[] STRING_03 = VPackWireFixtureTest.hex(
            "58 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37 2b 31 31 3a 30 30");
private static final byte[] STRING_UTC = VPackWireFixtureTest.hex(
            "46 31 32 3a 30 30 5a");
private static final byte[] CUSTOM_PATTERN_01 = VPackWireFixtureTest.hex(
            "14 18 45 76 61 6c 75 65 4e 31 35 3a 33 30 3a 34 35 2b 30 32 3a 30 30 01");
private static final byte[] CUSTOM_PATTERN_02 = VPackWireFixtureTest.hex(
            "14 13 45 76 61 6c 75 65 49 31 30 3a 31 35 3a 33 30 5a 01");
private static final byte[] TRUNCATE_NANOS_STRING = VPackWireFixtureTest.hex(
            "58 31 30 3a 33 30 3a 34 35 2e 31 32 33 34 35 36 37 38 39 2b 30 32 3a 30 30");
private static final byte[] TRUNCATE_MILLIS_ARRAY = VPackWireFixtureTest.hex(
            "13 15 28 0a 28 1e 28 2d 2b 15 cd 5b 07 46 2b 30 32 3a 30 30 05");
private static final byte[] ALREADY_TRUNCATED_STRING = VPackWireFixtureTest.hex(
            "52 31 30 3a 33 30 3a 34 35 2e 31 32 33 2b 30 32 3a 30 30");
private static final byte[] TYPE_INFO_01 = VPackWireFixtureTest.hex(
            "13 2b 54 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 54 69 6d 65 "
          + "13 13 28 16 28 1f 35 2a 8d a9 0c 46 2b 31 31 3a 30 30 05 02");
private static final byte[] TYPE_INFO_02 = VPackWireFixtureTest.hex(
            "13 2a 54 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 54 69 6d 65 "
          + "13 12 28 16 28 1f 35 29 a6 01 46 2b 31 31 3a 30 30 05 02");
private static final byte[] TYPE_INFO_03 = VPackWireFixtureTest.hex(
            "13 31 54 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 54 69 6d 65 "
          + "58 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37 2b 31 31 3a 30 30 02");

    // Provenance: OffsetTimeDeserTest#testDeserializationBadOffset.
    void testDeserializationBadOffsetVpack() {
        DatabindException failure = assertThrows(DatabindException.class,
                () -> new VPackMapper().readerFor(NanosDisabledWrapper.class)
                        .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                        .readValue(BAD_OFFSET));
        assertTrue(failure.getMessage().contains(
                "value -25 is not in the range -18 to 18"), failure.getMessage());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationCorruptOffset.
    void testDeserializationCorruptOffsetVpack() {
        DatabindException failure = assertThrows(DatabindException.class,
                () -> new VPackMapper().readerFor(NanosDisabledWrapper.class)
                        .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                        .readValue(CORRUPT_OFFSET));
        assertTrue(failure.getMessage().contains(
                "Invalid ID for ZoneOffset, invalid format: corrupt-offset"), failure.getMessage());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationFromArrayMissingTimeZone.
    void testDeserializationFromArrayMissingTimeZoneVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(ARRAY_MISSING_TIME_ZONE, OffsetTime.class));
        assertTrue(failure.getMessage().contains(
                "Expected string for TimeZone"), failure.getMessage());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationFromArrayMissingTimeZoneMinimal.
    void testDeserializationFromArrayMissingTimeZoneMinimalVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(ARRAY_MISSING_TIME_ZONE_MINIMAL, OffsetTime.class));
        assertTrue(failure.getMessage().contains(
                "Expected string for TimeZone"), failure.getMessage());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationFromIntegerFails.
    void testDeserializationFromIntegerFailsVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(INTEGER, OffsetTime.class));
        assertTrue(failure.getMessage().contains("raw timestamp"),
                failure.getMessage());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationFromString01.
    void testDeserializationFromString01Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetTime.class);
        assertEquals(OffsetTime.of(15, 43, 0, 0, ZoneOffset.of("+0300")),
                reader.readValue(STRING_01));
        assertEquals(OffsetTime.of(9, 22, 57, 0, ZoneOffset.of("-0630")),
                reader.readValue(STRING_02));
        assertEquals(OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100")),
                reader.readValue(STRING_03));
        assertEquals(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC),
                reader.readValue(STRING_UTC));
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationTruncateToMillis.
    void testDeserializationTruncateToMillisVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetTime.class)
                .with(DateTimeFeature.TRUNCATE_TO_MSECS_ON_READ);
        OffsetTime stringValue = reader.readValue(TRUNCATE_NANOS_STRING);
        assertEquals(123_000_000, stringValue.getNano());
        assertEquals(10, stringValue.getHour());
        assertEquals(30, stringValue.getMinute());
        assertEquals(45, stringValue.getSecond());

        OffsetTime arrayValue = reader.with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TRUNCATE_MILLIS_ARRAY);
        assertEquals(123_000_000, arrayValue.getNano());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationTruncateToMillisAlreadyTruncated.
    void testDeserializationTruncateToMillisAlreadyTruncatedVpack() throws Exception {
        OffsetTime value = new VPackMapper().readerFor(OffsetTime.class)
                .with(DateTimeFeature.TRUNCATE_TO_MSECS_ON_READ)
                .readValue(ALREADY_TRUNCATED_STRING);
        assertEquals(123_000_000, value.getNano());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationWithCustomPattern.
    void testDeserializationWithCustomPatternVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(CustomPatternWrapper.class);
        CustomPatternWrapper first = reader.readValue(CUSTOM_PATTERN_01);
        assertEquals(OffsetTime.of(15, 30, 45, 0, ZoneOffset.ofHours(2)), first.value);
        CustomPatternWrapper second = reader.readValue(CUSTOM_PATTERN_02);
        assertEquals(OffsetTime.of(10, 15, 30, 0, ZoneOffset.UTC), second.value);
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        Temporal value = typeInfoMapper().readerFor(Temporal.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_01);
        assertEquals(OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100")),
                assertInstanceOf(OffsetTime.class, value));
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationWithTypeInfo02.
    void testDeserializationWithTypeInfo02Vpack() throws Exception {
        Temporal value = typeInfoMapper().readerFor(Temporal.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_02);
        assertEquals(OffsetTime.of(22, 31, 5, 422_000_000, ZoneOffset.of("+1100")),
                assertInstanceOf(OffsetTime.class, value));
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationWithTypeInfo03.
    void testDeserializationWithTypeInfo03Vpack() throws Exception {
        Temporal value = typeInfoMapper().readValue(TYPE_INFO_03, Temporal.class);
        assertEquals(OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100")),
                assertInstanceOf(OffsetTime.class, value));
    }
private static ObjectMapper typeInfoMapper() {
        return VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
    }
static final class NanosDisabledWrapper {
        @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public OffsetTime value;

        public NanosDisabledWrapper() { }
    }
static final class CustomPatternWrapper {
        @JsonFormat(pattern = "HH:mm:ssXXX")
        public OffsetTime value;

        public CustomPatternWrapper() { }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testDeserializationBadOffsetVpack() throws Exception {
        try {
            testDeserializationBadOffsetVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationCorruptOffsetVpack() throws Exception {
        try {
            testDeserializationCorruptOffsetVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromArrayMissingTimeZoneVpack() throws Exception {
        try {
            testDeserializationFromArrayMissingTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromArrayMissingTimeZoneMinimalVpack() throws Exception {
        try {
            testDeserializationFromArrayMissingTimeZoneMinimalVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromIntegerFailsVpack() throws Exception {
        try {
            testDeserializationFromIntegerFailsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromString01Vpack() throws Exception {
        try {
            testDeserializationFromString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationTruncateToMillisVpack() throws Exception {
        try {
            testDeserializationTruncateToMillisVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationTruncateToMillisAlreadyTruncatedVpack() throws Exception {
        try {
            testDeserializationTruncateToMillisAlreadyTruncatedVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithCustomPatternVpack() throws Exception {
        try {
            testDeserializationWithCustomPatternVpack();
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

}
