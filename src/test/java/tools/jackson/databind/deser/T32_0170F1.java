package tools.jackson.databind.deser;

import java.util.List;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.DeferredBindingException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0170F1 {
private static final byte[] PERSON_INVALID_AGE = VPackWireFixtureTest.hex(
            "0b 1b 02 44 6e 61 6d 65 44 4a 6f 68 6e 43 61 67 65 47 69 6e 76 61 6c 69 64 0d 03");
private static final byte[] PRIMITIVE_INT = VPackWireFixtureTest.hex(
            "0b 15 01 48 69 6e 74 56 61 6c 75 65 47 69 6e 76 61 6c 69 64 03");
private static final byte[] PRIMITIVE_LONG = VPackWireFixtureTest.hex(
            "0b 16 01 49 6c 6f 6e 67 56 61 6c 75 65 47 69 6e 76 61 6c 69 64 03");
private static final byte[] PRIMITIVE_DOUBLE = VPackWireFixtureTest.hex(
            "0b 18 01 4b 64 6f 75 62 6c 65 56 61 6c 75 65 47 69 6e 76 61 6c 69 64 03");
private static final byte[] PRIMITIVE_BOOLEAN = VPackWireFixtureTest.hex(
            "0b 16 01 49 62 6f 6f 6c 56 61 6c 75 65 47 69 6e 76 61 6c 69 64 03");
private static final byte[] BOXED_INTEGER = VPackWireFixtureTest.hex(
            "0b 15 01 48 62 6f 78 65 64 49 6e 74 47 69 6e 76 61 6c 69 64 03");
private static final byte[] MULTIPLE_TYPE_ERRORS = VPackWireFixtureTest.hex(
            "0b 34 03 48 69 6e 74 56 61 6c 75 65 44 62 61 64 31 49 6c 6f 6e 67 56 61 6c 75 65 44 62 61 64 32 4b 64 6f 75 62 6c 65 56 61 6c 75 65 44 62 61 64 33 20 03 11");
private static final ObjectMapper MAPPER = new VPackMapper();

    void primitiveInt() {
        DeferredBindingException exception = expectCollected(PRIMITIVE_INT,
                TypedData.class);
        assertOneProblem(exception);
        assertEquals("invalid", exception.getProblems().get(0).getRawValue());
    }

    void primitiveLong() {
        assertOneProblem(expectCollected(PRIMITIVE_LONG, TypedData.class));
    }

    void primitiveDouble() {
        assertOneProblem(expectCollected(PRIMITIVE_DOUBLE, TypedData.class));
    }

    void primitiveBoolean() {
        assertOneProblem(expectCollected(PRIMITIVE_BOOLEAN, TypedData.class));
    }

    void boxedInteger() {
        assertOneProblem(expectCollected(BOXED_INTEGER, TypedData.class));
    }

    void multipleTypeErrors() {
        DeferredBindingException exception = expectCollected(MULTIPLE_TYPE_ERRORS,
                TypedData.class);
        assertEquals(3, exception.getProblems().size());
        List<String> paths = exception.getProblems().stream()
                .map(problem -> problem.getPath().toString())
                .toList();
        assertTrue(paths.contains("/intValue"), paths.toString());
        assertTrue(paths.contains("/longValue"), paths.toString());
        assertTrue(paths.contains("/doubleValue"), paths.toString());
    }
private static DeferredBindingException expectCollected(byte[] input,
            Class<?> valueType) {
        try {
            MAPPER.readerFor(valueType).problemCollectingReader()
                    .readValueCollectingProblems(input);
        } catch (DeferredBindingException exception) {
            return exception;
        } catch (Exception exception) {
            throw new AssertionError("Unexpected exception", exception);
        }
        throw new AssertionError("Expected DeferredBindingException");
    }
private static void assertOneProblem(DeferredBindingException exception) {
        assertEquals(1, exception.getProblems().size());
    }
static class Person {
        public String name;
        public int age;
        public boolean active;
    }
static class TypedData {
        public int intValue;
        public long longValue;
        public double doubleValue;
        public float floatValue;
        public boolean boolValue;
        public Integer boxedInt;
        public String stringValue;
    }

    void __invoke_primitiveInt() throws Exception {
        try {
            primitiveInt();
        } finally {
        }
    }


    void __invoke_primitiveLong() throws Exception {
        try {
            primitiveLong();
        } finally {
        }
    }


    void __invoke_primitiveDouble() throws Exception {
        try {
            primitiveDouble();
        } finally {
        }
    }


    void __invoke_primitiveBoolean() throws Exception {
        try {
            primitiveBoolean();
        } finally {
        }
    }


    void __invoke_boxedInteger() throws Exception {
        try {
            boxedInteger();
        } finally {
        }
    }


    void __invoke_multipleTypeErrors() throws Exception {
        try {
            multipleTypeErrors();
        } finally {
        }
    }

}
