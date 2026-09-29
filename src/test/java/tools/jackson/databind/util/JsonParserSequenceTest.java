package tools.jackson.databind.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.util.JsonParserSequenceTest}
 */
@ResourceLock(Resources.LOCALE)
class JsonParserSequenceTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.util.JsonParserSequenceTest}.
 * Original test methods: {@link tools.jackson.databind.util.JsonParserSequenceTest#testJsonParserSequenceOverridesSkipChildren()}.
 */
    @Test
    // Provenance: JsonParserSequenceTest#testJsonParserSequenceOverridesSkipChildren().
    void parserSequenceSkipChildrenSwitchesAcrossTokenBuffers() throws Exception {
            new T32_0627F0().__invoke_parserSequenceSkipChildrenSwitchesAcrossTokenBuffers();
        }
}
