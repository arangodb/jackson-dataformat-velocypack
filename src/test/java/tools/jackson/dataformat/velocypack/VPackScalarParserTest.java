package tools.jackson.dataformat.velocypack;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.io.SerializedString;
import tools.jackson.core.sym.PropertyNameMatcher;
import tools.jackson.core.sym.SimpleNameMatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackScalarParserTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void independentLiteralScalarsExposeCanonicalTokensAndPhysicalTypes() throws Exception {
        assertScalar(new byte[] { 0x18 }, JsonToken.VALUE_NULL, VPackType.NULL, null);
        assertScalar(new byte[] { 0x19 }, JsonToken.VALUE_FALSE, VPackType.BOOLEAN, Boolean.FALSE);
        assertScalar(new byte[] { 0x1A }, JsonToken.VALUE_TRUE, VPackType.BOOLEAN, Boolean.TRUE);
        assertScalar(new byte[] { 0x30 }, JsonToken.VALUE_NUMBER_INT, VPackType.SMALL_INTEGER, 0);
        assertScalar(new byte[] { 0x3A }, JsonToken.VALUE_NUMBER_INT, VPackType.SMALL_INTEGER, -6);
        assertScalar(new byte[] { 0x20, (byte) 0x80 }, JsonToken.VALUE_NUMBER_INT,
                VPackType.SIGNED_INTEGER, -128);
        assertScalar(new byte[] { 0x28, (byte) 0xFF }, JsonToken.VALUE_NUMBER_INT,
                VPackType.UNSIGNED_INTEGER, 255);
    }

    @Test
    void readsEverySignedAndUnsignedIntegerWidthFromIndependentLiteralBytes() throws Exception {
        assertInteger(bytes(0x20, 0x80), BigInteger.valueOf(-128), JsonParser.NumberType.INT,
                VPackType.SIGNED_INTEGER);
        assertInteger(bytes(0x21, 0x00, 0x80), BigInteger.valueOf(-32768),
                JsonParser.NumberType.INT, VPackType.SIGNED_INTEGER);
        assertInteger(bytes(0x22, 0x00, 0x00, 0x80), BigInteger.valueOf(-8_388_608),
                JsonParser.NumberType.INT, VPackType.SIGNED_INTEGER);
        assertInteger(bytes(0x23, 0x00, 0x00, 0x00, 0x80), BigInteger.valueOf(Integer.MIN_VALUE),
                JsonParser.NumberType.INT, VPackType.SIGNED_INTEGER);
        assertInteger(bytes(0x24, 0x00, 0x00, 0x00, 0x00, 0x80), BigInteger.ONE.shiftLeft(39).negate(),
                JsonParser.NumberType.LONG, VPackType.SIGNED_INTEGER);
        assertInteger(bytes(0x25, 0x00, 0x00, 0x00, 0x00, 0x00, 0x80), BigInteger.ONE.shiftLeft(47).negate(),
                JsonParser.NumberType.LONG, VPackType.SIGNED_INTEGER);
        assertInteger(bytes(0x26, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x80), BigInteger.ONE.shiftLeft(55).negate(),
                JsonParser.NumberType.LONG, VPackType.SIGNED_INTEGER);
        assertInteger(bytes(0x27, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x80),
                BigInteger.ONE.shiftLeft(63).negate(), JsonParser.NumberType.LONG,
                VPackType.SIGNED_INTEGER);

        assertInteger(bytes(0x28, 0xFF), BigInteger.valueOf(255), JsonParser.NumberType.INT,
                VPackType.UNSIGNED_INTEGER);
        assertInteger(bytes(0x29, 0xFF, 0xFF), BigInteger.valueOf(65_535),
                JsonParser.NumberType.INT, VPackType.UNSIGNED_INTEGER);
        assertInteger(bytes(0x2A, 0xFF, 0xFF, 0xFF), BigInteger.valueOf(16_777_215),
                JsonParser.NumberType.INT, VPackType.UNSIGNED_INTEGER);
        assertInteger(bytes(0x2B, 0xFF, 0xFF, 0xFF, 0xFF),
                BigInteger.ONE.shiftLeft(32).subtract(BigInteger.ONE), JsonParser.NumberType.LONG,
                VPackType.UNSIGNED_INTEGER);
        assertInteger(bytes(0x2C, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF),
                BigInteger.ONE.shiftLeft(40).subtract(BigInteger.ONE), JsonParser.NumberType.LONG,
                VPackType.UNSIGNED_INTEGER);
        assertInteger(bytes(0x2D, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF),
                BigInteger.ONE.shiftLeft(48).subtract(BigInteger.ONE), JsonParser.NumberType.LONG,
                VPackType.UNSIGNED_INTEGER);
        assertInteger(bytes(0x2E, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF),
                BigInteger.ONE.shiftLeft(56).subtract(BigInteger.ONE), JsonParser.NumberType.LONG,
                VPackType.UNSIGNED_INTEGER);
        assertInteger(bytes(0x2F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF),
                BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE),
                JsonParser.NumberType.BIG_INTEGER, VPackType.UNSIGNED_INTEGER);
    }

    private void assertInteger(byte[] literal, BigInteger expected,
            JsonParser.NumberType numberType, VPackType physicalType) throws Exception {
        try (VPackParser parser = (VPackParser) factory.createParser(literal)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(physicalType, parser.currentVPackType());
            assertEquals(numberType, parser.getNumberType());
            assertEquals(expected, parser.getBigIntegerValue());
            assertEquals(new BigInteger(parser.getNumberValue().toString()), expected);
        }
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = (byte) values[i];
        }
        return result;
    }

    @Test
    void exactUnsignedDoubleDateAndSentinelsDoNotRoundTripThroughText() throws Exception {
        byte[] maxUnsigned = new byte[] { 0x2F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        try (JsonParser parser = factory.createParser(maxUnsigned)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
            assertEquals(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE),
                    parser.getNumberValueExact());
            assertEquals(VPackType.UNSIGNED_INTEGER, ((VPackParser) parser).currentVPackType());
            assertEquals(JsonParser.NumberTypeFP.UNKNOWN, parser.getNumberTypeFP());
        }

        long nanBits = 0x7ff8_0000_0000_0042L;
        byte[] doubleBytes = new byte[] { 0x1B, 0x42, 0, 0, 0, 0, 0, (byte) 0xF8, 0x7F };
        try (VPackParser parser = (VPackParser) factory.createParser(doubleBytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
            assertEquals(JsonParser.NumberTypeFP.DOUBLE64, parser.getNumberTypeFP());
            assertEquals(nanBits, parser.currentDoubleBits());
            assertEquals(Double.longBitsToDouble(nanBits), parser.getDoubleValue());
            assertTrue(parser.isNaN());
            assertEquals(VPackType.DOUBLE, parser.currentVPackType());
            assertEquals(null, parser.currentAttributeId());
        }

        byte[] date = { 0x1C, 0x15, (byte) 0xCD, 0x5B, 0x07, 0, 0, 0, 0 };
        try (VPackParser parser = (VPackParser) factory.createParser(date)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.LONG, parser.getNumberType());
            assertEquals(123456789L, parser.getLongValue());
            assertEquals(VPackType.DATE, parser.currentVPackType());
        }

        try (VPackParser parser = (VPackParser) factory.createParser(new byte[] { 0x1E, 0x1F })) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertSame(VPackSpecialValue.MIN_KEY, parser.getEmbeddedObject());
            assertEquals(VPackType.MIN_KEY, parser.currentVPackType());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertSame(VPackSpecialValue.MAX_KEY, parser.getEmbeddedObject());
            assertEquals(VPackType.MAX_KEY, parser.currentVPackType());
            assertNull(parser.nextToken());
            assertNull(parser.currentVPackType());
        }
    }

    @Test
    void scalarStateClearsBetweenRootsAndAfterExplicitClear() throws Exception {
        try (VPackParser parser = (VPackParser) factory.createParser(new byte[] { 0x31, 0x18 })) {
            assertFalse(parser.hasCurrentToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(VPackType.SMALL_INTEGER, parser.currentVPackType());
            parser.clearCurrentToken();
            assertFalse(parser.hasCurrentToken());
            assertNull(parser.currentVPackType());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(VPackType.NULL, parser.currentVPackType());
            assertNull(parser.nextToken());
            assertTrue(parser.isClosed());
        }
    }

    @Test
    void nameConvenienceMethodsAdvanceLikeNextTokenForScalarRoots() throws Exception {
        try (VPackParser parser = (VPackParser) factory.createParser(new byte[] { 0x30, 0x31 })) {
            assertNull(parser.nextName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.currentToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        }
        try (VPackParser parser = (VPackParser) factory.createParser(new byte[] { 0x30, 0x31 })) {
            assertFalse(parser.nextName(new SerializedString("not-a-name")));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.currentToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        }
        PropertyNameMatcher matcher = SimpleNameMatcher.construct(Locale.ROOT, List.of("not-a-name"));
        try (VPackParser parser = (VPackParser) factory.createParser(new byte[] { 0x30, 0x31 })) {
            assertEquals(PropertyNameMatcher.MATCH_ODD_TOKEN, parser.nextNameMatch(matcher));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.currentToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        }
    }

    @Test
    void baseScalarCoercionsRemainAvailable() throws Exception {
        try (VPackParser parser = (VPackParser) factory.createParser(new byte[] { 0x30, 0x31, 0x18 })) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertFalse(parser.getValueAsBoolean(true));
            assertFalse(parser.hasStringCharacters());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertTrue(parser.getValueAsBoolean(false));
            assertFalse(parser.hasStringCharacters());

            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals("fallback", parser.getValueAsString("fallback"));
            assertFalse(parser.hasStringCharacters());
        }
    }

    @Test
    void canonicalNumericAccessorsIgnoreCoercionCacheOrder() throws Exception {
        assertCanonicalAfterCoercions(new byte[] {
                0x1B, 0, 0, 0, 0, 0, 0x40, 0x45, 0x40
        }, 42.5d, JsonParser.NumberType.DOUBLE, JsonParser.NumberTypeFP.DOUBLE64);
        assertCanonicalAfterCoercions(new byte[] {
                0x23, 0x40, (byte) 0xE2, 0x01, 0x00
        }, 123456, JsonParser.NumberType.INT, JsonParser.NumberTypeFP.UNKNOWN);
        assertCanonicalAfterCoercions(new byte[] {
                0x1C, 0x2A, 0, 0, 0, 0, 0, 0, 0
        }, 42L, JsonParser.NumberType.LONG, JsonParser.NumberTypeFP.UNKNOWN);
    }

    private void assertCanonicalAfterCoercions(byte[] bytes, Number expected,
        JsonParser.NumberType numberType, JsonParser.NumberTypeFP fpType) throws Exception {
        try (VPackParser parser = (VPackParser) factory.createParser(bytes)) {
            JsonToken expectedToken = expected instanceof Double
                    ? JsonToken.VALUE_NUMBER_FLOAT : JsonToken.VALUE_NUMBER_INT;
            assertEquals(expectedToken, parser.nextToken(),
                    expected instanceof Double ? "double token" : "integer token");
            parser.getIntValue();
            parser.getLongValue();
            parser.getBigIntegerValue();
            parser.getFloatValue();
            parser.getDoubleValue();
            parser.getDecimalValue();
            assertEquals(expected, parser.getNumberValue());
            assertEquals(expected, parser.getNumberValueExact());
            assertEquals(expected, parser.getNumberValueDeferred());
            assertEquals(numberType, parser.getNumberType());
            assertEquals(fpType, parser.getNumberTypeFP());
        }
    }

    private void assertScalar(byte[] bytes, JsonToken token, VPackType type, Object value)
            throws Exception {
        try (VPackParser parser = (VPackParser) factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertEquals(type, parser.currentVPackType());
            if (value instanceof Boolean bool) assertEquals(bool, parser.getBooleanValue());
            else if (value != null) assertEquals(value, parser.getNumberValue());
            assertNull(parser.nextToken());
        }
    }
}
