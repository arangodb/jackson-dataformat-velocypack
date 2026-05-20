package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for parsing VPack scalar values (null, boolean, integers, double, strings, binary).
 */
public class VPackScalarParseTest extends BaseTestForVPack {
    // =========================================================
    // NULL
    // =========================================================

    @Test
    public void testParseNull() {
        // 0x18 = null
        byte[] input = {0x18};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NULL, p.nextToken());
            assertThat(p.nextToken()).isNull();
        }
    }

    // =========================================================
    // BOOLEANS
    // =========================================================

    @Test
    public void testParseFalse() {
        byte[] input = {0x19};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_FALSE, p.nextToken());
            assertThat(p.nextToken()).isNull();
        }
    }

    @Test
    public void testParseTrue() {
        byte[] input = {0x1a};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_TRUE, p.nextToken());
            assertThat(p.nextToken()).isNull();
        }
    }

    // =========================================================
    // SMALL INTEGERS 0x30-0x39 (0..9)
    // =========================================================

    @Test
    public void testParseSmallInt_0() {
        byte[] input = {0x30}; // 0
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(0);
        }
    }

    @Test
    public void testParseSmallInt_9() {
        byte[] input = {0x39}; // 9
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(9);
        }
    }

    @Test
    public void testParseSmallIntAll() {
        for (int i = 0; i <= 9; i++) {
            byte[] input = {(byte) (0x30 + i)};
            try (JsonParser p = vpackParser(input)) {
                assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
                assertThat(p.getIntValue()).as("Failed for small int " + i).isEqualTo(i);
            }
        }
    }

    // =========================================================
    // SMALL NEGATIVES 0x3a-0x3f (-6..-1)
    // =========================================================

    @Test
    public void testParseSmallNeg_minus1() {
        byte[] input = {0x3f}; // -1
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(-1);
        }
    }

    @Test
    public void testParseSmallNeg_minus6() {
        byte[] input = {0x3a}; // -6
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(-6);
        }
    }

    @Test
    public void testParseSmallNegAll() {
        int[] expected = {-6, -5, -4, -3, -2, -1};
        for (int i = 0; i < 6; i++) {
            byte[] input = {(byte) (0x3a + i)};
            try (JsonParser p = vpackParser(input)) {
                assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
                assertThat(p.getIntValue())
                        .as("Failed for small neg at 0x3" + Integer.toHexString(0xa + i))
                        .isEqualTo(expected[i]);
            }
        }
    }

    // =========================================================
    // SIGNED INTEGERS 0x20-0x27 (1..8 bytes)
    // =========================================================

    @Test
    public void testParseSignedInt_1byte() {
        // 0x20 = 1-byte signed int; value = -1 = 0xFF
        byte[] input = {0x20, (byte) 0xFF};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(-1);
        }
    }

    @Test
    public void testParseSignedInt_1byte_positive() {
        // 0x20 = 1-byte signed; value = 100
        byte[] input = {0x20, 100};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(100);
        }
    }

    @Test
    public void testParseSignedInt_2byte() {
        // 0x21 = 2-byte signed int; value = 1000 = 0x03E8 (LE: E8 03)
        byte[] input = {0x21, (byte) 0xE8, 0x03};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(1000);
        }
    }

    @Test
    public void testParseSignedInt_4byte_negative() {
        // 0x23 = 4-byte signed int; value = Integer.MIN_VALUE
        // LE: 00 00 00 80
        byte[] input = {0x23, 0x00, 0x00, 0x00, (byte) 0x80};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(Integer.MIN_VALUE);
        }
    }

    @Test
    public void testParseSignedInt_8byte() {
        // 0x27 = 8-byte signed int; value = Long.MIN_VALUE
        // LE: 00 00 00 00 00 00 00 80
        byte[] input = {
                0x27, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80
        };
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getLongValue()).isEqualTo(Long.MIN_VALUE);
        }
    }

    // =========================================================
    // UNSIGNED INTEGERS 0x28-0x2f (1..8 bytes)
    // =========================================================

    @Test
    public void testParseUnsignedInt_1byte() {
        byte[] input = {0x28, (byte) 0xFF}; // 255
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(255);
        }
    }

    @Test
    public void testParseUnsignedInt_2byte() {
        byte[] input = {0x29, (byte) 0xFF, (byte) 0xFF}; // 65535
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getIntValue()).isEqualTo(65535);
        }
    }

    @Test
    public void testParseUnsignedInt_8byte_max() {
        // Long.MAX_VALUE = 0x7FFFFFFFFFFFFFFF
        byte[] input = {
                0x2f, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F
        };
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getLongValue()).isEqualTo(Long.MAX_VALUE);
        }
    }

    // =========================================================
    // DOUBLE 0x1b
    // =========================================================

    @Test
    public void testParseDouble_zero() {
        // 0.0 in IEEE 754 = 0x0000000000000000 (LE: same)
        byte[] input = {0x1b, 0, 0, 0, 0, 0, 0, 0, 0};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertThat(p.getDoubleValue()).isEqualTo(0.0);
        }
    }

    @Test
    public void testParseDouble_one() {
        // 1.0 in IEEE 754 = 0x3FF0000000000000 (LE: 00 00 00 00 00 00 F0 3F)
        byte[] input = {0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xF0, 0x3F};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertThat(p.getDoubleValue()).isEqualTo(1.0);
        }
    }

    @Test
    public void testParseDouble_NaN() {
        long nanBits = Double.doubleToRawLongBits(Double.NaN);
        byte[] input = new byte[9];
        input[0] = 0x1b;
        for (int i = 0; i < 8; i++) {
            input[1 + i] = (byte) (nanBits >>> (8 * i));
        }
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertThat(Double.isNaN(p.getDoubleValue())).isTrue();
            assertThat(p.isNaN()).isTrue();
        }
    }

    @Test
    public void testParseDouble_positiveInfinity() {
        long infBits = Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
        byte[] input = new byte[9];
        input[0] = 0x1b;
        for (int i = 0; i < 8; i++) {
            input[1 + i] = (byte) (infBits >>> (8 * i));
        }
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertThat(p.getDoubleValue()).isEqualTo(Double.POSITIVE_INFINITY);
        }
    }

    // =========================================================
    // SHORT STRINGS 0x40-0xbe
    // =========================================================

    @Test
    public void testParseShortString_empty() {
        byte[] input = {0x40}; // 0x40 = empty string
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_STRING, p.nextToken());
            assertThat(p.getString()).isEmpty();
        }
    }

    @Test
    public void testParseShortString_hello() {
        // "hello" = 5 bytes -> type = 0x40 + 5 = 0x45
        byte[] input = {0x45, 'h', 'e', 'l', 'l', 'o'};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_STRING, p.nextToken());
            assertThat(p.getString()).isEqualTo("hello");
        }
    }

    @Test
    public void testParseShortString_126bytes() {
        // 126 bytes = max short string; type = 0x40 + 126 = 0xbe
        byte[] str = new byte[126];
        java.util.Arrays.fill(str, (byte) 'A');
        byte[] input = new byte[127];
        input[0] = (byte) 0xbe;
        System.arraycopy(str, 0, input, 1, 126);
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_STRING, p.nextToken());
            assertThat(p.getString()).isEqualTo(new String(str, StandardCharsets.UTF_8));
        }
    }

    @Test
    public void testParseShortString_withNul() {
        // String containing NUL byte - per spec: strings may contain NUL bytes
        byte[] input = {0x43, 'a', 0x00, 'b'}; // 3-byte string "a\0b"
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_STRING, p.nextToken());
            String s = p.getString();
            assertThat(s.length()).isEqualTo(3);
            assertThat(s.charAt(0)).isEqualTo('a');
            assertThat(s.charAt(1)).isEqualTo('\0');
            assertThat(s.charAt(2)).isEqualTo('b');
        }
    }

    // =========================================================
    // LONG STRING 0xbf
    // =========================================================

    @Test
    public void testParseLongString_127bytes() {
        // 127 bytes = first long string; type = 0xbf, then 8-byte LE length = 127
        byte[] str = new byte[127];
        java.util.Arrays.fill(str, (byte) 'X');
        byte[] input = new byte[1 + 8 + 127];
        input[0] = (byte) 0xbf;
        // LE 127 = 7F 00 00 00 00 00 00 00
        input[1] = 0x7F;
        for (int i = 2; i < 9; i++) input[i] = 0;
        System.arraycopy(str, 0, input, 9, 127);
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_STRING, p.nextToken());
            assertThat(p.getString()).isEqualTo(new String(str, StandardCharsets.UTF_8));
        }
    }

    // =========================================================
    // BINARY 0xc0-0xc7
    // =========================================================

    @Test
    public void testParseBinary_1byteLenWidth() {
        // 0xc0 = binary with 1-byte length; payload = [0xDE, 0xAD]
        byte[] input = {(byte) 0xc0, 0x02, (byte) 0xDE, (byte) 0xAD};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            byte[] binary = p.getBinaryValue();
            assertThat(binary).isNotNull();
            assertThat(binary).hasSize(2);
            assertThat(binary[0]).isEqualTo((byte) 0xDE);
            assertThat(binary[1]).isEqualTo((byte) 0xAD);
        }
    }

    @Test
    public void testParseBinary_empty() {
        // 0xc0 with length 0
        byte[] input = {(byte) 0xc0, 0x00};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            byte[] binary = p.getBinaryValue();
            assertThat(binary).isNotNull();
            assertThat(binary).hasSize(0);
        }
    }

    // =========================================================
    // BCD FLOATS
    // =========================================================

    @Test
    public void testParseBcdFloat_12345_encoding1() {
        // c8 03 00 00 00 00 01 23 45
        // Type c8 = 0xc8 (positive BCD, mantissa length width = 1 byte)
        // mantissa length = 3
        // exponent (4 bytes LE) = 0x00000000 = 0
        // mantissa = [0x01, 0x23, 0x45]
        byte[] input = {(byte) 0xc8, 0x03, 0x00, 0x00, 0x00, 0x00, 0x01, 0x23, 0x45};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertThat(p.getDecimalValue()).isEqualTo("12345");
        }
    }

    @Test
    public void testParseBcdFloat_12345_encoding2() {
        // c8 03 ff ff ff ff 12 34 50
        // mantissa = [0x12, 0x34, 0x50], exponent = -1 (0xFFFFFFFF LE)
        byte[] input = {
                (byte) 0xc8, 0x03,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                0x12, 0x34, 0x50
        };
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertThat(p.getDecimalValue()).isEqualTo("12345");
        }
    }

    @Test
    public void testParseBcdFloat_negative() {
        // Negative BCD: 0xd0 + width - 1 = 0xd0 (1-byte mantissa length)
        // Value = -42
        // Encode -42: digits = "42", already even, exponent = 0
        // bcd = [0x42], exp = 0
        byte[] input = {(byte) 0xd0, 0x01, 0x00, 0x00, 0x00, 0x00, 0x42};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertThat(p.getDecimalValue()).isEqualTo("-42");
        }
    }

    // =========================================================
    // DATE 0x1c
    // =========================================================

    @Test
    public void testParseDate_epoch() {
        // 0x1c + 8 bytes LE signed = 0 (epoch)
        byte[] input = {0x1c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getLongValue()).isEqualTo(0L);
        }
    }

    @Test
    public void testParseDate_1ms() {
        byte[] input = {0x1c, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertThat(p.getLongValue()).isEqualTo(1L);
        }
    }

    // =========================================================
    // MIN/MAX KEY
    // =========================================================

    @Test
    public void testParseMinKey() {
        byte[] input = {0x1e};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            assertThat(p.getEmbeddedObject()).isEqualTo("minKey");
        }
    }

    @Test
    public void testParseMaxKey() {
        byte[] input = {0x1f};
        try (JsonParser p = vpackParser(input)) {
            assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            assertThat(p.getEmbeddedObject()).isEqualTo("maxKey");
        }
    }

    // =========================================================
    // REJECTED TYPES
    // =========================================================

    @Test
    public void testRejectNone_0x00() {
        byte[] input = {0x00};
        try (JsonParser p = vpackParser(input)) {
            Throwable e = catchThrowable(p::nextToken);
            assertThat(e).isInstanceOf(StreamReadException.class);
            assertThat(e.getMessage().toLowerCase(Locale.ROOT)).containsAnyOf("0x00", "none", "illegal");
        }
    }

    @Test
    public void testRejectIllegal_0x17() {
        byte[] input = {0x17};
        try (JsonParser p = vpackParser(input)) {
            Throwable e = catchThrowable(p::nextToken);
            assertThat(e).isInstanceOf(StreamReadException.class);
            assertThat(e.getMessage().toLowerCase(Locale.ROOT)).containsAnyOf("0x17", "illegal");
        }
    }

    @Test
    public void testRejectExternal_0x1d() {
        byte[] input = {0x1d};
        try (JsonParser p = vpackParser(input)) {
            Throwable e = catchThrowable(p::nextToken);
            assertThat(e).isInstanceOf(StreamReadException.class);
            assertThat(e.getMessage().toLowerCase(Locale.ROOT)).containsAnyOf("0x1d", "external");
        }
    }

    @Test
    public void testRejectReserved_0x15() {
        byte[] input = {0x15};
        try (JsonParser p = vpackParser(input)) {
            Throwable e = catchThrowable(p::nextToken);
            assertThat(e).isInstanceOf(StreamReadException.class);
            assertThat(e.getMessage()).isNotNull();
        }
    }

    @Test
    public void testRejectReserved_0xd8() {
        byte[] input = {(byte) 0xd8};
        try (JsonParser p = vpackParser(input)) {
            Throwable e = catchThrowable(p::nextToken);
            assertThat(e).isInstanceOf(StreamReadException.class);
            assertThat(e.getMessage()).isNotNull();
        }
    }
}
