package tools.jackson.core.unittest.read;

import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0074Fixture {
private static final byte[] UNSIGNED_ZERO = { 0x30 };

    void unsignedHexZeroRetainsExactVpackIntegerValue() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(UNSIGNED_ZERO)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(0, parser.getIntValue());
            assertEquals(0L, parser.getLongValue());
            assertEquals(BigInteger.ZERO, parser.getBigIntegerValue());
            assertEquals("0", parser.getString());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_unsignedHexZeroRetainsExactVpackIntegerValue() throws Exception {
        try {
            unsignedHexZeroRetainsExactVpackIntegerValue();
        } finally {
        }
    }

}
