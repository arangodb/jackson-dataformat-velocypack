package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.Version;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for basic factory properties.
 */
public class VPackFactoryPropertiesTest extends BaseTestForVPack
{
    @Test
    public void testFormatName() {
        VPackFactory f = new VPackFactory();
        assertEquals("VelocyPack", f.getFormatName());
    }

    @Test
    public void testVersion() {
        VPackFactory f = new VPackFactory();
        Version v = f.version();
        assertNotNull(v);
        assertFalse(v.isUnknownVersion());
    }

    @Test
    public void testCanParseAsync() {
        VPackFactory f = new VPackFactory();
        assertFalse(f.canParseAsync());
    }

    @Test
    public void testCanUseSchema() {
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
    public void testBuilderRoundtrip() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertTrue(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES));
        assertTrue(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
        assertFalse(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
    }

    @Test
    public void testFactoryCopy() {
        VPackFactory f1 = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        VPackFactory f2 = f1.copy();
        assertNotSame(f1, f2);
        assertTrue(f2.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
    }

    @Test
    public void testFactorySnapshot() {
        VPackFactory f = new VPackFactory();
        assertSame(f, f.snapshot());
    }

    @Test
    public void testMapperBuilderFeatures() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .enable(VPackWriteFeature.LENIENT_UTF_ENCODING)
                .build();
        assertTrue(m.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES));
        assertTrue(m.isEnabled(VPackWriteFeature.LENIENT_UTF_ENCODING));
    }

    @Test
    public void testSharedMapper() {
        VPackMapper shared = VPackMapper.shared();
        assertNotNull(shared);
        assertSame(shared, VPackMapper.shared());
    }

    @Test
    public void testMapperVersion() {
        VPackMapper m = new VPackMapper();
        Version v = m.version();
        assertNotNull(v);
        assertFalse(v.isUnknownVersion());
    }
}
