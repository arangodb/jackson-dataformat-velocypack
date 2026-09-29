package tools.jackson.databind.ser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.io.TempDir;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SequenceWriter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0548Fixture {
private static final byte[] FORTY_TWO = { 0x28, 0x2a };
private static final byte[] TEST = { 0x44, 't', 'e', 's', 't' };
private static final byte[] VALUES = { 0x31, 0x32 };
private static final byte[] VALUES_ARRAY = { 0x02, 0x04, 0x31, 0x32 };

    void testWriterWriteValueAsBytesVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        assertArrayEquals(FORTY_TWO, writer.writeValueAsBytes(42));
        assertEquals(1, count.get());
    }

    void testWriterWriteValueAsStringVpackUnsupported() {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        assertThrows(UnsupportedOperationException.class,
                () -> writer.writeValueAsString(42));
        assertEquals(0, count.get(), "No binary generator is created for text output");
    }

    void testWriterWriteValueDataOutputVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutput target = new DataOutputStream(output);
        writer.writeValue(target, "test");
        assertEquals(1, count.get());
        assertArrayEquals(TEST, output.toByteArray());
    }

    void testWriterWriteValueFileVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        File file = tempDir.resolve("test.vpack").toFile();
        writer.writeValue(file, "test");
        assertEquals(1, count.get());
        assertArrayEquals(TEST, Files.readAllBytes(file.toPath()));
    }

    void testWriterWriteValueOutputStreamVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        writer.writeValue(output, "test");
        assertEquals(1, count.get());
        assertArrayEquals(TEST, output.toByteArray());
    }

    void testWriterWriteValuePathVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        Path path = tempDir.resolve("test.vpack");
        writer.writeValue(path, "test");
        assertEquals(1, count.get());
        assertArrayEquals(TEST, Files.readAllBytes(path));
    }

    void testWriterWriteValueWithProvidedGeneratorVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ObjectWriter writer = mapper.writer();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(output)) {
            assertEquals(1, count.get());
            writer.writeValue(generator, 42);
            assertEquals(1, count.get(), "Provided generator must not be initialized again");
        }
        assertArrayEquals(FORTY_TWO, output.toByteArray());
    }

    void testWriterWriteValueWriterVpackUnsupported() {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        assertThrows(UnsupportedOperationException.class,
                () -> writer.writeValue(new java.io.StringWriter(), "test"));
        assertEquals(0, count.get(), "No binary generator is created for a Writer target");
    }

    void testWriterWriteValuesAsArrayDataOutputVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter sequence = writer.writeValuesAsArray(
                (DataOutput) new DataOutputStream(output))) {
            assertEquals(1, count.get());
            sequence.write(1);
            sequence.write(2);
            assertEquals(1, count.get());
        }
        assertArrayEquals(VALUES_ARRAY, output.toByteArray());
    }

    void testWriterWriteValuesAsArrayFileVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        File file = tempDir.resolve("values.vpack").toFile();
        try (SequenceWriter sequence = writer.writeValuesAsArray(file)) {
            assertEquals(1, count.get());
            sequence.write(1);
            sequence.write(2);
            assertEquals(1, count.get());
        }
        assertArrayEquals(VALUES_ARRAY, Files.readAllBytes(file.toPath()));
    }

    void testWriterWriteValuesAsArrayOutputStreamVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter sequence = writer.writeValuesAsArray(output)) {
            assertEquals(1, count.get());
            sequence.write(1);
            sequence.write(2);
            assertEquals(1, count.get());
        }
        assertArrayEquals(VALUES_ARRAY, output.toByteArray());
    }

    void testWriterWriteValuesAsArrayPathVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        Path path = tempDir.resolve("values.vpack");
        try (SequenceWriter sequence = writer.writeValuesAsArray(path)) {
            assertEquals(1, count.get());
            sequence.write(1);
            sequence.write(2);
            assertEquals(1, count.get());
        }
        assertArrayEquals(VALUES_ARRAY, Files.readAllBytes(path));
    }
private static ObjectMapper mapperWith(AtomicInteger count) {
        return VPackMapper.builder()
                .generatorInitializer((config, generator) -> count.incrementAndGet())
                .build();
    }
private static ObjectWriter writerWith(AtomicInteger count) {
        return mapperWith(count).writer();
    }

    void __invoke_testWriterWriteValueAsBytesVpack() throws Exception {
        try {
            testWriterWriteValueAsBytesVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValueAsStringVpackUnsupported() throws Exception {
        try {
            testWriterWriteValueAsStringVpackUnsupported();
        } finally {
        }
    }


    void __invoke_testWriterWriteValueDataOutputVpack() throws Exception {
        try {
            testWriterWriteValueDataOutputVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValueFileVpack(Path tempDir) throws Exception {
        try {
            testWriterWriteValueFileVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testWriterWriteValueOutputStreamVpack() throws Exception {
        try {
            testWriterWriteValueOutputStreamVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuePathVpack(Path tempDir) throws Exception {
        try {
            testWriterWriteValuePathVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testWriterWriteValueWithProvidedGeneratorVpack() throws Exception {
        try {
            testWriterWriteValueWithProvidedGeneratorVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValueWriterVpackUnsupported() throws Exception {
        try {
            testWriterWriteValueWriterVpackUnsupported();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesAsArrayDataOutputVpack() throws Exception {
        try {
            testWriterWriteValuesAsArrayDataOutputVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesAsArrayFileVpack(Path tempDir) throws Exception {
        try {
            testWriterWriteValuesAsArrayFileVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesAsArrayOutputStreamVpack() throws Exception {
        try {
            testWriterWriteValuesAsArrayOutputStreamVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesAsArrayPathVpack(Path tempDir) throws Exception {
        try {
            testWriterWriteValuesAsArrayPathVpack(tempDir);
        } finally {
        }
    }

}
