package tools.jackson.dataformat.velocypack;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    private final int _formatReadFeatures;
    private SimpleStreamReadContext _streamReadContext;
    private VPackRootReader.Root _root;
    private VPackType _currentVPackType;
    private BigInteger _currentAttributeId;
    private Object _embeddedValue;
    private String _stringValue;
    private long _binaryPayloadOffset;
    private int _binaryLength;
    private Number _canonicalNumber;
    private long _doubleBits;
    private long _currentLocation = -1L;
    private long _nextLocation;
    private boolean _inputClosed;
    private boolean _symbolsReleased;
    private boolean _releasedBuffered;
    private final ArrayDeque<Frame> _arrayFrames = new ArrayDeque<>();
    private VPackRootBudget _rootBudget;

    /** Compatibility constructor for direct package/API users of the parser. */
    @SuppressWarnings({"unused", "ClassEscapesItsScope"}) // Factory and compatibility constructor use the internal root source.
    public VPackParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            int streamReadFeatures, int formatReadFeatures,
            VPackReadConstraints vpackConstraints,
            VPackAttributeNameCodec attributeNameCodec, VPackRootReader roots) {
        this(readCtxt, ioCtxt, streamReadFeatures, formatReadFeatures,
                vpackConstraints, attributeNameCodec,
                ByteQuadsCanonicalizer.createRoot().makeChildOrPlaceholder(
                        TokenStreamFactory.Feature.collectDefaults()), roots);
    }

    @SuppressWarnings("ClassEscapesItsScope") // Factory constructs the parser with its internal root source.
    public VPackParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            int streamReadFeatures, int formatReadFeatures,
            VPackReadConstraints vpackConstraints,
            VPackAttributeNameCodec attributeNameCodec,
            ByteQuadsCanonicalizer symbols, VPackRootReader roots) {
        super(readCtxt, ioCtxt, streamReadFeatures);
        _roots = roots;
        _vpackConstraints = Objects.requireNonNull(vpackConstraints, "vpackConstraints");
        _attributeNameCodec = attributeNameCodec;
        _symbols = Objects.requireNonNull(symbols, "symbols");
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
    public boolean willInternPropertyNames() {
        return _symbols.willInternStrings();
    }

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
        _currentAttributeId = null;
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
            return _decodeScalar(new ValueView(0L, root.startOffset(), root.endOffset(),
                    root.marker(), root.classification()));
        } catch (RuntimeException e) {
            _currToken = null;
            _roots.close();
            _releaseBuffers();
            throw e;
        }
    }

    private JsonToken _decodeScalar(ValueView value) {
        int marker = value.marker();
        VPackMarker kind = value.classification();
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
            _decodeString(value);
            _currentVPackType = VPackType.STRING;
            return _updateToken(JsonToken.VALUE_STRING);
        }
        case BINARY -> {
            _decodeBinary(value);
            _currentVPackType = VPackType.BINARY;
            return _updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
        }
        case DOUBLE -> {
            byte[] payload = _readFixedPayload(value, 8);
            _doubleBits = VPackBounds.readBits(payload, 0, 8);
            _numberDouble = Double.longBitsToDouble(_doubleBits);
            _canonicalNumber = _numberDouble;
            _numberIsNaN = !Double.isFinite(_numberDouble);
            _numTypesValid = NR_DOUBLE;
            _currentVPackType = VPackType.DOUBLE;
            return _updateToken(JsonToken.VALUE_NUMBER_FLOAT);
        }
        case UTC_DATE -> {
            byte[] payload = _readFixedPayload(value, 8);
            _numberLong = VPackBounds.readSigned(payload, 0, 8);
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
            byte[] payload = _readFixedPayload(value, width);
            long numericValue = VPackBounds.readSigned(payload, 0, width);
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
            byte[] payload = _readFixedPayload(value, width);
            BigInteger numericValue = VPackBounds.readUnsigned(payload, 0, width);
            if (numericValue.bitLength() <= 31) {
                _numberInt = numericValue.intValue();
                _canonicalNumber = _numberInt;
                _numTypesValid = NR_INT;
            } else if (numericValue.bitLength() <= 63) {
                _numberLong = numericValue.longValue();
                _canonicalNumber = _numberLong;
                _numTypesValid = NR_LONG;
            } else {
                _numberBigInt = numericValue;
                _canonicalNumber = numericValue;
                _numTypesValid = NR_BIGINT;
            }
            _currentVPackType = VPackType.UNSIGNED_INTEGER;
            return _fixedIntegerToken();
        }
        case POSITIVE_BCD, NEGATIVE_BCD -> {
            int width = VPackMarker.width(marker);
            long mantissaLength = _readLength(value, width, "BCD mantissa length");
            byte[] exponentBytes = new byte[4];
            long exponentOffset = VPackBounds.checkedAdd(1L + width, 0L,
                    "BCD exponent offset");
            _root.range().copyTo(value.relativeStart() + exponentOffset, exponentBytes, 0, 4);
            int exponent = (int) VPackBounds.readSigned(exponentBytes, 0, 4);
            long mantissaOffset = VPackBounds.checkedAdd(exponentOffset, 4L,
                    "BCD mantissa offset");
            VPackNumbers.validateBcdInput(mantissaLength, exponent,
                    VPackBounds.checkedAdd(value.logicalStart(), mantissaOffset,
                            "BCD value location"), _streamReadConstraints);
            int length = VPackBounds.checkedInt(mantissaLength, "BCD mantissa length");
            byte[] mantissa = new byte[length];
            _root.range().copyTo(value.relativeStart() + mantissaOffset, mantissa, 0, length);
            Number numericValue = VPackNumbers.decodeBcd(mantissa, kind == VPackMarker.NEGATIVE_BCD,
                    exponent, VPackBounds.checkedAdd(value.logicalStart(), mantissaOffset,
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
        default -> throw VPackErrors.malformed("scalar parser", value.logicalStart(),
                "marker 0x" + Integer.toHexString(marker)
                        + " is not a supported scalar marker");
        }
    }

    private JsonToken _fixedIntegerToken() {
        // At most 20 decimal digits (uint64); never stringify an unbounded BCD here.
        String digits = _canonicalNumber.toString();
        _streamReadConstraints.validateIntegerLength(
                digits.length() - (digits.charAt(0) == '-' ? 1 : 0));
        return _updateToken(JsonToken.VALUE_NUMBER_INT);
    }

    private JsonToken _enterArray(Frame parent, long start, long enclosingEnd) {
        VPackMarker markerKind = VPackMarker.classify(_byteAt(start));
        VPackLayout.FixedLayout layout = null;
        CompactLayout compact = null;
        if (markerKind == VPackMarker.COMPACT_ARRAY) {
            compact = _analyzeCompact(start, enclosingEnd);
        } else if (markerKind != VPackMarker.EMPTY_ARRAY) {
            long relative = start - _root.startOffset();
            layout = VPackLayout.analyzeFixed(_root.range(), relative, _root.range().length(),
                    start, enclosingEnd);
            if (layout.classification() != VPackMarker.EQUAL_ARRAY
                    && layout.classification() != VPackMarker.INDEXED_ARRAY) {
                throw VPackErrors.malformed("array parser", start,
                        "marker is not a regular array");
            }
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
        VPackMarker markerKind = VPackMarker.classify(_byteAt(start));
        VPackLayout.FixedLayout layout = null;
        CompactLayout compact = null;
        if (markerKind == VPackMarker.COMPACT_OBJECT) {
            compact = _analyzeCompact(start, enclosingEnd);
        } else if (markerKind != VPackMarker.EMPTY_OBJECT) {
            long relative = start - _root.startOffset();
            layout = VPackLayout.analyzeFixed(_root.range(), relative, _root.range().length(),
                    start, enclosingEnd);
            if (layout.classification() != VPackMarker.SORTED_OBJECT
                    && layout.classification() != VPackMarker.UNSORTED_OBJECT) {
                throw VPackErrors.malformed("object parser", start,
                        "marker is not a regular object");
            }
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
        long end = _valueEnd(start, frame.bodyEnd, kind, marker);
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
            return _enterArray(frame, start, frame.bodyEnd);
        }
        if (kind == VPackMarker.EMPTY_OBJECT || kind == VPackMarker.SORTED_OBJECT
                || kind == VPackMarker.UNSORTED_OBJECT || kind == VPackMarker.COMPACT_OBJECT) {
            return _enterObject(frame, start, frame.bodyEnd);
        }
        if (VPackMarker.isContainer(marker)) {
            throw VPackErrors.malformed("array child", start,
                    "unhandled container marker");
        }
        _currentLocation = start;
        _nextLocation = end;
        JsonToken token = _decodeScalar(new ValueView(start - _root.startOffset(), start, end,
                marker, kind));
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
            NameValue name = _decodeName(start, end, marker, kind);
            if (frame.indexed) {
                frame.recordName(start - frame.start, name.utf8());
            }
            frame.cursor = end;
            frame.expectingName = false;
            frame.context.valueRead();
            frame.context.setCurrentName(name.text());
            _currentLocation = start;
            _nextLocation = end;
            _currentVPackType = name.attributeId() == null
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
        long end = _valueEnd(start, frame.bodyEnd, kind, marker);
        frame.cursor = end;
        if (kind == VPackMarker.EMPTY_ARRAY || kind == VPackMarker.EQUAL_ARRAY
                || kind == VPackMarker.INDEXED_ARRAY || kind == VPackMarker.COMPACT_ARRAY) {
            return _enterArray(frame, start, frame.bodyEnd);
        }
        if (kind == VPackMarker.EMPTY_OBJECT || kind == VPackMarker.SORTED_OBJECT
                || kind == VPackMarker.UNSORTED_OBJECT || kind == VPackMarker.COMPACT_OBJECT) {
            return _enterObject(frame, start, frame.bodyEnd);
        }
        _currentLocation = start;
        _nextLocation = end;
        JsonToken token = _decodeScalar(new ValueView(start - _root.startOffset(), start, end,
                marker, kind));
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
        case EQUAL_ARRAY, INDEXED_ARRAY, SORTED_OBJECT, UNSORTED_OBJECT -> {
            long relative = start - _root.startOffset();
            VPackLayout.FixedLayout nested = VPackLayout.analyzeFixed(_root.range(), relative,
                    _root.range().length(), start, bodyEnd);
            end = nested.end();
        }
        case COMPACT_ARRAY, COMPACT_OBJECT -> end = _analyzeCompact(start, bodyEnd).end();
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
        byte[] forward = new byte[forwardBytes];
        _root.range().copyTo(start - _root.startOffset() + 1L, forward, 0, forwardBytes);
        VPackVarInts.Decoded lengthValue = VPackVarInts.readForward(forward, 0, forward.length);
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
        byte[] suffix = new byte[(int) suffixLength];
        _root.range().copyTo(suffixBase - _root.startOffset(), suffix, 0, suffix.length);
        VPackVarInts.Decoded countValue = VPackVarInts.readReverse(suffix, 0, suffix.length);
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
        byte[] encoded = new byte[width];
        long relative = start - _root.startOffset() + 1L;
        _root.range().copyTo(relative, encoded, 0, width);
        return VPackBounds.readScalarLength(encoded, 0, width, start + 1L);
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
        Map<Long, Integer> observed = new HashMap<>(Math.max(4, frame.completed * 2));
        for (int i = 0; i < frame.completed; ++i) {
            Integer previous = observed.put(frame.keyStarts[i], i);
            if (previous != null) {
                throw VPackErrors.malformed("object index", frame.indexStart,
                        "object index domain contains a duplicate key start");
            }
        }
        boolean[] seen = new boolean[frame.completed];
        byte[] previousName = null;
        for (int i = 0; i < frame.completed; ++i) {
            long index = _readStructural(frame.indexStart + (long) i * frame.width,
                    frame.width);
            Integer key = observed.get(index);
            if (key == null) {
                throw VPackErrors.malformed("object index", frame.indexStart,
                        "index entry does not point to an observed key start");
            }
            if (seen[key]) {
                throw VPackErrors.malformed("object index", frame.indexStart,
                        "object index entries are not a bijection onto key starts");
            }
            seen[key] = true;
            if (frame.sorted) {
                byte[] currentName = frame.nameBytes[key];
                if (previousName != null && Arrays.compareUnsigned(previousName, currentName) > 0) {
                    throw VPackErrors.malformed("object index", frame.indexStart,
                            "sorted object index is not in unsigned UTF-8 name order");
                }
                previousName = currentName;
            }
        }
        for (boolean keySeen : seen) {
            if (!keySeen) {
                throw VPackErrors.malformed("object index", frame.indexStart,
                        "object index omits an observed key start");
            }
        }
    }

    private NameValue _decodeName(long start, long end, int marker, VPackMarker kind) {
        if (kind == VPackMarker.UNSIGNED_INTEGER || kind == VPackMarker.SMALL_POSITIVE) {
            BigInteger id = kind == VPackMarker.SMALL_POSITIVE
                    ? BigInteger.valueOf(marker - 0x30L)
                    : _readUnsignedKeyId(start, marker);
            if (id.signum() < 0 || id.compareTo(VPackBounds.UINT64_MAX) > 0) {
                throw VPackErrors.malformed("compressed attribute name", start,
                        "attribute ID is outside the uint64 domain");
            }
            if (_attributeNameCodec == null) {
                throw VPackErrors.malformed("compressed attribute name", start,
                        "no attribute-name codec is configured for ID " + id);
            }
            final String text;
            try {
                text = _attributeNameCodec.decode(id);
            } catch (RuntimeException e) {
                throw VPackErrors.input("compressed attribute name", start, e);
            }
            if (text == null) {
                throw VPackErrors.malformed("compressed attribute name", start,
                        "codec could not resolve ID " + id);
            }
            byte[] utf8 = _encodeResolvedName(text);
            _rootBudget.chargeName(utf8.length);
            _currentAttributeId = id;
            return new NameValue(_canonicalizeName(text, utf8), utf8, id);
        }
        long payloadOffset;
        long byteLength;
        if (kind == VPackMarker.SHORT_STRING) {
            payloadOffset = 1L;
            byteLength = marker - 0x40L;
        } else {
            payloadOffset = 9L;
            byteLength = _readLengthAt(start, 8);
        }
        VPackBounds.checkedRange(start - _root.startOffset() + payloadOffset, byteLength,
                _root.range().length(), "object key payload");
        if (end < start || end - start < payloadOffset
                || byteLength > end - start - payloadOffset) {
            throw VPackErrors.malformed("object property name", start,
                    "key payload exceeds its encoded string boundary");
        }
        _rootBudget.chargeName(byteLength);
        int length = VPackBounds.checkedInt(byteLength, "object key byte length");
        byte[] utf8 = new byte[length];
        _root.range().copyTo(start - _root.startOffset() + payloadOffset, utf8, 0, length);
        String decoded = new String(utf8, StandardCharsets.UTF_8);
        _streamReadConstraints.validateNameLength(decoded.length());
        return new NameValue(_canonicalizeName(decoded, utf8), utf8);
    }

    /** Feed the UTF-8 bytes to the bounded core canonicalizer. */
    private String _canonicalizeName(String text, byte[] utf8) {
        if (!_symbols.isCanonicalizing()) {
            return text;
        }
        if (utf8.length == 0) {
            return _symbols.willInternStrings() ? text.intern() : text;
        }
        int quadLength = (utf8.length + 3) >> 2;
        int[] quads = new int[quadLength];
        int input = 0;
        for (int i = 0; i < quadLength; ++i) {
            int remaining = utf8.length - input;
            if (remaining < 4) {
                int q = (utf8[input++] & 0xFF) | 0xFFFFFF00;
                while (--remaining > 0) {
                    q = (q << 8) | (utf8[input++] & 0xFF);
                }
                quads[i] = q;
            } else {
                quads[i] = ((utf8[input++] & 0xFF) << 24)
                        | ((utf8[input++] & 0xFF) << 16)
                        | ((utf8[input++] & 0xFF) << 8)
                        | (utf8[input++] & 0xFF);
            }
        }
        String existing = _symbols.findName(quads, quadLength);
        return existing == null ? _symbols.addName(text, quads, quadLength) : existing;
    }

    private BigInteger _readUnsignedKeyId(long start, int marker) {
        int width = VPackMarker.width(marker);
        byte[] payload = _readBytes(start - _root.startOffset() + 1L, width);
        return VPackBounds.readUnsigned(payload, 0, width);
    }

    private byte[] _readBytes(long relativeOffset, int length) {
        VPackBounds.checkedRange(relativeOffset, length, _root.range().length(),
                "compressed attribute ID");
        byte[] bytes = new byte[length];
        _root.range().copyTo(relativeOffset, bytes, 0, length);
        return bytes;
    }

    private byte[] _encodeResolvedName(String text) {
        _streamReadConstraints.validateNameLength(text.length());
        byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
        _rootBudget.checkName(utf8.length);
        return utf8;
    }

    private long _readStructural(long logicalOffset, int width) {
        byte[] encoded = new byte[width];
        _root.range().copyTo(logicalOffset - _root.startOffset(), encoded, 0, width);
        return VPackBounds.readStructural(encoded, 0, width, logicalOffset);
    }

    private int _byteAt(long logicalOffset) {
        return _root.range().byteAt(logicalOffset - _root.startOffset()) & 0xFF;
    }

    private record ValueView(long relativeStart, long logicalStart, long end,
            int marker, VPackMarker classification) { }

    private record CompactLayout(long end, long bodyStart, long bodyEnd, long count) { }

    private record NameValue(String text, byte[] utf8, BigInteger attributeId) {
        NameValue(String text, byte[] utf8) {
            this(text, utf8, null);
        }
    }

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
        long[] keyStarts;
        byte[][] nameBytes;

        ObjectFrame(Frame parent, SimpleStreamReadContext context, long start, long end,
                long bodyStart, long bodyEnd, int width, boolean sorted, long expectedCount,
                long indexStart, boolean compact) {
            super(parent, context, start, end, bodyStart, bodyEnd, width, !compact,
                    expectedCount, indexStart, compact);
            this.sorted = sorted;
        }

        void recordName(long keyStart, byte[] utf8) {
            if (keyStarts == null || completed >= keyStarts.length) {
                int oldLength = keyStarts == null ? 0 : keyStarts.length;
                // Keep the historical cap even when a very small maximum is below the initial size.
                //noinspection MathClampMigration
                int newLength = Math.min(Integer.MAX_VALUE - 8,
                        Math.max(completed + 1, Math.max(16, oldLength * 2)));
                keyStarts = keyStarts == null ? new long[newLength]
                        : java.util.Arrays.copyOf(keyStarts, newLength);
                nameBytes = nameBytes == null ? new byte[newLength][]
                        : java.util.Arrays.copyOf(nameBytes, newLength);
            }
            keyStarts[completed] = keyStart;
            nameBytes[completed] = utf8;
        }
    }

    private byte[] _readFixedPayload(ValueView value, int length) {
        byte[] payload = new byte[length];
        _root.range().copyTo(value.relativeStart() + 1L, payload, 0, length);
        return payload;
    }

    private void _decodeString(ValueView value) {
        long payloadOffset;
        long byteLength;
        if (value.classification() == VPackMarker.SHORT_STRING) {
            payloadOffset = 1L;
            byteLength = value.marker() - 0x40L;
        } else {
            payloadOffset = 9L;
            byteLength = _readLength(value, 8, "long string length");
        }
        int length = VPackBounds.checkedInt(byteLength, "string byte length");
        byte[] utf8 = new byte[length];
        _root.range().copyTo(value.relativeStart() + payloadOffset, utf8, 0, length);
        _stringValue = new String(utf8, StandardCharsets.UTF_8);
        _streamReadConstraints.validateStringLengthLong(_stringValue.length());
    }

    private void _decodeBinary(ValueView value) {
        int width = VPackMarker.width(value.marker());
        long payloadOffset = 1L + width;
        long length = _readLength(value, width, "binary length");
        _binaryPayloadOffset = value.relativeStart() + payloadOffset;
        _binaryLength = VPackBounds.checkedInt(length, "binary payload length");
        VPackBounds.checkedRange(value.relativeStart() + payloadOffset, length,
                _root.range().length(), "binary payload");
    }

    private long _readLength(ValueView value, int width, String context) {
        byte[] encoded = new byte[width];
        _root.range().copyTo(value.relativeStart() + 1L, encoded, 0, width);
        return VPackBounds.readScalarLength(encoded, 0, width,
                VPackBounds.checkedAdd(value.logicalStart(), 1L, context));
    }

    public VPackType currentVPackType() { return _currToken == null ? null : _currentVPackType; }

    int formatReadFeatures() { return _formatReadFeatures; }

    VPackReadConstraints vpackReadConstraints() { return _vpackConstraints; }

    long currentDoubleBits() {
        return _currToken == JsonToken.VALUE_NUMBER_FLOAT && _currentVPackType == VPackType.DOUBLE
                ? _doubleBits : 0L;
    }

    public BigInteger currentAttributeId() { return _currentAttributeId; }

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
        byte[] chunk = new byte[Math.min(VPackByteStore.PAGE_SIZE, _binaryLength)];
        int done = 0;
        try {
            while (done < _binaryLength) {
                int count = Math.min(chunk.length, _binaryLength - done);
                _root.range().copyTo(_binaryPayloadOffset + done, chunk, 0, count);
                out.write(chunk, 0, count);
                done += count;
            }
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return done;
    }

    @Override public void clearCurrentToken() {
        // Preserve Jackson's last-cleared token when called again without an
        // intervening token; clearing is not a terminal traversal operation.
        super.clearCurrentToken();
        _currentVPackType = null;
        _currentAttributeId = null;
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
        if (_root != null) {
            _root.close();
            _root = null;
        }
        if (_rootBudget != null) {
            _rootBudget.close();
            _rootBudget = null;
        }
        // Drop the context chain as well: it can retain application values,
        // duplicate-name tables and recyclable child contexts after early exit.
        _streamReadContext = SimpleStreamReadContext.createRootContext(null);
        _currentVPackType = null;
        _currentAttributeId = null;
        _embeddedValue = null;
        _clearRetainedValues();
        if (_byteArrayBuilder != null) {
            _byteArrayBuilder.release();
            _byteArrayBuilder = null;
        }
        super._releaseBuffers();
        if (!_symbolsReleased) {
            _symbolsReleased = true;
            _symbols.release();
        }
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
        _currentAttributeId = null;
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
        int length = VPackBounds.checkedInt(range.length(), "released range");
        byte[] buffer = new byte[Math.min(VPackByteStore.PAGE_SIZE, length)];
        int offset = 0;
        while (offset < length) {
            int count = Math.min(buffer.length, length - offset);
            range.copyTo(offset, buffer, 0, count);
            out.write(buffer, 0, count);
            offset += count;
        }
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
