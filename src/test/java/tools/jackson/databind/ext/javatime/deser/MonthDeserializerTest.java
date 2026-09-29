package tools.jackson.databind.ext.javatime.deser;

import java.time.Month;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}
 */
class MonthDeserializerTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testBadDeserializationAsString01_oneBased(String,String)}.
 */
    @ParameterizedTest(name = "testBadDeserializationAsString01OneBasedVpack[{index}]")
    @MethodSource("tools.jackson.databind.ext.javatime.deser.T32_0321F1#oneBasedBadMonthValues")
    // Provenance: MonthDeserializerTest#testBadDeserializationAsString01_oneBased(String,String).
    void testBadDeserializationAsString01OneBasedVpack(byte[] input, String expectedMessage) throws Exception {
            new T32_0321F1().__invoke_testBadDeserializationAsString01OneBasedVpack(input, expectedMessage);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testBadDeserializationAsString_zeroBasedOutOfRange(String,String)}.
 */
    @ParameterizedTest(name = "testBadDeserializationAsStringZeroBasedOutOfRangeVpack[{index}]")
    @MethodSource("tools.jackson.databind.ext.javatime.deser.T32_0321F1#zeroBasedBadMonthValues")
    // Provenance: MonthDeserializerTest#testBadDeserializationAsString_zeroBasedOutOfRange(String,String).
    void testBadDeserializationAsStringZeroBasedOutOfRangeVpack(byte[] input) throws Exception {
            new T32_0321F1().__invoke_testBadDeserializationAsStringZeroBasedOutOfRangeVpack(input);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserialization01_oneBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserialization01_oneBased.
    void testDeserialization01OneBasedVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserialization01OneBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserialization01_zeroBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserialization01_zeroBased.
    void testDeserialization01ZeroBasedVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserialization01ZeroBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserialization02_oneBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserialization02_oneBased.
    void testDeserialization02OneBasedVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserialization02OneBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserialization02_zeroBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserialization02_zeroBased.
    void testDeserialization02ZeroBasedVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserialization02ZeroBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithFloatUnwrapDisabled()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithFloatUnwrapDisabled.
    void testDeserializationAsArrayWithFloatUnwrapDisabledVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserializationAsArrayWithFloatUnwrapDisabledVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithIntValue()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithIntValue.
    void testDeserializationAsArrayWithIntValueVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserializationAsArrayWithIntValueVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithIntValue_withFeatureEnabled()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithIntValue_withFeatureEnabled.
    void testDeserializationAsArrayWithIntValueWithFeatureEnabledVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserializationAsArrayWithIntValueWithFeatureEnabledVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithIntValue_zeroBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithIntValue_zeroBased.
    void testDeserializationAsArrayWithIntValueZeroBasedVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserializationAsArrayWithIntValueZeroBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithMoreThanOneElement()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithMoreThanOneElement.
    void testDeserializationAsArrayWithMoreThanOneElementVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserializationAsArrayWithMoreThanOneElementVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithMoreThanOneString()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithMoreThanOneString.
    void testDeserializationAsArrayWithMoreThanOneStringVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserializationAsArrayWithMoreThanOneStringVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithNumericStringUnwrapEnabled()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithNumericStringUnwrapEnabled.
    void testDeserializationAsArrayWithNumericStringUnwrapEnabledVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserializationAsArrayWithNumericStringUnwrapEnabledVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithObjectUnwrapDisabled()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithObjectUnwrapDisabled.
    void testDeserializationAsArrayWithObjectUnwrapDisabledVpack() throws Exception {
            new T32_0322Fixture().__invoke_testDeserializationAsArrayWithObjectUnwrapDisabledVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsString01_oneBased(Month)}.
 */
    @ParameterizedTest(name = "testDeserializationAsString01OneBasedVpack[{index}]")
    @EnumSource(Month.class)
    // Provenance: MonthDeserializerTest#testDeserializationAsString01_oneBased(Month).
    void testDeserializationAsString01OneBasedVpack(Month expectedMonth) throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsString01OneBasedVpack(expectedMonth);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsString01_zeroBased(Month)}.
 */
    @ParameterizedTest(name = "testDeserializationAsString01ZeroBasedVpack[{index}]")
    @EnumSource(Month.class)
    // Provenance: MonthDeserializerTest#testDeserializationAsString01_zeroBased(Month).
    void testDeserializationAsString01ZeroBasedVpack(Month expectedMonth) throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsString01ZeroBasedVpack(expectedMonth);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsString02_oneBased(Month)}, {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsString02_zeroBased(Month)}.
 */
    @ParameterizedTest(name = "testDeserializationAsString02OneBasedVpack[{index}]")
    @EnumSource(Month.class)
    // Provenance: MonthDeserializerTest#testDeserializationAsString02_oneBased(Month).
    void testDeserializationAsString02OneBasedVpack(Month expectedMonth) throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsString02OneBasedVpack(expectedMonth);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsInt_oneBased(Month)}.
 */
    @ParameterizedTest(name = "testDeserializationAsIntOneBasedVpack[{index}]")
    @EnumSource(Month.class)
    // Provenance: MonthDeserializerTest#testDeserializationAsInt_oneBased(Month).
    void testDeserializationAsIntOneBasedVpack(Month expectedMonth) throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsIntOneBasedVpack(expectedMonth);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsInt_zeroBased(Month)}.
 */
    @ParameterizedTest(name = "testDeserializationAsIntZeroBasedVpack[{index}]")
    @EnumSource(Month.class)
    // Provenance: MonthDeserializerTest#testDeserializationAsInt_zeroBased(Month).
    void testDeserializationAsIntZeroBasedVpack(Month expectedMonth) throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsIntZeroBasedVpack(expectedMonth);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsIntOutOfRange_oneBased(int)}.
 */
    @ParameterizedTest(name = "testDeserializationAsIntOutOfRangeOneBasedVpack[{index}]")
    @ValueSource(ints = {0, -1, 13, 100})
    // Provenance: MonthDeserializerTest#testDeserializationAsIntOutOfRange_oneBased(int).
    void testDeserializationAsIntOutOfRangeOneBasedVpack(int invalidValue) throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsIntOutOfRangeOneBasedVpack(invalidValue);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsIntOutOfRange_zeroBased(int)}.
 */
    @ParameterizedTest(name = "testDeserializationAsIntOutOfRangeZeroBasedVpack[{index}]")
    @ValueSource(ints = {-1, 12, 13, 100})
    // Provenance: MonthDeserializerTest#testDeserializationAsIntOutOfRange_zeroBased(int).
    void testDeserializationAsIntOutOfRangeZeroBasedVpack(int invalidValue) throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsIntOutOfRangeZeroBasedVpack(invalidValue);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsEmptyArray()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsEmptyArray().
    void testDeserializationAsEmptyArrayVpack() throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsEmptyArrayVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsEmptyArray_withFeatureEnabled()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsEmptyArray_withFeatureEnabled().
    void testDeserializationAsEmptyArrayWithFeatureEnabledVpack() throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsEmptyArrayWithFeatureEnabledVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithWrongToken()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithWrongToken().
    void testDeserializationAsArrayWithWrongTokenVpack() throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsArrayWithWrongTokenVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithStringUnwrapDisabled()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithStringUnwrapDisabled().
    void testDeserializationAsArrayWithStringUnwrapDisabledVpack() throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsArrayWithStringUnwrapDisabledVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationAsArrayWithStringUnwrapEnabled()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithStringUnwrapEnabled().
    void testDeserializationAsArrayWithStringUnwrapEnabledVpack() throws Exception {
            new T32_0323Fixture().__invoke_testDeserializationAsArrayWithStringUnwrapEnabledVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationWithTypeInfo01_oneBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationWithTypeInfo01_oneBased.
    void testDeserializationWithTypeInfo01OneBasedVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationWithTypeInfo01OneBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationWithTypeInfo01_zeroBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationWithTypeInfo01_zeroBased.
    void testDeserializationWithTypeInfo01ZeroBasedVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationWithTypeInfo01ZeroBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationWithWhitespace()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationWithWhitespace.
    void testDeserializationWithWhitespaceVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationWithWhitespaceVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationWithWhitespaceNumeric()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationWithWhitespaceNumeric.
    void testDeserializationWithWhitespaceNumericVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationWithWhitespaceNumericVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationFromBoolean()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationFromBoolean.
    void testDeserializationFromBooleanVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationFromBooleanVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationFromFloat()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationFromFloat.
    void testDeserializationFromFloatVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationFromFloatVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationFromObject()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationFromObject.
    void testDeserializationFromObjectVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationFromObjectVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationWithCustomFormat()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationWithCustomFormat.
    void testDeserializationWithCustomFormatVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationWithCustomFormatVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationWithCustomFormatMarch()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationWithCustomFormatMarch.
    void testDeserializationWithCustomFormatMarchVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationWithCustomFormatMarchVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationWithCustomFormatInvalid()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationWithCustomFormatInvalid.
    void testDeserializationWithCustomFormatInvalidVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationWithCustomFormatInvalidVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializationWithFullMonthFormat()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializationWithFullMonthFormat.
    void testDeserializationWithFullMonthFormatVpack() throws Exception {
            new T32_0324Fixture().__invoke_testDeserializationWithFullMonthFormatVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testDeserializeFromEmptyString()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testDeserializeFromEmptyString.
    void testDeserializeFromEmptyStringVpack() throws Exception {
            new T32_0325F0().__invoke_testDeserializeFromEmptyStringVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testFormatAnnotation_oneBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testFormatAnnotation_oneBased.
    void testFormatAnnotationOneBasedVpack() throws Exception {
            new T32_0325F0().__invoke_testFormatAnnotationOneBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testFormatAnnotation_zeroBased()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testFormatAnnotation_zeroBased.
    void testFormatAnnotationZeroBasedVpack() throws Exception {
            new T32_0325F0().__invoke_testFormatAnnotationZeroBasedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testWithDateFormatCreatesNewInstance()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testWithDateFormatCreatesNewInstance.
    void testWithDateFormatCreatesNewInstanceVpack() throws Exception {
            new T32_0325F0().__invoke_testWithDateFormatCreatesNewInstanceVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest}.
 * Original test methods: {@link tools.jackson.databind.ext.javatime.deser.MonthDeserializerTest#testWithLeniencyCreatesNewInstance()}.
 */
    @Test
    // Provenance: MonthDeserializerTest#testWithLeniencyCreatesNewInstance.
    void testWithLeniencyCreatesNewInstanceVpack() throws Exception {
            new T32_0325F0().__invoke_testWithLeniencyCreatesNewInstanceVpack();
        }
}
