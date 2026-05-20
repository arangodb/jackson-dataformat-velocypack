package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static com.arangodb.jackson.dataformat.velocypack.VPackConstants.*;

/**
 * Unit tests for VPackConstants - verifying all type byte ranges are correct per spec.
 */
public class VPackConstantsTest
{
    @Test
    public void testNullFalseTrueBytes() {
        assertThat(VPACK_NULL).isEqualTo(0x18);
        assertThat(VPACK_FALSE).isEqualTo(0x19);
        assertThat(VPACK_TRUE).isEqualTo(0x1a);
    }

    @Test
    public void testDoubleDate() {
        assertThat(VPACK_DOUBLE).isEqualTo(0x1b);
        assertThat(VPACK_DATE).isEqualTo(0x1c);
    }

    @Test
    public void testIllegalAndSpecial() {
        assertThat(VPACK_NONE).isEqualTo(0x00);
        assertThat(VPACK_ILLEGAL).isEqualTo(0x17);
        assertThat(VPACK_EXTERNAL).isEqualTo(0x1d);
        assertThat(VPACK_MIN_KEY).isEqualTo(0x1e);
        assertThat(VPACK_MAX_KEY).isEqualTo(0x1f);
    }

    @Test
    public void testArrayTypes() {
        assertThat(VPACK_ARRAY_EMPTY).isEqualTo(0x01);
        assertThat(VPACK_ARRAY_NO_IDX_FIRST).isEqualTo(0x02);
        assertThat(VPACK_ARRAY_NO_IDX_LAST).isEqualTo(0x05);
        assertThat(VPACK_ARRAY_IDX_FIRST).isEqualTo(0x06);
        assertThat(VPACK_ARRAY_IDX_LAST).isEqualTo(0x09);
        assertThat(VPACK_ARRAY_COMPACT).isEqualTo(0x13);
    }

    @Test
    public void testObjectTypes() {
        assertThat(VPACK_OBJECT_EMPTY).isEqualTo(0x0a);
        assertThat(VPACK_OBJECT_SORTED_FIRST).isEqualTo(0x0b);
        assertThat(VPACK_OBJECT_SORTED_LAST).isEqualTo(0x0e);
        assertThat(VPACK_OBJECT_UNSORTED_FIRST).isEqualTo(0x0f);
        assertThat(VPACK_OBJECT_UNSORTED_LAST).isEqualTo(0x12);
        assertThat(VPACK_OBJECT_COMPACT).isEqualTo(0x14);
    }

    @Test
    public void testIntegerTypes() {
        assertThat(VPACK_INT_SIGNED_FIRST).isEqualTo(0x20);
        assertThat(VPACK_INT_SIGNED_LAST).isEqualTo(0x27);
        assertThat(VPACK_INT_UNSIGNED_FIRST).isEqualTo(0x28);
        assertThat(VPACK_INT_UNSIGNED_LAST).isEqualTo(0x2f);
        assertThat(VPACK_SMALL_INT_FIRST).isEqualTo(0x30);
        assertThat(VPACK_SMALL_INT_LAST).isEqualTo(0x39);
        assertThat(VPACK_SMALL_NEG_FIRST).isEqualTo(0x3a);
        assertThat(VPACK_SMALL_NEG_LAST).isEqualTo(0x3f);
    }

    @Test
    public void testStringTypes() {
        assertThat(VPACK_STRING_SHORT_FIRST).isEqualTo(0x40);
        assertThat(VPACK_STRING_SHORT_LAST).isEqualTo(0xbe);
        assertThat(VPACK_STRING_SHORT_MAX_LEN).isEqualTo(126);
        assertThat(VPACK_STRING_LONG).isEqualTo(0xbf);
    }

    @Test
    public void testBinaryTypes() {
        assertThat(VPACK_BINARY_FIRST).isEqualTo(0xc0);
        assertThat(VPACK_BINARY_LAST).isEqualTo(0xc7);
    }

    @Test
    public void testBcdTypes() {
        assertThat(VPACK_BCD_POS_FIRST).isEqualTo(0xc8);
        assertThat(VPACK_BCD_POS_LAST).isEqualTo(0xcf);
        assertThat(VPACK_BCD_NEG_FIRST).isEqualTo(0xd0);
        assertThat(VPACK_BCD_NEG_LAST).isEqualTo(0xd7);
    }

    @Test
    public void testTagTypes() {
        assertThat(VPACK_TAG_1BYTE).isEqualTo(0xee);
        assertThat(VPACK_TAG_8BYTE).isEqualTo(0xef);
    }

    @Test
    public void testCustomTypes() {
        assertThat(VPACK_CUSTOM_FIRST).isEqualTo(0xf0);
        assertThat(VPACK_CUSTOM_LAST).isEqualTo(0xff);
        assertThat(VPACK_CUSTOM_1B).isEqualTo(0xf0);
        assertThat(VPACK_CUSTOM_2B).isEqualTo(0xf1);
        assertThat(VPACK_CUSTOM_4B).isEqualTo(0xf2);
        assertThat(VPACK_CUSTOM_8B).isEqualTo(0xf3);
        assertThat(VPACK_CUSTOM_LEN1_FIRST).isEqualTo(0xf4);
        assertThat(VPACK_CUSTOM_LEN1_LAST).isEqualTo(0xf6);
        assertThat(VPACK_CUSTOM_LEN2_FIRST).isEqualTo(0xf7);
        assertThat(VPACK_CUSTOM_LEN2_LAST).isEqualTo(0xf9);
        assertThat(VPACK_CUSTOM_LEN4_FIRST).isEqualTo(0xfa);
        assertThat(VPACK_CUSTOM_LEN4_LAST).isEqualTo(0xfc);
        assertThat(VPACK_CUSTOM_LEN8_FIRST).isEqualTo(0xfd);
        assertThat(VPACK_CUSTOM_LEN8_LAST).isEqualTo(0xff);
    }

    @Test
    public void testReservedRanges() {
        assertThat(VPACK_RESERVED_15).isEqualTo(0x15);
        assertThat(VPACK_RESERVED_16).isEqualTo(0x16);
        assertThat(VPACK_RESERVED_D8).isEqualTo(0xd8);
        assertThat(VPACK_RESERVED_ED).isEqualTo(0xed);
    }

    // Verify contiguous ranges
    @Test
    public void testSignedIntRange() {
        // 8 signed int types: 0x20-0x27
        assertThat(VPACK_INT_SIGNED_LAST - VPACK_INT_SIGNED_FIRST + 1).isEqualTo(8);
    }

    @Test
    public void testUnsignedIntRange() {
        // 8 unsigned int types: 0x28-0x2f
        assertThat(VPACK_INT_UNSIGNED_LAST - VPACK_INT_UNSIGNED_FIRST + 1).isEqualTo(8);
    }

    @Test
    public void testSmallIntRange() {
        // 10 small ints: 0x30-0x39 = 0..9
        assertThat(VPACK_SMALL_INT_LAST - VPACK_SMALL_INT_FIRST + 1).isEqualTo(10);
    }

    @Test
    public void testSmallNegRange() {
        // 6 small negatives: 0x3a-0x3f = -6..-1
        assertThat(VPACK_SMALL_NEG_LAST - VPACK_SMALL_NEG_FIRST + 1).isEqualTo(6);
    }

    @Test
    public void testShortStringRange() {
        // 0x40-0xbe = 127 entries; length = 0..126
        assertThat(VPACK_STRING_SHORT_LAST - VPACK_STRING_SHORT_FIRST + 1).isEqualTo(127);
        // At 0x40: length = 0 (empty string)
        // At 0xbe: length = 0xbe - 0x40 = 126
        assertThat(0xbe - 0x40).isEqualTo(126);
    }
}
