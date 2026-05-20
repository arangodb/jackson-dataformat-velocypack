package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import com.arangodb.jackson.dataformat.velocypack.*;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for tagged values (0xee / 0xef) in VPack.
 */
public class VPackTaggedValueTest extends BaseTestForVPack
{
    // =========================================================
    // writeTaggedValuePrefix (generator)
    // =========================================================

    @Test
    public void testWriteTaggedValuePrefix_1byte_tag() {
        // Tag value 0 (1-byte tag encoding: 0xee 0x00)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeTaggedValuePrefix(0L);
            g.writeNull();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes[0]).isEqualTo((byte) 0xee); // VPACK_TAG_1BYTE
        assertThat(bytes[1]).isEqualTo((byte) 0x00); // tag number 0
        assertThat(bytes[2]).isEqualTo((byte) 0x18); // VPACK_NULL
    }

    @Test
    public void testWriteTaggedValuePrefix_1byte_max() {
        // Tag value 0xFF (still 1-byte encoding)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeTaggedValuePrefix(0xFFL);
            g.writeBoolean(true);
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes[0]).isEqualTo((byte) 0xee);
        assertThat(bytes[1]).isEqualTo((byte) 0xFF);
        assertThat(bytes[2]).isEqualTo((byte) 0x1a); // VPACK_TRUE
    }

    @Test
    public void testWriteTaggedValuePrefix_8byte_tag() {
        // Tag value 0x100 requires 8-byte encoding
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeTaggedValuePrefix(0x100L);
            g.writeNull();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes[0]).isEqualTo((byte) 0xef); // VPACK_TAG_8BYTE
        // LE encoding of 0x100: 00 01 00 00 00 00 00 00
        assertThat(bytes[1]).isEqualTo((byte) 0x00);
        assertThat(bytes[2]).isEqualTo((byte) 0x01);
        // Remaining 6 bytes should be 0
        for (int i = 3; i <= 8; i++) assertThat(bytes[i]).isEqualTo((byte) 0x00);
        assertThat(bytes[9]).isEqualTo((byte) 0x18); // VPACK_NULL
    }

    @Test
    public void testWriteTaggedValuePrefix_8byte_longMax() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeTaggedValuePrefix(Long.MAX_VALUE);
            g.writeNull();
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes[0]).isEqualTo((byte) 0xef); // VPACK_TAG_8BYTE
        // Last byte of LE Long.MAX_VALUE should be 0x7F
        assertThat(bytes[8]).isEqualTo((byte) 0x7F);
        assertThat(bytes[9]).isEqualTo((byte) 0x18); // VPACK_NULL
    }

    // =========================================================
    // Parser: 1-byte tag (0xee)
    // =========================================================

    @Test
    public void testParse_1byte_tag_null() {
        // 0xee 0x42 0x18 = tag(0x42) + null
        byte[] bytes = { (byte) 0xee, (byte) 0x42, (byte) 0x18 };
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
            assertThat(p.getLastTagNumber()).isEqualTo(0x42L);
        }
    }

    @Test
    public void testParse_1byte_tag_true() {
        byte[] bytes = { (byte) 0xee, (byte) 0x01, (byte) 0x1a };
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_TRUE);
            assertThat(p.getLastTagNumber()).isEqualTo(1L);
        }
    }

    @Test
    public void testParse_1byte_tag_string() {
        // 0xee 0x10 0x45 "hello" = tag(0x10) + short string "hello"
        byte[] strBytes = { (byte) 0xee, (byte) 0x10, (byte) 0x45,
                'h', 'e', 'l', 'l', 'o' };
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(strBytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo("hello");
            assertThat(p.getLastTagNumber()).isEqualTo(0x10L);
        }
    }

    // =========================================================
    // Parser: 8-byte tag (0xef)
    // =========================================================

    @Test
    public void testParse_8byte_tag_null() {
        // 0xef + 8 bytes tag number (0x100 = 256) + 0x18 null
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0xef;
        // LE 256 = 00 01 00 00 00 00 00 00
        bytes[1] = 0x00;
        bytes[2] = 0x01;
        bytes[9] = 0x18; // VPACK_NULL
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
            assertThat(p.getLastTagNumber()).isEqualTo(256L);
        }
    }

    // =========================================================
    // getLastTagNumber after non-tagged value
    // =========================================================

    @Test
    public void testGetLastTagNumber_notTagged_returnsNegativeOne() {
        byte[] bytes = { (byte) 0x18 }; // VPACK_NULL (no tag)
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(bytes)) {
            p.nextToken();
            assertThat(p.getLastTagNumber()).isEqualTo(-1L);
        }
    }

    // =========================================================
    // Tagged value inside array (ParseFrame.parseValueInBuf)
    // =========================================================

    @Test
    public void testTaggedValueInsideArray() {
        // Build an array containing a tagged null using the generator
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackMapper m = new VPackMapper();
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeStartArray();
            // Write tagged value manually
            ((VPackGenerator) g).writeTaggedValuePrefix(0x42L);
            g.writeNull();
            g.writeEndArray();
        }
        byte[] bytes = out.toByteArray();
        try (JsonParser p = m.createParser(bytes)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // FAIL_ON_TAGGED_VALUES: 8-byte tag also fails
    // =========================================================

    @Test
    public void testFailOnTaggedValues_8byte_throws() {
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0xef;
        bytes[9] = 0x18; // VPACK_NULL
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .build();
        try (JsonParser p = m.createParser(bytes)) {
            assertThatThrownBy(p::nextToken).isInstanceOf(tools.jackson.core.exc.StreamReadException.class);
        }
    }
}
