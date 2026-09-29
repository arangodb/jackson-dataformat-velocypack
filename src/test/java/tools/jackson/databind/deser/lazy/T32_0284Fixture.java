package tools.jackson.databind.deser.lazy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    // Provenance: LazyIgnoralForNumbers3730Test#testIgnoreBigInteger().
    void testIgnoreBigIntegerVpack() throws Exception {
        byte[] input = recordWithNumber(bigIntegerBcd(999));

        ExtractFields result = MAPPER.readValue(input, ExtractFields.class);
        assertNotNull(result);
        assertEquals("s", result.s);
        assertEquals(1, result.i);

        UnwrappedWithNumber mapped = STRICT_MAPPER.readValue(input,
                UnwrappedWithNumber.class);
        assertNotNull(mapped.values);
        assertEquals(new BigInteger("9".repeat(999)), mapped.values.n);
    }

    // Provenance: LazyIgnoralForNumbers3730Test#testIgnoreFPValuesDefault().
    void testIgnoreFPValuesDefaultVpack() throws Exception {
        byte[] input = VPackWireFixtureTest.hex(
                "14 15 41 73 41 73 41 6e 1b 00 00 00 00 00 00 d0 3f "
              + "41 69 31 03");

        ExtractFields result = MAPPER.readValue(input, ExtractFields.class);
        assertNotNull(result);
        assertEquals("s", result.s);
        assertEquals(1, result.i);

        UnwrappedWithNumber number = STRICT_MAPPER.readValue(input,
                UnwrappedWithNumber.class);
        assertEquals(Double.valueOf(0.25), number.values.n);
        UnwrappedWithDouble dbl = STRICT_MAPPER.readValue(input,
                UnwrappedWithDouble.class);
        assertEquals(0.25d, dbl.values.n);
    }

    // Provenance: LazyIgnoralForNumbers3730Test#testIgnoreFPValuesBigDecimal().
    void testIgnoreFPValuesBigDecimalVpack() throws Exception {
        byte[] input = VPackWireFixtureTest.hex(
                "14 13 41 73 41 73 41 6e c8 01 fe ff ff ff 25 "
              + "41 69 31 03");
        ObjectReader reader = MAPPER.readerFor(ExtractFields.class)
                .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        ExtractFields result = reader.readValue(input);
        assertNotNull(result);
        assertEquals("s", result.s);
        assertEquals(1, result.i);

        ObjectReader strict = reader.with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        UnwrappedWithNumber number = strict.forType(UnwrappedWithNumber.class)
                .readValue(input);
        assertEquals(new BigDecimal("0.25"), number.values.n);
        UnwrappedWithBigDecimal decimal = strict.forType(UnwrappedWithBigDecimal.class)
                .readValue(input);
        assertEquals(new BigDecimal("0.25"), decimal.values.n);
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

    void __invoke_testIgnoreBigIntegerVpack() throws Exception {
        try {
            testIgnoreBigIntegerVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreFPValuesDefaultVpack() throws Exception {
        try {
            testIgnoreFPValuesDefaultVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreFPValuesBigDecimalVpack() throws Exception {
        try {
            testIgnoreFPValuesBigDecimalVpack();
        } finally {
        }
    }

}
