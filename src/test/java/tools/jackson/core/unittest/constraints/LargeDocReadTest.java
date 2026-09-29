package tools.jackson.core.unittest.constraints;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.constraints.LargeDocReadTest}
 */
class LargeDocReadTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeDocReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#dataInputWithDocLengthLimitEnforced()}.
 */
    @Test
    void dataInputRootLengthLimitIsEnforcedBeforeTheRootIsRetained() throws Exception {
            new T32_0010F2().__invoke_dataInputRootLengthLimitIsEnforcedBeforeTheRootIsRetained();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeDocReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#dataInputWithoutDocLengthLimitWorks()}.
 */
    @Test
    void dataInputRootWithoutDocumentLengthLimitWorks() throws Exception {
            new T32_0010F2().__invoke_dataInputRootWithoutDocumentLengthLimitWorks();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeDocReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#largeNameBytes()}.
 */
    @Test
    void largeBinaryRootFromInputStreamWorksUnderDefaultLimits() throws Exception {
            new T32_0010F2().__invoke_largeBinaryRootFromInputStreamWorksUnderDefaultLimits();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeDocReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#largeNameWithSmallLimitBytes()}.
 */
    @Test
    void largeRootDocumentLimitAppliesToInputStreamAndByteArray() throws Exception {
            new T32_0011F0().__invoke_largeRootDocumentLimitAppliesToInputStreamAndByteArray();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeDocReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeDocReadTest#tokenLimitBytes()}.
 */
    @Test
    void tokenLimitAppliesToLiteralCompactArrayFromInputStream() throws Exception {
            new T32_0011F0().__invoke_tokenLimitAppliesToLiteralCompactArrayFromInputStream();
        }
}
