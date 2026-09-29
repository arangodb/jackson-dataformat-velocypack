package tools.jackson.dataformat.velocypack;

/** Physical VelocyPack family of the current token. */
public enum VPackType {
    ARRAY, OBJECT, NULL, BOOLEAN, DOUBLE, DATE,
    SIGNED_INTEGER, UNSIGNED_INTEGER, SMALL_INTEGER,
    STRING, BINARY, BCD, MIN_KEY, MAX_KEY
}
