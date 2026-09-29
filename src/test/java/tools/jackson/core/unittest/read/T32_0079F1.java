package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0079F1 {
private static final VPackFactory FACTORY = new VPackFactory();
private static final byte[] ISSUE_1397 = {
            0x14, 0x54,
            0x47, 0x72, 0x65, 0x73, 0x75, 0x6c, 0x74, 0x73,
            0x13, 0x49,
            0x14, 0x46,
            0x46, 0x72, 0x61, 0x64, 0x69, 0x75, 0x73,
            (byte) 0xc8, 0x0a, 0x00, 0x00, 0x00, 0x00,
            0x12, 0x34, 0x56, 0x78, (byte) 0x90,
            0x12, 0x34, 0x56, 0x78, (byte) 0x90,
            0x44, 0x74, 0x79, 0x70, 0x65,
            0x46, 0x63, 0x65, 0x6e, 0x74, 0x65, 0x72,
            0x46, 0x63, 0x65, 0x6e, 0x74, 0x65, 0x72,
            0x14, 0x19,
            0x41, 0x78,
            0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x26, (byte) 0xc0,
            0x41, 0x79,
            0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xc0,
            0x02,
            0x03,
            0x01,
            0x01
    };
private static final byte[] ISSUE_4917 = {
            0x14, 0x23,
            0x4d, 0x64, 0x65, 0x63, 0x69, 0x6d, 0x61, 0x6c,
            0x48, 0x6f, 0x6c, 0x64, 0x65, 0x72,
            (byte) 0xc8, 0x03, (byte) 0xfe, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x00,
            0x46, 0x6e, 0x75, 0x6d, 0x62, 0x65, 0x72,
            0x28, 0x32,
            0x02
    };
private static final BigInteger ISSUE_1397_RADIUS =
            new BigInteger("12345678901234567890");

    void bigDecimal4917IntegerAccessorsRemainStableAfterScaledDecimal() throws Exception {
        for (boolean stream : new boolean[] { false, true }) {
            for (IntegerAccessor accessor : IntegerAccessor.values()) {
                assert4917(accessor, stream);
            }
        }
    }

    void bigDecimal4917FloatAccessorsRetainExactScaleAndValue() throws Exception {
        for (boolean stream : new boolean[] { false, true }) {
            for (FloatAccessor accessor : FloatAccessor.values()) {
                assert4917(accessor, stream);
            }
        }
    }
private static void assert4917(IntegerAccessor accessor, boolean stream) throws Exception {
        try (JsonParser parser = parser(stream)) {
            assertDecimalHolder(parser);
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("number", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
            assertEquals(Integer.valueOf(50), parser.getNumberValueDeferred());
            switch (accessor) {
            case BIG_INTEGER -> assertEquals(BigInteger.valueOf(50), parser.getBigIntegerValue());
            case INT -> assertEquals(50, parser.getIntValue());
            case LONG -> assertEquals(50L, parser.getLongValue());
            }
            assertEquals(50, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assert4917(FloatAccessor accessor, boolean stream) throws Exception {
        try (JsonParser parser = parser(stream)) {
            assertDecimalHolder(parser);
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("number", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
            assertEquals(Integer.valueOf(50), parser.getNumberValueDeferred());
            switch (accessor) {
            case DOUBLE -> assertEquals(50.0d, parser.getDoubleValue());
            case FLOAT -> assertEquals(50.0f, parser.getFloatValue());
            case BIG_DECIMAL -> assertEquals(BigDecimal.valueOf(50), parser.getDecimalValue());
            }
            assertEquals(50, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertDecimalHolder(JsonParser parser) throws Exception {
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals("decimalHolder", parser.currentName());
        assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        assertEquals(JsonParser.NumberType.BIG_DECIMAL, parser.getNumberType());
        assertEquals(new BigDecimal("100.00"), parser.getDecimalValue());
        assertEquals(2, parser.getDecimalValue().scale());
        assertEquals("100.00", parser.getString());
    }
private static JsonParser parser(boolean stream) throws Exception {
        return stream
                ? FACTORY.createParser(new ByteArrayInputStream(ISSUE_4917))
                : FACTORY.createParser(ISSUE_4917);
    }
private enum IntegerAccessor { BIG_INTEGER, INT, LONG }
private enum FloatAccessor { DOUBLE, FLOAT, BIG_DECIMAL }

    void __invoke_bigDecimal4917IntegerAccessorsRemainStableAfterScaledDecimal() throws Exception {
        try {
            bigDecimal4917IntegerAccessorsRemainStableAfterScaledDecimal();
        } finally {
        }
    }


    void __invoke_bigDecimal4917FloatAccessorsRetainExactScaleAndValue() throws Exception {
        try {
            bigDecimal4917FloatAccessorsRetainExactScaleAndValue();
        } finally {
        }
    }

}
