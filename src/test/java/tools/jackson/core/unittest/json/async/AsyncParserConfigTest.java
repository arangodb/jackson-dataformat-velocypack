package tools.jackson.core.unittest.json.async;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.async.AsyncParserConfigTest}
 * {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest}
 */
class AsyncParserConfigTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest#filteredNonBlockingParserNotExplicitlyAllowed()}, {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest#filteringNonBlockingParserWithoutInputFed()}, {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest#testFilteredNonBlockingParserAllContent()}, {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest#testSkipChildrenFailOnSplit()}.
 */
    @Test
    void asyncParserConfigurationHasAnExplicitVpackBoundary() throws Exception {
            new T32_0055F1().__invoke_asyncParserConfigurationHasAnExplicitVpackBoundary();
        }
}
