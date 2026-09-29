package tools.jackson.databind.misc;

import org.junit.jupiter.api.RepeatedTest;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.misc.ThreadSafetyWithConverterMixin5813Test}
 */
class ThreadSafetyWithConverterMixin5813Test {
/**
 * VPack adaptation of {@link tools.jackson.databind.misc.ThreadSafetyWithConverterMixin5813Test}.
 * Original test methods: {@link tools.jackson.databind.misc.ThreadSafetyWithConverterMixin5813Test#testConcurrentSerializationWithConverterMixin()}.
 */
    @RepeatedTest(value = 50, name = "testConcurrentSerializationWithConverterMixinVpack[{currentRepetition}/{totalRepetitions}]")
    // Provenance: ThreadSafetyWithConverterMixin5813Test#testConcurrentSerializationWithConverterMixin().
    void testConcurrentSerializationWithConverterMixinVpack() throws Exception {
            new T32_0462F0().__invoke_testConcurrentSerializationWithConverterMixinVpack();
        }
}
