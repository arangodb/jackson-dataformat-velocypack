package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackStringGeneratorTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void usesUtf8ByteBoundariesAndPreservesEmbeddedNul() throws Exception {
        String shortValue = "s".repeat(126);
        String longValue = "l".repeat(127);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeString(shortValue);
            generator.writeString(longValue);
            generator.writeString("a\0€😀");
        }

        byte[] actual = output.toByteArray();
        assertEquals((byte) 0xBE, actual[0]);
        assertEquals((byte) 0xBF, actual[127]);
        assertEquals(127, actual[128] & 0xFF);
        assertArrayEquals(new byte[] { 0x49, 'a', 0, (byte) 0xE2, (byte) 0x82,
                (byte) 0xAC, (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x80 },
                Arrays.copyOfRange(actual, actual.length - 10, actual.length));
    }

    @Test
    void charArraysAndUtf8OverloadUseStrictEncoding() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            char[] chars = "prefix😀suffix".toCharArray();
            generator.writeString(chars, 6, 2);
            generator.writeUTF8String(new byte[] { (byte) 0xE2, (byte) 0x82, (byte) 0xAC },
                    0, 3);
        }
        assertArrayEquals(new byte[] { 0x44, (byte) 0xF0, (byte) 0x9F, (byte) 0x98,
                (byte) 0x80, 0x43, (byte) 0xE2, (byte) 0x82, (byte) 0xAC },
                output.toByteArray());
    }

    @Test
    void rejectsMalformedUtf16AndUtf8AndRawOutput() {
        assertStringFailure("\uD800");
        assertStringFailure("\uDC00");
        assertUtf8Failure(new byte[] { (byte) 0xC0, (byte) 0x80 });
        assertUtf8Failure(new byte[] { (byte) 0xED, (byte) 0xA0, (byte) 0x80 });

        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(UnsupportedOperationException.class, () -> generator.writeRaw("raw"));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
                // Failed generators retain their primary failure for close().
            }
        }
    }

    @Test
    void readerConsumesExactCharactersAndDoesNotCloseBorrowedInput() throws Exception {
        TrackingReader reader = new TrackingReader("abcXYZ");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeString(reader, 3);
        }
        assertArrayEquals(new byte[] { 0x43, 'a', 'b', 'c' }, output.toByteArray());
        assertEquals('X', reader.read());
        if (reader.closed) {
            throw new AssertionError("reader was closed");
        }
    }

    @Test
    void readerReadsUntilEofAndHandlesZeroRead() throws Exception {
        ZeroReadReader reader = new ZeroReadReader("€😀");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeString(reader, -1);
        }
        assertArrayEquals(new byte[] { 0x47, (byte) 0xE2, (byte) 0x82, (byte) 0xAC,
                (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x80 },
                output.toByteArray());
    }

    @Test
    void readerReportsEarlyEofAndRootBudgetBeforeLargeEncoding() {
        JsonGenerator early = factory.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(StreamWriteException.class,
                    () -> early.writeString(new StringReader("x"), 2));
        } finally {
            try {
                early.close();
            } catch (RuntimeException ignored) {
            }
        }

        VPackFactory constrained = VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder()
                        .maxRootValueBytes(4).build())
                .build();
        JsonGenerator limited = constrained.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(StreamConstraintsException.class,
                    () -> limited.writeString(new StringReader("abcd"), -1));
        } finally {
            try {
                limited.close();
            } catch (RuntimeException ignored) {
            }
        }
    }

    private void assertStringFailure(String value) {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(StreamWriteException.class, () -> generator.writeString(value));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
            }
        }
    }

    private void assertUtf8Failure(byte[] value) {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(StreamWriteException.class, () -> generator.writeUTF8String(value, 0,
                    value.length));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
            }
        }
    }

    private static class TrackingReader extends StringReader {
        boolean closed;

        TrackingReader(String value) {
            super(value);
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    private static final class ZeroReadReader extends TrackingReader {
        ZeroReadReader(String value) {
            super(value);
        }

        @Override
        public int read(char[] buffer, int offset, int length) {
            return 0;
        }
    }
}
