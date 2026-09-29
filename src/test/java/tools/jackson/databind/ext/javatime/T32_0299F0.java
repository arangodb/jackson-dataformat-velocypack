package tools.jackson.databind.ext.javatime;

import java.math.BigDecimal;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0299F0 {
private static final byte[] DECIMAL_LARGE_NANOS_MAX = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 19 82 73 42 23 19 99 99 99 99");
private static final byte[] DECIMAL_ZERO = VPackWireFixtureTest.hex(
            "c8 01 00 00 00 00 00");
private static final byte[] DECIMAL_15_NANOS = VPackWireFixtureTest.hex(
            "c8 06 f7 ff ff ff 01 50 00 00 00 72");
private static final byte[] DECIMAL_15_72 = VPackWireFixtureTest.hex(
            "c8 02 fe ff ff ff 15 72");
private static final byte[] DECIMAL_LARGE_NANOS = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 19 82 73 42 23 11 92 83 74 65");
private static final byte[] DECIMAL_LARGE_INTEGER = VPackWireFixtureTest.hex(
            "c8 06 00 00 00 00 01 98 27 34 22 31");
private static final byte[] DECIMAL_HUGE_EXPONENT = VPackWireFixtureTest.hex(
            "c8 01 80 96 98 00 01");
private static final byte[] DECIMAL_NEGATIVE = VPackWireFixtureTest.hex(
            "d0 09 f7 ff ff ff 02 27 04 86 25 99 00 00 00");
private static final byte[] DECIMAL_ZERO_TENTH = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff ff 00");
private static final byte[] DECIMAL_LARGE_ZERO_NANOS = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 19 82 73 42 23 10 00 00 00 00");
private static final byte[] DECIMAL_LARGE_999888000 = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 19 82 73 42 23 19 99 88 80 00");

    // Provenance: TestDecimalUtils#testExtractNanosecondDecimal06.
    void testExtractNanosecondDecimal06Vpack() throws Exception {
        assertSecondsAndNanos(DECIMAL_LARGE_NANOS_MAX,
                new BigDecimal("19827342231.999999999"), 19827342231L, 999999999);
    }

    // Provenance: TestDecimalUtils#testExtractSecondsAndNanos01.
    void testExtractSecondsAndNanos01Vpack() throws Exception {
        assertSecondsAndNanos(DECIMAL_ZERO, new BigDecimal("0"), 0L, 0);
    }

    // Provenance: TestDecimalUtils#testExtractSecondsAndNanos02.
    void testExtractSecondsAndNanos02Vpack() throws Exception {
        assertSecondsAndNanos(DECIMAL_15_NANOS, new BigDecimal("15.000000072"), 15L, 72);
    }

    // Provenance: TestDecimalUtils#testExtractSecondsAndNanos03.
    void testExtractSecondsAndNanos03Vpack() throws Exception {
        assertSecondsAndNanos(DECIMAL_15_72, new BigDecimal("15.72"), 15L, 720000000);
    }

    // Provenance: TestDecimalUtils#testExtractSecondsAndNanos04.
    void testExtractSecondsAndNanos04Vpack() throws Exception {
        assertSecondsAndNanos(DECIMAL_LARGE_NANOS,
                new BigDecimal("19827342231.192837465"), 19827342231L, 192837465);
    }

    // Provenance: TestDecimalUtils#testExtractSecondsAndNanos05.
    void testExtractSecondsAndNanos05Vpack() throws Exception {
        assertSecondsAndNanos(DECIMAL_LARGE_INTEGER,
                new BigDecimal("19827342231"), 19827342231L, 0);
    }

    // Provenance: TestDecimalUtils#testExtractSecondsAndNanos06.
    void testExtractSecondsAndNanos06Vpack() throws Exception {
        assertSecondsAndNanos(DECIMAL_LARGE_NANOS_MAX,
                new BigDecimal("19827342231.999999999"), 19827342231L, 999999999);
    }

    // Provenance: TestDecimalUtils#testExtractSecondsAndNanos07.
    void testExtractSecondsAndNanos07Vpack() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(DECIMAL_HUGE_EXPONENT)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            BigDecimal value = parser.getDecimalValue();
            assertEquals(new BigDecimal("1E+10000000"), value);
            assertEquals(-10000000, value.scale());
            assertEquals(0L, value.longValue());
            assertNull(parser.nextToken());
        }
    }

    // Provenance: TestDecimalUtils#testExtractSecondsAndNanosFromNegativeBigDecimal.
    void testExtractSecondsAndNanosFromNegativeBigDecimalVpack() throws Exception {
        assertSecondsAndNanos(DECIMAL_NEGATIVE,
                new BigDecimal("-22704862.599000000"), -22704862L, 599000000);
    }

    // Provenance: TestDecimalUtils#testToDecimal01.
    void testToDecimal01Vpack() throws Exception {
        assertDecimalText(DECIMAL_ZERO_TENTH, "0.0");
        assertDecimalText(DECIMAL_15_NANOS, "15.000000072");
        assertDecimalText(DECIMAL_LARGE_NANOS, "19827342231.192837465");
        assertDecimalText(DECIMAL_LARGE_ZERO_NANOS, "19827342231.000000000");
        assertDecimalText(DECIMAL_LARGE_999888000, "19827342231.999888000");
        assertDecimalText(DECIMAL_NEGATIVE, "-22704862.599000000");
    }
private static void assertSecondsAndNanos(byte[] fixture, BigDecimal expected,
            long seconds, int nanos) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            JsonToken token = parser.nextToken();
            assertTrue(token == JsonToken.VALUE_NUMBER_INT
                    || token == JsonToken.VALUE_NUMBER_FLOAT);
            BigDecimal value = parser.getDecimalValue();
            assertEquals(expected, value);
            assertEquals(expected.scale(), value.scale());
            assertEquals(seconds, value.longValue());
            BigDecimal fraction = value.remainder(BigDecimal.ONE).abs();
            assertEquals(nanos, fraction.movePointRight(9).intValueExact());
            assertNull(parser.nextToken());
        }
    }
private static void assertDecimalText(byte[] fixture, String expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            JsonToken token = parser.nextToken();
            assertTrue(token == JsonToken.VALUE_NUMBER_INT
                    || token == JsonToken.VALUE_NUMBER_FLOAT);
            assertEquals(expected, parser.getDecimalValue().toPlainString());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_testExtractNanosecondDecimal06Vpack() throws Exception {
        try {
            testExtractNanosecondDecimal06Vpack();
        } finally {
        }
    }


    void __invoke_testExtractSecondsAndNanos01Vpack() throws Exception {
        try {
            testExtractSecondsAndNanos01Vpack();
        } finally {
        }
    }


    void __invoke_testExtractSecondsAndNanos02Vpack() throws Exception {
        try {
            testExtractSecondsAndNanos02Vpack();
        } finally {
        }
    }


    void __invoke_testExtractSecondsAndNanos03Vpack() throws Exception {
        try {
            testExtractSecondsAndNanos03Vpack();
        } finally {
        }
    }


    void __invoke_testExtractSecondsAndNanos04Vpack() throws Exception {
        try {
            testExtractSecondsAndNanos04Vpack();
        } finally {
        }
    }


    void __invoke_testExtractSecondsAndNanos05Vpack() throws Exception {
        try {
            testExtractSecondsAndNanos05Vpack();
        } finally {
        }
    }


    void __invoke_testExtractSecondsAndNanos06Vpack() throws Exception {
        try {
            testExtractSecondsAndNanos06Vpack();
        } finally {
        }
    }


    void __invoke_testExtractSecondsAndNanos07Vpack() throws Exception {
        try {
            testExtractSecondsAndNanos07Vpack();
        } finally {
        }
    }


    void __invoke_testExtractSecondsAndNanosFromNegativeBigDecimalVpack() throws Exception {
        try {
            testExtractSecondsAndNanosFromNegativeBigDecimalVpack();
        } finally {
        }
    }


    void __invoke_testToDecimal01Vpack() throws Exception {
        try {
            testToDecimal01Vpack();
        } finally {
        }
    }

}
