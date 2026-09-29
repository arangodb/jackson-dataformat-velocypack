package tools.jackson.databind.deser.merge;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0284Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();
private static final ObjectMapper STRICT_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();

    // Provenance: ArrayMergeTest#testObjectArrayMerging().
    void testObjectArrayMergingVpack() throws Exception {
        MergedX<Object[]> input = new MergedX<>(new Object[] { "foo" });
        ObjectReader reader = MAPPER.readerFor(new TypeReference<MergedX<Object[]>>() { })
                .withValueToUpdate(input);
        MergedX<?> result = reader.readValue(holder(arrayOfString("bar")));
        assertSame(input, result);
        assertArrayEquals(new Object[] { "foo", "bar" }, input.value);

        result = MAPPER.readerFor(new TypeReference<MergedX<Object[]>>() { })
                .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .withValueToUpdate(input)
                .readValue(holder(string("zap")));
        assertSame(input, result);
        assertArrayEquals(new Object[] { "foo", "bar", "zap" }, input.value);
    }

    // Provenance: ArrayMergeTest#testComponentTypeArrayMerging().
    void testComponentTypeArrayMergingVpack() throws Exception {
        Merged4121 input = new Merged4121();
        input.value = new Date[] { new Date(1000L) };
        Merged4121 result = MAPPER.readerFor(Merged4121.class)
                .withValueToUpdate(input)
                .readValue(holder(arrayOfNumber(2000)));
        assertSame(input, result);
        assertEquals(2, input.value.length);
        assertEquals(1000L, input.value[0].getTime());
        assertEquals(2000L, input.value[1].getTime());

        result = MAPPER.readerFor(Merged4121.class)
                .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .withValueToUpdate(input)
                .readValue(holder(number(3000)));
        assertSame(input, result);
        assertEquals(3, input.value.length);
        assertEquals(3000L, input.value[2].getTime());
    }

    // Provenance: ArrayMergeTest#testStringArrayMerging().
    void testStringArrayMergingVpack() throws Exception {
        MergedX<String[]> input = new MergedX<>(new String[] { "foo" });
        MergedX<?> result = MAPPER.readerFor(new TypeReference<MergedX<String[]>>() { })
                .withValueToUpdate(input)
                .readValue(holder(arrayOfString("bar")));
        assertSame(input, result);
        assertArrayEquals(new String[] { "foo", "bar" }, input.value);
    }

    // Provenance: ArrayMergeTest#testBooleanArrayMerging().
    void testBooleanArrayMergingVpack() throws Exception {
        MergedX<boolean[]> input = new MergedX<>(new boolean[] { true, false });
        MergedX<?> result = MAPPER.readerFor(new TypeReference<MergedX<boolean[]>>() { })
                .withValueToUpdate(input)
                .readValue(holder(arrayOfBoolean(true)));
        assertSame(input, result);
        assertArrayEquals(new boolean[] { true, false, true }, input.value);
    }

    // Provenance: ArrayMergeTest#testByteArrayMerging().
    void testByteArrayMergingVpack() throws Exception {
        MergedX<byte[]> input = new MergedX<>(new byte[] { 1, 2 });
        MergedX<?> result = MAPPER.readerFor(new TypeReference<MergedX<byte[]>>() { })
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .withValueToUpdate(input)
                .readValue(holder(byteArrayUpdate()));
        assertSame(input, result);
        assertArrayEquals(new byte[] { 1, 2, 4, 6, 0 }, input.value);
    }

    // Provenance: ArrayMergeTest#testShortArrayMerging().
    void testShortArrayMergingVpack() throws Exception {
        MergedX<short[]> input = new MergedX<>(new short[] { 1, 2 });
        MergedX<?> result = MAPPER.readerFor(new TypeReference<MergedX<short[]>>() { })
                .withValueToUpdate(input)
                .readValue(holder(arrayOfShorts(4, 6)));
        assertSame(input, result);
        assertArrayEquals(new short[] { 1, 2, 4, 6 }, input.value);
    }

    // Provenance: ArrayMergeTest#testCharArrayMerging().
    void testCharArrayMergingVpack() throws Exception {
        MergedX<char[]> input = new MergedX<>(new char[] { 'a', 'b' });
        MergedX<?> result = MAPPER.readerFor(new TypeReference<MergedX<char[]>>() { })
                .withValueToUpdate(input)
                .readValue(holder(arrayOfString("c")));
        assertSame(input, result);
        assertArrayEquals(new char[] { 'a', 'b', 'c' }, input.value);

        input = new MergedX<>(new char[] { });
        result = MAPPER.readerFor(new TypeReference<MergedX<char[]>>() { })
                .withValueToUpdate(input)
                .readValue(holder(arrayOfString("c")));
        assertSame(input, result);
        assertArrayEquals(new char[] { 'c' }, input.value);
    }

    // Provenance: ArrayMergeTest#testIntArrayMerging().
    void testIntArrayMergingVpack() throws Exception {
        MergedX<int[]> input = new MergedX<>(new int[] { 1, 2 });
        MergedX<?> result = MAPPER.readerFor(new TypeReference<MergedX<int[]>>() { })
                .withValueToUpdate(input)
                .readValue(holder(arrayOfShorts(4, 6)));
        assertSame(input, result);
        assertArrayEquals(new int[] { 1, 2, 4, 6 }, input.value);

        input = new MergedX<>(new int[] { 3, 4, 6 });
        result = MAPPER.readerFor(new TypeReference<MergedX<int[]>>() { })
                .withValueToUpdate(input)
                .readValue(holder(emptyArray()));
        assertSame(input, result);
        assertArrayEquals(new int[] { 3, 4, 6 }, input.value);
    }

    // Provenance: ArrayMergeTest#testLongArrayMerging().
    void testLongArrayMergingVpack() throws Exception {
        MergedX<long[]> input = new MergedX<>(new long[] { 1, 2 });
        MergedX<?> result = MAPPER.readerFor(new TypeReference<MergedX<long[]>>() { })
                .withValueToUpdate(input)
                .readValue(holder(arrayOfShorts(4, 6)));
        assertSame(input, result);
        assertArrayEquals(new long[] { 1, 2, 4, 6 }, input.value);
    }
private static byte[] holder(byte[] value) {
        byte[] key = VPackWireFixtureTest.hex("45 76 61 6c 75 65");
        byte[] body = concat(key, value);
        return compactObject(body, 1);
    }
private static byte[] recordWithNumber(byte[] number) {
        byte[] body = concat(VPackWireFixtureTest.hex("41 73 41 73"),
                VPackWireFixtureTest.hex("41 6e"), number,
                VPackWireFixtureTest.hex("41 69 31"));
        return compactObject(body, 3);
    }
private static byte[] compactObject(byte[] body, int count) {
        byte[] countBytes = reverseVarint(count);
        int length = 1 + 1 + body.length + countBytes.length;
        byte[] lengthBytes = forwardVarint(length);
        length = 1 + lengthBytes.length + body.length + countBytes.length;
        lengthBytes = forwardVarint(length);
        return concat(new byte[] { 0x14 }, lengthBytes, body, countBytes);
    }
private static byte[] arrayOfString(String value) {
        byte[] text = new byte[1 + value.length()];
        text[0] = (byte) (0x40 + value.length());
        for (int i = 0; i < value.length(); ++i) text[i + 1] = (byte) value.charAt(i);
        return compactArray(text, 1);
    }
private static byte[] arrayOfBoolean(boolean value) {
        return compactArray(new byte[] { (byte) (value ? 0x1a : 0x19) }, 1);
    }
private static byte[] arrayOfNumber(int value) {
        return compactArray(number(value), 1);
    }
private static byte[] arrayOfShorts(int first, int second) {
        return compactArray(new byte[] { (byte) (0x30 + first), (byte) (0x30 + second) }, 2);
    }
private static byte[] byteArrayUpdate() {
        return compactArray(new byte[] { 0x34, 0x1b, 0, 0, 0, 0, 0, 0, 0x18, 0x40, 0x18 }, 3);
    }
private static byte[] emptyArray() {
        return new byte[] { 0x01 };
    }
private static byte[] string(String value) {
        return new byte[] { (byte) (0x40 + value.length()), (byte) value.charAt(0),
                (byte) value.charAt(1), (byte) value.charAt(2) };
    }
private static byte[] number(int value) {
        if (value < 10) return new byte[] { (byte) (0x30 + value) };
        if (value <= 0xff) return new byte[] { 0x28, (byte) value };
        return new byte[] { 0x29, (byte) value, (byte) (value >>> 8) };
    }
private static byte[] compactArray(byte[] body, int count) {
        byte[] countBytes = reverseVarint(count);
        int length = 1 + 1 + body.length + countBytes.length;
        byte[] lengthBytes = forwardVarint(length);
        length = 1 + lengthBytes.length + body.length + countBytes.length;
        lengthBytes = forwardVarint(length);
        return concat(new byte[] { 0x13 }, lengthBytes, body, countBytes);
    }
private static byte[] bigIntegerBcd(int digits) {
        int mantissaLength = (digits + 1) / 2;
        byte[] result = new byte[1 + 2 + 4 + mantissaLength];
        result[0] = (byte) (mantissaLength < 256 ? 0xc8 : 0xc9);
        result[1] = (byte) mantissaLength;
        result[2] = (byte) (mantissaLength >>> 8);
        for (int i = 0; i < mantissaLength; ++i) {
            result[7 + i] = (byte) ((i == 0 && (digits & 1) != 0) ? 0x09 : 0x99);
        }
        return result;
    }
private static byte[] forwardVarint(int value) {
        byte[] result = new byte[8];
        int length = 0;
        do {
            int group = value & 0x7f;
            value >>>= 7;
            result[length++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        byte[] exact = new byte[length];
        System.arraycopy(result, 0, exact, 0, length);
        return exact;
    }
private static byte[] reverseVarint(int value) {
        byte[] forward = forwardVarint(value);
        for (int i = 0, j = forward.length - 1; i < j; ++i, --j) {
            byte swap = forward[i];
            forward[i] = forward[j];
            forward[j] = swap;
        }
        return forward;
    }
private static byte[] concat(byte[]... parts) {
        int length = 0;
        for (byte[] part : parts) length += part.length;
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }
static class ExtractFields {
        public String s;
        public int i;
    }
static class UnwrappedWithNumber {
        @JsonUnwrapped
        public Values values;

        static class Values {
            public String s;
            public int i;
            public Number n;
        }
    }
static class UnwrappedWithBigDecimal {
        @JsonUnwrapped
        public Values values;

        static class Values {
            public String s;
            public int i;
            public BigDecimal n;
        }
    }
static class UnwrappedWithDouble {
        @JsonUnwrapped
        public Values values;

        static class Values {
            public String s;
            public int i;
            public double n;
        }
    }
static class MergedX<T> {
        @JsonMerge(OptBoolean.TRUE)
        public T value;

        MergedX() { }
        MergedX(T value) { this.value = value; }
    }
static class Merged4121 {
        @JsonMerge(OptBoolean.TRUE)
        public Date[] value;
    }

    void __invoke_testObjectArrayMergingVpack() throws Exception {
        try {
            testObjectArrayMergingVpack();
        } finally {
        }
    }


    void __invoke_testComponentTypeArrayMergingVpack() throws Exception {
        try {
            testComponentTypeArrayMergingVpack();
        } finally {
        }
    }


    void __invoke_testStringArrayMergingVpack() throws Exception {
        try {
            testStringArrayMergingVpack();
        } finally {
        }
    }


    void __invoke_testBooleanArrayMergingVpack() throws Exception {
        try {
            testBooleanArrayMergingVpack();
        } finally {
        }
    }


    void __invoke_testByteArrayMergingVpack() throws Exception {
        try {
            testByteArrayMergingVpack();
        } finally {
        }
    }


    void __invoke_testShortArrayMergingVpack() throws Exception {
        try {
            testShortArrayMergingVpack();
        } finally {
        }
    }


    void __invoke_testCharArrayMergingVpack() throws Exception {
        try {
            testCharArrayMergingVpack();
        } finally {
        }
    }


    void __invoke_testIntArrayMergingVpack() throws Exception {
        try {
            testIntArrayMergingVpack();
        } finally {
        }
    }


    void __invoke_testLongArrayMergingVpack() throws Exception {
        try {
            testLongArrayMergingVpack();
        } finally {
        }
    }

}
