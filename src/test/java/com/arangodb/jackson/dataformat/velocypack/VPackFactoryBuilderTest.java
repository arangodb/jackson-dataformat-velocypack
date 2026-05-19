package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link VPackFactory}, {@link VPackFactoryBuilder}: builder APIs,
 * feature toggles, copy(), rebuild(), format name, capabilities.
 */
public class VPackFactoryBuilderTest extends BaseTestForVPack
{
    // =========================================================
    // Basic factory properties
    // =========================================================

    @Test
    public void testFormatName() {
        VPackFactory f = new VPackFactory();
        assertEquals("VelocyPack", f.getFormatName());
    }

    @Test
    public void testVersion_notNull() {
        VPackFactory f = new VPackFactory();
        assertNotNull(f.version());
    }

    @Test
    public void testCanUseSchema_returnsFalse() {
        VPackFactory f = new VPackFactory();
        assertFalse(f.canUseSchema(null));
    }

    @Test
    public void testGetFormatReadFeatureType() {
        VPackFactory f = new VPackFactory();
        assertEquals(VPackReadFeature.class, f.getFormatReadFeatureType());
    }

    @Test
    public void testGetFormatWriteFeatureType() {
        VPackFactory f = new VPackFactory();
        assertEquals(VPackWriteFeature.class, f.getFormatWriteFeatureType());
    }

    @Test
    public void testCanParseAsync_returnsFalse() {
        VPackFactory f = new VPackFactory();
        assertFalse(f.canParseAsync());
    }

    @Test
    public void testDefaultWriteFeatures() {
        VPackFactory f = new VPackFactory();
        assertTrue(f.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED));
        assertTrue(f.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH));
        assertFalse(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
        assertFalse(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
    }

    @Test
    public void testDefaultReadFeatures() {
        VPackFactory f = new VPackFactory();
        assertFalse(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES));
        assertFalse(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    // =========================================================
    // VPackFactoryBuilder: write feature toggles
    // =========================================================

    @Test
    public void testBuilder_enableCompactArrays() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertTrue(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
    }

    @Test
    public void testBuilder_disableSortedKeys() {
        VPackFactory f = VPackFactory.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .build();
        assertFalse(f.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED));
    }

    @Test
    public void testBuilder_enableCompactObjects() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        assertTrue(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
    }

    @Test
    public void testBuilder_disableMinIntWidth() {
        VPackFactory f = VPackFactory.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        assertFalse(f.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH));
    }

    @Test
    public void testBuilder_enableFailOnCustomTypes() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertTrue(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    @Test
    public void testBuilder_enableFailOnTaggedValues() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .build();
        assertTrue(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES));
    }

    @Test
    public void testBuilder_disableReadFeature() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .disable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertFalse(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    @Test
    public void testBuilder_configure_writeFeature_true() {
        VPackFactory f = VPackFactory.builder()
                .configure(VPackWriteFeature.WRITE_COMPACT_ARRAYS, true)
                .build();
        assertTrue(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
    }

    @Test
    public void testBuilder_configure_writeFeature_false() {
        VPackFactory f = VPackFactory.builder()
                .configure(VPackWriteFeature.WRITE_MIN_INT_WIDTH, false)
                .build();
        assertFalse(f.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH));
    }

    @Test
    public void testBuilder_configure_readFeature() {
        VPackFactory f = VPackFactory.builder()
                .configure(VPackReadFeature.FAIL_ON_CUSTOM_TYPES, true)
                .build();
        assertTrue(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    @Test
    public void testBuilder_enableLenientWriteUtf() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.LENIENT_UTF_ENCODING)
                .build();
        assertTrue(f.isEnabled(VPackWriteFeature.LENIENT_UTF_ENCODING));
    }

    @Test
    public void testBuilder_enableLenientReadUtf() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.LENIENT_UTF_ENCODING)
                .build();
        assertTrue(f.isEnabled(VPackReadFeature.LENIENT_UTF_ENCODING));
    }

    // =========================================================
    // copy() and rebuild()
    // =========================================================

    @Test
    public void testCopy_preservesWriteFeatures() {
        VPackFactory original = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        VPackFactory copy = (VPackFactory) original.copy();
        assertTrue(copy.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
    }

    @Test
    public void testCopy_preservesReadFeatures() {
        VPackFactory original = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        VPackFactory copy = (VPackFactory) original.copy();
        assertTrue(copy.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
    }

    @Test
    public void testRebuild_producesNewBuilder() {
        VPackFactory original = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        VPackFactory rebuilt = original.rebuild().build();
        assertTrue(rebuilt.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
    }

    @Test
    public void testSnapshot_returnsSelf() {
        VPackFactory f = new VPackFactory();
        assertSame(f, f.snapshot());
    }

    // =========================================================
    // Parser and Generator creation via factory
    // =========================================================

    @Test
    public void testCreateParser_fromBytes() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] vpackNull = { 0x18 }; // VPACK_NULL
        try (JsonParser p = m.createParser(vpackNull)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
        }
    }

    @Test
    public void testCreateParser_fromInputStream() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] vpackTrue = { 0x1a }; // VPACK_TRUE
        ByteArrayInputStream in = new ByteArrayInputStream(vpackTrue);
        try (JsonParser p = m.createParser(in)) {
            assertEquals(JsonToken.VALUE_TRUE, p.nextToken());
        }
    }

    @Test
    public void testCreateGenerator_toOutputStream() throws Exception {
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeBoolean(false);
        }
        byte[] bytes = out.toByteArray();
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x19, bytes[0]); // VPACK_FALSE
    }

    // =========================================================
    // Feature enum sanity
    // =========================================================

    @Test
    public void testWriteFeature_collectDefaults() {
        int defaults = VPackWriteFeature.collectDefaults();
        assertTrue(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED.enabledIn(defaults));
        assertTrue(VPackWriteFeature.WRITE_MIN_INT_WIDTH.enabledIn(defaults));
        assertFalse(VPackWriteFeature.WRITE_COMPACT_ARRAYS.enabledIn(defaults));
        assertFalse(VPackWriteFeature.WRITE_COMPACT_OBJECTS.enabledIn(defaults));
    }

    @Test
    public void testReadFeature_collectDefaults() {
        int defaults = VPackReadFeature.collectDefaults();
        assertFalse(VPackReadFeature.FAIL_ON_CUSTOM_TYPES.enabledIn(defaults));
        assertFalse(VPackReadFeature.FAIL_ON_TAGGED_VALUES.enabledIn(defaults));
    }

    @Test
    public void testWriteFeature_enabledByDefault() {
        for (VPackWriteFeature f : VPackWriteFeature.values()) {
            assertEquals(f.enabledByDefault(), f.enabledIn(VPackWriteFeature.collectDefaults()));
        }
    }

    @Test
    public void testReadFeature_enabledByDefault() {
        for (VPackReadFeature f : VPackReadFeature.values()) {
            assertEquals(f.enabledByDefault(), f.enabledIn(VPackReadFeature.collectDefaults()));
        }
    }

    @Test
    public void testWriteFeature_maskNotZero() {
        for (VPackWriteFeature f : VPackWriteFeature.values()) {
            assertTrue(f.getMask() != 0);
        }
    }

    @Test
    public void testReadFeature_maskNotZero() {
        for (VPackReadFeature f : VPackReadFeature.values()) {
            assertTrue(f.getMask() != 0);
        }
    }

    // =========================================================
    // VPackMapper builder
    // =========================================================

    @Test
    public void testMapperBuilder_withWriteFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertNotNull(m);
    }

    @Test
    public void testMapperBuilder_withReadFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertNotNull(m);
    }
}
