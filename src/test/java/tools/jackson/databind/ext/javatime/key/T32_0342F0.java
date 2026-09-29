package tools.jackson.databind.ext.javatime.key;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0342F0 {
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

    // Provenance: ZoneIdAsKeyTest#testSerialization0.
    void zoneIdAsKeySerialization0Vpack() throws Exception {
        assertArrayEquals(ZONE_0_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(ZONE_0, "test")));
    }

    // Provenance: ZoneIdAsKeyTest#testSerialization1.
    void zoneIdAsKeySerialization1Vpack() throws Exception {
        assertArrayEquals(ZONE_1_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(ZONE_1, "test")));
    }

    // Provenance: ZoneIdAsKeyTest#testSerialization2.
    void zoneIdAsKeySerialization2Vpack() throws Exception {
        assertArrayEquals(ZONE_2_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(ZONE_2, "test")));
    }

    // Provenance: ZoneIdAsKeyTest#testDeserialization0.
    void zoneIdAsKeyDeserialization0Vpack() throws Exception {
        assertEquals(Collections.singletonMap(ZONE_0, "test"),
                new VPackMapper().readerFor(ZONE_ID_KEY_MAP).readValue(ZONE_0_KEY));
    }

    // Provenance: ZoneIdAsKeyTest#testDeserialization1.
    void zoneIdAsKeyDeserialization1Vpack() throws Exception {
        assertEquals(Collections.singletonMap(ZONE_1, "test"),
                new VPackMapper().readerFor(ZONE_ID_KEY_MAP).readValue(ZONE_1_KEY));
    }

    // Provenance: ZoneIdAsKeyTest#testDeserialization2.
    void zoneIdAsKeyDeserialization2Vpack() throws Exception {
        assertEquals(Collections.singletonMap(ZONE_2, "test"),
                new VPackMapper().readerFor(ZONE_ID_KEY_MAP).readValue(ZONE_2_KEY));
    }

    void __invoke_zoneIdAsKeySerialization0Vpack() throws Exception {
        try {
            zoneIdAsKeySerialization0Vpack();
        } finally {
        }
    }


    void __invoke_zoneIdAsKeySerialization1Vpack() throws Exception {
        try {
            zoneIdAsKeySerialization1Vpack();
        } finally {
        }
    }


    void __invoke_zoneIdAsKeySerialization2Vpack() throws Exception {
        try {
            zoneIdAsKeySerialization2Vpack();
        } finally {
        }
    }


    void __invoke_zoneIdAsKeyDeserialization0Vpack() throws Exception {
        try {
            zoneIdAsKeyDeserialization0Vpack();
        } finally {
        }
    }


    void __invoke_zoneIdAsKeyDeserialization1Vpack() throws Exception {
        try {
            zoneIdAsKeyDeserialization1Vpack();
        } finally {
        }
    }


    void __invoke_zoneIdAsKeyDeserialization2Vpack() throws Exception {
        try {
            zoneIdAsKeyDeserialization2Vpack();
        } finally {
        }
    }

}
