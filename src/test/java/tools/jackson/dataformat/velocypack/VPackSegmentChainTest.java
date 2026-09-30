package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackSegmentChainTest {
    @Test
    void arenaCopiesCallerBytesAndChainTransfersNodesInOrder() throws Exception {
        VPackOutputArena arena = new VPackOutputArena(100L);
        VPackSegmentChain parent = new VPackSegmentChain(arena);
        VPackSegmentChain child = new VPackSegmentChain(arena);
        byte[] caller = { 1, 2, 3 };
        child.append(arena.append(caller));
        caller[0] = 99;
        child.append(arena.append(new byte[] { 4, 5 }));
        parent.append(arena.append(new byte[] { 0 }));
        parent.transferFrom(child);
        assertEquals(0, child.nodeCount());
        assertEquals(2, parent.nodeCount());
        assertEquals(6L, parent.size());
        assertEquals(1L, parent.transferCount());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        parent.writeTo(output);
        assertArrayEquals(new byte[] { 0, 1, 2, 3, 4, 5 }, output.toByteArray());
        assertEquals(6L, arena.copiedBytes());
    }

    @Test
    void writesLongPayloadAcrossSeveralPagesByteForByte() throws Exception {
        int length = 3 * VPackByteStore.PAGE_SIZE + 17;
        byte[] expected = new byte[length];
        for (int i = 0; i < length; ++i) expected[i] = (byte) (i * 31);
        VPackOutputArena arena = new VPackOutputArena((long) length);
        VPackSegmentChain chain = new VPackSegmentChain(arena);
        chain.append(arena.append(expected));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        chain.writeTo(output);
        assertArrayEquals(expected, output.toByteArray());
    }

    @Test
    void appendPrependAndTransferCoalesceAdjacentRanges() throws Exception {
        VPackOutputArena arena = new VPackOutputArena(20L);
        VPackByteStore.Range first = arena.append(new byte[] { 1 });
        VPackByteStore.Range second = arena.append(new byte[] { 2, 3 });

        VPackSegmentChain appended = new VPackSegmentChain(arena);
        appended.append(first);
        appended.append(second);
        assertEquals(1, appended.nodeCount());
        assertArrayEquals(new byte[] { 1, 2, 3 }, appended.toByteArray());

        VPackSegmentChain prepended = new VPackSegmentChain(arena);
        prepended.append(second);
        prepended.prepend(first);
        assertEquals(1, prepended.nodeCount());
        assertArrayEquals(new byte[] { 1, 2, 3 }, prepended.toByteArray());

        VPackSegmentChain parent = new VPackSegmentChain(arena);
        VPackSegmentChain donor = new VPackSegmentChain(arena);
        parent.append(first);
        donor.append(second);
        parent.transferFrom(donor);
        assertEquals(1, parent.nodeCount());
        assertEquals(0, donor.nodeCount());
        assertTrue(donor.isEmpty());
        assertArrayEquals(new byte[] { 1, 2, 3 }, parent.toByteArray());
    }

    @Test
    void resetCannotAliasAnOldChainToReplacementRoot() throws Exception {
        VPackOutputArena arena = new VPackOutputArena(20L);
        VPackSegmentChain chain = new VPackSegmentChain(arena);
        chain.append(arena.append(new byte[] { 1, 2 }));
        VPackByteStore oldStore = arena.store();

        arena.reset();
        VPackByteStore.Range replacement = arena.append(new byte[] { 9, 8 });
        assertEquals(0, oldStore.pageCount());
        assertThrows(tools.jackson.core.exc.StreamWriteException.class, chain::toByteArray);
        assertThrows(tools.jackson.core.exc.StreamWriteException.class,
                () -> chain.writeTo(new ByteArrayOutputStream()));
        assertThrows(tools.jackson.core.exc.StreamWriteException.class,
                () -> chain.append(replacement));
        VPackSegmentChain recipient = new VPackSegmentChain(arena);
        assertThrows(tools.jackson.core.exc.StreamWriteException.class,
                () -> recipient.transferFrom(chain));

        chain.clear();
        chain.append(replacement);
        assertArrayEquals(new byte[] { 9, 8 }, chain.toByteArray());
    }

    @Test
    void transferSplicesMultipleDonorNodesWithOneTransferOperation() {
        VPackOutputArena arena = new VPackOutputArena(100L);
        VPackSegmentChain donor = new VPackSegmentChain(arena);
        VPackSegmentChain parent = new VPackSegmentChain(arena);
        donor.append(arena.append(new byte[] { 1 }));
        arena.append(new byte[] { 99 });
        donor.append(arena.append(new byte[] { 2 }));
        arena.append(new byte[] { 98 });
        donor.append(arena.append(new byte[] { 3 }));

        parent.transferFrom(donor);

        assertEquals(1L, parent.transferCount());
        assertEquals(3, parent.nodeCount());
        assertEquals(0, donor.nodeCount());
        assertTrue(donor.isEmpty());
        assertArrayEquals(new byte[] { 1, 2, 3 }, parent.toByteArray());
    }

    @Test
    void releaseAndClearDropChainOwnership() {
        VPackOutputArena arena = new VPackOutputArena(20L);
        VPackSegmentChain chain = new VPackSegmentChain(arena);
        chain.append(arena.append(new byte[] { 1, 2 }));
        chain.clear();
        assertTrue(chain.isEmpty());
        assertEquals(0, chain.nodeCount());
        assertEquals(0L, chain.size());
        chain.append(arena.append(new byte[] { 3 }));
        chain.release();
        assertTrue(chain.isEmpty());
    }

    @Test
    void releasedArenaRejectsStaleChainConsumptionAndExtension() {
        VPackOutputArena arena = new VPackOutputArena(20L);
        VPackSegmentChain chain = new VPackSegmentChain(arena);
        VPackByteStore.Range oldRange = arena.append(new byte[] { 1 });
        chain.append(oldRange);
        arena.release();

        assertThrows(tools.jackson.core.exc.StreamWriteException.class, chain::toByteArray);
        assertThrows(tools.jackson.core.exc.StreamWriteException.class,
                () -> chain.append(oldRange));
    }

    @Test
    void emptyRangesDoNotCreateNodesAndWrongArenaCannotTransfer() {
        VPackOutputArena first = new VPackOutputArena(10L);
        VPackOutputArena second = new VPackOutputArena(10L);
        VPackSegmentChain a = new VPackSegmentChain(first);
        VPackSegmentChain b = new VPackSegmentChain(second);
        a.append(first.append(new byte[0]));
        assertEquals(0, a.nodeCount());
        assertThrows(tools.jackson.core.exc.StreamWriteException.class,
                () -> a.transferFrom(b));
    }

    @Test
    void rootBudgetChargesEntriesNamesAndBytesOncePerCallAndResets() {
        VPackRootBudget budget = new VPackRootBudget(20L, 1, 4L);
        budget.chargeEntry();
        budget.chargeName(4L);
        budget.chargeBytes(20L);
        assertEquals(1, budget.entries());
        budget.reset();
        assertEquals(0, budget.entries());
        assertEquals(0L, budget.nameBytes());
        assertEquals(0L, budget.bytes());
        budget.release();
        budget.release();
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                budget::bytes);
    }

    @Test
    void arenaBudgetFailureDoesNotRetainAPartialAppend() {
        VPackOutputArena arena = new VPackOutputArena(2L);
        arena.append(new byte[] { 1, 2 });
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                () -> arena.append(new byte[] { 3 }));
        assertEquals(2L, arena.size());
        assertEquals(2L, arena.copiedBytes());
        arena.release();
        arena.release();
        assertThrows(tools.jackson.core.exc.StreamWriteException.class, arena::size);
    }
}
