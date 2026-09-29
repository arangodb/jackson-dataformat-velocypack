package tools.jackson.core.unittest.read;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0064Fixture {
private static final JsonPointer EMPTY_PTR = JsonPointer.empty();

    void testInvalidEmptyMissingClose() {
        // Fixed-array length 4, but the literal root ends before its value does.
        byte[] truncated = { 0x02, 0x04, 0x31 };
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = new VPackFactory().createParser(truncated)) {
                parser.nextToken();
            }
        });
    }

    void testInvalidExtraComma() {
        // Compact array count says two values while the body contains one.
        byte[] inconsistentCount = { 0x13, 0x04, 0x31, 0x02 };
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = new VPackFactory().createParser(inconsistentCount)) {
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                parser.nextToken();
            }
        });
    }
private static void assertTokenAndPath(JsonParser parser, JsonToken token,
            String path) throws Exception {
        assertEquals(token, parser.nextToken());
        assertEquals(path, parser.streamReadContext().pathAsPointer(true).toString());
    }

    void __invoke_testInvalidEmptyMissingClose() throws Exception {
        try {
            testInvalidEmptyMissingClose();
        } finally {
        }
    }


    void __invoke_testInvalidExtraComma() throws Exception {
        try {
            testInvalidExtraComma();
        } finally {
        }
    }

}
