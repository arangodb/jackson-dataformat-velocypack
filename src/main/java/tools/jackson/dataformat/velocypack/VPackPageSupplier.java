package tools.jackson.dataformat.velocypack;

/** Supplies pages for owned byte stores. */
interface VPackPageSupplier extends AutoCloseable {
    byte[] acquire();

    void release(byte[] page);

    /** Whether this supplier owns the page for its whole lifetime. */
    default boolean ownsPage(byte[] page) { return false; }

    /** Release supplier-owned resources after its last store has been released. */
    @Override
    default void close() { }
}
