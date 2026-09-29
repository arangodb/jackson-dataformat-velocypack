package tools.jackson.core.unittest.json.async;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0055F0 {

    void literalVpackNumbersExposeExactEagerDeferredValues() throws Exception {
        assertDeferred(new byte[] { 0x35 }, Integer.valueOf(5),
                JsonParser.NumberType.INT, JsonToken.VALUE_NUMBER_INT);
        assertDeferred(new byte[] { 0x24, 0, 0, 0, (byte) 0x80, 0 },
                Long.valueOf(2_147_483_648L), JsonParser.NumberType.LONG,
                JsonToken.VALUE_NUMBER_INT);
        assertDeferred(new byte[] {
                (byte) 0xC8, 0x03, 0, 0, 0, 0, 0x01, 0x23, 0x45
        }, BigInteger.valueOf(12_345L), JsonParser.NumberType.BIG_INTEGER,
                JsonToken.VALUE_NUMBER_INT);
        assertDeferred(new byte[] {
                (byte) 0xC8, 0x02, (byte) 0xFE, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, 0x12, 0x34
        }, new BigDecimal("12.34"), JsonParser.NumberType.BIG_DECIMAL,
                JsonToken.VALUE_NUMBER_FLOAT);
        assertDeferred(new byte[] {
                0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xD0, 0x3F
        }, Double.valueOf(0.25d), JsonParser.NumberType.DOUBLE,
                JsonToken.VALUE_NUMBER_FLOAT);
    }
private static void assertDeferred(byte[] literal, Object expected,
            JsonParser.NumberType numberType, JsonToken token) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(literal)) {
            assertEquals(token, parser.nextToken());
            Object actual = parser.getNumberValueDeferred();
            assertEquals(expected, actual);
            assertEquals(expected.getClass(), actual.getClass());
            assertEquals(numberType, parser.getNumberType());
        }
    }

    void __invoke_literalVpackNumbersExposeExactEagerDeferredValues() throws Exception {
        try {
            literalVpackNumbersExposeExactEagerDeferredValues();
        } finally {
        }
    }

}
