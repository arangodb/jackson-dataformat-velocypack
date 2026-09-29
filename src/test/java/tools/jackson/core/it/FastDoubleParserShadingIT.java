package tools.jackson.core.it;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.it.FastDoubleParserShadingIT}
 */
class FastDoubleParserShadingIT {
/**
 * VPack adaptation of {@link tools.jackson.core.it.FastDoubleParserShadingIT}.
 * Original test methods: {@link tools.jackson.core.it.FastDoubleParserShadingIT#verifyNoUnshadedFDPClasses()}.
 */
    @Test
    void packagedVpackJarDoesNotEmbedFastDoubleParserClasses() throws Exception {
            new T32_0001F0().__invoke_packagedVpackJarDoesNotEmbedFastDoubleParserClasses();
        }
}
