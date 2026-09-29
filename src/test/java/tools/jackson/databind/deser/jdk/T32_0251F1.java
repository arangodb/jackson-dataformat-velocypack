package tools.jackson.databind.deser.jdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0251F1 {
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

    void testBeanArray() throws Exception {
        ArrayBean1 first = new ArrayBean1(1, 2,
                new ArrayList<>(List.of(new ArrayBean2("a"), new ArrayBean2("foobar"))));
        ArrayBean1 second = new ArrayBean1(4, 5,
                new ArrayList<>(Arrays.asList((ArrayBean2) null)));

        List<ArrayBean1> result = MAPPER.readValue(BEAN_ARRAY,
                new TypeReference<List<ArrayBean1>>() { });
        assertEquals(Arrays.asList(first, second), result);
    }

    void testBooleanArray() throws Exception {
        boolean[] result = MAPPER.readValue(BOOLEAN_ARRAY, boolean[].class);
        assertArrayEquals(new boolean[] { true, false, false }, result);
    }

    void testByteArrayAsBase64() throws Exception {
        byte[] result = MAPPER.readValue(BINARY_BYTES, byte[].class);
        assertArrayEquals(new byte[] { 0, 1, 2, 3 }, result);
    }

    void testByteArrayAsNumbers() throws Exception {
        byte[] result = MAPPER.readValue(NUMERIC_BYTE_ARRAY, byte[].class);
        assertArrayEquals(new byte[] { -128, -1, 0, 9, 10, 127, -128, -1 }, result);
    }

    void testByteArrayTypeOverride890() throws Exception {
        HiddenBinaryBean890 result = MAPPER.readValue(OVERRIDDEN_BINARY_PROPERTY,
                HiddenBinaryBean890.class);
        assertNotNull(result);
        assertNotNull(result.someBytes);
        assertEquals(byte[].class, result.someBytes.getClass());
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, (byte[]) result.someBytes);
    }

    void testByteArraysAsBase64() throws Exception {
        byte[][] result = MAPPER.readValue(BINARY_ARRAY, byte[][].class);
        assertArrayEquals(new byte[][] { { 'a' }, { 'b', 'c' } }, result);
    }

    void testByteArraysWith763() throws Exception {
        String[] input = { "YQ==", "Yg==", "Yw==" };
        byte[][] result = MAPPER.convertValue(input, byte[][].class);
        assertEquals("a", new String(result[0], java.nio.charset.StandardCharsets.US_ASCII));
        assertEquals("b", new String(result[1], java.nio.charset.StandardCharsets.US_ASCII));
        assertEquals("c", new String(result[2], java.nio.charset.StandardCharsets.US_ASCII));
    }

    void testCharArray() throws Exception {
        char[] fromString = MAPPER.readValue(CHAR_STRING, char[].class);
        assertEquals("abc", new String(fromString));

        char[] fromArray = MAPPER.readValue(CHAR_ARRAY, char[].class);
        assertEquals("abc", new String(fromArray));
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

    void __invoke_testBeanArray() throws Exception {
        try {
            testBeanArray();
        } finally {
        }
    }


    void __invoke_testBooleanArray() throws Exception {
        try {
            testBooleanArray();
        } finally {
        }
    }


    void __invoke_testByteArrayAsBase64() throws Exception {
        try {
            testByteArrayAsBase64();
        } finally {
        }
    }


    void __invoke_testByteArrayAsNumbers() throws Exception {
        try {
            testByteArrayAsNumbers();
        } finally {
        }
    }


    void __invoke_testByteArrayTypeOverride890() throws Exception {
        try {
            testByteArrayTypeOverride890();
        } finally {
        }
    }


    void __invoke_testByteArraysAsBase64() throws Exception {
        try {
            testByteArraysAsBase64();
        } finally {
        }
    }


    void __invoke_testByteArraysWith763() throws Exception {
        try {
            testByteArraysWith763();
        } finally {
        }
    }


    void __invoke_testCharArray() throws Exception {
        try {
            testCharArray();
        } finally {
        }
    }

}
