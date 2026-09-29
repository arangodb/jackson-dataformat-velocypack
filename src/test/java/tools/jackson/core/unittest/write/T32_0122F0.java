package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0122F0 {

    void forwardSlashIsLiteralBinaryData() throws Exception {
        byte[] expected = bytes(
                0x0B, 0x1B, 0x01, 0x43, 'u', 'r', 'l', 0x52,
                'h', 't', 't', 'p', ':', '/', '/', 'e', 'x', 'a', 'm', 'p', 'l', 'e', '.', 'c', 'o', 'm',
                0x03);

        for (int mode = 0; mode < 2; ++mode) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            JsonGenerator generator = mode == 0
                    ? new VPackFactory().createGenerator(output)
                    : new VPackFactory().createGenerator(ObjectWriteContext.empty(),
                            (java.io.DataOutput) new DataOutputStream(output));
            generator.writeStartObject();
            generator.writeStringProperty("url", "http://example.com");
            generator.writeEndObject();
            generator.close();
            assertArrayEquals(expected, output.toByteArray());
        }

        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("url", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("http://example.com", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertInteger(JsonParser parser, String name, long value)
            throws Exception {
        assertProperty(parser, name);
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(value, parser.getLongValue());
    }
private static void assertProperty(JsonParser parser, String name) throws Exception {
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(name, parser.getString());
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }

    void __invoke_forwardSlashIsLiteralBinaryData() throws Exception {
        try {
            forwardSlashIsLiteralBinaryData();
        } finally {
        }
    }

}
