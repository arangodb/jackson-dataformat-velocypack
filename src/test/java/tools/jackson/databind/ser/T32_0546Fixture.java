package tools.jackson.databind.ser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.io.TempDir;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0546Fixture {
private static final byte[] ONE = { 0x31 };
private static final byte[] FORTY_TWO = { 0x28, 0x2a };
private static final byte[] TEST = { 0x44, 't', 'e', 's', 't' };

    void testMapperCreateGeneratorOutputStreamVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(output)) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, output.toByteArray());
    }

    void testMapperCreateGeneratorOutputStreamEncodingVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(output, JsonEncoding.UTF8)) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, output.toByteArray());
    }

    void testMapperCreateGeneratorWriterVpackUnsupported() {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.createGenerator(new StringWriter()));
        assertEquals(0, count.get(), "No binary generator is created for a Writer target");
    }

    void testMapperCreateGeneratorFileVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        File file = tempDir.resolve("test.vpack").toFile();
        try (JsonGenerator generator = mapper.createGenerator(file, JsonEncoding.UTF8)) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, Files.readAllBytes(file.toPath()));
    }

    void testMapperCreateGeneratorPathVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        Path path = tempDir.resolve("test.vpack");
        try (JsonGenerator generator = mapper.createGenerator(path, JsonEncoding.UTF8)) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, Files.readAllBytes(path));
    }

    void testMapperCreateGeneratorDataOutputVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(
                (java.io.DataOutput) new DataOutputStream(output))) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, output.toByteArray());
    }

    void testMapperWriteValueAsBytesVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        assertArrayEquals(FORTY_TWO, mapper.writeValueAsBytes(42));
        assertEquals(1, count.get());
    }

    void testMapperWriteValueAsStringVpackUnsupported() {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.writeValueAsString(42));
        assertEquals(0, count.get(), "No binary generator is created for text output");
    }

    void testMapperWriteValueOutputStreamVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mapper.writeValue(output, "test");
        assertEquals(1, count.get());
        assertArrayEquals(TEST, output.toByteArray());
    }

    void testMapperWriteValueFileVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        File file = tempDir.resolve("test.vpack").toFile();
        mapper.writeValue(file, "test");
        assertEquals(1, count.get());
        assertArrayEquals(TEST, Files.readAllBytes(file.toPath()));
    }

    void testMapperWriteValuePathVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        Path path = tempDir.resolve("test.vpack");
        mapper.writeValue(path, "test");
        assertEquals(1, count.get());
        assertArrayEquals(TEST, Files.readAllBytes(path));
    }

    void testMapperWriteValueDataOutputVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mapper.writeValue((java.io.DataOutput) new DataOutputStream(output), "test");
        assertEquals(1, count.get());
        assertArrayEquals(TEST, output.toByteArray());
    }
private static ObjectMapper mapperWith(AtomicInteger count) {
        return VPackMapper.builder()
                .generatorInitializer((config, generator) -> count.incrementAndGet())
                .build();
    }

    void __invoke_testMapperCreateGeneratorOutputStreamVpack() throws Exception {
        try {
            testMapperCreateGeneratorOutputStreamVpack();
        } finally {
        }
    }


    void __invoke_testMapperCreateGeneratorOutputStreamEncodingVpack() throws Exception {
        try {
            testMapperCreateGeneratorOutputStreamEncodingVpack();
        } finally {
        }
    }


    void __invoke_testMapperCreateGeneratorWriterVpackUnsupported() throws Exception {
        try {
            testMapperCreateGeneratorWriterVpackUnsupported();
        } finally {
        }
    }


    void __invoke_testMapperCreateGeneratorFileVpack(Path tempDir) throws Exception {
        try {
            testMapperCreateGeneratorFileVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testMapperCreateGeneratorPathVpack(Path tempDir) throws Exception {
        try {
            testMapperCreateGeneratorPathVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testMapperCreateGeneratorDataOutputVpack() throws Exception {
        try {
            testMapperCreateGeneratorDataOutputVpack();
        } finally {
        }
    }


    void __invoke_testMapperWriteValueAsBytesVpack() throws Exception {
        try {
            testMapperWriteValueAsBytesVpack();
        } finally {
        }
    }


    void __invoke_testMapperWriteValueAsStringVpackUnsupported() throws Exception {
        try {
            testMapperWriteValueAsStringVpackUnsupported();
        } finally {
        }
    }


    void __invoke_testMapperWriteValueOutputStreamVpack() throws Exception {
        try {
            testMapperWriteValueOutputStreamVpack();
        } finally {
        }
    }


    void __invoke_testMapperWriteValueFileVpack(Path tempDir) throws Exception {
        try {
            testMapperWriteValueFileVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testMapperWriteValuePathVpack(Path tempDir) throws Exception {
        try {
            testMapperWriteValuePathVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testMapperWriteValueDataOutputVpack() throws Exception {
        try {
            testMapperWriteValueDataOutputVpack();
        } finally {
        }
    }

}
