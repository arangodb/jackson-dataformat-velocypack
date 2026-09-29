package tools.jackson.core.unittest.io.schubfach;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static java.lang.Float.intBitsToFloat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0032F1 {
private final VPackFactory factory = new VPackFactory();

    void floatSchubfachCorpusRetainsPortableBinaryValues() throws Exception {
        int[] specialValues = {
                0xFF80_0000, // -infinity
                0xFF7F_FFFF, // -max
                0x8080_0000, // -min normal
                0x8000_0001, // -min subnormal
                0x8000_0000, // -0.0
                0x0000_0000, // +0.0
                0x0000_0001, // +min subnormal
                0x0080_0000, // +min normal
                0x7F7F_FFFF, // +max
                0x7F80_0000, // +infinity
                0x7FC0_0001, // quiet NaN
                0x7F80_0001, // signaling NaN
                0xFFC0_0001, // negative quiet NaN
                0xFF80_0001  // negative signaling NaN
        };
        for (int bits : specialValues) {
            assertFloat(bits);
        }

        // Powers of two exercise the complete finite binary32 exponent range.
        for (float value = Float.MIN_VALUE; Float.isFinite(value); value *= 2.0f) {
            assertFloat(Float.floatToRawIntBits(value));
        }

        // These are the portable value families behind the source's powers,
        // integer, anomaly, Paxson, and random shortest-decimal checks.
        for (int exponent = -45; exponent <= 38; ++exponent) {
            assertFloat(Float.floatToRawIntBits(Float.parseFloat("1e" + exponent)));
        }
        String[] anomalies = {
                "1.1754944E-38", "2.2E-44", "1.0E16", "2.0E16", "3.0E16",
                "5.0E16", "3.0E17", "3.2E18", "3.7E18", "3.7E16", "3.72E17",
                "9.9E-44"
        };
        for (String value : anomalies) {
            assertFloat(Float.floatToRawIntBits(Float.parseFloat(value)));
        }
        for (int value = 1; value < 1 << 23; value += 8191) {
            assertFloat(Float.floatToRawIntBits((float) value));
        }
        Random random = new Random(0x5EED_0032L);
        for (int i = 0; i < 2048; ++i) {
            assertFloat(random.nextInt());
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

    void __invoke_floatSchubfachCorpusRetainsPortableBinaryValues() throws Exception {
        try {
            floatSchubfachCorpusRetainsPortableBinaryValues();
        } finally {
        }
    }

}
