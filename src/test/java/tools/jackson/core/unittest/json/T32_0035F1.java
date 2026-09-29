package tools.jackson.core.unittest.json;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0035F1 {
private final VPackFactory factory = new VPackFactory();

    void consecutiveNamesAreRejected() {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            generator.writeStartObject();
            generator.writeName("a");
            StreamWriteException failure = assertThrows(StreamWriteException.class,
                    () -> generator.writeName("b"));
            assertTrue(failure.getMessage().contains("expecting a value"));
        } finally {
            closeAfterFailure(generator);
        }
    }

    void propertyNameAtRootIsRejected() {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            StreamWriteException failure = assertThrows(StreamWriteException.class,
                    () -> generator.writeName("a"));
            assertTrue(failure.getMessage().contains("no object is open"));
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

    void __invoke_consecutiveNamesAreRejected() throws Exception {
        try {
            consecutiveNamesAreRejected();
        } finally {
        }
    }


    void __invoke_propertyNameAtRootIsRejected() throws Exception {
        try {
            propertyNameAtRootIsRejected();
        } finally {
        }
    }

}
