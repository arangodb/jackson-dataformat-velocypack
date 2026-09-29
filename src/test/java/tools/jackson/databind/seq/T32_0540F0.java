package tools.jackson.databind.seq;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MappingIterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0540F0 {
private static final VPackMapper MAPPER = new VPackMapper();
private static final byte[] BEAN_A3 = {
            0x14, 0x06, 0x41, 0x61, 0x33, 0x01
    };
private static final byte[] BEAN_A27 = {
            0x14, 0x07, 0x41, 0x61, 0x28, 0x1B, 0x01
    };
private static final byte[] BEAN_A6 = {
            0x14, 0x06, 0x41, 0x61, 0x36, 0x01
    };
private static final byte[] BEAN_A_NEG7 = {
            0x14, 0x07, 0x41, 0x61, 0x20, (byte) 0xF9, 0x01
    };
private static final byte[] BEAN_A3_A27_ARRAY = {
            0x13, 0x10,
            0x14, 0x06, 0x41, 0x61, 0x36, 0x01,
            0x14, 0x07, 0x41, 0x61, 0x20, (byte) 0xF9, 0x01,
            0x02
    };
private static final byte[] ARRAY_ROOTS = {
            0x13, 0x04, 0x31, 0x01,
            0x13, 0x04, 0x33, 0x01
    };
private static final byte[] DECIMAL_ARRAY = {
            0x06, 0x3F, 0x02,
            0x0B, 0x19, 0x02, 0x44, 0x76, 0x61, 0x6C, 0x32, 0x35,
            0x44, 0x76, 0x61, 0x6C, 0x31, 0x1B,
            0x38, 0x32, (byte) 0x8F, (byte) 0xFC, (byte) 0xC1, (byte) 0xC0, (byte) 0xF3, 0x3F,
            0x09, 0x03,
            0x0B, 0x21, 0x02, 0x44, 0x76, 0x61, 0x6C, 0x32, 0x1B,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1A, (byte) 0xC0,
            0x44, 0x76, 0x61, 0x6C, 0x31, 0x1B,
            0x1F, (byte) 0x85, (byte) 0xEB, 0x51, (byte) 0xB8, 0x1E, 0x09, 0x40,
            0x11, 0x03,
            0x03, 0x1C
    };

    void testRootBeansVpack() throws Exception {
        byte[] sequence = concat(BEAN_A3, BEAN_A27);
        for (Object input : new Object[] { sequence,
                new ByteArrayInputStream(sequence) }) {
            MappingIterator<Bean> it = input instanceof byte[]
                    ? MAPPER.readerFor(Bean.class).readValues((byte[]) input)
                    : MAPPER.readerFor(Bean.class)
                            .readValues((ByteArrayInputStream) input);
            try (it) {
                assertNotNull(it.currentLocation());
                assertTrue(it.hasNext());
                assertEquals(3, it.next().a);
                assertTrue(it.hasNext());
                assertEquals(27, it.next().a);
                assertFalse(it.hasNext());
            }
        }

        MappingIterator<Bean> allIterator = MAPPER.readerFor(Bean.class)
                .readValues(sequence);
        List<Bean> all = allIterator.readAll();
        assertEquals(List.of(new Bean(3), new Bean(27)), all);

        MappingIterator<Bean> setIterator = MAPPER.readerFor(Bean.class)
                .readValues(concat(BEAN_A3, BEAN_A3));
        Set<Bean> set = setIterator.readAll(new HashSet<Bean>());
        assertEquals(1, set.size());
        assertEquals(3, set.iterator().next().a);
    }

    void testRootBeansInArrayVpack() throws Exception {
        try (MappingIterator<Bean> it = MAPPER.readerFor(Bean.class)
                .readValues(BEAN_A3_A27_ARRAY)) {
            assertNotNull(it.currentLocation());
            assertTrue(it.hasNext());
            assertEquals(6, it.next().a);
            assertTrue(it.hasNext());
            assertEquals(-7, it.next().a);
            assertFalse(it.hasNext());
        }

        MappingIterator<Bean> allIterator = MAPPER.readerFor(Bean.class)
                .readValues(BEAN_A3_A27_ARRAY);
        List<Bean> all = allIterator.readAll();
        assertEquals(List.of(new Bean(6), new Bean(-7)), all);
    }

    void testRootMapsVpack() throws Exception {
        byte[] sequence = concat(BEAN_A3, BEAN_A27);
        try (MappingIterator<Map<?, ?>> it = MAPPER.readerFor(Map.class)
                .readValues(sequence)) {
            assertTrue(it.hasNext());
            assertEquals(Integer.valueOf(3), it.next().get("a"));
            assertTrue(it.hasNext());
            assertEquals(Integer.valueOf(27), it.next().get("a"));
            assertFalse(it.hasNext());
        }
    }

    void testRootArraysWithParserVpack() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(ARRAY_ROOTS)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            try (MappingIterator<int[]> it = MAPPER.readerFor(int[].class)
                    .readValues(parser)) {
                assertTrue(it.hasNext());
                assertEquals(1, it.next()[0]);
                assertTrue(it.hasNext());
                assertEquals(3, it.next()[0]);
                assertFalse(it.hasNext());
            }
        }
    }

    void testObjectReaderWithJsonReadFeatureFastDoubleParserVpack() throws Exception {
        try (MappingIterator<Map<String, Double>> it = MAPPER.reader()
                .forType(new TypeReference<Map<String, Double>>() { })
                .readValues(DECIMAL_ARRAY)) {
            Map<String, Double> first = it.next();
            assertEquals(Double.valueOf(1.23456), first.get("val1"));
            assertEquals(Double.valueOf(5), first.get("val2"));
            Map<String, Double> second = it.next();
            assertEquals(Double.valueOf(3.14), second.get("val1"));
            assertEquals(Double.valueOf(-6.5), second.get("val2"));
            assertFalse(it.hasNext());
        }
    }

    void testObjectReaderWithJsonReadFeatureFastFloatParserVpack() throws Exception {
        try (MappingIterator<Map<String, Float>> it = MAPPER.reader()
                .forType(new TypeReference<Map<String, Float>>() { })
                .readValues(DECIMAL_ARRAY)) {
            Map<String, Float> first = it.next();
            assertEquals(Float.valueOf(1.23456f), first.get("val1"));
            assertEquals(Float.valueOf(5), first.get("val2"));
            Map<String, Float> second = it.next();
            assertEquals(Float.valueOf(3.14f), second.get("val1"));
            assertEquals(Float.valueOf(-6.5f), second.get("val2"));
            assertFalse(it.hasNext());
        }
    }
private static byte[] concat(byte[]... values) {
        int length = 0;
        for (byte[] value : values) {
            length += value.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        return result;
    }
static class Bean {
        public int a;

        Bean() { }

        Bean(int value) { a = value; }

        @Override
        public boolean equals(Object other) {
            return other instanceof Bean that && a == that.a;
        }

        @Override
        public int hashCode() { return a; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImplA.class, name = "A"),
            @JsonSubTypes.Type(value = ImplB.class, name = "B")
    })
    static class PolyBase { }
@JsonTypeName("A")
    static class ImplA extends PolyBase {
        public int value;

        ImplA() { }

        ImplA(int value) { this.value = value; }
    }
@JsonTypeName("B")
    static class ImplB extends PolyBase {
        public int b;

        ImplB() { }

        ImplB(int value) { b = value; }
    }
static class CloseableValue implements Closeable {
        public int x;
        public boolean closed;

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }

    void __invoke_testRootBeansVpack() throws Exception {
        try {
            testRootBeansVpack();
        } finally {
        }
    }


    void __invoke_testRootBeansInArrayVpack() throws Exception {
        try {
            testRootBeansInArrayVpack();
        } finally {
        }
    }


    void __invoke_testRootMapsVpack() throws Exception {
        try {
            testRootMapsVpack();
        } finally {
        }
    }


    void __invoke_testRootArraysWithParserVpack() throws Exception {
        try {
            testRootArraysWithParserVpack();
        } finally {
        }
    }


    void __invoke_testObjectReaderWithJsonReadFeatureFastDoubleParserVpack() throws Exception {
        try {
            testObjectReaderWithJsonReadFeatureFastDoubleParserVpack();
        } finally {
        }
    }


    void __invoke_testObjectReaderWithJsonReadFeatureFastFloatParserVpack() throws Exception {
        try {
            testObjectReaderWithJsonReadFeatureFastFloatParserVpack();
        } finally {
        }
    }

}
