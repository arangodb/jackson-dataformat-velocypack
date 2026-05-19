package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;

import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackCustomValue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link VPackCustomValue}: equals, hashCode, toString, getters.
 */
public class VPackCustomValueTest extends BaseTestForVPack
{
    @Test
    public void testGetTypeByte() {
        VPackCustomValue cv = new VPackCustomValue(0xf0, new byte[]{0x42});
        assertEquals(0xf0, cv.getTypeByte());
    }

    @Test
    public void testGetPayload_returnsCopy() {
        byte[] payload = {0x01, 0x02, 0x03};
        VPackCustomValue cv = new VPackCustomValue(0xf1, payload);
        byte[] result = cv.getPayload();
        assertArrayEquals(payload, result);
        // Verify it's a copy, not the same reference
        result[0] = (byte) 0xFF;
        assertArrayEquals(payload, cv.getPayload()); // original unchanged
    }

    @Test
    public void testGetPayload_empty() {
        VPackCustomValue cv = new VPackCustomValue(0xf0, new byte[0]);
        assertEquals(0, cv.getPayload().length);
    }

    @Test
    public void testToString_containsTypeByte() {
        VPackCustomValue cv = new VPackCustomValue(0xf3, new byte[]{0x11, 0x22, 0x33, 0x44});
        String str = cv.toString();
        assertTrue(str.contains("f3"), "toString should contain type byte hex: " + str);
        assertTrue(str.contains("4"), "toString should contain payload length: " + str);
    }

    @Test
    public void testToString_zeroPayload() {
        VPackCustomValue cv = new VPackCustomValue(0xff, new byte[0]);
        String str = cv.toString();
        assertTrue(str.contains("ff"), "toString should contain type byte: " + str);
        assertTrue(str.contains("0"), "toString should contain payload length 0: " + str);
    }

    @Test
    public void testEquals_sameObject() {
        VPackCustomValue cv = new VPackCustomValue(0xf0, new byte[]{0x01});
        assertEquals(cv, cv);
    }

    @Test
    public void testEquals_equalObjects() {
        byte[] p1 = {0x01, 0x02};
        byte[] p2 = {0x01, 0x02};
        VPackCustomValue cv1 = new VPackCustomValue(0xf4, p1);
        VPackCustomValue cv2 = new VPackCustomValue(0xf4, p2);
        assertEquals(cv1, cv2);
    }

    @Test
    public void testEquals_differentTypeByte() {
        VPackCustomValue cv1 = new VPackCustomValue(0xf0, new byte[]{0x01});
        VPackCustomValue cv2 = new VPackCustomValue(0xf1, new byte[]{0x01});
        assertNotEquals(cv1, cv2);
    }

    @Test
    public void testEquals_differentPayload() {
        VPackCustomValue cv1 = new VPackCustomValue(0xf0, new byte[]{0x01});
        VPackCustomValue cv2 = new VPackCustomValue(0xf0, new byte[]{0x02});
        assertNotEquals(cv1, cv2);
    }

    @Test
    public void testEquals_null() {
        VPackCustomValue cv = new VPackCustomValue(0xf0, new byte[]{0x01});
        assertNotEquals(null, cv);
    }

    @Test
    public void testEquals_differentType() {
        VPackCustomValue cv = new VPackCustomValue(0xf0, new byte[]{0x01});
        assertNotEquals("not a custom value", cv);
    }

    @Test
    public void testHashCode_equalObjects() {
        byte[] p1 = {0x0A, 0x0B};
        byte[] p2 = {0x0A, 0x0B};
        VPackCustomValue cv1 = new VPackCustomValue(0xf5, p1);
        VPackCustomValue cv2 = new VPackCustomValue(0xf5, p2);
        assertEquals(cv1.hashCode(), cv2.hashCode());
    }

    @Test
    public void testHashCode_differentObjects() {
        VPackCustomValue cv1 = new VPackCustomValue(0xf0, new byte[]{0x01});
        VPackCustomValue cv2 = new VPackCustomValue(0xf1, new byte[]{0x02});
        // May collide, but very unlikely with these values
        assertNotEquals(cv1, cv2);
    }
}
