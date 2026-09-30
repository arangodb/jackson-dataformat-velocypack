package tools.jackson.dataformat.velocypack;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import tools.jackson.core.exc.StreamReadException;

/**
 * Acquires exactly one self-delimiting VPack root from a binary source.
 *
 * <p>The byte-array adapter keeps one borrowed store for the complete supplied
 * slice. Forward-only adapters own one bounded store for the root currently
 * being acquired. This class deliberately frames only the outer value; nested
 * structure validation belongs to the parser. Its source adapters and root
 * metadata are also exercised by package-level ownership tests.</p>
 */
@SuppressWarnings("unused")
final class VPackRootReader implements AutoCloseable {
    enum SourceKind {
        BYTE_ARRAY,
        INPUT_STREAM,
        DATA_INPUT
    }

    private static final long UNLIMITED = -1L;
    private static final int READ_CHUNK = VPackByteStore.PAGE_SIZE;

    private final SourceKind sourceKind;
    private final Object source;
    private final VPackReadConstraints constraints;
    private final long maxDocumentLength;
    private final long initialOffset;
    private final byte[] inputArray;
    private final int inputArrayOffset;
    private final int inputArrayEnd;
    private final InputStream inputStream;
    private final DataInput dataInput;
    private final VPackPageSupplier pageSupplier;
    private final VPackByteStore borrowedStore;
    private long position;
    private boolean failed;
    private boolean closed;

    private VPackRootReader(SourceKind sourceKind, Object source,
            VPackReadConstraints constraints, long initialOffset, long maxDocumentLength,
            byte[] inputArray, int inputArrayOffset, int inputArrayEnd,
            InputStream inputStream, DataInput dataInput,
            VPackPageSupplier pageSupplier) {
        if (constraints == null) {
            throw new NullPointerException("constraints");
        }
        if (initialOffset < 0L) {
            throw new IllegalArgumentException("initialOffset must be non-negative");
        }
        if (maxDocumentLength < -1L) {
            throw new IllegalArgumentException("maxDocumentLength must be non-negative or -1");
        }
        this.sourceKind = sourceKind;
        this.source = source;
        this.constraints = constraints;
        this.initialOffset = initialOffset;
        this.maxDocumentLength = maxDocumentLength <= 0L ? UNLIMITED : maxDocumentLength;
        this.inputArray = inputArray;
        this.inputArrayOffset = inputArrayOffset;
        this.inputArrayEnd = inputArrayEnd;
        this.inputStream = inputStream;
        this.dataInput = dataInput;
        this.pageSupplier = pageSupplier;
        borrowedStore = inputArray == null ? null
                : VPackByteStore.borrowed(inputArray, inputArrayOffset,
                        inputArrayEnd - (long) inputArrayOffset);
        position = initialOffset;
        if (inputArray != null && maxDocumentLength > 0L
                && inputArrayEnd - (long) inputArrayOffset > maxDocumentLength) {
            throw VPackErrors.constraint("document length", 0L,
                    "known byte-array slice exceeds configured document length "
                            + maxDocumentLength);
        }
    }

    static VPackRootReader forByteArray(byte[] input) {
        return forByteArray(input, 0, input == null ? 0 : input.length,
                VPackReadConstraints.defaults());
    }

    static VPackRootReader forByteArray(byte[] input, int offset, int length) {
        return forByteArray(input, offset, length, VPackReadConstraints.defaults());
    }

    static VPackRootReader forByteArray(byte[] input, int offset, int length,
            VPackReadConstraints constraints) {
        return forByteArray(input, offset, length, constraints, 0L, UNLIMITED);
    }

    static VPackRootReader forByteArray(byte[] input, long offset, long length) {
        return forByteArray(input, offset, length, VPackReadConstraints.defaults(),
                0L, UNLIMITED);
    }

    static VPackRootReader forByteArray(byte[] input, long offset, long length,
            VPackReadConstraints constraints, long initialOffset, long maxDocumentLength) {
        int end = VPackBounds.checkedArrayRange(input, offset, length, "byte-array source");
        return new VPackRootReader(SourceKind.BYTE_ARRAY, input, constraints, initialOffset,
                maxDocumentLength, input, (int) offset, end, null, null, null);
    }

    static VPackRootReader forByteArray(byte[] input, int offset, int length,
            VPackReadConstraints constraints, long initialOffset, long maxDocumentLength) {
        return forByteArray(input, offset + 0L, length, constraints,
                initialOffset, maxDocumentLength);
    }

    static VPackRootReader forInputStream(InputStream input) {
        return forInputStream(input, VPackReadConstraints.defaults(), 0L, UNLIMITED);
    }

    static VPackRootReader forInputStream(InputStream input, VPackReadConstraints constraints) {
        return forInputStream(input, constraints, 0L, UNLIMITED);
    }

    static VPackRootReader forInputStream(InputStream input,
            VPackReadConstraints constraints, long initialOffset) {
        return forInputStream(input, constraints, initialOffset, UNLIMITED);
    }

    static VPackRootReader forInputStream(InputStream input, VPackReadConstraints constraints,
            long initialOffset, long maxDocumentLength) {
        return forInputStream(input, constraints, initialOffset, maxDocumentLength, null);
    }

    static VPackRootReader forInputStream(InputStream input, VPackReadConstraints constraints,
            long initialOffset, long maxDocumentLength, VPackPageSupplier pageSupplier) {
        if (input == null) {
            throw new NullPointerException("input");
        }
        return new VPackRootReader(SourceKind.INPUT_STREAM, input, constraints, initialOffset,
                maxDocumentLength, null, 0, 0, input, null, pageSupplier);
    }

    static VPackRootReader forDataInput(DataInput input) {
        return forDataInput(input, VPackReadConstraints.defaults(), 0L, UNLIMITED);
    }

    static VPackRootReader forDataInput(DataInput input, VPackReadConstraints constraints) {
        return forDataInput(input, constraints, 0L, UNLIMITED);
    }

    static VPackRootReader forDataInput(DataInput input,
            VPackReadConstraints constraints, long initialOffset) {
        return forDataInput(input, constraints, initialOffset, UNLIMITED);
    }

    static VPackRootReader forDataInput(DataInput input, VPackReadConstraints constraints,
            long initialOffset, long maxDocumentLength) {
        return forDataInput(input, constraints, initialOffset, maxDocumentLength, null);
    }

    static VPackRootReader forDataInput(DataInput input, VPackReadConstraints constraints,
            long initialOffset, long maxDocumentLength, VPackPageSupplier pageSupplier) {
        if (input == null) {
            throw new NullPointerException("input");
        }
        return new VPackRootReader(SourceKind.DATA_INPUT, input, constraints, initialOffset,
                maxDocumentLength, null, 0, 0, null, input, pageSupplier);
    }

    SourceKind sourceKind() {
        return sourceKind;
    }

    Object source() {
        return source;
    }

    long logicalPosition() {
        return position;
    }

    /**
     * Detach bytes already present in a borrowed byte-array source. Forward-only
     * sources have no unread bytes beyond the root that has already been framed,
     * so they return {@code null} without reading from the source.
     */
    VPackByteStore.Range detachRemaining() {
        ensureUsable();
        if (sourceKind != SourceKind.BYTE_ARRAY) {
            return null;
        }
        long relative = position - initialOffset;
        long available = inputArrayEnd - (long) inputArrayOffset;
        if (relative < 0L || relative > available) {
            throw VPackErrors.malformed("releaseBuffered", position,
                    "logical position is outside the byte-array source");
        }
        position = VPackBounds.checkedAdd(initialOffset, available,
                "logical source position", position);
        return borrowedStore.range(relative, available - relative);
    }

    /** Acquire the next root, or {@code null} only at a clean source boundary. */
    Root nextRoot() {
        ensureUsable();
        if (sourceKind == SourceKind.BYTE_ARRAY) {
            return nextByteArrayRoot();
        }
        return nextForwardOnlyRoot();
    }

    private Root nextByteArrayRoot() {
        long relative = position - initialOffset;
        long available = inputArrayEnd - (long) inputArrayOffset;
        if (relative == available) {
            return null;
        }
        try {
            Frame frame = frameArray(inputArray, inputArrayOffset + VPackBounds.checkedInt(relative,
                    "byte-array logical position"), inputArrayEnd, relative);
            checkDocumentEnd(position, frame.totalLength, relative);
            long rootStart = position;
            position = VPackBounds.checkedAdd(position, frame.totalLength,
                    "logical source position", relative);
            return new Root(borrowedStore, borrowedStore.range(relative, frame.totalLength),
                    rootStart, position, frame.marker, frame.classification, sourceKind, source,
                    true);
        } catch (RuntimeException e) {
            failed = true;
            throw e;
        }
    }

    private Root nextForwardOnlyRoot() {
        VPackByteStore store = VPackByteStore.owned(pageSupplier);
        long rootStart = position;
        try {
            int marker = readOne(store, true);
            if (marker < 0) {
                store.release();
                return null;
            }
            VPackMarker classification = VPackMarker.requireSupported(marker, "root marker",
                    rootStart);
            long total;
            switch (classification) {
            case EMPTY_ARRAY, EMPTY_OBJECT, NULL, FALSE, TRUE, MIN_KEY, MAX_KEY,
                    SMALL_POSITIVE, SMALL_NEGATIVE -> total = 1L;
            case DOUBLE, UTC_DATE -> total = 9L;
            case SIGNED_INTEGER, UNSIGNED_INTEGER -> {
                int width = VPackMarker.width(marker);
                total = 1L + width;
                ensureRootLength(store, total);
                readAndAppend(store, width);
            }
            case SHORT_STRING -> {
                total = VPackBounds.checkedAdd(1L, marker - 0x40L, "short string length",
                        rootStart);
                ensureRootLength(store, total, rootStart);
                readAndAppend(store, VPackBounds.checkedInt(total - 1L,
                        "short string payload length"));
            }
            case LONG_STRING -> {
                long lengthBits = readHeader(store, 8);
                long length = VPackBounds.readStructural(lengthBits, 8,
                        logicalOffset(rootStart, 1L));
                total = VPackBounds.checkedAdd(9L, length, "long string length",
                        logicalOffset(rootStart, 1L));
                ensureRootLength(store, total, rootStart);
                readAndAppend(store, VPackBounds.checkedInt(length, "long string payload length"));
            }
            case BINARY -> {
                int width = VPackMarker.width(marker);
                long lengthBits = readHeader(store, width);
                long length = VPackBounds.readScalarLength(lengthBits, width,
                        logicalOffset(rootStart, 1L));
                total = VPackBounds.checkedAdd(1L + width, length, "binary length",
                        logicalOffset(rootStart, 1L));
                ensureRootLength(store, total, rootStart);
                readAndAppend(store, VPackBounds.checkedInt(length, "binary payload length"));
            }
            case POSITIVE_BCD, NEGATIVE_BCD -> {
                int width = VPackMarker.width(marker);
                long lengthBits = readHeader(store, width);
                long length = VPackBounds.readScalarLength(lengthBits, width,
                        logicalOffset(rootStart, 1L));
                if (length == 0L) {
                    throw VPackErrors.malformed("BCD root", logicalOffset(rootStart, 1L),
                            "mantissa length must be positive");
                }
                total = VPackBounds.checkedAdd(1L + width + 4L, length, "BCD length",
                        logicalOffset(rootStart, 1L));
                ensureRootLength(store, total, rootStart);
                readAndAppend(store, 4);
                readAndAppend(store, VPackBounds.checkedInt(length, "BCD mantissa length"));
            }
            case EQUAL_ARRAY, INDEXED_ARRAY, SORTED_OBJECT, UNSORTED_OBJECT -> {
                int width = VPackMarker.width(marker);
                long lengthBits = readHeader(store, width);
                long length = VPackBounds.readStructural(lengthBits, width,
                        logicalOffset(rootStart, 1L));
                long minimum = 1L + width;
                if (length < minimum) {
                    throw VPackErrors.malformed("container length", logicalOffset(rootStart, 1L),
                            "declared length is smaller than its consumed header");
                }
                total = length;
                ensureRootLength(store, total, rootStart);
                readAndAppend(store, VPackBounds.checkedInt(total - minimum,
                        "container payload length"));
            }
            case COMPACT_ARRAY, COMPACT_OBJECT -> {
                long[] compact = readCompactLength(store);
                total = compact[0];
                ensureRootLength(store, total, rootStart);
                readAndAppend(store, VPackBounds.checkedInt(total - compact[1],
                        "compact container payload length"));
            }
            default -> throw VPackErrors.malformed("root marker", rootStart,
                    "unsupported root marker");
            }
            ensureRootLength(store, total, rootStart);
            if (store.size() < total) {
                readAndAppend(store, VPackBounds.checkedInt(total - store.size(),
                        "root payload length"));
            }
            long rootEnd = VPackBounds.checkedAdd(rootStart, total, "logical source position",
                    rootStart);
            return new Root(store, store.range(0L, total), rootStart, rootEnd, marker,
                    classification, sourceKind, source, false);
        } catch (RuntimeException e) {
            failed = true;
            store.release();
            throw e;
        }
    }

    private Frame frameArray(byte[] input, int start, int end, long logicalStart) {
        int marker = input[start] & 0xFF;
        VPackMarker classification = VPackMarker.requireSupported(marker, "root marker",
                logicalStart);
        long total;
        switch (classification) {
        case EMPTY_ARRAY, EMPTY_OBJECT, NULL, FALSE, TRUE, MIN_KEY, MAX_KEY,
                SMALL_POSITIVE, SMALL_NEGATIVE -> total = 1L;
        case DOUBLE, UTC_DATE -> total = 9L;
        case SIGNED_INTEGER, UNSIGNED_INTEGER -> {
            int width = VPackMarker.width(marker);
            requireArrayBytes(start, end, 1L + width, "fixed integer root", logicalStart);
            total = 1L + width;
        }
        case SHORT_STRING -> total = 1L + marker - 0x40L;
        case LONG_STRING -> {
            requireArrayBytes(start, end, 9L, "long string header", logicalStart);
            long length = VPackBounds.readStructural(input, start + 1, 8,
                    logicalOffset(logicalStart, 1L));
            total = VPackBounds.checkedAdd(9L, length, "long string length",
                    logicalOffset(logicalStart, 1L));
        }
        case BINARY -> {
            int width = VPackMarker.width(marker);
            requireArrayBytes(start, end, 1L + width, "binary header", logicalStart);
            long length = VPackBounds.readScalarLength(input, start + 1, width,
                    logicalOffset(logicalStart, 1L));
            total = VPackBounds.checkedAdd(1L + width, length, "binary length",
                    logicalOffset(logicalStart, 1L));
        }
        case POSITIVE_BCD, NEGATIVE_BCD -> {
            int width = VPackMarker.width(marker);
            requireArrayBytes(start, end, 1L + width, "BCD length prefix", logicalStart);
            long length = VPackBounds.readScalarLength(input, start + 1, width,
                    logicalOffset(logicalStart, 1L));
            if (length == 0L) {
                throw VPackErrors.malformed("BCD root", logicalOffset(logicalStart, 1L),
                        "mantissa length must be positive");
            }
            total = VPackBounds.checkedAdd(1L + width + 4L, length, "BCD length",
                    logicalOffset(logicalStart, 1L));
        }
        case EQUAL_ARRAY, INDEXED_ARRAY, SORTED_OBJECT, UNSORTED_OBJECT -> {
            int width = VPackMarker.width(marker);
            requireArrayBytes(start, end, 1L + width, "container length header", logicalStart);
            total = VPackBounds.readStructural(input, start + 1, width,
                    logicalOffset(logicalStart, 1L));
            if (total < 1L + width) {
                throw VPackErrors.malformed("container length", logicalOffset(logicalStart, 1L),
                        "declared length is smaller than its consumed header");
            }
        }
        case COMPACT_ARRAY, COMPACT_OBJECT -> {
            long[] compact = readCompactArray(input, start, end, logicalStart);
            total = compact[0];
        }
        default -> throw VPackErrors.malformed("root marker", logicalStart,
                "unsupported root marker");
        }
        checkRootLength(total, logicalStart);
        checkDocumentEnd(position, total, logicalStart);
        requireArrayBytes(start, end, total, "root frame", logicalStart);
        return new Frame(marker, classification, total);
    }

    private long[] readCompactLength(VPackByteStore store) {
        long value = 0L;
        int groups = 0;
        while (groups < 8) {
            int next = readOne(store, false);
            if (next < 0) {
                throw truncated("compact length", position);
            }
            if (groups == 7 && (next & 0x80) != 0) {
                throw VPackErrors.malformed("compact length", position - 1L,
                        "length uses more than eight groups");
            }
            value |= (long) (next & 0x7F) << (7 * groups);
            groups++;
            if ((next & 0x80) == 0) {
                long header = 1L + groups;
                if (value < header) {
                    throw VPackErrors.malformed("compact length", position - groups,
                            "declared length is smaller than its header");
                }
                return new long[] { value, header };
            }
        }
        throw VPackErrors.malformed("compact length", position - groups,
                "length uses more than eight groups");
    }

    private long[] readCompactArray(byte[] input, int start, int end, long logicalStart) {
        long value = 0L;
        int groups = 0;
        int cursor = start + 1;
        while (groups < 8) {
            if (cursor >= end) {
                throw truncated("compact length",
                        logicalOffset(logicalStart, cursor - start));
            }
            int next = input[cursor++] & 0xFF;
            if (groups == 7 && (next & 0x80) != 0) {
                throw VPackErrors.malformed("compact length",
                        logicalOffset(logicalStart, cursor - start - 1L),
                        "length uses more than eight groups");
            }
            value |= (long) (next & 0x7F) << (7 * groups);
            groups++;
            if ((next & 0x80) == 0) {
                long header = 1L + groups;
                if (value < header) {
                    throw VPackErrors.malformed("compact length", logicalOffset(logicalStart, 1L),
                            "declared length is smaller than its header");
                }
                return new long[] { value, header };
            }
        }
        throw VPackErrors.malformed("compact length", logicalOffset(logicalStart, 1L),
                "length uses more than eight groups");
    }

    private long readHeader(VPackByteStore store, int length) {
        // The header itself must fit before a forward-only source is touched.
        ensureRootLength(store, VPackBounds.checkedAdd(store.size(), length,
                "root header length", position));
        readExact(length, store);
        return store.readLE(store.size() - length, length);
    }

    private void readAndAppend(VPackByteStore store, int length) {
        if (length < 0) {
            throw VPackErrors.malformed("root payload", "length is negative");
        }
        if (length == 0) {
            return;
        }
        int remaining = length;
        while (remaining != 0) {
            int count = Math.min(remaining, READ_CHUNK);
            readExact(count, store);
            remaining -= count;
        }
    }

    private int readOne(VPackByteStore store, boolean marker) {
        // A marker read is also the clean-EOF probe at an exact document limit.
        // Compact length continuation bytes, unlike that probe, are mandatory.
        if (!marker) {
            ensureRootLength(store, VPackBounds.checkedAdd(store.size(), 1L,
                    "root header length", position));
        }
        try {
            int value;
            if (sourceKind == SourceKind.INPUT_STREAM) {
                value = inputStream.read();
            } else {
                value = dataInput.readUnsignedByte();
            }
            if (value < 0) {
                return -1;
            }
            store.append((byte) value);
            position = VPackBounds.checkedAdd(position, 1L, "logical source position", position);
            return value;
        } catch (EOFException e) {
            if (marker) {
                return -1;
            }
            throw truncated("root", position, e);
        } catch (IOException e) {
            throw VPackErrors.input("root input", position, e);
        }
    }

    private void readExact(int length, VPackByteStore store) {
        if (sourceKind == SourceKind.DATA_INPUT) {
            try {
                store.appendFullyFrom(dataInput, length);
                position = VPackBounds.checkedAdd(position, length,
                        "logical source position", position);
            } catch (EOFException e) {
                throw truncated("root payload", position, e);
            } catch (IOException e) {
                throw VPackErrors.input("root input", position, e);
            }
            return;
        }

        int remaining = length;
        while (remaining != 0) {
            int requested = Math.min(remaining, READ_CHUNK);
            int requestedSlice = Math.min(requested, VPackByteStore.PAGE_SIZE
                    - (int) (store.size() % VPackByteStore.PAGE_SIZE));
            int count;
            try {
                count = store.appendFrom(inputStream, requested);
                if (count == 0) {
                    int one = inputStream.read();
                    if (one < 0) {
                        throw truncated("root payload", position);
                    }
                    store.append((byte) one);
                    count = 1;
                } else if (count < 0) {
                    throw truncated("root payload", position);
                } else if (count > requestedSlice) {
                    throw VPackErrors.malformed("root input", position,
                            "input returned more bytes than requested");
                }
            } catch (EOFException e) {
                throw truncated("root payload", position, e);
            } catch (IOException e) {
                throw VPackErrors.input("root input", position, e);
            }
            remaining -= count;
            position = VPackBounds.checkedAdd(position, count,
                    "logical source position", position);
        }
    }

    private void ensureRootLength(VPackByteStore store, long total) {
        ensureRootLength(store, total, position - store.size());
    }

    private void ensureRootLength(VPackByteStore store, long total, long errorOffset) {
        checkRootLength(total, errorOffset);
        long rootStart = VPackBounds.checkedSubtract(position, store.size(),
                "logical root start");
        checkDocumentEnd(rootStart, total, errorOffset);
    }

    private void checkRootLength(long total, long errorOffset) {
        if (total < 1L) {
            throw VPackErrors.malformed("root length", errorOffset, "root length must be positive");
        }
        if (total > constraints.getMaxRootValueBytes()) {
            throw VPackErrors.constraint("root length", errorOffset,
                    "root length exceeds configured root-byte budget "
                            + constraints.getMaxRootValueBytes());
        }
    }

    private void checkDocumentEnd(long rootStart, long total, long errorOffset) {
        if (maxDocumentLength > 0L) {
            long consumed = VPackBounds.checkedSubtract(rootStart, initialOffset,
                    "logical document position");
            long end = VPackBounds.checkedAdd(consumed, total, "logical document length",
                    errorOffset);
            if (end > maxDocumentLength) {
                throw VPackErrors.constraint("document length", errorOffset,
                        "framed input exceeds configured document length " + maxDocumentLength);
            }
        }
    }

    private static void requireArrayBytes(int start, int end, long length, String context,
            long logicalStart) {
        if (length < 0L || length > end - (long) start) {
            long available = Math.max(0L, end - (long) start);
            long missingAt = Math.min(length, available);
            throw VPackErrors.malformed(context, logicalOffset(logicalStart, missingAt),
                    "root is truncated before its declared length");
        }
    }

    private static long logicalOffset(long base, long delta) {
        return VPackBounds.checkedAdd(base, delta, "logical byte offset", base);
    }

    private static StreamReadException truncated(String context, long offset) {
        return VPackErrors.malformed(context, offset, "truncated root");
    }

    private static StreamReadException truncated(String context, long offset, Throwable cause) {
        return VPackErrors.input(context, offset, cause);
    }

    private void ensureUsable() {
        if (closed) {
            throw VPackErrors.malformed("root reader", position, "reader is closed");
        }
        if (failed) {
            throw VPackErrors.malformed("root reader", position,
                    "reader cannot continue after a framing failure");
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        if (borrowedStore != null) {
            borrowedStore.release();
        }
    }

    /** Called after the parser has released its current root. */
    void releasePageSupplier() {
        if (pageSupplier != null) {
            pageSupplier.close();
        }
    }

    /** One framed root and the exact storage range containing it. */
    static final class Root implements AutoCloseable {
        private final VPackByteStore store;
        private final VPackByteStore.Range range;
        private final long startOffset;
        private final long endOffset;
        private final int marker;
        private final VPackMarker classification;
        private final SourceKind sourceKind;
        private final Object source;
        private final boolean borrowed;

        private Root(VPackByteStore store, VPackByteStore.Range range, long startOffset,
                long endOffset, int marker, VPackMarker classification, SourceKind sourceKind,
                Object source, boolean borrowed) {
            this.store = store;
            this.range = range;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.marker = marker;
            this.classification = classification;
            this.sourceKind = sourceKind;
            this.source = source;
            this.borrowed = borrowed;
        }

        VPackByteStore store() { return store; }
        VPackByteStore.Range range() { return range; }
        long startOffset() { return startOffset; }
        long endOffset() { return endOffset; }
        long length() { return range.length(); }
        int marker() { return marker; }
        VPackMarker classification() { return classification; }
        SourceKind sourceKind() { return sourceKind; }
        Object source() { return source; }
        boolean isBorrowed() { return borrowed; }
        byte byteAt(long offset) { return range.byteAt(offset); }
        byte[] toByteArray() { return range.toByteArray(); }

        @Override
        public void close() {
            if (!borrowed) {
                store.release();
            }
        }
    }

    private record Frame(int marker, VPackMarker classification, long totalLength) { }

}
