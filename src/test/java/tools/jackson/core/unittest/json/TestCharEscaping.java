package tools.jackson.core.unittest.json;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.TestCharEscaping}
 * {@link tools.jackson.core.unittest.json.async.AsyncCharEscapingTest}
 * {@link tools.jackson.databind.ser.CustomSerializersTest}
 */
class TestCharEscaping {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCharEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCharEscaping#escapeNonLatin1Bytes()}, {@link tools.jackson.core.unittest.json.TestCharEscaping#escapeNonLatin1Chars()}.
 */
    @Test
    void nonLatin1CharactersAreLiteralUtf8ForStringCharAndUtf8Overloads() throws Exception {
            new T32_0042F1().__invoke_nonLatin1CharactersAreLiteralUtf8ForStringCharAndUtf8Overloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCharEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCharEscaping#escapesForCharArrays()}.
 */
    @Test
    void charArrayWritingPreservesAnEmbeddedNul() throws Exception {
            new T32_0042F1().__invoke_charArrayWritingPreservesAnEmbeddedNul();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCharEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCharEscaping#missingEscaping()}, {@link tools.jackson.core.unittest.json.TestCharEscaping#simpleEscaping()}.
 */
    @Test
    void validTextEscapeSpellingsBecomeLiteralVpackCharacters() throws Exception {
            new T32_0042F1().__invoke_validTextEscapeSpellingsBecomeLiteralVpackCharacters();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCharEscaping}, {@link tools.jackson.core.unittest.json.async.AsyncCharEscapingTest}, {@link tools.jackson.databind.ser.CustomSerializersTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCharEscaping#invalid()}, {@link tools.jackson.core.unittest.json.TestCharEscaping#invalidEscape()}, {@link tools.jackson.core.unittest.json.TestCharEscaping#test8DigitSequence()}, {@link tools.jackson.core.unittest.json.async.AsyncCharEscapingTest#missingLinefeedEscaping()}, {@link tools.jackson.core.unittest.json.async.AsyncCharEscapingTest#simpleEscaping()}, {@link tools.jackson.core.unittest.json.async.AsyncCharEscapingTest#test8DigitSequence()}, {@link tools.jackson.databind.ser.CustomSerializersTest#testCustomEscapes()}.
 */
    @Test
    void jsonEscapeSyntaxIsNotInterpretedByVpack() throws Exception {
            new T32_0042F1().__invoke_jsonEscapeSyntaxIsNotInterpretedByVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCharEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCharEscaping#simpleNameEscaping()}.
 */
    @Test
    void quotedPropertyNamesAreLiteralAndTheNumericValueRemainsExact() throws Exception {
            new T32_0042F1().__invoke_quotedPropertyNamesAreLiteralAndTheNumericValueRemainsExact();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.TestCharEscaping}.
 * Original test methods: {@link tools.jackson.core.unittest.json.TestCharEscaping#writeLongCustomEscapes()}.
 */
    @Test
    void longUnicodeStringUsesLiteralLongStringEncoding() throws Exception {
            new T32_0042F1().__invoke_longUnicodeStringUsesLiteralLongStringEncoding();
        }
}
