package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple bootstrapper for VelocyPack format parser.
 * Unlike Smile, VPack has no header signature; all inputs are valid regardless
 * of the first byte (the type byte drives parsing).
 */
public class VPackParserBootstrapper
{
    protected final IOContext _ioContext;
    protected final InputStream _in;

    protected final byte[] _inputBuffer;
    protected int _inputPtr;
    protected int _inputEnd;
    protected final boolean _bufferRecyclable;
    protected int _inputProcessed;

    public VPackParserBootstrapper(IOContext ctxt, InputStream in)
    {
        _ioContext = ctxt;
        _in = in;
        _inputBuffer = ctxt.allocReadIOBuffer();
        _inputEnd = _inputPtr = 0;
        _inputProcessed = 0;
        _bufferRecyclable = true;
    }

    public VPackParserBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen)
    {
        _ioContext = ctxt;
        _in = null;
        _inputBuffer = inputBuffer;
        _inputPtr = inputStart;
        _inputEnd = (inputStart + inputLen);
        _inputProcessed = -inputStart;
        _bufferRecyclable = false;
    }

    public VPackParser constructParser(ObjectReadContext readCtxt,
            int factoryFeatures,
            int generalParserFeatures, int vpackFeatures,
            ByteQuadsCanonicalizer rootByteSymbols)
        throws JacksonException
    {
        ByteQuadsCanonicalizer can = rootByteSymbols.makeChildOrPlaceholder(factoryFeatures);
        // Pre-load some bytes if reading from stream
        if (_in != null && _inputPtr >= _inputEnd) {
            try {
                int count = _in.read(_inputBuffer, _inputEnd, _inputBuffer.length - _inputEnd);
                if (count > 0) {
                    _inputEnd += count;
                }
            } catch (IOException e) {
                throw JacksonIOException.construct(e);
            }
        }
        return new VPackParser(readCtxt, _ioContext, generalParserFeatures, vpackFeatures,
                can,
                _in, _inputBuffer, _inputPtr, _inputEnd, _bufferRecyclable);
    }
}
