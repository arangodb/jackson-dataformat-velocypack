package tools.jackson.databind.deser.jdk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0253F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] UNTYPED_ARRAY_OF_ARRAYS = VPackWireFixtureTest.hex(
            "13 5a 13 57 "
          + "13 15 1b 46 7e fd 10 1b 2c 9c bf 1b 89 d4 b4 8b 69 c0 49 40 02 "
          + "13 15 1b 8d b3 e9 08 e0 66 81 bf 1b 89 d4 b4 8b 69 c0 49 40 02 "
          + "13 15 1b 8d b3 e9 08 e0 66 81 bf 1b 86 aa 98 4a 3f c1 49 40 02 "
          + "13 15 1b 46 7e fd 10 1b 2c 9c bf 1b 86 aa 98 4a 3f c1 49 40 02 04 01");
private static final byte[] HELLO_BYTES_AS_ARRAY = VPackWireFixtureTest.hex(
            "13 0d 28 68 28 65 28 6c 28 6c 28 6f 05");
private static final byte[] BIG_INTEGER_KEY_OBJECT = VPackWireFixtureTest.hex(
            "14 1a 54 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 36 37 38 39 30 41 61 01");
private static final byte[] BIG_DECIMAL_KEY_OBJECT = VPackWireFixtureTest.hex(
            "14 16 50 33 2e 31 34 31 35 39 32 36 35 33 35 38 39 37 39 41 62 01");
private static final byte[] MALFORMED_NUMBER_KEY_OBJECT = VPackWireFixtureTest.hex(
            "14 09 43 61 62 63 41 61 01");
private static final byte[] LARGE_DECIMAL_OBJECT = largeDecimalObject();

    // Provenance: BigNumbersDeserTest#testBigDecimal.
    void testBigDecimal() {
        assertThrows(StreamConstraintsException.class,
                () -> MAPPER.readValue(LARGE_DECIMAL_OBJECT, DecimalWrapper.class));
    }

    // Provenance: BigNumbersDeserTest#testBigDecimalUnlimited.
    void testBigDecimalUnlimited() throws Exception {
        ObjectMapper unlimited = VPackMapper.builder(
                VPackFactory.builder().streamReadConstraints(StreamReadConstraints.builder()
                        .maxNumberLength(Integer.MAX_VALUE).build()).build()).build();
        DecimalWrapper result = unlimited.readValue(LARGE_DECIMAL_OBJECT, DecimalWrapper.class);
        assertNotNull(result);
        assertEquals(1200, result.number.precision());
        assertEquals(0, result.number.scale());
    }

    // Provenance: BigNumbersDeserTest#bigNumberMapKeysWithinLimit.
    void bigNumberMapKeysWithinLimit() throws Exception {
        Map<BigInteger, String> integers = MAPPER.readValue(BIG_INTEGER_KEY_OBJECT,
                new TypeReference<Map<BigInteger, String>>() { });
        assertEquals("a", integers.get(new BigInteger("12345678901234567890")));

        Map<BigDecimal, String> decimals = MAPPER.readValue(BIG_DECIMAL_KEY_OBJECT,
                new TypeReference<Map<BigDecimal, String>>() { });
        assertEquals("b", decimals.get(new BigDecimal("3.14159265358979")));
    }

    // Provenance: BigNumbersDeserTest#bigIntegerAsMapKey.
    void bigIntegerAsMapKey() {
        assertKeyTooLong("1".repeat(1200), new TypeReference<Map<BigInteger, String>>() { });
    }

    // Provenance: BigNumbersDeserTest#bigDecimalAsMapKey.
    void bigDecimalAsMapKey() {
        assertKeyTooLong("1".repeat(1200), new TypeReference<Map<BigDecimal, String>>() { });
    }

    // Provenance: BigNumbersDeserTest#floatingPointAsMapKey.
    void floatingPointAsMapKey() {
        String key = "1".repeat(1200);
        assertKeyTooLong(key, new TypeReference<Map<Double, String>>() { });
        assertKeyTooLong(key, new TypeReference<Map<Float, String>>() { });
    }

    // Provenance: BigNumbersDeserTest#mapKeyAtLengthLimit.
    void mapKeyAtLengthLimit() throws Exception {
        int maxLength = StreamReadConstraints.defaults().getMaxNumberLength();
        String key = "1".repeat(maxLength);
        Map<BigInteger, String> accepted = MAPPER.readValue(singlePropertyObject(key,
                VPackWireFixtureTest.hex("41 61")),
                new TypeReference<Map<BigInteger, String>>() { });
        assertEquals("a", accepted.get(new BigInteger(key)));
        assertKeyTooLong("1".repeat(maxLength + 1),
                new TypeReference<Map<BigInteger, String>>() { });
    }

    // Provenance: BigNumbersDeserTest#mapKeyWithLoweredLengthLimit.
    void mapKeyWithLoweredLengthLimit() {
        ObjectMapper limited = VPackMapper.builder(
                VPackFactory.builder().streamReadConstraints(StreamReadConstraints.builder()
                        .maxNumberLength(20).build()).build()).build();
        assertKeyTooLong(limited, "1".repeat(25),
                new TypeReference<Map<BigInteger, String>>() { });
    }

    // Provenance: BigNumbersDeserTest#malformedBigNumberMapKey.
    void malformedBigNumberMapKey() {
        InvalidFormatException exception = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(MALFORMED_NUMBER_KEY_OBJECT,
                new TypeReference<Map<BigInteger, String>>() { }));
        assertEquals(true, exception.getMessage().contains("BigInteger"));
    }
private static byte[] largeDecimalObject() {
        final int digits = 1200;
        final int mantissaLength = digits / 2;
        final int bodyLength = 7 + 1 + 2 + 4 + mantissaLength;
        final int length = 1 + 2 + bodyLength + 1;
        byte[] result = new byte[length];
        int offset = 0;
        result[offset++] = 0x14;
        offset = writeForwardVarInt(result, offset, length);
        result[offset++] = 0x46;
        for (byte value : "number".getBytes(StandardCharsets.UTF_8)) {
            result[offset++] = value;
        }
        result[offset++] = (byte) 0xC9;
        result[offset++] = (byte) mantissaLength;
        result[offset++] = (byte) (mantissaLength >>> 8);
        offset += 4; // zero exponent, already zero-filled
        for (int i = 0; i < mantissaLength; ++i) {
            result[offset++] = 0x11;
        }
        result[offset] = 0x01;
        return result;
    }
private static byte[] singlePropertyObject(String name, byte[] value) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        int nameHeader = nameBytes.length <= 126 ? 1 : 9;
        int bodyLength = nameHeader + nameBytes.length + value.length;
        int lengthWidth = 1;
        int length;
        do {
            length = 1 + lengthWidth + bodyLength + 1;
            int required = forwardVarIntLength(length);
            if (required == lengthWidth) {
                break;
            }
            lengthWidth = required;
        } while (true);
        byte[] result = new byte[length];
        int offset = 0;
        result[offset++] = 0x14;
        offset = writeForwardVarInt(result, offset, length);
        if (nameBytes.length <= 126) {
            result[offset++] = (byte) (0x40 + nameBytes.length);
        } else {
            result[offset++] = (byte) 0xBF;
            long nameLength = nameBytes.length;
            for (int i = 0; i < 8; ++i) {
                result[offset++] = (byte) (nameLength >>> (8 * i));
            }
        }
        System.arraycopy(nameBytes, 0, result, offset, nameBytes.length);
        offset += nameBytes.length;
        System.arraycopy(value, 0, result, offset, value.length);
        result[length - 1] = 0x01;
        return result;
    }
private static void assertKeyTooLong(String key, TypeReference<?> targetType) {
        assertKeyTooLong(MAPPER, key, targetType);
    }
private static void assertKeyTooLong(ObjectMapper mapper, String key, TypeReference<?> targetType) {
        StreamConstraintsException exception = assertThrows(StreamConstraintsException.class,
                () -> mapper.readValue(singlePropertyObject(key,
                        VPackWireFixtureTest.hex("41 78")), targetType));
        assertEquals(true, exception.getMessage().contains("Number value length (" + key.length() + ")"));
    }
private static int forwardVarIntLength(int value) {
        int length = 1;
        while ((value >>> (7 * length)) != 0) {
            ++length;
        }
        return length;
    }
private static int writeForwardVarInt(byte[] target, int offset, int value) {
        while (value > 0x7f) {
            target[offset++] = (byte) ((value & 0x7f) | 0x80);
            value >>>= 7;
        }
        target[offset++] = (byte) value;
        return offset;
    }
static class ObjectWrapper {
        public Object wrapped;
    }
static class ObjectArrayWrapper {
        public Object[] wrapped;
    }
static class DecimalWrapper {
        public BigDecimal number;
    }

    void __invoke_testBigDecimal() throws Exception {
        try {
            testBigDecimal();
        } finally {
        }
    }


    void __invoke_testBigDecimalUnlimited() throws Exception {
        try {
            testBigDecimalUnlimited();
        } finally {
        }
    }


    void __invoke_bigNumberMapKeysWithinLimit() throws Exception {
        try {
            bigNumberMapKeysWithinLimit();
        } finally {
        }
    }


    void __invoke_bigIntegerAsMapKey() throws Exception {
        try {
            bigIntegerAsMapKey();
        } finally {
        }
    }


    void __invoke_bigDecimalAsMapKey() throws Exception {
        try {
            bigDecimalAsMapKey();
        } finally {
        }
    }


    void __invoke_floatingPointAsMapKey() throws Exception {
        try {
            floatingPointAsMapKey();
        } finally {
        }
    }


    void __invoke_mapKeyAtLengthLimit() throws Exception {
        try {
            mapKeyAtLengthLimit();
        } finally {
        }
    }


    void __invoke_mapKeyWithLoweredLengthLimit() throws Exception {
        try {
            mapKeyWithLoweredLengthLimit();
        } finally {
        }
    }


    void __invoke_malformedBigNumberMapKey() throws Exception {
        try {
            malformedBigNumberMapKey();
        } finally {
        }
    }

}
