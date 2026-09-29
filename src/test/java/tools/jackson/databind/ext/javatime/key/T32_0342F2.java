package tools.jackson.databind.ext.javatime.key;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0342F2 {
private static final ZoneId ZONE_0 = ZoneId.of("UTC");
private static final ZoneId ZONE_1 = ZoneId.of("+06:00");
private static final ZoneId ZONE_2 = ZoneId.of("Europe/London");
private static final ZoneOffset OFFSET_0 = ZoneOffset.UTC;
private static final ZoneOffset OFFSET_1 = ZoneOffset.ofHours(6);
private static final ZonedDateTime DATE_TIME_0 = ZonedDateTime.parse("1970-01-01T00:00:00Z");
private static final ZonedDateTime DATE_TIME_1 = ZonedDateTime.parse("2015-03-14T09:26:53.59Z");
private static final TypeReference<Map<ZoneId, String>> ZONE_ID_KEY_MAP =
            new TypeReference<Map<ZoneId, String>>() { };
private static final TypeReference<Map<ZoneOffset, String>> ZONE_OFFSET_KEY_MAP =
            new TypeReference<Map<ZoneOffset, String>>() { };
private static final TypeReference<Map<ZonedDateTime, String>> ZONED_DATE_TIME_KEY_MAP =
            new TypeReference<Map<ZonedDateTime, String>>() { };
private static final byte[] ZONE_0_KEY = VPackWireFixtureTest.hex(
            "0b 0d 01 43 55 54 43 44 74 65 73 74 03");
private static final byte[] ZONE_1_KEY = VPackWireFixtureTest.hex(
            "0b 10 01 46 2b 30 36 3a 30 30 44 74 65 73 74 03");
private static final byte[] ZONE_2_KEY = VPackWireFixtureTest.hex(
            "0b 17 01 4d 45 75 72 6f 70 65 2f 4c 6f 6e 64 6f 6e 44 74 65 73 74 03");
private static final byte[] OFFSET_0_KEY = VPackWireFixtureTest.hex(
            "0b 0b 01 41 5a 44 74 65 73 74 03");
private static final byte[] OFFSET_1_KEY = VPackWireFixtureTest.hex(
            "0b 10 01 46 2b 30 36 3a 30 30 44 74 65 73 74 03");
private static final byte[] DATE_TIME_0_KEY = VPackWireFixtureTest.hex(
            "0b 1e 01 54 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 5a 44 74 65 73 74 03");
private static final byte[] DATE_TIME_1_KEY = VPackWireFixtureTest.hex(
            "0b 21 01 57 32 30 31 35 2d 30 33 2d 31 34 54 30 39 3a 32 36 3a 35 33 2e 35 39 5a 44 74 65 73 74 03");

    // Provenance: ZonedDateTimeAsKeyTest#testDeserialization0.
    void zonedDateTimeAsKeyDeserialization0Vpack() throws Exception {
        assertEquals(Collections.singletonMap(DATE_TIME_0, "test"),
                new VPackMapper().readerFor(ZONED_DATE_TIME_KEY_MAP).readValue(DATE_TIME_0_KEY));
    }

    // Provenance: ZonedDateTimeAsKeyTest#testDeserialization1.
    void zonedDateTimeAsKeyDeserialization1Vpack() throws Exception {
        assertEquals(Collections.singletonMap(DATE_TIME_1, "test"),
                new VPackMapper().readerFor(ZONED_DATE_TIME_KEY_MAP).readValue(DATE_TIME_1_KEY));
    }

    void __invoke_zonedDateTimeAsKeyDeserialization0Vpack() throws Exception {
        try {
            zonedDateTimeAsKeyDeserialization0Vpack();
        } finally {
        }
    }


    void __invoke_zonedDateTimeAsKeyDeserialization1Vpack() throws Exception {
        try {
            zonedDateTimeAsKeyDeserialization1Vpack();
        } finally {
        }
    }

}
