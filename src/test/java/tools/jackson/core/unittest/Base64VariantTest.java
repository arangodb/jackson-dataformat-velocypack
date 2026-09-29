package tools.jackson.core.unittest;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.Base64VariantTest}
 * {@link tools.jackson.core.unittest.JDKSerializabilityTest}
 * {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}
 * {@link tools.jackson.core.unittest.base64.Base64CodecTest}
 * {@link tools.jackson.core.unittest.base64.Base64GenerationTest}
 */
class Base64VariantTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.Base64VariantTest}, {@link tools.jackson.core.unittest.base64.Base64CodecTest}.
 * Original test methods: {@link tools.jackson.core.unittest.Base64VariantTest#testDecodeTaking2ArgumentsOne()}, {@link tools.jackson.core.unittest.base64.Base64CodecTest#convenienceMethods()}, {@link tools.jackson.core.unittest.base64.Base64CodecTest#props()}.
 */
    @Test
    void vpackStringBinaryDecodingHonorsConfiguredBase64Variant() throws Exception {
            new T32_0001Fixture().__invoke_vpackStringBinaryDecodingHonorsConfiguredBase64Variant();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.Base64VariantTest}.
 * Original test methods: {@link tools.jackson.core.unittest.Base64VariantTest#testDecodeTaking2ArgumentsThrowsIllegalArgumentException()}.
 */
    @Test
    void vpackStringBinaryDecodingRejectsInvalidBase64() throws Exception {
            new T32_0001Fixture().__invoke_vpackStringBinaryDecodingRejectsInvalidBase64();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.Base64VariantTest}, {@link tools.jackson.core.unittest.JDKSerializabilityTest}, {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest}, {@link tools.jackson.core.unittest.base64.Base64CodecTest}, {@link tools.jackson.core.unittest.base64.Base64GenerationTest}.
 * Original test methods: {@link tools.jackson.core.unittest.Base64VariantTest#testEncodeTaking2ArgumentsWithTrue()}, {@link tools.jackson.core.unittest.Base64VariantTest#test_reportInvalidBase64ThrowsIllegalArgumentException()}, {@link tools.jackson.core.unittest.JDKSerializabilityTest#base64Variant()}, {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#base64UsingInputStream()}, {@link tools.jackson.core.unittest.base64.Base64BinaryParsingTest#base64UsingReader()}, {@link tools.jackson.core.unittest.base64.Base64CodecTest#charEncoding()}, {@link tools.jackson.core.unittest.base64.Base64CodecTest#convenienceMethodWithLFs()}, {@link tools.jackson.core.unittest.base64.Base64CodecTest#props()}, {@link tools.jackson.core.unittest.base64.Base64CodecTest#variantAccess()}, {@link tools.jackson.core.unittest.base64.Base64GenerationTest#simpleBinaryWrite()}.
 */
    @Test
    void writeBinaryUsesNativeVpackBytesInsteadOfBase64Text() throws Exception {
            new T32_0001Fixture().__invoke_writeBinaryUsesNativeVpackBytesInsteadOfBase64Text();
        }
}
