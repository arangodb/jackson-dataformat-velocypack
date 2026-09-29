package tools.jackson.databind.exc;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0291F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] UNKNOWN_BAR = VPackWireFixtureTest.hex(
            "14 08 43 62 61 72 33 01");
private static final byte[] INNER_VALUE = VPackWireFixtureTest.hex(
            "14 0f 45 69 6e 6e 65 72 14 06 41 78 31 01 01");
private static final byte[] MALFORMED_NESTED = VPackWireFixtureTest.hex(
            "14 16 43 62 61 72 14 0f 43 62 61 7a 14 08 43 71 75 78 15 01 01 01");
private static final byte[] INCOMPLETE_ARRAY_PREFIX = VPackWireFixtureTest.hex(
            "13 06 31");

    // Provenance: DeserExceptionTypeTest#testExceptionForNoCreators().
    void testExceptionForNoCreatorsVpack() {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(EMPTY_OBJECT, NoCreatorsBean.class));
        assertTrue(exception.getMessage().contains("no Creators"));
    }

    // Provenance: DeserExceptionTypeTest#testExceptionWithEOF().
    void testExceptionWithEOFVpack() throws Exception {
        try (JsonParser parser = MAPPER.createParser(VPackWireFixtureTest.hex("33"))) {
            assertEquals(Integer.valueOf(3), MAPPER.readValue(parser, Integer.class));

            MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                    () -> MAPPER.readValue(parser, Integer.class));
            assertTrue(exception.getMessage().contains("No content"));
            assertNull(exception.getCurrentToken());
            assertNull(parser.currentToken());
        }
    }

    // Provenance: DeserExceptionTypeTest#testExceptionWithEmpty().
    void testExceptionWithEmptyVpack() {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(new byte[0], Object.class));
        assertTrue(exception.getMessage().contains("No content"));
        assertNull(exception.getCurrentToken());
    }

    // Provenance: DeserExceptionTypeTest#testExceptionWithIncomplete().
    void testExceptionWithIncompleteVpack() throws Exception {
        try (JsonParser parser = MAPPER.createParser(
                new BrokenInputStream(INCOMPLETE_ARRAY_PREFIX, "TEST"))) {
            JacksonException exception = assertThrows(JacksonException.class,
                    () -> MAPPER.readValue(parser, Object.class));
            assertInstanceOf(StreamReadException.class, exception);
            assertInstanceOf(IOException.class, exception.getCause());
            assertTrue(exception.getMessage().contains("TEST"));
        }
    }

    // Provenance: DeserExceptionTypeTest#testHandlingOfUnrecognized().
    void testHandlingOfUnrecognizedVpack() {
        ObjectMapper strict = VPackMapper.builder()
                .enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        UnrecognizedPropertyException exception = assertThrows(UnrecognizedPropertyException.class,
                () -> strict.readValue(UNKNOWN_BAR, Bean.class));
        assertEquals("bar", exception.getPropertyName());
        assertEquals(Bean.class, exception.getReferringClass());
        assertTrue(exception.getKnownPropertyIds().contains("propX"));
        assertEquals(JsonToken.VALUE_NUMBER_INT, exception.getCurrentToken());
    }
private BeanDescription serializationDescription() {
        JavaType type = MAPPER.constructType(getClass());
        return MAPPER._serializationContext().introspectBeanDescription(type,
                MAPPER.serializationConfig().classIntrospectorInstance()
                        .introspectClassAnnotations(type));
    }
static class Bean {
        public String propX;
    }
static class NoCreatorsBean {
        public int x;

        protected NoCreatorsBean(boolean ignored, int alsoIgnored) { }
    }
static class Outer {
        public Inner inner = new Inner();
    }
static class Inner {
        public int x;

        @JsonCreator
        public static Inner create(@JsonProperty("x") int x) {
            throw new RuntimeException("test-exception");
        }
    }
static class PathFoo {
        private PathBar bar;

        public PathFoo() { }

        public PathBar getBar() { return bar; }
    }
static class PathBar {
        private PathBaz baz;

        public PathBar() { }

        public PathBaz getBaz() { return baz; }
    }
static class PathBaz {
        private String qux;

        public PathBaz() { }

        public String getQux() { return qux; }
    }
static class CreatorFoo {
        private CreatorBar bar;

        @JsonCreator
        CreatorFoo(@JsonProperty("bar") CreatorBar bar) { this.bar = bar; }

        public CreatorBar getBar() { return bar; }
    }
static class CreatorBar {
        private CreatorBaz baz;

        @JsonCreator
        CreatorBar(@JsonProperty("baz") CreatorBaz baz) { this.baz = baz; }

        public CreatorBaz getBaz() { return baz; }
    }
static class CreatorBaz {
        private String qux;

        @JsonCreator
        CreatorBaz(@JsonProperty("qux") String qux) { this.qux = qux; }

        public String getQux() { return qux; }
    }
private static final class ExposedInvalidDefinitionException
            extends InvalidDefinitionException {
        ExposedInvalidDefinitionException(JsonParser parser, String message, JavaType type) {
            super(parser, message, type);
        }

        ExposedInvalidDefinitionException(JsonGenerator generator, String message,
                JavaType type) {
            super(generator, message, type);
        }
    }
private static final class BrokenInputStream extends InputStream {
        private final byte[] bytes;
        private final String message;
        private int index;

        BrokenInputStream(byte[] bytes, String message) {
            this.bytes = bytes;
            this.message = message;
        }

        @Override
        public int read() throws IOException {
            if (index == bytes.length) {
                throw new IOException(message);
            }
            return bytes[index++] & 0xFF;
        }
    }

    void __invoke_testExceptionForNoCreatorsVpack() throws Exception {
        try {
            testExceptionForNoCreatorsVpack();
        } finally {
        }
    }


    void __invoke_testExceptionWithEOFVpack() throws Exception {
        try {
            testExceptionWithEOFVpack();
        } finally {
        }
    }


    void __invoke_testExceptionWithEmptyVpack() throws Exception {
        try {
            testExceptionWithEmptyVpack();
        } finally {
        }
    }


    void __invoke_testExceptionWithIncompleteVpack() throws Exception {
        try {
            testExceptionWithIncompleteVpack();
        } finally {
        }
    }


    void __invoke_testHandlingOfUnrecognizedVpack() throws Exception {
        try {
            testHandlingOfUnrecognizedVpack();
        } finally {
        }
    }

}
