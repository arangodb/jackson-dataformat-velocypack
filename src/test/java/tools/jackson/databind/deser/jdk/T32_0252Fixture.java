package tools.jackson.databind.deser.jdk;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0252Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] UNTYPED_ARRAY = VPackWireFixtureTest.hex(
            "13 11 31 18 41 78 1a 1b 00 00 00 00 00 00 00 40 05");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] EMPTY_STRING_PROPERTY = VPackWireFixtureTest.hex(
            "14 0c 47 74 68 65 6c 69 73 74 40 01");
private static final byte[] STRING_ARRAY = VPackWireFixtureTest.hex(
            "13 20 "
                    + "41 61 41 62 44 61 62 63 64 40 43 3f 3f 3f "
                    + "48 22 71 75 6f 74 65 64 22 45 6c 66 3a 20 0a 07");
private static final byte[] NULL_STRING_ARRAY = VPackWireFixtureTest.hex(
            "13 04 18 01");
private static final byte[] TRUE_STRING = VPackWireFixtureTest.hex("44 74 72 75 65");
private static final byte[] A_STRING = VPackWireFixtureTest.hex("41 61");
private static final byte[] ONE_STRING = VPackWireFixtureTest.hex("41 31");

    void testUntypedArray() throws Exception {
        Object[] result = MAPPER.readValue(UNTYPED_ARRAY, Object[].class);
        assertNotNull(result);
        assertEquals(5, result.length);
        assertEquals(Integer.valueOf(1), result[0]);
        assertNull(result[1]);
        assertEquals("x", result[2]);
        assertEquals(Boolean.TRUE, result[3]);
        assertEquals(Double.valueOf(2.0), result[4]);
    }

    void testIntegerArray() throws Exception {
        final int length = 90000;
        Integer[] result = MAPPER.readValue(indexedArray(length,
                (out, index) -> writeUnsignedInteger(out, index)), Integer[].class);
        assertNotNull(result);
        assertEquals(length, result.length);
        for (int i = 0; i < length; ++i) {
            assertEquals(i, result[i].intValue());
        }
    }

    void testFromEmptyString() throws Exception {
        ObjectReader reader = MAPPER.reader()
                .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        assertNull(reader.forType(Object[].class).readValue(EMPTY_STRING));
        assertNull(reader.forType(String[].class).readValue(EMPTY_STRING));
        assertNull(reader.forType(int[].class).readValue(EMPTY_STRING));
    }

    void testFromEmptyString2() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                        DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        Product product = mapper.readValue(EMPTY_STRING_PROPERTY, Product.class);
        assertNotNull(product);
        assertNull(product.thelist);
    }

    void testShortArray() throws Exception {
        final int length = 31001;
        short[] result = MAPPER.readValue(indexedArray(length,
                (out, index) -> writeUnsignedInteger(out, index)), short[].class);
        assertNotNull(result);
        assertEquals(length, result.length);
        for (int i = 0; i < length; ++i) {
            assertEquals((short) i, result[i]);
        }
    }

    void testIntArray() throws Exception {
        final int length = 70000;
        int[] result = MAPPER.readValue(indexedArray(length,
                (out, index) -> writeSignedInteger(out, -index)), int[].class);
        assertNotNull(result);
        assertEquals(length, result.length);
        for (int i = 0; i < length; ++i) {
            assertEquals(-i, result[i]);
        }
    }

    void testLongArray() throws Exception {
        final int length = 12300;
        long[] result = MAPPER.readValue(indexedArray(length,
                (out, index) -> writeUnsignedInteger(out, index)), long[].class);
        assertNotNull(result);
        assertEquals(length, result.length);
        for (int i = 0; i < length; ++i) {
            assertEquals((long) i, result[i]);
        }
    }

    void testDoubleArray() throws Exception {
        final int length = 7000;
        double[] result = MAPPER.readValue(indexedArray(length,
                T32_0252Fixture::writeDecimalDouble), double[].class);
        assertNotNull(result);
        assertEquals(length, result.length);
        for (int i = 0; i < length; ++i) {
            String expected = String.valueOf(i) + "." + (i % 10);
            assertEquals(expected, String.valueOf(result[i]), "entry " + i);
        }
    }

    void testFloatArray() throws Exception {
        final int length = 7000;
        float[] result = MAPPER.readValue(indexedArray(length,
                T32_0252Fixture::writeDecimalDouble), float[].class);
        assertNotNull(result);
        assertEquals(length, result.length);
        for (int i = 0; i < length; ++i) {
            String expected = String.valueOf(i) + "." + (i % 10);
            assertEquals(expected, String.valueOf(result[i]), "entry " + i);
        }
    }

    void testSingleStringToPrimitiveArray() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        assertLengthValue(mapper.readValue(TRUE_STRING, boolean[].class), true);
        assertLengthValue(mapper.readValue(A_STRING, char[].class), 'a');
        assertLengthValue(mapper.readValue(ONE_STRING, short[].class), (short) 1);
        assertLengthValue(mapper.readValue(ONE_STRING, int[].class), 1);
        assertLengthValue(mapper.readValue(ONE_STRING, long[].class), 1L);
    }

    void testStringArray() throws Exception {
        String[] expected = { "a", "b", "abcd", "", "???", "\"quoted\"", "lf: \n" };
        String[] result = MAPPER.readValue(STRING_ARRAY, String[].class);
        assertArrayEquals(expected, result);

        result = MAPPER.readValue(NULL_STRING_ARRAY, String[].class);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertNull(result[0]);
    }

    void testCustomDeserializers() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(NonDeserializable[].class, new CustomNonDeserArrayDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        NonDeserializable[] result = mapper.readValue(
                VPackWireFixtureTest.hex("13 05 41 61 01"), NonDeserializable[].class);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals("a", result[0].value);
    }
private static void assertLengthValue(boolean[] values, boolean expected) {
        assertEquals(1, values.length);
        assertEquals(expected, values[0]);
    }
private static void assertLengthValue(char[] values, char expected) {
        assertEquals(1, values.length);
        assertEquals(expected, values[0]);
    }
private static void assertLengthValue(short[] values, short expected) {
        assertEquals(1, values.length);
        assertEquals(expected, values[0]);
    }
private static void assertLengthValue(int[] values, int expected) {
        assertEquals(1, values.length);
        assertEquals(expected, values[0]);
    }
private static void assertLengthValue(long[] values, long expected) {
        assertEquals(1, values.length);
        assertEquals(expected, values[0]);
    }
private static byte[] indexedArray(int count, ElementWriter writer) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        int[] relativeOffsets = new int[count];
        for (int i = 0; i < count; ++i) {
            relativeOffsets[i] = body.size();
            writer.write(body, i);
        }

        int width = 1;
        long length;
        while (true) {
            length = 1L + 2L * width + body.size() + (long) count * width;
            if (width == 8 || length <= unsignedLimit(width)) {
                break;
            }
            width *= 2;
        }
        byte[] result = new byte[(int) length];
        result[0] = (byte) (0x06 + Integer.numberOfTrailingZeros(width));
        putLittleEndian(result, 1, width, length);
        putLittleEndian(result, 1 + width, width, count);
        int bodyStart = 1 + 2 * width;
        byte[] bodyBytes = body.toByteArray();
        System.arraycopy(bodyBytes, 0, result, bodyStart, bodyBytes.length);
        int indexStart = bodyStart + bodyBytes.length;
        for (int i = 0; i < count; ++i) {
            putLittleEndian(result, indexStart + i * width, width, bodyStart + relativeOffsets[i]);
        }
        return result;
    }
private static long unsignedLimit(int width) {
        return width == 4 ? 0xffff_ffffL : (1L << (width * 8)) - 1L;
    }
private static void writeUnsignedInteger(ByteArrayOutputStream out, int value) {
        if (value <= 9) {
            out.write(0x30 + value);
        } else {
            int width = value <= 0xff ? 1 : value <= 0xffff ? 2 : value <= 0xffff_ffffL ? 4 : 8;
            out.write(0x28 + width - 1);
            putLittleEndian(out, width, value);
        }
    }
private static void writeSignedInteger(ByteArrayOutputStream out, int value) {
        if (value >= -6 && value <= -1) {
            out.write(0x40 + value);
            return;
        }
        if (value >= 0) {
            writeUnsignedInteger(out, value);
            return;
        }
        int width = value >= -0x80 ? 1 : value >= -0x8000 ? 2 : 4;
        out.write(0x20 + width - 1);
        putLittleEndian(out, width, value);
    }
private static void writeDecimalDouble(ByteArrayOutputStream out, int value) {
        out.write(0x1b);
        long bits = Double.doubleToRawLongBits(value + (value % 10) / 10.0d);
        putLittleEndian(out, 8, bits);
    }
private static void putLittleEndian(byte[] target, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
private static void putLittleEndian(ByteArrayOutputStream target, int width, long value) {
        for (int i = 0; i < width; ++i) {
            target.write((int) (value >>> (8 * i)) & 0xff);
        }
    }
@FunctionalInterface
    private interface ElementWriter {
        void write(ByteArrayOutputStream out, int index);
    }
private static final class Product {
        public List<String> thelist;
    }
private static final class CustomNonDeserArrayDeserializer
            extends ValueDeserializer<NonDeserializable[]> {
        @Override
        public NonDeserializable[] deserialize(JsonParser parser, DeserializationContext ctxt) {
            List<NonDeserializable> values = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                values.add(new NonDeserializable(parser.getString()));
            }
            return values.toArray(new NonDeserializable[0]);
        }
    }
private static final class NonDeserializable {
        private final String value;

        private NonDeserializable(String value) {
            this.value = value;
        }
    }

    void __invoke_testUntypedArray() throws Exception {
        try {
            testUntypedArray();
        } finally {
        }
    }


    void __invoke_testIntegerArray() throws Exception {
        try {
            testIntegerArray();
        } finally {
        }
    }


    void __invoke_testFromEmptyString() throws Exception {
        try {
            testFromEmptyString();
        } finally {
        }
    }


    void __invoke_testFromEmptyString2() throws Exception {
        try {
            testFromEmptyString2();
        } finally {
        }
    }


    void __invoke_testShortArray() throws Exception {
        try {
            testShortArray();
        } finally {
        }
    }


    void __invoke_testIntArray() throws Exception {
        try {
            testIntArray();
        } finally {
        }
    }


    void __invoke_testLongArray() throws Exception {
        try {
            testLongArray();
        } finally {
        }
    }


    void __invoke_testDoubleArray() throws Exception {
        try {
            testDoubleArray();
        } finally {
        }
    }


    void __invoke_testFloatArray() throws Exception {
        try {
            testFloatArray();
        } finally {
        }
    }


    void __invoke_testSingleStringToPrimitiveArray() throws Exception {
        try {
            testSingleStringToPrimitiveArray();
        } finally {
        }
    }


    void __invoke_testStringArray() throws Exception {
        try {
            testStringArray();
        } finally {
        }
    }


    void __invoke_testCustomDeserializers() throws Exception {
        try {
            testCustomDeserializers();
        } finally {
        }
    }

}
