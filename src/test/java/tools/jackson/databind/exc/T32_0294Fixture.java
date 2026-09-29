package tools.jackson.databind.exc;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0294Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] NULL_STACK_TRACE = VPackWireFixtureTest.hex(
            "14 1c 47 6d 65 73 73 61 67 65 44 74 65 73 74 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 18 02");
private static final byte[] NULL_STACK_TRACE_BEFORE_MESSAGE = VPackWireFixtureTest.hex(
            "14 1c 4a 73 74 61 63 6b 54 72 61 63 65 18 "
          + "47 6d 65 73 73 61 67 65 44 74 65 73 74 02");
private static final byte[] NULL_SUPPRESSED = VPackWireFixtureTest.hex(
            "14 1c 47 6d 65 73 73 61 67 65 44 74 65 73 74 "
          + "4a 73 75 70 70 72 65 73 73 65 64 18 02");
private static final byte[] CAUSE_BEFORE_MESSAGE = VPackWireFixtureTest.hex(
            "14 30 45 63 61 75 73 65 14 15 47 6d 65 73 73 61 67 65 "
          + "49 63 61 75 73 65 20 6d 73 67 01 "
          + "47 6d 65 73 73 61 67 65 49 6f 75 74 65 72 20 6d 73 67 02");
private static final byte[] SIMPLE_IO_EXCEPTION = VPackWireFixtureTest.hex(
            "14 16 47 6d 65 73 73 61 67 65 4a 74 65 73 74 20 65 72 72 6f 72 01");
private static final byte[] STACK_TRACE = VPackWireFixtureTest.hex(
            "14 72 47 6d 65 73 73 61 67 65 44 74 65 73 74 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 13 57 "
          + "14 54 49 63 6c 61 73 73 4e 61 6d 65 50 63 6f 6d 2e 65 78 61 6d 70 6c 65 2e 54 65 73 74 "
          + "4a 6d 65 74 68 6f 64 4e 61 6d 65 4a 74 65 73 74 4d 65 74 68 6f 64 "
          + "48 66 69 6c 65 4e 61 6d 65 49 54 65 73 74 2e 6a 61 76 61 "
          + "4a 6c 69 6e 65 4e 75 6d 62 65 72 28 2a 04 01 02");
private static final byte[] SINGLE_VALUE_EXCEPTION = VPackWireFixtureTest.hex(
            "13 78 14 75 47 6d 65 73 73 61 67 65 47 74 65 73 74 69 6e 67 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 13 57 "
          + "14 54 49 63 6c 61 73 73 4e 61 6d 65 50 63 6f 6d 2e 65 78 61 6d 70 6c 65 2e 54 65 73 74 "
          + "4a 6d 65 74 68 6f 64 4e 61 6d 65 4a 74 65 73 74 4d 65 74 68 6f 64 "
          + "48 66 69 6c 65 4e 61 6d 65 49 54 65 73 74 2e 6a 61 76 61 "
          + "4a 6c 69 6e 65 4e 75 6d 62 65 72 28 2a 04 01 02 01");
private static final byte[] SUBCLASSED_WITH_CAUSE = VPackWireFixtureTest.hex(
            "14 3c 47 6d 65 73 73 61 67 65 4c 54 65 73 74 20 4d 65 73 73 61 67 65 "
          + "45 63 61 75 73 65 14 1e 47 6d 65 73 73 61 67 65 52 74 65 73 74 20 72 75 6e 74 69 6d 65 20 63 61 75 73 65 01 02");

    // Provenance: ThrowableDeserializerTest#testNullStackTrace().
    void testNullStackTraceVpack() throws Exception {
        IOException result = MAPPER.readValue(NULL_STACK_TRACE, IOException.class);
        assertNotNull(result);
        assertEquals("test", result.getMessage());
        assertNotNull(result.getStackTrace());
    }

    // Provenance: ThrowableDeserializerTest#testNullStackTraceBeforeMessage().
    void testNullStackTraceBeforeMessageVpack() throws Exception {
        IOException result = MAPPER.readValue(NULL_STACK_TRACE_BEFORE_MESSAGE,
                IOException.class);
        assertNotNull(result);
        assertEquals("test", result.getMessage());
        assertNotNull(result.getStackTrace());
    }

    // Provenance: ThrowableDeserializerTest#testNullSuppressedArray().
    void testNullSuppressedArrayVpack() throws Exception {
        IOException result = MAPPER.readValue(NULL_SUPPRESSED, IOException.class);
        assertNotNull(result);
        assertEquals(0, result.getSuppressed().length);
    }

    // Provenance: ThrowableDeserializerTest#testPropertiesBeforeMessage().
    void testPropertiesBeforeMessageVpack() throws Exception {
        IOException result = MAPPER.readValue(CAUSE_BEFORE_MESSAGE, IOException.class);
        assertNotNull(result);
        assertEquals("outer msg", result.getMessage());
        assertNotNull(result.getCause());
        assertEquals("cause msg", result.getCause().getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testRoundtripWithoutNamingStrategy().
    void testRoundtripWithoutNamingStrategyVpack() throws Exception {
        assertRoundtrip(MAPPER);
    }

    // Provenance: ThrowableDeserializerTest#testRoundtripWithNamingStrategy().
    void testRoundtripWithNamingStrategyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE)
                .build();
        assertRoundtrip(mapper);
    }

    // Provenance: ThrowableDeserializerTest#testSimpleIOException().
    void testSimpleIOExceptionVpack() throws Exception {
        IOException result = MAPPER.readValue(SIMPLE_IO_EXCEPTION, IOException.class);
        assertNotNull(result);
        assertEquals("test error", result.getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testSingleValueArrayDeserialization().
    void testSingleValueArrayDeserializationVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        IOException result = mapper.readValue(SINGLE_VALUE_EXCEPTION, IOException.class);
        assertEquals("testing", result.getMessage());
        assertEquals(1, result.getStackTrace().length);
        assertEquals("com.example.Test", result.getStackTrace()[0].getClassName());
        assertEquals("testMethod", result.getStackTrace()[0].getMethodName());
        assertEquals("Test.java", result.getStackTrace()[0].getFileName());
        assertEquals(42, result.getStackTrace()[0].getLineNumber());
    }

    // Provenance: ThrowableDeserializerTest#testSingleValueArrayDeserializationException().
    void testSingleValueArrayDeserializationExceptionVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(SINGLE_VALUE_EXCEPTION, IOException.class));
    }

    // Provenance: ThrowableDeserializerTest#testStackTraceDeserialization().
    void testStackTraceDeserializationVpack() throws Exception {
        IOException result = MAPPER.readValue(STACK_TRACE, IOException.class);
        assertNotNull(result);
        assertEquals("test", result.getMessage());
        assertEquals(1, result.getStackTrace().length);
        assertEquals("com.example.Test", result.getStackTrace()[0].getClassName());
        assertEquals("testMethod", result.getStackTrace()[0].getMethodName());
        assertEquals("Test.java", result.getStackTrace()[0].getFileName());
        assertEquals(42, result.getStackTrace()[0].getLineNumber());
    }

    // Provenance: ThrowableDeserializerTest#testSubclassedExceptionWithCauseCreator().
    void testSubclassedExceptionWithCauseCreatorVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        SubclassedExceptionWithCause result = mapper.readValue(SUBCLASSED_WITH_CAUSE,
                SubclassedExceptionWithCause.class);
        assertEquals("Test Message", result.getMessage());
        assertNotNull(result.getCause());
        assertEquals("test runtime cause", result.getCause().getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testSuppressedDeserialization().
    void testSuppressedDeserializationVpack() throws Exception {
        IOException result = MAPPER.readValue(SUPPRESSED, IOException.class);
        assertNotNull(result);
        assertEquals("main", result.getMessage());
        assertEquals(2, result.getSuppressed().length);
        assertEquals("suppressed1", result.getSuppressed()[0].getMessage());
        assertEquals("suppressed2", result.getSuppressed()[1].getMessage());
    }
private static final byte[] SUPPRESSED = VPackWireFixtureTest.hex(
            "14 4c 47 6d 65 73 73 61 67 65 44 6d 61 69 6e "
          + "4a 73 75 70 70 72 65 73 73 65 64 13 31 "
          + "14 17 47 6d 65 73 73 61 67 65 4b 73 75 70 70 72 65 73 73 65 64 31 01 "
          + "14 17 47 6d 65 73 73 61 67 65 4b 73 75 70 70 72 65 73 73 65 64 32 01 02 02");
private static void assertRoundtrip(ObjectMapper mapper) throws Exception {
        Exception root = new Exception("Root cause");
        Exception leaf = new Exception("Leaf message", root);
        Exception result = mapper.readValue(mapper.writeValueAsBytes(leaf), Exception.class);
        assertEquals(leaf.getMessage(), result.getMessage());
        assertNotNull(result.getCause());
        assertEquals(root.getMessage(), result.getCause().getMessage());
    }
static class SubclassedExceptionWithCause extends Exception {
        @JsonCreator
        public SubclassedExceptionWithCause(
                @JsonProperty("message") String message,
                @JsonProperty("cause") Throwable cause) {
            super(message, cause);
        }
    }

    void __invoke_testNullStackTraceVpack() throws Exception {
        try {
            testNullStackTraceVpack();
        } finally {
        }
    }


    void __invoke_testNullStackTraceBeforeMessageVpack() throws Exception {
        try {
            testNullStackTraceBeforeMessageVpack();
        } finally {
        }
    }


    void __invoke_testNullSuppressedArrayVpack() throws Exception {
        try {
            testNullSuppressedArrayVpack();
        } finally {
        }
    }


    void __invoke_testPropertiesBeforeMessageVpack() throws Exception {
        try {
            testPropertiesBeforeMessageVpack();
        } finally {
        }
    }


    void __invoke_testRoundtripWithoutNamingStrategyVpack() throws Exception {
        try {
            testRoundtripWithoutNamingStrategyVpack();
        } finally {
        }
    }


    void __invoke_testRoundtripWithNamingStrategyVpack() throws Exception {
        try {
            testRoundtripWithNamingStrategyVpack();
        } finally {
        }
    }


    void __invoke_testSimpleIOExceptionVpack() throws Exception {
        try {
            testSimpleIOExceptionVpack();
        } finally {
        }
    }


    void __invoke_testSingleValueArrayDeserializationVpack() throws Exception {
        try {
            testSingleValueArrayDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testSingleValueArrayDeserializationExceptionVpack() throws Exception {
        try {
            testSingleValueArrayDeserializationExceptionVpack();
        } finally {
        }
    }


    void __invoke_testStackTraceDeserializationVpack() throws Exception {
        try {
            testStackTraceDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testSubclassedExceptionWithCauseCreatorVpack() throws Exception {
        try {
            testSubclassedExceptionWithCauseCreatorVpack();
        } finally {
        }
    }


    void __invoke_testSuppressedDeserializationVpack() throws Exception {
        try {
            testSuppressedDeserializationVpack();
        } finally {
        }
    }

}
