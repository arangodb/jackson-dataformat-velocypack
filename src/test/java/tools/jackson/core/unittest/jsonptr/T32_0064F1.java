package tools.jackson.core.unittest.jsonptr;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0064F1 {
private static final JsonPointer EMPTY_PTR = JsonPointer.empty();

    void testViaGenerator() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            assertSame(EMPTY_PTR, generator.streamWriteContext().pathAsPointer());

            generator.writeStartArray();
            assertSame(EMPTY_PTR, generator.streamWriteContext().pathAsPointer());
            generator.writeBoolean(true);
            assertEquals("/0", generator.streamWriteContext().pathAsPointer().toString());

            generator.writeStartObject();
            assertEquals("/1", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeName("x");
            assertEquals("/1/x", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeString("foo");
            assertEquals("/1/x", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeName("stats");
            assertEquals("/1/stats", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeStartObject();
            assertEquals("/1/stats", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeName("rate");
            assertEquals("/1/stats/rate", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeNumber(13);
            assertEquals("/1/stats/rate", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeEndObject();
            assertEquals("/1/stats", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeEndObject();
            assertEquals("/1", generator.streamWriteContext().pathAsPointer().toString());
            generator.writeEndArray();
            assertSame(EMPTY_PTR, generator.streamWriteContext().pathAsPointer());
        }
        assertTrue(output.size() > 0);
    }

    void testParserWithRoot() throws Exception {
        // Independent literal roots: {"a":1,"b":3}, {"a":5,"c":[1,2]}, [1,2].
        byte[] input = {
                0x14, 0x09, 0x41, 'a', 0x31, 0x41, 'b', 0x33, 0x02,
                0x14, 0x0D, 0x41, 'a', 0x35, 0x41, 'c',
                0x13, 0x05, 0x31, 0x32, 0x02, 0x02,
                0x02, 0x04, 0x31, 0x32
        };

        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertSame(EMPTY_PTR, parser.streamReadContext().pathAsPointer(true));
            assertTokenAndPath(parser, JsonToken.START_OBJECT, "/0");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/0/a");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/0/a");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/0/b");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/0/b");
            assertTokenAndPath(parser, JsonToken.END_OBJECT, "/0");

            assertTokenAndPath(parser, JsonToken.START_OBJECT, "/1");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/1/a");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/1/a");
            assertTokenAndPath(parser, JsonToken.PROPERTY_NAME, "/1/c");
            assertTokenAndPath(parser, JsonToken.START_ARRAY, "/1/c");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/1/c/0");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/1/c/1");
            assertTokenAndPath(parser, JsonToken.END_ARRAY, "/1/c");
            assertTokenAndPath(parser, JsonToken.END_OBJECT, "/1");

            assertTokenAndPath(parser, JsonToken.START_ARRAY, "/2");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/2/0");
            assertTokenAndPath(parser, JsonToken.VALUE_NUMBER_INT, "/2/1");
            assertTokenAndPath(parser, JsonToken.END_ARRAY, "/2");
            assertNull(parser.nextToken());
            assertEquals("/2", parser.streamReadContext().pathAsPointer(true).toString());
        }
    }

    void testGeneratorWithRoot() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            assertSame(EMPTY_PTR, generator.streamWriteContext().pathAsPointer(true));

            generator.writeStartArray();
            assertEquals("/0", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeBoolean(true);
            assertEquals("/0/0", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeStartObject();
            assertEquals("/0/1", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeName("x");
            assertEquals("/0/1/x", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeString("foo");
            assertEquals("/0/1/x", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeEndObject();
            assertEquals("/0/1", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeEndArray();
            assertEquals("/0", generator.streamWriteContext().pathAsPointer(true).toString());

            generator.writeBoolean(true);
            assertEquals("/1", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeStartArray();
            assertEquals("/2", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeString("foo");
            assertEquals("/2/0", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeString("bar");
            assertEquals("/2/1", generator.streamWriteContext().pathAsPointer(true).toString());
            generator.writeEndArray();
            assertEquals("/2", generator.streamWriteContext().pathAsPointer(true).toString());
            assertEquals("/2", generator.streamWriteContext().pathAsPointer(true).toString());
        }
        assertTrue(output.size() > 0);
    }
private static void assertTokenAndPath(JsonParser parser, JsonToken token,
            String path) throws Exception {
        assertEquals(token, parser.nextToken());
        assertEquals(path, parser.streamReadContext().pathAsPointer(true).toString());
    }

    void __invoke_testViaGenerator() throws Exception {
        try {
            testViaGenerator();
        } finally {
        }
    }


    void __invoke_testParserWithRoot() throws Exception {
        try {
            testParserWithRoot();
        } finally {
        }
    }


    void __invoke_testGeneratorWithRoot() throws Exception {
        try {
            testGeneratorWithRoot();
        } finally {
        }
    }

}
