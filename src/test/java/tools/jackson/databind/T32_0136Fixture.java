package tools.jackson.databind;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0136Fixture {
private static final byte[] VALUE_STRING = VPackWireFixtureTest.hex(
            "45 76 61 6c 75 65");
private static final byte[] VIEW_A = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 41 31 03");
private static final byte[] VIEW_B = VPackWireFixtureTest.hex(
            "0b 08 01 41 62 41 32 03");

    void closeCloseableWriterClosesValuesForByteArrayAndBinaryGenerator() throws Exception {
        ObjectWriter writer = new VPackMapper().writer()
                .with(SerializationFeature.CLOSE_CLOSEABLE);
        assertTrue(writer.isEnabled(SerializationFeature.CLOSE_CLOSEABLE));

        CloseableValue input = new CloseableValue();
        assertFalse(input.closed);
        assertArrayEquals(VPackWireFixtureTest.hex("0b 07 01 41 78 30 03"),
                writer.writeValueAsBytes(input));
        assertTrue(input.closed);
        input.close();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        input = new CloseableValue();
        assertFalse(input.closed);
        try (JsonGenerator generator = new VPackMapper().createGenerator(output)) {
            writer.writeValue(generator, input);
        }
        assertTrue(input.closed);
        assertArrayEquals(VPackWireFixtureTest.hex("0b 07 01 41 78 30 03"),
                output.toByteArray());
        input.close();
    }

    void viewSettingsPreserveWriterCopyIdentity() {
        ObjectWriter writer = new VPackMapper().writer();
        ObjectWriter changed = writer.withView(String.class);
        assertNotSame(writer, changed);
        assertSame(changed, changed.withView(String.class));

        Locale locale = findNonDefaultLocale();
        changed = writer.with(locale);
        assertNotSame(writer, changed);
        assertSame(changed, changed.with(locale));
    }

    void valueToTreeWithViewUsesLiteralVpackViewObjects() throws Exception {
        ViewBean input = new ViewBean();
        ObjectMapper mapper = new VPackMapper();

        ObjectWriter writerA = mapper.writerWithView(ViewA.class);
        JsonNode treeA = writerA.valueToTree(input);
        assertTrue(treeA.has("a"));
        assertFalse(treeA.has("b"));
        assertEquals(mapper.readTree(VIEW_A), treeA);
        assertEquals(mapper.readTree(writerA.writeValueAsBytes(input)), treeA);

        JsonNode treeB = mapper.writerWithView(ViewB.class).valueToTree(input);
        assertFalse(treeB.has("a"));
        assertTrue(treeB.has("b"));
        assertEquals(mapper.readTree(VIEW_B), treeB);
    }

    void createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackMapper().writer()
                .createGenerator(output)) {
            generator.writeString("value");
        }
        assertArrayEquals(VALUE_STRING, output.toByteArray());
        output.write(0x7f);
    }

    void createGeneratorFileWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0136-", ".vpack");
        try {
            try (JsonGenerator generator = new VPackMapper().writer()
                    .createGenerator(path.toFile(), JsonEncoding.UTF8)) {
                generator.writeString("value");
            }
            assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void createGeneratorPathWritesLiteralVpack() throws Exception {
        Path path = Files.createTempFile("vpack-t32-0136-", ".vpack");
        try {
            try (JsonGenerator generator = new VPackMapper().writer()
                    .createGenerator(path, JsonEncoding.UTF8)) {
                generator.writeString("value");
            }
            assertArrayEquals(VALUE_STRING, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    void createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack() {
        assertThrows(UnsupportedOperationException.class,
                () -> new VPackMapper().writer().createGenerator(new StringWriter()));
    }

    void createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutput output = new DataOutputStream(bytes);
        try (JsonGenerator generator = new VPackMapper().writer()
                .createGenerator(output)) {
            generator.writeString("value");
        }
        assertArrayEquals(VALUE_STRING, bytes.toByteArray());
        output.writeByte(0x7f);
    }

    void createGeneratorRejectsNullArguments() {
        ObjectWriter writer = new VPackMapper().writer();
        assertThrows(IllegalArgumentException.class,
                () -> writer.createGenerator((OutputStream) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.createGenerator((OutputStream) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.createGenerator((DataOutput) null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.createGenerator((Path) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.createGenerator((File) null, null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.createGenerator((Writer) null));
    }
private static Locale findNonDefaultLocale() {
        for (Locale locale : Locale.getAvailableLocales()) {
            if (!locale.equals(Locale.getDefault())) {
                return locale;
            }
        }
        throw new AssertionError("JDK must provide a locale distinct from the default");
    }
private static final class CloseableValue implements Closeable {
        public int x;
        boolean closed;

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }
private static class ViewA { }
private static class ViewB { }
private static class ViewBean {
        @JsonView(ViewA.class)
        public String a = "1";

        @JsonView(ViewB.class)
        public String b = "2";
    }

    void __invoke_closeCloseableWriterClosesValuesForByteArrayAndBinaryGenerator() throws Exception {
        try {
            closeCloseableWriterClosesValuesForByteArrayAndBinaryGenerator();
        } finally {
        }
    }


    void __invoke_viewSettingsPreserveWriterCopyIdentity() throws Exception {
        try {
            viewSettingsPreserveWriterCopyIdentity();
        } finally {
        }
    }


    void __invoke_valueToTreeWithViewUsesLiteralVpackViewObjects() throws Exception {
        try {
            valueToTreeWithViewUsesLiteralVpackViewObjects();
        } finally {
        }
    }


    void __invoke_createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget() throws Exception {
        try {
            createGeneratorOutputStreamWritesLiteralVpackAndBorrowsTarget();
        } finally {
        }
    }


    void __invoke_createGeneratorFileWritesLiteralVpack() throws Exception {
        try {
            createGeneratorFileWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_createGeneratorPathWritesLiteralVpack() throws Exception {
        try {
            createGeneratorPathWritesLiteralVpack();
        } finally {
        }
    }


    void __invoke_createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack() throws Exception {
        try {
            createGeneratorWriterIsExplicitlyUnsupportedForBinaryVpack();
        } finally {
        }
    }


    void __invoke_createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget() throws Exception {
        try {
            createGeneratorDataOutputWritesLiteralVpackAndBorrowsTarget();
        } finally {
        }
    }


    void __invoke_createGeneratorRejectsNullArguments() throws Exception {
        try {
            createGeneratorRejectsNullArguments();
        } finally {
        }
    }

}
