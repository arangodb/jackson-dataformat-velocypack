package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES)).isTrue();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isTrue();
    }

    @Test
    public void testBuilder_disable_readFeature_varargs() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .disable(VPackReadFeature.FAIL_ON_TAGGED_VALUES, VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES)).isFalse();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isFalse();
    }

    @Test
    public void testBuilder_enable_writeFeature_varargs() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS, VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS)).isTrue();
    }

    @Test
    public void testBuilder_disable_writeFeature_varargs() {
        VPackFactory f = VPackFactory.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED, VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)).isFalse();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH)).isFalse();
    }

    // =========================================================
    // VPackMapper: isEnabled, builder with factory, shared(), rebuild()
    // =========================================================

    @Test
    public void testMapper_isEnabled_readFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertThat(m.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isTrue();
        assertThat(m.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES)).isFalse();
    }

    @Test
    public void testMapper_isEnabled_writeFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertThat(m.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
    }

    @Test
    public void testMapper_shared_notNull() {
        assertThat(VPackMapper.shared()).isNotNull();
    }

    @Test
    public void testMapper_rebuild() {
        VPackMapper m = VPackMapper.builder().build();
        VPackMapper rebuilt = m.rebuild().build();
        assertThat(rebuilt).isNotNull();
    }

    @Test
    public void testMapper_builderWithFactory() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        VPackMapper m = VPackMapper.builder(f).build();
        assertThat(m).isNotNull();
    }

    @Test
    public void testMapper_version() {
        VPackMapper m = new VPackMapper();
        assertThat(m.version()).isNotNull();
    }

    @Test
    public void testMapper_tokenStreamFactory() {
        VPackMapper m = new VPackMapper();
        assertThat(m.tokenStreamFactory()).isNotNull();
        assertThat(m.tokenStreamFactory()).isInstanceOf(VPackFactory.class);
    }

    @Test
    public void testMapper_constructorWithFactory() {
        VPackFactory f = new VPackFactory();
        VPackMapper m = new VPackMapper(f);
        assertThat(m).isNotNull();
    }

    @Test
    public void testMapper_builder_disable_readFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .disable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertThat(m.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isFalse();
    }

    @Test
    public void testMapper_builder_disable_writeFeature() {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        assertThat(m.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH)).isFalse();
    }

    @Test
    public void testMapper_builder_configure_readFeature_false() {
        VPackMapper m = VPackMapper.builder()
                .configure(VPackReadFeature.FAIL_ON_CUSTOM_TYPES, true)
                .configure(VPackReadFeature.FAIL_ON_CUSTOM_TYPES, false)
                .build();
        assertThat(m.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isFalse();
    }

    @Test
    public void testMapper_builder_configure_writeFeature() {
        VPackMapper m = VPackMapper.builder()
                .configure(VPackWriteFeature.WRITE_COMPACT_ARRAYS, true)
                .build();
        assertThat(m.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
    }

    @Test
    public void testMapper_builder_configure_writeFeature_false() {
        VPackMapper m = VPackMapper.builder()
                .configure(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED, false)
                .build();
        assertThat(m.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)).isFalse();
    }

    // =========================================================
    // VPackParserBase: Number conversion from various starting types
    // =========================================================

    @Test
    public void testNumberConversion_bigint_from_long() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Long.MAX_VALUE);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.LONG);
            BigInteger bi = p.getBigIntegerValue();
            assertThat(bi).isEqualTo(BigInteger.valueOf(Long.MAX_VALUE));
        }
    }

    @Test
    public void testNumberConversion_bigint_from_int() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.INT);
            BigInteger bi = p.getBigIntegerValue();
            assertThat(bi).isEqualTo(BigInteger.valueOf(42));
        }
    }

    @Test
    public void testNumberConversion_bigint_from_double() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(3.14);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.DOUBLE);
            BigInteger bi = p.getBigIntegerValue();
            assertThat(bi).isNotNull();
        }
    }

    @Test
    public void testNumberConversion_long_from_int() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.INT);
            long v = p.getLongValue();
            assertThat(v).isEqualTo(42L);
        }
    }

    @Test
    public void testNumberConversion_long_from_bigdecimal() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(new BigDecimal("12345"));
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.BIG_DECIMAL);
            long v = p.getLongValue();
            assertThat(v).isEqualTo(12345L);
        }
    }

    @Test
    public void testNumberConversion_long_from_double() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(7.0);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.DOUBLE);
            long v = p.getLongValue();
            assertThat(v).isEqualTo(7L);
        }
    }

    @Test
    public void testNumberConversion_long_from_bigint() {
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.BIG_INTEGER);
            long v = p.getLongValue();
            assertThat(v).isEqualTo(-1L);
        }
    }

    @Test
    public void testNumberConversion_int_from_long() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Long.MAX_VALUE);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.LONG);
            int v = p.getIntValue();
            assertThat(v).isEqualTo((int) Long.MAX_VALUE);
        }
    }

    @Test
    public void testNumberConversion_int_from_bigint() {
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            int v = p.getIntValue();
            assertThat(v).isEqualTo(-1);
        }
    }

    @Test
    public void testNumberConversion_int_from_bigdecimal() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(new BigDecimal("99"));
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.BIG_DECIMAL);
            int v = p.getIntValue();
            assertThat(v).isEqualTo(99);
        }
    }

    @Test
    public void testNumberConversion_int_from_double() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(3.7);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            int v = p.getIntValue();
            assertThat(v).isEqualTo(3);
        }
    }

    @Test
    public void testNumberConversion_double_from_int() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(5);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.INT);
            double d = p.getDoubleValue();
            assertThat(d).isEqualTo(5.0);
        }
    }

    @Test
    public void testNumberConversion_double_from_long() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(1000L);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            double d = p.getDoubleValue();
            assertThat(d).isEqualTo(1000.0);
        }
    }

    @Test
    public void testNumberConversion_double_from_bigint() {
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            double d = p.getDoubleValue();
            assertThat(Double.isNaN(d)).isFalse();
        }
    }

    @Test
    public void testNumberConversion_decimal_from_int() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            BigDecimal bd = p.getDecimalValue();
            assertThat(bd).isEqualTo("42");
        }
    }

    @Test
    public void testNumberConversion_decimal_from_long() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(1000L);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            long lv = p.getLongValue();
            BigDecimal bd = p.getDecimalValue();
            assertThat(bd).isEqualTo(BigDecimal.valueOf(lv));
        }
    }

    @Test
    public void testNumberConversion_decimal_from_bigint() {
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.BIG_INTEGER);
            BigDecimal bd = p.getDecimalValue();
            assertThat(bd).isNotNull();
        }
    }

    // =========================================================
    // VPackParser: BCD (BigDecimal) round-trip inside container
    // =========================================================

    @Test
    public void testBcdInArray_roundTrip() {
        VPackMapper m = new VPackMapper();
        List<BigDecimal> original = Arrays.asList(
                new BigDecimal("12345.67"),
                new BigDecimal("-99.5"));
        byte[] bytes = m.writeValueAsBytes(original);
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            BigDecimal d1 = p.getDecimalValue();
            assertThat(d1).isEqualTo("12345.67");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            BigDecimal d2 = p.getDecimalValue();
            assertThat(d2).isEqualTo("-99.5");
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testBcdInObject_roundTrip() {
        VPackMapper m = new VPackMapper();
        Map<String, BigDecimal> original = new LinkedHashMap<>();
        original.put("val", new BigDecimal("1.5"));
        byte[] bytes = m.writeValueAsBytes(original);
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("val");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDecimalValue()).isEqualTo("1.5");
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // VPackParser: binary in container
    // =========================================================

    @Test
    public void testBinaryInArray() {
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeStartArray();
            g.writeBinary(new byte[]{0x01, 0x02});
            g.writeEndArray();
        }
        byte[] bytes = baos.toByteArray();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            byte[] result = p.getBinaryValue();
            assertThat(result).isEqualTo(new byte[]{0x01, 0x02});
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testBinaryInObject() {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("data");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            byte[] result = p.getBinaryValue();
            assertThat(result).isEqualTo(new byte[]{0x0A, 0x0B, 0x0C});
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // VPackParser: custom type in container (ParseFrame.parseValueInBuf)
    // =========================================================

    @Test
    public void testCustomTypeInArray_fromBuf() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = {
            0x13,       // VPACK_ARRAY_COMPACT
            0x05,       // VByte total length = 5
            (byte) 0xf0, (byte) 0xAB, // custom type 0xf0 + 1-byte payload
            0x01        // reversed VByte nritems = 1
        };
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            Object obj = p.getEmbeddedObject();
            assertThat(obj).isInstanceOf(VPackCustomValue.class);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // VPackParser: integer key in object
    // =========================================================

    @Test
    public void testObjectWithSmallIntKey() {
        byte[] bytes = {
            0x0b,       // sorted object, 1-byte width
            0x06,       // byteLen = 6
            0x01,       // nritems = 1
            0x30,       // key: small int 0
            0x1a,       // value: true
            0x03        // offset
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("0");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_TRUE);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // VPackParser: no-index array with padding bytes
    // =========================================================

    @Test
    public void testNoIndexArray_withNegativeSmallInts() {
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
        assertThat(bytes[0]).isEqualTo((byte) 0x02);
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(-1);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(-2);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(-3);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // VPackParser: compact object round-trip
    // =========================================================

    @Test
    public void testCompactObject_withManyKeys_roundTrip() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        Map<String, Integer> original = new LinkedHashMap<>();
        for (int i = 0; i < 20; i++) {
            original.put("key" + i, i);
        }
        byte[] bytes = m.writeValueAsBytes(original);
        assertThat(bytes[0]).isEqualTo((byte) 0x14);
        Map<?, ?> result = m.readValue(bytes, Map.class);
        assertThat(result).hasSize(20);
        for (int i = 0; i < 20; i++) {
            assertThat(((Number) result.get("key" + i)).intValue()).isEqualTo(i);
        }
    }

    // =========================================================
    // VPackParser: getNumberTypeFP
    // =========================================================

    @Test
    public void testGetNumberTypeFP_double() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(3.14);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberTypeFP()).isEqualTo(JsonParser.NumberTypeFP.DOUBLE64);
        }
    }

    @Test
    public void testGetNumberTypeFP_bigDecimal() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(new BigDecimal("1.5"));
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberTypeFP()).isEqualTo(JsonParser.NumberTypeFP.BIG_DECIMAL);
        }
    }

    @Test
    public void testGetNumberTypeFP_integer() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberTypeFP()).isEqualTo(JsonParser.NumberTypeFP.UNKNOWN);
        }
    }

    @Test
    public void testGetNumberValueExact() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        try (JsonParser p = m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getNumberValueExact()).isNotNull();
        }
    }

    // =========================================================
    // VPackParser: minKey/maxKey in container
    // =========================================================

    @Test
    public void testMinKeyInArray() {
        byte[] bytes = {
            0x13,   // compact array
            0x04,   // totalLen = 4
            0x1e,   // VPACK_MIN_KEY
            0x01    // reversed VByte nritems = 1
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            assertThat(p.getEmbeddedObject()).isEqualTo("minKey");
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testMaxKeyInArray() {
        byte[] bytes = {
            0x13,   // compact array
            0x04,   // totalLen = 4
            0x1f,   // VPACK_MAX_KEY
            0x01    // reversed VByte nritems = 1
        };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            assertThat(p.getEmbeddedObject()).isEqualTo("maxKey");
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // VPackParser: date value in array
    // =========================================================

    @Test
    public void testDateInArray() {
        byte[] bytes = new byte[12];
        bytes[0] = 0x13;  // compact array
        bytes[1] = 12;    // totalLen = 12
        bytes[2] = 0x1c;  // VPACK_DATE
        bytes[11] = 0x01; // reversed VByte nritems = 1
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getLongValue()).isEqualTo(0L);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // VPackParser: empty array/object in buf (nested)
    // =========================================================

    @Test
    public void testEmptyArrayInsideObject() {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("arr");
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    @Test
    public void testEmptyObjectInsideArray() {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // VPackGenerator: WRITE_MIN_INT_WIDTH disabled
    // =========================================================

    @Test
    public void testWriteMinIntWidth_disabled_smallNeg() {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeNumber(-3);
        }
        byte[] bytes = baos.toByteArray();
        assertThat(bytes[0]).isEqualTo((byte) 0x20);
        assertThat(bytes[1]).isEqualTo((byte) -3);
    }

    @Test
    public void testWriteMinIntWidth_disabled_smallPos() {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(baos)) {
            g.writeNumber(5);
        }
        byte[] bytes = baos.toByteArray();
        assertThat(bytes[0]).isEqualTo((byte) 0x20);
        assertThat(bytes[1]).isEqualTo((byte) 5);
    }

    // =========================================================
    // VPackFactory: readResolve (serialization)
    // =========================================================

    @Test
    public void testFactory_readResolve() throws IOException, ClassNotFoundException {
        VPackFactory f = new VPackFactory();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(f);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        VPackFactory f2 = (VPackFactory) ois.readObject();
        assertThat(f2).isNotNull();
        assertThat(f2.getFormatName()).isEqualTo("VelocyPack");
    }

    // =========================================================
    // VPackParserBase: _handleEOF in array/object
    // =========================================================

    @Test
    public void testHandleEOF_inRoot_noException() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = {};
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isNull();
        }
    }

    // =========================================================
    // VPackParser: unsigned int in object value
    // =========================================================

    @Test
    public void testUnsignedIntInObject() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = {
            0x14,       // compact object
            0x07,       // totalLen = 7
            0x41, 0x76, // key "v" (short string len=1)
            0x28, (byte)0xFF, // value: unsigned 1-byte 255
            0x01        // reversed VByte nritems = 1
        };
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("v");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(255);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }
}
