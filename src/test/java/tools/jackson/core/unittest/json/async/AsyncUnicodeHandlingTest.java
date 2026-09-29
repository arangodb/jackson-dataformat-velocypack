package tools.jackson.core.unittest.json.async;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.async.AsyncUnicodeHandlingTest}
 */
class AsyncUnicodeHandlingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncUnicodeHandlingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncUnicodeHandlingTest#longUnicodeWithSurrogates()}, {@link tools.jackson.core.unittest.json.async.AsyncUnicodeHandlingTest#shortUnicodeWithSurrogates()}.
 */
    @Test
    void literalVpackUnicodeSurrogateValuesAndNamesRetainExactTextAndSkip()
            throws Exception {
            new T32_0061Fixture().__invoke_literalVpackUnicodeSurrogateValuesAndNamesRetainExactTextAndSkip();
        }
}
