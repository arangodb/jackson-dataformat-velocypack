package tools.jackson.core.unittest.read;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0073Fixture {
private static final byte[] UNSIGNED_7F = { 0x28, 0x7F };
private static final byte[] UNSIGNED_80000000 = {
            0x2B, 0x00, 0x00, 0x00, (byte) 0x80
    };

    void unsignedHexWithLeadingZerosRetainsExactVpackValue() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(UNSIGNED_7F)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(127, parser.getIntValue());
            assertEquals(127L, parser.getLongValue());
            assertNull(parser.nextToken());
        }
    }

    void hexLongRangeRetainsExactVpackLongValue() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(UNSIGNED_80000000)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2147483648L, parser.getLongValue());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_unsignedHexWithLeadingZerosRetainsExactVpackValue() throws Exception {
        try {
            unsignedHexWithLeadingZerosRetainsExactVpackValue();
        } finally {
        }
    }


    void __invoke_hexLongRangeRetainsExactVpackLongValue() throws Exception {
        try {
            hexLongRangeRetainsExactVpackLongValue();
        } finally {
        }
    }

}
