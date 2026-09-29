package tools.jackson.databind.tofix;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.tofix.JsonTypeIdConflict3681Test}
 */
class JsonTypeIdConflict3681Test {
/**
 * VPack adaptation of {@link tools.jackson.databind.tofix.JsonTypeIdConflict3681Test}.
 * Original test methods: {@link tools.jackson.databind.tofix.JsonTypeIdConflict3681Test#failureWithTypeIdConflict()}.
 */
    @Disabled("Databind tofix #3681: conflicting interface type ids; revisit after upstream fix")
    @Test
    // Provenance: JsonTypeIdConflict3681Test#failureWithTypeIdConflict(); upstream marks this known failure expected.
    void conflictingInterfaceTypeIdsFailForVpackInput() throws Exception {
            new T32_0611Fixture().__invoke_conflictingInterfaceTypeIdsFailForVpackInput();
        }
}
