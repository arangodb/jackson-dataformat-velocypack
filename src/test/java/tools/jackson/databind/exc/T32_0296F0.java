package tools.jackson.databind.exc;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0296F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] FAILING_SETTER = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 33 01");
private static final byte[] FAILING_ANY_SETTER = VPackWireFixtureTest.hex(
            "14 07 41 61 28 48 01");

    // Provenance: ThrowableSerializationTest#testSimpleOther().
    void testSimpleOtherVpack() throws Exception {
        try (JsonParser parser = MAPPER.createParser(EMPTY_OBJECT)) {
            InvalidFormatException exception = InvalidFormatException.from(
                    parser, "Test", getClass(), String.class);
            byte[] encoded = MAPPER.writeValueAsBytes(exception);
            assertNotNull(encoded);
            assertTrue(encoded.length > 0);
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

    void __invoke_testSimpleOtherVpack() throws Exception {
        try {
            testSimpleOtherVpack();
        } finally {
        }
    }

}
