package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VPackContextNavigationTest {
    @Test
    void nextNameSkipChildrenAndContextFollowNestedObjectAndArrayOrder() throws Exception {
        // Independent literal: {"outer":[[1, 2]]}.
        byte[] input = { 0x14, 0x0f, 0x45, 'o', 'u', 't', 'e', 'r',
                0x02, 0x06, 0x02, 0x04, 0x31, 0x32, 0x01 };
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals("outer", parser.nextName());
            assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
            TokenStreamContext object = parser.streamReadContext();
            assertEquals("outer", parser.currentName());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(0, parser.streamReadContext().getCurrentIndex());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            parser.skipChildren();
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(object.getParent(), parser.streamReadContext());
            assertNull(parser.nextToken());
        }
    }
}
