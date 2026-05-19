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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for VPackGenerator feature flags and scalar write variants.
 */
public class VPackGeneratorFeaturesTest extends BaseTestForVPack
{
    private byte[] gen(WriteAction action) throws Exception {
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
    // Feature enable/disable/configure on VPackGenerator
    // =========================================================

    @Test
    public void testEnableDisableFeature() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (VPackGenerator g = (VPackGenerator) m.createGenerator(out)) {
            g.enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS);
            assertTrue(g.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
            g.disable(VPackWriteFeature.WRITE_COMPACT_ARRAYS);
            assertFalse(g.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
        }
    }

    @Test
    public void testConfigureFeature() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (VPackGenerator g = (VPackGenerator) m.createGenerator(out)) {
            g.configure(VPackWriteFeature.WRITE_COMPACT_OBJECTS, true);
            assertTrue(g.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
            g.configure(VPackWriteFeature.WRITE_COMPACT_OBJECTS, false);
            assertFalse(g.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
        }
    }

    // =========================================================
    // writeRaw* throws UnsupportedOperationException
    // =========================================================

    @Test
    public void testWriteRaw_String_throws() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(UnsupportedOperationException.class, () -> g.writeRaw("raw"));
        }
    }

    @Test
    public void testWriteRaw_StringOffLen_throws() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(UnsupportedOperationException.class, () -> g.writeRaw("raw", 0, 3));
        }
    }

    @Test
    public void testWriteRaw_CharArray_throws() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(UnsupportedOperationException.class,
                    () -> g.writeRaw(new char[]{'a'}, 0, 1));
        }
    }

    @Test
    public void testWriteRaw_Char_throws() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(UnsupportedOperationException.class, () -> g.writeRaw('a'));
        }
    }

    @Test
    public void testWriteRawValue_String_throws() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(UnsupportedOperationException.class, () -> g.writeRawValue("raw"));
        }
    }

    @Test
    public void testWriteRawValue_StringOffLen_throws() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(UnsupportedOperationException.class,
                    () -> g.writeRawValue("raw", 0, 3));
        }
    }

    @Test
    public void testWriteRawValue_CharArray_throws() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(UnsupportedOperationException.class,
                    () -> g.writeRawValue(new char[]{'a'}, 0, 1));
        }
    }

    // =========================================================
    // writeNumber(String) -- various formats
    // =========================================================

    @Test
    public void testWriteNumberString_integer() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber("42"));
        assertEquals(2, bytes.length);
        assertEquals((byte) 0x20, bytes[0]); // 1-byte signed int
        assertEquals((byte) 42, bytes[1]);
    }

    @Test
    public void testWriteNumberString_decimal() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber("3.14"));
        // Should produce BCD (BigDecimal path)
        assertTrue(bytes.length > 1);
        int tb = bytes[0] & 0xFF;
        assertTrue(tb >= 0xc8 && tb <= 0xcf, "Expected BCD type, got 0x" + Integer.toHexString(tb));
    }

    @Test
    public void testWriteNumberString_withExponent() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber("1e5"));
        assertTrue(bytes.length > 0);
    }

    @Test
    public void testWriteNumberString_null_writesNull() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber((String) null));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x18, bytes[0]); // VPACK_NULL
    }

    @Test
    public void testWriteNumberString_invalid_throws() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(tools.jackson.core.exc.StreamWriteException.class,
                    () -> g.writeNumber("not_a_number"));
        }
    }

    @Test
    public void testWriteNumber_BigInteger_null_writesNull() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber((BigInteger) null));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x18, bytes[0]);
    }

    @Test
    public void testWriteNumber_BigDecimal_null_writesNull() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber((BigDecimal) null));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x18, bytes[0]);
    }

    @Test
    public void testWriteNumber_BigDecimal_negative() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(new BigDecimal("-12345")));
        // Should produce negative BCD
        int tb = bytes[0] & 0xFF;
        assertTrue(tb >= 0xd0 && tb <= 0xd7,
                "Expected negative BCD type, got 0x" + Integer.toHexString(tb));
    }

    @Test
    public void testWriteNumber_short() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber((short) 100));
        // Short is cast to int, 100 > 9 → 1-byte signed
        assertEquals(2, bytes.length);
        assertEquals((byte) 0x20, bytes[0]);
        assertEquals((byte) 100, bytes[1]);
    }

    @Test
    public void testWriteNumber_float() throws Exception {
        byte[] bytes = gen(g -> g.writeNumber(1.5f));
        // Float is cast to double
        assertEquals(9, bytes.length);
        assertEquals((byte) 0x1b, bytes[0]); // VPACK_DOUBLE
    }

    // =========================================================
    // writeString variants
    // =========================================================

    @Test
    public void testWriteString_charArray() throws Exception {
        char[] chars = {'h', 'i'};
        byte[] bytes = gen(g -> g.writeString(chars, 0, 2));
        assertEquals(3, bytes.length); // 0x42 'h' 'i'
        assertEquals((byte) 0x42, bytes[0]);
    }

    @Test
    public void testWriteString_serializableString() throws Exception {
        byte[] bytes = gen(g -> g.writeString(new tools.jackson.core.io.SerializedString("ok")));
        assertEquals(3, bytes.length); // 0x42 'o' 'k'
        assertEquals((byte) 0x42, bytes[0]);
    }

    @Test
    public void testWriteRawUTF8String() throws Exception {
        byte[] utf8 = "hi".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bytes = gen(g -> g.writeRawUTF8String(utf8, 0, utf8.length));
        assertEquals(3, bytes.length);
        assertEquals((byte) 0x42, bytes[0]);
    }

    @Test
    public void testWriteUTF8String() throws Exception {
        byte[] utf8 = "ab".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bytes = gen(g -> g.writeUTF8String(utf8, 0, utf8.length));
        assertEquals(3, bytes.length);
        assertEquals((byte) 0x42, bytes[0]);
    }

    @Test
    public void testWriteString_null_writesNull() throws Exception {
        byte[] bytes = gen(g -> g.writeString((String) null));
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x18, bytes[0]);
    }

    // =========================================================
    // writeBinary via InputStream - success
    // =========================================================

    @Test
    public void testWriteBinary_fromInputStream_success() throws Exception {
        byte[] data = { 0x01, 0x02, 0x03 };
        java.io.InputStream in = new java.io.ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            int written = g.writeBinary(in, 3);
            assertEquals(3, written);
        }
        byte[] bytes = out.toByteArray();
        // 0xc0 + 03 + data
        assertEquals((byte) 0xc0, bytes[0]);
        assertEquals((byte) 0x03, bytes[1]);
        assertArrayEquals(data, java.util.Arrays.copyOfRange(bytes, 2, 5));
    }

    // =========================================================
    // writePropertyId
    // =========================================================

    @Test
    public void testWritePropertyId() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeStartObject();
            g.writePropertyId(42L);
            g.writeNumber(99);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    // =========================================================
    // Generator capabilities / accessors
    // =========================================================

    @Test
    public void testStreamWriteOutputTarget() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertSame(out, g.streamWriteOutputTarget());
        }
    }

    @Test
    public void testStreamWriteOutputBuffered() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertEquals(0, g.streamWriteOutputBuffered());
        }
    }

    @Test
    public void testGetPrettyPrinter_null() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertNull(g.getPrettyPrinter());
        }
    }

    @Test
    public void testStreamWriteCapabilities() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertNotNull(g.streamWriteCapabilities());
        }
    }

    @Test
    public void testVersion() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            assertNotNull(g.version());
        }
    }

    @Test
    public void testAssignCurrentValue() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeStartObject();
            g.writeName("k");
            g.assignCurrentValue("v");
            assertEquals("v", g.currentValue());
            g.writeNumber(1);
            g.writeEndObject();
        }
    }

    // =========================================================
    // writeArray shortcuts
    // =========================================================

    @Test
    public void testWriteArray_intArray() throws Exception {
        int[] arr = {1, 2, 3};
        byte[] bytes = gen(g -> g.writeArray(arr, 0, arr.length));
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(1, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(2, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(3, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testWriteArray_longArray() throws Exception {
        long[] arr = {100L, 200L};
        byte[] bytes = gen(g -> g.writeArray(arr, 0, arr.length));
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    public void testWriteArray_doubleArray() throws Exception {
        double[] arr = {1.1, 2.2};
        byte[] bytes = gen(g -> g.writeArray(arr, 0, arr.length));
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }
}
