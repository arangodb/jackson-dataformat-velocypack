package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadCapability;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0039F1 {
private static final byte[] EMPTY_OBJECT = { 0x0A };

    void vpackGeneratorPublishesAWriteContext() throws Exception {
        try (JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), new ByteArrayOutputStream())) {
            assertNotNull(generator.streamWriteContext());
            generator.writeNumber(1);
        }
    }
private static void assertDefaultSettings(JsonParser parser) throws Exception {
        try (parser) {
            assertFalse(parser.canReadObjectId());
            assertFalse(parser.canReadTypeId());
            assertFalse(parser.streamReadCapabilities()
                    .isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES));
            assertFalse(parser.streamReadCapabilities()
                    .isEnabled(StreamReadCapability.SCALARS_AS_OBJECTS));
            assertFalse(parser.streamReadCapabilities()
                    .isEnabled(StreamReadCapability.UNTYPED_SCALARS));
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
private static final class FailingVpackFactory extends VPackFactory {
        private FailingOutputStream lastStream;

        @Override
        protected OutputStream _fileOutputStream(File file) {
            return lastStream = new FailingOutputStream();
        }
    }
private static final class FailingOutputStream extends OutputStream {
        private boolean failWrites;
        private boolean closed;

        void startFailingWrites() {
            failWrites = true;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public void write(int b) throws IOException {
            if (failWrites) {
                throw new IOException("No writes!");
            }
        }
    }

    void __invoke_vpackGeneratorPublishesAWriteContext() throws Exception {
        try {
            vpackGeneratorPublishesAWriteContext();
        } finally {
        }
    }

}
