package tools.jackson.dataformat.velocypack;

import java.io.Serial;

/** Immutable root, entry, name and number-digit limits for VelocyPack output. */
public final class VPackWriteConstraints implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final long DEFAULT_MAX_ROOT_VALUE_BYTES = VPackReadConstraints.DEFAULT_MAX_ROOT_VALUE_BYTES;
    public static final int DEFAULT_MAX_ROOT_ENTRIES = VPackReadConstraints.DEFAULT_MAX_ROOT_ENTRIES;
    public static final long DEFAULT_MAX_ROOT_NAME_BYTES = VPackReadConstraints.DEFAULT_MAX_ROOT_NAME_BYTES;
    public static final int DEFAULT_MAX_NUMBER_DIGITS = 1_000;
    public static final long MAX_ROOT_VALUE_BYTES = VPackReadConstraints.MAX_ROOT_VALUE_BYTES;
    public static final long MAX_ROOT_NAME_BYTES = VPackReadConstraints.MAX_ROOT_NAME_BYTES;

    private final long maxRootValueBytes;
    private final int maxRootEntries;
    private final long maxRootNameBytes;
    private final int maxNumberDigits;

    private VPackWriteConstraints(long maxRootValueBytes, int maxRootEntries,
            long maxRootNameBytes, int maxNumberDigits) {
        this.maxRootValueBytes = maxRootValueBytes;
        this.maxRootEntries = maxRootEntries;
        this.maxRootNameBytes = maxRootNameBytes;
        this.maxNumberDigits = maxNumberDigits;
    }

    public static VPackWriteConstraints defaults() {
        return new VPackWriteConstraints(DEFAULT_MAX_ROOT_VALUE_BYTES,
                DEFAULT_MAX_ROOT_ENTRIES, DEFAULT_MAX_ROOT_NAME_BYTES,
                DEFAULT_MAX_NUMBER_DIGITS);
    }

    public static Builder builder() {
        return new Builder(defaults());
    }

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

    public int getMaxNumberDigits() {
        return maxNumberDigits;
    }

    public static final class Builder {
        private long maxRootValueBytes;
        private int maxRootEntries;
        private long maxRootNameBytes;
        private int maxNumberDigits;

        private Builder(VPackWriteConstraints source) {
            maxRootValueBytes = source.maxRootValueBytes;
            maxRootEntries = source.maxRootEntries;
            maxRootNameBytes = source.maxRootNameBytes;
            maxNumberDigits = source.maxNumberDigits;
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

        public Builder maxNumberDigits(int value) {
            if (value < 1) {
                throw new IllegalArgumentException("maxNumberDigits must be positive: " + value);
            }
            maxNumberDigits = value;
            return this;
        }

        public VPackWriteConstraints build() {
            return new VPackWriteConstraints(maxRootValueBytes, maxRootEntries,
                    maxRootNameBytes, maxNumberDigits);
        }
    }
}
