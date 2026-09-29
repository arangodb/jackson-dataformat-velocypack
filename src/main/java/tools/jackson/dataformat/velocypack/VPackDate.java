package tools.jackson.dataformat.velocypack;

/** Native VelocyPack UTC-date value represented as milliseconds from the epoch. */
public record VPackDate(long epochMillis) { }
