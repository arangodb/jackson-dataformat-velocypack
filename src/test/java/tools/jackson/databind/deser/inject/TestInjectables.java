package tools.jackson.databind.deser.inject;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.deser.inject.TestInjectables}
 */
class TestInjectables {
/**
 * VPack adaptation of {@link tools.jackson.databind.deser.inject.TestInjectables}.
 * Original test methods: {@link tools.jackson.databind.deser.inject.TestInjectables#testSimple()}.
 */
    @Test
    void testSimple() throws Exception {
            new T32_0250Fixture().__invoke_testSimple();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.deser.inject.TestInjectables}.
 * Original test methods: {@link tools.jackson.databind.deser.inject.TestInjectables#testWithCtors()}.
 */
    @Test
    void testWithCtors() throws Exception {
            new T32_0250Fixture().__invoke_testWithCtors();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.deser.inject.TestInjectables}.
 * Original test methods: {@link tools.jackson.databind.deser.inject.TestInjectables#testTwoInjectablesViaCreator()}.
 */
    @Test
    void testTwoInjectablesViaCreator() throws Exception {
            new T32_0250Fixture().__invoke_testTwoInjectablesViaCreator();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.deser.inject.TestInjectables}.
 * Original test methods: {@link tools.jackson.databind.deser.inject.TestInjectables#testIssue471()}.
 */
    @Test
    void testIssue471() throws Exception {
            new T32_0250Fixture().__invoke_testIssue471();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.deser.inject.TestInjectables}.
 * Original test methods: {@link tools.jackson.databind.deser.inject.TestInjectables#testTransientField()}.
 */
    @Test
    void testTransientField() throws Exception {
            new T32_0250Fixture().__invoke_testTransientField();
        }
}
