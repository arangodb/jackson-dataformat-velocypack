package tools.jackson.core.unittest.io;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.io.BigDecimalParserTest}
 */
class BigDecimalParserTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.BigDecimalParserTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.BigDecimalParserTest#issueDatabind4694()}.
 */
    @Test
    void issueDatabind4694NegativeDecimalRetainsTrailingZeroScale() throws Exception {
            new T32_0025Fixture().__invoke_issueDatabind4694NegativeDecimalRetainsTrailingZeroScale();
        }
}
