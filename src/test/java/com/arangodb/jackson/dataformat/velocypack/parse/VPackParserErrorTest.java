package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackCustomValue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for parser error conditions and illegal input handling.
 */
public class VPackParserErrorTest extends BaseTestForVPack
{
    // =========================================================
    // Illegal type bytes
    // =========================================================

    @Test
    public void testNoneByte_0x00_throws() throws Exception {
        byte[] bytes = { 0x00 }; // VPACK_NONE
        try (JsonParser p = vpackParser(bytes)) {
            StreamReadException e = assertThrows(StreamReadException.class, () -> p.nextToken());
            assertTrue(e.getMessage().toLowerCase().contains("0x00")
                    || e.getMessage().toLowerCase().contains("none")
                    || e.getMessage().toLowerCase().contains("absent"));
        }
    }

    @Test
    public void testIllegalByte_0x17_throws() throws Exception {
        byte[] bytes = { 0x17 }; // VPACK_ILLEGAL
        try (JsonParser p = vpackParser(bytes)) {
            StreamReadException e = assertThrows(StreamReadException.class, () -> p.nextToken());
            assertTrue(e.getMessage().toLowerCase().contains("0x17")
                    || e.getMessage().toLowerCase().contains("illegal"));
        }
    }

    @Test
    public void testExternalByte_0x1d_throws() throws Exception {
        byte[] bytes = { 0x1d }; // VPACK_EXTERNAL
        try (JsonParser p = vpackParser(bytes)) {
            StreamReadException e = assertThrows(StreamReadException.class, () -> p.nextToken());
            assertTrue(e.getMessage().toLowerCase().contains("0x1d")
                    || e.getMessage().toLowerCase().contains("external"));
        }
    }

    @Test
    public void testReservedByte_0x15_throws() throws Exception {
        byte[] bytes = { 0x15 }; // VPACK_RESERVED_15
        try (JsonParser p = vpackParser(bytes)) {
            assertThrows(StreamReadException.class, () -> p.nextToken());
        }
    }

    @Test
    public void testReservedByte_0x16_throws() throws Exception {
        byte[] bytes = { 0x16 }; // VPACK_RESERVED_16
        try (JsonParser p = vpackParser(bytes)) {
            assertThrows(StreamReadException.class, () -> p.nextToken());
        }
    }

    @Test
    public void testReservedByte_0xd8_throws() throws Exception {
        byte[] bytes = { (byte) 0xd8 }; // VPACK_RESERVED_D8
        try (JsonParser p = vpackParser(bytes)) {
            assertThrows(StreamReadException.class, () -> p.nextToken());
        }
    }

    @Test
    public void testReservedByte_0xed_throws() throws Exception {
        byte[] bytes = { (byte) 0xed }; // VPACK_RESERVED_ED
        try (JsonParser p = vpackParser(bytes)) {
            assertThrows(StreamReadException.class, () -> p.nextToken());
        }
    }

    // =========================================================
    // Date type (0x1c) - parsed as long
    // =========================================================

    @Test
    public void testDateType_parsedAsLong() throws Exception {
        // 0x1c + 8 bytes LE signed = date
        byte[] bytes = new byte[9];
        bytes[0] = 0x1c;
        // timestamp = 1000000 ms
        long ts = 1000000L;
        for (int i = 0; i < 8; i++) {
            bytes[1 + i] = (byte) (ts & 0xFF);
            ts >>>= 8;
        }
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(1000000L, p.getLongValue());
        }
    }

    // =========================================================
    // Custom type variants
    // =========================================================

    @Test
    public void testCustomType_2B_payload() throws Exception {
        // 0xf1 = custom type with 2-byte payload
        byte[] bytes = { (byte) 0xf1, 0x11, 0x22 };
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            Object obj = p.getEmbeddedObject();
            assertInstanceOf(VPackCustomValue.class, obj);
            VPackCustomValue cv = (VPackCustomValue) obj;
            assertEquals(0xf1, cv.getTypeByte());
            assertEquals(2, cv.getPayload().length);
        }
    }

    @Test
    public void testCustomType_4B_payload() throws Exception {
        byte[] bytes = { (byte) 0xf2, 0x01, 0x02, 0x03, 0x04 };
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertEquals(0xf2, cv.getTypeByte());
            assertEquals(4, cv.getPayload().length);
        }
    }

    @Test
    public void testCustomType_8B_payload() throws Exception {
        byte[] bytes = new byte[9];
        bytes[0] = (byte) 0xf3; // VPACK_CUSTOM_8B
        // fill 8 bytes of payload
        for (int i = 1; i <= 8; i++) bytes[i] = (byte) i;
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertEquals(0xf3, cv.getTypeByte());
            assertEquals(8, cv.getPayload().length);
        }
    }

    @Test
    public void testCustomType_len1_variable() throws Exception {
        // 0xf4 (VPACK_CUSTOM_LEN1_FIRST) + 1-byte length + payload
        byte[] bytes = { (byte) 0xf4, 0x03, 0x0A, 0x0B, 0x0C };
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertEquals(0xf4, cv.getTypeByte());
            assertEquals(3, cv.getPayload().length);
        }
    }

    @Test
    public void testCustomType_len2_variable() throws Exception {
        // 0xf7 (VPACK_CUSTOM_LEN2_FIRST) + 2-byte LE length + payload
        byte[] bytes = { (byte) 0xf7, 0x02, 0x00, 0x55, 0x66 };
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertEquals(0xf7, cv.getTypeByte());
            assertEquals(2, cv.getPayload().length);
        }
    }

    @Test
    public void testCustomType_len4_variable() throws Exception {
        // 0xfa (VPACK_CUSTOM_LEN4_FIRST) + 4-byte LE length + payload
        byte[] bytes = { (byte) 0xfa, 0x02, 0x00, 0x00, 0x00, (byte) 0xAA, (byte) 0xBB };
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertEquals(0xfa, cv.getTypeByte());
            assertEquals(2, cv.getPayload().length);
        }
    }

    // =========================================================
    // Array parsing - various type bytes
    // =========================================================

    @Test
    public void testEmptyArray_0x01() throws Exception {
        byte[] bytes = { 0x01 }; // VPACK_ARRAY_EMPTY
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testEmptyObject_0x0a() throws Exception {
        byte[] bytes = { 0x0a }; // VPACK_OBJECT_EMPTY
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // writeBinary via InputStream - error paths
    // =========================================================

    @Test
    public void testWriteBinary_inputStream_unknownLength_throws() throws Exception {
        java.io.InputStream in = new java.io.ByteArrayInputStream(new byte[]{0x01, 0x02});
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(UnsupportedOperationException.class,
                    () -> g.writeBinary(in, -1));
        }
    }

    @Test
    public void testWriteBinary_inputStream_eofBeforeComplete_throws() throws Exception {
        // Stream has only 2 bytes but we claim 5
        java.io.InputStream in = new java.io.ByteArrayInputStream(new byte[]{0x01, 0x02});
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThrows(Exception.class, () -> g.writeBinary(in, 5));
        }
    }
}
