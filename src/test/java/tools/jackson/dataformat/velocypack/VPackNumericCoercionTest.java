package tools.jackson.dataformat.velocypack;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackNumericCoercionTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void exactBcdValueSurvivesAccessorCallOrder() throws Exception {
        try (JsonParser parser = factory.createParser(bcd(false, -2, 0x12, 0x34))) {
            parser.nextToken();
            assertEquals(12, parser.getIntValue());
            assertEquals(12L, parser.getLongValue());
            assertEquals(BigInteger.valueOf(12), parser.getBigIntegerValue());
            assertEquals(12.34f, parser.getFloatValue());
            assertEquals(12.34d, parser.getDoubleValue());
            assertEquals(new BigDecimal("12.34"), parser.getDecimalValue());
            assertEquals(new BigDecimal("12.34"), parser.getNumberValue());
            assertEquals(JsonParser.NumberType.BIG_DECIMAL, parser.getNumberType());
            assertEquals(JsonParser.NumberTypeFP.BIG_DECIMAL, parser.getNumberTypeFP());
        }
    }

    @Test
    void hugeUnsignedIntegerRemainsExactAfterConversions() throws Exception {
        byte[] maxUnsigned = { 0x2F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        try (JsonParser parser = factory.createParser(maxUnsigned)) {
            parser.nextToken();
            BigInteger expected = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
            assertEquals(expected, parser.getBigIntegerValue());
            assertEquals(new BigDecimal(expected), parser.getDecimalValue());
            assertEquals(expected, parser.getNumberValueExact());
            assertEquals(expected, parser.getNumberValueDeferred());
            assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
            assertThrows(InputCoercionException.class, parser::getIntValue);
            assertThrows(InputCoercionException.class, parser::getLongValue);
        }
    }

    @Test
    void scaleExpansionIsBoundedAndNonfiniteCoercionsAreRejected() throws Exception {
        try (JsonParser parser = factory.createParser(bcd(false, Integer.MAX_VALUE, 0x01))) {
            parser.nextToken();
            assertEquals(new BigDecimal(BigInteger.ONE, -Integer.MAX_VALUE),
                    parser.getDecimalValue());
            assertThrows(StreamConstraintsException.class, parser::getBigIntegerValue);
        }

        byte[] nan = { 0x1B, 0x42, 0, 0, 0, 0, 0, (byte) 0xF8, 0x7F };
        try (JsonParser parser = factory.createParser(nan)) {
            parser.nextToken();
            assertThrows(InputCoercionException.class, parser::getIntValue);
            assertThrows(InputCoercionException.class, parser::getLongValue);
            assertThrows(InputCoercionException.class, parser::getBigIntegerValue);
            assertThrows(InputCoercionException.class, parser::getDecimalValue);
        }
    }

    @Test
    void decimalToIntegerUsesPinnedScaleConstraint() throws Exception {
        try (JsonParser parser = factory.createParser(bcd(false, 100_001, 0x01))) {
            parser.nextToken();
            assertThrows(StreamConstraintsException.class, parser::getBigIntegerValue);
        }
    }

    @Test
    void scaleExtremesRemainLazyAndRejectOnlyTheUnrepresentableExponent() throws Exception {
        try (JsonParser parser = factory.createParser(bcd(false, Integer.MAX_VALUE, 0x01))) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            BigDecimal value = parser.getDecimalValue();
            assertEquals(-Integer.MAX_VALUE, value.scale());
            assertThrows(StreamConstraintsException.class, parser::getBigIntegerValue);
            assertEquals(-Integer.MAX_VALUE, parser.getDecimalValue().scale());
        }
        try (JsonParser parser = factory.createParser(bcd(false, -Integer.MAX_VALUE, 0x01))) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            BigDecimal value = parser.getDecimalValue();
            assertEquals(Integer.MAX_VALUE, value.scale());
            assertThrows(StreamConstraintsException.class, parser::getBigIntegerValue);
            assertEquals(Integer.MAX_VALUE, parser.getDecimalValue().scale());
        }
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = factory.createParser(bcd(false, Integer.MIN_VALUE, 0x01))) {
                parser.nextToken();
            }
        });
    }

    @Test
    void digitConstraintsApplyToPositiveAndNegativeBcdBeforeConversion() {
        VPackFactory constrained = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder().maxNumberLength(2).build())
                .build();
        for (boolean negative : new boolean[] { false, true }) {
            for (int exponent : new int[] { 0, -2 }) {
                assertThrows(StreamConstraintsException.class, () -> {
                    try (JsonParser parser = constrained.createParser(
                            bcd(negative, exponent, 0x12, 0x34))) {
                        parser.nextToken();
                    }
                });
            }
        }
    }

    @Test
    void integralCoercionOverflowIsCheckedForEveryExactSourceFamily() throws Exception {
        assertOverflow(new byte[] { 0x27, 0, 0, 0, 0, 1, 0, 0, 0 },
                Long.valueOf(1L << 32), JsonParser.NumberType.LONG, true, false);
        assertOverflow(new byte[] { 0x2F, 0, 0, 0, 0, 0, 0, 0, (byte) 0x80 },
                BigInteger.ONE.shiftLeft(63), JsonParser.NumberType.BIG_INTEGER, true, true);
        BigInteger largeBcd = new BigInteger("99999999999999999999");
        assertOverflow(bcd(false, 0, 0x99, 0x99, 0x99, 0x99, 0x99, 0x99, 0x99,
                0x99, 0x99, 0x99), largeBcd, JsonParser.NumberType.BIG_INTEGER, true, true);
        assertOverflow(bcd(true, 0, 0x99, 0x99, 0x99, 0x99, 0x99, 0x99, 0x99,
                0x99, 0x99, 0x99), largeBcd.negate(), JsonParser.NumberType.BIG_INTEGER,
                true, true);

        byte[] tooLargeDouble = { 0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xE0, 0x7F };
        try (JsonParser parser = factory.createParser(tooLargeDouble)) {
            parser.nextToken();
            assertThrows(InputCoercionException.class, parser::getIntValue);
            assertThrows(InputCoercionException.class, parser::getLongValue);
            assertEquals(Double.longBitsToDouble(0x7FE0_0000_0000_0000L),
                    parser.getNumberValue());
        }
    }

    private void assertOverflow(byte[] bytes, Number expected, JsonParser.NumberType numberType,
            boolean intOverflows, boolean longOverflows) throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            parser.nextToken();
            if (intOverflows) assertThrows(InputCoercionException.class, parser::getIntValue);
            else parser.getIntValue();
            if (longOverflows) assertThrows(InputCoercionException.class, parser::getLongValue);
            else parser.getLongValue();
            assertEquals(expected, parser.getNumberValue());
            assertEquals(expected, parser.getNumberValueExact());
            assertEquals(expected, parser.getNumberValueDeferred());
            assertEquals(numberType, parser.getNumberType());
            assertEquals(new BigDecimal(expected.toString()), parser.getDecimalValue());
            assertEquals(expected.floatValue(), parser.getFloatValue(), 0.0f);
            assertEquals(expected.doubleValue(), parser.getDoubleValue(), 0.0d);
        }
    }

    private static byte[] bcd(boolean negative, int exponent, int... mantissa) {
        byte[] result = new byte[6 + mantissa.length];
        result[0] = (byte) ((negative ? 0xD0 : 0xC8) + 0);
        result[1] = (byte) mantissa.length;
        result[2] = (byte) exponent;
        result[3] = (byte) (exponent >>> 8);
        result[4] = (byte) (exponent >>> 16);
        result[5] = (byte) (exponent >>> 24);
        for (int i = 0; i < mantissa.length; ++i) {
            result[6 + i] = (byte) mantissa[i];
        }
        return result;
    }
}
