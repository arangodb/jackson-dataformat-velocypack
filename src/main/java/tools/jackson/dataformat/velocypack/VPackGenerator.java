package tools.jackson.dataformat.velocypack;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;

import tools.jackson.core.*;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.SimpleStreamWriteContext;

/** VelocyPack generator with iterative container assembly. Nested serializers write through it. */
@SuppressWarnings({"resource", "AutoCloseableResource"})
public class VPackGenerator extends GeneratorBase {
    private final OutputStream _out;
    private final Object _outputTarget;
    private final VPackWriteConstraints _vpackWriteConstraints;
    private final VPackAttributeNameCodec _attributeNameCodec;
    private final int _formatFeatures;
    private final VPackOutputArena _arena;
    private final ArrayDeque<ContainerFrame> _containerFrames = new ArrayDeque<>();
    private final VPackRootBudget _rootBudget;

    private SimpleStreamWriteContext _streamWriteContext;
    private boolean _failed;
    private RuntimeException _failure;
    private long _bytesCopied;
    private long _segmentTransfers;

    @SuppressWarnings("unused") // Public compatibility constructor for direct callers.
    public VPackGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt,
            int streamWriteFeatures, int formatFeatures, OutputStream out,
            VPackWriteConstraints constraints) {
        this(writeCtxt, ioCtxt, streamWriteFeatures, formatFeatures, out,
                constraints, null);
    }

    public VPackGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt,
            int streamWriteFeatures, int formatFeatures, OutputStream out,
            VPackWriteConstraints constraints, VPackAttributeNameCodec attributeNameCodec) {
        super(writeCtxt, ioCtxt, streamWriteFeatures);
        _out = out;
        _outputTarget = out instanceof VPackDataOutputStream dataOutput
                ? dataOutput.target() : out;
        _formatFeatures = formatFeatures;
        _vpackWriteConstraints = constraints;
        _attributeNameCodec = attributeNameCodec;
        DupDetector dups = StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamWriteFeatures)
                ? DupDetector.rootDetector(this) : null;
        _streamWriteContext = SimpleStreamWriteContext.createRootContext(dups);
        _arena = new VPackOutputArena(constraints,
                VPackRecyclerPageSupplier.forWrite(_ioContext));
        _rootBudget = new VPackRootBudget(constraints);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public JacksonFeatureSet<StreamWriteCapability> streamWriteCapabilities() {
        return DEFAULT_BINARY_WRITE_CAPABILITIES;
    }

    @Override
    public Object streamWriteOutputTarget() {
        return _outputTarget;
    }

    @Override
    public int streamWriteOutputBuffered() {
        if (_failed || _closed || _arena.isReleased()) {
            return 0;
        }
        return (int) Math.min(Integer.MAX_VALUE, _arena.size());
    }

    @Override
    public TokenStreamContext streamWriteContext() {
        return new VPackWriteContextView(_streamWriteContext);
    }

    @Override
    public Object currentValue() {
        return _streamWriteContext.currentValue();
    }

    @Override
    public void assignCurrentValue(Object value) {
        _streamWriteContext.assignCurrentValue(value);
    }

    @Override
    public JsonGenerator writeBoolean(boolean value) throws JacksonException {
        return _writeScalar(new byte[] { (byte) (value ? VPackConstants.TRUE : VPackConstants.FALSE) },
                "write boolean value");
    }

    @Override
    public JsonGenerator writeNull() throws JacksonException {
        return _writeScalar(new byte[] { (byte) VPackConstants.NULL }, "write null");
    }

    @Override
    public JsonGenerator writeNumber(short value) throws JacksonException {
        return writeNumber((long) value);
    }

    @Override
    public JsonGenerator writeNumber(int value) throws JacksonException {
        return writeNumber((long) value);
    }

    @Override
    public JsonGenerator writeNumber(long value) throws JacksonException {
        return _writeScalar(encodeInteger(value), "write number");
    }

    @Override
    public JsonGenerator writeNumber(BigInteger value) throws JacksonException {
        if (value == null) {
            return writeNull();
        }
        try {
            byte[] encoded;
            if (value.signum() < 0 && value.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
                encoded = VPackNumbers.encodeBcd(value, 0,
                        _vpackWriteConstraints.getMaxNumberDigits(),
                        remainingRootBytes());
            } else if (value.signum() >= 0 && value.compareTo(VPackBounds.UINT64_MAX) > 0) {
                encoded = VPackNumbers.encodeBcd(value, 0,
                        _vpackWriteConstraints.getMaxNumberDigits(),
                        remainingRootBytes());
            } else {
                encoded = encodeInteger(value);
            }
            checkScalarSize(encoded.length - 1L, 1L, "number");
            return _writeScalar(encoded, "write number");
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeNumber(double value) throws JacksonException {
        byte[] encoded = new byte[9];
        encoded[0] = (byte) VPackConstants.DOUBLE;
        VPackBounds.writeBits(encoded, 1, 8, Double.doubleToRawLongBits(value));
        return _writeScalar(encoded, "write number");
    }

    @Override
    public JsonGenerator writeNumber(float value) throws JacksonException {
        return writeNumber((double) value);
    }

    @Override
    public JsonGenerator writeNumber(BigDecimal value) throws JacksonException {
        if (value == null) {
            return writeNull();
        }
        try {
            long exponent = -(long) value.scale();
            if (exponent < Integer.MIN_VALUE || exponent > Integer.MAX_VALUE) {
                throw VPackErrors.write("BigDecimal", "scale cannot be represented by a BCD int32 exponent");
            }
            byte[] encoded = VPackNumbers.encodeBcd(value.unscaledValue(), (int) exponent,
                    _vpackWriteConstraints.getMaxNumberDigits(),
                    remainingRootBytes());
            checkScalarSize(encoded.length - 1L, 1L, "BigDecimal");
            return _writeScalar(encoded, "write number");
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeNumber(String value) throws JacksonException {
        if (value == null) {
            return writeNull();
        }
        try {
            Number parsed = VPackNumbers.parseTextualNumber(value,
                    _vpackWriteConstraints.getMaxNumberDigits());
            if (parsed instanceof BigInteger integer) {
                return writeNumber(integer);
            }
            return writeNumber((BigDecimal) parsed);
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeNumber(char[] value, int offset, int length)
            throws JacksonException {
        try {
            if (value == null || offset < 0 || length < 0
                    || offset > value.length - length) {
                throw VPackErrors.write("number", "character range is invalid");
            }
            ensureActive();
            VPackNumbers.validateTextualLength(length, _vpackWriteConstraints.getMaxNumberDigits());
            return writeNumber(new String(value, offset, length));
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    /** Write the physical VPack UTC-date marker without changing ordinary long semantics. */
    public VPackGenerator writeVPackDate(long epochMillis) throws JacksonException {
        try {
            byte[] encoded = new byte[9];
            encoded[0] = (byte) VPackConstants.UTC_DATE;
            VPackBounds.writeBits(encoded, 1, 8, epochMillis);
            _writeScalar(encoded, "write VPack date");
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    /** Write a physical VPack min/max sentinel. */
    public VPackGenerator writeVPackSpecial(VPackSpecialValue value) throws JacksonException {
        try {
            if (value == null) {
                throw VPackErrors.write("VPack special", "value is null; use writeNull explicitly");
            }
            int marker = value == VPackSpecialValue.MIN_KEY
                    ? VPackConstants.MIN_KEY : VPackConstants.MAX_KEY;
            _writeScalar(new byte[] { (byte) marker }, "write VPack special");
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeEmbeddedObject(Object object) throws JacksonException {
        if (object == null) {
            return writeNull();
        }
        if (object instanceof byte[] bytes) {
            return writeBinary(null, bytes, 0, bytes.length);
        }
        if (object instanceof VPackDate date) {
            return writeVPackDate(date.epochMillis());
        }
        if (object instanceof VPackSpecialValue special) {
            return writeVPackSpecial(special);
        }
        try {
            throw VPackErrors.write("embedded object",
                    "unsupported native value type " + object.getClass().getName());
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    /**
     * Copy one token while retaining VPack-only scalar representations when
     * the source is this format.  The core implementation necessarily goes
     * through the generic JsonParser surface, which cannot expose DATE or a
     * double's original payload bits.
     */
    @Override
    public void copyCurrentEvent(JsonParser parser) throws JacksonException {
        try {
            ensureActive();
            if (parser instanceof VPackParser source) {
                JsonToken token = source.currentToken();
                if (token == null) {
                    _reportError("No current event to copy");
                    return;
                }
                switch (token.id()) {
                case JsonTokenId.ID_NUMBER_INT:
                    if (source.currentVPackType() == VPackType.DATE) {
                        writeVPackDate(source.getLongValue());
                    } else {
                        _copyVPackNumber(source);
                    }
                    return;
                case JsonTokenId.ID_NUMBER_FLOAT:
                    if (source.currentVPackType() == VPackType.DOUBLE) {
                        _writeVPackDoubleBits(source.currentDoubleBits());
                    } else {
                        _copyVPackNumber(source);
                    }
                    return;
                case JsonTokenId.ID_EMBEDDED_OBJECT:
                    writeEmbeddedObject(source.getEmbeddedObject());
                    return;
                default:
                    // Structural and ordinary scalar tokens have no additional
                    // VPack state beyond the normal Jackson token value.
                    break;
                }
            }
            // JsonGenerator's generic fallback calls writePOJO() for embedded
            // values.  That is intentionally broader than VPack's native
            // embedded-value contract, so use the format capability explicitly.
            if (parser.currentToken() == JsonToken.VALUE_EMBEDDED_OBJECT) {
                writeEmbeddedObject(parser.getEmbeddedObject());
                return;
            }
            super.copyCurrentEvent(parser);
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    /**
     * Preserve exact numeric values for a VPack source, including BCD scale.
     */
    private void _copyVPackNumber(JsonParser parser) throws JacksonException {
        Number value = parser.getNumberValue();
        if (value instanceof Integer integer) {
            writeNumber(integer);
        } else if (value instanceof Long longValue) {
            writeNumber(longValue);
        } else if (value instanceof BigInteger bigInteger) {
            writeNumber(bigInteger);
        } else if (value instanceof BigDecimal bigDecimal) {
            writeNumber(bigDecimal);
        } else if (value instanceof Float floatValue) {
            writeNumber(floatValue);
        } else if (value instanceof Double doubleValue) {
            writeNumber(doubleValue);
        } else {
            throw _constructWriteException("Unsupported numeric value type %s",
                    value == null ? "null" : value.getClass().getName());
        }
    }

    /** Write a double without converting its already-retained wire payload. */
    private void _writeVPackDoubleBits(long bits) throws JacksonException {
        try {
            byte[] encoded = new byte[9];
            encoded[0] = (byte) VPackConstants.DOUBLE;
            VPackBounds.writeBits(encoded, 1, 8, bits);
            _writeScalar(encoded, "copy VPack double");
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public void copyCurrentEventExact(JsonParser parser) throws JacksonException {
        try {
            ensureActive();
            // VPack's canonical numeric value and retained double bits already
            // provide the exact copy behavior required by the format.
            if (parser instanceof VPackParser) {
                copyCurrentEvent(parser);
                return;
            }
            if (parser.currentToken() == JsonToken.VALUE_EMBEDDED_OBJECT) {
                writeEmbeddedObject(parser.getEmbeddedObject());
                return;
            }
            super.copyCurrentEventExact(parser);
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public void copyCurrentStructure(JsonParser parser) throws JacksonException {
        try {
            ensureActive();
            _copyCurrentStructure(parser, false);
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public void copyCurrentStructureExact(JsonParser parser) throws JacksonException {
        try {
            ensureActive();
            _copyCurrentStructure(parser, true);
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    private void _copyCurrentStructure(JsonParser parser, boolean exact)
            throws JacksonException {
        JsonToken token = parser.currentToken();
        if (token == null) {
            _reportError("No current event to copy");
        }
        if (token == JsonToken.PROPERTY_NAME) {
            writeName(parser.currentName());
            token = parser.nextToken();
            if (token == null) {
                throw VPackErrors.malformed("copy structure", "property has no value");
            }
        }
        if (token != JsonToken.START_OBJECT && token != JsonToken.START_ARRAY) {
            if (exact) {
                copyCurrentEventExact(parser);
            } else {
                copyCurrentEvent(parser);
            }
            return;
        }

        int depth = 1;
        if (exact) {
            copyCurrentEventExact(parser);
        } else {
            copyCurrentEvent(parser);
        }
        while (depth != 0) {
            token = parser.nextToken();
            if (token == null) {
                throw VPackErrors.malformed("copy structure",
                        "unexpected end of input before container close");
            }
            if (exact) {
                copyCurrentEventExact(parser);
            } else {
                copyCurrentEvent(parser);
            }
            if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                ++depth;
            } else if (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY) {
                --depth;
            }
        }
    }

    @Override
    public JsonGenerator writeStartArray() throws JacksonException {
        return _writeStartArray(null);
    }

    @Override
    public JsonGenerator writeStartArray(Object currentValue) throws JacksonException {
        return _writeStartArray(currentValue);
    }

    @Override
    public JsonGenerator writeStartArray(Object currentValue, int size) throws JacksonException {
        // Size hints are deliberately not trusted; observed children choose the layout.
        return _writeStartArray(currentValue);
    }

    @Override
    public JsonGenerator writeEndArray() throws JacksonException {
        try {
            ensureActive();
            ContainerFrame current = _containerFrames.peek();
            if (!(current instanceof ArrayFrame frame)) {
                throw VPackErrors.write("array end", "no array is open");
            }
            completeContainer(frame);
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeStartObject() throws JacksonException {
        return _writeStartObject(null);
    }

    @Override
    public JsonGenerator writeStartObject(Object currentValue) throws JacksonException {
        return _writeStartObject(currentValue);
    }

    @Override
    public JsonGenerator writeStartObject(Object currentValue, int size) throws JacksonException {
        return _writeStartObject(currentValue);
    }

    @Override
    public JsonGenerator writeEndObject() throws JacksonException {
        try {
            ensureActive();
            ContainerFrame current = _containerFrames.peek();
            if (!(current instanceof ObjectFrame frame)) {
                throw VPackErrors.write("object end", "no object is open");
            }
            completeContainer(frame);
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeName(String name) throws JacksonException {
        try {
            ensureActive();
            ContainerFrame current = _containerFrames.peek();
            if (!(current instanceof ObjectFrame frame)) {
                throw VPackErrors.write("object name", "no object is open");
            }
            _rootBudget.chargeEntry();
            ResolvedName resolved = resolveName(name);
            writeObjectName(frame, resolved);
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writePropertyId(long id) throws JacksonException {
        try {
            ensureActive();
            ContainerFrame current = _containerFrames.peek();
            if (!(current instanceof ObjectFrame frame)) {
                throw VPackErrors.write("property id", "no object is open");
            }
            if (id < 0L) {
                throw VPackErrors.write("property id", "signed integer keys are forbidden");
            }
            if (_attributeNameCodec == null) {
                throw VPackErrors.write("property id", "no attribute-name codec is configured");
            }
            BigInteger unsignedId = BigInteger.valueOf(id);
            String resolved;
            try {
                resolved = _attributeNameCodec.decode(unsignedId);
            } catch (RuntimeException e) {
                throw VPackErrors.write("property id", "attribute-name codec failed: " + e);
            }
            if (resolved == null) {
                throw VPackErrors.write("property id", "codec could not resolve ID " + id);
            }
            ResolvedName name = resolveName(resolved, unsignedId);
            _rootBudget.chargeEntry();
            writeObjectName(frame, name);
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeString(String value) throws JacksonException {
        if (value == null) {
            return writeNull();
        }
        try {
            byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
            return _writeScalar(encodeString(utf8, 0, utf8.length, "string"),
                    "write string");
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeString(Reader reader, int len) throws JacksonException {
        try {
            ensureActive();
            if (reader == null) {
                throw VPackErrors.write("string reader", "reader is null");
            }
            if (len >= 0 && (long) len > maxStringCharacters()) {
                throw VPackErrors.constraint("string reader", -1L,
                        "character count exceeds the configured root byte budget");
            }
            StringBuilder value = new StringBuilder(Math.min(len >= 0 ? len : 1024, 4096));
            char[] buffer = new char[4096];
            long remaining = len;
            while (len < 0 || remaining > 0L) {
                int requested = len < 0
                        ? buffer.length
                        : (int) Math.min(buffer.length, remaining);
                int count;
                try {
                    count = reader.read(buffer, 0, requested);
                } catch (IOException e) {
                    throw JacksonIOException.construct(e, this);
                }
                if (count < 0) {
                    if (len >= 0) {
                        throw VPackErrors.write("string reader",
                                "reader ended before the requested character count");
                    }
                    break;
                }
                if (count == 0) {
                    int one;
                    try {
                        one = reader.read();
                    } catch (IOException e) {
                        throw JacksonIOException.construct(e, this);
                    }
                    if (one < 0) {
                        if (len >= 0) {
                            throw VPackErrors.write("string reader",
                                    "reader ended before the requested character count");
                        }
                        break;
                    }
                    value.append((char) one);
                    checkStringCharacters(value.length());
                    if (len >= 0) {
                        --remaining;
                    }
                    continue;
                }
                if (count > requested) {
                    throw VPackErrors.write("string reader", "reader returned too many characters");
                }
                value.append(buffer, 0, count);
                checkStringCharacters(value.length());
                if (len >= 0) {
                    remaining -= count;
                }
            }
            return writeString(value.toString());
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeString(char[] buffer, int offset, int len) throws JacksonException {
        try {
            if (buffer == null || offset < 0 || len < 0 || offset > buffer.length - len) {
                throw VPackErrors.write("string", "character range is invalid");
            }
            return writeString(new String(buffer, offset, len));
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeRawUTF8String(byte[] buffer, int offset, int len)
            throws JacksonException {
        return _unsupportedRaw("raw UTF-8 string writing");
    }

    @Override
    public JsonGenerator writeUTF8String(byte[] buffer, int offset, int len)
            throws JacksonException {
        try {
            VPackBounds.checkedWriteArrayRange(buffer, offset, len, "UTF-8 string");
            return _writeScalar(encodeString(buffer, offset, len, "UTF-8 string"),
                    "write UTF-8 string");
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public JsonGenerator writeRaw(String text) throws JacksonException {
        return _unsupportedRaw("raw string writing");
    }

    @Override
    public JsonGenerator writeRaw(String text, int offset, int len) throws JacksonException {
        return _unsupportedRaw("raw string writing");
    }

    @Override
    public JsonGenerator writeRaw(char[] buffer, int offset, int len) throws JacksonException {
        return _unsupportedRaw("raw string writing");
    }

    @Override
    public JsonGenerator writeRaw(char c) throws JacksonException {
        return _unsupportedRaw("raw string writing");
    }

    @Override
    public JsonGenerator writeBinary(Base64Variant variant, byte[] data, int offset, int len)
            throws JacksonException {
        try {
            VPackBounds.checkedWriteArrayRange(data, offset, len, "binary");
            return _writeScalar(encodeBinary(data, offset, len), "write binary");
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public int writeBinary(Base64Variant variant, java.io.InputStream data, int dataLength)
            throws JacksonException {
        try {
            byte[] payload = readBinary(data, dataLength);
            _writeScalar(encodeBinary(payload, 0, payload.length), "write binary");
            return payload.length;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    @Override
    public void flush() throws JacksonException {
        if (_closed) {
            return;
        }
        ensureActive();
        if (StreamWriteFeature.FLUSH_PASSED_TO_STREAM.enabledIn(_streamWriteFeatures)) {
            try {
                _out.flush();
            } catch (IOException e) {
                throw fail(JacksonIOException.construct(e, this));
            }
        }
    }

    @Override
    public void close() {
        if (_closed) {
            return;
        }
        RuntimeException problem = _failed ? closeFailure() : null;
        try {
            if (problem == null) {
                if (!_containerFrames.isEmpty()) {
                    if (!StreamWriteFeature.AUTO_CLOSE_CONTENT.enabledIn(_streamWriteFeatures)) {
                        throw VPackErrors.write("generator close",
                                "open content was discarded because AUTO_CLOSE_CONTENT is disabled");
                    }
                    while (!_containerFrames.isEmpty()) {
                        completeContainer(_containerFrames.peek());
                    }
                }
                if (StreamWriteFeature.FLUSH_PASSED_TO_STREAM.enabledIn(_streamWriteFeatures)) {
                    _out.flush();
                }
            }
        } catch (IOException e) {
            problem = fail(JacksonIOException.construct(e, this));
        } catch (RuntimeException e) {
            problem = fail(e);
        } finally {
            try {
                if ((_ioContext != null && _ioContext.isResourceManaged())
                        || StreamWriteFeature.AUTO_CLOSE_TARGET.enabledIn(_streamWriteFeatures)) {
                    _out.close();
                }
            } catch (IOException e) {
                RuntimeException cleanup = JacksonIOException.construct(e, this);
                problem = addCleanupFailure(problem, cleanup);
            } catch (RuntimeException e) {
                problem = addCleanupFailure(problem, e);
            } finally {
                try {
                    releaseOwnedBuffers();
                } catch (RuntimeException e) {
                    problem = addCleanupFailure(problem, e);
                }
                try {
                    if (_ioContext != null) {
                        _ioContext.close();
                    }
                } catch (RuntimeException e) {
                    problem = addCleanupFailure(problem, e);
                } finally {
                    _closed = true;
                }
            }
        }
        if (problem != null) {
            throw problem;
        }
    }

    @Override
    protected void _closeInput() throws IOException {
        // close() owns the lifecycle so failures are retained and cleanup is suppressed.
    }

    @Override
    protected void _releaseBuffers() {
        releaseOwnedBuffers();
    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws JacksonException {
        ensureActive();
        if (!_streamWriteContext.writeValue()) {
            throw fail(_constructWriteException("Cannot " + typeMsg
                    + ", expecting a property name/id"));
        }
        if (_containerFrames.peek() instanceof ArrayFrame) {
            _rootBudget.chargeEntry();
        }
    }

    private JsonGenerator _writeScalar(byte[] encoded, String typeMsg) {
        try {
            _verifyValueWrite(typeMsg);
            VPackByteStore.Range range = _arena.append(encoded);
            ContainerFrame frame = _containerFrames.peek();
            if (frame != null) {
                frame.body.append(range);
                recordValue(frame, range.length());
                return this;
            }
            try {
                _out.write(encoded, 0, encoded.length);
            } catch (IOException e) {
                throw fail(JacksonIOException.construct(e, this));
            } finally {
                _bytesCopied += _arena.copiedBytes();
                if (!_arena.isReleased()) {
                    _arena.reset();
                    _rootBudget.reset();
                }
            }
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    private JsonGenerator _staged(String message) {
        throw fail(new UnsupportedOperationException(message));
    }

    private void completeContainer(ContainerFrame frame) {
        if (frame instanceof ArrayFrame array) {
            finishArray(array);
        } else {
            finishObject((ObjectFrame) frame);
        }
        _containerFrames.pop();
        _streamWriteContext = frame.streamContext.clearAndGetParent();
        ContainerFrame parent = frame.parent;
        if (parent == null) {
            outputRoot(frame.body);
        } else {
            long childSize = frame.body.size();
            parent.body.transferFrom(frame.body);
            ++_segmentTransfers;
            recordValue(parent, childSize);
        }
    }

    private JsonGenerator _writeStartArray(Object currentValue) {
        try {
            ensureActive();
            _verifyValueWrite("start an array");
            ContainerFrame parent = _containerFrames.peek();
            SimpleStreamWriteContext childContext = _streamWriteContext
                    .createChildArrayContext(currentValue);
            streamWriteConstraints().validateNestingDepth(childContext.getNestingDepth());
            ArrayFrame frame = new ArrayFrame(parent, childContext, _arena);
            _containerFrames.push(frame);
            _streamWriteContext = childContext;
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    private JsonGenerator _writeStartObject(Object currentValue) {
        try {
            ensureActive();
            _verifyValueWrite("start an object");
            ContainerFrame parent = _containerFrames.peek();
            SimpleStreamWriteContext childContext = _streamWriteContext
                    .createChildObjectContext(currentValue);
            streamWriteConstraints().validateNestingDepth(childContext.getNestingDepth());
            ObjectFrame frame = new ObjectFrame(parent, childContext, _arena);
            _containerFrames.push(frame);
            _streamWriteContext = childContext;
            return this;
        } catch (RuntimeException e) {
            throw fail(e);
        }
    }

    private void finishArray(ArrayFrame frame) {
        int count = frame.count;
        if (count == 0) {
            checkMetadataBudget(1L);
            frame.body.prepend(_arena.append((byte) VPackConstants.EMPTY_ARRAY));
            return;
        }

        if (VPackWriteFeature.WRITE_COMPACT_ARRAYS.enabledIn(_formatFeatures)) {
            finishCompact(frame.body, false, count);
            return;
        }

        boolean equal = VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS.enabledIn(_formatFeatures)
                && frame.equalSize >= 0L;
        if (equal) {
            VPackLayout.FixedCandidate candidate = VPackLayout.selectEqualWidth(frame.body.size());
            checkMetadataBudget(candidate.length() - frame.body.size());
            byte[] header = new byte[VPackBounds.checkedInt(candidate.bodyStart(),
                    "equal-array header length")];
            header[0] = (byte) candidate.marker();
            VPackBounds.writeBits(header, 1, candidate.width(), candidate.length());
            frame.body.prepend(_arena.append(header));
            return;
        }

        long[] offsets = Arrays.copyOf(frame.offsets, count);
        VPackLayout.FixedCandidate candidate = VPackLayout.selectIndexedWidth(false,
                frame.body.size(), count, offsets);
        long metadata = VPackBounds.checkedSubtract(candidate.length(), frame.body.size(),
                "array metadata");
        checkMetadataBudget(metadata);

        byte[] header;
        if (candidate.trailingCount()) {
            header = new byte[1 + candidate.width()];
            header[0] = (byte) candidate.marker();
            VPackBounds.writeBits(header, 1, candidate.width(), candidate.length());
        } else {
            header = new byte[1 + 2 * candidate.width()];
            header[0] = (byte) candidate.marker();
            VPackBounds.writeBits(header, 1, candidate.width(), candidate.length());
            VPackBounds.writeBits(header, 1 + candidate.width(), candidate.width(), count);
        }
        frame.body.prepend(_arena.append(header));

        int indexLength = VPackBounds.checkedInt(candidate.indexLength(), "array index length");
        byte[] index = new byte[indexLength];
        for (int i = 0; i < count; ++i) {
            long absoluteOffset = VPackBounds.checkedAdd(candidate.bodyStart(), offsets[i],
                    "array index offset");
            VPackBounds.writeBits(index, i * candidate.width(), candidate.width(), absoluteOffset);
        }
        frame.body.append(_arena.append(index));
        if (candidate.trailingCount()) {
            byte[] countBytes = new byte[candidate.width()];
            VPackBounds.writeBits(countBytes, 0, candidate.width(), count);
            frame.body.append(_arena.append(countBytes));
        }
    }

    private void finishCompact(VPackSegmentChain body, boolean object, int count) {
        VPackLayout.CompactCandidate candidate = VPackLayout.compactCandidate(
                object, body.size(), count);
        checkMetadataBudget(VPackBounds.checkedSubtract(candidate.length(), body.size(),
                "compact metadata"));

        byte[] lengthBytes = VPackVarInts.encodeForward(candidate.length());
        if (lengthBytes.length != candidate.lengthWidth()) {
            throw VPackErrors.write("compact length", "length width fixed point changed");
        }
        byte[] header = new byte[1 + lengthBytes.length];
        header[0] = (byte) candidate.marker();
        System.arraycopy(lengthBytes, 0, header, 1, lengthBytes.length);
        body.prepend(_arena.append(header));

        byte[] countBytes = VPackVarInts.encodeReverse(count);
        if (countBytes.length != candidate.countWidth()) {
            throw VPackErrors.write("compact count", "count width changed after layout selection");
        }
        body.append(_arena.append(countBytes));
    }

    private void checkMetadataBudget(long metadata) {
        if (metadata < 0L) {
            throw VPackErrors.write("array metadata", "metadata length is negative");
        }
        long retained = _arena.size();
        if (metadata > _vpackWriteConstraints.getMaxRootValueBytes() - retained) {
            throw VPackErrors.constraint("array metadata", retained,
                    "header and index exceed the configured root byte budget");
        }
    }

    private void outputRoot(VPackSegmentChain root) {
        try {
            root.writeTo(_out);
        } catch (IOException e) {
            throw fail(JacksonIOException.construct(e, this));
        }
        _bytesCopied += _arena.copiedBytes();
        root.clear();
        _arena.reset();
        _rootBudget.reset();
    }

    /** Test-only deterministic evidence for payload copies into the output arena. */
    long bytesCopied() {
        return _bytesCopied + _arena.copiedBytes();
    }

    /** Test-only deterministic evidence for O(1) child-chain transfers. */
    long segmentTransfers() {
        return _segmentTransfers;
    }

    /** Test evidence for current root entry, name-byte, and frame retention. */
    int retainedRootEntries() { return _rootBudget.entries(); }

    long retainedRootNameBytes() { return _rootBudget.nameBytes(); }

    int retainedContainerDepth() { return _containerFrames.size(); }

    private static abstract class ContainerFrame {
        final ContainerFrame parent;
        final SimpleStreamWriteContext streamContext;
        final VPackSegmentChain body;

        private ContainerFrame(ContainerFrame parent, SimpleStreamWriteContext streamContext,
                VPackOutputArena arena) {
            this.parent = parent;
            this.streamContext = streamContext;
            body = new VPackSegmentChain(arena);
        }
    }

    private static final class ArrayFrame extends ContainerFrame {
        private long[] offsets;
        private int count;
        private long equalSize = -1L;

        private ArrayFrame(ContainerFrame parent, SimpleStreamWriteContext streamContext,
                VPackOutputArena arena) {
            super(parent, streamContext, arena);
        }

        private void recordEntry(long size) {
            if (size <= 0L) {
                throw VPackErrors.write("array entry", "encoded child is empty");
            }
            if (offsets == null) {
                offsets = new long[8];
            } else if (count == offsets.length) {
                if (count > Integer.MAX_VALUE / 2) {
                    throw VPackErrors.write("array entries", "entry metadata is too large");
                }
                offsets = Arrays.copyOf(offsets, count * 2);
            }
            offsets[count++] = body.size() - size;
            if (equalSize == -1L) {
                equalSize = size;
            } else if (equalSize >= 0L && equalSize != size) {
                equalSize = -2L;
            }
        }
    }

    private static final class ObjectFrame extends ContainerFrame {
        private ObjectEntry[] entries;
        private int count;
        private ObjectEntry pending;

        private ObjectFrame(ContainerFrame parent, SimpleStreamWriteContext streamContext,
                VPackOutputArena arena) {
            super(parent, streamContext, arena);
        }

        private void reserveName(long keyOffset, byte[] resolvedUtf8) {
            if (pending != null) {
                throw VPackErrors.write("object name", "a value is required before the next name");
            }
            if (entries == null) {
                entries = new ObjectEntry[8];
            } else if (count == entries.length) {
                if (count > Integer.MAX_VALUE / 2) {
                    throw VPackErrors.write("object entries", "entry metadata is too large");
                }
                entries = Arrays.copyOf(entries, count * 2);
            }
            pending = entries[count++] = new ObjectEntry(keyOffset, resolvedUtf8);
        }

        private void recordValue() {
            if (pending == null) {
                throw VPackErrors.write("object value", "a property name is required before the value");
            }
            pending = null;
        }
    }

    private record ObjectEntry(long keyOffset, byte[] resolvedUtf8) { }

    private void recordValue(ContainerFrame frame, long size) {
        if (size <= 0L) {
            throw VPackErrors.write("container value", "encoded child is empty");
        }
        if (frame instanceof ArrayFrame array) {
            array.recordEntry(size);
        } else {
            ((ObjectFrame) frame).recordValue();
        }
    }

    private void writeObjectName(ObjectFrame frame, ResolvedName resolved) {
        _rootBudget.checkName(resolved.utf8().length);
        if (!frame.streamContext.writeName(resolved.text())) {
            throw _constructWriteException("Cannot write an object name, expecting a value");
        }
        VPackByteStore.Range encoded = _arena.append(resolved.wireBytes());
        long keyOffset = frame.body.size();
        frame.body.append(encoded);
        _rootBudget.chargeName(resolved.utf8().length);
        frame.reserveName(keyOffset, resolved.utf8());
    }

    private ResolvedName resolveName(String name) {
        if (name == null) {
            throw VPackErrors.write("object name", "name is null");
        }
        BigInteger id = null;
        if (_attributeNameCodec != null) {
            try {
                id = _attributeNameCodec.encode(name);
            } catch (RuntimeException e) {
                throw VPackErrors.write("compressed attribute name",
                        "attribute-name codec failed: " + e);
            }
        }
        return resolveName(name, id);
    }

    private ResolvedName resolveName(String name, BigInteger id) {
        if (name == null) {
            throw VPackErrors.write("object name", "name is null");
        }
        if (id != null) {
            if (_attributeNameCodec == null) {
                throw VPackErrors.write("compressed attribute name",
                        "no attribute-name codec is configured");
            }
            if (id.signum() < 0 || id.compareTo(VPackBounds.UINT64_MAX) > 0) {
                throw VPackErrors.write("compressed attribute name",
                        "attribute ID is outside the uint64 domain");
            }
            String decoded;
            try {
                decoded = _attributeNameCodec.decode(id);
            } catch (RuntimeException e) {
                throw VPackErrors.write("compressed attribute name",
                        "attribute-name codec failed: " + e);
            }
            if (!name.equals(decoded)) {
                throw VPackErrors.write("compressed attribute name",
                        "decode(encode(name)) did not return the original name");
            }
        }
        byte[] resolvedUtf8 = name.getBytes(StandardCharsets.UTF_8);
        _rootBudget.checkName(resolvedUtf8.length);
        byte[] wireBytes;
        if (id == null) {
            wireBytes = encodeString(resolvedUtf8, 0, resolvedUtf8.length, "object name");
        } else {
            wireBytes = encodeInteger(id);
        }
        return new ResolvedName(name, resolvedUtf8, wireBytes);
    }

    private void finishObject(ObjectFrame frame) {
        if (frame.pending != null) {
            throw VPackErrors.write("object end", "dangling property name without a value");
        }
        int count = frame.count;
        if (count == 0) {
            checkMetadataBudget(1L);
            frame.body.prepend(_arena.append((byte) 0x0A));
            return;
        }
        if (VPackWriteFeature.WRITE_COMPACT_OBJECTS.enabledIn(_formatFeatures)) {
            finishCompact(frame.body, true, count);
            return;
        }
        ObjectEntry[] sorted = Arrays.copyOf(frame.entries, count);
        Arrays.sort(sorted, Comparator.comparing(ObjectEntry::resolvedUtf8,
                Arrays::compareUnsigned));
        long[] offsets = new long[count];
        for (int i = 0; i < count; ++i) {
            offsets[i] = sorted[i].keyOffset();
        }
        VPackLayout.FixedCandidate candidate = VPackLayout.selectIndexedWidth(true,
                frame.body.size(), count, offsets);
        checkMetadataBudget(VPackBounds.checkedSubtract(candidate.length(), frame.body.size(),
                "object metadata"));
        byte[] header = new byte[VPackBounds.checkedInt(candidate.bodyStart(),
                "object header length")];
        header[0] = (byte) candidate.marker();
        VPackBounds.writeBits(header, 1, candidate.width(), candidate.length());
        VPackBounds.writeBits(header, 1 + candidate.width(), candidate.width(), count);
        frame.body.prepend(_arena.append(header));
        byte[] index = new byte[VPackBounds.checkedInt(candidate.indexLength(),
                "object index length")];
        for (int i = 0; i < count; ++i) {
            long absoluteOffset = VPackBounds.checkedAdd(candidate.bodyStart(), offsets[i],
                    "object index offset");
            VPackBounds.writeBits(index, i * candidate.width(), candidate.width(), absoluteOffset);
        }
        frame.body.append(_arena.append(index));
    }

    private record ResolvedName(String text, byte[] utf8, byte[] wireBytes) { }

    private JsonGenerator _unsupportedRaw(String operation) {
        return _staged(operation + " is unsupported for binary VPack output");
    }

    @SuppressWarnings("SameParameterValue") // Keep context-specific failure messages at callers.
    private byte[] encodeString(byte[] payload, int offset, int length, String context) {
        checkScalarSize(length, length <= 126L ? 1L : 9L, context);
        long end = VPackBounds.checkedAdd(offset, length, context + " range");
        int start = VPackBounds.checkedInt(offset, context + " offset");
        int finish = VPackBounds.checkedInt(end, context + " end");
        byte[] result = allocateStringFrame(length, context);
        System.arraycopy(payload, start, result, result.length - length, length);
        return result;
    }

    private byte[] allocateStringFrame(int length, String context) {
        long total = (long) length + (length <= 126 ? 1L : 9L);
        checkScalarSize(length, length <= 126 ? 1L : 9L, context);
        byte[] result = new byte[(int) total];
        result[0] = (byte) (length <= 126 ? 0x40 + length : 0xBF);
        if (length > 126) {
            VPackBounds.writeBits(result, 1, 8, length);
        }
        return result;
    }

    private byte[] encodeBinary(byte[] value, int offset, int length) {
        int width = binaryWidth(length);
        checkScalarSize(length, 1L + width, "binary");
        long total = (long) length + 1L + width;
        byte[] result = new byte[(int) total];
        result[0] = (byte) (0xC0 + width - 1);
        VPackBounds.writeUnsigned(result, 1, width, BigInteger.valueOf(length));
        System.arraycopy(value, offset, result, width + 1, length);
        return result;
    }

    private byte[] readBinary(InputStream input, int requestedLength) {
        if (input == null) {
            throw VPackErrors.write("binary stream", "input stream is null");
        }
        if (requestedLength >= 0) {
            checkScalarSize(requestedLength, 1L + binaryWidth(requestedLength),
                    "binary stream");
            byte[] payload = new byte[requestedLength];
            readExactly(input, payload, requestedLength);
            return payload;
        }

        checkScalarSize(0L, 2L, "binary stream");
        long maximumPayload = remainingRootBytes() - 2L;
        ByteArrayOutputStream collected = new ByteArrayOutputStream(
                (int) Math.min(4096L, maximumPayload));
        byte[] buffer = new byte[4096];
        while (true) {
            long remaining = maximumPayload - collected.size();
            if (remaining == 0L) {
                int extra;
                try {
                    extra = input.read();
                } catch (IOException e) {
                    throw JacksonIOException.construct(e, this);
                }
                if (extra < 0) {
                    break;
                }
                throw VPackErrors.constraint("binary stream", collected.size(),
                        "unknown-length binary exceeds the configured root byte budget");
            }
            int requested = (int) Math.min(buffer.length, remaining);
            int count;
            try {
                count = input.read(buffer, 0, requested);
            } catch (IOException e) {
                throw JacksonIOException.construct(e, this);
            }
            if (count < 0) {
                break;
            }
            if (count == 0) {
                int one;
                try {
                    one = input.read();
                } catch (IOException e) {
                    throw JacksonIOException.construct(e, this);
                }
                if (one < 0) {
                    break;
                }
                long length = (long) collected.size() + 1L;
                checkScalarSize(length, 1L + binaryWidth(length), "binary stream");
                collected.write(one);
            } else if (count <= requested) {
                long length = (long) collected.size() + count;
                checkScalarSize(length, 1L + binaryWidth(length), "binary stream");
                collected.write(buffer, 0, count);
            } else {
                throw VPackErrors.write("binary stream", "stream returned too many bytes");
            }
        }
        return collected.toByteArray();
    }

    private void readExactly(InputStream input, byte[] target, int length) {
        int offset = 0;
        while (offset < length) {
            int count;
            try {
                count = input.read(target, offset, length - offset);
            } catch (IOException e) {
                throw JacksonIOException.construct(e, this);
            }
            if (count < 0) {
                throw VPackErrors.write("binary stream",
                        "stream ended before the requested byte count");
            }
            if (count == 0) {
                int one;
                try {
                    one = input.read();
                } catch (IOException e) {
                    throw JacksonIOException.construct(e, this);
                }
                if (one < 0) {
                    throw VPackErrors.write("binary stream",
                            "stream ended before the requested byte count");
                }
                target[offset++] = (byte) one;
            } else if (count <= length - offset) {
                offset += count;
            } else {
                throw VPackErrors.write("binary stream", "stream returned too many bytes");
            }
        }
    }

    private int binaryWidth(long length) {
        if (length < 0L) {
            throw VPackErrors.write("binary", "length is negative");
        }
        return VPackNumbers.scalarLengthWidth(length);
    }

    private long remainingRootBytes() {
        ensureActive();
        return _vpackWriteConstraints.getMaxRootValueBytes() - _arena.size();
    }

    private long maxStringCharacters() {
        return Math.max(0L, remainingRootBytes() - 1L);
    }

    private void checkStringCharacters(long length) {
        if (length > maxStringCharacters()) {
            throw VPackErrors.constraint("string", length,
                    "UTF-16 character count exceeds the configured root byte budget");
        }
    }

    private void checkScalarSize(long payloadLength, long headerLength, String context) {
        ensureActive();
        if (payloadLength < 0L || headerLength < 0L
                || payloadLength > Long.MAX_VALUE - headerLength) {
            throw VPackErrors.write(context, "encoded scalar length overflows");
        }
        long total = payloadLength + headerLength;
        if (total > remainingRootBytes()) {
            throw VPackErrors.constraint(context, -1L,
                    "encoded scalar exceeds the configured root byte budget");
        }
    }

    /**
     * The shared simple write context reports an object name only while its
     * value is pending.  Jackson's pointer contract retains the name after the
     * value is written, so expose a read-only view with that contract while
     * keeping the mutable context private to the generator.
     */
    private static final class VPackWriteContextView extends TokenStreamContext {
        private final SimpleStreamWriteContext delegate;

        private VPackWriteContextView(SimpleStreamWriteContext delegate) {
            super(delegate.inArray() ? TYPE_ARRAY
                    : delegate.inRoot() ? TYPE_ROOT : TYPE_OBJECT,
                    delegate.hasCurrentIndex() ? delegate.getCurrentIndex() : -1);
            this.delegate = delegate;
            _nestingDepth = delegate.getNestingDepth();
        }

        @Override
        public TokenStreamContext getParent() {
            return delegate.getParent() == null ? null
                    : new VPackWriteContextView(delegate.getParent());
        }

        @Override
        public String currentName() {
            return delegate.currentName();
        }

        @Override
        public boolean hasCurrentName() {
            return delegate.currentName() != null;
        }

        @Override
        public boolean hasCurrentIndex() {
            return delegate.hasCurrentIndex();
        }

        @Override
        public Object currentValue() {
            return delegate.currentValue();
        }

        @Override
        public void assignCurrentValue(Object value) {
            delegate.assignCurrentValue(value);
        }
    }

    private void ensureActive() {
        if (_closed) {
            throw VPackErrors.write("generator", "generator is closed");
        }
        if (_failed) {
            if (_failure != null) throw VPackErrors.write("generator", "generator has failed");
            throw VPackErrors.write("generator", "generator has failed");
        }
    }

    private <T extends RuntimeException> T fail(T failure) {
        if (!_failed) {
            _failed = true;
            _failure = failure;
            releaseOwnedBuffers();
        }
        return failure;
    }

    private RuntimeException closeFailure() {
        if (_failure == null) {
            return null;
        }
        if (_failure instanceof StreamConstraintsException) {
            return new StreamConstraintsException("generator: generator has failed");
        }
        if (_failure instanceof StreamWriteException) {
            return new StreamWriteException(this, "generator has failed", _failure);
        }
        if (_failure instanceof JacksonIOException ioFailure && ioFailure.getCause() != null) {
            return JacksonIOException.construct(ioFailure.getCause(), this);
        }
        return new StreamWriteException(this, "generator has failed", _failure);
    }

    private void releaseOwnedBuffers() {
        for (ContainerFrame frame : _containerFrames) {
            frame.body.release();
        }
        _containerFrames.clear();
        _streamWriteContext = SimpleStreamWriteContext.createRootContext(null);
        _arena.release();
        _rootBudget.release();
    }

    private static RuntimeException addCleanupFailure(RuntimeException primary,
            RuntimeException cleanup) {
        if (primary == null) {
            return cleanup;
        }
        if (primary != cleanup) {
            primary.addSuppressed(cleanup);
        }
        return primary;
    }

    private static byte[] encodeInteger(long value) {
        if (value >= -6L && value <= 9L) {
            return new byte[] { (byte) (value < 0L ? 0x3A + value + 6L : 0x30 + value) };
        }
        if (value < 0L) {
            int width = signedWidth(value);
            byte[] result = new byte[1 + width];
            result[0] = (byte) (0x20 + width - 1);
            VPackBounds.writeSigned(result, 1, width, value);
            return result;
        }
        return encodeUnsigned(BigInteger.valueOf(value));
    }

    private static byte[] encodeInteger(BigInteger value) {
        if (value.compareTo(BigInteger.valueOf(-6L)) >= 0
                && value.compareTo(BigInteger.valueOf(9L)) <= 0) {
            return new byte[] { (byte) (value.signum() < 0
                    ? 0x3A + value.intValue() + 6 : 0x30 + value.intValue()) };
        }
        if (value.signum() < 0) {
            if (value.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
                throw VPackErrors.write("number", "integer outside the T20 signed range");
            }
            return encodeInteger(value.longValue());
        }
        return encodeUnsigned(value);
    }

    private static byte[] encodeUnsigned(BigInteger value) {
        int width = Math.max(1, (value.bitLength() + 7) / 8);
        byte[] result = new byte[1 + width];
        result[0] = (byte) (0x28 + width - 1);
        VPackBounds.writeUnsigned(result, 1, width, value);
        return result;
    }

    private static int signedWidth(long value) {
        for (int width = 1; width < 8; ++width) {
            int bits = width * 8;
            long minimum = -(1L << (bits - 1));
            if (value >= minimum) return width;
        }
        return 8;
    }
}
