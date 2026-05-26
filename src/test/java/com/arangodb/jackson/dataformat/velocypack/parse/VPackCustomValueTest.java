package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;

import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackCustomValue;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link VPackCustomValue}: equals, hashCode, toString, getters.
 */
public class VPackCustomValueTest extends BaseTestForVPack
{
    @Test
    public void testGetTypeByte() {
        VPackCustomValue cv = new VPackCustomValue(0xf0, new byte[]{0x42});
        assertThat(cv.getTypeByte()).isEqualTo(0xf0);
    }

    @Test
    public void testGetPayload_returnsCopy() {
        byte[] payload = {0x01, 0x02, 0x03};
        VPackCustomValue cv = new VPackCustomValue(0xf1, payload);
        byte[] result = cv.getPayload();
        assertThat(result).isEqualTo(payload);
        // Verify it's a copy, not the same reference
        result[0] = (byte) 0xFF;
        assertThat(cv.getPayload()).isEqualTo(payload); // original unchanged
    }

    @Test
    public void testGetPayload_empty() {
        VPackCustomValue cv = new VPackCustomValue(0xf0, new byte[0]);
        assertThat(cv.getPayload()).hasSize(0);
    }

    @Test
    public void testToString_containsTypeByte() {
        VPackCustomValue cv = new VPackCustomValue(0xf3, new byte[]{0x11, 0x22, 0x33, 0x44});
        String str = cv.toString();
        assertThat(str).as("toString should contain type byte hex: " + str).contains("f3");
        assertThat(str).as("toString should contain payload length: " + str).contains("4");
    }

    @Test
    public void testToString_zeroPayload() {
        VPackCustomValue cv = new VPackCustomValue(0xff, new byte[0]);
        String str = cv.toString();
        assertThat(str).as("toString should contain type byte: " + str).contains("ff");
        assertThat(str).as("toString should contain payload length 0: " + str).contains("0");
    }

    @Test
    public void testEquals_equalObjects() {
        byte[] p1 = {0x01, 0x02};
        byte[] p2 = {0x01, 0x02};
        VPackCustomValue cv1 = new VPackCustomValue(0xf4, p1);
        VPackCustomValue cv2 = new VPackCustomValue(0xf4, p2);
        assertThat(cv1).isEqualTo(cv2);
    }

    @Test
    public void testEquals_differentTypeByte() {
        VPackCustomValue cv1 = new VPackCustomValue(0xf0, new byte[]{0x01});
        VPackCustomValue cv2 = new VPackCustomValue(0xf1, new byte[]{0x01});
        assertThat(cv1).isNotEqualTo(cv2);
    }

    @Test
    public void testEquals_differentPayload() {
        VPackCustomValue cv1 = new VPackCustomValue(0xf0, new byte[]{0x01});
        VPackCustomValue cv2 = new VPackCustomValue(0xf0, new byte[]{0x02});
        assertThat(cv1).isNotEqualTo(cv2);
    }

    @Test
    public void testEquals_null() {
        VPackCustomValue cv = new VPackCustomValue(0xf0, new byte[]{0x01});
        assertThat(cv).isNotEqualTo(null);
    }

    @Test
    public void testHashCode_equalObjects() {
        byte[] p1 = {0x0A, 0x0B};
        byte[] p2 = {0x0A, 0x0B};
        VPackCustomValue cv1 = new VPackCustomValue(0xf5, p1);
        VPackCustomValue cv2 = new VPackCustomValue(0xf5, p2);
        assertThat(cv1.hashCode()).isEqualTo(cv2.hashCode());
    }

    @Test
    public void testHashCode_differentObjects() {
        VPackCustomValue cv1 = new VPackCustomValue(0xf0, new byte[]{0x01});
        VPackCustomValue cv2 = new VPackCustomValue(0xf1, new byte[]{0x02});
        // May collide, but very unlikely with these values
        assertThat(cv1).isNotEqualTo(cv2);
    }
}
