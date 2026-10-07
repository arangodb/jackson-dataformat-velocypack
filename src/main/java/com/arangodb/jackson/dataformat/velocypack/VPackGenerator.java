package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.SimpleStreamWriteContext;
import tools.jackson.databind.cfg.PackageVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import static com.arangodb.jackson.dataformat.velocypack.VPackConstants.*;

/**
 * {@link JsonGenerator} implementation for VelocyPack-encoded content.
 *<p>
 * Key design decisions:
 * <ul>
 *   <li>All multi-byte integers are little-endian (per spec).
 *   <li>Open containers share a payload buffer and primitive boundary stacks.
 *       Nine internal bytes are reserved at each opening; closing selects the
 *       layout and compacts its header. A completed root is emitted directly.
 *       Optional object sorting also reorders physical pairs, stably.
 *   <li>Short container strings encode once and patch a local one-byte header;
 *       reservations are bounded to 126 payload bytes. Longer/root strings use
 *       an exact UTF-8 byte-length pass followed by direct encoding.
 *       Malformed UTF-16 retains the legacy '?' replacement in both lenient
 *       feature states. SerializableString uses its value, not its potentially
 *       stricter cached UTF-8 encoder. Raw UTF-8 remains byte pass-through.
 *   <li>Known-length binary streams fill container payload ranges directly.
 *       Huge root strings and binaries use only the separate output buffer.
 *       Caller arrays are consumed/copied during the write call.
 *   <li>Payload, sorting scratch and primitive stacks are reused only by this
 *       generator and cleared at close. Only the original recyclable output
 *       buffer is returned to IOContext. Output failures discard pending bytes
 *       and prohibit further values; close still honors target ownership.
 *   <li>Integers are encoded using the minimal representation when
 *       {@link VPackWriteFeature#WRITE_MIN_INT_WIDTH} is enabled (default true).
 * </ul>
 */
public class VPackGenerator extends GeneratorBase
{
    private static final int MIN_BUFFER_LENGTH = 256;

    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */

    protected final OutputStream _out;
    protected int _formatFeatures;

    /*
    /**********************************************************************
    /* Output state
    /**********************************************************************
     */

    protected SimpleStreamWriteContext _streamWriteContext;

    /*
    /**********************************************************************
    /* Output buffering (for root-level writes)
    /**********************************************************************
     */

    protected byte[] _outputBuffer;
    protected int _outputTail = 0;
    protected final int _outputEnd;
    protected final boolean _bufferRecyclable;
    private boolean _outputFailed;

    /*
    /**********************************************************************
    /* Nested container buffering
    /**********************************************************************
     */

    // One arena for the active root. The IOContext output buffer is independent.
    private static final int HEADER_RESERVATION = 9;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    protected byte[] _payload;
    protected int _payloadTail;
    protected int _depth;
    protected int[] _frameStarts = new int[16];
    protected int[] _frameBases = new int[16];
    protected int[] _frameCounts = new int[16];
    protected int[] _frameNext = new int[16];
    protected boolean[] _frameObjects = new boolean[16];
    // Starts and key ends for direct children of open containers only. A parent's
    // pending item/pair precedes the child's segment and survives its retirement.
    protected int[] _offsets = new int[64];
    protected int[] _keyEnds = new int[64];
    protected int _offsetTail;
    private int[] _sortOrder;
    private int[] _sortWork;
    private byte[] _sortScratch;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public VPackGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt,
            int streamWriteFeatures, int vpackFeatures, OutputStream out)
    {
        super(writeCtxt, ioCtxt, streamWriteFeatures);
        _formatFeatures = vpackFeatures;
        final DupDetector dups = StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamWriteFeatures)
                ? DupDetector.rootDetector(this) : null;
        _streamWriteContext = SimpleStreamWriteContext.createRootContext(dups);
        _out = out;
        _bufferRecyclable = true;
        _outputBuffer = ioCtxt.allocWriteEncodingBuffer();
        _outputEnd = _outputBuffer.length;
        if (_outputEnd < MIN_BUFFER_LENGTH) {
            throw new IllegalStateException(String.format(
                    "Internal encoding buffer length (%d) too short, must be at least %d",
                    _outputEnd, MIN_BUFFER_LENGTH));
        }
    }

    public VPackGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt,
            int streamWriteFeatures, int vpackFeatures, OutputStream out,
            byte[] outputBuffer, int offset, boolean bufferRecyclable)
    {
        super(writeCtxt, ioCtxt, streamWriteFeatures);
        _formatFeatures = vpackFeatures;
        final DupDetector dups = StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamWriteFeatures)
                ? DupDetector.rootDetector(this) : null;
        _streamWriteContext = SimpleStreamWriteContext.createRootContext(dups);
        _out = out;
        _outputBuffer = outputBuffer;
        _outputTail = offset;
        _outputEnd = outputBuffer.length;
        _bufferRecyclable = bufferRecyclable;
    }

    /*
    /**********************************************************************
    /* Versioned, capabilities
    /**********************************************************************
     */

    @Override
    public Version version() { return PackageVersion.VERSION; }

    @Override
    public JacksonFeatureSet<StreamWriteCapability> streamWriteCapabilities() {
        return DEFAULT_WRITE_CAPABILITIES;
    }

    @Override
    public Object streamWriteOutputTarget() { return _out; }

    @Override
    public int streamWriteOutputBuffered() { return _outputTail; }

    @Override
    public Object currentValue() { return _streamWriteContext.currentValue(); }

    @Override
    public void assignCurrentValue(Object v) { _streamWriteContext.assignCurrentValue(v); }

    @Override
    public TokenStreamContext streamWriteContext() { return _streamWriteContext; }

    /*
    /**********************************************************************
    /* Feature management
    /**********************************************************************
     */

    public VPackGenerator enable(VPackWriteFeature f) {
        _formatFeatures |= f.getMask();
        return this;
    }

    public VPackGenerator disable(VPackWriteFeature f) {
        _formatFeatures &= ~f.getMask();
        return this;
    }

    public boolean isEnabled(VPackWriteFeature f) {
        return f.enabledIn(_formatFeatures);
    }

    public VPackGenerator configure(VPackWriteFeature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    /*
    /**********************************************************************
    /* Extended API: tagged values
    /**********************************************************************
     */

    /**
     * Write a VPack tagged-value prefix ({@code 0xee}/{@code 0xef}).
     * The tag number is followed immediately by the wrapped VPack value.
     * This does not consume a value context slot. Inside an object it must
     * follow the property name. The prefix belongs to the following scalar or
     * container, including its parent's item boundary and encoded length.
     */
    public void writeTaggedValuePrefix(long tagNumber) throws JacksonException {
        _checkOutputFailure();
        if (_depth != 0 && _frameObjects[_depth - 1]
                && _offsetTail - _frameBases[_depth - 1] == _frameCounts[_depth - 1]) {
            _reportError("Tagged prefix in an object must follow a property name");
        }
        if (tagNumber >= 0L && tagNumber <= 0xFFL) {
            _rawByte((byte) VPACK_TAG_1BYTE);
            _rawByte((byte) (int) tagNumber);
        } else {
            _rawByte((byte) VPACK_TAG_8BYTE);
            _rawLeUnsignedLong(tagNumber);
        }
    }

    /*
    /**********************************************************************
    /* Low-level write methods (not part of JsonGenerator API but useful for tests)
    /**********************************************************************
     */

    /**
     * Root-only byte pass-through. Nested fragments have no unambiguous item
     * boundary contract; use writeRawValue for a complete pre-encoded value.
     */
    public JsonGenerator writeRaw(byte b) throws JacksonException {
        _verifyRootRaw();
        _verifyValueWrite("write raw byte");
        _rawByte(b);
        return this;
    }

    /** Root-only block pass-through, including blocks containing multiple values. */
    public JsonGenerator writeBytes(byte[] data, int offset, int len) throws JacksonException {
        _verifyRootRaw();
        _verifyValueWrite("write raw bytes");
        _rawBytes(data, offset, len);
        return this;
    }

    /*
    /**********************************************************************
    /* Structural: arrays
    /**********************************************************************
     */

    @Override
    public JsonGenerator writeStartArray() throws JacksonException {
        _verifyValueWrite("start an array");
        _streamWriteContext = _streamWriteContext.createChildArrayContext(null);
        _openContainer(false);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(Object forValue) throws JacksonException {
        _verifyValueWrite("start an array");
        _streamWriteContext = _streamWriteContext.createChildArrayContext(forValue);
        _openContainer(false);
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(Object forValue, int size) throws JacksonException {
        _verifyValueWrite("start an array");
        _streamWriteContext = _streamWriteContext.createChildArrayContext(forValue);
        _openContainer(false);
        return this;
    }

    @Override
    public JsonGenerator writeEndArray() throws JacksonException {
        if (!_streamWriteContext.inArray()) {
            _reportError("Current context not Array but " + _streamWriteContext.typeDesc());
        }
        _closeContainer();
        return this;
    }

    /*
    /**********************************************************************
    /* Structural: objects
    /**********************************************************************
     */

    @Override
    public JsonGenerator writeStartObject() throws JacksonException {
        _verifyValueWrite("start an object");
        _streamWriteContext = _streamWriteContext.createChildObjectContext(null);
        _openContainer(true);
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(Object forValue) throws JacksonException {
        _verifyValueWrite("start an object");
        _streamWriteContext = _streamWriteContext.createChildObjectContext(forValue);
        _openContainer(true);
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(Object forValue, int size) throws JacksonException {
        _verifyValueWrite("start an object");
        _streamWriteContext = _streamWriteContext.createChildObjectContext(forValue);
        _openContainer(true);
        return this;
    }

    @Override
    public JsonGenerator writeEndObject() throws JacksonException {
        if (!_streamWriteContext.inObject()) {
            _reportError("Current context not Object but " + _streamWriteContext.typeDesc());
        }
        _closeContainer();
        return this;
    }

    /*
    /**********************************************************************
    /* Property names
    /**********************************************************************
     */

    @Override
    public JsonGenerator writeName(String name) throws JacksonException {
        _checkOutputFailure();
        if (!_streamWriteContext.writeName(name)) {
            _reportError("Cannot write a property name, expecting a value");
        }
        _startPair();
        _doWriteString(name);
        _keyEnds[_offsetTail - 1] = _payloadTail;
        return this;
    }

    @Override
    public JsonGenerator writeName(SerializableString name) throws JacksonException {
        return writeName(name.getValue());
    }

    @Override
    public JsonGenerator writePropertyId(long id) throws JacksonException {
        _checkOutputFailure();
        if (!_streamWriteContext.writeName(String.valueOf(id))) {
            _reportError("Cannot write a property name, expecting a value");
        }
        _startPair();
        _doWriteUnsignedInt(id);
        _keyEnds[_offsetTail - 1] = _payloadTail;
        return this;
    }

    /*
    /**********************************************************************
    /* Scalar values
    /**********************************************************************
     */

    @Override
    public JsonGenerator writeString(String text) throws JacksonException {
        _verifyValueWrite("write a string value");
        if (text == null) {
            _emitNull();
            return this;
        }
        _doWriteString(text);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeString(char[] text, int offset, int len) throws JacksonException {
        _verifyValueWrite("write a string value");
        Objects.checkFromIndexSize(offset, len, text.length);
        _doWriteChars(null, text, offset, len);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeString(SerializableString sstr) throws JacksonException {
        _verifyValueWrite("write a string value");
        // asUnquotedUTF8() may reject surrogates that the legacy writer replaced.
        _doWriteString(sstr.getValue());
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeRawUTF8String(byte[] text, int offset, int len) throws JacksonException {
        _verifyValueWrite("write raw UTF-8 string value");
        _doWriteUtf8Bytes(text, offset, len);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeUTF8String(byte[] text, int offset, int len) throws JacksonException {
        _verifyValueWrite("write UTF-8 string value");
        _doWriteUtf8Bytes(text, offset, len);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeRaw(String text) throws JacksonException {
        throw new UnsupportedOperationException("writeRaw not supported for VelocyPack format");
    }

    @Override
    public JsonGenerator writeRaw(String text, int offset, int len) throws JacksonException {
        throw new UnsupportedOperationException("writeRaw not supported for VelocyPack format");
    }

    @Override
    public JsonGenerator writeRaw(char[] text, int offset, int len) throws JacksonException {
        throw new UnsupportedOperationException("writeRaw not supported for VelocyPack format");
    }

    @Override
    public JsonGenerator writeRaw(char c) throws JacksonException {
        throw new UnsupportedOperationException("writeRaw not supported for VelocyPack format");
    }

    @Override
    public JsonGenerator writeRawValue(String text) throws JacksonException {
        throw new UnsupportedOperationException("writeRawValue not supported for VelocyPack format");
    }

    @Override
    public JsonGenerator writeRawValue(String text, int offset, int len) throws JacksonException {
        throw new UnsupportedOperationException("writeRawValue not supported for VelocyPack format");
    }

    @Override
    public JsonGenerator writeRawValue(char[] text, int offset, int len) throws JacksonException {
        throw new UnsupportedOperationException("writeRawValue not supported for VelocyPack format");
    }

    /**
     * Copies one pre-encoded value inside a container. The caller owns wire
     * validity: this method does not parse blocks to establish value boundaries.
     * Empty blocks and a leading None byte are rejected inside containers.
     */
    @Override
    public JsonGenerator writeRawValue(SerializableString text) throws JacksonException {
        _verifyValueWrite("write raw value");
        byte[] raw = text.asUnquotedUTF8();
        if (_depth != 0 && (raw.length == 0 || raw[0] == VPACK_NONE)) {
            _reportError("Nested raw value must contain one complete on-wire value; None (0x00) is illegal");
        }
        _rawBytes(raw, 0, raw.length);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
            throws JacksonException
    {
        _verifyValueWrite("write binary value");
        _doWriteBinary(data, offset, len);
        _valueFinished();
        return this;
    }

    @Override
    public int writeBinary(InputStream data, int dataLength) throws JacksonException {
        return writeBinary(Base64Variants.getDefaultVariant(), data, dataLength);
    }

    @Override
    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength)
            throws JacksonException
    {
        _verifyValueWrite("write binary value");
        if (dataLength < 0) {
            throw new UnsupportedOperationException("VPack binary requires known length");
        }
        int width = VPackUtil.unsignedByteWidth(dataLength);
        _rawByte((byte) (VPACK_BINARY_FIRST + width - 1));
        _rawLeWidth(dataLength, width);
        if (_depth != 0) {
            _ensurePayload((long) _payloadTail + dataLength);
            _readBinaryChunk(data, _payload, _payloadTail, dataLength, dataLength, 0);
            _payloadTail += dataLength;
        } else if (dataLength <= _outputEnd) {
            if (dataLength > _outputEnd - _outputTail) _flushBuffer();
            _readBinaryChunk(data, _outputBuffer, _outputTail, dataLength, dataLength, 0);
            _outputTail += dataLength;
        } else {
            // Match root _rawBytes visibility: flush the header and emit all
            // large binary bytes before returning, with no final buffered tail.
            _flushBuffer();
            for (int read = 0; read < dataLength; ) {
                int n = Math.min(_outputEnd, dataLength - read);
                _readBinaryChunk(data, _outputBuffer, 0, n, dataLength, read);
                try {
                    _out.write(_outputBuffer, 0, n);
                } catch (IOException e) {
                    throw _outputFailure(e);
                }
                read += n;
            }
        }
        _valueFinished();
        return dataLength;
    }

    private void _readBinaryChunk(InputStream data, byte[] target, int offset,
            int length, int totalLength, int alreadyRead) throws JacksonException {
        int read = 0;
        try {
            while (read < length) {
                int n = data.read(target, offset + read, length - read);
                // Handle streams that make no progress on a bulk read.
                if (n == 0) {
                    int b = data.read();
                    if (b >= 0) { target[offset + read] = (byte) b; n = 1; }
                    else n = -1;
                }
                if (n < 0) {
                    throw new StreamWriteException(this,
                            "End of stream before all binary data read: expected "
                            + totalLength + " bytes, got " + (alreadyRead + read));
                }
                read += n;
            }
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public JsonGenerator writeBoolean(boolean state) throws JacksonException {
        _verifyValueWrite("write boolean value");
        _rawByte((byte) (state ? VPACK_TRUE : VPACK_FALSE));
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeNull() throws JacksonException {
        _verifyValueWrite("write null value");
        _emitNull();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(short v) throws JacksonException {
        return writeNumber((int) v);
    }

    @Override
    public JsonGenerator writeNumber(int v) throws JacksonException {
        _verifyValueWrite("write int value");
        _doWriteInt(v);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(long l) throws JacksonException {
        _verifyValueWrite("write long value");
        _doWriteLong(l);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(BigInteger v) throws JacksonException {
        if (v == null) {
            return writeNull();
        }
        _verifyValueWrite("write BigInteger value");
        try {
            _doWriteLong(v.longValueExact());
        } catch (ArithmeticException e) {
            _doWriteBigDecimal(new BigDecimal(v));
        }
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(float f) throws JacksonException {
        return writeNumber((double) f);
    }

    @Override
    public JsonGenerator writeNumber(double d) throws JacksonException {
        _verifyValueWrite("write double value");
        _doWriteDouble(d);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(BigDecimal dec) throws JacksonException {
        if (dec == null) {
            return writeNull();
        }
        _verifyValueWrite("write BigDecimal value");
        _doWriteBigDecimal(dec);
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(String encodedValue) throws JacksonException {
        if (encodedValue == null) {
            return writeNull();
        }
        _verifyValueWrite("write number value");
        try {
            if (encodedValue.indexOf('.') >= 0 || encodedValue.indexOf('e') >= 0
                    || encodedValue.indexOf('E') >= 0) {
                _doWriteBigDecimal(new BigDecimal(encodedValue).stripTrailingZeros());
            } else {
                try {
                    _doWriteLong(Long.parseLong(encodedValue));
                } catch (NumberFormatException ex) {
                    _doWriteBigDecimal(new BigDecimal(encodedValue));
                }
            }
        } catch (NumberFormatException e) {
            throw new StreamWriteException(this, "Invalid number string: " + encodedValue);
        }
        _valueFinished();
        return this;
    }

    /*
    /**********************************************************************
    /* Array shortcuts
    /**********************************************************************
     */

    @Override
    public JsonGenerator writeArray(int[] array, int offset, int length) throws JacksonException
    {
        Objects.checkFromIndexSize(offset, length, array.length);
        writeStartArray();
        for (int i = offset, end = offset + length; i < end; i++) {
            writeNumber(array[i]);
        }
        writeEndArray();
        return this;
    }

    @Override
    public JsonGenerator writeArray(long[] array, int offset, int length) throws JacksonException
    {
        Objects.checkFromIndexSize(offset, length, array.length);
        writeStartArray();
        for (int i = offset, end = offset + length; i < end; i++) {
            writeNumber(array[i]);
        }
        writeEndArray();
        return this;
    }

    @Override
    public JsonGenerator writeArray(double[] array, int offset, int length) throws JacksonException
    {
        Objects.checkFromIndexSize(offset, length, array.length);
        writeStartArray();
        for (int i = offset, end = offset + length; i < end; i++) {
            writeNumber(array[i]);
        }
        writeEndArray();
        return this;
    }

    /*
    /**********************************************************************
    /* Internal: value-type encoding
    /**********************************************************************
     */

    protected void _emitNull() throws JacksonException {
        _rawByte((byte) VPACK_NULL);
        _valueFinished();
    }

    protected void _doWriteInt(int v) throws JacksonException {
        if (VPackWriteFeature.WRITE_MIN_INT_WIDTH.enabledIn(_formatFeatures)) {
            if (v >= 0 && v <= 9) {
                _rawByte((byte) (VPACK_SMALL_INT_FIRST + v));
                return;
            }
            if (v >= -6 && v < 0) {
                _rawByte((byte) (0x40 + v)); // 0x3a..(0x3f)
                return;
            }
        }
        _doWriteLong(v);
    }

    protected void _doWriteLong(long v) throws JacksonException {
        if (VPackWriteFeature.WRITE_MIN_INT_WIDTH.enabledIn(_formatFeatures)) {
            if (v >= 0L && v <= 9L) {
                _rawByte((byte) (VPACK_SMALL_INT_FIRST + (int) v));
                return;
            }
            if (v >= -6L && v < 0L) {
                _rawByte((byte) (0x40 + (int) v));
                return;
            }
        }
        int w = VPackUtil.signedByteWidth(v);
        _rawByte((byte) (VPACK_INT_SIGNED_FIRST + w - 1));
        _rawLeWidth(v, w);
    }

    protected void _doWriteUnsignedInt(long v) throws JacksonException {
        int w = VPackUtil.unsignedByteWidth(v);
        _rawByte((byte) (VPACK_INT_UNSIGNED_FIRST + w - 1));
        _rawLeWidth(v, w);
    }

    protected void _doWriteDouble(double d) throws JacksonException {
        long bits = Double.doubleToRawLongBits(d);
        _rawByte((byte) VPACK_DOUBLE);
        _rawLeUnsignedLong(bits);
    }

    protected void _doWriteBigDecimal(BigDecimal dec) throws JacksonException {
        boolean neg = dec.signum() < 0;
        int[] outExp = new int[1];
        byte[] bcd = VPackUtil.encodeBcd(dec, outExp);
        int mantLen = bcd.length;
        int exp = outExp[0];
        int mantLenWidth = VPackUtil.unsignedByteWidth(mantLen);
        int typeByte = (neg ? VPACK_BCD_NEG_FIRST : VPACK_BCD_POS_FIRST) + mantLenWidth - 1;
        _rawByte((byte) typeByte);
        _rawLeWidth(mantLen, mantLenWidth);
        _rawLeWidth(exp, 4);
        _rawBytes(bcd, 0, bcd.length);
    }

    protected void _doWriteBinary(byte[] data, int offset, int len) throws JacksonException {
        int lenWidth = VPackUtil.unsignedByteWidth(len);
        int typeByte = VPACK_BINARY_FIRST + lenWidth - 1;
        _rawByte((byte) typeByte);
        _rawLeWidth(len, lenWidth);
        _rawBytes(data, offset, len);
    }

    @SuppressWarnings("deprecation") // Entire range is proven ASCII before the JDK bulk copy.
    protected void _doWriteString(String text) throws JacksonException {
        int length = text.length();
        if (_depth != 0 && length <= VPACK_STRING_SHORT_MAX_LEN / 3) {
            int start = _payloadTail;
            _ensurePayload((long) start + 1 + 3L * length);
            int bits = 0;
            for (int i = 0; i < length; ++i) bits |= text.charAt(i);
            int end;
            if (bits < 0x80) {
                // String's compact Latin-1 storage can be copied by the JDK.
                // The deprecated overload truncates Unicode, hence the guard.
                text.getBytes(0, length, _payload, start + 1);
                end = start + 1 + length;
            } else {
                end = _encodeUtf8(text, null, 0, length, _payload, start + 1);
            }
            _payload[start] = (byte) (VPACK_STRING_SHORT_FIRST + end - start - 1);
            _payloadTail = end;
        } else {
            _doWriteChars(text, null, 0, length);
        }
    }

    private static char _charAt(String text, char[] chars, int pos) {
        return text == null ? chars[pos] : text.charAt(pos);
    }

    // Exact length, with long arithmetic: no 3*charCount reservation and no
    // encoded temporary. Match String.getBytes(UTF_8), including '?' for each
    // malformed surrogate. LENIENT_UTF_ENCODING historically has no effect.
    private static long _utf8Length(String text, char[] chars, int offset, int length) {
        long bytes = length;
        for (int i = offset, end = offset + length; i < end; ++i) {
            char c = _charAt(text, chars, i);
            if (c < 0x80) continue;
            if (c < 0x800) ++bytes;
            else if (c < 0xd800 || c > 0xdfff) bytes += 2;
            else if (c <= 0xdbff && i + 1 < end
                    && Character.isLowSurrogate(_charAt(text, chars, i + 1))) {
                bytes += 2;
                ++i;
            }
        }
        return bytes;
    }

    private void _doWriteChars(String text, char[] chars, int offset, int length) {
        if (_depth != 0 && length <= VPACK_STRING_SHORT_MAX_LEN / 3) {
            // At most 42 UTF-16 code units: even all three-byte characters fit
            // the short header. Reserve <=127 bytes locally and patch its byte
            // length after one encoding pass. No moves or global worst-case
            // reservation, and no length pass for the common small names/values.
            int start = _payloadTail;
            _ensurePayload((long) start + 1 + 3L * length);
            int end = _encodeUtf8(text, chars, offset, length, _payload, start + 1);
            _payload[start] = (byte) (VPACK_STRING_SHORT_FIRST + end - start - 1);
            _payloadTail = end;
            return;
        }
        long bytes = _utf8Length(text, chars, offset, length);
        _writeStringHeader(bytes);
        if (_depth != 0) {
            int end = checkedArraySize((long) _payloadTail + bytes);
            _ensurePayload(end);
            _encodeUtf8(text, chars, offset, length, _payload, _payloadTail);
            _payloadTail = end;
        } else if (bytes <= _outputEnd) {
            if (bytes > _outputEnd - _outputTail) _flushBuffer();
            _encodeUtf8(text, chars, offset, length, _outputBuffer, _outputTail);
            _outputTail += (int) bytes;
        } else {
            // Huge root scalars never require a document arena. Emit bounded
            // chunks, preserving the legacy large-string visibility on return.
            _flushBuffer();
            _encodeRootUtf8(text, chars, offset, length);
            _flushBuffer();
        }
    }

    private static int _codePoint(String text, char[] chars, int pos, int end) {
        char c = _charAt(text, chars, pos);
        if (c < 0xd800 || c > 0xdfff) return c;
        if (c <= 0xdbff && pos + 1 < end) {
            char low = _charAt(text, chars, pos + 1);
            if (Character.isLowSurrogate(low)) return Character.toCodePoint(c, low);
        }
        return '?';
    }

    private static int _encodeUtf8(String text, char[] chars, int offset, int length,
            byte[] target, int pos) {
        for (int i = offset, end = offset + length; i < end; ++i) {
            int c = _charAt(text, chars, i);
            if (c < 0x80) target[pos++] = (byte) c;
            else if (c < 0x800) {
                target[pos++] = (byte) (0xc0 | (c >> 6));
                target[pos++] = (byte) (0x80 | (c & 63));
            } else {
                if (c >= 0xd800 && c <= 0xdfff) {
                    c = _codePoint(text, chars, i, end);
                    if (c == '?') { target[pos++] = (byte) c; continue; }
                }
                if (c >= 0x10000) {
                    ++i;
                    target[pos++] = (byte) (0xf0 | (c >> 18));
                    target[pos++] = (byte) (0x80 | ((c >> 12) & 63));
                } else target[pos++] = (byte) (0xe0 | (c >> 12));
                target[pos++] = (byte) (0x80 | ((c >> 6) & 63));
                target[pos++] = (byte) (0x80 | (c & 63));
            }
        }
        return pos;
    }

    private void _encodeRootUtf8(String text, char[] chars, int offset, int length) {
        for (int i = offset, end = offset + length; i < end; ++i) {
            int c = _codePoint(text, chars, i, end);
            if (c < 0x80) _rawByte((byte) c);
            else if (c < 0x800) {
                _rawByte((byte) (0xc0 | (c >> 6)));
                _rawByte((byte) (0x80 | (c & 63)));
            } else {
                if (c >= 0x10000) {
                    ++i;
                    _rawByte((byte) (0xf0 | (c >> 18)));
                    _rawByte((byte) (0x80 | ((c >> 12) & 63)));
                } else _rawByte((byte) (0xe0 | (c >> 12)));
                _rawByte((byte) (0x80 | ((c >> 6) & 63)));
                _rawByte((byte) (0x80 | (c & 63)));
            }
        }
    }

    private void _writeStringHeader(long bytes) {
        if (bytes <= VPACK_STRING_SHORT_MAX_LEN) {
            _rawByte((byte) (VPACK_STRING_SHORT_FIRST + bytes));
        } else {
            _rawByte((byte) VPACK_STRING_LONG);
            _rawLeUnsignedLong(bytes);
        }
    }

    protected void _doWriteUtf8Bytes(byte[] utf8, int offset, int len) throws JacksonException {
        _writeStringHeader(len);
        _rawBytes(utf8, offset, len);
    }

    /*
    /**********************************************************************
    /* Shared payload / primitive frame and direct-child stacks
    /**********************************************************************
     */

    private static int grownCapacity(int old, long required) {
        int size = checkedArraySize(required);
        return (int) Math.min(MAX_ARRAY_SIZE, Math.max(size, Math.max(256L, old + (old >> 1) + 1L)));
    }

    // Package-visible arithmetic helpers let tests reach width-8 and overflow
    // without allocating multi-gigabyte arrays. All sums/products are checked.
    static int checkedArraySize(long size) {
        if (size < 0 || size > MAX_ARRAY_SIZE) {
            throw new IllegalStateException("VPack payload exceeds Java array limit: " + size);
        }
        return (int) size;
    }

    static int vbyteWidth(long value) {
        if (value < 0) throw new IllegalStateException("Negative VByte size");
        int width = 1;
        while ((value >>>= 7) != 0) ++width;
        return width;
    }

    static long compactSize(long content, long count) {
        if (content < 0 || count < 0) throw new IllegalStateException("Negative container size/count");
        try {
            long base = Math.addExact(Math.addExact(1L, content), vbyteWidth(count));
            long length = Math.addExact(base, 1L);
            long next;
            while ((next = Math.addExact(base, vbyteWidth(length))) != length) length = next;
            return length;
        } catch (ArithmeticException e) {
            throw new IllegalStateException("VPack compact size overflow", e);
        }
    }

    static long framedSize(long content, long count, int width, boolean indexed) {
        if (content < 0 || count < 0 || (width != 1 && width != 2 && width != 4 && width != 8)) {
            throw new IllegalStateException("Invalid container size/count/width");
        }
        try {
            long length = Math.addExact(content, indexed && width < 8 ? 1L + 2L * width : 1L + width);
            if (indexed) {
                length = Math.addExact(length, Math.multiplyExact(count, width));
                if (width == 8) length = Math.addExact(length, 8L);
            }
            return length;
        } catch (ArithmeticException e) {
            throw new IllegalStateException("VPack indexed size overflow", e);
        }
    }

    static int containerWidth(long content, long count, boolean indexed) {
        for (int w = 1; ; w = nextWidth(w)) {
            if (framedSize(content, count, w, indexed) <= maxForWidth(w)) return w;
            if (w == 8) throw new IllegalStateException("VPack container too large");
        }
    }

    private void _ensurePayload(long required) {
        if (_payload == null) _payload = new byte[grownCapacity(0, required)];
        else if (required > _payload.length) {
            _payload = Arrays.copyOf(_payload, grownCapacity(_payload.length, required));
        }
    }

    private void _appendOffset(int start) {
        if (_offsetTail == _offsets.length) {
            int capacity = grownCapacity(_offsets.length, (long) _offsetTail + 1);
            _offsets = Arrays.copyOf(_offsets, capacity);
            _keyEnds = Arrays.copyOf(_keyEnds, capacity);
        }
        _offsets[_offsetTail++] = start;
    }

    private void _openContainer(boolean object) {
        if (_depth == _frameStarts.length) {
            int capacity = grownCapacity(_depth, (long) _depth + 1);
            _frameStarts = Arrays.copyOf(_frameStarts, capacity);
            _frameBases = Arrays.copyOf(_frameBases, capacity);
            _frameCounts = Arrays.copyOf(_frameCounts, capacity);
            _frameNext = Arrays.copyOf(_frameNext, capacity);
            _frameObjects = Arrays.copyOf(_frameObjects, capacity);
        }
        _ensurePayload((long) _payloadTail + HEADER_RESERVATION);
        _frameStarts[_depth] = _payloadTail;
        _payloadTail += HEADER_RESERVATION;
        _frameBases[_depth] = _offsetTail;
        _frameCounts[_depth] = 0;
        _frameNext[_depth] = _payloadTail;
        _frameObjects[_depth++] = object;
    }

    private void _startPair() throws JacksonException {
        if (_depth == 0 || !_frameObjects[_depth - 1]) {
            _reportError("Cannot write a property name outside an object");
        }
        if (_frameNext[_depth - 1] != _payloadTail) {
            _reportError("Tagged prefix must follow the property name");
        }
        _appendOffset(_payloadTail);
    }

    private void _verifyRootRaw() throws JacksonException {
        if (_depth != 0) {
            _reportError("Nested writeRaw/writeBytes fragments are ambiguous; use writeRawValue for one complete value or writeTaggedValuePrefix");
        }
    }

    protected void _valueFinished() throws JacksonException {
        if (_depth != 0) {
            int frame = _depth - 1;
            ++_frameCounts[frame];
            _frameNext[frame] = _payloadTail;
        }
    }

    private void _closeContainer() throws JacksonException {
        int frame = _depth - 1;
        int start = _frameStarts[frame], base = _frameBases[frame], count = _frameCounts[frame];
        boolean object = _frameObjects[frame];
        if (_offsetTail - base != count || _frameNext[frame] != _payloadTail) {
            _reportError("Incomplete container value or dangling tagged prefix");
        }
        int contentStart = start + HEADER_RESERVATION;
        int contentLength = _payloadTail - contentStart;
        if (count == 0) {
            _payload[start] = (byte) (object ? VPACK_OBJECT_EMPTY : VPACK_ARRAY_EMPTY);
            _payloadTail = start + 1;
        } else {
            boolean compact = (object ? VPackWriteFeature.WRITE_COMPACT_OBJECTS
                    : VPackWriteFeature.WRITE_COMPACT_ARRAYS).enabledIn(_formatFeatures);
            boolean sorted = object && VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED.enabledIn(_formatFeatures);
            boolean indexed = object;
            if (!object && !compact) {
                int firstLength = (count == 1 ? _payloadTail : _offsets[base + 1]) - _offsets[base];
                for (int i = 1; i < count; ++i) {
                    int end = i + 1 == count ? _payloadTail : _offsets[base + i + 1];
                    if (end - _offsets[base + i] != firstLength) { indexed = true; break; }
                }
            }
            // None is not an on-wire value. In particular, it cannot masquerade
            // as optional padding in a no-index array. Nested rawValue checks it
            // at write time; valid encoders always start with a nonzero type byte.
            long length = compact ? compactSize(contentLength, count)
                    : framedSize(contentLength, count, containerWidth(contentLength, count, indexed), indexed);
            int total = checkedArraySize(length);
            int end = checkedArraySize(Math.addExact((long) start, length));
            int width = compact ? vbyteWidth(length) : containerWidth(contentLength, count, indexed);
            int header = compact ? 1 + width : indexed && width < 8 ? 1 + 2 * width : 1 + width;
            _ensurePayload(end);
            if (sorted) _sortPairs(base, count, contentStart, contentLength);
            // Exactly one overlapping move for header compaction, independent
            // of item count. Sorting (when needed) incurs two extra content copies.
            if (header != HEADER_RESERVATION) {
                System.arraycopy(_payload, contentStart, _payload, start + header, contentLength);
            }
            if (compact) {
                _payload[start] = (byte) (object ? VPACK_OBJECT_COMPACT : VPACK_ARRAY_COMPACT);
                _writeVByte(start + 1, length, false);
                _writeVByte(start + header + contentLength, count, true);
            } else {
                int type = object ? sorted ? VPACK_OBJECT_SORTED_FIRST : VPACK_OBJECT_UNSORTED_FIRST
                        : indexed ? VPACK_ARRAY_IDX_FIRST : VPACK_ARRAY_NO_IDX_FIRST;
                _payload[start] = (byte) (type + widthIdx(width));
                VPackUtil.writeLeUnsigned(_payload, start + 1, total, width);
                if (indexed) {
                    if (width < 8) VPackUtil.writeLeUnsigned(_payload, start + 1 + width, count, width);
                    int pos = start + header + contentLength;
                    for (int i = 0; i < count; ++i, pos += width) {
                        VPackUtil.writeLeUnsigned(_payload, pos, _offsets[base + i] - contentStart + header, width);
                    }
                    if (width == 8) VPackUtil.writeLeUnsigned(_payload, pos, count, 8);
                }
            }
            _payloadTail = end;
        }
        // Retire all direct-child metadata. Parent boundaries and ancestor
        // starts precede this segment/content and need no relocation.
        _offsetTail = base;
        --_depth;
        _streamWriteContext = _streamWriteContext.getParent();
        if (_depth == 0) {
            int length = _payloadTail;
            _payloadTail = 0;
            _offsetTail = 0;
            _flushBuffer();
            try {
                _out.write(_payload, 0, length);
            } catch (IOException e) {
                throw _outputFailure(e);
            }
        } else {
            _valueFinished();
        }
    }

    private void _writeVByte(int pos, long value, boolean reverse) {
        int width = vbyteWidth(value);
        if (reverse) pos += width - 1;
        do {
            _payload[pos] = (byte) ((value & 127) | (value > 127 ? 128 : 0));
            pos += reverse ? -1 : 1;
            value >>>= 7;
        } while (value != 0);
    }

    // Compare UTF-8 spans without extracting arrays. Non-string numeric IDs
    // retain the legacy unsigned lexicographic comparison of their encoded bytes.
    private int _comparePairKeys(int a, int b) {
        int pa = _offsets[a], pb = _offsets[b];
        int ea = _keyEnds[a], eb = _keyEnds[b];
        int ta = _payload[pa] & 255, tb = _payload[pb] & 255;
        if (ta >= VPACK_STRING_SHORT_FIRST && ta <= VPACK_STRING_SHORT_LAST) ++pa;
        else if (ta == VPACK_STRING_LONG) pa += 9;
        if (tb >= VPACK_STRING_SHORT_FIRST && tb <= VPACK_STRING_SHORT_LAST) ++pb;
        else if (tb == VPACK_STRING_LONG) pb += 9;
        int la = ea - pa, lb = eb - pb;
        for (int i = 0, n = Math.min(la, lb); i < n; ++i) {
            int d = (_payload[pa + i] & 255) - (_payload[pb + i] & 255);
            if (d != 0) return d;
        }
        return la - lb;
    }

    private void _sortPairs(int base, int count, int contentStart, int contentLength) {
        boolean ordered = true;
        for (int i = 1; i < count; ++i) {
            if (_comparePairKeys(base + i - 1, base + i) > 0) { ordered = false; break; }
        }
        if (ordered) return;
        if (_sortOrder == null || _sortOrder.length < count) {
            int capacity = grownCapacity(_sortOrder == null ? 0 : _sortOrder.length, count);
            _sortOrder = new int[capacity];
            _sortWork = new int[capacity];
        }
        for (int i = 0; i < count; ++i) _sortOrder[i] = base + i;
        // Stable bottom-up merge sort of primitive pair IDs.
        for (long run = 1; run < count; run *= 2) {
            for (long lo = 0; lo < count; lo += 2 * run) {
                int l = (int) lo, mid = (int) Math.min(lo + run, count);
                int r = mid, hi = (int) Math.min(lo + 2 * run, count), out = l;
                while (l < mid && r < hi) {
                    _sortWork[out++] = _comparePairKeys(_sortOrder[l], _sortOrder[r]) <= 0
                            ? _sortOrder[l++] : _sortOrder[r++];
                }
                while (l < mid) _sortWork[out++] = _sortOrder[l++];
                while (r < hi) _sortWork[out++] = _sortOrder[r++];
            }
            int[] swap = _sortOrder; _sortOrder = _sortWork; _sortWork = swap;
        }
        if (_sortScratch == null || _sortScratch.length < contentLength) {
            _sortScratch = new byte[grownCapacity(_sortScratch == null ? 0 : _sortScratch.length, contentLength)];
        }
        int pos = 0;
        for (int i = 0; i < count; ++i) {
            int id = _sortOrder[i], from = _offsets[id];
            int end = id + 1 == base + count ? contentStart + contentLength : _offsets[id + 1];
            int len = end - from;
            System.arraycopy(_payload, from, _sortScratch, pos, len);
            _sortWork[i] = contentStart + pos; // new physical pair boundary
            pos += len;
        }
        System.arraycopy(_sortScratch, 0, _payload, contentStart, contentLength);
        System.arraycopy(_sortWork, 0, _offsets, base, count);
    }

    /*
    /**********************************************************************
    /* Raw byte routing to the payload or the separate root output buffer
    /**********************************************************************
     */

    protected void _rawByte(byte b) throws JacksonException {
        if (_depth != 0) {
            _ensurePayload((long) _payloadTail + 1);
            _payload[_payloadTail++] = b;
        } else {
            if (_outputTail >= _outputEnd) _flushBuffer();
            _outputBuffer[_outputTail++] = b;
        }
    }

    protected void _rawBytes(byte[] data, int offset, int len) throws JacksonException {
        Objects.checkFromIndexSize(offset, len, data.length);
        if (_depth != 0) {
            _ensurePayload((long) _payloadTail + len);
            System.arraycopy(data, offset, _payload, _payloadTail, len);
            _payloadTail += len;
        } else {
            if (len > _outputEnd - _outputTail) {
                _flushBuffer();
                if (len > _outputEnd) {
                    try {
                        _out.write(data, offset, len);
                    } catch (IOException e) {
                        throw _outputFailure(e);
                    }
                    return;
                }
            }
            System.arraycopy(data, offset, _outputBuffer, _outputTail, len);
            _outputTail += len;
        }
    }

    protected void _rawLeWidth(long v, int w) throws JacksonException {
        if (_depth != 0) {
            _ensurePayload((long) _payloadTail + w);
            VPackUtil.writeLeUnsigned(_payload, _payloadTail, v, w);
            _payloadTail += w;
        } else {
            if (w > _outputEnd - _outputTail) _flushBuffer();
            // Caller-owned buffers can be smaller than a numeric value.
            while (w > 0) {
                int n = Math.min(w, _outputEnd - _outputTail);
                VPackUtil.writeLeUnsigned(_outputBuffer, _outputTail, v, n);
                _outputTail += n;
                w -= n;
                if (w > 0) { v >>>= n * 8; _flushBuffer(); }
            }
        }
    }

    protected void _rawLeUnsignedLong(long v) throws JacksonException {
        _rawLeWidth(v, 8);
    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws JacksonException {
        _checkOutputFailure();
        if (!_streamWriteContext.writeValue()) {
            _reportError("Can not " + typeMsg + ", expecting field name (context: "
                    + _streamWriteContext.typeDesc() + ")");
        }
        if (_depth != 0 && !_frameObjects[_depth - 1]) {
            // Include any explicit tags written since the previous completion.
            _appendOffset(_frameNext[_depth - 1]);
        }
    }

    /*
    /**********************************************************************
    /* Flush / close
    /**********************************************************************
     */

    @Override
    public void flush() throws JacksonException {
        _flushBuffer();
        try {
            _out.flush();
        } catch (IOException e) {
            throw _outputFailure(e);
        }
    }

    @Override
    protected void _closeInput() throws JacksonException {
        JacksonException failure = null;
        try {
            _flushBuffer();
        } catch (JacksonException e) {
            failure = e;
        }
        try {
            if (_ioContext.isResourceManaged()
                    || isEnabled(StreamWriteFeature.AUTO_CLOSE_TARGET)) {
                _out.close();
            } else if (isEnabled(StreamWriteFeature.FLUSH_PASSED_TO_STREAM)) {
                _out.flush();
            }
        } catch (IOException e) {
            JacksonException targetFailure = _outputFailure(e);
            if (failure == null) failure = targetFailure;
            else failure.addSuppressed(targetFailure);
        }
        if (failure != null) throw failure;
    }

    @Override
    protected void _releaseBuffers() {
        byte[] buf = _outputBuffer;
        _outputBuffer = null;
        _outputTail = 0;
        _payload = null;
        _sortScratch = null;
        _sortOrder = _sortWork = null;
        _frameStarts = _frameBases = _frameCounts = _frameNext = null;
        _frameObjects = null;
        _offsets = _keyEnds = null;
        _payloadTail = _offsetTail = _depth = 0;
        if (_bufferRecyclable && buf != null) {
            _ioContext.releaseWriteEncodingBuffer(buf);
        }
    }

    protected void _flushBuffer() throws JacksonException {
        if (_outputTail > 0 && _depth == 0) {
            int length = _outputTail;
            _outputTail = 0;
            try {
                _out.write(_outputBuffer, 0, length);
            } catch (IOException e) {
                throw _outputFailure(e);
            }
        }
    }

    private JacksonException _outputFailure(IOException e) {
        // A stream may throw after writing an arbitrary prefix. Retrying any
        // pending bytes would duplicate it. Further values cannot be reliable.
        _outputFailed = true;
        _outputTail = _payloadTail = 0;
        return _wrapIOFailure(e);
    }

    private void _checkOutputFailure() {
        if (_outputFailed) _reportError("Cannot write after an output failure");
    }

    /*
    /**********************************************************************
    /* Helper statics
    /**********************************************************************
     */

    protected static long maxForWidth(int w) {
        return (w >= 8) ? Long.MAX_VALUE : (1L << (w * 8)) - 1L;
    }

    protected static int widthIdx(int w) {
        return switch (w) {
            case 1 -> 0;
            case 2 -> 1;
            case 4 -> 2;
            default -> 3;
        };
    }

    protected static int nextWidth(int w) {
        return (w == 1) ? 2 : (w == 2) ? 4 : 8;
    }

}
