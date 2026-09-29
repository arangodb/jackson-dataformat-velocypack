package tools.jackson.databind;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SequenceWriter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0137Fixture {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");
private static final byte[] ARRAY_WITH_VALUE = VPackWireFixtureTest.hex(
            "02 08 45 76 61 6c 75 65");

    void writeValuesAsArrayDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutput output = new DataOutputStream(bytes);
        SequenceWriter writer = new VPackMapper().writer().writeValuesAsArray(output);
        writer.write("value");
        writer.flush();
        writer.close();

        assertArrayEquals(ARRAY_WITH_VALUE, bytes.toByteArray());
        output.writeByte(0x7f);
    }

    void writeValuesAsArrayFileWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0137-", ".vpack");
        try {
            SequenceWriter writer = new VPackMapper().writer()
                    .writeValuesAsArray(path.toFile());
            writer.write("value");
            writer.flush();
            writer.close();

            assertArrayEquals(ARRAY_WITH_VALUE, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void writeValuesAsArrayJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable()
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectMapper mapper = new VPackMapper();
        JsonGenerator generator = mapper.createGenerator(output);
        SequenceWriter writer = mapper.writer().writeValuesAsArray(generator);
        writer.write("value");
        writer.flush();
        writer.close();
        generator.flush();
        generator.close();

        assertArrayEquals(ARRAY_WITH_VALUE, output.toByteArray());
        output.write(0x7f);
    }

    void writeValuesAsArrayOutputStreamWritesLiteralVpackAndLeavesTargetUsable()
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        SequenceWriter writer = new VPackMapper().writer()
                .writeValuesAsArray(output);
        writer.write("value");
        writer.flush();
        writer.close();

        assertArrayEquals(ARRAY_WITH_VALUE, output.toByteArray());
        output.write(0x7f);
    }

    void writeValuesAsArrayPathWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0137-", ".vpack");
        try {
            SequenceWriter writer = new VPackMapper().writer()
                    .writeValuesAsArray(path);
            writer.write("value");
            writer.flush();
            writer.close();

            assertArrayEquals(ARRAY_WITH_VALUE, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void writeValuesAsArrayWriterIsExplicitlyUnsupportedForBinaryVpack() {
        ObjectWriter writer = new VPackMapper().writer();
        assertThrows(UnsupportedOperationException.class,
                () -> writer.writeValuesAsArray((Writer) new StringWriter()));
    }

    void writeValuesAsArrayRejectsNullTargetsAcrossAssignedOverloads() {
        ObjectWriter writer = new VPackMapper().writer();
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValuesAsArray((java.io.OutputStream) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValuesAsArray((DataOutput) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValuesAsArray((Path) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValuesAsArray((File) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValuesAsArray((Writer) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.writeValuesAsArray((JsonGenerator) null));
    }

    void writeValuesDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutput output = new DataOutputStream(bytes);
        SequenceWriter writer = new VPackMapper().writer().writeValues(output);
        writer.write("value");

        assertArrayEquals(VALUE_STRING, bytes.toByteArray());
        output.writeByte(0x7f);
        writer.close();
    }

    void __invoke_writeValuesAsArrayDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValuesAsArrayDataOutputWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }


    void __invoke_writeValuesAsArrayFileWritesLiteralVpack() throws Exception {
        try {
            writeValuesAsArrayFileWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_writeValuesAsArrayJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValuesAsArrayJsonGeneratorWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }


    void __invoke_writeValuesAsArrayOutputStreamWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValuesAsArrayOutputStreamWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }


    void __invoke_writeValuesAsArrayPathWritesLiteralVpack() throws Exception {
        try {
            writeValuesAsArrayPathWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_writeValuesAsArrayWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
        try {
            writeValuesAsArrayWriterIsExplicitlyUnsupportedForBinaryVpack();
        } finally {
        }
    }


    void __invoke_writeValuesAsArrayRejectsNullTargetsAcrossAssignedOverloads() throws Exception {
        try {
            writeValuesAsArrayRejectsNullTargetsAcrossAssignedOverloads();
        } finally {
        }
    }


    void __invoke_writeValuesDataOutputWritesLiteralVpackAndLeavesTargetUsable() throws Exception {
        try {
            writeValuesDataOutputWritesLiteralVpackAndLeavesTargetUsable();
        } finally {
        }
    }

}
