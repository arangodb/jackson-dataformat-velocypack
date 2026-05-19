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

import static org.junit.jupiter.api.Assertions.*;

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
public class VPackGeneratorAdvancedTest extends BaseTestForVPack
{
    // =========================================================
    // Second constructor with custom buffer
    // =========================================================

    @Test
    public void testSecondConstructor_customBuffer() throws Exception {
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
        assertEquals(1, result.length);
        assertEquals((byte) 0x18, result[0]); // 0x18 = VPACK_NULL
    }

    @Test
    public void testSecondConstructor_withOffset() throws Exception {
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
        assertTrue(result.length > 0);
    }

    // =========================================================
    // writeStartArray(forValue, size) overload
    // =========================================================

    @Test
    public void testWriteStartArray_withForValueAndSize() throws Exception {
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
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(1, p.nextToken() == JsonToken.VALUE_NUMBER_INT ? p.getIntValue() : -1);
            p.nextToken(); // 2
            p.nextToken(); // 3
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // writeStartObject(forValue, size) overload
    // =========================================================

    @Test
    public void testWriteStartObject_withForValueAndSize() throws Exception {
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
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // writeArray(int[]) shortcut
    // =========================================================

    @Test
    public void testWriteArray_intArray() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeArray(new int[]{10, 20, 30}, 0, 3);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(10, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(20, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(30, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testWriteArray_intArray_partialRange() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeArray(new int[]{10, 20, 30, 40}, 1, 2); // offset=1, length=2
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(20, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(30, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // writeArray(long[]) shortcut
    // =========================================================

    @Test
    public void testWriteArray_longArray() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeArray(new long[]{1000L, 2000L, 3000L}, 0, 3);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(1000L, p.getLongValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(2000L, p.getLongValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(3000L, p.getLongValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // writeArray(double[]) shortcut
    // =========================================================

    @Test
    public void testWriteArray_doubleArray() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeArray(new double[]{1.1, 2.2, 3.3}, 0, 3);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(1.1, p.getDoubleValue(), 1e-9);
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(2.2, p.getDoubleValue(), 1e-9);
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(3.3, p.getDoubleValue(), 1e-9);
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // writePropertyId (integer key)
    // =========================================================

    @Test
    public void testWritePropertyId_simple() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            g.writePropertyId(42L); // integer key
            g.writeNumber(100);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0);
        // Parse back: the integer key becomes a property name string
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            // integer key is represented as a string
            assertNotNull(p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(100, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // _rawBytes large data path (len > outputEnd → write directly to stream)
    // =========================================================

    @Test
    public void testWriteLargeString_exceedsBuffer() throws Exception {
        // Write a string larger than the output buffer to force direct-write path
        // The default output buffer is 8KB, so we need > 8KB of string data
        String largeStr = "x".repeat(10000); // 10KB
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeString(largeStr);
        }
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 10000);
        // Parse back
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals(largeStr, p.getString());
        }
    }

    @Test
    public void testWriteLargeObject_exceedsBuffer() throws Exception {
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
        assertTrue(bytes.length > 10000);
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            int count = 0;
            while (p.nextToken() == JsonToken.PROPERTY_NAME) {
                assertEquals(JsonToken.VALUE_STRING, p.nextToken());
                count++;
            }
            assertEquals(10, count);
        }
    }

    // =========================================================
    // _buildCompactObject with WRITE_OBJECT_KEYS_SORTED
    // =========================================================

    @Test
    public void testCompactObject_withSortedKeys() throws Exception {
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
        assertEquals((byte) 0x14, bytes[0]);
        // Parse and verify all keys present
        try (JsonParser p = new VPackMapper().createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            java.util.Map<String, Integer> result = new java.util.LinkedHashMap<>();
            while (p.nextToken() == JsonToken.PROPERTY_NAME) {
                String key = p.getString();
                p.nextToken();
                result.put(key, p.getIntValue());
            }
            assertEquals(3, result.size());
            assertEquals(1, result.get("apple").intValue());
            assertEquals(2, result.get("mango").intValue());
            assertEquals(3, result.get("zebra").intValue());
        }
    }

    // =========================================================
    // _extractStringBytes with long key (> 126 chars)
    // =========================================================

    @Test
    public void testSortedObject_withLongKeys() throws Exception {
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
        assertTrue(bytes.length > 0);
        // Parse and verify the sort order
        try (JsonParser p = new VPackMapper().createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            String firstKey = p.getString();
            p.nextToken(); // value
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            String secondKey = p.getString();
            p.nextToken();
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            // With sorted keys, longKeyA (aaa...) comes before longKeyB (bbb...)
            assertEquals(longKeyA, firstKey);
            assertEquals(longKeyB, secondKey);
        }
    }

    // =========================================================
    // writeNumber(String) paths
    // =========================================================

    @Test
    public void testWriteNumberString_integerValue() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber("12345");
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(12345, p.getIntValue());
        }
    }

    @Test
    public void testWriteNumberString_floatValue() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber("3.14");
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        }
    }

    @Test
    public void testWriteNumberString_scientificNotation() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber("1e5");
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        }
    }

    @Test
    public void testWriteNumberString_veryLargeBigInteger() throws Exception {
        // A very large integer that exceeds long range — falls into BigDecimal path
        String bigInt = "99999999999999999999999999999";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber(bigInt);
        }
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0);
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        }
    }

    @Test
    public void testWriteNumberString_null_writesNull() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber((String) null);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
        }
    }

    // =========================================================
    // streamWriteOutputTarget / streamWriteOutputBuffered
    // =========================================================

    @Test
    public void testStreamWriteOutputTarget() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertSame(out, g.streamWriteOutputTarget());
        }
    }

    @Test
    public void testStreamWriteOutputBuffered() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            // Initially buffered = 0
            assertEquals(0, g.streamWriteOutputBuffered());
        }
    }

    // =========================================================
    // getPrettyPrinter (always null for binary format)
    // =========================================================

    @Test
    public void testGetPrettyPrinter_isNull() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertNull(g.getPrettyPrinter());
        }
    }

    // =========================================================
    // currentValue / assignCurrentValue
    // =========================================================

    @Test
    public void testCurrentValue_assignCurrentValue() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertNull(g.currentValue()); // root has no value initially
            Object obj = new Object();
            g.assignCurrentValue(obj);
            assertSame(obj, g.currentValue());
            g.writeNull();
        }
    }

    // =========================================================
    // streamWriteContext
    // =========================================================

    @Test
    public void testStreamWriteContext_returnedByMethod() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertNotNull(g.streamWriteContext());
        }
    }

    // =========================================================
    // flush() triggers output
    // =========================================================

    @Test
    public void testFlush_sendsDataToOutput() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNull();
            g.flush();
            // After flush, data should be in output stream
            assertTrue(out.toByteArray().length > 0);
        }
    }

    // =========================================================
    // writeName(SerializableString)
    // =========================================================

    @Test
    public void testWriteName_serializableString() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            // Use SerializableString via writeName(SerializableString) with SerializedString impl
            g.writeName(new tools.jackson.core.io.SerializedString("myKey"));
            g.writeNumber(99);
            g.writeEndObject();
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("myKey", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(99, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // writeNumber(short)
    // =========================================================

    @Test
    public void testWriteNumber_short() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber((short) 255);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(255, p.getIntValue());
        }
    }

    // =========================================================
    // writeNumber(float) — delegates to writeNumber(double)
    // =========================================================

    @Test
    public void testWriteNumber_float() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeNumber(1.5f);
        }
        try (JsonParser p = vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(1.5, p.getDoubleValue(), 0.01);
        }
    }

    // =========================================================
    // Helper: testIOContext from BaseTestForVPack
    // =========================================================
}
