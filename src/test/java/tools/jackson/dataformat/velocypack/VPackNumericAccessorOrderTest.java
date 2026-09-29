package tools.jackson.dataformat.velocypack;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Numeric expectations in this class are derived from literal bytes, not from
 * the VPack writer or any production numeric helper.
 */
class VPackNumericAccessorOrderTest {
    private static final BigInteger UINT64_BIT_63 = BigInteger.ONE.shiftLeft(63);
    private static final long NAN_BITS = 0x7ff8_0000_0000_0042L;

    private final VPackFactory factory = new VPackFactory();

    @Test
    void exactIntegerCanonicalValuesSurviveAccessorPermutations() throws Exception {
        assertIntegerCase(new byte[] { 0x20, 0x2A }, 42, JsonParser.NumberType.INT,
                VPackType.SIGNED_INTEGER);
        assertIntegerCase(new byte[] { 0x23, 0x40, (byte) 0xE2, 0x01, 0x00 },
                123456, JsonParser.NumberType.INT, VPackType.SIGNED_INTEGER);
        assertIntegerCase(new byte[] { 0x21, (byte) 0x80, (byte) 0xFF },
                -128, JsonParser.NumberType.INT, VPackType.SIGNED_INTEGER);
        assertIntegerCase(new byte[] { (byte) 0xC8, 0x01, 0, 0, 0, 0, 0x00 },
                BigInteger.ZERO, JsonParser.NumberType.BIG_INTEGER, VPackType.BCD);
        assertIntegerCase(new byte[] { (byte) 0xD0, 0x01, 0, 0, 0, 0, 0x42 },
                BigInteger.valueOf(-42), JsonParser.NumberType.BIG_INTEGER, VPackType.BCD);
    }

    @Test
    void exactDecimalCanonicalValuesSurviveAccessorPermutations() throws Exception {
        assertDecimalCase(new byte[] { (byte) 0xC8, 0x02, (byte) 0xFE, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, 0x12, 0x34 },
                new BigDecimal("12.34"));
        assertDecimalCase(new byte[] { (byte) 0xD0, 0x02, (byte) 0xFE, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, 0x12, 0x34 },
                new BigDecimal("-12.34"));
        assertDoubleCase(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0x40, 0x45, 0x40 },
                42.5d);
    }

    @Test
    void rawNaNPayloadAndNegativeZeroSurviveConversions() throws Exception {
        byte[] nan = { 0x1B, 0x42, 0, 0, 0, 0, 0, (byte) 0xF8, 0x7F };
        try (VPackParser parser = (VPackParser) factory.createParser(nan)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertTrue(Double.isNaN(parser.getFloatValue()));
            assertTrue(Double.isNaN(parser.getDoubleValue()));
            assertThrows(InputCoercionException.class, parser::getIntValue);
            assertThrows(InputCoercionException.class, parser::getLongValue);
            assertThrows(InputCoercionException.class, parser::getBigIntegerValue);
            assertThrows(InputCoercionException.class, parser::getDecimalValue);
            assertEquals(NAN_BITS, parser.currentDoubleBits());
            assertEquals(NAN_BITS, Double.doubleToRawLongBits((Double) parser.getNumberValue()));
            assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
            assertEquals(JsonParser.NumberTypeFP.DOUBLE64, parser.getNumberTypeFP());
        }

        byte[] negativeZero = { 0x1B, 0, 0, 0, 0, 0, 0, 0, (byte) 0x80 };
        try (VPackParser parser = (VPackParser) factory.createParser(negativeZero)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(0x8000_0000_0000_0000L, parser.currentDoubleBits());
            assertEquals(0x8000_0000_0000_0000L,
                    Double.doubleToRawLongBits(parser.getDoubleValue()));
            assertEquals(0x8000_0000, Float.floatToRawIntBits(parser.getFloatValue()));
            assertEquals(0, parser.getIntValue());
            assertEquals(0L, parser.getLongValue());
            assertEquals(BigInteger.ZERO, parser.getBigIntegerValue());
            assertEquals(new BigDecimal("0.0"), parser.getDecimalValue());
            assertEquals(0x8000_0000_0000_0000L,
                    Double.doubleToRawLongBits((Double) parser.getNumberValueExact()));
        }
    }

    private void assertIntegerCase(byte[] bytes, Number expected, JsonParser.NumberType numberType,
            VPackType physical)
            throws Exception {
        for (int order = 0; order < 3; ++order) {
            try (VPackParser parser = (VPackParser) factory.createParser(bytes)) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(physical, parser.currentVPackType());
                if (order == 0) {
                    assertEquals(expected, parser.getNumberValue());
                    exerciseIntegerConversions(parser);
                } else if (order == 1) {
                    exerciseIntegerConversions(parser);
                    assertEquals(expected, parser.getNumberValue());
                } else {
                    assertEquals(expected, parser.getNumberValueExact());
                    exerciseIntegerConversions(parser);
                    assertEquals(expected, parser.getNumberValueDeferred());
                }
                assertEquals(expected, parser.getNumberValue());
                assertEquals(expected, parser.getNumberValueExact());
                assertEquals(numberType, parser.getNumberType());
                assertEquals(JsonParser.NumberTypeFP.UNKNOWN, parser.getNumberTypeFP());
            }
        }
    }

    private void exerciseIntegerConversions(JsonParser parser) throws Exception {
        BigInteger exact = parser.getBigIntegerValue();
        if (exact.compareTo(BigInteger.valueOf(-128)) < 0
                || exact.compareTo(BigInteger.valueOf(255)) > 0) {
            assertThrows(InputCoercionException.class, parser::getByteValue);
        } else {
            assertEquals(exact.byteValue(), parser.getByteValue());
        }
        if (exact.compareTo(BigInteger.valueOf(Short.MIN_VALUE)) < 0
                || exact.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0) {
            assertThrows(InputCoercionException.class, parser::getShortValue);
        } else {
            assertEquals(exact.shortValue(), parser.getShortValue());
        }
        assertEquals(exact.intValue(), parser.getIntValue());
        assertEquals(exact.longValue(), parser.getLongValue());
        assertEquals(exact.intValue(), parser.getValueAsInt());
        assertEquals(exact.longValue(), parser.getValueAsLong());
        assertEquals(exact.doubleValue(), parser.getValueAsDouble(), 0.0d);
        assertEquals(exact, parser.getDecimalValue().toBigIntegerExact());
        assertEquals(exact.floatValue(), parser.getFloatValue(), 0.0f);
        assertEquals(exact.doubleValue(), parser.getDoubleValue(), 0.0d);
    }

    private void assertDecimalCase(byte[] bytes, BigDecimal expected) throws Exception {
        for (int order = 0; order < 3; ++order) {
            try (VPackParser parser = (VPackParser) factory.createParser(bytes)) {
                assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                if (order == 0) {
                    assertEquals(expected, parser.getNumberValue());
                    exerciseDecimalConversions(parser, expected);
                } else if (order == 1) {
                    exerciseDecimalConversions(parser, expected);
                    assertEquals(expected, parser.getNumberValue());
                } else {
                    assertEquals(expected, parser.getNumberValueDeferred());
                    exerciseDecimalConversions(parser, expected);
                    assertEquals(expected, parser.getNumberValueExact());
                }
                assertEquals(expected, parser.getNumberValue());
                assertEquals(expected, parser.getNumberValueExact());
                assertEquals(JsonParser.NumberType.BIG_DECIMAL, parser.getNumberType());
                assertEquals(JsonParser.NumberTypeFP.BIG_DECIMAL, parser.getNumberTypeFP());
            }
        }
    }

    private void exerciseDecimalConversions(JsonParser parser, BigDecimal expected)
            throws Exception {
        assertEquals(expected.byteValue(), parser.getByteValue());
        assertEquals(expected.shortValue(), parser.getShortValue());
        assertEquals(expected.intValue(), parser.getIntValue());
        assertEquals(expected.longValue(), parser.getLongValue());
        assertEquals(expected.intValue(), parser.getValueAsInt());
        assertEquals(expected.longValue(), parser.getValueAsLong());
        assertEquals(expected.doubleValue(), parser.getValueAsDouble(), 0.0d);
        assertEquals(expected.toBigInteger(), parser.getBigIntegerValue());
        assertEquals(expected.floatValue(), parser.getFloatValue(), 0.0f);
        assertEquals(expected.doubleValue(), parser.getDoubleValue(), 0.0d);
        assertEquals(expected, parser.getDecimalValue());
    }

    private void assertDoubleCase(byte[] bytes, double expected) throws Exception {
        BigDecimal decimal = BigDecimal.valueOf(expected);
        for (int order = 0; order < 3; ++order) {
            try (VPackParser parser = (VPackParser) factory.createParser(bytes)) {
                assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                if (order == 0) {
                    assertEquals(expected, parser.getNumberValue());
                    exerciseDoubleConversions(parser, expected, decimal);
                } else if (order == 1) {
                    exerciseDoubleConversions(parser, expected, decimal);
                    assertEquals(expected, parser.getNumberValue());
                } else {
                    assertEquals(expected, parser.getNumberValueDeferred());
                    exerciseDoubleConversions(parser, expected, decimal);
                    assertEquals(expected, parser.getNumberValueExact());
                }
                assertEquals(expected, parser.getNumberValue());
                assertEquals(expected, parser.getNumberValueExact());
                assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
                assertEquals(JsonParser.NumberTypeFP.DOUBLE64, parser.getNumberTypeFP());
            }
        }
    }

    private void exerciseDoubleConversions(JsonParser parser, double expected,
            BigDecimal decimal) throws Exception {
        assertEquals((byte) expected, parser.getByteValue());
        assertEquals((short) expected, parser.getShortValue());
        assertEquals((int) expected, parser.getIntValue());
        assertEquals((long) expected, parser.getLongValue());
        assertEquals((int) expected, parser.getValueAsInt());
        assertEquals((long) expected, parser.getValueAsLong());
        assertEquals(expected, parser.getValueAsDouble(), 0.0d);
        assertEquals(decimal.toBigInteger(), parser.getBigIntegerValue());
        assertEquals((float) expected, parser.getFloatValue(), 0.0f);
        assertEquals(Double.doubleToRawLongBits(expected),
                Double.doubleToRawLongBits(parser.getDoubleValue()));
        assertEquals(decimal, parser.getDecimalValue());
    }
}
