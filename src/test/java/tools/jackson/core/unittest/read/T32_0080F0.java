package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0080F0 {
private static final VPackFactory FACTORY = new VPackFactory();

    void getNumberTypeOnLiteralVpackTokens() throws Exception {
        byte[] input = {
                0x02, 0x0C,
                0x23, 0x7B, 0x00, 0x00, 0x00,
                0x23, 0x00, 0x00, 0x00, 0x00
        };
        for (boolean stream : new boolean[] { false, true }) {
            JsonParser parser = stream
                    ? FACTORY.createParser(new ByteArrayInputStream(input))
                    : FACTORY.createParser(input);
            try (parser) {
                assertNull(parser.getNumberType());
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                assertNull(parser.getNumberType());
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
                assertEquals(123, parser.getIntValue());
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
                assertEquals(0, parser.getIntValue());
                assertEquals(JsonToken.END_ARRAY, parser.nextToken());
                assertNull(parser.getNumberType());
                assertNull(parser.nextToken());
                assertNull(parser.getNumberType());
            }
            assertNull(parser.getNumberType());
        }
    }

    void __invoke_getNumberTypeOnLiteralVpackTokens() throws Exception {
        try {
            getNumberTypeOnLiteralVpackTokens();
        } finally {
        }
    }

}
