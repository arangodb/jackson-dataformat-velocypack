package tools.jackson.core.unittest.io.schubfach;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0031Fixture {
private final VPackFactory factory = new VPackFactory();

    void doubleBoundariesUseExactNativeBinary64Fixtures() throws Exception {
        long[] values = {
                0x4163_12D0_0000_0000L, // 1.0E7
                0x4163_12CF_FFFF_FFFFL, // 9999999.999999998
                0x3F50_624D_D2F1_A9FCL, // 1.0E-3
                0x3F50_624D_D2F1_A9FBL, // just below 1.0E-3
                0x7FEF_FFFF_FFFF_FFFFL, // Double.MAX_VALUE
                0x0000_0000_0000_0001L, // Double.MIN_VALUE
                0x0010_0000_0000_0000L, // Double.MIN_NORMAL
                0x8000_0000_0000_0000L, // -0.0
                0x7FF0_0000_0000_0000L, // +infinity
                0x7FF8_0000_0000_0000L  // canonical NaN
        };

        // Anchor the test-only assembler against independently written bytes
        // before using it for the larger value family.
        assertArrayEquals(new byte[] {
                0x1B, 0x00, 0x00, 0x00, 0x00, (byte) 0xD0, 0x12, 0x63, 0x41
        }, literalDouble(values[0]));
        assertArrayEquals(new byte[] {
                0x1B, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xEF, 0x7F
        }, literalDouble(values[4]));

        for (long bits : values) {
            assertLiteralDouble(bits);
            assertWrittenDouble(bits);
        }
    }

    void doublePowerAndRandomFamiliesRemainRawBits() throws Exception {
        long[] powers = new long[3000];
        int count = 0;
        for (double value = Double.MIN_VALUE; value <= Double.MAX_VALUE; value *= 2.0) {
            powers[count++] = Double.doubleToRawLongBits(value);
        }
        for (int exponent = -324; exponent <= 309; ++exponent) {
            powers[count++] = Double.doubleToRawLongBits(Double.parseDouble("1e" + exponent));
        }

        Random random = new Random(0x5EED_0031L);
        long[] randomBits = new long[2048];
        for (int i = 0; i < randomBits.length; ++i) {
            randomBits[i] = random.nextLong();
        }

        for (int i = 0; i < count; ++i) {
            assertLiteralDouble(powers[i]);
            assertWrittenDouble(powers[i]);
        }
        for (long bits : randomBits) {
            assertLiteralDouble(bits);
            assertWrittenDouble(bits);
        }
    }
private void assertLiteralDouble(long bits) throws Exception {
        try (VPackParser parser = (VPackParser) factory.createParser(literalDouble(bits))) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(VPackType.DOUBLE, parser.currentVPackType());
            assertEquals(bits, VPackTestAccess.currentDoubleBits(parser));
            assertEquals(bits, Double.doubleToRawLongBits(parser.getDoubleValue()));
            assertNull(parser.nextToken());
        }
    }
private void assertWrittenDouble(long bits) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeNumber(Double.longBitsToDouble(bits));
        }
        assertArrayEquals(literalDouble(bits), output.toByteArray());
    }
private static byte[] literalDouble(long bits) {
        byte[] result = new byte[9];
        result[0] = 0x1B;
        for (int i = 0; i < 8; ++i) {
            result[i + 1] = (byte) (bits >>> (8 * i));
        }
        return result;
    }

    void __invoke_doubleBoundariesUseExactNativeBinary64Fixtures() throws Exception {
        try {
            doubleBoundariesUseExactNativeBinary64Fixtures();
        } finally {
        }
    }


    void __invoke_doublePowerAndRandomFamiliesRemainRawBits() throws Exception {
        try {
            doublePowerAndRandomFamiliesRemainRawBits();
        } finally {
        }
    }

}
