package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackObjectNameCacheTest {
    @Test
    void decodesCachedBoundaryUtf8AndLongStringKeys() throws Exception {
        byte[] key64 = fill(64, (byte) 'x');
        byte[] key65 = fill(65, (byte) 'y');
        byte[] key127 = fill(127, (byte) 'z');
        byte[][] keys = {
                new byte[0], key64, key65,
                "café".getBytes(StandardCharsets.UTF_8), new byte[] { (byte) 0xFF }, key127
        };
        byte[] input = objectWithKeys(keys);
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("", parser.currentName());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            for (int i = 1; i < keys.length; ++i) {
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                if (i == 1) assertEquals("x".repeat(64), parser.currentName());
                if (i == 2) assertEquals("y".repeat(65), parser.currentName());
                if (i == 3) assertEquals("café", parser.currentName());
                if (i == 4) assertEquals("\uFFFD", parser.currentName());
                if (i == 5) assertEquals("z".repeat(127), parser.currentName());
                assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    @Test
    void repeatedNamesAcrossRootsAndHashCollisionsDecodeCorrectly() throws Exception {
        ByteArrayOutputStream roots = new ByteArrayOutputStream();
        for (int i = 0; i < 300; ++i) roots.writeBytes(objectWithKeys(
                new byte[][] { "type".getBytes(StandardCharsets.UTF_8),
                        "description".getBytes(StandardCharsets.UTF_8) }));
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(roots.toByteArray())) {
            int names = 0;
            while (parser.nextToken() != null) {
                if (parser.currentToken() == JsonToken.PROPERTY_NAME) {
                    assertEquals(names % 2 == 0 ? "type" : "description", parser.currentName());
                    ++names;
                }
            }
            assertEquals(600, names);
        }

        String[] collision = collisionPair();
        byte[] colliding = objectWithKeys(new byte[][] {
                collision[0].getBytes(StandardCharsets.US_ASCII),
                collision[1].getBytes(StandardCharsets.US_ASCII) });
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(colliding)) {
            parser.nextToken();
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(collision[0], parser.currentName());
            parser.nextToken();
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(collision[1], parser.currentName());
        }
    }

    @Test
    void nameLimitAcceptsBoundaryAndRejectsLongerUtf8Name() throws Exception {
        VPackFactory constrained = VPackFactory.builder().streamReadConstraints(
                StreamReadConstraints.builder().maxNameLength(64).build()).build();
        try (VPackParser parser = (VPackParser) constrained.createParser(
                objectWithKeys(new byte[][] { fill(64, (byte) 'a') }))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(64, parser.currentName().length());
        }
        try (VPackParser parser = (VPackParser) constrained.createParser(
                objectWithKeys(new byte[][] { fill(65, (byte) 'a') }))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        }
    }

    @Test
    void sortedIndexValidationUsesResolvedNamesIncludingCacheHits() throws Exception {
        byte[] body = pair("a", new byte[] { 0x18 });
        body = concat(body, pair("b", new byte[] { 0x18 }),
                pair("a", new byte[] { 0x18 }));
        // Sorted name order is first a (offset 3), second a (offset 9), then b (offset 6).
        byte[] correctlyOrdered = VPackObjectParserTest.object(1, true, body,
                new long[] { 3, 9, 6 });
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(correctlyOrdered)) {
            while (parser.nextToken() != null) { }
        }
        byte[] misordered = VPackObjectParserTest.object(1, true, body,
                new long[] { 6, 3, 9 });
        assertThrows(StreamReadException.class, () -> {
            try (VPackParser parser = (VPackParser) new VPackFactory().createParser(misordered)) {
                while (parser.nextToken() != null) { }
            }
        });

        byte[] unsorted = VPackObjectParserTest.object(1, false, body,
                new long[] { 3, 6, 9 });
        consume(unsorted);
        // Same a, b, a keys in a compact object (length 12, reverse count 3).
        consume(new byte[] { 0x14, 0x0C, 0x41, 'a', 0x18, 0x41, 'b', 0x18,
                0x41, 'a', 0x18, 0x03 });
    }

    private static String[] collisionPair() {
        Map<Integer, String> firstBySlot = new HashMap<>();
        for (int i = 0; ; ++i) {
            String value = "collision" + i;
            int hash = value.length();
            for (byte b : value.getBytes(StandardCharsets.US_ASCII)) hash = 31 * hash + b;
            int slot = (hash ^ (hash >>> 16)) & 255;
            String previous = firstBySlot.putIfAbsent(slot, value);
            if (previous != null && !previous.equals(value)) return new String[] { previous, value };
        }
    }

    // Independent fixed-object fixture with one-byte body-relative key offsets.
    private static byte[] objectWithKeys(byte[][] keys) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        long[] offsets = new long[keys.length];
        for (int i = 0; i < keys.length; ++i) {
            offsets[i] = 5L + body.size();
            byte[] key = keys[i];
            if (key.length < 127) {
                body.write(0x40 + key.length);
                body.writeBytes(key);
            } else {
                body.write(0xBF);
                writeLE(body, key.length, 8);
                body.writeBytes(key);
            }
            body.write(0x18);
        }
        return VPackObjectParserTest.object(2, false, body.toByteArray(), offsets);
    }

    private static byte[] fill(int length, byte value) {
        byte[] result = new byte[length];
        java.util.Arrays.fill(result, value);
        return result;
    }

    private static byte[] pair(String name, byte[] value) {
        byte[] key = name.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0x40 + key.length);
        out.writeBytes(key);
        out.writeBytes(value);
        return out.toByteArray();
    }

    private static byte[] concat(byte[]... arrays) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] array : arrays) out.writeBytes(array);
        return out.toByteArray();
    }

    private static void consume(byte[] input) throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(input)) {
            while (parser.nextToken() != null) { }
        }
    }

    private static void writeLE(ByteArrayOutputStream out, long value, int width) {
        for (int i = 0; i < width; ++i) out.write((int) (value >>> (i * 8)) & 0xFF);
    }
}
