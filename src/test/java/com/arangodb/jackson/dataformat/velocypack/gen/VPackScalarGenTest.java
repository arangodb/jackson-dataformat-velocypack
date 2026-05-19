package com.arangodb.jackson.dataformat.velocypack.gen;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackWriteFeature;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for generating VPack scalar values.
 */
public class VPackScalarGenTest extends BaseTestForVPack
{
    private byte[] gen(WriteAction action) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            action.write(g);
        }
        return out.toByteArray();
    }

    @FunctionalInterface
    interface WriteAction {
        void write(JsonGenerator g) throws Exception;
    }

    // =========================================================
    // NULL
    // =========================================================

    @Test
    public void testGenNull() throws Exception {
        byte[] bytes = gen(g -> g.writeNull());
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x18, bytes[0]);
    }

    // =========================================================
    // BOOLEANS
    // =========================================================

    @Test
    public void testGenFalse() throws Exception {
        byte[] bytes = gen(g -> g.writeBoolean(false));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x19, bytes[0]);
    }

    @Test
    public void testGenTrue() throws Exception {
        byte[] bytes = gen(g -> g.writeBoolean(true));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x1a, bytes[0]);
    }

    // =========================================================
    // SMALL INTEGERS (WRITE_MIN_INT_WIDTH enabled by default)
    // =========================================================

    @Test
    public void testGenSmallInt_0() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(0));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x30, bytes[0]);
    }

    @Test
    public void testGenSmallInt_9() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(9));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x39, bytes[0]);
    }

    @Test
    public void testGenSmallNeg_minus1() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(-1));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x3f, bytes[0]); // 0x40 + (-1) = 0x3f
    }

    @Test
    public void testGenSmallNeg_minus6() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(-6));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x3a, bytes[0]); // 0x40 + (-6) = 0x3a
    }

    // =========================================================
    // LARGER INTEGERS
    // =========================================================

    @Test
    public void testGenInt_10_usesSignedEncoding() throws Exception {
        // 10 doesn't fit in small int (0-9), so uses signed encoding
        byte[] bytes = gen(g -> g.writeNumber(10));
        assertEquals(2, bytes.length);
        assertEquals((byte) 0x20, bytes[0]); // 0x20 = 1-byte signed
        assertEquals((byte) 10, bytes[1]);
    }

    @Test
    public void testGenInt_minus7_usesSignedEncoding() throws Exception {
        // -7 doesn't fit in small neg (-6..-1), so uses signed encoding
        byte[] bytes = gen(g -> g.writeNumber(-7));
        assertEquals(2, bytes.length);
        assertEquals((byte) 0x20, bytes[0]);
        assertEquals((byte) -7, bytes[1]);
    }

    @Test
    public void testGenLong_maxValue() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(Long.MAX_VALUE));
        assertEquals(9, bytes.length);
        assertEquals((byte) 0x27, bytes[0]); // 8-byte signed
        // Check LE encoding: 7F FF FF FF FF FF FF FF
        assertEquals((byte) 0xFF, bytes[1]);
        assertEquals((byte) 0xFF, bytes[2]);
        assertEquals((byte) 0xFF, bytes[3]);
        assertEquals((byte) 0xFF, bytes[4]);
        assertEquals((byte) 0xFF, bytes[5]);
        assertEquals((byte) 0xFF, bytes[6]);
        assertEquals((byte) 0xFF, bytes[7]);
        assertEquals((byte) 0x7F, bytes[8]);
    }

    @Test
    public void testGenLong_minValue() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(Long.MIN_VALUE));
        assertEquals(9, bytes.length);
        assertEquals((byte) 0x27, bytes[0]); // 8-byte signed
        // Long.MIN_VALUE = 0x8000000000000000 in LE: 00 00 00 00 00 00 00 80
        assertEquals((byte) 0x00, bytes[1]);
        assertEquals((byte) 0x80, bytes[8]);
    }

    @Test
    public void testGenNoMinIntWidth() throws Exception {
        // Without WRITE_MIN_INT_WIDTH: 0 should NOT use small int
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeNumber(0);
        }
        byte[] bytes = out.toByteArray();
        // Should use 1-byte signed encoding
        assertEquals(2, bytes.length);
        assertEquals((byte) 0x20, bytes[0]);
    }

    // =========================================================
    // DOUBLE
    // =========================================================

    @Test
    public void testGenDouble_zero() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(0.0));
        assertEquals(9, bytes.length);
        assertEquals((byte) 0x1b, bytes[0]);
        // 0.0 = all zeros
        for (int i = 1; i < 9; i++) {
            assertEquals((byte) 0, bytes[i]);
        }
    }

    @Test
    public void testGenDouble_one() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(1.0));
        assertEquals(9, bytes.length);
        assertEquals((byte) 0x1b, bytes[0]);
        // 1.0 = 0x3FF0000000000000 in LE: 00 00 00 00 00 00 F0 3F
        assertEquals((byte) 0x00, bytes[1]);
        assertEquals((byte) 0x00, bytes[2]);
        assertEquals((byte) 0x00, bytes[3]);
        assertEquals((byte) 0x00, bytes[4]);
        assertEquals((byte) 0x00, bytes[5]);
        assertEquals((byte) 0x00, bytes[6]);
        assertEquals((byte) 0xF0, bytes[7]);
        assertEquals((byte) 0x3F, bytes[8]);
    }

    @Test
    public void testGenDouble_NaN() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(Double.NaN));
        assertEquals(9, bytes.length);
        assertEquals((byte) 0x1b, bytes[0]);
        long expectedBits = Double.doubleToRawLongBits(Double.NaN);
        for (int i = 0; i < 8; i++) {
            assertEquals((byte) (expectedBits >>> (8 * i)), bytes[1 + i]);
        }
    }

    // =========================================================
    // STRINGS
    // =========================================================

    @Test
    public void testGenEmptyString() throws Exception {
        byte[] bytes = gen(g -> g.writeString(""));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x40, bytes[0]);
    }

    @Test
    public void testGenShortString_hello() throws Exception {
        byte[] bytes = gen(g -> g.writeString("hello"));
        assertEquals(6, bytes.length);
        assertEquals((byte) 0x45, bytes[0]); // 0x40 + 5
        assertEquals('h', (char) bytes[1]);
        assertEquals('o', (char) bytes[5]);
    }

    @Test
    public void testGenShortString_126bytes() throws Exception {
        String s = "A".repeat(126);
        byte[] bytes = gen(g -> g.writeString(s));
        assertEquals(127, bytes.length);
        assertEquals((byte) 0xbe, bytes[0]); // 0x40 + 126
    }

    @Test
    public void testGenLongString_127bytes() throws Exception {
        String s = "B".repeat(127);
        byte[] bytes = gen(g -> g.writeString(s));
        assertEquals(1 + 8 + 127, bytes.length);
        assertEquals((byte) 0xbf, bytes[0]);
        // LE 127 = 7F 00 00 00 00 00 00 00
        assertEquals((byte) 0x7F, bytes[1]);
        for (int i = 2; i < 9; i++) assertEquals((byte) 0, bytes[i]);
    }

    // =========================================================
    // BINARY
    // =========================================================

    @Test
    public void testGenBinary_small() throws Exception {
        byte[] data = { 0x01, 0x02, 0x03 };
        byte[] bytes = gen(g -> g.writeBinary(data));
        // 0xc0 = binary, 1-byte length; length = 3; then data
        assertEquals(1 + 1 + 3, bytes.length);
        assertEquals((byte) 0xc0, bytes[0]);
        assertEquals((byte) 3, bytes[1]);
        assertEquals((byte) 1, bytes[2]);
        assertEquals((byte) 2, bytes[3]);
        assertEquals((byte) 3, bytes[4]);
    }

    @Test
    public void testGenBinary_256bytes() throws Exception {
        byte[] data = new byte[256]; // length > 255, needs 2-byte length field
        java.util.Arrays.fill(data, (byte) 0x55);
        byte[] bytes = gen(g -> g.writeBinary(data));
        // 0xc1 = binary, 2-byte length; length = 256 = 0x0100 in LE: 00 01
        assertEquals(1 + 2 + 256, bytes.length);
        assertEquals((byte) 0xc1, bytes[0]); // VPACK_BINARY_FIRST + 2 - 1 = 0xc1
        assertEquals((byte) 0x00, bytes[1]); // LE low byte of 256
        assertEquals((byte) 0x01, bytes[2]); // LE high byte of 256
    }

    // =========================================================
    // BIGDECIMAL
    // =========================================================

    @Test
    public void testGenBigDecimal_12345() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(new BigDecimal("12345")));
        // Trailing-zero approach: "12345" (odd) → "123450", exponent = -1
        // Spec example: c8 03 ff ff ff ff 12 34 50
        assertEquals((byte) 0xc8, bytes[0]); // positive BCD, 1-byte mantissa len
        assertEquals((byte) 0x03, bytes[1]); // mantissa len = 3
        // exponent = -1: FF FF FF FF in 4-byte LE
        assertEquals((byte) 0xFF, bytes[2]);
        assertEquals((byte) 0xFF, bytes[3]);
        assertEquals((byte) 0xFF, bytes[4]);
        assertEquals((byte) 0xFF, bytes[5]);
        // mantissa: 12 34 50 ("123450" packed)
        assertEquals((byte) 0x12, bytes[6]);
        assertEquals((byte) 0x34, bytes[7]);
        assertEquals((byte) 0x50, bytes[8]);
    }

    @Test
    public void testGenBigInteger_small() throws Exception {
        // BigInteger that fits in long - 42 > 9 so uses signed 1-byte encoding
        byte[] bytes = gen(g -> g.writeNumber(BigInteger.valueOf(42)));
        assertEquals(2, bytes.length); // 0x20 (1-byte signed) + 42
        assertEquals((byte) 0x20, bytes[0]);
        assertEquals((byte) 42, bytes[1]);
    }

    @Test
    public void testGenBigInteger_large() throws Exception {
        // BigInteger that doesn't fit in long
        BigInteger big = new BigInteger("12345678901234567890");
        byte[] bytes = gen(g -> g.writeNumber(big));
        // Should produce BCD encoding
        assertTrue(bytes.length > 1);
        // First byte should be a BCD type
        int tb = bytes[0] & 0xFF;
        assertTrue(tb >= 0xc8 && tb <= 0xcf, "Expected positive BCD type, got 0x" + Integer.toHexString(tb));
    }
}
