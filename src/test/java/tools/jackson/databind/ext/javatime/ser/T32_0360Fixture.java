package tools.jackson.databind.ext.javatime.ser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0360Fixture {
private static final ZoneId DEFAULT_TZ = ZoneOffset.UTC;
private static final byte[] CUSTOM_PATTERN = VPackWireFixtureTest.hex(
            "0b 25 01 45 76 61 6c 75 65 5a 31 39 37 30 5f 30 31 5f 30 31 20 30 30 3a 30 30 3a 30 30 28 2b 30 30 30 30 29 03");
private static final byte[] CUSTOM_NUMERIC_TIMESTAMP = VPackWireFixtureTest.hex(
            "0b 16 01 45 76 61 6c 75 65 4b 33 2e 31 34 31 35 39 32 36 35 33 03");
private static final byte[] FLOAT_01 = VPackWireFixtureTest.hex(
            "c8 01 f7 ff ff ff 00");
private static final byte[] FLOAT_02 = VPackWireFixtureTest.hex(
            "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22");
private static final byte[] FLOAT_03 = VPackWireFixtureTest.hex(
            "c8 09 f7 ff ff ff 98 76 54 32 11 23 45 67 89");
private static final byte[] INT_01 = VPackWireFixtureTest.hex("30");

    // Provenance: ZonedDateTimeSerTest#testCustomPatternWithAnnotations.
    void testCustomPatternWithAnnotationsVpack() throws Exception {
        ZonedDateTime inputValue = ZonedDateTime.ofInstant(Instant.EPOCH, DEFAULT_TZ);
        Wrapper input = new Wrapper(inputValue);

        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(CUSTOM_PATTERN, mapper.writeValueAsBytes(input));

        Wrapper result = mapper.readerFor(Wrapper.class)
                .without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(CUSTOM_PATTERN);
        assertEquals(input.value.toInstant(), result.value.toInstant());
    }

    // Provenance: ZonedDateTimeSerTest#testCustomPatternWithNumericTimestamp.
    void testCustomPatternWithNumericTimestampVpack() throws Exception {
        Wrapper result = VPackMapper.builder()
                .enable(DateTimeFeature.ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS)
                .build()
                .readValue(CUSTOM_NUMERIC_TIMESTAMP, Wrapper.class);

        assertEquals(Instant.ofEpochSecond(3L, 141592653L), result.value.toInstant());
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsFloat01WithTimeZone.
    void testDeserializationAsFloat01WithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(TimeZone.getDefault())
                .readValue(FLOAT_01);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsFloat01WithoutTimeZone.
    void testDeserializationAsFloat01WithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readValue(FLOAT_01, ZonedDateTime.class);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsFloat02WithTimeZone.
    void testDeserializationAsFloat02WithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(TimeZone.getDefault())
                .readValue(FLOAT_02);

        assertEquals(Instant.ofEpochSecond(123456789L, 183917322L), value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsFloat02WithoutTimeZone.
    void testDeserializationAsFloat02WithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readValue(FLOAT_02, ZonedDateTime.class);

        assertEquals(Instant.ofEpochSecond(123456789L, 183917322L), value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsFloat03WithTimeZone.
    void testDeserializationAsFloat03WithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(TimeZone.getDefault())
                .readValue(FLOAT_03);

        assertEquals(Instant.ofEpochSecond(987654321L, 123456789L), value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsFloat03WithoutTimeZone.
    void testDeserializationAsFloat03WithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readValue(FLOAT_03, ZonedDateTime.class);

        assertEquals(Instant.ofEpochSecond(987654321L, 123456789L), value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt01MillisecondsWithTimeZone.
    void testDeserializationAsInt01MillisecondsWithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .with(TimeZone.getDefault())
                .readValue(INT_01);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt01MillisecondsWithoutTimeZone.
    void testDeserializationAsInt01MillisecondsWithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(INT_01);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt01NanosecondsWithTimeZone.
    void testDeserializationAsInt01NanosecondsWithTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .with(TimeZone.getDefault())
                .readValue(INT_01);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(),
                "The time zone is not correct.");
    }

    // Provenance: ZonedDateTimeSerTest#testDeserializationAsInt01NanosecondsWithoutTimeZone.
    void testDeserializationAsInt01NanosecondsWithoutTimeZoneVpack() throws Exception {
        ZonedDateTime value = new VPackMapper().readerFor(ZonedDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(INT_01);

        assertEquals(Instant.EPOCH, value.toInstant());
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }
private static final class Wrapper {
        @JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss(Z)", shape = JsonFormat.Shape.STRING)
        public ZonedDateTime value;

        public Wrapper() { }

        Wrapper(ZonedDateTime value) {
            this.value = value;
        }
    }

    void __invoke_testCustomPatternWithAnnotationsVpack() throws Exception {
        try {
            testCustomPatternWithAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testCustomPatternWithNumericTimestampVpack() throws Exception {
        try {
            testCustomPatternWithNumericTimestampVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat01WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat01WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat01WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat01WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat02WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat02WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat02WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat02WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat03WithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat03WithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat03WithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsFloat03WithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01MillisecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt01MillisecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01MillisecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt01MillisecondsWithoutTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01NanosecondsWithTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt01NanosecondsWithTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01NanosecondsWithoutTimeZoneVpack() throws Exception {
        try {
            testDeserializationAsInt01NanosecondsWithoutTimeZoneVpack();
        } finally {
        }
    }

}
