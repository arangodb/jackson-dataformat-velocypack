package tools.jackson.core.unittest.util;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest}
 */
class ByteArrayBuilderTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest}.
 * Original test methods: {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest#testSimple()}.
 */
    @Test
    void byteArrayBuilderSimpleContentIsPreserved() throws Exception {
            new T32_0105Fixture().__invoke_byteArrayBuilderSimpleContentIsPreserved();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest}.
 * Original test methods: {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest#testAppendFourBytesWithPositive()}.
 */
    @Test
    void byteArrayBuilderAppendFourBytesWithPositiveValue() throws Exception {
            new T32_0105Fixture().__invoke_byteArrayBuilderAppendFourBytesWithPositiveValue();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest}.
 * Original test methods: {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest#testAppendTwoBytesWithZero()}.
 */
    @Test
    void byteArrayBuilderAppendTwoBytesWithZeroValue() throws Exception {
            new T32_0105Fixture().__invoke_byteArrayBuilderAppendTwoBytesWithZeroValue();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest}.
 * Original test methods: {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest#testFinishCurrentSegment()}.
 */
    @Test
    void byteArrayBuilderFinishCurrentSegmentResetsCurrentLength() throws Exception {
            new T32_0105Fixture().__invoke_byteArrayBuilderFinishCurrentSegmentResetsCurrentLength();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest}.
 * Original test methods: {@link tools.jackson.core.unittest.util.ByteArrayBuilderTest#testBufferRecyclerReuse()}.
 */
    @Test
    void byteArrayBuilderRecyclerRemainsLinkedThroughVpackGeneratorClose() throws Exception {
            new T32_0105Fixture().__invoke_byteArrayBuilderRecyclerRemainsLinkedThroughVpackGeneratorClose();
        }
}
