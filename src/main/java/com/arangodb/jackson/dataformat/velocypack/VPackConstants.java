package com.arangodb.jackson.dataformat.velocypack;

/**
 * Constants used by {@link VPackGenerator} and {@link VPackParser}.
 * All byte values are taken directly from the VelocyPack specification (spec/VelocyPack.md).
 */
public final class VPackConstants
{
    /*
    /**********************************************************
    /* Type bytes: special / structural
    /**********************************************************
     */

    /** 0x00 - none/absent; illegal in on-wire VPack */
    public static final int VPACK_NONE              = 0x00;

    /** 0x01 - empty array */
    public static final int VPACK_ARRAY_EMPTY       = 0x01;

    /** 0x02-0x05 - array without index table, 1/2/4/8-byte byte length */
    public static final int VPACK_ARRAY_NO_IDX_FIRST = 0x02;
    public static final int VPACK_ARRAY_NO_IDX_LAST  = 0x05;

    /** 0x06-0x09 - array with index table, 1/2/4/8-byte widths */
    public static final int VPACK_ARRAY_IDX_FIRST   = 0x06;
    public static final int VPACK_ARRAY_IDX_LAST    = 0x09;

    /** 0x0a - empty object */
    public static final int VPACK_OBJECT_EMPTY      = 0x0a;

    /** 0x0b-0x0e - sorted object, 1/2/4/8-byte widths */
    public static final int VPACK_OBJECT_SORTED_FIRST = 0x0b;
    public static final int VPACK_OBJECT_SORTED_LAST  = 0x0e;

    /** 0x0f-0x12 - unsorted (obsolete) object, 1/2/4/8-byte widths */
    public static final int VPACK_OBJECT_UNSORTED_FIRST = 0x0f;
    public static final int VPACK_OBJECT_UNSORTED_LAST  = 0x12;

    /** 0x13 - compact array (VByte-encoded lengths, no index table) */
    public static final int VPACK_ARRAY_COMPACT     = 0x13;

    /** 0x14 - compact object (VByte-encoded lengths, no index table) */
    public static final int VPACK_OBJECT_COMPACT    = 0x14;

    /** 0x15-0x16 - reserved */
    public static final int VPACK_RESERVED_15       = 0x15;
    public static final int VPACK_RESERVED_16       = 0x16;

    /** 0x17 - illegal; rejected on read */
    public static final int VPACK_ILLEGAL           = 0x17;

    /** 0x18 - null */
    public static final int VPACK_NULL              = 0x18;

    /** 0x19 - false */
    public static final int VPACK_FALSE             = 0x19;

    /** 0x1a - true */
    public static final int VPACK_TRUE              = 0x1a;

    /** 0x1b - double (IEEE-754, 8-byte LE uint64) */
    public static final int VPACK_DOUBLE            = 0x1b;

    /** 0x1c - UTC-date (8-byte LE signed int, ms since epoch) */
    public static final int VPACK_DATE              = 0x1c;

    /** 0x1d - external pointer; illegal on disk/wire */
    public static final int VPACK_EXTERNAL          = 0x1d;

    /** 0x1e - minKey (compares smaller than all others) */
    public static final int VPACK_MIN_KEY           = 0x1e;

    /** 0x1f - maxKey (compares greater than all others) */
    public static final int VPACK_MAX_KEY           = 0x1f;

    /*
    /**********************************************************
    /* Type bytes: integers
    /**********************************************************
     */

    /** 0x20-0x27 - signed int, LE, 1..8 bytes (count = V - 0x1f) */
    public static final int VPACK_INT_SIGNED_FIRST  = 0x20;
    public static final int VPACK_INT_SIGNED_LAST   = 0x27;

    /** 0x28-0x2f - unsigned int, LE, 1..8 bytes (count = V - 0x27) */
    public static final int VPACK_INT_UNSIGNED_FIRST = 0x28;
    public static final int VPACK_INT_UNSIGNED_LAST  = 0x2f;

    /** 0x30-0x39 - small integers 0..9 (value = V - 0x30) */
    public static final int VPACK_SMALL_INT_FIRST   = 0x30;
    public static final int VPACK_SMALL_INT_LAST    = 0x39;

    /** 0x3a-0x3f - small negative integers -6..-1 (value = V - 0x40) */
    public static final int VPACK_SMALL_NEG_FIRST   = 0x3a;
    public static final int VPACK_SMALL_NEG_LAST    = 0x3f;

    /*
    /**********************************************************
    /* Type bytes: strings
    /**********************************************************
     */

    /**
     * 0x40-0xbe - short UTF-8 string; byte length = V - 0x40 (0..126 bytes).
     * 0x40 is the empty string.
     */
    public static final int VPACK_STRING_SHORT_FIRST = 0x40;
    public static final int VPACK_STRING_SHORT_LAST  = 0xbe;
    public static final int VPACK_STRING_SHORT_MAX_LEN = 126;

    /**
     * 0xbf - long UTF-8 string; next 8 bytes are LE unsigned length, then content.
     */
    public static final int VPACK_STRING_LONG        = 0xbf;

    /*
    /**********************************************************
    /* Type bytes: binary
    /**********************************************************
     */

    /**
     * 0xc0-0xc7 - binary blob; V - 0xbf bytes encode the LE unsigned length.
     */
    public static final int VPACK_BINARY_FIRST       = 0xc0;
    public static final int VPACK_BINARY_LAST        = 0xc7;

    /*
    /**********************************************************
    /* Type bytes: BCD floats
    /**********************************************************
     */

    /**
     * 0xc8-0xcf - positive BCD float; V - 0xc7 bytes encode LE mantissa length.
     */
    public static final int VPACK_BCD_POS_FIRST      = 0xc8;
    public static final int VPACK_BCD_POS_LAST       = 0xcf;

    /**
     * 0xd0-0xd7 - negative BCD float; V - 0xcf bytes encode LE mantissa length.
     */
    public static final int VPACK_BCD_NEG_FIRST      = 0xd0;
    public static final int VPACK_BCD_NEG_LAST       = 0xd7;

    /** 0xd8-0xed - reserved */
    public static final int VPACK_RESERVED_D8        = 0xd8;
    public static final int VPACK_RESERVED_ED        = 0xed;

    /*
    /**********************************************************
    /* Type bytes: tagging
    /**********************************************************
     */

    /** 0xee - tag with 1-byte tag number; sub-VPack follows */
    public static final int VPACK_TAG_1BYTE          = 0xee;

    /** 0xef - tag with 8-byte LE tag number; sub-VPack follows */
    public static final int VPACK_TAG_8BYTE          = 0xef;

    /*
    /**********************************************************
    /* Type bytes: custom types
    /**********************************************************
     */

    /** 0xf0 - custom type, 1 byte payload */
    public static final int VPACK_CUSTOM_FIRST        = 0xf0;
    public static final int VPACK_CUSTOM_LAST         = 0xff;

    // Fixed-payload custom types
    public static final int VPACK_CUSTOM_1B          = 0xf0;
    public static final int VPACK_CUSTOM_2B          = 0xf1;
    public static final int VPACK_CUSTOM_4B          = 0xf2;
    public static final int VPACK_CUSTOM_8B          = 0xf3;

    // 1-byte length prefix
    public static final int VPACK_CUSTOM_LEN1_FIRST  = 0xf4;
    public static final int VPACK_CUSTOM_LEN1_LAST   = 0xf6;

    // 2-byte LE length prefix
    public static final int VPACK_CUSTOM_LEN2_FIRST  = 0xf7;
    public static final int VPACK_CUSTOM_LEN2_LAST   = 0xf9;

    // 4-byte LE length prefix
    public static final int VPACK_CUSTOM_LEN4_FIRST  = 0xfa;
    public static final int VPACK_CUSTOM_LEN4_LAST   = 0xfc;

    // 8-byte LE length prefix
    public static final int VPACK_CUSTOM_LEN8_FIRST  = 0xfd;
    public static final int VPACK_CUSTOM_LEN8_LAST   = 0xff;

    /*
    /**********************************************************
    /* Misc
    /**********************************************************
     */

    /**
     * Minimum output buffer size. We need enough space to write any single
     * atomic value without flushing: at minimum a 9-byte double + 1 header.
     */
    public static final int MIN_OUTPUT_BUFFER_SIZE   = 256;
}
