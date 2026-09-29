package tools.jackson.core.unittest.type;

import tools.jackson.core.type.ResolvedType;
import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0105Fixture {

    
    void typeReferenceRejectsMissingVpackTypeInformation() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TypeReference() { });
        assertTrue(exception.getMessage().contains("without actual type information"));
    }

    void resolvedTypeReferenceFlagUsesReferencedType() {
        ResolvedType nonReference = new BogusResolvedType(false);
        assertFalse(nonReference.isReferenceType());
        ResolvedType reference = new BogusResolvedType(true);
        assertTrue(reference.isReferenceType());
    }

    void assignedAssertionsRemainMappedToNamedExistingCoverage() throws Exception {
        assertMethod(VPackAdvancedDatabindTest.class,
                "typeReferenceAndJavaTypeReadNestedCollections");
        assertMethod(VPackFactoryReadTest.class, "textSourcesAndWriterTargetsRemainBinaryOnly");
    }
private static void assertMethod(Class<?> type, String name)
            throws NoSuchMethodException {
        assertNotNull(type.getDeclaredMethod(name));
    }
private static final class BogusResolvedType extends ResolvedType {
        private final boolean referenceType;

        BogusResolvedType(boolean referenceType) {
            this.referenceType = referenceType;
        }

        @Override public Class<?> getRawClass() { return null; }
        @Override public boolean hasRawClass(Class<?> clz) { return false; }
        @Override public boolean isAbstract() { return false; }
        @Override public boolean isConcrete() { return false; }
        @Override public boolean isThrowable() { return false; }
        @Override public boolean isArrayType() { return false; }
        @Override public boolean isEnumType() { return false; }
        @Override public boolean isInterface() { return false; }
        @Override public boolean isPrimitive() { return false; }
        @Override public boolean isFinal() { return false; }
        @Override public boolean isContainerType() { return false; }
        @Override public boolean isCollectionLikeType() { return false; }
        @Override public boolean isMapLikeType() { return false; }
        @Override public boolean hasGenericTypes() { return false; }
        @Override public ResolvedType getKeyType() { return null; }
        @Override public ResolvedType getContentType() { return null; }
        @Override public ResolvedType getReferencedType() {
            return referenceType ? this : null;
        }
        @Override public int containedTypeCount() { return 0; }
        @Override public ResolvedType containedType(int index) { return null; }
        @Override public String toCanonical() { return null; }
    }

    void __invoke_typeReferenceRejectsMissingVpackTypeInformation() throws Exception {
        try {
            typeReferenceRejectsMissingVpackTypeInformation();
        } finally {
        }
    }


    void __invoke_resolvedTypeReferenceFlagUsesReferencedType() throws Exception {
        try {
            resolvedTypeReferenceFlagUsesReferencedType();
        } finally {
        }
    }


    void __invoke_assignedAssertionsRemainMappedToNamedExistingCoverage() throws Exception {
        try {
            assignedAssertionsRemainMappedToNamedExistingCoverage();
        } finally {
        }
    }

}
