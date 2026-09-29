package tools.jackson.core.unittest.constraints;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.constraints.LargeNameReadTest}
 */
class LargeNameReadTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeNameReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeNameReadTest#largeNameBytes()}.
 */
    @Test
    void largeNameBytesBelowVpackDefaultNameBudgetParsesFromInputStream() throws Exception {
            new T32_0011F1().__invoke_largeNameBytesBelowVpackDefaultNameBudgetParsesFromInputStream();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeNameReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeNameReadTest#largeNameWithSmallLimitBytes()}.
 */
    @Test
    void largeNameWithSmallLimitBytesIsRejectedFromInputStream() throws Exception {
            new T32_0011F1().__invoke_largeNameWithSmallLimitBytesIsRejectedFromInputStream();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeNameReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeNameReadTest#largeNameWithSmallLimitDataInput()}.
 */
    @Test
    void largeNameWithSmallLimitDataInputIsRejected() throws Exception {
            new T32_0011F1().__invoke_largeNameWithSmallLimitDataInputIsRejected();
        }
}
