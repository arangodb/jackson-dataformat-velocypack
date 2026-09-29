package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}
 * {@link tools.jackson.core.unittest.read.SimpleParserTest}
 */
class ReadStringStreamingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#emptyString()}.
 */
    @Test
    void emptyStringCanBeReadIntoWriter() throws Exception {
            new T32_0087Fixture().__invoke_emptyStringCanBeReadIntoWriter();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#commonEscapeSequences()}.
 */
    @Test
    void commonStringCharactersAreWrittenVerbatim() throws Exception {
            new T32_0087Fixture().__invoke_commonStringCharactersAreWrittenVerbatim();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#escapesAtOutputBufferBoundary()}.
 */
    @Test
    void contentAtOutputBufferBoundaryIsWrittenExactly() throws Exception {
            new T32_0087Fixture().__invoke_contentAtOutputBufferBoundaryIsWrittenExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}, {@link tools.jackson.core.unittest.read.SimpleParserTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#constraintAtExactLimit()}, {@link tools.jackson.core.unittest.read.SimpleParserTest#readStringWithIncreasedLimit()}.
 */
    @Test
    void configuredStringLimitAcceptsExactLength() throws Exception {
            new T32_0087Fixture().__invoke_configuredStringLimitAcceptsExactLength();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#constraintOneLargerThanFlushBoundary()}.
 */
    @Test
    void configuredStringLimitRejectsOneBeyondFlushBoundary() throws Exception {
            new T32_0087Fixture().__invoke_configuredStringLimitRejectsOneBeyondFlushBoundary();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}, {@link tools.jackson.core.unittest.read.SimpleParserTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#constraintExactlyAtFlushBoundary()}, {@link tools.jackson.core.unittest.read.SimpleParserTest#readStringEnforcesMaxStringLength()}.
 */
    @Test
    void configuredStringLimitRejectsOneBeyondExactBoundary() throws Exception {
            new T32_0087Fixture().__invoke_configuredStringLimitRejectsOneBeyondExactBoundary();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#consumingSemantics()}.
 */
    @Test
    void readStringConsumesTheCurrentString() throws Exception {
            new T32_0087Fixture().__invoke_readStringConsumesTheCurrentString();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#multipleStringsInArray()}.
 */
    @Test
    void multipleStringsInArrayCanBeReadSequentially() throws Exception {
            new T32_0087Fixture().__invoke_multipleStringsInArrayCanBeReadSequentially();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#mixedContent()}.
 */
    @Test
    void mixedUtf8ContentIsWrittenExactly() throws Exception {
            new T32_0087Fixture().__invoke_mixedUtf8ContentIsWrittenExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#longMixedContent()}.
 */
    @Test
    void longMixedUtf8ContentIsWrittenExactly() throws Exception {
            new T32_0087Fixture().__invoke_longMixedUtf8ContentIsWrittenExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#escapeSplitAcrossInputChunks()}.
 */
    @Test
    void rawUtf8StringSurvivesOneByteInputChunks() throws Exception {
            new T32_0087Fixture().__invoke_rawUtf8StringSurvivesOneByteInputChunks();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#longStringThrottled()}.
 */
    @Test
    void longStringSurvivesOneByteInputChunks() throws Exception {
            new T32_0087Fixture().__invoke_longStringSurvivesOneByteInputChunks();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#singleCharString()}.
 */
    @Test
    void singleCharacterStringCanBeReadIntoWriter() throws Exception {
            new T32_0088Fixture().__invoke_singleCharacterStringCanBeReadIntoWriter();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#threeByteUtf8()}.
 */
    @Test
    void threeByteUtf8StringIsDecodedExactly() throws Exception {
            new T32_0088Fixture().__invoke_threeByteUtf8StringIsDecodedExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#surrogateRoundTrip()}.
 */
    @Test
    void surrogatePairStringIsDecodedExactly() throws Exception {
            new T32_0088Fixture().__invoke_surrogatePairStringIsDecodedExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#surrogateAtOutputBufferBoundary()}.
 */
    @Test
    void surrogatePairAtOutputBufferBoundaryIsDecodedExactly() throws Exception {
            new T32_0088Fixture().__invoke_surrogatePairAtOutputBufferBoundaryIsDecodedExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#stringExactlyAtOutputBufferSize()}.
 */
    @Test
    void stringExactlyAtOutputBufferSizeIsWrittenExactly() throws Exception {
            new T32_0088Fixture().__invoke_stringExactlyAtOutputBufferSizeIsWrittenExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#stringOneOverOutputBufferSize()}.
 */
    @Test
    void stringOneOverOutputBufferSizeIsWrittenExactly() throws Exception {
            new T32_0088Fixture().__invoke_stringOneOverOutputBufferSizeIsWrittenExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#stringTwoFullOutputBuffers()}.
 */
    @Test
    void stringOfTwoFullOutputBuffersIsWrittenExactly() throws Exception {
            new T32_0088Fixture().__invoke_stringOfTwoFullOutputBuffersIsWrittenExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#stringTwoFullOutputBuffersPlusOne()}.
 */
    @Test
    void stringOfTwoFullOutputBuffersPlusOneIsWrittenExactly() throws Exception {
            new T32_0088Fixture().__invoke_stringOfTwoFullOutputBuffersPlusOneIsWrittenExactly();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#readStringOnPropertyName()}.
 */
    @Test
    void readStringOnPropertyNameWritesTheCurrentName() throws Exception {
            new T32_0088Fixture().__invoke_readStringOnPropertyNameWritesTheCurrentName();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#readStringOnNumberTokens()}.
 */
    @Test
    void readStringOnNumberTokensWritesTheirText() throws Exception {
            new T32_0088Fixture().__invoke_readStringOnNumberTokensWritesTheirText();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#readStringOnNullToken()}.
 */
    @Test
    void readStringOnNullTokenWritesNull() throws Exception {
            new T32_0088Fixture().__invoke_readStringOnNullTokenWritesNull();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#readStringBeforeFirstToken()}.
 */
    @Test
    void readStringBeforeFirstTokenWritesNothing() throws Exception {
            new T32_0088Fixture().__invoke_readStringBeforeFirstTokenWritesNothing();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#twoByteUtf8()}.
 */
    @Test
    void twoByteUtf8StringIsDecodedExactlyAcrossBinarySources() throws Exception {
            new T32_0089F0().__invoke_twoByteUtf8StringIsDecodedExactlyAcrossBinarySources();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ReadStringStreamingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ReadStringStreamingTest#unicodeEscapeSequences()}.
 */
    @Test
    void literalUnicodeUtf8ReplacesJsonUnicodeEscapeSpelling() throws Exception {
            new T32_0089F0().__invoke_literalUnicodeUtf8ReplacesJsonUnicodeEscapeSpelling();
        }
}
