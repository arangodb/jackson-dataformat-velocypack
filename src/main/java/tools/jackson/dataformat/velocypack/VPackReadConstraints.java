package tools.jackson.dataformat.velocypack;

import java.io.Serial;

/**
 * Immutable limits specific to one VelocyPack read factory.
 * Defaults permit 64 MiB of wire bytes, 1,000,000 entries and 16 MiB of
 * cumulative resolved UTF-8 name bytes per root (not per individual name).
 * Forward-only sources buffer and frame one root before returning its tokens;
 * nested structure and trailing indexes are validated during token traversal.
 * Byte-array sources borrow the supplied slice instead of copying the root.
 */
public final class VPackReadConstraints implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final long DEFAULT_MAX_ROOT_VALUE_BYTES = 64L * 1024L * 1024L;
    public static final int DEFAULT_MAX_ROOT_ENTRIES = 1_000_000;
    public static final long DEFAULT_MAX_ROOT_NAME_BYTES = 16L * 1024L * 1024L;
    public static final long MAX_ROOT_VALUE_BYTES = Integer.MAX_VALUE - 8L;
    public static final long MAX_ROOT_NAME_BYTES = Integer.MAX_VALUE - 8L;

    private final long maxRootValueBytes;
    private final int maxRootEntries;
    private final long maxRootNameBytes;

    private VPackReadConstraints(long maxRootValueBytes, int maxRootEntries,
            long maxRootNameBytes) {
        this.maxRootValueBytes = maxRootValueBytes;
        this.maxRootEntries = maxRootEntries;
        this.maxRootNameBytes = maxRootNameBytes;
    }

    public static VPackReadConstraints defaults() {
        return new VPackReadConstraints(DEFAULT_MAX_ROOT_VALUE_BYTES,
                DEFAULT_MAX_ROOT_ENTRIES, DEFAULT_MAX_ROOT_NAME_BYTES);
    }

    public static Builder builder() {
        return new Builder(defaults());
    }

    @SuppressWarnings("unused") // Public immutable-configuration copy API.
    public Builder rebuild() {
        return new Builder(this);
    }

    public long getMaxRootValueBytes() {
        return maxRootValueBytes;
    }

    public int getMaxRootEntries() {
        return maxRootEntries;
    }

    public long getMaxRootNameBytes() {
        return maxRootNameBytes;
    }

    public static final class Builder {
        private long maxRootValueBytes;
        private int maxRootEntries;
        private long maxRootNameBytes;

        private Builder(VPackReadConstraints source) {
            maxRootValueBytes = source.maxRootValueBytes;
            maxRootEntries = source.maxRootEntries;
            maxRootNameBytes = source.maxRootNameBytes;
        }

        public Builder maxRootValueBytes(long value) {
            if (value < 1L || value > MAX_ROOT_VALUE_BYTES) {
                throw new IllegalArgumentException("maxRootValueBytes must be in 1.."
                        + MAX_ROOT_VALUE_BYTES + ": " + value);
            }
            maxRootValueBytes = value;
            return this;
        }

        public Builder maxRootEntries(int value) {
            if (value < 0) {
                throw new IllegalArgumentException("maxRootEntries must be non-negative: " + value);
            }
            maxRootEntries = value;
            return this;
        }

        public Builder maxRootNameBytes(long value) {
            if (value < 0L || value > MAX_ROOT_NAME_BYTES) {
                throw new IllegalArgumentException("maxRootNameBytes must be in 0.."
                        + MAX_ROOT_NAME_BYTES + ": " + value);
            }
            maxRootNameBytes = value;
            return this;
        }

        public VPackReadConstraints build() {
            return new VPackReadConstraints(maxRootValueBytes, maxRootEntries, maxRootNameBytes);
        }
    }
}
