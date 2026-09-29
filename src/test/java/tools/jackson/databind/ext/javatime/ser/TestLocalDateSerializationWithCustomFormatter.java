package tools.jackson.databind.ext.javatime.ser;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateSerializationWithCustomFormatter}
 */
class TestLocalDateSerializationWithCustomFormatter {
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateSerializationWithCustomFormatter#testSerialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testLocalDateSerializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0354F2#customFormatters")
    // Provenance: TestLocalDateSerializationWithCustomFormatter#testSerialization(DateTimeFormatter).
    void testLocalDateSerializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0354F2().__invoke_testLocalDateSerializationWithCustomFormatterVpack(formatter);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestLocalDateSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testLocalDateDeserializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0354F2#customFormatters")
    // Provenance: TestLocalDateSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter).
    void testLocalDateDeserializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0354F2().__invoke_testLocalDateDeserializationWithCustomFormatterVpack(formatter);
        }
}
