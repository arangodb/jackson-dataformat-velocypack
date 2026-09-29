package tools.jackson.databind.ext.javatime.deser;

import java.time.Duration;

import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0305Fixture {
private static final byte[] BELOW_MIN_DURATION = VPackWireFixtureTest.hex(
            "d0 0a ff ff ff ff 92 23 37 20 36 85 47 75 80 81");
private static final byte[] ABOVE_MAX_DURATION = VPackWireFixtureTest.hex(
            "c8 0a ff ff ff ff 92 23 37 20 36 85 47 75 80 80");
private static final byte[] BELOW_MIN_INTEGER_DURATION = VPackWireFixtureTest.hex(
            "d0 0a ff ff ff ff 92 23 37 20 36 85 47 75 80 90");
private static final byte[] POSITIVE_HUGE_DURATION = VPackWireFixtureTest.hex(
            "c8 01 40 00 00 00 01");
private static final byte[] NEGATIVE_HUGE_DURATION = VPackWireFixtureTest.hex(
            "d0 01 40 00 00 00 01");
private static final byte[] POSITIVE_LARGE_EXPONENT_DURATION = VPackWireFixtureTest.hex(
            "c8 01 80 96 98 00 01");
private static final byte[] NEGATIVE_LARGE_EXPONENT_DURATION = VPackWireFixtureTest.hex(
            "d0 01 80 96 98 00 01");
private static final byte[] POSITIVE_LARGE_NEGATIVE_EXPONENT_DURATION = VPackWireFixtureTest.hex(
            "c8 01 80 69 67 ff 01");
private static final byte[] NEGATIVE_LARGE_NEGATIVE_EXPONENT_DURATION = VPackWireFixtureTest.hex(
            "d0 01 80 69 67 ff 01");
private static final byte[] SIXTY = VPackWireFixtureTest.hex("28 3c");
private static final byte[] SIXTY_THOUSAND = VPackWireFixtureTest.hex("29 60 ea");
private static final byte[] THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT =
            VPackWireFixtureTest.hex("29 ba 34");

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase03.
    void testDeserializationAsFloatEdgeCase03Vpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> reader().readValue(BELOW_MIN_DURATION));
        assertInstanceOf(ArithmeticException.class, failure.getCause());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase04.
    void testDeserializationAsFloatEdgeCase04Vpack() throws Exception {
        Duration value = reader().readValue(ABOVE_MAX_DURATION);
        assertEquals(Long.MIN_VALUE, value.getSeconds());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase05.
    void testDeserializationAsFloatEdgeCase05Vpack() throws Exception {
        Duration value = reader().readValue(BELOW_MIN_INTEGER_DURATION);
        assertEquals(Long.MAX_VALUE, value.getSeconds());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase06.
    void testDeserializationAsFloatEdgeCase06Vpack() throws Exception {
        Duration value = reader().readValue(POSITIVE_HUGE_DURATION);
        assertEquals(0, value.getSeconds());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase07.
    void testDeserializationAsFloatEdgeCase07Vpack() throws Exception {
        Duration value = reader().readValue(NEGATIVE_HUGE_DURATION);
        assertEquals(0, value.getSeconds());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase08.
    void testDeserializationAsFloatEdgeCase08Vpack() throws Exception {
        Duration value = reader().readValue(POSITIVE_LARGE_EXPONENT_DURATION);
        assertEquals(0, value.getSeconds());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase09.
    void testDeserializationAsFloatEdgeCase09Vpack() throws Exception {
        Duration value = reader().readValue(NEGATIVE_LARGE_EXPONENT_DURATION);
        assertEquals(0, value.getSeconds());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase10.
    void testDeserializationAsFloatEdgeCase10Vpack() throws Exception {
        Duration value = reader().readValue(POSITIVE_LARGE_NEGATIVE_EXPONENT_DURATION);
        assertEquals(0, value.getSeconds());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase11.
    void testDeserializationAsFloatEdgeCase11Vpack() throws Exception {
        Duration value = reader().readValue(NEGATIVE_LARGE_NEGATIVE_EXPONENT_DURATION);
        assertEquals(0, value.getSeconds());
    }

    // Provenance: DurationDeserTest#testDeserializationAsInt01.
    void testDeserializationAsInt01Vpack() throws Exception {
        Duration value = reader().with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(SIXTY);
        assertEquals(Duration.ofSeconds(60L, 0), value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsInt02.
    void testDeserializationAsInt02Vpack() throws Exception {
        Duration value = reader().without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(SIXTY_THOUSAND);
        assertEquals(Duration.ofSeconds(60L, 0), value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsInt03.
    void testDeserializationAsInt03Vpack() throws Exception {
        Duration value = reader().with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT);
        assertEquals(Duration.ofSeconds(13498L, 0), value);
    }
private static tools.jackson.databind.ObjectReader reader() {
        return new VPackMapper().readerFor(Duration.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    void __invoke_testDeserializationAsFloatEdgeCase03Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase03Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase04Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase04Vpack();
        } finally {
        }
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


    void __invoke_testDeserializationAsInt01Vpack() throws Exception {
        try {
            testDeserializationAsInt01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt02Vpack() throws Exception {
        try {
            testDeserializationAsInt02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt03Vpack() throws Exception {
        try {
            testDeserializationAsInt03Vpack();
        } finally {
        }
    }

}
