package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.ParserDupHandlingTest}
 */
class ParserDupHandlingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserDupHandlingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserDupHandlingTest#simpleDupCheckDisabled()}.
 */
    @Test
    void duplicateNamesAreAcceptedWhenStrictDetectionIsDisabled() throws Exception {
            new T32_0083F2().__invoke_duplicateNamesAreAcceptedWhenStrictDetectionIsDisabled();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserDupHandlingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserDupHandlingTest#simpleDupsBytes()}, {@link tools.jackson.core.unittest.read.ParserDupHandlingTest#simpleDupsDataInput()}.
 */
    @Test
    void duplicateNamesAreRejectedAcrossBinarySourcesWhenStrictDetectionIsEnabled()
            throws Exception {
            new T32_0083F2().__invoke_duplicateNamesAreRejectedAcrossBinarySourcesWhenStrictDetectionIsEnabled();
        }
}
