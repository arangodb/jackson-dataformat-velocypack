package tools.jackson.databind.deser;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.BucketIsolationTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultBehaviorTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.HardFailureTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.LimitReachedTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.MessageFormattingTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.RootLevelTests}
 * {@link tools.jackson.databind.deser.CollectingErrorsTest.UnknownPropertyTests}
 */
class CollectingErrorsTest {
    @org.junit.jupiter.api.Nested
    class BucketIsolationTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.BucketIsolationTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.BucketIsolationTests#successiveCalls()}.
 */
    @Test
    void successiveCalls() throws Exception {
            new T32_0169F2().__invoke_successiveCalls();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.BucketIsolationTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.BucketIsolationTests#concurrentCalls()}.
 */
    @Test
    void concurrentCalls() throws Exception {
            new T32_0169F2().__invoke_concurrentCalls();
        }
    }
    @org.junit.jupiter.api.Nested
    class DefaultBehaviorTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultBehaviorTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultBehaviorTests#failFastDefault()}.
 */
    @Test
    void failFastDefault() throws Exception {
            new T32_0170F0().__invoke_failFastDefault();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultBehaviorTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultBehaviorTests#failFastAfterCollectErrors()}.
 */
    @Test
    void failFastAfterCollectErrors() throws Exception {
            new T32_0170F0().__invoke_failFastAfterCollectErrors();
        }
    }
    @org.junit.jupiter.api.Nested
    class DefaultValuePolicyTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests#primitiveInt()}.
 */
    @Test
    void primitiveInt() throws Exception {
            new T32_0170F1().__invoke_primitiveInt();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests#primitiveLong()}.
 */
    @Test
    void primitiveLong() throws Exception {
            new T32_0170F1().__invoke_primitiveLong();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests#primitiveDouble()}.
 */
    @Test
    void primitiveDouble() throws Exception {
            new T32_0170F1().__invoke_primitiveDouble();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests#primitiveBoolean()}.
 */
    @Test
    void primitiveBoolean() throws Exception {
            new T32_0170F1().__invoke_primitiveBoolean();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests#boxedInteger()}.
 */
    @Test
    void boxedInteger() throws Exception {
            new T32_0170F1().__invoke_boxedInteger();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.DefaultValuePolicyTests#multipleTypeErrors()}.
 */
    @Test
    void multipleTypeErrors() throws Exception {
            new T32_0170F1().__invoke_multipleTypeErrors();
        }
    }
    @org.junit.jupiter.api.Nested
    class EdgeCaseTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests#collectFromByteArray()}.
 */
    @Test
    void collectFromByteArray() throws Exception {
            new T32_0170F2().__invoke_collectFromByteArray();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests#collectFromFile()}.
 */
    @Test
    void collectFromFile() throws Exception {
            new T32_0170F2().__invoke_collectFromFile();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests#collectFromInputStream()}.
 */
    @Test
    void collectFromInputStream() throws Exception {
            new T32_0170F2().__invoke_collectFromInputStream();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests#collectFromReader()}.
 */
    @Test
    void collectFromReader() throws Exception {
            new T32_0170F2().__invoke_collectFromReader();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests#validateMaxProblems()}.
 */
    @Test
    void validateMaxProblems() throws Exception {
            new T32_0171F0().__invoke_validateMaxProblems();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests#emptyJson()}.
 */
    @Test
    void emptyJson() throws Exception {
            new T32_0171F0().__invoke_emptyJson();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.EdgeCaseTests#nullParser()}.
 */
    @Test
    void nullParser() throws Exception {
            new T32_0171F0().__invoke_nullParser();
        }
    }
    @org.junit.jupiter.api.Nested
    class HardFailureTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.HardFailureTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.HardFailureTests#suppressedProblems()}.
 */
    @Test
    void suppressedProblems() throws Exception {
            new T32_0171F1().__invoke_suppressedProblems();
        }
    }
    @org.junit.jupiter.api.Nested
    class JsonPointerEscapingTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests#escapeTilde()}.
 */
    @Test
    void escapeTilde() throws Exception {
            new T32_0171F2().__invoke_escapeTilde();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests#escapeSlash()}.
 */
    @Test
    void escapeSlash() throws Exception {
            new T32_0171F2().__invoke_escapeSlash();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests#escapeBoth()}.
 */
    @Test
    void escapeBoth() throws Exception {
            new T32_0171F2().__invoke_escapeBoth();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.JsonPointerEscapingTests#arrayIndices()}.
 */
    @Test
    void arrayIndices() throws Exception {
            new T32_0171F2().__invoke_arrayIndices();
        }
    }
    @org.junit.jupiter.api.Nested
    class LimitReachedTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.LimitReachedTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.LimitReachedTests#defaultLimit()}.
 */
    @Test
    void defaultLimit() throws Exception {
            new T32_0172F0().__invoke_defaultLimit();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.LimitReachedTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.LimitReachedTests#customLimit()}.
 */
    @Test
    void customLimit() throws Exception {
            new T32_0172F0().__invoke_customLimit();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.LimitReachedTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.LimitReachedTests#underLimit()}.
 */
    @Test
    void underLimit() throws Exception {
            new T32_0172F0().__invoke_underLimit();
        }
    }
    @org.junit.jupiter.api.Nested
    class MessageFormattingTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.MessageFormattingTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.MessageFormattingTests#multipleErrors()}.
 */
    @Test
    void multipleErrors() throws Exception {
            new T32_0172F1().__invoke_multipleErrors();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.MessageFormattingTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.MessageFormattingTests#singleError()}.
 */
    @Test
    void singleError() throws Exception {
            new T32_0172F1().__invoke_singleError();
        }
    }
    @org.junit.jupiter.api.Nested
    class RootLevelTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.RootLevelTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.RootLevelTests#propertyPathFormatting()}.
 */
    @Test
    void propertyPathFormatting() throws Exception {
            new T32_0172F2().__invoke_propertyPathFormatting();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.RootLevelTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.RootLevelTests#rootLevelTypeMismatch()}.
 */
    @Test
    void rootLevelTypeMismatch() throws Exception {
            new T32_0172F2().__invoke_rootLevelTypeMismatch();
        }
    }
    @org.junit.jupiter.api.Nested
    class UnknownPropertyTests {
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.UnknownPropertyTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.UnknownPropertyTests#unknownProperty()}.
 */
    @Test
    void unknownProperty() throws Exception {
            new T32_0173F0().__invoke_unknownProperty();
        }
    /**
 * VPack adaptation of {@link tools.jackson.databind.deser.CollectingErrorsTest.UnknownPropertyTests}.
 * Original test methods: {@link tools.jackson.databind.deser.CollectingErrorsTest.UnknownPropertyTests#skipUnknownChildren()}.
 */
    @Test
    void skipUnknownChildren() throws Exception {
            new T32_0173F0().__invoke_skipUnknownChildren();
        }
    }
}
