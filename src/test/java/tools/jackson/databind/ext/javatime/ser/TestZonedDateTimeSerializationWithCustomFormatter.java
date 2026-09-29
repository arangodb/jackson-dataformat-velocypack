package tools.jackson.databind.ext.javatime.ser;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ext.javatime.ser.TestZonedDateTimeSerializationWithCustomFormatter}
 */
class TestZonedDateTimeSerializationWithCustomFormatter {
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestZonedDateTimeSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestZonedDateTimeSerializationWithCustomFormatter#testSerialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testZonedDateTimeSerializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0356F1#zonedDateTimeFormatters")
    // Provenance: TestZonedDateTimeSerializationWithCustomFormatter#testSerialization(DateTimeFormatter).
    void testZonedDateTimeSerializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0356F1().__invoke_testZonedDateTimeSerializationWithCustomFormatterVpack(formatter);
        }
}
