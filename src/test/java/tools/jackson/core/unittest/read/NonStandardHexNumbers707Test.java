package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.NonStandardHexNumbers707Test}
 */
class NonStandardHexNumbers707Test {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NonStandardHexNumbers707Test}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NonStandardHexNumbers707Test#unsignedHexWithLeadingZeros()}.
 */
    @Test
    void unsignedHexWithLeadingZerosRetainsExactVpackValue() throws Exception {
            new T32_0073Fixture().__invoke_unsignedHexWithLeadingZerosRetainsExactVpackValue();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NonStandardHexNumbers707Test}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NonStandardHexNumbers707Test#hexLongRange()}.
 */
    @Test
    void hexLongRangeRetainsExactVpackLongValue() throws Exception {
            new T32_0073Fixture().__invoke_hexLongRangeRetainsExactVpackLongValue();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NonStandardHexNumbers707Test}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NonStandardHexNumbers707Test#unsignedHexZero()}.
 */
    @Test
    void unsignedHexZeroRetainsExactVpackIntegerValue() throws Exception {
            new T32_0074Fixture().__invoke_unsignedHexZeroRetainsExactVpackIntegerValue();
        }
}
