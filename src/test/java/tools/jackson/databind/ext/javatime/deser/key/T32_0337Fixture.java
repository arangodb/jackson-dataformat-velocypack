package tools.jackson.databind.ext.javatime.deser.key;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0337Fixture {
private static final TypeReference<Map<String, ZonedDateTime>> ZONED_MAP =
            new TypeReference<Map<String, ZonedDateTime>>() { };
private static final TypeReference<Map<ZonedDateTime, String>> ZONED_KEY_MAP =
            new TypeReference<Map<ZonedDateTime, String>>() { };
private static final TypeReference<Map<Duration, String>> DURATION_KEY_MAP =
            new TypeReference<Map<Duration, String>>() { };
private static final byte[] STANDARD = VPackWireFixtureTest.hex(
            "5c 32 30 32 31 2d 30 32 2d 30 31 54 31 39 3a 34 39 3a 30 34 2e 30 35 31 33 34 38 36 5a");
private static final byte[] STANDARD_WITH_ZONE = VPackWireFixtureTest.hex(
            "61 32 30 32 31 2d 30 32 2d 30 31 54 31 39 3a 34 39 3a 30 34 2e 30 35 31 33 34 38 36 5a 5b 55 54 43 5d");
private static final byte[] ZONE_PRESERVED = VPackWireFixtureTest.hex(
            "14 20 45 76 61 6c 75 65 56 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 2b 30 31 3a 30 30 01");
private static final byte[] OFFSET_WITHOUT_COLON = VPackWireFixtureTest.hex(
            "14 1f 45 76 61 6c 75 65 55 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 2b 30 31 30 30 01");
private static final byte[] OFFSET_WITHOUT_COLON_TZDB = VPackWireFixtureTest.hex(
            "14 2d 45 76 61 6c 75 65 63 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 2b 30 31 30 30 5b 45 75 72 6f 70 65 2f 50 61 72 69 73 5d 01");
private static final byte[] EMPTY_VALUE_NULL = VPackWireFixtureTest.hex(
            "14 11 4c 7a 6f 6e 65 44 61 74 65 54 69 6d 65 18 01");
private static final byte[] EMPTY_VALUE_STRING = VPackWireFixtureTest.hex(
            "14 11 4c 7a 6f 6e 65 44 61 74 65 54 69 6d 65 40 01");
private static final byte[] KEY_INSTANT = VPackWireFixtureTest.hex(
            "14 2d 58 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 5a 50 54 68 69 73 20 69 73 20 61 20 73 74 72 69 6e 67 01");
private static final byte[] KEY_ZONE_NAME = VPackWireFixtureTest.hex(
            "14 32 5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 5a 5b 55 54 43 5d 50 54 68 69 73 20 69 73 20 61 20 73 74 72 69 6e 67 01");
private static final byte[] KEY_PLACE_NAME = VPackWireFixtureTest.hex(
            "14 3c 67 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 5a 5b 45 75 72 6f 70 65 2f 4c 6f 6e 64 6f 6e 5d 50 54 68 69 73 20 69 73 20 61 20 73 74 72 69 6e 67 01");
private static final byte[] KEY_OFFSET = VPackWireFixtureTest.hex(
            "14 32 5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 32 3a 30 30 50 54 68 69 73 20 69 73 20 61 20 73 74 72 69 6e 67 01");
private static final byte[] DURATION_KEY = VPackWireFixtureTest.hex(
            "14 14 4b 50 54 31 33 4d 33 37 2e 31 32 53 44 74 65 73 74 01" );

    // Provenance: ZonedDateTimeKeyDeserializerTest#Instant_style_can_be_deserialized.
    void instantStyleCanBeDeserializedVpack() throws Exception {
        assertEquals("2015-07-24T12:23:34.184Z",
                zonedKey(KEY_INSTANT).keySet().iterator().next().toString());
    }

    // Provenance: ZonedDateTimeKeyDeserializerTest#ZonedDateTime_with_zone_name_can_be_deserialized.
    void zonedDateTimeWithZoneNameCanBeDeserializedVpack() throws Exception {
        assertEquals("2015-07-24T12:23:34.184Z[UTC]",
                zonedKey(KEY_ZONE_NAME).keySet().iterator().next().toString());
    }

    // Provenance: ZonedDateTimeKeyDeserializerTest#ZonedDateTime_with_place_name_can_be_deserialized.
    void zonedDateTimeWithPlaceNameCanBeDeserializedVpack() throws Exception {
        assertEquals("2015-07-24T13:23:34.184+01:00[Europe/London]",
                zonedKey(KEY_PLACE_NAME).keySet().iterator().next().toString());
    }

    // Provenance: ZonedDateTimeKeyDeserializerTest#ZonedDateTime_with_offset_can_be_deserialized.
    void zonedDateTimeWithOffsetCanBeDeserializedVpack() throws Exception {
        assertEquals("2015-07-24T12:23:34.184+02:00",
                zonedKey(KEY_OFFSET).keySet().iterator().next().toString());
    }
private static Map<ZonedDateTime, String> zonedKey(byte[] input) throws Exception {
        return new VPackMapper().readerFor(ZONED_KEY_MAP).readValue(input);
    }
static class ZonedWrapper {
        @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public ZonedDateTime value;
    }

    void __invoke_instantStyleCanBeDeserializedVpack() throws Exception {
        try {
            instantStyleCanBeDeserializedVpack();
        } finally {
        }
    }


    void __invoke_zonedDateTimeWithZoneNameCanBeDeserializedVpack() throws Exception {
        try {
            zonedDateTimeWithZoneNameCanBeDeserializedVpack();
        } finally {
        }
    }


    void __invoke_zonedDateTimeWithPlaceNameCanBeDeserializedVpack() throws Exception {
        try {
            zonedDateTimeWithPlaceNameCanBeDeserializedVpack();
        } finally {
        }
    }


    void __invoke_zonedDateTimeWithOffsetCanBeDeserializedVpack() throws Exception {
        try {
            zonedDateTimeWithOffsetCanBeDeserializedVpack();
        } finally {
        }
    }

}
