package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.*;

class VPackComplexityTest {
    @Test
    void nestedContainersTransferSegmentsAndCopyEachWireByteOnce() throws Exception {
        final int depth = 256;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(output);
        for (int i = 0; i < depth; ++i) generator.writeStartArray();
        generator.writeNumber(7);
        assertEquals(depth, generator.retainedContainerDepth());
        assertEquals(depth, generator.retainedRootEntries());
        for (int i = 0; i < depth; ++i) generator.writeEndArray();
        byte[] wire = output.toByteArray();

        assertTrue(wire.length > depth);
        // The outermost chain is written directly; all inner chains transfer once.
        assertEquals(depth - 1L, generator.segmentTransfers());
        // The one-byte scalar is appended directly; only framing bytes are copied.
        assertEquals(wire.length - 1L, generator.bytesCopied());
        generator.close();
        assertEquals(0, generator.streamWriteOutputBuffered());
    }

    @Test
    void directSegmentTransferMovesOwnershipWithoutCopyingPayload() {
        VPackOutputArena arena = new VPackOutputArena(1024);
        VPackSegmentChain parent = new VPackSegmentChain(arena);
        VPackSegmentChain child = new VPackSegmentChain(arena);
        child.append(arena.append(new byte[] { 1, 2, 3, 4 }));
        parent.transferFrom(child);
        assertEquals(4, parent.size());
        assertEquals(0, child.size());
        assertEquals(1, parent.transferCount());
        assertEquals(4, arena.copiedBytes());
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, parent.toByteArray());
        parent.close();
        child.close();
        arena.close();
        assertTrue(arena.isReleased());
    }

    @Test
    void alternatingNestedArraysAndObjectsStayLinear() throws Exception {
        final int depth = 128;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(output);
        for (int i = 0; i < depth; ++i) {
            if ((i & 1) == 0) {
                generator.writeStartArray();
            } else {
                generator.writeStartObject();
                generator.writeName("k");
            }
        }
        generator.writeNumber(9);
        assertEquals(depth, generator.retainedContainerDepth());
        assertEquals(depth, generator.retainedRootEntries());
        assertEquals(depth / 2L, generator.retainedRootNameBytes());
        for (int i = depth - 1; i >= 0; --i) {
            if ((i & 1) == 0) generator.writeEndArray();
            else generator.writeEndObject();
        }
        byte[] wire = output.toByteArray();
        assertEquals(depth - 1L, generator.segmentTransfers());
        // The one-byte scalar is appended directly; only framing bytes are copied.
        assertEquals(wire.length - 1L, generator.bytesCopied());

        int tokens = 0;
        int maxDepth = 0;
        long scanned = -1L;
        long copied = -1L;
        int retainedEntries = -1;
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(wire)) {
            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                ++tokens;
                maxDepth = Math.max(maxDepth, parser.retainedContainerDepth());
                if (token == JsonToken.END_ARRAY && parser.retainedContainerDepth() == 0) {
                    scanned = parser.sourceBytesScanned();
                    copied = parser.sourceBytesCopied();
                    retainedEntries = parser.retainedRootEntries();
                    assertEquals(depth / 2L, parser.retainedRootNameBytes());
                }
            }
        }
        assertEquals(2 * depth + depth / 2 + 1, tokens);
        assertEquals(depth, maxDepth);
        assertEquals(depth, retainedEntries);
        assertTrue(scanned <= 16L * wire.length, "source scan work must remain linear");
        assertTrue(copied <= 8L * wire.length, "nested input must not be copied per ancestor");
        generator.close();
    }
}
