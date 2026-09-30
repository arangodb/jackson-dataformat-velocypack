package tools.jackson.dataformat.velocypack;

import java.math.BigInteger;

/**
 * Codec for the optional numeric attribute-name space used by object slices.
 * Implementations must be immutable and thread-safe, mapping names to unsigned 64-bit IDs.
 * Return {@code null} to decline a mapping; unknown IDs on input are errors.
 */
public interface VPackAttributeNameCodec {
    /** Resolve an unsigned wire ID; {@code null} means that the ID is unknown. */
    String decode(BigInteger unsignedId);

    /**
     * Resolve an unsigned wire ID given as its raw uint64 bit pattern (IDs >= 2^63
     * appear negative; use Long.toUnsignedString / Long.compareUnsigned). The parser
     * calls this method; override it to avoid a BigInteger allocation per key.
     */
    default String decode(long unsignedIdBits) {
        return decode(VPackBounds.unsignedLong(unsignedIdBits));
    }

    /** Resolve a name to an unsigned wire ID; {@code null} requests a string name. */
    BigInteger encode(String name);
}
