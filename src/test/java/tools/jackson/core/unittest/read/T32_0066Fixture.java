package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0066Fixture {
private static final byte[] ONE_ELEMENT_ARRAY = { 0x13, 0x04, 0x31, 0x01 };
private static final byte[] TRUE_OBJECT = {
            0x14, 0x0A, 0x45, 'v', 'a', 'l', 'u', 'e', 0x1A, 0x01
    };
private static final byte[] FOOBAR = { 0x46, 'f', 'o', 'o', 'b', 'a', 'r' };

    void eofAfterArray() throws Exception {
        try (JsonParser parser = dataInputParser(ONE_ELEMENT_ARRAY)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void eofAfterObject() throws Exception {
        try (JsonParser parser = dataInputParser(TRUE_OBJECT)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void eofAfterScalar() throws Exception {
        try (JsonParser parser = dataInputParser(FOOBAR)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("foobar", parser.getString());
            assertNull(parser.nextToken());
        }
    }
private static JsonParser dataInputParser(byte[] input) {
        DataInput dataInput = new DataInputStream(new ByteArrayInputStream(input));
        return new VPackFactory().createParser(ObjectReadContext.empty(), dataInput);
    }

    void __invoke_eofAfterArray() throws Exception {
        try {
            eofAfterArray();
        } finally {
        }
    }


    void __invoke_eofAfterObject() throws Exception {
        try {
            eofAfterObject();
        } finally {
        }
    }


    void __invoke_eofAfterScalar() throws Exception {
        try {
            eofAfterScalar();
        } finally {
        }
    }

}
