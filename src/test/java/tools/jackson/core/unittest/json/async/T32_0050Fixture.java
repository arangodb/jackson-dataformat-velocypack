package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0050Fixture {

    void literalVpackArraysPreserveAcceptedMissingValueResults() throws Exception {
        // ["a",, "b"] with ALLOW_MISSING_VALUES.
        assertArray(new byte[] {
                0x13, 0x08, 0x41, 0x61, 0x18, 0x41, 0x62, 0x03
        }, "a", null, "b");

        // [,"a", "b"] with ALLOW_MISSING_VALUES.
        assertArray(new byte[] {
                0x13, 0x08, 0x18, 0x41, 0x61, 0x41, 0x62, 0x03
        }, null, "a", "b");

        // ["a", "b",] with ALLOW_TRAILING_COMMA.
        assertArray(new byte[] {
                0x13, 0x07, 0x41, 0x61, 0x41, 0x62, 0x02
        }, "a", "b");

        // ["a", "b",] with ALLOW_MISSING_VALUES.
        assertArray(new byte[] {
                0x13, 0x08, 0x41, 0x61, 0x41, 0x62, 0x18, 0x03
        }, "a", "b", null);

        // ["a", "b",,] with both features.
        assertArray(new byte[] {
                0x13, 0x09, 0x41, 0x61, 0x41, 0x62, 0x18, 0x18, 0x04
        }, "a", "b", null, null);

        // ["a", "b",,,] with ALLOW_MISSING_VALUES.
        assertArray(new byte[] {
                0x13, 0x0A, 0x41, 0x61, 0x41, 0x62, 0x18, 0x18, 0x18, 0x05
        }, "a", "b", null, null, null);
    }
private static void assertArray(byte[] input, String... expectedValues) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (String expected : expectedValues) {
                if (expected == null) {
                    assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                } else {
                    assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                    assertEquals(expected, parser.getString());
                }
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_literalVpackArraysPreserveAcceptedMissingValueResults() throws Exception {
        try {
            literalVpackArraysPreserveAcceptedMissingValueResults();
        } finally {
        }
    }

}
