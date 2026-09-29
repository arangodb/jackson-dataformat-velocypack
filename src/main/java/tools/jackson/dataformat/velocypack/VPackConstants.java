package tools.jackson.dataformat.velocypack;

/** Wire marker values for VelocyPack; structural algorithms remain internal. */
public final class VPackConstants {
    private VPackConstants() { }

    /** Marker for an empty array. */
    public static final int EMPTY_ARRAY = 0x01;

    public static final int NULL = 0x18;
    public static final int FALSE = 0x19;
    public static final int TRUE = 0x1A;
    public static final int DOUBLE = 0x1B;
    public static final int UTC_DATE = 0x1C;
    public static final int MIN_KEY = 0x1E;
    public static final int MAX_KEY = 0x1F;
}
