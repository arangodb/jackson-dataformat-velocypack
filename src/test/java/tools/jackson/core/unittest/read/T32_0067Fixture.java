package tools.jackson.core.unittest.read;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0067Fixture {

    void literalSupplementaryFieldNameRemainsExact() throws Exception {
        // Compact object: {"a\ud83d\udc4d":"value"}, literal UTF-8 key bytes.
        byte[] literal = {
                0x14, 0x0F, 0x45, 'a', (byte) 0xF0, (byte) 0x9F,
                (byte) 0x91, (byte) 0x8D, 0x45, 'v', 'a', 'l', 'u', 'e', 0x01
        };
        try (JsonParser parser = new VPackFactory().createParser(literal)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a\ud83d\udc4d", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_literalSupplementaryFieldNameRemainsExact() throws Exception {
        try {
            literalSupplementaryFieldNameRemainsExact();
        } finally {
        }
    }

}
