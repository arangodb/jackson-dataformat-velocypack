package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.ser.ZonedDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0364Fixture {
private static final ZoneId UTC = ZoneOffset.UTC;
private static final ZoneId Z1 = ZoneId.of("America/Chicago");
private static final ZoneId Z2 = ZoneId.of("America/Anchorage");
private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");
private static final ZonedDateTime DATE_01 = ZonedDateTime.ofInstant(Instant.EPOCH, Z1);
private static final ZonedDateTime FRACTIONAL_DATE = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L, 183917322L), Z2);
private static final ZonedDateTime DETERMINISTIC_DATE = ZonedDateTime.of(
            2020, 6, 7, 8, 9, 10, 123456789,
            Z3.getRules().getOffset(Instant.parse("2020-06-07T15:09:10Z")));
private static final ZonedDateTime ZONE_ID_DATE = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(1603365380L, 123456789L), Z3);
private static final byte[] TIMESTAMP_01_NANOS = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff ff 00");
private static final byte[] TIMESTAMP_01_MILLIS = VPackWireFixtureTest.hex("30");
private static final byte[] TIMESTAMP_01_NEGATIVE_NANOS = VPackWireFixtureTest.hex(
            "d0 0a f7 ff ff ff 14 15 90 19 99 98 16 08 26 78");
private static final byte[] TIMESTAMP_01_NEGATIVE_DEFAULT = VPackWireFixtureTest.hex(
            "58 31 39 36 39 2d 30 34 2d 31 33 54 30 35 3a 30 35 3a 33 38 2e 35 39 39 5a");
private static final byte[] MAPPER_TIME_ZONE_01 = VPackWireFixtureTest.hex(
            "59 31 39 36 39 2d 31 32 2d 33 31 54 31 38 3a 30 30 3a 30 30 2d 30 36 3a 30 30");
private static final byte[] MAPPER_TIME_ZONE_02 = VPackWireFixtureTest.hex(
            "63 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 3a 30 30");
private static final byte[] MAPPER_TIME_ZONE_03 = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 30 36 2d 30 37 54 30 38 3a 30 39 3a 31 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");
private static final byte[] ZONE_ID_OFF = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");
private static final byte[] ZONE_ID_ON = VPackWireFixtureTest.hex(
            "78 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30 5b 41 6d 65 72 69 63 61 2f 4c 6f 73 5f 41 6e 67 65 6c 65 73 5d");
private static final byte[] CUSTOM_FORMATTER_CONTEXT_ON = VPackWireFixtureTest.hex(
            "53 32 30 32 30 2d 31 30 2d 32 32 54 30 33 3a 31 36 3a 32 30");

    // Provenance: ZonedDateTimeSerTest#testSerializationAsTimestamp01Nanoseconds.
    void testSerializationAsTimestamp01NanosecondsVpack() throws Exception {
        assertArrayEquals(TIMESTAMP_01_NANOS,
                timestampMapper(true).writeValueAsBytes(DATE_01));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsTimestamp01NegativeSeconds.
    void testSerializationAsTimestamp01NegativeSecondsVpack() throws Exception {
        ZonedDateTime date = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(-14159020000L, 183917322L), UTC);
        ObjectMapper mapper = timestampMapper(true);

        assertArrayEquals(TIMESTAMP_01_NEGATIVE_NANOS,
                mapper.writeValueAsBytes(date));
        assertEquals(date, mapper.readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TIMESTAMP_01_NEGATIVE_NANOS));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsTimestamp01NegativeSecondsWithDefaults.
    void testSerializationAsTimestamp01NegativeSecondsWithDefaultsVpack() throws Exception {
        ZonedDateTime original = ZonedDateTime.parse(
                "Apr 13 1969 05:05:38.599 UTC",
                DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss.SSS zzz", Locale.ENGLISH));
        ObjectMapper mapper = new VPackMapper();

        assertArrayEquals(TIMESTAMP_01_NEGATIVE_DEFAULT,
                mapper.writeValueAsBytes(original));
        ZonedDateTime deserialized = mapper.readValue(TIMESTAMP_01_NEGATIVE_DEFAULT,
                ZonedDateTime.class);
        assertEquals(original.getDayOfMonth(), deserialized.getDayOfMonth());
        assertEquals(original.getMonthValue(), deserialized.getMonthValue());
        assertEquals(original.getYear(), deserialized.getYear());
        assertEquals(original.getHour(), deserialized.getHour());
        assertEquals(original.getMinute(), deserialized.getMinute());
        assertEquals(original.getSecond(), deserialized.getSecond());
        assertEquals(original.getNano(), deserialized.getNano());
        assertEquals(original.getZone().getRules(), deserialized.getZone().getRules());
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsTimestamp01Milliseconds.
    void testSerializationAsTimestamp01MillisecondsVpack() throws Exception {
        assertArrayEquals(TIMESTAMP_01_MILLIS,
                timestampMapper(false).writeValueAsBytes(DATE_01));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithMapperTimeZone01.
    void testSerializationAsStringWithMapperTimeZone01Vpack() throws Exception {
        assertArrayEquals(MAPPER_TIME_ZONE_01,
                stringMapper().writer().with(TimeZone.getTimeZone(Z1))
                        .writeValueAsBytes(DATE_01));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithMapperTimeZone02.
    void testSerializationAsStringWithMapperTimeZone02Vpack() throws Exception {
        assertArrayEquals(MAPPER_TIME_ZONE_02,
                stringMapper().writer().with(TimeZone.getTimeZone(Z2))
                        .writeValueAsBytes(FRACTIONAL_DATE));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithMapperTimeZone03.
    void testSerializationAsStringWithMapperTimeZone03Vpack() throws Exception {
        assertArrayEquals(MAPPER_TIME_ZONE_03,
                stringMapper().writer().with(TimeZone.getTimeZone(Z3))
                        .writeValueAsBytes(DETERMINISTIC_DATE));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithZoneIdOff.
    void testSerializationAsStringWithZoneIdOffVpack() throws Exception {
        assertArrayEquals(ZONE_ID_OFF,
                stringMapper().writer()
                        .without(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .writeValueAsBytes(ZONE_ID_DATE));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithZoneIdOffAndMapperTimeZone.
    void testSerializationAsStringWithZoneIdOffAndMapperTimeZoneVpack() throws Exception {
        assertArrayEquals(ZONE_ID_OFF,
                stringMapper().writer()
                        .with(TimeZone.getTimeZone(Z3))
                        .without(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .writeValueAsBytes(ZONE_ID_DATE));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithZoneIdOn.
    void testSerializationAsStringWithZoneIdOnVpack() throws Exception {
        assertArrayEquals(ZONE_ID_ON,
                stringMapper().writer()
                        .with(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .writeValueAsBytes(ZONE_ID_DATE));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnAndACustomFormatter.
    void testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnAndACustomFormatterVpack()
            throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().addSerializer(new ZonedDateTimeSerializer(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        assertArrayEquals(CUSTOM_FORMATTER_CONTEXT_ON,
                mapper.writer()
                        .with(TimeZone.getTimeZone(Z2))
                        .without(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .with(DateTimeFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                        .writeValueAsBytes(ZONE_ID_DATE));
    }

    // Provenance: ZonedDateTimeSerTest#testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOn.
    void testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnVpack()
            throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "63 32 30 32 30 2d 31 30 2d 32 32 54 30 33 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 38 3a 30 30");
        assertArrayEquals(expected,
                stringMapper().writer()
                        .with(TimeZone.getTimeZone(Z2))
                        .without(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                        .with(DateTimeFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                        .writeValueAsBytes(ZONE_ID_DATE));
    }
private static ObjectMapper timestampMapper(boolean nanos) {
        var builder = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (nanos) {
            builder.enable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        } else {
            builder.disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        }
        return builder.build();
    }
private static ObjectMapper stringMapper() {
        return VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    void __invoke_testSerializationAsTimestamp01NanosecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp01NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestamp01NegativeSecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp01NegativeSecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestamp01NegativeSecondsWithDefaultsVpack() throws Exception {
        try {
            testSerializationAsTimestamp01NegativeSecondsWithDefaultsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTimestamp01MillisecondsVpack() throws Exception {
        try {
            testSerializationAsTimestamp01MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithMapperTimeZone01Vpack() throws Exception {
        try {
            testSerializationAsStringWithMapperTimeZone01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithMapperTimeZone02Vpack() throws Exception {
        try {
            testSerializationAsStringWithMapperTimeZone02Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithMapperTimeZone03Vpack() throws Exception {
        try {
            testSerializationAsStringWithMapperTimeZone03Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithZoneIdOffVpack() throws Exception {
        try {
            testSerializationAsStringWithZoneIdOffVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithZoneIdOffAndMapperTimeZoneVpack() throws Exception {
        try {
            testSerializationAsStringWithZoneIdOffAndMapperTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithZoneIdOnVpack() throws Exception {
        try {
            testSerializationAsStringWithZoneIdOnVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnAndACustomFormatterVpack() throws Exception {
        try {
            testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnAndACustomFormatterVpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnVpack() throws Exception {
        try {
            testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOnVpack();
        } finally {
        }
    }

}
