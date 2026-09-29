package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0046Fixture {
private static final byte[] SUPPLEMENTARY_FIELD_NAME = {
            0x14, 0x0E,
            0x44, (byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8D,
            0x45, 'v', 'a', 'l', 'u', 'e',
            0x01
    };

    void supplementaryFieldNameIsDecodedFromLiteralUtf8Object() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(SUPPLEMENTARY_FIELD_NAME)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("\uD83D\uDC4D", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_supplementaryFieldNameIsDecodedFromLiteralUtf8Object() throws Exception {
        try {
            supplementaryFieldNameIsDecodedFromLiteralUtf8Object();
        } finally {
        }
    }

}
