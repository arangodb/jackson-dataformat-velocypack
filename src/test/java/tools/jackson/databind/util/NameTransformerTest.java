package tools.jackson.databind.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.util.NameTransformerTest}
 */
@ResourceLock(Resources.LOCALE)
class NameTransformerTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.util.NameTransformerTest}.
 * Original test methods: {@link tools.jackson.databind.util.NameTransformerTest#testSimpleTransformer()}.
 */
    @Test
    // Provenance: NameTransformerTest#testSimpleTransformer().
    void simpleNameTransformerPreservesPrefixSuffixAndReverse() throws Exception {
            new T32_0627F1().__invoke_simpleNameTransformerPreservesPrefixSuffixAndReverse();
        }
}
