package tools.jackson.core.unittest.read;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.util.JsonParserSequence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0085F1 {
private static final byte[] BIG_DECIMAL_1E_PLUS_999 = {
            (byte) 0xC8, 0x01, (byte) 0xE7, 0x03, 0x00, 0x00, 0x01
    };

    void parserSequenceInitializationFlagControlsExistingLiteralVpackTokens()
            throws Exception {
        try (JsonParser p1 = new VPackFactory().createParser(new byte[] { 0x31, 0x32 });
                JsonParser p2 = new VPackFactory().createParser(new byte[] { 0x33, 0x1A })) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p1.nextToken());
            assertEquals(1, p1.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p2.nextToken());
            assertEquals(3, p2.getIntValue());

            try (JsonParserSequence sequence = JsonParserSequence.createFlattened(false, p1, p2)) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
                assertEquals(2, sequence.getIntValue());
                assertEquals(JsonToken.VALUE_TRUE, sequence.nextToken());
                assertNull(sequence.nextToken());
            }
        }

        try (JsonParser p1 = new VPackFactory().createParser(new byte[] { 0x31, 0x32 });
                JsonParser p2 = new VPackFactory().createParser(new byte[] { 0x33, 0x1A })) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p1.nextToken());
            assertEquals(1, p1.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p2.nextToken());
            assertEquals(3, p2.getIntValue());

            try (JsonParserSequence sequence = JsonParserSequence.createFlattened(true, p1, p2)) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
                assertEquals(1, sequence.getIntValue());
                assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
                assertEquals(2, sequence.getIntValue());
                assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
                assertEquals(3, sequence.getIntValue());
                assertEquals(JsonToken.VALUE_TRUE, sequence.nextToken());
                assertNull(sequence.nextToken());
            }
        }
    }

    void __invoke_parserSequenceInitializationFlagControlsExistingLiteralVpackTokens() throws Exception {
        try {
            parserSequenceInitializationFlagControlsExistingLiteralVpackTokens();
        } finally {
        }
    }

}
