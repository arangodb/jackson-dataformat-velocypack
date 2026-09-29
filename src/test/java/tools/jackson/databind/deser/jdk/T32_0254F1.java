package tools.jackson.databind.deser.jdk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0254F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] LARGE_DOUBLE_OBJECT = largeBcdObject("d", 1200);
private static final byte[] LARGE_INTEGER_OBJECT = largeBcdObject("number", 1200);
private static final byte[] HELLO_BINARY = VPackWireFixtureTest.hex(
            "c0 05 68 65 6c 6c 6f");
private static final byte[] BYTE_BUFFER_PROPERTY = VPackWireFixtureTest.hex(
            "14 0d 44 64 61 74 61 c0 03 01 02 03 01");
private static final byte[] EMPTY_BINARY = VPackWireFixtureTest.hex("c0 00");

    // Provenance: ByteBufferDeserializerTest#testByteBuffer.
    void testByteBuffer() throws Exception {
        ByteBuffer result = MAPPER.readValue(HELLO_BINARY, ByteBuffer.class);
        assertNotNull(result);
        assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), remaining(result));
    }

    // Provenance: ByteBufferDeserializerTest#testByteBufferAsProperty.
    void testByteBufferAsProperty() throws Exception {
        ByteBufferBean bean = MAPPER.readValue(BYTE_BUFFER_PROPERTY, ByteBufferBean.class);
        assertNotNull(bean);
        assertNotNull(bean.data);
        assertEquals(3, bean.data.remaining());
        assertEquals(1, bean.data.get());
        assertEquals(2, bean.data.get());
        assertEquals(3, bean.data.get());
    }

    // Provenance: ByteBufferDeserializerTest#testByteBufferPropertyRoundTrip.
    void testByteBufferPropertyRoundTrip() throws Exception {
        ByteBufferBean input = new ByteBufferBean();
        input.data = ByteBuffer.wrap(new byte[] { 10, 20, 30, 40 });

        ByteBufferBean result = MAPPER.readValue(MAPPER.writeValueAsBytes(input),
                ByteBufferBean.class);
        assertNotNull(result);
        assertNotNull(result.data);
        assertEquals(4, result.data.remaining());
        assertEquals(10, result.data.get());
        assertEquals(20, result.data.get());
    }

    // Provenance: ByteBufferDeserializerTest#testByteBufferRoundTrip.
    void testByteBufferRoundTrip() throws Exception {
        byte[] original = "ByteBuffer round-trip test data".getBytes(StandardCharsets.UTF_8);
        ByteBuffer result = MAPPER.readValue(MAPPER.writeValueAsBytes(ByteBuffer.wrap(original)),
                ByteBuffer.class);
        assertNotNull(result);
        assertArrayEquals(original, remaining(result));
    }

    // Provenance: ByteBufferDeserializerTest#testEmptyByteBuffer.
    void testEmptyByteBuffer() throws Exception {
        ByteBuffer result = MAPPER.readValue(EMPTY_BINARY, ByteBuffer.class);
        assertNotNull(result);
        assertEquals(0, result.remaining());
    }

    // Provenance: ByteBufferDeserializerTest#testLargeByteBuffer.
    void testLargeByteBuffer() throws Exception {
        byte[] original = new byte[10000];
        for (int i = 0; i < original.length; ++i) {
            original[i] = (byte) (i & 0xFF);
        }

        ByteBuffer result = MAPPER.readValue(
                MAPPER.writeValueAsBytes(ByteBuffer.wrap(original)), ByteBuffer.class);
        assertNotNull(result);
        assertArrayEquals(original, remaining(result));
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

    void __invoke_testByteBuffer() throws Exception {
        try {
            testByteBuffer();
        } finally {
        }
    }


    void __invoke_testByteBufferAsProperty() throws Exception {
        try {
            testByteBufferAsProperty();
        } finally {
        }
    }


    void __invoke_testByteBufferPropertyRoundTrip() throws Exception {
        try {
            testByteBufferPropertyRoundTrip();
        } finally {
        }
    }


    void __invoke_testByteBufferRoundTrip() throws Exception {
        try {
            testByteBufferRoundTrip();
        } finally {
        }
    }


    void __invoke_testEmptyByteBuffer() throws Exception {
        try {
            testEmptyByteBuffer();
        } finally {
        }
    }


    void __invoke_testLargeByteBuffer() throws Exception {
        try {
            testLargeByteBuffer();
        } finally {
        }
    }

}
