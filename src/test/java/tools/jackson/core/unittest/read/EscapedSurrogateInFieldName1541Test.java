package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test}
 */
class EscapedSurrogateInFieldName1541Test {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test}.
 * Original test methods: {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test#nameVariationsAposDataInput()}.
 */
    @Test
    void literalSupplementaryFieldNameRemainsExact() throws Exception {
            new T32_0067Fixture().__invoke_literalSupplementaryFieldNameRemainsExact();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test}.
 * Original test methods: {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test#surrogateInFieldNameStream()}, {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test#surrogateInFieldNameStreamThrottled()}.
 */
    @Test
    void supplementaryFieldNameRemainsExactAcrossStreamChunking() throws Exception {
            new T32_0068F0().__invoke_supplementaryFieldNameRemainsExactAcrossStreamChunking();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test}.
 * Original test methods: {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test#surrogateInStringValueDataInput()}, {@link tools.jackson.core.unittest.read.EscapedSurrogateInFieldName1541Test#surrogateInStringValueStream()}.
 */
    @Test
    void supplementaryStringValueRemainsExactAcrossBinarySources() throws Exception {
            new T32_0068F0().__invoke_supplementaryStringValueRemainsExactAcrossBinarySources();
        }
}
