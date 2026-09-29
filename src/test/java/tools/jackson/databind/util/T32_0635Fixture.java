package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0635Fixture {
private final VPackFactory factory = new VPackFactory();

    void repeatedBinaryAccessAcrossValuesReturnsEachPayload() throws Exception {
        // Independent literal VPack strings containing Base64 for [1,2,3] and [4,5,6,7].
        byte[] input = { 0x44, 'A', 'Q', 'I', 'D',
                0x48, 'B', 'A', 'U', 'G', 'B', 'w', '=', '=' };
        try (JsonParser parser = factory.createParser(input)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertArrayEquals(new byte[] { 1, 2, 3 }, parser.getBinaryValue());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertArrayEquals(new byte[] { 4, 5, 6, 7 }, parser.getBinaryValue());
            assertNull(parser.nextToken());
        }
    }

    void decimalAccessorPreservesExactIntegerAndFloatingValues() throws Exception {
        // Independent literal uints and doubles exercise the same value conversion.
        assertDecimal(new byte[] { 0x28, 0x2A }, new BigDecimal("42"));
        assertDecimal(new byte[] { 0x2C, 0x14, 0x1A, (byte) 0x99, (byte) 0xBE, 0x1C },
                new BigDecimal("123456789012"));
        // Positive BCD for 9223372036854775808 (one above Long.MAX_VALUE).
        assertDecimal(new byte[] { (byte) 0xC8, 0x0A, 0, 0, 0, 0,
                0x09, 0x22, 0x33, 0x72, 0x03, 0x68, 0x54, 0x77, 0x58, 0x08 },
                new BigDecimal("9223372036854775808"));
        assertDecimal(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xF8, 0x3F },
                BigDecimal.valueOf(1.5));
        assertDecimal(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0, 0x40 },
                BigDecimal.valueOf(2.0));
        assertDecimal(new byte[] { (byte) 0xC8, 0x03, (byte) 0xFE, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, 0x01, 0x23, 0x40 }, new BigDecimal("123.40"));
    }

    void numberTypeReflectsWireValueAndIsNullBeforeFirstToken() throws Exception {
        try (JsonParser parser = factory.createParser(new byte[] { 0x31 })) {
            assertNull(parser.getNumberType());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
        }
        try (JsonParser parser = factory.createParser(new byte[] {
                (byte) 0xC8, 0x02, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, 0x12, 0x34 })) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(JsonParser.NumberType.BIG_DECIMAL, parser.getNumberType());
        }
        try (JsonParser parser = factory.createParser(new byte[] {
                0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xF8, 0x3F })) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
        }
    }

    void emptyInputHasNoFirstTokenAndLiteralArrayHasOne() throws Exception {
        try (JsonParser parser = factory.createParser(new byte[0])) {
            assertNull(parser.nextToken());
        }
        // Independent literal [1].
        try (JsonParser parser = factory.createParser(new byte[] { 0x02, 0x03, 0x31 })) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
        }
    }

    void textualNumbersWriteAsExactVpackNumbers() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeNumber("123");
            generator.writeNumber("-123");
            generator.writeNumber("123.45");
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(BigInteger.valueOf(123), parser.getBigIntegerValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(BigInteger.valueOf(-123), parser.getBigIntegerValue());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(new BigDecimal("123.45"), parser.getDecimalValue());
            assertNull(parser.nextToken());
        }
    }

    void serializedTextNumberRetainsItsNumericValueAfterVpackReadback() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeNumber("42");
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(42, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }
private void assertDecimal(byte[] literal, BigDecimal expected) throws Exception {
        try (JsonParser parser = factory.createParser(literal)) {
            JsonToken token = parser.nextToken();
            assertEquals(expected.scale() == 0 ? JsonToken.VALUE_NUMBER_INT
                    : JsonToken.VALUE_NUMBER_FLOAT, token);
            assertEquals(expected, parser.getDecimalValue());
        }
    }

    void __invoke_repeatedBinaryAccessAcrossValuesReturnsEachPayload() throws Exception {
        try {
            repeatedBinaryAccessAcrossValuesReturnsEachPayload();
        } finally {
        }
    }


    void __invoke_decimalAccessorPreservesExactIntegerAndFloatingValues() throws Exception {
        try {
            decimalAccessorPreservesExactIntegerAndFloatingValues();
        } finally {
        }
    }


    void __invoke_numberTypeReflectsWireValueAndIsNullBeforeFirstToken() throws Exception {
        try {
            numberTypeReflectsWireValueAndIsNullBeforeFirstToken();
        } finally {
        }
    }


    void __invoke_emptyInputHasNoFirstTokenAndLiteralArrayHasOne() throws Exception {
        try {
            emptyInputHasNoFirstTokenAndLiteralArrayHasOne();
        } finally {
        }
    }


    void __invoke_textualNumbersWriteAsExactVpackNumbers() throws Exception {
        try {
            textualNumbersWriteAsExactVpackNumbers();
        } finally {
        }
    }


    void __invoke_serializedTextNumberRetainsItsNumericValueAfterVpackReadback() throws Exception {
        try {
            serializedTextNumberRetainsItsNumericValueAfterVpackReadback();
        } finally {
        }
    }

}
