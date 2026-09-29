package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.ext.javatime.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0313Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] INSTANT_STRING = VPackWireFixtureTest.hex(
            "54 32 30 32 34 2d 30 37 2d 32 31 54 32 31 3a 35 39 3a 35 39 5a");
private static final byte[] BUDAPEST_BEFORE_MIDNIGHT = VPackWireFixtureTest.hex(
            "54 32 30 32 34 2d 30 37 2d 32 31 54 32 31 3a 35 39 3a 35 39 5a");
private static final byte[] BUDAPEST_AFTER_MIDNIGHT = VPackWireFixtureTest.hex(
            "54 32 30 32 34 2d 30 37 2d 32 31 54 32 32 3a 30 30 3a 30 30 5a");
private static final byte[] CHICAGO_BEFORE_MIDNIGHT = VPackWireFixtureTest.hex(
            "54 32 30 32 34 2d 30 37 2d 32 32 54 30 34 3a 35 39 3a 35 39 5a");
private static final byte[] CHICAGO_AFTER_MIDNIGHT = VPackWireFixtureTest.hex(
            "54 32 30 32 34 2d 30 37 2d 32 32 54 30 35 3a 30 30 3a 30 30 5a");
private static final byte[] TYPE_INFO_DATE = VPackWireFixtureTest.hex(
            "13 22 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 44 61 74 65 "
          + "4a 32 30 30 35 2d 31 31 2d 30 35 02");
private static final byte[] INVALID_EPOCH_DAY = VPackWireFixtureTest.hex(
            "27 1b 2a 6b 40 29 ff ff ff");
private static final byte[] INTEGER_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] EMPTY_STRING_WRAPPER = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 40 01");
private static final byte[] CASE_INSENSITIVE_CANONICAL = VPackWireFixtureTest.hex(
            "4b 30 31 2d 4a 61 6e 2d 32 30 30 30");
private static final byte[] CASE_INSENSITIVE_UPPER = VPackWireFixtureTest.hex(
            "4b 30 31 2d 4a 41 4e 2d 32 30 30 30");
private static final byte[] CASE_INSENSITIVE_LOWER = VPackWireFixtureTest.hex(
            "4b 30 31 2d 6a 61 6e 2d 32 30 30 30");

    // Provenance: LocalDateDeserTest#testLenientDeserializationAsString01.
    void testLenientDeserializationAsString01Vpack() throws Exception {
        assertEquals(LocalDate.of(2024, 7, 21),
                MAPPER.readValue(INSTANT_STRING, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testLenientDeserializationAsString02.
    void testLenientDeserializationAsString02Vpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(LocalDate.class)
                .with(TimeZone.getTimeZone("Europe/Budapest"));
        assertEquals(LocalDate.of(2024, 7, 21), reader.readValue(INSTANT_STRING));
    }

    // Provenance: LocalDateDeserTest#testLenientDeserializationAsString03.
    void testLenientDeserializationAsString03Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DateTimeFeature.USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING)
                .build();
        assertEquals(LocalDate.of(2024, 7, 21),
                mapper.readValue(INSTANT_STRING, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testLenientDeserializationAsString04.
    void testLenientDeserializationAsString04Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DateTimeFeature.USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING)
                .build();
        ObjectReader budapest = mapper.readerFor(LocalDate.class)
                .with(TimeZone.getTimeZone("Europe/Budapest"));
        ObjectReader chicago = mapper.readerFor(LocalDate.class)
                .with(TimeZone.getTimeZone("America/Chicago"));

        assertEquals(LocalDate.of(2024, 7, 21),
                budapest.readValue(BUDAPEST_BEFORE_MIDNIGHT));
        assertEquals(LocalDate.of(2024, 7, 22),
                budapest.readValue(BUDAPEST_AFTER_MIDNIGHT));
        assertEquals(LocalDate.of(2024, 7, 21),
                chicago.readValue(CHICAGO_BEFORE_MIDNIGHT));
        assertEquals(LocalDate.of(2024, 7, 22),
                chicago.readValue(CHICAGO_AFTER_MIDNIGHT));
    }

    // Provenance: LocalDateDeserTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
        assertEquals(LocalDate.of(2005, 11, 5),
                mapper.readValue(TYPE_INFO_DATE, Temporal.class));
    }

    // Provenance: LocalDateDeserTest#testInvalidEpochDayValueInLenientMode.
    void testInvalidEpochDayValueInLenientModeVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readValue(INVALID_EPOCH_DAY, LocalDate.class));
        assertTrue(failure.getMessage().contains("-922337203685"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("Invalid value for EpochDay"),
                failure.getMessage());
    }

    // Provenance: LocalDateDeserTest#testDeserializationCaseInsensitiveEnabledOnValue.
    void testDeserializationCaseInsensitiveEnabledOnValueVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class, o -> o.setFormat(JsonFormat.Value
                        .forPattern("dd-MMM-yyyy")
                        .withFeature(Feature.ACCEPT_CASE_INSENSITIVE_VALUES)))
                .build();
        ObjectReader reader = mapper.readerFor(LocalDate.class);
        assertEquals(LocalDate.of(2000, 1, 1),
                reader.readValue(CASE_INSENSITIVE_CANONICAL));
        assertEquals(LocalDate.of(2000, 1, 1),
                reader.readValue(CASE_INSENSITIVE_UPPER));
        assertEquals(LocalDate.of(2000, 1, 1),
                reader.readValue(CASE_INSENSITIVE_LOWER));
    }

    // Provenance: LocalDateDeserTest#testDeserializationCaseInsensitiveEnabled.
    void testDeserializationCaseInsensitiveEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES)
                .withConfigOverride(LocalDate.class, o -> o.setFormat(
                        JsonFormat.Value.forPattern("dd-MMM-yyyy")))
                .build();
        ObjectReader reader = mapper.readerFor(LocalDate.class);
        assertEquals(LocalDate.of(2000, 1, 1),
                reader.readValue(CASE_INSENSITIVE_CANONICAL));
        assertEquals(LocalDate.of(2000, 1, 1),
                reader.readValue(CASE_INSENSITIVE_UPPER));
        assertEquals(LocalDate.of(2000, 1, 1),
                reader.readValue(CASE_INSENSITIVE_LOWER));
    }

    // Provenance: LocalDateDeserTest#testDeserializationCaseInsensitiveDisabled_InvalidDate.
    void testDeserializationCaseInsensitiveDisabledInvalidDateVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false)
                .withConfigOverride(LocalDate.class, o -> o.setFormat(
                        JsonFormat.Value.forPattern("dd-MMM-yyyy")))
                .build();
        ObjectReader reader = mapper.readerFor(LocalDate.class);
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(CASE_INSENSITIVE_UPPER));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(CASE_INSENSITIVE_LOWER));
    }

    // Provenance: LocalDateDeserTest#testInvalidEpochDayValue.
    void testInvalidEpochDayValueVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class, o -> o.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER_INT)))
                .build();
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> mapper.readValue(INVALID_EPOCH_DAY, LocalDate.class));
        assertTrue(failure.getMessage().contains("-922337203685"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("Invalid value for EpochDay"),
                failure.getMessage());
    }

    // Provenance: LocalDateDeserTest#testDeserializeFromIntegerWithCoercionActionFail.
    void testDeserializeFromIntegerWithCoercionActionFailVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfig(LocalDate.class, cfg -> cfg.setCoercion(
                        CoercionInputShape.Integer, CoercionAction.Fail))
                .build();
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(INTEGER_123, LocalDate.class));
        assertTrue(failure.getMessage().contains(
                "Cannot coerce Integer value (123) to `java.time.LocalDate`"),
                failure.getMessage());
    }

    // Provenance: LocalDateDeserTest#testDeserializeFromEmptyStringWithCoercionActionFail.
    void testDeserializeFromEmptyStringWithCoercionActionFailVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfig(LocalDate.class, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.Fail))
                .build();
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(EMPTY_STRING_WRAPPER, CoercionWrapper.class));
        assertTrue(failure.getMessage().contains(
                "Cannot coerce empty String (\"\") to `java.time.LocalDate`"),
                failure.getMessage());
    }
static final class CoercionWrapper {
        public LocalDate value;

        public CoercionWrapper() { }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testLenientDeserializationAsString01Vpack() throws Exception {
        try {
            testLenientDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializationAsString02Vpack() throws Exception {
        try {
            testLenientDeserializationAsString02Vpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializationAsString03Vpack() throws Exception {
        try {
            testLenientDeserializationAsString03Vpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializationAsString04Vpack() throws Exception {
        try {
            testLenientDeserializationAsString04Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testInvalidEpochDayValueInLenientModeVpack() throws Exception {
        try {
            testInvalidEpochDayValueInLenientModeVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationCaseInsensitiveEnabledOnValueVpack() throws Exception {
        try {
            testDeserializationCaseInsensitiveEnabledOnValueVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationCaseInsensitiveEnabledVpack() throws Exception {
        try {
            testDeserializationCaseInsensitiveEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationCaseInsensitiveDisabledInvalidDateVpack() throws Exception {
        try {
            testDeserializationCaseInsensitiveDisabledInvalidDateVpack();
        } finally {
        }
    }


    void __invoke_testInvalidEpochDayValueVpack() throws Exception {
        try {
            testInvalidEpochDayValueVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeFromIntegerWithCoercionActionFailVpack() throws Exception {
        try {
            testDeserializeFromIntegerWithCoercionActionFailVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeFromEmptyStringWithCoercionActionFailVpack() throws Exception {
        try {
            testDeserializeFromEmptyStringWithCoercionActionFailVpack();
        } finally {
        }
    }

}
