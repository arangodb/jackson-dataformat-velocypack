package tools.jackson.databind.ext.javatime.ser;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ext.javatime.ser.TestYearMonthSerializationWithCustomFormatter}
 */
class TestYearMonthSerializationWithCustomFormatter {
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestYearMonthSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestYearMonthSerializationWithCustomFormatter#testSerialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testYearMonthSerializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0355F2#yearMonthFormatters")
    // Provenance: TestYearMonthSerializationWithCustomFormatter#testSerialization(DateTimeFormatter).
    void testYearMonthSerializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0355F2().__invoke_testYearMonthSerializationWithCustomFormatterVpack(formatter);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.ser.TestYearMonthSerializationWithCustomFormatter}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.ser.TestYearMonthSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter)}.
 */
    @ParameterizedTest(name = "testYearMonthDeserializationWithCustomFormatterVpack[{index}] - {0}")
    @MethodSource("tools.jackson.databind.ext.javatime.ser.T32_0355F2#yearMonthFormatters")
    // Provenance: TestYearMonthSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter).
    void testYearMonthDeserializationWithCustomFormatterVpack(Object formatter)
            throws Exception {
            new T32_0355F2().__invoke_testYearMonthDeserializationWithCustomFormatterVpack(formatter);
        }
}
