package tools.jackson.core.unittest.read.loc;

import java.io.ByteArrayInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0096Fixture {

    void simpleInitialOffsetsForEmptyObject() throws Exception {
        // Literal VPack empty-object marker; no layout helper is used.
        byte[] emptyObject = { 0x0A };
        try (JsonParser parser = new VPackFactory().createParser(emptyObject)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertLocation(parser.currentTokenLocation(), 0L);
            assertLocation(parser.currentLocation(), 1L);

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertLocation(parser.currentTokenLocation(), 1L);
            assertLocation(parser.currentLocation(), 1L);
            assertNull(parser.nextToken());
        }
    }

    void largeLiteralStringKeepsByteLocations() throws Exception {
        final int payloadLength = 50_000;
        byte[] string = longAsciiString(payloadLength);
        try (JsonParser parser = new VPackFactory().createParser(
                new ByteArrayInputStream(string))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertLocation(parser.currentTokenLocation(), 0L);
            assertLocation(parser.currentLocation(), string.length);
            assertEquals("A".repeat(payloadLength), parser.getString());
            assertLocation(parser.currentTokenLocation(), 0L);
            assertLocation(parser.currentLocation(), string.length);
            assertNull(parser.nextToken());
        }
    }
private static byte[] longAsciiString(int payloadLength) {
        // Literal VPack long-string layout: marker, uint64 little-endian byte
        // length, then the UTF-8 payload.
        byte[] result = new byte[9 + payloadLength];
        result[0] = (byte) 0xBF;
        long length = payloadLength;
        for (int i = 0; i < 8; ++i) {
            result[1 + i] = (byte) (length >>> (8 * i));
        }
        for (int i = 9; i < result.length; ++i) {
            result[i] = 'A';
        }
        return result;
    }
private static void assertLocation(TokenStreamLocation location, long expectedByteOffset) {
        assertEquals(expectedByteOffset, location.getByteOffset());
        assertEquals(-1L, location.getCharOffset());
        assertEquals(-1, location.getLineNr());
        assertEquals(-1, location.getColumnNr());
    }

    void __invoke_simpleInitialOffsetsForEmptyObject() throws Exception {
        try {
            simpleInitialOffsetsForEmptyObject();
        } finally {
        }
    }


    void __invoke_largeLiteralStringKeepsByteLocations() throws Exception {
        try {
            largeLiteralStringKeepsByteLocations();
        } finally {
        }
    }

}
