package tools.jackson.dataformat.velocypack;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackBoundsTest {
    @Test
    void checkedArithmeticRejectsNegativeResultsAndOverflow() {
        assertEquals(12L, VPackBounds.checkedAdd(5L, 7L));
        assertEquals(3L, VPackBounds.checkedSubtract(8L, 5L));
        assertEquals(42L, VPackBounds.checkedMultiply(6L, 7L));
        assertEquals(Long.MAX_VALUE, VPackBounds.checkedAdd(Long.MAX_VALUE, 0L));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedAdd(Long.MAX_VALUE, 1L));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedSubtract(0L, 1L));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedMultiply(Long.MAX_VALUE, 2L));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedAdd(-1L, 1L));
    }

    @Test
    void suffixedDiagnosticsPreserveChecksWithoutChangingSuccessfulResults() {
        assertEquals(12L, VPackBounds.checkedAdd(5L, 7L, "string", " range"));
        assertEquals(Long.MAX_VALUE,
                VPackBounds.checkedAdd(Long.MAX_VALUE, 0L, "string", " range"));
        assertEquals(0, VPackBounds.checkedInt(0L, "string", " offset"));
        assertEquals(Integer.MAX_VALUE,
                VPackBounds.checkedInt(Integer.MAX_VALUE, "string", " end"));

        StreamReadException negativeLeft = assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedAdd(-1L, -2L, "string", " range"));
        assertEquals("string range: left operand is negative", negativeLeft.getMessage());

        StreamReadException negativeRight = assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedAdd(1L, -2L, "string", " range"));
        assertEquals("string range: right operand is negative", negativeRight.getMessage());

        StreamReadException overflow = assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedAdd(Long.MAX_VALUE, 1L, "string", " range"));
        assertEquals("string range: addition overflow", overflow.getMessage());

        StreamReadException negativeInt = assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedInt(-1L, "string", " offset"));
        assertEquals("string offset: value is negative", negativeInt.getMessage());

        StreamConstraintsException largeInt = assertThrows(StreamConstraintsException.class,
                () -> VPackBounds.checkedInt((long) Integer.MAX_VALUE + 1L,
                        "string", " end"));
        assertEquals("string end: value does not fit in an int\n"
                + " at [Source: UNKNOWN; byte offset: #UNKNOWN]", largeInt.getMessage());
    }

    @Test
    void rangesAndWidthsAreCheckedBeforeArrayIndexArithmetic() {
        assertEquals(8L, VPackBounds.checkedRange(3L, 5L, 8L, "test range"));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedRange(Long.MAX_VALUE, 1L, Long.MAX_VALUE, "test range"));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedRange(7L, 2L, 8L, "test range"));
        assertThrows(StreamConstraintsException.class,
                () -> VPackBounds.checkedInt((long) Integer.MAX_VALUE + 1L, "test cast"));
        assertThrows(StreamConstraintsException.class,
                () -> VPackBounds.requireBudget(11L, 10L, "test budget"));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.checkedArrayRange(new byte[4], Integer.MAX_VALUE, 2L, "array"));
        assertThrows(StreamReadException.class, () -> VPackBounds.requireWidth(3, "width"));
    }

    @Test
    void writeWidthsAndOutputRangesUseWriteExceptions() {
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeSigned(new byte[1], 0, 0, 0L));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeUnsigned(new byte[8], 0, 9, BigInteger.ZERO));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeBits(new byte[4], 0, 3, 0L));

        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeSigned(null, 0, 1, 0L));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeUnsigned(null, 0, 1, BigInteger.ZERO));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeBits(null, 0, 1, 0L));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeSigned(new byte[1], -1, 1, 0L));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeUnsigned(new byte[1], Integer.MAX_VALUE, 1,
                        BigInteger.ZERO));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeBits(new byte[1], Integer.MAX_VALUE, 8, 0L));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.checkedWriteArrayRange(new byte[0], Long.MAX_VALUE, 1L,
                        "output"));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.checkedWriteArrayRange(new byte[0], 0L, -1L, "output"));
    }

    @Test
    void readWidthsAndInputRangesRemainReadExceptions() {
        assertThrows(StreamReadException.class,
                () -> VPackBounds.readSigned(new byte[1], 0, 0));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.readUnsigned(new byte[8], 0, 9));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.readBits(new byte[4], 0, 3));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.readSigned(null, 0, 1));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.readUnsigned(new byte[1], -1, 1));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.readBits(new byte[1], 1, 1));
    }

    @Test
    void littleEndianScalarFieldsDecodeEverySequentialWidth() {
        byte[][] negativeOne = new byte[][] {
            VPackWireFixtureTest.hex("ff"),
            VPackWireFixtureTest.hex("ff ff"),
            VPackWireFixtureTest.hex("ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff ff ff ff ff")
        };
        byte[][] minimum = new byte[][] {
            VPackWireFixtureTest.hex("80"),
            VPackWireFixtureTest.hex("00 80"),
            VPackWireFixtureTest.hex("00 00 80"),
            VPackWireFixtureTest.hex("00 00 00 80"),
            VPackWireFixtureTest.hex("00 00 00 00 80"),
            VPackWireFixtureTest.hex("00 00 00 00 00 80"),
            VPackWireFixtureTest.hex("00 00 00 00 00 00 80"),
            VPackWireFixtureTest.hex("00 00 00 00 00 00 00 80")
        };
        byte[][] maximum = new byte[][] {
            VPackWireFixtureTest.hex("7f"),
            VPackWireFixtureTest.hex("ff 7f"),
            VPackWireFixtureTest.hex("ff ff 7f"),
            VPackWireFixtureTest.hex("ff ff ff 7f"),
            VPackWireFixtureTest.hex("ff ff ff ff 7f"),
            VPackWireFixtureTest.hex("ff ff ff ff ff 7f"),
            VPackWireFixtureTest.hex("ff ff ff ff ff ff 7f"),
            VPackWireFixtureTest.hex("ff ff ff ff ff ff ff 7f")
        };
        for (int width = 1; width <= 8; ++width) {
            assertEquals(-1L, VPackBounds.readSigned(negativeOne[width - 1], 0, width));
            long expectedMinimum = width == 8
                    ? Long.MIN_VALUE : -(1L << (width * 8 - 1));
            long expectedMaximum = width == 8
                    ? Long.MAX_VALUE : (1L << (width * 8 - 1)) - 1L;
            assertEquals(expectedMinimum, VPackBounds.readSigned(minimum[width - 1], 0, width));
            assertEquals(expectedMaximum, VPackBounds.readSigned(maximum[width - 1], 0, width));
        }

        byte[][] unsignedMaximum = new byte[][] {
            VPackWireFixtureTest.hex("ff"),
            VPackWireFixtureTest.hex("ff ff"),
            VPackWireFixtureTest.hex("ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff ff ff ff"),
            VPackWireFixtureTest.hex("ff ff ff ff ff ff ff ff")
        };
        for (int width = 1; width <= 8; ++width) {
            BigInteger expected = BigInteger.ONE.shiftLeft(width * 8).subtract(BigInteger.ONE);
            assertEquals(expected,
                    VPackBounds.readUnsigned(unsignedMaximum[width - 1], 0, width));
        }
        assertEquals(BigInteger.ONE.shiftLeft(63), VPackBounds.readUnsigned(
                VPackWireFixtureTest.hex("00 00 00 00 00 00 00 80"), 0, 8));
    }

    @Test
    void structuralFieldsRemainRestrictedAndRejectUnsignedHighBit() {
        byte[] fields = VPackWireFixtureTest.hex(
                "ff 7f 00 80 ff ff ff ff 00 00 00 00 00 00 00 00 00 00 00 80");
        assertEquals(0xFFFFFFFFL, VPackBounds.readStructural(fields, 4, 4));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.readStructural(fields, 12, 8));
        assertThrows(StreamReadException.class,
                () -> VPackBounds.readStructural(new byte[8], 0, 3));
    }

    @Test
    void scalarWritersEnforceSequentialWidthFitBoundaries() {
        for (int width = 1; width <= 8; ++width) {
            final int fieldWidth = width;
            byte[] output = new byte[width];
            long minimum = width == 8 ? Long.MIN_VALUE : -(1L << (width * 8 - 1));
            long maximum = width == 8 ? Long.MAX_VALUE : (1L << (width * 8 - 1)) - 1L;
            VPackBounds.writeSigned(output, 0, width, minimum);
            assertEquals(minimum, VPackBounds.readSigned(output, 0, width));
            VPackBounds.writeSigned(output, 0, width, maximum);
            assertEquals(maximum, VPackBounds.readSigned(output, 0, width));
            if (width < 8) {
                assertThrows(StreamWriteException.class,
                        () -> VPackBounds.writeSigned(output, 0, fieldWidth, minimum - 1L));
                assertThrows(StreamWriteException.class,
                        () -> VPackBounds.writeSigned(output, 0, fieldWidth, maximum + 1L));
            }

            BigInteger unsignedMaximum = BigInteger.ONE.shiftLeft(width * 8).subtract(BigInteger.ONE);
            VPackBounds.writeUnsigned(output, 0, width, unsignedMaximum);
            assertEquals(unsignedMaximum, VPackBounds.readUnsigned(output, 0, width));
            assertThrows(StreamWriteException.class,
                    () -> VPackBounds.writeUnsigned(output, 0, fieldWidth,
                            unsignedMaximum.add(BigInteger.ONE)));
        }
    }

    @Test
    void rawLittleEndianWriterPreservesExactBits() {
        byte[] output = new byte[8];
        VPackBounds.writeBits(output, 0, 8, 0xFEDCBA9876543210L);
        assertArrayEquals(VPackWireFixtureTest.hex("10 32 54 76 98 ba dc fe"), output);
        VPackBounds.writeUnsigned(output, 0, 8, VPackBounds.UINT64_MAX);
        assertArrayEquals(VPackWireFixtureTest.hex("ff ff ff ff ff ff ff ff"), output);
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeUnsigned(output, 0, 2, BigInteger.valueOf(65536L)));
    }

    @Test
    void primitiveUnsignedWriterValidatesBeforeWritingRawBits() {
        byte[] output = new byte[8];
        VPackBounds.writeUnsigned(output, 0, 8, Long.MAX_VALUE);
        assertArrayEquals(VPackWireFixtureTest.hex("ff ff ff ff ff ff ff 7f"), output);
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeUnsigned(output, 0, 1, 256L));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeUnsigned(output, 0, 8, -1L));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeUnsigned(output, 0, 9, 0L));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.writeUnsigned(output, 1, 8, 1L));
    }

    @Test
    void unsignedLongConversionPreservesTheHighBitExactly() {
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE), VPackBounds.unsignedLong(Long.MAX_VALUE));
        assertEquals(BigInteger.ONE.shiftLeft(63), VPackBounds.unsignedLong(Long.MIN_VALUE));
        assertEquals(VPackBounds.UINT64_MAX, VPackBounds.unsignedLong(-1L));
        assertEquals(-1L, VPackBounds.requireUint64(VPackBounds.UINT64_MAX, "uint64"));
        assertThrows(StreamWriteException.class,
                () -> VPackBounds.requireUint64(VPackBounds.UINT64_MAX.add(BigInteger.ONE), "uint64"));
    }
}
