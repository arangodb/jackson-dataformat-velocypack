package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Fixed malformed vectors cover structural boundaries through each public source kind. */
@Timeout(20)
class VPackMalformedMatrixTest {
    private static final VPackFactory FACTORY = new VPackFactory();
    private static final long SEED = 0x34_4d_5554L;

    @Test
    void everyTruncationOfCompactFixtureFailsAsLocatedJacksonErrorForAllSources() throws Exception {
        byte[] fixture = { 0x13, 0x06, 0x31, 0x28, 0x10, 0x02 };
        try (JsonParser empty = parser(new byte[0], 0)) {
            org.junit.jupiter.api.Assertions.assertNull(empty.nextToken(), "empty source is clean EOF");
        }
        for (int length = 1; length < fixture.length; ++length) {
            byte[] truncated = java.util.Arrays.copyOf(fixture, length);
            for (int source = 0; source < 4; ++source) {
                assertJacksonError(truncated, source, "seed=" + SEED + " trunc=" + length);
            }
        }
    }

    @Test
    void markerLengthCountIndexUtf8BcdAndContinuationMutationsAreBounded() throws Exception {
        byte[][] vectors = {
                { (byte) 0xf0 },                                      // unsupported marker
                { 0x13, 0x05, 0x31, 0x28, 0x10, 0x02 },             // short compact length
                { 0x13, 0x07, 0x31, 0x28, 0x10, 0x02 },             // long compact length
                { 0x13, 0x06, 0x31, 0x28, 0x10, 0x03 },             // count/body mismatch
                { 0x06, 0x05, 0x01, 0x31, 0x04 },                   // out-of-body index
                { 0x02, 0x05, 0x00, 0x00, 0x31 },                   // forbidden padding start
                { 0x41, (byte) 0xff },                               // invalid UTF-8
                { (byte) 0xc8, 1, 0, 0, 0, 0, (byte) 0xfa },         // invalid BCD digit
                { 0x13, 0x05, (byte) 0x80, 0x01, 0x01 }              // wrong continuation direction
        };
        for (int caseId = 0; caseId < vectors.length; ++caseId) {
            for (int source = 0; source < 4; ++source) {
                assertJacksonError(vectors[caseId], source,
                        "seed=" + SEED + " case=" + caseId + " source=" + source);
            }
        }
    }

    private static void assertJacksonError(byte[] bytes, int source, String context) throws Exception {
        try (JsonParser parser = parser(bytes, source)) {
            StreamReadException failure = assertThrows(StreamReadException.class, () -> {
                while (parser.nextToken() != null) { }
            }, context);
            assertNotNull(failure.getLocation(), context + " should carry a parser location");
        }
    }

    private static JsonParser parser(byte[] bytes, int source) throws Exception {
        return switch (source) {
            case 0 -> FACTORY.createParser(bytes);
            case 1 -> {
                byte[] slice = new byte[bytes.length + 2];
                slice[0] = 0x55;
                System.arraycopy(bytes, 0, slice, 1, bytes.length);
                slice[slice.length - 1] = 0x66;
                yield FACTORY.createParser(slice, 1, bytes.length);
            }
            case 2 -> FACTORY.createParser(new ZeroChunkInput(bytes));
            default -> FACTORY.createParser(ObjectReadContext.empty(),
                    (java.io.DataInput) new DataInputStream(new ByteArrayInputStream(bytes)));
        };
    }

    private static final class ZeroChunkInput extends InputStream {
        private final ByteArrayInputStream input;
        private boolean zero = true;
        ZeroChunkInput(byte[] bytes) { input = new ByteArrayInputStream(bytes); }
        @Override public int read() { return input.read(); }
        @Override public int read(byte[] b, int off, int len) {
            if (zero) { zero = false; return 0; }
            zero = true;
            return input.read(b, off, Math.min(1, len));
        }
    }
}
