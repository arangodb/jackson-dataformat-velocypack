package tools.jackson.dataformat.velocypack;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class VPackWidthSelectionEquivalenceTest {
    private static final long[] BODY_LENGTHS = bodyLengths();
    private static final long[] ENTRY_COUNTS = {
            0L, 1L, 2L, 255L, 256L, 65535L, 65536L
    };

    @Test
    void equalSelectionMatchesTheFormerThrowingSelector() {
        for (long bodyLength : BODY_LENGTHS) {
            assertSameOutcome(() -> oldSelectEqualWidth(bodyLength),
                    () -> VPackLayout.selectEqualWidth(bodyLength));
        }
    }

    @Test
    void indexedSelectionMatchesTheFormerThrowingSelector() {
        for (boolean object : new boolean[] { false, true }) {
            for (long bodyLength : BODY_LENGTHS) {
                for (long entryCount : ENTRY_COUNTS) {
                    for (long[] offsets : offsetsFor(bodyLength, entryCount)) {
                        assertSameOutcome(
                                () -> oldSelectIndexedWidth(object, bodyLength, entryCount, offsets),
                                () -> VPackLayout.selectIndexedWidth(
                                        object, bodyLength, entryCount, offsets));
                    }
                }
            }
        }
    }

    private static VPackLayout.FixedCandidate oldSelectEqualWidth(long bodyLength) {
        for (int width : new int[] { 1, 2, 4, 8 }) {
            try {
                return VPackLayout.equalCandidate(width, bodyLength);
            } catch (RuntimeException e) {
                if (!(e instanceof StreamWriteException)) throw e;
            }
        }
        throw VPackErrors.write("equal-array width", "body does not fit any structural width");
    }

    private static VPackLayout.FixedCandidate oldSelectIndexedWidth(boolean object,
            long bodyLength, long entryCount, long[] bodyOffsets) {
        RuntimeException last = null;
        for (int width : new int[] { 1, 2, 4, 8 }) {
            try {
                return VPackLayout.indexedCandidate(object, width, bodyLength,
                        entryCount, bodyOffsets);
            } catch (RuntimeException e) {
                if (!(e instanceof StreamWriteException)) throw e;
                last = e;
            }
        }
        throw last;
    }

    private static List<long[]> offsetsFor(long bodyLength, long entryCount) {
        List<long[]> result = new ArrayList<>();
        result.add(null);
        if (entryCount <= 2L) {
            long[] valid = new long[(int) entryCount];
            for (int i = 0; i < valid.length; ++i) valid[i] = i;
            result.add(valid);
            long[] invalid = new long[(int) entryCount];
            java.util.Arrays.fill(invalid, -1L);
            result.add(invalid);
        } else {
            // A short array exercises the throwing count-mismatch validation without large allocation.
            result.add(new long[] { bodyLength == 0L ? 0L : bodyLength });
        }
        return result;
    }

    private static void assertSameOutcome(CandidateCall oldCall, CandidateCall newCall) {
        Outcome expected = capture(oldCall);
        Outcome actual = capture(newCall);
        if (expected.candidate != null) {
            assertEquals(expected.candidate, actual.candidate);
            return;
        }
        if (actual.candidate != null) {
            fail("Expected " + expected.exceptionClass.getName() + " but got candidate "
                    + actual.candidate);
        }
        assertEquals(expected.exceptionClass, actual.exceptionClass);
        assertEquals(expected.message, actual.message);
    }

    private static Outcome capture(CandidateCall call) {
        try {
            return new Outcome(call.call(), null, null);
        } catch (RuntimeException e) {
            return new Outcome(null, e.getClass(), e.getMessage());
        }
    }

    private static long[] bodyLengths() {
        List<Long> values = new ArrayList<>();
        values.add(0L);
        values.add(1L);
        addRange(values, 250L, 260L);
        addRange(values, 65530L, 65545L);
        addRange(values, 0xFFFFFFFFL - 20L, 0xFFFFFFFFL + 5L);
        values.add(Long.MAX_VALUE / 4L);
        values.add(Long.MAX_VALUE);
        long[] result = new long[values.size()];
        for (int i = 0; i < result.length; ++i) result[i] = values.get(i);
        return result;
    }

    private static void addRange(List<Long> values, long first, long last) {
        for (long value = first; value <= last; ++value) values.add(value);
    }

    @FunctionalInterface
    private interface CandidateCall {
        VPackLayout.FixedCandidate call();
    }

    private record Outcome(VPackLayout.FixedCandidate candidate,
            Class<?> exceptionClass, String message) {
    }
}
