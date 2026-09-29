package tools.jackson.core.unittest.read;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0065Fixture {

    void testInvalidMissingFieldName() {
        // Compact array with one body byte: 00 is not a value marker.
        byte[] invalid = { 0x13, 0x04, 0x00, 0x01 };
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = new VPackFactory().createParser(invalid)) {
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                parser.nextToken();
            }
        });
    }

    void __invoke_testInvalidMissingFieldName() throws Exception {
        try {
            testInvalidMissingFieldName();
        } finally {
        }
    }

}
