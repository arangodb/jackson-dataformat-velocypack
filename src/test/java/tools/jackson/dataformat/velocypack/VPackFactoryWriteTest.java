package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackFactoryWriteTest {
    @Test
    void createsGeneratorWithEffectiveContextFeaturesAndOutputStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackFactory factory = new VPackFactory();
        try (JsonGenerator generator = factory.createGenerator(ObjectWriteContext.empty(), out)) {
            generator.writeNull();
            generator.writeNumber(42);
        }
        assertArrayEquals(new byte[] { 0x18, 0x28, 0x2A }, out.toByteArray());
    }

    @Test
    void rejectsWriterTargetsAndKeepsBinaryOnlyFactoryBehavior() {
        assertThrows(UnsupportedOperationException.class,
                () -> new VPackFactory().createGenerator(new StringWriter()));
    }
}
