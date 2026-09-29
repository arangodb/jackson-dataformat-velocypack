package tools.jackson.core.unittest.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0037Fixture {
private static final byte[] VALUE = { 0x45, 'v', 'a', 'l', 'u', 'e' };

    void outputStreamGeneratorWritesLiteralVpackAndLeavesTargetOpen() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), output)) {
            generator.writeString("value");
        }

        output.write(0x18);
        assertArrayEquals(concat(VALUE, new byte[] { 0x18 }), output.toByteArray());
    }

    void dataOutputGeneratorWritesLiteralVpackAndLeavesTargetOpen() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(output);
        try (JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), dataOutput)) {
            generator.writeString("value");
        }

        dataOutput.write(0x18);
        assertArrayEquals(concat(VALUE, new byte[] { 0x18 }), output.toByteArray());
    }

    void pathGeneratorWritesLiteralVpackRoot() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0037", ".bin");
        try {
            try (JsonGenerator generator = new VPackFactory().createGenerator(
                    ObjectWriteContext.empty(), path, JsonEncoding.UTF8)) {
                generator.writeString("value");
            }
            assertArrayEquals(VALUE, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void byteArrayParserReadsLiteralVpackRoot() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), VALUE, 0, VALUE.length)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertNull(parser.nextToken());
        }
    }

    void inputStreamParserReadsLiteralVpackRoot() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), new ByteArrayInputStream(VALUE))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertNull(parser.nextToken());
        }
    }

    void pathParserReadsLiteralVpackRoot() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0037", ".bin");
        try {
            Files.write(path, VALUE);
            try (JsonParser parser = new VPackFactory().createParser(
                    ObjectReadContext.empty(), path)) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertEquals("value", parser.getString());
                assertNull(parser.nextToken());
            }
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void dataInputParserReadsLiteralVpackRoot() throws Exception {
        DataInput dataInput = new DataInputStream(new ByteArrayInputStream(VALUE));
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), dataInput)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertNull(parser.nextToken());
        }
    }

    void characterSourcesAndWriterTargetAreExplicitlyUnsupported() {
        VPackFactory factory = new VPackFactory();
        assertThrows(UnsupportedOperationException.class, () -> factory.createGenerator(
                ObjectWriteContext.empty(), new StringWriter()));
        assertThrows(UnsupportedOperationException.class, () -> factory.createParser(
                ObjectReadContext.empty(), new StringReader("value")));
        assertThrows(UnsupportedOperationException.class, () -> factory.createParser(
                ObjectReadContext.empty(), new char[] { 'v' }, 0, 1));
    }
private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    void __invoke_outputStreamGeneratorWritesLiteralVpackAndLeavesTargetOpen() throws Exception {
        try {
            outputStreamGeneratorWritesLiteralVpackAndLeavesTargetOpen();
        } finally {
        }
    }


    void __invoke_dataOutputGeneratorWritesLiteralVpackAndLeavesTargetOpen() throws Exception {
        try {
            dataOutputGeneratorWritesLiteralVpackAndLeavesTargetOpen();
        } finally {
        }
    }


    void __invoke_pathGeneratorWritesLiteralVpackRoot() throws Exception {
        try {
            pathGeneratorWritesLiteralVpackRoot();
        } finally {
        }
    }


    void __invoke_byteArrayParserReadsLiteralVpackRoot() throws Exception {
        try {
            byteArrayParserReadsLiteralVpackRoot();
        } finally {
        }
    }


    void __invoke_inputStreamParserReadsLiteralVpackRoot() throws Exception {
        try {
            inputStreamParserReadsLiteralVpackRoot();
        } finally {
        }
    }


    void __invoke_pathParserReadsLiteralVpackRoot() throws Exception {
        try {
            pathParserReadsLiteralVpackRoot();
        } finally {
        }
    }


    void __invoke_dataInputParserReadsLiteralVpackRoot() throws Exception {
        try {
            dataInputParserReadsLiteralVpackRoot();
        } finally {
        }
    }


    void __invoke_characterSourcesAndWriterTargetAreExplicitlyUnsupported() throws Exception {
        try {
            characterSourcesAndWriterTargetAreExplicitlyUnsupported();
        } finally {
        }
    }

}
