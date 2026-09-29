package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0309Fixture {
private static final Instant WHOLE_SECOND = Instant.ofEpochSecond(123456789L);
private static final Instant WITH_NANOS = Instant.ofEpochSecond(123456789L, 183917322L);
private static final Instant WITH_MILLIS = Instant.ofEpochSecond(123456789L, 422000000L);
private static final Instant OFFSET_BASE = Instant.parse("2024-05-06T07:08:09.123456789Z");
private static final byte[] NANOSECONDS_WHOLE_SECOND = VPackWireFixtureTest.hex(
            "2b 15 cd 5b 07");
private static final byte[] NANOSECONDS_WRAPPER = VPackWireFixtureTest.hex(
            "14 0e 45 76 61 6c 75 65 2b 15 cd 5b 07 01");
private static final byte[] MILLISECONDS_WRAPPER = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 2f ae 1b 99 be 1c 00 00 00 01");
private static final byte[] STRING_EPOCH = VPackWireFixtureTest.hex(
            "54 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 5a");
private static final byte[] STRING_WITH_NANOS = VPackWireFixtureTest.hex(
            "5e 31 39 37 33 2d 31 31 2d 32 39 54 32 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 5a");
private static final byte[] STRING_FIXED_INSTANT = VPackWireFixtureTest.hex(
            "5e 32 30 32 34 2d 30 35 2d 30 36 54 30 37 3a 30 38 3a 30 39 2e 31 32 33 34 35 36 37 38 39 5a");
private static final byte[] DECIMAL_TIMESTAMP = VPackWireFixtureTest.hex(
            "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22");
private static final byte[] STRINGIFIED_DECIMAL_TIMESTAMP = VPackWireFixtureTest.hex(
            "53 31 32 33 34 35 36 37 38 39 2e 31 38 33 39 31 37 33 32 32");
private static final byte[] OFFSET_PLUS_COLON = VPackWireFixtureTest.hex(
            "63 32 30 32 34 2d 30 35 2d 30 36 54 30 37 3a 30 38 3a 30 39 2e 31 32 33 34 35 36 37 38 39 2b 30 30 3a 30 30");
private static final byte[] OFFSET_PLUS_COMPACT = VPackWireFixtureTest.hex(
            "62 32 30 32 34 2d 30 35 2d 30 36 54 30 37 3a 30 38 3a 30 39 2e 31 32 33 34 35 36 37 38 39 2b 30 30 30 30");
private static final byte[] OFFSET_PLUS_HOUR = VPackWireFixtureTest.hex(
            "60 32 30 32 34 2d 30 35 2d 30 36 54 30 37 3a 30 38 3a 30 39 2e 31 32 33 34 35 36 37 38 39 2b 30 30");
private static final byte[] OFFSET_PLUS_HALF_HOUR = VPackWireFixtureTest.hex(
            "63 32 30 32 34 2d 30 35 2d 30 36 54 30 37 3a 30 38 3a 30 39 2e 31 32 33 34 35 36 37 38 39 2b 30 30 3a 33 30");
private static final byte[] OFFSET_PLUS_ONE_HALF_HOURS = VPackWireFixtureTest.hex(
            "63 32 30 32 34 2d 30 35 2d 30 36 54 30 37 3a 30 38 3a 30 39 2e 31 32 33 34 35 36 37 38 39 2b 30 31 3a 33 30");

    // Provenance: InstantDeserTest#testDeserializationAsInt03Nanoseconds.
    void testDeserializationAsInt03NanosecondsVpack() throws Exception {
        Instant value = reader().with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(NANOSECONDS_WHOLE_SECOND);
        assertEquals(WHOLE_SECOND, value);
    }

    // Provenance: InstantDeserTest#testDeserializationAsInt04Nanoseconds.
    void testDeserializationAsInt04NanosecondsVpack() throws Exception {
        WrapperWithReadTimestampsAsNanosEnabled actual = new VPackMapper()
                .readerFor(WrapperWithReadTimestampsAsNanosEnabled.class)
                .readValue(NANOSECONDS_WRAPPER);
        assertEquals(WHOLE_SECOND, actual.value);
    }

    // Provenance: InstantDeserTest#testDeserializationAsInt04Milliseconds.
    void testDeserializationAsInt04MillisecondsVpack() throws Exception {
        WrapperWithReadTimestampsAsNanosDisabled actual = new VPackMapper()
                .readerFor(WrapperWithReadTimestampsAsNanosDisabled.class)
                .readValue(MILLISECONDS_WRAPPER);
        assertEquals(WITH_MILLIS, actual.value);
    }

    // Provenance: InstantDeserTest#testDeserializationAsString01.
    void testDeserializationAsString01Vpack() throws Exception {
        assertEquals(Instant.EPOCH, reader().readValue(STRING_EPOCH));
    }

    // Provenance: InstantDeserTest#testDeserializationAsString02.
    void testDeserializationAsString02Vpack() throws Exception {
        assertEquals(WITH_NANOS, reader().readValue(STRING_WITH_NANOS));
    }

    // Provenance: InstantDeserTest#testDeserializationAsString03.
    void testDeserializationAsString03Vpack() throws Exception {
        assertEquals(OFFSET_BASE, reader().readValue(STRING_FIXED_INSTANT));
    }

    // Provenance: InstantDeserTest#testDeserializationFromStringAsNumber.
    void testDeserializationFromStringAsNumberVpack() throws Exception {
        assertEquals(WITH_NANOS, reader().readValue(DECIMAL_TIMESTAMP));
        assertEquals(WITH_NANOS, reader().readValue(STRINGIFIED_DECIMAL_TIMESTAMP));
    }

    // Provenance: InstantDeserTest#testDeserializationFromStringWithZeroZoneOffset01.
    void testDeserializationFromStringWithZeroZoneOffset01Vpack() throws Exception {
        assertEquals(OFFSET_BASE, reader().readValue(OFFSET_PLUS_COLON));
    }

    // Provenance: InstantDeserTest#testDeserializationFromStringWithZeroZoneOffset02.
    void testDeserializationFromStringWithZeroZoneOffset02Vpack() throws Exception {
        assertEquals(OFFSET_BASE, reader().readValue(OFFSET_PLUS_COMPACT));
    }

    // Provenance: InstantDeserTest#testDeserializationFromStringWithZeroZoneOffset03.
    void testDeserializationFromStringWithZeroZoneOffset03Vpack() throws Exception {
        assertEquals(OFFSET_BASE, reader().readValue(OFFSET_PLUS_HOUR));
    }

    // Provenance: InstantDeserTest#testDeserializationFromStringWithZeroZoneOffset04.
    void testDeserializationFromStringWithZeroZoneOffset04Vpack() throws Exception {
        assertNotEquals(OFFSET_BASE, reader().readValue(OFFSET_PLUS_HALF_HOUR));
    }

    // Provenance: InstantDeserTest#testDeserializationFromStringWithZeroZoneOffset05.
    void testDeserializationFromStringWithZeroZoneOffset05Vpack() throws Exception {
        assertNotEquals(OFFSET_BASE, reader().readValue(OFFSET_PLUS_ONE_HALF_HOURS));
    }
private static ObjectReader reader() {
        return new VPackMapper().readerFor(Instant.class);
    }
static final class WrapperWithReadTimestampsAsNanosEnabled {
        @JsonFormat(with = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public Instant value;
    }
static final class WrapperWithReadTimestampsAsNanosDisabled {
        @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public Instant value;
    }

    void __invoke_testDeserializationAsInt03NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsInt03NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt04NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsInt04NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt04MillisecondsVpack() throws Exception {
        try {
            testDeserializationAsInt04MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01Vpack() throws Exception {
        try {
            testDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString02Vpack() throws Exception {
        try {
            testDeserializationAsString02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString03Vpack() throws Exception {
        try {
            testDeserializationAsString03Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromStringAsNumberVpack() throws Exception {
        try {
            testDeserializationFromStringAsNumberVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromStringWithZeroZoneOffset01Vpack() throws Exception {
        try {
            testDeserializationFromStringWithZeroZoneOffset01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromStringWithZeroZoneOffset02Vpack() throws Exception {
        try {
            testDeserializationFromStringWithZeroZoneOffset02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromStringWithZeroZoneOffset03Vpack() throws Exception {
        try {
            testDeserializationFromStringWithZeroZoneOffset03Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromStringWithZeroZoneOffset04Vpack() throws Exception {
        try {
            testDeserializationFromStringWithZeroZoneOffset04Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromStringWithZeroZoneOffset05Vpack() throws Exception {
        try {
            testDeserializationFromStringWithZeroZoneOffset05Vpack();
        } finally {
        }
    }

}
