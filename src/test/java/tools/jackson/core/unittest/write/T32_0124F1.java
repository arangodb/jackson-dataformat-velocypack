package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.filter.FilteringGeneratorDelegate;
import tools.jackson.core.filter.JsonPointerBasedFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0124F1 {

    void utf8BoundarySequenceRetainsAllNumbersAndSurrogateString() throws Exception {
        String value = "Natuurlijk is alles gelukt en weer een tevreden klant\uD83D\uDE04";
        int count = 4000 - 38;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            for (int i = 1; i <= count; ++i) {
                generator.writeNumber(1);
            }
            generator.writeString(value);
        }

        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            for (int i = 1; i <= count; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(1, parser.getIntValue());
            }
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(value, parser.getString());
            assertNull(parser.nextToken());
        }
    }

    void nestingDepthWithSmallLimit() {
        VPackFactory factory = VPackFactory.builder()
                .streamWriteConstraints(StreamWriteConstraints.builder()
                        .maxNestingDepth(1).build())
                .build();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = factory.createGenerator(output);
        try {
            generator.writeStartObject();
            generator.writeName("array");
            assertThrows(StreamConstraintsException.class, generator::writeStartArray);
        } finally {
            try {
                generator.close();
            } catch (StreamConstraintsException ignored) {
                // The failed generator retains the expected constraint failure.
            }
        }
    }

    void nestingDepthWithSmallLimitNestedObject() {
        VPackFactory factory = VPackFactory.builder()
                .streamWriteConstraints(StreamWriteConstraints.builder()
                        .maxNestingDepth(1).build())
                .build();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = factory.createGenerator(output);
        try {
            generator.writeStartObject();
            generator.writeName("object");
            assertThrows(StreamConstraintsException.class, generator::writeStartObject);
        } finally {
            try {
                generator.close();
            } catch (StreamConstraintsException ignored) {
                // The failed generator retains the expected constraint failure.
            }
        }
    }

    void rawSurrogateTextIsExplicitlyUnsupported() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = generator(output);
        try {
            generator.writeStartArray();
            assertThrows(UnsupportedOperationException.class,
                    () -> generator.writeRaw("\"\uD83D\uDE0C\""));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
                // Preserve the primary unsupported-operation assertion.
            }
        }
    }

    void filteringWithEscapedCharsUsesLiteralVpackStringBytes() throws Exception {
        String value = "\b\t\f\n\r\"foo\"\u0000";
        byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new FilteringGeneratorDelegate(
                generator(output), new JsonPointerBasedFilter("/escapes"),
                Inclusion.INCLUDE_ALL_AND_PATH, false)) {
            generator.writeStartObject();
            generator.writeName("a");
            generator.writeNumber(123);
            generator.writeName("array");
            generator.writeStartArray();
            generator.writeNumber((short) 1);
            generator.writeNumber((short) 2);
            generator.writeEndArray();
            generator.writeName("escapes");
            generator.writeUTF8String(utf8, 0, utf8.length);
            generator.writeEndObject();
        }

        byte[] expected = VPackWireFixtureTest.hex(
                "0b 18 01 47 65 73 63 61 70 65 73 4b "
                + "08 09 0c 0a 0d 22 66 6f 6f 22 00 03");
        assertArrayEquals(expected, output.toByteArray());
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

    void __invoke_utf8BoundarySequenceRetainsAllNumbersAndSurrogateString() throws Exception {
        try {
            utf8BoundarySequenceRetainsAllNumbersAndSurrogateString();
        } finally {
        }
    }


    void __invoke_nestingDepthWithSmallLimit() throws Exception {
        try {
            nestingDepthWithSmallLimit();
        } finally {
        }
    }


    void __invoke_nestingDepthWithSmallLimitNestedObject() throws Exception {
        try {
            nestingDepthWithSmallLimitNestedObject();
        } finally {
        }
    }


    void __invoke_rawSurrogateTextIsExplicitlyUnsupported() throws Exception {
        try {
            rawSurrogateTextIsExplicitlyUnsupported();
        } finally {
        }
    }


    void __invoke_filteringWithEscapedCharsUsesLiteralVpackStringBytes() throws Exception {
        try {
            filteringWithEscapedCharsUsesLiteralVpackStringBytes();
        } finally {
        }
    }

}
