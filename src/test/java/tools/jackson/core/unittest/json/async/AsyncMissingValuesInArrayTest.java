package tools.jackson.core.unittest.json.async;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInArrayTest}
 * {@link tools.jackson.core.unittest.read.ArrayParsingTest}
 */
class AsyncMissingValuesInArrayTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInArrayTest}, {@link tools.jackson.core.unittest.read.ArrayParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInArrayTest#arrayInnerComma(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInArrayTest#arrayLeadingComma(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInArrayTest#arrayTrailingComma(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInArrayTest#arrayTrailingCommas(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInArrayTest#arrayTrailingCommasTriple(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.ArrayParsingTest#testMissingValueAsNullByEnablingFeature()}, {@link tools.jackson.core.unittest.read.ArrayParsingTest#testNotMissingValueByEnablingFeature()}.
 */
    @Test
    void literalVpackArraysPreserveAcceptedMissingValueResults() throws Exception {
            new T32_0050Fixture().__invoke_literalVpackArraysPreserveAcceptedMissingValueResults();
        }
}
