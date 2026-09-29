package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackByteStoreTest {
    @Test
    void borrowedSliceUsesCheckedStableCoordinates() {
        byte[] input = { 9, 10, 11, 12, 13 };
        VPackByteStore store = VPackByteStore.borrowed(input, 1L, 3L);
        VPackByteStore.Range range = store.range(1L, 2L);
        assertEquals(11, range.byteAt(0L));
        assertArrayEquals(new byte[] { 11, 12 }, range.toByteArray());
        input[2] = 42;
        assertEquals(42, range.byteAt(0L));
        assertThrows(tools.jackson.core.exc.StreamReadException.class,
                () -> store.byteAt(3L));
        assertThrows(tools.jackson.core.exc.StreamReadException.class,
                () -> store.range(Long.MAX_VALUE, 1L));
    }

    @Test
    void ownedStoreAllocatesPagesLazilyAndCopiesAcrossBoundaries() {
        VPackByteStore store = VPackByteStore.owned();
        assertEquals(0L, store.size());
        byte[] input = new byte[VPackByteStore.PAGE_SIZE + 3];
        input[0] = 1;
        input[VPackByteStore.PAGE_SIZE] = 2;
        input[input.length - 1] = 3;
        store.append(input, 0, input.length);
        input[0] = 99;
        assertEquals(input.length, store.size());
        assertEquals(1, store.byteAt(0L));
        assertEquals(2, store.byteAt(VPackByteStore.PAGE_SIZE));
        assertEquals(3, store.byteAt(input.length - 1L));
        assertArrayEquals(new byte[] { 1, 2, 3 }, new byte[] {
            store.byteAt(0L), store.byteAt(VPackByteStore.PAGE_SIZE),
            store.byteAt(input.length - 1L)
        });
    }

    @Test
    void arenaResetAndReleaseClearOwnedPages() {
        VPackOutputArena arena = new VPackOutputArena((long) VPackByteStore.PAGE_SIZE * 2L + 1L);
        VPackByteStore firstRoot = arena.store();
        arena.append(new byte[VPackByteStore.PAGE_SIZE + 1]);
        assertEquals(2, firstRoot.pageCount());

        arena.reset();
        assertEquals(0, firstRoot.pageCount());
        VPackByteStore secondRoot = arena.store();
        arena.append(new byte[] { 7 });
        assertEquals(1, secondRoot.pageCount());
        arena.release();
        assertEquals(0, secondRoot.pageCount());
    }

    @Test
    void arenaRangeAppendCopiesInBoundedChunksAndAccountsBytes() {
        VPackByteStore source = VPackByteStore.owned();
        byte[] input = new byte[VPackByteStore.PAGE_SIZE + 3];
        input[0] = 1;
        input[VPackByteStore.PAGE_SIZE] = 2;
        input[input.length - 1] = 3;
        source.append(input, 0, input.length);

        VPackOutputArena arena = new VPackOutputArena((long) input.length);
        VPackByteStore.Range result = arena.append(source.range(0L, input.length));

        assertEquals(input.length, result.length());
        assertEquals(input.length, arena.size());
        assertEquals(input.length, arena.copiedBytes());
        assertEquals(0L, source.materializedRanges());
        assertEquals(1, result.byteAt(0L));
        assertEquals(2, result.byteAt(VPackByteStore.PAGE_SIZE));
        assertEquals(3, result.byteAt(input.length - 1L));
    }

    @Test
    void arenaRangeAppendRejectsBeforeMaterializingOrMutatingOnBudgetFailure() {
        VPackByteStore source = VPackByteStore.owned();
        byte[] input = new byte[VPackByteStore.PAGE_SIZE + 3];
        source.append(input, 0, input.length);
        VPackByteStore.Range range = source.range(0L, input.length);

        VPackOutputArena arena = new VPackOutputArena(1L);
        arena.append((byte) 7);
        VPackByteStore destination = arena.store();
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                () -> arena.append(range));

        assertEquals(1L, arena.size());
        assertEquals(0L, arena.copiedBytes());
        assertEquals(1, destination.pageCount());
        assertEquals(7, destination.byteAt(0L));
        assertEquals(0L, source.materializedRanges());
    }

    @Test
    void arenaRangeAppendRejectsReleasedSourceDeterministically() {
        VPackByteStore source = VPackByteStore.owned();
        source.append((byte) 1);
        VPackByteStore.Range range = source.range(0L, 1L);
        source.release();
        VPackOutputArena arena = new VPackOutputArena(1L);

        assertThrows(tools.jackson.core.exc.StreamReadException.class,
                () -> arena.append(range));
        assertEquals(0L, arena.size());
    }

    @Test
    void releaseIsDeterministicAndBorrowedStoreCannotBeMutated() {
        VPackByteStore borrowed = VPackByteStore.borrowed(new byte[] { 1 }, 0, 1);
        assertThrows(tools.jackson.core.exc.StreamWriteException.class,
                () -> borrowed.append((byte) 2));
        borrowed.release();
        borrowed.release();
        assertThrows(tools.jackson.core.exc.StreamReadException.class, borrowed::size);
    }
}
