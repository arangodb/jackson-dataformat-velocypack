package tools.jackson.databind;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SequenceWriter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0138F0 {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");
private static final byte[] POLY_OBJECT = VPackWireFixtureTest.hex(
            "0b 21 02 45 40 74 79 70 65 47 73 75 62 74 79 70 65 "
            + "47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03 11");
private static final byte[] POLY_ARRAY = VPackWireFixtureTest.hex(
            "02 23 0b 21 02 45 40 74 79 70 65 47 73 75 62 74 79 70 65 "
            + "47 63 6f 6e 74 65 6e 74 45 68 65 6c 6c 6f 03 11");

    void writeValuesOutputStreamWritesLiteralVpackAndLeavesTargetUsable()
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        SequenceWriter writer = new VPackMapper().writer().writeValues(output);
        writer.write("value");

        assertArrayEquals(VALUE_STRING, output.toByteArray());
        output.write(0x7f);
        writer.close();
    }

    void writeValuesFileWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0138-", ".vpack");
        try {
            SequenceWriter writer = new VPackMapper().writer()
                    .writeValues(path.toFile());
            writer.write("value");
            writer.close();

            assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void writeValuesPathWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0138-", ".vpack");
        try {
            SequenceWriter writer = new VPackMapper().writer().writeValues(path);
            writer.write("value");
            writer.close();

            assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void writeValuesWriterIsExplicitlyUnsupportedForBinaryVpack() {
        ObjectWriter writer = new VPackMapper().writer();
        assertThrows(UnsupportedOperationException.class,
                () -> writer.writeValues((Writer) new StringWriter()));
    }

    void writeValuesJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable()
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectMapper mapper = new VPackMapper();
        JsonGenerator generator = mapper.createGenerator(output);
        SequenceWriter writer = mapper.writer().writeValues(generator);
        writer.write("value");

        assertArrayEquals(VALUE_STRING, output.toByteArray());
        output.write(0x7f);
        writer.close();
    }

    void writeValuesRejectsNullArgumentsAcrossAssignedOverloads() {
        ObjectWriter writer = new VPackMapper().writer();
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValues((java.io.OutputStream) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValues((DataOutput) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValues((Path) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValues((File) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValues((Writer) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValues((JsonGenerator) null));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "@type")
    @JsonSubTypes(@JsonSubTypes.Type(name = "subtype", value = Subtype.class))
    private interface Supertype { }
@JsonTypeName("subtype")
    private static class Subtype implements Supertype {
        public String content = "hello";
    }

    void __invoke_writeValuesOutputStreamWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValuesOutputStreamWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }


    void __invoke_writeValuesFileWritesLiteralVpack() throws Exception {
        try {
            writeValuesFileWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_writeValuesPathWritesLiteralVpack() throws Exception {
        try {
            writeValuesPathWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_writeValuesWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
        try {
            writeValuesWriterIsExplicitlyUnsupportedForBinaryVpack();
        } finally {
        }
    }


    void __invoke_writeValuesJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValuesJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }


    void __invoke_writeValuesRejectsNullArgumentsAcrossAssignedOverloads() throws Exception {
        try {
            writeValuesRejectsNullArgumentsAcrossAssignedOverloads();
        } finally {
        }
    }

}
