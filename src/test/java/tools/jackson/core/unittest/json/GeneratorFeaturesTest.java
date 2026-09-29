package tools.jackson.core.unittest.json;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest}
 */
class GeneratorFeaturesTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest#configDefaults()}.
 */
    @Test
    void vpackGeneratorDefaultsExposeBinaryCapabilitiesAndCoreDefaults() throws Exception {
            new T32_0040F0().__invoke_vpackGeneratorDefaultsExposeBinaryCapabilitiesAndCoreDefaults();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest#bigDecimalAsPlain()}, {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest#bigDecimalAsPlainString()}.
 */
    @Test
    void vpackBigDecimalPlainFeatureRetainsExactBcdValueAndScale() throws Exception {
            new T32_0040F0().__invoke_vpackBigDecimalPlainFeatureRetainsExactBcdValueAndScale();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest#numbersAsJSONStrings()}.
 */
    @Test
    void numbersAsJSONStringsHaveNoVpackStringMode() throws Exception {
            new T32_0041F0().__invoke_numbersAsJSONStringsHaveNoVpackStringMode();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.GeneratorFeaturesTest#tooBigBigDecimal()}.
 */
    @Test
    void tooBigBigDecimalUsesVpackExponentInsteadOfPlainTextScaleLimits() throws Exception {
            new T32_0041F0().__invoke_tooBigBigDecimalUsesVpackExponentInsteadOfPlainTextScaleLimits();
        }
}
