package tools.jackson.core.unittest.constraints;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest}
 * {@link tools.jackson.core.unittest.io.BigDecimalParserTest}
 */
class LargeNumberReadTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest#bigBigDecimalsBytes()}.
 */
    @Test
    void largeBigDecimalsBytesFailWithDefaultNumberLength() throws Exception {
            new T32_0012F0().__invoke_largeBigDecimalsBytesFailWithDefaultNumberLength();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest}, {@link tools.jackson.core.unittest.io.BigDecimalParserTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest#bigBigDecimalsBytesFailByDefault()}, {@link tools.jackson.core.unittest.io.BigDecimalParserTest#longValidStringFastParse()}, {@link tools.jackson.core.unittest.io.BigDecimalParserTest#longValidStringParse()}.
 */
    @Test
    void largeBigDecimalsBytesReadExactlyWithUnlimitedNumberLength() throws Exception {
            new T32_0012F0().__invoke_largeBigDecimalsBytesReadExactlyWithUnlimitedNumberLength();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest#bigBigDecimalsDataInputFailByDefault()}.
 */
    @Test
    void largeBigDecimalsDataInputFailsWithDefaultNumberLength() throws Exception {
            new T32_0012F0().__invoke_largeBigDecimalsDataInputFailsWithDefaultNumberLength();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest}, {@link tools.jackson.core.unittest.io.BigDecimalParserTest}.
 * Original test methods: {@link tools.jackson.core.unittest.constraints.LargeNumberReadTest#bigBigDecimalsDataInput()}, {@link tools.jackson.core.unittest.io.BigDecimalParserTest#longValidStringFastParse()}, {@link tools.jackson.core.unittest.io.BigDecimalParserTest#longValidStringParse()}.
 */
    @Test
    void largeBigDecimalsDataInputReadsExactlyWithUnlimitedNumberLength() throws Exception {
            new T32_0012F0().__invoke_largeBigDecimalsDataInputReadsExactlyWithUnlimitedNumberLength();
        }
}
