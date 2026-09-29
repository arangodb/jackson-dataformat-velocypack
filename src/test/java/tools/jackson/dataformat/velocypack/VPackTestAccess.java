package tools.jackson.dataformat.velocypack;

import tools.jackson.core.sym.ByteQuadsCanonicalizer;

/** Test-only bridges for package-private VPack probes used by relocated compatibility fixtures. */
public final class VPackTestAccess {
    private VPackTestAccess() { }

    public static long currentDoubleBits(VPackParser parser) {
        return parser.currentDoubleBits();
    }

    public static ByteQuadsCanonicalizer byteSymbolCanonicalizer(VPackFactory factory) {
        return factory._byteSymbolCanonicalizer;
    }
}
