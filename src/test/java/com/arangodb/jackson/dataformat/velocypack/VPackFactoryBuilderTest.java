package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(f.getFormatName()).isEqualTo("VelocyPack");
    }

    @Test
    public void testVersion_notNull() {
        VPackFactory f = new VPackFactory();
        assertThat(f.version()).isNotNull();
    }

    @Test
    public void testCanUseSchema_returnsFalse() {
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
    public void testCanParseAsync_returnsFalse() {
        VPackFactory f = new VPackFactory();
        assertThat(f.canParseAsync()).isFalse();
    }

    @Test
    public void testDefaultWriteFeatures() {
        VPackFactory f = new VPackFactory();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)).isFalse();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH)).isTrue();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS)).isTrue();
    }

    @Test
    public void testDefaultReadFeatures() {
        VPackFactory f = new VPackFactory();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES)).isFalse();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isFalse();
    }

    // =========================================================
    // VPackFactoryBuilder: write feature toggles
    // =========================================================

    @Test
    public void testBuilder_enableCompactArrays() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
    }

    @Test
    public void testBuilder_disableSortedKeys() {
        VPackFactory f = VPackFactory.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)).isFalse();
    }

    @Test
    public void testBuilder_enableCompactObjects() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS)).isTrue();
    }

    @Test
    public void testBuilder_disableMinIntWidth() {
        VPackFactory f = VPackFactory.builder()
                .disable(VPackWriteFeature.WRITE_MIN_INT_WIDTH)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH)).isFalse();
    }

    @Test
    public void testBuilder_enableFailOnCustomTypes() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isTrue();
    }

    @Test
    public void testBuilder_enableFailOnTaggedValues() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES)
                .build();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_TAGGED_VALUES)).isTrue();
    }

    @Test
    public void testBuilder_disableReadFeature() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .disable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isFalse();
    }

    @Test
    public void testBuilder_configure_writeFeature_true() {
        VPackFactory f = VPackFactory.builder()
                .configure(VPackWriteFeature.WRITE_COMPACT_ARRAYS, true)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
    }

    @Test
    public void testBuilder_configure_writeFeature_false() {
        VPackFactory f = VPackFactory.builder()
                .configure(VPackWriteFeature.WRITE_MIN_INT_WIDTH, false)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.WRITE_MIN_INT_WIDTH)).isFalse();
    }

    @Test
    public void testBuilder_configure_readFeature() {
        VPackFactory f = VPackFactory.builder()
                .configure(VPackReadFeature.FAIL_ON_CUSTOM_TYPES, true)
                .build();
        assertThat(f.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isTrue();
    }

    @Test
    public void testBuilder_enableLenientWriteUtf() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackWriteFeature.LENIENT_UTF_ENCODING)
                .build();
        assertThat(f.isEnabled(VPackWriteFeature.LENIENT_UTF_ENCODING)).isTrue();
    }

    @Test
    public void testBuilder_enableLenientReadUtf() {
        VPackFactory f = VPackFactory.builder()
                .enable(VPackReadFeature.LENIENT_UTF_ENCODING)
                .build();
        assertThat(f.isEnabled(VPackReadFeature.LENIENT_UTF_ENCODING)).isTrue();
    }

    // =========================================================
    // copy() and rebuild()
    // =========================================================

    @Test
    public void testCopy_preservesWriteFeatures() {
        VPackFactory original = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        VPackFactory copy = original.copy();
        assertThat(copy.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS)).isTrue();
    }

    @Test
    public void testCopy_preservesReadFeatures() {
        VPackFactory original = VPackFactory.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        VPackFactory copy = original.copy();
        assertThat(copy.isEnabled(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)).isTrue();
    }

    @Test
    public void testRebuild_producesNewBuilder() {
        VPackFactory original = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        VPackFactory rebuilt = original.rebuild().build();
        assertThat(rebuilt.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS)).isTrue();
    }

    @Test
    public void testSnapshot_returnsSelf() {
        VPackFactory f = new VPackFactory();
        assertThat(f.snapshot()).isSameAs(f);
    }

    // =========================================================
    // Parser and Generator creation via factory
    // =========================================================

    @Test
    public void testCreateParser_fromBytes() {
        VPackMapper m = new VPackMapper();
        byte[] vpackNull = { 0x18 }; // VPACK_NULL
        try (JsonParser p = m.createParser(vpackNull)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
        }
    }

    @Test
    public void testCreateParser_fromInputStream() {
        VPackMapper m = new VPackMapper();
        byte[] vpackTrue = { 0x1a }; // VPACK_TRUE
        ByteArrayInputStream in = new ByteArrayInputStream(vpackTrue);
        try (JsonParser p = m.createParser(in)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_TRUE);
        }
    }

    @Test
    public void testCreateGenerator_toOutputStream() {
        VPackMapper m = new VPackMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = m.createGenerator(out)) {
            g.writeBoolean(false);
        }
        byte[] bytes = out.toByteArray();
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x19); // VPACK_FALSE
    }

    // =========================================================
    // Feature enum sanity
    // =========================================================

    @Test
    public void testWriteFeature_collectDefaults() {
        int defaults = VPackWriteFeature.collectDefaults();
        assertThat(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED.enabledIn(defaults)).isFalse();
        assertThat(VPackWriteFeature.WRITE_MIN_INT_WIDTH.enabledIn(defaults)).isTrue();
        assertThat(VPackWriteFeature.WRITE_COMPACT_ARRAYS.enabledIn(defaults)).isTrue();
        assertThat(VPackWriteFeature.WRITE_COMPACT_OBJECTS.enabledIn(defaults)).isTrue();
    }

    @Test
    public void testReadFeature_collectDefaults() {
        int defaults = VPackReadFeature.collectDefaults();
        assertThat(VPackReadFeature.FAIL_ON_CUSTOM_TYPES.enabledIn(defaults)).isFalse();
        assertThat(VPackReadFeature.FAIL_ON_TAGGED_VALUES.enabledIn(defaults)).isFalse();
    }

    @Test
    public void testWriteFeature_enabledByDefault() {
        for (VPackWriteFeature f : VPackWriteFeature.values()) {
            assertThat(f.enabledIn(VPackWriteFeature.collectDefaults())).isEqualTo(f.enabledByDefault());
        }
    }

    @Test
    public void testReadFeature_enabledByDefault() {
        for (VPackReadFeature f : VPackReadFeature.values()) {
            assertThat(f.enabledIn(VPackReadFeature.collectDefaults())).isEqualTo(f.enabledByDefault());
        }
    }

    @Test
    public void testWriteFeature_maskNotZero() {
        for (VPackWriteFeature f : VPackWriteFeature.values()) {
            assertThat(f.getMask()).isNotZero();
        }
    }

    @Test
    public void testReadFeature_maskNotZero() {
        for (VPackReadFeature f : VPackReadFeature.values()) {
            assertThat(f.getMask()).isNotZero();
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
        assertThat(m).isNotNull();
    }

    @Test
    public void testMapperBuilder_withReadFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertThat(m).isNotNull();
    }
}
