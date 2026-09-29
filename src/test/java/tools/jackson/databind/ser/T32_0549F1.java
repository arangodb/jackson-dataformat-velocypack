package tools.jackson.databind.ser;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0549F1 {
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

    void testInArrayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.USE_STATIC_TYPING)
                .build();
        SubType[] input = { new SubType() };
        byte[] encoded = mapper.writerFor(BaseInterface[].class).writeValueAsBytes(input);
        List<?> values = mapper.readValue(encoded, List.class);
        assertEquals(1, values.size());
        Map<?, ?> value = assertInstanceOf(Map.class, values.get(0));
        assertEquals(1, value.size());
        assertEquals(3, value.get("b"));
        assertFalse(value.containsKey("a"));
    }

    void testIncompatibleRootTypeVpack() {
        ObjectWriter writer = new VPackMapper().writerFor(java.util.HashMap.class);
        SubType bean = new SubType();
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> writer.writeValueAsBytes(bean));
        assertEquals(true, exception.getMessage().contains("Incompatible types"));
    }

    void testIndexedListExampleVpack() throws Exception {
        IndexedList<TestIndexed, String> value = new VPackMapper().readValue(
                INDEXED_LIST, new TypeReference<IndexedList<TestIndexed, String>>() { });
        assertEquals(1, value.size());
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                value.delegate.get(0).value);
    }

    void testIssue456WrapperPartVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(INT_123,
                mapper.writerFor(Integer.TYPE).writeValueAsBytes(Integer.valueOf(123)));
        assertArrayEquals(LONG_456,
                mapper.writerFor(Long.TYPE).writeValueAsBytes(Long.valueOf(456L)));
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

    void __invoke_testInArrayVpack() throws Exception {
        try {
            testInArrayVpack();
        } finally {
        }
    }


    void __invoke_testIncompatibleRootTypeVpack() throws Exception {
        try {
            testIncompatibleRootTypeVpack();
        } finally {
        }
    }


    void __invoke_testIndexedListExampleVpack() throws Exception {
        try {
            testIndexedListExampleVpack();
        } finally {
        }
    }


    void __invoke_testIssue456WrapperPartVpack() throws Exception {
        try {
            testIssue456WrapperPartVpack();
        } finally {
        }
    }

}
