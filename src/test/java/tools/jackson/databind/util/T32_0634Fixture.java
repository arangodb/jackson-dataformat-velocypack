package tools.jackson.databind.util;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0634Fixture {
private final VPackFactory factory = new VPackFactory();

    void testConvertBigDecimalToInt() throws Exception {
        // BigDecimal 99, exponent zero, BCD mantissa 99.
        assertInt(new byte[] { (byte) 0xC8, 0x01, 0, 0, 0, 0, (byte) 0x99 }, 99,
                JsonToken.VALUE_NUMBER_INT);
    }

    void testConvertBigDecimalToIntOverflow() throws Exception {
        // BigDecimal 99999999999, exponent zero, leading zero nibble for odd digits.
        assertIntOverflow(new byte[] {
                (byte) 0xC8, 0x06, 0, 0, 0, 0,
                0x09, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99
        }, JsonToken.VALUE_NUMBER_INT);
    }

    void testConvertBigDecimalToLong() throws Exception {
        // BigDecimal 1234567890, exponent zero.
        assertLong(new byte[] {
                (byte) 0xC8, 0x05, 0, 0, 0, 0, 0x12, 0x34, 0x56, 0x78, (byte) 0x90
        }, 1_234_567_890L, JsonToken.VALUE_NUMBER_INT);
    }

    void testConvertBigDecimalToLongOverflow() throws Exception {
        // BigDecimal 99999999999999999999, exponent zero.
        assertLongOverflow(new byte[] {
                (byte) 0xC8, 0x0A, 0, 0, 0, 0,
                (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99,
                (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99
        }, JsonToken.VALUE_NUMBER_INT);
    }

    void testConvertBigIntegerToInt() throws Exception {
        // Unsigned one-byte VPack integer 77.
        assertInt(new byte[] { 0x28, 0x4D }, 77, JsonToken.VALUE_NUMBER_INT);
    }

    void testConvertDoubleToInt() throws Exception {
        // IEEE-754 little-endian double 42.0.
        assertInt(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0x45, 0x40 }, 42,
                JsonToken.VALUE_NUMBER_FLOAT);
    }

    void testConvertDoubleToIntOverflow() throws Exception {
        // IEEE-754 little-endian double 1e20.
        assertIntOverflow(new byte[] {
                0x1B, 0x40, (byte) 0x8C, (byte) 0xB5, 0x78, 0x1D, (byte) 0xAF, 0x15, 0x44
        }, JsonToken.VALUE_NUMBER_FLOAT);
    }

    void testConvertDoubleToLong() throws Exception {
        // IEEE-754 little-endian double 123456.0.
        assertLong(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0x24, (byte) 0xFE, 0x40 },
                123_456L, JsonToken.VALUE_NUMBER_FLOAT);
    }

    void testConvertDoubleToLongOverflow() throws Exception {
        // IEEE-754 little-endian double 1e30.
        assertLongOverflow(new byte[] {
                0x1B, (byte) 0xEA, (byte) 0x8C, (byte) 0xA0, 0x39, 0x59, 0x3E, 0x29, 0x46
        }, JsonToken.VALUE_NUMBER_FLOAT);
    }

    void testConvertFloatToInt() throws Exception {
        // VPack widens the source float 7.0f to the native double representation.
        assertInt(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0x1C, 0x40 }, 7,
                JsonToken.VALUE_NUMBER_FLOAT);
    }

    void testConvertFloatToLong() throws Exception {
        // VPack widens the source float 42.0f to the native double representation.
        assertLong(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0x45, 0x40 }, 42L,
                JsonToken.VALUE_NUMBER_FLOAT);
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
private void assertIntOverflow(byte[] bytes, JsonToken token) throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertThrows(InputCoercionException.class, parser::getIntValue);
        }
    }
private void assertLongOverflow(byte[] bytes, JsonToken token) throws Exception {
        try (JsonParser parser = factory.createParser(bytes)) {
            assertEquals(token, parser.nextToken());
            assertThrows(InputCoercionException.class, parser::getLongValue);
        }
    }

    void __invoke_testConvertBigDecimalToInt() throws Exception {
        try {
            testConvertBigDecimalToInt();
        } finally {
        }
    }


    void __invoke_testConvertBigDecimalToIntOverflow() throws Exception {
        try {
            testConvertBigDecimalToIntOverflow();
        } finally {
        }
    }


    void __invoke_testConvertBigDecimalToLong() throws Exception {
        try {
            testConvertBigDecimalToLong();
        } finally {
        }
    }


    void __invoke_testConvertBigDecimalToLongOverflow() throws Exception {
        try {
            testConvertBigDecimalToLongOverflow();
        } finally {
        }
    }


    void __invoke_testConvertBigIntegerToInt() throws Exception {
        try {
            testConvertBigIntegerToInt();
        } finally {
        }
    }


    void __invoke_testConvertDoubleToInt() throws Exception {
        try {
            testConvertDoubleToInt();
        } finally {
        }
    }


    void __invoke_testConvertDoubleToIntOverflow() throws Exception {
        try {
            testConvertDoubleToIntOverflow();
        } finally {
        }
    }


    void __invoke_testConvertDoubleToLong() throws Exception {
        try {
            testConvertDoubleToLong();
        } finally {
        }
    }


    void __invoke_testConvertDoubleToLongOverflow() throws Exception {
        try {
            testConvertDoubleToLongOverflow();
        } finally {
        }
    }


    void __invoke_testConvertFloatToInt() throws Exception {
        try {
            testConvertFloatToInt();
        } finally {
        }
    }


    void __invoke_testConvertFloatToLong() throws Exception {
        try {
            testConvertFloatToLong();
        } finally {
        }
    }

}
