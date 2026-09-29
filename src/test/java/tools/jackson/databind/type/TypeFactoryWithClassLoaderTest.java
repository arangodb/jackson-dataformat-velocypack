package tools.jackson.databind.type;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}
 */
class TypeFactoryWithClassLoaderTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testCallingOnlyWithModifierGivesExpectedResults()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testCallingOnlyWithModifierGivesExpectedResults().
    void onlyWithModifierVpack() throws Exception {
            new T32_0619F1().__invoke_onlyWithModifierVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testCallingOnlyWithClassLoaderGivesExpectedResults()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testCallingOnlyWithClassLoaderGivesExpectedResults().
    void onlyWithClassLoaderVpack() throws Exception {
            new T32_0619F1().__invoke_onlyWithClassLoaderVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testDefaultTypeFactoryNotAffectedByWithConstructors()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testDefaultTypeFactoryNotAffectedByWithConstructors().
    void defaultTypeFactoryNotAffectedVpack() throws Exception {
            new T32_0619F1().__invoke_defaultTypeFactoryNotAffectedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testSetsTheCorrectClassLoderIfUsingWithModifierFollowedByWithClassLoader()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testSetsTheCorrectClassLoderIfUsingWithModifierFollowedByWithClassLoader().
    void modifierThenClassLoaderVpack() throws Exception {
            new T32_0619F1().__invoke_modifierThenClassLoaderVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testSetsTheCorrectClassLoderIfUsingWithClassLoaderFollowedByWithModifier()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testSetsTheCorrectClassLoderIfUsingWithClassLoaderFollowedByWithModifier().
    void classLoaderThenModifierVpack() throws Exception {
            new T32_0619F1().__invoke_classLoaderThenModifierVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testThreadContextClassLoaderIsUsedIfNotUsingWithClassLoader()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testThreadContextClassLoaderIsUsedIfNotUsingWithClassLoader().
    void threadContextClassLoaderUsedVpack() throws Exception {
            new T32_0619F1().__invoke_threadContextClassLoaderUsedVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testUsesCorrectClassLoaderWhenThreadClassLoaderIsNotNull()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testUsesCorrectClassLoaderWhenThreadClassLoaderIsNotNull().
    void explicitClassLoaderWithThreadLoaderVpack() throws Exception {
            new T32_0620F0().__invoke_explicitClassLoaderWithThreadLoaderVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testUsesCorrectClassLoaderWhenThreadClassLoaderIsNull()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testUsesCorrectClassLoaderWhenThreadClassLoaderIsNull().
    void explicitClassLoaderWithNullThreadLoaderVpack() throws Exception {
            new T32_0620F0().__invoke_explicitClassLoaderWithNullThreadLoaderVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest}.
 * Original test methods: {@link tools.jackson.databind.type.TypeFactoryWithClassLoaderTest#testUsesFallBackClassLoaderIfNoThreadClassLoaderAndNoWithClassLoader()}.
 */
    @Test
    // Provenance: TypeFactoryWithClassLoaderTest#testUsesFallBackClassLoaderIfNoThreadClassLoaderAndNoWithClassLoader().
    void fallbackClassLoaderVpack() throws Exception {
            new T32_0620F0().__invoke_fallbackClassLoaderVpack();
        }
}
