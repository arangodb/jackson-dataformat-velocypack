package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VPackGeneratorSegmentOwnershipTest {
    @Test
    void flushKeepsOpenRootBufferedAndCompletedRootIsReset() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        generator.writeStartArray();
        generator.writeNumber(1);
        generator.flush();
        assertEquals(0, out.size());
        assertEquals(1, generator.streamWriteOutputBuffered());
        generator.writeEndArray();
        assertEquals(3, out.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        generator.close();
    }

    @Test
    void childChainsAreTransferredInLogicalOrder() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartArray();
            generator.writeNumber(1);
            generator.writeStartArray();
            generator.writeNumber(2);
            generator.writeEndArray();
            generator.writeEndArray();
        }
        // Independent literal form: outer indexed array containing 31 and 02 03 32.
        assertEquals(9, out.size());
        byte[] bytes = out.toByteArray();
        assertEquals(0x06, bytes[0] & 0xFF);
        assertEquals(0x09, bytes[1] & 0xFF);
        assertEquals(0x02, bytes[2] & 0xFF);
        assertEquals(0x31, bytes[3] & 0xFF);
        assertEquals(0x02, bytes[4] & 0xFF);
        assertEquals(0x03, bytes[5] & 0xFF);
        assertEquals(0x32, bytes[6] & 0xFF);
        assertEquals(0x03, bytes[7] & 0xFF);
        assertEquals(0x04, bytes[8] & 0xFF);
    }
}
