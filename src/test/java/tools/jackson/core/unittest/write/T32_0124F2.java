package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0124F2 {

    void delegateCanWriteCommentsMatchesVpackGenerator() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            tools.jackson.core.util.JsonGeneratorDelegate delegate =
                    new tools.jackson.core.util.JsonGeneratorDelegate(generator);
            assertFalse(generator.canWriteComments());
            assertFalse(delegate.canWriteComments());
            assertEquals(generator.canWriteComments(), delegate.canWriteComments());
        }
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }
private static void assertStringValue(byte[] bytes, String expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), bytes)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(expected, parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_delegateCanWriteCommentsMatchesVpackGenerator() throws Exception {
        try {
            delegateCanWriteCommentsMatchesVpackGenerator();
        } finally {
        }
    }

}
