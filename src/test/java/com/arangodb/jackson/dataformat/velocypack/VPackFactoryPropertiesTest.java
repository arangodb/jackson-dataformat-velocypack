package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.Version;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for basic factory properties.
 */
public class VPackFactoryPropertiesTest extends BaseTestForVPack
{
    @Test
    public void testFormatName() {
        VPackFactory f = new VPackFactory();
        assertThat(f.getFormatName()).isEqualTo("VelocyPack");
    }

    @Test
    public void testVersion() {
        VPackFactory f = new VPackFactory();
        Version v = f.version();
        assertThat(v).isNotNull();
        assertThat(v.isUnknownVersion()).isFalse();
    }

    @Test
    public void testCanParseAsync() {
        VPackFactory f = new VPackFactory();
        assertThat(f.canParseAsync()).isFalse();
    }

    @Test
    public void testCanUseSchema() {
        VPackFactory f = new VPackFactory();
        assertThat(f.canUseSchema(null)).isFalse();
    }

    @Test
    public void testGetFormatReadFeatureType() {
        VPackFactory f = new VPackFactory();
        assertThat(f.getFormatReadFeatureType()).isEqualTo(VPackReadFeature.class);
    }

    @Test
    public void testGetFormatWriteFeatureType() {
        VPackFactory f = new VPackFactory();
        assertThat(f.getFormatWriteFeatureType()).isEqualTo(VPackWriteFeature.class);
    }

    @Test
    public void testBuilderRoundtrip() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES)).isTrue();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)).isFalse();
    }

    @Test
    public void testFactoryCopy() {
        VPackFactory f1 = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        VPackFactory f2 = f1.copy();
        assertThat(f2).isNotSameAs(f1);
        assertThat(f2.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
    }

    @Test
    public void testFactorySnapshot() {
        VPackFactory f = new VPackFactory();
        assertThat(f.snapshot()).isSameAs(f);
    }

    @Test
    public void testMapperBuilderFeatures() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .enable(VPackWriteFeature.LENIENT_UTF_ENCODING)
                .build();
        assertThat(m.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isTrue();
        assertThat(m.isEnabled(VPackWriteFeature.LENIENT_UTF_ENCODING)).isTrue();
    }

    @Test
    public void testSharedMapper() {
        VPackMapper shared = VPackMapper.shared();
        assertThat(shared).isNotNull();
        assertThat(VPackMapper.shared()).isSameAs(shared);
    }

    @Test
    public void testMapperVersion() {
        VPackMapper m = new VPackMapper();
        Version v = m.version();
        assertThat(v).isNotNull();
        assertThat(v.isUnknownVersion()).isFalse();
    }
}
