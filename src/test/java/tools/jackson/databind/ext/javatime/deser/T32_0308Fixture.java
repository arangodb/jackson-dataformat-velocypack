package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;

import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0308Fixture {
private static final byte[] BELOW_INSTANT_MIN = VPackWireFixtureTest.hex(
            "d0 09 00 00 00 00 03 15 57 01 41 67 21 92 01");
private static final byte[] POSITIVE_HUGE = VPackWireFixtureTest.hex(
            "c8 01 40 00 00 00 01");
private static final byte[] NEGATIVE_HUGE = VPackWireFixtureTest.hex(
            "d0 01 40 00 00 00 01");
private static final byte[] POSITIVE_LARGE_EXPONENT = VPackWireFixtureTest.hex(
            "c8 01 80 96 98 00 01");
private static final byte[] NEGATIVE_LARGE_EXPONENT = VPackWireFixtureTest.hex(
            "d0 01 80 96 98 00 01");
private static final byte[] POSITIVE_LARGE_NEGATIVE_EXPONENT = VPackWireFixtureTest.hex(
            "c8 01 80 69 67 ff 01");
private static final byte[] NEGATIVE_LARGE_NEGATIVE_EXPONENT = VPackWireFixtureTest.hex(
            "d0 01 80 69 67 ff 01");
private static final byte[] ZERO = VPackWireFixtureTest.hex("30");
private static final byte[] NANOSECONDS_123456789 = VPackWireFixtureTest.hex(
            "2b 15 cd 5b 07");
private static final byte[] MILLISECONDS_123456789422 = VPackWireFixtureTest.hex(
            "2f ae 1b 99 be 1c 00 00 00");
private static final byte[] MILLISECONDS_123456789000 = VPackWireFixtureTest.hex(
            "2f 08 1a 99 be 1c 00 00 00");

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase05.
    void testDeserializationAsFloatEdgeCase05Vpack() {
        assertThrows(DateTimeParseException.class,
                () -> reader().readValue(BELOW_INSTANT_MIN));
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase06.
    void testDeserializationAsFloatEdgeCase06Vpack() throws Exception {
        Instant value = reader().readValue(POSITIVE_HUGE);
        assertEquals(0, value.getEpochSecond());
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase07.
    void testDeserializationAsFloatEdgeCase07Vpack() throws Exception {
        Instant value = reader().readValue(NEGATIVE_HUGE);
        assertEquals(0, value.getEpochSecond());
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase08.
    void testDeserializationAsFloatEdgeCase08Vpack() throws Exception {
        Instant value = reader().readValue(POSITIVE_LARGE_EXPONENT);
        assertEquals(0, value.getEpochSecond());
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase09.
    void testDeserializationAsFloatEdgeCase09Vpack() throws Exception {
        Instant value = reader().readValue(NEGATIVE_LARGE_EXPONENT);
        assertEquals(0, value.getEpochSecond());
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase10.
    void testDeserializationAsFloatEdgeCase10Vpack() throws Exception {
        Instant value = reader().readValue(POSITIVE_LARGE_NEGATIVE_EXPONENT);
        assertEquals(0, value.getEpochSecond());
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase11.
    void testDeserializationAsFloatEdgeCase11Vpack() throws Exception {
        Instant value = reader().readValue(NEGATIVE_LARGE_NEGATIVE_EXPONENT);
        assertEquals(0, value.getEpochSecond());
    }

    // Provenance: InstantDeserTest#testDeserializationAsInt01Milliseconds.
    void testDeserializationAsInt01MillisecondsVpack() throws Exception {
        Instant value = reader().without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(ZERO);
        assertEquals(Instant.ofEpochSecond(0L), value);
    }

    // Provenance: InstantDeserTest#testDeserializationAsInt01Nanoseconds.
    void testDeserializationAsInt01NanosecondsVpack() throws Exception {
        Instant value = reader().with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(ZERO);
        assertEquals(Instant.ofEpochSecond(0L), value);
    }

    // Provenance: InstantDeserTest#testDeserializationAsInt02Milliseconds.
    void testDeserializationAsInt02MillisecondsVpack() throws Exception {
        Instant value = reader().without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(MILLISECONDS_123456789422);
        assertEquals(Instant.ofEpochSecond(123456789L, 422000000L), value);
    }

    // Provenance: InstantDeserTest#testDeserializationAsInt02Nanoseconds.
    void testDeserializationAsInt02NanosecondsVpack() throws Exception {
        Instant value = reader().with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(NANOSECONDS_123456789);
        assertEquals(Instant.ofEpochSecond(123456789L), value);
    }

    // Provenance: InstantDeserTest#testDeserializationAsInt03Milliseconds.
    void testDeserializationAsInt03MillisecondsVpack() throws Exception {
        Instant value = reader().without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(MILLISECONDS_123456789000);
        assertEquals(Instant.ofEpochSecond(123456789L), value);
    }
private static ObjectReader reader() {
        return new VPackMapper().readerFor(Instant.class);
    }

    void __invoke_testDeserializationAsFloatEdgeCase05Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase05Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase06Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase06Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase07Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase07Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase08Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase08Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase09Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase09Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase10Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase10Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase11Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase11Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01MillisecondsVpack() throws Exception {
        try {
            testDeserializationAsInt01MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt01NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsInt01NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02MillisecondsVpack() throws Exception {
        try {
            testDeserializationAsInt02MillisecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02NanosecondsVpack() throws Exception {
        try {
            testDeserializationAsInt02NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt03MillisecondsVpack() throws Exception {
        try {
            testDeserializationAsInt03MillisecondsVpack();
        } finally {
        }
    }

}
