package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.SerializableString;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.util.JsonParserSequence;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0637Fixture {
private final VPackFactory factory = new VPackFactory();
private final ObjectMapper mapper = new VPackMapper();

    void simpleWritesReadIndependentLiteralRootValues() throws Exception {
        // Independent VPack roots: "abc" and integer 13.
        byte[] literal = { 0x43, 'a', 'b', 'c', 0x28, 0x0D };
        try (JsonParser parser = factory.createParser(literal)) {
            assertNull(parser.currentToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("abc", parser.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(13, parser.getIntValue());
            assertNull(parser.nextToken());
        }

        // Preserve the source test's TokenBuffer write path, then check the
        // resulting VPack bytes against the independent scalar literals above.
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeString("abc");
        buffer.writeNumber(13);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser source = buffer.asParser();
                JsonGenerator generator = factory.createGenerator(output)) {
            while (source.nextToken() != null) generator.copyCurrentEvent(source);
        }
        assertArrayEquals(literal, output.toByteArray());
        buffer.close();
    }

    void simpleObjectRetainsEmptyAndNumericPropertySemantics() throws Exception {
        // Independent literal empty object and {"num":1.25}.
        try (JsonParser parser = factory.createParser(new byte[] { 0x0A })) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }

        TokenBuffer empty = TokenBuffer.forGeneration();
        empty.writeStartObject();
        empty.writeEndObject();
        ByteArrayOutputStream emptyOutput = new ByteArrayOutputStream();
        try (JsonParser source = empty.asParser();
                JsonGenerator generator = factory.createGenerator(emptyOutput)) {
            while (source.nextToken() != null) generator.copyCurrentEvent(source);
        }
        assertArrayEquals(new byte[] { 0x0A }, emptyOutput.toByteArray());
        empty.close();

        TokenBuffer populated = TokenBuffer.forGeneration();
        populated.writeStartObject();
        populated.writeNumberProperty("num", 1.25);
        populated.writeEndObject();
        ByteArrayOutputStream populatedOutput = new ByteArrayOutputStream();
        try (JsonParser source = populated.asParser();
                JsonGenerator generator = factory.createGenerator(populatedOutput)) {
            while (source.nextToken() != null) generator.copyCurrentEvent(source);
        }
        assertArrayEquals(new byte[] { 0x0B, 0x11, 0x01, 0x43, 'n', 'u', 'm',
                0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xF4, 0x3F, 0x03 },
                populatedOutput.toByteArray());
        populated.close();

        byte[] object = { 0x14, 0x10, 0x43, 'n', 'u', 'm',
                0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xF4, 0x3F, 0x01 };
        try (JsonParser parser = factory.createParser(object)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertNull(parser.currentName());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("num", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(1.25, parser.getDoubleValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.currentName());
            assertNull(parser.nextToken());
        }
    }

    void vpackSampleDocumentRetainsPortableJsonStructure() throws Exception {
        // A deterministic typed equivalent of the upstream JSON sample: an object,
        // nested array/object, booleans, null, integer, and floating point value.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartObject();
            generator.writeName("number");
            generator.writeNumber(1);
            generator.writeName("nested");
            generator.writeStartArray();
            generator.writeBoolean(true);
            generator.writeNull();
            generator.writeStartObject();
            generator.writeName("value");
            generator.writeNumber(1.5);
            generator.writeEndObject();
            generator.writeEndArray();
            generator.writeEndObject();
        }
        TokenBuffer buffer = TokenBuffer.forGeneration();
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            while (parser.nextToken() != null) buffer.copyCurrentEvent(parser);
        }
        try (JsonParser parser = buffer.asParser()) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("number", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("nested", parser.currentName());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(1.5, parser.getDoubleValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
        buffer.close();
    }

    void tokenBufferDescriptionTruncatesAfterManyVpackTokens() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartArray();
            for (int i = 0; i < 110; ++i) generator.writeNumber(i);
            generator.writeEndArray();
        }
        TokenBuffer buffer = TokenBuffer.forGeneration();
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            while (parser.nextToken() != null) buffer.copyCurrentEvent(parser);
        }
        String description = buffer.toString();
        assertNotNull(description);
        assertTrue(description.contains("... (truncated"));
        buffer.close();
    }

    void parserSequenceContinuesAcrossIndependentVpackSources() throws Exception {
        // ["test"] followed by [true,null], independently encoded VPack roots.
        byte[] first = { 0x02, 0x07, 0x44, 't', 'e', 's', 't' };
        byte[] second = { 0x02, 0x04, 0x1A, 0x18 };
        TokenBuffer buffer = TokenBuffer.forGeneration();
        try (JsonParser source = factory.createParser(first)) {
            while (source.nextToken() != null) buffer.copyCurrentEvent(source);
        }
        try (JsonParser p1 = buffer.asParser();
                JsonParser p2 = factory.createParser(second);
                JsonParserSequence sequence = JsonParserSequence.createFlattened(false, p1, p2)) {
            assertEquals(2, sequence.containedParsersCount());
            assertEquals(JsonToken.START_ARRAY, sequence.nextToken());
            assertEquals(JsonToken.VALUE_STRING, sequence.nextToken());
            assertEquals("test", sequence.getString());
            assertEquals(JsonToken.END_ARRAY, sequence.nextToken());
            assertEquals(JsonToken.START_ARRAY, sequence.nextToken());
            assertEquals(JsonToken.VALUE_TRUE, sequence.nextToken());
            assertEquals(JsonToken.VALUE_NULL, sequence.nextToken());
            assertEquals(JsonToken.END_ARRAY, sequence.nextToken());
            assertNull(sequence.nextToken());
        }
        buffer.close();
    }

    void tokenBuffersInteroperateWithNestedVpackParserSequences() throws Exception {
        TokenBuffer arrayStart = TokenBuffer.forGeneration();
        arrayStart.writeStartArray();
        TokenBuffer stringValue = TokenBuffer.forGeneration();
        stringValue.writeString("a");
        JsonParser integerValue = factory.createParser(new byte[] { 0x28, 0x0D });
        TokenBuffer arrayEnd = TokenBuffer.forGeneration();
        arrayEnd.writeEndArray();

        try (JsonParserSequence first = JsonParserSequence.createFlattened(false,
                    arrayStart.asParser(), stringValue.asParser());
                JsonParserSequence second = JsonParserSequence.createFlattened(false,
                    integerValue, arrayEnd.asParser());
                JsonParserSequence combined = JsonParserSequence.createFlattened(false,
                    first, second)) {
            assertEquals(4, combined.containedParsersCount());
            assertEquals(JsonToken.START_ARRAY, combined.nextToken());
            assertEquals(JsonToken.VALUE_STRING, combined.nextToken());
            assertEquals("a", combined.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, combined.nextToken());
            assertEquals(13, combined.getIntValue());
            assertEquals(JsonToken.END_ARRAY, combined.nextToken());
            assertNull(combined.nextToken());
        }
        arrayStart.close();
        stringValue.close();
        arrayEnd.close();
    }

    void uuidWrittenToTokenBufferRemainsAStringAndReadsBack() throws Exception {
        UUID uuid = UUID.fromString("76e6d183-5f68-4afa-b94a-922c1fdb83f8");
        TokenBuffer buffer = TokenBuffer.forGeneration();
        mapper.writeValue(buffer, uuid);
        buffer.close();

        try (JsonParser parser = buffer.asParser()) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(uuid.toString(), parser.getString());
            assertNull(parser.nextToken());
        }
        try (JsonParser parser = buffer.asParser()) {
            assertEquals(uuid.toString(), mapper.readValue(parser, UUID.class).toString());
        }
    }

    void nullStringOverloadsWriteNullToken() throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        SerializableString nullString = null;
        buffer.writeString((String) null);
        buffer.writeString(nullString);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser source = buffer.asParser();
                JsonGenerator generator = factory.createGenerator(output)) {
            while (source.nextToken() != null) generator.copyCurrentEvent(source);
        }
        try (JsonParser parser = buffer.asParser()) {
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertNull(parser.nextToken());
        }
        assertArrayEquals(new byte[] { 0x18, 0x18 }, output.toByteArray());
        buffer.close();
    }

    void pojoNullWritesNullToken() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writePOJO(null);
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void rawValueOffsetOverloadsAreExplicitlyUnsupportedForVpack() throws Exception {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        assertThrows(UnsupportedOperationException.class,
                () -> generator.writeRawValue("xxxABCyyy", 3, 3));
        assertThrows(StreamWriteException.class, generator::close);
        JsonGenerator chars = factory.createGenerator(new ByteArrayOutputStream());
        assertThrows(UnsupportedOperationException.class,
                () -> chars.writeRawValue("Hello World!".toCharArray(), 6, 5));
        assertThrows(StreamWriteException.class, chars::close);
    }

    void tokenBufferDescriptionWithNativeIdsAndUncontextualizedPojo() throws Exception {
        TokenBuffer buffer = new TokenBuffer(null, true);
        buffer.writeTypeId("typeA");
        buffer.writeObjectId("objB");
        buffer.writeStartObject();
        buffer.writeEndObject();
        assertNotNull(buffer.toString());
        buffer.close();

        JsonGenerator pojo = factory.createGenerator(new ByteArrayOutputStream());
        assertThrows(UnsupportedOperationException.class, () -> pojo.writePOJO(new Object()));
        pojo.close();
    }

    void __invoke_simpleWritesReadIndependentLiteralRootValues() throws Exception {
        try {
            simpleWritesReadIndependentLiteralRootValues();
        } finally {
        }
    }


    void __invoke_simpleObjectRetainsEmptyAndNumericPropertySemantics() throws Exception {
        try {
            simpleObjectRetainsEmptyAndNumericPropertySemantics();
        } finally {
        }
    }


    void __invoke_vpackSampleDocumentRetainsPortableJsonStructure() throws Exception {
        try {
            vpackSampleDocumentRetainsPortableJsonStructure();
        } finally {
        }
    }


    void __invoke_tokenBufferDescriptionTruncatesAfterManyVpackTokens() throws Exception {
        try {
            tokenBufferDescriptionTruncatesAfterManyVpackTokens();
        } finally {
        }
    }


    void __invoke_parserSequenceContinuesAcrossIndependentVpackSources() throws Exception {
        try {
            parserSequenceContinuesAcrossIndependentVpackSources();
        } finally {
        }
    }


    void __invoke_tokenBuffersInteroperateWithNestedVpackParserSequences() throws Exception {
        try {
            tokenBuffersInteroperateWithNestedVpackParserSequences();
        } finally {
        }
    }


    void __invoke_uuidWrittenToTokenBufferRemainsAStringAndReadsBack() throws Exception {
        try {
            uuidWrittenToTokenBufferRemainsAStringAndReadsBack();
        } finally {
        }
    }


    void __invoke_nullStringOverloadsWriteNullToken() throws Exception {
        try {
            nullStringOverloadsWriteNullToken();
        } finally {
        }
    }


    void __invoke_pojoNullWritesNullToken() throws Exception {
        try {
            pojoNullWritesNullToken();
        } finally {
        }
    }


    void __invoke_rawValueOffsetOverloadsAreExplicitlyUnsupportedForVpack() throws Exception {
        try {
            rawValueOffsetOverloadsAreExplicitlyUnsupportedForVpack();
        } finally {
        }
    }


    void __invoke_tokenBufferDescriptionWithNativeIdsAndUncontextualizedPojo() throws Exception {
        try {
            tokenBufferDescriptionWithNativeIdsAndUncontextualizedPojo();
        } finally {
        }
    }

}
