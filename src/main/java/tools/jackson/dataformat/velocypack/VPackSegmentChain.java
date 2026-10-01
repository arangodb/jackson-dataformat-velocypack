package tools.jackson.dataformat.velocypack;

import java.io.IOException;
import java.io.OutputStream;

/** Linked ranges retained until transfer or release under their owner's lifecycle. */
@SuppressWarnings({"resource", "AutoCloseableResource"})
final class VPackSegmentChain implements AutoCloseable {
    private final VPackOutputArena arena;
    private Node head;
    private Node tail;
    private long size;
    private int nodeCount;
    private long transfers;
    private VPackByteStore originStore;
    private boolean released;

    VPackSegmentChain(VPackOutputArena arena) {
        if (arena == null) {
            throw new NullPointerException("arena");
        }
        this.arena = arena;
    }

    boolean isEmpty() {
        return head == null;
    }

    long size() {
        ensureOpen();
        return size;
    }

    int nodeCount() {
        ensureOpen();
        return nodeCount;
    }

    long transferCount() {
        return transfers;
    }

    void append(VPackByteStore.Range range) {
        appendRange(range);
    }

    void appendStringFrame(byte[] payload, int offset, int length) {
        VPackByteStore currentStore = ensureCurrentStore();
        if (payload == null || offset < 0 || length < 0
                || offset > payload.length - length) {
            throw VPackErrors.write("segment chain", "string payload range is invalid");
        }
        long totalLength = VPackBounds.checkedAdd(length, length <= 126 ? 1L : 9L,
                "segment chain string frame");
        long newSize = VPackBounds.checkedAdd(size, totalLength, "segment chain size");
        long offsetInStore = arena.appendStringFrame(payload, offset, length);
        if (ensureCurrentStore() != currentStore) {
            throw VPackErrors.write("segment chain", "arena root changed during append");
        }
        currentStore.validateRange(offsetInStore, totalLength, "segment chain string range");
        appendRange(currentStore, offsetInStore, totalLength, newSize);
    }

    void prepend(VPackByteStore.Range range) {
        VPackByteStore currentStore = validateRange(range);
        long length = range.length();
        if (length == 0L) {
            return;
        }
        long offset = range.offset();
        long newSize = VPackBounds.checkedAdd(size, length, "segment chain size");
        if (head != null && adjacentBefore(currentStore, offset, length, head)) {
            long mergedLength = VPackBounds.checkedAdd(length, head.length,
                    "segment chain node length");
            head.offset = offset;
            head.length = mergedLength;
        } else {
            Node node = new Node(currentStore, offset, length);
            node.next = head;
            head = node;
            if (tail == null) {
                tail = node;
                originStore = node.store;
            }
            nodeCount++;
        }
        size = newSize;
    }

    void transferFrom(VPackSegmentChain donor) {
        ensureCurrentStore();
        if (donor == null) {
            throw VPackErrors.write("segment chain", "donor is null");
        }
        donor.ensureOpen();
        if (donor == this) {
            throw VPackErrors.write("segment chain", "a chain cannot transfer to itself");
        }
        if (donor.arena != arena) {
            throw VPackErrors.write("segment chain", "chains do not share an output arena");
        }
        donor.ensureCurrentStore();
        if (donor.head == null) {
            return;
        }
        long combinedSize = VPackBounds.checkedAdd(size, donor.size, "segment chain size");
        if (donor.nodeCount > Integer.MAX_VALUE - nodeCount) {
            throw VPackErrors.write("segment chain", "node count overflow");
        }
        Node donorHead = donor.head;
        boolean mergeBoundary = tail != null && adjacent(tail, donorHead);
        long mergedLength = 0L;
        if (mergeBoundary) {
            mergedLength = VPackBounds.checkedAdd(tail.length, donorHead.length,
                    "segment chain node length");
        }
        int combinedNodeCount = nodeCount + donor.nodeCount;
        if (mergeBoundary) {
            combinedNodeCount--;
        }
        if (tail == null) {
            head = donorHead;
            tail = donor.tail;
            originStore = donor.originStore;
        } else if (mergeBoundary) {
            tail.length = mergedLength;
            tail.next = donorHead.next;
            if (donorHead != donor.tail) {
                tail = donor.tail;
            }
        } else {
            tail.next = donorHead;
            tail = donor.tail;
        }
        size = combinedSize;
        nodeCount = combinedNodeCount;
        donor.head = null;
        donor.tail = null;
        donor.size = 0L;
        donor.nodeCount = 0;
        donor.originStore = null;
        transfers++;
    }

    void clear() {
        ensureOpen();
        head = null;
        tail = null;
        size = 0L;
        nodeCount = 0;
        originStore = null;
    }

    void writeTo(OutputStream output) throws IOException {
        ensureCurrentStore();
        if (output == null) {
            throw new NullPointerException("output");
        }
        for (Node node = head; node != null; node = node.next) {
            node.store.writeTo(node.offset, node.length, output);
        }
    }

    void writeTo(OutputStream output, byte[] buffer) throws IOException {
        ensureCurrentStore();
        if (output == null) {
            throw new NullPointerException("output");
        }
        if (buffer == null || buffer.length == 0) {
            throw new IllegalArgumentException("output buffer is empty");
        }
        int buffered = 0;
        for (Node node = head; node != null; node = node.next) {
            buffered = node.store.writeTo(node.offset, node.length, output, buffer, buffered);
        }
        if (buffered != 0) {
            output.write(buffer, 0, buffered);
        }
    }

    byte[] toByteArray() {
        ensureCurrentStore();
        byte[] result = new byte[VPackBounds.checkedInt(size, "segment chain materialization")];
        int offset = 0;
        for (Node node = head; node != null; node = node.next) {
            int length = VPackBounds.checkedInt(node.length, "segment chain node");
            node.store.copyTo(node.offset, result, offset, length);
            offset += length;
        }
        return result;
    }

    @Override
    public void close() {
        release();
    }

    void release() {
        if (released) {
            return;
        }
        clear();
        released = true;
    }

    private VPackByteStore validateRange(VPackByteStore.Range range) {
        if (range == null) {
            throw VPackErrors.write("segment chain", "range is null");
        }
        VPackByteStore currentStore = ensureCurrentStore();
        if (range.store() != currentStore) {
            throw VPackErrors.write("segment chain", "range belongs to another store");
        }
        return currentStore;
    }

    private void appendRange(VPackByteStore.Range range) {
        VPackByteStore currentStore = validateRange(range);
        long length = range.length();
        if (length == 0L) {
            return;
        }
        ensureCurrentStore();
        long offset = range.offset();
        long newSize = VPackBounds.checkedAdd(size, length, "segment chain size");
        appendRange(currentStore, offset, length, newSize);
    }

    private void appendRange(VPackByteStore currentStore, long offset, long length,
            long newSize) {
        if (length == 0L) {
            return;
        }
        if (tail != null && adjacent(tail.store, tail.offset, tail.length,
                currentStore, offset, length)) {
            tail.length = VPackBounds.checkedAdd(tail.length, length,
                    "segment chain node length");
        } else {
            Node node = new Node(currentStore, offset, length);
            if (tail == null) {
                head = node;
                originStore = node.store;
            } else {
                tail.next = node;
            }
            tail = node;
            nodeCount++;
        }
        size = newSize;
    }

    private void ensureOpen() {
        if (released) {
            throw VPackErrors.write("segment chain", "chain has been released");
        }
    }

    private VPackByteStore ensureCurrentStore() {
        ensureOpen();
        VPackByteStore currentStore = arena.store();
        if (currentStore.isReleased()) {
            throw VPackErrors.write("segment chain", "arena store has been released");
        }
        if (head != null && originStore != currentStore) {
            throw VPackErrors.write("segment chain", "chain belongs to a released arena root");
        }
        return currentStore;
    }

    private static boolean adjacent(VPackByteStore firstStore, long firstOffset,
            long firstLength, VPackByteStore secondStore, long secondOffset,
            long secondLength) {
        return firstStore == secondStore
                && firstOffset <= Long.MAX_VALUE - firstLength
                && firstOffset + firstLength == secondOffset;
    }

    private static boolean adjacent(Node first, Node second) {
        return adjacent(first.store, first.offset, first.length,
                second.store, second.offset, second.length);
    }

    private static boolean adjacentBefore(VPackByteStore firstStore, long firstOffset,
            long firstLength, Node second) {
        return firstStore == second.store
                && firstOffset <= Long.MAX_VALUE - firstLength
                && firstOffset + firstLength == second.offset;
    }

    private static final class Node {
        private final VPackByteStore store;
        private long offset;
        private long length;
        private Node next;

        private Node(VPackByteStore store, long offset, long length) {
            this.store = store;
            this.offset = offset;
            this.length = length;
        }
    }
}
