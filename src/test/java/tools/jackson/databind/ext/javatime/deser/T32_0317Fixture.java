package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0317Fixture {
private static final TypeReference<Map<String, LocalDateTime>> MAP_TYPE_REF =
            new TypeReference<Map<String, LocalDateTime>>() { };
private static final byte[] CASE_INSENSITIVE_UPPER = VPackWireFixtureTest.hex(
            "4b 30 31 2d 4a 41 4e 2d 32 30 30 30");
private static final byte[] CASE_INSENSITIVE_LOWER = VPackWireFixtureTest.hex(
            "4b 30 31 2d 6a 61 6e 2d 32 30 30 30");
private static final byte[] CASE_INSENSITIVE_CANONICAL = VPackWireFixtureTest.hex(
            "51 30 31 2d 4a 61 6e 2d 32 30 30 30 20 31 33 3a 34 35");
private static final byte[] ZULU_LOCAL_DATE_TIME = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 30 2d 32 32 54 30 30 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] DATETIME_NULL = VPackWireFixtureTest.hex(
            "14 0d 48 64 61 74 65 74 69 6d 65 18 01");
private static final byte[] DATETIME_EMPTY_STRING = VPackWireFixtureTest.hex(
            "14 0d 48 64 61 74 65 74 69 6d 65 40 01");
private static final byte[] RAW_TIMESTAMP = VPackWireFixtureTest.hex("29 d3 04");
private static final byte[] DATE_CONVERSION_STRING = VPackWireFixtureTest.hex(
            "53 31 39 39 39 2d 31 30 2d 31 32 54 31 33 3a 34 35 3a 30 35");
private static final byte[] INVALID_DATE_AND_TIME_WITH_ERA = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 53 32 30 31 39 2d 31 31 2d 33 31 20 32 35 3a 34 35 20 41 44 01");
private static final byte[] LOCAL_DATE_TIME_MAX = VPackWireFixtureTest.hex(
            "63 2b 39 39 39 39 39 39 39 39 39 2d 31 32 2d 33 31 54 32 33 3a 35 39 3a 35 39 2e 39 39 39 39 39 39 39 39 39");
private static final byte[] LOCAL_DATE_TIME_MIN = VPackWireFixtureTest.hex(
            "56 2d 39 39 39 39 39 39 39 39 39 2d 30 31 2d 30 31 54 30 30 3a 30 30");
private static final byte[] TYPE_INFO_ARRAY_NANOS = VPackWireFixtureTest.hex(
            "13 2d 57 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 54 69 6d 65 "
          + "13 12 29 d5 07 28 0b 35 28 16 28 1f 35 2a 8d a9 0c 07 02");
private static final byte[] TYPE_INFO_ARRAY_MILLIS = VPackWireFixtureTest.hex(
            "13 2c 57 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 54 69 6d 65 "
          + "13 11 29 d5 07 28 0b 35 28 16 28 1f 35 29 a6 01 07 02");
private static final byte[] TYPE_INFO_STRING = VPackWireFixtureTest.hex(
            "13 39 57 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 54 69 6d 65 "
          + "5d 32 30 30 35 2d 31 31 2d 30 35 54 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37 02");

    // Provenance: LocalDateTimeDeserTest#testDeserializationCaseInsensitiveDisabled_InvalidDate.
    void testDeserializationCaseInsensitiveDisabledInvalidDateVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false)
                .withConfigOverride(LocalDateTime.class, o -> o.setFormat(
                        JsonFormat.Value.forPattern("dd-MMM-yyyy")))
                .build();
        ObjectReader reader = mapper.readerFor(LocalDateTime.class);
        MismatchedInputException upper = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(CASE_INSENSITIVE_UPPER));
        assertTrue(upper.getMessage().contains(
                "Failed to deserialize `java.time.LocalDateTime` (with format"), upper.getMessage());
        MismatchedInputException lower = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(CASE_INSENSITIVE_LOWER));
        assertTrue(lower.getMessage().contains(
                "Failed to deserialize `java.time.LocalDateTime` (with format"), lower.getMessage());
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationCaseInsensitiveEnabled.
    void testDeserializationCaseInsensitiveEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES)
                .withConfigOverride(LocalDateTime.class, o -> o.setFormat(
                        JsonFormat.Value.forPattern("dd-MMM-yyyy HH:mm")))
                .build();
        ObjectReader reader = mapper.readerFor(LocalDateTime.class);
        assertEquals(LocalDateTime.of(2000, 1, 1, 13, 45),
                reader.readValue(CASE_INSENSITIVE_CANONICAL));
        assertEquals(LocalDateTime.of(2000, 1, 1, 13, 45),
                reader.readValue(VPackWireFixtureTest.hex(
                        "51 30 31 2d 4a 41 4e 2d 32 30 30 30 20 31 33 3a 34 35")));
        assertEquals(LocalDateTime.of(2000, 1, 1, 13, 45),
                reader.readValue(VPackWireFixtureTest.hex(
                        "51 30 31 2d 6a 61 6e 2d 32 30 30 30 20 31 33 3a 34 35")));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationCaseInsensitiveEnabledOnValue.
    void testDeserializationCaseInsensitiveEnabledOnValueVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDateTime.class, o -> o.setFormat(JsonFormat.Value
                        .forPattern("dd-MMM-yyyy HH:mm")
                        .withFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)))
                .build();
        ObjectReader reader = mapper.readerFor(LocalDateTime.class);
        assertEquals(LocalDateTime.of(2000, 1, 1, 13, 14),
                reader.readValue(VPackWireFixtureTest.hex(
                        "51 30 31 2d 4a 61 6e 2d 32 30 30 30 20 31 33 3a 31 34")));
        assertEquals(LocalDateTime.of(2000, 1, 1, 13, 14),
                reader.readValue(VPackWireFixtureTest.hex(
                        "51 30 31 2d 4a 41 4e 2d 32 30 30 30 20 31 33 3a 31 34")));
        assertEquals(LocalDateTime.of(2000, 1, 1, 13, 14),
                reader.readValue(VPackWireFixtureTest.hex(
                        "51 30 31 2d 6a 61 6e 2d 32 30 30 30 20 31 33 3a 31 34")));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationOfLocalDateTimeMax.
    void testDeserializationOfLocalDateTimeMaxVpack() throws Exception {
        ObjectMapper timestampMapper = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS).build();
        ObjectMapper stringMapper = VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS).build();
        assertLocalDateTimeExtrema(timestampMapper);
        assertLocalDateTimeExtrema(stringMapper);
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
        Temporal value = mapper.readerFor(Temporal.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_ARRAY_NANOS);
        assertInstanceOf(LocalDateTime.class, value);
        assertEquals(LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837), value);
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationWithTypeInfo02.
    void testDeserializationWithTypeInfo02Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
        Temporal value = mapper.readerFor(Temporal.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_ARRAY_MILLIS);
        assertInstanceOf(LocalDateTime.class, value);
        assertEquals(LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 422_000_000), value);
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationWithTypeInfo03.
    void testDeserializationWithTypeInfo03Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
        Temporal value = mapper.readValue(TYPE_INFO_STRING, Temporal.class);
        assertInstanceOf(LocalDateTime.class, value);
        assertEquals(LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837), value);
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializeToDate.
    void testDeserializeToDateVpack() throws Exception {
        Date date = new VPackMapper().readValue(DATE_CONVERSION_STRING, Date.class);
        assertNotNull(date);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(date.getTime());
        assertEquals(1999, calendar.get(Calendar.YEAR));
        assertEquals(12, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, calendar.get(Calendar.MINUTE));
        assertEquals(5, calendar.get(Calendar.SECOND));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserilizeFromSimpleTimestamp.
    void testDeserilizeFromSimpleTimestampVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(RAW_TIMESTAMP, LocalDateTime.class));
        assertTrue(failure.getMessage().contains(
                "raw timestamp (1235) not allowed for `java.time.LocalDateTime`"),
                failure.getMessage());
    }

    // Provenance: LocalDateTimeDeserTest#testFailOnZuluIfStrict.
    void testFailOnZuluIfStrictVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDateTime.class,
                        c -> c.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> mapper.readValue(ZULU_LOCAL_DATE_TIME, LocalDateTime.class));
        assertTrue(failure.getMessage().contains("Cannot deserialize value of type"), failure.getMessage());
        assertTrue(failure.getMessage().contains(
                "Should not contain offset when 'strict' mode"), failure.getMessage());
    }

    // Provenance: LocalDateTimeDeserTest#testLenientDeserializeFromEmptyString.
    void testLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        Map<String, LocalDateTime> fromNull = mapper.readValue(DATETIME_NULL, MAP_TYPE_REF);
        Map<String, LocalDateTime> fromEmpty = mapper.readValue(DATETIME_EMPTY_STRING, MAP_TYPE_REF);
        assertNull(fromNull.get("datetime"));
        assertEquals(fromNull.get("datetime"), fromEmpty.get("datetime"));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidDateAndTimeWithEra.
    void testStrictCustomFormatForInvalidDateAndTimeWithEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_DATE_AND_TIME_WITH_ERA,
                        StrictWrapperWithYearOfEra.class));
    }
private static void assertLocalDateTimeExtrema(ObjectMapper mapper) throws Exception {
        assertEquals(LocalDateTime.MAX, mapper.readValue(LOCAL_DATE_TIME_MAX, LocalDateTime.class));
        assertEquals(LocalDateTime.MIN, mapper.readValue(LOCAL_DATE_TIME_MIN, LocalDateTime.class));
        assertEquals(LocalDateTime.MAX,
                mapper.readValue(mapper.writeValueAsBytes(LocalDateTime.MAX), LocalDateTime.class));
        assertEquals(LocalDateTime.MIN,
                mapper.readValue(mapper.writeValueAsBytes(LocalDateTime.MIN), LocalDateTime.class));
    }
static final class NanosEnabledWrapper {
        @JsonFormat(with = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public LocalDateTime value;

        public NanosEnabledWrapper() { }
    }
static final class NanosDisabledWrapper {
        @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public LocalDateTime value;

        public NanosDisabledWrapper() { }
    }
static final class StrictWrapperWithYearOfEra {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm G", lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
        public LocalDateTime value;

        public StrictWrapperWithYearOfEra() { }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testDeserializationCaseInsensitiveDisabledInvalidDateVpack() throws Exception {
        try {
            testDeserializationCaseInsensitiveDisabledInvalidDateVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationCaseInsensitiveEnabledVpack() throws Exception {
        try {
            testDeserializationCaseInsensitiveEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationCaseInsensitiveEnabledOnValueVpack() throws Exception {
        try {
            testDeserializationCaseInsensitiveEnabledOnValueVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationOfLocalDateTimeMaxVpack() throws Exception {
        try {
            testDeserializationOfLocalDateTimeMaxVpack();
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


    void __invoke_testDeserializeToDateVpack() throws Exception {
        try {
            testDeserializeToDateVpack();
        } finally {
        }
    }


    void __invoke_testDeserilizeFromSimpleTimestampVpack() throws Exception {
        try {
            testDeserilizeFromSimpleTimestampVpack();
        } finally {
        }
    }


    void __invoke_testFailOnZuluIfStrictVpack() throws Exception {
        try {
            testFailOnZuluIfStrictVpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testLenientDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidDateAndTimeWithEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidDateAndTimeWithEraVpack();
        } finally {
        }
    }

}
