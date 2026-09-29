package tools.jackson.core.unittest.json.async;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.async.AsyncRootValuesTest}
 * {@link tools.jackson.core.unittest.json.async.AsyncScalarArrayTest}
 */
class AsyncRootValuesTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncRootValuesTest}, {@link tools.jackson.core.unittest.json.async.AsyncScalarArrayTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncRootValuesTest#mixedRootSequence()}, {@link tools.jackson.core.unittest.json.async.AsyncRootValuesTest#tokenRootSequence()}, {@link tools.jackson.core.unittest.json.async.AsyncRootValuesTest#tokenRootTokens()}, {@link tools.jackson.core.unittest.json.async.AsyncScalarArrayTest#ints()}, {@link tools.jackson.core.unittest.json.async.AsyncScalarArrayTest#tokens()}.
 */
    @Test
    void literalVpackMixedRootSequencePreservesPortableTokensAndValues() throws Exception {
            new T32_0058Fixture().__invoke_literalVpackMixedRootSequencePreservesPortableTokensAndValues();
        }
}
