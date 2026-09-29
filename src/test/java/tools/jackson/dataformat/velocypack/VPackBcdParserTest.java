package tools.jackson.dataformat.velocypack;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackBcdParserTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void literalBcdKeepsCanonicalTokenAndScale() throws Exception {
        try (JsonParser parser = factory.createParser(bcd(false, 0, 0x01, 0x23, 0x45))) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
            assertEquals(BigInteger.valueOf(12345L), parser.getNumberValue());
            assertEquals(VPackType.BCD, ((VPackParser) parser).currentVPackType());
        }
        try (JsonParser parser = factory.createParser(bcd(false, -1, 0x12, 0x34, 0x50))) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_DECIMAL, parser.getNumberType());
            assertEquals(JsonParser.NumberTypeFP.BIG_DECIMAL, parser.getNumberTypeFP());
            assertEquals(new BigDecimal("12345.0"), parser.getNumberValue());
            assertEquals(1, parser.getDecimalValue().scale());
        }
        try (JsonParser parser = factory.createParser(bcd(true, 2, 0x00, 0x01))) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(new BigDecimal("-1E+2"), parser.getDecimalValue());
            assertEquals(-100, parser.getBigIntegerValue().intValue());
        }
    }

    @Test
    void readsEveryPositiveAndNegativeBcdLengthWidthFromIndependentBytes() throws Exception {
        for (int width = 1; width <= 8; ++width) {
            for (boolean negative : new boolean[] { false, true }) {
                byte[] literal = bcdWithLengthWidth(width, negative);
                try (JsonParser parser = factory.createParser(literal)) {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
                    assertEquals(BigInteger.valueOf(negative ? -42 : 42),
                            parser.getNumberValueExact());
                    assertEquals(VPackType.BCD, ((VPackParser) parser).currentVPackType());
                }
            }
        }
    }

    @Test
    void leadingZeroAndNegativeZeroAreExactJavaValues() throws Exception {
        try (JsonParser parser = factory.createParser(bcd(false, 0, 0x00, 0x00, 0x07))) {
            parser.nextToken();
            assertEquals(BigInteger.valueOf(7), parser.getNumberValueExact());
        }
        try (JsonParser parser = factory.createParser(bcd(true, -2, 0x00, 0x00))) {
            parser.nextToken();
            BigDecimal value = parser.getDecimalValue();
            assertEquals(BigDecimal.ZERO.setScale(2), value);
            assertEquals(0, value.signum());
        }
    }

    @Test
    void malformedDigitsAndUnrepresentableScaleFailBeforeMaterialization() {
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = factory.createParser(bcd(false, 0, 0x1A))) {
                parser.nextToken();
            }
        });
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = factory.createParser(bcd(true, 0, 0x1A))) {
                parser.nextToken();
            }
        });
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = factory.createParser(bcd(false, Integer.MIN_VALUE, 0x01))) {
                parser.nextToken();
            }
        });
    }

    @Test
    void bcdDigitsUseThePinnedNumberLengthConstraint() {
        VPackFactory constrained = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder().maxNumberLength(3).build())
                .build();
        assertThrows(StreamConstraintsException.class, () -> {
            try (JsonParser parser = constrained.createParser(bcd(false, 0, 0x01, 0x23))) {
                parser.nextToken();
            }
        });
    }

    private static byte[] bcd(boolean negative, int exponent, int... mantissa) {
        byte[] result = new byte[1 + 1 + 4 + mantissa.length];
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

    private static byte[] bcdWithLengthWidth(int width, boolean negative) {
        byte[] result = new byte[1 + width + 4 + 1];
        result[0] = (byte) ((negative ? 0xD0 : 0xC8) + width - 1);
        result[1] = 1;
        result[result.length - 1] = 0x42;
        return result;
    }
}
