package tools.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0131F1 {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] ARRAY_WITH_POINT = VPackWireFixtureTest.hex(
            "02 0b 14 09 41 78 31 41 79 32 02");

    void objectReaderCreateParserVariantsUseLiteralVpackSources() throws Exception {
        ObjectReader reader = new VPackMapper().reader();
        Path path = Files.createTempFile("vpack-t32-0131-", ".vpack");
        try {
            Files.write(path, EMPTY_OBJECT);
            try (JsonParser parser = reader.createParser(path.toFile())) {
                assertNotNull(parser);
            }
            try (JsonParser parser = reader.createParser(path)) {
                assertNotNull(parser);
            }
        } finally {
            Files.deleteIfExists(path);
        }

        try (JsonParser parser = reader.createParser(
                new ByteArrayInputStream(EMPTY_ARRAY))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }
        try (JsonParser parser = reader.createParser(new byte[0])) {
            assertNotNull(parser);
        }
        try (JsonParser parser = reader.createParser(new byte[0], 0, 0)) {
            assertNotNull(parser);
        }
    }

    void objectReaderCharacterParserVariantsAreExplicitlyUnsupported() {
        ObjectReader reader = new VPackMapper().reader();
        assertThrows(UnsupportedOperationException.class,
                () -> reader.createParser((Reader) new StringReader("[]")));
        assertThrows(UnsupportedOperationException.class,
                () -> reader.createParser("[]"));
        assertThrows(UnsupportedOperationException.class,
                () -> reader.createParser("[]".toCharArray()));
        assertThrows(UnsupportedOperationException.class,
                () -> reader.createParser("[]".toCharArray(), 0, 2));
    }

    void objectReaderWriteTreeRetainsUnsupportedDatabindContract() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ObjectReader reader = mapper.readerFor(String.class);
        ObjectNode tree = mapper.createObjectNode();
        try (JsonGenerator generator = mapper.createGenerator(new ByteArrayOutputStream())) {
            assertThrows(UnsupportedOperationException.class,
                    () -> reader.writeTree(generator, tree));
        }
    }

    void objectReaderReadsCustomArrayNodeFromLiteralVpackTree() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ArrayNode defaultNode = (ArrayNode) mapper.readTree(ARRAY_WITH_POINT);
        DelegatingArrayNode customNode = new DelegatingArrayNode(defaultNode);

        Point[] points = mapper.readerFor(Point[].class).readValue(customNode);

        assertEquals(1, points.length);
        assertEquals(1, points[0].x());
        assertEquals(2, points[0].y());
    }
private static void assertNullReadValue(ThrowingRunnable action) {
        assertThrows(IllegalArgumentException.class, action::run);
    }
@FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
private static final class DelegatingArrayNode extends ArrayNode {
        private final ArrayNode delegate;

        DelegatingArrayNode(ArrayNode delegate) {
            super(JsonNodeFactory.instance);
            this.delegate = delegate;
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public JsonNode get(int index) {
            return delegate.get(index);
        }

        @Override
        public JsonNode path(int index) {
            return delegate.path(index);
        }

        @Override
        public Collection<JsonNode> values() {
            return delegate.values();
        }

        @Override
        public Stream<JsonNode> valueStream() {
            return delegate.valueStream();
        }

        @Override
        public DelegatingArrayNode deepCopy() {
            return new DelegatingArrayNode(delegate);
        }
    }
record Point(int x, int y) { }

    void __invoke_objectReaderCreateParserVariantsUseLiteralVpackSources() throws Exception {
        try {
            objectReaderCreateParserVariantsUseLiteralVpackSources();
        } finally {
        }
    }


    void __invoke_objectReaderCharacterParserVariantsAreExplicitlyUnsupported() throws Exception {
        try {
            objectReaderCharacterParserVariantsAreExplicitlyUnsupported();
        } finally {
        }
    }


    void __invoke_objectReaderWriteTreeRetainsUnsupportedDatabindContract() throws Exception {
        try {
            objectReaderWriteTreeRetainsUnsupportedDatabindContract();
        } finally {
        }
    }


    void __invoke_objectReaderReadsCustomArrayNodeFromLiteralVpackTree() throws Exception {
        try {
            objectReaderReadsCustomArrayNodeFromLiteralVpackTree();
        } finally {
        }
    }

}
