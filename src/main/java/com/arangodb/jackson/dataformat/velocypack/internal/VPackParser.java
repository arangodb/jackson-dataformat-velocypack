package com.arangodb.jackson.dataformat.velocypack.internal;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.ValueType;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Mark Vollmary
 * @author Michele Rastelli
 */
public class VPackParser extends ParserMinimalBase {
    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    /**
     * Codec used for data binding when (if) requested.
     */
    protected ObjectCodec objectCodec;

    /*
    /**********************************************************
    /* Generic I/O state
    /**********************************************************
     */

    /**
     * I/O context for this reader. It handles buffer allocation
     * for the reader.
     */
    protected final IOContext ioContext;

    /**
     * Flag that indicates whether parser is closed or not. Gets
     * set when parser is either closed by explicit call
     * ({@link #close}) or when end-of-input is reached.
     */
    protected boolean closed;

    /*
    /**********************************************************
    /* Parsing state
    /**********************************************************
     */

    protected VPackSlice currentValue;
    protected boolean beforeFirstToken;
    protected String currentName;
    protected final LinkedList<Iterator<Map.Entry<String, VPackSlice>>> objectIterators;
    protected final LinkedList<Iterator<VPackSlice>> arrayIterators;
    protected final LinkedList<JsonToken> currentCompoundValue;

    /*
    /**********************************************************
    /* Input source config, state (from ex StreamBasedParserBase)
    /**********************************************************
     */

    /**
     * Current buffer from which data is read; generally data is read into
     * buffer from input source, but in some cases pre-loaded buffer
     * is handed to the parser.
     */
    protected byte[] inputBuffer;

    /**
     * Flag that indicates whether the input buffer is recycable (and
     * needs to be returned to recycler once we are done) or not.
     * <p>
     * If it is not, it also means that parser can NOT modify underlying
     * buffer.
     */
    protected boolean bufferRecyclable;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public VPackParser(
            IOContext ctxt,
            int parserFeatures,
            ObjectCodec codec,
            byte[] inputBuffer,
            int start,
            boolean bufferRecyclable) {
        super(parserFeatures);
        ioContext = ctxt;
        objectCodec = codec;

        this.inputBuffer = inputBuffer;
        this.bufferRecyclable = bufferRecyclable;

        currentValue = new VPackSlice(inputBuffer, start);
        beforeFirstToken = true;
        _currToken = null;
        objectIterators = new LinkedList<>();
        arrayIterators = new LinkedList<>();
        currentCompoundValue = new LinkedList<>();
    }

    @Override
    public ObjectCodec getCodec() {
        return objectCodec;
    }

    @Override
    public void setCodec(ObjectCodec c) {
        objectCodec = c;
    }

    /*
    /**********************************************************
    /* Versioned
    /**********************************************************
     */

    @Override
    public Version version() {
        return com.fasterxml.jackson.databind.cfg.PackageVersion.VERSION;
    }

    /*
    /**********************************************************
    /* Abstract impls
    /**********************************************************
     */

    @Override
    public int releaseBuffered(OutputStream out) {
        return 0;
    }

    @Override
    public Object getInputSource() {
        return null;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return null;
    }

    @Override
    public JsonLocation getCurrentLocation() {
        return null;
    }

    /**
     * Method that can be called to get the name associated with
     * the current event.
     */
    @Override
    public String getCurrentName() {
        return currentName;
    }

    @Override
    public void overrideCurrentName(String name) {
        currentName = name;
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            releaseBuffers();
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public JsonStreamContext getParsingContext() {
        return null;
    }

    /*
    /**********************************************************
    /* Overridden methods
    /**********************************************************
     */

    protected void releaseBuffers() {
        if (bufferRecyclable) {
            byte[] buf = inputBuffer;
            if (buf != null) {
                inputBuffer = null;
                ioContext.releaseReadIOBuffer(buf);
            }
        }
    }

    /*
    /**********************************************************
    /* JsonParser impl
    /**********************************************************
     */

    @Override
    public JsonToken nextToken() {
        // ignore clear token
        if (_currToken == null && _lastClearedToken != null && !currentCompoundValue.isEmpty()) {
            _currToken = _lastClearedToken;
        }
        if (beforeFirstToken && _currToken == null) {
            beforeFirstToken = false;
            _currToken = getToken(currentValue.getType(), currentValue);
            return _currToken;
        }
        if (_currToken == JsonToken.START_OBJECT) {
            objectIterators.add(currentValue.objectIterator());
            currentCompoundValue.add(JsonToken.START_OBJECT);
        } else if (_currToken == JsonToken.START_ARRAY) {
            arrayIterators.add(currentValue.arrayIterator());
            currentCompoundValue.add(JsonToken.START_ARRAY);
        }
        if (_currToken == JsonToken.FIELD_NAME) {
            _currToken = getToken(currentValue.getType(), currentValue);
            return _currToken;
        }
        if (currentCompoundValue.isEmpty()) {
            close();
            return null;
        }
        if (currentCompoundValue.getLast() == JsonToken.START_OBJECT && !objectIterators.isEmpty()) {
            final Iterator<Map.Entry<String, VPackSlice>> lastObject = objectIterators.getLast();
            if (lastObject.hasNext()) {
                final Map.Entry<String, VPackSlice> next = lastObject.next();
                currentName = next.getKey();
                currentValue = next.getValue();
                _currToken = JsonToken.FIELD_NAME;
            } else {
                _currToken = JsonToken.END_OBJECT;
                objectIterators.removeLast();
                currentCompoundValue.removeLast();
            }
        } else if (currentCompoundValue.getLast() == JsonToken.START_ARRAY && !arrayIterators.isEmpty()) {
            final Iterator<VPackSlice> lastArray = arrayIterators.getLast();
            if (lastArray.hasNext()) {
                currentName = null;
                currentValue = lastArray.next();
                _currToken = getToken(currentValue.getType(), currentValue);
            } else {
                _currToken = JsonToken.END_ARRAY;
                arrayIterators.removeLast();
                currentCompoundValue.removeLast();
            }
        }
        return _currToken;
    }

    private JsonToken getToken(final ValueType type, final VPackSlice value) {
        final JsonToken token;
        switch (type) {
        case OBJECT:
            token = JsonToken.START_OBJECT;
            break;
        case ARRAY:
            token = JsonToken.START_ARRAY;
            break;
        case STRING:
            token = JsonToken.VALUE_STRING;
            break;
        case BOOL:
            token = value.isTrue() ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
            break;
        case DOUBLE:
            token = JsonToken.VALUE_NUMBER_FLOAT;
            break;
        case INT:
        case SMALLINT:
        case UINT:
            token = JsonToken.VALUE_NUMBER_INT;
            break;
        case NULL:
            token = JsonToken.VALUE_NULL;
            break;
        default:
            token = null;
            break;
        }
        return token;
    }

    /*
    /**********************************************************
    /* Public API, access to token information, text
    /**********************************************************
     */

    @Override
    public String getText() {
        if (_currToken == JsonToken.FIELD_NAME) {
            return currentName;
        } else if (currentValue.isNumber()) {
            return String.valueOf(getNumberValue());
        } else if (currentValue.isBoolean()) {
            return String.valueOf(currentValue.getAsBoolean());
        } else if (currentValue.isNull()) {
            return "null";
        } else if (currentValue.isString()) {
            return String.valueOf(currentValue.getAsString());
        } else {
            throw new UnsupportedOperationException("Calling getText() on " + currentValue.getType());
        }
    }

    @Override
    public char[] getTextCharacters() {
        return getText().toCharArray();
    }

    @Override
    public boolean hasTextCharacters() {
        return false;
    }

    @Override
    public int getTextLength() {
        return getTextCharacters().length;
    }

    @Override
    public int getTextOffset() {
        return 0;
    }

    /*
    /**********************************************************
    /* Public API, access to token information, binary
    /**********************************************************
     */

    @Override
    public byte[] getBinaryValue(final Base64Variant b64variant) {
        if (currentValue.isBinary()) {
            return currentValue.getAsBinary();
        } else if (currentValue.isString()) {
            return b64variant.decode(currentValue.getAsString());
        }
        return Arrays.copyOfRange(currentValue.getBuffer(), currentValue.getStart(),
                currentValue.getStart() + currentValue.getByteSize());
    }

    public VPackSlice getVPack() {
        return currentValue;
    }

    @Override
    public int readBinaryValue(Base64Variant b64variant, OutputStream out) {
        return -1;
    }

    /*
    /**********************************************************
    /* Numeric accessors of public API
    /**********************************************************
     */

    @Override
    public Number getNumberValue() {
        switch (currentValue.getType()) {
        case SMALLINT:
            return currentValue.getAsInt();
        case INT:
            long longValue = currentValue.getAsLong();
            if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
                return longValue;
            } else {
                return currentValue.getAsInt();
            }
        case UINT:
            return currentValue.getAsBigInteger();
        case DOUBLE:
            return currentValue.getAsDouble();
        default:
            throw new UnsupportedOperationException("Cannot get number from " + currentValue.getType());
        }
    }

    @Override
    public NumberType getNumberType() {
        final NumberType type;
        switch (currentValue.getType()) {
        case SMALLINT:
            type = NumberType.INT;
            break;
        case INT:
            long longValue = currentValue.getAsLong();
            if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
                type = NumberType.LONG;
            } else {
                type = NumberType.INT;
            }
            break;
        case UINT:
            type = NumberType.BIG_INTEGER;
            break;
        case DOUBLE:
            type = NumberType.DOUBLE;
            break;
        default:
            type = null;
            break;
        }
        return type;
    }

    @Override
    public int getIntValue() {
        return currentValue.getAsInt();
    }

    @Override
    public long getLongValue() {
        return currentValue.getAsLong();
    }

    @Override
    public BigInteger getBigIntegerValue() {
        return currentValue.getAsBigInteger();
    }

    @Override
    public float getFloatValue() {
        return currentValue.getAsFloat();
    }

    @Override
    public double getDoubleValue() {
        return currentValue.getAsDouble();
    }

    @Override
    public BigDecimal getDecimalValue() {
        return currentValue.getAsBigDecimal();
    }

    @Override
    public Object getEmbeddedObject() throws IOException {
        return null;
    }

    @Override
    protected void _handleEOF() {
        throw new UnsupportedOperationException("Stream decoding is not supported!");
    }

}
