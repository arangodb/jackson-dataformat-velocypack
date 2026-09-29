package tools.jackson.databind.exc;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0295F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SUPPRESSED_NULL_ENTRY = VPackWireFixtureTest.hex(
            "14 1f 47 6d 65 73 73 61 67 65 44 74 65 73 74 "
          + "4a 73 75 70 70 72 65 73 73 65 64 13 04 18 01 02");
private static final byte[] DUPLICATE_PROPERTIES = VPackWireFixtureTest.hex(
            "14 b0 01 4a 73 75 70 70 72 65 73 73 65 64 18 "
          + "45 63 61 75 73 65 18 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 01 "
          + "47 6d 65 73 73 61 67 65 43 66 6f 6f "
          + "50 6c 6f 63 61 6c 69 7a 65 64 4d 65 73 73 61 67 65 43 62 61 72 0e");
private static final byte[] NO_CREATOR_VALUE = VPackWireFixtureTest.hex(
            "14 0b 43 76 61 6c 43 66 6f 6f 01");

    // Provenance: ThrowableSerializationTest#testDatabindExceptionSerialization().
    void testDatabindExceptionSerializationVpack() throws IOException {
        MismatchedInputException exception = null;
        try {
            MAPPER.readValue(NO_CREATOR_VALUE, NoSerdeConstructor.class);
        } catch (MismatchedInputException failure) {
            exception = failure;
        }
        assertNotNull(exception);
        byte[] encoded = MAPPER.writeValueAsBytes(exception);
        String message = MAPPER.readTree(encoded).path("message").asString();
        assertTrue(message.toLowerCase().contains("cannot construct instance"), message);
    }

    // Provenance: ThrowableSerializationTest#testIgnorals().
    void testIgnoralsVpack() throws Exception {
        ExceptionWithIgnoral input = new ExceptionWithIgnoral("foobar");
        input.initCause(new IOException("surprise!"));
        Map<String, Object> result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertEquals("foobar", result.get("message"));
        assertNull(result.get("bogus1"));
        assertNotNull(result.get("bogus2"));

        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(ExceptionWithIgnoral.class,
                        o -> o.setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("bogus2")))
                .build();
        byte[] encoded = mapper.writeValueAsBytes(new ExceptionWithIgnoral("foobar"));
        Map<String, Object> result2 = mapper.readValue(encoded, Map.class);
        assertNull(result2.get("bogus1"));
        assertNull(result2.get("bogus2"));
        ExceptionWithIgnoral output = mapper.readValue(encoded, ExceptionWithIgnoral.class);
        assertNotNull(output);
        assertEquals("foobar", output.getMessage());
    }

    // Provenance: ThrowableSerializationTest#testJacksonExceptionSerialization().
    void testJacksonExceptionSerializationVpack() throws Exception {
        JacksonException exception = null;
        try {
            MAPPER.readValue(new byte[] { 0x14 }, Map.class);
        } catch (JacksonException failure) {
            exception = failure;
        }
        assertNotNull(exception);
        assertTrue(MAPPER.readTree(MAPPER.writeValueAsBytes(exception)).isObject());
    }

    // Provenance: ThrowableSerializationTest#testSerializeWithNamingStrategy().
    void testSerializeWithNamingStrategyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE)
                .build();
        Map<?, ?> map = mapper.readValue(mapper.writeValueAsBytes(new Exception("message!")), Map.class);
        assertEquals(new HashSet<>(Arrays.asList("Cause", "StackTrace", "Message", "Suppressed",
                "LocalizedMessage")), map.keySet());
    }

    // Provenance: ThrowableSerializationTest#testSimple().
    void testSimpleVpack() throws Exception {
        String expected = "test exception";
        Map<String, Object> result = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new Exception(expected)), Map.class);
        Object suppressed = result.get("suppressed");
        if (suppressed != null) {
            assertEquals(5, result.size());
        } else {
            assertEquals(4, result.size());
        }
        assertEquals(expected, result.get("message"));
        assertNull(result.get("cause"));
        assertEquals(expected, result.get("localizedMessage"));
        assertTrue(result.get("stackTrace") instanceof java.util.List<?>);
    }
private static <T extends Throwable> T roundTrip(T value, Class<T> type) throws Exception {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(value), type);
    }
@SuppressWarnings("serial")
    @JsonIgnoreProperties({ "bogus1" })
    static class ExceptionWithIgnoral extends RuntimeException {
        public int bogus1 = 3;
        public int bogus2 = 5;
        protected ExceptionWithIgnoral() { }
        ExceptionWithIgnoral(String message) { super(message); }
    }
static class NoSerdeConstructor {
        private String strVal;
        public String getVal() { return strVal; }
        public NoSerdeConstructor(String strVal) { this.strVal = strVal; }
    }
@SuppressWarnings("serial")
    static class MyException extends Exception {
        protected int value;
        protected java.util.HashMap<String, Object> stuff = new java.util.HashMap<>();

        @JsonCreator
        MyException(@JsonProperty("message") String message, @JsonProperty("value") int value) {
            super(message);
            this.value = value;
        }

        public int getValue() { return value; }
        public String getFoo() { return "bar"; }
        @com.fasterxml.jackson.annotation.JsonAnySetter
        public void setter(String key, Object value) { stuff.put(key, value); }
    }

    void __invoke_testDatabindExceptionSerializationVpack() throws Exception {
        try {
            testDatabindExceptionSerializationVpack();
        } finally {
        }
    }


    void __invoke_testIgnoralsVpack() throws Exception {
        try {
            testIgnoralsVpack();
        } finally {
        }
    }


    void __invoke_testJacksonExceptionSerializationVpack() throws Exception {
        try {
            testJacksonExceptionSerializationVpack();
        } finally {
        }
    }


    void __invoke_testSerializeWithNamingStrategyVpack() throws Exception {
        try {
            testSerializeWithNamingStrategyVpack();
        } finally {
        }
    }


    void __invoke_testSimpleVpack() throws Exception {
        try {
            testSimpleVpack();
        } finally {
        }
    }

}
