package com.arangodb.jackson.dataformat.velocypack.gen;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.io.IOContext;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackGenerator;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackWriteFeature;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Tests for advanced VPackGenerator paths:
 * - Second constructor (custom buffer, offset, recyclable flag)
 * - writeStartArray/writeStartObject forValue+size overloads
 * - writeArray(int[]/long[]/double[]) shortcuts
 * - writePropertyId (integer key)
 * - _rawBytes large-data path (direct write when len > outputEnd)
 * - _buildCompactObject with WRITE_OBJECT_KEYS_SORTED
 * - _extractStringBytes with long key
 * - writeNumber(String) with integer string
 * - writeNumber(String) with BigDecimal-style (non-long-parseable) numeric string
 * - streamWriteOutputTarget / streamWriteOutputBuffered
 * - currentValue / assignCurrentValue
 */
public class VPackGeneratorAdvancedTest extends BaseTestForVPack {
    // =========================================================
    // Second constructor with custom buffer
    // =========================================================

    @Test
    public void testSecondConstructor_customBuffer() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOContext ioCtxt = testIOContext();
        byte[] customBuffer = new byte[512];
        // Use second constructor: (writeCtxt, ioCtxt, streamFeatures, vpackFeatures, out, buffer, offset, recyclable)
        VPackGenerator gen = new VPackGenerator(
                ObjectWriteContext.empty(), ioCtxt,
                0, VPackWriteFeature.collectDefaults(), out,
                customBuffer, 0, false);
        gen.writeNull();
        gen.close();
        byte[] result = out.toByteArray();
        assertThat(result).hasSize(1);
        assertThat(result[0]).isEqualTo((byte) 0x18); // 0x18 = VPACK_NULL
    }

    @Test
    public void testSecondConstructor_withOffset() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOContext ioCtxt = testIOContext();
        byte[] customBuffer = new byte[512];
        // Fill first 16 bytes with sentinel (they should not be written to output)
        for (int i = 0; i < 16; i++) customBuffer[i] = (byte) 0xFF;

        VPackGenerator gen = new VPackGenerator(
                ObjectWriteContext.empty(), ioCtxt,
                0, VPackWriteFeature.collectDefaults(), out,
                customBuffer, 16, false);
        gen.writeBoolean(true);
        gen.close();
        byte[] result = out.toByteArray();
        // The output should contain the bytes written after offset
        assertThat(result).isNotEmpty();
    }

    // =========================================================
    // writeStartArray(forValue, size) overload
    // =========================================================

    @Test
    public void testWriteStartArray_withForValueAndSize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartArray(new Object(), 3); // forValue + size overload
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeNumber(3);
            g.writeEndArray();
        }
        // Parse back
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken() == JsonToken.VALUE_NUMBER_INT ? p.getIntValue() : -1).isEqualTo(1);
            p.nextToken(); // 2
            p.nextToken(); // 3
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // writeStartObject(forValue, size) overload
    // =========================================================

    @Test
    public void testWriteStartObject_withForValueAndSize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject(new Object(), 2); // forValue + size overload
            g.writeName("a");
            g.writeNumber(1);
            g.writeName("b");
            g.writeNumber(2);
            g.writeEndObject();
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // writeArray(int[]) shortcut
    // =========================================================

    @Test
    public void testWriteArray_intArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeArray(new int[]{10, 20, 30}, 0, 3);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(10);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(20);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(30);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testWriteArray_intArray_partialRange() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeArray(new int[]{10, 20, 30, 40}, 1, 2); // offset=1, length=2
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(20);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(30);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // writeArray(long[]) shortcut
    // =========================================================

    @Test
    public void testWriteArray_longArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeArray(new long[]{1000L, 2000L, 3000L}, 0, 3);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getLongValue()).isEqualTo(1000L);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getLongValue()).isEqualTo(2000L);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getLongValue()).isEqualTo(3000L);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // writeArray(double[]) shortcut
    // =========================================================

    @Test
    public void testWriteArray_doubleArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeArray(new double[]{1.1, 2.2, 3.3}, 0, 3);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDoubleValue()).isEqualTo(1.1, offset(1e-9));
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDoubleValue()).isEqualTo(2.2, offset(1e-9));
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDoubleValue()).isEqualTo(3.3, offset(1e-9));
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // writePropertyId (integer key)
    // =========================================================

    @Test
    public void testWritePropertyId_simple() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            g.writePropertyId(42L); // integer key
            g.writeNumber(100);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length ).isPositive();
        // Parse back: the integer key becomes a property name string
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            // integer key is represented as a string
            assertThat(p.getString()).isNotNull();
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(100);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // _rawBytes large data path (len > outputEnd → write directly to stream)
    // =========================================================

    @Test
    public void testWriteLargeString_exceedsBuffer() {
        // Write a string larger than the output buffer to force direct-write path
        // The default output buffer is 8KB, so we need > 8KB of string data
        String largeStr = "x".repeat(10000); // 10KB
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeString(largeStr);
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length > 10000).isTrue();
        // Parse back
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo(largeStr);
        }
    }

    @Test
    public void testWriteLargeObject_exceedsBuffer() {
        // Write an object with many large string values
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            for (int i = 0; i < 10; i++) {
                g.writeName("key" + i);
                g.writeString("value".repeat(200)); // 1KB per value
            }
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length > 10000).isTrue();
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            int count = 0;
            while (p.nextToken() == JsonToken.PROPERTY_NAME) {
                assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
                count++;
            }
            assertThat(count).isEqualTo(10);
        }
    }

    // =========================================================
    // _buildCompactObject with WRITE_OBJECT_KEYS_SORTED
    // =========================================================

    @Test
    public void testCompactObject_withSortedKeys() {
        VPackMapper mapper = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .enable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = mapper.createGenerator(out)) {
            g.writeStartObject();
            g.writeName("zebra");
            g.writeNumber(3);
            g.writeName("apple");
            g.writeNumber(1);
            g.writeName("mango");
            g.writeNumber(2);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        // First byte should be 0x14 (compact object)
        assertThat(bytes[0]).isEqualTo((byte) 0x14);
        // Parse and verify all keys present
        try (JsonParser p = new VPackMapper().createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            java.util.Map<String, Integer> result = new java.util.LinkedHashMap<>();
            while (p.nextToken() == JsonToken.PROPERTY_NAME) {
                String key = p.getString();
                p.nextToken();
                result.put(key, p.getIntValue());
            }
            assertThat(result ).hasSize(3);
            assertThat(result.get("apple").intValue()).isEqualTo(1);
            assertThat(result.get("mango").intValue()).isEqualTo(2);
            assertThat(result.get("zebra").intValue()).isEqualTo(3);
        }
    }

    // =========================================================
    // _extractStringBytes with long key (> 126 chars)
    // =========================================================

    @Test
    public void testSortedObject_withLongKeys() {
        VPackMapper mapper = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .build();
        // Build keys longer than 126 chars (forces long-string encoding 0xbf in key)
        String longKeyA = "aaa" + "x".repeat(130); // > 126 chars, sorts before bbb...
        String longKeyB = "bbb" + "x".repeat(130);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = mapper.createGenerator(out)) {
            g.writeStartObject();
            g.writeName(longKeyB);
            g.writeNumber(2);
            g.writeName(longKeyA);
            g.writeNumber(1);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length ).isPositive();
        // Parse and verify the sort order
        try (JsonParser p = new VPackMapper().createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            String firstKey = p.getString();
            p.nextToken(); // value
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            String secondKey = p.getString();
            p.nextToken();
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
            // With sorted keys, longKeyA (aaa...) comes before longKeyB (bbb...)
            assertThat(firstKey).isEqualTo(longKeyA);
            assertThat(secondKey).isEqualTo(longKeyB);
        }
    }

    // =========================================================
    // writeNumber(String) paths
    // =========================================================

    @Test
    public void testWriteNumberString_integerValue() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber("12345");
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(12345);
        }
    }

    @Test
    public void testWriteNumberString_floatValue() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber("3.14");
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
        }
    }

    @Test
    public void testWriteNumberString_scientificNotation() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber("1e5");
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
        }
    }

    @Test
    public void testWriteNumberString_veryLargeBigInteger() {
        // A very large integer that exceeds long range — falls into BigDecimal path
        String bigInt = "99999999999999999999999999999";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber(bigInt);
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes.length ).isPositive();
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
        }
    }

    @Test
    public void testWriteNumberString_null_writesNull() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber((String) null);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
        }
    }

    // =========================================================
    // streamWriteOutputTarget / streamWriteOutputBuffered
    // =========================================================

    @Test
    public void testStreamWriteOutputTarget() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThat(g.streamWriteOutputTarget()).isSameAs(out);
        }
    }

    @Test
    public void testStreamWriteOutputBuffered() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            // Initially buffered = 0
            assertThat(g.streamWriteOutputBuffered()).isEqualTo(0);
        }
    }

    // =========================================================
    // getPrettyPrinter (always null for binary format)
    // =========================================================

    @Test
    public void testGetPrettyPrinter_isNull() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThat(g.getPrettyPrinter()).isNull();
        }
    }

    // =========================================================
    // currentValue / assignCurrentValue
    // =========================================================

    @Test
    public void testCurrentValue_assignCurrentValue() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThat(g.currentValue()).isNull(); // root has no value initially
            Object obj = new Object();
            g.assignCurrentValue(obj);
            assertThat(g.currentValue()).isSameAs(obj);
            g.writeNull();
        }
    }

    // =========================================================
    // streamWriteContext
    // =========================================================

    @Test
    public void testStreamWriteContext_returnedByMethod() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThat(g.streamWriteContext()).isNotNull();
        }
    }

    // =========================================================
    // flush() triggers output
    // =========================================================

    @Test
    public void testFlush_sendsDataToOutput() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNull();
            g.flush();
            // After flush, data should be in output stream
            assertThat(out.toByteArray().length).isPositive();
        }
    }

    // =========================================================
    // writeName(SerializableString)
    // =========================================================

    @Test
    public void testWriteName_serializableString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            // Use SerializableString via writeName(SerializableString) with SerializedString impl
            g.writeName(new tools.jackson.core.io.SerializedString("myKey"));
            g.writeNumber(99);
            g.writeEndObject();
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("myKey");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(99);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // writeNumber(short)
    // =========================================================

    @Test
    public void testWriteNumber_short() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber((short) 255);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(255);
        }
    }

    // =========================================================
    // writeNumber(float) — delegates to writeNumber(double)
    // =========================================================

    @Test
    public void testWriteNumber_float() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber(1.5f);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDoubleValue()).isEqualTo(1.5, offset(0.01));
        }
    }

}
