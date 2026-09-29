package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0081Fixture {
private static final VPackFactory FACTORY = new VPackFactory();

    void intExponentCoercionUsesExactLiteralBcd() throws Exception {
        // Positive BCD: 1 * 10^5, the VPack equivalent of JSON 1e5.
        byte[] input = { (byte) 0xC8, 0x01, 0x05, 0x00, 0x00, 0x00, 0x01 };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(100000, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }

    void nineteenDigitIntegersRemainExactAsBigInteger() throws Exception {
        assertBigInteger(new byte[] {
                (byte) 0xC8, 0x0A, 0x00, 0x00, 0x00, 0x00,
                0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x08
        }, "9223372036854775808");
        assertBigInteger(new byte[] {
                (byte) 0xC8, 0x0A, 0x00, 0x00, 0x00, 0x00,
                0x09, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99,
                (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99
        }, "9999999999999999999");
    }

    void largeExponentIntegerCoercionAvoidsDouble() throws Exception {
        // Positive BCD: 2 * 10^308, beyond Double.MAX_VALUE.
        byte[] input = { (byte) 0xC8, 0x01, 0x34, 0x01, 0x00, 0x00, 0x02 };
        BigInteger expected = BigInteger.TWO.multiply(BigInteger.TEN.pow(308));
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(expected, parser.getBigIntegerValue());
            assertNull(parser.nextToken());
        }
    }

    void invalidScalarAccessorsRaiseInputCoercion() throws Exception {
        // ["abc"]: the string is not a boolean or numeric scalar.
        byte[] stringArray = { 0x02, 0x06, 0x43, 0x61, 0x62, 0x63 };
        for (boolean stream : new boolean[] { false, true }) {
            try (JsonParser parser = stream
                    ? FACTORY.createParser(new ByteArrayInputStream(stringArray))
                    : FACTORY.createParser(stringArray)) {
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertThrows(InputCoercionException.class, parser::getBooleanValue);
                assertThrows(InputCoercionException.class, parser::getIntValue);
                assertEquals(JsonToken.END_ARRAY, parser.nextToken());
                assertNull(parser.nextToken());
            }
        }

        // [false]: the boolean is not a numeric scalar.
        byte[] falseArray = { 0x02, 0x03, 0x19 };
        for (boolean stream : new boolean[] { false, true }) {
            try (JsonParser parser = stream
                    ? FACTORY.createParser(new ByteArrayInputStream(falseArray))
                    : FACTORY.createParser(falseArray)) {
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
                assertThrows(InputCoercionException.class, parser::getLongValue);
                assertEquals(JsonToken.END_ARRAY, parser.nextToken());
                assertNull(parser.nextToken());
            }
        }
    }
private static void assertBigInteger(byte[] input, String expectedText) throws Exception {
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
            assertEquals(expectedText, parser.getString());
            assertEquals(new BigInteger(expectedText), parser.getBigIntegerValue());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_intExponentCoercionUsesExactLiteralBcd() throws Exception {
        try {
            intExponentCoercionUsesExactLiteralBcd();
        } finally {
        }
    }


    void __invoke_nineteenDigitIntegersRemainExactAsBigInteger() throws Exception {
        try {
            nineteenDigitIntegersRemainExactAsBigInteger();
        } finally {
        }
    }


    void __invoke_largeExponentIntegerCoercionAvoidsDouble() throws Exception {
        try {
            largeExponentIntegerCoercionAvoidsDouble();
        } finally {
        }
    }


    void __invoke_invalidScalarAccessorsRaiseInputCoercion() throws Exception {
        try {
            invalidScalarAccessorsRaiseInputCoercion();
        } finally {
        }
    }

}
