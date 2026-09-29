package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackTextNumberGeneratorTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void parsesTextExactlyWithoutFloatingPointRoundTrip() throws Exception {
        assertScalar(g -> g.writeNumber("10"), 0x28, 0x0A);
        assertScalar(g -> g.writeNumber("-0"), 0x30);
        assertScalar(g -> g.writeNumber("12345.0"),
                0xC8, 0x03, 0xFF, 0xFF, 0xFF, 0xFF, 0x12, 0x34, 0x50);
        assertScalar(g -> g.writeNumber("1e+2"),
                0xC8, 0x01, 0x02, 0, 0, 0, 0x01);
        assertScalar(g -> g.writeNumber("18446744073709551616"),
                0xC8, 0x0A, 0, 0, 0, 0,
                0x18, 0x44, 0x67, 0x44, 0x07, 0x37, 0x09, 0x55, 0x16, 0x16);
    }

    @Test
    void acceptsOnlyStrictJsonNumberGrammar() {
        for (String invalid : new String[] { "+1", "01", "1.", ".1", "1e", "1e+",
                "1  ", "NaN", "Infinity" }) {
            assertFailure(g -> g.writeNumber(invalid), StreamWriteException.class);
        }
    }

    @Test
    void boundsTextualDigitsBeforeExactConversion() {
        VPackFactory constrained = VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder().maxNumberDigits(3).build())
                .build();
        assertFailure(constrained, g -> g.writeNumber("12.34"), StreamConstraintsException.class);
    }

    private void assertScalar(WriterCall call, int... expected) throws Exception {
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
