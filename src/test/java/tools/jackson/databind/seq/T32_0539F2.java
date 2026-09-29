package tools.jackson.databind.seq;

import java.util.Iterator;
import java.util.Map;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MappingIterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0539F2 {
private static final VPackMapper MAPPER = new VPackMapper();
private static final byte[] BEAN_A3 = {
            0x14, 0x06, 0x41, 0x61, 0x33, 0x01
    };
private static final byte[] BEAN_A1_B2 = {
            0x14, 0x09, 0x41, 0x61, 0x31, 0x41, 0x62, 0x32, 0x02
    };
private static final byte[] BEAN_A27 = {
            0x14, 0x07, 0x41, 0x61, 0x28, 0x1B, 0x01
    };
private static final byte[] BEAN_A27_UNKNOWN = {
            0x14, 0x18,
            0x41, 0x61, 0x28, 0x1B,
            0x43, 0x66, 0x6F, 0x6F,
            0x13, 0x05, 0x31, 0x32, 0x02,
            0x41, 0x62,
            0x14, 0x06, 0x41, 0x78, 0x33, 0x01,
            0x03
    };
private static final byte[] BEAN_A1_B2_SEQUENCE = concat(
            BEAN_A3, BEAN_A27_UNKNOWN, BEAN_A1_B2);
private static final byte[] BEAN_RECOVERY_ARRAY = {
            0x13, 0x2A,
            0x14, 0x06, 0x41, 0x61, 0x33, 0x01,
            0x14, 0x18,
            0x41, 0x61, 0x28, 0x1B,
            0x43, 0x66, 0x6F, 0x6F,
            0x13, 0x05, 0x31, 0x32, 0x02,
            0x41, 0x62,
            0x14, 0x06, 0x41, 0x78, 0x33, 0x01,
            0x03,
            0x14, 0x09, 0x41, 0x61, 0x31, 0x41, 0x62, 0x32, 0x02,
            0x03
    };
private static final byte[] BEAN_CONTAINER = {
            0x14, 0x18, 0x44, 0x6C, 0x65, 0x61, 0x66,
            0x13, 0x10,
            0x14, 0x06, 0x41, 0x61, 0x33, 0x01,
            0x14, 0x07, 0x41, 0x61, 0x28, 0x1B, 0x01,
            0x02,
            0x01
    };
private static final byte[] BEAN_A3_A27_ARRAY = {
            0x13, 0x10,
            0x14, 0x06, 0x41, 0x61, 0x33, 0x01,
            0x14, 0x07, 0x41, 0x61, 0x28, 0x1B, 0x01,
            0x02
    };
private static final byte[] TREE_SEQUENCE = {
            0x14, 0x0F, 0x42, 0x69, 0x64, 0x31,
            0x45, 0x76, 0x61, 0x6C, 0x75, 0x65, 0x28, (byte) 0x89, 0x02,
            0x14, 0x10, 0x42, 0x69, 0x64, 0x32,
            0x45, 0x76, 0x61, 0x6C, 0x75, 0x65, 0x29, 0x00, 0x01, 0x02,
            0x14, 0x0F, 0x42, 0x69, 0x64, 0x33,
            0x45, 0x76, 0x61, 0x6C, 0x75, 0x65, 0x20, (byte) 0xA7, 0x02
    };
private static final byte[] TREE_FAILURE_SEQUENCE = {
            0x14, 0x0F, 0x42, 0x69, 0x64, 0x31,
            0x45, 0x76, 0x61, 0x6C, 0x75, 0x65, 0x28, (byte) 0x89, 0x02,
            0x14, 0x14, 0x42, 0x69, 0x64, 0x32,
            0x45, 0x76, 0x61, 0x6C, 0x75, 0x65, 0x46,
            0x66, 0x6F, 0x6F, 0x62, 0x61, 0x72, 0x02,
            0x14, 0x0F, 0x42, 0x69, 0x64, 0x33,
            0x45, 0x76, 0x61, 0x6C, 0x75, 0x65, 0x20, (byte) 0xA7, 0x02
    };
private static final byte[] SIMPLE_ARRAY = { 0x02, 0x04, 0x31, 0x33 };
private static final byte[] NESTED_ARRAY = {
            0x02, 0x08, 0x02, 0x03, 0x31, 0x02, 0x03, 0x33
    };
private static final byte[] TWO_MAPS = {
            0x13, 0x2F,
            0x14, 0x16, 0x42, 0x68, 0x69, 0x42, 0x68, 0x6F,
            0x48, 0x6E, 0x65, 0x69, 0x67, 0x68, 0x62, 0x6F, 0x72,
            0x43, 0x4A, 0x6F, 0x65, 0x02,
            0x14, 0x16, 0x43, 0x62, 0x6F, 0x79, 0x45, 0x68, 0x6F,
            0x77, 0x64, 0x79, 0x43, 0x68, 0x75, 0x68, 0x44,
            0x77, 0x68, 0x61, 0x74, 0x02,
            0x02
    };

    void testEmptyIteratorVpack() {
        MappingIterator<Object> empty = MappingIterator.emptyIterator();
        assertFalse(empty.hasNext());
        assertFalse(empty.hasNextValue());
        empty.close();
    }

    void testHasNextWithEndArrayVpack() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(SIMPLE_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            parser.nextToken();
            MappingIterator<Integer> it = MAPPER.readerFor(Integer.class).readValues(parser);
            assertTrue(it.hasNext());
            assertEquals(1, it.next());
            assertTrue(it.hasNext());
            assertEquals(3, it.next());
            assertFalse(it.hasNext());
            assertFalse(it.hasNext());
        }
    }

    void testHasNextWithEndArrayManagedParserVpack() throws Exception {
        try (MappingIterator<Integer> it = MAPPER.readerFor(Integer.class)
                .readValues(SIMPLE_ARRAY)) {
            assertTrue(it.hasNext());
            assertEquals(1, it.next());
            assertTrue(it.hasNext());
            assertEquals(3, it.next());
            assertFalse(it.hasNext());
            assertFalse(it.hasNext());
        }
    }

    void testNonRootArraysUsingParserVpack() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(NESTED_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            Iterator<int[]> it = MAPPER.readValues(parser, int[].class);
            assertTrue(it.hasNext());
            int[] array = it.next();
            assertEquals(1, array.length);
            assertEquals(1, array[0]);
            assertTrue(it.hasNext());
            array = it.next();
            assertEquals(1, array.length);
            assertEquals(3, array[0]);
            assertFalse(it.hasNext());
        }
    }

    void testNonRootBeansVpack() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(BEAN_CONTAINER)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            Iterator<Bean> it = MAPPER.readerFor(Bean.class).readValues(parser);
            assertTrue(it.hasNext());
            Bean bean = it.next();
            assertEquals(3, bean.a);
            assertTrue(it.hasNext());
            bean = it.next();
            assertEquals(27, bean.a);
            assertFalse(it.hasNext());
        }
    }

    void testNonRootMapsWithObjectReaderVpack() throws Exception {
        try (MappingIterator<Map<String, Object>> it = MAPPER.reader()
                .forType(new TypeReference<Map<String, Object>>() { })
                .readValues(TWO_MAPS)) {
            assertTrue(it.hasNext());
            assertEquals(2, it.nextValue().size());
            assertTrue(it.hasNext());
            assertEquals(2, it.nextValue().size());
            assertFalse(it.hasNext());
        }
    }

    void testNonRootMapsWithParserVpack() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(BEAN_A3_A27_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            parser.clearCurrentToken();
            Iterator<Map<?, ?>> it = MAPPER.readerFor(Map.class).readValues(parser);
            assertTrue(it.hasNext());
            Map<?, ?> map = it.next();
            assertEquals(1, map.size());
            assertEquals(Integer.valueOf(3), map.get("a"));
            assertTrue(it.hasNext());
            map = it.next();
            assertEquals(1, map.size());
            assertEquals(Integer.valueOf(27), map.get("a"));
            assertFalse(it.hasNext());
        }
    }
private MappingIterator<Bean> reader(byte[] input) throws Exception {
        return MAPPER.readerFor(Bean.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValues(input);
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
        public int a, b;
    }
static class IdValue {
        public int id, value;
    }

    void __invoke_testEmptyIteratorVpack() throws Exception {
        try {
            testEmptyIteratorVpack();
        } finally {
        }
    }


    void __invoke_testHasNextWithEndArrayVpack() throws Exception {
        try {
            testHasNextWithEndArrayVpack();
        } finally {
        }
    }


    void __invoke_testHasNextWithEndArrayManagedParserVpack() throws Exception {
        try {
            testHasNextWithEndArrayManagedParserVpack();
        } finally {
        }
    }


    void __invoke_testNonRootArraysUsingParserVpack() throws Exception {
        try {
            testNonRootArraysUsingParserVpack();
        } finally {
        }
    }


    void __invoke_testNonRootBeansVpack() throws Exception {
        try {
            testNonRootBeansVpack();
        } finally {
        }
    }


    void __invoke_testNonRootMapsWithObjectReaderVpack() throws Exception {
        try {
            testNonRootMapsWithObjectReaderVpack();
        } finally {
        }
    }


    void __invoke_testNonRootMapsWithParserVpack() throws Exception {
        try {
            testNonRootMapsWithParserVpack();
        } finally {
        }
    }

}
