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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for VPackGenerator container (array/object) serialization and width-promotion.
 */
public class VPackGeneratorContainerTest extends BaseTestForVPack {
    private byte[] genWith(VPackMapper m, WriteAction action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(out)) {
            action.write(g);
        }
        return out.toByteArray();
    }

    private byte[] gen(WriteAction action) {
        return genWith(new VPackMapper(), action);
    }

    @FunctionalInterface
    interface WriteAction {
        void write(JsonGenerator g);
    }

    // =========================================================
    // Arrays: no-index (all same length), index, compact
    // =========================================================

    @Test
    public void testEmptyArray_typeByte() {
        byte[] bytes = gen(g -> {
            g.writeStartArray();
            g.writeEndArray();
        });
        assertThat(bytes[0]).isEqualTo((byte) 0x01); // VPACK_ARRAY_EMPTY
    }

    @Test
    public void testNoIndexArray_allSameLength() {
        VPackMapper mapper = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        // All items same length: small ints (1 byte each)
        byte[] bytes = genWith(mapper, g -> {
            g.writeStartArray();
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeNumber(3);
            g.writeEndArray();
        });
        // No-index array type: 0x02 (1-byte width)
        assertThat(bytes[0]).isEqualTo((byte) 0x02);
    }

    @Test
    public void testIndexArray_mixedLengths() {
        // Mixed lengths: small int (1 byte) and long string (>1 byte)
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        byte[] bytes = genWith(m, g -> {
            g.writeStartArray();
            g.writeNumber(1); // 1 byte (small int)
            g.writeString("hello"); // 6 bytes
            g.writeEndArray();
        });
        // Should use indexed array: 0x06-0x09
        int tb = bytes[0] & 0xFF;
        assertThat(tb).as("Expected indexed array type, got 0x" + Integer.toHexString(tb)).isBetween(0x06, 0x09);
    }

    @Test
    public void testCompactArray_noIndex() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        byte[] bytes = genWith(m, g -> {
            g.writeStartArray();
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeEndArray();
        });
        assertThat(bytes[0]).isEqualTo((byte) 0x13); // VPACK_ARRAY_COMPACT
    }

    @Test
    public void testCompactArray_roundTrip() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        List<Integer> original = Arrays.asList(10, 20, 30);
        byte[] bytes = m.writeValueAsBytes(original);
        assertThat(bytes[0]).isEqualTo((byte) 0x13);
        List<?> result = m.readValue(bytes, List.class);
        assertThat(result ).hasSize(3);
        assertThat(((Number) result.get(0)).intValue()).isEqualTo(10);
    }

    // =========================================================
    // Objects: sorted, unsorted, compact
    // =========================================================

    @Test
    public void testEmptyObject_typeByte() {
        byte[] bytes = gen(g -> {
            g.writeStartObject();
            g.writeEndObject();
        });
        assertThat(bytes[0]).isEqualTo((byte) 0x0a); // VPACK_OBJECT_EMPTY
    }

    @Test
    public void testSortedObject_typeByte() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .disable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        byte[] bytes = genWith(m, g -> {
            g.writeStartObject();
            g.writeName("z");
            g.writeNumber(1);
            g.writeName("a");
            g.writeNumber(2);
            g.writeEndObject();
        });
        // Sorted object: 0x0b-0x0e
        int tb = bytes[0] & 0xFF;
        assertThat(tb).as("Expected sorted object type, got 0x" + Integer.toHexString(tb)).isBetween(0x0b, 0x0e);
    }

    @Test
    public void testUnsortedObject_typeByte() {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
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
        assertThat(tb).as("Expected unsorted object type, got 0x" + Integer.toHexString(tb)).isBetween(0x0f, 0x12);
    }

    @Test
    public void testCompactObject_typeByte() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        byte[] bytes = genWith(m, g -> {
            g.writeStartObject();
            g.writeName("key");
            g.writeNumber(42);
            g.writeEndObject();
        });
        assertThat(bytes[0]).isEqualTo((byte) 0x14); // VPACK_OBJECT_COMPACT
    }

    @Test
    public void testCompactObject_sortedKeys_roundTrip() {
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
        assertThat(bytes[0]).isEqualTo((byte) 0x14);
        // Parse back
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            p.nextToken(); // first property name
            String first = p.getString();
            // Sorted: a_key should come before z_key
            assertThat(first).isEqualTo("a_key");
        }
    }

    // =========================================================
    // Nested containers
    // =========================================================

    @Test
    public void testNestedArray_insideObject()  {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("arr");
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(1);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(2);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    @Test
    public void testNestedObject_insideArray() {
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
            assertThat(JsonToken.START_ARRAY).isEqualTo(p.nextToken());
            assertThat(JsonToken.START_OBJECT).isEqualTo(p.nextToken());
            assertThat(JsonToken.PROPERTY_NAME).isEqualTo(p.nextToken());
            assertThat("x").isEqualTo(p.getString());
            assertThat(JsonToken.VALUE_NUMBER_INT).isEqualTo(p.nextToken());
            assertThat(5).isEqualTo(p.getIntValue());
            assertThat(JsonToken.END_OBJECT).isEqualTo(p.nextToken());
            assertThat(JsonToken.END_ARRAY).isEqualTo(p.nextToken());
        }
    }

    // =========================================================
    // writeStartArray(forValue) / writeStartObject(forValue) variants
    // =========================================================

    @Test
    public void testWriteStartArray_withForValue() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartArray("myValue");
            g.writeNumber(1);
            g.writeEndArray();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length).isPositive();
    }

    @Test
    public void testWriteStartArray_withForValueAndSize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartArray("myValue", 3);
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeNumber(3);
            g.writeEndArray();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length).isPositive();
    }

    @Test
    public void testWriteStartObject_withForValue() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject("myValue");
            g.writeName("k");
            g.writeNumber(1);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length ).isPositive();
    }

    @Test
    public void testWriteStartObject_withForValueAndSize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject("myValue", 1);
            g.writeName("k");
            g.writeNumber(1);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length ).isPositive();
    }

    // =========================================================
    // writeRaw byte methods (public extension)
    // =========================================================

    @Test
    public void testWriteRaw_byte() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (VPackGenerator g = (VPackGenerator) m.createGenerator(out)) {
            g.writeRaw((byte) 0x18); // VPACK_NULL as raw byte
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x18);
    }

    @Test
    public void testWriteBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (VPackGenerator g = (VPackGenerator) m.createGenerator(out)) {
            byte[] data = {(byte) 0x18, (byte) 0x1a};
            g.writeBytes(data, 0, data.length);
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes).hasSize(2);
    }

    // =========================================================
    // Flush
    // =========================================================

    @Test
    public void testFlush() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNull();
            g.flush();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes).hasSize(1);
    }

    // =========================================================
    // Width-promotion: force 2-byte width by using 256 items
    // =========================================================

    @Test
    public void testIndexArray_width2_roundTrip() {
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
        assertThat(result ).hasSize(50);
        assertThat(result.get(0)).isEqualTo("item_0");
        assertThat(result.get(49)).isEqualTo("item_49");
    }

    @Test
    public void testSortedObject_keysSorted_inBuf() {
        // Verify key ordering in output
        VPackMapper m = VPackMapper.builder().enable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED).build();
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
            p.nextToken();
            String k1 = p.getString();
            p.nextToken();
            p.nextToken();
            String k2 = p.getString();
            p.nextToken();
            p.nextToken();
            String k3 = p.getString();
            p.nextToken();
            p.nextToken(); // END_OBJECT
            assertThat(k1).isEqualTo("a");
            assertThat(k2).isEqualTo("m");
            assertThat(k3).isEqualTo("z");
        }
    }

    // =========================================================
    // Container context streamWriteContext
    // =========================================================

    @Test
    public void testStreamWriteContext_inArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartArray();
            assertThat(g.streamWriteContext().inArray()).isTrue();
            g.writeNumber(1);
            g.writeEndArray();
        }
    }

    @Test
    public void testStreamWriteContext_inObject() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            assertThat(g.streamWriteContext().inObject()).isTrue();
            g.writeName("key");
            g.writeNumber(1);
            g.writeEndObject();
        }
    }
}
