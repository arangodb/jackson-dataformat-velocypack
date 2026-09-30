package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackBinaryParserTest {
    @Test
    void supportsEveryNativeBinaryLengthWidthAndZeroLength() throws Exception {
        for (int width = 1; width <= 8; width++) {
            try (VPackParser parser = (VPackParser) new VPackFactory().createParser(
                    binary(width, new byte[0]))) {
                assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
                assertArrayEquals(new byte[0], parser.getBinaryValue());
                assertArrayEquals(new byte[0], (byte[]) parser.getEmbeddedObject());
                assertEquals(0, parser.readBinaryValue(new ByteArrayOutputStream()));
            }
        }
    }

    @Test
    void binaryMaterializationIsDefensiveAndReadBinaryValueStreamsTheRetainedSpan()
            throws Exception {
        byte[] payload = new byte[3 * VPackByteStore.PAGE_SIZE + 17];
        for (int i = 0; i < payload.length; i++) payload[i] = (byte) i;
        byte[] encoded = binary(2, payload);
        byte[] source = encoded.clone();
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(
                new ByteArrayInputStream(source))) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            byte[] materialized = parser.getBinaryValue();
            source[source.length - 1] = 99;
            assertEquals(payload[payload.length - 1], materialized[payload.length - 1]);
            source[source.length - 1] = encoded[encoded.length - 1];

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            assertEquals(payload.length, parser.readBinaryValue(output));
            assertArrayEquals(payload, output.toByteArray());
            assertEquals(VPackType.BINARY, parser.currentVPackType());
            assertNull(parser.getString());
        }
    }

    @Test
    void binaryAndStringAccessorsRejectWrongEmbeddedKinds() throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(new byte[] { 0x1E })) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertNull(parser.getString());
            assertThrows(StreamReadException.class, parser::getBinaryValue);
            assertThrows(StreamReadException.class,
                    () -> parser.readBinaryValue(new ByteArrayOutputStream()));
        }
    }

    private static byte[] binary(int width, byte[] payload) {
        byte[] value = new byte[1 + width + payload.length];
        value[0] = (byte) (0xC0 + width - 1);
        long length = payload.length;
        for (int i = 0; i < width; i++) value[1 + i] = (byte) (length >>> (8 * i));
        System.arraycopy(payload, 0, value, width + 1, payload.length);
        return value;
    }
}
