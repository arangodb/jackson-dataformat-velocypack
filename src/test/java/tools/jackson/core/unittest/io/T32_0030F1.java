package tools.jackson.core.unittest.io;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0030F1 {

    void flushAfterCloseIsIdempotent() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(output);
        generator.writeString("XY");
        generator.close();

        assertArrayEquals(new byte[] { 0x42, 'X', 'Y' }, output.toByteArray());
        int sizeAfterClose = output.size();
        assertDoesNotThrow(generator::flush);
        assertDoesNotThrow(generator::close);
        assertDoesNotThrow(generator::flush);
        assertEquals(sizeAfterClose, output.size());
    }

    void __invoke_flushAfterCloseIsIdempotent() throws Exception {
        try {
            flushAfterCloseIsIdempotent();
        } finally {
        }
    }

}
