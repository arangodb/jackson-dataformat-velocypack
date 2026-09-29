package tools.jackson.core.unittest.json.async;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.json.async.AsyncParserConfigTest}
 * {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest}
 * {@link tools.jackson.core.unittest.read.InternPropertyNamesTest}
 */
class AsyncParserConfigTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.json.async.AsyncParserConfigTest}, {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest}, {@link tools.jackson.core.unittest.read.InternPropertyNamesTest}.
 * Original test methods: {@link tools.jackson.core.unittest.json.async.AsyncParserConfigTest#asyncParserDefaults()}, {@link tools.jackson.core.unittest.json.async.AsyncParserConfigTest#factoryDefaults()}, {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest#filteredNonBlockingParserNotExplicitlyAllowed()}, {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest#filteringNonBlockingParserWithoutInputFed()}, {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest#testFilteredNonBlockingParserAllContent()}, {@link tools.jackson.core.unittest.json.async.AsyncTokenFilterTest#testSkipChildrenFailOnSplit()}, {@link tools.jackson.core.unittest.read.InternPropertyNamesTest#interningDisabledWithAsyncParser()}, {@link tools.jackson.core.unittest.read.InternPropertyNamesTest#interningEnabledWithAsyncParser()}.
 */
    @Test
    void asyncParserConfigurationHasAnExplicitVpackBoundary() throws Exception {
            new T32_0055F1().__invoke_asyncParserConfigurationHasAnExplicitVpackBoundary();
        }
}
