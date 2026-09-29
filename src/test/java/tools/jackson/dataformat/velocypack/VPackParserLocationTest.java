package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VPackParserLocationTest {
    @Test
    void reportsLogicalTokenAndNextUnreadOffsetsForSliceAndConcatenatedRoots() throws Exception {
        byte[] source = { 0x55, 0x66, 0x41, 'a', 0x18, 0x1A };
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(source, 2, 4)) {
            assertEquals(-1L, parser.currentTokenLocation().getByteOffset());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(0L, parser.currentTokenLocation().getByteOffset());
            assertEquals(2L, parser.currentLocation().getByteOffset());
            assertEquals("a", parser.getString());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(2L, parser.currentTokenLocation().getByteOffset());
            assertEquals(3L, parser.currentLocation().getByteOffset());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(3L, parser.currentTokenLocation().getByteOffset());
            assertEquals(4L, parser.currentLocation().getByteOffset());
            assertNull(parser.nextToken());
        }
    }

    @Test
    void sourceReferenceFollowsIncludeSourceInLocation() throws Exception {
        byte[] source = { 0x18 };
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(source)) {
            parser.nextToken();
            assertNull(parser.currentTokenLocation().contentReference().getRawContent());
        }
        ObjectReadContext context = new ObjectReadContext.Base() {
            @Override public int getStreamReadFeatures(int defaults) {
                return defaults | StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.getMask();
            }
        };
        try (VPackParser parser = (VPackParser) new VPackFactory()
                .createParser(context, new ByteArrayInputStream(source))) {
            parser.nextToken();
            assertEquals(ByteArrayInputStream.class,
                    parser.currentTokenLocation().contentReference().getRawContent().getClass());
        }
    }
}
