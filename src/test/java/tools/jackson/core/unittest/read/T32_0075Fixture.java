package tools.jackson.core.unittest.read;

import java.math.BigDecimal;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0075Fixture {
private static final byte[] LARGE_DECIMAL = {
            (byte) 0xC8, 0x08,
            0x35, 0x01, 0x00, 0x00,
            0x79, 0x76, (byte) 0x93, 0x13,
            0x48, 0x62, 0x31, 0x57
    };

    void largeDecimalRemainsExactWhenDoubleAccessorOverflows() throws Exception {
        final BigDecimal expected = new BigDecimal("7976931348623157e309");
        try (JsonParser parser = new VPackFactory().createParser(LARGE_DECIMAL)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(expected, parser.getDecimalValue());
            assertFalse(parser.isNaN());
            assertEquals(Double.POSITIVE_INFINITY, parser.getValueAsDouble());
            assertFalse(parser.isNaN());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_largeDecimalRemainsExactWhenDoubleAccessorOverflows() throws Exception {
        try {
            largeDecimalRemainsExactWhenDoubleAccessorOverflows();
        } finally {
        }
    }

}
