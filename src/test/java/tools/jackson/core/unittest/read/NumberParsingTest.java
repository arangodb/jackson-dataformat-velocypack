package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.NumberParsingTest}
 */
class NumberParsingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#intRange()}.
 */
    @Test
    void intRangeUsesLiteralVpackSignedIntegers() throws Exception {
            new T32_0080F1().__invoke_intRangeUsesLiteralVpackSignedIntegers();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#bigDecimalRange()}.
 */
    @Test
    void bigDecimalRangeUsesLiteralBcdOutsideLongRange() throws Exception {
            new T32_0080F1().__invoke_bigDecimalRangeUsesLiteralBcdOutsideLongRange();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#bigIntegerWithENotation()}.
 */
    @Test
    void bigIntegerExponentIsCoercedExactly() throws Exception {
            new T32_0080F1().__invoke_bigIntegerExponentIsCoercedExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#biggerThanFloatHandling()}.
 */
    @Test
    void bcdBeyondDoubleRangeIsFiniteAndExact() throws Exception {
            new T32_0080F1().__invoke_bcdBeyondDoubleRangeIsFiniteAndExact();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#databind4694()}.
 */
    @Test
    void databind4694ScaledDecimalIsFiniteAndExact() throws Exception {
            new T32_0080F1().__invoke_databind4694ScaledDecimalIsFiniteAndExact();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#intWithENotation()}.
 */
    @Test
    void intExponentCoercionUsesExactLiteralBcd() throws Exception {
            new T32_0081Fixture().__invoke_intExponentCoercionUsesExactLiteralBcd();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#intsWith19Chars()}.
 */
    @Test
    void nineteenDigitIntegersRemainExactAsBigInteger() throws Exception {
            new T32_0081Fixture().__invoke_nineteenDigitIntegersRemainExactAsBigInteger();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#largeBigIntegerWithENotation()}.
 */
    @Test
    void largeExponentIntegerCoercionAvoidsDouble() throws Exception {
            new T32_0081Fixture().__invoke_largeExponentIntegerCoercionAvoidsDouble();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#invalidBooleanAccess()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#invalidIntAccess()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#invalidLongAccess()}.
 */
    @Test
    void invalidScalarAccessorsRaiseInputCoercion() throws Exception {
            new T32_0081Fixture().__invoke_invalidScalarAccessorsRaiseInputCoercion();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#longRange()}.
 */
    @Test
    void longRangeUsesLiteralIndexedArray() throws Exception {
            new T32_0082Fixture().__invoke_longRangeUsesLiteralIndexedArray();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#longWithENotation()}.
 */
    @Test
    void longWithENotationUsesLiteralBcdExponent() throws Exception {
            new T32_0082Fixture().__invoke_longWithENotationUsesLiteralBcdExponent();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#negativeMaxNumberLength()}.
 */
    @Test
    void negativeMaxNumberLengthIsRejectedForVpackConfiguration() throws Exception {
            new T32_0082Fixture().__invoke_negativeMaxNumberLengthIsRejectedForVpackConfiguration();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberParsingTest#veryLongIntRootValue()}.
 */
    @Test
    void veryLongIntRootValueUsesExactLiteralBcd() throws Exception {
            new T32_0083F0().__invoke_veryLongIntRootValueUsesExactLiteralBcd();
        }
}
