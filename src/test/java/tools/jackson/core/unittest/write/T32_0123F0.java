package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.io.SerializedString;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0123F0 {

    void rawSerializedStringOutputIsExplicitlyUnsupported() throws Exception {
        JsonGenerator generator = generator(new ByteArrayOutputStream());
        try {
            generator.writeStartArray();
            assertThrows(UnsupportedOperationException.class,
                    () -> generator.writeRawValue(new SerializedString("\"foo\"")));
            assertThrows(UnsupportedOperationException.class,
                    () -> generator.writeRaw(new SerializedString(", false")));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
                // Failed generators retain their primary unsupported-operation failure.
            }
        }
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }

    void __invoke_rawSerializedStringOutputIsExplicitlyUnsupported() throws Exception {
        try {
            rawSerializedStringOutputIsExplicitlyUnsupported();
        } finally {
        }
    }

}
