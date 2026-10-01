package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackStringGeneratorTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void usesUtf8ByteBoundariesAndPreservesEmbeddedNul() throws Exception {
        String shortValue = "s".repeat(126);
        String longValue = "l".repeat(127);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeString(shortValue);
            generator.writeString(longValue);
            generator.writeString("a\0€😀");
        }

        byte[] actual = output.toByteArray();
        assertEquals((byte) 0xBE, actual[0]);
        assertEquals((byte) 0xBF, actual[127]);
        assertEquals(127, actual[128] & 0xFF);
        assertArrayEquals(new byte[] { 0x49, 'a', 0, (byte) 0xE2, (byte) 0x82,
                (byte) 0xAC, (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x80 },
                Arrays.copyOfRange(actual, actual.length - 10, actual.length));
    }

    @Test
    void charArraysAndUtf8OverloadUseStrictEncoding() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            char[] chars = "prefix😀suffix".toCharArray();
            generator.writeString(chars, 6, 2);
            generator.writeUTF8String(new byte[] { (byte) 0xE2, (byte) 0x82, (byte) 0xAC },
                    0, 3);
        }
        assertArrayEquals(new byte[] { 0x44, (byte) 0xF0, (byte) 0x9F, (byte) 0x98,
                (byte) 0x80, 0x43, (byte) 0xE2, (byte) 0x82, (byte) 0xAC },
                output.toByteArray());
    }

    @Test
    void nestedStringsAndLiteralNamesAppendPayloadsIntoTheRootArena() throws Exception {
        byte[] source = { '!', (byte) 0xE2, (byte) 0x82, (byte) 0xAC, '?' };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (VPackGenerator generator = (VPackGenerator) factory.createGenerator(output)) {
            generator.writeStartObject();
            generator.writeName("b");
            generator.writeUTF8String(source, 1, 3);
            source[1] = 'x';
            generator.writeName("a");
            generator.writeString("long".repeat(32));
            generator.writeName("c");
            generator.writeString("\uD800");
            generator.writeEndObject();
        }

        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("€", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("long".repeat(32), parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("c", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("?", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(null, parser.nextToken());
        }
    }

    @Test
    void literalObjectNamesKeepShortAndLongLengthBoundaries() throws Exception {
        String shortName = "s".repeat(126);
        String longName = "l".repeat(127);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeStartObject();
            generator.writeName(shortName);
            generator.writeNull();
            generator.writeName(longName);
            generator.writeNull();
            generator.writeEndObject();
        }

        byte[] encoded = output.toByteArray();
        assertEquals((byte) 0xBE, encoded[5]);
        assertEquals((byte) 0xBF, encoded[133]);
        assertEquals(127, encoded[134] & 0xFF);
        try (JsonParser parser = factory.createParser(encoded)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(shortName, parser.currentName());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(longName, parser.currentName());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    @Test
    void rawOutputRemainsUnsupported() {
        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(UnsupportedOperationException.class, () -> generator.writeRaw("raw"));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
                // Failed generators retain their primary failure for close().
            }
        }
    }

    @Test
    void readerConsumesExactCharactersAndDoesNotCloseBorrowedInput() throws Exception {
        TrackingReader reader = new TrackingReader("abcXYZ");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeString(reader, 3);
        }
        assertArrayEquals(new byte[] { 0x43, 'a', 'b', 'c' }, output.toByteArray());
        assertEquals('X', reader.read());
        if (reader.closed) {
            throw new AssertionError("reader was closed");
        }
    }

    @Test
    void readerReadsUntilEofAndHandlesZeroRead() throws Exception {
        ZeroReadReader reader = new ZeroReadReader("€😀");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeString(reader, -1);
        }
        assertArrayEquals(new byte[] { 0x47, (byte) 0xE2, (byte) 0x82, (byte) 0xAC,
                (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x80 },
                output.toByteArray());
    }

    @Test
    void readerReportsEarlyEofAndRootBudgetBeforeLargeEncoding() {
        JsonGenerator early = factory.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(StreamWriteException.class,
                    () -> early.writeString(new StringReader("x"), 2));
        } finally {
            try {
                early.close();
            } catch (RuntimeException ignored) {
            }
        }

        VPackFactory constrained = VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder()
                        .maxRootValueBytes(4).build())
                .build();
        JsonGenerator limited = constrained.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(StreamConstraintsException.class,
                    () -> limited.writeString(new StringReader("abcd"), -1));
        } finally {
            try {
                limited.close();
            } catch (RuntimeException ignored) {
            }
        }
    }

    private static class TrackingReader extends StringReader {
        boolean closed;

        TrackingReader(String value) {
            super(value);
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    private static final class ZeroReadReader extends TrackingReader {
        ZeroReadReader(String value) {
            super(value);
        }

        @Override
        public int read(char[] buffer, int offset, int length) {
            return 0;
        }
    }
}
