package tools.jackson.core.unittest.json.async;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest}
 * {@link tools.jackson.core.unittest.read.CommentParsingTest}
 */
class AsyncStringArrayTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest}, {@link tools.jackson.core.unittest.read.CommentParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest#longAsciiStrings()}, {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest#longAsciiStringsSmallLimit()}, {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest#longUnicodeStrings()}, {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest#shortAsciiStrings()}, {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest#shortUnicodeStrings()}, {@link tools.jackson.core.unittest.read.CommentParsingTest#commentsWithUTF8()}.
 */
    @Test
    void literalVpackStringArrayRetainsShortAndLongAsciiAndUnicodeValues() throws Exception {
            new T32_0060F0().__invoke_literalVpackStringArrayRetainsShortAndLongAsciiAndUnicodeValues();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncStringArrayTest#longAsciiStringsSmallLimit()}.
 */
    @Test
    void literalVpackStringArrayHonorsSmallStringConstraint() throws Exception {
            new T32_0060F0().__invoke_literalVpackStringArrayHonorsSmallStringConstraint();
        }
}
