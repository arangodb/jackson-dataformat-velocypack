package tools.jackson.core.unittest.io;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0028F0 {

    void literalNineteenDigitValuesRemainExact() throws Exception {
        // Independent little-endian VPack scalar fixtures for the source's
        // positive and negative 19-digit values.
        assertLongFixture(new byte[] {
                0x2F, 0x15, (byte) 0x81, (byte) 0xE9, 0x7D,
                (byte) 0xF4, 0x10, 0x22, 0x11
        }, 1234567890123456789L);
        assertLongFixture(new byte[] {
                0x27, (byte) 0xEB, 0x7E, 0x16, (byte) 0x82,
                0x0B, (byte) 0xEF, (byte) 0xDD, (byte) 0xEE
        }, -1234567890123456789L);
    }
private static void assertLongFixture(byte[] fixture, long expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getLongValue());
            assertNull(parser.nextToken());
        }
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = (byte) values[i];
        }
        return result;
    }
private static byte[] slice(byte[] input, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(input, offset, result, 0, length);
        return result;
    }

    void __invoke_literalNineteenDigitValuesRemainExact() throws Exception {
        try {
            literalNineteenDigitValuesRemainExact();
        } finally {
        }
    }

}
