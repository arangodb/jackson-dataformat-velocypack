package tools.jackson.core.unittest.write;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.write.UTF8GeneratorTest}
 * {@link tools.jackson.core.unittest.write.WriterBasedJsonGeneratorTest}
 */
class UTF8GeneratorTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.UTF8GeneratorTest}.
 * Original test methods: {@link tools.jackson.core.unittest.write.UTF8GeneratorTest#utf8Issue462()}.
 */
    @Test
    void utf8BoundarySequenceRetainsAllNumbersAndSurrogateString() throws Exception {
            new T32_0124F1().__invoke_utf8BoundarySequenceRetainsAllNumbersAndSurrogateString();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.UTF8GeneratorTest}, {@link tools.jackson.core.unittest.write.WriterBasedJsonGeneratorTest}.
 * Original test methods: {@link tools.jackson.core.unittest.write.UTF8GeneratorTest#nestingDepthWithSmallLimit()}, {@link tools.jackson.core.unittest.write.WriterBasedJsonGeneratorTest#nestingDepthWithSmallLimit()}.
 */
    @Test
    void nestingDepthWithSmallLimit() throws Exception {
            new T32_0124F1().__invoke_nestingDepthWithSmallLimit();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.UTF8GeneratorTest}, {@link tools.jackson.core.unittest.write.WriterBasedJsonGeneratorTest}.
 * Original test methods: {@link tools.jackson.core.unittest.write.UTF8GeneratorTest#nestingDepthWithSmallLimitNestedObject()}, {@link tools.jackson.core.unittest.write.WriterBasedJsonGeneratorTest#nestingDepthWithSmallLimitNestedObject()}.
 */
    @Test
    void nestingDepthWithSmallLimitNestedObject() throws Exception {
            new T32_0124F1().__invoke_nestingDepthWithSmallLimitNestedObject();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.UTF8GeneratorTest}.
 * Original test methods: {@link tools.jackson.core.unittest.write.UTF8GeneratorTest#surrogatesWithRaw()}.
 */
    @Test
    void rawSurrogateTextIsExplicitlyUnsupported() throws Exception {
            new T32_0124F1().__invoke_rawSurrogateTextIsExplicitlyUnsupported();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.UTF8GeneratorTest}.
 * Original test methods: {@link tools.jackson.core.unittest.write.UTF8GeneratorTest#filteringWithEscapedChars()}.
 */
    @Test
    void filteringWithEscapedCharsUsesLiteralVpackStringBytes() throws Exception {
            new T32_0124F1().__invoke_filteringWithEscapedCharsUsesLiteralVpackStringBytes();
        }
}
