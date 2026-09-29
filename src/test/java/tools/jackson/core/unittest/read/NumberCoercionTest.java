package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.NumberCoercionTest}
 * {@link tools.jackson.core.unittest.read.NumberParsingTest}
 */
class NumberCoercionTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NumberCoercionTest}, {@link tools.jackson.core.unittest.read.NumberParsingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NumberCoercionTest#toBigDecimalCoercion()}, {@link tools.jackson.core.unittest.read.NumberCoercionTest#toBigIntegerCoercion()}, {@link tools.jackson.core.unittest.read.NumberCoercionTest#toDoubleCoercion()}, {@link tools.jackson.core.unittest.read.NumberCoercionTest#toIntCoercion()}, {@link tools.jackson.core.unittest.read.NumberCoercionTest#toIntFailing()}, {@link tools.jackson.core.unittest.read.NumberCoercionTest#toLongCoercion()}, {@link tools.jackson.core.unittest.read.NumberCoercionTest#toLongFailing()}, {@link tools.jackson.core.unittest.read.NumberParsingTest#numbers()}.
 */
    @Test
    void numberCoercionAssertionsUseLiteralVpackFamilies() throws Exception {
            new T32_0078Fixture().__invoke_numberCoercionAssertionsUseLiteralVpackFamilies();
        }
}
