package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.ParserSymbolHandlingTest}
 */
class ParserSymbolHandlingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserSymbolHandlingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserSymbolHandlingTest#symbolsWithNullBytes()}.
 */
    @Test
    void literalVpackObjectNamesPreserveEmbeddedNulBytesAcrossFactoryReuse()
            throws Exception {
            new T32_0086F1().__invoke_literalVpackObjectNamesPreserveEmbeddedNulBytesAcrossFactoryReuse();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserSymbolHandlingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserSymbolHandlingTest#symbolsWithNullOnlyNameBytes()}.
 */
    @Test
    void literalVpackObjectNamesPreserveOneThroughFourNulsAcrossFactoryReuse()
            throws Exception {
            new T32_0086F1().__invoke_literalVpackObjectNamesPreserveOneThroughFourNulsAcrossFactoryReuse();
        }
}
