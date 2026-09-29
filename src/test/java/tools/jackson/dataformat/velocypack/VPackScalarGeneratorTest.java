package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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
