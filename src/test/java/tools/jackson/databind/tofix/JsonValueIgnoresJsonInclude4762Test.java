package tools.jackson.databind.tofix;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.tofix.JsonValueIgnoresJsonInclude4762Test}
 */
class JsonValueIgnoresJsonInclude4762Test {
/**
 * VPack adaptation of {@link tools.jackson.databind.tofix.JsonValueIgnoresJsonInclude4762Test}.
 * Original test methods: {@link tools.jackson.databind.tofix.JsonValueIgnoresJsonInclude4762Test#jsonValueShouldHonorJsonIncludeOnField()}.
 */
    // FIXME
    @Disabled("Databind tofix #4762: @JsonValue ignores field @JsonInclude on 3.2.2; revisit after upgrade")
    @Test
    // Provenance: JsonValueIgnoresJsonInclude4762Test#jsonValueShouldHonorJsonIncludeOnField().
    void jsonValueShouldHonorJsonIncludeOnFieldVpack() throws Exception {
            new T32_0612Fixture().__invoke_jsonValueShouldHonorJsonIncludeOnFieldVpack();
        }
}
