package tools.jackson.core.unittest.sym;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.sym.TestSymbolTables}
 */
class TestSymbolTables {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#byteBasedSymbolTable()}.
 */
    @Test
    void byteBasedSymbolTableRetainsShortMediumAndLongNames() throws Exception {
            new T32_0101Fixture().__invoke_byteBasedSymbolTableRetainsShortMediumAndLongNames();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#syntheticWithBytesNew()}.
 */
    @Test
    void syntheticByteSymbolsRetainSourceDistribution() throws Exception {
            new T32_0101Fixture().__invoke_syntheticByteSymbolsRetainSourceDistribution();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#collisionsWithBytesNew187a()}.
 */
    @Test
    void numericByteCollisions187aRetainSourceDistribution() throws Exception {
            new T32_0101Fixture().__invoke_numericByteCollisions187aRetainSourceDistribution();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#collisionsWithBytesNew187b()}.
 */
    @Test
    void numericByteCollisions187bRetainSourceDistribution() throws Exception {
            new T32_0101Fixture().__invoke_numericByteCollisions187bRetainSourceDistribution();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#shortNameCollisionsViaParser()}.
 */
    @Test
    void shortNameCollisionsViaByteParserRetainAllNames() throws Exception {
            new T32_0101Fixture().__invoke_shortNameCollisionsViaByteParserRetainAllNames();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#shortQuotedDirectBytes()}.
 */
    @Test
    void shortQuotedByteSymbolsRetainSourceDistribution() throws Exception {
            new T32_0101Fixture().__invoke_shortQuotedByteSymbolsRetainSourceDistribution();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#shortNameCollisionsDirectNew()}.
 */
    @Test
    void shortDirectByteSymbolsRetainSourceDistribution() throws Exception {
            new T32_0101Fixture().__invoke_shortDirectByteSymbolsRetainSourceDistribution();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#longSymbols17Bytes()}.
 */
    @Test
    void seventeenByteSymbolsRetainAllNames() throws Exception {
            new T32_0101Fixture().__invoke_seventeenByteSymbolsRetainAllNames();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.sym.TestSymbolTables}.
 * Original test methods: {@link tools.jackson.core.unittest.sym.TestSymbolTables#thousandsOfSymbolsWithNew()}.
 */
    @Test
    void thousandsOfByteSymbolsRetainSourceLifecycleAndDistribution() throws Exception {
            new T32_0102F0().__invoke_thousandsOfByteSymbolsRetainSourceLifecycleAndDistribution();
        }
}
