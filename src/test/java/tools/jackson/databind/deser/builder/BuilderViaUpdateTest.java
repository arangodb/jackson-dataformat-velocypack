package tools.jackson.databind.deser.builder;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.deser.builder.BuilderViaUpdateTest}
 */
class BuilderViaUpdateTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.deser.builder.BuilderViaUpdateTest}.
 * Original test methods: {@link tools.jackson.databind.deser.builder.BuilderViaUpdateTest#testBuilderUpdateWithValue()}.
 */
    @Test
    // Provenance: databind/deser/builder/BuilderViaUpdateTest#testBuilderUpdateWithValue.
    // Builder-backed immutable values cannot be updated from the built instance;
    // preserve the source's InvalidDefinitionException contract and hint.
    void testBuilderUpdateWithValue() throws Exception {
            new T32_0194F0().__invoke_testBuilderUpdateWithValue();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.deser.builder.BuilderViaUpdateTest}.
 * Original test methods: {@link tools.jackson.databind.deser.builder.BuilderViaUpdateTest#testBuilderUpdateWithBuilder()}.
 */
    @Test
    // Provenance: databind/deser/builder/BuilderViaUpdateTest#testBuilderUpdateWithBuilder.
    void testBuilderUpdateWithBuilder() throws Exception {
            new T32_0194F0().__invoke_testBuilderUpdateWithBuilder();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.deser.builder.BuilderViaUpdateTest}.
 * Original test methods: {@link tools.jackson.databind.deser.builder.BuilderViaUpdateTest#testIssue2100Reproducer()}.
 */
    @Test
    // Provenance: databind/deser/builder/BuilderViaUpdateTest#testIssue2100Reproducer.
    void testIssue2100Reproducer() throws Exception {
            new T32_0194F0().__invoke_testIssue2100Reproducer();
        }
}
