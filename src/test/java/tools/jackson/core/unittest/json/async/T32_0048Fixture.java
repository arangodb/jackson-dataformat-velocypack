package tools.jackson.core.unittest.json.async;

import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0048Fixture {
private static final byte[] LONG_MAX = {
            0x2F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F
    };
private static final byte[] TWO_TO_63 = {
            0x2F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            (byte) 0x80
    };
private static final byte[] POSITIVE_BIG_INTEGER = {
            (byte) 0xC8, 0x0A, 0x00, 0x00, 0x00, 0x00,
            0x36, (byte) 0x89, 0x34, (byte) 0x88, 0x14,
            0x74, 0x19, 0x10, 0x32, 0x31
    };
private static final byte[] NEGATIVE_BIG_INTEGER = {
            (byte) 0xD0, 0x0A, 0x00, 0x00, 0x00, 0x00,
            0x36, (byte) 0x89, 0x34, (byte) 0x88, 0x14,
            0x74, 0x19, 0x10, 0x32, 0x31
    };
private static final byte[] INTEGER_ARRAY = {
            0x13, 0x08, 0x31, 0x28, (byte) 0xFF, 0x20, (byte) 0xF0, 0x03
    };

    void hex16DigitsLongMaxRetainsExactLongValue() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(LONG_MAX)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(Long.MAX_VALUE, parser.getLongValue());
            assertNull(parser.nextToken());
        }
    }

    void hex16DigitsOverflowRetainsExactBigIntegerValue() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(TWO_TO_63)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(BigInteger.ONE.shiftLeft(63), parser.getBigIntegerValue());
            assertNull(parser.nextToken());
        }
    }

    void hexBigIntegerRangeRetainsExactValueWithoutFloatingPoint() throws Exception {
        BigInteger expected = new BigInteger("1ffffffffffffffff", 16);
        try (JsonParser parser = new VPackFactory().createParser(POSITIVE_BIG_INTEGER)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getBigIntegerValue());
            assertNull(parser.nextToken());
        }
    }

    void hexInsideArrayRetainsExactLiteralIntegerValues() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(INTEGER_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(255, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-16, parser.getIntValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void hexNegativeBigIntegerRangeRetainsExactValueWithoutFloatingPoint() throws Exception {
        BigInteger expected = new BigInteger("-1ffffffffffffffff", 16);
        try (JsonParser parser = new VPackFactory().createParser(NEGATIVE_BIG_INTEGER)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getBigIntegerValue());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_hex16DigitsLongMaxRetainsExactLongValue() throws Exception {
        try {
            hex16DigitsLongMaxRetainsExactLongValue();
        } finally {
        }
    }


    void __invoke_hex16DigitsOverflowRetainsExactBigIntegerValue() throws Exception {
        try {
            hex16DigitsOverflowRetainsExactBigIntegerValue();
        } finally {
        }
    }


    void __invoke_hexBigIntegerRangeRetainsExactValueWithoutFloatingPoint() throws Exception {
        try {
            hexBigIntegerRangeRetainsExactValueWithoutFloatingPoint();
        } finally {
        }
    }


    void __invoke_hexInsideArrayRetainsExactLiteralIntegerValues() throws Exception {
        try {
            hexInsideArrayRetainsExactLiteralIntegerValues();
        } finally {
        }
    }


    void __invoke_hexNegativeBigIntegerRangeRetainsExactValueWithoutFloatingPoint() throws Exception {
        try {
            hexNegativeBigIntegerRangeRetainsExactValueWithoutFloatingPoint();
        } finally {
        }
    }

}
