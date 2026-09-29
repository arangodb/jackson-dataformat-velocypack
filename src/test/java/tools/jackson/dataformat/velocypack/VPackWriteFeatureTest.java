package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VPackWriteFeatureTest {
    @Test
    void declaresOnlyThePinnedFeaturesAndDefaults() {
        assertEquals(3, VPackWriteFeature.values().length);
        assertTrue(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS.enabledByDefault());
        assertFalse(VPackWriteFeature.WRITE_COMPACT_ARRAYS.enabledByDefault());
        assertFalse(VPackWriteFeature.WRITE_COMPACT_OBJECTS.enabledByDefault());
        assertEquals(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS.getMask(),
                VPackWriteFeature.collectDefaults());
        assertEquals(0, VPackReadFeature.values().length);
        assertEquals(0, VPackReadFeature.collectDefaults());
    }

    @Test
    void factoryAndRebuildPreserveIndependentTypedFeatureSnapshots() {
        VPackFactory compact = VPackFactory.builder()
                .disable(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS)
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        assertFalse(compact.isEnabled(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS));
        assertTrue(compact.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
        assertTrue(compact.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));

        VPackFactory rebuilt = compact.rebuild()
                .disable(VPackWriteFeature.WRITE_COMPACT_OBJECTS).build();
        assertTrue(compact.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
        assertFalse(rebuilt.isEnabled(VPackWriteFeature.WRITE_COMPACT_OBJECTS));
        assertFalse(rebuilt.isEnabled(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS));
        assertTrue(rebuilt.isEnabled(VPackWriteFeature.WRITE_COMPACT_ARRAYS));
    }
}
