package tools.jackson.core.unittest.json.async;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest}
 * {@link tools.jackson.core.unittest.json.async.AsyncNaNHandlingTest}
 * {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest}
 * {@link tools.jackson.core.unittest.read.ArrayParsingTest}
 * {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest}
 */
class AsyncMissingValuesInObjectTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest#objectBasic(Collection<JsonReadFeature>)}.
 */
    @Test
    void literalBooleanObjectRetainsObjectTokenSemantics() throws Exception {
            new T32_0051F0().__invoke_literalBooleanObjectRetainsObjectTokenSemantics();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest}, {@link tools.jackson.core.unittest.json.async.AsyncNaNHandlingTest}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest}, {@link tools.jackson.core.unittest.read.ArrayParsingTest}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest#objectBasic(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest#objectInnerComma(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest#objectLeadingComma(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest#objectTrailingComma(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncMissingValuesInObjectTest#objectTrailingCommas(Collection<JsonReadFeature>)}, {@link tools.jackson.core.unittest.json.async.AsyncNaNHandlingTest#allowInf()}, {@link tools.jackson.core.unittest.json.async.AsyncNaNHandlingTest#allowNaN()}, {@link tools.jackson.core.unittest.json.async.AsyncNaNHandlingTest#defaultsForAsync()}, {@link tools.jackson.core.unittest.json.async.AsyncNaNHandlingTest#disallowInf()}, {@link tools.jackson.core.unittest.json.async.AsyncNaNHandlingTest#disallowNaN()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#doubleMarker()}, {@link tools.jackson.core.unittest.json.async.AsyncNonStandardNumberParsingTest#floatMarker()}, {@link tools.jackson.core.unittest.read.ArrayParsingTest#testMissingValueAsNullByNotEnablingFeature()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#doubleMarker()}, {@link tools.jackson.core.unittest.read.NonStandardNumberParsingTest#floatMarker()}.
 */
    @Test
    void jsonOnlyMissingAndNonstandardSpellingsHaveNoVpackWireSurface() throws Exception {
            new T32_0051F0().__invoke_jsonOnlyMissingAndNonstandardSpellingsHaveNoVpackWireSurface();
        }
}
