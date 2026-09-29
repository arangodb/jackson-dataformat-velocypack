package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0051F1 {
private static final byte[] BOOLEAN_OBJECT = {
            0x0B, 0x0B, 0x02,
            0x41, 0x61, 0x1A,
            0x41, 0x62, 0x19,
            0x03, 0x06
    };
private static final byte[] NONFINITE_ARRAY = {
            0x13, 0x1E,
            0x1B, 0x42, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xF8, 0x7F,
            0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xF0, 0x7F,
            0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xF0, (byte) 0xFF,
            0x03
    };

    void literalNonfiniteDoublesRetainNativeValuesAndRejectDecimalCoercion() throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(NONFINITE_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());

            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertTrue(parser.isNaN());
            assertEquals(0x7ff8_0000_0000_0042L, VPackTestAccess.currentDoubleBits(parser));
            assertThrows(InputCoercionException.class, parser::getDecimalValue);

            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(Double.POSITIVE_INFINITY, parser.getDoubleValue());
            assertThrows(InputCoercionException.class, parser::getDecimalValue);

            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(Double.NEGATIVE_INFINITY, parser.getDoubleValue());
            assertThrows(InputCoercionException.class, parser::getDecimalValue);

            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_literalNonfiniteDoublesRetainNativeValuesAndRejectDecimalCoercion() throws Exception {
        try {
            literalNonfiniteDoublesRetainNativeValuesAndRejectDecimalCoercion();
        } finally {
        }
    }

}
