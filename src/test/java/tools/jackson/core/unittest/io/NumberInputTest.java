package tools.jackson.core.unittest.io;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.io.NumberInputTest}
 */
class NumberInputTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.NumberInputTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.NumberInputTest#nastySmallDouble()}.
 */
    @Test
    void literalMinimumNormalDoubleDoesNotUnderflow() throws Exception {
            new T32_0027Fixture().__invoke_literalMinimumNormalDoubleDoesNotUnderflow();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.NumberInputTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.NumberInputTest#inLongRangeFromCharArray()}, {@link tools.jackson.core.unittest.io.NumberInputTest#inLongRangeFromString()}.
 */
    @Test
    void longRangeBoundariesRemainExactAcrossBinarySources() throws Exception {
            new T32_0027Fixture().__invoke_longRangeBoundariesRemainExactAcrossBinarySources();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.NumberInputTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.NumberInputTest#parseLong19Digits()}.
 */
    @Test
    void literalNineteenDigitValuesRemainExact() throws Exception {
            new T32_0028F0().__invoke_literalNineteenDigitValuesRemainExact();
        }
}
