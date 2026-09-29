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
        addLast(newNode(range));
    }

    void prepend(VPackByteStore.Range range) {
        Node node = newNode(range);
        if (node == null) {
            return;
        }
        ensureOpen();
        long newSize = VPackBounds.checkedAdd(size, node.length, "segment chain size");
        if (head != null && adjacent(node, head)) {
            long mergedLength = VPackBounds.checkedAdd(node.length, head.length,
                    "segment chain node length");
            head.offset = node.offset;
            head.length = mergedLength;
        } else {
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
        byte[] scratch = new byte[VPackByteStore.PAGE_SIZE];
        for (Node node = head; node != null; node = node.next) {
            long offset = node.offset;
            long remaining = node.length;
            while (remaining != 0L) {
                int count = (int) Math.min(remaining, scratch.length);
                node.store.copyTo(offset, scratch, 0, count);
                output.write(scratch, 0, count);
                offset += count;
                remaining -= count;
            }
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

    private Node newNode(VPackByteStore.Range range) {
        if (range == null) {
            throw VPackErrors.write("segment chain", "range is null");
        }
        VPackByteStore currentStore = ensureCurrentStore();
        if (range.store() != currentStore) {
            throw VPackErrors.write("segment chain", "range belongs to another store");
        }
        return range.length() == 0L ? null
                : new Node(range.store(), range.offset(), range.length());
    }

    private void addLast(Node node) {
        if (node == null) {
            return;
        }
        ensureCurrentStore();
        long newSize = VPackBounds.checkedAdd(size, node.length, "segment chain size");
        if (tail != null && adjacent(tail, node)) {
            tail.length = VPackBounds.checkedAdd(tail.length, node.length,
                    "segment chain node length");
        } else {
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

    private static boolean adjacent(Node first, Node second) {
        return first.store == second.store
                && first.offset <= Long.MAX_VALUE - first.length
                && first.offset + first.length == second.offset;
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
