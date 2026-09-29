package tools.jackson.databind.introspect;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import tools.jackson.databind.EnumNamingStrategy;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.introspect.EnumNamingStrategiesTest}
 */
class EnumNamingStrategiesTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.introspect.EnumNamingStrategiesTest}.
 * Original test methods: {@link tools.jackson.databind.introspect.EnumNamingStrategiesTest#testEnumNameConversions(EnumNamingStrategy,String,String)}.
 */
    @ParameterizedTest(name = "testEnumNameConversionsVpack[{index}] - {0}: {1} -> {2}")
    @MethodSource("tools.jackson.databind.introspect.T32_0394F1#enumNameConversionTestCases")
    // Provenance: EnumNamingStrategiesTest#testEnumNameConversions(EnumNamingStrategy,String,String).
    void testEnumNameConversionsVpack(EnumNamingStrategy strategy, String input, String output) throws Exception {
            new T32_0394F1().__invoke_testEnumNameConversionsVpack(strategy, input, output);
        }
}
