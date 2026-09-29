package tools.jackson.databind;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.type.SimpleType;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0131F0 {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] ARRAY_WITH_POINT = VPackWireFixtureTest.hex(
            "02 0b 14 09 41 78 31 41 79 32 02");

    void readValueRejectsNullSourcesAcrossAssignedOverloads() {
        ObjectMapper mapper = new VPackMapper();
        assertNullReadValue(() -> mapper.readValue((InputStream) null, Map.class));
        assertNullReadValue(() -> mapper.readValue((InputStream) null,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((InputStream) null,
                new TypeReference<Map>() { }));
        assertNullReadValue(() -> mapper.readValue((DataInput) null, Map.class));
        assertNullReadValue(() -> mapper.readValue((DataInput) null,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((DataInput) null,
                new TypeReference<Map>() { }));
        assertNullReadValue(() -> mapper.readValue((Path) null, Map.class));
        assertNullReadValue(() -> mapper.readValue((Path) null,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((Path) null,
                new TypeReference<Map>() { }));
        assertNullReadValue(() -> mapper.readValue((File) null, Map.class));
        assertNullReadValue(() -> mapper.readValue((File) null,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((File) null,
                new TypeReference<Map>() { }));
        assertNullReadValue(() -> mapper.readValue((Reader) null, Map.class));
        assertNullReadValue(() -> mapper.readValue((Reader) null,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((Reader) null,
                new TypeReference<Map>() { }));
        assertNullReadValue(() -> mapper.readValue((String) null, Map.class));
        assertNullReadValue(() -> mapper.readValue((String) null,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((String) null,
                new TypeReference<Map>() { }));
        assertNullReadValue(() -> mapper.readValue((JsonParser) null, Map.class));
        assertNullReadValue(() -> mapper.readValue((JsonParser) null,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((JsonParser) null,
                new TypeReference<Map>() { }));
        assertNullReadValue(() -> mapper.readValue((byte[]) null, Map.class));
        assertNullReadValue(() -> mapper.readValue((byte[]) null,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((byte[]) null,
                new TypeReference<Map>() { }));
        assertNullReadValue(() -> mapper.readValue((byte[]) null, -1, -1,
                Map.class));
        assertNullReadValue(() -> mapper.readValue((byte[]) null, -1, -1,
                SimpleType.constructUnsafe(Map.class)));
        assertNullReadValue(() -> mapper.readValue((byte[]) null, -1, -1,
                new TypeReference<Map>() { }));
    }

    void writeValueOutputStreamWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new VPackMapper().writeValue(output, "value");

        assertArrayEquals(VALUE_STRING, output.toByteArray());
        output.write(0x7f);
    }

    void writeValueFileWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0131-", ".vpack");
        try {
            new VPackMapper().writeValue(path.toFile(), "value");
            assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void writeValuePathWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0131-", ".vpack");
        try {
            new VPackMapper().writeValue(path, "value");
            assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void writeValueWriterIsExplicitlyUnsupportedForBinaryVpack() {
        assertThrows(UnsupportedOperationException.class,
                () -> new VPackMapper().writeValue((Writer) new StringWriter(), "value"));
    }

    void writeValueDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutput output = new DataOutputStream(bytes);
        new VPackMapper().writeValue(output, "value");

        assertArrayEquals(VALUE_STRING, bytes.toByteArray());
        output.writeByte(0x7f);
    }

    void writeValueJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectMapper mapper = new VPackMapper();
        JsonGenerator generator = mapper.createGenerator(output);
        mapper.writeValue(generator, "value");

        assertArrayEquals(VALUE_STRING, output.toByteArray());
        output.write(0x7f);
    }

    void writeValueRejectsNullTargetsAcrossAssignedOverloads() {
        ObjectMapper mapper = new VPackMapper();
        assertThrows(IllegalArgumentException.class,
                () -> mapper.writeValue((java.io.OutputStream) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.writeValue((DataOutput) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.writeValue((Path) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.writeValue((File) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.writeValue((Writer) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.writeValue((JsonGenerator) null, null));
    }

    void readValueWriteValuePathWorksWithNonDefaultFileSystem() throws Exception {
        Path zipFile = Files.createTempFile("vpack-t32-0131-", ".zip");
        try {
            try (java.io.OutputStream out = Files.newOutputStream(zipFile);
                    ZipOutputStream zipped = new ZipOutputStream(out)) {
            }

            try (FileSystem zipFs = FileSystems.newFileSystem(zipFile, (ClassLoader) null)) {
                Path path = zipFs.getPath("/test.vpack");
                ObjectMapper mapper = new VPackMapper();
                mapper.writeValue(path, "value");
                assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
                assertEquals("value", mapper.readValue(path, String.class));
            }
        } finally {
            Files.deleteIfExists(zipFile);
        }
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

    void __invoke_readValueRejectsNullSourcesAcrossAssignedOverloads() throws Exception {
        try {
            readValueRejectsNullSourcesAcrossAssignedOverloads();
        } finally {
        }
    }


    void __invoke_writeValueOutputStreamWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValueOutputStreamWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }


    void __invoke_writeValueFileWritesLiteralVpack() throws Exception {
        try {
            writeValueFileWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_writeValuePathWritesLiteralVpack() throws Exception {
        try {
            writeValuePathWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_writeValueWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
        try {
            writeValueWriterIsExplicitlyUnsupportedForBinaryVpack();
        } finally {
        }
    }


    void __invoke_writeValueDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValueDataOutputWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }


    void __invoke_writeValueJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValueJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }


    void __invoke_writeValueRejectsNullTargetsAcrossAssignedOverloads() throws Exception {
        try {
            writeValueRejectsNullTargetsAcrossAssignedOverloads();
        } finally {
        }
    }


    void __invoke_readValueWriteValuePathWorksWithNonDefaultFileSystem() throws Exception {
        try {
            readValueWriteValuePathWorksWithNonDefaultFileSystem();
        } finally {
        }
    }

}
