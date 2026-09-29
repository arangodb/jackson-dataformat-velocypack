package tools.jackson.databind.seq;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0539F0 {
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

    void testRootBeansVpack() throws Exception {
        try (MappingIterator<Bean> it = reader( concat(BEAN_A3,
                new byte[] { 0x14, 0x06, 0x41, 0x78, 0x35, 0x01 }))) {
            assertTrue(it.hasNextValue());
            Bean bean = it.nextValue();
            assertEquals(3, bean.a);
            try {
                it.nextValue();
                fail("Should not have succeeded");
            } catch (UnrecognizedPropertyException e) {
                assertTrue(e.getMessage().contains("Unrecognized property \"x\""));
            }
            assertFalse(it.hasNextValue());
        }
    }

    void testSimpleRootRecoveryVpack() throws Exception {
        try (MappingIterator<Bean> it = reader(BEAN_A1_B2_SEQUENCE)) {
            Bean bean = it.nextValue();
            assertNotNull(bean);
            assertEquals(3, bean.a);
            try {
                it.nextValue();
                fail("Should reject the unknown structured property");
            } catch (UnrecognizedPropertyException e) {
                assertTrue(e.getMessage().contains("Unrecognized property \"foo\""));
            }
            bean = it.nextValue();
            assertNotNull(bean);
            assertEquals(1, bean.a);
            assertEquals(2, bean.b);
            assertFalse(it.hasNextValue());
        }
    }

    void testSimpleArrayRecoveryVpack() throws Exception {
        try (MappingIterator<Bean> it = reader(BEAN_RECOVERY_ARRAY)) {
            Bean bean = it.nextValue();
            assertNotNull(bean);
            assertEquals(3, bean.a);
            try {
                it.nextValue();
                fail("Should reject the unknown structured property");
            } catch (UnrecognizedPropertyException e) {
                assertTrue(e.getMessage().contains("Unrecognized property \"foo\""));
            }
            bean = it.nextValue();
            assertNotNull(bean);
            assertEquals(1, bean.a);
            assertEquals(2, bean.b);
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

    void __invoke_testRootBeansVpack() throws Exception {
        try {
            testRootBeansVpack();
        } finally {
        }
    }


    void __invoke_testSimpleRootRecoveryVpack() throws Exception {
        try {
            testSimpleRootRecoveryVpack();
        } finally {
        }
    }


    void __invoke_testSimpleArrayRecoveryVpack() throws Exception {
        try {
            testSimpleArrayRecoveryVpack();
        } finally {
        }
    }

}
