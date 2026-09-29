package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackGeneratorLifecycleTest {
    @Test
    void closeFinishesCompleteOpenRootsIteratively() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        for (int i = 0; i < 96; ++i) {
            generator.writeStartArray();
        }
        generator.writeNumber(1);

        assertEquals(1, generator.streamWriteOutputBuffered());
        assertDoesNotThrow(generator::close);
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertEquals(0x02, out.toByteArray()[0] & 0xFF);
        assertDoesNotThrow(generator::close);
    }

    @Test
    void closeEmitsACompleteRootButFlushDoesNot() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        generator.writeStartArray();
        generator.writeNumber(1);
        generator.flush();
        assertEquals(0, out.size());
        assertEquals(1, generator.streamWriteOutputBuffered());

        generator.close();
        assertArrayEquals(new byte[] { 0x02, 0x03, 0x31 }, out.toByteArray());
    }

    @Test
    void disabledAutoCloseDiscardsAnOpenRootAndCloseIsIdempotent() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) VPackFactory.builder()
                .disable(StreamWriteFeature.AUTO_CLOSE_CONTENT)
                .build().createGenerator(out);
        generator.writeStartArray();
        generator.writeNumber(1);

        assertThrows(StreamWriteException.class, generator::close);
        assertEquals(0, out.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertDoesNotThrow(generator::close);
    }

    @Test
    void danglingNamesAreErrorsEvenWhenAutoCloseContentIsEnabled() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        generator.writeStartObject();
        generator.writeName("dangling");

        assertThrows(StreamWriteException.class, generator::close);
        assertEquals(0, out.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertDoesNotThrow(generator::close);
    }

    @Test
    void appliesEffectiveStreamWriteDepthConstraints() {
        VPackFactory factory = VPackFactory.builder()
                .streamWriteConstraints(StreamWriteConstraints.builder()
                        .maxNestingDepth(1).build())
                .build();
        VPackGenerator generator = (VPackGenerator) factory.createGenerator(
                new ByteArrayOutputStream());
        generator.writeStartArray();
        assertThrows(StreamConstraintsException.class, generator::writeStartArray);
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertThrows(RuntimeException.class, generator::close);
        assertDoesNotThrow(generator::close);
    }
}
