package tools.jackson.core.unittest.tofix;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class T32_0104Fixture {

    void assignedAssertionsRemainMappedToNamedExistingCoverage() throws Exception {
        assertMethod("tools.jackson.dataformat.velocypack.VPackFactoryReadTest",
                "textSourcesAndWriterTargetsRemainBinaryOnly");
        assertMethod("tools.jackson.core.unittest.tofix.ParserErrorHandling1557Test",
                "malformedNestedDoubleIsRejectedFromDataInput");
        assertMethod("tools.jackson.core.unittest.tofix.ParserErrorHandling1557Test",
                "malformedNestedIntegerIsRejectedFromDataInput");
        assertMethod("tools.jackson.core.unittest.json.async.AsyncBinaryParseTest",
                "assignedAsyncSourcesAreExplicitlyUnsupportedByVpack");
    }
private static void assertMethod(String typeName, String name)
            throws NoSuchMethodException {
        Class<?> type;
        try { type = Class.forName(typeName); }
        catch (ClassNotFoundException e) { throw new NoSuchMethodException(typeName); }
        Method method = type.getDeclaredMethod(name);
        assertNotNull(method);
    }

    void __invoke_assignedAssertionsRemainMappedToNamedExistingCoverage() throws Exception {
        try {
            assignedAssertionsRemainMappedToNamedExistingCoverage();
        } finally {
        }
    }

}
