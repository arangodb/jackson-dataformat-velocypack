package tools.jackson.databind.exc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.IgnoredPropertyException;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0291F0 {
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

    // Provenance: BasicExceptionTest#testBadDefinition().
    void testBadDefinitionVpack() throws Exception {
        JavaType type = MAPPER.constructType(String.class);
        try (JsonParser parser = MAPPER.createParser(EMPTY_OBJECT)) {
            InvalidDefinitionException exception = new ExposedInvalidDefinitionException(
                    parser, "Testing", type);
            assertEquals("Testing", exception.getOriginalMessage());
            assertEquals(String.class, exception.getType().getRawClass());
            assertNull(exception.getBeanDescription());
            assertNull(exception.getProperty());
            assertSame(parser, exception.processor());

            BeanDescription beanDescription = serializationDescription();
            exception = InvalidDefinitionException.from(parser, "Testing", beanDescription,
                    (BeanPropertyDefinition) null);
            assertEquals(beanDescription.getType(), exception.getType());
            assertNotNull(exception);

            exception = InvalidDefinitionException.from(parser, "Testing", type);
            assertEquals("Testing", exception.getOriginalMessage());
            assertEquals(String.class, exception.getType().getRawClass());
        }

        try (JsonGenerator generator = MAPPER.createGenerator(new ByteArrayOutputStream())) {
            InvalidDefinitionException exception = new ExposedInvalidDefinitionException(
                    generator, "Testing", type);
            assertEquals("Testing", exception.getOriginalMessage());
            assertEquals(String.class, exception.getType().getRawClass());

            exception = InvalidDefinitionException.from(generator, "Testing",
                    serializationDescription(), (BeanPropertyDefinition) null);
            assertEquals(MAPPER.constructType(T32_0291F0.class),
                    exception.getType());
            assertNotNull(exception);
        }
    }

    // Provenance: BasicExceptionTest#testIgnoredProperty().
    void testIgnoredPropertyVpack() throws Exception {
        try (JsonParser parser = MAPPER.createParser(EMPTY_OBJECT)) {
            IgnoredPropertyException exception = IgnoredPropertyException.from(parser, this,
                    "testProp", Collections.<Object>singletonList("x"));
            assertNotNull(exception);

            exception = IgnoredPropertyException.from(parser, getClass(), "testProp", null);
            assertNotNull(exception);
            assertNull(exception.getKnownPropertyIds());

            assertThrows(NullPointerException.class,
                    () -> IgnoredPropertyException.from(parser, null, "testProp",
                            Collections.<Object>singletonList("x")));
        }
    }

    // Provenance: BasicExceptionTest#testLocationAddition().
    void testLocationAdditionVpack() {
        StreamReadException exception = assertThrows(StreamReadException.class,
                () -> MAPPER.readValue(MALFORMED_NESTED, PathFoo.class));
        String message = exception.getMessage();
        assertEquals(2, message.split(" at \\[", -1).length);
        assertEquals(18L, exception.getLocation().getByteOffset());
    }

    // Provenance: BasicExceptionTest#testUnrecognizedProperty().
    void testUnrecognizedPropertyVpack() throws Exception {
        try (JsonParser parser = MAPPER.createParser(EMPTY_OBJECT)) {
            UnrecognizedPropertyException exception = UnrecognizedPropertyException.from(parser,
                    this, "testProp", Collections.<Object>singletonList("y"));
            assertNotNull(exception);
            assertEquals(getClass(), exception.getReferringClass());
            Collection<Object> ids = exception.getKnownPropertyIds();
            assertNotNull(ids);
            assertEquals(1, ids.size());
            assertTrue(ids.contains("y"));

            exception = UnrecognizedPropertyException.from(parser, getClass(), "testProp",
                    Collections.<Object>singletonList("y"));
            assertEquals(getClass(), exception.getReferringClass());
        }
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

    void __invoke_testBadDefinitionVpack() throws Exception {
        try {
            testBadDefinitionVpack();
        } finally {
        }
    }


    void __invoke_testIgnoredPropertyVpack() throws Exception {
        try {
            testIgnoredPropertyVpack();
        } finally {
        }
    }


    void __invoke_testLocationAdditionVpack() throws Exception {
        try {
            testLocationAdditionVpack();
        } finally {
        }
    }


    void __invoke_testUnrecognizedPropertyVpack() throws Exception {
        try {
            testUnrecognizedPropertyVpack();
        } finally {
        }
    }

}
