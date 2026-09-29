package tools.jackson.core.unittest.base64;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.base64.Base64GenerationTest}
 * {@link tools.jackson.core.unittest.base64.BinaryWriteBufferSize1622Test}
 * {@link tools.jackson.core.unittest.write.GeneratorMiscTest}
 */
class Base64GenerationTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64GenerationTest}, {@link tools.jackson.core.unittest.write.GeneratorMiscTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64GenerationTest#binaryAsEmbeddedObject()}, {@link tools.jackson.core.unittest.write.GeneratorMiscTest#asEmbedded()}.
 */
    @Test
    void nativeBinaryEmbeddedObjectWriteUsesRawBytes() throws Exception {
            new T32_0008F1().__invoke_nativeBinaryEmbeddedObjectWriteUsesRawBytes();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64GenerationTest}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64GenerationTest#simpleBinaryWrite()}.
 */
    @Test
    void nativeBinaryWriteRetainsRootArrayAndObjectContexts() throws Exception {
            new T32_0008F1().__invoke_nativeBinaryWriteRetainsRootArrayAndObjectContexts();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.base64.Base64GenerationTest}, {@link tools.jackson.core.unittest.base64.BinaryWriteBufferSize1622Test}.
 * Original test methods: {@link tools.jackson.core.unittest.base64.Base64GenerationTest#streamingBinaryWrites()}, {@link tools.jackson.core.unittest.base64.BinaryWriteBufferSize1622Test#sizeHintAppliedByteBacked()}, {@link tools.jackson.core.unittest.base64.BinaryWriteBufferSize1622Test#sizeHintCappedByteBacked()}.
 */
    @Test
    void nativeBinaryStreamingWritesUseExactBytesForVariantsAndChunkSizes() throws Exception {
            new T32_0008F1().__invoke_nativeBinaryStreamingWritesUseExactBytesForVariantsAndChunkSizes();
        }
}
