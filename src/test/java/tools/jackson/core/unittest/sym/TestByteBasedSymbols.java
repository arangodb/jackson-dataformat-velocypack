package tools.jackson.core.unittest.sym;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols}
 * {@link tools.jackson.core.unittest.sym.TestHashCollisionChars}
 */
class TestByteBasedSymbols {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols#sharedSymbols()}.
 */
    @Test
    void simultaneousParsersShareByteSymbolsWithoutCrossContamination() throws Exception {
            new T32_0100F1().__invoke_simultaneousParsersShareByteSymbolsWithoutCrossContamination();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols#auxMethodsWithNewSymboTable()}.
 */
    @Test
    void auxiliaryByteCanonicalizerLookupsRemainUsableForVpack() throws Exception {
            new T32_0100F1().__invoke_auxiliaryByteCanonicalizerLookupsRemainUsableForVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols#issue207()}.
 */
    @Test
    void largeByteNameSetTraversesWithoutCanonicalizerCorruption() throws Exception {
            new T32_0100F1().__invoke_largeByteNameSetTraversesWithoutCanonicalizerCorruption();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols}, {@link tools.jackson.core.unittest.sym.TestHashCollisionChars}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestByteBasedSymbols#quadsIssue548()}, {@link tools.jackson.core.unittest.sym.TestHashCollisionChars#readerCollisions()}.
 */
    @Test
    void byteCanonicalizerSpilloverRemainsSafeAfterRelease() throws Exception {
            new T32_0100F1().__invoke_byteCanonicalizerSpilloverRemainsSafeAfterRelease();
        }
}
