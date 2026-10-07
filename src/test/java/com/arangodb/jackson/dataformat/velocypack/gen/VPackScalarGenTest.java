package com.arangodb.jackson.dataformat.velocypack.gen;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackWriteFeature;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for generating VPack scalar values.
 */
public class VPackScalarGenTest extends BaseTestForVPack
{
    private byte[] gen(WriteAction action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            action.write(g);
        }
        return out.toByteArray();
    }

    @FunctionalInterface
    interface WriteAction {
        void write(JsonGenerator g);
    }

    // =========================================================
    // NULL
    // =========================================================

    @Test
    public void testGenNull() {
        byte[] bytes = gen(JsonGenerator::writeNull);
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x18);
    }

    // =========================================================
    // BOOLEANS
    // =========================================================

    @Test
    public void testGenFalse() {
        byte[] bytes = gen(g -> g.writeBoolean(false));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x19);
    }

    @Test
    public void testGenTrue() {
        byte[] bytes = gen(g -> g.writeBoolean(true));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x1a);
    }

    // =========================================================
    // SMALL INTEGERS (WRITE_MIN_INT_WIDTH enabled by default)
    // =========================================================

    @Test
    public void testGenSmallInt_0() {
        byte[] bytes = gen(g -> g.writeNumber(0));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x30);
    }

    @Test
    public void testGenSmallInt_9() {
        byte[] bytes = gen(g -> g.writeNumber(9));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x39);
    }

    @Test
    public void testGenSmallNeg_minus1() {
        byte[] bytes = gen(g -> g.writeNumber(-1));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x3f); // 0x40 + (-1) = 0x3f
    }

    @Test
    public void testGenSmallNeg_minus6() {
        byte[] bytes = gen(g -> g.writeNumber(-6));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x3a); // 0x40 + (-6) = 0x3a
    }

    // =========================================================
    // LARGER INTEGERS
    // =========================================================

    @Test
    public void testGenInt_10_usesSignedEncoding() {
        // 10 doesn't fit in small int (0-9), so uses signed encoding
        byte[] bytes = gen(g -> g.writeNumber(10));
        assertThat(bytes).hasSize(2);
        assertThat(bytes[0]).isEqualTo((byte) 0x20); // 0x20 = 1-byte signed
        assertThat(bytes[1]).isEqualTo((byte) 10);
    }

    @Test
    public void testGenInt_minus7_usesSignedEncoding() {
        // -7 doesn't fit in small neg (-6..-1), so uses signed encoding
        byte[] bytes = gen(g -> g.writeNumber(-7));
        assertThat(bytes).hasSize(2);
        assertThat(bytes[0]).isEqualTo((byte) 0x20);
        assertThat(bytes[1]).isEqualTo((byte) -7);
    }

    @Test
    public void testGenLong_maxValue() {
        byte[] bytes = gen(g -> g.writeNumber(Long.MAX_VALUE));
        assertThat(bytes).hasSize(9);
        assertThat(bytes[0]).isEqualTo((byte) 0x27); // 8-byte signed
        // Check LE encoding: 7F FF FF FF FF FF FF FF
        assertThat(bytes[1]).isEqualTo((byte) 0xFF);
        assertThat(bytes[2]).isEqualTo((byte) 0xFF);
        assertThat(bytes[3]).isEqualTo((byte) 0xFF);
        assertThat(bytes[4]).isEqualTo((byte) 0xFF);
        assertThat(bytes[5]).isEqualTo((byte) 0xFF);
        assertThat(bytes[6]).isEqualTo((byte) 0xFF);
        assertThat(bytes[7]).isEqualTo((byte) 0xFF);
        assertThat(bytes[8]).isEqualTo((byte) 0x7F);
    }

    @Test
    public void testGenLong_minValue() {
        byte[] bytes = gen(g -> g.writeNumber(Long.MIN_VALUE));
        assertThat(bytes).hasSize(9);
        assertThat(bytes[0]).isEqualTo((byte) 0x27); // 8-byte signed
        // Long.MIN_VALUE = 0x8000000000000000 in LE: 00 00 00 00 00 00 00 80
        assertThat(bytes[1]).isEqualTo((byte) 0x00);
        assertThat(bytes[8]).isEqualTo((byte) 0x80);
    }

    @Test
    public void testGenNoMinIntWidth() {
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
        assertThat(bytes).hasSize(2);
        assertThat(bytes[0]).isEqualTo((byte) 0x20);
    }

    // =========================================================
    // DOUBLE
    // =========================================================

    @Test
    public void testGenDouble_zero() {
        byte[] bytes = gen(g -> g.writeNumber(0.0));
        assertThat(bytes).hasSize(9);
        assertThat(bytes[0]).isEqualTo((byte) 0x1b);
        // 0.0 = all zeros
        for (int i = 1; i < 9; i++) {
            assertThat(bytes[i]).isEqualTo((byte) 0);
        }
    }

    @Test
    public void testGenDouble_one() {
        byte[] bytes = gen(g -> g.writeNumber(1.0));
        assertThat(bytes).hasSize(9);
        assertThat(bytes[0]).isEqualTo((byte) 0x1b);
        // 1.0 = 0x3FF0000000000000 in LE: 00 00 00 00 00 00 F0 3F
        assertThat(bytes[1]).isEqualTo((byte) 0x00);
        assertThat(bytes[2]).isEqualTo((byte) 0x00);
        assertThat(bytes[3]).isEqualTo((byte) 0x00);
        assertThat(bytes[4]).isEqualTo((byte) 0x00);
        assertThat(bytes[5]).isEqualTo((byte) 0x00);
        assertThat(bytes[6]).isEqualTo((byte) 0x00);
        assertThat(bytes[7]).isEqualTo((byte) 0xF0);
        assertThat(bytes[8]).isEqualTo((byte) 0x3F);
    }

    @Test
    public void testGenDouble_NaN() {
        byte[] bytes = gen(g -> g.writeNumber(Double.NaN));
        assertThat(bytes).hasSize(9);
        assertThat(bytes[0]).isEqualTo((byte) 0x1b);
        long expectedBits = Double.doubleToRawLongBits(Double.NaN);
        for (int i = 0; i < 8; i++) {
            assertThat(bytes[1 + i]).isEqualTo((byte) (expectedBits >>> (8 * i)));
        }
    }

    // =========================================================
    // STRINGS
    // =========================================================

    @Test
    public void testGenEmptyString() {
        byte[] bytes = gen(g -> g.writeString(""));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x40);
    }

    @Test
    public void testGenShortString_hello() {
        byte[] bytes = gen(g -> g.writeString("hello"));
        assertThat(bytes).hasSize(6);
        assertThat(bytes[0]).isEqualTo((byte) 0x45); // 0x40 + 5
        assertThat((char) bytes[1]).isEqualTo('h');
        assertThat((char) bytes[5]).isEqualTo('o');
    }

    @Test
    public void testGenShortString_126bytes() {
        String s = "A".repeat(126);
        byte[] bytes = gen(g -> g.writeString(s));
        assertThat(bytes).hasSize(127);
        assertThat(bytes[0]).isEqualTo((byte) 0xbe); // 0x40 + 126
    }

    @Test
    public void testGenLongString_127bytes() {
        String s = "B".repeat(127);
        byte[] bytes = gen(g -> g.writeString(s));
        assertThat(bytes).hasSize(1 + 8 + 127);
        assertThat(bytes[0]).isEqualTo((byte) 0xbf);
        // LE 127 = 7F 00 00 00 00 00 00 00
        assertThat(bytes[1]).isEqualTo((byte) 0x7F);
        for (int i = 2; i < 9; i++) assertThat(bytes[i]).isEqualTo((byte) 0);
    }

    // =========================================================
    // BINARY
    // =========================================================

    @Test
    public void testGenBinary_small() {
        byte[] data = { 0x01, 0x02, 0x03 };
        byte[] bytes = gen(g -> g.writeBinary(data));
        // 0xc0 = binary, 1-byte length; length = 3; then data
        assertThat(bytes).hasSize(1 + 1 + 3);
        assertThat(bytes[0]).isEqualTo((byte) 0xc0);
        assertThat(bytes[1]).isEqualTo((byte) 3);
        assertThat(bytes[2]).isEqualTo((byte) 1);
        assertThat(bytes[3]).isEqualTo((byte) 2);
        assertThat(bytes[4]).isEqualTo((byte) 3);
    }

    @Test
    public void testGenBinary_256bytes() {
        byte[] data = new byte[256]; // length > 255, needs 2-byte length field
        java.util.Arrays.fill(data, (byte) 0x55);
        byte[] bytes = gen(g -> g.writeBinary(data));
        // 0xc1 = binary, 2-byte length; length = 256 = 0x0100 in LE: 00 01
        assertThat(bytes).hasSize(1 + 2 + 256);
        assertThat(bytes[0]).isEqualTo((byte) 0xc1); // VPACK_BINARY_FIRST + 2 - 1 = 0xc1
        assertThat(bytes[1]).isEqualTo((byte) 0x00); // LE low byte of 256
        assertThat(bytes[2]).isEqualTo((byte) 0x01); // LE high byte of 256
    }

    // =========================================================
    // BIGDECIMAL
    // =========================================================

    @Test
    public void testGenBigDecimal_12345() {
        byte[] bytes = gen(g -> g.writeNumber(new BigDecimal("12345")));
        // scale=0 (integer), no trailing-zero removal; digits="12345" (odd) -> prepend "012345"
        // exponent=0, mantLen=3
        // type=0xc8, mantLen=3, exp=0 (00 00 00 00), bcd=01 23 45
        assertThat(bytes[0]).isEqualTo((byte) 0xc8); // positive BCD, 1-byte mantissa len
        assertThat(bytes[1]).isEqualTo((byte) 0x03); // mantissa len = 3
        // exponent = 0: 00 00 00 00 in 4-byte LE
        assertThat(bytes[2]).isEqualTo((byte) 0x00);
        assertThat(bytes[3]).isEqualTo((byte) 0x00);
        assertThat(bytes[4]).isEqualTo((byte) 0x00);
        assertThat(bytes[5]).isEqualTo((byte) 0x00);
        // mantissa: 01 23 45 ("012345" packed)
        assertThat(bytes[6]).isEqualTo((byte) 0x01);
        assertThat(bytes[7]).isEqualTo((byte) 0x23);
        assertThat(bytes[8]).isEqualTo((byte) 0x45);
    }

    @Test
    public void testGenBigInteger_small() {
        // BigInteger that fits in long - 42 > 9 so uses signed 1-byte encoding
        byte[] bytes = gen(g -> g.writeNumber(BigInteger.valueOf(42)));
        assertThat(bytes).hasSize(2); // 0x20 (1-byte signed) + 42
        assertThat(bytes[0]).isEqualTo((byte) 0x20);
        assertThat(bytes[1]).isEqualTo((byte) 42);
    }

    @Test
    public void testGenBigInteger_large() {
        // BigInteger that doesn't fit in long
        BigInteger big = new BigInteger("12345678901234567890");
        byte[] bytes = gen(g -> g.writeNumber(big));
        // Should produce BCD encoding
        assertThat(bytes).hasSizeGreaterThan(1);
        // First byte should be a BCD type
        int tb = bytes[0] & 0xFF;
        assertThat(tb).as("Expected positive BCD type, got 0x" + Integer.toHexString(tb)).isBetween(0xc8, 0xcf);
    }
}
