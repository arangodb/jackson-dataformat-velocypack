package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackConstraintsTest {
    @Test
    void defaultsAndSnapshotsAreIndependent() {
        VPackReadConstraints read = VPackReadConstraints.defaults();
        assertEquals(67_108_864L, read.getMaxRootValueBytes());
        assertEquals(1_000_000, read.getMaxRootEntries());
        assertEquals(16_777_216L, read.getMaxRootNameBytes());

        VPackWriteConstraints write = VPackWriteConstraints.defaults();
        assertEquals(1_000, write.getMaxNumberDigits());
        VPackWriteConstraints changed = write.rebuild().maxRootEntries(7)
                .maxNumberDigits(33).build();
        assertNotSame(write, changed);
        assertEquals(1_000_000, write.getMaxRootEntries());
        assertEquals(7, changed.getMaxRootEntries());
        assertEquals(33, changed.getMaxNumberDigits());
    }

    @Test
    void buildersEnforceDocumentedRanges() {
        assertThrows(IllegalArgumentException.class,
                () -> VPackReadConstraints.builder().maxRootValueBytes(0L));
        assertThrows(IllegalArgumentException.class,
                () -> VPackReadConstraints.builder().maxRootValueBytes(Integer.MAX_VALUE - 7L));
        assertThrows(IllegalArgumentException.class,
                () -> VPackReadConstraints.builder().maxRootEntries(-1));
        assertThrows(IllegalArgumentException.class,
                () -> VPackReadConstraints.builder().maxRootNameBytes(-1L));
        assertThrows(IllegalArgumentException.class,
                () -> VPackReadConstraints.builder().maxRootNameBytes(Integer.MAX_VALUE - 7L));
        assertThrows(IllegalArgumentException.class,
                () -> VPackWriteConstraints.builder().maxNumberDigits(0));
    }

    @Test
    void maximumValuesAndLongOverflowDoNotWrap() {
        VPackReadConstraints limits = VPackReadConstraints.builder()
                .maxRootValueBytes(Integer.MAX_VALUE - 8L)
                .maxRootEntries(Integer.MAX_VALUE)
                .maxRootNameBytes(Integer.MAX_VALUE - 8L)
                .build();
        assertEquals(Integer.MAX_VALUE - 8L, limits.getMaxRootValueBytes());
        assertEquals(Integer.MAX_VALUE, limits.getMaxRootEntries());

        VPackRootBudget budget = new VPackRootBudget(100L, 2, 10L);
        budget.chargeBytes(100L);
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                () -> budget.chargeBytes(1L));
        budget.reset();
        budget.chargeEntry();
        budget.chargeNameBytes(10L);
        assertEquals(1, budget.entries());
        assertEquals(10L, budget.nameBytes());
    }
}
