package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;

import static org.junit.jupiter.api.Assertions.assertFalse;

class VPackScalarInputOwnershipTest {
    @Test
    void successfulScalarStreamWritesNeverCloseBorrowedInputs() throws Exception {
        TrackingInputStream input = new TrackingInputStream(new byte[] { 1, 2 });
        TrackingReader reader = new TrackingReader("ok");
        try (JsonGenerator generator = new VPackFactory().createGenerator(
                new ByteArrayOutputStream())) {
            generator.writeBinary(input, -1);
            generator.writeString(reader, -1);
        }
        assertFalse(input.closed);
        assertFalse(reader.closed);
    }

    private static final class TrackingInputStream extends InputStream {
        private final byte[] value;
        private int position;
        boolean closed;

        TrackingInputStream(byte[] value) {
            this.value = value;
        }

        @Override
        public int read() {
            return position == value.length ? -1 : value[position++] & 0xFF;
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    private static final class TrackingReader extends Reader {
        private final String value;
        private int position;
        boolean closed;

        TrackingReader(String value) {
            this.value = value;
        }

        @Override
        public int read(char[] buffer, int offset, int length) {
            if (position == value.length()) {
                return -1;
            }
            int count = Math.min(length, value.length() - position);
            value.getChars(position, position + count, buffer, offset);
            position += count;
            return count;
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }
}
