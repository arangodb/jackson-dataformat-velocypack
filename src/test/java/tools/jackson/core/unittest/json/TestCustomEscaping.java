package tools.jackson.core.unittest.json;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.TestCustomEscaping}
 */
class TestCustomEscaping {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCustomEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCustomEscaping#aboveAsciiEscapeWithReader()}.
 */
    @Test
    void aboveAsciiCharactersRemainLiteralForReaderAndCharArrayInputs() throws Exception {
            new T32_0042F2().__invoke_aboveAsciiCharactersRemainLiteralForReaderAndCharArrayInputs();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCustomEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCustomEscaping#aboveAsciiEscapeWithUTF8Stream()}.
 */
    @Test
    void aboveAsciiCharactersRemainLiteralForUtf8Output() throws Exception {
            new T32_0043F0().__invoke_aboveAsciiCharactersRemainLiteralForUtf8Output();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCustomEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCustomEscaping#escapeCustomWithReader()}, {@link tools.jackson.core.unittest.json.TestCustomEscaping#escapeCustomWithUTF8Stream()}.
 */
    @Test
    void customEscapesAreTextOnlyAndVpackKeepsInputCharacters() throws Exception {
            new T32_0043F0().__invoke_customEscapesAreTextOnlyAndVpackKeepsInputCharacters();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCustomEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCustomEscaping#jsonpEscapes()}.
 */
    @Test
    void jsonpLineSeparatorsAreLiteralUtf8Characters() throws Exception {
            new T32_0043F0().__invoke_jsonpLineSeparatorsAreLiteralUtf8Characters();
        }
}
