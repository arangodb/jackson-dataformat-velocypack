package tools.jackson.core.unittest.read;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0080F1 {
private static final VPackFactory FACTORY = new VPackFactory();

    void intRangeUsesLiteralVpackSignedIntegers() throws Exception {
        // Equal array: length 12, followed by two four-byte signed integers.
        byte[] input = {
                0x02, 0x0C,
                0x23, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F,
                0x23, 0x00, 0x00, 0x00, (byte) 0x80
        };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
            assertEquals(Integer.MAX_VALUE, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
            assertEquals(Integer.MIN_VALUE, parser.getIntValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void bigDecimalRangeUsesLiteralBcdOutsideLongRange() throws Exception {
        // [-9223372036854775809, 9223372036854775808], each BCD value is 16 bytes.
        byte[] input = {
                0x02, 0x22,
                (byte) 0xD0, 0x0A, 0x00, 0x00, 0x00, 0x00,
                0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x09,
                (byte) 0xC8, 0x0A, 0x00, 0x00, 0x00, 0x00,
                0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x08
        };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
            assertEquals(new BigInteger("-9223372036854775809"),
                    parser.getBigIntegerValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
            assertEquals(new BigInteger("9223372036854775808"), parser.getBigIntegerValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void bigIntegerExponentIsCoercedExactly() throws Exception {
        // Positive BCD: 1 * 10^5. This is a float token with exact integer coercion.
        byte[] input = { (byte) 0xC8, 0x01, 0x05, 0x00, 0x00, 0x00, 0x01 };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(100000, parser.getBigIntegerValue().intValueExact());
            assertEquals(new BigDecimal("1E+5"), parser.getDecimalValue());
        }
    }

    void bcdBeyondDoubleRangeIsFiniteAndExact() throws Exception {
        // Positive BCD: 2 * 10^309, beyond IEEE-754 double range.
        byte[] input = { (byte) 0xC8, 0x01, 0x35, 0x01, 0x00, 0x00, 0x02 };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertFalse(parser.isNaN());
            assertEquals(new BigDecimal("2E+309"), parser.getDecimalValue());
            assertFalse(parser.isNaN());
        }
    }

    void databind4694ScaledDecimalIsFiniteAndExact() throws Exception {
        // Negative BCD: 11000000 * 10^-3 = -11000.000, retaining scale three.
        byte[] input = {
                (byte) 0xD0, 0x04, (byte) 0xFD, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                0x11, 0x00, 0x00, 0x00
        };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertFalse(parser.isNaN());
            BigDecimal value = parser.getDecimalValue();
            assertEquals(new BigDecimal("-11000.000"), value);
            assertEquals(3, value.scale());
            assertFalse(parser.isNaN());
        }
    }

    void __invoke_intRangeUsesLiteralVpackSignedIntegers() throws Exception {
        try {
            intRangeUsesLiteralVpackSignedIntegers();
        } finally {
        }
    }


    void __invoke_bigDecimalRangeUsesLiteralBcdOutsideLongRange() throws Exception {
        try {
            bigDecimalRangeUsesLiteralBcdOutsideLongRange();
        } finally {
        }
    }


    void __invoke_bigIntegerExponentIsCoercedExactly() throws Exception {
        try {
            bigIntegerExponentIsCoercedExactly();
        } finally {
        }
    }


    void __invoke_bcdBeyondDoubleRangeIsFiniteAndExact() throws Exception {
        try {
            bcdBeyondDoubleRangeIsFiniteAndExact();
        } finally {
        }
    }


    void __invoke_databind4694ScaledDecimalIsFiniteAndExact() throws Exception {
        try {
            databind4694ScaledDecimalIsFiniteAndExact();
        } finally {
        }
    }

}
