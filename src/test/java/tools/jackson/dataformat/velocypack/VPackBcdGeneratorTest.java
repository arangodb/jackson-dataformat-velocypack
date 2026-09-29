package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackBcdGeneratorTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void writesExactBcdBytesAndPreservesScale() throws Exception {
        assertScalar(g -> g.writeNumber(new BigDecimal("12345")),
                0xC8, 0x03, 0, 0, 0, 0, 0x01, 0x23, 0x45);
        assertScalar(g -> g.writeNumber(new BigDecimal("12345.0")),
                0xC8, 0x03, 0xFF, 0xFF, 0xFF, 0xFF, 0x12, 0x34, 0x50);
        assertScalar(g -> g.writeNumber(new BigDecimal("0.00")),
                0xC8, 0x01, 0xFE, 0xFF, 0xFF, 0xFF, 0x00);
    }

    @Test
    void promotesBigIntegerBeyondUnsigned64ToExactBcd() throws Exception {
        assertScalar(g -> g.writeNumber(BigInteger.ONE.shiftLeft(64)),
                0xC8, 0x0A, 0, 0, 0, 0,
                0x18, 0x44, 0x67, 0x44, 0x07, 0x37, 0x09, 0x55, 0x16, 0x16);
        assertScalar(g -> g.writeNumber(BigInteger.ONE.shiftLeft(64).negate()),
                0xD0, 0x0A, 0, 0, 0, 0,
                0x18, 0x44, 0x67, 0x44, 0x07, 0x37, 0x09, 0x55, 0x16, 0x16);
    }

    @Test
    void rejectsUnrepresentableScaleAndBcdDigitBudgetBeforeOutput() {
        BigDecimal extreme = new BigDecimal(BigInteger.ONE, Integer.MIN_VALUE);
        assertFailure(g -> g.writeNumber(extreme), StreamWriteException.class);

        VPackFactory constrained = VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder().maxNumberDigits(3).build())
                .build();
        assertFailure(constrained, g -> g.writeNumber(new BigDecimal("1234.0")),
                StreamConstraintsException.class);

        VPackFactory byteConstrained = VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder().maxRootValueBytes(7).build())
                .build();
        assertFailure(byteConstrained, g -> g.writeNumber(new BigDecimal("12345")),
                StreamConstraintsException.class);
    }

    private void assertScalar(WriterCall call, int... expected) throws Exception {
        assertScalar(factory, call, expected);
    }

    private static void assertScalar(VPackFactory factory, WriterCall call, int... expected)
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            call.write(generator);
        }
        assertArrayEquals(bytes(expected), output.toByteArray());
    }

    private static void assertFailure(WriterCall call,
            Class<? extends Throwable> failureType) {
        assertFailure(new VPackFactory(), call, failureType);
    }

    private static void assertFailure(VPackFactory factory, WriterCall call,
            Class<? extends Throwable> failureType) {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        assertThrows(failureType, () -> call.write(generator));
        assertThrows(RuntimeException.class, generator::close);
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
