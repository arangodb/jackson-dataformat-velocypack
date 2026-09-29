package tools.jackson.databind.ext.javatime.key;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0340F0 {
private static final OffsetDateTime DATE_TIME_0 = OffsetDateTime.ofInstant(
            java.time.Instant.ofEpochSecond(0), ZoneOffset.UTC);
private static final OffsetDateTime DATE_TIME_1 = OffsetDateTime.of(
            2015, 3, 14, 9, 26, 53, 590 * 1_000_000, ZoneOffset.UTC);
private static final OffsetDateTime DATE_TIME_2 = OffsetDateTime.of(
            2015, 3, 14, 9, 26, 53, 590 * 1_000_000, ZoneOffset.ofHours(6));
private static final OffsetTime TIME_0 = OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC);
private static final OffsetTime TIME_1 = OffsetTime.of(
            3, 14, 15, 920 * 1_000_000, ZoneOffset.UTC);
private static final OffsetTime TIME_2 = OffsetTime.of(
            3, 14, 15, 920 * 1_000_000, ZoneOffset.ofHours(6));
private static final TypeReference<Map<OffsetDateTime, String>> DATE_TIME_KEY_MAP =
            new TypeReference<Map<OffsetDateTime, String>>() { };
private static final TypeReference<Map<OffsetTime, String>> TIME_KEY_MAP =
            new TypeReference<Map<OffsetTime, String>>() { };
private static final byte[] DATE_TIME_0_KEY = VPackWireFixtureTest.hex(
            "0b 1b 01 51 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 5a"
                    + "44 74 65 73 74 03");
private static final byte[] DATE_TIME_1_KEY = VPackWireFixtureTest.hex(
            "0b 22 01 58 32 30 31 35 2d 30 33 2d 31 34 54 30 39 3a 32 36 3a 35 33 2e 35 39 30 5a"
                    + "44 74 65 73 74 03");
private static final byte[] DATE_TIME_2_KEY = VPackWireFixtureTest.hex(
            "0b 27 01 5d 32 30 31 35 2d 30 33 2d 31 34 54 30 39 3a 32 36 3a 35 33 2e 35 39 30"
                    + "2b 30 36 3a 30 30 44 74 65 73 74 03");
private static final byte[] TIME_0_KEY = VPackWireFixtureTest.hex(
            "0b 10 01 46 30 30 3a 30 30 5a 44 74 65 73 74 03");
private static final byte[] TIME_1_KEY = VPackWireFixtureTest.hex(
            "0b 17 01 4d 30 33 3a 31 34 3a 31 35 2e 39 32 30 5a 44 74 65 73 74 03");
private static final byte[] TIME_2_KEY = VPackWireFixtureTest.hex(
            "0b 1c 01 52 30 33 3a 31 34 3a 31 35 2e 39 32 30 2b 30 36 3a 30 30 44 74 65 73 74 03");

    // Provenance: OffsetDateTimeAsKeyTest#testSerialization0.
    void offsetDateTimeAsKeySerialization0Vpack() throws Exception {
        assertArrayEquals(DATE_TIME_0_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(DATE_TIME_0, "test")));
    }

    // Provenance: OffsetDateTimeAsKeyTest#testSerialization1.
    void offsetDateTimeAsKeySerialization1Vpack() throws Exception {
        assertArrayEquals(DATE_TIME_1_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(DATE_TIME_1, "test")));
    }

    // Provenance: OffsetDateTimeAsKeyTest#testSerialization2.
    void offsetDateTimeAsKeySerialization2Vpack() throws Exception {
        assertArrayEquals(DATE_TIME_2_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(DATE_TIME_2, "test")));
    }

    // Provenance: OffsetDateTimeAsKeyTest#testDeserialization0.
    void offsetDateTimeAsKeyDeserialization0Vpack() throws Exception {
        assertEquals(Collections.singletonMap(DATE_TIME_0, "test"),
                new VPackMapper().readerFor(DATE_TIME_KEY_MAP).readValue(DATE_TIME_0_KEY));
    }

    // Provenance: OffsetDateTimeAsKeyTest#testDeserialization1.
    void offsetDateTimeAsKeyDeserialization1Vpack() throws Exception {
        assertEquals(Collections.singletonMap(DATE_TIME_1, "test"),
                new VPackMapper().readerFor(DATE_TIME_KEY_MAP).readValue(DATE_TIME_1_KEY));
    }

    // Provenance: OffsetDateTimeAsKeyTest#testDeserialization2.
    void offsetDateTimeAsKeyDeserialization2Vpack() throws Exception {
        assertEquals(Collections.singletonMap(DATE_TIME_2, "test"),
                new VPackMapper().readerFor(DATE_TIME_KEY_MAP).readValue(DATE_TIME_2_KEY));
    }

    void __invoke_offsetDateTimeAsKeySerialization0Vpack() throws Exception {
        try {
            offsetDateTimeAsKeySerialization0Vpack();
        } finally {
        }
    }


    void __invoke_offsetDateTimeAsKeySerialization1Vpack() throws Exception {
        try {
            offsetDateTimeAsKeySerialization1Vpack();
        } finally {
        }
    }


    void __invoke_offsetDateTimeAsKeySerialization2Vpack() throws Exception {
        try {
            offsetDateTimeAsKeySerialization2Vpack();
        } finally {
        }
    }


    void __invoke_offsetDateTimeAsKeyDeserialization0Vpack() throws Exception {
        try {
            offsetDateTimeAsKeyDeserialization0Vpack();
        } finally {
        }
    }


    void __invoke_offsetDateTimeAsKeyDeserialization1Vpack() throws Exception {
        try {
            offsetDateTimeAsKeyDeserialization1Vpack();
        } finally {
        }
    }


    void __invoke_offsetDateTimeAsKeyDeserialization2Vpack() throws Exception {
        try {
            offsetDateTimeAsKeyDeserialization2Vpack();
        } finally {
        }
    }

}
