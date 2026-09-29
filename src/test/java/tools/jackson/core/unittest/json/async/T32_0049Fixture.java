package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0049Fixture {
private static final byte[] UNSIGNED_C0FFEE = {
            0x2A, (byte) 0xEE, (byte) 0xFF, (byte) 0xC0
    };
private static final byte[] UNSIGNED_CAFE = {
            0x29, (byte) 0xFE, (byte) 0xCA
    };
private static final byte[] SIGNED_NEGATIVE_SIXTEEN = {
            0x20, (byte) 0xF0
    };
private static final byte[] UNSIGNED_255 = {
            0x28, (byte) 0xFF
    };

    void unsignedHexValueRetainsExactVpackInteger() throws Exception {
        assertInteger(UNSIGNED_C0FFEE, 0xC0FFEE);
    }

    void uppercaseHexValueRetainsExactVpackInteger() throws Exception {
        assertInteger(UNSIGNED_CAFE, 0xCAFE);
    }

    void negativeHexValueRetainsExactVpackInteger() throws Exception {
        assertInteger(SIGNED_NEGATIVE_SIXTEEN, -16);
    }

    void positiveHexValueRetainsExactVpackInteger() throws Exception {
        assertInteger(UNSIGNED_255, 255);
    }

    void plainZeroRetainsExactVpackInteger() throws Exception {
        assertInteger(new byte[] { 0x30 }, 0);
    }
private static void assertInteger(byte[] input, long expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getLongValue());
            assertEquals((int) expected, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_unsignedHexValueRetainsExactVpackInteger() throws Exception {
        try {
            unsignedHexValueRetainsExactVpackInteger();
        } finally {
        }
    }


    void __invoke_uppercaseHexValueRetainsExactVpackInteger() throws Exception {
        try {
            uppercaseHexValueRetainsExactVpackInteger();
        } finally {
        }
    }


    void __invoke_negativeHexValueRetainsExactVpackInteger() throws Exception {
        try {
            negativeHexValueRetainsExactVpackInteger();
        } finally {
        }
    }


    void __invoke_positiveHexValueRetainsExactVpackInteger() throws Exception {
        try {
            positiveHexValueRetainsExactVpackInteger();
        } finally {
        }
    }


    void __invoke_plainZeroRetainsExactVpackInteger() throws Exception {
        try {
            plainZeroRetainsExactVpackInteger();
        } finally {
        }
    }

}
