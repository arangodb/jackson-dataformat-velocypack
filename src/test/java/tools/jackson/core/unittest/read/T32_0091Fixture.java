package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0091Fixture {
private static final byte[] TWO_STRINGS = {
            0x02, 0x06, 0x41, 0x61, 0x41, 0x62
    };
private static final byte[] TRUE_FALSE_OBJECT = {
            0x0B, 0x0B, 0x02, 0x41, 0x61, 0x1A,
            0x41, 0x62, 0x19, 0x03, 0x06
    };

    void arrayBasicRetainsTokensAcrossBinarySources() throws Exception {
        VPackFactory factory = new VPackFactory();

        assertArray(factory.createParser(TWO_STRINGS));
        assertArray(factory.createParser(new ByteArrayInputStream(TWO_STRINGS)));
        assertArray(factory.createParser(new DataInputStream(
                new ByteArrayInputStream(TWO_STRINGS))));
    }

    void objectBasicRetainsTokensAcrossBinarySources() throws Exception {
        VPackFactory factory = new VPackFactory();

        assertObject(factory.createParser(TRUE_FALSE_OBJECT));
        assertObject(factory.createParser(new ByteArrayInputStream(TRUE_FALSE_OBJECT)));
        assertObject(factory.createParser(new DataInputStream(
                new ByteArrayInputStream(TRUE_FALSE_OBJECT))));
    }
private static void assertArray(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("a", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("b", parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertObject(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_arrayBasicRetainsTokensAcrossBinarySources() throws Exception {
        try {
            arrayBasicRetainsTokensAcrossBinarySources();
        } finally {
        }
    }


    void __invoke_objectBasicRetainsTokensAcrossBinarySources() throws Exception {
        try {
            objectBasicRetainsTokensAcrossBinarySources();
        } finally {
        }
    }

}
