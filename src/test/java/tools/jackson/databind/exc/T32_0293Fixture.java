package tools.jackson.databind.exc;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0293Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] MESSAGE_IGNORED = VPackWireFixtureTest.hex(
            "14 13 47 6d 65 73 73 61 67 65 47 69 67 6e 6f 72 65 64 01");
private static final byte[] DUAL_CTOR_MESSAGE = VPackWireFixtureTest.hex(
            "14 1a 47 6d 65 73 73 61 67 65 4e 64 75 61 6c 20 63 74 6f 72 20 74 65 73 74 01");
private static final byte[] EMPTY_SUPPRESSED = VPackWireFixtureTest.hex(
            "14 1c 47 6d 65 73 73 61 67 65 44 74 65 73 74 4a 73 75 70 70 72 65 73 73 65 64 01 02");
private static final byte[] SUPPRESSED_ONLY = VPackWireFixtureTest.hex(
            "14 0f 4a 73 75 70 70 72 65 73 73 65 64 01 01");
private static final byte[] STACK_LINE_NUMBER = VPackWireFixtureTest.hex(
            "14 2f 47 6d 65 73 73 61 67 65 44 54 65 73 74 "
          + "4a 73 74 61 63 6b 54 72 61 63 65 "
          + "13 14 14 11 4a 6c 69 6e 65 4e 75 6d 62 65 72 42 35 30 01 01 02");
private static final byte[] LOCALIZED_MESSAGE = VPackWireFixtureTest.hex(
            "14 36 47 6d 65 73 73 61 67 65 47 74 68 65 20 6d 73 67 "
          + "50 6c 6f 63 61 6c 69 7a 65 64 4d 65 73 73 61 67 65 "
          + "51 6c 6f 63 61 6c 69 7a 65 64 20 69 67 6e 6f 72 65 64 02");
private static final byte[] NULL_MESSAGES = VPackWireFixtureTest.hex(
            "14 1e 47 6d 65 73 73 61 67 65 18 "
          + "50 6c 6f 63 61 6c 69 7a 65 64 4d 65 73 73 61 67 65 18 02");
private static final byte[] NULL_CAUSE = VPackWireFixtureTest.hex(
            "14 17 47 6d 65 73 73 61 67 65 44 74 65 73 74 45 63 61 75 73 65 18 02");
private static final byte[] NULL_MESSAGE = VPackWireFixtureTest.hex(
            "14 0c 47 6d 65 73 73 61 67 65 18 01");

    // Provenance: ThrowableDeserializerTest#testDefaultCtorExceptionWithMessage().
    void testDefaultCtorExceptionWithMessageVpack() throws Exception {
        DefaultCtorException result = MAPPER.readValue(MESSAGE_IGNORED,
                DefaultCtorException.class);
        assertNotNull(result);
        assertNull(result.getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testDualCtorWithMessage().
    void testDualCtorWithMessageVpack() throws Exception {
        DualCtorException result = MAPPER.readValue(DUAL_CTOR_MESSAGE,
                DualCtorException.class);
        assertNotNull(result);
        assertEquals("dual ctor test", result.getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testDualCtorWithoutMessage().
    void testDualCtorWithoutMessageVpack() throws Exception {
        DualCtorException result = MAPPER.readValue(VPackWireFixtureTest.hex("0a"),
                DualCtorException.class);
        assertNotNull(result);
        assertNull(result.getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testEmptySuppressedArray().
    void testEmptySuppressedArrayVpack() throws Exception {
        IOException result = MAPPER.readValue(EMPTY_SUPPRESSED, IOException.class);
        assertNotNull(result);
        assertEquals("test", result.getMessage());
        assertEquals(0, result.getSuppressed().length);
    }

    // Provenance: ThrowableDeserializerTest#testIOExceptionRoundTrip().
    void testIOExceptionRoundTripVpack() throws Exception {
        IOException input = new IOException("round-trip test");
        IOException result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), IOException.class);
        assertEquals(input.getMessage(), result.getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testJDK7SuppressionProperty().
    void testJDK7SuppressionPropertyVpack() throws Exception {
        Exception result = MAPPER.readValue(SUPPRESSED_ONLY, IOException.class);
        assertNotNull(result);
    }

    // Provenance: ThrowableDeserializerTest#testLineNumberAsString().
    void testLineNumberAsStringVpack() throws Exception {
        Exception result = MAPPER.readValue(STACK_LINE_NUMBER, IOException.class);
        assertNotNull(result);
    }

    // Provenance: ThrowableDeserializerTest#testLocalizedMessageIgnored().
    void testLocalizedMessageIgnoredVpack() throws Exception {
        IOException result = MAPPER.readValue(LOCALIZED_MESSAGE, IOException.class);
        assertNotNull(result);
        assertEquals("the msg", result.getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testNoArgsException().
    void testNoArgsExceptionVpack() throws Exception {
        MyNoArgException result = MAPPER.readValue(VPackWireFixtureTest.hex("0a"),
                MyNoArgException.class);
        assertNotNull(result);
    }

    // Provenance: ThrowableDeserializerTest#testNullAsMessageAndLocalizedMessage().
    void testNullAsMessageAndLocalizedMessageVpack() throws Exception {
        Exception result = MAPPER.readValue(NULL_MESSAGES, IOException.class);
        assertNotNull(result);
        assertNull(result.getMessage());
        assertNull(result.getLocalizedMessage());
    }

    // Provenance: ThrowableDeserializerTest#testNullCauseDeserialization().
    void testNullCauseDeserializationVpack() throws Exception {
        IOException result = MAPPER.readValue(NULL_CAUSE, IOException.class);
        assertNotNull(result);
        assertEquals("test", result.getMessage());
    }

    // Provenance: ThrowableDeserializerTest#testNullMessage().
    void testNullMessageVpack() throws Exception {
        IOException result = MAPPER.readValue(NULL_MESSAGE, IOException.class);
        assertNotNull(result);
        assertNull(result.getMessage());
    }
static class DefaultCtorException extends Exception {
        DefaultCtorException() { super(); }
    }
static class DualCtorException extends Exception {
        DualCtorException() { super(); }
        DualCtorException(String message) { super(message); }
    }
static class MyNoArgException extends Exception {
        @JsonCreator
        MyNoArgException() { }
    }

    void __invoke_testDefaultCtorExceptionWithMessageVpack() throws Exception {
        try {
            testDefaultCtorExceptionWithMessageVpack();
        } finally {
        }
    }


    void __invoke_testDualCtorWithMessageVpack() throws Exception {
        try {
            testDualCtorWithMessageVpack();
        } finally {
        }
    }


    void __invoke_testDualCtorWithoutMessageVpack() throws Exception {
        try {
            testDualCtorWithoutMessageVpack();
        } finally {
        }
    }


    void __invoke_testEmptySuppressedArrayVpack() throws Exception {
        try {
            testEmptySuppressedArrayVpack();
        } finally {
        }
    }


    void __invoke_testIOExceptionRoundTripVpack() throws Exception {
        try {
            testIOExceptionRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testJDK7SuppressionPropertyVpack() throws Exception {
        try {
            testJDK7SuppressionPropertyVpack();
        } finally {
        }
    }


    void __invoke_testLineNumberAsStringVpack() throws Exception {
        try {
            testLineNumberAsStringVpack();
        } finally {
        }
    }


    void __invoke_testLocalizedMessageIgnoredVpack() throws Exception {
        try {
            testLocalizedMessageIgnoredVpack();
        } finally {
        }
    }


    void __invoke_testNoArgsExceptionVpack() throws Exception {
        try {
            testNoArgsExceptionVpack();
        } finally {
        }
    }


    void __invoke_testNullAsMessageAndLocalizedMessageVpack() throws Exception {
        try {
            testNullAsMessageAndLocalizedMessageVpack();
        } finally {
        }
    }


    void __invoke_testNullCauseDeserializationVpack() throws Exception {
        try {
            testNullCauseDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testNullMessageVpack() throws Exception {
        try {
            testNullMessageVpack();
        } finally {
        }
    }

}
