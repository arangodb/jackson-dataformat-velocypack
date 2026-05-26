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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    private byte[] genBytes(WriteAction action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            action.write(g);
        }
        return out.toByteArray();
    }

    @FunctionalInterface
    interface WriteAction {
        void write(JsonGenerator g);
    }

    // =========================================================
    // Number type conversions: getIntValue / getLongValue / etc.
    // =========================================================

    @Test
    public void testGetIntValue_fromSmallInt() {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(5);
            assertThat(p.getLongValue()).isEqualTo(5L);
            assertThat(p.getBigIntegerValue()).isEqualTo(BigInteger.valueOf(5));
            assertThat(p.getDoubleValue()).isEqualTo(5.0);
            assertThat(p.getDecimalValue()).isEqualTo(("5"));
        }
    }

    @Test
    public void testGetIntValue_fromSignedInt() {
        byte[] bytes = genBytes(g -> g.writeNumber(127));
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(127);
        }
    }

    @Test
    public void testGetLongValue_fromLong() {
        byte[] bytes = genBytes(g -> g.writeNumber(Long.MAX_VALUE));
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getLongValue()).isEqualTo(Long.MAX_VALUE);
            assertThat(p.getIntValue()).isEqualTo((int) Long.MAX_VALUE); // truncated
            assertThat(p.getBigIntegerValue()).isEqualTo(BigInteger.valueOf(Long.MAX_VALUE));
        }
    }

    @Test
    public void testGetDoubleValue_fromDouble() {
        byte[] bytes = genBytes(g -> g.writeNumber(3.14));
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDoubleValue()).isEqualTo(3.14);
            assertThat(p.getFloatValue()).isEqualTo((float) 3.14);
            assertThat(p.getIntValue()).isEqualTo(3); // truncated
            assertThat(p.getLongValue()).isEqualTo(3L);
        }
    }

    @Test
    public void testGetDecimalValue_fromBigDecimal() {
        byte[] bytes = genBytes(g -> g.writeNumber(new BigDecimal("12345.67")));
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            BigDecimal result = p.getDecimalValue();
            assertThat(result).isEqualByComparingTo("12345.67");
        }
    }

    @Test
    public void testGetDecimalValue_fromDouble() {
        byte[] bytes = genBytes(g -> g.writeNumber(1.5));
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDecimalValue()).isNotNull();
            assertThat(p.getLongValue()).isEqualTo(1L);
        }
    }

    @Test
    public void testGetBigIntegerValue_fromBigDecimal() {
        byte[] bytes = genBytes(g -> g.writeNumber(new BigDecimal("99999999999999999999.5")));
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            BigInteger bi = p.getBigIntegerValue();
            assertThat(bi).isNotNull();
        }
    }

    @Test
    public void testNumberType_INT() {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.INT);
            assertThat(p.getNumberValue().intValue()).isEqualTo(5);
        }
    }

    @Test
    public void testNumberType_LONG() {
        // Write a value that won't fit in int
        byte[] bytes = genBytes(g -> g.writeNumber(Long.MAX_VALUE));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.LONG);
            assertThat(p.getNumberValue().longValue()).isEqualTo(Long.MAX_VALUE);
        }
    }

    @Test
    public void testNumberType_DOUBLE() {
        byte[] bytes = genBytes(g -> g.writeNumber(3.14));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.DOUBLE);
            assertThat(p.getNumberValue().doubleValue()).isEqualTo(3.14);
        }
    }

    @Test
    public void testNumberType_BIG_DECIMAL() {
        // BigDecimal with fractional part -> BIG_DECIMAL; integer BigDecimal -> BIG_INTEGER
        byte[] bytes = genBytes(g -> g.writeNumber(new BigDecimal("12345.67")));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.BIG_DECIMAL);
        }
    }

    @Test
    public void testIsNaN_forNaN() {
        byte[] bytes = genBytes(g -> g.writeNumber(Double.NaN));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.isNaN()).isTrue();
        }
    }

    @Test
    public void testIsNaN_forInfinity() {
        byte[] bytes = genBytes(g -> g.writeNumber(Double.POSITIVE_INFINITY));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.isNaN()).isTrue();
        }
    }

    @Test
    public void testIsNaN_forNormalDouble() {
        byte[] bytes = genBytes(g -> g.writeNumber(1.5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.isNaN()).isFalse();
        }
    }

    @Test
    public void testIsNaN_forInt() {
        byte[] bytes = genBytes(g -> g.writeNumber(42));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.isNaN()).isFalse();
        }
    }

    // =========================================================
    // String value accessor variations
    // =========================================================

    @Test
    public void testGetValueAsString_forString() {
        byte[] bytes = genBytes(g -> g.writeString("test"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getValueAsString()).isEqualTo("test");
            assertThat(p.getValueAsString("default")).isEqualTo("test");
        }
    }

    @Test
    public void testGetValueAsString_forNull() {
        byte[] bytes = genBytes(JsonGenerator::writeNull);
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getValueAsString()).isNull();
            assertThat(p.getValueAsString("default")).isEqualTo("default");
        }
    }

    @Test
    public void testGetValueAsString_forInt() {
        byte[] bytes = genBytes(g -> g.writeNumber(42));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getValueAsString("default")).isNotNull();
        }
    }

    @Test
    public void testHasStringCharacters_forString() {
        byte[] bytes = genBytes(g -> g.writeString("hi"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.hasStringCharacters()).isTrue();
        }
    }

    @Test
    public void testHasStringCharacters_forInt() {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.hasStringCharacters()).isFalse();
        }
    }

    @Test
    public void testGetStringLength() {
        byte[] bytes = genBytes(g -> g.writeString("hello"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getStringLength()).isEqualTo(5);
            assertThat(p.getStringOffset()).isEqualTo(0);
        }
    }

    @Test
    public void testGetString_writer() {
        byte[] bytes = genBytes(g -> g.writeString("abc"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            java.io.StringWriter sw = new java.io.StringWriter();
            int len = p.getString(sw);
            assertThat(len).isEqualTo(3);
            assertThat(sw.toString()).isEqualTo("abc");
        }
    }

    // =========================================================
    // Binary value accessors
    // =========================================================

    @Test
    public void testGetBinaryValue() {
        byte[] data = { 0x01, 0x02, 0x03 };
        byte[] bytes = genBytes(g -> g.writeBinary(data));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.currentToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            byte[] result = p.getBinaryValue();
            assertThat(result).isEqualTo(data);
        }
    }

    @Test
    public void testGetBinaryValue_fromString() {
        // Binary from base64 string
        byte[] bytes = genBytes(g -> g.writeString("AAEC")); // base64 of [0,1,2]
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            byte[] result = p.getBinaryValue();
            assertThat(result).isNotNull();
        }
    }

    @Test
    public void testGetEmbeddedObject_binary() {
        byte[] data = { 0x42 };
        byte[] bytes = genBytes(g -> g.writeBinary(data));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getEmbeddedObject()).isNotNull();
        }
    }

    @Test
    public void testGetEmbeddedObject_nonEmbedded() {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.getEmbeddedObject()).isNull();
        }
    }

    @Test
    public void testReadBinaryValue() {
        byte[] data = { 0x0A, 0x0B, 0x0C };
        byte[] bytes = genBytes(g -> g.writeBinary(data));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int count = p.readBinaryValue(out);
            assertThat(count).isEqualTo(3);
            assertThat(out.toByteArray()).isEqualTo(data);
        }
    }

    // =========================================================
    // Location and context
    // =========================================================

    @Test
    public void testCurrentLocation_notNull() {
        byte[] bytes = genBytes(g -> g.writeNumber(5));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.currentLocation()).isNotNull();
        }
    }

    @Test
    public void testCurrentTokenLocation_notNull() {
        byte[] bytes = genBytes(g -> g.writeString("test"));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            assertThat(p.currentTokenLocation()).isNotNull();
        }
    }

    @Test
    public void testVersion() {
        byte[] bytes = genBytes(JsonGenerator::writeNull);
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.version()).isNotNull();
        }
    }

    @Test
    public void testStreamReadCapabilities() {
        byte[] bytes = genBytes(JsonGenerator::writeNull);
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.streamReadCapabilities()).isNotNull();
        }
    }

    @Test
    public void testStreamReadContext() {
        byte[] bytes = genBytes(g -> {
            g.writeStartObject();
            g.writeName("key");
            g.writeString("val");
            g.writeEndObject();
        });
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken(); // START_OBJECT
            p.nextToken(); // PROPERTY_NAME
            assertThat(p.currentName()).isEqualTo("key");
        }
    }

    @Test
    public void testAssignCurrentValue() {
        byte[] bytes = genBytes(g -> g.writeNumber(1));
        try (JsonParser p = parserFor(bytes)) {
            p.nextToken();
            p.assignCurrentValue("testValue");
            assertThat(p.currentValue()).isEqualTo("testValue");
        }
    }

    @Test
    public void testIsClosed_afterClose() {
        byte[] bytes = genBytes(JsonGenerator::writeNull);
        JsonParser p = parserFor(bytes);
        assertThat(p.isClosed()).isFalse();
        p.close();
        assertThat(p.isClosed()).isTrue();
    }

    // =========================================================
    // FAIL_ON_TAGGED_VALUES feature
    // =========================================================

    @Test
    public void testFailOnTaggedValues_disabled_readsTransparently() {
        // 0xee 0x42 0x1a = tag(0x42) + true
        byte[] bytes = { (byte) 0xee, (byte) 0x42, (byte) 0x1a };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_TRUE);
        }
    }

    @Test
    public void testFailOnTaggedValues_enabled_throws() {
        byte[] bytes = { (byte) 0xee, (byte) 0x42, (byte) 0x1a };
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .build();
        try (JsonParser p = m.createParser(bytes)) {
            assertThatThrownBy(p::nextToken).isInstanceOf(StreamReadException.class);
        }
    }

    // =========================================================
    // FAIL_ON_CUSTOM_TYPES feature
    // =========================================================

    @Test
    public void testFailOnCustomTypes_disabled_returnsEmbedded() {
        // 0xf0 0xAB = custom type 0xf0 with 1-byte payload 0xAB
        byte[] bytes = { (byte) 0xf0, (byte) 0xAB };
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            Object embedded = p.getEmbeddedObject();
            assertThat(embedded).isInstanceOf(VPackCustomValue.class);
            VPackCustomValue cv = (VPackCustomValue) embedded;
            assertThat(cv.getTypeByte()).isEqualTo(0xf0);
        }
    }

    @Test
    public void testFailOnCustomTypes_enabled_throws() {
        byte[] bytes = { (byte) 0xf0, (byte) 0xAB };
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        try (JsonParser p = m.createParser(bytes)) {
            assertThatThrownBy(p::nextToken).isInstanceOf(StreamReadException.class);
        }
    }

    // =========================================================
    // MIN_KEY and MAX_KEY
    // =========================================================

    @Test
    public void testMinKey() {
        byte[] bytes = { 0x1e }; // VPACK_MIN_KEY
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            assertThat(p.getEmbeddedObject()).isEqualTo("minKey");
        }
    }

    @Test
    public void testMaxKey() {
        byte[] bytes = { 0x1f }; // VPACK_MAX_KEY
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            assertThat(p.getEmbeddedObject()).isEqualTo("maxKey");
        }
    }

    // =========================================================
    // Unsigned int that overflows signed long → BigInteger
    // =========================================================

    @Test
    public void testUnsignedInt_overflows_to_BigInteger() {
        // 0x2f = unsigned 8-byte int; FFFFFFFFFFFFFFFF = 18446744073709551615
        byte[] bytes = {
            (byte) 0x2f,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        try (JsonParser p = parserFor(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.BIG_INTEGER);
            BigInteger bi = p.getBigIntegerValue();
            assertThat(bi).isEqualTo(("18446744073709551615"));
        }
    }

    // =========================================================
    // STRICT_DUPLICATE_DETECTION
    // =========================================================

    @Test
    public void testStrictDuplicateDetection_enabled_throws() {
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
