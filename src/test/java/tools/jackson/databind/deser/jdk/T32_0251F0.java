package tools.jackson.databind.deser.jdk;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0251F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SINGLE_STRING_QUEUE = VPackWireFixtureTest.hex(
            "13 09 45 68 65 6c 6c 6f 01");
private static final byte[] STRING_QUEUE = VPackWireFixtureTest.hex(
            "13 09 41 61 41 62 41 63 03");
private static final byte[] POJO_QUEUE = VPackWireFixtureTest.hex(
            "13 2e "
                    + "14 15 44 6e 61 6d 65 45 66 69 72 73 74 "
                    + "45 76 61 6c 75 65 31 02 "
                    + "14 16 44 6e 61 6d 65 46 73 65 63 6f 6e 64 "
                    + "45 76 61 6c 75 65 32 02 02");
private static final byte[] BOOLEAN_ARRAY = VPackWireFixtureTest.hex(
            "13 06 1a 19 19 03");
private static final byte[] BINARY_BYTES = VPackWireFixtureTest.hex(
            "c0 04 00 01 02 03");
private static final byte[] NUMERIC_BYTE_ARRAY = VPackWireFixtureTest.hex(
            "13 10 20 80 3f 30 39 28 0a 28 7f 28 80 28 ff 08");
private static final byte[] OVERRIDDEN_BINARY_PROPERTY = VPackWireFixtureTest.hex(
            "14 13 49 73 6f 6d 65 42 79 74 65 73 c0 04 01 02 03 04 01");
private static final byte[] BINARY_ARRAY = VPackWireFixtureTest.hex(
            "13 0a c0 01 61 c0 02 62 63 02");
private static final byte[] BEAN_ARRAY = VPackWireFixtureTest.hex(
            "13 31 "
                    + "14 1b 41 78 31 41 79 32 45 62 65 61 6e 73 "
                    + "13 0c 41 61 46 66 6f 6f 62 61 72 02 03 "
                    + "14 13 41 78 34 41 79 35 45 62 65 61 6e 73 "
                    + "13 04 18 01 03 02");
private static final byte[] CHAR_STRING = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] CHAR_ARRAY = VPackWireFixtureTest.hex(
            "13 09 41 61 41 62 41 63 03");

    void testSingleElementQueue() throws Exception {
        ArrayBlockingQueue<String> result = MAPPER.readValue(SINGLE_STRING_QUEUE,
                new TypeReference<ArrayBlockingQueue<String>>() { });
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hello", result.poll());
    }

    void testQueueWithStrings() throws Exception {
        ArrayBlockingQueue<String> result = MAPPER.readValue(STRING_QUEUE,
                new TypeReference<ArrayBlockingQueue<String>>() { });
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("a", result.poll());
        assertEquals("b", result.poll());
        assertEquals("c", result.poll());
    }

    void testQueueWithPojo() throws Exception {
        ArrayBlockingQueue<QueuePojoItem> result = MAPPER.readValue(POJO_QUEUE,
                new TypeReference<ArrayBlockingQueue<QueuePojoItem>>() { });
        assertNotNull(result);
        assertEquals(2, result.size());
        QueuePojoItem first = result.poll();
        assertEquals("first", first.name);
        assertEquals(1, first.value);
        QueuePojoItem second = result.poll();
        assertEquals("second", second.name);
        assertEquals(2, second.value);
    }

    void testRoundTrip() throws Exception {
        ArrayBlockingQueue<String> original = new ArrayBlockingQueue<>(5);
        original.add("alpha");
        original.add("beta");
        original.add("gamma");

        byte[] encoded = MAPPER.writeValueAsBytes(original);
        ArrayBlockingQueue<String> result = MAPPER.readValue(encoded,
                new TypeReference<ArrayBlockingQueue<String>>() { });
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("alpha", result.poll());
        assertEquals("beta", result.poll());
        assertEquals("gamma", result.poll());
    }
public static class QueuePojoItem {
        public String name;
        public int value;
    }
public static final class ArrayBean1 {
        private int x;
        private int y;
        private List<ArrayBean2> beans;

        private ArrayBean1() { }

        ArrayBean1(int x, int y, List<ArrayBean2> beans) {
            this.x = x;
            this.y = y;
            this.beans = beans;
        }

        public int getX() { return x; }
        public int getY() { return y; }
        public List<ArrayBean2> getBeans() { return beans; }
        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
        public void setBeans(List<ArrayBean2> beans) { this.beans = beans; }

        @Override
        public boolean equals(Object value) {
            if (!(value instanceof ArrayBean1 other)) {
                return false;
            }
            return x == other.x && y == other.y && beans.equals(other.beans);
        }
    }
public static final class ArrayBean2 {
        private final String description;

        public ArrayBean2(String description) {
            this.description = description;
        }

        @Override
        public boolean equals(Object value) {
            return value instanceof ArrayBean2 other
                    && description.equals(other.description);
        }
    }
static class HiddenBinaryBean890 {
        @JsonDeserialize(as = byte[].class)
        public Object someBytes;
    }

    void __invoke_testSingleElementQueue() throws Exception {
        try {
            testSingleElementQueue();
        } finally {
        }
    }


    void __invoke_testQueueWithStrings() throws Exception {
        try {
            testQueueWithStrings();
        } finally {
        }
    }


    void __invoke_testQueueWithPojo() throws Exception {
        try {
            testQueueWithPojo();
        } finally {
        }
    }


    void __invoke_testRoundTrip() throws Exception {
        try {
            testRoundTrip();
        } finally {
        }
    }

}
