package tools.jackson.core.unittest.fuzz;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.fuzz.Fuzz52688ParseTest}
 */
class Fuzz52688ParseTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.fuzz.Fuzz52688ParseTest}.
 * Original test methods: {@link tools.jackson.core.unittest.fuzz.Fuzz52688ParseTest#bigNumberUTF16Parse()}.
 */
    @Test
    void bigNumberByteArrayRemainsExactBeforeMalformedNextRoot() throws Exception {
            new T32_0024Fixture().__invoke_bigNumberByteArrayRemainsExactBeforeMalformedNextRoot();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.fuzz.Fuzz52688ParseTest}.
 * Original test methods: {@link tools.jackson.core.unittest.fuzz.Fuzz52688ParseTest#bigNumberUTF8Parse()}.
 */
    @Test
    void bigNumberOneByteStreamRemainsExactBeforeMalformedNextRoot() throws Exception {
            new T32_0024Fixture().__invoke_bigNumberOneByteStreamRemainsExactBeforeMalformedNextRoot();
        }
}
