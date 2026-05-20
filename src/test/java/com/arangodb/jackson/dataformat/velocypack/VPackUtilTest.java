package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for VPackUtil methods.
 */
public class VPackUtilTest
{
    @Test
    public void testUnsignedByteWidth() {
        assertThat(VPackUtil.unsignedByteWidth(0)).isEqualTo(1);
        assertThat(VPackUtil.unsignedByteWidth(1)).isEqualTo(1);
        assertThat(VPackUtil.unsignedByteWidth(255)).isEqualTo(1);
        assertThat(VPackUtil.unsignedByteWidth(256)).isEqualTo(2);
        assertThat(VPackUtil.unsignedByteWidth(65535)).isEqualTo(2);
        assertThat(VPackUtil.unsignedByteWidth(65536)).isEqualTo(4);
        assertThat(VPackUtil.unsignedByteWidth(0xFFFFFFFFL)).isEqualTo(4);
        assertThat(VPackUtil.unsignedByteWidth(0x100000000L)).isEqualTo(8);
        assertThat(VPackUtil.unsignedByteWidth(Long.MAX_VALUE)).isEqualTo(8);
    }

    @Test
    public void testSignedByteWidth() {
        assertThat(VPackUtil.signedByteWidth(0)).isEqualTo(1);
        assertThat(VPackUtil.signedByteWidth(127)).isEqualTo(1);
        assertThat(VPackUtil.signedByteWidth(-128)).isEqualTo(1);
        assertThat(VPackUtil.signedByteWidth(128)).isEqualTo(2);
        assertThat(VPackUtil.signedByteWidth(-129)).isEqualTo(2);
        assertThat(VPackUtil.signedByteWidth(32767)).isEqualTo(2);
        assertThat(VPackUtil.signedByteWidth(-32768)).isEqualTo(2);
        assertThat(VPackUtil.signedByteWidth(32768)).isEqualTo(4);
        assertThat(VPackUtil.signedByteWidth(-32769)).isEqualTo(4);
        assertThat(VPackUtil.signedByteWidth(Integer.MAX_VALUE)).isEqualTo(4);
        assertThat(VPackUtil.signedByteWidth(Integer.MIN_VALUE)).isEqualTo(4);
        assertThat(VPackUtil.signedByteWidth((long) Integer.MAX_VALUE + 1)).isEqualTo(8);
        assertThat(VPackUtil.signedByteWidth((long) Integer.MIN_VALUE - 1)).isEqualTo(8);
        assertThat(VPackUtil.signedByteWidth(Long.MAX_VALUE)).isEqualTo(8);
        assertThat(VPackUtil.signedByteWidth(Long.MIN_VALUE)).isEqualTo(8);
    }

    @Test
    public void testWriteLeUnsigned_1byte() {
        byte[] buf = new byte[1];
        VPackUtil.writeLeUnsigned(buf, 0, 0xABL, 1);
        assertThat(buf[0]).isEqualTo((byte) 0xAB);
    }

    @Test
    public void testWriteLeUnsigned_2bytes() {
        byte[] buf = new byte[2];
        VPackUtil.writeLeUnsigned(buf, 0, 0x1234L, 2);
        assertThat(buf[0]).isEqualTo((byte) 0x34);
        assertThat(buf[1]).isEqualTo((byte) 0x12);
    }

    @Test
    public void testWriteLeUnsigned_4bytes() {
        byte[] buf = new byte[4];
        VPackUtil.writeLeUnsigned(buf, 0, 0x12345678L, 4);
        assertThat(buf[0]).isEqualTo((byte) 0x78);
        assertThat(buf[1]).isEqualTo((byte) 0x56);
        assertThat(buf[2]).isEqualTo((byte) 0x34);
        assertThat(buf[3]).isEqualTo((byte) 0x12);
    }

    @Test
    public void testWriteLeUnsigned_8bytes() {
        byte[] buf = new byte[8];
        VPackUtil.writeLeUnsigned(buf, 0, 0x0102030405060708L, 8);
        assertThat(buf[0]).isEqualTo((byte) 0x08);
        assertThat(buf[1]).isEqualTo((byte) 0x07);
        assertThat(buf[2]).isEqualTo((byte) 0x06);
        assertThat(buf[3]).isEqualTo((byte) 0x05);
        assertThat(buf[4]).isEqualTo((byte) 0x04);
        assertThat(buf[5]).isEqualTo((byte) 0x03);
        assertThat(buf[6]).isEqualTo((byte) 0x02);
        assertThat(buf[7]).isEqualTo((byte) 0x01);
    }

    @Test
    public void testReadLeUnsigned() {
        byte[] buf = { 0x34, 0x12 };
        assertThat(VPackUtil.readLeUnsigned(buf, 0, 2)).isEqualTo(0x1234L);
    }

    @Test
    public void testReadLeSigned_positive() {
        byte[] buf = { 0x01 };
        assertThat(VPackUtil.readLeSigned(buf, 0, 1)).isEqualTo(1L);
    }

    @Test
    public void testReadLeSigned_negative_1byte() {
        byte[] buf = { (byte) 0xFF };
        assertThat(VPackUtil.readLeSigned(buf, 0, 1)).isEqualTo(-1L);
    }

    @Test
    public void testReadLeSigned_negative_4bytes() {
        byte[] buf = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        assertThat(VPackUtil.readLeSigned(buf, 0, 4)).isEqualTo(-1L);
    }

    @Test
    public void testVByteEncode_decode_small() {
        // Value < 128: single byte
        byte[] encoded = VPackUtil.encodeVByte(42L);
        assertThat(encoded).hasSize(1);
        assertThat(encoded[0]).isEqualTo((byte) 42);

        int[] outLen = new int[1];
        long decoded = VPackUtil.decodeVByte(encoded, 0, outLen);
        assertThat(decoded).isEqualTo(42L);
        assertThat(outLen[0]).isEqualTo(1);
    }

    @Test
    public void testVByteEncode_decode_large() {
        // Value >= 128: multiple bytes
        byte[] encoded = VPackUtil.encodeVByte(200L);
        assertThat(encoded).hasSize(2);

        int[] outLen = new int[1];
        long decoded = VPackUtil.decodeVByte(encoded, 0, outLen);
        assertThat(decoded).isEqualTo(200L);
        assertThat(outLen[0]).isEqualTo(2);
    }

    @Test
    public void testVByteReverse() {
        // From spec: compact array [1, 16] => nritems = 2
        // encodeVByte(2) = [0x02] (single byte, fits in 7 bits)
        // reverseBytes([0x02]) = [0x02]
        byte[] nrVB = VPackUtil.encodeVByte(2L);
        assertThat(nrVB).hasSize(1);
        assertThat(nrVB[0]).isEqualTo((byte) 0x02);

        // Reverse decode: last byte (high bit clear) = LSBs
        byte[] data = { 0x31, 0x28, 0x10, 0x02 }; // content + nritems
        int[] outLen = new int[1];
        long nr = VPackUtil.decodeVByteReverse(data, data.length, outLen);
        assertThat(nr).isEqualTo(2L);
        assertThat(outLen[0]).isEqualTo(1);
    }

    @Test
    public void testBcdEncodeDecodeSimple() {
        // 12345: digits = "12345" (odd length), trailing zero appended → "123450", exponent = -1
        // This matches the spec's second example: c8 03 ff ff ff ff 12 34 50
        BigDecimal val = new BigDecimal("12345");
        int[] outExp = new int[1];
        byte[] bcd = VPackUtil.encodeBcd(val, outExp);
        assertThat(outExp[0]).isEqualTo(-1);
        assertThat(bcd).hasSize(3);
        assertThat(bcd[0]).isEqualTo((byte) 0x12);
        assertThat(bcd[1]).isEqualTo((byte) 0x34);
        assertThat(bcd[2]).isEqualTo((byte) 0x50);

        // Decode it back
        BigDecimal decoded = VPackUtil.decodeBcd(bcd, outExp[0], false);
        assertThat((decoded)).isEqualTo(val);
    }

    @Test
    public void testBcdDecodeUnholyNibble_c8_03_00_00_00_00_01_23_45() {
        // From spec: c8 03 00 00 00 00 01 23 45 -> 12345
        // bcd bytes: [0x01, 0x23, 0x45], exponent: 0
        byte[] bcd = { 0x01, 0x23, 0x45 };
        BigDecimal result = VPackUtil.decodeBcd(bcd, 0, false);
        assertThat(result).isEqualTo(("12345"));
    }

    @Test
    public void testBcdDecodeUnholyNibble_c8_03_ff_ff_ff_ff_12_34_50() {
        // From spec: c8 03 ff ff ff ff 12 34 50 -> 12345 (exponent -1)
        // Mantissa = 123450, exponent = -1
        // => 123450 * 10^(-1) = 12345.0
        byte[] bcd = { 0x12, 0x34, 0x50 };
        int exponent = -1;
        BigDecimal result = VPackUtil.decodeBcd(bcd, exponent, false);
        assertThat(result).isEqualTo("12345");
    }

    @Test
    public void testBcdNegative() {
        BigDecimal val = new BigDecimal("-42");
        int[] outExp = new int[1];
        byte[] bcd = VPackUtil.encodeBcd(val, outExp);
        BigDecimal decoded = VPackUtil.decodeBcd(bcd, outExp[0], true);
        assertThat((decoded)).isEqualTo(val);
    }

    @Test
    public void testBcdZero() {
        BigDecimal val = BigDecimal.ZERO;
        int[] outExp = new int[1];
        byte[] bcd = VPackUtil.encodeBcd(val, outExp);
        BigDecimal decoded = VPackUtil.decodeBcd(bcd, outExp[0], false);
        assertThat((decoded)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void testBcdEmptyBytes() {
        BigDecimal decoded = VPackUtil.decodeBcd(new byte[0], 0, false);
        assertThat((decoded)).isEqualTo(BigDecimal.ZERO);
        BigDecimal decodedNeg = VPackUtil.decodeBcd(new byte[0], 0, true);
        // negative zero is still zero
        assertThat((decodedNeg)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void testVByteEncodeNegativeThrows() {
        assertThatThrownBy(() -> VPackUtil.encodeVByte(-1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testBytesNeededForUnsigned() {
        assertThat(VPackUtil.bytesNeededForUnsigned(0)).isEqualTo(1);
        assertThat(VPackUtil.bytesNeededForUnsigned(255)).isEqualTo(1);
        assertThat(VPackUtil.bytesNeededForUnsigned(256)).isEqualTo(2);
        assertThat(VPackUtil.bytesNeededForUnsigned(65536)).isEqualTo(4);
        assertThat(VPackUtil.bytesNeededForUnsigned(0x100000000L)).isEqualTo(8);
    }
}
