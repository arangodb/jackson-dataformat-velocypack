package tools.jackson.databind.ser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.io.TempDir;
import com.fasterxml.jackson.annotation.JsonCreator;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SequenceWriter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0549F0 {
private static final byte[] VALUES = { 0x31, 0x32 };
private static final byte[] VALUES_ARRAY = { 0x02, 0x04, 0x31, 0x32 };
private static final byte[] INT_123 = { 0x28, 0x7b };
private static final byte[] LONG_456 = { 0x29, (byte) 0xc8, 0x01 };
private static final byte[] INDEXED_LIST = {
            0x02, 0x27, 0x64,
            '1', '2', '3', 'e', '4', '5', '6', '7', '-',
            'e', '8', '9', 'b', '-', '1', '2', 'd', '3', '-',
            'a', '4', '5', '6', '-', '4', '2', '6', '6', '1',
            '4', '1', '7', '4', '0', '0', '0'
    };

    void testWriterWriteValuesOutputStreamVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter sequence = writer.writeValues(output)) {
            assertEquals(1, count.get());
            sequence.write(1);
            sequence.write(2);
            assertEquals(1, count.get());
        }
        assertArrayEquals(VALUES, output.toByteArray());
    }

    void testWriterWriteValuesWriterVpackUnsupported() {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        assertThrows(UnsupportedOperationException.class,
                () -> writer.writeValues(new StringWriter()));
        assertEquals(0, count.get(), "No binary generator is created for a Writer target");
    }

    void testWriterWriteValuesFileVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        File file = tempDir.resolve("values.vpack").toFile();
        try (SequenceWriter sequence = writer.writeValues(file)) {
            assertEquals(1, count.get());
            sequence.write(1);
            sequence.write(2);
            assertEquals(1, count.get());
        }
        assertArrayEquals(VALUES, Files.readAllBytes(file.toPath()));
    }

    void testWriterWriteValuesPathVpack(@TempDir Path tempDir) throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        Path path = tempDir.resolve("values.vpack");
        try (SequenceWriter sequence = writer.writeValues(path)) {
            assertEquals(1, count.get());
            sequence.write(1);
            sequence.write(2);
            assertEquals(1, count.get());
        }
        assertArrayEquals(VALUES, Files.readAllBytes(path));
    }

    void testWriterWriteValuesDataOutputVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter sequence = writer.writeValues(
                (DataOutput) new DataOutputStream(output))) {
            assertEquals(1, count.get());
            sequence.write(1);
            sequence.write(2);
            assertEquals(1, count.get());
        }
        assertArrayEquals(VALUES, output.toByteArray());
    }

    void testWriterWriteValuesWithProvidedGeneratorVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(output)) {
            assertEquals(1, count.get());
            try (SequenceWriter sequence = mapper.writer().writeValues(generator)) {
                sequence.write(1);
                sequence.write(2);
            }
            assertEquals(1, count.get(), "Provided generator must not be initialized again");
        }
        assertArrayEquals(VALUES, output.toByteArray());
    }

    void testWriterWriteValuesAsArrayWriterVpackUnsupported() {
        AtomicInteger count = new AtomicInteger();
        ObjectWriter writer = writerWith(count);
        assertThrows(UnsupportedOperationException.class,
                () -> writer.writeValuesAsArray(new StringWriter()));
        assertEquals(0, count.get(), "No binary generator is created for a Writer target");
    }

    void testWriterWriteValuesAsArrayWithProvidedGeneratorVpack() throws Exception {
        AtomicInteger count = new AtomicInteger();
        ObjectMapper mapper = mapperWith(count);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = mapper.createGenerator(output)) {
            assertEquals(1, count.get());
            try (SequenceWriter sequence = mapper.writer().writeValuesAsArray(generator)) {
                sequence.write(1);
            }
            assertEquals(1, count.get(), "Provided generator must not be initialized again");
        }
        assertArrayEquals(new byte[] { 0x02, 0x03, 0x31 }, output.toByteArray());
    }
private static ObjectMapper mapperWith(AtomicInteger count) {
        return VPackMapper.builder()
                .generatorInitializer((config, generator) -> count.incrementAndGet())
                .build();
    }
private static ObjectWriter writerWith(AtomicInteger count) {
        return mapperWith(count).writer();
    }
interface Indexed<T> {
        T index();
    }
static class TestIndexed implements Indexed<String> {
        final UUID value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        TestIndexed(UUID value) {
            this.value = value;
        }

        @Override
        public String index() {
            return value.toString();
        }
    }
static final class IndexedList<T extends Indexed<K>, K> extends AbstractList<T> {
        final ArrayList<T> delegate;

        private IndexedList(ArrayList<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get(int index) {
            return delegate.get(index);
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static <T extends Indexed<K>, K> IndexedList<T, K> fromJson(
                Iterable<? extends T> values) {
            ArrayList<T> list = new ArrayList<>();
            for (T value : values) {
                list.add(value);
            }
            return new IndexedList<>(list);
        }
    }
interface BaseInterface {
        int getB();
    }
static class BaseType implements BaseInterface {
        public String a = "a";

        @Override
        public int getB() {
            return 3;
        }
    }
static class SubType extends BaseType {
        public String a2 = "x";

        public boolean getB2() {
            return true;
        }
    }

    void __invoke_testWriterWriteValuesOutputStreamVpack() throws Exception {
        try {
            testWriterWriteValuesOutputStreamVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesWriterVpackUnsupported() throws Exception {
        try {
            testWriterWriteValuesWriterVpackUnsupported();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesFileVpack(Path tempDir) throws Exception {
        try {
            testWriterWriteValuesFileVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesPathVpack(Path tempDir) throws Exception {
        try {
            testWriterWriteValuesPathVpack(tempDir);
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesDataOutputVpack() throws Exception {
        try {
            testWriterWriteValuesDataOutputVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesWithProvidedGeneratorVpack() throws Exception {
        try {
            testWriterWriteValuesWithProvidedGeneratorVpack();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesAsArrayWriterVpackUnsupported() throws Exception {
        try {
            testWriterWriteValuesAsArrayWriterVpackUnsupported();
        } finally {
        }
    }


    void __invoke_testWriterWriteValuesAsArrayWithProvidedGeneratorVpack() throws Exception {
        try {
            testWriterWriteValuesAsArrayWithProvidedGeneratorVpack();
        } finally {
        }
    }

}
