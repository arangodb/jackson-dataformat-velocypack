package tools.jackson.dataformat.velocypack;

import com.fasterxml.jackson.annotation.JsonTypeName;

/** Test-only external permitted subtype preserving the original simple type id. */
@JsonTypeName("DuplicateSubClassForSealedClasses")
public final class RelocatedDuplicateSubClassForSealedClasses
        extends VPackSealedTypes.DuplicateSuperClass { }
