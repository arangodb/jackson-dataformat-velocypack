package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0035F0 {
private final VPackFactory factory = new VPackFactory();

    void readerNullIsRejected() {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            generator.writeStartObject();
            generator.writeName("a");
            StreamWriteException failure = assertThrows(StreamWriteException.class,
                    () -> generator.writeString((StringReader) null, -1));
            assertTrue(failure.getMessage().contains("reader is null"));
        } finally {
            closeAfterFailure(generator);
        }
    }

    void readerStringCannotSupplyPropertyName() {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            generator.writeStartObject();
            StreamWriteException failure = assertThrows(StreamWriteException.class,
                    () -> generator.writeString(new StringReader("a"), -1));
            assertTrue(failure.getMessage().contains("expecting a property name/id"));
        } finally {
            closeAfterFailure(generator);
        }
    }
private static void closeAfterFailure(JsonGenerator generator) {
        try {
            generator.close();
        } catch (RuntimeException ignored) {
            // A failed generator may retain and report its primary failure on close().
        }
    }

    void __invoke_readerNullIsRejected() throws Exception {
        try {
            readerNullIsRejected();
        } finally {
        }
    }


    void __invoke_readerStringCannotSupplyPropertyName() throws Exception {
        try {
            readerStringCannotSupplyPropertyName();
        } finally {
        }
    }

}
