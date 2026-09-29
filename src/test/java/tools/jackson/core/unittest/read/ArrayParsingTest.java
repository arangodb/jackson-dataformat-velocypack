package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.ArrayParsingTest}
 * {@link tools.jackson.core.unittest.read.TrailingCommasTest}
 */
class ArrayParsingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ArrayParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ArrayParsingTest#testInvalidEmptyMissingClose()}.
 */
    @Test
    void testInvalidEmptyMissingClose() throws Exception {
            new T32_0064Fixture().__invoke_testInvalidEmptyMissingClose();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ArrayParsingTest}, {@link tools.jackson.core.unittest.read.TrailingCommasTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ArrayParsingTest#testInvalidExtraComma()}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#arrayInnerComma(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#arrayLeadingComma(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#arrayTrailingComma(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#arrayTrailingCommas(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#arrayTrailingCommasTriple(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#objectInnerComma(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#objectLeadingComma(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#objectTrailingComma(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#objectTrailingCommaWithNextFieldName(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#objectTrailingCommaWithNextFieldNameStr(int,List<JsonReadFeature>)}, {@link tools.jackson.core.unittest.read.TrailingCommasTest#objectTrailingCommas(int,List<JsonReadFeature>)}.
 */
    @Test
    void testInvalidExtraComma() throws Exception {
            new T32_0064Fixture().__invoke_testInvalidExtraComma();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ArrayParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ArrayParsingTest#testInvalidMissingFieldName()}.
 */
    @Test
    void testInvalidMissingFieldName() throws Exception {
            new T32_0065Fixture().__invoke_testInvalidMissingFieldName();
        }
}
