package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0055F1 {

    void asyncParserConfigurationHasAnExplicitVpackBoundary() throws Exception {
        VPackFactory factory = new VPackFactory();

        assertFalse(factory.canParseAsync());
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createNonBlockingByteArrayParser(ObjectReadContext.empty()));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createNonBlockingByteBufferParser(ObjectReadContext.empty()));

        // A regular binary parser has a concrete source; it is not an async
        // parser with the JSON test's null input-source contract.
        try (JsonParser parser = factory.createParser(new byte[] { 0x19 })) {
            assertNotNull(parser.streamReadInputSource());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
        }
    }
private static void assertDeferred(byte[] literal, Object expected,
            JsonParser.NumberType numberType, JsonToken token) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(literal)) {
            assertEquals(token, parser.nextToken());
            Object actual = parser.getNumberValueDeferred();
            assertEquals(expected, actual);
            assertEquals(expected.getClass(), actual.getClass());
            assertEquals(numberType, parser.getNumberType());
        }
    }

    void __invoke_asyncParserConfigurationHasAnExplicitVpackBoundary() throws Exception {
        try {
            asyncParserConfigurationHasAnExplicitVpackBoundary();
        } finally {
        }
    }

}
