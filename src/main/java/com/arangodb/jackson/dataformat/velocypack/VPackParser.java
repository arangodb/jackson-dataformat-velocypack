package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Deque;

import static com.arangodb.jackson.dataformat.velocypack.VPackConstants.*;

/**
 * {@link JsonParser} implementation for VelocyPack-encoded content.
 *<p>
 * Byte-array inputs are traversed in place. Stream inputs materialize one
 * stable root container at a time; nested frames share it and never reference
 * the refill buffer. Frames use absolute backing-array coordinates, including
 * an explicit type-byte origin for translating wire index offsets.
 */
public class VPackParser extends VPackParserBase
{
    /*
    /**********************************************************************
    /* Input source config, state
    /**********************************************************************
     */

    protected InputStream _inputStream;
    protected byte[] _inputBuffer;
    protected final boolean _bufferRecyclable;

    protected final ByteQuadsCanonicalizer _symbols;
    protected final boolean _symbolsCanonical;

    // Only names needing more than three quads use scratch. Grows on demand,
    // bounded by the configured name-byte limit; never retains input slices.
    private int[] _nameQuads;
    private final int _inputStart;
    private byte[] _containerHeader;

    /*
    /**********************************************************************
    /* Additional parsing state
    /**********************************************************************
     */

    /**
     * Stack of frames for nested containers. Top frame = innermost container.
     */
    protected final Deque<ParseFrame> _parseStack = new ArrayDeque<>();

    // Parser-local high-water storage, one frame per nesting depth. Jackson's
    // SimpleStreamReadContext already reuses its child context; no second pool.
    private final List<ParseFrame> _framesByDepth = new ArrayList<>();

    /**
     * The tag number from the most recently parsed tag (0xee/0xef), or -1 if none.
     */
    protected long _lastTagNumber = -1L;

    /**
     * Embedded object for VALUE_EMBEDDED_OBJECT tokens (custom types, minKey, maxKey).
     */
    protected Object _embeddedObject;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public VPackParser(ObjectReadContext readCtxt, IOContext ctxt,
            int parserFeatures, int vpackFeatures,
                       InputStream in, byte[] inputBuffer, int start, int end,
            boolean bufferRecyclable)
    {
        this(readCtxt, ctxt, parserFeatures, vpackFeatures, in, inputBuffer, start, end,
                bufferRecyclable, ByteQuadsCanonicalizer.createRoot()
                        .makeChildOrPlaceholder(TokenStreamFactory.Feature.collectDefaults()));
    }

    /** The caller transfers ownership of this parser's child symbol table. */
    public VPackParser(ObjectReadContext readCtxt, IOContext ctxt,
            int parserFeatures, int vpackFeatures,
            InputStream in, byte[] inputBuffer, int start, int end,
            boolean bufferRecyclable, ByteQuadsCanonicalizer symbols)
    {
        super(readCtxt, ctxt, parserFeatures, vpackFeatures);
        _symbols = symbols;
        _symbolsCanonical = symbols.isCanonicalizing();
        _inputStream = in;
        _inputBuffer = inputBuffer;
        _inputStart = start;
        _inputPtr = start;
        _inputEnd = end;
        _bufferRecyclable = bufferRecyclable;
    }

    /*
    /**********************************************************************
    /* Extended API: tag access
    /**********************************************************************
     */

    /**
     * Returns the tag number from the most recently parsed tagged value
     * ({@code 0xee}/{@code 0xef}), or {@code -1} if the current or last
     * value was not tagged.
     */
    public long getLastTagNumber() {
        return _lastTagNumber;
    }

    /*
    /**********************************************************************
    /* Input loading
    /**********************************************************************
     */

    protected boolean _loadMore() throws JacksonException {
        if (_inputStream != null) {
            try {
                int count = _inputStream.read(_inputBuffer, 0, _inputBuffer.length);
                if (count == 0) {
                    throw _constructReadException("InputStream returned zero bytes");
                }
                if (count > 0) {
                    _currInputProcessed += _inputEnd;
                    _inputPtr = 0;
                    _inputEnd = count;
                    return true;
                }
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            }
        }
        return false;
    }

    protected void _loadMoreGuaranteed() throws JacksonException {
        if (!_loadMore()) {
            _reportInvalidEOF(" in VPack value", _currToken);
        }
    }

    protected boolean _ensureAvailable(int needed) throws JacksonException {
        while (_inputEnd - _inputPtr < needed) {
            if (_inputStream == null) return false;
            // Try to load more data
            if (_inputPtr < _inputEnd) {
                // Move existing data to front
                int remaining = _inputEnd - _inputPtr;
                System.arraycopy(_inputBuffer, _inputPtr, _inputBuffer, 0, remaining);
                _currInputProcessed += _inputPtr;
                _inputPtr = 0;
                _inputEnd = remaining;
            } else {
                _currInputProcessed += _inputEnd;
                _inputPtr = 0;
                _inputEnd = 0;
            }
            try {
                int count = _inputStream.read(_inputBuffer, _inputEnd, _inputBuffer.length - _inputEnd);
                if (count == 0) throw _constructReadException("InputStream returned zero bytes");
                if (count < 0) return false;
                _inputEnd += count;
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            }
        }
        return true;
    }

    protected int _nextByte() throws JacksonException {
        if (_inputPtr >= _inputEnd) {
            _loadMoreGuaranteed();
        }
        _validateDocumentPosition(1);
        return _inputBuffer[_inputPtr++] & 0xFF;
    }

    protected long _readLeUnsigned(int numBytes) throws JacksonException {
        _validateDocumentPosition(numBytes);
        if (!_ensureAvailable(numBytes)) _reportInvalidEOF(" in VPack number", _currToken);
        long result = VPackUtil.readLeUnsigned(_inputBuffer, _inputPtr, numBytes);
        _inputPtr += numBytes;
        return result;
    }

    protected long _readLeSigned(int numBytes) throws JacksonException {
        _validateDocumentPosition(numBytes);
        if (!_ensureAvailable(numBytes)) _reportInvalidEOF(" in VPack number", _currToken);
        long result = VPackUtil.readLeSigned(_inputBuffer, _inputPtr, numBytes);
        _inputPtr += numBytes;
        return result;
    }


    /*
    /**********************************************************************
    /* Close / release
    /**********************************************************************
     */

    @Override
    protected void _closeInput() throws IOException {
        if (_inputStream != null) {
            InputStream source = _inputStream;
            _inputStream = null;
            if (_ioContext.isResourceManaged() || isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE)) {
                source.close();
            }
        }
    }

    @Override
    protected void _releaseBuffers2() {
        try {
            byte[] buf = _inputBuffer;
            _inputBuffer = null;
            if (_bufferRecyclable && buf != null) {
                _ioContext.releaseReadIOBuffer(buf);
            }
        } finally {
            for (ParseFrame frame : _framesByDepth) frame.clear();
            _framesByDepth.clear();
            _parseStack.clear();
            _containerHeader = null;
            _nameQuads = null;
            _symbols.release();
        }
    }

    @Override
    public boolean willInternPropertyNames() {
        return _symbols.willInternStrings();
    }

    @Override
    public Object streamReadInputSource() { return _inputStream; }

    /*
    /**********************************************************************
    /* Public API: generic traversal
    /**********************************************************************
     */

    @Override
    public JsonToken nextToken() throws JacksonException
    {
        if (_closed) {
            return null;
        }
        _resetTransientValueState();
        _tokenOffsetForTotal = _inputPtr;

        // Are we inside a container?
        if (!_parseStack.isEmpty()) {
            ParseFrame frame = _parseStack.peek();
            JsonToken t = frame.nextToken(this);
            if (t != null) {
                return _updateToken(t);
            }
            // Frame exhausted: pop it
            _parseStack.pop();
            boolean object = frame.isObject;
            frame.clear();
            if (object) {
                _streamReadContext = _streamReadContext.clearAndGetParent();
                return _updateToken(JsonToken.END_OBJECT);
            } else {
                _streamReadContext = _streamReadContext.clearAndGetParent();
                return _updateToken(JsonToken.END_ARRAY);
            }
        }

        // Root level: check for EOF
        if (_inputPtr >= _inputEnd && !_ensureAvailable(1)) {
            return _eofToken();
        }

        // Advance root context index for the next root-level value
        _streamReadContext.valueRead();
        JsonToken token = _readValue();
        _validateDocumentPosition(0);
        return token;
    }

    protected void _resetTransientValueState() {
        _numTypesValid = NR_UNKNOWN;
        _binaryValue = null;
        _embeddedObject = null;
        _lastTagNumber = -1L;
        _textBuffer.resetWithEmpty();
    }

    protected JsonToken _eofToken() throws JacksonException {
        _handleEOF();
        close();
        return _updateTokenToNull();
    }

    /**
     * Read the next VPack value and return the corresponding Jackson token.
     */
    protected JsonToken _readValue() throws JacksonException {
        int ch = _nextByte();
        return _readValueFromByte(ch);
    }

    protected JsonToken _readValueFromByte(int ch) throws JacksonException {
        while (ch == VPACK_TAG_1BYTE || ch == VPACK_TAG_8BYTE) {
            if (VPackReadFeature.FAIL_ON_TAGGED_VALUES.enabledIn(_formatFeatures)) {
                throw _constructReadException("Encountered tagged value but FAIL_ON_TAGGED_VALUES is enabled");
            }
            _lastTagNumber = ch == VPACK_TAG_1BYTE ? _nextByte() : _readLeUnsigned(8);
            ch = _nextByte();
        }

        // Dispatch on type byte
        if (ch == VPACK_NULL) {
            return _updateToken(JsonToken.VALUE_NULL);
        }
        if (ch == VPACK_FALSE) {
            return _updateToken(JsonToken.VALUE_FALSE);
        }
        if (ch == VPACK_TRUE) {
            return _updateToken(JsonToken.VALUE_TRUE);
        }
        if (ch == VPACK_ARRAY_EMPTY) {
            return _startEmptyArray();
        }
        if (ch >= VPACK_ARRAY_NO_IDX_FIRST && ch <= VPACK_ARRAY_IDX_LAST) {
            return _startArray(ch);
        }
        if (ch == VPACK_ARRAY_COMPACT) {
            return _startCompactArray();
        }
        if (ch == VPACK_OBJECT_EMPTY) {
            return _startEmptyObject();
        }
        if ((ch >= VPACK_OBJECT_SORTED_FIRST && ch <= VPACK_OBJECT_SORTED_LAST)
                || (ch >= VPACK_OBJECT_UNSORTED_FIRST && ch <= VPACK_OBJECT_UNSORTED_LAST)) {
            return _startObject(ch);
        }
        if (ch == VPACK_OBJECT_COMPACT) {
            return _startCompactObject();
        }
        if (ch == VPACK_DOUBLE) {
            return _readDouble();
        }
        if (ch == VPACK_DATE) {
            return _readDate();
        }
        if (ch >= VPACK_INT_SIGNED_FIRST && ch <= VPACK_INT_SIGNED_LAST) {
            return _readSignedInt(ch);
        }
        if (ch >= VPACK_INT_UNSIGNED_FIRST && ch <= VPACK_INT_UNSIGNED_LAST) {
            return _readUnsignedInt(ch);
        }
        if (ch >= VPACK_SMALL_INT_FIRST && ch <= VPACK_SMALL_INT_LAST) {
            _numberInt = ch - VPACK_SMALL_INT_FIRST;
            _numTypesValid = NR_INT;
            _numberType = NumberType.INT;
            return _updateToken(JsonToken.VALUE_NUMBER_INT);
        }
        if (ch >= VPACK_SMALL_NEG_FIRST && ch <= VPACK_SMALL_NEG_LAST) {
            _numberInt = ch - 0x40; // e.g. 0x3a - 0x40 = -6
            _numTypesValid = NR_INT;
            _numberType = NumberType.INT;
            return _updateToken(JsonToken.VALUE_NUMBER_INT);
        }
        if (ch >= VPACK_STRING_SHORT_FIRST && ch <= VPACK_STRING_SHORT_LAST) {
            return _readShortString(ch);
        }
        if (ch == VPACK_STRING_LONG) {
            return _readLongString();
        }
        if (ch >= VPACK_BINARY_FIRST && ch <= VPACK_BINARY_LAST) {
            return _readBinary(ch);
        }
        if (ch >= VPACK_BCD_POS_FIRST && ch <= VPACK_BCD_POS_LAST) {
            return _readBcdFloat(ch, false);
        }
        if (ch >= VPACK_BCD_NEG_FIRST && ch <= VPACK_BCD_NEG_LAST) {
            return _readBcdFloat(ch, true);
        }
        if (ch == VPACK_MIN_KEY) {
            _embeddedObject = "minKey";
            return _updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
        }
        if (ch == VPACK_MAX_KEY) {
            _embeddedObject = "maxKey";
            return _updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
        }
        if (ch >= VPACK_CUSTOM_FIRST && ch <= VPACK_CUSTOM_LAST) {
            return _readCustomType(ch);
        }
        // Illegal/rejected types
        if (ch == VPACK_NONE) {
            throw _constructReadException("Encountered type byte 0x00 (none/absent): illegal in VPack on-wire data");
        }
        if (ch == VPACK_ILLEGAL) {
            throw _constructReadException("Encountered type byte 0x17 (illegal): not a valid VPack value");
        }
        if (ch == VPACK_EXTERNAL) {
            throw _constructReadException("Encountered type byte 0x1d (external): not allowed in on-disk/on-wire VPack");
        }
        if (ch == VPACK_RESERVED_15 || ch == VPACK_RESERVED_16) {
            throw _constructReadException(String.format("Encountered reserved type byte 0x%02x", ch));
        }
        if (ch >= VPACK_RESERVED_D8 && ch <= VPACK_RESERVED_ED) {
            throw _constructReadException(String.format("Encountered reserved type byte 0x%02x", ch));
        }
        throw _constructReadException(String.format("Unrecognized VPack type byte 0x%02x", ch));
    }

    /*
    /**********************************************************************
    /* Token reading: arrays
    /**********************************************************************
     */

    protected JsonToken _startEmptyArray() throws JacksonException {
        return _startContainerRoot(VPACK_ARRAY_EMPTY);
    }

    protected JsonToken _startArray(int typeByte) throws JacksonException {
        return _startContainerRoot(typeByte);
    }

    protected JsonToken _startCompactArray() throws JacksonException {
        return _startContainerRoot(VPACK_ARRAY_COMPACT);
    }

    protected JsonToken _startEmptyObject() throws JacksonException {
        return _startContainerRoot(VPACK_OBJECT_EMPTY);
    }

    protected JsonToken _startObject(int typeByte) throws JacksonException {
        return _startContainerRoot(typeByte);
    }

    protected JsonToken _startCompactObject() throws JacksonException {
        return _startContainerRoot(VPACK_OBJECT_COMPACT);
    }

    private JsonToken _startContainerRoot(int tb) {
        _streamReadConstraints.validateNestingDepth(_parseStack.size() + 1);
        if (_inputStream == null) {
            int origin = _inputPtr - 1;
            int size = _valueByteSize(_inputBuffer, origin, _inputEnd);
            JsonToken token = _startContainer(_inputBuffer, origin, origin + size);
            _inputPtr = origin + size;
            return token;
        }
        if (tb == VPACK_ARRAY_EMPTY || tb == VPACK_OBJECT_EMPTY) {
            // Empty stream containers need no stable encoded storage.
            return _pushContainer(tb == VPACK_OBJECT_EMPTY, null, 0, 0, 0, 0, 0, 1, false);
        }
        byte[] stable = _readRootContainer(tb);
        return _startContainer(stable, 0, stable.length);
    }

    /** Read only this root, preserving its exact header. Grow only after more
     * bytes arrive, so a malicious declared length cannot cause a huge upfront
     * allocation. The refill buffer remains owned by IOContext throughout. */
    protected byte[] _readRootContainer(int tb) {
        if (_containerHeader == null) _containerHeader = new byte[10];
        _containerHeader[0] = (byte) tb;
        boolean compact = tb == VPACK_ARRAY_COMPACT || tb == VPACK_OBJECT_COMPACT;
        int header = 1;
        long length = 0;
        if (compact) {
            for (int shift = 0; ; shift += 7) {
                if (shift >= 63) throw _constructReadException("VPack VByte length overflow");
                int ch = _nextByte();
                _containerHeader[header++] = (byte) ch;
                length |= (long) (ch & 127) << shift;
                if ((ch & 128) == 0) break;
            }
        } else {
            int width = tb <= VPACK_ARRAY_IDX_LAST ? widthFromTypeByte_NoIdx(tb) : widthFromTypeByte_Obj(tb);
            for (int i = 0; i < width; i++) _containerHeader[header++] = (byte) _nextByte();
            length = VPackUtil.readLeUnsigned(_containerHeader, 1, width);
        }
        _validateDocLen(length);
        if (length <= header) throw _constructReadException("VPack container length too small");
        _validateDocumentPosition(length - header);
        int total = (int) length;
        byte[] stable = new byte[Math.min(total, Math.max(header, _inputBuffer.length))];
        System.arraycopy(_containerHeader, 0, stable, 0, header);
        int pos = header;
        while (pos < total) {
            if (!_ensureAvailable(1)) _reportInvalidEOF(" in VPack container", _currToken);
            if (pos == stable.length) {
                stable = _growContainerBuffer(stable, (int) Math.min(total, (long) stable.length * 2));
            }
            int count = Math.min(total - pos, Math.min(stable.length - pos, _inputEnd - _inputPtr));
            _copyContainerBytes(stable, pos, count);
            pos += count;
        }
        return stable;
    }

    protected byte[] _growContainerBuffer(byte[] buffer, int length) {
        return Arrays.copyOf(buffer, length);
    }

    protected void _copyContainerBytes(byte[] dest, int pos, int count) {
        System.arraycopy(_inputBuffer, _inputPtr, dest, pos, count);
        _inputPtr += count;
    }

    /** All positions are absolute indices into buf; end/contentEnd are exclusive.
     * Only on-wire index entries are relative, always to origin (the type byte).
     * A child is bounded by its parent's contentEnd, never by buf.length. */
    private JsonToken _startContainer(byte[] buf, int origin, int limit) {
        _streamReadConstraints.validateNestingDepth(_parseStack.size() + 1);
        int size = _valueByteSize(buf, origin, limit);
        int end = origin + size;
        int tb = buf[origin] & 255;
        boolean object = tb == VPACK_OBJECT_EMPTY || tb >= VPACK_OBJECT_SORTED_FIRST && tb <= VPACK_OBJECT_UNSORTED_LAST
                || tb == VPACK_OBJECT_COMPACT;
        if (tb == VPACK_ARRAY_EMPTY || tb == VPACK_OBJECT_EMPTY) {
            return _pushContainer(object, buf, origin, end, end, end, 0, 1, false);
        }
        boolean compact = tb == VPACK_ARRAY_COMPACT || tb == VPACK_OBJECT_COMPACT;
        int width = compact ? 1 : object ? widthFromTypeByte_Obj(tb) : widthFromTypeByte_NoIdx(tb);
        int start = origin + 1 + width;
        int contentEnd = end;
        int count = -1;
        boolean indexed = !compact && (object || tb >= VPACK_ARRAY_IDX_FIRST);
        if (compact) {
            _readVByte(buf, origin + 1, end, false);
            // Use the actual forward header width, including non-minimal encodings.
            start = origin + 1;
            while ((buf[start++] & 128) != 0) { }
            long items = _readVByte(buf, end - 1, start, true);
            contentEnd = end - 1;
            while ((buf[contentEnd--] & 128) != 0) { }
            contentEnd++;
            count = _checkedCount(items, contentEnd - start, object);
        } else if (indexed) {
            _requireRange(buf, width == 8 ? end - width : start, width, end);
            long items = VPackUtil.readLeUnsigned(buf, width == 8 ? end - width : start, width);
            if (width != 8) start += width;
            int metadataEnd = end - (width == 8 ? width : 0);
            if (items < 0 || items > (metadataEnd - start) / width) {
                throw _constructReadException("Invalid VPack item count/index size");
            }
            contentEnd = metadataEnd - (int) items * width;
            count = _checkedCount(items, contentEnd - start, object);
        }
        if (!compact) {
            // Optional padding can only occupy the unused bytes of the 9-byte header.
            int paddingEnd = origin + Math.min(9, contentEnd - origin);
            while (start < paddingEnd && buf[start] == 0) start++;
        }
        if (start > contentEnd || count == 0 && start != contentEnd) {
            throw _constructReadException("Invalid VPack container content bounds");
        }
        JsonToken token = _pushContainer(object, buf, origin, start, contentEnd, contentEnd, count, width, indexed);
        _parseStack.peek().containerEnd = end;
        return token;
    }

    private int _checkedCount(long count, int contentBytes, boolean object) {
        if (count < 0 || count > contentBytes / (object ? 2 : 1)) {
            throw _constructReadException("Invalid VPack container item count");
        }
        return (int) count;
    }

    private JsonToken _pushContainer(boolean object, byte[] buf, int origin, int start,
            int end, int indexStart, int count, int width, boolean indexed) {
        if (object) createChildObjectContext(); else createChildArrayContext();
        int depth = _parseStack.size();
        ParseFrame frame;
        if (depth == _framesByDepth.size()) {
            frame = new ParseFrame(object, buf, start, end, count, width, indexed);
            _framesByDepth.add(frame);
        } else {
            frame = _framesByDepth.get(depth);
            frame.reset(object, buf, start, end, count, width, indexed);
        }
        frame.origin = origin;
        frame.containerEnd = end;
        frame.contentStart = start;
        frame.indexStart = indexStart;
        _parseStack.push(frame);
        return _updateToken(object ? JsonToken.START_OBJECT : JsonToken.START_ARRAY);
    }

    /*
    /**********************************************************************
    /* Token reading: scalars
    /**********************************************************************
     */

    protected JsonToken _readDouble() throws JacksonException {
        long bits = _readLeUnsigned(8);
        _numberDouble = Double.longBitsToDouble(bits);
        _numTypesValid = NR_DOUBLE;
        _numberType = NumberType.DOUBLE;
        return _updateToken(JsonToken.VALUE_NUMBER_FLOAT);
    }

    protected JsonToken _readDate() throws JacksonException {
        // Expose as a number (ms since epoch) by default
        _numberLong = _readLeSigned(8);
        _numTypesValid = NR_LONG;
        _numberType = NumberType.LONG;
        return _updateToken(JsonToken.VALUE_NUMBER_INT);
    }

    protected JsonToken _readSignedInt(int typeByte) throws JacksonException {
        int w = typeByte - VPACK_INT_SIGNED_FIRST + 1; // 1..8
        long v = _readLeSigned(w);
        if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) {
            _numberInt = (int) v;
            _numTypesValid = NR_INT;
            _numberType = NumberType.INT;
        } else {
            _numberLong = v;
            _numTypesValid = NR_LONG;
            _numberType = NumberType.LONG;
        }
        return _updateToken(JsonToken.VALUE_NUMBER_INT);
    }

    protected JsonToken _readUnsignedInt(int typeByte) throws JacksonException {
        int w = typeByte - VPACK_INT_UNSIGNED_FIRST + 1; // 1..8
        long v = _readLeUnsigned(w);
        if (v >= 0 && v <= Integer.MAX_VALUE) {
            _numberInt = (int) v;
            _numTypesValid = NR_INT;
            _numberType = NumberType.INT;
        } else if (v >= 0) {
            _numberLong = v;
            _numTypesValid = NR_LONG;
            _numberType = NumberType.LONG;
        } else {
            // Unsigned 8-byte value that overflows signed long
            _numberBigInt = new BigInteger(Long.toUnsignedString(v));
            _numTypesValid = NR_BIGINT;
            _numberType = NumberType.BIG_INTEGER;
        }
        return _updateToken(JsonToken.VALUE_NUMBER_INT);
    }

    protected JsonToken _readShortString(int typeByte) throws JacksonException {
        int len = typeByte - VPACK_STRING_SHORT_FIRST;
        _validateStringLen(len);
        byte[] strBytes = _readBytes(len);
        _textBuffer.resetWithString(new String(strBytes, StandardCharsets.UTF_8));
        return _updateToken(JsonToken.VALUE_STRING);
    }

    protected JsonToken _readLongString() throws JacksonException {
        long len = _readLeUnsigned(8);
        _validateStringLen(len);
        byte[] strBytes = _readBytes((int) len);
        _textBuffer.resetWithString(new String(strBytes, StandardCharsets.UTF_8));
        return _updateToken(JsonToken.VALUE_STRING);
    }

    protected JsonToken _readBinary(int typeByte) throws JacksonException {
        int lenWidth = typeByte - VPACK_BINARY_FIRST + 1; // = typeByte - 0xbf
        long len = _readLeUnsigned(lenWidth);
        _validateBinaryLen(len);
        _binaryValue = _readBytes((int) len);
        return _updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
    }

    protected JsonToken _readBcdFloat(int typeByte, boolean negative) throws JacksonException {
        int mantLenWidth = negative
                ? typeByte - VPACK_BCD_NEG_FIRST + 1
                : typeByte - VPACK_BCD_POS_FIRST + 1;
        long mantLen = _readLeUnsigned(mantLenWidth);
        _validateBcdLength(mantLen);
        int exponent = (int) _readLeSigned(4);
        byte[] bcd = _readBytes((int) mantLen);
        java.math.BigDecimal bd = VPackUtil.decodeBcd(bcd, exponent, negative);
        if (bd.scale() == 0) {
            // Integral value (scale=0, no fractional part): return as BigInteger
            _numberBigInt = bd.toBigIntegerExact();
            _numTypesValid = NR_BIGINT;
            _numberType = NumberType.BIG_INTEGER;
            return _updateToken(JsonToken.VALUE_NUMBER_INT);
        }
        _numberBigDecimal = bd;
        _numTypesValid = NR_BIGDECIMAL;
        _numberType = NumberType.BIG_DECIMAL;
        return _updateToken(JsonToken.VALUE_NUMBER_FLOAT);
    }

    /** BCD packs two decimal digits per mantissa byte. */
    protected void _validateBcdLength(long mantLen) throws JacksonException {
        if (mantLen < 0 || mantLen > Integer.MAX_VALUE) {
            throw _constructReadException("BCD mantissa too large: " + mantLen);
        }
        long digits = mantLen * 2L;
        if (digits > Integer.MAX_VALUE) {
            throw _constructReadException("BCD number too large: " + digits + " digits");
        }
        _streamReadConstraints.validateFPLength((int) digits);
    }

    protected JsonToken _readCustomType(int typeByte) throws JacksonException {
        if (VPackReadFeature.FAIL_ON_CUSTOM_TYPES.enabledIn(_formatFeatures)) {
            throw _constructReadException(String.format(
                    "Encountered custom type byte 0x%02x but FAIL_ON_CUSTOM_TYPES is enabled", typeByte));
        }
        byte[] payload = _readCustomPayload(typeByte);
        _embeddedObject = new VPackCustomValue(typeByte, payload);
        return _updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
    }

    protected byte[] _readCustomPayload(int typeByte) throws JacksonException {
        if (typeByte == VPACK_CUSTOM_1B) return _readBytes(1);
        if (typeByte == VPACK_CUSTOM_2B) return _readBytes(2);
        if (typeByte == VPACK_CUSTOM_4B) return _readBytes(4);
        if (typeByte == VPACK_CUSTOM_8B) return _readBytes(8);
        if (typeByte >= VPACK_CUSTOM_LEN1_FIRST && typeByte <= VPACK_CUSTOM_LEN1_LAST) {
            int len = _nextByte();
            return _readBytes(len);
        }
        if (typeByte >= VPACK_CUSTOM_LEN2_FIRST && typeByte <= VPACK_CUSTOM_LEN2_LAST) {
            int len = (int) _readLeUnsigned(2);
            return _readBytes(len);
        }
        if (typeByte >= VPACK_CUSTOM_LEN4_FIRST && typeByte <= VPACK_CUSTOM_LEN4_LAST) {
            long len = _readLeUnsigned(4);
            _validateBinaryLen(len);
            return _readBytes((int) len);
        }
        // 8-byte length
        long len = _readLeUnsigned(8);
        if (len < 0 || len > Integer.MAX_VALUE) {
            throw _constructReadException("Custom type payload too large: " + len);
        }
        return _readBytes((int) len);
    }

    /*
    /**********************************************************************
    /* Property name handling
    /**********************************************************************
     */

    /**
     * Read a property name from a VPack string (or integer key) at the given
     * position in the buffer. Returns the Java string name and advances parsing.
     */
    protected String _readPropertyName(byte[] buf, int pos) throws JacksonException {
        int typeByte = buf[pos] & 0xFF;
        if (typeByte >= VPACK_STRING_SHORT_FIRST && typeByte <= VPACK_STRING_SHORT_LAST) {
            int len = typeByte - VPACK_STRING_SHORT_FIRST;
            return _findPropertyName(buf, pos + 1, len);
        }
        if (typeByte == VPACK_STRING_LONG) {
            long len = VPackUtil.readLeUnsigned(buf, pos + 1, 8);
            if (len < 0 || len > Integer.MAX_VALUE) {
                throw _constructReadException("VPack property name length out of range: " + len);
            }
            return _findPropertyName(buf, pos + 9, (int) len);
        }
        // Integer key (index into attribute name table): not commonly used
        // Represent as string for now
        if (typeByte >= VPACK_SMALL_INT_FIRST && typeByte <= VPACK_SMALL_INT_LAST) {
            return String.valueOf(typeByte - VPACK_SMALL_INT_FIRST);
        }
        if (typeByte >= VPACK_INT_UNSIGNED_FIRST && typeByte <= VPACK_INT_UNSIGNED_LAST) {
            int w = typeByte - VPACK_INT_UNSIGNED_FIRST + 1;
            long v = VPackUtil.readLeUnsigned(buf, pos + 1, w);
            return String.valueOf(v);
        }
        throw _constructReadException(String.format(
                "Invalid VPack object key type byte 0x%02x at position %d", typeByte, pos));
    }

    private String _findPropertyName(byte[] buf, int start, int len) {
        // Check even on hits: factory symbols may have been populated by a
        // parser with different read constraints. Jackson's UTF-8 name limit
        // is measured in encoded bytes.
        _streamReadConstraints.validateNameLength(len);
        if (start < 0 || len > buf.length - start) {
            throw _constructReadException("Truncated VPack property name");
        }
        if (!_symbolsCanonical) {
            return _decodePropertyName(buf, start, len);
        }

        // Jackson's UTF-8 parsers right-align the final data bytes and pad
        // unused high bytes with 0xff. That distinguishes lengths and NULs
        // for valid UTF-8. Preserve our existing replacement decoding of
        // malformed input too: escape an ambiguous 0xff-leading final quad
        // (or the reserved first quad) into a separate, length-tagged domain.
        if (len == 0) {
            String name = _symbols.findName(-1);
            return name == null ? _symbols.addName("", -1) : name;
        }
        int dataQuads = (len >>> 2) + ((len & 3) == 0 ? 0 : 1);
        int lastBytes = ((len - 1) & 3) + 1;
        int q1 = _nameQuad(buf, start, Math.min(len, 4));
        boolean escaped = q1 == -1 || buf[start + ((dataQuads - 1) << 2)] == (byte) 0xff;
        if (len <= 4) {
            if (escaped) {
                String name = _symbols.findName(-1, len, q1);
                return name == null ? _symbols.addName(
                        _decodePropertyName(buf, start, len), -1, len, q1) : name;
            }
            q1 = _padNameQuad(q1, lastBytes);
            String name = _symbols.findName(q1);
            return name == null ? _symbols.addName(
                    _decodePropertyName(buf, start, len), q1) : name;
        }
        int q2 = _nameQuad(buf, start + 4, Math.min(len - 4, 4));
        if (!escaped && len <= 8) {
            q2 = _padNameQuad(q2, lastBytes);
            String name = _symbols.findName(q1, q2);
            return name == null ? _symbols.addName(
                    _decodePropertyName(buf, start, len), q1, q2) : name;
        }
        int q3 = len > 8 ? _nameQuad(buf, start + 8, Math.min(len - 8, 4)) : 0;
        if (!escaped && len <= 12) {
            q3 = _padNameQuad(q3, lastBytes);
            String name = _symbols.findName(q1, q2, q3);
            return name == null ? _symbols.addName(
                    _decodePropertyName(buf, start, len), q1, q2, q3) : name;
        }
        int prefix = escaped ? 2 : 0;
        int count = dataQuads + prefix;
        if (_nameQuads == null || _nameQuads.length < count) {
            int maxQuads = (_streamReadConstraints.getMaxNameLength() >>> 2) + 3;
            int grown = _nameQuads == null ? 16 : _nameQuads.length * 2;
            _nameQuads = new int[Math.min(maxQuads, Math.max(count, grown))];
        }
        if (escaped) {
            _nameQuads[0] = -1;
            _nameQuads[1] = len;
        }
        _nameQuads[prefix] = q1;
        _nameQuads[prefix + 1] = q2;
        if (dataQuads > 2) {
            _nameQuads[prefix + 2] = q3;
        }
        for (int i = 3, offset = 12; i < dataQuads; i++, offset += 4) {
            _nameQuads[prefix + i] = _nameQuad(buf, start + offset, Math.min(len - offset, 4));
        }
        if (!escaped) {
            _nameQuads[count - 1] = _padNameQuad(_nameQuads[count - 1], lastBytes);
        }
        String name = _symbols.findName(_nameQuads, count);
        return name == null ? _symbols.addName(
                _decodePropertyName(buf, start, len), _nameQuads, count) : name;
    }

    // Same padding as the resolved 3.2.0 UTF8StreamJsonParser. The escaped
    // namespace above makes it injective even for our malformed-input path.
    private static int _padNameQuad(int quad, int bytes) {
        return bytes == 4 ? quad : quad | (-1 << (bytes << 3));
    }

    /** Decoding is deliberately isolated for allocation diagnostics. */
    protected String _decodePropertyName(byte[] buf, int start, int len) {
        return new String(buf, start, len, StandardCharsets.UTF_8);
    }

    private static int _nameQuad(byte[] buf, int start, int bytes) {
        int quad = buf[start] & 0xff;
        if (bytes > 1) {
            quad = (quad << 8) | (buf[start + 1] & 0xff);
            if (bytes > 2) {
                quad = (quad << 8) | (buf[start + 2] & 0xff);
                if (bytes > 3) {
                    quad = (quad << 8) | (buf[start + 3] & 0xff);
                }
            }
        }
        return quad;
    }

    /**
     * Return the byte size of a VPack value starting at {@code buf[pos]}.
     */
    protected int _valueByteSize(byte[] buf, int pos) throws JacksonException {
        return _valueByteSize(buf, pos, buf.length);
    }

    private int _valueByteSize(byte[] buf, int pos, int limit) {
        int original = pos;
        while (true) {
            _requireRange(buf, pos, 1, limit);
            int tb = buf[pos] & 255;
            if (tb != VPACK_TAG_1BYTE && tb != VPACK_TAG_8BYTE) break;
            int skip = tb == VPACK_TAG_1BYTE ? 2 : 9;
            _requireRange(buf, pos, skip, limit);
            pos += skip;
        }
        int tb = buf[pos] & 255;
        long size = 1;
        int width = 0;
        int header = 0;
        boolean container = false;
        if (tb >= VPACK_INT_SIGNED_FIRST && tb <= VPACK_INT_UNSIGNED_LAST) size = 2 + (tb & 7);
        else if (tb == VPACK_DOUBLE || tb == VPACK_DATE) size = 9;
        else if (tb >= VPACK_STRING_SHORT_FIRST && tb <= VPACK_STRING_SHORT_LAST) size = 1 + tb - VPACK_STRING_SHORT_FIRST;
        else if (tb == VPACK_STRING_LONG) { width = 8; header = 9; }
        else if (tb >= VPACK_BINARY_FIRST && tb <= VPACK_BINARY_LAST) { width = tb - VPACK_BINARY_FIRST + 1; header = 1 + width; }
        else if (tb >= VPACK_BCD_POS_FIRST && tb <= VPACK_BCD_NEG_LAST) { width = 1 + (tb & 7); header = 5 + width; }
        else if (tb >= VPACK_ARRAY_NO_IDX_FIRST && tb <= VPACK_ARRAY_IDX_LAST) {
            width = widthFromTypeByte_NoIdx(tb); header = 1 + width; container = true;
        } else if (tb >= VPACK_OBJECT_SORTED_FIRST && tb <= VPACK_OBJECT_UNSORTED_LAST) {
            width = widthFromTypeByte_Obj(tb); header = 1 + width; container = true;
        } else if (tb == VPACK_ARRAY_COMPACT || tb == VPACK_OBJECT_COMPACT) {
            size = _readVByte(buf, pos + 1, limit, false);
            if (size <= 2) throw _constructReadException("VPack compact container too small");
        } else if (tb >= VPACK_CUSTOM_1B && tb <= VPACK_CUSTOM_8B) size = 1 + (1 << (tb - VPACK_CUSTOM_1B));
        else if (tb >= VPACK_CUSTOM_LEN1_FIRST && tb <= VPACK_CUSTOM_LAST) {
            width = 1 << ((tb - VPACK_CUSTOM_LEN1_FIRST) / 3); header = 1 + width;
        }
        if (width != 0) {
            _requireRange(buf, pos, header, limit);
            long length = VPackUtil.readLeUnsigned(buf, pos + 1, width);
            if (length < 0 || length > Integer.MAX_VALUE) throw _constructReadException("VPack length out of range");
            if (container && length <= header) throw _constructReadException("VPack container length too small");
            size = container ? length : header + length;
        }
        if (size > Integer.MAX_VALUE) throw _constructReadException("VPack value too large");
        _requireRange(buf, pos, (int) size, limit);
        return pos - original + (int) size;
    }

    private void _requireRange(byte[] buf, int pos, int size, int limit) {
        if (pos < 0 || size < 0 || limit < pos || limit > buf.length || size > limit - pos) {
            throw _constructReadException("Truncated or out-of-bounds VPack value");
        }
    }

    /** Bounded VBytes have at most nine groups (63 nonnegative bits). */
    private long _readVByte(byte[] buf, int pos, int bound, boolean reverse) {
        long result = 0;
        for (int shift = 0; shift < 63; shift += 7) {
            if (reverse ? pos < bound : pos >= bound) throw _constructReadException("Truncated VPack VByte");
            int ch = buf[pos] & 255;
            result |= (long) (ch & 127) << shift;
            if ((ch & 128) == 0) return result;
            pos += reverse ? -1 : 1;
        }
        throw _constructReadException("VPack VByte overflow");
    }

    /*
    /**********************************************************************
    /* String accessors
    /**********************************************************************
     */

    @Override
    public boolean hasStringCharacters() {
        return _currToken == JsonToken.VALUE_STRING || _currToken == JsonToken.PROPERTY_NAME;
    }

    @Override
    public String getString() throws JacksonException {
        if (_currToken == null) {
            return null;
        }
        return switch (_currToken) {
            case VALUE_STRING, PROPERTY_NAME -> _textBuffer.contentsAsString();
            case VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT -> _numberAsString();
            case VALUE_TRUE -> "true";
            case VALUE_FALSE -> "false";
            case VALUE_NULL -> "null";
            default -> _currToken.asString();
        };
    }

    @Override
    public char[] getStringCharacters() throws JacksonException {
        if (_currToken == JsonToken.VALUE_STRING || _currToken == JsonToken.PROPERTY_NAME) {
            return _textBuffer.getTextBuffer();
        }
        String s = getString();
        return (s == null) ? null : s.toCharArray();
    }

    @Override
    public int getStringLength() throws JacksonException {
        if (_currToken == JsonToken.VALUE_STRING || _currToken == JsonToken.PROPERTY_NAME) {
            return _textBuffer.size();
        }
        String s = getString();
        return (s == null) ? 0 : s.length();
    }

    @Override
    public int getStringOffset() throws JacksonException {
        if (_currToken == JsonToken.VALUE_STRING || _currToken == JsonToken.PROPERTY_NAME) {
            return _textBuffer.getTextOffset();
        }
        return 0;
    }

    @Override
    public String getValueAsString() throws JacksonException {
        return getValueAsString(null);
    }

    @Override
    public String getValueAsString(String defaultValue) throws JacksonException {
        if (_currToken == JsonToken.VALUE_STRING || _currToken == JsonToken.PROPERTY_NAME) {
            return _textBuffer.contentsAsString();
        }
        if (_currToken == null || _currToken == JsonToken.VALUE_NULL) {
            return defaultValue;
        }
        // For numbers/booleans, fall back to the textual representation
        String s = getString();
        return (s == null) ? defaultValue : s;
    }

    @Override
    public int getString(Writer writer) throws JacksonException {
        if (_currToken == JsonToken.VALUE_NULL) return 0;
        String str = getString();
        if (str == null) return 0;
        try {
            writer.write(str);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return str.length();
    }

    protected String _numberAsString() throws JacksonException {
        if ((_numTypesValid & NR_INT) != 0)        return Integer.toString(_numberInt);
        if ((_numTypesValid & NR_LONG) != 0)       return Long.toString(_numberLong);
        if ((_numTypesValid & NR_BIGINT) != 0)     return _numberBigInt.toString();
        if ((_numTypesValid & NR_BIGDECIMAL) != 0) return _numberBigDecimal.toString();
        if ((_numTypesValid & NR_DOUBLE) != 0)     return Double.toString(_numberDouble);
        return "";
    }

    /*
    /**********************************************************************
    /* Binary value accessors
    /**********************************************************************
     */

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant) throws JacksonException {
        if (_currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
            return _binaryValue;
        }
        if (_currToken == JsonToken.VALUE_STRING) {
            // Try base64 decode
            try {
                return b64variant.decode(_textBuffer.contentsAsString());
            } catch (Exception e) {
                throw _constructReadException("Cannot decode binary from string: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public Object getEmbeddedObject() throws JacksonException {
        if (_currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
            if (_embeddedObject != null) return _embeddedObject;
            return _binaryValue;
        }
        return null;
    }

    @Override
    public int readBinaryValue(Base64Variant b64variant, OutputStream out)
            throws JacksonException
    {
        byte[] data = getBinaryValue(b64variant);
        if (data == null) return 0;
        try {
            out.write(data);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return data.length;
    }

    /*
    /**********************************************************************
    /* Context creation
    /**********************************************************************
     */

    private void createChildArrayContext() throws JacksonException {
        _streamReadContext = _streamReadContext.createChildArrayContext(-1, -1);
        _streamReadConstraints.validateNestingDepth(_streamReadContext.getNestingDepth());
    }

    private void createChildObjectContext() throws JacksonException {
        _streamReadContext = _streamReadContext.createChildObjectContext(-1, -1);
        _streamReadConstraints.validateNestingDepth(_streamReadContext.getNestingDepth());
    }

    /*
    /**********************************************************************
    /* Helpers
    /**********************************************************************
     */

    protected byte[] _readBytes(int len) throws JacksonException {
        _validateBinaryLen(len);
        _validateDocumentPosition(len);
        if (_inputStream == null && len > _inputEnd - _inputPtr) _reportInvalidEOF(" in VPack payload", _currToken);
        // Streams also grow only as bytes arrive, avoiding declared-length allocations.
        if (_inputStream != null && len > _inputBuffer.length) {
            byte[] result = new byte[Math.min(len, _inputBuffer.length)];
            int pos = 0;
            while (pos < len) {
                if (!_ensureAvailable(1)) _reportInvalidEOF(" in VPack payload", _currToken);
                if (pos == result.length) result = Arrays.copyOf(result, (int) Math.min(len, (long) result.length * 2));
                int count = Math.min(len - pos, Math.min(result.length - pos, _inputEnd - _inputPtr));
                System.arraycopy(_inputBuffer, _inputPtr, result, pos, count);
                _inputPtr += count;
                pos += count;
            }
            return result;
        }
        byte[] result = new byte[len];
        _readBytesInto(result, len);
        return result;
    }

    protected void _readBytesInto(byte[] dest, int len) throws JacksonException {
        int pos = 0;
        while (pos < len) {
            int avail = _inputEnd - _inputPtr;
            if (avail <= 0) {
                _loadMoreGuaranteed();
                avail = _inputEnd - _inputPtr;
            }
            int copy = Math.min(len - pos, avail);
            System.arraycopy(_inputBuffer, _inputPtr, dest, pos, copy);
            _inputPtr += copy;
            pos += copy;
        }
    }

    /**
     * Read a forward VByte from the input stream, populating outLen[0] with the number
     * of bytes consumed.
     */
    protected long _readVByteFromStream(int[] outLen) throws JacksonException {
        long result = 0L;
        int shift = 0;
        int consumed = 0;
        while (true) {
            if (shift >= 63) throw _constructReadException("VPack VByte overflow");
            int b = _nextByte();
            consumed++;
            result |= (long)(b & 0x7F) << shift;
            shift += 7;
            if ((b & 0x80) == 0) break;
        }
        outLen[0] = consumed;
        return result;
    }

    private void _validateDocumentPosition(long additional) {
        _streamReadConstraints.validateDocumentLength(_currInputProcessed + _inputPtr - _inputStart + additional);
    }

    protected void _validateDocLen(long len) throws JacksonException {
        // Basic sanity
        if (len < 0 || len > Integer.MAX_VALUE) {
            throw _constructReadException("VPack value byte length out of range: " + len);
        }
    }

    protected void _validateStringLen(long len) throws JacksonException {
        if (len < 0 || len > Integer.MAX_VALUE) {
            throw _constructReadException("VPack string length out of range: " + len);
        }
        _streamReadConstraints.validateStringLength((int) len);
    }

    protected void _validateBinaryLen(long len) throws JacksonException {
        if (len < 0 || len > Integer.MAX_VALUE) {
            throw _constructReadException("VPack binary length out of range: " + len);
        }
    }

    /**
     * Return byte width for array types 0x02-0x09 (both with and without index table).
     * Per spec: 0x02/0x06=1-byte, 0x03/0x07=2-byte, 0x04/0x08=4-byte, 0x05/0x09=8-byte.
     */
    protected static int widthFromTypeByte_NoIdx(int tb) {
        // The low 2 bits relative to the base give the width index
        // 0x02,0x06 → low 2 bits = 2 → but we want 1-byte...
        // Pattern: 0x02=1, 0x03=2, 0x04=4, 0x05=8, 0x06=1, 0x07=2, 0x08=4, 0x09=8
        // Both ranges start at even type bytes; within range, offset 0=1B,1=2B,2=4B,3=8B
        int base = (tb <= VPACK_ARRAY_NO_IDX_LAST) ? VPACK_ARRAY_NO_IDX_FIRST : VPACK_ARRAY_IDX_FIRST;
        int idx = tb - base;
        if (idx == 0) return 1;
        if (idx == 1) return 2;
        if (idx == 2) return 4;
        return 8;
    }

    protected static int widthFromTypeByte_Obj(int tb) {
        // 0x0b-0x0e: sorted, w=1/2/4/8
        // 0x0f-0x12: unsorted, w=1/2/4/8
        int base = (tb < VPACK_OBJECT_UNSORTED_FIRST) ? VPACK_OBJECT_SORTED_FIRST : VPACK_OBJECT_UNSORTED_FIRST;
        int idx = tb - base;
        if (idx == 0) return 1;
        if (idx == 1) return 2;
        if (idx == 2) return 4;
        return 8;
    }

    /*
    /**********************************************************************
    /* Inner class: ParseFrame
    /**********************************************************************
     */

    /**
     * Tracks parsing state within an open array or object container.
     * Positions are absolute indices into the stable shared {@code buf}.
     * {@code origin} is the original type byte; content bounds exclude metadata.
     */
    protected class ParseFrame
    {
        boolean isObject;
        byte[] buf;
        int contentEnd;   // exclusive end of item content
        int nItems;       // number of items/pairs
        int w;            // width for offsets
        boolean hasIndex; // has explicit index table

        int origin, containerEnd, contentStart, indexStart;
        int currentIndex = 0;   // which item we're at (0-based)
        int currentPos;         // current byte position in buf
        boolean expectingValue = false; // for objects: true when expecting value

        ParseFrame(boolean isObject, byte[] buf, int start, int end, int n) {
            this(isObject, buf, start, end, n, 1, false);
        }

        ParseFrame(boolean isObject, byte[] buf, int start, int end,
                int nItems, int w, boolean hasIndex) {
            reset(isObject, buf, start, end, nItems, w, hasIndex);
        }

        void reset(boolean object, byte[] buffer, int start, int end,
                int count, int width, boolean indexed) {
            clear();
            isObject = object;
            buf = buffer;
            contentStart = start;
            contentEnd = end;
            nItems = count;
            w = width;
            hasIndex = indexed;
            currentPos = start;
        }

        void clear() {
            buf = null;
            isObject = hasIndex = expectingValue = false;
            origin = containerEnd = contentStart = contentEnd = indexStart = 0;
            currentIndex = currentPos = nItems = w = 0;
        }

        boolean isDone() {
            // For no-index arrays (nItems == -1), done when we've scanned all content
            if (nItems < 0) {
                return currentPos >= contentEnd;
            }
            return currentIndex >= nItems && !expectingValue;
        }

        /**
         * Return the next token for this frame, or null if the container is exhausted.
         */
        JsonToken nextToken(VPackParser parser) throws JacksonException {
            if (isDone()) {
                if (!hasIndex && currentPos != contentEnd) throw parser._constructReadException("VPack item count does not match content");
                return null;
            }

            parser._resetTransientValueState();

            if (isObject) {
                return nextObjectToken(parser);
            } else {
                return nextArrayToken(parser);
            }
        }

        JsonToken nextArrayToken(VPackParser parser) throws JacksonException {
            if (isDone()) return null;

            // Navigate to the current item's position
            int itemPos = getItemPos(currentIndex);
            if (itemPos < 0 || itemPos >= contentEnd) {
                throw parser._constructReadException("Invalid VPack item offset");
            }

            int valueSize = parser._valueByteSize(buf, itemPos, contentEnd);
            int tb = buf[itemPos] & 0xFF;
            currentIndex++;
            // Advance currentPos (for sequential access)
            if (!hasIndex) {
                currentPos = itemPos + valueSize;
            }

            // Advance the array's stream-read-context index so that
            // error reporting / JsonStreamContext can report the index.
            _streamReadContext.valueRead();

            return parseValueInBuf(parser, tb, itemPos);
        }

        JsonToken nextObjectToken(VPackParser parser) throws JacksonException {
            if (isDone()) return null;

            if (!expectingValue) {
                // Read the key
                int pairPos = getPairPos(currentIndex);
                if (pairPos < 0 || pairPos >= contentEnd) {
                    throw parser._constructReadException("Invalid VPack pair offset");
                }
                int keySize = parser._valueByteSize(buf, pairPos, contentEnd);
                String name = parser._readPropertyName(buf, pairPos);
                // Update streamReadContext with name
                _streamReadContext.setCurrentName(name);
                _textBuffer.resetWithString(name);
                // Advance position to value
                currentPos = pairPos + keySize;
                expectingValue = true;
                return JsonToken.PROPERTY_NAME;
            } else {
                // Read the value
                int valuePos = currentPos;
                int valueSize = parser._valueByteSize(buf, valuePos, contentEnd);
                int tb = buf[valuePos] & 0xFF;
                currentPos = valuePos + valueSize;
                currentIndex++;
                expectingValue = false;
                // Account for the value just consumed within this object context
                _streamReadContext.valueRead();
                return parseValueInBuf(parser, tb, valuePos);
            }
        }

        int getItemPos(int idx) {
            if (!hasIndex) return currentPos;
            long offset = VPackUtil.readLeUnsigned(buf, indexStart + idx * w, w);
            if (offset < contentStart - origin || offset >= contentEnd - origin) {
                throw _constructReadException("VPack index offset outside container content");
            }
            return origin + (int) offset;
        }

        int getPairPos(int idx) {
            return getItemPos(idx);
        }

        /**
         * Parse a VPack value from the internal buf at position pos and return the token.
         * Nested containers push frames; traversal never recurses through their contents.
         */
        JsonToken parseValueInBuf(VPackParser parser, int tb, int pos) throws JacksonException {
            while (tb == VPACK_TAG_1BYTE || tb == VPACK_TAG_8BYTE) {
                if (VPackReadFeature.FAIL_ON_TAGGED_VALUES.enabledIn(_formatFeatures)) {
                    throw parser._constructReadException("Encountered tagged value but FAIL_ON_TAGGED_VALUES enabled");
                }
                _lastTagNumber = tb == VPACK_TAG_1BYTE ? buf[pos + 1] & 255 : VPackUtil.readLeUnsigned(buf, pos + 1, 8);
                pos += tb == VPACK_TAG_1BYTE ? 2 : 9;
                tb = buf[pos] & 255;
            }

            if (tb == VPACK_NULL) return parser._updateToken(JsonToken.VALUE_NULL);
            if (tb == VPACK_FALSE) return parser._updateToken(JsonToken.VALUE_FALSE);
            if (tb == VPACK_TRUE) return parser._updateToken(JsonToken.VALUE_TRUE);

            if (tb >= VPACK_SMALL_INT_FIRST && tb <= VPACK_SMALL_INT_LAST) {
                _numTypesValid = NR_INT;
                _numberInt = tb - VPACK_SMALL_INT_FIRST;
                _numberType = NumberType.INT;
                return parser._updateToken(JsonToken.VALUE_NUMBER_INT);
            }
            if (tb >= VPACK_SMALL_NEG_FIRST && tb <= VPACK_SMALL_NEG_LAST) {
                _numTypesValid = NR_INT;
                _numberInt = tb - 0x40;
                _numberType = NumberType.INT;
                return parser._updateToken(JsonToken.VALUE_NUMBER_INT);
            }
            if (tb >= VPACK_INT_SIGNED_FIRST && tb <= VPACK_INT_SIGNED_LAST) {
                int bw = tb - VPACK_INT_SIGNED_FIRST + 1;
                long v = VPackUtil.readLeSigned(buf, pos + 1, bw);
                if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) {
                    _numTypesValid = NR_INT; _numberInt = (int) v; _numberType = NumberType.INT;
                } else {
                    _numTypesValid = NR_LONG; _numberLong = v; _numberType = NumberType.LONG;
                }
                return parser._updateToken(JsonToken.VALUE_NUMBER_INT);
            }
            if (tb >= VPACK_INT_UNSIGNED_FIRST && tb <= VPACK_INT_UNSIGNED_LAST) {
                int bw = tb - VPACK_INT_UNSIGNED_FIRST + 1;
                long v = VPackUtil.readLeUnsigned(buf, pos + 1, bw);
                if (v >= 0 && v <= Integer.MAX_VALUE) {
                    _numTypesValid = NR_INT; _numberInt = (int) v; _numberType = NumberType.INT;
                } else if (v >= 0) {
                    _numTypesValid = NR_LONG; _numberLong = v; _numberType = NumberType.LONG;
                } else {
                    _numTypesValid = NR_BIGINT;
                    _numberBigInt = new BigInteger(Long.toUnsignedString(v));
                    _numberType = NumberType.BIG_INTEGER;
                }
                return parser._updateToken(JsonToken.VALUE_NUMBER_INT);
            }
            if (tb == VPACK_DOUBLE) {
                long bits = VPackUtil.readLeUnsigned(buf, pos + 1, 8);
                _numTypesValid = NR_DOUBLE;
                _numberDouble = Double.longBitsToDouble(bits);
                _numberType = NumberType.DOUBLE;
                return parser._updateToken(JsonToken.VALUE_NUMBER_FLOAT);
            }
            if (tb == VPACK_DATE) {
                long ms = VPackUtil.readLeSigned(buf, pos + 1, 8);
                _numTypesValid = NR_LONG; _numberLong = ms; _numberType = NumberType.LONG;
                return parser._updateToken(JsonToken.VALUE_NUMBER_INT);
            }
            if (tb >= VPACK_STRING_SHORT_FIRST && tb <= VPACK_STRING_SHORT_LAST) {
                int len = tb - VPACK_STRING_SHORT_FIRST;
                parser._validateStringLen(len);
                _textBuffer.resetWithString(new String(buf, pos + 1, len, StandardCharsets.UTF_8));
                return parser._updateToken(JsonToken.VALUE_STRING);
            }
            if (tb == VPACK_STRING_LONG) {
                long len = VPackUtil.readLeUnsigned(buf, pos + 1, 8);
                parser._validateStringLen(len);
                _textBuffer.resetWithString(new String(buf, pos + 9, (int) len, StandardCharsets.UTF_8));
                return parser._updateToken(JsonToken.VALUE_STRING);
            }
            if (tb >= VPACK_BINARY_FIRST && tb <= VPACK_BINARY_LAST) {
                int lw = tb - VPACK_BINARY_FIRST + 1;
                long len = VPackUtil.readLeUnsigned(buf, pos + 1, lw);
                parser._validateBinaryLen(len);
                _binaryValue = Arrays.copyOfRange(buf, pos + 1 + lw, pos + 1 + lw + (int) len);
                return parser._updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
            }
            if (tb >= VPACK_BCD_POS_FIRST && tb <= VPACK_BCD_POS_LAST) {
                return parseBcdInBuf(parser, tb, pos, false);
            }
            if (tb >= VPACK_BCD_NEG_FIRST && tb <= VPACK_BCD_NEG_LAST) {
                return parseBcdInBuf(parser, tb, pos, true);
            }
            if (tb == VPACK_ARRAY_EMPTY) {
                return parser._startContainer(buf, pos, contentEnd);
            }
            if (tb >= VPACK_ARRAY_NO_IDX_FIRST && tb <= VPACK_ARRAY_IDX_LAST) {
                return parser._startContainer(buf, pos, contentEnd);
            }
            if (tb == VPACK_ARRAY_COMPACT) {
                return parser._startContainer(buf, pos, contentEnd);
            }
            if (tb == VPACK_OBJECT_EMPTY) {
                return parser._startContainer(buf, pos, contentEnd);
            }
            if ((tb >= VPACK_OBJECT_SORTED_FIRST && tb <= VPACK_OBJECT_SORTED_LAST)
                    || (tb >= VPACK_OBJECT_UNSORTED_FIRST && tb <= VPACK_OBJECT_UNSORTED_LAST)) {
                return parser._startContainer(buf, pos, contentEnd);
            }
            if (tb == VPACK_OBJECT_COMPACT) {
                return parser._startContainer(buf, pos, contentEnd);
            }
            if (tb == VPACK_MIN_KEY) {
                _embeddedObject = "minKey";
                return parser._updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
            }
            if (tb == VPACK_MAX_KEY) {
                _embeddedObject = "maxKey";
                return parser._updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
            }
            if (tb >= VPACK_CUSTOM_FIRST && tb <= VPACK_CUSTOM_LAST) {
                if (VPackReadFeature.FAIL_ON_CUSTOM_TYPES.enabledIn(_formatFeatures)) {
                    throw parser._constructReadException(String.format(
                            "Custom type 0x%02x but FAIL_ON_CUSTOM_TYPES enabled", tb));
                }
                int totalSize = parser._valueByteSize(buf, pos, contentEnd);
                byte[] payload = extractPayload(tb, pos, totalSize);
                _embeddedObject = new VPackCustomValue(tb, payload);
                return parser._updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
            }
            throw parser._constructReadException(String.format("Unrecognized VPack type byte 0x%02x in buf", tb));
        }

        private byte[] extractPayload(int tb, int pos, int totalSize) {
            int headerSize;
            if (tb == VPACK_CUSTOM_1B) headerSize = 1;
            else if (tb == VPACK_CUSTOM_2B) headerSize = 1;
            else if (tb == VPACK_CUSTOM_4B) headerSize = 1;
            else if (tb == VPACK_CUSTOM_8B) headerSize = 1;
            else if (tb >= VPACK_CUSTOM_LEN1_FIRST && tb <= VPACK_CUSTOM_LEN1_LAST) headerSize = 2;
            else if (tb >= VPACK_CUSTOM_LEN2_FIRST && tb <= VPACK_CUSTOM_LEN2_LAST) headerSize = 3;
            else if (tb >= VPACK_CUSTOM_LEN4_FIRST && tb <= VPACK_CUSTOM_LEN4_LAST) headerSize = 5;
            else headerSize = 9;
            return Arrays.copyOfRange(buf, pos + headerSize, pos + totalSize);
        }

        JsonToken parseBcdInBuf(VPackParser parser, int tb, int pos, boolean neg) throws JacksonException {
            int lw = neg ? (tb - VPACK_BCD_NEG_FIRST + 1) : (tb - VPACK_BCD_POS_FIRST + 1);
            long mantLen = VPackUtil.readLeUnsigned(buf, pos + 1, lw);
            parser._validateBcdLength(mantLen);
            int exp = (int) VPackUtil.readLeSigned(buf, pos + 1 + lw, 4);
            byte[] bcd = Arrays.copyOfRange(buf, pos + 1 + lw + 4, pos + 1 + lw + 4 + (int) mantLen);
            java.math.BigDecimal bd = VPackUtil.decodeBcd(bcd, exp, neg);
            if (bd.scale() == 0) {
                _numberBigInt = bd.toBigIntegerExact();
                _numTypesValid = NR_BIGINT;
                _numberType = NumberType.BIG_INTEGER;
                return parser._updateToken(JsonToken.VALUE_NUMBER_INT);
            }
            _numberBigDecimal = bd;
            _numTypesValid = NR_BIGDECIMAL;
            _numberType = NumberType.BIG_DECIMAL;
            return parser._updateToken(JsonToken.VALUE_NUMBER_FLOAT);
        }
    }

    /*
    /**********************************************************************
    /* Array/object start from buf (used by ParseFrame.parseValueInBuf)
    /**********************************************************************
     */

    protected JsonToken _startEmptyArrayInBuf() throws JacksonException {
        return _pushContainer(false, null, 0, 0, 0, 0, 0, 1, false);
    }

    protected JsonToken _startEmptyObjectInBuf() throws JacksonException {
        return _pushContainer(true, null, 0, 0, 0, 0, 0, 1, false);
    }

    protected JsonToken _startArrayInBuf(byte[] srcBuf, int pos) throws JacksonException {
        return _startContainer(srcBuf, pos, srcBuf.length);
    }

    protected JsonToken _startCompactArrayInBuf(byte[] srcBuf, int pos) throws JacksonException {
        return _startContainer(srcBuf, pos, srcBuf.length);
    }

    protected JsonToken _startObjectInBuf(byte[] srcBuf, int pos) throws JacksonException {
        return _startContainer(srcBuf, pos, srcBuf.length);
    }

    protected JsonToken _startCompactObjectInBuf(byte[] srcBuf, int pos) throws JacksonException {
        return _startContainer(srcBuf, pos, srcBuf.length);
    }

}
