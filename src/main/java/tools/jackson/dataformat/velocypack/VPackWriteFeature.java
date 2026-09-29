package tools.jackson.dataformat.velocypack;

import tools.jackson.core.FormatFeature;

/** Format-specific writer policy bits for canonical VPack container output. */
public enum VPackWriteFeature implements FormatFeature {
    USE_EQUAL_LENGTH_ARRAYS(true),
    WRITE_COMPACT_ARRAYS(false),
    WRITE_COMPACT_OBJECTS(false);

    private final boolean defaultState;
    private final int mask;

    VPackWriteFeature(boolean defaultState) {
        this.defaultState = defaultState;
        mask = 1 << ordinal();
    }

    public static int collectDefaults() {
        int flags = 0;
        for (VPackWriteFeature feature : values()) {
            if (feature.enabledByDefault()) {
                flags |= feature.getMask();
            }
        }
        return flags;
    }

    @Override
    public boolean enabledByDefault() {
        return defaultState;
    }

    @Override
    public boolean enabledIn(int flags) {
        return (flags & mask) != 0;
    }

    @Override
    public int getMask() {
        return mask;
    }
}
