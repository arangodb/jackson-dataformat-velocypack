package tools.jackson.core.unittest.io.schubfach;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest}
 * {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest}
 */
class DoubleToStringTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest}, {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest#simpleCases()}, {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest#nonNumericQuoting()}.
 */
    @Test
    void simpleDoubleValuesUseLiteralNativeBinary64() throws Exception {
            new T32_0032F0().__invoke_simpleDoubleValuesUseLiteralNativeBinary64();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest#switchToSubnormal()}.
 */
    @Test
    void subnormalTransitionUsesLiteralNativeBinary64() throws Exception {
            new T32_0032F0().__invoke_subnormalTransitionUsesLiteralNativeBinary64();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest#regressionTest()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest#roundingModeEven()}.
 */
    @Test
    void roundingAndRegressionValuesRemainExactBinary64() throws Exception {
            new T32_0032F0().__invoke_roundingAndRegressionValuesRemainExactBinary64();
        }
}
