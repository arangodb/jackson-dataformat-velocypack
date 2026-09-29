package tools.jackson.core.unittest.io.schubfach;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest}
 * {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest}
 */
class DoubleToDecimalTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#constants()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#extremeValues()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest#boundaryConditions()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToStringTest#minAndMax()}.
 */
    @Test
    void doubleBoundariesUseExactNativeBinary64Fixtures() throws Exception {
            new T32_0031Fixture().__invoke_doubleBoundariesUseExactNativeBinary64Fixtures();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest}.
 * Original test methods: {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#hardValues()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#ints()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#longs()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#paxson()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#powersOf10()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#powersOf2()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#randomNumberTests()}, {@link tools.jackson.core.unittest.io.schubfach.DoubleToDecimalTest#someAnomalies()}.
 */
    @Test
    void doublePowerAndRandomFamiliesRemainRawBits() throws Exception {
            new T32_0031Fixture().__invoke_doublePowerAndRandomFamiliesRemainRawBits();
        }
}
