package tools.jackson.dataformat.velocypack;

/** Test-only bridge for package-private VPack parser probes. */
public final class VPackTestAccess {
    private VPackTestAccess() { }

    public static long currentDoubleBits(VPackParser parser) {
        return parser.currentDoubleBits();
    }
}
