package tools.jackson.core.unittest.json;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JacksonException;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadCapability;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0039F2 {
private static final byte[] EMPTY_OBJECT = { 0x0A };

    void fileGeneratorReportsWriteFailureAndClosesOwnedOutput() throws Exception {
        FailingVpackFactory factory = new FailingVpackFactory();
        JsonGenerator generator = factory.createGenerator(ObjectWriteContext.empty(),
                new File("/tmp/t32-0039-test.vpack"), JsonEncoding.UTF8);
        try {
            // Keep a literal buffered container open so close() owns the
            // failing write.
            generator.writeStartArray();
            generator.writeString("foo");
            factory.lastStream.startFailingWrites();
            JacksonException failure = assertThrows(JacksonException.class,
                    generator::close);
            assertTrue(failure.getMessage().contains("No writes"));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
                // The primary write failure is asserted above.
            }
        }
        assertNotNull(factory.lastStream);
        assertTrue(factory.lastStream.closed);
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

    void __invoke_fileGeneratorReportsWriteFailureAndClosesOwnedOutput() throws Exception {
        try {
            fileGeneratorReportsWriteFailureAndClosesOwnedOutput();
        } finally {
        }
    }

}
