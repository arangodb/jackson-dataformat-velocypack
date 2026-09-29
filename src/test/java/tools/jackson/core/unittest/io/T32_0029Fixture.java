package tools.jackson.core.unittest.io;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T32_0029Fixture {

    void allocations() {
        IOContext ctxt = new IOContext(StreamReadConstraints.defaults(),
                StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                new BufferRecycler(),
                ContentReference.rawReference("N/A"), true,
                JsonEncoding.UTF8);

        byte[] read = ctxt.allocReadIOBuffer();
        assertNotNull(read);
        IllegalStateException readAgain = assertThrows(IllegalStateException.class,
                ctxt::allocReadIOBuffer);
        assertTrue(readAgain.getMessage().contains("second time"));
        IllegalArgumentException readSmall = assertThrows(IllegalArgumentException.class,
                () -> ctxt.releaseReadIOBuffer(new byte[1]));
        assertTrue(readSmall.getMessage().contains("smaller than original"));
        ctxt.releaseReadIOBuffer(null);
        ctxt.releaseReadIOBuffer(read);

        byte[] write = ctxt.allocWriteEncodingBuffer();
        assertNotNull(write);
        IllegalStateException writeAgain = assertThrows(IllegalStateException.class,
                ctxt::allocWriteEncodingBuffer);
        assertTrue(writeAgain.getMessage().contains("second time"));
        IllegalArgumentException writeSmall = assertThrows(IllegalArgumentException.class,
                () -> ctxt.releaseWriteEncodingBuffer(new byte[1]));
        assertTrue(writeSmall.getMessage().contains("smaller than original"));
        ctxt.releaseWriteEncodingBuffer(null);
        ctxt.releaseWriteEncodingBuffer(write);

        char[] token = ctxt.allocTokenBuffer();
        assertNotNull(token);
        IllegalStateException tokenAgain = assertThrows(IllegalStateException.class,
                ctxt::allocTokenBuffer);
        assertTrue(tokenAgain.getMessage().contains("second time"));
        IllegalArgumentException tokenSmall = assertThrows(IllegalArgumentException.class,
                () -> ctxt.releaseTokenBuffer(new char[1]));
        assertTrue(tokenSmall.getMessage().contains("smaller than original"));
        ctxt.releaseTokenBuffer(null);
        ctxt.releaseTokenBuffer(token);

        char[] concat = ctxt.allocConcatBuffer();
        assertNotNull(concat);
        IllegalStateException concatAgain = assertThrows(IllegalStateException.class,
                ctxt::allocConcatBuffer);
        assertTrue(concatAgain.getMessage().contains("second time"));
        IllegalArgumentException concatSmall = assertThrows(IllegalArgumentException.class,
                () -> ctxt.releaseConcatBuffer(new char[1]));
        assertTrue(concatSmall.getMessage().contains("smaller than original"));
        ctxt.releaseConcatBuffer(null);
        ctxt.releaseConcatBuffer(concat);

        char[] nameCopy = ctxt.allocNameCopyBuffer(100);
        assertNotNull(nameCopy);
        IllegalStateException nameCopyAgain = assertThrows(IllegalStateException.class,
                () -> ctxt.allocNameCopyBuffer(100));
        assertTrue(nameCopyAgain.getMessage().contains("second time"));
        IllegalArgumentException nameCopySmall = assertThrows(IllegalArgumentException.class,
                () -> ctxt.releaseNameCopyBuffer(new char[1]));
        assertTrue(nameCopySmall.getMessage().contains("smaller than original"));
        ctxt.releaseNameCopyBuffer(null);
        ctxt.releaseNameCopyBuffer(nameCopy);
        ctxt.close();
    }

    void __invoke_allocations() throws Exception {
        try {
            allocations();
        } finally {
        }
    }

}
