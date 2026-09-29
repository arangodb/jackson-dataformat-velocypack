package tools.jackson.databind.exc;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.UnresolvedForwardReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0296F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] FAILING_SETTER = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 33 01");
private static final byte[] FAILING_ANY_SETTER = VPackWireFixtureTest.hex(
            "14 07 41 61 28 48 01");

    // Provenance: UnresolvedForwardReferenceTest#testWithAndWithoutStackTraces().
    void testWithAndWithoutStackTracesVpack() throws Exception {
        try (JsonParser parser = MAPPER.createParser(EMPTY_OBJECT)) {
            UnresolvedForwardReference exception = new UnresolvedForwardReference(
                    parser, "test");
            StackTraceElement[] stack = exception.getStackTrace();
            assertEquals(0, stack.length);

            exception = exception.withStackTrace();
            stack = exception.getStackTrace();
            assertTrue(stack.length >= 1,
                    "Should have filled in stack traces, only got: " + stack.length);
        }
    }
private static void assertLocationAfterValue(TokenStreamLocation location, int byteOffset) {
        assertNotSame(TokenStreamLocation.NA, location);
        assertEquals(byteOffset, location.getByteOffset());
        assertEquals(-1, location.getLineNr());
        assertEquals(-1, location.getColumnNr());
    }
static class CustomException extends RuntimeException {
        CustomException(String message) {
            super(message);
        }
    }
static class Feature1347DeserBean {
        public void setValue(int value) {
            throw new CustomException("setValue, fail on purpose");
        }
    }
static class AnySetterBean {
        protected Map<String, Integer> props = new HashMap<>();

        @JsonAnySetter
        public void prop(String name, Integer value) {
            throw new CustomException("@JsonAnySetter, fail on purpose");
        }
    }

    void __invoke_testWithAndWithoutStackTracesVpack() throws Exception {
        try {
            testWithAndWithoutStackTracesVpack();
        } finally {
        }
    }

}
