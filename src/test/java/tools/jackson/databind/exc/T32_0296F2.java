package tools.jackson.databind.exc;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import tools.jackson.core.JacksonException;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0296F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] FAILING_SETTER = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 33 01");
private static final byte[] FAILING_ANY_SETTER = VPackWireFixtureTest.hex(
            "14 07 41 61 28 48 01");

    // Provenance: UnwrapRootCause4603Test#testExceptionWrappingConfiguration().
    void testExceptionWrappingConfigurationVpack() throws Exception {
        JacksonException result = assertThrows(JacksonException.class,
                () -> MAPPER.readValue(FAILING_SETTER, Feature1347DeserBean.class));
        assertInstanceOf(DatabindException.class, result);
        assertInstanceOf(CustomException.class, result.getCause());
        assertLocationAfterValue(result.getLocation(), 9);
    }

    // Provenance: UnwrapRootCause4603Test#testWithAnySetter().
    void testWithAnySetterVpack() throws Exception {
        DatabindException result = assertThrows(DatabindException.class,
                () -> MAPPER.readValue(FAILING_ANY_SETTER, AnySetterBean.class));
        assertInstanceOf(CustomException.class, result.getCause());
        assertLocationAfterValue(result.getLocation(), 6);
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

    void __invoke_testExceptionWrappingConfigurationVpack() throws Exception {
        try {
            testExceptionWrappingConfigurationVpack();
        } finally {
        }
    }


    void __invoke_testWithAnySetterVpack() throws Exception {
        try {
            testWithAnySetterVpack();
        } finally {
        }
    }

}
