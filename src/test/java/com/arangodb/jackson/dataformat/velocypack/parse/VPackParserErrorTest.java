package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackCustomValue;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for parser error conditions and illegal input handling.
 */
public class VPackParserErrorTest extends BaseTestForVPack
{
    // =========================================================
    // Illegal type bytes
    // =========================================================

    @Test
    public void testNoneByte_0x00_throws() {
        byte[] bytes = { 0x00 }; // VPACK_NONE
        try (JsonParser p = vpackParser(bytes)) {
            assertThatThrownBy(p::nextToken)
                    .isInstanceOf(StreamReadException.class)
                    .satisfies(e -> {
                        String msg = e.getMessage().toLowerCase(Locale.ROOT);
                        assertThat(msg).containsAnyOf("0x00","none","absent");
                    });
        }
    }

    @Test
    public void testIllegalByte_0x17_throws() {
        byte[] bytes = { 0x17 }; // VPACK_ILLEGAL
        try (JsonParser p = vpackParser(bytes)) {
            assertThatThrownBy(p::nextToken)
                    .isInstanceOf(StreamReadException.class)
                    .satisfies(e -> {
                        String msg = e.getMessage().toLowerCase(Locale.ROOT);
                        assertThat(msg).containsAnyOf("0x17", "illegal");
                    });
        }
    }

    @Test
    public void testExternalByte_0x1d_throws() {
        byte[] bytes = { 0x1d }; // VPACK_EXTERNAL
        try (JsonParser p = vpackParser(bytes)) {
            assertThatThrownBy(p::nextToken)
                    .isInstanceOf(StreamReadException.class)
                    .satisfies(e -> {
                        String msg = e.getMessage().toLowerCase(Locale.ROOT);
                        assertThat(msg).containsAnyOf("0x1d", "external");
                    });
        }
    }

    @Test
    public void testReservedByte_0x15_throws() {
        byte[] bytes = { 0x15 }; // VPACK_RESERVED_15
        try (JsonParser p = vpackParser(bytes)) {
            assertThatThrownBy(p::nextToken).isInstanceOf(StreamReadException.class);
        }
    }

    @Test
    public void testReservedByte_0x16_throws() {
        byte[] bytes = { 0x16 }; // VPACK_RESERVED_16
        try (JsonParser p = vpackParser(bytes)) {
            assertThatThrownBy(p::nextToken).isInstanceOf(StreamReadException.class);
        }
    }

    @Test
    public void testReservedByte_0xd8_throws() {
        byte[] bytes = { (byte) 0xd8 }; // VPACK_RESERVED_D8
        try (JsonParser p = vpackParser(bytes)) {
            assertThatThrownBy(p::nextToken).isInstanceOf(StreamReadException.class);
        }
    }

    @Test
    public void testReservedByte_0xed_throws() {
        byte[] bytes = { (byte) 0xed }; // VPACK_RESERVED_ED
        try (JsonParser p = vpackParser(bytes)) {
            assertThatThrownBy(p::nextToken).isInstanceOf(StreamReadException.class);
        }
    }

    // =========================================================
    // Date type (0x1c) - parsed as long
    // =========================================================

    @Test
    public void testDateType_parsedAsLong() {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getLongValue()).isEqualTo(1000000L);
        }
    }

    // =========================================================
    // Custom type variants
    // =========================================================

    @Test
    public void testCustomType_2B_payload() {
        // 0xf1 = custom type with 2-byte payload
        byte[] bytes = { (byte) 0xf1, 0x11, 0x22 };
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            Object obj = p.getEmbeddedObject();
            assertThat(obj).isInstanceOf(VPackCustomValue.class);
            VPackCustomValue cv = (VPackCustomValue) obj;
            assertThat(cv.getTypeByte()).isEqualTo(0xf1);
            assertThat(cv.getPayload()).hasSize(2);
        }
    }

    @Test
    public void testCustomType_4B_payload() {
        byte[] bytes = { (byte) 0xf2, 0x01, 0x02, 0x03, 0x04 };
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertThat(cv.getTypeByte()).isEqualTo(0xf2);
            assertThat(cv.getPayload()).hasSize(4);
        }
    }

    @Test
    public void testCustomType_8B_payload() {
        byte[] bytes = new byte[9];
        bytes[0] = (byte) 0xf3; // VPACK_CUSTOM_8B
        for (int i = 1; i <= 8; i++) bytes[i] = (byte) i;
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertThat(cv.getTypeByte()).isEqualTo(0xf3);
            assertThat(cv.getPayload()).hasSize(8);
        }
    }

    @Test
    public void testCustomType_len1_variable() {
        // 0xf4 (VPACK_CUSTOM_LEN1_FIRST) + 1-byte length + payload
        byte[] bytes = { (byte) 0xf4, 0x03, 0x0A, 0x0B, 0x0C };
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertThat(cv.getTypeByte()).isEqualTo(0xf4);
            assertThat(cv.getPayload()).hasSize(3);
        }
    }

    @Test
    public void testCustomType_len2_variable() {
        // 0xf7 (VPACK_CUSTOM_LEN2_FIRST) + 2-byte LE length + payload
        byte[] bytes = { (byte) 0xf7, 0x02, 0x00, 0x55, 0x66 };
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertThat(cv.getTypeByte()).isEqualTo(0xf7);
            assertThat(cv.getPayload()).hasSize(2);
        }
    }

    @Test
    public void testCustomType_len4_variable() {
        // 0xfa (VPACK_CUSTOM_LEN4_FIRST) + 4-byte LE length + payload
        byte[] bytes = { (byte) 0xfa, 0x02, 0x00, 0x00, 0x00, (byte) 0xAA, (byte) 0xBB };
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            VPackCustomValue cv = (VPackCustomValue) p.getEmbeddedObject();
            assertThat(cv.getTypeByte()).isEqualTo(0xfa);
            assertThat(cv.getPayload()).hasSize(2);
        }
    }

    // =========================================================
    // Array parsing - various type bytes
    // =========================================================

    @Test
    public void testEmptyArray_0x01() {
        byte[] bytes = { 0x01 }; // VPACK_ARRAY_EMPTY
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testEmptyObject_0x0a() {
        byte[] bytes = { 0x0a }; // VPACK_OBJECT_EMPTY
        try (JsonParser p = vpackParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // writeBinary via InputStream - error paths
    // =========================================================

    @Test
    public void testWriteBinary_inputStream_unknownLength_throws() {
        java.io.InputStream in = new java.io.ByteArrayInputStream(new byte[]{0x01, 0x02});
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeBinary(in, -1))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    public void testWriteBinary_inputStream_eofBeforeComplete_throws() {
        // Stream has only 2 bytes but we claim 5
        java.io.InputStream in = new java.io.ByteArrayInputStream(new byte[]{0x01, 0x02});
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeBinary(in, 5))
                    .isInstanceOf(Exception.class);
        }
    }
}
