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

    /** Resolve a name to an unsigned wire ID; {@code null} requests a string name. */
    BigInteger encode(String name);
}
