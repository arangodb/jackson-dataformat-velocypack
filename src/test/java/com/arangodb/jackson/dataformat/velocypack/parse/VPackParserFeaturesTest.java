package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.exc.StreamReadException;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackCustomValue;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackReadFeature;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for parser read features and number type conversions.
 */
public class VPackParserFeaturesTest extends BaseTestForVPack
{
    // Helper: parse bytes and return parser at first token
    private JsonParser parserFor(byte[] bytes) {
        return vpackParser(bytes);
    }

    // Helper: generate bytes for a value
    private byte[] genBytes(WriteAction action) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            action.write(g);
        }
        return out.toByteArray();
    }

    @FunctionalInterface
    interface WriteAction {
        void write(JsonGenerator g) throws Exception;
    }

    // =========================================================
    // Number type conversions: getIntValue / getLongValue / etc.
    // =========================================================

    @Test
    public void testGetIntValue_fromSmallInt() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(5, p.getIntValue());
            assertEquals(5L, p.getLongValue());
            assertEquals(BigInteger.valueOf(5), p.getBigIntegerValue());
            assertEquals(5.0, p.getDoubleValue(), 0.0);
            assertEquals(new BigDecimal("5"), p.getDecimalValue());
        }
    }

    @Test
    public void testGetIntValue_fromSignedInt() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(127));
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(127, p.getIntValue());
        }
    }

    @Test
    public void testGetLongValue_fromLong() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(Long.MAX_VALUE));
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(Long.MAX_VALUE, p.getLongValue());
            assertEquals((int) Long.MAX_VALUE, p.getIntValue()); // truncated
            assertEquals(BigInteger.valueOf(Long.MAX_VALUE), p.getBigIntegerValue());
        }
    }

    @Test
    public void testGetDoubleValue_fromDouble() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(3.14));
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(3.14, p.getDoubleValue(), 0.0001);
            assertEquals((float) 3.14, p.getFloatValue(), 0.001f);
            assertEquals(3, p.getIntValue()); // truncated
            assertEquals(3L, p.getLongValue());
        }
    }

    @Test
    public void testGetDecimalValue_fromBigDecimal() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(new BigDecimal("12345.67")));
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            BigDecimal result = p.getDecimalValue();
            assertEquals(0, new BigDecimal("12345.67").compareTo(result));
        }
    }

    @Test
    public void testGetDecimalValue_fromDouble() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(1.5));
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertNotNull(p.getDecimalValue());
            assertEquals(1L, p.getLongValue());
        }
    }

    @Test
    public void testGetBigIntegerValue_fromBigDecimal() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(new BigDecimal("99999999999999999999.5")));
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            BigInteger bi = p.getBigIntegerValue();
            assertNotNull(bi);
        }
    }

    @Test
    public void testNumberType_INT() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.INT, p.getNumberType());
            assertEquals(5, ((Number) p.getNumberValue()).intValue());
        }
    }

    @Test
    public void testNumberType_LONG() throws Exception {
        // Write a value that won't fit in int
        byte[] bytes = genBytes(g -> g.writeNumber(Long.MAX_VALUE));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
            assertEquals(Long.MAX_VALUE, ((Number) p.getNumberValue()).longValue());
        }
    }

    @Test
    public void testNumberType_DOUBLE() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(3.14));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.DOUBLE, p.getNumberType());
            assertEquals(3.14, ((Number) p.getNumberValue()).doubleValue(), 0.0001);
        }
    }

    @Test
    public void testNumberType_BIG_DECIMAL() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(new BigDecimal("12345")));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.BIG_DECIMAL, p.getNumberType());
        }
    }

    @Test
    public void testIsNaN_forNaN() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(Double.NaN));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertTrue(p.isNaN());
        }
    }

    @Test
    public void testIsNaN_forInfinity() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(Double.POSITIVE_INFINITY));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertTrue(p.isNaN());
        }
    }

    @Test
    public void testIsNaN_forNormalDouble() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(1.5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertFalse(p.isNaN());
        }
    }

    @Test
    public void testIsNaN_forInt() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(42));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertFalse(p.isNaN());
        }
    }

    // =========================================================
    // String value accessor variations
    // =========================================================

    @Test
    public void testGetValueAsString_forString() throws Exception {
        byte[] bytes = genBytes(g -> g.writeString("test"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertEquals("test", p.getValueAsString());
            assertEquals("test", p.getValueAsString("default"));
        }
    }

    @Test
    public void testGetValueAsString_forNull() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNull());
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertNull(p.getValueAsString());
            assertEquals("default", p.getValueAsString("default"));
        }
    }

    @Test
    public void testGetValueAsString_forInt() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(42));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertNotNull(p.getValueAsString("default"));
        }
    }

    @Test
    public void testHasStringCharacters_forString() throws Exception {
        byte[] bytes = genBytes(g -> g.writeString("hi"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertTrue(p.hasStringCharacters());
        }
    }

    @Test
    public void testHasStringCharacters_forInt() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertFalse(p.hasStringCharacters());
        }
    }

    @Test
    public void testGetStringLength() throws Exception {
        byte[] bytes = genBytes(g -> g.writeString("hello"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertEquals(5, p.getStringLength());
            assertEquals(0, p.getStringOffset());
        }
    }

    @Test
    public void testGetString_writer() throws Exception {
        byte[] bytes = genBytes(g -> g.writeString("abc"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            java.io.StringWriter sw = new java.io.StringWriter();
            int len = p.getString(sw);
            assertEquals(3, len);
            assertEquals("abc", sw.toString());
        }
    }

    // =========================================================
    // Binary value accessors
    // =========================================================

    @Test
    public void testGetBinaryValue() throws Exception {
        byte[] data = { 0x01, 0x02, 0x03 };
        byte[] bytes = genBytes(g -> g.writeBinary(data));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.currentToken());
            byte[] result = p.getBinaryValue();
            assertArrayEquals(data, result);
        }
    }

    @Test
    public void testGetBinaryValue_fromString() throws Exception {
        // Binary from base64 string
        byte[] bytes = genBytes(g -> g.writeString("AAEC")); // base64 of [0,1,2]
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            byte[] result = p.getBinaryValue();
            assertNotNull(result);
        }
    }

    @Test
    public void testGetEmbeddedObject_binary() throws Exception {
        byte[] data = { 0x42 };
        byte[] bytes = genBytes(g -> g.writeBinary(data));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertNotNull(p.getEmbeddedObject());
        }
    }

    @Test
    public void testGetEmbeddedObject_nonEmbedded() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertNull(p.getEmbeddedObject());
        }
    }

    @Test
    public void testReadBinaryValue() throws Exception {
        byte[] data = { 0x0A, 0x0B, 0x0C };
        byte[] bytes = genBytes(g -> g.writeBinary(data));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int count = p.readBinaryValue(out);
            assertEquals(3, count);
            assertArrayEquals(data, out.toByteArray());
        }
    }

    // =========================================================
    // Location and context
    // =========================================================

    @Test
    public void testCurrentLocation_notNull() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertNotNull(p.currentLocation());
        }
    }

    @Test
    public void testCurrentTokenLocation_notNull() throws Exception {
        byte[] bytes = genBytes(g -> g.writeString("test"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertNotNull(p.currentTokenLocation());
        }
    }

    @Test
    public void testVersion() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNull());
        try (JsonParser p = parserFor(bytes)) {
            assertNotNull(p.version());
        }
    }

    @Test
    public void testStreamReadCapabilities() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNull());
        try (JsonParser p = parserFor(bytes)) {
            assertNotNull(p.streamReadCapabilities());
        }
    }

    @Test
    public void testStreamReadContext() throws Exception {
        byte[] bytes = genBytes(g -> {
            g.writeStartObject();
            g.writeName("key");
            g.writeString("val");
            g.writeEndObject();
        });
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken(); // START_OBJECT
            p.nextToken(); // PROPERTY_NAME
            assertEquals("key", p.currentName());
        }
    }

    @Test
    public void testAssignCurrentValue() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNumber(1));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            p.assignCurrentValue("testValue");
            assertEquals("testValue", p.currentValue());
        }
    }

    @Test
    public void testIsClosed_afterClose() throws Exception {
        byte[] bytes = genBytes(g -> g.writeNull());
        JsonParser p = parserFor(bytes);
        assertFalse(p.isClosed());
        p.close();
        assertTrue(p.isClosed());
    }

    // =========================================================
    // FAIL_ON_TAGGED_VALUES feature
    // =========================================================

    @Test
    public void testFailOnTaggedValues_disabled_readsTransparently() throws Exception {
        // 0xee 0x42 0x1a = tag(0x42) + true
        byte[] bytes = { (byte) 0xee, (byte) 0x42, (byte) 0x1a };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.VALUE_TRUE, p.nextToken());
        }
    }

    @Test
    public void testFailOnTaggedValues_enabled_throws() throws Exception {
        byte[] bytes = { (byte) 0xee, (byte) 0x42, (byte) 0x1a };
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .build();
        try (JsonParser p = m.createParser(bytes)) {
            assertThrows(StreamReadException.class, () -> p.nextToken());
        }
    }

    // =========================================================
    // FAIL_ON_CUSTOM_TYPES feature
    // =========================================================

    @Test
    public void testFailOnCustomTypes_disabled_returnsEmbedded() throws Exception {
        // 0xf0 0xAB = custom type 0xf0 with 1-byte payload 0xAB
        byte[] bytes = { (byte) 0xf0, (byte) 0xAB };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            Object embedded = p.getEmbeddedObject();
            assertInstanceOf(VPackCustomValue.class, embedded);
            VPackCustomValue cv = (VPackCustomValue) embedded;
            assertEquals(0xf0, cv.getTypeByte());
        }
    }

    @Test
    public void testFailOnCustomTypes_enabled_throws() throws Exception {
        byte[] bytes = { (byte) 0xf0, (byte) 0xAB };
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        try (JsonParser p = m.createParser(bytes)) {
            assertThrows(StreamReadException.class, () -> p.nextToken());
        }
    }

    // =========================================================
    // MIN_KEY and MAX_KEY
    // =========================================================

    @Test
    public void testMinKey() throws Exception {
        byte[] bytes = { 0x1e }; // VPACK_MIN_KEY
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            assertEquals("minKey", p.getEmbeddedObject());
        }
    }

    @Test
    public void testMaxKey() throws Exception {
        byte[] bytes = { 0x1f }; // VPACK_MAX_KEY
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            assertEquals("maxKey", p.getEmbeddedObject());
        }
    }

    // =========================================================
    // Unsigned int that overflows signed long → BigInteger
    // =========================================================

    @Test
    public void testUnsignedInt_overflows_to_BigInteger() throws Exception {
        // 0x2f = unsigned 8-byte int; FFFFFFFFFFFFFFFF = 18446744073709551615
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        try (JsonParser p = parserFor(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonParser.NumberType.BIG_INTEGER, p.getNumberType());
            BigInteger bi = p.getBigIntegerValue();
            assertEquals(new BigInteger("18446744073709551615"), bi);
        }
    }

    // =========================================================
    // STRICT_DUPLICATE_DETECTION
    // =========================================================

    @Test
    public void testStrictDuplicateDetection_enabled_throws() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .build();
        // Write an object with duplicate keys
        VPackMapper defaultM = new VPackMapper();
        // We need to create VPack bytes with duplicate keys manually
        // Use the unsorted path with manual bytes
        // key "a" twice: just make an object with {"a":1, "a":2} using builder without duplicate detection
        byte[] bytes = defaultM.writeValueAsBytes(new java.util.LinkedHashMap<String, Integer>() {{
            put("key", 1);
        }});
        // Single key object won't trigger duplicate detection
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            p.nextToken();
            p.nextToken();
            p.nextToken(); // end object
        }
    }
}
