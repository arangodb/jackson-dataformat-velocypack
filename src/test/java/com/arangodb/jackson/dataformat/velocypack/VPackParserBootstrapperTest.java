package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link VPackParserBootstrapper}: parser construction from byte arrays
 * and input streams, covering various VPack type bytes.
 */
public class VPackParserBootstrapperTest extends BaseTestForVPack
{
    // =========================================================
    // Parser from byte array
    // =========================================================

    @Test
    public void testFromBytes_null() throws Exception {
        byte[] bytes = { 0x18 }; // VPACK_NULL
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertNull(p.nextToken());
        }
    }

    @Test
    public void testFromBytes_true() throws Exception {
        byte[] bytes = { 0x1a }; // VPACK_TRUE
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_TRUE, p.nextToken());
        }
    }

    @Test
    public void testFromBytes_false() throws Exception {
        byte[] bytes = { 0x19 }; // VPACK_FALSE
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_FALSE, p.nextToken());
        }
    }

    @Test
    public void testFromBytes_smallInt() throws Exception {
        byte[] bytes = { 0x35 }; // VPACK_SMALL_INT_FIRST + 5 = 5
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(5, p.getIntValue());
        }
    }

    @Test
    public void testFromBytes_smallNeg() throws Exception {
        byte[] bytes = { 0x3f }; // 0x40 + (-1) = 0x3f
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(-1, p.getIntValue());
        }
    }

    @Test
    public void testFromBytes_string_short() throws Exception {
        byte[] bytes = { 0x45, 'h', 'e', 'l', 'l', 'o' }; // 5-char string
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("hello", p.getString());
        }
    }

    @Test
    public void testFromBytes_emptyString() throws Exception {
        byte[] bytes = { 0x40 }; // 0-char string
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("", p.getString());
        }
    }

    @Test
    public void testFromBytes_double() throws Exception {
        byte[] bytes = new byte[9];
        bytes[0] = 0x1b; // VPACK_DOUBLE
        long bits = Double.doubleToLongBits(2.5);
        for (int i = 0; i < 8; i++) {
            bytes[1 + i] = (byte) (bits & 0xFF);
            bits >>>= 8;
        }
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(2.5, p.getDoubleValue(), 0.0);
        }
    }

    @Test
    public void testFromBytes_emptyArray() throws Exception {
        byte[] bytes = { 0x01 }; // VPACK_ARRAY_EMPTY
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testFromBytes_emptyObject() throws Exception {
        byte[] bytes = { 0x0a }; // VPACK_OBJECT_EMPTY
        try (JsonParser p = vpackParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Parser from InputStream
    // =========================================================

    @Test
    public void testFromInputStream_null() throws Exception {
        byte[] bytes = { 0x18 }; // VPACK_NULL
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(new ByteArrayInputStream(bytes))) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
        }
    }

    @Test
    public void testFromInputStream_array() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(new int[]{1, 2, 3});
        try (JsonParser p = m.createParser(new ByteArrayInputStream(bytes))) {
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

    // =========================================================
    // EOF / empty input
    // =========================================================

    @Test
    public void testFromBytes_empty_returnsNull() throws Exception {
        byte[] bytes = {};
        try (JsonParser p = vpackParser(bytes)) {
            assertNull(p.nextToken());
        }
    }

    @Test
    public void testFromInputStream_empty_returnsNull() throws Exception {
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(new ByteArrayInputStream(new byte[0]))) {
            assertNull(p.nextToken());
        }
    }

    // =========================================================
    // Signed int widths (1, 2, 4, 8)
    // =========================================================

    @Test
    public void testSignedInt_1byte() throws Exception {
        byte[] bytes = { 0x20, 0x7F }; // 1-byte signed, 127
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertEquals(127, p.getIntValue());
        }
    }

    @Test
    public void testSignedInt_2bytes() throws Exception {
        byte[] bytes = { 0x21, (byte) 0x00, (byte) 0x01 }; // 2-byte signed LE, 256
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertEquals(256, p.getIntValue());
        }
    }

    @Test
    public void testSignedInt_4bytes() throws Exception {
        // 4-byte signed LE, 65536 = 0x00010000
        byte[] bytes = { 0x23, 0x00, 0x00, 0x01, 0x00 };
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertEquals(65536, p.getIntValue());
        }
    }

    @Test
    public void testSignedNeg_1byte() throws Exception {
        byte[] bytes = { 0x20, (byte) 0xFF }; // 1-byte signed, -1
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertEquals(-1, p.getIntValue());
        }
    }

    // =========================================================
    // Unsigned int widths
    // =========================================================

    @Test
    public void testUnsignedInt_1byte() throws Exception {
        byte[] bytes = { 0x28, (byte) 0xFF }; // 1-byte unsigned, 255
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertEquals(255, p.getIntValue());
        }
    }

    @Test
    public void testUnsignedInt_2bytes() throws Exception {
        // 2-byte unsigned LE, 1000 = 0x03E8
        byte[] bytes = { 0x29, (byte) 0xE8, 0x03 };
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertEquals(1000, p.getIntValue());
        }
    }

    // =========================================================
    // Long string
    // =========================================================

    @Test
    public void testLongString_127chars() throws Exception {
        VPackMapper m = new VPackMapper();
        String s = "X".repeat(127);
        byte[] bytes = m.writeValueAsBytes(s);
        assertEquals((byte) 0xbf, bytes[0]); // VPACK_STRING_LONG
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals(s, p.getString());
        }
    }

    // =========================================================
    // writeName (SerializableString)
    // =========================================================

    @Test
    public void testWriteName_serializableString() throws Exception {
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeStartObject();
            g.writeName(new tools.jackson.core.io.SerializedString("myKey"));
            g.writeNumber(42);
            g.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        try (JsonParser p = m.createParser(bytes)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("myKey", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(42, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }
}
