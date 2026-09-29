package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0328Fixture {
private static final byte[] STRING_02 = VPackWireFixtureTest.hex(
            "63 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 3a 30 30");
private static final byte[] STRING_03 = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");
private static final byte[] STRING_03_COLONLESS = VPackWireFixtureTest.hex(
            "62 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 30 30" );
private static final byte[] TYPE_INFO_01 = VPackWireFixtureTest.hex(
            "06 2d 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22 03 1c");
private static final byte[] TYPE_INFO_02 = VPackWireFixtureTest.hex(
            "06 23 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "2b 15 cd 5b 07 03 1c");
private static final byte[] TYPE_INFO_03 = VPackWireFixtureTest.hex(
            "06 27 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "2f ae 1b 99 be 1c 00 00 00 03 1c");
private static final byte[] TYPE_INFO_MIN = VPackWireFixtureTest.hex(
            "06 3e 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "5f 2d 39 39 39 39 39 39 39 39 39 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 2b 31 38 3a 30 30 03 1c");
private static final byte[] TYPE_INFO_MAX = VPackWireFixtureTest.hex(
            "06 48 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "69 2b 39 39 39 39 39 39 39 39 39 2d 31 32 2d 33 31 54 32 33 3a 35 39 3a 35 39 2e 39 39 39 39 39 39 39 39 39 2d 31 38 3a 30 30 03 1c");
private static final OffsetDateTime DATE_02 = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L, 183917322L),
            ZoneId.of("America/Anchorage"));
private static final OffsetDateTime DATE_03 = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(1603365380L, 123456789L),
            ZoneId.of("America/Los_Angeles"));
private static final OffsetDateTime DATE_02_SECONDS = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L), ZoneId.of("America/Anchorage"));

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString02WithoutTimeZone.
    void testDeserializationAsString02WithoutTimeZoneVpack() throws Exception {
        OffsetDateTime value = new VPackMapper().readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_02);

        assertTimestamp(DATE_02.toInstant(), ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString03WithTimeZone.
    void testDeserializationAsString03WithTimeZoneVpack() throws Exception {
        OffsetDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_03);

        assertTimestamp(DATE_03.toInstant(), defaultOffset(DATE_03), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString03WithTimeZoneColonless.
    void testDeserializationAsString03WithTimeZoneColonlessVpack() throws Exception {
        OffsetDateTime value = new VPackMapper().readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_03_COLONLESS);

        assertTimestamp(DATE_03.toInstant(), DATE_03.getOffset(), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString03WithTimeZoneTurnedOff.
    void testDeserializationAsString03WithTimeZoneTurnedOffVpack() throws Exception {
        OffsetDateTime value = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getDefault())
                .build()
                .readerFor(OffsetDateTime.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_03);

        assertTimestamp(DATE_03.toInstant(), DATE_03.getOffset(), value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationAsString03WithoutTimeZone.
    void testDeserializationAsString03WithoutTimeZoneVpack() throws Exception {
        OffsetDateTime value = new VPackMapper().readerFor(OffsetDateTime.class)
                .with(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(STRING_03);

        assertTimestamp(DATE_03.toInstant(), ZoneOffset.UTC, value);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationNoAdjustIfMAX.
    void testDeserializationNoAdjustIfMAXVpack() throws Exception {
        Temporal value = extremaMapper().readValue(TYPE_INFO_MAX, Temporal.class);

        assertNotNull(value);
        OffsetDateTime actual = assertInstanceOf(OffsetDateTime.class, value);
        assertTimestamp(OffsetDateTime.MAX.toInstant(), OffsetDateTime.MAX.getOffset(), actual);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationNoAdjustIfMIN.
    void testDeserializationNoAdjustIfMINVpack() throws Exception {
        Temporal value = extremaMapper().readValue(TYPE_INFO_MIN, Temporal.class);

        assertNotNull(value);
        OffsetDateTime actual = assertInstanceOf(OffsetDateTime.class, value);
        assertTimestamp(OffsetDateTime.MIN.toInstant(), OffsetDateTime.MIN.getOffset(), actual);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationWithTypeInfo01WithTimeZone.
    void testDeserializationWithTypeInfo01WithTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(TimeZone.getDefault()).readValue(TYPE_INFO_01,
                Temporal.class);

        assertNotNull(value);
        OffsetDateTime actual = assertInstanceOf(OffsetDateTime.class, value);
        assertTimestamp(DATE_02.toInstant(), defaultOffset(DATE_02), actual);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationWithTypeInfo01WithoutTimeZone.
    void testDeserializationWithTypeInfo01WithoutTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(null).readValue(TYPE_INFO_01, Temporal.class);

        assertNotNull(value);
        OffsetDateTime actual = assertInstanceOf(OffsetDateTime.class, value);
        assertTimestamp(DATE_02.toInstant(), ZoneOffset.UTC, actual);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationWithTypeInfo02WithTimeZone.
    void testDeserializationWithTypeInfo02WithTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(TimeZone.getDefault()).readerFor(Temporal.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_02);

        assertNotNull(value);
        OffsetDateTime actual = assertInstanceOf(OffsetDateTime.class, value);
        assertTimestamp(Instant.ofEpochSecond(123456789L),
                defaultOffset(DATE_02_SECONDS), actual);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationWithTypeInfo02WithoutTimeZone.
    void testDeserializationWithTypeInfo02WithoutTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(null).readerFor(Temporal.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_02);

        assertNotNull(value);
        OffsetDateTime actual = assertInstanceOf(OffsetDateTime.class, value);
        assertTimestamp(Instant.ofEpochSecond(123456789L), ZoneOffset.UTC, actual);
    }

    // Provenance: OffsetDateTimeDeserTest#testDeserializationWithTypeInfo03WithTimeZone.
    void testDeserializationWithTypeInfo03WithTimeZoneVpack() throws Exception {
        Temporal value = typeInfoMapper(TimeZone.getDefault()).readerFor(Temporal.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_03);

        assertNotNull(value);
        OffsetDateTime actual = assertInstanceOf(OffsetDateTime.class, value);
        Instant instant = Instant.ofEpochSecond(123456789L, 422_000_000L);
        assertTimestamp(instant, defaultOffset(DATE_02), actual);
    }
private static ObjectMapper typeInfoMapper(TimeZone timeZone) {
        VPackMapper.Builder builder = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class);
        if (timeZone != null) {
            builder.defaultTimeZone(timeZone);
        }
        return builder.build();
    }
private static ObjectMapper extremaMapper() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .defaultTimeZone(TimeZone.getTimeZone("America/Chicago"))
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
    }
private static void assertTimestamp(Instant expected, ZoneOffset expectedOffset,
            OffsetDateTime actual) {
        assertTrue(expected.equals(actual.toInstant()),
                "The value is not correct. Expected instant <" + expected
                        + ">, actual <" + actual + ">");
        assertEquals(expectedOffset, actual.getOffset(), "The time zone is not correct.");
    }
private static ZoneOffset defaultOffset(OffsetDateTime date) {
        return ZoneId.systemDefault().getRules().getOffset(date.toLocalDateTime());
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testDeserializationAsString02WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString02WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString03WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03WithTimeZoneColonlessVpack() throws Exception {
        try {
            testDeserializationAsString03WithTimeZoneColonlessVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03WithTimeZoneTurnedOffVpack() throws Exception {
        try {
            testDeserializationAsString03WithTimeZoneTurnedOffVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsString03WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationNoAdjustIfMAXVpack() throws Exception {
        try {
            testDeserializationNoAdjustIfMAXVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationNoAdjustIfMINVpack() throws Exception {
        try {
            testDeserializationNoAdjustIfMINVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo02WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo02WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo02WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo02WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo03WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo03WithTimeZoneVpack();
        } finally {
        }
    }

}
