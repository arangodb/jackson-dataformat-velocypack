package tools.jackson.databind.ext.javatime.ser;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ext.javatime.ser.TestYearSerializationWithCustomFormatter}
 */
class TestYearSerializationWithCustomFormatter {
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestYearSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestYearSerializationWithCustomFormatter#testSerialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testYearSerializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0356F0#yearFormatters")
    // Provenance: TestYearSerializationWithCustomFormatter#testSerialization(DateTimeFormatter).
    void testYearSerializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0356F0().__invoke_testYearSerializationWithCustomFormatterVpack(formatter);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestYearSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestYearSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testYearDeserializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0356F0#yearFormatters")
    // Provenance: TestYearSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter).
    void testYearDeserializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0356F0().__invoke_testYearDeserializationWithCustomFormatterVpack(formatter);
        }
}
