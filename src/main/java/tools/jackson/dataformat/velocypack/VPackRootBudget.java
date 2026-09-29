package tools.jackson.dataformat.velocypack;

/** Mutable accounting for one root; a fresh budget is used for every root. */
final class VPackRootBudget implements AutoCloseable {
    private final long maxBytes;
    private final long maxNames;
    private final int maxEntries;
    private long bytes;
    private long names;
    private int entries;
    private boolean released;

    VPackRootBudget(VPackReadConstraints constraints) {
        if (constraints == null) {
            throw new NullPointerException("constraints");
        }
        maxBytes = constraints.getMaxRootValueBytes();
        maxNames = constraints.getMaxRootNameBytes();
        maxEntries = constraints.getMaxRootEntries();
    }

    VPackRootBudget(VPackWriteConstraints constraints) {
        if (constraints == null) {
            throw new NullPointerException("constraints");
        }
        maxBytes = constraints.getMaxRootValueBytes();
        maxNames = constraints.getMaxRootNameBytes();
        maxEntries = constraints.getMaxRootEntries();
    }

    VPackRootBudget(long maxBytes, int maxEntries, long maxNames) {
        if (maxBytes < 1L || maxBytes > VPackReadConstraints.MAX_ROOT_VALUE_BYTES
                || maxEntries < 0 || maxNames < 0L
                || maxNames > VPackReadConstraints.MAX_ROOT_NAME_BYTES) {
            throw new IllegalArgumentException("root budget is outside the supported limits");
        }
        this.maxBytes = maxBytes;
        this.maxEntries = maxEntries;
        this.maxNames = maxNames;
    }

    long bytes() {
        ensureOpen();
        return bytes;
    }

    int entries() {
        ensureOpen();
        return entries;
    }

    long nameBytes() {
        ensureOpen();
        return names;
    }

    void chargeBytes(long amount) {
        ensureOpen();
        if (amount < 0L || amount > maxBytes - bytes) {
            throw VPackErrors.constraint("root bytes", bytes, "root byte budget exceeded");
        }
        bytes += amount;
    }

    void chargeEntry() {
        ensureOpen();
        if (entries == maxEntries) {
            throw VPackErrors.constraint("root entries", entries, "root entry budget exceeded");
        }
        entries++;
    }

    void chargeName(long amount) {
        ensureOpen();
        if (amount < 0L || amount > maxNames - names) {
            throw VPackErrors.constraint("root names", names, "root name-byte budget exceeded");
        }
        names += amount;
    }

    /** Validate a name charge before an operation allocates its UTF-8 bytes. */
    void checkName(long amount) {
        ensureOpen();
        if (amount < 0L || amount > maxNames - names) {
            throw VPackErrors.constraint("root names", names, "root name-byte budget exceeded");
        }
    }

    void chargeNameBytes(long amount) {
        chargeName(amount);
    }

    void reset() {
        ensureOpen();
        bytes = 0L;
        names = 0L;
        entries = 0;
    }

    @Override
    public void close() {
        release();
    }

    void release() {
        if (released) {
            return;
        }
        released = true;
        bytes = 0L;
        names = 0L;
        entries = 0;
    }

    private void ensureOpen() {
        if (released) {
            throw VPackErrors.constraint("root budget", -1L, "budget has been released");
        }
    }
}
