package com.arangodb.jackson.dataformat.velocypack;

import java.util.Arrays;

/**
 * Container for a VelocyPack custom type value ({@code 0xf0}–{@code 0xff}).
 * Exposed as the embedded object for {@link tools.jackson.core.JsonToken#VALUE_EMBEDDED_OBJECT}
 * tokens when the parser encounters a custom-type byte and
 * {@link VPackReadFeature#FAIL_ON_CUSTOM_TYPES} is disabled.
 */
public final class VPackCustomValue
{
    /**
     * The custom type byte (0xf0-0xff).
     */
    private final int _typeByte;

    /**
     * The raw payload bytes following the type byte (and any length prefix).
     */
    private final byte[] _payload;

    public VPackCustomValue(int typeByte, byte[] payload) {
        _typeByte = typeByte;
        _payload = payload;
    }

    /** Returns the custom type byte (0xf0-0xff). */
    public int getTypeByte() {
        return _typeByte;
    }

    /** Returns a copy of the raw payload bytes. */
    public byte[] getPayload() {
        return Arrays.copyOf(_payload, _payload.length);
    }

    @Override
    public String toString() {
        return String.format("VPackCustomValue[type=0x%02x, payloadLen=%d]", _typeByte, _payload.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VPackCustomValue)) return false;
        VPackCustomValue other = (VPackCustomValue) o;
        return _typeByte == other._typeByte && Arrays.equals(_payload, other._payload);
    }

    @Override
    public int hashCode() {
        return _typeByte * 31 + Arrays.hashCode(_payload);
    }
}
