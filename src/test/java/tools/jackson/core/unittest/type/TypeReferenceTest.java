package tools.jackson.core.unittest.type;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.type.TypeReferenceTest}
 */
class TypeReferenceTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.type.TypeReferenceTest}.
 * Original test methods: {@link tools.jackson.core.unittest.type.TypeReferenceTest#invalid()}.
 */
    @Test
    @SuppressWarnings("rawtypes")
    void typeReferenceRejectsMissingVpackTypeInformation() throws Exception {
            new T32_0105Fixture().__invoke_typeReferenceRejectsMissingVpackTypeInformation();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.type.TypeReferenceTest}.
 * Original test methods: {@link tools.jackson.core.unittest.type.TypeReferenceTest#resolvedType()}.
 */
    @Test
    void resolvedTypeReferenceFlagUsesReferencedType() throws Exception {
            new T32_0105Fixture().__invoke_resolvedTypeReferenceFlagUsesReferencedType();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.type.TypeReferenceTest}.
 * Original test methods: {@link tools.jackson.core.unittest.type.TypeReferenceTest#simple()}.
 */
    @Test
    void assignedAssertionsRemainMappedToNamedExistingCoverage() throws Exception {
            new T32_0105Fixture().__invoke_assignedAssertionsRemainMappedToNamedExistingCoverage();
        }
}
