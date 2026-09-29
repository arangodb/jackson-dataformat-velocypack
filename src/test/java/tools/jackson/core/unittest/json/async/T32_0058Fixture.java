package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0058Fixture {
private static final byte[] MIXED_ROOTS = {
            0x14, 0x06, 0x41, 'a', 0x34, 0x01,
            0x13, 0x09, 0x28, 0x0C, 0x21, 0x25, (byte) 0xFC, 0x19, 0x03,
            0x29, 0x44, 0x30,
            0x1A
    };

    void literalVpackMixedRootSequencePreservesPortableTokensAndValues() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(MIXED_ROOTS)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(4, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());

            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(12, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-987, parser.getIntValue());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(12_356, parser.getIntValue());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_literalVpackMixedRootSequencePreservesPortableTokensAndValues() throws Exception {
        try {
            literalVpackMixedRootSequencePreservesPortableTokensAndValues();
        } finally {
        }
    }

}
