package tools.jackson.databind.deser.jdk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0254F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] LARGE_DOUBLE_OBJECT = largeBcdObject("d", 1200);
private static final byte[] LARGE_INTEGER_OBJECT = largeBcdObject("number", 1200);
private static final byte[] HELLO_BINARY = VPackWireFixtureTest.hex(
            "c0 05 68 65 6c 6c 6f");
private static final byte[] BYTE_BUFFER_PROPERTY = VPackWireFixtureTest.hex(
            "14 0d 44 64 61 74 61 c0 03 01 02 03 01");
private static final byte[] EMPTY_BINARY = VPackWireFixtureTest.hex("c0 00");

    // Provenance: BigNumbersDeserTest#testBigInteger.
    void testBigInteger() {
        assertThrows(StreamConstraintsException.class,
                () -> MAPPER.readValue(LARGE_INTEGER_OBJECT, BigIntegerWrapper.class));
    }

    // Provenance: BigNumbersDeserTest#testBigIntegerUnlimited.
    void testBigIntegerUnlimited() throws Exception {
        ObjectMapper unlimited = mapperWithUnlimitedNumbers();
        BigIntegerWrapper result = unlimited.readValue(LARGE_INTEGER_OBJECT,
                BigIntegerWrapper.class);
        assertNotNull(result);
        assertEquals(1200, result.number.toString().length());
        assertEquals(new BigInteger("1".repeat(1200)), result.number);
    }

    // Provenance: BigNumbersDeserTest#testDouble.
    void testDouble() {
        assertThrows(StreamConstraintsException.class,
                () -> MAPPER.readValue(LARGE_DOUBLE_OBJECT, DoubleWrapper.class));
    }

    // Provenance: BigNumbersDeserTest#testDoubleUnlimited.
    void testDoubleUnlimited() throws Exception {
        DoubleWrapper result = mapperWithUnlimitedNumbers().readValue(
                LARGE_DOUBLE_OBJECT, DoubleWrapper.class);
        assertNotNull(result);
    }

    // Provenance: BigNumbersDeserTest#testNumberEndingWithDot.
    void testNumberEndingWithDot() throws Exception {
        testNumberWith("55.");
        testNumberWith("-55.");
        testNumberWith("+55.");
    }

    // Provenance: BigNumbersDeserTest#testNumberStartingWithDot.
    void testNumberStartingWithDot() throws Exception {
        testNumberWith(".555555555555555555555555555555");
        testNumberWith("-.555555555555555555555555555555");
        testNumberWith("+.555555555555555555555555555555");
    }
private static ObjectMapper mapperWithUnlimitedNumbers() {
        return VPackMapper.builder(VPackFactory.builder().streamReadConstraints(
                StreamReadConstraints.builder().maxNumberLength(Integer.MAX_VALUE).build())
                .build()).build();
    }
private static void testNumberWith(String value) throws Exception {
        BigDecimal expected = new BigDecimal(value);
        BigDecimalWrapper result = MAPPER.readValue(numberStringObject(value),
                BigDecimalWrapper.class);
        assertEquals(expected, result.number);
    }
private static byte[] remaining(ByteBuffer value) {
        byte[] result = new byte[value.remaining()];
        value.get(result);
        return result;
    }
private static byte[] largeBcdObject(String name, int digits) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        int nameHeaderLength = nameBytes.length <= 126 ? 1 : 9;
        int mantissaLength = (digits + 1) / 2;
        int numberLength = 1 + (mantissaLength <= 255 ? 1 : 2) + 4 + mantissaLength;
        int bodyLength = nameHeaderLength + nameBytes.length + numberLength;
        int lengthWidth = 1;
        int length;
        do {
            length = 1 + lengthWidth + bodyLength + 1;
            int required = forwardVarIntLength(length);
            if (required == lengthWidth) {
                break;
            }
            lengthWidth = required;
        } while (true);

        byte[] result = new byte[length];
        int offset = 0;
        result[offset++] = 0x14;
        offset = writeForwardVarInt(result, offset, length);
        result[offset++] = (byte) (0x40 + nameBytes.length);
        System.arraycopy(nameBytes, 0, result, offset, nameBytes.length);
        offset += nameBytes.length;
        result[offset++] = (byte) (mantissaLength <= 255 ? 0xC8 : 0xC9);
        if (mantissaLength <= 255) {
            result[offset++] = (byte) mantissaLength;
        } else {
            result[offset++] = (byte) mantissaLength;
            result[offset++] = (byte) (mantissaLength >>> 8);
        }
        offset += 4; // zero exponent, already zero-filled
        for (int i = 0; i < mantissaLength; ++i) {
            result[offset++] = 0x11;
        }
        result[offset] = 0x01;
        return result;
    }
private static byte[] numberStringObject(String value) {
        byte[] name = "number".getBytes(StandardCharsets.UTF_8);
        byte[] text = value.getBytes(StandardCharsets.UTF_8);
        int bodyLength = 1 + name.length + 1 + text.length;
        int length = 1 + 1 + bodyLength + 1;
        byte[] result = new byte[length];
        int offset = 0;
        result[offset++] = 0x14;
        result[offset++] = (byte) length;
        result[offset++] = (byte) (0x40 + name.length);
        System.arraycopy(name, 0, result, offset, name.length);
        offset += name.length;
        result[offset++] = (byte) (0x40 + text.length);
        System.arraycopy(text, 0, result, offset, text.length);
        result[length - 1] = 0x01;
        return result;
    }
private static int forwardVarIntLength(int value) {
        int length = 1;
        while ((value >>> (7 * length)) != 0) {
            ++length;
        }
        return length;
    }
private static int writeForwardVarInt(byte[] target, int offset, int value) {
        while (value > 0x7F) {
            target[offset++] = (byte) ((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        target[offset] = (byte) value;
        return offset + 1;
    }
static class BigIntegerWrapper {
        public BigInteger number;
    }
static class BigDecimalWrapper {
        public BigDecimal number;
    }
static class DoubleWrapper {
        public double d;
    }
static class ByteBufferBean {
        public ByteBuffer data;
    }

    void __invoke_testBigInteger() throws Exception {
        try {
            testBigInteger();
        } finally {
        }
    }


    void __invoke_testBigIntegerUnlimited() throws Exception {
        try {
            testBigIntegerUnlimited();
        } finally {
        }
    }


    void __invoke_testDouble() throws Exception {
        try {
            testDouble();
        } finally {
        }
    }


    void __invoke_testDoubleUnlimited() throws Exception {
        try {
            testDoubleUnlimited();
        } finally {
        }
    }


    void __invoke_testNumberEndingWithDot() throws Exception {
        try {
            testNumberEndingWithDot();
        } finally {
        }
    }


    void __invoke_testNumberStartingWithDot() throws Exception {
        try {
            testNumberStartingWithDot();
        } finally {
        }
    }

}
