package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamConstraintsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The mandatory header, not just its eventual payload, must fit before I/O. */
class VPackHeaderBudgetRegressionTest {
    @Test
    void fixedLengthHeadersRespectRootBudgetBeforeReading() {
        for (int marker = 0; marker <= 255; ++marker) {
            if (!hasLengthHeader(marker)) continue;
            for (boolean dataInput : new boolean[] { false, true }) {
                GuardedInput input = new GuardedInput((byte) marker);
                try (VPackRootReader reader = reader(input, dataInput, 1L, -1L)) {
                    assertThrows(StreamConstraintsException.class, reader::nextRoot,
                            "marker=" + marker + ", dataInput=" + dataInput);
                    assertEquals(1, input.consumed);
                }
            }
        }
    }

    @Test
    void fixedLengthHeadersRespectRemainingDocumentBudgetBeforeReading() {
        for (int marker = 0; marker <= 255; ++marker) {
            if (!hasLengthHeader(marker)) continue;
            for (boolean dataInput : new boolean[] { false, true }) {
                GuardedInput input = new GuardedInput((byte) 0x18, (byte) marker);
                try (VPackRootReader reader = reader(input, dataInput, 1024L, 2L)) {
                    try (VPackRootReader.Root first = reader.nextRoot()) {
                        assertEquals(1L, first.length());
                    }
                    StreamConstraintsException error = assertThrows(
                            StreamConstraintsException.class, reader::nextRoot);
                    assertEquals(3_000_000_001L, error.getLocation().getByteOffset());
                    assertEquals(2, input.consumed);
                }
            }
        }
    }

    @Test
    void compactLengthChecksEachContinuationAgainstBothBudgets() {
        for (int marker : new int[] { 0x13, 0x14 }) {
            for (boolean dataInput : new boolean[] { false, true }) {
                for (boolean documentLimit : new boolean[] { false, true }) {
                    GuardedInput input = new GuardedInput((byte) marker, (byte) 0x80);
                    try (VPackRootReader reader = reader(input, dataInput,
                            documentLimit ? 1024L : 2L, documentLimit ? 2L : -1L)) {
                        assertThrows(StreamConstraintsException.class, reader::nextRoot);
                        assertEquals(2, input.consumed);
                    }
                }
            }
        }
    }

    @Test
    void exactLimitsPermitCompleteHeadersAndCleanEof() {
        byte[][] values = {
                { 0x18 },
                { (byte) 0xC0, 0 },
                { (byte) 0xBF, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0x13, 3, 0 },
                { 0x14, 3, 0 }
        };
        for (byte[] value : values) {
            for (boolean dataInput : new boolean[] { false, true }) {
                try (VPackRootReader reader = reader(new ByteArrayInputStream(value),
                        dataInput, value.length, value.length)) {
                    try (VPackRootReader.Root root = reader.nextRoot()) {
                        assertEquals(value.length, root.length());
                    }
                    assertNull(reader.nextRoot());
                }
            }
        }
    }

    @Test
    void completeHeaderStillChecksPayloadBeforeReadingIt() {
        for (boolean dataInput : new boolean[] { false, true }) {
            GuardedInput input = new GuardedInput((byte) 0xC0, (byte) 2);
            try (VPackRootReader reader = reader(input, dataInput, 3L, -1L)) {
                assertThrows(StreamConstraintsException.class, reader::nextRoot);
                assertEquals(2, input.consumed);
            }
        }
    }

    private static boolean hasLengthHeader(int marker) {
        return marker == 0xBF || (marker >= 0xC0 && marker <= 0xD7)
                || (marker >= 0x02 && marker <= 0x09)
                || (marker >= 0x0B && marker <= 0x12);
    }

    private static VPackRootReader reader(InputStream input, boolean dataInput,
            long rootLimit, long documentLimit) {
        VPackReadConstraints limits = VPackReadConstraints.builder()
                .maxRootValueBytes(rootLimit).build();
        return dataInput ? VPackRootReader.forDataInput(
                (DataInput) new DataInputStream(input), limits, 3_000_000_000L, documentLimit)
                : VPackRootReader.forInputStream(input, limits, 3_000_000_000L, documentLimit);
    }

    /** Fails immediately instead of hanging if an impossible header is read. */
    private static final class GuardedInput extends InputStream {
        private final byte[] prefix;
        int consumed;

        GuardedInput(byte... prefix) { this.prefix = prefix; }

        @Override public int read() {
            if (consumed == prefix.length) {
                throw new AssertionError("Source read past the permitted header prefix");
            }
            return prefix[consumed++] & 0xFF;
        }

        @Override public int read(byte[] bytes, int offset, int length) {
            if (length == 0) return 0;
            bytes[offset] = (byte) read();
            return 1;
        }
    }
}
