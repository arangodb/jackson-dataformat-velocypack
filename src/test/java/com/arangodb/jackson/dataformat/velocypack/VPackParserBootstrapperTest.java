package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void testFromBytes_null() {
        byte[] bytes = { 0x18 }; // VPACK_NULL
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
            assertThat(p.nextToken()).isNull();
        }
    }

    @Test
    public void testFromBytes_true() {
        byte[] bytes = { 0x1a }; // VPACK_TRUE
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_TRUE);
        }
    }

    @Test
    public void testFromBytes_false() {
        byte[] bytes = { 0x19 }; // VPACK_FALSE
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_FALSE);
        }
    }

    @Test
    public void testFromBytes_smallInt() {
        byte[] bytes = { 0x35 }; // VPACK_SMALL_INT_FIRST + 5 = 5
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(5);
        }
    }

    @Test
    public void testFromBytes_smallNeg() {
        byte[] bytes = { 0x3f }; // 0x40 + (-1) = 0x3f
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(-1);
        }
    }

    @Test
    public void testFromBytes_string_short() {
        byte[] bytes = { 0x45, 'h', 'e', 'l', 'l', 'o' }; // 5-char string
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo("hello");
        }
    }

    @Test
    public void testFromBytes_emptyString() {
        byte[] bytes = { 0x40 }; // 0-char string
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEmpty();
        }
    }

    @Test
    public void testFromBytes_double() {
        byte[] bytes = new byte[9];
        bytes[0] = 0x1b; // VPACK_DOUBLE
        long bits = Double.doubleToLongBits(2.5);
        for (int i = 0; i < 8; i++) {
            bytes[1 + i] = (byte) (bits & 0xFF);
            bits >>>= 8;
        }
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDoubleValue()).isEqualTo(2.5);
        }
    }

    @Test
    public void testFromBytes_emptyArray() {
        byte[] bytes = { 0x01 }; // VPACK_ARRAY_EMPTY
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testFromBytes_emptyObject() {
        byte[] bytes = { 0x0a }; // VPACK_OBJECT_EMPTY
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // Parser from InputStream
    // =========================================================

    @Test
    public void testFromInputStream_null() {
        byte[] bytes = { 0x18 }; // VPACK_NULL
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(new ByteArrayInputStream(bytes))) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
        }
    }

    @Test
    public void testFromInputStream_array() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(new int[]{1, 2, 3});
        try (JsonParser p = m.createParser(new ByteArrayInputStream(bytes))) {
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

    // =========================================================
    // EOF / empty input
    // =========================================================

    @Test
    public void testFromBytes_empty_returnsNull() {
        byte[] bytes = {};
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isNull();
        }
    }

    @Test
    public void testFromInputStream_empty_returnsNull() {
        VPackMapper m = new VPackMapper();
        try (JsonParser p = m.createParser(new ByteArrayInputStream(new byte[0]))) {
            assertThat(p.nextToken()).isNull();
        }
    }

    // =========================================================
    // Signed int widths (1, 2, 4, 8)
    // =========================================================

    @Test
    public void testSignedInt_1byte() {
        byte[] bytes = { 0x20, 0x7F }; // 1-byte signed, 127
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertThat(p.getIntValue()).isEqualTo(127);
        }
    }

    @Test
    public void testSignedInt_2bytes() {
        byte[] bytes = { 0x21, (byte) 0x00, (byte) 0x01 }; // 2-byte signed LE, 256
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertThat(p.getIntValue()).isEqualTo(256);
        }
    }

    @Test
    public void testSignedInt_4bytes() {
        // 4-byte signed LE, 65536 = 0x00010000
        byte[] bytes = { 0x23, 0x00, 0x00, 0x01, 0x00 };
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertThat(p.getIntValue()).isEqualTo(65536);
        }
    }

    @Test
    public void testSignedNeg_1byte() {
        byte[] bytes = { 0x20, (byte) 0xFF }; // 1-byte signed, -1
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertThat(p.getIntValue()).isEqualTo(-1);
        }
    }

    // =========================================================
    // Unsigned int widths
    // =========================================================

    @Test
    public void testUnsignedInt_1byte() {
        byte[] bytes = { 0x28, (byte) 0xFF }; // 1-byte unsigned, 255
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertThat(p.getIntValue()).isEqualTo(255);
        }
    }

    @Test
    public void testUnsignedInt_2bytes() {
        // 2-byte unsigned LE, 1000 = 0x03E8
        byte[] bytes = { 0x29, (byte) 0xE8, 0x03 };
        try (JsonParser p = vpackParser(bytes)) {
            p.nextToken();
            assertThat(p.getIntValue()).isEqualTo(1000);
        }
    }

    // =========================================================
    // Long string
    // =========================================================

    @Test
    public void testLongString_127chars() {
        VPackMapper m = new VPackMapper();
        String s = "X".repeat(127);
        byte[] bytes = m.writeValueAsBytes(s);
        assertThat(bytes[0]).isEqualTo((byte) 0xbf); // VPACK_STRING_LONG
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo(s);
        }
    }

    // =========================================================
    // writeName (SerializableString)
    // =========================================================

    @Test
    public void testWriteName_serializableString() {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("myKey");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(42);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }
}
