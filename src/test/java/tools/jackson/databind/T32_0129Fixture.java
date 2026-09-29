package tools.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0129Fixture {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");

    void createParserInputStreamReadsLiteralVpackString() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        try (JsonParser parser = mapper.createParser(
                new ByteArrayInputStream(VALUE_STRING))) {
            assertEquals("value", parser.nextStringValue());
        }
    }

    void createParserFileReadsLiteralVpackString() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0129-", ".vpack");
        try {
            Files.write(path, VALUE_STRING);
            try (JsonParser parser = new VPackMapper().createParser(path.toFile())) {
                assertEquals("value", parser.nextStringValue());
            }
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void createParserPathReadsLiteralVpackString() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0129-", ".vpack");
        try {
            Files.write(path, VALUE_STRING);
            try (JsonParser parser = new VPackMapper().createParser(path)) {
                assertEquals("value", parser.nextStringValue());
            }
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void createParserByteArrayReadsLiteralVpackString() throws Exception {
        try (JsonParser parser = new VPackMapper().createParser(VALUE_STRING)) {
            assertEquals("value", parser.nextStringValue());
        }
    }

    void createParserDataInputReadsLiteralVpackString() throws Exception {
        DataInput input = new DataInputStream(new ByteArrayInputStream(VALUE_STRING));
        try (JsonParser parser = new VPackMapper().createParser(input)) {
            assertEquals("value", parser.nextStringValue());
        }
    }

    void createParserRejectsNullArgumentsAcrossObjectMapperOverloads() {
        ObjectMapper mapper = new VPackMapper();
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((InputStream) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((DataInput) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((Path) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((File) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((Reader) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((String) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((byte[]) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((byte[]) null, -1, -1));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((char[]) null));
        assertThrows(IllegalArgumentException.class,
                () -> mapper.createParser((char[]) null, -1, -1));
    }

    void readTreeInputStreamReadsLiteralVpackString() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        JsonNode node = mapper.readTree(new ByteArrayInputStream(VALUE_STRING));
        assertEquals("value", node.stringValue());
    }

    void readTreeFileReadsLiteralVpackString() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0129-", ".vpack");
        try {
            Files.write(path, VALUE_STRING);
            JsonNode node = new VPackMapper().readTree(path.toFile());
            assertEquals("value", node.stringValue());
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void readTreeByteArrayReadsLiteralVpackStringWithAndWithoutBounds() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        byte[] wrapped = new byte[VALUE_STRING.length + 2];
        wrapped[0] = 0x7f;
        System.arraycopy(VALUE_STRING, 0, wrapped, 1, VALUE_STRING.length);
        wrapped[wrapped.length - 1] = 0x7f;

        assertEquals("value", mapper.readTree(VALUE_STRING).stringValue());
        assertEquals("value", mapper.readTree(wrapped, 1, VALUE_STRING.length).stringValue());
    }

    void __invoke_createParserInputStreamReadsLiteralVpackString() throws Exception {
        try {
            createParserInputStreamReadsLiteralVpackString();
        } finally {
        }
    }


    void __invoke_createParserFileReadsLiteralVpackString() throws Exception {
        try {
            createParserFileReadsLiteralVpackString();
        } finally {
        }
    }


    void __invoke_createParserPathReadsLiteralVpackString() throws Exception {
        try {
            createParserPathReadsLiteralVpackString();
        } finally {
        }
    }


    void __invoke_createParserByteArrayReadsLiteralVpackString() throws Exception {
        try {
            createParserByteArrayReadsLiteralVpackString();
        } finally {
        }
    }


    void __invoke_createParserDataInputReadsLiteralVpackString() throws Exception {
        try {
            createParserDataInputReadsLiteralVpackString();
        } finally {
        }
    }


    void __invoke_createParserRejectsNullArgumentsAcrossObjectMapperOverloads() throws Exception {
        try {
            createParserRejectsNullArgumentsAcrossObjectMapperOverloads();
        } finally {
        }
    }


    void __invoke_readTreeInputStreamReadsLiteralVpackString() throws Exception {
        try {
            readTreeInputStreamReadsLiteralVpackString();
        } finally {
        }
    }


    void __invoke_readTreeFileReadsLiteralVpackString() throws Exception {
        try {
            readTreeFileReadsLiteralVpackString();
        } finally {
        }
    }


    void __invoke_readTreeByteArrayReadsLiteralVpackStringWithAndWithoutBounds() throws Exception {
        try {
            readTreeByteArrayReadsLiteralVpackStringWithAndWithoutBounds();
        } finally {
        }
    }

}
