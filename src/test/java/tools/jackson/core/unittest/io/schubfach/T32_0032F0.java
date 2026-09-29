package tools.jackson.core.unittest.io.schubfach;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static java.lang.Float.intBitsToFloat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0032F0 {
private final VPackFactory factory = new VPackFactory();

    void simpleDoubleValuesUseLiteralNativeBinary64() throws Exception {
        long[] values = {
                0x0000_0000_0000_0000L,
                0x8000_0000_0000_0000L,
                0x3FF0_0000_0000_0000L,
                0xBFF0_0000_0000_0000L,
                0x7FF8_0000_0000_0000L,
                0x7FF0_0000_0000_0000L,
                0xFFF0_0000_0000_0000L
        };
        for (long bits : values) {
            assertDouble(bits);
        }
    }

    void subnormalTransitionUsesLiteralNativeBinary64() throws Exception {
        assertDouble(0x0010_0000_0000_0000L);
    }

    void roundingAndRegressionValuesRemainExactBinary64() throws Exception {
        long[] values = {
                Double.doubleToRawLongBits(-2.109808898695963E16),
                Double.doubleToRawLongBits(4.940656E-318),
                Double.doubleToRawLongBits(1.18575755E-316),
                Double.doubleToRawLongBits(2.989102097996E-312),
                Double.doubleToRawLongBits(9.0608011534336E15),
                Double.doubleToRawLongBits(4.708356024711512E18),
                Double.doubleToRawLongBits(9.409340012568248E18),
                Double.doubleToRawLongBits(1.8531501765868567E21),
                Double.doubleToRawLongBits(-3.347727380279489E33),
                Double.doubleToRawLongBits(1.9430376160308388E16),
                Double.doubleToRawLongBits(-6.9741824662760956E19),
                Double.doubleToRawLongBits(4.3816050601147837E18)
        };
        for (long bits : values) {
            assertDouble(bits);
        }
    }
private void assertDouble(long bits) throws Exception {
        byte[] expected = literalDouble(bits);
        try (JsonParser parser = factory.createParser(expected)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(bits, VPackTestAccess.currentDoubleBits(((VPackParser) parser)));
            assertEquals(bits, Double.doubleToRawLongBits(parser.getDoubleValue()));
            assertEquals(null, parser.nextToken());
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeNumber(Double.longBitsToDouble(bits));
        }
        assertArrayEquals(expected, output.toByteArray());
    }
private void assertFloat(int bits) throws Exception {
        float value = intBitsToFloat(bits);
        long widenedBits = Double.doubleToRawLongBits((double) value);
        byte[] expected = literalDouble(widenedBits);

        try (JsonParser parser = factory.createParser(expected)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            if (Float.isNaN(value)) {
                assertTrue(Float.isNaN(parser.getFloatValue()));
            } else {
                assertEquals(bits, Float.floatToRawIntBits(parser.getFloatValue()));
            }
            assertEquals(widenedBits, VPackTestAccess.currentDoubleBits(((VPackParser) parser)));
            assertEquals(null, parser.nextToken());
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeNumber(value);
        }
        assertArrayEquals(expected, output.toByteArray());
    }
private static byte[] literalDouble(long bits) {
        byte[] result = new byte[9];
        result[0] = 0x1B;
        for (int i = 0; i < 8; ++i) {
            result[i + 1] = (byte) (bits >>> (i * 8));
        }
        return result;
    }

    void __invoke_simpleDoubleValuesUseLiteralNativeBinary64() throws Exception {
        try {
            simpleDoubleValuesUseLiteralNativeBinary64();
        } finally {
        }
    }


    void __invoke_subnormalTransitionUsesLiteralNativeBinary64() throws Exception {
        try {
            subnormalTransitionUsesLiteralNativeBinary64();
        } finally {
        }
    }


    void __invoke_roundingAndRegressionValuesRemainExactBinary64() throws Exception {
        try {
            roundingAndRegressionValuesRemainExactBinary64();
        } finally {
        }
    }

}
