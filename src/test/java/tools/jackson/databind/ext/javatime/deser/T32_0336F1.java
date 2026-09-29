package tools.jackson.databind.ext.javatime.deser;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0336F1 {
private static final TypeReference<Map<String, ZoneOffset>> OFFSET_MAP =
            new TypeReference<Map<String, ZoneOffset>>() { };
private static final TypeReference<Map<String, ZonedDateTime>> ZONED_MAP =
            new TypeReference<Map<String, ZonedDateTime>>() { };
private static final byte[] OFFSET_NULL = VPackWireFixtureTest.hex(
            "14 0f 4a 7a 6f 6e 65 4f 66 66 73 65 74 18 01");
private static final byte[] OFFSET_EMPTY = VPackWireFixtureTest.hex(
            "14 0f 4a 7a 6f 6e 65 4f 66 66 73 65 74 40 01");
private static final byte[] OFFSET_TWO_SPACES = VPackWireFixtureTest.hex("42 20 20");
private static final byte[] ZONED_2000_UTC = VPackWireFixtureTest.hex(
            "51 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 5a");
private static final byte[] ZONED_BAD = VPackWireFixtureTest.hex(
            "48 6e 6f 74 61 7a 6f 6e 65");
private static final byte[] ZONED_MILLIS = VPackWireFixtureTest.hex(
            "14 0c 45 76 61 6c 75 65 29 e9 03 01");
private static final byte[] ZONED_SECONDS = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 31 01");
private static final byte[] ZONED_NULL = VPackWireFixtureTest.hex(
            "14 12 4d 7a 6f 6e 65 64 44 61 74 65 54 69 6d 65 18 01");
private static final byte[] ZONED_EMPTY = VPackWireFixtureTest.hex(
            "14 12 4d 7a 6f 6e 65 64 44 61 74 65 54 69 6d 65 40 01");
private static final byte[] ZONED_SINGLE_ARRAY = VPackWireFixtureTest.hex(
            "13 15 51 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 5a 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] OFFSET_PLUS_HOUR = VPackWireFixtureTest.hex(
            "5a 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31");
private static final byte[] OFFSET_MINUS_HOUR = VPackWireFixtureTest.hex(
            "5a 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31");
private static final byte[] OFFSET_PLUS_COMPACT = VPackWireFixtureTest.hex(
            "5c 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31 30 30");
private static final byte[] OFFSET_MINUS_COMPACT = VPackWireFixtureTest.hex(
            "5c 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31 30 30");
private static final byte[] OFFSET_PLUS_COLON = VPackWireFixtureTest.hex(
            "5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31 3a 30 30");
private static final byte[] OFFSET_MINUS_COLON = VPackWireFixtureTest.hex(
            "5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31 3a 30 30");
private static final byte[] OFFSET_PLUS_MINUTES = VPackWireFixtureTest.hex(
            "5c 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31 33 30");
private static final byte[] OFFSET_MINUS_MINUTES = VPackWireFixtureTest.hex(
            "5c 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31 33 30");
private static final byte[] OFFSET_PLUS_COLON_MINUTES = VPackWireFixtureTest.hex(
            "5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31 3a 33 30");
private static final byte[] OFFSET_MINUS_COLON_MINUTES = VPackWireFixtureTest.hex(
            "5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31 3a 33 30");

    // Provenance: ZonedDateTimeDeserTest#ZonedDateTime_with_offset_can_be_deserialized.
    void zonedDateTimeWithOffsetCanBeDeserializedVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        ZonedDateTime base = ZonedDateTime.of(2015, 7, 24, 12, 23, 34, 184_000_000,
                ZoneOffset.UTC);
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHours(1)),
                reader.readValue(OFFSET_PLUS_HOUR));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHours(-1)),
                reader.readValue(OFFSET_MINUS_HOUR));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHours(1)),
                reader.readValue(OFFSET_PLUS_COMPACT));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHours(-1)),
                reader.readValue(OFFSET_MINUS_COMPACT));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHours(1)),
                reader.readValue(OFFSET_PLUS_COLON));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHours(-1)),
                reader.readValue(OFFSET_MINUS_COLON));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHoursMinutes(1, 30)),
                reader.readValue(OFFSET_PLUS_MINUTES));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHoursMinutes(-1, -30)),
                reader.readValue(OFFSET_MINUS_MINUTES));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHoursMinutes(1, 30)),
                reader.readValue(OFFSET_PLUS_COLON_MINUTES));
        assertEquals(base.withZoneSameLocal(ZoneOffset.ofHoursMinutes(-1, -30)),
                reader.readValue(OFFSET_MINUS_COLON_MINUTES));
    }

    // Provenance: ZonedDateTimeDeserTest#testBadDeserializationAsString01.
    void testBadDeserializationAsString01Vpack() {
        assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(ZONED_BAD, ZonedDateTime.class));
    }

    // Provenance: ZonedDateTimeDeserTest#testDeserFromString.
    void testDeserFromStringVpack() throws Exception {
        assertEquals(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                new VPackMapper().readValue(ZONED_2000_UTC, ZonedDateTime.class));
    }

    // Provenance: ZonedDateTimeDeserTest#testDeserFromStringNoZoneIdNormalization.
    void testDeserFromStringNoZoneIdNormalizationVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .disable(DateTimeFeature.NORMALIZE_DESERIALIZED_ZONE_ID)
                .build()
                .readerFor(ZonedDateTime.class);
        assertEquals(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0,
                        TimeZone.getTimeZone("UTC").toZoneId()),
                reader.readValue(ZONED_2000_UTC));
    }

    // Provenance: ZonedDateTimeDeserTest#testDeserializationAsArrayDisabled.
    void testDeserializationAsArrayDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(ZONED_SINGLE_ARRAY, ZonedDateTime.class));
    }

    // Provenance: ZonedDateTimeDeserTest#testDeserializationAsArrayEnabled.
    void testDeserializationAsArrayEnabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertEquals(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                reader.readValue(ZONED_SINGLE_ARRAY));
    }

    // Provenance: ZonedDateTimeDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testDeserializationAsEmptyArrayDisabledVpack() {
        ObjectReader reader = new VPackMapper().readerFor(ZonedDateTime.class);
        assertThrows(MismatchedInputException.class, () -> reader.readValue(EMPTY_ARRAY));
        assertThrows(MismatchedInputException.class,
                () -> reader.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                        .readValue(EMPTY_ARRAY));
    }

    // Provenance: ZonedDateTimeDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        assertNull(reader.readValue(EMPTY_ARRAY));
    }

    // Provenance: ZonedDateTimeDeserTest#testDeserializationAsInt01.
    void testDeserializationAsInt01Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(MillisDisabledWrapper.class);
        ZonedDateTime expected = ZonedDateTime.of(
                java.time.LocalDateTime.ofEpochSecond(1, 1_000_000, ZoneOffset.UTC),
                ZoneOffset.UTC);
        MillisDisabledWrapper actual = reader.readValue(ZONED_MILLIS);
        assertEquals(expected, actual.value);
    }

    // Provenance: ZonedDateTimeDeserTest#testDeserializationAsInt02.
    void testDeserializationAsInt02Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(NanosEnabledWrapper.class);
        ZonedDateTime expected = ZonedDateTime.of(
                java.time.LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC),
                ZoneOffset.UTC);
        NanosEnabledWrapper actual = reader.readValue(ZONED_SECONDS);
        assertEquals(expected, actual.value);
    }
static class MillisDisabledWrapper {
        @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public ZonedDateTime value;

        public MillisDisabledWrapper() { }
    }
static class NanosEnabledWrapper {
        @JsonFormat(with = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public ZonedDateTime value;

        public NanosEnabledWrapper() { }
    }

    void __invoke_zonedDateTimeWithOffsetCanBeDeserializedVpack() throws Exception {
        try {
            zonedDateTimeWithOffsetCanBeDeserializedVpack();
        } finally {
        }
    }


    void __invoke_testBadDeserializationAsString01Vpack() throws Exception {
        try {
            testBadDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserFromStringVpack() throws Exception {
        try {
            testDeserFromStringVpack();
        } finally {
        }
    }


    void __invoke_testDeserFromStringNoZoneIdNormalizationVpack() throws Exception {
        try {
            testDeserFromStringNoZoneIdNormalizationVpack();
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


    void __invoke_testDeserializationAsInt01Vpack() throws Exception {
        try {
            testDeserializationAsInt01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02Vpack() throws Exception {
        try {
            testDeserializationAsInt02Vpack();
        } finally {
        }
    }

}
