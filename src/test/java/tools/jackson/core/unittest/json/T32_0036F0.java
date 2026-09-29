package tools.jackson.core.unittest.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import tools.jackson.core.FormatSchema;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0036F0 {

    void fileInputFailureIsReportedAndOwnedStreamIsClosed() throws Exception {
        FailingFileFactory factory = new FailingFileFactory();
        JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                new File("/tmp/t32-0036-test.json"));
        try {
            StreamReadException failure = assertThrows(StreamReadException.class,
                    parser::nextToken);
            assertTrue(failure.getMessage().contains("Will not read"));
        } finally {
            parser.close();
        }
        assertTrue(factory.lastStream != null);
        assertTrue(factory.lastStream.closed);
    }
private static final class BogusSchema implements FormatSchema {
        @Override
        public String getSchemaType() {
            return "test";
        }
    }
private static final class FailingFileFactory extends VPackFactory {
        private FailingInputStream lastStream;

        @Override
        protected InputStream _fileInputStream(File file) {
            return lastStream = new FailingInputStream();
        }
    }
private static final class FailingInputStream extends InputStream {
        private boolean closed;

        @Override
        public int read() throws IOException {
            throw new IOException("Will not read");
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    void __invoke_fileInputFailureIsReportedAndOwnedStreamIsClosed() throws Exception {
        try {
            fileInputFailureIsReportedAndOwnedStreamIsClosed();
        } finally {
        }
    }

}
