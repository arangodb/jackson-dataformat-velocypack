package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.base.ParserMinimalBase;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.SimpleStreamReadContext;
import tools.jackson.core.util.TextBuffer;
import tools.jackson.databind.cfg.PackageVersion;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Base class for the VelocyPack parser. Contains state and common
 * number/string accessors shared with potential sub-classes.
 */
public abstract class VPackParserBase extends ParserMinimalBase
{

    protected static final JacksonFeatureSet<StreamReadCapability> VPACK_READ_CAPABILITIES
        = DEFAULT_READ_CAPABILITIES.with(StreamReadCapability.EXACT_FLOATS);

    /*
    /**********************************************************************
    /* Config
    /**********************************************************************
     */

    protected final int _formatFeatures;

    /*
    /**********************************************************************
    /* Current input data
    /**********************************************************************
     */

    protected int _inputPtr = 0;
    protected int _inputEnd = 0;

    /*
    /**********************************************************************
    /* Parsing state, location
    /**********************************************************************
     */

    protected long _currInputProcessed;
    protected int _tokenOffsetForTotal;

    protected SimpleStreamReadContext _streamReadContext;

    /*
    /**********************************************************************
    /* Decoded values, text, binary
    /**********************************************************************
     */

    protected final TextBuffer _textBuffer;
    protected byte[] _binaryValue;

    /*
    /**********************************************************************
    /* Decoded values, numbers
    /**********************************************************************
     */

    protected NumberType _numberType;
    protected int _numTypesValid = NR_UNKNOWN;

    protected BigInteger _numberBigInt;
    protected BigDecimal _numberBigDecimal;
    protected int _numberInt;
    protected long _numberLong;
    protected double _numberDouble;

    /*
    /**********************************************************************
    /* Symbol handling
    /**********************************************************************
     */

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public VPackParserBase(ObjectReadContext readCtxt, IOContext ioCtxt,
            int parserFeatures, int formatFeatures)
    {
        super(readCtxt, ioCtxt, parserFeatures);
        _formatFeatures = formatFeatures;
        DupDetector dups = StreamReadFeature.STRICT_DUPLICATE_DETECTION.enabledIn(parserFeatures)
                ? DupDetector.rootDetector(this) : null;
        _streamReadContext = SimpleStreamReadContext.createRootContext(dups);
        _textBuffer = ioCtxt.constructReadConstrainedTextBuffer();
    }

    /*
    /**********************************************************************
    /* Versioned
    /**********************************************************************
     */

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    /*
    /**********************************************************************
    /* Capability introspection
    /**********************************************************************
     */

    @Override
    public JacksonFeatureSet<StreamReadCapability> streamReadCapabilities() {
        return VPACK_READ_CAPABILITIES;
    }

    // Note: willInternPropertyNames and mayContainRawBinary are non-abstract in base
    // class so no @Override needed here; they are defined in concrete subclass.

    /*
    /**********************************************************************
    /* Abstract implementations
    /**********************************************************************
     */

    @Override
    protected void _closeInput() throws IOException { }

    @Override
    protected void _handleEOF() throws StreamReadException {
        if (!_streamReadContext.inRoot()) {
            String marker = _streamReadContext.inArray() ? "Array" : "Object";
            _reportInvalidEOF(String.format(
                    ": expected close marker for %s (start marker at %s)",
                    marker, _streamReadContext.startLocation(_sourceReference())),
                    _currToken);
        }
    }

    /*
    /**********************************************************************
    /* Overrides: context access
    /**********************************************************************
     */

    @Override public SimpleStreamReadContext streamReadContext() { return _streamReadContext; }
    @Override public void assignCurrentValue(Object v) { _streamReadContext.assignCurrentValue(v); }
    @Override public Object currentValue() { return _streamReadContext.currentValue(); }

    /*
    /**********************************************************************
    /* Location info
    /**********************************************************************
     */

    @Override
    public TokenStreamLocation currentTokenLocation() {
        long total = _currInputProcessed + _tokenOffsetForTotal;
        return new TokenStreamLocation(_sourceReference(),
                total, -1L, -1, (int) total);
    }

    @Override
    public TokenStreamLocation currentLocation() {
        long total = _currInputProcessed + _inputPtr;
        return new TokenStreamLocation(_sourceReference(),
                total, -1L, -1, (int) total);
    }

    @Override
    public String currentName() throws JacksonException {
        String name = _streamReadContext.currentName();
        if (name != null) {
            return name;
        }
        SimpleStreamReadContext parent = _streamReadContext.getParent();
        return (parent == null) ? null : parent.currentName();
    }

    /*
    /**********************************************************************
    /* Close
    /**********************************************************************
     */

    @Override
    public void close() throws JacksonException {
        if (!_closed) {
            _closed = true;
            try {
                _closeInput();
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            } finally {
                _releaseBuffers();
            }
        }
    }

    @Override
    protected void _releaseBuffers() {
        _textBuffer.releaseBuffers();
        _releaseBuffers2();
    }

    protected void _releaseBuffers2() { }

    /*
    /**********************************************************************
    /* Numeric accessors
    /**********************************************************************
     */

    @Override
    public boolean isNaN() throws JacksonException {
        if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            if ((_numTypesValid & NR_DOUBLE) != 0) {
                return Double.isNaN(_numberDouble) || Double.isInfinite(_numberDouble);
            }
        }
        return false;
    }

    @Override
    public Number getNumberValue() throws JacksonException {
        if (_currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((_numTypesValid & NR_INT) != 0) return _numberInt;
            if ((_numTypesValid & NR_LONG) != 0) return _numberLong;
            if ((_numTypesValid & NR_BIGINT) != 0) return _numberBigInt;
        }
        if ((_numTypesValid & NR_BIGDECIMAL) != 0) return _numberBigDecimal;
        if ((_numTypesValid & NR_DOUBLE) != 0) return _numberDouble;
        _throwInternal();
        return null;
    }

    @Override
    public Number getNumberValueExact() throws JacksonException {
        return getNumberValue();
    }

    @Override
    public NumberType getNumberType() throws JacksonException {
        return _numberType;
    }

    @Override
    public NumberTypeFP getNumberTypeFP() throws JacksonException {
        if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            if ((_numTypesValid & NR_BIGDECIMAL) != 0) return NumberTypeFP.BIG_DECIMAL;
            if ((_numTypesValid & NR_DOUBLE) != 0) return NumberTypeFP.DOUBLE64;
        }
        return NumberTypeFP.UNKNOWN;
    }

    @Override
    public int getIntValue() throws JacksonException {
        if ((_numTypesValid & NR_INT) == 0) {
            convertNumberToInt();
        }
        return _numberInt;
    }

    @Override
    public long getLongValue() throws JacksonException {
        if ((_numTypesValid & NR_LONG) == 0) {
            convertNumberToLong();
        }
        return _numberLong;
    }

    @Override
    public BigInteger getBigIntegerValue() throws JacksonException {
        if ((_numTypesValid & NR_BIGINT) == 0) {
            convertNumberToBigInteger();
        }
        return _numberBigInt;
    }

    @Override
    public float getFloatValue() throws JacksonException {
        return (float) getDoubleValue();
    }

    @Override
    public double getDoubleValue() throws JacksonException {
        if ((_numTypesValid & NR_DOUBLE) == 0) {
            convertNumberToDouble();
        }
        return _numberDouble;
    }

    @Override
    public BigDecimal getDecimalValue() throws JacksonException {
        if ((_numTypesValid & NR_BIGDECIMAL) == 0) {
            convertNumberToBigDecimal();
        }
        return _numberBigDecimal;
    }

    /*
    /**********************************************************************
    /* Number conversions
    /**********************************************************************
     */

    protected void convertNumberToInt() throws JacksonException {
        if ((_numTypesValid & NR_LONG) != 0) {
            _numberInt = (int) _numberLong;
            _numTypesValid |= NR_INT;
        } else if ((_numTypesValid & NR_BIGINT) != 0) {
            _numberInt = _numberBigInt.intValue();
            _numTypesValid |= NR_INT;
        } else if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
            _numberInt = _numberBigDecimal.intValue();
            _numTypesValid |= NR_INT;
        } else if ((_numTypesValid & NR_DOUBLE) != 0) {
            _numberInt = (int) _numberDouble;
            _numTypesValid |= NR_INT;
        } else {
            _throwInternal();
        }
    }

    protected void convertNumberToLong() throws JacksonException {
        if ((_numTypesValid & NR_INT) != 0) {
            _numberLong = _numberInt;
            _numTypesValid |= NR_LONG;
        } else if ((_numTypesValid & NR_BIGINT) != 0) {
            _numberLong = _numberBigInt.longValue();
            _numTypesValid |= NR_LONG;
        } else if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
            _numberLong = _numberBigDecimal.longValue();
            _numTypesValid |= NR_LONG;
        } else if ((_numTypesValid & NR_DOUBLE) != 0) {
            _numberLong = (long) _numberDouble;
            _numTypesValid |= NR_LONG;
        } else {
            _throwInternal();
        }
    }

    protected void convertNumberToBigInteger() throws JacksonException {
        if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
            _numberBigInt = _numberBigDecimal.toBigInteger();
            _numTypesValid |= NR_BIGINT;
        } else if ((_numTypesValid & NR_LONG) != 0) {
            _numberBigInt = BigInteger.valueOf(_numberLong);
            _numTypesValid |= NR_BIGINT;
        } else if ((_numTypesValid & NR_INT) != 0) {
            _numberBigInt = BigInteger.valueOf(_numberInt);
            _numTypesValid |= NR_BIGINT;
        } else if ((_numTypesValid & NR_DOUBLE) != 0) {
            _numberBigInt = BigDecimal.valueOf(_numberDouble).toBigInteger();
            _numTypesValid |= NR_BIGINT;
        } else {
            _throwInternal();
        }
    }

    protected void convertNumberToDouble() throws JacksonException {
        if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
            _numberDouble = _numberBigDecimal.doubleValue();
            _numTypesValid |= NR_DOUBLE;
        } else if ((_numTypesValid & NR_BIGINT) != 0) {
            _numberDouble = _numberBigInt.doubleValue();
            _numTypesValid |= NR_DOUBLE;
        } else if ((_numTypesValid & NR_LONG) != 0) {
            _numberDouble = _numberLong;
            _numTypesValid |= NR_DOUBLE;
        } else if ((_numTypesValid & NR_INT) != 0) {
            _numberDouble = _numberInt;
            _numTypesValid |= NR_DOUBLE;
        } else {
            _throwInternal();
        }
    }

    protected void convertNumberToBigDecimal() throws JacksonException {
        if ((_numTypesValid & NR_DOUBLE) != 0) {
            _numberBigDecimal = new BigDecimal(_numberDouble);
            _numTypesValid |= NR_BIGDECIMAL;
        } else if ((_numTypesValid & NR_BIGINT) != 0) {
            _numberBigDecimal = new BigDecimal(_numberBigInt);
            _numTypesValid |= NR_BIGDECIMAL;
        } else if ((_numTypesValid & NR_LONG) != 0) {
            _numberBigDecimal = BigDecimal.valueOf(_numberLong);
            _numTypesValid |= NR_BIGDECIMAL;
        } else if ((_numTypesValid & NR_INT) != 0) {
            _numberBigDecimal = BigDecimal.valueOf(_numberInt);
            _numTypesValid |= NR_BIGDECIMAL;
        } else {
            _throwInternal();
        }
    }

    protected ContentReference _sourceReference() {
        return _ioContext.contentReference();
    }
}
