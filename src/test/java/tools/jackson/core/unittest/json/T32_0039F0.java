package tools.jackson.core.unittest.json;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadCapability;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import tools.jackson.dataformat.velocypack.*;

class T32_0039F0 {
private static final byte[] EMPTY_OBJECT = { 0x0A };

    void defaultVpackParserSettingsRemainWithoutNativeIdsOrUntypedCapabilities()
            throws Exception {
        VPackFactory factory = new VPackFactory();
        assertDefaultSettings(factory.createParser(ObjectReadContext.empty(), EMPTY_OBJECT));
        assertDefaultSettings(factory.createParser(ObjectReadContext.empty(),
                new ByteArrayInputStream(EMPTY_OBJECT)));
        DataInput input = new DataInputStream(new ByteArrayInputStream(EMPTY_OBJECT));
        assertDefaultSettings(factory.createParser(ObjectReadContext.empty(), input));
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

    void __invoke_defaultVpackParserSettingsRemainWithoutNativeIdsOrUntypedCapabilities() throws Exception {
        try {
            defaultVpackParserSettingsRemainWithoutNativeIdsOrUntypedCapabilities();
        } finally {
        }
    }

}
