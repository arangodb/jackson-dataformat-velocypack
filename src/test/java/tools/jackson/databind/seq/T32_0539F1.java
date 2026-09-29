package tools.jackson.databind.seq;

import java.util.List;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.exc.InvalidFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0539F1 {
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

    void testReadTreeSequenceVpack() throws Exception {
        try (MappingIterator<JsonNode> it = MAPPER.readerFor(JsonNode.class)
                .readValues(TREE_SEQUENCE)) {
            assertTrue(it.hasNextValue());
            JsonNode node = it.nextValue();
            assertEquals("{\"id\":1,\"value\":137}", node.toString());
            assertEquals(1, node.path("id").intValue());
            assertEquals(1, node.path("id").asInt());
            assertTrue(it.hasNextValue());
            node = it.nextValue();
            assertEquals("{\"id\":2,\"value\":256}", node.toString());
            assertTrue(it.hasNextValue());
            node = it.nextValue();
            assertEquals("{\"id\":3,\"value\":-89}", node.toString());
            assertFalse(it.hasNextValue());
        }

        try (MappingIterator<JsonNode> it = MAPPER.readerFor(JsonNode.class)
                .readValues(TREE_SEQUENCE)) {
            List<JsonNode> all = it.readAll();
            assertEquals(3, all.size());
            assertEquals("{\"id\":3,\"value\":-89}", all.get(2).toString());
        }
    }

    void testReadPOJOHandleFailVpack() throws Exception {
        try (MappingIterator<IdValue> it = MAPPER.readerFor(IdValue.class)
                .readValues(TREE_FAILURE_SEQUENCE)) {
            assertTrue(it.hasNextValue());
            IdValue value = it.nextValue();
            assertEquals(137, value.value);
            assertTrue(it.hasNextValue());
            try {
                it.nextValue();
                fail("Should catch the problem");
            } catch (InvalidFormatException e) {
                assertTrue(e.getMessage().contains("Cannot deserialize value"));
            }
            assertTrue(it.hasNextValue());
            value = it.nextValue();
            assertEquals(-89, value.value);
            assertFalse(it.hasNextValue());
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

    void __invoke_testReadTreeSequenceVpack() throws Exception {
        try {
            testReadTreeSequenceVpack();
        } finally {
        }
    }


    void __invoke_testReadPOJOHandleFailVpack() throws Exception {
        try {
            testReadPOJOHandleFailVpack();
        } finally {
        }
    }

}
