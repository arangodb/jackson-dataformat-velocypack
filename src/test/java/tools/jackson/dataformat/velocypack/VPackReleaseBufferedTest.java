package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackReleaseBufferedTest {
    @Test
    void releasesMultiPageArrayTailFromOwnedInputStore() throws Exception {
        String value = "x".repeat(3 * VPackByteStore.PAGE_SIZE + 17);
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(encoded)) {
            generator.writeStartArray();
            generator.writeString(value);
            generator.writeEndArray();
        }
        byte[] bytes = encoded.toByteArray();
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(
                new java.io.ByteArrayInputStream(bytes))) {
            assertEquals(tools.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
            ByteArrayOutputStream tail = new ByteArrayOutputStream();
            assertEquals(bytes.length - 3, parser.releaseBuffered(tail));
            assertArrayEquals(Arrays.copyOfRange(bytes, 3, bytes.length), tail.toByteArray());
        }
    }

    @Test
    void releasesCurrentTailAndRemainingByteArrayRootsOnlyOnce() throws Exception {
        byte[] input = { 0x02, 0x04, 0x31, 0x32, 0x18 };
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(input)) {
            assertEquals(tools.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            assertEquals(3, parser.releaseBuffered(out));
            assertArrayEquals(new byte[] { 0x31, 0x32, 0x18 }, out.toByteArray());
            assertEquals(0, parser.releaseBuffered(new ByteArrayOutputStream()));
            assertEquals(null, parser.nextToken());
        }
    }

    @Test
    void advancesBeforeAWriteFailureAndWriterIsUnsupported() throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(
                new byte[] { 0x41, 'a', 0x18 })) {
            parser.nextToken();
            assertThrows(tools.jackson.core.JacksonException.class,
                    () -> parser.releaseBuffered(new OutputStream() {
                        @Override public void write(int value) throws IOException {
                            throw new IOException("boom");
                        }
                    }));
            assertEquals(0, parser.releaseBuffered(new ByteArrayOutputStream()));
            assertEquals(null, parser.nextToken());
        }
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(new byte[] { 0x18 })) {
            assertEquals(-1, parser.releaseBuffered(new StringWriter()));
        }
    }
}
