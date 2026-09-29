package tools.jackson.databind.util;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.util.NumberUtilTest}
 */
class NumberUtilTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.util.NumberUtilTest}.
 * Original test methods: {@link tools.jackson.databind.util.NumberUtilTest#testDecimalAndFloat()}, {@link tools.jackson.databind.util.NumberUtilTest#testEmptyAndSignOnly()}, {@link tools.jackson.databind.util.NumberUtilTest#testLeadingZeroes()}, {@link tools.jackson.databind.util.NumberUtilTest#testNonNumeric()}, {@link tools.jackson.databind.util.NumberUtilTest#testSignedNumbers()}, {@link tools.jackson.databind.util.NumberUtilTest#testSimpleDigits()}, {@link tools.jackson.databind.util.NumberUtilTest#testWhitespaceAndSpecialChars()}.
 */
    @Test
    // Provenance: NumberUtilTest#testSimpleDigits through #testWhitespaceAndSpecialChars().
    // Text spelling is not wire data: legal VPack numbers get literal encodings and
    // Java-only integer spellings / malformed text are rejected by the text writer.
    void jdkIntegerLexemesMapToVpackNumbersOrTextRejections() throws Exception {
            new T32_0628F0().__invoke_jdkIntegerLexemesMapToVpackNumbersOrTextRejections();
        }
}
