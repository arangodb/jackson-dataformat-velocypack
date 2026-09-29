package tools.jackson.core.unittest.json;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.GeneratorFailFromReaderTest}
 * {@link tools.jackson.core.unittest.json.GeneratorFailTest}
 */
class GeneratorFailFromReaderTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.GeneratorFailFromReaderTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.GeneratorFailFromReaderTest#failOnWritingStringFromNullReader()}.
 */
    @Test
    void readerNullIsRejected() throws Exception {
            new T32_0035F0().__invoke_readerNullIsRejected();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.GeneratorFailFromReaderTest}, {@link tools.jackson.core.unittest.json.GeneratorFailTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.GeneratorFailFromReaderTest#failOnWritingStringNotFieldNameBytes()}, {@link tools.jackson.core.unittest.json.GeneratorFailFromReaderTest#failOnWritingStringNotFieldNameChars()}, {@link tools.jackson.core.unittest.json.GeneratorFailTest#failOnWritingStringNotFieldNameBytes()}, {@link tools.jackson.core.unittest.json.GeneratorFailTest#failOnWritingStringNotFieldNameChars()}.
 */
    @Test
    void readerStringCannotSupplyPropertyName() throws Exception {
            new T32_0035F0().__invoke_readerStringCannotSupplyPropertyName();
        }
}
