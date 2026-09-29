package tools.jackson.core.unittest.read;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.core.unittest.read.NextXxxAccessTest}
 * {@link tools.jackson.core.unittest.read.UTF8NamesParseTest}
 */
class NextXxxAccessTest {
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#isNextTokenName()}.
 */
    @Test
    void isNextTokenNameMatchesAndSkipsLiteralObject() throws Exception {
            new T32_0071Fixture().__invoke_isNextTokenNameMatchesAndSkipsLiteralObject();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#isNextTokenName2()}.
 */
    @Test
    void isNextTokenNameAcceptsSerializableStringInterface() throws Exception {
            new T32_0071Fixture().__invoke_isNextTokenNameAcceptsSerializableStringInterface();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#isNextTokenName3()}.
 */
    @Test
    void nextNameReturnsNamesAndAdvances() throws Exception {
            new T32_0071Fixture().__invoke_nextNameReturnsNamesAndAdvances();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}, {@link tools.jackson.core.unittest.read.UTF8NamesParseTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#isNextTokenName4()}, {@link tools.jackson.core.unittest.read.UTF8NamesParseTest#nextFieldName()}.
 */
    @Test
    void nextNameMatchesNegativeAndPositiveIntegers() throws Exception {
            new T32_0071Fixture().__invoke_nextNameMatchesNegativeAndPositiveIntegers();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#isNextTokenName5()}.
 */
    @Test
    void nextNameMatchesNestedObjectAndNull() throws Exception {
            new T32_0071Fixture().__invoke_nextNameMatchesNestedObjectAndNull();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#isNextTokenName()}.
 */
    @Test
    void nextNameSkipsEmptyArrayInBinaryObject() throws Exception {
            new T32_0071Fixture().__invoke_nextNameSkipsEmptyArrayInBinaryObject();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#nextNameWithLongContent()}.
 */
    @Test
    void nextNameHandlesManyDeterministicFields() throws Exception {
            new T32_0071Fixture().__invoke_nextNameHandlesManyDeterministicFields();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#nextIntValue()}.
 */
    @Test
    void nextIntValueUsesVpackIntegerTokens() throws Exception {
            new T32_0071Fixture().__invoke_nextIntValueUsesVpackIntegerTokens();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#nextLongValue()}.
 */
    @Test
    void nextLongValueUsesVpackIntegerTokens() throws Exception {
            new T32_0071Fixture().__invoke_nextLongValueUsesVpackIntegerTokens();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#nextBooleanValue()}.
 */
    @Test
    void nextBooleanValueUsesBooleanTokens() throws Exception {
            new T32_0071Fixture().__invoke_nextBooleanValueUsesBooleanTokens();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#issue34()}.
 */
    @Test
    void repeatedRootsKeepNextNameStateAfterIssue34() throws Exception {
            new T32_0071Fixture().__invoke_repeatedRootsKeepNextNameStateAfterIssue34();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#issue38()}.
 */
    @Test
    void nextNameHandlesIssue38Object() throws Exception {
            new T32_0071Fixture().__invoke_nextNameHandlesIssue38Object();
        }
/**
 * VPack adaptation of {@link tools.jackson.core.unittest.read.NextXxxAccessTest}.
 * Original test methods: {@link tools.jackson.core.unittest.read.NextXxxAccessTest#nextTextValue()}.
 */
    @Test
    void nextStringValueMatchesPortableTextAccessorProgression() throws Exception {
            new T32_0072F0().__invoke_nextStringValueMatchesPortableTextAccessorProgression();
        }
}
