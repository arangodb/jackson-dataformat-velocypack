package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest}
 * {@link tools.jackson.core.unittest.read.NonStandardUnquotedNamesTest}
 * {@link tools.jackson.core.unittest.read.NumberParsingTest}
 */
class NonStandardNumberParsingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#largeDecimal()}.
 */
    @Test
    void largeDecimalRemainsExactWhenDoubleAccessorOverflows() throws Exception {
            new T32_0075Fixture().__invoke_largeDecimalRemainsExactWhenDoubleAccessorOverflows();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest}, {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#leadingDotInNegativeDecimalAllowedAsync()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#leadingDotInNegativeDecimalAllowedBytes()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#leadingDotInNegativeDecimalAllowedReader()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#leadingPlusSignInDecimalAllowedBytes()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#leadingPlusSignInDecimalAllowedDataInput()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#leadingPlusSignInDecimalAllowedReader()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#leadingPlusSignInDecimalDefaultFail()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#negativeHexadecimal()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#signedLeadingDotAsFieldValueBytes()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#signedLeadingDotAsFieldValueDataInput()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#signedLeadingDotAsFieldValueReader()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#test2DecimalPoints()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#bigNumbers()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#floatBoundary146Bytes()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#floatBoundary146Chars()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#intOverflow()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#intParsing()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#intParsingWithStrings()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#longBoundsChecks()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#longNumbers()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#longNumbers2()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#longOverflow()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#longParsing()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#longParsingWithStrings()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#longerFloatingPoint()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#parsingOfLongerSequences()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#parsingOfLongerSequencesWithNonNumeric()}.
 */
    @Test
    void jsonOnlyNumberSpellingsRemainOutsideVpackWireSurface() throws Exception {
            new T32_0076Fixture().__invoke_jsonOnlyNumberSpellingsRemainOutsideVpackWireSurface();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest}, {@link tools.jackson.core.unittest.read.NonStandardUnquotedNamesTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#test2DecimalPointsInArray()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#testLeadingDotInDecimalAllowedBytes()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#testLeadingDotInDecimalAllowedDataInput()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#testLeadingDotInDecimalAllowedReader()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#testTrailingDotInDecimalAllowedBytes()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#testTrailingDotInDecimalAllowedDataInput()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#trailingDotInDecimal()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#trailingDotInDecimalAllowedReader()}, {@link tools.jackson.core.unittest.read.NonStandardUnquotedNamesTest#largeUnquoted()}, {@link tools.jackson.core.unittest.read.NonStandardUnquotedNamesTest#nonStandardNameChars()}, {@link tools.jackson.core.unittest.read.NonStandardUnquotedNamesTest#simpleUnquotedBytes()}, {@link tools.jackson.core.unittest.read.NonStandardUnquotedNamesTest#simpleUnquotedChars()}, {@link tools.jackson.core.unittest.read.NonStandardUnquotedNamesTest#unquotedIssue510()}.
 */
    @Test
    void assignedDecimalTextSpellingsHaveNoVpackWireSurface() throws Exception {
            new T32_0077Fixture().__invoke_assignedDecimalTextSpellingsHaveNoVpackWireSurface();
        }
}
