package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import com.arangodb.jackson.dataformat.velocypack.*;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for VPackParser buffer-based parsing paths (ParseFrame.parseValueInBuf):
 * - Objects containing long strings, binary data, BCD floats, tagged values
 * - Custom type bytes with LEN2/LEN4/8-byte payloads inside arrays/objects
 * - Integer-keyed objects (writePropertyId)
 * - getString(Writer writer)
 * - getValueAsString() variants
 * - readBinaryValue(OutputStream)
 * - getBinaryValue from string (base64 decode)
 * - _valueByteSize for tagged values and custom types
 * - Nested arrays within objects, nested objects within arrays
 */
public class VPackParserBufTest extends BaseTestForVPack
{
    // =========================================================
    // Objects with long strings (> 126 chars) as values
    // =========================================================

    @Test
    public void testObjectWithLongStringValue() throws Exception {
        String longVal = "v".repeat(200);
        byte[] data = vpackBytes("{\"key\":\"" + longVal + "\"}");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("key", p.getString());
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals(longVal, p.getString());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Objects with long string keys
    // =========================================================

    @Test
    public void testObjectWithLongStringKey() throws Exception {
        // Long key > 126 chars
        String longKey = "k".repeat(150);
        byte[] data = vpackBytes("{\"" + longKey + "\":42}");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals(longKey, p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(42, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Objects with binary values
    // =========================================================

    @Test
    public void testObjectWithBinaryValue() throws Exception {
        // Manually construct an object with a binary value
        // Object: {"data": <binary bytes: 0x11 0x22 0x33>}
        // We use VPackGenerator to write binary inside object
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            g.writeName("data");
            g.writeBinary(new byte[]{0x11, 0x22, 0x33});
            g.writeEndObject();
        }
        byte[] data = out.toByteArray();
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("data", p.getString());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            byte[] bin = p.getBinaryValue();
            assertArrayEquals(new byte[]{0x11, 0x22, 0x33}, bin);
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Objects with BCD (BigDecimal) values
    // =========================================================

    @Test
    public void testObjectWithBigDecimalValue() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            g.writeName("price");
            g.writeNumber(new BigDecimal("99.99"));
            g.writeName("tax");
            g.writeNumber(new BigDecimal("-0.01"));
            g.writeEndObject();
        }
        byte[] data = out.toByteArray();
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("price", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(0, new BigDecimal("99.99").compareTo(p.getDecimalValue()));
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("tax", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(0, new BigDecimal("-0.01").compareTo(p.getDecimalValue()));
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Objects with tagged values
    // =========================================================

    @Test
    public void testObjectWithTaggedValue_1byte() throws Exception {
        // Manually build: object with tagged value as value
        // Key: "x" (0x41 + 'x'), Value: tagged (0xee, tag=7, then null 0x18)
        // Use a single-pair sorted object (0x0b = sorted 1-byte)
        // Total: type(1) + byteLen(1) + nritems(1) + key + value + idx(1)
        // key = 0x41 'x' = [0x41, 0x78] = 2 bytes
        // value = [0xee, 0x07, 0x18] = 3 bytes
        // header = 1 + 1 (byteLen) + 1 (nritems) = 3 bytes
        // pair starts at offset 3 from start
        // idx table: 1 entry x 1 byte = 1
        // total = 3 + 2 + 3 + 1 = 9
        byte[] obj = {
            (byte) 0x0b,    // sorted object, 1-byte widths
            9,              // byteLen = 9 (stored as 1 byte LE)
            1,              // nritems = 1
            0x41, 0x78,     // key: short string len=1, 'x'
            (byte) 0xee, 7, (byte) 0x18, // value: tagged (tag=7) + null
            3               // index: offset of pair[0] from start = 3 (absolute from A)
        };
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(obj)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("x", p.getString());
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertEquals(7L, p.getLastTagNumber());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Objects with nested arrays
    // =========================================================

    @Test
    public void testObjectWithNestedArray() throws Exception {
        byte[] data = vpackBytes("{\"nums\":[1,2,3],\"strs\":[\"a\",\"b\"]}");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            String key1 = p.getString();
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            p.nextToken(); p.nextToken(); p.nextToken(); // 1,2,3
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            String key2 = p.getString();
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            p.nextToken(); p.nextToken(); // "a","b"
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Arrays with nested objects
    // =========================================================

    @Test
    public void testArrayWithNestedObjects() throws Exception {
        byte[] data = vpackBytes("[{\"a\":1},{\"b\":2}]");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // Arrays with compact sub-arrays (WRITE_COMPACT_ARRAYS)
    // =========================================================

    @Test
    public void testArrayWithCompactSubArray() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = mapper.createGenerator(out)) {
            g.writeStartArray();
            g.writeStartArray(); // nested compact array
            g.writeNumber(10);
            g.writeNumber(20);
            g.writeEndArray();
            g.writeNumber(99);
            g.writeEndArray();
        }
        byte[] data = out.toByteArray();
        try (JsonParser p = new VPackMapper().createParser(data)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(10, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(20, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(99, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // Objects with compact sub-objects
    // =========================================================

    @Test
    public void testObjectWithCompactSubObject() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = mapper.createGenerator(out)) {
            g.writeStartObject();
            g.writeName("inner");
            g.writeStartObject();
            g.writeName("x");
            g.writeNumber(5);
            g.writeEndObject();
            g.writeEndObject();
        }
        byte[] data = out.toByteArray();
        try (JsonParser p = new VPackMapper().createParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("inner", p.getString());
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("x", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(5, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Integer-keyed objects (writePropertyId)
    // =========================================================

    @Test
    public void testObjectWithIntegerKey() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            g.writePropertyId(0L); // small int key = 0
            g.writeNumber(42);
            g.writeEndObject();
        }
        byte[] data = out.toByteArray();
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            // Integer keys parsed as strings
            assertNotNull(p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(42, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    @Test
    public void testObjectWithLargeIntegerKey() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeStartObject();
            g.writePropertyId(1000L); // unsigned int key
            g.writeNumber(99);
            g.writeEndObject();
        }
        byte[] data = out.toByteArray();
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            // The key should be parseable as a string representation of 1000
            assertEquals("1000", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // getString(Writer writer)
    // =========================================================

    @Test
    public void testGetString_writerInterface() throws Exception {
        byte[] data = vpackBytes("\"hello writer\"");
        try (VPackParser p = (VPackParser) vpackParser(data)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            StringWriter sw = new StringWriter();
            int len = p.getString(sw);
            assertEquals(12, len);
            assertEquals("hello writer", sw.toString());
        }
    }

    @Test
    public void testGetString_writer_nullString() throws Exception {
        byte[] data = vpackBytes("null");
        try (VPackParser p = (VPackParser) vpackParser(data)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            StringWriter sw = new StringWriter();
            int len = p.getString(sw);
            assertEquals(0, len);
        }
    }

    // =========================================================
    // getValueAsString() / getValueAsString(default)
    // =========================================================

    @Test
    public void testGetValueAsString_fromString() throws Exception {
        byte[] data = vpackBytes("\"test\"");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("test", p.getValueAsString());
            assertEquals("test", p.getValueAsString("default"));
        }
    }

    @Test
    public void testGetValueAsString_fromNull() throws Exception {
        byte[] data = vpackBytes("null");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertNull(p.getValueAsString());
            assertEquals("fallback", p.getValueAsString("fallback"));
        }
    }

    @Test
    public void testGetValueAsString_fromInt() throws Exception {
        byte[] data = vpackBytes("42");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            // non-string, non-null → super.getValueAsString(default)
            assertNotNull(p.getValueAsString());
        }
    }

    @Test
    public void testGetValueAsString_propertyName() throws Exception {
        byte[] data = vpackBytes("{\"myKey\":1}");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("myKey", p.getValueAsString());
        }
    }

    // =========================================================
    // readBinaryValue(OutputStream)
    // =========================================================

    @Test
    public void testReadBinaryValue_outputStream() throws Exception {
        byte[] payload = {0x10, 0x20, 0x30, 0x40};
        ByteArrayOutputStream genOut = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(genOut)) {
            g.writeBinary(payload);
        }
        try (VPackParser p = (VPackParser) vpackParser(genOut.toByteArray())) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            ByteArrayOutputStream dest = new ByteArrayOutputStream();
            int n = p.readBinaryValue(dest);
            assertEquals(4, n);
            assertArrayEquals(payload, dest.toByteArray());
        }
    }

    @Test
    public void testReadBinaryValue_fromNonBinary_returnsZero() throws Exception {
        byte[] data = vpackBytes("null");
        try (VPackParser p = (VPackParser) vpackParser(data)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            ByteArrayOutputStream dest = new ByteArrayOutputStream();
            int n = p.readBinaryValue(dest);
            assertEquals(0, n);
        }
    }

    // =========================================================
    // getBinaryValue from string (base64 decode)
    // =========================================================

    @Test
    public void testGetBinaryValue_fromBase64String() throws Exception {
        // "AQID" is base64 for bytes {0x01, 0x02, 0x03}
        byte[] data = vpackBytes("\"AQID\"");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            byte[] decoded = p.getBinaryValue();
            assertArrayEquals(new byte[]{0x01, 0x02, 0x03}, decoded);
        }
    }

    // =========================================================
    // Custom types with LEN2 payload inside arrays
    // =========================================================

    @Test
    public void testCustomTypeLEN2_inArray() throws Exception {
        // Build an array containing a custom-type-LEN2 value (0xf7)
        // Custom LEN2: type=0xf7, 2-byte LE length, then payload
        // Payload = 3 bytes: 0xAA, 0xBB, 0xCC
        // custom value bytes: [0xf7, 0x03, 0x00, 0xAA, 0xBB, 0xCC] = 6 bytes
        // No-index array with 1 item: type=0x02, byteLen = 1+1+6 = 8
        // [0x02, 0x08, 0xf7, 0x03, 0x00, 0xAA, 0xBB, 0xCC]
        byte[] arr = {
            0x02,                          // no-index array, 1-byte byteLen
            8,                             // total byte length = 8
            (byte)0xf7, 3, 0, (byte)0xAA, (byte)0xBB, (byte)0xCC // LEN2 custom: len=3
        };
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(arr)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            Object embedded = p.getEmbeddedObject();
            assertNotNull(embedded);
            assertTrue(embedded instanceof VPackCustomValue);
            VPackCustomValue cv = (VPackCustomValue) embedded;
            assertEquals(0xf7, cv.getTypeByte() & 0xFF);
            assertEquals(3, cv.getPayload().length);
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testCustomTypeLEN4_inArray() throws Exception {
        // Custom LEN4: type=0xfa, 4-byte LE length, then payload
        // Payload = 2 bytes: 0x11, 0x22
        // [0xfa, 0x02, 0x00, 0x00, 0x00, 0x11, 0x22] = 7 bytes
        // No-index array: type=0x02, byteLen=9
        byte[] arr = {
            0x02,                                  // no-index array, 1-byte byteLen
            9,                                     // total byte length = 9
            (byte)0xfa, 2, 0, 0, 0, 0x11, 0x22   // LEN4 custom: len=2
        };
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(arr)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            Object embedded = p.getEmbeddedObject();
            assertTrue(embedded instanceof VPackCustomValue);
            VPackCustomValue cv = (VPackCustomValue) embedded;
            assertEquals(0xfa, cv.getTypeByte() & 0xFF);
            assertEquals(2, cv.getPayload().length);
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testCustomTypeLEN8_inArray() throws Exception {
        // Custom LEN8: type=0xfd, 8-byte LE length, then payload
        // Payload = 2 bytes: 0x55, 0x66
        // [0xfd, 2, 0, 0, 0, 0, 0, 0, 0, 0x55, 0x66] = 11 bytes
        // No-index array: type=0x02, byteLen=13
        byte[] arr = new byte[13];
        arr[0] = 0x02;    // no-index array
        arr[1] = 13;      // byteLen
        arr[2] = (byte)0xfd; // LEN8 custom
        arr[3] = 2;       // len[0] = 2
        // arr[4..9] = 0 (LE 8-byte length: 2)
        arr[10] = (byte)0x55;
        arr[11] = (byte)0x66;
        // arr[12] = 0 (padding)
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(arr)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            Object embedded = p.getEmbeddedObject();
            assertTrue(embedded instanceof VPackCustomValue);
            VPackCustomValue cv = (VPackCustomValue) embedded;
            assertEquals(0xfd, cv.getTypeByte() & 0xFF);
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // Custom types FAIL_ON_CUSTOM_TYPES in buf context
    // =========================================================

    @Test
    public void testCustomType_inArray_failOnCustomTypes() throws Exception {
        VPackMapper failMapper = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        // Simple array with custom-1B value: [0x02, 5, 0xf0, 0xAB, 0x00]
        // Wait — custom 1B: type=0xf0, payload=1byte. Total=2. Array: type+byteLen+item = 1+1+2=4
        byte[] arr = {0x02, 4, (byte)0xf0, (byte)0xAB};
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser p = failMapper.createParser(arr)) {
                p.nextToken(); // START_ARRAY
                p.nextToken(); // Should throw
            }
        });
    }

    // =========================================================
    // Tagged values in buf context, FAIL_ON_TAGGED_VALUES
    // =========================================================

    @Test
    public void testTaggedValue_inArray_failOnTagged() throws Exception {
        VPackMapper failMapper = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .build();
        // Array with tagged null: [0x02, byteLen, 0xee, tag, 0x18]
        // byteLen = 1+1+3 = 5
        byte[] arr = {0x02, 5, (byte)0xee, 10, (byte)0x18};
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser p = failMapper.createParser(arr)) {
                p.nextToken(); // START_ARRAY
                p.nextToken(); // Should throw
            }
        });
    }

    @Test
    public void testTaggedValue_8byte_inArray() throws Exception {
        // 8-byte tag inside array: [0x02, byteLen, 0xef, tag8bytes(LE), 0x18]
        // tag value bytes: 0xef + 8-byte LE tag + 1-byte null = 10 bytes
        // no-index array: byteLen = 1(type) + 1(byteLen) + 10(items) = 12
        // But byteLen field value = total bytes = 12
        // remaining = 12 - 1(type) - 1(byteLen) = 10 ✓
        byte[] arr = new byte[12];
        arr[0] = 0x02;         // no-index array
        arr[1] = 12;           // byteLen = 12
        arr[2] = (byte)0xef;   // 8-byte tag
        arr[3] = 42;           // tag LE: bytes 3..10, tag[0]=42
        // arr[4..9] = 0 → tag number = 42
        arr[11] = (byte)0x18;  // null value follows at index 11 (pos 9 in remaining)
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(arr)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertEquals(42L, p.getLastTagNumber());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // MinKey / MaxKey inside arrays
    // =========================================================

    @Test
    public void testMinKey_MaxKey_inArray() throws Exception {
        // Manually construct array with min/max key
        // [0x02, 4, 0x1e, 0x1f] → array with two 1-byte values
        byte[] arr = {0x02, 4, (byte)0x1e, (byte)0x1f};
        try (JsonParser p = vpackParser(arr)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            assertEquals("minKey", p.getEmbeddedObject());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            assertEquals("maxKey", p.getEmbeddedObject());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // Empty array/object inside objects (buf path)
    // =========================================================

    @Test
    public void testObjectWithEmptyArrayValue() throws Exception {
        byte[] data = vpackBytes("{\"list\":[]}");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("list", p.getString());
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    @Test
    public void testObjectWithEmptyObjectValue() throws Exception {
        byte[] data = vpackBytes("{\"nested\":{}}");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("nested", p.getString());
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // hasStringCharacters
    // =========================================================

    @Test
    public void testHasStringCharacters_trueForString() throws Exception {
        byte[] data = vpackBytes("\"hello\"");
        try (VPackParser p = (VPackParser) vpackParser(data)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertTrue(p.hasStringCharacters());
        }
    }

    @Test
    public void testHasStringCharacters_trueForPropertyName() throws Exception {
        byte[] data = vpackBytes("{\"myProp\":1}");
        try (VPackParser p = (VPackParser) vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertTrue(p.hasStringCharacters());
        }
    }

    @Test
    public void testHasStringCharacters_falseForNull() throws Exception {
        byte[] data = vpackBytes("null");
        try (VPackParser p = (VPackParser) vpackParser(data)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertFalse(p.hasStringCharacters());
        }
    }

    // =========================================================
    // getEmbeddedObject
    // =========================================================

    @Test
    public void testGetEmbeddedObject_forBinary() throws Exception {
        byte[] payload = {1, 2, 3};
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            g.writeBinary(payload);
        }
        try (VPackParser p = (VPackParser) vpackParser(out.toByteArray())) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            // getEmbeddedObject returns _binaryValue when no embedded object
            byte[] embedded = (byte[]) p.getEmbeddedObject();
            assertArrayEquals(payload, embedded);
        }
    }

    @Test
    public void testGetEmbeddedObject_forNonEmbedded_returnsNull() throws Exception {
        byte[] data = vpackBytes("42");
        try (VPackParser p = (VPackParser) vpackParser(data)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertNull(p.getEmbeddedObject());
        }
    }

    // =========================================================
    // streamWriteCapabilities on generator
    // =========================================================

    @Test
    public void testGenerator_streamWriteCapabilities() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertNotNull(g.streamWriteCapabilities());
        }
    }

    // =========================================================
    // Objects with double values in buf
    // =========================================================

    @Test
    public void testObjectWithDoubleValue() throws Exception {
        byte[] data = vpackBytes("{\"pi\":3.14159}");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("pi", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(3.14159, p.getDoubleValue(), 1e-5);
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Objects with date values in buf
    // =========================================================

    @Test
    public void testObjectWithDateValue() throws Exception {
        // VPack date: 0x1c + 8-byte LE signed ms
        // Date = 1000ms from epoch
        // Object: {key: date_value}
        // Build via generator — date is not a standard JSON type so build manually
        // Build array with date value: [0x02, byteLen, 0x1c, ms8bytes]
        // ms = 1000 → little-endian = [0xe8, 0x03, 0, 0, 0, 0, 0, 0]
        byte[] arr = {
            0x02,                                    // no-index array
            11,                                      // byteLen = 1+1+9 = 11
            0x1c,                                    // date type byte
            (byte)0xe8, 0x03, 0, 0, 0, 0, 0, 0     // ms = 1000
        };
        try (JsonParser p = vpackParser(arr)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(1000L, p.getLongValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // Signed int values in buf with long range
    // =========================================================

    @Test
    public void testSignedIntLongRange_inArray() throws Exception {
        // Write a long value that won't fit in int
        byte[] data = vpackBytes("[" + Long.MAX_VALUE + "]");
        try (JsonParser p = vpackParser(data)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(Long.MAX_VALUE, p.getLongValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // Unsigned int overflow to BigInteger in buf
    // =========================================================

    @Test
    public void testUnsignedInt_bigInteger_inArray() throws Exception {
        // Build array with VPACK_INT_UNSIGNED 8-byte value = 0xFFFFFFFFFFFFFFFF (all ones)
        // type = 0x2f (VPACK_INT_UNSIGNED + 7 = 0x28 + 7)
        // [0x02, byteLen, 0x2f, 0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff]
        // byteLen = 1+1+9 = 11
        byte[] arr = {
            0x02, 11,
            0x2f,
            (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
            (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff
        };
        try (JsonParser p = vpackParser(arr)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            // 0xFFFFFFFFFFFFFFFF as BigInteger = 2^64 - 1
            java.math.BigInteger val = p.getBigIntegerValue();
            assertEquals(java.math.BigInteger.ONE.shiftLeft(64).subtract(java.math.BigInteger.ONE), val);
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }
}
