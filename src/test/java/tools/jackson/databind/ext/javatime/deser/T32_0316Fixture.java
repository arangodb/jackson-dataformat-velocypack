package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0316Fixture {
private static final byte[] TIMESTAMP_01 = VPackWireFixtureTest.hex(
            "13 0d 29 c2 07 31 28 11 28 0f 28 2b 05");
private static final byte[] TIMESTAMP_02 = VPackWireFixtureTest.hex(
            "13 0e 29 dd 07 38 28 15 39 28 16 28 39 06");
private static final byte[] TIMESTAMP_03 = VPackWireFixtureTest.hex(
            "13 0f 29 dd 07 38 28 15 39 28 16 30 28 39 07");
private static final byte[] TIMESTAMP_04_NANOS = VPackWireFixtureTest.hex(
            "13 12 29 d5 07 28 0b 35 28 16 28 1f 35 2a 8d a9 0c 07");
private static final byte[] TIMESTAMP_04_MILLIS = VPackWireFixtureTest.hex(
            "13 11 29 d5 07 28 0b 35 28 16 28 1f 35 29 3d 03 07");
private static final byte[] TIMESTAMP_05_MILLIS = VPackWireFixtureTest.hex(
            "13 10 29 dd 07 38 28 15 39 28 16 30 29 a1 10 07");
private static final byte[] WRAPPED_TIMESTAMP_03 = VPackWireFixtureTest.hex(
            "14 18 45 76 61 6c 75 65 13 0f 29 dd 07 38 28 15 39 28 16 30 28 39 07 01");
private static final byte[] WRAPPED_TIMESTAMP_05_MILLIS = VPackWireFixtureTest.hex(
            "14 19 45 76 61 6c 75 65 13 10 29 dd 07 38 28 15 39 28 16 30 29 a1 10 07 01");
private static final byte[] LOCAL_DATE_TIME_STRING_03 = VPackWireFixtureTest.hex(
            "5d 32 30 30 35 2d 31 31 2d 30 35 54 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37");
private static final byte[] CASE_INSENSITIVE_CANONICAL = VPackWireFixtureTest.hex(
            "51 30 31 2d 4a 61 6e 2d 32 30 30 30 20 31 33 3a 34 35");

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsString03.
    void testDeserializationAsString03Vpack() throws Exception {
        assertEquals(LocalDateTime.of(2005, 11, 5, 22, 31, 5, 829837),
                new VPackMapper().readValue(LOCAL_DATE_TIME_STRING_03,
                        LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp01.
    void testDeserializationAsTimestamp01Vpack() throws Exception {
        assertEquals(LocalDateTime.of(1986, 1, 17, 15, 43),
                new VPackMapper().readValue(TIMESTAMP_01, LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp02.
    void testDeserializationAsTimestamp02Vpack() throws Exception {
        assertEquals(LocalDateTime.of(2013, 8, 21, 9, 22, 57),
                new VPackMapper().readValue(TIMESTAMP_02, LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp03Milliseconds.
    void testDeserializationAsTimestamp03MillisecondsVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalDateTime.of(2013, 8, 21, 9, 22, 0, 57_000_000),
                reader.readValue(TIMESTAMP_03));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp03Nanoseconds.
    void testDeserializationAsTimestamp03NanosecondsVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalDateTime.of(2013, 8, 21, 9, 22, 0, 57),
                reader.readValue(TIMESTAMP_03));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp04Milliseconds01.
    void testDeserializationAsTimestamp04Milliseconds01Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalDateTime.of(2005, 11, 5, 22, 31, 5, 829837),
                reader.readValue(TIMESTAMP_04_NANOS));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp04Milliseconds02.
    void testDeserializationAsTimestamp04Milliseconds02Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalDateTime.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalDateTime.of(2005, 11, 5, 22, 31, 5, 829_000_000),
                reader.readValue(TIMESTAMP_04_MILLIS));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp04Nanoseconds.
    void testDeserializationAsTimestamp04NanosecondsVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalDateTime.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        assertEquals(LocalDateTime.of(2005, 11, 5, 22, 31, 5, 829837),
                reader.readValue(TIMESTAMP_04_NANOS));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp05Milliseconds01.
    void testDeserializationAsTimestamp05Milliseconds01Vpack() throws Exception {
        NanosDisabledWrapper wrapper = new VPackMapper().readValue(WRAPPED_TIMESTAMP_03,
                NanosDisabledWrapper.class);
        assertEquals(LocalDateTime.of(2013, 8, 21, 9, 22, 0, 57_000_000),
                wrapper.value);
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp05Milliseconds02.
    void testDeserializationAsTimestamp05Milliseconds02Vpack() throws Exception {
        NanosDisabledWrapper wrapper = new VPackMapper().readValue(
                WRAPPED_TIMESTAMP_05_MILLIS, NanosDisabledWrapper.class);
        assertEquals(LocalDateTime.of(2013, 8, 21, 9, 22, 0, 4257), wrapper.value);
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsTimestamp05Nanoseconds.
    void testDeserializationAsTimestamp05NanosecondsVpack() throws Exception {
        NanosEnabledWrapper wrapper = new VPackMapper().readValue(WRAPPED_TIMESTAMP_03,
                NanosEnabledWrapper.class);
        assertEquals(LocalDateTime.of(2013, 8, 21, 9, 22, 0, 57), wrapper.value);
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationCaseInsensitiveDisabled.
    void testDeserializationCaseInsensitiveDisabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false)
                .withConfigOverride(LocalDateTime.class, o -> o.setFormat(
                        JsonFormat.Value.forPattern("dd-MMM-yyyy HH:mm")))
                .build();
        assertEquals(LocalDateTime.of(2000, 1, 1, 13, 45),
                mapper.readValue(CASE_INSENSITIVE_CANONICAL, LocalDateTime.class));
    }
static final class NanosEnabledWrapper {
        @JsonFormat(with = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public LocalDateTime value;

        public NanosEnabledWrapper() { }
    }
static final class NanosDisabledWrapper {
        @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public LocalDateTime value;

        public NanosDisabledWrapper() { }
    }

    void __invoke_testDeserializationAsString03Vpack() throws Exception {
        try {
            testDeserializationAsString03Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp01Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp02Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp03MillisecondsVpack() throws Exception {
        try {
            testDeserializationAsTimestamp03MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp03NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsTimestamp03NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp04Milliseconds01Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp04Milliseconds01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp04Milliseconds02Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp04Milliseconds02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp04NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsTimestamp04NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp05Milliseconds01Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp05Milliseconds01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp05Milliseconds02Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp05Milliseconds02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp05NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsTimestamp05NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationCaseInsensitiveDisabledVpack() throws Exception {
        try {
            testDeserializationCaseInsensitiveDisabledVpack();
        } finally {
        }
    }

}
