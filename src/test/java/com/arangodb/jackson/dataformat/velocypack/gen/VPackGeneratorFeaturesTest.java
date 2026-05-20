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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for VPackGenerator feature flags and scalar write variants.
 */
public class VPackGeneratorFeaturesTest extends BaseTestForVPack
{
    private byte[] gen(WriteAction action) {
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
    // Feature enable/disable/configure on VPackGenerator
    // =========================================================

    @Test
    public void testEnableDisableFeature() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (VPackGenerator g = (VPackGenerator) m.createGenerator(out)) {
            g.enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS);
            assertThat(g.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
            g.disable(VPackWriteFeature.WRITE_COMPACT_ARRAYS);
            assertThat(g.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isFalse();
        }
    }

    @Test
    public void testConfigureFeature() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (VPackGenerator g = (VPackGenerator) m.createGenerator(out)) {
            g.configure(VPackWriteFeature.WRITE_COMPACT_OBJECTS, true);
            assertThat(g.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS)).isTrue();
            g.configure(VPackWriteFeature.WRITE_COMPACT_OBJECTS, false);
            assertThat(g.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS)).isFalse();
        }
    }

    // =========================================================
    // writeRaw* throws UnsupportedOperationException
    // =========================================================

    @Test
    public void testWriteRaw_String_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeRaw("raw"))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    public void testWriteRaw_StringOffLen_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeRaw("raw", 0, 3))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    public void testWriteRaw_CharArray_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeRaw(new char[]{'a'}, 0, 1))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    public void testWriteRaw_Char_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeRaw('a'))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    public void testWriteRawValue_String_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeRawValue("raw"))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    public void testWriteRawValue_StringOffLen_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeRawValue("raw", 0, 3))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    public void testWriteRawValue_CharArray_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeRawValue(new char[]{'a'}, 0, 1))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // =========================================================
    // writeNumber(String) -- various formats
    // =========================================================

    @Test
    public void testWriteNumberString_integer() {
        byte[] bytes = gen(g -> g.writeNumber("42"));
        assertThat(bytes).hasSize(2);
        assertThat(bytes[0]).isEqualTo((byte) 0x20); // 1-byte signed int
        assertThat(bytes[1]).isEqualTo((byte) 42);
    }

    @Test
    public void testWriteNumberString_decimal() {
        byte[] bytes = gen(g -> g.writeNumber("3.14"));
        // Should produce BCD (BigDecimal path)
        assertThat(bytes.length > 1).isTrue();
        int tb = bytes[0] & 0xFF;
        assertThat(tb).as("Expected BCD type, got 0x" + Integer.toHexString(tb)).isBetween(0xc8 , 0xcf);
    }

    @Test
    public void testWriteNumberString_withExponent() {
        byte[] bytes = gen(g -> g.writeNumber("1e5"));
        assertThat(bytes.length ).isPositive();
    }

    @Test
    public void testWriteNumberString_null_writesNull() {
        byte[] bytes = gen(g -> g.writeNumber((String) null));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x18); // VPACK_NULL
    }

    @Test
    public void testWriteNumberString_invalid_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeNumber("not_a_number"))
                .isInstanceOf(tools.jackson.core.exc.StreamWriteException.class);
        }
    }

    @Test
    public void testWriteNumber_BigInteger_null_writesNull() {
        byte[] bytes = gen(g -> g.writeNumber((BigInteger) null));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x18);
    }

    @Test
    public void testWriteNumber_BigDecimal_null_writesNull() {
        byte[] bytes = gen(g -> g.writeNumber((BigDecimal) null));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x18);
    }

    @Test
    public void testWriteNumber_BigDecimal_negative() {
        byte[] bytes = gen(g -> g.writeNumber(new BigDecimal("-12345")));
        // Should produce negative BCD
        int tb = bytes[0] & 0xFF;
        assertThat(tb).as("Expected negative BCD type, got 0x" + Integer.toHexString(tb)).isBetween(0xd0, 0xd7);
    }

    @Test
    public void testWriteNumber_short() {
        byte[] bytes = gen(g -> g.writeNumber((short) 100));
        // Short is cast to int, 100 > 9 → 1-byte signed
        assertThat(bytes).hasSize(2);
        assertThat(bytes[0]).isEqualTo((byte) 0x20);
        assertThat(bytes[1]).isEqualTo((byte) 100);
    }

    @Test
    public void testWriteNumber_float() {
        byte[] bytes = gen(g -> g.writeNumber(1.5f));
        // Float is cast to double
        assertThat(bytes).hasSize(9);
        assertThat(bytes[0]).isEqualTo((byte) 0x1b); // VPACK_DOUBLE
    }

    // =========================================================
    // writeString variants
    // =========================================================

    @Test
    public void testWriteString_charArray() {
        char[] chars = {'h', 'i'};
        byte[] bytes = gen(g -> g.writeString(chars, 0, 2));
        assertThat(bytes).hasSize(3); // 0x42 'h' 'i'
        assertThat(bytes[0]).isEqualTo((byte) 0x42);
    }

    @Test
    public void testWriteString_serializableString() {
        byte[] bytes = gen(g -> g.writeString(new tools.jackson.core.io.SerializedString("ok")));
        assertThat(bytes).hasSize(3); // 0x42 'o' 'k'
        assertThat(bytes[0]).isEqualTo((byte) 0x42);
    }

    @Test
    public void testWriteRawUTF8String() {
        byte[] utf8 = "hi".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bytes = gen(g -> g.writeRawUTF8String(utf8, 0, utf8.length));
        assertThat(bytes).hasSize(3);
        assertThat(bytes[0]).isEqualTo((byte) 0x42);
    }

    @Test
    public void testWriteUTF8String() {
        byte[] utf8 = "ab".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bytes = gen(g -> g.writeUTF8String(utf8, 0, utf8.length));
        assertThat(bytes).hasSize(3);
        assertThat(bytes[0]).isEqualTo((byte) 0x42);
    }

    @Test
    public void testWriteString_null_writesNull() {
        byte[] bytes = gen(g -> g.writeString((String) null));
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x18);
    }

    // =========================================================
    // writeBinary via InputStream - success
    // =========================================================

    @Test
    public void testWriteBinary_fromInputStream_success() {
        byte[] data = { 0x01, 0x02, 0x03 };
        java.io.InputStream in = new java.io.ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            int written = g.writeBinary(in, 3);
            assertThat(written).isEqualTo(3);
        }
        byte[] bytes = out.toByteArray();
        // 0xc0 + 03 + data
        assertThat(bytes[0]).isEqualTo((byte) 0xc0);
        assertThat(bytes[1]).isEqualTo((byte) 0x03);
        assertThat(data).containsExactly(Arrays.copyOfRange(bytes, 2, 5));
    }

    // =========================================================
    // writePropertyId
    // =========================================================

    @Test
    public void testWritePropertyId() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeStartObject();
            g.writePropertyId(42L);
            g.writeNumber(99);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes).isNotNull();
        assertThat(bytes.length ).isPositive();
    }

    // =========================================================
    // Generator capabilities / accessors
    // =========================================================

    @Test
    public void testStreamWriteOutputTarget() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertThat(g.streamWriteOutputTarget()).isSameAs(out);
        }
    }

    @Test
    public void testStreamWriteOutputBuffered() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertThat(g.streamWriteOutputBuffered()).isEqualTo(0);
        }
    }

    @Test
    public void testGetPrettyPrinter_null() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertThat(g.getPrettyPrinter()).isNull();
        }
    }

    @Test
    public void testStreamWriteCapabilities() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertThat(g.streamWriteCapabilities()).isNotNull();
        }
    }

    @Test
    public void testVersion() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertThat(g.version()).isNotNull();
        }
    }

    @Test
    public void testAssignCurrentValue() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeStartObject();
            g.writeName("k");
            g.assignCurrentValue("v");
            assertThat(g.currentValue()).isEqualTo("v");
            g.writeNumber(1);
            g.writeEndObject();
        }
    }

    // =========================================================
    // writeArray shortcuts
    // =========================================================

    @Test
    public void testWriteArray_intArray() {
        int[] arr = {1, 2, 3};
        byte[] bytes = gen(g -> g.writeArray(arr, 0, arr.length));
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(1);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(2);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(3);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testWriteArray_longArray() {
        long[] arr = {100L, 200L};
        byte[] bytes = gen(g -> g.writeArray(arr, 0, arr.length));
        assertThat(bytes).isNotNull();
        assertThat(bytes.length ).isPositive();
    }

    @Test
    public void testWriteArray_doubleArray() {
        double[] arr = {1.1, 2.2};
        byte[] bytes = gen(g -> g.writeArray(arr, 0, arr.length));
        assertThat(bytes).isNotNull();
        assertThat(bytes.length ).isPositive();
    }
}
