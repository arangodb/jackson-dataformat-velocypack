package tools.jackson.databind.misc;

import org.junit.jupiter.api.RepeatedTest;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.misc.ThreadSafetyWithPolymorphicSer5615Test}
 */
class ThreadSafetyWithPolymorphicSer5615Test {
/**
 * VPack adaptation of {@link tools.jackson.databind.misc.ThreadSafetyWithPolymorphicSer5615Test}.
 * Original test methods: {@link tools.jackson.databind.misc.ThreadSafetyWithPolymorphicSer5615Test#testConcurrentDeserializationWithJsonIgnoreAndTypeInfo()}.
 */
    @RepeatedTest(value = 50, name = "testConcurrentDeserializationWithJsonIgnoreAndTypeInfoVpack[{currentRepetition}/{totalRepetitions}]")
    // Provenance: ThreadSafetyWithPolymorphicSer5615Test#testConcurrentDeserializationWithJsonIgnoreAndTypeInfo().
    void testConcurrentDeserializationWithJsonIgnoreAndTypeInfoVpack() throws Exception {
            new T32_0462F1().__invoke_testConcurrentDeserializationWithJsonIgnoreAndTypeInfoVpack();
        }
}
