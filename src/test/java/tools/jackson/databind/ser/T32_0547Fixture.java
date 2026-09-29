package tools.jackson.databind.ser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.io.TempDir;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0547Fixture {
private static final byte[] ONE = { 0x31 };
private static final byte[] TWO = { 0x32 };
private static final byte[] THREE = { 0x33 };
private static final byte[] FORTY_TWO = { 0x28, 0x2a };

    void testMapperWriteValueWithProvidedGeneratorVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(output)) {
            assertEquals(1, count.get());
            mapper.writeValue(generator, 42);
            assertEquals(1, count.get(), "Provided generator must not be initialized again");
        }
        assertArrayEquals(FORTY_TWO, output.toByteArray());
    }

    void testMapperWriteValueWriterVpackUnsupported() {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.writeValue(new StringWriter(), "test"));
        assertEquals(0, count.get(), "No binary generator is created for a Writer target");
    }

    void testMultipleWriterWritesIncrementCountVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);

        assertArrayEquals(ONE, writer.writeValueAsBytes(1));
        assertEquals(1, count.get());
        assertArrayEquals(TWO, writer.writeValueAsBytes(2));
        assertEquals(2, count.get());
        assertArrayEquals(THREE, writer.writeValueAsBytes(3));
        assertEquals(3, count.get());
    }

    void testMultipleWritesIncrementCountVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);

        assertArrayEquals(ONE, mapper.writeValueAsBytes(1));
        assertEquals(1, count.get());
        assertArrayEquals(TWO, mapper.writeValueAsBytes(2));
        assertEquals(2, count.get());
        assertArrayEquals(THREE, mapper.writeValueAsBytes(3));
        assertEquals(3, count.get());
    }

    void testNoInitializerByDefaultVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertNull(mapper.serializationConfig().getGeneratorInitializer());
        assertArrayEquals(FORTY_TWO, mapper.writeValueAsBytes(42));
    }

    void testWriterCreateGeneratorDataOutputVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = writer.createGenerator(
                (java.io.DataOutput) new DataOutputStream(output))) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, output.toByteArray());
    }

    void testWriterCreateGeneratorFileVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        File file = tempDir.resolve("test.vpack").toFile();
        try (JsonGenerator generator = writer.createGenerator(file, JsonEncoding.UTF8)) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, Files.readAllBytes(file.toPath()));
    }

    void testWriterCreateGeneratorOutputStreamVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = writer.createGenerator(output)) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, output.toByteArray());
    }

    void testWriterCreateGeneratorOutputStreamEncodingVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = writer.createGenerator(output, JsonEncoding.UTF8)) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, output.toByteArray());
    }

    void testWriterCreateGeneratorPathVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        Path path = tempDir.resolve("test.vpack");
        try (JsonGenerator generator = writer.createGenerator(path, JsonEncoding.UTF8)) {
            assertEquals(1, count.get());
            generator.writeNumber(1);
        }
        assertEquals(1, count.get());
        assertArrayEquals(ONE, Files.readAllBytes(path));
    }

    void testWriterCreateGeneratorWriterVpackUnsupported() {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        assertThrows(UnsupportedOperationException.class,
                () -> writer.createGenerator(new StringWriter()));
        assertEquals(0, count.get(), "No binary generator is created for a Writer target");
    }

    void testWriterValueToTreeVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        JsonNode node = writer.valueToTree(Map.of("a", 1));
        assertNotNull(node);
        assertTrue(node.isObject());
        assertEquals(1, node.get("a").asInt());
        assertEquals(1, count.get());
    }
private static ObjectMapper mapperWith(AtomicInteger count) {
        return VPackMapper.builder()
                .generatorInitializer((config, generator) -> count.incrementAndGet())
                .build();
    }
private static ObjectWriter writerWith(AtomicInteger count) {
        return mapperWith(count).writer();
    }

    void __invoke_testMapperWriteValueWithProvidedGeneratorVpack() throws Exception {
        try {
            testMapperWriteValueWithProvidedGeneratorVpack();
        } finally {
        }
    }


    void __invoke_testMapperWriteValueWriterVpackUnsupported() throws Exception {
        try {
            testMapperWriteValueWriterVpackUnsupported();
        } finally {
        }
    }


    void __invoke_testMultipleWriterWritesIncrementCountVpack() throws Exception {
        try {
            testMultipleWriterWritesIncrementCountVpack();
        } finally {
        }
    }


    void __invoke_testMultipleWritesIncrementCountVpack() throws Exception {
        try {
            testMultipleWritesIncrementCountVpack();
        } finally {
        }
    }


    void __invoke_testNoInitializerByDefaultVpack() throws Exception {
        try {
            testNoInitializerByDefaultVpack();
        } finally {
        }
    }


    void __invoke_testWriterCreateGeneratorDataOutputVpack() throws Exception {
        try {
            testWriterCreateGeneratorDataOutputVpack();
        } finally {
        }
    }


    void __invoke_testWriterCreateGeneratorFileVpack(Path tempDir) throws Exception {
        try {
            testWriterCreateGeneratorFileVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testWriterCreateGeneratorOutputStreamVpack() throws Exception {
        try {
            testWriterCreateGeneratorOutputStreamVpack();
        } finally {
        }
    }


    void __invoke_testWriterCreateGeneratorOutputStreamEncodingVpack() throws Exception {
        try {
            testWriterCreateGeneratorOutputStreamEncodingVpack();
        } finally {
        }
    }


    void __invoke_testWriterCreateGeneratorPathVpack(Path tempDir) throws Exception {
        try {
            testWriterCreateGeneratorPathVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testWriterCreateGeneratorWriterVpackUnsupported() throws Exception {
        try {
            testWriterCreateGeneratorWriterVpackUnsupported();
        } finally {
        }
    }


    void __invoke_testWriterValueToTreeVpack() throws Exception {
        try {
            testWriterValueToTreeVpack();
        } finally {
        }
    }

}
