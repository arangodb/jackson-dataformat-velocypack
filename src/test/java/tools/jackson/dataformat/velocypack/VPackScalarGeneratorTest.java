package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamConstraintsException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VPackScalarGeneratorTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void writesBooleanAndNullMarkersExactly() throws Exception {
        assertScalar(g -> g.writeNull(), 0x18);
        assertScalar(g -> g.writeBoolean(false), 0x19);
        assertScalar(g -> g.writeBoolean(true), 0x1A);
    }

    @Test
    void writesEverySignedWidthTransitionAsIndependentBytes() throws Exception {
        assertLong(-6L, 0x3A);
        assertLong(-1L, 0x3F);
        assertLong(-7L, 0x20, 0xF9);
        assertLong(-128L, 0x20, 0x80);
        assertLong(-129L, 0x21, 0x7F, 0xFF);
        assertLong(-32768L, 0x21, 0x00, 0x80);
        assertLong(-32769L, 0x22, 0xFF, 0x7F, 0xFF);
        assertLong(-8388608L, 0x22, 0x00, 0x00, 0x80);
        assertLong(-8388609L, 0x23, 0xFF, 0xFF, 0x7F, 0xFF);
        assertLong(Integer.MIN_VALUE, 0x23, 0x00, 0x00, 0x00, 0x80);
        assertLong((long) Integer.MIN_VALUE - 1L,
                0x24, 0xFF, 0xFF, 0xFF, 0x7F, 0xFF);
        assertLong(-(1L << 39), 0x24, 0, 0, 0, 0, 0x80);
        assertLong(-(1L << 39) - 1L, 0x25, 0xFF, 0xFF, 0xFF, 0xFF, 0x7F, 0xFF);
        assertLong(-(1L << 47), 0x25, 0, 0, 0, 0, 0, 0x80);
        assertLong(-(1L << 47) - 1L,
                0x26, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x7F, 0xFF);
        assertLong(-(1L << 55), 0x26, 0, 0, 0, 0, 0, 0, 0x80);
        assertLong(-(1L << 55) - 1L,
                0x27, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x7F, 0xFF);
        assertLong(Long.MIN_VALUE, 0x27, 0, 0, 0, 0, 0, 0, 0, 0x80);
    }

    @Test
    void writesEveryUnsignedWidthTransitionAsIndependentBytes() throws Exception {
        assertLong(0L, 0x30);
        assertLong(9L, 0x39);
        assertLong(10L, 0x28, 0x0A);
        assertLong(255L, 0x28, 0xFF);
        assertLong(256L, 0x29, 0, 1);
        assertLong(65535L, 0x29, 0xFF, 0xFF);
        assertLong(65536L, 0x2A, 0, 0, 1);
        assertLong(0xFFFFFFL, 0x2A, 0xFF, 0xFF, 0xFF);
        assertLong(0x1000000L, 0x2B, 0, 0, 0, 1);
        assertLong(0xFFFFFFFFL, 0x2B, 0xFF, 0xFF, 0xFF, 0xFF);
        assertLong(0x100000000L, 0x2C, 0, 0, 0, 0, 1);
        assertLong(1L << 40, 0x2D, 0, 0, 0, 0, 0, 1);
        assertLong((1L << 48) - 1L, 0x2D, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF);
        assertLong(1L << 48, 0x2E, 0, 0, 0, 0, 0, 0, 1);
        assertLong((1L << 56) - 1L, 0x2E, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF);
        assertLong(1L << 56, 0x2F, 0, 0, 0, 0, 0, 0, 0, 1);
        assertLong(Long.MAX_VALUE, 0x2F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x7F);
        assertBigInteger(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE),
                0x2F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF);
    }

    @Test
    void writesRawDoubleBitsAndWidensFloat() throws Exception {
        long bits = 0x7FF8_0000_0000_0042L;
        assertScalar(g -> g.writeNumber(Double.longBitsToDouble(bits)),
                0x1B, 0x42, 0, 0, 0, 0, 0, 0xF8, 0x7F);
        assertScalar(g -> g.writeNumber(-0.0f),
                0x1B, 0, 0, 0, 0, 0, 0, 0, 0x80);
    }

    @Test
    void writesConcatenatedScalarRootsWithoutSeparators() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(out)) {
            generator.writeNumber(1);
            generator.writeNumber(-7);
            generator.writeBoolean(true);
        }
        assertArrayEquals(bytes(0x31, 0x20, 0xF9, 0x1A), out.toByteArray());
    }

    @Test
    void rootScalarsPreserveWireBytesAcrossScalarTypesAndRepeatedRoots() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(out)) {
            generator.writeNull();
            generator.writeBoolean(false);
            generator.writeBoolean(true);
            for (int i = -6; i <= 9; ++i) generator.writeNumber(i);
            generator.writeNumber(BigInteger.valueOf(-6));
            generator.writeNumber(BigInteger.valueOf(9));
            generator.writeNumber(10);
            generator.writeNumber(-7);
            generator.writeNumber(1.5d);
            generator.writeString("x");
            generator.writeBinary(null, new byte[] { 1, 2 }, 0, 2);
            ((VPackGenerator) generator).writeVPackDate(1L);
            ((VPackGenerator) generator).writeVPackSpecial(VPackSpecialValue.MIN_KEY);
            ((VPackGenerator) generator).writeVPackSpecial(VPackSpecialValue.MAX_KEY);
            // Root scalars go straight to the stream, so arena copy accounting stays at zero.
            assertEquals(0L, ((VPackGenerator) generator).bytesCopied());
        }
        assertArrayEquals(bytes(0x18, 0x19, 0x1A,
                0x3A, 0x3B, 0x3C, 0x3D, 0x3E, 0x3F,
                0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
                0x3A, 0x39, 0x28, 0x0A, 0x20, 0xF9,
                0x1B, 0, 0, 0, 0, 0, 0, 0xF8, 0x3F,
                0x41, 0x78, 0xC0, 0x02, 0x01, 0x02,
                0x1C, 0x01, 0, 0, 0, 0, 0, 0, 0,
                0x1E, 0x1F), out.toByteArray());
    }

    @Test
    void directRootScalarBudgetFailureKeepsArenaConstraintDetails() {
        VPackFactory limited = VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder()
                        .maxRootValueBytes(8).build())
                .build();
        VPackGenerator generator = (VPackGenerator) limited.createGenerator(new ByteArrayOutputStream());
        StreamConstraintsException error = org.junit.jupiter.api.Assertions.assertThrows(
                StreamConstraintsException.class, () -> generator.writeNumber(1.5d));
        assertEquals("output arena: root byte budget exceeded (offset 0)\n"
                + " at [Source: UNKNOWN; byte offset: #0]", error.getMessage());
        try {
            generator.close();
        } catch (RuntimeException ignored) {
            // The generator retains its failed state after a constraint violation.
        }
    }

    private void assertScalar(WriterCall call, int... expected) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(out)) {
            call.write(generator);
        }
        assertArrayEquals(bytes(expected), out.toByteArray());
    }

    private void assertLong(long value, int... expected) throws Exception {
        assertScalar(g -> g.writeNumber(value), expected);
    }

    private void assertBigInteger(BigInteger value, int... expected) throws Exception {
        assertScalar(g -> g.writeNumber(value), expected);
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }

    @FunctionalInterface
    private interface WriterCall {
        void write(JsonGenerator generator) throws Exception;
    }
}
