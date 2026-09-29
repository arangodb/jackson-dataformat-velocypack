package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackNativeGeneratorTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void writesExplicitDateAndSpecialMarkersOnlyThroughNativeApis() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (VPackGenerator generator = (VPackGenerator) factory.createGenerator(output)) {
            generator.writeNumber(42);
            generator.writeVPackDate(-2L);
            generator.writeVPackSpecial(VPackSpecialValue.MIN_KEY);
            generator.writeEmbeddedObject(VPackSpecialValue.MAX_KEY);
        }
        assertArrayEquals(bytes(0x28, 0x2A, 0x1C, 0xFE, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
                0x1E, 0x1F), output.toByteArray());
    }

    @Test
    void embeddedObjectSupportsNullAndBinaryButRejectsOtherTypes() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (VPackGenerator generator = (VPackGenerator) factory.createGenerator(output)) {
            generator.writeEmbeddedObject(null);
            generator.writeEmbeddedObject(new byte[] { 1, 2 });
            generator.writeEmbeddedObject(new VPackDate(7L));
        }
        assertArrayEquals(bytes(0x18, 0xC0, 0x02, 0x01, 0x02, 0x1C, 7, 0, 0, 0, 0, 0, 0, 0),
                output.toByteArray());

        VPackGenerator unsupported = (VPackGenerator) factory.createGenerator(
                new ByteArrayOutputStream());
        assertThrows(StreamWriteException.class,
                () -> unsupported.writeEmbeddedObject(new Object()));
        assertThrows(RuntimeException.class, unsupported::close);

        VPackGenerator nullSpecial = (VPackGenerator) factory.createGenerator(
                new ByteArrayOutputStream());
        assertThrows(StreamWriteException.class, () -> nullSpecial.writeVPackSpecial(null));
        assertThrows(RuntimeException.class, nullSpecial::close);
    }

    private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }
}
