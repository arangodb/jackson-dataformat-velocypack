package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackGeneratorFailureTest {
    @Test
    void failedOutputIsNotReplayedAndOwnedBuffersAreReleased() {
        PartialFailureOutput target = new PartialFailureOutput(false);
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(target);

        assertThrows(JacksonIOException.class, () -> generator.writeNumber(10));
        int bytesAfterWrite = target.bytes.size();
        assertEquals(0, generator.streamWriteOutputBuffered());

        RuntimeException closeFailure = assertThrows(RuntimeException.class, generator::close);
        assertEquals(bytesAfterWrite, target.bytes.size());
        assertEquals(1, target.closeCount);
        assertDoesNotThrow(generator::close);
        assertEquals(0, closeFailure.getSuppressed().length);
    }

    @Test
    void cleanupFailureIsSuppressedAfterThePrimaryFailure() {
        PartialFailureOutput target = new PartialFailureOutput(true);
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(target);
        assertThrows(JacksonIOException.class, () -> generator.writeNumber(10));

        RuntimeException closeFailure = assertThrows(RuntimeException.class, generator::close);
        assertEquals(1, target.closeCount);
        assertEquals(1, closeFailure.getSuppressed().length);
        assertDoesNotThrow(generator::close);
    }

    @Test
    void contentValidationFailureDiscardsOpenRootAndPreventsFurtherWrites() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        generator.writeStartArray();
        generator.writeNumber(1);
        assertThrows(StreamWriteException.class, generator::writeEndObject);
        assertEquals(0, out.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertThrows(StreamWriteException.class, generator::writeNull);
        assertDoesNotThrow(() -> assertThrows(RuntimeException.class, generator::close));
        assertDoesNotThrow(generator::close);
    }

    private static final class PartialFailureOutput extends OutputStream {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final boolean failClose;
        int closeCount;

        PartialFailureOutput(boolean failClose) {
            this.failClose = failClose;
        }

        @Override
        public void write(int value) {
            bytes.write(value);
        }

        @Override
        public void write(byte[] value, int offset, int length) throws IOException {
            if (length > 0) {
                bytes.write(value[offset]);
            }
            throw new IOException("write failure");
        }

        @Override
        public void close() throws IOException {
            ++closeCount;
            if (failClose) {
                throw new IOException("close failure");
            }
        }
    }
}
