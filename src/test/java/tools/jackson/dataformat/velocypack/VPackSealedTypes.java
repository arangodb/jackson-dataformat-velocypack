package tools.jackson.dataformat.velocypack;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Shared sealed hierarchy kept in this package for its permitted package-local subtype. */
public final class VPackSealedTypes {
    private VPackSealedTypes() { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    public static sealed class DuplicateSuperClass
            permits LocalDuplicateSubClass, RelocatedDuplicateSubClassForSealedClasses { }

    public static final class LocalDuplicateSubClass extends DuplicateSuperClass { }
}
