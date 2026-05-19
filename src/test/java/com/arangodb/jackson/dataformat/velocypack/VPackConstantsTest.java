package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.arangodb.jackson.dataformat.velocypack.VPackConstants.*;

/**
 * Unit tests for VPackConstants - verifying all type byte ranges are correct per spec.
 */
public class VPackConstantsTest
{
    @Test
    public void testNullFalseTrueBytes() {
        assertEquals(0x18, VPACK_NULL);
        assertEquals(0x19, VPACK_FALSE);
        assertEquals(0x1a, VPACK_TRUE);
    }

    @Test
    public void testDoubleDate() {
        assertEquals(0x1b, VPACK_DOUBLE);
        assertEquals(0x1c, VPACK_DATE);
    }

    @Test
    public void testIllegalAndSpecial() {
        assertEquals(0x00, VPACK_NONE);
        assertEquals(0x17, VPACK_ILLEGAL);
        assertEquals(0x1d, VPACK_EXTERNAL);
        assertEquals(0x1e, VPACK_MIN_KEY);
        assertEquals(0x1f, VPACK_MAX_KEY);
    }

    @Test
    public void testArrayTypes() {
        assertEquals(0x01, VPACK_ARRAY_EMPTY);
        assertEquals(0x02, VPACK_ARRAY_NO_IDX_FIRST);
        assertEquals(0x05, VPACK_ARRAY_NO_IDX_LAST);
        assertEquals(0x06, VPACK_ARRAY_IDX_FIRST);
        assertEquals(0x09, VPACK_ARRAY_IDX_LAST);
        assertEquals(0x13, VPACK_ARRAY_COMPACT);
    }

    @Test
    public void testObjectTypes() {
        assertEquals(0x0a, VPACK_OBJECT_EMPTY);
        assertEquals(0x0b, VPACK_OBJECT_SORTED_FIRST);
        assertEquals(0x0e, VPACK_OBJECT_SORTED_LAST);
        assertEquals(0x0f, VPACK_OBJECT_UNSORTED_FIRST);
        assertEquals(0x12, VPACK_OBJECT_UNSORTED_LAST);
        assertEquals(0x14, VPACK_OBJECT_COMPACT);
    }

    @Test
    public void testIntegerTypes() {
        assertEquals(0x20, VPACK_INT_SIGNED_FIRST);
        assertEquals(0x27, VPACK_INT_SIGNED_LAST);
        assertEquals(0x28, VPACK_INT_UNSIGNED_FIRST);
        assertEquals(0x2f, VPACK_INT_UNSIGNED_LAST);
        assertEquals(0x30, VPACK_SMALL_INT_FIRST);
        assertEquals(0x39, VPACK_SMALL_INT_LAST);
        assertEquals(0x3a, VPACK_SMALL_NEG_FIRST);
        assertEquals(0x3f, VPACK_SMALL_NEG_LAST);
    }

    @Test
    public void testStringTypes() {
        assertEquals(0x40, VPACK_STRING_SHORT_FIRST);
        assertEquals(0xbe, VPACK_STRING_SHORT_LAST);
        assertEquals(126, VPACK_STRING_SHORT_MAX_LEN);
        assertEquals(0xbf, VPACK_STRING_LONG);
    }

    @Test
    public void testBinaryTypes() {
        assertEquals(0xc0, VPACK_BINARY_FIRST);
        assertEquals(0xc7, VPACK_BINARY_LAST);
    }

    @Test
    public void testBcdTypes() {
        assertEquals(0xc8, VPACK_BCD_POS_FIRST);
        assertEquals(0xcf, VPACK_BCD_POS_LAST);
        assertEquals(0xd0, VPACK_BCD_NEG_FIRST);
        assertEquals(0xd7, VPACK_BCD_NEG_LAST);
    }

    @Test
    public void testTagTypes() {
        assertEquals(0xee, VPACK_TAG_1BYTE);
        assertEquals(0xef, VPACK_TAG_8BYTE);
    }

    @Test
    public void testCustomTypes() {
        assertEquals(0xf0, VPACK_CUSTOM_FIRST);
        assertEquals(0xff, VPACK_CUSTOM_LAST);
        assertEquals(0xf0, VPACK_CUSTOM_1B);
        assertEquals(0xf1, VPACK_CUSTOM_2B);
        assertEquals(0xf2, VPACK_CUSTOM_4B);
        assertEquals(0xf3, VPACK_CUSTOM_8B);
        assertEquals(0xf4, VPACK_CUSTOM_LEN1_FIRST);
        assertEquals(0xf6, VPACK_CUSTOM_LEN1_LAST);
        assertEquals(0xf7, VPACK_CUSTOM_LEN2_FIRST);
        assertEquals(0xf9, VPACK_CUSTOM_LEN2_LAST);
        assertEquals(0xfa, VPACK_CUSTOM_LEN4_FIRST);
        assertEquals(0xfc, VPACK_CUSTOM_LEN4_LAST);
        assertEquals(0xfd, VPACK_CUSTOM_LEN8_FIRST);
        assertEquals(0xff, VPACK_CUSTOM_LEN8_LAST);
    }

    @Test
    public void testReservedRanges() {
        assertEquals(0x15, VPACK_RESERVED_15);
        assertEquals(0x16, VPACK_RESERVED_16);
        assertEquals(0xd8, VPACK_RESERVED_D8);
        assertEquals(0xed, VPACK_RESERVED_ED);
    }

    // Verify contiguous ranges
    @Test
    public void testSignedIntRange() {
        // 8 signed int types: 0x20-0x27
        assertEquals(8, VPACK_INT_SIGNED_LAST - VPACK_INT_SIGNED_FIRST + 1);
    }

    @Test
    public void testUnsignedIntRange() {
        // 8 unsigned int types: 0x28-0x2f
        assertEquals(8, VPACK_INT_UNSIGNED_LAST - VPACK_INT_UNSIGNED_FIRST + 1);
    }

    @Test
    public void testSmallIntRange() {
        // 10 small ints: 0x30-0x39 = 0..9
        assertEquals(10, VPACK_SMALL_INT_LAST - VPACK_SMALL_INT_FIRST + 1);
    }

    @Test
    public void testSmallNegRange() {
        // 6 small negatives: 0x3a-0x3f = -6..-1
        assertEquals(6, VPACK_SMALL_NEG_LAST - VPACK_SMALL_NEG_FIRST + 1);
    }

    @Test
    public void testShortStringRange() {
        // 0x40-0xbe = 127 entries; length = 0..126
        assertEquals(127, VPACK_STRING_SHORT_LAST - VPACK_STRING_SHORT_FIRST + 1);
        // At 0x40: length = 0 (empty string)
        // At 0xbe: length = 0xbe - 0x40 = 126
        assertEquals(126, 0xbe - 0x40);
    }
}
