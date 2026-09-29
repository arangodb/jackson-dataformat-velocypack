package tools.jackson.databind.ext.javatime;

import java.math.BigDecimal;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0299F1 {
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

    // Provenance: TestFeatures#testAdjustDatesToContextTimeZoneSettingEnabledByDefault.
    void testAdjustDatesToContextTimeZoneSettingEnabledByDefaultVpack() {
        assertTrue(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE.enabledByDefault());
    }

    // Provenance: TestFeatures#testReadDateTimestampsAsNanosecondsSettingEnabledByDefault.
    void testReadDateTimestampsAsNanosecondsSettingEnabledByDefaultVpack() {
        assertTrue(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS.enabledByDefault());
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

    void __invoke_testAdjustDatesToContextTimeZoneSettingEnabledByDefaultVpack() throws Exception {
        try {
            testAdjustDatesToContextTimeZoneSettingEnabledByDefaultVpack();
        } finally {
        }
    }


    void __invoke_testReadDateTimestampsAsNanosecondsSettingEnabledByDefaultVpack() throws Exception {
        try {
            testReadDateTimestampsAsNanosecondsSettingEnabledByDefaultVpack();
        } finally {
        }
    }

}
