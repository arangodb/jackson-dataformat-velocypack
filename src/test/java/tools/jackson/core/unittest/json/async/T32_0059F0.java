package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0059F0 {
private static final byte[] NESTED_OBJECT = {
            0x14, 0x36,
            0x46, 'f', 'o', 'o', 'b', 'a', 'r',
            0x13, 0x08, 0x31, 0x32, 0x21, 0x19, (byte) 0xFC, 0x03,
            0x4B, 'e', 'm', 'p', 't', 'y', 'O', 'b', 'j', 'e', 'c', 't', 0x0A,
            0x4A, 'e', 'm', 'p', 't', 'y', 'A', 'r', 'r', 'a', 'y', 0x01,
            0x45, 'o', 't', 'h', 'e', 'r',
            0x14, 0x05, 0x40, 0x18, 0x01,
            0x04
    };
private static final byte[] NESTED_ARRAY = {
            0x13, 0x29,
            0x1A,
            0x14, 0x0E, 0x49, 'm', 'o', 'r', 'e', 'S', 't', 'u', 'f', 'f', 0x30, 0x01,
            0x13, 0x04, 0x18, 0x01,
            0x14, 0x13, 0x4D, 'e', 'x', 't', 'r', 'a', 'O', 'r', 'd', 'i', 'n', 'a', 'r', 'y', 0x28, 0x17, 0x01,
            0x04
    };
private static final byte[] NESTED_ARRAY_WITH_NAMES = {
            0x13, 0x2F,
            0x14, 0x08, 0x43, 'u', '-', 's', 0x1A, 0x01,
            0x14, 0x0E, 0x49, 'l', 'o', 'n', 'g', '-', 'n', 'a', 'm', 'e', 0x19, 0x01,
            0x14, 0x08, 0x43, 'u', '-', 's', 0x1A, 0x01,
            0x14, 0x0E, 0x49, 'l', 'o', 'n', 'g', '-', 'n', 'a', 'm', 'e', 0x19, 0x01,
            0x04
    };
private static final byte[] BOOLEAN_OBJECT = {
            0x14, 0x48,
            0x41, 'a', 0x1A,
            0x41, 'b', 0x19,
            0x44, 'a', 'c', 'd', 'c', 0x1A,
            0x50, 'U', 'n', 'i', 'c', 'o', 'd', 'e', '-', (byte) 0xC3, (byte) 0xA9, '-', 'R', 'l', 'z', 'O', 'k', 0x1A,
            0x48, 'a', '1', '2', '3', '4', '5', '6', '7', 0x19,
            0x5B, 'U', 'n', 'i', 'c', 'o', 'd', 'e', '-', 'w', 'i', 't', 'h', '-', (byte) 0xC3, (byte) 0xA9, '-', 'm', 'u', 'c', 'h', '-', 'l', 'o', 'n', 'g', 'e', 'r', 0x1A,
            0x06
    };
private static final byte[] NUMBER_OBJECT = {
            0x14, 0x3C,
            0x42, 'i', '1', 0x22, (byte) 0xC0, 0x1D, (byte) 0xFE,
            0x47, 'd', 'o', 'u', 'b', 'l', 'e', 'y', 0x1B, 0x00, 0x00, 0x00, 0x40, 0x3C, 0x46, 0x2F, 0x41,
            0x4D, 'b', 'i', 'g', 'g', 'i', 'e', 'D', 'e', 'c', 'i', 'm', 'a', 'l',
            (byte) 0xC8, 0x0D, (byte) 0xF6, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            0x12, 0x43, 0x56, 0x57, 0x68, 0x67, (byte) 0x90, 0x65, 0x12, 0x47, 0x30, 0x58, 0x34,
            0x03
    };

    void malformedVpackScopesAreRejectedAsStructuralInput() throws Exception {
        // Truncated compact roots are the self-delimiting VPack equivalent of
        // JSON documents ending before their close marker.
        assertRejected(new byte[] { 0x13, 0x05, 0x31, 0x32 });
        assertRejected(new byte[] { 0x14, 0x04, 0x41, 0x6B, 0x33 });

        // Reserved child markers and an object with a missing value are not
        // valid VPack close-marker substitutions or key/value separators.
        assertRejected(new byte[] { 0x13, 0x03, 0x15, 0x01 });
        assertRejected(new byte[] { 0x14, 0x04, 0x41, 0x6B, 0x15, 0x01 });
        assertRejected(new byte[] { 0x14, 0x04, 0x41, 0x6B, 0x01 });
    }
private static void assertNestedBoolean(JsonParser parser, String name,
            JsonToken valueToken) throws Exception {
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(name, parser.currentName());
        assertEquals(valueToken, parser.nextToken());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
    }
private static void assertBooleanProperty(JsonParser parser, String name,
            JsonToken valueToken) throws Exception {
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(name, parser.currentName());
        assertEquals(valueToken, parser.nextToken());
        if (valueToken == JsonToken.VALUE_TRUE) {
            assertTrue(parser.getBooleanValue());
        } else {
            assertFalse(parser.getBooleanValue());
        }
        assertThrows(InputCoercionException.class, parser::getDoubleValue);
        assertThrows(StreamReadException.class, parser::getBinaryValue);
    }
private static void assertRejected(byte[] input) {
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = new VPackFactory().createParser(input)) {
                while (parser.nextToken() != null) {
                    // Exhaust the root so layout validation is performed.
                }
            }
        });
    }

    void __invoke_malformedVpackScopesAreRejectedAsStructuralInput() throws Exception {
        try {
            malformedVpackScopesAreRejectedAsStructuralInput();
        } finally {
        }
    }

}
