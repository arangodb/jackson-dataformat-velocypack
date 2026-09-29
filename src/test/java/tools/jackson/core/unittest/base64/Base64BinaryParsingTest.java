package tools.jackson.core.unittest.base64;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}
 * {@link tools.jackson.core.unittest.base64.Base64CodecTest}
 * {@link tools.jackson.core.unittest.io.TestCharTypes}
 * {@link tools.jackson.core.unittest.io.TestJsonStringEncoder}
 */
class Base64BinaryParsingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#streaming()}.
 */
    @Test
    void nativeBinaryCanBeStreamedFromLiteralObjectAcrossBinarySources() throws Exception {
            new T32_0007Fixture().__invoke_nativeBinaryCanBeStreamedFromLiteralObjectAcrossBinarySources();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#inArray()}.
 */
    @Test
    void nativeBinaryValuesInLiteralIndexedArrayAreReadableInOrder() throws Exception {
            new T32_0007Fixture().__invoke_nativeBinaryValuesInLiteralIndexedArrayAreReadableInOrder();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#simple()}.
 */
    @Test
    void vpackTextBinaryDecodingAcceptsBoundaryWhitespace() throws Exception {
            new T32_0007Fixture().__invoke_vpackTextBinaryDecodingAcceptsBoundaryWhitespace();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#invalidTokenForBase64()}.
 */
    @Test
    void binaryAccessorRejectsLiteralArrayToken() throws Exception {
            new T32_0007Fixture().__invoke_binaryAccessorRejectsLiteralArrayToken();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#failDueToMissingPadding()}.
 */
    @Test
    void vpackTextBinaryDecodingRetainsMissingPaddingRules() throws Exception {
            new T32_0007Fixture().__invoke_vpackTextBinaryDecodingRetainsMissingPaddingRules();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}, {@link tools.jackson.core.unittest.base64.Base64CodecTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#okMissingPadding()}, {@link tools.jackson.core.unittest.base64.Base64CodecTest#paddingReadBehaviour()}.
 */
    @Test
    void vpackTextBinaryDecodingAcceptsUrlMissingPadding() throws Exception {
            new T32_0007Fixture().__invoke_vpackTextBinaryDecodingAcceptsUrlMissingPadding();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}, {@link tools.jackson.core.unittest.base64.Base64CodecTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#invalidChar()}, {@link tools.jackson.core.unittest.base64.Base64CodecTest#errors()}.
 */
    @Test
    void vpackTextBinaryDecodingRejectsInvalidCharactersAndPadding() throws Exception {
            new T32_0007Fixture().__invoke_vpackTextBinaryDecodingRejectsInvalidCharactersAndPadding();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}, {@link tools.jackson.core.unittest.io.TestCharTypes}, {@link tools.jackson.core.unittest.io.TestJsonStringEncoder}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#withEscaped()}, {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#withEscapedPadding()}, {@link tools.jackson.core.unittest.io.TestCharTypes#appendQuoted031()}, {@link tools.jackson.core.unittest.io.TestJsonStringEncoder#charSequenceWithCtrlChars()}, {@link tools.jackson.core.unittest.io.TestJsonStringEncoder#quoteAsUTF8()}, {@link tools.jackson.core.unittest.io.TestJsonStringEncoder#quoteCharSequenceAsString()}.
 */
    @Test
    void vpackStringPayloadDoesNotInterpretJsonEscapes() throws Exception {
            new T32_0007Fixture().__invoke_vpackStringPayloadDoesNotInterpretJsonEscapes();
        }
}
