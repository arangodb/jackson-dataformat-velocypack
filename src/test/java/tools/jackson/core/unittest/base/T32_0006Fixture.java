package tools.jackson.core.unittest.base;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0006Fixture {

    void numberConversionEdgeCases() throws Exception {
        // Independent literal obsolete-unsorted indexed object. Pair starts are
        // 3, 9, 18 and 23; the index preserves body/call order and its four
        // one-byte fields are relative to the marker address.
        byte[] input = {
                0x0F, 0x23, 0x04,
                0x44, 'z', 'e', 'r', 'o', 0x30,
                0x47, 'n', 'e', 'g', 'Z', 'e', 'r', 'o', 0x30,
                0x43, 'o', 'n', 'e', 0x31,
                0x46, 'n', 'e', 'g', 'O', 'n', 'e', 0x3F,
                0x03, 0x09, 0x12, 0x17
        };
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("zero", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(0, parser.getIntValue());
            assertEquals(0L, parser.getLongValue());
            assertEquals(0.0, parser.getDoubleValue());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("negZero", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(0, parser.getIntValue());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("one", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("negOne", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-1, parser.getIntValue());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertTrue(parser.nextToken() == null);
        }
    }

    void __invoke_numberConversionEdgeCases() throws Exception {
        try {
            numberConversionEdgeCases();
        } finally {
        }
    }

}
