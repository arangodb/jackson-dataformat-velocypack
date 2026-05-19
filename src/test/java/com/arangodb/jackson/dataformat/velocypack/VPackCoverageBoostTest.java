package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Targeted tests to boost coverage for specific uncovered code paths.
 */
public class VPackCoverageBoostTest extends BaseTestForVPack
{
    // =========================================================
    // VPackFactoryBuilder: varargs enable/disable
    // =========================================================

    @Test
    public void testBuilder_enable_readFeature_varargs() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES, VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertTrue(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES));
        assertTrue(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    @Test
    public void testBuilder_disable_readFeature_varargs() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .disable(VPackReadFeature.FAIL_ON_TAGGED_VALUES, VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertFalse(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES));
        assertFalse(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    @Test
    public void testBuilder_enable_writeFeature_varargs() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS, VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        assertTrue(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
        assertTrue(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
    }

    @Test
    public void testBuilder_disable_writeFeature_varargs() {
        VPackFactory f = VPackFactory.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED, VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        assertFalse(f.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED));
        assertFalse(f.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH));
    }

    // =========================================================
    // VPackMapper: isEnabled, builder with factory, shared(), rebuild()
    // =========================================================

    @Test
    public void testMapper_isEnabled_readFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertTrue(m.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
        assertFalse(m.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES));
    }

    @Test
    public void testMapper_isEnabled_writeFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertTrue(m.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
    }

    @Test
    public void testMapper_shared_notNull() {
        assertNotNull(VPackMapper.shared());
    }

    @Test
    public void testMapper_rebuild() throws Exception {
        VPackMapper m = VPackMapper.builder().build();
        VPackMapper rebuilt = m.rebuild().build();
        assertNotNull(rebuilt);
    }

    @Test
    public void testMapper_builderWithFactory() throws Exception {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        VPackMapper m = VPackMapper.builder(f).build();
        assertNotNull(m);
    }

    @Test
    public void testMapper_version() throws Exception {
        VPackMapper m = new VPackMapper();
        assertNotNull(m.version());
    }

    @Test
    public void testMapper_tokenStreamFactory() throws Exception {
        VPackMapper m = new VPackMapper();
        assertNotNull(m.tokenStreamFactory());
        assertInstanceOf(VPackFactory.class, m.tokenStreamFactory());
    }

    @Test
    public void testMapper_constructorWithFactory() throws Exception {
        VPackFactory f = new VPackFactory();
        VPackMapper m = new VPackMapper(f);
        assertNotNull(m);
    }

    @Test
    public void testMapper_builder_disable_readFeature() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .disable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertFalse(m.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    @Test
    public void testMapper_builder_disable_writeFeature() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        assertFalse(m.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH));
    }

    @Test
    public void testMapper_builder_configure_readFeature_false() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .configure(VPackReadFeature.FAIL_ON_CUSTOM_TYPES, true)
                .configure(VPackReadFeature.FAIL_ON_CUSTOM_TYPES, false)
                .build();
        assertFalse(m.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    @Test
    public void testMapper_builder_configure_writeFeature() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .configure(VPackWriteFeature.WRITE_COMPACT_ARRAYS, true)
                .build();
        assertTrue(m.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
    }

    @Test
    public void testMapper_builder_configure_writeFeature_false() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .configure(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED, false)
                .build();
        assertFalse(m.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED));
    }

    // =========================================================
    // VPackParserBase: Number conversion from various starting types
    // =========================================================

    @Test
    public void testNumberConversion_bigint_from_long() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Long.MAX_VALUE);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
            BigInteger bi = p.getBigIntegerValue();
            assertEquals(BigInteger.valueOf(Long.MAX_VALUE), bi);
        }
    }

    @Test
    public void testNumberConversion_bigint_from_int() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.INT, p.getNumberType());
            BigInteger bi = p.getBigIntegerValue();
            assertEquals(BigInteger.valueOf(42), bi);
        }
    }

    @Test
    public void testNumberConversion_bigint_from_double() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(3.14);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.DOUBLE, p.getNumberType());
            BigInteger bi = p.getBigIntegerValue();
            assertNotNull(bi);
        }
    }

    @Test
    public void testNumberConversion_long_from_int() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.INT, p.getNumberType());
            long v = p.getLongValue();
            assertEquals(42L, v);
        }
    }

    @Test
    public void testNumberConversion_long_from_bigdecimal() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(new BigDecimal("12345"));
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.BIG_DECIMAL, p.getNumberType());
            long v = p.getLongValue();
            assertEquals(12345L, v);
        }
    }

    @Test
    public void testNumberConversion_long_from_double() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(7.0);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.DOUBLE, p.getNumberType());
            long v = p.getLongValue();
            assertEquals(7L, v);
        }
    }

    @Test
    public void testNumberConversion_long_from_bigint() throws Exception {
        // Use unsigned overflow BigInteger from parser
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.BIG_INTEGER, p.getNumberType());
            // getLongValue converts from BigInteger
            long v = p.getLongValue();
            // -1 as signed long representation of 0xFFFFFFFFFFFFFFFF
            assertEquals(-1L, v);
        }
    }

    @Test
    public void testNumberConversion_int_from_long() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Long.MAX_VALUE);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
            int v = p.getIntValue(); // truncated conversion
            assertEquals((int) Long.MAX_VALUE, v);
        }
    }

    @Test
    public void testNumberConversion_int_from_bigint() throws Exception {
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            // getIntValue converts from BigInteger
            int v = p.getIntValue();
            assertEquals(-1, v); // low bits of 0xFFFF...
        }
    }

    @Test
    public void testNumberConversion_int_from_bigdecimal() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(new BigDecimal("99"));
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.BIG_DECIMAL, p.getNumberType());
            int v = p.getIntValue();
            assertEquals(99, v);
        }
    }

    @Test
    public void testNumberConversion_int_from_double() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(3.7);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            int v = p.getIntValue();
            assertEquals(3, v);
        }
    }

    @Test
    public void testNumberConversion_double_from_int() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(5);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.INT, p.getNumberType());
            double d = p.getDoubleValue();
            assertEquals(5.0, d, 0.0);
        }
    }

    @Test
    public void testNumberConversion_double_from_long() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(1000L);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            double d = p.getDoubleValue();
            assertEquals(1000.0, d, 0.0);
        }
    }

    @Test
    public void testNumberConversion_double_from_bigint() throws Exception {
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            // Value 1 as unsigned 8-byte should be INT (not BigInteger since it's small)
            // Let's just verify it works
            double d = p.getDoubleValue();
            assertFalse(Double.isNaN(d));
        }
    }

    @Test
    public void testNumberConversion_decimal_from_int() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            BigDecimal bd = p.getDecimalValue();
            assertEquals(0, new BigDecimal("42").compareTo(bd));
        }
    }

    @Test
    public void testNumberConversion_decimal_from_long() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(1000L);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            // After getting longValue, convert to decimal
            long lv = p.getLongValue();
            BigDecimal bd = p.getDecimalValue();
            assertEquals(0, BigDecimal.valueOf(lv).compareTo(bd));
        }
    }

    @Test
    public void testNumberConversion_decimal_from_bigint() throws Exception {
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberType.BIG_INTEGER, p.getNumberType());
            BigDecimal bd = p.getDecimalValue();
            assertNotNull(bd);
        }
    }

    // =========================================================
    // VPackParser: BCD (BigDecimal) round-trip inside container
    // =========================================================

    @Test
    public void testBcdInArray_roundTrip() throws Exception {
        VPackMapper m = new VPackMapper();
        List<BigDecimal> original = Arrays.asList(
                new BigDecimal("12345.67"),
                new BigDecimal("-99.5"));
        byte[] bytes = m.writeValueAsBytes(original);
        // Parse back
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            BigDecimal d1 = p.getDecimalValue();
            assertEquals(0, new BigDecimal("12345.67").compareTo(d1));
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            BigDecimal d2 = p.getDecimalValue();
            assertEquals(0, new BigDecimal("-99.5").compareTo(d2));
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testBcdInObject_roundTrip() throws Exception {
        VPackMapper m = new VPackMapper();
        Map<String, BigDecimal> original = new LinkedHashMap<>();
        original.put("val", new BigDecimal("1.5"));
        byte[] bytes = m.writeValueAsBytes(original);
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("val", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(0, new BigDecimal("1.5").compareTo(p.getDecimalValue()));
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // VPackParser: binary in container  
    // =========================================================

    @Test
    public void testBinaryInArray() throws Exception {
        VPackMapper m = new VPackMapper();
        // Build array with binary data manually
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeStartArray();
            g.writeBinary(new byte[]{0x01, 0x02});
            g.writeEndArray();
        }
        byte[] bytes = baos.toByteArray();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            byte[] result = p.getBinaryValue();
            assertArrayEquals(new byte[]{0x01, 0x02}, result);
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testBinaryInObject() throws Exception {
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeStartObject();
            g.writeName("data");
            g.writeBinary(new byte[]{0x0A, 0x0B, 0x0C});
            g.writeEndObject();
        }
        byte[] bytes = baos.toByteArray();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("data", p.getString());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            byte[] result = p.getBinaryValue();
            assertArrayEquals(new byte[]{0x0A, 0x0B, 0x0C}, result);
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // VPackParser: custom type in container (ParseFrame.parseValueInBuf)
    // =========================================================

    @Test
    public void testCustomTypeInArray_fromBuf() throws Exception {
        // Build array manually with a custom type inside
        // [0xf0, 0xAB] is custom type 0xf0 with payload 0xAB
        // Array: 0x06 (indexed, 1-byte) + byteLen + nritems + [custom_value] + [offset]
        // Let's use a simpler approach: compact array
        VPackMapper m = new VPackMapper();
        // Build compact array containing a custom type byte
        // 0x13 (compact array) + vbyte_len + custom_value + rev_vbyte_nritems
        // Custom type: 0xf0 0xAB (2 bytes total)
        // nritems = 1, vbyte = 0x01
        // content = [0xf0, 0xAB]
        // totalLen = 1 (type) + 1 (len_vbyte) + 2 (content) + 1 (nritems_rev) = 5
        // len_vbyte = 5 as forward VByte = 0x05
        // nritems_rev (reversed) = reversed of VByte(1) = 0x01
        byte[] bytes = {
            0x13,       // VPACK_ARRAY_COMPACT
            0x05,       // VByte total length = 5
            (byte) 0xf0, (byte) 0xAB, // custom type 0xf0 + 1-byte payload
            0x01        // reversed VByte nritems = 1
        };
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            Object obj = p.getEmbeddedObject();
            assertInstanceOf(VPackCustomValue.class, obj);
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // VPackParser: integer key in object
    // =========================================================

    @Test
    public void testObjectWithSmallIntKey() throws Exception {
        // Build object with small int key (0x30 = 0)
        // This exercises the _readPropertyName small-int path
        // Object: 0x0b (sorted, 1-byte) + byteLen(1) + nritems(1) + [key_val_pair] + [offset]
        // key = 0x30 (small int 0), value = 0x1a (true)
        // pair = 2 bytes, header = 1+2 = 3 bytes, idxTable = 1 byte, total = 3+2+1 = 6
        byte[] bytes = {
            0x0b,       // sorted object, 1-byte width
            0x06,       // byteLen = 6
            0x01,       // nritems = 1
            0x30,       // key: small int 0
            0x1a,       // value: true
            0x03        // offset of pair: 1+2=3 (but offset stored relative to full VPack start)
                        // Actually offset is from start of value: 3
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("0", p.getString()); // small int key as string
            assertEquals(JsonToken.VALUE_TRUE, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // VPackParser: no-index array with padding bytes
    // =========================================================

    @Test
    public void testNoIndexArray_withNegativeSmallInts() throws Exception {
        // Small negative ints are also 1 byte each, so same-length array
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeStartArray();
            g.writeNumber(-1);
            g.writeNumber(-2);
            g.writeNumber(-3);
            g.writeEndArray();
        }
        byte[] bytes = baos.toByteArray();
        // All same length: no-index array
        assertEquals((byte) 0x02, bytes[0]);
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(-1, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(-2, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(-3, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // VPackParser: compact object round-trip
    // =========================================================

    @Test
    public void testCompactObject_withManyKeys_roundTrip() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        Map<String, Integer> original = new LinkedHashMap<>();
        for (int i = 0; i < 20; i++) {
            original.put("key" + i, i);
        }
        byte[] bytes = m.writeValueAsBytes(original);
        assertEquals((byte) 0x14, bytes[0]);
        Map<?, ?> result = m.readValue(bytes, Map.class);
        assertEquals(20, result.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, ((Number) result.get("key" + i)).intValue());
        }
    }

    // =========================================================
    // VPackParser: getNumberTypeFP
    // =========================================================

    @Test
    public void testGetNumberTypeFP_double() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(3.14);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberTypeFP.DOUBLE64, p.getNumberTypeFP());
        }
    }

    @Test
    public void testGetNumberTypeFP_bigDecimal() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(new BigDecimal("1.5"));
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberTypeFP.BIG_DECIMAL, p.getNumberTypeFP());
        }
    }

    @Test
    public void testGetNumberTypeFP_integer() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertEquals(JsonParser.NumberTypeFP.UNKNOWN, p.getNumberTypeFP());
        }
    }

    @Test
    public void testGetNumberValueExact() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertNotNull(p.getNumberValueExact());
        }
    }

    // =========================================================
    // VPackParser: minKey/maxKey in container
    // =========================================================

    @Test
    public void testMinKeyInArray() throws Exception {
        // Compact array with minKey value
        // totalLen = 1(type) + 1(len_vbyte) + 1(minKey) + 1(nritems) = 4
        byte[] bytes = {
            0x13,   // compact array
            0x04,   // totalLen = 4
            0x1e,   // VPACK_MIN_KEY
            0x01    // reversed VByte nritems = 1
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            assertEquals("minKey", p.getEmbeddedObject());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testMaxKeyInArray() throws Exception {
        byte[] bytes = {
            0x13,   // compact array
            0x04,   // totalLen = 4
            0x1f,   // VPACK_MAX_KEY
            0x01    // reversed VByte nritems = 1
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            assertEquals("maxKey", p.getEmbeddedObject());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // VPackParser: date value in array
    // =========================================================

    @Test
    public void testDateInArray() throws Exception {
        // Compact array with date value
        // date: 0x1c + 8 bytes LE = 9 bytes total
        // totalLen = 1 + 1 + 9 + 1 = 12
        byte[] bytes = new byte[12];
        bytes[0] = 0x13;  // compact array
        bytes[1] = 12;    // totalLen = 12
        bytes[2] = 0x1c;  // VPACK_DATE
        // 8 bytes of date (timestamp = 0)
        // bytes[3..10] = 0
        bytes[11] = 0x01; // reversed VByte nritems = 1
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(0L, p.getLongValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // VPackParser: empty array/object in buf (nested)
    // =========================================================

    @Test
    public void testEmptyArrayInsideObject() throws Exception {
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeStartObject();
            g.writeName("arr");
            g.writeStartArray();
            g.writeEndArray();
            g.writeEndObject();
        }
        byte[] bytes = baos.toByteArray();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("arr", p.getString());
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    @Test
    public void testEmptyObjectInsideArray() throws Exception {
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeStartArray();
            g.writeStartObject();
            g.writeEndObject();
            g.writeEndArray();
        }
        byte[] bytes = baos.toByteArray();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // VPackGenerator: WRITE_MIN_INT_WIDTH disabled
    // =========================================================

    @Test
    public void testWriteMinIntWidth_disabled_smallNeg() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeNumber(-3);
        }
        byte[] bytes = baos.toByteArray();
        // Should use signed encoding, not small neg
        assertEquals((byte) 0x20, bytes[0]); // 1-byte signed
        assertEquals((byte) -3, bytes[1]);
    }

    @Test
    public void testWriteMinIntWidth_disabled_smallPos() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeNumber(5);
        }
        byte[] bytes = baos.toByteArray();
        // Should use signed encoding, not small int
        assertEquals((byte) 0x20, bytes[0]); // 1-byte signed
        assertEquals((byte) 5, bytes[1]);
    }

    // =========================================================
    // VPackFactory: readResolve (serialization)
    // =========================================================

    @Test
    public void testFactory_readResolve() throws Exception {
        VPackFactory f = new VPackFactory();
        // Test serialization round-trip (exercises readResolve)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(f);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        VPackFactory f2 = (VPackFactory) ois.readObject();
        assertNotNull(f2);
        assertEquals("VelocyPack", f2.getFormatName());
    }

    // =========================================================
    // VPackParserBase: _handleEOF in array/object
    // =========================================================

    @Test
    public void testHandleEOF_inRoot_noException() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = {};
        try (JsonParser p = m.createParser(bytes)) {
            assertNull(p.nextToken()); // EOF in root = null
        }
    }

    // =========================================================
    // VPackParser: unsigned int in object value
    // =========================================================

    @Test
    public void testUnsignedIntInObject() throws Exception {
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeStartObject();
            g.writeName("val");
            // Write unsigned int 255 manually: 0x28 0xFF
            // (We can't write unsigned int directly via generator, but we can disable min-width)
        }
        // Instead, use builder approach with raw bytes in an object
        // Compact object with unsigned int value
        // key="v" (0x41 0x76), value=0x28 0xFF (unsigned 1-byte = 255)
        // nritems=1, content=4 bytes
        // totalLen = 1 + 1 + 4 + 1 = 7
        byte[] bytes = {
            0x14,       // compact object
            0x07,       // totalLen = 7
            0x41, 0x76, // key "v" (short string len=1)
            0x28, (byte)0xFF, // value: unsigned 1-byte 255
            0x01        // reversed VByte nritems = 1
        };
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("v", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(255, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }
}
