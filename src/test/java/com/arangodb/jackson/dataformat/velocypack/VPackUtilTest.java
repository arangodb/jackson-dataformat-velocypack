package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for VPackUtil methods.
 */
public class VPackUtilTest
{
    @Test
    public void testUnsignedByteWidth() {
        assertEquals(1, VPackUtil.unsignedByteWidth(0));
        assertEquals(1, VPackUtil.unsignedByteWidth(1));
        assertEquals(1, VPackUtil.unsignedByteWidth(255));
        assertEquals(2, VPackUtil.unsignedByteWidth(256));
        assertEquals(2, VPackUtil.unsignedByteWidth(65535));
        assertEquals(4, VPackUtil.unsignedByteWidth(65536));
        assertEquals(4, VPackUtil.unsignedByteWidth(0xFFFFFFFFL));
        assertEquals(8, VPackUtil.unsignedByteWidth(0x100000000L));
        assertEquals(8, VPackUtil.unsignedByteWidth(Long.MAX_VALUE));
    }

    @Test
    public void testSignedByteWidth() {
        assertEquals(1, VPackUtil.signedByteWidth(0));
        assertEquals(1, VPackUtil.signedByteWidth(127));
        assertEquals(1, VPackUtil.signedByteWidth(-128));
        assertEquals(2, VPackUtil.signedByteWidth(128));
        assertEquals(2, VPackUtil.signedByteWidth(-129));
        assertEquals(2, VPackUtil.signedByteWidth(32767));
        assertEquals(2, VPackUtil.signedByteWidth(-32768));
        assertEquals(4, VPackUtil.signedByteWidth(32768));
        assertEquals(4, VPackUtil.signedByteWidth(-32769));
        assertEquals(4, VPackUtil.signedByteWidth(Integer.MAX_VALUE));
        assertEquals(4, VPackUtil.signedByteWidth(Integer.MIN_VALUE));
        assertEquals(8, VPackUtil.signedByteWidth((long) Integer.MAX_VALUE + 1));
        assertEquals(8, VPackUtil.signedByteWidth((long) Integer.MIN_VALUE - 1));
        assertEquals(8, VPackUtil.signedByteWidth(Long.MAX_VALUE));
        assertEquals(8, VPackUtil.signedByteWidth(Long.MIN_VALUE));
    }

    @Test
    public void testWriteLeUnsigned_1byte() {
        byte[] buf = new byte[1];
        VPackUtil.writeLeUnsigned(buf, 0, 0xABL, 1);
        assertEquals((byte) 0xAB, buf[0]);
    }

    @Test
    public void testWriteLeUnsigned_2bytes() {
        byte[] buf = new byte[2];
        VPackUtil.writeLeUnsigned(buf, 0, 0x1234L, 2);
        assertEquals((byte) 0x34, buf[0]);
        assertEquals((byte) 0x12, buf[1]);
    }

    @Test
    public void testWriteLeUnsigned_4bytes() {
        byte[] buf = new byte[4];
        VPackUtil.writeLeUnsigned(buf, 0, 0x12345678L, 4);
        assertEquals((byte) 0x78, buf[0]);
        assertEquals((byte) 0x56, buf[1]);
        assertEquals((byte) 0x34, buf[2]);
        assertEquals((byte) 0x12, buf[3]);
    }

    @Test
    public void testWriteLeUnsigned_8bytes() {
        byte[] buf = new byte[8];
        VPackUtil.writeLeUnsigned(buf, 0, 0x0102030405060708L, 8);
        assertEquals((byte) 0x08, buf[0]);
        assertEquals((byte) 0x07, buf[1]);
        assertEquals((byte) 0x06, buf[2]);
        assertEquals((byte) 0x05, buf[3]);
        assertEquals((byte) 0x04, buf[4]);
        assertEquals((byte) 0x03, buf[5]);
        assertEquals((byte) 0x02, buf[6]);
        assertEquals((byte) 0x01, buf[7]);
    }

    @Test
    public void testReadLeUnsigned() {
        byte[] buf = { 0x34, 0x12 };
        assertEquals(0x1234L, VPackUtil.readLeUnsigned(buf, 0, 2));
    }

    @Test
    public void testReadLeSigned_positive() {
        byte[] buf = { 0x01 };
        assertEquals(1L, VPackUtil.readLeSigned(buf, 0, 1));
    }

    @Test
    public void testReadLeSigned_negative_1byte() {
        byte[] buf = { (byte) 0xFF };
        assertEquals(-1L, VPackUtil.readLeSigned(buf, 0, 1));
    }

    @Test
    public void testReadLeSigned_negative_4bytes() {
        byte[] buf = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        assertEquals(-1L, VPackUtil.readLeSigned(buf, 0, 4));
    }

    @Test
    public void testVByteEncode_decode_small() {
        // Value < 128: single byte
        byte[] encoded = VPackUtil.encodeVByte(42L);
        assertEquals(1, encoded.length);
        assertEquals((byte) 42, encoded[0]);

        int[] outLen = new int[1];
        long decoded = VPackUtil.decodeVByte(encoded, 0, outLen);
        assertEquals(42L, decoded);
        assertEquals(1, outLen[0]);
    }

    @Test
    public void testVByteEncode_decode_large() {
        // Value >= 128: multiple bytes
        byte[] encoded = VPackUtil.encodeVByte(200L);
        assertEquals(2, encoded.length);

        int[] outLen = new int[1];
        long decoded = VPackUtil.decodeVByte(encoded, 0, outLen);
        assertEquals(200L, decoded);
        assertEquals(2, outLen[0]);
    }

    @Test
    public void testVByteReverse() {
        // From spec: compact array [1, 16] => nritems = 2
        // encodeVByte(2) = [0x02] (single byte, fits in 7 bits)
        // reverseBytes([0x02]) = [0x02]
        byte[] nrVB = VPackUtil.encodeVByte(2L);
        assertEquals(1, nrVB.length);
        assertEquals((byte) 0x02, nrVB[0]);

        // Reverse decode: last byte (high bit clear) = LSBs
        byte[] data = { 0x31, 0x28, 0x10, 0x02 }; // content + nritems
        int[] outLen = new int[1];
        long nr = VPackUtil.decodeVByteReverse(data, data.length, outLen);
        assertEquals(2L, nr);
        assertEquals(1, outLen[0]);
    }

    @Test
    public void testBcdEncodeDecodeSimple() {
        // 12345: digits = "12345" (odd length), trailing zero appended → "123450", exponent = -1
        // This matches the spec's second example: c8 03 ff ff ff ff 12 34 50
        BigDecimal val = new BigDecimal("12345");
        int[] outExp = new int[1];
        byte[] bcd = VPackUtil.encodeBcd(val, outExp);
        assertEquals(-1, outExp[0]);
        assertEquals(3, bcd.length);
        assertEquals((byte) 0x12, bcd[0]);
        assertEquals((byte) 0x34, bcd[1]);
        assertEquals((byte) 0x50, bcd[2]);

        // Decode it back
        BigDecimal decoded = VPackUtil.decodeBcd(bcd, outExp[0], false);
        assertEquals(0, val.compareTo(decoded));
    }

    @Test
    public void testBcdDecodeUnholyNibble_c8_03_00_00_00_00_01_23_45() {
        // From spec: c8 03 00 00 00 00 01 23 45 -> 12345
        // bcd bytes: [0x01, 0x23, 0x45], exponent: 0
        byte[] bcd = { 0x01, 0x23, 0x45 };
        BigDecimal result = VPackUtil.decodeBcd(bcd, 0, false);
        assertEquals(0, new BigDecimal("12345").compareTo(result));
    }

    @Test
    public void testBcdDecodeUnholyNibble_c8_03_ff_ff_ff_ff_12_34_50() {
        // From spec: c8 03 ff ff ff ff 12 34 50 -> 12345 (exponent -1)
        // Mantissa = 123450, exponent = -1
        // => 123450 * 10^(-1) = 12345.0
        byte[] bcd = { 0x12, 0x34, 0x50 };
        int exponent = -1;
        BigDecimal result = VPackUtil.decodeBcd(bcd, exponent, false);
        assertEquals(0, new BigDecimal("12345").compareTo(result));
    }

    @Test
    public void testBcdNegative() {
        BigDecimal val = new BigDecimal("-42");
        int[] outExp = new int[1];
        byte[] bcd = VPackUtil.encodeBcd(val, outExp);
        BigDecimal decoded = VPackUtil.decodeBcd(bcd, outExp[0], true);
        assertEquals(0, val.compareTo(decoded));
    }

    @Test
    public void testBcdZero() {
        BigDecimal val = BigDecimal.ZERO;
        int[] outExp = new int[1];
        byte[] bcd = VPackUtil.encodeBcd(val, outExp);
        BigDecimal decoded = VPackUtil.decodeBcd(bcd, outExp[0], false);
        assertEquals(0, BigDecimal.ZERO.compareTo(decoded));
    }

    @Test
    public void testBcdEmptyBytes() {
        BigDecimal decoded = VPackUtil.decodeBcd(new byte[0], 0, false);
        assertEquals(0, BigDecimal.ZERO.compareTo(decoded));
        BigDecimal decodedNeg = VPackUtil.decodeBcd(new byte[0], 0, true);
        // negative zero is still zero
        assertEquals(0, BigDecimal.ZERO.compareTo(decodedNeg));
    }

    @Test
    public void testVByteEncodeNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> VPackUtil.encodeVByte(-1L));
    }

    @Test
    public void testBytesNeededForUnsigned() {
        assertEquals(1, VPackUtil.bytesNeededForUnsigned(0));
        assertEquals(1, VPackUtil.bytesNeededForUnsigned(255));
        assertEquals(2, VPackUtil.bytesNeededForUnsigned(256));
        assertEquals(4, VPackUtil.bytesNeededForUnsigned(65536));
        assertEquals(8, VPackUtil.bytesNeededForUnsigned(0x100000000L));
    }
}
