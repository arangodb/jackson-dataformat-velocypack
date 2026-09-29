package tools.jackson.databind;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ObjectWriterTest}
 */
class ObjectWriterTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testArgumentChecking()}.
 */
    @Test
    void writerArgumentCheckingRejectsNullType() throws Exception {
            new T32_0134F1().__invoke_writerArgumentCheckingRejectsNullType();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testDatatypeFeatures()}.
 */
    @Test
    void writerDatatypeFeaturesCanBeToggled() throws Exception {
            new T32_0134F1().__invoke_writerDatatypeFeaturesCanBeToggled();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testPrettyPrinter()}.
 */
    @Test
    void prettyPrinterHasNoTextualEffectOnLiteralVpack() throws Exception {
            new T32_0135Fixture().__invoke_prettyPrinterHasNoTextualEffectOnLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testPrefetch()}.
 */
    @Test
    void prefetchStateTracksWriterForType() throws Exception {
            new T32_0135Fixture().__invoke_prefetchStateTracksWriterForType();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testObjectWriterFeatures()}.
 */
    @Test
    void jsonPropertyQuotingFeatureDoesNotChangeLiteralVpack() throws Exception {
            new T32_0135Fixture().__invoke_jsonPropertyQuotingFeatureDoesNotChangeLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testObjectWriterWithNode()}.
 */
    @Test
    void objectWriterWithNodeWritesIndependentLiteralObject() throws Exception {
            new T32_0135Fixture().__invoke_objectWriterWithNodeWritesIndependentLiteralObject();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testPolymorphicWithTyping()}.
 */
    @Test
    void polymorphicTypingWritesOrdinaryVpackObjectProperties() throws Exception {
            new T32_0135Fixture().__invoke_polymorphicTypingWritesOrdinaryVpackObjectProperties();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testForNoType()}.
 */
    @Test
    void writerForNullTypesStillReturnsWriters() throws Exception {
            new T32_0135Fixture().__invoke_writerForNullTypesStillReturnsWriters();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testMiscSettings()}.
 */
    @Test
    void miscellaneousWriterSettingsRemainImmutableAndConfigurable() throws Exception {
            new T32_0135Fixture().__invoke_miscellaneousWriterSettingsRemainImmutableAndConfigurable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testNoPrefetch()}.
 */
    @Test
    void noPrefetchWriterWritesIndependentLiteralInteger() throws Exception {
            new T32_0135Fixture().__invoke_noPrefetchWriterWritesIndependentLiteralInteger();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testRootValueSettings()}.
 */
    @Test
    void rootSettingsPreserveWriterCopySemantics() throws Exception {
            new T32_0135Fixture().__invoke_rootSettingsPreserveWriterCopySemantics();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testFeatureSettings()}.
 */
    @Test
    void featureSettingsRemainImmutableAndToggleable() throws Exception {
            new T32_0135Fixture().__invoke_featureSettingsRemainImmutableAndToggleable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testSchema()}.
 */
    @Test
    void schemaWriterRetainsVpackFormatBoundary() throws Exception {
            new T32_0135Fixture().__invoke_schemaWriterRetainsVpackFormatBoundary();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testStreamWriteFeatures()}.
 */
    @Test
    void streamWriteFeaturesRemainConfigurableForVpackWriter() throws Exception {
            new T32_0135Fixture().__invoke_streamWriteFeaturesRemainConfigurableForVpackWriter();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testWithCloseCloseable()}.
 */
    @Test
    void closeCloseableWriterClosesValuesForByteArrayAndBinaryGenerator() throws Exception {
            new T32_0136Fixture().__invoke_closeCloseableWriterClosesValuesForByteArrayAndBinaryGenerator();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testViewSettings()}.
 */
    @Test
    void viewSettingsPreserveWriterCopyIdentity() throws Exception {
            new T32_0136Fixture().__invoke_viewSettingsPreserveWriterCopyIdentity();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#testValueToTreeWithView()}.
 */
    @Test
    void valueToTreeWithViewUsesLiteralVpackViewObjects() throws Exception {
            new T32_0136Fixture().__invoke_valueToTreeWithViewUsesLiteralVpackViewObjects();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_createGenerator_OutputStream()}.
 */
    @Test
    void createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget() throws Exception {
            new T32_0136Fixture().__invoke_createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_createGenerator_File()}.
 */
    @Test
    void createGeneratorFileWritesLiteralVpack() throws Exception {
            new T32_0136Fixture().__invoke_createGeneratorFileWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_createGenerator_Path()}.
 */
    @Test
    void createGeneratorPathWritesLiteralVpack() throws Exception {
            new T32_0136Fixture().__invoke_createGeneratorPathWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_createGenerator_Writer()}.
 */
    @Test
    void createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
            new T32_0136Fixture().__invoke_createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_createGenerator_DataOutput()}.
 */
    @Test
    void createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget() throws Exception {
            new T32_0136Fixture().__invoke_createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_createGenerator_failsIfArgumentIsNull()}.
 */
    @Test
    void createGeneratorRejectsNullArguments() throws Exception {
            new T32_0136Fixture().__invoke_createGeneratorRejectsNullArguments();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValuesAsArray_DataOutput()}.
 */
    @Test
    void writeValuesAsArrayDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
            new T32_0137Fixture().__invoke_writeValuesAsArrayDataOutputWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValuesAsArray_File()}.
 */
    @Test
    void writeValuesAsArrayFileWritesLiteralVpack() throws Exception {
            new T32_0137Fixture().__invoke_writeValuesAsArrayFileWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValuesAsArray_JsonGenerator()}.
 */
    @Test
    void writeValuesAsArrayJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable()
            throws Exception {
            new T32_0137Fixture().__invoke_writeValuesAsArrayJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValuesAsArray_OutputStream()}.
 */
    @Test
    void writeValuesAsArrayOutputStreamWritesLiteralVpackAndLeavesTargetUsable()
            throws Exception {
            new T32_0137Fixture().__invoke_writeValuesAsArrayOutputStreamWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValuesAsArray_Path()}.
 */
    @Test
    void writeValuesAsArrayPathWritesLiteralVpack() throws Exception {
            new T32_0137Fixture().__invoke_writeValuesAsArrayPathWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValuesAsArray_Writer()}.
 */
    @Test
    void writeValuesAsArrayWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
            new T32_0137Fixture().__invoke_writeValuesAsArrayWriterIsExplicitlyUnsupportedForBinaryVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValuesAsArray_failsIfArgumentIsNull()}.
 */
    @Test
    void writeValuesAsArrayRejectsNullTargetsAcrossAssignedOverloads() throws Exception {
            new T32_0137Fixture().__invoke_writeValuesAsArrayRejectsNullTargetsAcrossAssignedOverloads();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValues_DataOutput()}.
 */
    @Test
    void writeValuesDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
            new T32_0137Fixture().__invoke_writeValuesDataOutputWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValues_OutputStream()}.
 */
    @Test
    void writeValuesOutputStreamWritesLiteralVpackAndLeavesTargetUsable()
            throws Exception {
            new T32_0138F0().__invoke_writeValuesOutputStreamWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValues_File()}.
 */
    @Test
    void writeValuesFileWritesLiteralVpack() throws Exception {
            new T32_0138F0().__invoke_writeValuesFileWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValues_Path()}.
 */
    @Test
    void writeValuesPathWritesLiteralVpack() throws Exception {
            new T32_0138F0().__invoke_writeValuesPathWritesLiteralVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValues_Writer()}.
 */
    @Test
    void writeValuesWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
            new T32_0138F0().__invoke_writeValuesWriterIsExplicitlyUnsupportedForBinaryVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValues_JsonGenerator()}.
 */
    @Test
    void writeValuesJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable()
            throws Exception {
            new T32_0138F0().__invoke_writeValuesJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ObjectWriterTest}.
 * Original test methods: {@link tools.jackson.databind.ObjectWriterTest#test_writeValues_failsIfArgumentIsNull()}.
 */
    @Test
    void writeValuesRejectsNullArgumentsAcrossAssignedOverloads() throws Exception {
            new T32_0138F0().__invoke_writeValuesRejectsNullArgumentsAcrossAssignedOverloads();
        }
}
