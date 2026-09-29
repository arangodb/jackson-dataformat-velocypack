package tools.jackson.core.unittest.write;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.write.SurrogateWrite223Test}
 */
class SurrogateWrite223Test {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.SurrogateWrite223Test}.
 * Original test methods: {@link tools.jackson.core.unittest.write.SurrogateWrite223Test#surrogatesByteBacked()}.
 */
    @Test
    void surrogateByteBackedStringUsesLiteralUtf8() throws Exception {
            new T32_0124F0().__invoke_surrogateByteBackedStringUsesLiteralUtf8();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.SurrogateWrite223Test}.
 * Original test methods: {@link tools.jackson.core.unittest.write.SurrogateWrite223Test#surrogatesCharBacked()}.
 */
    @Test
    void surrogateCharBackedStringUsesLiteralUtf8() throws Exception {
            new T32_0124F0().__invoke_surrogateCharBackedStringUsesLiteralUtf8();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.SurrogateWrite223Test}.
 * Original test methods: {@link tools.jackson.core.unittest.write.SurrogateWrite223Test#checkNonSurrogates()}.
 */
    @Test
    void nonSurrogateUnicodeAndEmojiPreserveCallOrderAndWireBytes() throws Exception {
            new T32_0124F0().__invoke_nonSurrogateUnicodeAndEmojiPreserveCallOrderAndWireBytes();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.SurrogateWrite223Test}.
 * Original test methods: {@link tools.jackson.core.unittest.write.SurrogateWrite223Test#surrogateCharSplitInTwoSegments()}.
 */
    @Test
    void surrogateAtSegmentBoundaryRemainsOneUtf8String() throws Exception {
            new T32_0124F0().__invoke_surrogateAtSegmentBoundaryRemainsOneUtf8String();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.write.SurrogateWrite223Test}.
 * Original test methods: {@link tools.jackson.core.unittest.write.SurrogateWrite223Test#checkSurrogateWithCharacterEscapes()}, {@link tools.jackson.core.unittest.write.SurrogateWrite223Test#surrogatesDefaultSetting()}.
 */
    @Test
    void characterEscapesHaveAnExplicitVpackUnsupportedSeam() throws Exception {
            new T32_0124F0().__invoke_characterEscapesHaveAnExplicitVpackUnsupportedSeam();
        }
}
