package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackVarIntsTest {
    @Test
    void forwardAndReverseBoundariesHaveTheProfileBytes() {
        assertArrayEquals(new byte[] { 0x7f }, VPackVarInts.encodeForward(127L));
        assertArrayEquals(new byte[] { 0x7f }, VPackVarInts.encodeReverse(127L));
        assertArrayEquals(new byte[] { (byte) 0x80, 0x01 }, VPackVarInts.encodeForward(128L));
        assertArrayEquals(new byte[] { 0x01, (byte) 0x80 }, VPackVarInts.encodeReverse(128L));
        assertArrayEquals(new byte[] { (byte) 0x81, 0x01 }, VPackVarInts.encodeForward(129L));
        assertArrayEquals(new byte[] { 0x01, (byte) 0x81 }, VPackVarInts.encodeReverse(129L));

        long sevenGroupMaximum = (1L << 49) - 1L;
        for (long value : new long[] { 0L, 1L, 127L, 128L, 129L,
                16383L, 16384L, sevenGroupMaximum, sevenGroupMaximum + 1L,
                (1L << 56) - 1L }) {
            VPackVarInts.Decoded forward = VPackVarInts.readForward(
                    VPackVarInts.encodeForward(value), 0,
                    VPackVarInts.encodedLength(value));
            VPackVarInts.Decoded reverse = VPackVarInts.readReverse(
                    VPackVarInts.encodeReverse(value), 0,
                    VPackVarInts.encodedLength(value));
            assertEquals(value, forward.value());
            assertEquals(value, reverse.value());
            assertEquals(VPackVarInts.encodedLength(value), forward.length());
            assertEquals(VPackVarInts.encodedLength(value), reverse.length());
        }
    }

    @Test
    void nonminimalGroupsAreReadableButWritersAreMinimal() {
        VPackVarInts.Decoded forward = VPackVarInts.readForward(
                new byte[] { (byte) 0x80, 0x00 }, 0, 2);
        VPackVarInts.Decoded reverse = VPackVarInts.readReverse(
                new byte[] { 0x00, (byte) 0x80 }, 0, 2);
        assertEquals(0L, forward.value());
        assertEquals(0L, reverse.value());
        assertEquals(1, VPackVarInts.encodedLength(0L));
        assertArrayEquals(new byte[] { 0 }, VPackVarInts.encodeForward(0L));
        assertArrayEquals(new byte[] { 0 }, VPackVarInts.encodeReverse(0L));

        byte[] eightGroupForwardZero = new byte[] {
                (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                (byte) 0x80, (byte) 0x80, (byte) 0x80, 0x00
        };
        byte[] eightGroupReverseZero = new byte[] {
                0x00, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80
        };
        assertEquals(0L, VPackVarInts.readForward(
                eightGroupForwardZero, 0, eightGroupForwardZero.length).value());
        assertEquals(0L, VPackVarInts.readReverse(
                eightGroupReverseZero, 0, eightGroupReverseZero.length).value());

        byte[] eightGroupForward127 = new byte[] {
                (byte) 0xFF, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                (byte) 0x80, (byte) 0x80, (byte) 0x80, 0x00
        };
        byte[] eightGroupReverse127 = new byte[] {
                0x00, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xFF
        };
        assertEquals(127L, VPackVarInts.readForward(
                eightGroupForward127, 0, eightGroupForward127.length).value());
        assertEquals(127L, VPackVarInts.readReverse(
                eightGroupReverse127, 0, eightGroupReverse127.length).value());
    }

    @Test
    void truncationOverlongContinuationAndBoundsBecomeJacksonFailures() {
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readForward(new byte[0], 0, 0));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readReverse(new byte[0], 0, 0));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readForward(new byte[] { (byte) 0x80 }, 0, 1));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readReverse(new byte[] { (byte) 0x80 }, 0, 1));

        byte[] nineContinuations = new byte[9];
        for (int i = 0; i < nineContinuations.length; ++i) nineContinuations[i] = (byte) 0x80;
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readForward(nineContinuations, 0, nineContinuations.length));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readReverse(nineContinuations, 0, nineContinuations.length));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readForward(VPackVarInts.encodeForward(128L), 0, 2, 127L));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readForward(new byte[] { 0 }, 2, 1));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readReverse(new byte[] { 0 }, -1, 1));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readForward(new byte[] { 0 }, 0, 2));
        assertThrows(StreamWriteException.class,
                () -> VPackVarInts.encodeForward(VPackBounds.MAX_COMPACT_VALUE + 1L));
        assertThrows(StreamWriteException.class,
                () -> VPackVarInts.encodeReverse(-1L));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readReverse(new byte[] { 0x01, (byte) 0x80 }, 0, 2, 127L));
        assertThrows(StreamReadException.class,
                () -> VPackVarInts.readReverse(new byte[] { 0x00 }, 0, 1, -1L));
    }
}
