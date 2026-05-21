package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.base.BinaryTSFactory;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.sym.BinaryNameMatcher;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.sym.PropertyNameMatcher;
import tools.jackson.core.util.Named;
import tools.jackson.databind.cfg.PackageVersion;

import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * Factory used for constructing {@link VPackParser} and {@link VPackGenerator}
 * instances; both of which handle
 * <a href="https://github.com/arangodb/velocypack">VelocyPack</a> encoded data.
 *<p>
 * Note on using non-byte-based sources/targets (char-based, like
 * {@link Reader} and {@link Writer}): these can not be
 * used for VelocyPack documents.
 */
public class VPackFactory
    extends BinaryTSFactory
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    /**
     * Name used to identify VelocyPack format.
     * (and returned by {@link #getFormatName()})
     */
    public final static String FORMAT_NAME_VPACK = "VelocyPack";

    /**
     * Bitfield (set of flags) of all parser features that are enabled
     * by default.
     */
    final static int DEFAULT_VPACK_PARSER_FEATURE_FLAGS = VPackReadFeature.collectDefaults();

    /**
     * Bitfield (set of flags) of all generator features that are enabled
     * by default.
     */
    final static int DEFAULT_VPACK_GENERATOR_FEATURE_FLAGS = VPackWriteFeature.collectDefaults();

    /*
    /**********************************************************************
    /* Symbol table management
    /**********************************************************************
     */

    /**
     * Alternative to the basic symbol table; stream-based parsers use this
     * for name canonicalization.
     */
    protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();

    /*
    /**********************************************************************
    /* Factory construction, configuration
    /**********************************************************************
     */

    /**
     * Default constructor used to create factory instances.
     */
    public VPackFactory() {
        super(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                DEFAULT_VPACK_PARSER_FEATURE_FLAGS, DEFAULT_VPACK_GENERATOR_FEATURE_FLAGS);
    }

    public VPackFactory(VPackFactory src)
    {
        super(src);
    }

    /**
     * Constructor used by {@link VPackFactoryBuilder} for instantiation.
     */
    protected VPackFactory(VPackFactoryBuilder b) {
        super(b);
    }

    @Override
    public VPackFactoryBuilder rebuild() {
        return new VPackFactoryBuilder(this);
    }

    /**
     * Main factory method to use for constructing {@link VPackFactory} instances
     * with different configuration.
     */
    public static VPackFactoryBuilder builder() {
        return new VPackFactoryBuilder();
    }

    @Override
    public VPackFactory copy() {
        return new VPackFactory(this);
    }

    /**
     * Instances are immutable so just return {@code this}.
     */
    @Override
    public TokenStreamFactory snapshot() {
        return this;
    }

    /*
    /**********************************************************************
    /* Serializable overrides
    /**********************************************************************
     */

    @Serial
    protected Object readResolve() {
        return new VPackFactory(this);
    }

    /*
    /**********************************************************************
    /* Capability introspection
    /**********************************************************************
     */

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public boolean canParseAsync() { return false; }

    /**
     * Check whether specified parser feature is enabled.
     */
    public final boolean isEnabled(VPackReadFeature f) {
        return f.enabledIn(_formatReadFeatures);
    }

    /**
     * Check whether specified generator feature is enabled.
     */
    public final boolean isEnabled(VPackWriteFeature f) {
        return f.enabledIn(_formatWriteFeatures);
    }

    /*
    /**********************************************************************
    /* Format support
    /**********************************************************************
     */

    @Override
    public String getFormatName() {
        return FORMAT_NAME_VPACK;
    }

    @Override
    public boolean canUseSchema(FormatSchema schema) {
        return false;
    }

    @Override
    public Class<VPackReadFeature> getFormatReadFeatureType() {
        return VPackReadFeature.class;
    }

    @Override
    public Class<VPackWriteFeature> getFormatWriteFeatureType() {
        return VPackWriteFeature.class;
    }

    /*
    /**********************************************************************
    /* Factory method impls: parsers
    /**********************************************************************
     */

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            InputStream in)
    {
        return new VPackParserBootstrapper(ioCtxt, in)
            .constructParser(readCtxt, _factoryFeatures,
                    readCtxt.getStreamReadFeatures(_streamReadFeatures),
                    readCtxt.getFormatReadFeatures(_formatReadFeatures),
                    _byteSymbolCanonicalizer);
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            byte[] data, int offset, int len)
    {
        // Validate doc length up front for fixed buffers
        _streamReadConstraints.validateDocumentLength(len);
        return new VPackParserBootstrapper(ioCtxt, data, offset, len)
            .constructParser(readCtxt, _factoryFeatures,
                readCtxt.getStreamReadFeatures(_streamReadFeatures),
                readCtxt.getFormatReadFeatures(_formatReadFeatures),
                _byteSymbolCanonicalizer);
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            DataInput input) {
        return _unsupported();
    }

    /*
    /**********************************************************************
    /* Factory method impls: generators
    /**********************************************************************
     */

    @Override
    protected JsonGenerator _createGenerator(ObjectWriteContext writeCtxt,
            IOContext ioCtxt, OutputStream out)
    {
        return new VPackGenerator(writeCtxt, ioCtxt,
                writeCtxt.getStreamWriteFeatures(_streamWriteFeatures),
                writeCtxt.getFormatWriteFeatures(_formatWriteFeatures),
                out);
    }

    /*
    /**********************************************************************
    /* Other factory methods
    /**********************************************************************
     */

    @Override
    public PropertyNameMatcher constructNameMatcher(List<Named> matches, boolean alreadyInterned) {
        return BinaryNameMatcher.constructFrom(matches, alreadyInterned);
    }

    @Override
    public PropertyNameMatcher constructCINameMatcher(List<Named> matches, boolean alreadyInterned,
            Locale locale) {
        return BinaryNameMatcher.constructCaseInsensitive(locale, matches, alreadyInterned);
    }
}
