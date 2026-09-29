package tools.jackson.databind.exc;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0295F0 {
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

    // Provenance: ThrowableDeserializerTest#testSuppressedGenericThrowableDeserialization().
    void testSuppressedGenericThrowableDeserializationVpack() throws Exception {
        IOException input = new IOException("the outer exception");
        input.addSuppressed(new Throwable("the suppressed exception"));

        IOException result = roundTrip(input, IOException.class);
        assertNotNull(result.getSuppressed());
        assertEquals(1, result.getSuppressed().length);
        assertEquals(input.getSuppressed()[0].getMessage(), result.getSuppressed()[0].getMessage());
        assertEquals(input.getSuppressed()[0].getStackTrace().length,
                result.getSuppressed()[0].getStackTrace().length);
        assertEquals(Arrays.asList(input.getSuppressed()[0].getStackTrace()),
                Arrays.asList(result.getSuppressed()[0].getStackTrace()));
    }

    // Provenance: ThrowableDeserializerTest#testSuppressedRoundTrip().
    void testSuppressedRoundTripVpack() throws Exception {
        IOException input = new IOException("the outer");
        input.addSuppressed(new RuntimeException("supp1"));
        input.addSuppressed(new IllegalArgumentException("supp2"));

        IOException result = roundTrip(input, IOException.class);
        assertNotNull(result);
        assertEquals(input.getMessage(), result.getMessage());
        assertEquals(2, result.getSuppressed().length);
    }

    // Provenance: ThrowableDeserializerTest#testSuppressedTypedExceptionDeserialization().
    void testSuppressedTypedExceptionDeserializationVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubTypeIsArray()
                        .allowIfSubType(Throwable.class)
                        .build(), DefaultTyping.NON_FINAL)
                .build();
        IOException input = new IOException("the outer exception");
        input.addSuppressed(new IllegalArgumentException("the suppressed exception"));

        IOException result = mapper.readValue(mapper.writeValueAsBytes(input), IOException.class);
        assertNotNull(result.getSuppressed());
        assertEquals(1, result.getSuppressed().length);
        assertEquals(IllegalArgumentException.class, result.getSuppressed()[0].getClass());
        assertEquals(input.getSuppressed()[0].getMessage(), result.getSuppressed()[0].getMessage());
        assertEquals(Arrays.asList(input.getSuppressed()[0].getStackTrace()),
                Arrays.asList(result.getSuppressed()[0].getStackTrace()));
    }

    // Provenance: ThrowableDeserializerTest#testSuppressedWithNullEntries().
    void testSuppressedWithNullEntriesVpack() throws Exception {
        IOException result = MAPPER.readValue(SUPPRESSED_NULL_ENTRY, IOException.class);
        assertNotNull(result);
        assertEquals(0, result.getSuppressed().length);
    }

    // Provenance: ThrowableDeserializerTest#testWithCreator().
    void testWithCreatorVpack() throws Exception {
        MyException input = new MyException("the message", 3);
        MyException result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), MyException.class);
        assertEquals(input.getMessage(), result.getMessage());
        assertEquals(3, result.value);
        assertEquals(3, result.stuff.size());
        assertEquals(result.getFoo(), result.stuff.get("foo"));
        assertEquals("the message", result.stuff.get("localizedMessage"));
        assertTrue(result.stuff.containsKey("suppressed"));
    }

    // Provenance: ThrowableDeserializerTest#testWithDups().
    void testWithDupsVpack() throws Exception {
        IOException result = MAPPER.readValue(DUPLICATE_PROPERTIES, IOException.class);
        assertNotNull(result);
        assertEquals("foo", result.getLocalizedMessage());
    }

    // Provenance: ThrowableDeserializerTest#testWithNullMessageNonNullInclusion().
    void testWithNullMessageNonNullInclusionVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        IOException result = mapper.readValue(
                mapper.writeValueAsBytes(new IOException((String) null)), IOException.class);
        assertNotNull(result);
        assertNull(result.getMessage());
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

    void __invoke_testSuppressedGenericThrowableDeserializationVpack() throws Exception {
        try {
            testSuppressedGenericThrowableDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testSuppressedRoundTripVpack() throws Exception {
        try {
            testSuppressedRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testSuppressedTypedExceptionDeserializationVpack() throws Exception {
        try {
            testSuppressedTypedExceptionDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testSuppressedWithNullEntriesVpack() throws Exception {
        try {
            testSuppressedWithNullEntriesVpack();
        } finally {
        }
    }


    void __invoke_testWithCreatorVpack() throws Exception {
        try {
            testWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testWithDupsVpack() throws Exception {
        try {
            testWithDupsVpack();
        } finally {
        }
    }


    void __invoke_testWithNullMessageNonNullInclusionVpack() throws Exception {
        try {
            testWithNullMessageNonNullInclusionVpack();
        } finally {
        }
    }

}
