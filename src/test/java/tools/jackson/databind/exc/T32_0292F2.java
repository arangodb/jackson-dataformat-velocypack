package tools.jackson.databind.exc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0292F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] ALL_STACK_TRACE_FIELDS = VPackWireFixtureTest.hex(
            "14 8f 01 4f 63 6c 61 73 73 4c 6f 61 64 65 72 4e 61 6d 65 "
          + "4c 63 6c 61 73 73 4c 6f 61 64 65 72 58 4a 6d 6f 64 75 6c 65 4e 61 6d 65 "
          + "47 6d 6f 64 75 6c 65 59 4d 6d 6f 64 75 6c 65 56 65 72 73 69 6f 6e 43 31 2e 30 "
          + "49 63 6c 61 73 73 4e 61 6d 65 47 4d 79 43 6c 61 73 73 "
          + "4a 6d 65 74 68 6f 64 4e 61 6d 65 48 4d 79 4d 65 74 68 6f 64 "
          + "48 66 69 6c 65 4e 61 6d 65 4c 4d 79 43 6c 61 73 73 2e 6a 61 76 61 "
          + "4a 6c 69 6e 65 4e 75 6d 62 65 72 28 0a 07");
private static final byte[] ANY_SETTER = VPackWireFixtureTest.hex(
            "14 28 47 6d 65 73 73 61 67 65 48 61 6e 79 20 74 65 73 74 "
          + "4a 65 78 74 72 61 46 69 65 6c 64 48 65 78 74 72 61 56 61 6c 02");
private static final byte[] ANY_SETTER_WITHOUT_MESSAGE = VPackWireFixtureTest.hex(
            "14 13 4b 75 6e 6b 6e 6f 77 6e 50 72 6f 70 43 76 61 6c 01");
private static final byte[] CAUSE = VPackWireFixtureTest.hex(
            "14 28 47 6d 65 73 73 61 67 65 45 6f 75 74 65 72 "
          + "45 63 61 75 73 65 14 11 47 6d 65 73 73 61 67 65 45 69 6e 6e 65 72 01 02");
private static final byte[] CUSTOM_PROPERTY = VPackWireFixtureTest.hex(
            "14 1f 47 6d 65 73 73 61 67 65 4c 63 75 73 74 6f 6d 20 65 72 72 6f 72 "
          + "44 63 6f 64 65 28 2a 02");

    // Provenance: ThrowableDeserializerTest#testAnySetterException().
    void testAnySetterExceptionVpack() throws Exception {
        AnySetterException result = MAPPER.readValue(ANY_SETTER, AnySetterException.class);
        assertNotNull(result);
        assertEquals("any test", result.getMessage());
        assertEquals("extraVal", result.extra.get("extraField"));
    }

    // Provenance: ThrowableDeserializerTest#testAnySetterWithoutMessage().
    void testAnySetterWithoutMessageVpack() throws Exception {
        AnySetterException result = MAPPER.readValue(ANY_SETTER_WITHOUT_MESSAGE,
                AnySetterException.class);
        assertNotNull(result);
        assertNull(result.getMessage());
        assertEquals("val", result.extra.get("unknownProp"));
    }

    // Provenance: ThrowableDeserializerTest#testCauseDeserialization().
    void testCauseDeserializationVpack() throws Exception {
        IOException result = MAPPER.readValue(CAUSE, IOException.class);
        assertNotNull(result);
        assertEquals("outer", result.getMessage());
        assertNotNull(result.getCause());
        assertEquals("inner", result.getCause().getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testCauseDeserializationRoundTrip().
    void testCauseDeserializationRoundTripVpack() throws Exception {
        IOException input = new IOException("the outer exception", new Throwable("the cause"));
        IOException output = MAPPER.readValue(MAPPER.writeValueAsBytes(input), IOException.class);

        assertNotNull(output.getCause());
        assertEquals(input.getCause().getMessage(), output.getCause().getMessage());
        assertEquals(input.getCause().getStackTrace().length,
                output.getCause().getStackTrace().length);
        for (int i = 0; i < input.getCause().getStackTrace().length; ++i) {
            assertEquals(input.getCause().getStackTrace()[i], output.getCause().getStackTrace()[i]);
        }
    }

    // Provenance: ThrowableDeserializerTest#testCustomExceptionDefaultCtorRoundTrip().
    void testCustomExceptionDefaultCtorRoundTripVpack() throws Exception {
        assertNotNull(roundTrip(new CustomThrowable4071(), CustomThrowable4071.class));
        assertNotNull(roundTrip(new CustomRuntimeException4071(),
                CustomRuntimeException4071.class));
        assertNotNull(roundTrip(new CustomCheckedException4071(),
                CustomCheckedException4071.class));
    }

    // Provenance: ThrowableDeserializerTest#testCustomExceptionDeserAsThrowable().
    void testCustomExceptionDeserAsThrowableVpack() throws Exception {
        assertNotNull(roundTrip(new CustomRuntimeException4071(), Throwable.class));
        assertNotNull(roundTrip(new CustomCheckedException4071(), Throwable.class));
        assertNotNull(roundTrip(new CustomThrowable4071(), Throwable.class));
    }

    // Provenance: ThrowableDeserializerTest#testCustomPropException().
    void testCustomPropExceptionVpack() throws Exception {
        CustomPropException result = MAPPER.readValue(CUSTOM_PROPERTY,
                CustomPropException.class);
        assertNotNull(result);
        assertEquals("custom error", result.getMessage());
        assertEquals(42, result.getCode());
    }

    // Provenance: ThrowableDeserializerTest#testDefaultCtorException().
    void testDefaultCtorExceptionVpack() throws Exception {
        assertNotNull(MAPPER.readValue(VPackWireFixtureTest.hex("0a"),
                DefaultCtorException.class));
    }
private static <T extends Throwable> T roundTrip(T value, Class<T> type) throws Exception {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(value), type);
    }
private static Object createLongObject() {
        List<Bean> leaf = new ArrayList<>();
        for (int i = 0; i < 256; ++i) {
            leaf.add(new Bean());
        }
        List<Object> root = new ArrayList<>();
        for (int i = 0; i < 256; ++i) {
            root.add(leaf);
        }
        return root;
    }
static class ErrorObject {
        public String throwable;
        public String message;
        public StackTraceElement[] stackTrace;

        ErrorObject() { }

        ErrorObject(Throwable throwable) {
            this.throwable = throwable.getClass().getName();
            message = throwable.getMessage();
            stackTrace = throwable.getStackTrace();
        }
    }
static class Bean { }
static class SerializerWithErrors extends ValueSerializer<Bean> {
        @Override
        public void serialize(Bean value, JsonGenerator generator, SerializationContext ctxt) {
            throw new IllegalArgumentException("test string");
        }
    }
static class BrokenOutputStream extends OutputStream {
        private final String message;

        BrokenOutputStream(String message) {
            this.message = message;
        }

        @Override
        public void write(int value) throws IOException {
            throw new IOException(message);
        }

        @Override
        public void write(byte[] value, int offset, int length) throws IOException {
            throw new IOException(message);
        }
    }
static class DefaultCtorException extends Exception {
        DefaultCtorException() { super(); }
    }
static class AnySetterException extends Exception {
        @JsonAnySetter
        public Map<String, Object> extra = new LinkedHashMap<>();

        public AnySetterException() { super(); }
        public AnySetterException(String message) { super(message); }
    }
static class CustomPropException extends Exception {
        private int code;

        @JsonCreator
        CustomPropException(@JsonProperty("message") String message,
                @JsonProperty("code") int code) {
            super(message);
            this.code = code;
        }

        public int getCode() { return code; }
    }
static class CustomThrowable4071 extends Throwable { }
static class CustomRuntimeException4071 extends RuntimeException { }
static class CustomCheckedException4071 extends Exception { }

    void __invoke_testAnySetterExceptionVpack() throws Exception {
        try {
            testAnySetterExceptionVpack();
        } finally {
        }
    }


    void __invoke_testAnySetterWithoutMessageVpack() throws Exception {
        try {
            testAnySetterWithoutMessageVpack();
        } finally {
        }
    }


    void __invoke_testCauseDeserializationVpack() throws Exception {
        try {
            testCauseDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testCauseDeserializationRoundTripVpack() throws Exception {
        try {
            testCauseDeserializationRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testCustomExceptionDefaultCtorRoundTripVpack() throws Exception {
        try {
            testCustomExceptionDefaultCtorRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testCustomExceptionDeserAsThrowableVpack() throws Exception {
        try {
            testCustomExceptionDeserAsThrowableVpack();
        } finally {
        }
    }


    void __invoke_testCustomPropExceptionVpack() throws Exception {
        try {
            testCustomPropExceptionVpack();
        } finally {
        }
    }


    void __invoke_testDefaultCtorExceptionVpack() throws Exception {
        try {
            testDefaultCtorExceptionVpack();
        } finally {
        }
    }

}
