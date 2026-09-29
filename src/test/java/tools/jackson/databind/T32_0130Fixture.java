package tools.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.jackson.core.JsonParser;
import tools.jackson.core.type.ResolvedType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.SimpleType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0130Fixture {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");

    void readTreePathReadsLiteralVpackString() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0130-", ".vpack");
        try {
            Files.write(path, VALUE_STRING);
            JsonNode node = new VPackMapper().readTree(path);
            assertEquals("value", node.stringValue());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void readTreeTextSourcesAreExplicitlyUnsupportedForBinaryVpack() {
        ObjectMapper mapper = new VPackMapper();
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.readTree((Reader) new StringReader("\"value\"")));
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.readTree("\"value\""));
    }

    void readTreeRejectsNullArgumentsAcrossAssignedOverloads() {
        ObjectMapper mapper = new VPackMapper();
        assertThrows(IllegalArgumentException.class,
                () -> mapper.readTree((InputStream) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.readTree((Path) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.readTree((File) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.readTree((Reader) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.readTree((String) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.readTree((byte[]) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.readTree((byte[]) null, -1, -1));
    }

    void readValueByteArrayReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertEquals("value", mapper.readValue(VALUE_STRING, String.class));
        assertEquals("value", mapper.readValue(VALUE_STRING,
                mapper.constructType(String.class)));
        assertEquals("value", mapper.readValue(VALUE_STRING,
                new TypeReference<String>() { }));
        assertEquals("value", mapper.readValue(VALUE_STRING, 0, VALUE_STRING.length,
                String.class));
        assertEquals("value", mapper.readValue(VALUE_STRING, 0, VALUE_STRING.length,
                SimpleType.constructUnsafe(String.class)));
        assertEquals("value", mapper.readValue(VALUE_STRING, 0, VALUE_STRING.length,
                new TypeReference<String>() { }));
    }

    void readValueDataInputReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        DataInput input = new DataInputStream(new ByteArrayInputStream(VALUE_STRING));
        assertEquals("value", mapper.readValue(input, String.class));

        input = new DataInputStream(new ByteArrayInputStream(VALUE_STRING));
        assertEquals("value", mapper.readValue(input, mapper.constructType(String.class)));

        input = new DataInputStream(new ByteArrayInputStream(VALUE_STRING));
        assertEquals("value", mapper.readValue(input, new TypeReference<String>() { }));
    }

    void readValueFileReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0130-", ".vpack");
        try {
            Files.write(path, VALUE_STRING);
            ObjectMapper mapper = new VPackMapper();
            assertEquals("value", mapper.readValue(path.toFile(), String.class));
            assertEquals("value", mapper.readValue(path.toFile(),
                    mapper.constructType(String.class)));
            assertEquals("value", mapper.readValue(path.toFile(),
                    new TypeReference<String>() { }));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void readValueInputStreamReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        InputStream input = new ByteArrayInputStream(VALUE_STRING);
        assertEquals("value", mapper.readValue(input, String.class));

        input = new ByteArrayInputStream(VALUE_STRING);
        assertEquals("value", mapper.readValue(input, mapper.constructType(String.class)));

        input = new ByteArrayInputStream(VALUE_STRING);
        assertEquals("value", mapper.readValue(input, new TypeReference<String>() { }));
    }

    void readValueJsonParserReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        try (JsonParser parser = mapper.createParser(VALUE_STRING)) {
            assertEquals("value", mapper.readValue(parser, String.class));
        }
        try (JsonParser parser = mapper.createParser(VALUE_STRING)) {
            assertEquals("value", mapper.readValue(parser,
                    mapper.constructType(String.class)));
        }
        try (JsonParser parser = mapper.createParser(VALUE_STRING)) {
            ResolvedType type = mapper.constructType(String.class);
            assertEquals("value", mapper.readValue(parser, type));
        }
        try (JsonParser parser = mapper.createParser(VALUE_STRING)) {
            assertEquals("value", mapper.readValue(parser,
                    new TypeReference<String>() { }));
        }
    }

    void readValuePathReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0130-", ".vpack");
        try {
            Files.write(path, VALUE_STRING);
            ObjectMapper mapper = new VPackMapper();
            assertEquals("value", mapper.readValue(path, String.class));
            assertEquals("value", mapper.readValue(path,
                    mapper.constructType(String.class)));
            assertEquals("value", mapper.readValue(path,
                    new TypeReference<String>() { }));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void readValueTextSourcesAreExplicitlyUnsupportedForBinaryVpack() {
        ObjectMapper mapper = new VPackMapper();
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.readValue(new StringReader("\"value\""), String.class));
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.readValue(new StringReader("\"value\""),
                        mapper.constructType(String.class)));
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.readValue(new StringReader("\"value\""),
                        new TypeReference<String>() { }));
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.readValue("\"value\"", String.class));
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.readValue("\"value\"", mapper.constructType(String.class)));
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.readValue("\"value\"", new TypeReference<String>() { }));
    }

    void __invoke_readTreePathReadsLiteralVpackString() throws Exception {
        try {
            readTreePathReadsLiteralVpackString();
        } finally {
        }
    }


    void __invoke_readTreeTextSourcesAreExplicitlyUnsupportedForBinaryVpack() throws Exception {
        try {
            readTreeTextSourcesAreExplicitlyUnsupportedForBinaryVpack();
        } finally {
        }
    }


    void __invoke_readTreeRejectsNullArgumentsAcrossAssignedOverloads() throws Exception {
        try {
            readTreeRejectsNullArgumentsAcrossAssignedOverloads();
        } finally {
        }
    }


    void __invoke_readValueByteArrayReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        try {
            readValueByteArrayReadsLiteralVpackStringAcrossTypeOverloads();
        } finally {
        }
    }


    void __invoke_readValueDataInputReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        try {
            readValueDataInputReadsLiteralVpackStringAcrossTypeOverloads();
        } finally {
        }
    }


    void __invoke_readValueFileReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        try {
            readValueFileReadsLiteralVpackStringAcrossTypeOverloads();
        } finally {
        }
    }


    void __invoke_readValueInputStreamReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        try {
            readValueInputStreamReadsLiteralVpackStringAcrossTypeOverloads();
        } finally {
        }
    }


    void __invoke_readValueJsonParserReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        try {
            readValueJsonParserReadsLiteralVpackStringAcrossTypeOverloads();
        } finally {
        }
    }


    void __invoke_readValuePathReadsLiteralVpackStringAcrossTypeOverloads() throws Exception {
        try {
            readValuePathReadsLiteralVpackStringAcrossTypeOverloads();
        } finally {
        }
    }


    void __invoke_readValueTextSourcesAreExplicitlyUnsupportedForBinaryVpack() throws Exception {
        try {
            readValueTextSourcesAreExplicitlyUnsupportedForBinaryVpack();
        } finally {
        }
    }

}
