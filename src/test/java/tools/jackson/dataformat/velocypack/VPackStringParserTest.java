package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackStringParserTest {
    @Test
    void exactShortAndLongByteBoundariesDecodeAcrossBinarySources() throws Exception {
        byte[] shortPayload = new byte[126];
        byte[] longPayload = new byte[127];
        Arrays.fill(shortPayload, (byte) 's');
        Arrays.fill(longPayload, (byte) 'l');
        byte[] input = concat(shortString(shortPayload), longString(longPayload));
        for (int source = 0; source < 3; source++) {
            VPackFactory factory = new VPackFactory();
            try (VPackParser parser = switch (source) {
                case 0 -> (VPackParser) factory.createParser(input);
                case 1 -> (VPackParser) factory.createParser(new ChunkedInputStream(input));
                default -> (VPackParser) factory.createParser(ObjectReadContext.empty(),
                        (DataInput) new DataInputStream(new ByteArrayInputStream(input)));
            }) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertEquals(126, parser.getStringLength());
                assertEquals('s', parser.getStringCharacters()[125]);
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertEquals(127, parser.getStringLength());
                assertEquals('l', parser.getStringCharacters()[126]);
                assertNull(parser.nextToken());
            }
        }
    }

    @Test
    void clearCurrentTokenClearsStringMetadataAndNextRootStartsClean() throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(
                concat(shortString(new byte[] { 'o', 'k' }), new byte[] { 0x18 }))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertTrue(parser.hasStringCharacters());
            parser.clearCurrentToken();
            assertFalse(parser.hasCurrentToken());
            assertFalse(parser.hasStringCharacters());
            assertNull(parser.getString());
            assertEquals(0, parser.getStringLength());
            assertEquals(0, parser.getStringOffset());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals("null", parser.getString());
        }
    }

    @Test
    void stringIsNotNativeBinary() throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(
                shortString("AQI=".getBytes(java.nio.charset.StandardCharsets.US_ASCII)))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertNull(parser.getEmbeddedObject());
            assertEquals(2, parser.getBinaryValue().length);
            assertEquals(2, parser.getBinaryValue()[1]);
        }
    }

    private static byte[] shortString(byte[] payload) {
        byte[] value = new byte[payload.length + 1];
        value[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, value, 1, payload.length);
        return value;
    }

    private static byte[] longString(byte[] payload) {
        byte[] value = new byte[payload.length + 9];
        value[0] = (byte) 0xBF;
        value[1] = (byte) payload.length;
        System.arraycopy(payload, 0, value, 9, payload.length);
        return value;
    }

    private static byte[] concat(byte[] left, byte[] right) {
        byte[] value = Arrays.copyOf(left, left.length + right.length);
        System.arraycopy(right, 0, value, left.length, right.length);
        return value;
    }

    private static final class ChunkedInputStream extends ByteArrayInputStream {
        private ChunkedInputStream(byte[] input) {
            super(input);
        }

        @Override
        public synchronized int read(byte[] target, int offset, int length) {
            return super.read(target, offset, Math.min(length, 1));
        }
    }
}
