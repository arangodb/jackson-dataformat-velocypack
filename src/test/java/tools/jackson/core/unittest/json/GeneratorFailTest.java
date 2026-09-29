package tools.jackson.core.unittest.json;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.GeneratorFailTest}
 */
class GeneratorFailTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.GeneratorFailTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.GeneratorFailTest#dupFieldNameWrites()}.
 */
    @Test
    void consecutiveNamesAreRejected() throws Exception {
            new T32_0035F1().__invoke_consecutiveNamesAreRejected();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.GeneratorFailTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.GeneratorFailTest#failOnWritingFieldNameInRoot()}.
 */
    @Test
    void propertyNameAtRootIsRejected() throws Exception {
            new T32_0035F1().__invoke_propertyNameAtRootIsRejected();
        }
}
