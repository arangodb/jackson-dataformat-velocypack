package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamReadFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackParserLifecycleTest {
    @Test
    void chunkedAndZeroReadSourcesProgressAndCloseAccordingToEffectiveFlag() throws Exception {
        TrackingInput input = new TrackingInput(new byte[] { 0x41, 'a' });
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(
                new ObjectReadContext.Base() {
                    @Override public int getStreamReadFeatures(int defaults) {
                        return defaults & ~StreamReadFeature.AUTO_CLOSE_SOURCE.getMask();
                    }
                }, input)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("a", parser.getString());
            assertNull(parser.nextToken());
        }
        assertFalse(input.closed);

        TrackingInput auto = new TrackingInput(new byte[] { 0x18 });
        VPackParser parser = (VPackParser) new VPackFactory().createParser(auto);
        assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
        assertNull(parser.nextToken());
        assertTrue(auto.closed);
    }

    @Test
    void objectReadContextConstraintsAreUsedByParserConstruction() {
        ObjectReadContext context = new ObjectReadContext.Base() {
            @Override public StreamReadConstraints streamReadConstraints() {
                return StreamReadConstraints.builder().maxDocumentLength(1).build();
            }
        };
        org.junit.jupiter.api.Assertions.assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                () -> new VPackFactory().createParser(context, new byte[] { 0x18, 0x18 }));
    }

    private static final class TrackingInput extends ByteArrayInputStream {
        boolean closed;
        TrackingInput(byte[] data) { super(data); }
        @Override public int read(byte[] b, int off, int len) {
            if (len > 1) return 0;
            return super.read(b, off, len);
        }
        @Override public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
