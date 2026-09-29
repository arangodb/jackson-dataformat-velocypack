package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0051F0 {
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

    void literalBooleanObjectRetainsObjectTokenSemantics() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(BOOLEAN_OBJECT)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void jsonOnlyMissingAndNonstandardSpellingsHaveNoVpackWireSurface() {
        VPackFactory factory = new VPackFactory();
        for (String json : new String[] {
                "{\"a\": true,, \"b\": false}",
                "{,\"a\": true, \"b\": false}",
                "{\"a\": true, \"b\": false,}",
                "{\"a\": true, \"b\": false,,}",
                "[ NaN]", "[ Infinity]", "[ -Infinity]",
                "[ -0.123f ]", "[ -0.123d ]"
        }) {
            assertThrows(UnsupportedOperationException.class,
                    () -> factory.createParser(json), json);
        }
    }

    void __invoke_literalBooleanObjectRetainsObjectTokenSemantics() throws Exception {
        try {
            literalBooleanObjectRetainsObjectTokenSemantics();
        } finally {
        }
    }


    void __invoke_jsonOnlyMissingAndNonstandardSpellingsHaveNoVpackWireSurface() throws Exception {
        try {
            jsonOnlyMissingAndNonstandardSpellingsHaveNoVpackWireSurface();
        } finally {
        }
    }

}
