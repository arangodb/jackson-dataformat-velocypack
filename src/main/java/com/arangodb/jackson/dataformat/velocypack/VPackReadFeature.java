package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.FormatFeature;

/**
 * Enumeration that defines all togglable features for VelocyPack parsers.
 */
public enum VPackReadFeature implements FormatFeature
{
    /**
     * Feature that determines whether tagged values ({@code 0xee}/{@code 0xef})
     * should cause a {@link tools.jackson.core.exc.StreamReadException} when encountered.
     * If disabled (default), tagged values are read transparently: the tag number is
     * accessible via {@link VPackParser#getLastTagNumber()} and the wrapped value is
     * returned as the current token.
     *<p>
     * Default value is {@code false}.
     */
    FAIL_ON_TAGGED_VALUES(false),

    /**
     * Feature that determines whether custom type bytes ({@code 0xf0}–{@code 0xff})
     * should cause a {@link tools.jackson.core.exc.StreamReadException} when encountered.
     * If disabled (default), custom types are surfaced as
     * {@link tools.jackson.core.JsonToken#VALUE_EMBEDDED_OBJECT} tokens whose
     * embedded object is a {@link VPackCustomValue}.
     *<p>
     * Default value is {@code false}.
     */
    FAIL_ON_CUSTOM_TYPES(false),

    /**
     * Feature that determines whether invalid surrogate pairs found in incoming
     * data should be silently replaced with the Unicode REPLACEMENT CHARACTER
     * (U+FFFD) rather than causing an exception.
     *<p>
     * Default value is {@code false}.
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
        for (VPackReadFeature f : values()) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return flags;
    }

    VPackReadFeature(@SuppressWarnings("SameParameterValue") boolean defaultState) {
        _defaultState = defaultState;
        _mask = (1 << ordinal());
    }

    @Override public boolean enabledByDefault() { return _defaultState; }
    @Override public int getMask() { return _mask; }
    @Override public boolean enabledIn(int flags) { return (flags & getMask()) != 0; }
}
