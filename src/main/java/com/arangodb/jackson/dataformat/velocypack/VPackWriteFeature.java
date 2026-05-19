package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.FormatFeature;

/**
 * Enumeration that defines all togglable features for VelocyPack generators.
 */
public enum VPackWriteFeature
    implements FormatFeature
{
    /**
     * Feature that controls whether object keys are sorted by byte-wise comparison
     * of attribute names before writing, producing sorted object types
     * ({@code 0x0b}–{@code 0x0e}). If disabled, unsorted object types
     * ({@code 0x0f}–{@code 0x12}) are emitted instead.
     *<p>
     * The VelocyPack spec requires sorted keys for the primary object types.
     * Sorting requires buffering the complete object in memory before writing.
     *<p>
     * Default value is {@code false}.
     */
    WRITE_OBJECT_KEYS_SORTED(false),

    /**
     * Feature that controls whether the compact array type ({@code 0x13}) is used
     * when writing arrays. Compact arrays have no index table and use VByte-encoded
     * lengths, making them more compact but requiring sequential access.
     *<p>
     * Default value is {@code false}.
     */
    WRITE_COMPACT_ARRAYS(false),

    /**
     * Feature that controls whether the compact object type ({@code 0x14}) is used
     * when writing objects. Compact objects have no index table and use VByte-encoded
     * lengths, making them more compact but requiring sequential access.
     *<p>
     * Default value is {@code false}.
     */
    WRITE_COMPACT_OBJECTS(false),

    /**
     * Feature that controls whether integers are written using the minimal number of
     * bytes. If enabled (default), for example the value 5 is written as a small int
     * ({@code 0x35}), not as a 1-byte signed int.
     *<p>
     * Default value is {@code true}.
     */
    WRITE_MIN_INT_WIDTH(true),

    /**
     * Feature that determines if an invalid surrogate encoding found in the
     * incoming String should fail with an exception or silently be output
     * as the Unicode REPLACEMENT CHARACTER (U+FFFD) or not; if not,
     * an exception will be thrown to indicate invalid content.
     *<p>
     * Default value is {@code false} meaning that an invalid surrogate will
     * result in a {@code StreamWriteException}.
     */
    LENIENT_UTF_ENCODING(false),
    ;

    private final boolean _defaultState;
    private final int _mask;

    /**
     * Method that calculates bit set (flags) of all features that
     * are enabled by default.
     */
    public static int collectDefaults()
    {
        int flags = 0;
        for (VPackWriteFeature f : values()) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return flags;
    }

    private VPackWriteFeature(boolean defaultState) {
        _defaultState = defaultState;
        _mask = (1 << ordinal());
    }

    @Override public boolean enabledByDefault() { return _defaultState; }
    @Override public int getMask() { return _mask; }
    @Override public boolean enabledIn(int flags) { return (flags & _mask) != 0; }
}
