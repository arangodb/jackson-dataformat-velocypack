package tools.jackson.core.unittest.constraints;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0012F0 {
private static final String LARGE_FRACTION = repeatedDigits(1002);
private static final String LARGE_DECIMAL_TEXT = "0." + LARGE_FRACTION;
private static final BigDecimal LARGE_DECIMAL = new BigDecimal(LARGE_DECIMAL_TEXT);
private static final BigInteger LARGE_INTEGER = new BigInteger(repeatedDigits(2500));

    void largeBigDecimalsBytesFailWithDefaultNumberLength() {
        VPackFactory factory = new VPackFactory();
        byte[] fixture = bcdFixture(LARGE_DECIMAL);

        assertThrows(StreamConstraintsException.class, () -> {
            try (JsonParser parser = factory.createParser(fixture)) {
                parser.nextToken();
            }
        });
        assertThrows(StreamConstraintsException.class, () -> {
            try (JsonParser parser = factory.createParser(oneByteAtATime(fixture))) {
                parser.nextToken();
            }
        });
    }

    void largeBigDecimalsBytesReadExactlyWithUnlimitedNumberLength() throws Exception {
        VPackFactory factory = unlimitedReadFactory();
        byte[] fixture = bcdFixture(LARGE_DECIMAL);

        assertLargeDecimal(factory.createParser(fixture));
        assertLargeDecimal(factory.createParser(oneByteAtATime(fixture)));
    }

    void largeBigDecimalsDataInputFailsWithDefaultNumberLength() {
        VPackFactory factory = new VPackFactory();
        byte[] fixture = bcdFixture(LARGE_DECIMAL);

        assertThrows(StreamConstraintsException.class, () -> {
            try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                    (java.io.DataInput) new DataInputStream(new ByteArrayInputStream(fixture)))) {
                parser.nextToken();
            }
        });
    }

    void largeBigDecimalsDataInputReadsExactlyWithUnlimitedNumberLength() throws Exception {
        VPackFactory factory = unlimitedReadFactory();
        byte[] fixture = bcdFixture(LARGE_DECIMAL);

        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                (java.io.DataInput) new DataInputStream(new ByteArrayInputStream(fixture)))) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(LARGE_DECIMAL, parser.getDecimalValue());
            assertNull(parser.nextToken());
        }
    }
private static VPackFactory unlimitedReadFactory() {
        return VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxNumberLength(Integer.MAX_VALUE).build())
                .build();
    }
private static VPackFactory unlimitedWriteFactory() {
        return VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder()
                        .maxNumberDigits(Integer.MAX_VALUE).build())
                .build();
    }
private static byte[] writeLargeNumberToBytes(Number value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (JsonGenerator generator = unlimitedWriteFactory().createGenerator(bytes)) {
            writeLargeNumberDocument(generator, value);
        }
        return bytes.toByteArray();
    }
private static void writeLargeNumberDocument(JsonGenerator generator, Number value)
            throws Exception {
        generator.writeStartObject();
        generator.writeName("field");
        if (value instanceof BigInteger integer) {
            generator.writeNumber(integer);
        } else {
            generator.writeNumber((BigDecimal) value);
        }
        generator.writeEndObject();
    }
private static void assertLargeInteger(byte[] encoded) throws Exception {
        try (JsonParser parser = unlimitedReadFactory().createParser(encoded)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("field", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(LARGE_INTEGER, parser.getBigIntegerValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertLargeDecimal(byte[] encoded) throws Exception {
        try (JsonParser parser = unlimitedReadFactory().createParser(encoded)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("field", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(LARGE_DECIMAL, parser.getDecimalValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertLargeDecimal(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(LARGE_DECIMAL, parser.getDecimalValue());
            assertNull(parser.nextToken());
        }
    }
private static byte[] bcdFixture(BigDecimal value) {
        String digits = value.unscaledValue().abs().toString();
        int mantissaLength = (digits.length() + 1) / 2;
        int width = mantissaLength <= 0xFF ? 1 : 2;
        byte[] result = new byte[1 + width + 4 + mantissaLength];
        result[0] = (byte) (0xC8 + width - 1);
        for (int i = 0; i < width; ++i) {
            result[1 + i] = (byte) (mantissaLength >>> (8 * i));
        }
        int exponent = -value.scale();
        int exponentOffset = 1 + width;
        result[exponentOffset] = (byte) exponent;
        result[exponentOffset + 1] = (byte) (exponent >>> 8);
        result[exponentOffset + 2] = (byte) (exponent >>> 16);
        result[exponentOffset + 3] = (byte) (exponent >>> 24);
        int digit = 0;
        int mantissaOffset = exponentOffset + 4;
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
private static String repeatedDigits(int length) {
        char[] digits = new char[length];
        for (int i = 0; i < length; ++i) {
            digits[i] = (char) ('0' + (i % 10));
        }
        digits[0] = '1';
        return new String(digits);
    }

    void __invoke_largeBigDecimalsBytesFailWithDefaultNumberLength() throws Exception {
        try {
            largeBigDecimalsBytesFailWithDefaultNumberLength();
        } finally {
        }
    }


    void __invoke_largeBigDecimalsBytesReadExactlyWithUnlimitedNumberLength() throws Exception {
        try {
            largeBigDecimalsBytesReadExactlyWithUnlimitedNumberLength();
        } finally {
        }
    }


    void __invoke_largeBigDecimalsDataInputFailsWithDefaultNumberLength() throws Exception {
        try {
            largeBigDecimalsDataInputFailsWithDefaultNumberLength();
        } finally {
        }
    }


    void __invoke_largeBigDecimalsDataInputReadsExactlyWithUnlimitedNumberLength() throws Exception {
        try {
            largeBigDecimalsDataInputReadsExactlyWithUnlimitedNumberLength();
        } finally {
        }
    }

}
