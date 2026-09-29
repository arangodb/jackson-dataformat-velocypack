package tools.jackson.core.unittest.constraints;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest}
 * {@link tools.jackson.core.unittest.constraints.LargeDocReadTest}
 * {@link tools.jackson.core.unittest.constraints.LargeNameReadTest}
 * {@link tools.jackson.core.unittest.constraints.TokenCountTest}
 * {@link tools.jackson.databind.deser.DeserFromNonBlockingTest}
 */
class AsyncLargeNumberReadTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest}, {@link tools.jackson.core.unittest.constraints.LargeDocReadTest}, {@link tools.jackson.core.unittest.constraints.LargeNameReadTest}, {@link tools.jackson.core.unittest.constraints.TokenCountTest}, {@link tools.jackson.databind.deser.DeserFromNonBlockingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest#asyncParserFailsTooLongDecimal()}, {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest#asyncParserFailsTooLongDecimalWithExponent()}, {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest#asyncParserFailsTooLongInt()}, {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest#fractionPath_streamingChunks_rejectsBeyondMaxNumberLength()}, {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest#integerPath_justUnderMaxNumberLength_parsesCleanly()}, {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest#integerPath_smallChunksAccumulate_rejectAtBoundary()}, {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest#integerPath_streamingChunks_rejectsBeyondMaxNumberLength()}, {@link tools.jackson.core.unittest.constraints.AsyncLargeNumberReadTest#negativeIntegerPath_smallChunksAccumulate_rejectAtBoundary()}, {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#docLengthCountIntactAfterRejectedFeedByteBuffer()}, {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#docLengthCountIntactAfterRejectedFeedBytes()}, {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#largeNameWithSmallLimitAsync()}, {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#largeNameWithSmallLimitAsyncMultiFeedAtBoundary()}, {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#largeNameWithSmallLimitAsyncSingleFeed()}, {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#largeNameWithSmallLimitAsyncSingleFeedAtBoundary()}, {@link tools.jackson.core.unittest.constraints.LargeNameReadTest#largeNameWithSmallLimitAsync()}, {@link tools.jackson.core.unittest.constraints.TokenCountTest#arrayDocNonBlockingArray()}, {@link tools.jackson.core.unittest.constraints.TokenCountTest#arrayDocNonBlockingBuffer()}, {@link tools.jackson.core.unittest.constraints.TokenCountTest#sampleDocNonBlockingArray()}, {@link tools.jackson.core.unittest.constraints.TokenCountTest#sampleDocNonBlockingBuffer()}, {@link tools.jackson.core.unittest.constraints.TokenCountTest#shortArrayDocNonBlockingArray()}, {@link tools.jackson.core.unittest.constraints.TokenCountTest#shortArrayDocNonBlockingBuffer()}, {@link tools.jackson.databind.deser.DeserFromNonBlockingTest#testNonBlockingByteArrayParserViaMapper()}, {@link tools.jackson.databind.deser.DeserFromNonBlockingTest#testNonBlockingByteArrayParserViaReader()}, {@link tools.jackson.databind.deser.DeserFromNonBlockingTest#testNonBlockingByteBufferParserViaMapper()}, {@link tools.jackson.databind.deser.DeserFromNonBlockingTest#testNonBlockingByteBufferParserViaReader()}.
 */
    @Test
    void nonBlockingParserIsAnExplicitlyUnsupportedVpackCapability() throws Exception {
            new T32_0009Fixture().__invoke_nonBlockingParserIsAnExplicitlyUnsupportedVpackCapability();
        }
}
