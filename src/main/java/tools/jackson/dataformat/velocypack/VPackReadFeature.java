package tools.jackson.dataformat.velocypack;

import tools.jackson.core.FormatFeature;

/** No optional read leniency is enabled by the local VPack profile. */
public enum VPackReadFeature implements FormatFeature {
    ;

    @SuppressWarnings("SameReturnValue") // This release defines no optional read flags.
    public static int collectDefaults() {
        return 0;
    }

    @Override
    public boolean enabledByDefault() {
        return false;
    }

    @Override
    public boolean enabledIn(int flags) {
        return false;
    }

    @Override
    public int getMask() {
        return 0;
    }
}
