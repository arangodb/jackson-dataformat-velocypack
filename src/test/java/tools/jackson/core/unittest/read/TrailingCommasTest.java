package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.TrailingCommasTest}
 */
class TrailingCommasTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.TrailingCommasTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.TrailingCommasTest#arrayBasic(int,List<JsonReadFeature>)}.
 */
    @Test
    void arrayBasicRetainsTokensAcrossBinarySources() throws Exception {
            new T32_0091Fixture().__invoke_arrayBasicRetainsTokensAcrossBinarySources();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.TrailingCommasTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.TrailingCommasTest#objectBasic(int,List<JsonReadFeature>)}.
 */
    @Test
    void objectBasicRetainsTokensAcrossBinarySources() throws Exception {
            new T32_0091Fixture().__invoke_objectBasicRetainsTokensAcrossBinarySources();
        }
}
