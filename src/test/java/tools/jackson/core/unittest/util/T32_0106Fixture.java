package tools.jackson.core.unittest.util;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class T32_0106Fixture {

    void assignedAssertionsRemainMappedToVpackPrettyPrinterAbsence()
            throws Exception {
        assertMethod("tools.jackson.core.unittest.JDKSerializabilityTest", "prettyPrinter");
    }
private static void assertMethod(String typeName, String name)
            throws NoSuchMethodException {
        Class<?> type;
        try { type = Class.forName(typeName); }
        catch (ClassNotFoundException e) { throw new NoSuchMethodException(typeName); }
        Method method = type.getDeclaredMethod(name);
        assertNotNull(method);
    }

    void __invoke_assignedAssertionsRemainMappedToVpackPrettyPrinterAbsence() throws Exception {
        try {
            assignedAssertionsRemainMappedToVpackPrettyPrinterAbsence();
        } finally {
        }
    }

}
