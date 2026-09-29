package tools.jackson.databind.ext.javatime.ser;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ext.javatime.ser.TestLocalTimeSerializationWithCustomFormatter}
 */
class TestLocalTimeSerializationWithCustomFormatter {
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestLocalTimeSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestLocalTimeSerializationWithCustomFormatter#testSerialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testLocalTimeSerializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0355F1#localTimeFormatters")
    // Provenance: TestLocalTimeSerializationWithCustomFormatter#testSerialization(DateTimeFormatter).
    void testLocalTimeSerializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0355F1().__invoke_testLocalTimeSerializationWithCustomFormatterVpack(formatter);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestLocalTimeSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestLocalTimeSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testLocalTimeDeserializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0355F1#localTimeFormatters")
    // Provenance: TestLocalTimeSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter).
    void testLocalTimeDeserializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0355F1().__invoke_testLocalTimeDeserializationWithCustomFormatterVpack(formatter);
        }
}
