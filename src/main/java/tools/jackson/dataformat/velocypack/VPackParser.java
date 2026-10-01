package tools.jackson.dataformat.velocypack;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;

import tools.jackson.core.*;
import tools.jackson.core.base.ParserBase;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.SimpleStreamReadContext;

/**
 * Synchronous bounded-root VelocyPack parser with iterative container traversal.
 */
public class VPackParser extends ParserBase {
    private static final JacksonFeatureSet<StreamReadCapability> CAPABILITIES =
            DEFAULT_READ_CAPABILITIES.with(StreamReadCapability.EXACT_FLOATS);

    private final VPackRootReader _roots;
    private final VPackReadConstraints _vpackConstraints;
    private final VPackAttributeNameCodec _attributeNameCodec;
    private final ByteQuadsCanonicalizer _symbols;
    private final boolean _symbolsCanonical;
    private final int _formatReadFeatures;
    private final byte[] _compactScratch = new byte[VPackVarInts.MAX_GROUPS];
    private SimpleStreamReadContext _streamReadContext;
    private VPackRootReader.Root _root;
    private VPackType _currentVPackType;
    private long _currentAttributeId;
    private boolean _hasAttributeId;
    private Object _embeddedValue;
    private String _stringValue;
    private long _binaryPayloadOffset;
    private int _binaryLength;
    private Number _canonicalNumber;
    private long _doubleBits;
    private long _currentLocation = -1L;
    private long _nextLocation;
    private boolean _inputClosed;
    private boolean _releasedBuffered;
    private boolean _symbolsReleased;
    private final ArrayDeque<Frame> _arrayFrames = new ArrayDeque<>();
    private int[] _quadBuffer = new int[16];
    private VPackRootBudget _rootBudget;

    @SuppressWarnings({"unused", "ClassEscapesItsScope"}) // Factory-only constructor; callers should use VPackFactory.
    public VPackParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            int streamReadFeatures, int formatReadFeatures,
            VPackReadConstraints vpackConstraints,
            VPackAttributeNameCodec attributeNameCodec,
            ByteQuadsCanonicalizer symbols, VPackRootReader roots) {
        super(readCtxt, ioCtxt, streamReadFeatures);
        _roots = roots;
        _vpackConstraints = Objects.requireNonNull(vpackConstraints, "vpackConstraints");
        _attributeNameCodec = attributeNameCodec;
        _symbols = symbols;
        _symbolsCanonical = symbols != null && symbols.isCanonicalizing();
        _formatReadFeatures = formatReadFeatures;
        _streamReadContext = SimpleStreamReadContext.createRootContext(
                StreamReadFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamReadFeatures)
                        ? tools.jackson.core.json.DupDetector.rootDetector(this) : null);
        _tokenInputRow = -1;
        _tokenInputCol = -1;
    }

    @Override
    public Version version() { return PackageVersion.VERSION; }

    @Override
    public TokenStreamContext streamReadContext() { return _streamReadContext; }

    @Override
    public JacksonFeatureSet<StreamReadCapability> streamReadCapabilities() {
        return CAPABILITIES;
    }

    @Override
    public Object streamReadInputSource() { return _roots.source(); }

    @Override
    public TokenStreamLocation currentTokenLocation() {
        return new TokenStreamLocation(_contentReference(), _currentLocation,
                -1L, -1, -1);
    }

    @Override
    public TokenStreamLocation currentLocation() {
        return new TokenStreamLocation(_contentReference(), _nextLocation,
                -1L, -1, -1);
    }

    /** Test evidence for source reads/copies and live root retention. */
    long sourceBytesScanned() {
        return _root == null ? 0L : _root.range().scannedBytes();
    }

    long sourceBytesCopied() {
        return _root == null ? 0L : _root.range().copiedBytes();
    }

    int retainedRootEntries() {
        return _rootBudget == null ? 0 : _rootBudget.entries();
    }

    long retainedRootNameBytes() {
        return _rootBudget == null ? 0L : _rootBudget.nameBytes();
    }

    int retainedContainerDepth() { return _arrayFrames.size(); }

    @Override
    public Object currentValue() { return _streamReadContext.currentValue(); }

    @Override
    public void assignCurrentValue(Object value) { _streamReadContext.assignCurrentValue(value); }

    @Override
    public String nextName() throws JacksonException {
        return (nextToken() == JsonToken.PROPERTY_NAME) ? currentName() : null;
    }

    @Override
    public boolean nextName(SerializableString str) throws JacksonException {
        Objects.requireNonNull(str, "str");
        return (nextToken() == JsonToken.PROPERTY_NAME)
                && str.getValue().equals(currentName());
    }

    @Override
    public int nextNameMatch(tools.jackson.core.sym.PropertyNameMatcher matcher)
            throws JacksonException {
        Objects.requireNonNull(matcher, "matcher");
        String name = nextName();
        if (name != null) {
            return matcher.matchName(name);
        }
        if (_currToken == JsonToken.END_OBJECT) {
            return tools.jackson.core.sym.PropertyNameMatcher.MATCH_END_OBJECT;
        }
        return tools.jackson.core.sym.PropertyNameMatcher.MATCH_ODD_TOKEN;
    }

    @Override
    public int currentNameMatch(tools.jackson.core.sym.PropertyNameMatcher matcher) {
        Objects.requireNonNull(matcher, "matcher");
        if (_currToken == JsonToken.PROPERTY_NAME) {
            return matcher.matchName(currentName());
        }
        if (_currToken == JsonToken.END_OBJECT) {
            return tools.jackson.core.sym.PropertyNameMatcher.MATCH_END_OBJECT;
        }
        return tools.jackson.core.sym.PropertyNameMatcher.MATCH_ODD_TOKEN;
    }

    @Override
    public String nextStringValue() throws JacksonException {
        return (nextToken() == JsonToken.VALUE_STRING) ? getString() : null;
    }

    @Override
    public JsonToken nextToken() throws JacksonException {
        if (_closed || _releasedBuffered) {
            return null;
        }
        if (_root != null && _arrayFrames.isEmpty()) {
            _root.close();
            _root = null;
            if (_rootBudget != null) {
                _rootBudget.close();
                _rootBudget = null;
            }
        }
        _clearRetainedValues();
        _currentVPackType = null;
        _hasAttributeId = false;
        _embeddedValue = null;
        _stringValue = null;
        _binaryPayloadOffset = 0L;
        _binaryLength = 0;

        try {
            if (!_arrayFrames.isEmpty()) {
                return _arrayFrames.peek() instanceof ObjectFrame
                        ? _nextObjectToken() : _nextArrayToken();
            }
            VPackRootReader.Root root = _roots.nextRoot();
            if (root == null) {
                _currToken = null;
                int lastRootIndex = _streamReadContext.getEntryCount() - 1;
                close();
                // close() releases recyclable child contexts and application values.
                // Keep only the last completed root's path after normal EOF.
                _streamReadContext = new DetachedRootContext(lastRootIndex);
                return null;
            }
            _root = root;
            _rootBudget = new VPackRootBudget(_vpackConstraints);
            _rootBudget.chargeBytes(root.range().length());
            _currentLocation = root.startOffset();
            _nextLocation = root.endOffset();
            _tokenInputTotal = _currentLocation;
            _streamReadContext.valueRead();
            if (root.classification() == VPackMarker.EMPTY_ARRAY
                    || root.classification() == VPackMarker.EQUAL_ARRAY
                    || root.classification() == VPackMarker.INDEXED_ARRAY
                    || root.classification() == VPackMarker.COMPACT_ARRAY) {
                return _enterArray(null, root.startOffset(), root.endOffset());
            }
            if (root.classification() == VPackMarker.EMPTY_OBJECT
                    || root.classification() == VPackMarker.SORTED_OBJECT
                    || root.classification() == VPackMarker.UNSORTED_OBJECT
                    || root.classification() == VPackMarker.COMPACT_OBJECT) {
                return _enterObject(null, root.startOffset(), root.endOffset());
            }
            if (VPackMarker.isContainer(root.marker())) {
                throw VPackErrors.malformed("container parser", root.startOffset(),
                        "unhandled container marker");
            }
            return _decodeScalar(0L, root.startOffset(), root.marker(), root.classification());
        } catch (RuntimeException e) {
            _currToken = null;
            _roots.close();
            _releaseBuffers();
            throw e;
        }
    }

    private JsonToken _decodeScalar(long relativeStart, long logicalStart, int marker,
            VPackMarker kind) {
        switch (kind) {
        case NULL -> {
            _currentVPackType = VPackType.NULL;
            return _updateToken(JsonToken.VALUE_NULL);
        }
        case FALSE -> {
            _currentVPackType = VPackType.BOOLEAN;
            _embeddedValue = Boolean.FALSE;
            return _updateToken(JsonToken.VALUE_FALSE);
        }
        case TRUE -> {
            _currentVPackType = VPackType.BOOLEAN;
            _embeddedValue = Boolean.TRUE;
            return _updateToken(JsonToken.VALUE_TRUE);
        }
        case MIN_KEY -> {
            _currentVPackType = VPackType.MIN_KEY;
            _embeddedValue = VPackSpecialValue.MIN_KEY;
            return _updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
        }
        case MAX_KEY -> {
            _currentVPackType = VPackType.MAX_KEY;
            _embeddedValue = VPackSpecialValue.MAX_KEY;
            return _updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
        }
        case SHORT_STRING, LONG_STRING -> {
            _decodeString(relativeStart, logicalStart, marker, kind);
            _currentVPackType = VPackType.STRING;
            return _updateToken(JsonToken.VALUE_STRING);
        }
        case BINARY -> {
            _decodeBinary(relativeStart, logicalStart, marker);
            _currentVPackType = VPackType.BINARY;
            return _updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
        }
        case DOUBLE -> {
            _doubleBits = _readFixedPayload(relativeStart, 8);
            _numberDouble = Double.longBitsToDouble(_doubleBits);
            _canonicalNumber = _numberDouble;
            _numberIsNaN = !Double.isFinite(_numberDouble);
            _numTypesValid = NR_DOUBLE;
            _currentVPackType = VPackType.DOUBLE;
            return _updateToken(JsonToken.VALUE_NUMBER_FLOAT);
        }
        case UTC_DATE -> {
            _numberLong = VPackBounds.readSigned(_readFixedPayload(relativeStart, 8), 8);
            _canonicalNumber = _numberLong;
            _numTypesValid = NR_LONG;
            _currentVPackType = VPackType.DATE;
            return _fixedIntegerToken();
        }
        case SMALL_POSITIVE -> {
            _numberInt = marker - 0x30;
            _canonicalNumber = _numberInt;
            _numTypesValid = NR_INT;
            _currentVPackType = VPackType.SMALL_INTEGER;
            return _fixedIntegerToken();
        }
        case SMALL_NEGATIVE -> {
            _numberInt = marker - 0x40;
            _canonicalNumber = _numberInt;
            _numTypesValid = NR_INT;
            _currentVPackType = VPackType.SMALL_INTEGER;
            return _fixedIntegerToken();
        }
        case SIGNED_INTEGER -> {
            int width = VPackMarker.width(marker);
            long numericValue = VPackBounds.readSigned(_readFixedPayload(relativeStart, width), width);
            if (numericValue >= Integer.MIN_VALUE && numericValue <= Integer.MAX_VALUE) {
                _numberInt = (int) numericValue;
                _canonicalNumber = _numberInt;
                _numTypesValid = NR_INT;
            } else {
                _numberLong = numericValue;
                _canonicalNumber = _numberLong;
                _numTypesValid = NR_LONG;
            }
            _currentVPackType = VPackType.SIGNED_INTEGER;
            return _fixedIntegerToken();
        }
        case UNSIGNED_INTEGER -> {
            int width = VPackMarker.width(marker);
            long bits = _readFixedPayload(relativeStart, width);
            if (bits >= 0L && bits <= Integer.MAX_VALUE) {
                _numberInt = (int) bits;
                _canonicalNumber = _numberInt;
                _numTypesValid = NR_INT;
            } else if (bits >= 0L) {
                _numberLong = bits;
                _canonicalNumber = _numberLong;
                _numTypesValid = NR_LONG;
            } else {
                _numberBigInt = VPackBounds.unsignedLong(bits);
                _canonicalNumber = _numberBigInt;
                _numTypesValid = NR_BIGINT;
            }
            _currentVPackType = VPackType.UNSIGNED_INTEGER;
            return _fixedIntegerToken();
        }
        case POSITIVE_BCD, NEGATIVE_BCD -> {
            int width = VPackMarker.width(marker);
            long mantissaLength = _readLength(relativeStart, logicalStart, width,
                    "BCD mantissa length");
            long exponentOffset = VPackBounds.checkedAdd(1L + width, 0L,
                    "BCD exponent offset");
            int exponent = (int) VPackBounds.readSigned(_root.range().readLE(
                    relativeStart + exponentOffset, 4), 4);
            long mantissaOffset = VPackBounds.checkedAdd(exponentOffset, 4L,
                    "BCD mantissa offset");
            VPackNumbers.validateBcdInput(mantissaLength, exponent,
                    VPackBounds.checkedAdd(logicalStart, mantissaOffset,
                            "BCD value location"), _streamReadConstraints);
            int length = VPackBounds.checkedInt(mantissaLength, "BCD mantissa length");
            byte[] mantissa = new byte[length];
            _root.range().copyTo(relativeStart + mantissaOffset, mantissa, 0, length);
            Number numericValue = VPackNumbers.decodeBcd(mantissa, kind == VPackMarker.NEGATIVE_BCD,
                    exponent, VPackBounds.checkedAdd(logicalStart, mantissaOffset,
                            "BCD value location"), _streamReadConstraints);
            _canonicalNumber = numericValue;
            if (numericValue instanceof BigInteger integer) {
                _numberBigInt = integer;
                _numTypesValid = NR_BIGINT;
            } else {
                _numberBigDecimal = (BigDecimal) numericValue;
                _numTypesValid = NR_BIGDECIMAL;
            }
            _currentVPackType = VPackType.BCD;
            return _updateToken(exponent == 0
                    ? JsonToken.VALUE_NUMBER_INT : JsonToken.VALUE_NUMBER_FLOAT);
        }
        default -> throw VPackErrors.malformed("scalar parser", logicalStart,
                "marker 0x" + Integer.toHexString(marker)
                        + " is not a supported scalar marker");
        }
    }

    private JsonToken _fixedIntegerToken() {
        int digits;
        if (_canonicalNumber instanceof Integer value) {
            digits = _decimalDigits(value.longValue());
        } else if (_canonicalNumber instanceof Long value) {
            digits = _decimalDigits(value);
        } else {
            // Only uint64 values >= 2^63 reach this path; at most 20 digits.
            String text = _canonicalNumber.toString();
            digits = text.length() - (text.charAt(0) == '-' ? 1 : 0);
        }
        _streamReadConstraints.validateIntegerLength(digits);
        return _updateToken(JsonToken.VALUE_NUMBER_INT);
    }

    private static int _decimalDigits(long value) {
        int digits = 0;
        do {
            ++digits;
            value /= 10L;
        } while (value != 0L);
        return digits;
    }

    private JsonToken _enterArray(Frame parent, long start, long enclosingEnd) {
        return _enterArray(parent, start, enclosingEnd, null);
    }

    private JsonToken _enterArray(Frame parent, long start, long enclosingEnd,
            ContainerLayout analyzed) {
        ContainerLayout container = analyzed == null
                ? _analyzeContainer(start, enclosingEnd) : analyzed;
        VPackLayout.FixedLayout layout = container.fixed();
        CompactLayout compact = container.compact();
        VPackMarker markerKind = container.classification();
        if (markerKind != VPackMarker.EMPTY_ARRAY
                && markerKind != VPackMarker.COMPACT_ARRAY
                && (layout == null || (layout.classification() != VPackMarker.EQUAL_ARRAY
                        && layout.classification() != VPackMarker.INDEXED_ARRAY))) {
            throw VPackErrors.malformed("array parser", start,
                    "marker is not a regular array");
        }
        int depth = _arrayFrames.size() + 1;
        _streamReadConstraints.validateNestingDepth(depth);
        SimpleStreamReadContext context = _streamReadContext.createChildArrayContext(-1, -1);
        _streamReadContext = context;
        ArrayFrame frame = new ArrayFrame(parent, context, start,
                compact != null ? compact.end() : layout == null
                        ? _checkedEnd(start, 1L, "empty array") : layout.end(),
                compact != null ? compact.bodyStart() : layout == null
                        ? _checkedEnd(start, 1L, "empty array") : layout.bodyStart(),
                compact != null ? compact.bodyEnd() : layout == null
                        ? _checkedEnd(start, 1L, "empty array") : layout.bodyEnd(),
                compact != null ? 0 : layout == null ? 0 : layout.width(),
                compact == null && layout != null && layout.indexed(),
                compact != null ? compact.count() : layout == null ? 0L : layout.count(),
                compact != null ? compact.bodyEnd() : layout == null
                        ? _checkedEnd(start, 1L, "empty array") : layout.indexStart(),
                compact != null);
        frame.empty = layout == null;
        if ((frame.indexed || frame.compact)
                && frame.expectedCount > _vpackConstraints.getMaxRootEntries()) {
            throw VPackErrors.constraint("array entries", start,
                    "declared count exceeds configured VPack entry budget "
                            + _vpackConstraints.getMaxRootEntries());
        }
        // Do not size this from a wire-declared count.  Starts are observed as
        // body entries complete and grow only after the already-validated
        // entry budget permits another one.
        frame.observedStarts = null;
        _arrayFrames.push(frame);
        _currentLocation = start;
        _nextLocation = frame.bodyStart;
        _currentVPackType = VPackType.ARRAY;
        return _updateToken(JsonToken.START_ARRAY);
    }

    private JsonToken _enterObject(Frame parent, long start, long enclosingEnd) {
        return _enterObject(parent, start, enclosingEnd, null);
    }

    private JsonToken _enterObject(Frame parent, long start, long enclosingEnd,
            ContainerLayout analyzed) {
        ContainerLayout container = analyzed == null
                ? _analyzeContainer(start, enclosingEnd) : analyzed;
        VPackLayout.FixedLayout layout = container.fixed();
        CompactLayout compact = container.compact();
        VPackMarker markerKind = container.classification();
        if (markerKind != VPackMarker.EMPTY_OBJECT
                && markerKind != VPackMarker.COMPACT_OBJECT
                && (layout == null || (layout.classification() != VPackMarker.SORTED_OBJECT
                        && layout.classification() != VPackMarker.UNSORTED_OBJECT))) {
            throw VPackErrors.malformed("object parser", start,
                    "marker is not a regular object");
        }
        int depth = _arrayFrames.size() + 1;
        _streamReadConstraints.validateNestingDepth(depth);
        SimpleStreamReadContext context = _streamReadContext.createChildObjectContext(-1, -1);
        _streamReadContext = context;
        ObjectFrame frame = new ObjectFrame(parent, context, start,
                compact != null ? compact.end() : layout == null
                        ? _checkedEnd(start, 1L, "empty object") : layout.end(),
                compact != null ? compact.bodyStart() : layout == null
                        ? _checkedEnd(start, 1L, "empty object") : layout.bodyStart(),
                compact != null ? compact.bodyEnd() : layout == null
                        ? _checkedEnd(start, 1L, "empty object") : layout.bodyEnd(),
                compact != null ? 0 : layout == null ? 0 : layout.width(),
                compact == null && layout != null && layout.classification() == VPackMarker.SORTED_OBJECT,
                compact != null ? compact.count() : layout == null ? 0L : layout.count(),
                compact != null ? compact.bodyEnd() : layout == null
                        ? _checkedEnd(start, 1L, "empty object") : layout.indexStart(),
                compact != null);
        if (frame.expectedCount > _vpackConstraints.getMaxRootEntries()) {
            throw VPackErrors.constraint("object entries", start,
                    "declared count exceeds configured VPack entry budget "
                            + _vpackConstraints.getMaxRootEntries());
        }
        _arrayFrames.push(frame);
        _currentLocation = start;
        _nextLocation = frame.bodyStart;
        _currentVPackType = VPackType.OBJECT;
        return _updateToken(JsonToken.START_OBJECT);
    }

    @SuppressWarnings("DuplicatedCode") // Arrays and objects share traversal structure but validate different wire forms.
    private JsonToken _nextArrayToken() {
        ArrayFrame frame = (ArrayFrame) _arrayFrames.peek();
        if (frame == null) {
            throw VPackErrors.malformed("array traversal", _nextLocation,
                    "array traversal has no active frame");
        }
        if (frame.cursor == frame.bodyEnd) {
            _validateArrayEnd(frame);
            _arrayFrames.pop();
            _streamReadContext = frame.context.clearAndGetParent();
            _completeChild(frame);
            _currentLocation = frame.end;
            _nextLocation = frame.end;
            _currentVPackType = null;
            return _updateToken(JsonToken.END_ARRAY);
        }
        if (frame.cursor > frame.bodyEnd) {
            throw VPackErrors.malformed("array body", frame.cursor,
                    "array traversal passed the body boundary");
        }
        if ((frame.indexed || frame.compact) && frame.completed >= frame.expectedCount) {
            throw VPackErrors.malformed("array body", frame.cursor,
                    "array has leftover bytes after its declared count");
        }
        long start = frame.cursor;
        int marker = _byteAt(start);
        VPackMarker kind = VPackMarker.requireSupported(marker, "array child marker", start);
        ContainerLayout container = VPackMarker.isContainer(marker)
                ? _analyzeContainer(start, frame.bodyEnd) : null;
        long end = container == null ? _valueEnd(start, frame.bodyEnd, kind, marker)
                : container.end();
        long size = end - start;
        if (size <= 0L) {
            throw VPackErrors.malformed("array child", start, "child has no encoded bytes");
        }
        if (frame.equalSize == 0L) {
            frame.equalSize = size;
        } else if (!frame.indexed && !frame.compact && frame.equalSize != size) {
            throw VPackErrors.malformed("equal array", start,
                    "child encoded size differs from the first complete child");
        }
        _recordStart(frame, start);
        frame.cursor = end;
        frame.context.valueRead();
        if (kind == VPackMarker.EMPTY_ARRAY || kind == VPackMarker.EQUAL_ARRAY
                || kind == VPackMarker.INDEXED_ARRAY || kind == VPackMarker.COMPACT_ARRAY) {
            return _enterArray(frame, start, frame.bodyEnd, container);
        }
        if (kind == VPackMarker.EMPTY_OBJECT || kind == VPackMarker.SORTED_OBJECT
                || kind == VPackMarker.UNSORTED_OBJECT || kind == VPackMarker.COMPACT_OBJECT) {
            return _enterObject(frame, start, frame.bodyEnd, container);
        }
        if (VPackMarker.isContainer(marker)) {
            throw VPackErrors.malformed("array child", start,
                    "unhandled container marker");
        }
        _currentLocation = start;
        _nextLocation = end;
        JsonToken token = _decodeScalar(start - _root.startOffset(), start, marker, kind);
        frame.completed++;
        return token;
    }

    @SuppressWarnings("DuplicatedCode") // Arrays and objects share traversal structure but validate different wire forms.
    private JsonToken _nextObjectToken() {
        ObjectFrame frame = (ObjectFrame) _arrayFrames.peek();
        if (frame == null) {
            throw VPackErrors.malformed("object traversal", _nextLocation,
                    "object traversal has no active frame");
        }
        if (frame.cursor > frame.bodyEnd) {
            throw VPackErrors.malformed("object body", frame.cursor,
                    "object traversal passed the body boundary");
        }
        if (frame.compact && frame.completed >= frame.expectedCount
                && frame.cursor < frame.bodyEnd) {
            throw VPackErrors.malformed("object body", frame.cursor,
                    "object has leftover bytes after its declared pair count");
        }
        if (frame.expectingName) {
            if (frame.cursor == frame.bodyEnd) {
                _validateObjectEnd(frame);
                _arrayFrames.pop();
                _streamReadContext = frame.context.clearAndGetParent();
                _completeChild(frame);
                _currentLocation = frame.end;
                _nextLocation = frame.end;
                _currentVPackType = null;
                return _updateToken(JsonToken.END_OBJECT);
            }
            long start = frame.cursor;
            int marker = _byteAt(start);
            VPackMarker kind = VPackMarker.requireSupported(marker,
                    "object property name marker", start);
            if (kind != VPackMarker.SHORT_STRING && kind != VPackMarker.LONG_STRING
                    && kind != VPackMarker.UNSIGNED_INTEGER
                    && kind != VPackMarker.SMALL_POSITIVE) {
                throw VPackErrors.malformed("object property name", start,
                        kind == VPackMarker.SIGNED_INTEGER || kind == VPackMarker.SMALL_NEGATIVE
                                ? "signed integer keys are forbidden"
                                : "object keys must be strings or unsigned attribute IDs");
            }
            long end = _valueEnd(start, frame.bodyEnd, kind, marker);
            _rootBudget.chargeEntry();
            String name = _decodeName(frame, start, end, marker, kind);
            frame.cursor = end;
            frame.expectingName = false;
            frame.context.valueRead();
            frame.context.setCurrentName(name);
            _currentLocation = start;
            _nextLocation = end;
            _currentVPackType = kind != VPackMarker.UNSIGNED_INTEGER
                    && kind != VPackMarker.SMALL_POSITIVE
                    ? VPackType.STRING : (kind == VPackMarker.SMALL_POSITIVE
                            ? VPackType.SMALL_INTEGER : VPackType.UNSIGNED_INTEGER);
            return _updateToken(JsonToken.PROPERTY_NAME);
        }

        if (frame.cursor == frame.bodyEnd) {
            throw VPackErrors.malformed("object body", frame.cursor,
                    "object has a dangling property name without a value");
        }
        long start = frame.cursor;
        int marker = _byteAt(start);
        VPackMarker kind = VPackMarker.requireSupported(marker, "object value marker", start);
        ContainerLayout container = VPackMarker.isContainer(marker)
                ? _analyzeContainer(start, frame.bodyEnd) : null;
        long end = container == null ? _valueEnd(start, frame.bodyEnd, kind, marker)
                : container.end();
        frame.cursor = end;
        if (kind == VPackMarker.EMPTY_ARRAY || kind == VPackMarker.EQUAL_ARRAY
                || kind == VPackMarker.INDEXED_ARRAY || kind == VPackMarker.COMPACT_ARRAY) {
            return _enterArray(frame, start, frame.bodyEnd, container);
        }
        if (kind == VPackMarker.EMPTY_OBJECT || kind == VPackMarker.SORTED_OBJECT
                || kind == VPackMarker.UNSORTED_OBJECT || kind == VPackMarker.COMPACT_OBJECT) {
            return _enterObject(frame, start, frame.bodyEnd, container);
        }
        _currentLocation = start;
        _nextLocation = end;
        JsonToken token = _decodeScalar(start - _root.startOffset(), start, marker, kind);
        frame.completed++;
        frame.expectingName = true;
        return token;
    }

    private void _completeChild(Frame child) {
        if (child.parent == null) return;
        child.parent.completed++;
        child.parent.cursor = child.end;
        if (child.parent instanceof ObjectFrame object) {
            if (object.expectingName) {
                throw VPackErrors.malformed("object value", child.end,
                        "object child completed without a pending property name");
            }
            object.expectingName = true;
        }
    }

    private long _valueEnd(long start, long bodyEnd, VPackMarker kind, int marker) {
        long end;
        switch (kind) {
        case EMPTY_ARRAY -> end = _checkedEnd(start, 1L, "empty array child");
        case EQUAL_ARRAY, INDEXED_ARRAY, SORTED_OBJECT, UNSORTED_OBJECT,
                COMPACT_ARRAY, COMPACT_OBJECT -> end = _analyzeContainer(start, bodyEnd).end();
        case EMPTY_OBJECT -> end = _checkedEnd(start, 1L, "empty object child");
        case DOUBLE, UTC_DATE -> end = _checkedEnd(start, 9L, "fixed scalar child");
        case SIGNED_INTEGER, UNSIGNED_INTEGER -> end = _checkedEnd(start,
                1L + VPackMarker.width(marker), "integer child");
        case SMALL_POSITIVE, SMALL_NEGATIVE, NULL, FALSE, TRUE, MIN_KEY, MAX_KEY ->
                end = _checkedEnd(start, 1L, "scalar child");
        case SHORT_STRING -> end = _checkedEnd(start, 1L + marker - 0x40L,
                "short string child");
        case LONG_STRING -> end = _checkedEnd(start, 9L
                + _readLengthAt(start, 8), "long string child");
        case BINARY -> {
            int width = VPackMarker.width(marker);
            end = _checkedEnd(start, 1L + width
                    + _readLengthAt(start, width), "binary child");
        }
        case POSITIVE_BCD, NEGATIVE_BCD -> {
            int width = VPackMarker.width(marker);
            end = _checkedEnd(start, 1L + width + 4L
                    + _readLengthAt(start, width), "BCD child");
        }
        default -> throw VPackErrors.malformed("array child", start,
                "marker is not a scalar array value");
        }
        if (end <= start || end > bodyEnd || end > _root.endOffset()) {
            throw VPackErrors.malformed("array child", start,
                    "child exceeds its enclosing body boundary");
        }
        return end;
    }

    private ContainerLayout _analyzeContainer(long start, long enclosingEnd) {
        VPackMarker markerKind = VPackMarker.requireSupported(_byteAt(start),
                "container parser", start);
        if (markerKind == VPackMarker.EMPTY_ARRAY || markerKind == VPackMarker.EMPTY_OBJECT) {
            long end = _checkedEnd(start, 1L, "empty container");
            if (end > enclosingEnd) {
                throw VPackErrors.malformed("container parser", start,
                        "empty container exceeds its enclosing body boundary");
            }
            return new ContainerLayout(markerKind, null, null, end);
        }
        if (markerKind == VPackMarker.COMPACT_ARRAY || markerKind == VPackMarker.COMPACT_OBJECT) {
            CompactLayout compact = _analyzeCompact(start, enclosingEnd);
            return new ContainerLayout(markerKind, null, compact, compact.end());
        }
        if (markerKind == VPackMarker.EQUAL_ARRAY || markerKind == VPackMarker.INDEXED_ARRAY
                || markerKind == VPackMarker.SORTED_OBJECT
                || markerKind == VPackMarker.UNSORTED_OBJECT) {
            long relative = start - _root.startOffset();
            VPackLayout.FixedLayout fixed = VPackLayout.analyzeFixed(_root.range(), relative,
                    _root.range().length(), start, enclosingEnd);
            return new ContainerLayout(markerKind, fixed, null, fixed.end());
        }
        throw VPackErrors.malformed("container parser", start,
                "marker is not a supported container");
    }

    /**
     * Resolve both framing varints of a compact container.  The reverse
     * count is deliberately read from the already-framed root range; nested
     * containers never acquire or spool input of their own.
     */
    private CompactLayout _analyzeCompact(long start, long enclosingEnd) {
        long available = enclosingEnd - start;
        if (available < 2L) {
            throw VPackErrors.malformed("compact container", start,
                    "missing length or count framing");
        }
        int forwardBytes = (int) Math.min(VPackVarInts.MAX_GROUPS, available - 1L);
        _root.range().copyTo(start - _root.startOffset() + 1L,
                _compactScratch, 0, forwardBytes);
        VPackVarInts.Decoded lengthValue = VPackVarInts.readForward(
                _compactScratch, 0, forwardBytes);
        long length = lengthValue.value();
        long headerLength = 1L + lengthValue.length();
        if (length < headerLength) {
            throw VPackErrors.malformed("compact length", start + 1L,
                    "declared length is smaller than its header");
        }
        long end = _checkedEnd(start, length, "compact container length");
        if (end > enclosingEnd || end > _root.endOffset()) {
            throw VPackErrors.malformed("compact container", start,
                    "declared length exceeds its enclosing body");
        }

        long suffixLength = Math.min(VPackVarInts.MAX_GROUPS, end - (start + headerLength));
        if (suffixLength <= 0L) {
            throw VPackErrors.malformed("compact count", end,
                    "missing reverse count");
        }
        long suffixBase = end - suffixLength;
        int suffixBytes = (int) suffixLength;
        _root.range().copyTo(suffixBase - _root.startOffset(),
                _compactScratch, 0, suffixBytes);
        VPackVarInts.Decoded countValue = VPackVarInts.readReverse(
                _compactScratch, 0, suffixBytes);
        long bodyStart = start + headerLength;
        long bodyEnd = suffixBase + countValue.start();
        if (bodyEnd < bodyStart) {
            throw VPackErrors.malformed("compact count", bodyEnd,
                    "reverse count overlaps the length header");
        }
        long bodyLength = bodyEnd - bodyStart;
        VPackMarker kind = VPackMarker.classify(_byteAt(start));
        long minimum = kind == VPackMarker.COMPACT_OBJECT ? 2L : 1L;
        if (countValue.value() == 0L) {
            if (bodyLength != 0L) {
                throw VPackErrors.malformed("compact container", bodyStart,
                        "zero-count container has leftover body bytes");
            }
        } else if (countValue.value() > bodyLength / minimum) {
            throw VPackErrors.malformed("compact container", bodyStart,
                    "body cannot contain the declared entry count");
        }
        return new CompactLayout(end, bodyStart, bodyEnd, countValue.value());
    }

    private long _checkedEnd(long start, long length, String context) {
        return VPackBounds.checkedAdd(start, length, context);
    }

    private long _readLengthAt(long start, int width) {
        long relative = start - _root.startOffset() + 1L;
        return VPackBounds.readScalarLength(_root.range().readLE(relative, width),
                width, start + 1L);
    }

    private void _recordStart(ArrayFrame frame, long start) {
        _rootBudget.chargeEntry();
        // Equal-size and compact arrays validate only their body/count.  They
        // have no index to compare against, so retaining every observed start
        // would turn a bounded wire root into an avoidable large allocation.
        if (!frame.indexed) {
            return;
        }
        if (frame.observedStarts == null || frame.completed >= frame.observedStarts.length) {
            if (frame.completed >= _vpackConstraints.getMaxRootEntries()) {
                throw VPackErrors.constraint("array entries", start,
                        "array exceeds configured VPack entry budget "
                                + _vpackConstraints.getMaxRootEntries());
            }
            int oldLength = frame.observedStarts == null ? 0 : frame.observedStarts.length;
            // Preserve the old upper-bound behavior if the configured maximum is below the floor.
            //noinspection MathClampMigration
            int newLength = Math.min(_vpackConstraints.getMaxRootEntries(),
                    Math.max(frame.completed + 1, Math.max(16, oldLength * 2)));
            frame.observedStarts = frame.observedStarts == null
                    ? new long[newLength]
                    : java.util.Arrays.copyOf(frame.observedStarts, newLength);
        }
        frame.observedStarts[frame.completed] = start - frame.start;
    }

    private void _validateArrayEnd(ArrayFrame frame) {
        if ((frame.indexed || frame.compact) && frame.completed != frame.expectedCount) {
            throw VPackErrors.malformed("array count", frame.end,
                    "array ended before its declared count was completed");
        }
        if (!frame.indexed && !frame.empty && frame.completed == 0) {
            throw VPackErrors.malformed("equal array", frame.end,
                    "regular equal array has no complete child");
        }
        if (frame.indexed) {
            long previous = -1L;
            for (int i = 0; i < frame.completed; ++i) {
                long index = _readStructural(frame.indexStart + (long) i * frame.width,
                        frame.width);
                if (index != frame.observedStarts[i]) {
                    throw VPackErrors.malformed("array index", frame.indexStart,
                            "index entry does not equal the observed child start");
                }
                if (index <= previous) {
                    throw VPackErrors.malformed("array index", frame.indexStart,
                            "array index entries are not strictly increasing");
                }
                previous = index;
            }
        }
    }

    private void _validateObjectEnd(ObjectFrame frame) {
        if (frame.completed != frame.expectedCount) {
            throw VPackErrors.malformed("object count", frame.end,
                    "object ended before its declared pair count was completed");
        }
        if (!frame.indexed) {
            return;
        }
        if (frame.completed > 0
                && (frame.keyStarts == null || frame.keyStarts.length < frame.completed)) {
            throw VPackErrors.malformed("object keys", frame.end,
                    "object did not observe every declared property name");
        }
        for (int i = 1; i < frame.completed; ++i) {
            if (frame.keyStarts[i] <= frame.keyStarts[i - 1]) {
                throw VPackErrors.malformed("object index", frame.indexStart,
                        "object index domain contains a duplicate key start");
            }
        }
        boolean[] seen = null;
        int previousKey = -1;
        for (int i = 0; i < frame.completed; ++i) {
            long index = _readStructural(frame.indexStart + (long) i * frame.width,
                    frame.width);
            int key;
            if (seen == null && index == frame.keyStarts[i]) {
                key = i;
            } else {
                if (seen == null) {
                    seen = new boolean[frame.completed];
                    Arrays.fill(seen, 0, i, true);
                }
                key = index < 0L || index > Integer.MAX_VALUE ? -1
                        : Arrays.binarySearch(frame.keyStarts, 0, frame.completed, (int) index);
                if (key < 0) {
                    throw VPackErrors.malformed("object index", frame.indexStart,
                            "index entry does not point to an observed key start");
                }
                if (seen[key]) {
                    throw VPackErrors.malformed("object index", frame.indexStart,
                            "object index entries are not a bijection onto key starts");
                }
                seen[key] = true;
            }
            if (frame.sorted) {
                if (previousKey >= 0 && _compareObjectNames(frame, previousKey, key) > 0) {
                    throw VPackErrors.malformed("object index", frame.indexStart,
                            "sorted object index is not in unsigned UTF-8 name order");
                }
                previousKey = key;
            }
        }
        if (seen != null) {
            for (boolean keySeen : seen) {
                if (!keySeen) {
                    throw VPackErrors.malformed("object index", frame.indexStart,
                            "object index omits an observed key start");
                }
            }
        }
    }

    private int _compareObjectNames(ObjectFrame frame, int left, int right) {
        byte[] leftId = frame.attributeNameBytes == null ? null : frame.attributeNameBytes[left];
        byte[] rightId = frame.attributeNameBytes == null ? null : frame.attributeNameBytes[right];
        if (leftId == null && rightId == null) {
            // Indexed frames are validated before their root is popped, so these source offsets
            // still refer to the live root range.
            return _root.range().compareUnsigned(frame.nameOffset(left), frame.nameLength(left),
                    frame.nameOffset(right), frame.nameLength(right));
        }
        if (leftId == null) {
            return _root.range().compareUnsigned(frame.nameOffset(left), frame.nameLength(left), rightId);
        }
        if (rightId == null) {
            return -_root.range().compareUnsigned(frame.nameOffset(right), frame.nameLength(right), leftId);
        }
        return Arrays.compareUnsigned(leftId, rightId);
    }

    private String _decodeName(ObjectFrame frame, long start, long end, int marker,
            VPackMarker kind) {
        if (kind == VPackMarker.UNSIGNED_INTEGER || kind == VPackMarker.SMALL_POSITIVE) {
            long id = kind == VPackMarker.SMALL_POSITIVE
                    ? marker - 0x30L : _readUnsignedKeyIdBits(start, marker);
            if (_attributeNameCodec == null) {
                throw VPackErrors.malformed("compressed attribute name", start,
                        "no attribute-name codec is configured for ID "
                                + Long.toUnsignedString(id));
            }
            final String text;
            try {
                text = _attributeNameCodec.decode(id);
            } catch (RuntimeException e) {
                throw VPackErrors.input("compressed attribute name", start, e);
            }
            if (text == null) {
                throw VPackErrors.malformed("compressed attribute name", start,
                        "codec could not resolve ID " + Long.toUnsignedString(id));
            }
            byte[] utf8 = _encodeResolvedName(text);
            _rootBudget.chargeName(utf8.length);
            _currentAttributeId = id;
            _hasAttributeId = true;
            if (frame.indexed) {
                frame.recordName(start - frame.start, -1L, utf8.length, utf8);
            }
            return text;
        }
        long payloadOffset = kind == VPackMarker.SHORT_STRING ? 1L : 9L;
        long byteLength = end - start - payloadOffset;
        _rootBudget.chargeName(byteLength);
        int length = VPackBounds.checkedInt(byteLength, "object key byte length");
        long relative = start - _root.startOffset() + payloadOffset;
        String decoded;
        int canonicalLength = 0;
        boolean addToSymbols = false;
        if (_symbolsCanonical && length > 0 && length <= 64) {
            byte[] bytes = _root.range().contiguousArray(relative, length);
            if (bytes != null) {
                int offset = _root.range().contiguousOffset(relative);
                int quadLength = (length + 3) >> 2;
                canonicalLength = quadLength + 1;
                if (_quadBuffer.length < canonicalLength) {
                    _quadBuffer = Arrays.copyOf(_quadBuffer,
                            Math.max(canonicalLength, _quadBuffer.length << 1));
                }
                _packQuads(bytes, offset, length, _quadBuffer);
                // Retain the byte length as a final quad: malformed UTF-8 can contain the
                // same 0xFF values used by Smile-style final-quad padding.
                _quadBuffer[quadLength] = length;
                String name = _findName(_quadBuffer, canonicalLength);
                if (name == null) {
                    _root.range().recordDecodedBytes(length);
                    name = new String(bytes, offset, length, StandardCharsets.UTF_8);
                    addToSymbols = true;
                }
                decoded = name;
            } else {
                decoded = _root.range().decodeUtf8(relative, length);
            }
        } else {
            decoded = _root.range().decodeUtf8(relative, length);
        }
        if (length > _streamReadConstraints.getMaxNameLength()) {
            _streamReadConstraints.validateNameLength(decoded.length());
        }
        if (addToSymbols) {
            decoded = _addName(decoded, _quadBuffer, canonicalLength);
        }
        if (frame.indexed) {
            frame.recordName(start - frame.start, relative, length, null);
        }
        return decoded;
    }

    private void _packQuads(byte[] bytes, int offset, int length, int[] quads) {
        int fullQuads = length >> 2;
        int byteOffset = 0;
        for (int q = 0; q < fullQuads; ++q) {
            int index = offset + byteOffset;
            quads[q] = ((bytes[index] & 0xFF) << 24)
                    | ((bytes[index + 1] & 0xFF) << 16)
                    | ((bytes[index + 2] & 0xFF) << 8)
                    | (bytes[index + 3] & 0xFF);
            byteOffset += 4;
        }

        switch (length - byteOffset) {
            case 1 -> quads[fullQuads] = (bytes[offset + byteOffset] & 0xFF) | 0xFFFFFF00;
            case 2 -> quads[fullQuads] = ((bytes[offset + byteOffset] & 0xFF) << 8)
                    | (bytes[offset + byteOffset + 1] & 0xFF) | 0xFFFF0000;
            case 3 -> quads[fullQuads] = ((bytes[offset + byteOffset] & 0xFF) << 16)
                    | ((bytes[offset + byteOffset + 1] & 0xFF) << 8)
                    | (bytes[offset + byteOffset + 2] & 0xFF) | 0xFF000000;
            default -> { }
        }
    }

    private String _findName(int[] quads, int qlen) {
        return switch (qlen) {
            case 1 -> _symbols.findName(quads[0]);
            case 2 -> _symbols.findName(quads[0], quads[1]);
            case 3 -> _symbols.findName(quads[0], quads[1], quads[2]);
            default -> _symbols.findName(quads, qlen);
        };
    }

    private String _addName(String name, int[] quads, int qlen) {
        return switch (qlen) {
            case 1 -> _symbols.addName(name, quads[0]);
            case 2 -> _symbols.addName(name, quads[0], quads[1]);
            case 3 -> _symbols.addName(name, quads[0], quads[1], quads[2]);
            default -> _symbols.addName(name, quads, qlen);
        };
    }

    private long _readUnsignedKeyIdBits(long start, int marker) {
        int width = VPackMarker.width(marker);
        long relative = start - _root.startOffset() + 1L;
        VPackBounds.checkedRange(relative, width, _root.range().length(),
                "compressed attribute ID");
        return _root.range().readLE(relative, width);
    }

    private byte[] _encodeResolvedName(String text) {
        _streamReadConstraints.validateNameLength(text.length());
        byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
        _rootBudget.checkName(utf8.length);
        return utf8;
    }

    private long _readStructural(long logicalOffset, int width) {
        return VPackBounds.readStructural(_root.range().readLE(
                logicalOffset - _root.startOffset(), width), width, logicalOffset);
    }

    private int _byteAt(long logicalOffset) {
        return _root.range().byteAt(logicalOffset - _root.startOffset()) & 0xFF;
    }

    private record CompactLayout(long end, long bodyStart, long bodyEnd, long count) { }

    /** Fully checked framing retained from child-end calculation through entry. */
    private record ContainerLayout(VPackMarker classification,
            VPackLayout.FixedLayout fixed, CompactLayout compact, long end) { }

    private abstract static class Frame {
        final Frame parent;
        final SimpleStreamReadContext context;
        final long start;
        final long end;
        final long bodyStart;
        final long bodyEnd;
        final int width;
        final boolean indexed;
        final boolean compact;
        final long expectedCount;
        final long indexStart;
        long cursor;
        int completed;

        Frame(Frame parent, SimpleStreamReadContext context, long start, long end,
                long bodyStart, long bodyEnd, int width, boolean indexed, long expectedCount,
                long indexStart, boolean compact) {
            this.parent = parent;
            this.context = context;
            this.start = start;
            this.end = end;
            this.bodyStart = bodyStart;
            this.bodyEnd = bodyEnd;
            this.width = width;
            this.indexed = indexed;
            this.compact = compact;
            this.expectedCount = expectedCount;
            this.indexStart = indexStart;
            cursor = bodyStart;
        }
    }

    private static final class ArrayFrame extends Frame {
        long equalSize;
        long[] observedStarts;
        boolean empty;

        ArrayFrame(Frame parent, SimpleStreamReadContext context, long start, long end,
                long bodyStart, long bodyEnd, int width, boolean indexed, long expectedCount,
                long indexStart, boolean compact) {
            super(parent, context, start, end, bodyStart, bodyEnd, width, indexed, expectedCount,
                    indexStart, compact);
        }
    }

    private static final class ObjectFrame extends Frame {
        final boolean sorted;
        boolean expectingName = true;
        int[] keyStarts;
        long[] nameRanges;
        byte[][] attributeNameBytes;

        ObjectFrame(Frame parent, SimpleStreamReadContext context, long start, long end,
                long bodyStart, long bodyEnd, int width, boolean sorted, long expectedCount,
                long indexStart, boolean compact) {
            super(parent, context, start, end, bodyStart, bodyEnd, width, !compact,
                    expectedCount, indexStart, compact);
            this.sorted = sorted;
        }

        void recordName(long keyStart, long payloadOffset, int byteLength, byte[] attributeUtf8) {
            if (keyStarts == null || completed >= keyStarts.length) {
                int oldLength = keyStarts == null ? 0 : keyStarts.length;
                // Keep the historical cap even when a very small maximum is below the initial size.
                //noinspection MathClampMigration
                int newLength = Math.min(Integer.MAX_VALUE - 8,
                        Math.max(completed + 1, Math.max(16, oldLength * 2)));
                keyStarts = keyStarts == null ? new int[newLength]
                        : java.util.Arrays.copyOf(keyStarts, newLength);
                if (sorted) {
                    nameRanges = nameRanges == null ? new long[newLength]
                            : java.util.Arrays.copyOf(nameRanges, newLength);
                    if (attributeNameBytes != null) {
                        attributeNameBytes = java.util.Arrays.copyOf(attributeNameBytes, newLength);
                    }
                }
            }
            // Per-root offsets are bounded by the store's int-sized capacity.
            keyStarts[completed] = (int) keyStart;
            if (sorted) {
                // Root storage is capped below 2 GiB; store the 32-bit relative offset
                // and byte length in one primitive slot to avoid per-key arrays.
                nameRanges[completed] = ((long) byteLength << 32)
                        | (payloadOffset & 0xFFFF_FFFFL);
                if (attributeUtf8 != null) {
                    if (attributeNameBytes == null) {
                        attributeNameBytes = new byte[keyStarts.length][];
                    }
                    attributeNameBytes[completed] = attributeUtf8;
                }
            }
        }

        long nameOffset(int index) {
            return nameRanges[index] & 0xFFFF_FFFFL;
        }

        int nameLength(int index) {
            return (int) (nameRanges[index] >>> 32);
        }
    }

    private long _readFixedPayload(long relativeStart, int length) {
        return _root.range().readLE(relativeStart + 1L, length);
    }

    private void _decodeString(long relativeStart, long logicalStart, int marker,
            VPackMarker kind) {
        long payloadOffset;
        long byteLength;
        if (kind == VPackMarker.SHORT_STRING) {
            payloadOffset = 1L;
            byteLength = marker - 0x40L;
        } else {
            payloadOffset = 9L;
            byteLength = _readLength(relativeStart, logicalStart, 8, "long string length");
        }
        int length = VPackBounds.checkedInt(byteLength, "string byte length");
        _stringValue = _root.range().decodeUtf8(relativeStart + payloadOffset, length);
        _streamReadConstraints.validateStringLengthLong(_stringValue.length());
    }

    private void _decodeBinary(long relativeStart, long logicalStart, int marker) {
        int width = VPackMarker.width(marker);
        long payloadOffset = 1L + width;
        long length = _readLength(relativeStart, logicalStart, width, "binary length");
        _binaryPayloadOffset = relativeStart + payloadOffset;
        _binaryLength = VPackBounds.checkedInt(length, "binary payload length");
        VPackBounds.checkedRange(relativeStart + payloadOffset, length,
                _root.range().length(), "binary payload");
    }

    private long _readLength(long relativeStart, long logicalStart, int width, String context) {
        return VPackBounds.readScalarLength(_root.range().readLE(
                relativeStart + 1L, width), width,
                VPackBounds.checkedAdd(logicalStart, 1L, context));
    }

    public VPackType currentVPackType() { return _currToken == null ? null : _currentVPackType; }

    int formatReadFeatures() { return _formatReadFeatures; }

    VPackReadConstraints vpackReadConstraints() { return _vpackConstraints; }

    long currentDoubleBits() {
        return _currToken == JsonToken.VALUE_NUMBER_FLOAT && _currentVPackType == VPackType.DOUBLE
                ? _doubleBits : 0L;
    }

    public BigInteger currentAttributeId() {
        return _hasAttributeId ? VPackBounds.unsignedLong(_currentAttributeId) : null;
    }

    /**
     * Raw uint64 bit pattern for the current attribute ID; values >= 2^63 appear
     * negative. Check {@link #hasCurrentAttributeId()} before using it.
     */
    public long currentAttributeIdBits() { return _currentAttributeId; }

    /** Whether the current token is a property name encoded as an attribute ID. */
    public boolean hasCurrentAttributeId() { return _currToken != null && _hasAttributeId; }

    @Override
    public String currentName() {
        if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
            SimpleStreamReadContext parent = _streamReadContext.getParent();
            return parent == null ? null : parent.currentName();
        }
        return _streamReadContext.currentName();
    }

    @Override
    public String getString() throws JacksonException {
        if (_currToken == null) return null;
        if (_currToken == JsonToken.PROPERTY_NAME) return currentName();
        if (_currToken == JsonToken.VALUE_STRING) return _stringValue;
        if (_currToken == JsonToken.VALUE_NUMBER_INT || _currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            return getNumberValue().toString();
        }
        if (_currToken == JsonToken.VALUE_EMBEDDED_OBJECT) return null;
        if (_currToken == JsonToken.VALUE_NULL) return "null";
        return _currToken.asString();
    }

    @Override
    public int getString(Writer writer) throws JacksonException {
        String value = getString();
        if (value == null) return 0;
        try { writer.write(value); } catch (IOException e) { throw _wrapIOFailure(e); }
        return value.length();
    }

    @Override
    public long readString(Writer writer) throws JacksonException {
        if (_currToken == JsonToken.VALUE_STRING) {
            String value = _stringValue;
            if (value == null) {
                value = "";
            }
            try {
                writer.write(value);
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            }
            _stringValue = "";
            return value.length();
        }
        return getString(writer);
    }

    @Override public char[] getStringCharacters() throws JacksonException {
        if (_currToken == JsonToken.VALUE_STRING) return _stringValue.toCharArray();
        String value = getString(); return value == null ? null : value.toCharArray();
    }
    @Override public int getStringLength() throws JacksonException {
        if (_currToken == JsonToken.VALUE_STRING) return _stringValue.length();
        String value = getString(); return value == null ? 0 : value.length();
    }
    @Override public int getStringOffset() { return 0; }

    @Override public boolean hasStringCharacters() {
        return _currToken == JsonToken.VALUE_STRING && _stringValue != null;
    }

    @Override public Number getNumberValue() throws InputCoercionException {
        if (_currToken == JsonToken.VALUE_NUMBER_INT || _currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            return _canonicalNumber;
        }
        throw _constructNotNumericType(_currToken, NR_UNKNOWN);
    }

    @Override public Number getNumberValueExact() throws InputCoercionException { return getNumberValue(); }
    @Override public Object getNumberValueDeferred() throws InputCoercionException { return getNumberValue(); }

    @Override public NumberType getNumberType() {
        if (_currToken == JsonToken.VALUE_NUMBER_INT) {
            if (_canonicalNumber instanceof Integer) return NumberType.INT;
            if (_canonicalNumber instanceof Long) return NumberType.LONG;
            if (_canonicalNumber instanceof BigInteger) return NumberType.BIG_INTEGER;
            _throwInternal();
        }
        if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            return _canonicalNumber instanceof BigDecimal
                    ? NumberType.BIG_DECIMAL : NumberType.DOUBLE;
        }
        return null;
    }

    @Override public NumberTypeFP getNumberTypeFP() {
        if (_currToken != JsonToken.VALUE_NUMBER_FLOAT) return NumberTypeFP.UNKNOWN;
        return _canonicalNumber instanceof BigDecimal
                ? NumberTypeFP.BIG_DECIMAL : NumberTypeFP.DOUBLE64;
    }

    @Override public int getIntValue() throws JacksonException {
        Number value = _requireCanonicalNumber();
        if (value instanceof Integer integer) return integer;
        if (value instanceof Long longValue) {
            if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
                _reportOverflowInt();
            }
            return longValue.intValue();
        }
        if (value instanceof BigInteger integer) {
            if (integer.compareTo(BI_MIN_INT) < 0 || integer.compareTo(BI_MAX_INT) > 0) {
                _reportOverflowInt();
            }
            return integer.intValue();
        }
        if (value instanceof BigDecimal decimal) {
            if (decimal.compareTo(BD_MIN_INT) < 0 || decimal.compareTo(BD_MAX_INT) > 0) {
                _reportOverflowInt();
            }
            return decimal.intValue();
        }
        double doubleValue = value.doubleValue();
        if (!Double.isFinite(doubleValue)
                || BigDecimal.valueOf(doubleValue).compareTo(BD_MIN_INT) < 0
                || BigDecimal.valueOf(doubleValue).compareTo(BD_MAX_INT) > 0) {
            _reportOverflowInt();
        }
        return (int) doubleValue;
    }

    @Override public long getLongValue() throws JacksonException {
        Number value = _requireCanonicalNumber();
        if (value instanceof Integer integer) return integer.longValue();
        if (value instanceof Long longValue) return longValue;
        if (value instanceof BigInteger integer) {
            if (integer.compareTo(BI_MIN_LONG) < 0 || integer.compareTo(BI_MAX_LONG) > 0) {
                _reportOverflowLong();
            }
            return integer.longValue();
        }
        if (value instanceof BigDecimal decimal) {
            if (decimal.compareTo(BD_MIN_LONG) < 0 || decimal.compareTo(BD_MAX_LONG) > 0) {
                _reportOverflowLong();
            }
            return decimal.longValue();
        }
        double doubleValue = value.doubleValue();
        if (!Double.isFinite(doubleValue)
                || doubleValue < -0x1.0p63 || doubleValue >= 0x1.0p63) {
            _reportOverflowLong();
        }
        return (long) doubleValue;
    }

    @Override public BigInteger getBigIntegerValue() throws JacksonException {
        Number value = _requireCanonicalNumber();
        if (value instanceof BigInteger integer) return integer;
        if (value instanceof Integer integer) return BigInteger.valueOf(integer.longValue());
        if (value instanceof Long longValue) return BigInteger.valueOf(longValue);
        if (value instanceof BigDecimal decimal) {
            VPackNumbers.validateBigIntegerScale(decimal, _currentLocation, _streamReadConstraints);
            return decimal.toBigInteger();
        }
        double doubleValue = value.doubleValue();
        if (!Double.isFinite(doubleValue)) {
            throw _nonFiniteCoercion(BigInteger.class);
        }
        return BigDecimal.valueOf(doubleValue).toBigInteger();
    }

    @Override public float getFloatValue() throws JacksonException {
        Number value = _requireCanonicalNumber();
        if (value instanceof Float floatValue) return floatValue;
        if (value instanceof Double doubleValue) {
            float result = doubleValue.floatValue();
            if (Double.isFinite(doubleValue) && !Float.isFinite(result)) {
                throw _rangeCoercion(Float.TYPE, "float");
            }
            return result;
        }
        float result = value.floatValue();
        if (!Float.isFinite(result) && _isNonZero(value)) {
            throw _rangeCoercion(Float.TYPE, "float");
        }
        return result;
    }

    @Override public double getDoubleValue() throws JacksonException {
        Number value = _requireCanonicalNumber();
        if (value instanceof Double doubleValue) return doubleValue;
        double result = value.doubleValue();
        // Exact integer/decimal values may legitimately overflow the target
        // double, matching JsonParser's widening conversion semantics. Their
        // canonical value remains exact; only a native non-finite double is
        // already non-finite at the source and is returned above unchanged.
        if (Double.isNaN(result)) {
            throw _rangeCoercion(Double.TYPE, "double");
        }
        return result;
    }

    @Override public BigDecimal getDecimalValue() throws JacksonException {
        Number value = _requireCanonicalNumber();
        if (value instanceof BigDecimal decimal) return decimal;
        if (value instanceof BigInteger integer) return new BigDecimal(integer);
        if (value instanceof Integer integer) return BigDecimal.valueOf(integer.longValue());
        if (value instanceof Long longValue) return BigDecimal.valueOf(longValue);
        double doubleValue = value.doubleValue();
        if (!Double.isFinite(doubleValue)) {
            throw _nonFiniteCoercion(BigDecimal.class);
        }
        return BigDecimal.valueOf(doubleValue);
    }

    private Number _requireCanonicalNumber() throws InputCoercionException {
        if (_currToken == JsonToken.VALUE_NUMBER_INT || _currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            return _canonicalNumber;
        }
        throw _constructNotNumericType(_currToken, NR_UNKNOWN);
    }

    private InputCoercionException _nonFiniteCoercion(Class<?> target) {
        return _constructInputCoercion("Non-finite numeric value cannot be coerced to "
                + target.getSimpleName(), currentToken(), target);
    }

    private InputCoercionException _rangeCoercion(Class<?> target, String name) {
        return _constructInputCoercion("Numeric value out of range of `" + name + "`",
                currentToken(), target);
    }

    private boolean _isNonZero(Number value) {
        if (value instanceof BigInteger integer) return integer.signum() != 0;
        if (value instanceof BigDecimal decimal) return decimal.signum() != 0;
        return value.doubleValue() != 0.0d;
    }

    @Override protected void _parseNumericValue(int expType) throws JacksonException {
        if (_currToken != JsonToken.VALUE_NUMBER_INT && _currToken != JsonToken.VALUE_NUMBER_FLOAT) {
            throw _constructNotNumericType(_currToken, expType);
        }
    }

    @Override protected int _parseIntValue() throws JacksonException { return getIntValue(); }

    @Override public Object getEmbeddedObject() throws JacksonException {
        if (_currToken != JsonToken.VALUE_EMBEDDED_OBJECT) return null;
        if (_currentVPackType == VPackType.BINARY) return getBinaryValue(null);
        return _embeddedValue;
    }

    @Override public byte[] getBinaryValue(Base64Variant variant) throws JacksonException {
        if (_currToken == JsonToken.VALUE_EMBEDDED_OBJECT
                && _currentVPackType == VPackType.BINARY) {
            if (_binaryValue == null) {
                _binaryValue = new byte[_binaryLength];
                _root.range().copyTo(_binaryPayloadOffset, _binaryValue, 0, _binaryLength);
            }
            return _binaryValue;
        }
        if (_currToken == JsonToken.VALUE_STRING) {
            return super.getBinaryValue(variant);
        }
        throw _constructReadException("Current token (%s) is not native binary or a string",
                _currToken);
    }

    @Override public int readBinaryValue(Base64Variant variant, OutputStream out)
            throws JacksonException {
        if (_currToken != JsonToken.VALUE_EMBEDDED_OBJECT
                || _currentVPackType != VPackType.BINARY) {
            throw _constructReadException("Current token (%s) is not native binary", _currToken);
        }
        if (out == null) {
            throw _constructReadException("Output stream is null");
        }
        try {
            _root.range().writeTo(_binaryPayloadOffset, _binaryLength, out);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return _binaryLength;
    }

    @Override public void clearCurrentToken() {
        // Preserve Jackson's last-cleared token when called again without an
        // intervening token; clearing is not a terminal traversal operation.
        super.clearCurrentToken();
        _currentVPackType = null;
        _hasAttributeId = false;
        _embeddedValue = null;
        _clearRetainedValues();
    }

    @Override protected void _closeInput() throws IOException {
        if (_inputClosed) return;
        _inputClosed = true;
        _roots.close();
        Object source = _roots.source();
        if ((_ioContext.isResourceManaged() || isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE))
                && source instanceof java.io.Closeable closeable) {
            closeable.close();
        }
    }

    @Override protected void _releaseBuffers() {
        _arrayFrames.clear();
        if (!_symbolsReleased) {
            _symbolsReleased = true;
            if (_symbols != null) _symbols.release();
        }
        if (_root != null) {
            _root.close();
            _root = null;
        }
        _roots.releasePageSupplier();
        if (_rootBudget != null) {
            _rootBudget.close();
            _rootBudget = null;
        }
        // Drop the context chain as well: it can retain application values,
        // duplicate-name tables and recyclable child contexts after early exit.
        _streamReadContext = SimpleStreamReadContext.createRootContext(null);
        _currentVPackType = null;
        _hasAttributeId = false;
        _embeddedValue = null;
        _clearRetainedValues();
        if (_byteArrayBuilder != null) {
            _byteArrayBuilder.release();
            _byteArrayBuilder = null;
        }
        super._releaseBuffers();
    }
    @Override protected void _handleEOF() throws JacksonException { }

    private static final class DetachedRootContext extends SimpleStreamReadContext {
        DetachedRootContext(int index) {
            super(TYPE_ROOT, null, 0, null, 1, 0);
            _index = index;
        }
    }

    @Override public int releaseBuffered(OutputStream out) throws JacksonException {
        Objects.requireNonNull(out, "out");
        if (_releasedBuffered || _closed) {
            return 0;
        }

        // Capture all ranges and advance/clear parser traversal before calling
        // user code. A failed target, therefore, cannot make this operation
        // retryable or cause bytes to be replayed.
        VPackByteStore.Range current = null;
        int currentLength = 0;
        if (_root != null) {
            long logical = _nextLocation;
            if (logical < _root.startOffset() || logical > _root.endOffset()) {
                throw VPackErrors.malformed("releaseBuffered", logical,
                        "next logical position is outside the current root");
            }
            long length = _root.endOffset() - logical;
            if (length > 0L) {
                current = _root.store().range(_root.range().offset()
                        + (logical - _root.startOffset()), length);
                currentLength = VPackBounds.checkedInt(length, "released root bytes");
            }
        }
        VPackByteStore.Range remaining = _roots.detachRemaining();
        int remainingLength = remaining == null ? 0
                : VPackBounds.checkedInt(remaining.length(), "released source bytes");
        int total = VPackBounds.checkedInt((long) currentLength + remainingLength,
                "released byte count");

        _releasedBuffered = true;
        _arrayFrames.clear();
        _currToken = null;
        _currentVPackType = null;
        _hasAttributeId = false;
        _clearRetainedValues();

        try {
            if (current != null) {
                _writeRange(out, current);
            }
            if (remaining != null) {
                _writeRange(out, remaining);
            }
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        } finally {
            // Ranges must stay alive until the last write, even on failure.
            // Do not close the caller's source: close() still owns that policy.
            _releaseBuffers();
        }
        return total;
    }

    private static void _writeRange(OutputStream out, VPackByteStore.Range range)
            throws IOException {
        VPackBounds.checkedInt(range.length(), "released range");
        range.writeTo(0L, range.length(), out);
    }

    private void _clearRetainedValues() {
        _numberIsNaN = false;
        _numTypesValid = NR_UNKNOWN;
        _numberBigInt = null;
        _numberBigDecimal = null;
        _numberString = null;
        _binaryValue = null;
        _canonicalNumber = null;
        _doubleBits = 0L;
        _stringValue = null;
        _binaryPayloadOffset = 0L;
        _binaryLength = 0;
    }
}
