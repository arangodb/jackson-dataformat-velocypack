package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.io.SerializedString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0123F1 {

    void serializedStringNamesProduceLiteralVpackNames() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartArray();

            generator.writeStartObject();
            generator.writeName(new SerializedString("\"name\""));
            generator.writeString("a");
            generator.writeName(new SerializedString("Pöllö"));
            generator.writeString("b");
            generator.writeEndObject();

            generator.writeStartObject();
            generator.writeName(new SerializedString("Pöllö"));
            generator.writeString("c");
            generator.writeName(new SerializedString("\"name\""));
            generator.writeString("d");
            generator.writeEndObject();

            generator.writeEndArray();
        }

        byte[] expected = VPackWireFixtureTest.hex(
                "02 32 "
                + "0b 18 02 46 22 6e 61 6d 65 22 41 61 "
                + "47 50 c3 b6 6c 6c c3 b6 41 62 03 0c "
                + "0b 18 02 47 50 c3 b6 6c 6c c3 b6 41 63 "
                + "46 22 6e 61 6d 65 22 41 64 0d 03");
        assertArrayEquals(expected, output.toByteArray());

        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("\"name\"", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("a", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Pöllö", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("b", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());

            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Pöllö", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("c", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("\"name\"", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("d", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void serializedStringValuesProduceLiteralVpackValues() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            generator.writeStartArray();

            generator.writeStartObject();
            generator.writeName(new SerializedString("\"name\""));
            generator.writeString(new SerializedString("\"Value\""));
            generator.writeName(new SerializedString("Pöllö"));
            generator.writeString("long");
            generator.writeEndObject();

            generator.writeStartObject();
            generator.writeName(new SerializedString("Pöllö"));
            generator.writeString("\"Value\"");
            generator.writeName(new SerializedString("\"name\""));
            generator.writeString(new SerializedString("long"));
            generator.writeEndObject();

            generator.writeEndArray();
        }

        byte[] expected = VPackWireFixtureTest.hex(
                "02 44 "
                + "0b 21 02 46 22 6e 61 6d 65 22 47 22 56 61 6c 75 65 22 "
                + "47 50 c3 b6 6c 6c c3 b6 44 6c 6f 6e 67 03 12 "
                + "0b 21 02 47 50 c3 b6 6c 6c c3 b6 47 22 56 61 6c 75 65 22 "
                + "46 22 6e 61 6d 65 22 44 6c 6f 6e 67 13 03");
        assertArrayEquals(expected, output.toByteArray());

        try (JsonParser parser = new VPackFactory().createParser(expected)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("\"name\"", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("\"Value\"", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Pöllö", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("long", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());

            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("Pöllö", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("\"Value\"", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("\"name\"", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("long", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }

    void __invoke_serializedStringNamesProduceLiteralVpackNames() throws Exception {
        try {
            serializedStringNamesProduceLiteralVpackNames();
        } finally {
        }
    }


    void __invoke_serializedStringValuesProduceLiteralVpackValues() throws Exception {
        try {
            serializedStringValuesProduceLiteralVpackValues();
        } finally {
        }
    }

}
