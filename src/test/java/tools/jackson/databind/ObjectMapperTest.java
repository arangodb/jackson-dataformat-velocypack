package tools.jackson.databind;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ObjectMapperTest}
 * {@link tools.jackson.databind.ObjectWriterTest}
 */
class ObjectMapperTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testFeatureDefaults()}.
 */
    @Test
    void vpackMapperFeatureDefaultsAndOverridesRemainVisible() throws Exception {
            new T32_0127F1().__invoke_vpackMapperFeatureDefaultsAndOverridesRemainVisible();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testConfigForPropertySorting()}.
 */
    @Test
    void vpackMapperPropertySortingConfigurationMatchesDatabind() throws Exception {
            new T32_0127F1().__invoke_vpackMapperPropertySortingConfigurationMatchesDatabind();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testDataInputViaMapper()}.
 */
    @Test
    void dataInputViaMapperUsesLiteralVpackForMapperReaderAndTree() throws Exception {
            new T32_0127F1().__invoke_dataInputViaMapperUsesLiteralVpackForMapperReaderAndTree();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testDataOutputViaMapper()}.
 */
    @Test
    void dataOutputViaMapperWritesCanonicalLiteralVpack() throws Exception {
            new T32_0127F1().__invoke_dataOutputViaMapperWritesCanonicalLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testDeserializationContextCache()}.
 */
    @Test
    void deserializationContextCacheIsFilledAndFlushedForVpack() throws Exception {
            new T32_0127F1().__invoke_deserializationContextCacheIsFilledAndFlushedForVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testClearCaches()}.
 */
    @Test
    void clearCachesDropsVpackMapperCaches() throws Exception {
            new T32_0127F1().__invoke_clearCachesDropsVpackMapperCaches();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testRebuild()}.
 */
    @Test
    void rebuildPreservesVpackMapperFactoryContract() throws Exception {
            new T32_0128Fixture().__invoke_rebuildPreservesVpackMapperFactoryContract();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testProps()}.
 */
    @Test
    void propsExposeAndRetainConfiguredNodeFactory() throws Exception {
            new T32_0128Fixture().__invoke_propsExposeAndRetainConfiguredNodeFactory();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testRegisterDependentModules()}.
 */
    @Test
    void registerDependentModulesUsesDeclaredOrder() throws Exception {
            new T32_0128Fixture().__invoke_registerDependentModulesUsesDeclaredOrder();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testRegisterTransitiveModuleDependencies()}.
 */
    @Test
    void registerTransitiveModuleDependenciesUsesDepthFirstOrder() throws Exception {
            new T32_0128Fixture().__invoke_registerTransitiveModuleDependenciesUsesDepthFirstOrder();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testHasExplicitTimeZone()}.
 */
    @Test
    void explicitTimeZoneStateMatchesDatabindContract() throws Exception {
            new T32_0128Fixture().__invoke_explicitTimeZoneStateMatchesDatabindContract();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#testWithCloseCloseable()}.
 */
    @Test
    void closeCloseableFeatureClosesValuesForVpackTargets() throws Exception {
            new T32_0128Fixture().__invoke_closeCloseableFeatureClosesValuesForVpackTargets();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createGenerator_OutputStream()}.
 */
    @Test
    void createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget() throws Exception {
            new T32_0128Fixture().__invoke_createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createGenerator_File()}.
 */
    @Test
    void createGeneratorFileWritesLiteralVpack() throws Exception {
            new T32_0128Fixture().__invoke_createGeneratorFileWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createGenerator_Path()}.
 */
    @Test
    void createGeneratorPathWritesLiteralVpack() throws Exception {
            new T32_0128Fixture().__invoke_createGeneratorPathWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createGenerator_DataOutput()}.
 */
    @Test
    void createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget() throws Exception {
            new T32_0128Fixture().__invoke_createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createGenerator_Writer()}.
 */
    @Test
    void createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
            new T32_0128Fixture().__invoke_createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createGenerator_failsIfArgumentIsNull()}.
 */
    @Test
    void createGeneratorRejectsNullArguments() throws Exception {
            new T32_0128Fixture().__invoke_createGeneratorRejectsNullArguments();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createParser_InputStream()}.
 */
    @Test
    void createParserInputStreamReadsLiteralVpackString() throws Exception {
            new T32_0129Fixture().__invoke_createParserInputStreamReadsLiteralVpackString();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createParser_File()}.
 */
    @Test
    void createParserFileReadsLiteralVpackString() throws Exception {
            new T32_0129Fixture().__invoke_createParserFileReadsLiteralVpackString();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createParser_Path()}.
 */
    @Test
    void createParserPathReadsLiteralVpackString() throws Exception {
            new T32_0129Fixture().__invoke_createParserPathReadsLiteralVpackString();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createParser_ByteArray()}.
 */
    @Test
    void createParserByteArrayReadsLiteralVpackString() throws Exception {
            new T32_0129Fixture().__invoke_createParserByteArrayReadsLiteralVpackString();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createParser_DataInput()}.
 */
    @Test
    void createParserDataInputReadsLiteralVpackString() throws Exception {
            new T32_0129Fixture().__invoke_createParserDataInputReadsLiteralVpackString();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_createParser_failsIfArgumentIsNull()}.
 */
    @Test
    void createParserRejectsNullArgumentsAcrossObjectMapperOverloads() throws Exception {
            new T32_0129Fixture().__invoke_createParserRejectsNullArgumentsAcrossObjectMapperOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readTree_InputStream()}.
 */
    @Test
    void readTreeInputStreamReadsLiteralVpackString() throws Exception {
            new T32_0129Fixture().__invoke_readTreeInputStreamReadsLiteralVpackString();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readTree_File()}.
 */
    @Test
    void readTreeFileReadsLiteralVpackString() throws Exception {
            new T32_0129Fixture().__invoke_readTreeFileReadsLiteralVpackString();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readTree_ByteArray()}.
 */
    @Test
    void readTreeByteArrayReadsLiteralVpackStringWithAndWithoutBounds() throws Exception {
            new T32_0129Fixture().__invoke_readTreeByteArrayReadsLiteralVpackStringWithAndWithoutBounds();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readTree_Path()}.
 */
    @Test
    void readTreePathReadsLiteralVpackString() throws Exception {
            new T32_0130Fixture().__invoke_readTreePathReadsLiteralVpackString();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readTree_Reader()}, {@link tools.jackson.databind.ObjectMapperTest#test_readTree_String()}.
 */
    @Test
    void readTreeTextSourcesAreExplicitlyUnsupportedForBinaryVpack() throws Exception {
            new T32_0130Fixture().__invoke_readTreeTextSourcesAreExplicitlyUnsupportedForBinaryVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readTree_failsIfArgumentIsNull()}.
 */
    @Test
    void readTreeRejectsNullArgumentsAcrossAssignedOverloads() throws Exception {
            new T32_0130Fixture().__invoke_readTreeRejectsNullArgumentsAcrossAssignedOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_ByteArray()}.
 */
    @Test
    void readValueByteArrayReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
            new T32_0130Fixture().__invoke_readValueByteArrayReadsLiteralVpackStringAcrossTypeOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_DataInput()}.
 */
    @Test
    void readValueDataInputReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
            new T32_0130Fixture().__invoke_readValueDataInputReadsLiteralVpackStringAcrossTypeOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_File()}.
 */
    @Test
    void readValueFileReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
            new T32_0130Fixture().__invoke_readValueFileReadsLiteralVpackStringAcrossTypeOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_InputStream()}.
 */
    @Test
    void readValueInputStreamReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
            new T32_0130Fixture().__invoke_readValueInputStreamReadsLiteralVpackStringAcrossTypeOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_JsonParser()}.
 */
    @Test
    void readValueJsonParserReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
            new T32_0130Fixture().__invoke_readValueJsonParserReadsLiteralVpackStringAcrossTypeOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_Path()}.
 */
    @Test
    void readValuePathReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
            new T32_0130Fixture().__invoke_readValuePathReadsLiteralVpackStringAcrossTypeOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_Reader()}, {@link tools.jackson.databind.ObjectMapperTest#test_readValue_String()}.
 */
    @Test
    void readValueTextSourcesAreExplicitlyUnsupportedForBinaryVpack() throws Exception {
            new T32_0130Fixture().__invoke_readValueTextSourcesAreExplicitlyUnsupportedForBinaryVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_failsIfArgumentIsNull()}.
 */
    @Test
    void readValueRejectsNullSourcesAcrossAssignedOverloads() throws Exception {
            new T32_0131F0().__invoke_readValueRejectsNullSourcesAcrossAssignedOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}, {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_writeValue_OutputStream()}, {@link tools.jackson.databind.ObjectWriterTest#test_writeValue_OutputStream()}.
 */
    @Test
    void writeValueOutputStreamWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
            new T32_0131F0().__invoke_writeValueOutputStreamWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}, {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_writeValue_File()}, {@link tools.jackson.databind.ObjectWriterTest#test_writeValue_File()}.
 */
    @Test
    void writeValueFileWritesLiteralVpack() throws Exception {
            new T32_0131F0().__invoke_writeValueFileWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}, {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_writeValue_Path()}, {@link tools.jackson.databind.ObjectWriterTest#test_writeValue_Path()}.
 */
    @Test
    void writeValuePathWritesLiteralVpack() throws Exception {
            new T32_0131F0().__invoke_writeValuePathWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}, {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_writeValue_Writer()}, {@link tools.jackson.databind.ObjectWriterTest#test_writeValue_Writer()}.
 */
    @Test
    void writeValueWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
            new T32_0131F0().__invoke_writeValueWriterIsExplicitlyUnsupportedForBinaryVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}, {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_writeValue_DataOutput()}, {@link tools.jackson.databind.ObjectWriterTest#test_writeValue_DataOutput()}.
 */
    @Test
    void writeValueDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
            new T32_0131F0().__invoke_writeValueDataOutputWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}, {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_writeValue_JsonGenerator()}, {@link tools.jackson.databind.ObjectWriterTest#test_writeValue_JsonGenerator()}.
 */
    @Test
    void writeValueJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
            new T32_0131F0().__invoke_writeValueJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}, {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_writeValue_failsIfArgumentIsNull()}, {@link tools.jackson.databind.ObjectWriterTest#test_writeValue_failsIfArgumentIsNull()}.
 */
    @Test
    void writeValueRejectsNullTargetsAcrossAssignedOverloads() throws Exception {
            new T32_0131F0().__invoke_writeValueRejectsNullTargetsAcrossAssignedOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectMapperTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectMapperTest#test_readValue_writeValue_Path_nonDefaultFileSystem()}.
 */
    @Test
    void readValueWriteValuePathWorksWithNonDefaultFileSystem() throws Exception {
            new T32_0131F0().__invoke_readValueWriteValuePathWorksWithNonDefaultFileSystem();
        }
}
