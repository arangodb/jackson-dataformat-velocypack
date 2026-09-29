package tools.jackson.dataformat.velocypack;

/** Total classification of the local 256-marker byte space. */
enum VPackMarker {
    PADDING,
    EMPTY_ARRAY,
    EQUAL_ARRAY,
    INDEXED_ARRAY,
    EMPTY_OBJECT,
    SORTED_OBJECT,
    UNSORTED_OBJECT,
    COMPACT_ARRAY,
    COMPACT_OBJECT,
    NULL,
    FALSE,
    TRUE,
    DOUBLE,
    UTC_DATE,
    EXTERNAL_POINTER,
    MIN_KEY,
    MAX_KEY,
    SIGNED_INTEGER,
    UNSIGNED_INTEGER,
    SMALL_POSITIVE,
    SMALL_NEGATIVE,
    SHORT_STRING,
    LONG_STRING,
    BINARY,
    POSITIVE_BCD,
    NEGATIVE_BCD,
    RESERVED;

    private static final int[] CONTAINER_WIDTHS = { 1, 2, 4, 8 };
    private static final int[] SCALAR_WIDTHS = { 1, 2, 3, 4, 5, 6, 7, 8 };

    static VPackMarker classify(int marker) {
        if (marker < 0 || marker > 0xFF) {
            throw VPackErrors.malformed("marker", "marker must be an unsigned byte");
        }
        if (marker == 0x00) return PADDING;
        if (marker == 0x01) return EMPTY_ARRAY;
        if (marker <= 0x05) return EQUAL_ARRAY;
        if (marker <= 0x09) return INDEXED_ARRAY;
        if (marker == 0x0A) return EMPTY_OBJECT;
        if (marker <= 0x0E) return SORTED_OBJECT;
        if (marker <= 0x12) return UNSORTED_OBJECT;
        if (marker == 0x13) return COMPACT_ARRAY;
        if (marker == 0x14) return COMPACT_OBJECT;
        if (marker <= 0x17) return RESERVED;
        if (marker == 0x18) return NULL;
        if (marker == 0x19) return FALSE;
        if (marker == 0x1A) return TRUE;
        if (marker == 0x1B) return DOUBLE;
        if (marker == 0x1C) return UTC_DATE;
        if (marker == 0x1D) return EXTERNAL_POINTER;
        if (marker == 0x1E) return MIN_KEY;
        if (marker == 0x1F) return MAX_KEY;
        if (marker <= 0x27) return SIGNED_INTEGER;
        if (marker <= 0x2F) return UNSIGNED_INTEGER;
        if (marker <= 0x39) return SMALL_POSITIVE;
        if (marker <= 0x3F) return SMALL_NEGATIVE;
        if (marker <= 0xBE) return SHORT_STRING;
        if (marker == 0xBF) return LONG_STRING;
        if (marker <= 0xC7) return BINARY;
        if (marker <= 0xCF) return POSITIVE_BCD;
        if (marker <= 0xD7) return NEGATIVE_BCD;
        return RESERVED;
    }

    static VPackMarker requireSupported(int marker, String context) {
        return requireSupported(marker, context, marker);
    }

    static VPackMarker requireSupported(int marker, String context, long errorOffset) {
        VPackMarker classification = classify(marker);
        if (classification == PADDING || classification == RESERVED
                || classification == EXTERNAL_POINTER) {
            throw VPackErrors.malformed(context, errorOffset,
                    "unsupported marker 0x" + Integer.toHexString(marker));
        }
        return classification;
    }

    static int width(int marker) {
        VPackMarker kind = classify(marker);
        if (kind == EQUAL_ARRAY) return CONTAINER_WIDTHS[marker - 0x02];
        if (kind == INDEXED_ARRAY) return CONTAINER_WIDTHS[marker - 0x06];
        if (kind == SORTED_OBJECT) return CONTAINER_WIDTHS[marker - 0x0B];
        if (kind == UNSORTED_OBJECT) return CONTAINER_WIDTHS[marker - 0x0F];
        if (kind == SIGNED_INTEGER) return SCALAR_WIDTHS[marker - 0x20];
        if (kind == UNSIGNED_INTEGER) return SCALAR_WIDTHS[marker - 0x28];
        if (kind == BINARY) return SCALAR_WIDTHS[marker - 0xC0];
        if (kind == POSITIVE_BCD) return SCALAR_WIDTHS[marker - 0xC8];
        if (kind == NEGATIVE_BCD) return SCALAR_WIDTHS[marker - 0xD0];
        throw VPackErrors.malformed("marker", marker, "marker has no width field");
    }

    static boolean isContainer(int marker) {
        VPackMarker kind = classify(marker);
        return kind == EMPTY_ARRAY || kind == EQUAL_ARRAY || kind == INDEXED_ARRAY
                || kind == EMPTY_OBJECT || kind == SORTED_OBJECT
                || kind == UNSORTED_OBJECT || kind == COMPACT_ARRAY
                || kind == COMPACT_OBJECT;
    }
}
