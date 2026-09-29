package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.ParserClosingTest}
 */
class ParserClosingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserClosingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserClosingTest#clearTokenOnClose()}.
 */
    @Test
    void closeClearsCurrentTokenOnLiteralArrayRoot() throws Exception {
            new T32_0083F1().__invoke_closeClearsCurrentTokenOnLiteralArrayRoot();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserClosingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserClosingTest#autoCloseReader()}.
 */
    @Test
    void autoCloseInputStreamCoversReaderCloseAssertions() throws Exception {
            new T32_0083F1().__invoke_autoCloseInputStreamCoversReaderCloseAssertions();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserClosingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserClosingTest#noAutoCloseInputStream()}.
 */
    @Test
    void noAutoCloseInputStreamLeavesLiteralSourceOpen() throws Exception {
            new T32_0083F1().__invoke_noAutoCloseInputStreamLeavesLiteralSourceOpen();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserClosingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserClosingTest#releaseContentBytes()}.
 */
    @Test
    void releaseContentBytesReturnsCompletedRootTailOnce() throws Exception {
            new T32_0083F1().__invoke_releaseContentBytesReturnsCompletedRootTailOnce();
        }
}
