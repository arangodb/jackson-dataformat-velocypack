package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackRootReaderTest {
    @Test
    void byteArrayFramesBorrowedRootsAndKeepsNextRoot() {
        byte[] input = { 9, 9, 0x41, 'a', 0x41, 'b', 9 };
        VPackRootReader reader = VPackRootReader.forByteArray(input, 2, 4);

        VPackRootReader.Root first = reader.nextRoot();
        VPackRootReader.Root second = reader.nextRoot();

        assertEquals(VPackRootReader.SourceKind.BYTE_ARRAY, first.sourceKind());
        assertSame(input, first.source());
        assertTrue(first.isBorrowed());
        assertEquals(0L, first.startOffset());
        assertEquals(2L, first.endOffset());
        assertArrayEquals(new byte[] { 0x41, 'a' }, first.toByteArray());
        assertEquals(2L, second.startOffset());
        assertEquals(4L, second.endOffset());
        assertArrayEquals(new byte[] { 0x41, 'b' }, second.toByteArray());
        assertNull(reader.nextRoot());

        input[3] = 'z';
        assertEquals('z', first.byteAt(1));
    }

    @Test
    void streamReadsOnlyTheCurrentRootAndOwnsItsBuffer() {
        TrackingInputStream input = new TrackingInputStream(new byte[] {
                0x41, 'a', 0x41, 'b'
        });
        VPackRootReader reader = VPackRootReader.forInputStream(input);

        VPackRootReader.Root first = reader.nextRoot();
        assertEquals(2, input.physicalPosition());
        assertFalse(first.isBorrowed());
        assertEquals(VPackRootReader.SourceKind.INPUT_STREAM, first.sourceKind());
        assertArrayEquals(new byte[] { 0x41, 'a' }, first.toByteArray());

        VPackRootReader.Root second = reader.nextRoot();
        assertEquals(4, input.physicalPosition());
        assertEquals(2L, second.startOffset());
        assertArrayEquals(new byte[] { 0x41, 'b' }, second.toByteArray());
        assertNull(reader.nextRoot());
    }

    @Test
    void dataInputUsesTheSameExactFramingAndCumulativePositions() {
        VPackRootReader reader = VPackRootReader.forDataInput(
                new DataInputStream(new ByteArrayInputStream(new byte[] {
                        0x18, 0x1B, 1, 2, 3, 4, 5, 6, 7, 8
                })), VPackReadConstraints.defaults(), 3_000_000_000L);

        VPackRootReader.Root first = reader.nextRoot();
        VPackRootReader.Root second = reader.nextRoot();
        assertEquals(3_000_000_000L, first.startOffset());
        assertEquals(3_000_000_001L, first.endOffset());
        assertEquals(3_000_000_001L, second.startOffset());
        assertEquals(9L, second.length());
        assertEquals(3_000_000_010L, reader.logicalPosition());
    }

    @Test
    void framesLengthPrefixedScalarsAndContainersWithoutWalkingPayload() {
        byte[] bytes = {
                (byte) 0xC0, 3, 10, 11, 12,
                (byte) 0xC8, 1, 0, 0, 0, 0, 0x12,
                0x02, 0x03, 0x31,
                0x13, 0x03, 0
        };
        VPackRootReader reader = VPackRootReader.forByteArray(bytes);

        assertEquals(5L, reader.nextRoot().length());
        assertEquals(7L, reader.nextRoot().length());
        assertEquals(3L, reader.nextRoot().length());
        assertEquals(3L, reader.nextRoot().length());
        assertNull(reader.nextRoot());
    }

    @Test
    void acceptsEveryIndependentScalarLengthWidthFromBothSources() {
        for (int width = 1; width <= 8; ++width) {
            byte[] binary = scalarFixture(0xC0 + width - 1, width, (byte) 0x55);
            assertArrayEquals(binary, VPackRootReader.forByteArray(binary).nextRoot().toByteArray());
            assertArrayEquals(binary, VPackRootReader.forInputStream(
                    new ByteArrayInputStream(binary)).nextRoot().toByteArray());

            byte[] positiveBcd = bcdFixture(0xC8 + width - 1, width);
            assertArrayEquals(positiveBcd,
                    VPackRootReader.forByteArray(positiveBcd).nextRoot().toByteArray());
            assertArrayEquals(positiveBcd, VPackRootReader.forInputStream(
                    new ByteArrayInputStream(positiveBcd)).nextRoot().toByteArray());

            byte[] negativeBcd = bcdFixture(0xD0 + width - 1, width);
            assertArrayEquals(negativeBcd,
                    VPackRootReader.forByteArray(negativeBcd).nextRoot().toByteArray());
            assertArrayEquals(negativeBcd, VPackRootReader.forInputStream(
                    new ByteArrayInputStream(negativeBcd)).nextRoot().toByteArray());
        }
    }

    @Test
    void zeroLengthBulkReadsStillMakeProgress() {
        InputStream input = new InputStream() {
            private final byte[] bytes = { 0x41, 'a' };
            private int index;

            @Override
            public int read() {
                return index == bytes.length ? -1 : bytes[index++] & 0xFF;
            }

            @Override
            public int read(byte[] target, int offset, int length) {
                return 0;
            }
        };
        VPackRootReader.Root root = VPackRootReader.forInputStream(input).nextRoot();
        assertArrayEquals(new byte[] { 0x41, 'a' }, root.toByteArray());
    }

    private static final class TrackingInputStream extends InputStream {
        private final byte[] bytes;
        private int index;

        private TrackingInputStream(byte[] bytes) {
            this.bytes = Arrays.copyOf(bytes, bytes.length);
        }

        @Override
        public int read() {
            return index == bytes.length ? -1 : bytes[index++] & 0xFF;
        }

        @Override
        public int read(byte[] target, int offset, int length) throws IOException {
            if (length == 0) {
                return 0;
            }
            int count = Math.min(length, bytes.length - index);
            if (count == 0) {
                return -1;
            }
            System.arraycopy(bytes, index, target, offset, count);
            index += count;
            return count;
        }

        private int physicalPosition() {
            return index;
        }
    }

    // Independently assembles deliberately nonminimal length prefixes; this does
    // not call the production width, layout, or scalar code.
    private static byte[] scalarFixture(int marker, int width, byte payload) {
        byte[] result = new byte[1 + width + 1];
        result[0] = (byte) marker;
        result[1] = 1;
        result[result.length - 1] = payload;
        return result;
    }

    private static byte[] bcdFixture(int marker, int width) {
        byte[] result = new byte[1 + width + 4 + 1];
        result[0] = (byte) marker;
        result[1] = 1;
        result[result.length - 1] = 0x12;
        return result;
    }
}
