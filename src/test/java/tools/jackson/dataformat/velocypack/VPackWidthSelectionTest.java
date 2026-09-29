package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackWidthSelectionTest {
    @Test
    void fixedCandidatesIncludeAllWidthDependentOverhead() {
        VPackLayout.FixedCandidate equal = VPackLayout.selectEqualWidth(253L);
        assertEquals(1, equal.width());
        assertEquals(255L, equal.length());
        assertEquals(2, VPackLayout.selectEqualWidth(254L).width());

        VPackLayout.FixedCandidate indexed = VPackLayout.selectIndexedWidth(
                false, 252L, 1L, new long[] { 0L });
        assertEquals(2, indexed.width());
        assertEquals(1 + 4 + 252 + 2, indexed.length());
        assertEquals(indexed.bodyStart(), indexed.indexStart() - 252L);
        assertEquals(indexed.bodyStart(), 5L);
    }

    @Test
    void fourGibBoundaryUsesSyntheticArithmeticAndNeverAllocatesTheBody() {
        long max = 0xFFFFFFFFL;
        long widthFourBody = max - (1L + 8L + 4L);
        assertEquals(4, VPackLayout.selectIndexedWidth(false, widthFourBody, 1L, null).width());
        assertEquals(8, VPackLayout.selectIndexedWidth(false, widthFourBody + 1L, 1L, null).width());

        assertEquals(4, VPackLayout.selectEqualWidth(max - 5L).width());
        assertEquals(8, VPackLayout.selectEqualWidth(max - 4L).width());
    }

    @Test
    void compactLengthAndReverseCountReachTheirOwnFixedPoint() {
        VPackLayout.CompactCandidate zero = VPackLayout.compactCandidate(false, 0L, 0L);
        assertEquals(3L, zero.length());
        assertEquals(1, zero.lengthWidth());
        assertEquals(1, zero.countWidth());

        VPackLayout.CompactCandidate boundary = VPackLayout.compactCandidate(
                false, 126L, 1L);
        assertEquals(130L, boundary.length());
        assertEquals(2, boundary.lengthWidth());
        assertEquals(boundary.bodyEnd(), boundary.countStart());
        assertEquals(1, boundary.countWidth());
        assertEquals(boundary.length(), boundary.countEnd());
        assertEquals(3L, boundary.bodyStart());
    }

    @Test
    void writerArithmeticRejectsOversizedCountsLengthsAndInvalidOffsets() {
        assertThrows(StreamWriteException.class,
                () -> VPackLayout.selectIndexedWidth(false, 1L, 256L, null));
        assertThrows(StreamWriteException.class,
                () -> VPackLayout.indexedCandidate(false, 1, 1L, 1L, new long[] { 1L }));
        assertThrows(StreamWriteException.class,
                () -> VPackLayout.compactCandidate(false, VPackBounds.MAX_COMPACT_VALUE, 1L));
        assertThrows(StreamWriteException.class,
                () -> VPackLayout.compactCandidate(true, 1L, 1L));
    }
}
