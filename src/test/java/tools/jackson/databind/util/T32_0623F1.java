package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.ClassUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0623F1 {

    // Provenance: ClassUtilTest#testApostrophed().
    void classUtilApostrophedVpack() {
        assertEquals("'test'", ClassUtil.apostrophed("test"));
        assertEquals("[null]", ClassUtil.apostrophed(null));
        assertEquals("''", ClassUtil.apostrophed(""));
    }

    // Provenance: ClassUtilTest#testBackticked().
    void classUtilBacktickedVpack() {
        assertEquals("`test`", ClassUtil.backticked("test"));
        assertEquals("[null]", ClassUtil.backticked(null));
        assertEquals("``", ClassUtil.backticked(""));
    }

    // Provenance: ClassUtilTest#testCanBeABeanType().
    void classUtilCanBeABeanTypeVpack() {
        assertEquals("annotation", ClassUtil.canBeABeanType(java.lang.annotation.Retention.class));
        assertEquals("array", ClassUtil.canBeABeanType(String[].class));
        assertEquals("enum", ClassUtil.canBeABeanType(TestEnum.class));
        assertEquals("primitive", ClassUtil.canBeABeanType(Integer.TYPE));
        assertNull(ClassUtil.canBeABeanType(Integer.class));
        assertEquals("non-static member class", ClassUtil.isLocalType(InnerNonStatic.class, false));
        assertNull(ClassUtil.isLocalType(Integer.class, false));
    }

    // Provenance: ClassUtilTest#testCloseEtc().
    void classUtilCloseEtcVpack() throws Exception {
        Exception runtime = new IllegalArgumentException("test");
        try {
            ClassUtil.closeOnFailAndThrowAsJacksonE(null, null, runtime);
            fail("Should not pass");
        } catch (Exception actual) {
            assertSame(runtime, actual);
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(ObjectWriteContext.empty(), bytes);
        Exception checked = new Exception("test");
        try {
            ClassUtil.closeOnFailAndThrowAsJacksonE(generator, bytes, checked);
            fail("Should not pass");
        } catch (Exception actual) {
            assertEquals(RuntimeException.class, actual.getClass());
            assertSame(checked, actual.getCause());
            assertEquals("test", actual.getCause().getMessage());
            assertTrue(generator.isClosed());
        }
        generator.close();
    }

    // Provenance: ClassUtilTest#testDescs().
    void classUtilDescsVpack() {
        TypeFactory types = TypeFactory.createDefaultInstance();
        String expected = "`java.lang.String`";
        assertEquals(expected, ClassUtil.getClassDescription("foo"));
        assertEquals(expected, ClassUtil.getClassDescription(String.class));
        JavaType stringType = types.constructType(String.class);
        assertEquals(expected, ClassUtil.getTypeDescription(stringType));
        JavaType mapType = types.constructType(new TypeReference<Map<String, Integer>>() {
            private static final long serialVersionUID = 1L;
        });
        assertEquals("`java.util.Map<java.lang.String,java.lang.Integer>`",
                ClassUtil.getTypeDescription(mapType));
    }

    // Provenance: ClassUtilTest#testEnforceSubtype().
    void classUtilEnforceSubtypeVpack() {
        try {
            ClassUtil.verifyMustOverride(Number.class, Boolean.TRUE, "Test");
            fail("Expected missing override failure");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("must override method 'Test'"));
        }
    }

    // Provenance: ClassUtilTest#testExceptionHelpers().
    void classUtilExceptionHelpersVpack() {
        RuntimeException cause = new RuntimeException("test");
        RuntimeException wrapper = new RuntimeException(cause);
        assertSame(cause, ClassUtil.getRootCause(wrapper));
        try {
            ClassUtil.throwAsIAE(cause);
            fail("Shouldn't get this far");
        } catch (RuntimeException actual) {
            assertSame(cause, actual);
        }
        Error error = new Error();
        try {
            ClassUtil.throwAsIAE(error);
            fail("Shouldn't get this far");
        } catch (Error actual) {
            assertSame(error, actual);
        }
        try {
            ClassUtil.unwrapAndThrowAsIAE(wrapper);
            fail("Shouldn't get this far");
        } catch (RuntimeException actual) {
            assertSame(cause, actual);
        }
    }

    // Provenance: ClassUtilTest#testExceptionMessage().
    void classUtilExceptionMessageVpack() {
        DatabindException jacksonException = new DatabindException("A message") {
            @Override
            public String getOriginalMessage() {
                return "The original message";
            }
        };
        assertEquals("The original message", ClassUtil.exceptionMessage(jacksonException));
        try {
            T32_0623F1.class.getDeclaredMethod("throwsException").invoke(null);
        } catch (ReflectiveOperationException e) {
            assertEquals("A custom message", ClassUtil.exceptionMessage(e));
        }
    }
static void throwsException() {
        throw new IllegalArgumentException("A custom message");
    }
enum TestEnum { A }
class InnerNonStatic { }

    void __invoke_classUtilApostrophedVpack() throws Exception {
        try {
            classUtilApostrophedVpack();
        } finally {
        }
    }


    void __invoke_classUtilBacktickedVpack() throws Exception {
        try {
            classUtilBacktickedVpack();
        } finally {
        }
    }


    void __invoke_classUtilCanBeABeanTypeVpack() throws Exception {
        try {
            classUtilCanBeABeanTypeVpack();
        } finally {
        }
    }


    void __invoke_classUtilCloseEtcVpack() throws Exception {
        try {
            classUtilCloseEtcVpack();
        } finally {
        }
    }


    void __invoke_classUtilDescsVpack() throws Exception {
        try {
            classUtilDescsVpack();
        } finally {
        }
    }


    void __invoke_classUtilEnforceSubtypeVpack() throws Exception {
        try {
            classUtilEnforceSubtypeVpack();
        } finally {
        }
    }


    void __invoke_classUtilExceptionHelpersVpack() throws Exception {
        try {
            classUtilExceptionHelpersVpack();
        } finally {
        }
    }


    void __invoke_classUtilExceptionMessageVpack() throws Exception {
        try {
            classUtilExceptionMessageVpack();
        } finally {
        }
    }

}
