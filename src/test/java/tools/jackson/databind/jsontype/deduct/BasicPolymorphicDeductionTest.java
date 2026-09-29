package tools.jackson.databind.jsontype.deduct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}
 */
class BasicPolymorphicDeductionTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testAliasWithPolymorphicDeduction4327(String)}.
 */
    @ParameterizedTest(name = "testAliasWithPolymorphicDeduction4327Vpack[{index}]")
    @ValueSource(strings = {"y", "Y", "yy", "ff", "X"})
    // Provenance: BasicPolymorphicDeductionTest#testAliasWithPolymorphicDeduction4327(String).
    void testAliasWithPolymorphicDeduction4327Vpack(String field) throws Exception {
            new T32_0433Fixture().__invoke_testAliasWithPolymorphicDeduction4327Vpack(field);
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testAmbiguousClasses()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testAmbiguousClasses().
    void testAmbiguousClassesVpack() throws Exception {
            new T32_0433Fixture().__invoke_testAmbiguousClassesVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testAmbiguousProperties()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testAmbiguousProperties().
    void testAmbiguousPropertiesVpack() throws Exception {
            new T32_0433Fixture().__invoke_testAmbiguousPropertiesVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testArrayInference()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testArrayInference().
    void testArrayInferenceVpack() throws Exception {
            new T32_0433Fixture().__invoke_testArrayInferenceVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testCaseInsensitiveInference()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testCaseInsensitiveInference().
    void testCaseInsensitiveInferenceVpack() throws Exception {
            new T32_0433Fixture().__invoke_testCaseInsensitiveInferenceVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testContainedInference()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testContainedInference().
    void testContainedInferenceVpack() throws Exception {
            new T32_0433Fixture().__invoke_testContainedInferenceVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testContainedInferenceOfEmptySubtype()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testContainedInferenceOfEmptySubtype().
    void testContainedInferenceOfEmptySubtypeVpack() throws Exception {
            new T32_0433Fixture().__invoke_testContainedInferenceOfEmptySubtypeVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testDefaultImpl()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testDefaultImpl().
    void testDefaultImplVpack() throws Exception {
            new T32_0433Fixture().__invoke_testDefaultImplVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testFailOnInvalidSubtype()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testFailOnInvalidSubtype().
    void testFailOnInvalidSubtypeVpack() throws Exception {
            new T32_0433Fixture().__invoke_testFailOnInvalidSubtypeVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testIgnoreProperties()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testIgnoreProperties().
    void testIgnorePropertiesVpack() throws Exception {
            new T32_0433Fixture().__invoke_testIgnorePropertiesVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testListInference()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testListInference().
    void testListInferenceVpack() throws Exception {
            new T32_0434F0().__invoke_testListInferenceVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testListSerialization()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testListSerialization().
    void testListSerializationVpack() throws Exception {
            new T32_0434F0().__invoke_testListSerializationVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testMapInference()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testMapInference().
    void testMapInferenceVpack() throws Exception {
            new T32_0434F0().__invoke_testMapInferenceVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testSimpleInference()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testSimpleInference().
    void testSimpleInferenceVpack() throws Exception {
            new T32_0434F0().__invoke_testSimpleInferenceVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testSimpleInferenceOfEmptySubtype()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testSimpleInferenceOfEmptySubtype().
    void testSimpleInferenceOfEmptySubtypeVpack() throws Exception {
            new T32_0434F0().__invoke_testSimpleInferenceOfEmptySubtypeVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testSimpleInferenceOfEmptySubtypeDoesntMatchNull()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testSimpleInferenceOfEmptySubtypeDoesntMatchNull().
    void testSimpleInferenceOfEmptySubtypeDoesntMatchNullVpack() throws Exception {
            new T32_0434F0().__invoke_testSimpleInferenceOfEmptySubtypeDoesntMatchNullVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testSimpleSerialization()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testSimpleSerialization().
    void testSimpleSerializationVpack() throws Exception {
            new T32_0434F0().__invoke_testSimpleSerializationVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testWithEnum()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testWithEnum().
    void testWithEnumVpack() throws Exception {
            new T32_0434F0().__invoke_testWithEnumVpack();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest}.
 * Original test methods: {@link tools.jackson.databind.jsontype.deduct.BasicPolymorphicDeductionTest#testWithPojoAsJsonValue()}.
 */
    @Test
    // Provenance: BasicPolymorphicDeductionTest#testWithPojoAsJsonValue().
    void testWithPojoAsJsonValueVpack() throws Exception {
            new T32_0434F0().__invoke_testWithPojoAsJsonValueVpack();
        }
}
