package com.arangodb.jackson.dataformat.velocypack.gen;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.exc.StreamWriteException;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for generator error conditions.
 */
public class VPackGeneratorErrorTest extends BaseTestForVPack
{
    // =========================================================
    // writeEndArray without writeStartArray
    // =========================================================

    @Test
    public void testWriteEndArray_withoutStart_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(g::writeEndArray).isInstanceOf(StreamWriteException.class);
        }
    }

    // =========================================================
    // writeEndObject without writeStartObject
    // =========================================================

    @Test
    public void testWriteEndObject_withoutStart_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(g::writeEndObject).isInstanceOf(StreamWriteException.class);
        }
    }

    // =========================================================
    // writeName outside object context
    // =========================================================

    @Test
    public void testWriteName_outsideObject_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            // At root level, writeName is not expected before a value
            // This should fail on the second writeName (first sets up for value)
            assertThatThrownBy(() -> {
                g.writeName("key");
                g.writeName("key2"); // can't write name when expecting value
            }).isInstanceOf(StreamWriteException.class);
        }
    }

    // =========================================================
    // writeNumber(String) with invalid format
    // =========================================================

    @Test
    public void testWriteNumber_invalidString_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            assertThatThrownBy(() -> g.writeNumber("abc_not_a_number")).isInstanceOf(StreamWriteException.class);
        }
    }

    // =========================================================
    // writeBinary(InputStream) with -1 length
    // =========================================================

    @Test
    public void testWriteBinary_negativeLength_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            java.io.InputStream in = new java.io.ByteArrayInputStream(new byte[]{1, 2, 3});
            assertThatThrownBy(() -> g.writeBinary(in, -1)).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // =========================================================
    // writeBinary(InputStream) with EOF before complete
    // =========================================================

    @Test
    public void testWriteBinary_eofBeforeComplete_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = vpackGenerator(out)) {
            java.io.InputStream in = new java.io.ByteArrayInputStream(new byte[]{0x01});
            // Claim 5 bytes but stream only has 1
            assertThatThrownBy(() -> g.writeBinary(in, 5)).isInstanceOf(StreamWriteException.class);
        }
    }

    // =========================================================
    // Flush and close semantics
    // =========================================================

    @Test
    public void testFlush_afterClose_noException() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator g = vpackGenerator(out);
        g.writeNull();
        g.close();
        // After close, flush shouldn't throw (already closed)
        g.flush(); // should be safe
    }

    // =========================================================
    // Duplicate key detection (STRICT_DUPLICATE_DETECTION)
    // =========================================================

    @Test
    public void testDuplicateKeyDetection_enabled_throws() {
        VPackMapper m = VPackMapper.builder()
                .enable(StreamWriteFeature.STRICT_DUPLICATE_DETECTION)
                .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeStartObject();
            g.writeName("key");
            g.writeNumber(1);
        assertThatThrownBy(() -> {
                g.writeName("key"); // duplicate!
                g.writeNumber(2);
            }).isInstanceOf(StreamWriteException.class);
        }
    }

    @Test
    public void testDuplicateKeyDetection_disabled_noThrow() {
        VPackMapper m = VPackMapper.builder()
                .disable(StreamWriteFeature.STRICT_DUPLICATE_DETECTION)
                .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Should not throw with duplicate detection disabled
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeStartObject();
            g.writeName("key");
            g.writeNumber(1);
            g.writeName("key");
            g.writeNumber(2);
            g.writeEndObject();
        }
        // Just verify it produced bytes
        assertThat(out.toByteArray()).isNotEmpty();
    }
}
