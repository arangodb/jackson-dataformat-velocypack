package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.ParserErrorHandlingTest}
 */
class ParserErrorHandlingTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.ParserErrorHandlingTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.ParserErrorHandlingTest#mangledRootFloatsBytes()}, {@link tools.jackson.core.unittest.read.ParserErrorHandlingTest#mangledRootIntsBytes()}.
 */
    @Test
    void adjacentBinaryRootsAreSelfDelimitingWithoutJsonWhitespace() throws Exception {
            new T32_0084Fixture().__invoke_adjacentBinaryRootsAreSelfDelimitingWithoutJsonWhitespace();
        }
}
