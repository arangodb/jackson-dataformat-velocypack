package tools.jackson.core.unittest.read;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0078Fixture {
private final VPackFactory factory = new VPackFactory();

    void numberCoercionAssertionsUseLiteralVpackFamilies() throws Exception {
        // NumberCoercionTest#toIntCoercion(): int, BigInteger, double and
        // BigDecimal sources all retain their exact integral conversion.
        assertInt(new byte[] { 0x31 }, 1, JsonToken.VALUE_NUMBER_INT);
        assertInt(new byte[] { (byte) 0xC8, 0x01, 0, 0, 0, 0, 0x10 }, 10,
                JsonToken.VALUE_NUMBER_INT);
        assertInt(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0, 0x40 }, 2,
                JsonToken.VALUE_NUMBER_FLOAT);
        assertInt(new byte[] {
                (byte) 0xC8, 0x02, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, 0x01, 0x00
        }, 10, JsonToken.VALUE_NUMBER_FLOAT);

        // NumberCoercionTest#toLongCoercion().
        assertLong(new byte[] { 0x31 }, 1L, JsonToken.VALUE_NUMBER_INT);
        assertLong(new byte[] { 0x24, 0x35, 0x1C, (byte) 0xDC,
                (byte) 0xDF, 0x02, 0, 0, 0 }, 12_345_678_901L,
                JsonToken.VALUE_NUMBER_INT);
        assertLong(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0, 0x40 }, 2L,
                JsonToken.VALUE_NUMBER_FLOAT);
        assertLong(new byte[] {
                (byte) 0xC8, 0x02, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, 0x01, 0x00
        }, 10L, JsonToken.VALUE_NUMBER_FLOAT);

        // NumberCoercionTest#toBigIntegerCoercion().
        assertBigInteger(new byte[] { 0x31 }, BigInteger.ONE, JsonToken.VALUE_NUMBER_INT);
        assertBigInteger(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0, 0x40 },
                BigInteger.valueOf(2), JsonToken.VALUE_NUMBER_FLOAT);
        assertBigInteger(new byte[] { 0x27, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, 0x7F }, BigInteger.valueOf(Long.MAX_VALUE),
                JsonToken.VALUE_NUMBER_INT);
        assertBigInteger(new byte[] {
                (byte) 0xC8, 0x02, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, 0x20, 0x00
        }, BigInteger.valueOf(200), JsonToken.VALUE_NUMBER_FLOAT);

        // NumberCoercionTest#toDoubleCoercion().
        assertDouble(new byte[] {
                (byte) 0xC8, 0x02, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, 0x10, 0x05
        }, 100.5d, JsonToken.VALUE_NUMBER_FLOAT);
        assertDouble(new byte[] { 0x31 }, 1.0d, JsonToken.VALUE_NUMBER_INT);

        // NumberCoercionTest#toBigDecimalCoercion(), including a BCD value
        // outside the Java long range.
        assertBigDecimal(new byte[] { 0x31 }, BigDecimal.ONE, JsonToken.VALUE_NUMBER_INT);
        assertBigDecimal(new byte[] { 0x27, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, 0x7F }, BigDecimal.valueOf(Long.MAX_VALUE),
                JsonToken.VALUE_NUMBER_INT);
        assertBigDecimal(new byte[] {
                (byte) 0xC8, 0x0A, 0, 0, 0, 0,
                (byte) 0x92, 0x23, 0x37, 0x20, 0x36,
                (byte) 0x85, 0x47, 0x75, (byte) 0x80, 0x70
        }, new BigDecimal("92233720368547758070"), JsonToken.VALUE_NUMBER_INT);

        // NumberCoercionTest#toIntFailing(): both signed sides and each
        // exact source family reject conversion outside int.
        assertIntOverflow(new byte[] { 0x27, 0, 0, 0, (byte) 0x80,
                0, 0, 0, 0 }, JsonToken.VALUE_NUMBER_INT);
        assertIntOverflow(new byte[] { 0x27, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF }, JsonToken.VALUE_NUMBER_INT);
        assertIntOverflow(new byte[] {
                (byte) 0xC8, 0x05, 0, 0, 0, 0, 0x21, 0x47, 0x48, 0x36, 0x48
        }, JsonToken.VALUE_NUMBER_INT);
        assertIntOverflow(new byte[] {
                (byte) 0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xE0, 0x41
        }, JsonToken.VALUE_NUMBER_FLOAT);

        // NumberCoercionTest#toLongFailing(): exact BCD values on both sides
        // of long remain BigInteger and fail checked narrowing.
        assertLongOverflow(new byte[] {
                (byte) 0xC8, 0x0A, 0, 0, 0, 0,
                0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x17
        });
        assertLongOverflow(new byte[] {
                (byte) 0xD0, 0x0A, 0, 0, 0, 0,
                0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x18
        });
    }
private void assertInt(byte[] bytes, int expected, JsonToken token) throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertEquals(expected, parser.getIntValue());
        }
    }
private void assertLong(byte[] bytes, long expected, JsonToken token) throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertEquals(expected, parser.getLongValue());
        }
    }
private void assertBigInteger(byte[] bytes, BigInteger expected, JsonToken token)
            throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertEquals(expected, parser.getBigIntegerValue());
        }
    }
private void assertDouble(byte[] bytes, double expected, JsonToken token) throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertEquals(expected, parser.getDoubleValue());
        }
    }
private void assertBigDecimal(byte[] bytes, BigDecimal expected, JsonToken token)
            throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertEquals(expected, parser.getDecimalValue());
        }
    }
private void assertIntOverflow(byte[] bytes, JsonToken token) throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertThrows(InputCoercionException.class, parser::getIntValue);
        }
    }
private void assertLongOverflow(byte[] bytes) throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertThrows(InputCoercionException.class, parser::getLongValue);
        }
    }

    void __invoke_numberCoercionAssertionsUseLiteralVpackFamilies() throws Exception {
        try {
            numberCoercionAssertionsUseLiteralVpackFamilies();
        } finally {
        }
    }

}
