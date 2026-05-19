package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import com.arangodb.jackson.dataformat.velocypack.*;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for tagged values (0xee / 0xef) in VPack.
 */
public class VPackTaggedValueTest extends BaseTestForVPack
{
    // =========================================================
    // writeTaggedValuePrefix (generator)
    // =========================================================

    @Test
    public void testWriteTaggedValuePrefix_1byte_tag() throws Exception {
        // Tag value 0 (1-byte tag encoding: 0xee 0x00)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeTaggedValuePrefix(0L);
            g.writeNull();
        }
        byte[] bytes = out.toByteArray();
        assertEquals((byte) 0xee, bytes[0]); // VPACK_TAG_1BYTE
        assertEquals((byte) 0x00, bytes[1]); // tag number 0
        assertEquals((byte) 0x18, bytes[2]); // VPACK_NULL
    }

    @Test
    public void testWriteTaggedValuePrefix_1byte_max() throws Exception {
        // Tag value 0xFF (still 1-byte encoding)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeTaggedValuePrefix(0xFFL);
            g.writeBoolean(true);
        }
        byte[] bytes = out.toByteArray();
        assertEquals((byte) 0xee, bytes[0]);
        assertEquals((byte) 0xFF, bytes[1]);
        assertEquals((byte) 0x1a, bytes[2]); // VPACK_TRUE
    }

    @Test
    public void testWriteTaggedValuePrefix_8byte_tag() throws Exception {
        // Tag value 0x100 requires 8-byte encoding
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeTaggedValuePrefix(0x100L);
            g.writeNull();
        }
        byte[] bytes = out.toByteArray();
        assertEquals((byte) 0xef, bytes[0]); // VPACK_TAG_8BYTE
        // LE encoding of 0x100: 00 01 00 00 00 00 00 00
        assertEquals((byte) 0x00, bytes[1]);
        assertEquals((byte) 0x01, bytes[2]);
        // Remaining 6 bytes should be 0
        for (int i = 3; i <= 8; i++) assertEquals((byte) 0x00, bytes[i]);
        assertEquals((byte) 0x18, bytes[9]); // VPACK_NULL
    }

    @Test
    public void testWriteTaggedValuePrefix_8byte_longMax() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (VPackGenerator g = (VPackGenerator) vpackGenerator(out)) {
            g.writeTaggedValuePrefix(Long.MAX_VALUE);
            g.writeNull();
        }
        byte[] bytes = out.toByteArray();
        assertEquals((byte) 0xef, bytes[0]); // VPACK_TAG_8BYTE
        // Last byte of LE Long.MAX_VALUE should be 0x7F
        assertEquals((byte) 0x7F, bytes[8]);
        assertEquals((byte) 0x18, bytes[9]); // VPACK_NULL
    }

    // =========================================================
    // Parser: 1-byte tag (0xee)
    // =========================================================

    @Test
    public void testParse_1byte_tag_null() throws Exception {
        // 0xee 0x42 0x18 = tag(0x42) + null
        byte[] bytes = { (byte) 0xee, (byte) 0x42, (byte) 0x18 };
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(bytes)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertEquals(0x42L, p.getLastTagNumber());
        }
    }

    @Test
    public void testParse_1byte_tag_true() throws Exception {
        byte[] bytes = { (byte) 0xee, (byte) 0x01, (byte) 0x1a };
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(bytes)) {
            assertEquals(JsonToken.VALUE_TRUE, p.nextToken());
            assertEquals(1L, p.getLastTagNumber());
        }
    }

    @Test
    public void testParse_1byte_tag_string() throws Exception {
        // 0xee 0x10 0x45 "hello" = tag(0x10) + short string "hello"
        byte[] strBytes = { (byte) 0xee, (byte) 0x10, (byte) 0x45,
                'h', 'e', 'l', 'l', 'o' };
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(strBytes)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("hello", p.getString());
            assertEquals(0x10L, p.getLastTagNumber());
        }
    }

    // =========================================================
    // Parser: 8-byte tag (0xef)
    // =========================================================

    @Test
    public void testParse_8byte_tag_null() throws Exception {
        // 0xef + 8 bytes tag number (0x100 = 256) + 0x18 null
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0xef;
        // LE 256 = 00 01 00 00 00 00 00 00
        bytes[1] = 0x00;
        bytes[2] = 0x01;
        bytes[9] = 0x18; // VPACK_NULL
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(bytes)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertEquals(256L, p.getLastTagNumber());
        }
    }

    // =========================================================
    // getLastTagNumber after non-tagged value
    // =========================================================

    @Test
    public void testGetLastTagNumber_notTagged_returnsNegativeOne() throws Exception {
        byte[] bytes = { (byte) 0x18 }; // VPACK_NULL (no tag)
        VPackMapper m = new VPackMapper();
        try (VPackParser p = (VPackParser) m.createParser(bytes)) {
            p.nextToken();
            assertEquals(-1L, p.getLastTagNumber());
        }
    }

    // =========================================================
    // Tagged value inside array (ParseFrame.parseValueInBuf)
    // =========================================================

    @Test
    public void testTaggedValueInsideArray() throws Exception {
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
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // FAIL_ON_TAGGED_VALUES: 8-byte tag also fails
    // =========================================================

    @Test
    public void testFailOnTaggedValues_8byte_throws() throws Exception {
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0xef;
        bytes[9] = 0x18; // VPACK_NULL
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .build();
        try (JsonParser p = m.createParser(bytes)) {
            assertThrows(tools.jackson.core.exc.StreamReadException.class, () -> p.nextToken());
        }
    }
}
