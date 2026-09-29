package tools.jackson.databind.ext.javatime.ser;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateTimeSerializationWithCustomFormatter}
 */
class TestLocalDateTimeSerializationWithCustomFormatter {
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateTimeSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateTimeSerializationWithCustomFormatter#testSerialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testLocalDateTimeSerializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0355F0#localDateTimeFormatters")
    // Provenance: TestLocalDateTimeSerializationWithCustomFormatter#testSerialization(DateTimeFormatter).
    void testLocalDateTimeSerializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0355F0().__invoke_testLocalDateTimeSerializationWithCustomFormatterVpack(formatter);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateTimeSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateTimeSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testLocalDateTimeDeserializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0355F0#localDateTimeFormatters")
    // Provenance: TestLocalDateTimeSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter).
    void testLocalDateTimeDeserializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0355F0().__invoke_testLocalDateTimeDeserializationWithCustomFormatterVpack(formatter);
        }
}
