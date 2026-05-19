package com.arangodb.jackson.dataformat.velocypack.gen;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackGenerator;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackWriteFeature;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for VPackGenerator container (array/object) serialization and width-promotion.
 */
public class VPackGeneratorContainerTest extends BaseTestForVPack
{
    private byte[] genWith(VPackMapper m, WriteAction action) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(out)) {
            action.write(g);
        }
        return out.toByteArray();
    }

    private byte[] gen(WriteAction action) throws Exception {
        return genWith(new VPackMapper(), action);
    }

    @FunctionalInterface
    interface WriteAction {
        void write(JsonGenerator g) throws Exception;
    }

    // =========================================================
    // Arrays: no-index (all same length), index, compact
    // =========================================================

    @Test
    public void testEmptyArray_typeByte() throws Exception {
        byte[] bytes = gen(g -> {
            g.writeStartArray();
            g.writeEndArray();
        });
        assertEquals((byte) 0x01, bytes[0]); // VPACK_ARRAY_EMPTY
    }

    @Test
    public void testNoIndexArray_allSameLength() throws Exception {
        // All items same length: small ints (1 byte each)
        byte[] bytes = gen(g -> {
            g.writeStartArray();
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeNumber(3);
            g.writeEndArray();
        });
        // No-index array type: 0x02 (1-byte width)
        assertEquals((byte) 0x02, bytes[0]);
    }

    @Test
    public void testIndexArray_mixedLengths() throws Exception {
        // Mixed lengths: small int (1 byte) and long string (>1 byte)
        byte[] bytes = gen(g -> {
            g.writeStartArray();
            g.writeNumber(1); // 1 byte (small int)
            g.writeString("hello"); // 6 bytes
            g.writeEndArray();
        });
        // Should use indexed array: 0x06-0x09
        int tb = bytes[0] & 0xFF;
        assertTrue(tb >= 0x06 && tb <= 0x09,
                "Expected indexed array type, got 0x" + Integer.toHexString(tb));
    }

    @Test
    public void testCompactArray_noIndex() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        byte[] bytes = genWith(m, g -> {
            g.writeStartArray();
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeEndArray();
        });
        assertEquals((byte) 0x13, bytes[0]); // VPACK_ARRAY_COMPACT
    }

    @Test
    public void testCompactArray_roundTrip() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        List<Integer> original = Arrays.asList(10, 20, 30);
        byte[] bytes = m.writeValueAsBytes(original);
        assertEquals((byte) 0x13, bytes[0]);
        List<?> result = m.readValue(bytes, List.class);
        assertEquals(3, result.size());
        assertEquals(10, ((Number) result.get(0)).intValue());
    }

    // =========================================================
    // Objects: sorted, unsorted, compact
    // =========================================================

    @Test
    public void testEmptyObject_typeByte() throws Exception {
        byte[] bytes = gen(g -> {
            g.writeStartObject();
            g.writeEndObject();
        });
        assertEquals((byte) 0x0a, bytes[0]); // VPACK_OBJECT_EMPTY
    }

    @Test
    public void testSortedObject_typeByte() throws Exception {
        byte[] bytes = gen(g -> {
            g.writeStartObject();
            g.writeName("z");
            g.writeNumber(1);
            g.writeName("a");
            g.writeNumber(2);
            g.writeEndObject();
        });
        // Sorted object: 0x0b-0x0e
        int tb = bytes[0] & 0xFF;
        assertTrue(tb >= 0x0b && tb <= 0x0e,
                "Expected sorted object type, got 0x" + Integer.toHexString(tb));
    }

    @Test
    public void testUnsortedObject_typeByte() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .build();
        byte[] bytes = genWith(m, g -> {
            g.writeStartObject();
            g.writeName("z");
            g.writeNumber(1);
            g.writeName("a");
            g.writeNumber(2);
            g.writeEndObject();
        });
        // Unsorted object: 0x0f-0x12
        int tb = bytes[0] & 0xFF;
        assertTrue(tb >= 0x0f && tb <= 0x12,
                "Expected unsorted object type, got 0x" + Integer.toHexString(tb));
    }

    @Test
    public void testCompactObject_typeByte() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        byte[] bytes = genWith(m, g -> {
            g.writeStartObject();
            g.writeName("key");
            g.writeNumber(42);
            g.writeEndObject();
        });
        assertEquals((byte) 0x14, bytes[0]); // VPACK_OBJECT_COMPACT
    }

    @Test
    public void testCompactObject_sortedKeys_roundTrip() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .enable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .build();
        byte[] bytes = genWith(m, g -> {
            g.writeStartObject();
            g.writeName("z_key");
            g.writeNumber(1);
            g.writeName("a_key");
            g.writeNumber(2);
            g.writeEndObject();
        });
        assertEquals((byte) 0x14, bytes[0]);
        // Parse back
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            p.nextToken(); // first property name
            String first = p.getString();
            // Sorted: a_key should come before z_key
            assertEquals("a_key", first);
        }
    }

    // =========================================================
    // Nested containers
    // =========================================================

    @Test
    public void testNestedArray_insideObject() throws Exception {
        byte[] bytes = gen(g -> {
            g.writeStartObject();
            g.writeName("arr");
            g.writeStartArray();
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeEndArray();
            g.writeEndObject();
        });
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("arr", p.getString());
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(1, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(2, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    @Test
    public void testNestedObject_insideArray() throws Exception {
        byte[] bytes = gen(g -> {
            g.writeStartArray();
            g.writeStartObject();
            g.writeName("x");
            g.writeNumber(5);
            g.writeEndObject();
            g.writeEndArray();
        });
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("x", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(5, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // writeStartArray(forValue) / writeStartObject(forValue) variants
    // =========================================================

    @Test
    public void testWriteStartArray_withForValue() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartArray("myValue");
            g.writeNumber(1);
            g.writeEndArray();
        }
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0);
    }

    @Test
    public void testWriteStartArray_withForValueAndSize() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartArray("myValue", 3);
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeNumber(3);
            g.writeEndArray();
        }
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0);
    }

    @Test
    public void testWriteStartObject_withForValue() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject("myValue");
            g.writeName("k");
            g.writeNumber(1);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0);
    }

    @Test
    public void testWriteStartObject_withForValueAndSize() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject("myValue", 1);
            g.writeName("k");
            g.writeNumber(1);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0);
    }

    // =========================================================
    // writeRaw byte methods (public extension)
    // =========================================================

    @Test
    public void testWriteRaw_byte() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (VPackGenerator g = (VPackGenerator) m.createGenerator(out)) {
            g.writeRaw((byte) 0x18); // VPACK_NULL as raw byte
        }
        byte[] bytes = out.toByteArray();
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x18, bytes[0]);
    }

    @Test
    public void testWriteBytes() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (VPackGenerator g = (VPackGenerator) m.createGenerator(out)) {
            byte[] data = { (byte) 0x18, (byte) 0x1a };
            g.writeBytes(data, 0, data.length);
        }
        byte[] bytes = out.toByteArray();
        assertEquals(2, bytes.length);
    }

    // =========================================================
    // Flush
    // =========================================================

    @Test
    public void testFlush() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNull();
            g.flush();
        }
        byte[] bytes = out.toByteArray();
        assertEquals(1, bytes.length);
    }

    // =========================================================
    // Width-promotion: force 2-byte width by using 256 items
    // =========================================================

    @Test
    public void testIndexArray_width2_roundTrip() throws Exception {
        // Build array with many items of mixed length to require 2-byte offsets
        // Each item is a string of various lengths
        List<String> items = new ArrayList<>();
        // Add 50 items with varying lengths to force the index table to be larger
        for (int i = 0; i < 50; i++) {
            items.add("item_" + i); // varying lengths
        }
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(items);
        List<?> result = m.readValue(bytes, List.class);
        assertEquals(50, result.size());
        assertEquals("item_0", result.get(0));
        assertEquals("item_49", result.get(49));
    }

    @Test
    public void testSortedObject_keysSorted_inBuf() throws Exception {
        // Verify key ordering in output
        VPackMapper m = new VPackMapper();
        byte[] bytes = genWith(m, g -> {
            g.writeStartObject();
            g.writeName("z");
            g.writeNumber(2);
            g.writeName("a");
            g.writeNumber(1);
            g.writeName("m");
            g.writeNumber(3);
            g.writeEndObject();
        });
        // Parse back and check key order (sorted)
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken(); // START_OBJECT
            p.nextToken(); String k1 = p.getString(); p.nextToken();
            p.nextToken(); String k2 = p.getString(); p.nextToken();
            p.nextToken(); String k3 = p.getString(); p.nextToken();
            p.nextToken(); // END_OBJECT
            assertEquals("a", k1);
            assertEquals("m", k2);
            assertEquals("z", k3);
        }
    }

    // =========================================================
    // Container context streamWriteContext
    // =========================================================

    @Test
    public void testStreamWriteContext_inArray() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartArray();
            assertTrue(g.streamWriteContext().inArray());
            g.writeNumber(1);
            g.writeEndArray();
        }
    }

    @Test
    public void testStreamWriteContext_inObject() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            assertTrue(g.streamWriteContext().inObject());
            g.writeName("key");
            g.writeNumber(1);
            g.writeEndObject();
        }
    }
}
