package tools.jackson.core.unittest.json.async;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest}
 * {@link tools.jackson.core.unittest.json.async.AsyncNonStdNumberHandlingTest}
 * {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest}
 */
class AsyncNonStdParsingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdNumberHandlingTest}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#negativeHexadecimal()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#rootMinusZeroAtEOF()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#rootPlainZeroAtEOF()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#rootPlusZeroAtEOF()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#test2DecimalPoints()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#trailingDotInDecimal()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#trailingDotInDecimalEnabled()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdNumberHandlingTest#defaultsForAsync()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdNumberHandlingTest#leadingPeriodFloat()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdNumberHandlingTest#leadingZeroesFloat()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdNumberHandlingTest#leadingZeroesInt()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest#aposQuotingDisabled()}.
 */
    @Test
    void assignedJsonSpellingsHaveNoVpackWireSurface() throws Exception {
            new T32_0053Fixture().__invoke_assignedJsonSpellingsHaveNoVpackWireSurface();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest#aposQuotingEnabled()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest#largeUnquotedNames()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest#nonStandarBackslashQuotingForValues()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest#nonStandardNameChars()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest#simpleUnquotedNames()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStdParsingTest#singleQuotesEscaped()}.
 */
    @Test
    void jsonNonStandardParsingHasNoVpackWireSurface() throws Exception {
            new T32_0054Fixture().__invoke_jsonNonStandardParsingHasNoVpackWireSurface();
        }
}
