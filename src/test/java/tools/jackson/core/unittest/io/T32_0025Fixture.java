package tools.jackson.core.unittest.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0025Fixture {

    void issueDatabind4694NegativeDecimalRetainsTrailingZeroScale() throws Exception {
        BigDecimal expected = new BigDecimal("-11000." + "0".repeat(652));
        byte[] fixture = negativeDecimalFixture();

        // Anchor the independently assembled header before parsing it.
        assertEquals(0xD1, fixture[0] & 0xFF);
        assertEquals(0x49, fixture[1] & 0xFF);
        assertEquals(0x01, fixture[2] & 0xFF);
        assertEquals(0x74, fixture[3] & 0xFF);
        assertEquals(0xFD, fixture[4] & 0xFF);
        assertEquals(0xFF, fixture[5] & 0xFF);
        assertEquals(0xFF, fixture[6] & 0xFF);

        assertExactDecimal(new VPackFactory().createParser(fixture), expected);
        assertExactDecimal(new VPackFactory().createParser(oneByteAtATime(fixture)), expected);
    }
private static void assertExactDecimal(JsonParser parser, BigDecimal expected)
            throws Exception {
        try (parser) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            BigDecimal actual = parser.getDecimalValue();
            assertEquals(expected, actual);
            assertEquals(expected.scale(), actual.scale());
            assertEquals(expected.unscaledValue(), actual.unscaledValue());
            assertEquals(null, parser.nextToken());
        }
    }
private static byte[] negativeDecimalFixture() {
        // -11000 followed by 652 fractional zeroes: 657 decimal digits,
        // encoded as 329 big-endian BCD bytes with a leading zero nibble.
        String digits = "11000" + "0".repeat(652);
        int mantissaLength = (digits.length() + 1) / 2;
        byte[] result = new byte[1 + 2 + 4 + mantissaLength];
        result[0] = (byte) 0xD1; // negative BCD, two-byte mantissa length
        result[1] = (byte) mantissaLength;
        result[2] = (byte) (mantissaLength >>> 8);
        int exponent = -652;
        result[3] = (byte) exponent;
        result[4] = (byte) (exponent >>> 8);
        result[5] = (byte) (exponent >>> 16);
        result[6] = (byte) (exponent >>> 24);

        int digit = 0;
        int mantissaOffset = 7;
        for (int i = 0; i < mantissaLength; ++i) {
            int high = (digits.length() & 1) != 0 && i == 0
                    ? 0 : digits.charAt(digit++) - '0';
            int low = digits.charAt(digit++) - '0';
            result[mantissaOffset + i] = (byte) ((high << 4) | low);
        }
        return result;
    }
private static InputStream oneByteAtATime(byte[] input) {
        return new ByteArrayInputStream(input) {
            @Override
            public int read(byte[] buffer, int offset, int length) {
                return super.read(buffer, offset, Math.min(length, 1));
            }
        };
    }

    void __invoke_issueDatabind4694NegativeDecimalRetainsTrailingZeroScale() throws Exception {
        try {
            issueDatabind4694NegativeDecimalRetainsTrailingZeroScale();
        } finally {
        }
    }

}
