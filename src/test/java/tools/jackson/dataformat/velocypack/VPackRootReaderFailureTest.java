package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackRootReaderFailureTest {
    @Test
    void emptySourcesAreCleanButPartialRootsAreTruncation() {
        assertEquals(null, VPackRootReader.forByteArray(new byte[0]).nextRoot());
        assertEquals(null, VPackRootReader.forInputStream(
                new ByteArrayInputStream(new byte[0])).nextRoot());
        assertEquals(null, VPackRootReader.forDataInput(new EofDataInput()).nextRoot());

        assertThrows(StreamReadException.class,
                () -> VPackRootReader.forByteArray(new byte[] { 0x41 }).nextRoot());
        assertThrows(StreamReadException.class,
                () -> VPackRootReader.forInputStream(
                        new ByteArrayInputStream(new byte[] { (byte) 0xC1, 2, 1 })).nextRoot());
        VPackRootReader data = VPackRootReader.forDataInput(new DataInputThatEndsAfter(0x41));
        assertThrows(StreamReadException.class, data::nextRoot);

        VPackRootReader completeData = VPackRootReader.forDataInput(
                new DataInputThatEndsAfter(0x18));
        completeData.nextRoot();
        assertEquals(null, completeData.nextRoot());
    }

    @Test
    void unsupportedMarkersDoNotReadTheirFollowingBytes() {
        NoPayloadReadInput input = new NoPayloadReadInput(new byte[] { 0x1D, 1, 2, 3 });
        assertThrows(StreamReadException.class,
                () -> VPackRootReader.forInputStream(input).nextRoot());
        assertEquals(1, input.reads);
    }

    @Test
    void checksLengthsBeforePayloadAcquisitionOrAllocation() {
        VPackReadConstraints constraints = VPackReadConstraints.builder()
                .maxRootValueBytes(3L).build();
        TrackingInputStream input = new TrackingInputStream(new byte[] {
                (byte) 0xC1, 4, 1, 2, 3, 4
        });
        assertThrows(StreamConstraintsException.class,
                () -> VPackRootReader.forInputStream(input, constraints).nextRoot());
        assertEquals(3, input.position());

        assertThrows(StreamReadException.class,
                () -> VPackRootReader.forByteArray(new byte[] { 2, 1, 0 }).nextRoot());
        assertThrows(StreamReadException.class,
                () -> VPackRootReader.forByteArray(new byte[] { 0x13, (byte) 0x80 }).nextRoot());
    }

    @Test
    void bcdChecksCompleteRootAndDocumentBudgetsBeforeExponentOrMantissa() {
        VPackReadConstraints rootConstraints = VPackReadConstraints.builder()
                .maxRootValueBytes(8L).build();
        TrackingInputStream rootInput = new TrackingInputStream(new byte[] {
                (byte) 0xC8, 0x7F, 0, 0, 0, 0, 0x12, 0x34
        });
        assertThrows(StreamConstraintsException.class,
                () -> VPackRootReader.forInputStream(rootInput, rootConstraints).nextRoot());
        assertEquals(2, rootInput.position());

        TrackingInputStream documentInput = new TrackingInputStream(new byte[] {
                (byte) 0xC8, 1, 0, 0, 0, 0, 0x12
        });
        assertThrows(StreamConstraintsException.class,
                () -> VPackRootReader.forInputStream(documentInput,
                        VPackReadConstraints.defaults(), 3_000_000_000L, 5L).nextRoot());
        assertEquals(2, documentInput.position());
    }

    @Test
    void framingErrorsUseLogicalSliceAndStreamOffsets() {
        StreamReadException sliceMarker = assertThrows(StreamReadException.class,
                () -> VPackRootReader.forByteArray(new byte[] { 0x55, 0x1D }, 1, 1)
                        .nextRoot());
        assertEquals(0L, sliceMarker.getLocation().getByteOffset());

        byte[] highBit = new byte[10];
        highBit[0] = (byte) 0xC7;
        highBit[8] = (byte) 0x80;
        StreamReadException sliceLength = assertThrows(StreamReadException.class,
                () -> VPackRootReader.forByteArray(joinPrefix(highBit), 3, highBit.length)
                        .nextRoot());
        assertEquals(1L, sliceLength.getLocation().getByteOffset());

        StreamReadException streamMarker = assertThrows(StreamReadException.class,
                () -> VPackRootReader.forInputStream(new ByteArrayInputStream(new byte[] { 0x1D }),
                        VPackReadConstraints.defaults(), 3_000_000_000L).nextRoot());
        assertEquals(3_000_000_000L, streamMarker.getLocation().getByteOffset());

    }

    @Test
    void rejectsZeroMantissaAndHighBitStructuralLengths() {
        assertThrows(StreamReadException.class,
                () -> VPackRootReader.forByteArray(new byte[] {
                        (byte) 0xC8, 0, 0, 0, 0, 0
                }).nextRoot());
        assertThrows(StreamReadException.class,
                () -> VPackRootReader.forByteArray(new byte[] {
                        (byte) 0xBF, 0, 0, 0, 0, 0, 0, 0, (byte) 0x80
                }).nextRoot());
    }

    @Test
    void translatesDataInputEofAfterMarkerAndIoFailures() {
        VPackRootReader reader = VPackRootReader.forDataInput(new DataInputThatEndsAfter(0xC1, 2));
        assertThrows(StreamReadException.class, reader::nextRoot);

        InputStream failing = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };
        assertThrows(StreamReadException.class,
                () -> VPackRootReader.forInputStream(failing).nextRoot());
    }

    @Test
    void knownByteArrayDocumentLimitIsCheckedBeforeRootsAreBorrowed() {
        VPackReadConstraints constraints = VPackReadConstraints.defaults();
        assertThrows(StreamConstraintsException.class,
                () -> VPackRootReader.forByteArray(new byte[] { 0x18, 0x18 }, 0, 2,
                        constraints, 42L, 1L));
    }

    private static final class NoPayloadReadInput extends InputStream {
        private final byte[] bytes;
        private int index;
        private int reads;

        private NoPayloadReadInput(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public int read() {
            reads++;
            return index == bytes.length ? -1 : bytes[index++] & 0xFF;
        }
    }

    private static final class TrackingInputStream extends ByteArrayInputStream {
        private TrackingInputStream(byte[] bytes) { super(bytes); }
        private int position() { return pos; }
    }

    private static byte[] joinPrefix(byte[] root) {
        byte[] input = new byte[root.length + 3];
        input[0] = 0x11;
        input[1] = 0x22;
        input[2] = 0x33;
        System.arraycopy(root, 0, input, 3, root.length);
        return input;
    }

    private static final class EofDataInput implements DataInput {
        @Override public void readFully(byte[] b) throws IOException { throw new EOFException(); }
        @Override public void readFully(byte[] b, int off, int len) throws IOException { throw new EOFException(); }
        @Override public int skipBytes(int n) { return 0; }
        @Override public boolean readBoolean() throws IOException { throw new EOFException(); }
        @Override public byte readByte() throws IOException { throw new EOFException(); }
        @Override public int readUnsignedByte() throws IOException { throw new EOFException(); }
        @Override public short readShort() throws IOException { throw new EOFException(); }
        @Override public int readUnsignedShort() throws IOException { throw new EOFException(); }
        @Override public char readChar() throws IOException { throw new EOFException(); }
        @Override public int readInt() throws IOException { throw new EOFException(); }
        @Override public long readLong() throws IOException { throw new EOFException(); }
        @Override public float readFloat() throws IOException { throw new EOFException(); }
        @Override public double readDouble() throws IOException { throw new EOFException(); }
        @Override public String readLine() throws IOException { throw new EOFException(); }
        @Override public String readUTF() throws IOException { throw new EOFException(); }
    }

    private static final class DataInputThatEndsAfter implements DataInput {
        private final int[] bytes;
        private int index;

        private DataInputThatEndsAfter(int... bytes) { this.bytes = bytes; }

        @Override public void readFully(byte[] b) throws IOException { readFully(b, 0, b.length); }
        @Override public void readFully(byte[] b, int off, int len) throws IOException {
            for (int i = 0; i < len; ++i) b[off + i] = (byte) readUnsignedByte();
        }
        @Override public int skipBytes(int n) { int skipped = Math.min(n, bytes.length - index); index += skipped; return skipped; }
        @Override public boolean readBoolean() throws IOException { return readUnsignedByte() != 0; }
        @Override public byte readByte() throws IOException { return (byte) readUnsignedByte(); }
        @Override public int readUnsignedByte() throws IOException {
            if (index == bytes.length) throw new EOFException();
            return bytes[index++];
        }
        @Override public short readShort() throws IOException { throw new EOFException(); }
        @Override public int readUnsignedShort() throws IOException { throw new EOFException(); }
        @Override public char readChar() throws IOException { throw new EOFException(); }
        @Override public int readInt() throws IOException { throw new EOFException(); }
        @Override public long readLong() throws IOException { throw new EOFException(); }
        @Override public float readFloat() throws IOException { throw new EOFException(); }
        @Override public double readDouble() throws IOException { throw new EOFException(); }
        @Override public String readLine() throws IOException { throw new EOFException(); }
        @Override public String readUTF() throws IOException { throw new EOFException(); }
    }
}
