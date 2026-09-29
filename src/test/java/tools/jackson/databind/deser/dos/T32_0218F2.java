package tools.jackson.databind.deser.dos;

import java.io.ByteArrayInputStream;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0218F2 {
private static final int DEEP_NESTING = StreamReadConstraints.DEFAULT_MAX_DEPTH * 10;
private static final int LIMITED_STRING_LENGTH = 100;
private static final int TEST_STRING_LENGTH = LIMITED_STRING_LENGTH + 1;
private static final int BIG_NUM_LEN = 199_999;
private static final ObjectMapper DEEP_MAPPER = VPackMapper.builder(
            VPackFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxNestingDepth(Integer.MAX_VALUE).build())
                    .build()).build();
private static final ObjectMapper LIMITED_STRING_MAPPER = VPackMapper.builder(
            VPackFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxStringLength(LIMITED_STRING_LENGTH).build())
                    .build()).build();
private static final ObjectMapper UNLIMITED_STRING_MAPPER = VPackMapper.builder(
            VPackFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxStringLength(Integer.MAX_VALUE).build())
                    .build()).build();
private static final ObjectMapper HUGE_NUMBER_MAPPER = VPackMapper.builder(
            VPackFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxNumberLength(BIG_NUM_LEN + 10).build())
                    .build()).build();
private static final byte[] DEEP_ARRAY = nestedEqualArrays(DEEP_NESTING,
            new byte[] { 0x01 });
private static final byte[] DEEP_OBJECT = nestedObjects(DEEP_NESTING);
private static final byte[] HUGE_POSITIVE_INTEGER = hugePositiveBcd();

    // Provenance: StreamReadStringConstraintsTest#testBigString.
    void testBigStringVpack() {
        assertStringLimitFailure(stringObject(TEST_STRING_LENGTH));
    }

    // Provenance: StreamReadStringConstraintsTest#testBiggerString.
    void testBiggerStringVpack() throws Exception {
        assertStringLimitFailure(new ByteArrayInputStream(stringObject(TEST_STRING_LENGTH)));
    }

    // Provenance: StreamReadStringConstraintsTest#testUnlimitedString.
    void testUnlimitedStringVpack() throws Exception {
        StringWrapper value = UNLIMITED_STRING_MAPPER.readValue(
                stringObject(TEST_STRING_LENGTH), StringWrapper.class);
        assertEquals(TEST_STRING_LENGTH, value.string.length());
    }
private static void assertStringLimitFailure(byte[] input) {
        StreamConstraintsException exception = assertThrows(StreamConstraintsException.class,
                () -> LIMITED_STRING_MAPPER.readValue(input, StringWrapper.class));
        assertTrue(exception.getMessage().startsWith("String value length"));
        assertTrue(exception.getMessage().contains("exceeds the maximum allowed ("));
    }
private static void assertStringLimitFailure(ByteArrayInputStream input) {
        StreamConstraintsException exception = assertThrows(StreamConstraintsException.class,
                () -> LIMITED_STRING_MAPPER.readValue(input, StringWrapper.class));
        assertTrue(exception.getMessage().startsWith("String value length"));
        assertTrue(exception.getMessage().contains("exceeds the maximum allowed ("));
    }
private static byte[] stringObject(int valueLength) {
        if (valueLength > 126) {
            throw new IllegalArgumentException("test fixture expects a short VPack string");
        }
        int keyLength = 1 + 6;
        int valueEncodedLength = 1 + valueLength;
        int bodyLength = keyLength + valueEncodedLength;
        int length = 1 + 1 + 1 + bodyLength + 1;
        byte[] result = new byte[length];
        result[0] = 0x0b;
        result[1] = (byte) length;
        result[2] = 0x01;
        int cursor = 3;
        result[cursor++] = 0x46;
        result[cursor++] = 's';
        result[cursor++] = 't';
        result[cursor++] = 'r';
        result[cursor++] = 'i';
        result[cursor++] = 'n';
        result[cursor++] = 'g';
        result[cursor++] = (byte) (0x40 + valueLength);
        for (int i = 0; i < valueLength; ++i) {
            result[cursor++] = 'a';
        }
        result[cursor] = 0x03;
        return result;
    }
private static byte[] nestedEqualArrays(int depth, byte[] leaf) {
        int length = Math.addExact(Math.multiplyExact(depth, 9), leaf.length);
        byte[] result = new byte[length];
        int offset = 0;
        int containerLength = length;
        for (int i = 0; i < depth; ++i) {
            result[offset++] = 0x05;
            putLittleEndian(result, offset, containerLength);
            offset += 8;
            containerLength -= 9;
        }
        System.arraycopy(leaf, 0, result, offset, leaf.length);
        return result;
    }
private static byte[] nestedObjects(int depth) {
        int length = Math.addExact(Math.multiplyExact(depth, 27), 1);
        byte[] result = new byte[length];
        int offset = 0;
        for (int i = 0; i < depth; ++i) {
            int containerLength = 1 + 27 * (depth - i);
            result[offset] = 0x0e;
            putLittleEndian(result, offset + 1, containerLength);
            putLittleEndian(result, offset + 9, 1);
            result[offset + 17] = 0x41;
            result[offset + 18] = 'x';
            putLittleEndian(result, offset + containerLength - 8, 17);
            offset += 19;
        }
        result[offset] = 0x0a;
        return result;
    }
private static byte[] hugePositiveBcd() {
        int mantissaLength = (BIG_NUM_LEN + 1) / 2;
        byte[] result = new byte[1 + 3 + 4 + mantissaLength];
        result[0] = (byte) 0xca; // positive BCD, three-byte mantissa length
        result[1] = (byte) mantissaLength;
        result[2] = (byte) (mantissaLength >>> 8);
        result[3] = (byte) (mantissaLength >>> 16);
        // result[4..7] is exponent zero in little-endian form.
        result[8] = 0x09;
        for (int i = 9; i < result.length; ++i) {
            result[i] = (byte) 0x99;
        }
        return result;
    }
private static void putLittleEndian(byte[] target, int offset, long value) {
        for (int i = 0; i < 8; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
static class StringWrapper {
        public String string;
    }
private enum ABC { A, B, C }

    void __invoke_testBigStringVpack() throws Exception {
        try {
            testBigStringVpack();
        } finally {
        }
    }


    void __invoke_testBiggerStringVpack() throws Exception {
        try {
            testBiggerStringVpack();
        } finally {
        }
    }


    void __invoke_testUnlimitedStringVpack() throws Exception {
        try {
            testUnlimitedStringVpack();
        } finally {
        }
    }

}
