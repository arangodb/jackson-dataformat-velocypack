package tools.jackson.databind.ser.dos;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.ser.dos.CyclicDataSerTest}
 */
class CyclicDataSerTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.ser.dos.CyclicDataSerTest}.
 * Original test methods: {@link tools.jackson.databind.ser.dos.CyclicDataSerTest#testLinkedAndCyclic()}.
 */
    @Test
    // Provenance: CyclicDataSerTest#testLinkedAndCyclic().
    void testLinkedAndCyclicVpack() throws Exception {
            new T32_0563Fixture().__invoke_testLinkedAndCyclicVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.ser.dos.CyclicDataSerTest}.
 * Original test methods: {@link tools.jackson.databind.ser.dos.CyclicDataSerTest#testListWithSelfReference()}.
 */
    @Test
    // Provenance: CyclicDataSerTest#testListWithSelfReference().
    void testListWithSelfReferenceVpack() throws Exception {
            new T32_0564Fixture().__invoke_testListWithSelfReferenceVpack();
        }
}
