package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDateTime;
import java.util.TimeZone;

import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0319F0 {
private static final byte[] TIME_TIMESTAMP_01 = VPackWireFixtureTest.hex(
            "13 07 28 0f 28 2b 02");
private static final byte[] TIME_TIMESTAMP_02 = VPackWireFixtureTest.hex(
            "13 08 39 28 16 28 39 03");
private static final byte[] TIME_TIMESTAMP_03 = VPackWireFixtureTest.hex(
            "13 09 39 28 16 30 28 39 04");
private static final byte[] TIME_TIMESTAMP_04_NANOS = VPackWireFixtureTest.hex(
            "13 0c 28 16 28 1f 35 2a 8d a9 0c 04");
private static final byte[] TIME_TIMESTAMP_04_MILLIS = VPackWireFixtureTest.hex(
            "13 0b 28 16 28 1f 35 29 3d 03 04");
private static final byte[] SINGLE_TIME_STRING = VPackWireFixtureTest.hex(
            "13 09 45 31 32 3a 30 30 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] INVALID_LOCAL_TIME = VPackWireFixtureTest.hex(
            "4c 6e 6f 74 61 6c 6f 63 61 6c 74 69 6d 65");
private static final byte[] ZULU_2020_10_22_04 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] ZULU_2020_10_25_00 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 30 2d 32 35 54 30 30 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] ZULU_2020_10_25_01 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 30 2d 32 35 54 30 31 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] ZULU_2020_11_01_06 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 31 2d 30 31 54 30 36 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] ZULU_2020_11_01_07 = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 31 2d 30 31 54 30 37 3a 31 36 3a 32 30 2e 35 30 34 5a");

    // Provenance: LocalDateTimeDeserTest#testUseTimeZoneForZuluIfEnabled.
    void testUseTimeZoneForZuluIfEnabledVpack() throws Exception {
        assertZulu(TimeZone.getTimeZone("UTC"), ZULU_2020_10_22_04,
                LocalDateTime.of(2020, 10, 22, 4, 16, 20, 504_000_000));
        assertZulu(TimeZone.getTimeZone("Europe/Budapest"), ZULU_2020_10_22_04,
                LocalDateTime.of(2020, 10, 22, 6, 16, 20, 504_000_000));
        assertZulu(TimeZone.getTimeZone("Europe/Budapest"), ZULU_2020_10_25_00,
                LocalDateTime.of(2020, 10, 25, 2, 16, 20, 504_000_000));
        assertZulu(TimeZone.getTimeZone("Europe/Budapest"), ZULU_2020_10_25_01,
                LocalDateTime.of(2020, 10, 25, 2, 16, 20, 504_000_000));
        assertZulu(TimeZone.getTimeZone("America/Chicago"), ZULU_2020_10_22_04,
                LocalDateTime.of(2020, 10, 21, 23, 16, 20, 504_000_000));
        assertZulu(TimeZone.getTimeZone("America/Chicago"), ZULU_2020_11_01_06,
                LocalDateTime.of(2020, 11, 1, 1, 16, 20, 504_000_000));
        assertZulu(TimeZone.getTimeZone("America/Chicago"), ZULU_2020_11_01_07,
                LocalDateTime.of(2020, 11, 1, 1, 16, 20, 504_000_000));
    }
private static void assertZulu(TimeZone zone, byte[] input, LocalDateTime expected)
            throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .enable(DateTimeFeature.USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING)
                .build()
                .readerFor(LocalDateTime.class)
                .with(zone);
        assertEquals(expected, reader.readValue(input));
    }

    void __invoke_testUseTimeZoneForZuluIfEnabledVpack() throws Exception {
        try {
            testUseTimeZoneForZuluIfEnabledVpack();
        } finally {
        }
    }

}
