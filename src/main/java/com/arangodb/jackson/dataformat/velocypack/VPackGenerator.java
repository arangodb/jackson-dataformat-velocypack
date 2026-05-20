package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.SimpleStreamWriteContext;
import tools.jackson.databind.cfg.PackageVersion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.arangodb.jackson.dataformat.velocypack.VPackConstants.*;

/**
 * {@link JsonGenerator} implementation for VelocyPack-encoded content.
 *<p>
 * Key design decisions:
 * <ul>
 *   <li>All multi-byte integers are little-endian (per spec).
 *   <li>Arrays and objects are buffered in memory. On close, the complete VPack
 *       structure is emitted. This is required because:
 *       (a) object keys must be sorted, and
 *       (b) byte lengths must be filled before the content.
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
    protected boolean _bufferRecyclable;

    /*
    /**********************************************************************
    /* Nested container buffering
    /**********************************************************************
     */

    /**
     * Stack of open containers. The top is the innermost container being written.
     * When a value is written, its bytes go to the top container's current capture buffer.
     */
    protected final Deque<ContainerState> _containerStack = new ArrayDeque<>();

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
        return DEFAULT_BINARY_WRITE_CAPABILITIES;
    }

    @Override
    public Object streamWriteOutputTarget() { return _out; }

    @Override
    public int streamWriteOutputBuffered() { return _outputTail; }

    @Override
    public PrettyPrinter getPrettyPrinter() { return null; }

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
     * Note: this does not go through the normal value-write path; the caller
     * is responsible for correctly placing the prefix before a value token.
     */
    public void writeTaggedValuePrefix(long tagNumber) throws JacksonException {
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

    public JsonGenerator writeRaw(byte b) throws JacksonException {
        _verifyValueWrite("write raw byte");
        _rawByte(b);
        return this;
    }

    public JsonGenerator writeBytes(byte[] data, int offset, int len) throws JacksonException {
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
        _containerStack.push(new ContainerState(false));
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(Object forValue) throws JacksonException {
        _verifyValueWrite("start an array");
        _streamWriteContext = _streamWriteContext.createChildArrayContext(forValue);
        _containerStack.push(new ContainerState(false));
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(Object forValue, int size) throws JacksonException {
        _verifyValueWrite("start an array");
        _streamWriteContext = _streamWriteContext.createChildArrayContext(forValue);
        _containerStack.push(new ContainerState(false));
        return this;
    }

    @Override
    public JsonGenerator writeEndArray() throws JacksonException {
        if (!_streamWriteContext.inArray()) {
            _reportError("Current context not Array but " + _streamWriteContext.typeDesc());
        }
        _streamWriteContext = _streamWriteContext.getParent();
        ContainerState cs = _containerStack.pop();
        _emitValue(_buildArray(cs));
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
        _containerStack.push(new ContainerState(true));
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(Object forValue) throws JacksonException {
        _verifyValueWrite("start an object");
        _streamWriteContext = _streamWriteContext.createChildObjectContext(forValue);
        _containerStack.push(new ContainerState(true));
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(Object forValue, int size) throws JacksonException {
        _verifyValueWrite("start an object");
        _streamWriteContext = _streamWriteContext.createChildObjectContext(forValue);
        _containerStack.push(new ContainerState(true));
        return this;
    }

    @Override
    public JsonGenerator writeEndObject() throws JacksonException {
        if (!_streamWriteContext.inObject()) {
            _reportError("Current context not Object but " + _streamWriteContext.typeDesc());
        }
        _streamWriteContext = _streamWriteContext.getParent();
        ContainerState cs = _containerStack.pop();
        _emitValue(_buildObject(cs));
        return this;
    }

    /*
    /**********************************************************************
    /* Property names
    /**********************************************************************
     */

    @Override
    public JsonGenerator writeName(String name) throws JacksonException {
        if (!_streamWriteContext.writeName(name)) {
            _reportError("Cannot write a property name, expecting a value");
        }
        // Write key to the current object's key buffer
        ContainerState cs = _containerStack.peek();
        cs.startKey();
        _doWriteString(name);
        cs.finishKey();
        return this;
    }

    @Override
    public JsonGenerator writeName(SerializableString name) throws JacksonException {
        return writeName(name.getValue());
    }

    @Override
    public JsonGenerator writePropertyId(long id) throws JacksonException {
        if (!_streamWriteContext.writeName(String.valueOf(id))) {
            _reportError("Cannot write a property name, expecting a value");
        }
        ContainerState cs = _containerStack.peek();
        cs.startKey();
        _doWriteUnsignedInt(id);
        cs.finishKey();
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
        _doWriteString(new String(text, offset, len));
        _valueFinished();
        return this;
    }

    @Override
    public JsonGenerator writeString(SerializableString sstr) throws JacksonException {
        _verifyValueWrite("write a string value");
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
        byte[] buf = new byte[dataLength];
        int offset = 0;
        try {
            while (offset < dataLength) {
                int n = data.read(buf, offset, dataLength - offset);
                if (n < 0) {
                    throw new StreamWriteException(this,
                            "End of stream before all binary data read: expected "
                            + dataLength + " bytes, got " + offset);
                }
                offset += n;
            }
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        _doWriteBinary(buf, 0, dataLength);
        _valueFinished();
        return dataLength;
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
                _doWriteBigDecimal(new BigDecimal(encodedValue));
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
        _verifyValueWrite("write int array");
        writeStartArray(array, length);
        for (int i = offset, end = offset + length; i < end; i++) {
            writeNumber(array[i]);
        }
        writeEndArray();
        return this;
    }

    @Override
    public JsonGenerator writeArray(long[] array, int offset, int length) throws JacksonException
    {
        _verifyValueWrite("write long array");
        writeStartArray(array, length);
        for (int i = offset, end = offset + length; i < end; i++) {
            writeNumber(array[i]);
        }
        writeEndArray();
        return this;
    }

    @Override
    public JsonGenerator writeArray(double[] array, int offset, int length) throws JacksonException
    {
        _verifyValueWrite("write double array");
        writeStartArray(array, length);
        for (int i = offset, end = offset + length; i < end; i++) {
            writeNumber(array[i]);
        }
        writeEndArray();
        return this;
    }

    /*
    /**********************************************************************
    /* Internal: value-type encoding (_do* methods write to current capture buf)
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
        // 4-byte LE signed exponent
        _rawByte((byte) (exp & 0xFF));
        _rawByte((byte) ((exp >> 8) & 0xFF));
        _rawByte((byte) ((exp >> 16) & 0xFF));
        _rawByte((byte) ((exp >> 24) & 0xFF));
        _rawBytes(bcd, 0, bcd.length);
    }

    protected void _doWriteBinary(byte[] data, int offset, int len) throws JacksonException {
        int lenWidth = VPackUtil.unsignedByteWidth(len);
        int typeByte = VPACK_BINARY_FIRST + lenWidth - 1;
        _rawByte((byte) typeByte);
        _rawLeWidth(len, lenWidth);
        _rawBytes(data, offset, len);
    }

    protected void _doWriteString(String text) throws JacksonException {
        byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
        _doWriteUtf8Bytes(utf8, 0, utf8.length);
    }

    protected void _doWriteUtf8Bytes(byte[] utf8, int offset, int len) throws JacksonException {
        if (len <= VPACK_STRING_SHORT_MAX_LEN) {
            _rawByte((byte) (VPACK_STRING_SHORT_FIRST + len));
            _rawBytes(utf8, offset, len);
        } else {
            _rawByte((byte) VPACK_STRING_LONG);
            _rawLeUnsignedLong(len);
            _rawBytes(utf8, offset, len);
        }
    }

    /*
    /**********************************************************************
    /* Container serialization: arrays
    /**********************************************************************
     */

    protected byte[] _buildArray(ContainerState cs) throws JacksonException {
        List<byte[]> items = cs.items;
        if (items.isEmpty()) {
            return new byte[] { (byte) VPACK_ARRAY_EMPTY };
        }
        if (VPackWriteFeature.WRITE_COMPACT_ARRAYS.enabledIn(_formatFeatures)) {
            return _buildCompactArray(items);
        }
        // Check if all items have same length
        boolean allSame = true;
        int firstLen = items.get(0).length;
        for (byte[] item : items) {
            if (item.length != firstLen) { allSame = false; break; }
        }
        return allSame ? _buildNoIndexArray(items, firstLen) : _buildIndexArray(items);
    }

    protected byte[] _buildNoIndexArray(List<byte[]> items, int itemLen) {
        int n = items.size();
        int contentLen = n * itemLen;
        for (int w = 1; w <= 8; w = nextWidth(w)) {
            long totalLen = 1L + w + contentLen;
            if (totalLen <= maxForWidth(w)) {
                byte[] result = new byte[(int) totalLen];
                result[0] = (byte) (VPACK_ARRAY_NO_IDX_FIRST + widthIdx(w));
                VPackUtil.writeLeUnsigned(result, 1, totalLen, w);
                int pos = 1 + w;
                for (byte[] item : items) {
                    System.arraycopy(item, 0, result, pos, item.length);
                    pos += item.length;
                }
                return result;
            }
        }
        throw new IllegalStateException("Array too large");
    }

    protected byte[] _buildIndexArray(List<byte[]> items) {
        int n = items.size();
        int[] offsets = new int[n];
        for (int w = 1; w <= 8; w = nextWidth(w)) {
            int hdrSize = 1 + (w == 8 ? w : 2 * w); // for 0x09, nritems is at end
            // For 0x06-0x08: header = type + byteLen(w) + nritems(w) = 1+2w
            // For 0x09: header = type + byteLen(8) = 1+8
            // Let's recalculate:
            int headerSize = (w == 8) ? (1 + 8) : (1 + 2 * w);
            int contentLen = 0;
            for (int i = 0; i < n; i++) {
                offsets[i] = headerSize + contentLen;
                contentLen += items.get(i).length;
            }
            int idxTableSize = n * w;
            long totalLen = (w == 8)
                    ? (long) headerSize + contentLen + idxTableSize + 8L  // nritems at end
                    : (long) headerSize + contentLen + idxTableSize;
            if (totalLen <= maxForWidth(w)) {
                byte[] result = new byte[(int) totalLen];
                int pos = 0;
                result[pos++] = (byte) (VPACK_ARRAY_IDX_FIRST + widthIdx(w));
                VPackUtil.writeLeUnsigned(result, pos, totalLen, w);
                pos += w;
                if (w < 8) {
                    VPackUtil.writeLeUnsigned(result, pos, n, w);
                    pos += w;
                }
                // items
                for (byte[] item : items) {
                    System.arraycopy(item, 0, result, pos, item.length);
                    pos += item.length;
                }
                // index table
                for (int offset : offsets) {
                    VPackUtil.writeLeUnsigned(result, pos, offset, w);
                    pos += w;
                }
                if (w == 8) {
                    VPackUtil.writeLeUnsigned(result, pos, n, 8);
                }
                return result;
            }
        }
        throw new IllegalStateException("Array too large");
    }

    protected byte[] _buildCompactArray(List<byte[]> items) {
        int n = items.size();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (byte[] item : items) buf.write(item, 0, item.length);
        byte[] content = buf.toByteArray();
        byte[] nrVB = VPackUtil.encodeVByte(n);
        for (int bw = 1; bw <= 8; bw++) {
            long totalLen = 1L + bw + content.length + nrVB.length;
            byte[] lenVB = VPackUtil.encodeVByte(totalLen);
            if (lenVB.length == bw) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(VPACK_ARRAY_COMPACT);
                out.write(lenVB, 0, lenVB.length);
                out.write(content, 0, content.length);
                out.write(reverseBytes(nrVB), 0, nrVB.length);
                return out.toByteArray();
            }
        }
        throw new IllegalStateException("Compact array too large");
    }

    /*
    /**********************************************************************
    /* Container serialization: objects
    /**********************************************************************
     */

    protected byte[] _buildObject(ContainerState cs) throws JacksonException {
        List<byte[]> keys = cs.keys;
        List<byte[]> values = cs.values;
        int n = keys.size();
        if (n == 0) {
            return new byte[] { (byte) VPACK_OBJECT_EMPTY };
        }
        if (VPackWriteFeature.WRITE_COMPACT_OBJECTS.enabledIn(_formatFeatures)) {
            return _buildCompactObject(keys, values);
        }
        boolean sorted = VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED.enabledIn(_formatFeatures);
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        if (sorted) {
            Arrays.sort(order, (a, b) -> _compareKeys(keys.get(a), keys.get(b)));
        }
        return _buildIndexObject(keys, values, order, sorted);
    }

    protected byte[] _buildIndexObject(List<byte[]> keys, List<byte[]> values,
            Integer[] order, boolean sorted)
    {
        int n = order.length;
        int[] pairOffsets = new int[n];
        for (int w = 1; w <= 8; w = nextWidth(w)) {
            int headerSize = (w == 8) ? (1 + 8) : (1 + 2 * w);
            int contentLen = 0;
            for (int i = 0; i < n; i++) {
                int idx = order[i];
                pairOffsets[i] = headerSize + contentLen;
                contentLen += keys.get(idx).length + values.get(idx).length;
            }
            int idxTableSize = n * w;
            long totalLen = (w == 8)
                    ? (long) headerSize + contentLen + idxTableSize + 8L
                    : (long) headerSize + contentLen + idxTableSize;
            if (totalLen <= maxForWidth(w)) {
                int typeBase = sorted ? VPACK_OBJECT_SORTED_FIRST : VPACK_OBJECT_UNSORTED_FIRST;
                byte[] result = new byte[(int) totalLen];
                int pos = 0;
                result[pos++] = (byte) (typeBase + widthIdx(w));
                VPackUtil.writeLeUnsigned(result, pos, totalLen, w);
                pos += w;
                if (w < 8) {
                    VPackUtil.writeLeUnsigned(result, pos, n, w);
                    pos += w;
                }
                for (int i = 0; i < n; i++) {
                    int idx = order[i];
                    byte[] k = keys.get(idx);
                    byte[] v = values.get(idx);
                    System.arraycopy(k, 0, result, pos, k.length);
                    pos += k.length;
                    System.arraycopy(v, 0, result, pos, v.length);
                    pos += v.length;
                }
                for (int i = 0; i < n; i++) {
                    VPackUtil.writeLeUnsigned(result, pos, pairOffsets[i], w);
                    pos += w;
                }
                if (w == 8) {
                    VPackUtil.writeLeUnsigned(result, pos, n, 8);
                }
                return result;
            }
        }
        throw new IllegalStateException("Object too large");
    }

    protected byte[] _buildCompactObject(List<byte[]> keys, List<byte[]> values) {
        int n = keys.size();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        if (VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED.enabledIn(_formatFeatures)) {
            Arrays.sort(order, (a, b) -> _compareKeys(keys.get(a), keys.get(b)));
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (int i = 0; i < n; i++) {
            int idx = order[i];
            buf.write(keys.get(idx), 0, keys.get(idx).length);
            buf.write(values.get(idx), 0, values.get(idx).length);
        }
        byte[] content = buf.toByteArray();
        byte[] nrVB = VPackUtil.encodeVByte(n);
        for (int bw = 1; bw <= 8; bw++) {
            long totalLen = 1L + bw + content.length + nrVB.length;
            byte[] lenVB = VPackUtil.encodeVByte(totalLen);
            if (lenVB.length == bw) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(VPACK_OBJECT_COMPACT);
                out.write(lenVB, 0, lenVB.length);
                out.write(content, 0, content.length);
                out.write(reverseBytes(nrVB), 0, nrVB.length);
                return out.toByteArray();
            }
        }
        throw new IllegalStateException("Compact object too large");
    }

    protected int _compareKeys(byte[] ka, byte[] kb) {
        byte[] a = _extractStringBytes(ka);
        byte[] b = _extractStringBytes(kb);
        int minLen = Math.min(a.length, b.length);
        for (int i = 0; i < minLen; i++) {
            int d = (a[i] & 0xFF) - (b[i] & 0xFF);
            if (d != 0) return d;
        }
        return a.length - b.length;
    }

    protected byte[] _extractStringBytes(byte[] vpackStr) {
        if (vpackStr.length == 0) return new byte[0];
        int tb = vpackStr[0] & 0xFF;
        if (tb >= VPACK_STRING_SHORT_FIRST && tb <= VPACK_STRING_SHORT_LAST) {
            int len = tb - VPACK_STRING_SHORT_FIRST;
            return Arrays.copyOfRange(vpackStr, 1, 1 + len);
        }
        if (tb == VPACK_STRING_LONG) {
            long len = VPackUtil.readLeUnsigned(vpackStr, 1, 8);
            return Arrays.copyOfRange(vpackStr, 9, 9 + (int) len);
        }
        return vpackStr;
    }

    /*
    /**********************************************************************
    /* Emit completed value into parent / output
    /**********************************************************************
     */

    /**
     * Called after encoding a complete scalar value. If we're inside a container,
     * the bytes go to the container's current capture buffer. Then we finalize.
     */
    protected void _valueFinished() throws JacksonException {
        if (_containerStack.isEmpty()) return;
        ContainerState cs = _containerStack.peek();
        if (cs.isObject) {
            // Value bytes are in cs.currentCapture; finalize as value
            cs.finishValue();
        } else {
            // Array: finalize item
            cs.finishItem();
        }
    }

    /**
     * Called when a complete composite value (array or object) has been built.
     * Writes the byte[] into the parent context, or to the output stream.
     */
    protected void _emitValue(byte[] bytes) throws JacksonException {
        if (!_containerStack.isEmpty()) {
            ContainerState parent = _containerStack.peek();
            if (parent.isObject) {
                // It's a value for the object (key was already written)
                parent.addValueBytes(bytes);
            } else {
                parent.addItemBytes(bytes);
            }
        } else {
            // Root level: write to output
            _flushBuffer();
            try {
                _out.write(bytes);
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            }
        }
    }

    /*
    /**********************************************************************
    /* Raw byte routing: goes to current capture buffer or output buffer
    /**********************************************************************
     */

    /**
     * Writes a byte to the currently active capture buffer:
     * - If inside a container: to the container's currentCapture stream
     * - Otherwise: to the output buffer
     */
    protected void _rawByte(byte b) throws JacksonException {
        if (!_containerStack.isEmpty()) {
            _containerStack.peek().currentCapture.write(b);
        } else {
            if (_outputTail >= _outputEnd) {
                _flushBuffer();
            }
            _outputBuffer[_outputTail++] = b;
        }
    }

    protected void _rawBytes(byte[] data, int offset, int len) throws JacksonException {
        if (!_containerStack.isEmpty()) {
            _containerStack.peek().currentCapture.write(data, offset, len);
        } else {
            if (_outputTail + len > _outputEnd) {
                _flushBuffer();
                if (len > _outputEnd) {
                    try {
                        _out.write(data, offset, len);
                    } catch (IOException e) {
                        throw _wrapIOFailure(e);
                    }
                    return;
                }
            }
            System.arraycopy(data, offset, _outputBuffer, _outputTail, len);
            _outputTail += len;
        }
    }

    protected void _rawLeWidth(long v, int w) throws JacksonException {
        for (int i = 0; i < w; i++) {
            _rawByte((byte) (v & 0xFF));
            v >>>= 8;
        }
    }

    protected void _rawLeUnsignedLong(long v) throws JacksonException {
        for (int i = 0; i < 8; i++) {
            _rawByte((byte) (v & 0xFF));
            v >>>= 8;
        }
    }

    /*
    /**********************************************************************
    /* Context verification
    /**********************************************************************
     */

    @Override
    protected void _verifyValueWrite(String typeMsg) throws JacksonException {
        if (!_streamWriteContext.writeValue()) {
            _reportError("Can not " + typeMsg + ", expecting field name (context: "
                    + _streamWriteContext.typeDesc() + ")");
        }
        // For objects: set currentCapture to value capture buffer
        if (!_containerStack.isEmpty()) {
            ContainerState cs = _containerStack.peek();
            if (cs.isObject) {
                cs.startValue();
            }
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
            throw _wrapIOFailure(e);
        }
    }

    @Override
    protected void _closeInput() throws JacksonException {
        _flushBuffer();
        try {
            if (_ioContext.isResourceManaged()
                    || isEnabled(StreamWriteFeature.AUTO_CLOSE_TARGET)) {
                _out.close();
            } else if (isEnabled(StreamWriteFeature.FLUSH_PASSED_TO_STREAM)) {
                _out.flush();
            }
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        if (_outputBuffer != null && _bufferRecyclable) {
            byte[] buf = _outputBuffer;
            _outputBuffer = null;
            _ioContext.releaseWriteEncodingBuffer(buf);
        }
    }

    @Override
    protected void _releaseBuffers() {
        if (_bufferRecyclable && _outputBuffer != null) {
            byte[] buf = _outputBuffer;
            _outputBuffer = null;
            _ioContext.releaseWriteEncodingBuffer(buf);
        }
    }

    protected void _flushBuffer() throws JacksonException {
        if (_outputTail > 0 && _containerStack.isEmpty()) {
            try {
                _out.write(_outputBuffer, 0, _outputTail);
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            }
            _outputTail = 0;
        }
    }

    /*
    /**********************************************************************
    /* Helper statics
    /**********************************************************************
     */

    protected static byte[] reverseBytes(byte[] arr) {
        byte[] r = Arrays.copyOf(arr, arr.length);
        for (int i = 0, j = r.length - 1; i < j; i++, j--) {
            byte t = r[i]; r[i] = r[j]; r[j] = t;
        }
        return r;
    }

    protected static long maxForWidth(int w) {
        return (w >= 8) ? Long.MAX_VALUE : (1L << (w * 8)) - 1L;
    }

    protected static int widthIdx(int w) {
        switch (w) { case 1: return 0; case 2: return 1; case 4: return 2; default: return 3; }
    }

    protected static int nextWidth(int w) {
        return (w == 1) ? 2 : (w == 2) ? 4 : 8;
    }

    /*
    /**********************************************************************
    /* Inner class: ContainerState
    /**********************************************************************
     */

    /**
     * Tracks state for an open array or object container.
     *<p>
     * For arrays: writes go to {@code currentCapture}; on each item completion,
     * the captured bytes are added to {@code items}.
     *<p>
     * For objects: key writes go to {@code keyCapture}; value writes go to
     * {@code valueCapture}. On each pair completion, the pair is added to
     * {@code keys}/{@code values}.
     */
    protected static class ContainerState
    {
        final boolean isObject;

        // For arrays
        final List<byte[]> items = new ArrayList<>();

        // For objects
        final List<byte[]> keys = new ArrayList<>();
        final List<byte[]> values = new ArrayList<>();

        // Key capture buffer (for objects, while writing key)
        ByteArrayOutputStream keyCapture = new ByteArrayOutputStream();

        // Value capture buffer (for objects, while writing value)
        ByteArrayOutputStream valueCapture = new ByteArrayOutputStream();

        // Item capture buffer (for arrays, while writing each item)
        ByteArrayOutputStream itemCapture = new ByteArrayOutputStream();

        // Points to the currently active buffer for _rawByte writes
        ByteArrayOutputStream currentCapture;

        ContainerState(boolean isObject) {
            this.isObject = isObject;
            // Initially point at item/value capture
            currentCapture = isObject ? valueCapture : itemCapture;
        }

        /** Called when we start writing an object key */
        void startKey() {
            keyCapture.reset();
            currentCapture = keyCapture;
        }

        /** Called after finishing an object key */
        void finishKey() {
            keys.add(keyCapture.toByteArray());
            currentCapture = valueCapture; // ready for value
        }

        /** Called (from _verifyValueWrite) before writing an object value */
        void startValue() {
            valueCapture.reset();
            currentCapture = valueCapture;
        }

        /** Called after a scalar value in an object is finished */
        void finishValue() {
            values.add(valueCapture.toByteArray());
            currentCapture = valueCapture; // reset for next value
        }

        /** Called after a scalar value in an array is finished */
        void finishItem() {
            items.add(itemCapture.toByteArray());
            itemCapture.reset();
            currentCapture = itemCapture;
        }

        /** Called when a composite (array/object) value is added */
        void addValueBytes(byte[] bytes) {
            values.add(bytes);
        }

        void addItemBytes(byte[] bytes) {
            items.add(bytes);
        }
    }
}
