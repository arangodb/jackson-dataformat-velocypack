package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.io.IOContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static com.arangodb.jackson.dataformat.velocypack.VPackConstants.*;

/**
 * {@link JsonParser} implementation for VelocyPack-encoded content.
 *<p>
 * Design: The parser reads the entire VPack value into memory when constructing
 * the parser (or loads it eagerly from the stream). It then navigates the in-memory
 * buffer to produce tokens. This approach is natural for VPack because:
 * <ol>
 *   <li>Arrays and objects are length-prefixed, so we must know the full byte range.
 *   <li>The index table for objects/arrays is at the end of the value.
 * </ol>
 *<p>
 * The parser maintains a stack of {@link ParseFrame} objects, one per open
 * container, to track the current position within each container.
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

    /*
    /**********************************************************************
    /* Additional parsing state
    /**********************************************************************
     */

    /**
     * Stack of frames for nested containers. Top frame = innermost container.
     */
    protected final Deque<ParseFrame> _parseStack = new ArrayDeque<>();

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
        super(readCtxt, ctxt, parserFeatures, vpackFeatures);
        _inputStream = in;
        _inputBuffer = inputBuffer;
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
                if (count <= 0) return false;
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
        return _inputBuffer[_inputPtr++] & 0xFF;
    }

    protected long _readLeUnsigned(int numBytes) throws JacksonException {
        _ensureAvailable(numBytes);
        long result = VPackUtil.readLeUnsigned(_inputBuffer, _inputPtr, numBytes);
        _inputPtr += numBytes;
        return result;
    }

    protected long _readLeSigned(int numBytes) throws JacksonException {
        _ensureAvailable(numBytes);
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
            if (_ioContext.isResourceManaged() || isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE)) {
                _inputStream.close();
            }
            _inputStream = null;
        }
    }

    @Override
    protected void _releaseBuffers2() {
        if (_bufferRecyclable && _inputBuffer != null) {
            byte[] buf = _inputBuffer;
            _inputBuffer = null;
            _ioContext.releaseReadIOBuffer(buf);
        }
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
            if (frame.isObject) {
                _streamReadContext = _streamReadContext.getParent();
                return _updateToken(JsonToken.END_OBJECT);
            } else {
                _streamReadContext = _streamReadContext.getParent();
                return _updateToken(JsonToken.END_ARRAY);
            }
        }

        // Root level: check for EOF
        if (_inputPtr >= _inputEnd && !_ensureAvailable(1)) {
            return _eofToken();
        }

        return _readValue();
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
        // Handle tagged values first (0xee/0xef) - transparently skip the tag prefix
        if (ch == VPACK_TAG_1BYTE) {
            if (VPackReadFeature.FAIL_ON_TAGGED_VALUES.enabledIn(_formatFeatures)) {
                throw _constructReadException("Encountered tagged value (0xee) but FAIL_ON_TAGGED_VALUES is enabled");
            }
            _lastTagNumber = _nextByte();
            return _readValue(); // recurse to read wrapped value
        }
        if (ch == VPACK_TAG_8BYTE) {
            if (VPackReadFeature.FAIL_ON_TAGGED_VALUES.enabledIn(_formatFeatures)) {
                throw _constructReadException("Encountered tagged value (0xef) but FAIL_ON_TAGGED_VALUES is enabled");
            }
            _lastTagNumber = _readLeUnsigned(8);
            return _readValue();
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
        createChildArrayContext();
        // Empty: immediately push an exhausted frame and return START_ARRAY
        // The next call to nextToken() will return END_ARRAY
        _parseStack.push(new ParseFrame(false, new byte[0], 0, 0, 0));
        return _updateToken(JsonToken.START_ARRAY);
    }

    protected JsonToken _startArray(int typeByte) throws JacksonException {
        // Read array content into a local byte array for easier parsing
        int w = widthFromTypeByte_NoIdx(typeByte);
        long byteLen = _readLeUnsigned(w);
        // Total bytes already read: 1 (type) + w (byteLen field)
        // Remaining bytes = byteLen - 1 - w
        long remaining = byteLen - 1L - w;
        if (remaining < 0) {
            throw _constructReadException("Invalid VPack array: byteLen=" + byteLen + " too small");
        }
        _validateDocLen(byteLen);
        byte[] buf = new byte[(int) remaining];
        // Read remaining bytes
        _readBytesInto(buf, (int) remaining);
        // Build a ParseFrame
        ParseFrame frame = _buildArrayFrame(typeByte, w, buf);
        createChildArrayContext();
        _parseStack.push(frame);
        return _updateToken(JsonToken.START_ARRAY);
    }

    protected ParseFrame _buildArrayFrame(int typeByte, int w, byte[] remaining) {
        // Types 0x02-0x05: no index table, all items have same length
        // Types 0x06-0x09: have index table
        boolean hasIndex = typeByte >= VPACK_ARRAY_IDX_FIRST;
        if (!hasIndex) {
            // buf starts at first item (position 0 in remaining)
            // No item count stored; derive from content length / item length
            // The optional padding consists of zero bytes before the first item
            int start = 0;
            // Skip optional padding (zero bytes)
            while (start < remaining.length && remaining[start] == 0) {
                start++;
            }
            return new ParseFrame(false, remaining, start, remaining.length, -1);
        } else {
            // Has index table: items are followed by index table, then (for 0x09) nritems
            long nItems;
            if (w == 8) {
                // nritems at end (last 8 bytes)
                nItems = VPackUtil.readLeUnsigned(remaining, remaining.length - 8, 8);
            } else {
                // nritems = first w bytes of remaining (after byteLen field)
                nItems = VPackUtil.readLeUnsigned(remaining, 0, w);
                // skip optional padding (zero bytes after nritems)
            }
            // Items start after nritems field (and optional padding)
            // For simplicity, derive item start from index table
            // Index table is at: remaining.length - (nItems * w) [for non-8] or
            //                    remaining.length - 8 - (nItems * 8) [for 8]
            int idxTableOffset;
            if (w == 8) {
                idxTableOffset = (int) (remaining.length - 8 - nItems * 8);
            } else {
                idxTableOffset = (int) (remaining.length - nItems * w);
            }
            return new ParseFrame(false, remaining, 0, idxTableOffset, (int) nItems, w, true);
        }
    }

    protected JsonToken _startCompactArray() throws JacksonException {
        // Read VByte byteLen
        int[] outLen = new int[1];
        // Read VByte from stream
        long byteLen = _readVByteFromStream(outLen);
        // remaining = byteLen - 1 (typeByte) - outLen[0] (byteLen bytes)
        long remaining = byteLen - 1L - outLen[0];
        if (remaining < 0) {
            throw _constructReadException("Invalid compact array: byteLen=" + byteLen);
        }
        _validateDocLen(byteLen);
        byte[] buf = new byte[(int) remaining];
        _readBytesInto(buf, (int) remaining);
        // The last bytes of buf are the reverse-VByte NRITEMS
        int[] nr = new int[1];
        long nItems = VPackUtil.decodeVByteReverse(buf, buf.length, nr);
        int contentLen = buf.length - nr[0];
        ParseFrame frame = new ParseFrame(false, buf, 0, contentLen, (int) nItems, 1, false);
        createChildArrayContext();
        _parseStack.push(frame);
        return _updateToken(JsonToken.START_ARRAY);
    }

    /*
    /**********************************************************************
    /* Token reading: objects
    /**********************************************************************
     */

    protected JsonToken _startEmptyObject() throws JacksonException {
        createChildObjectContext();
        _parseStack.push(new ParseFrame(true, new byte[0], 0, 0, 0));
        return _updateToken(JsonToken.START_OBJECT);
    }

    protected JsonToken _startObject(int typeByte) throws JacksonException {
        int w = widthFromTypeByte_Obj(typeByte);
        long byteLen = _readLeUnsigned(w);
        long remaining = byteLen - 1L - w;
        if (remaining < 0) {
            throw _constructReadException("Invalid VPack object: byteLen=" + byteLen + " too small");
        }
        _validateDocLen(byteLen);
        byte[] buf = new byte[(int) remaining];
        _readBytesInto(buf, (int) remaining);
        ParseFrame frame = _buildObjectFrame(w, buf);
        createChildObjectContext();
        _parseStack.push(frame);
        return _updateToken(JsonToken.START_OBJECT);
    }

    protected ParseFrame _buildObjectFrame(int w, byte[] remaining) {
        long nPairs;
        int pairsStart; // offset in remaining where pairs begin
        if (w == 8) {
            // For 8-byte width: nritems at END, pairs start at byte 0
            nPairs = VPackUtil.readLeUnsigned(remaining, remaining.length - 8, 8);
            pairsStart = 0;
        } else {
            // nritems = first w bytes; pairs start at w (after nritems)
            nPairs = VPackUtil.readLeUnsigned(remaining, 0, w);
            pairsStart = w;
        }
        int idxTableOffset;
        if (w == 8) {
            idxTableOffset = (int) (remaining.length - 8 - nPairs * 8);
        } else {
            idxTableOffset = (int) (remaining.length - nPairs * w);
        }
        return new ParseFrame(true, remaining, pairsStart, idxTableOffset, (int) nPairs, w, true);
    }

    protected JsonToken _startCompactObject() throws JacksonException {
        int[] outLen = new int[1];
        long byteLen = _readVByteFromStream(outLen);
        long remaining = byteLen - 1L - outLen[0];
        if (remaining < 0) {
            throw _constructReadException("Invalid compact object: byteLen=" + byteLen);
        }
        _validateDocLen(byteLen);
        byte[] buf = new byte[(int) remaining];
        _readBytesInto(buf, (int) remaining);
        int[] nr = new int[1];
        long nPairs = VPackUtil.decodeVByteReverse(buf, buf.length, nr);
        int contentLen = buf.length - nr[0];
        ParseFrame frame = new ParseFrame(true, buf, 0, contentLen, (int) nPairs, 1, false);
        createChildObjectContext();
        _parseStack.push(frame);
        return _updateToken(JsonToken.START_OBJECT);
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
        int exponent = (int) _readLeSigned(4);
        byte[] bcd = _readBytes((int) mantLen);
        _numberBigDecimal = VPackUtil.decodeBcd(bcd, exponent, negative);
        _numTypesValid = NR_BIGDECIMAL;
        _numberType = NumberType.BIG_DECIMAL;
        return _updateToken(JsonToken.VALUE_NUMBER_FLOAT);
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
            return _readBytes((int) len);
        }
        // 8-byte length
        long len = _readLeUnsigned(8);
        if (len > Integer.MAX_VALUE) {
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
            return new String(buf, pos + 1, len, StandardCharsets.UTF_8);
        }
        if (typeByte == VPACK_STRING_LONG) {
            long len = VPackUtil.readLeUnsigned(buf, pos + 1, 8);
            return new String(buf, pos + 9, (int) len, StandardCharsets.UTF_8);
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

    /**
     * Return the byte size of a VPack value starting at {@code buf[pos]}.
     */
    protected int _valueByteSize(byte[] buf, int pos) throws JacksonException {
        int tb = buf[pos] & 0xFF;
        // Single-byte values
        if (tb == VPACK_NULL || tb == VPACK_FALSE || tb == VPACK_TRUE
                || tb == VPACK_MIN_KEY || tb == VPACK_MAX_KEY || tb == VPACK_ILLEGAL) {
            return 1;
        }
        if (tb >= VPACK_SMALL_INT_FIRST && tb <= VPACK_SMALL_INT_LAST) return 1;
        if (tb >= VPACK_SMALL_NEG_FIRST && tb <= VPACK_SMALL_NEG_LAST) return 1;
        if (tb == VPACK_ARRAY_EMPTY || tb == VPACK_OBJECT_EMPTY) return 1;
        // Fixed multi-byte
        if (tb == VPACK_DOUBLE) return 9;
        if (tb == VPACK_DATE) return 9;
        // Signed int: 0x20-0x27
        if (tb >= VPACK_INT_SIGNED_FIRST && tb <= VPACK_INT_SIGNED_LAST) {
            return 1 + (tb - VPACK_INT_SIGNED_FIRST + 1);
        }
        // Unsigned int: 0x28-0x2f
        if (tb >= VPACK_INT_UNSIGNED_FIRST && tb <= VPACK_INT_UNSIGNED_LAST) {
            return 1 + (tb - VPACK_INT_UNSIGNED_FIRST + 1);
        }
        // Short string
        if (tb >= VPACK_STRING_SHORT_FIRST && tb <= VPACK_STRING_SHORT_LAST) {
            return 1 + (tb - VPACK_STRING_SHORT_FIRST);
        }
        // Long string
        if (tb == VPACK_STRING_LONG) {
            long len = VPackUtil.readLeUnsigned(buf, pos + 1, 8);
            return (int) (9 + len);
        }
        // Binary
        if (tb >= VPACK_BINARY_FIRST && tb <= VPACK_BINARY_LAST) {
            int lw = tb - VPACK_BINARY_FIRST + 1;
            long len = VPackUtil.readLeUnsigned(buf, pos + 1, lw);
            return (int) (1 + lw + len);
        }
        // BCD pos/neg
        if (tb >= VPACK_BCD_POS_FIRST && tb <= VPACK_BCD_POS_LAST) {
            int lw = tb - VPACK_BCD_POS_FIRST + 1;
            long mantLen = VPackUtil.readLeUnsigned(buf, pos + 1, lw);
            return (int) (1 + lw + 4 + mantLen);
        }
        if (tb >= VPACK_BCD_NEG_FIRST && tb <= VPACK_BCD_NEG_LAST) {
            int lw = tb - VPACK_BCD_NEG_FIRST + 1;
            long mantLen = VPackUtil.readLeUnsigned(buf, pos + 1, lw);
            return (int) (1 + lw + 4 + mantLen);
        }
        // Arrays 0x02-0x09: byteLen field
        if (tb >= VPACK_ARRAY_NO_IDX_FIRST && tb <= VPACK_ARRAY_IDX_LAST) {
            int w = widthFromTypeByte_NoIdx(tb);
            long total = VPackUtil.readLeUnsigned(buf, pos + 1, w);
            return (int) total;
        }
        if (tb == VPACK_ARRAY_COMPACT || tb == VPACK_OBJECT_COMPACT) {
            // VByte byteLen
            int[] ol = new int[1];
            long total = VPackUtil.decodeVByte(buf, pos + 1, ol);
            return (int) total;
        }
        // Objects 0x0b-0x12
        if ((tb >= VPACK_OBJECT_SORTED_FIRST && tb <= VPACK_OBJECT_SORTED_LAST)
                || (tb >= VPACK_OBJECT_UNSORTED_FIRST && tb <= VPACK_OBJECT_UNSORTED_LAST)) {
            int w = widthFromTypeByte_Obj(tb);
            long total = VPackUtil.readLeUnsigned(buf, pos + 1, w);
            return (int) total;
        }
        // Tags
        if (tb == VPACK_TAG_1BYTE) {
            return 2 + _valueByteSize(buf, pos + 2);
        }
        if (tb == VPACK_TAG_8BYTE) {
            return 9 + _valueByteSize(buf, pos + 9);
        }
        // Custom types
        if (tb == VPACK_CUSTOM_1B) return 2;
        if (tb == VPACK_CUSTOM_2B) return 3;
        if (tb == VPACK_CUSTOM_4B) return 5;
        if (tb == VPACK_CUSTOM_8B) return 9;
        if (tb >= VPACK_CUSTOM_LEN1_FIRST && tb <= VPACK_CUSTOM_LEN1_LAST) {
            int len = buf[pos + 1] & 0xFF;
            return 2 + len;
        }
        if (tb >= VPACK_CUSTOM_LEN2_FIRST && tb <= VPACK_CUSTOM_LEN2_LAST) {
            int len = (int) VPackUtil.readLeUnsigned(buf, pos + 1, 2);
            return 3 + len;
        }
        if (tb >= VPACK_CUSTOM_LEN4_FIRST && tb <= VPACK_CUSTOM_LEN4_LAST) {
            long len = VPackUtil.readLeUnsigned(buf, pos + 1, 4);
            return (int) (5 + len);
        }
        if (tb >= VPACK_CUSTOM_LEN8_FIRST) {
            long len = VPackUtil.readLeUnsigned(buf, pos + 1, 8);
            return (int) (9 + len);
        }
        // Fall through: 1 byte for now
        return 1;
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
            int b = _nextByte();
            consumed++;
            result |= (long)(b & 0x7F) << shift;
            shift += 7;
            if ((b & 0x80) == 0) break;
        }
        outLen[0] = consumed;
        return result;
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
     * The container content (minus the type byte and byteLen field) is
     * stored in {@code buf}.
     */
    protected class ParseFrame
    {
        final boolean isObject;
        final byte[] buf;
        final int contentEnd;   // exclusive end of item content
        final int nItems;       // number of items/pairs
        final int w;            // width for offsets
        final boolean hasIndex; // has explicit index table

        int currentIndex = 0;   // which item we're at (0-based)
        int currentPos;         // current byte position in buf
        boolean expectingValue = false; // for objects: true when expecting value

        // Constructor for empty containers
        ParseFrame(boolean isObject, byte[] buf, int start, int end, int n) {
            this.isObject = isObject;
            this.buf = buf;
            this.contentEnd = end;
            this.nItems = n;
            this.w = 1;
            this.hasIndex = false;
            this.currentPos = start;
        }

        // Full constructor
        ParseFrame(boolean isObject, byte[] buf, int start, int contentEnd,
                int nItems, int w, boolean hasIndex) {
            this.isObject = isObject;
            this.buf = buf;
            this.contentEnd = contentEnd;
            this.nItems = nItems;
            this.w = w;
            this.hasIndex = hasIndex;
            this.currentPos = start;
        }

        boolean isDone() {
            // For no-index arrays (nItems == -1), done when we've scanned all content
            if (nItems < 0) {
                return currentPos >= contentEnd;
            }
            return currentIndex >= nItems;
        }

        /**
         * Return the next token for this frame, or null if the container is exhausted.
         */
        JsonToken nextToken(VPackParser parser) throws JacksonException {
            if (isDone()) return null;

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
                return null; // exhausted
            }

            int tb = buf[itemPos] & 0xFF;
            currentIndex++;
            // Advance currentPos (for sequential access)
            if (!hasIndex) {
                currentPos = itemPos + parser._valueByteSize(buf, itemPos);
            }

            return parseValueInBuf(parser, tb, itemPos);
        }

        JsonToken nextObjectToken(VPackParser parser) throws JacksonException {
            if (isDone()) return null;

            if (!expectingValue) {
                // Read the key
                int pairPos = getPairPos(currentIndex);
                if (pairPos < 0 || pairPos >= contentEnd) {
                    return null;
                }
                String name = parser._readPropertyName(buf, pairPos);
                // Update streamReadContext with name
                _streamReadContext.setCurrentName(name);
                _textBuffer.resetWithString(name);
                // Advance position to value
                int keySize = parser._valueByteSize(buf, pairPos);
                currentPos = pairPos + keySize;
                expectingValue = true;
                return JsonToken.PROPERTY_NAME;
            } else {
                // Read the value
                int valuePos = currentPos;
                int tb = buf[valuePos] & 0xFF;
                int valueSize = parser._valueByteSize(buf, valuePos);
                currentPos = valuePos + valueSize;
                currentIndex++;
                expectingValue = false;
                return parseValueInBuf(parser, tb, valuePos);
            }
        }

        int getItemPos(int idx) {
            if (hasIndex) {
                // Read offset from index table (at contentEnd in buf)
                long offset = VPackUtil.readLeUnsigned(buf, contentEnd + idx * w, w);
                // Offsets are from base A (start of full VPack value).
                // Our buf starts at A + 1 (type) + w (byteLen field).
                // So buf_offset = absolute_offset - (1 + w)
                int bufBase = 1 + w;
                return (int) (offset - bufBase);
            } else {
                // Sequential: use currentPos (already maintained)
                return currentPos;
            }
        }

        int getPairPos(int idx) {
            if (hasIndex) {
                long offset = VPackUtil.readLeUnsigned(buf, contentEnd + idx * w, w);
                // buf starts at A + 1 (type) + w (byteLen field) = A + 1 + w
                // So buf_offset = absolute_offset - (1 + w)
                int bufBase = 1 + w; // bytes of header before buf starts
                return (int) (offset - bufBase);
            } else {
                return currentPos;
            }
        }

        /**
         * Parse a VPack value from the internal buf at position pos and return the token.
         * This handles nested containers by recursion (pushing new frames to _parseStack).
         */
        JsonToken parseValueInBuf(VPackParser parser, int tb, int pos) throws JacksonException {
            // Redirect input to the buf slice for parsing
            // We need to parse this value from buf[pos..]
            // Push a sub-parser context

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
                _textBuffer.resetWithString(new String(buf, pos + 1, len, StandardCharsets.UTF_8));
                return parser._updateToken(JsonToken.VALUE_STRING);
            }
            if (tb == VPACK_STRING_LONG) {
                long len = VPackUtil.readLeUnsigned(buf, pos + 1, 8);
                _textBuffer.resetWithString(new String(buf, pos + 9, (int) len, StandardCharsets.UTF_8));
                return parser._updateToken(JsonToken.VALUE_STRING);
            }
            if (tb >= VPACK_BINARY_FIRST && tb <= VPACK_BINARY_LAST) {
                int lw = tb - VPACK_BINARY_FIRST + 1;
                long len = VPackUtil.readLeUnsigned(buf, pos + 1, lw);
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
                return parser._startEmptyArrayInBuf();
            }
            if (tb >= VPACK_ARRAY_NO_IDX_FIRST && tb <= VPACK_ARRAY_IDX_LAST) {
                return parser._startArrayInBuf(buf, pos);
            }
            if (tb == VPACK_ARRAY_COMPACT) {
                return parser._startCompactArrayInBuf(buf, pos);
            }
            if (tb == VPACK_OBJECT_EMPTY) {
                return parser._startEmptyObjectInBuf();
            }
            if ((tb >= VPACK_OBJECT_SORTED_FIRST && tb <= VPACK_OBJECT_SORTED_LAST)
                    || (tb >= VPACK_OBJECT_UNSORTED_FIRST && tb <= VPACK_OBJECT_UNSORTED_LAST)) {
                return parser._startObjectInBuf(buf, pos);
            }
            if (tb == VPACK_OBJECT_COMPACT) {
                return parser._startCompactObjectInBuf(buf, pos);
            }
            if (tb == VPACK_MIN_KEY) {
                _embeddedObject = "minKey";
                return parser._updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
            }
            if (tb == VPACK_MAX_KEY) {
                _embeddedObject = "maxKey";
                return parser._updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
            }
            if (tb == VPACK_TAG_1BYTE || tb == VPACK_TAG_8BYTE) {
                if (VPackReadFeature.FAIL_ON_TAGGED_VALUES.enabledIn(_formatFeatures)) {
                    throw parser._constructReadException("Encountered tagged value but FAIL_ON_TAGGED_VALUES enabled");
                }
                int tagSkip = (tb == VPACK_TAG_1BYTE) ? 2 : 9;
                _lastTagNumber = (tb == VPACK_TAG_1BYTE) ? (buf[pos + 1] & 0xFF)
                        : VPackUtil.readLeUnsigned(buf, pos + 1, 8);
                return parseValueInBuf(parser, buf[pos + tagSkip] & 0xFF, pos + tagSkip);
            }
            if (tb >= VPACK_CUSTOM_FIRST && tb <= VPACK_CUSTOM_LAST) {
                if (VPackReadFeature.FAIL_ON_CUSTOM_TYPES.enabledIn(_formatFeatures)) {
                    throw parser._constructReadException(String.format(
                            "Custom type 0x%02x but FAIL_ON_CUSTOM_TYPES enabled", tb));
                }
                int totalSize = parser._valueByteSize(buf, pos);
                byte[] payload = extractPayload(tb, pos, totalSize);
                _embeddedObject = new VPackCustomValue(tb, payload);
                return parser._updateToken(JsonToken.VALUE_EMBEDDED_OBJECT);
            }
            throw parser._constructReadException(String.format("Unrecognized VPack type byte 0x%02x in buf", tb));
        }

        private byte[] extractPayload(int tb, int pos, int totalSize) {
            // simplified
            // Read payload
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
            int exp = (int) VPackUtil.readLeSigned(buf, pos + 1 + lw, 4);
            byte[] bcd = Arrays.copyOfRange(buf, pos + 1 + lw + 4, pos + 1 + lw + 4 + (int) mantLen);
            _numberBigDecimal = VPackUtil.decodeBcd(bcd, exp, neg);
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
        createChildArrayContext();
        _parseStack.push(new ParseFrame(false, new byte[0], 0, 0, 0));
        return _updateToken(JsonToken.START_ARRAY);
    }

    protected JsonToken _startEmptyObjectInBuf() throws JacksonException {
        createChildObjectContext();
        _parseStack.push(new ParseFrame(true, new byte[0], 0, 0, 0));
        return _updateToken(JsonToken.START_OBJECT);
    }

    protected JsonToken _startArrayInBuf(byte[] srcBuf, int pos) throws JacksonException {
        int tb = srcBuf[pos] & 0xFF;
        int w = widthFromTypeByte_NoIdx(tb);
        long totalLen = VPackUtil.readLeUnsigned(srcBuf, pos + 1, w);
        // remaining = total - 1 (type) - w (byteLen)
        int headerSize = 1 + w;
        byte[] sub = Arrays.copyOfRange(srcBuf, pos + headerSize, pos + (int) totalLen);
        ParseFrame frame = _buildArrayFrame(tb, w, sub);
        createChildArrayContext();
        _parseStack.push(frame);
        return _updateToken(JsonToken.START_ARRAY);
    }

    protected JsonToken _startCompactArrayInBuf(byte[] srcBuf, int pos) throws JacksonException {
        // VByte byteLen
        int[] ol = new int[1];
        long totalLen = VPackUtil.decodeVByte(srcBuf, pos + 1, ol);
        int headerSize = 1 + ol[0];
        byte[] sub = Arrays.copyOfRange(srcBuf, pos + headerSize, pos + (int) totalLen);
        int[] nr = new int[1];
        long nItems = VPackUtil.decodeVByteReverse(sub, sub.length, nr);
        int contentLen = sub.length - nr[0];
        ParseFrame frame = new ParseFrame(false, sub, 0, contentLen, (int) nItems, 1, false);
        createChildArrayContext();
        _parseStack.push(frame);
        return _updateToken(JsonToken.START_ARRAY);
    }

    protected JsonToken _startObjectInBuf(byte[] srcBuf, int pos) throws JacksonException {
        int tb = srcBuf[pos] & 0xFF;
        int w = widthFromTypeByte_Obj(tb);
        long totalLen = VPackUtil.readLeUnsigned(srcBuf, pos + 1, w);
        int headerSize = 1 + w;
        byte[] sub = Arrays.copyOfRange(srcBuf, pos + headerSize, pos + (int) totalLen);
        ParseFrame frame = _buildObjectFrame(w, sub);
        createChildObjectContext();
        _parseStack.push(frame);
        return _updateToken(JsonToken.START_OBJECT);
    }

    protected JsonToken _startCompactObjectInBuf(byte[] srcBuf, int pos) throws JacksonException {
        int[] ol = new int[1];
        long totalLen = VPackUtil.decodeVByte(srcBuf, pos + 1, ol);
        int headerSize = 1 + ol[0];
        byte[] sub = Arrays.copyOfRange(srcBuf, pos + headerSize, pos + (int) totalLen);
        int[] nr = new int[1];
        long nPairs = VPackUtil.decodeVByteReverse(sub, sub.length, nr);
        int contentLen = sub.length - nr[0];
        ParseFrame frame = new ParseFrame(true, sub, 0, contentLen, (int) nPairs, 1, false);
        createChildObjectContext();
        _parseStack.push(frame);
        return _updateToken(JsonToken.START_OBJECT);
    }

}
