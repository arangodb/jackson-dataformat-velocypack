package tools.jackson.dataformat.velocypack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.io.Serial;
import java.util.Locale;
import java.util.List;

import tools.jackson.core.*;
import tools.jackson.core.base.BinaryTSFactory;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.sym.BinaryNameMatcher;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.sym.PropertyNameMatcher;
import tools.jackson.core.util.Named;

/**
 * Factory for the bounded, binary-only VelocyPack parser and generator.
 *
 * <p>For databind, build a matching mapper:
 * <pre>{@code
 * VPackFactory factory = VPackFactory.builder().build();
 * VPackMapper mapper = VPackMapper.builder(factory).build();
 * byte[] bytes = mapper.writeValueAsBytes("value");
 * }
 * </pre>
 * A parser validates and buffers one complete bounded root before exposing its
 * first token. See the module README for supported sources and targets.
 */
public class VPackFactory extends BinaryTSFactory implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final String FORMAT_NAME = "VPack";

    protected final transient ByteQuadsCanonicalizer _byteSymbolCanonicalizer =
            ByteQuadsCanonicalizer.createRoot();

    static final int DEFAULT_VPACK_PARSER_FEATURE_FLAGS = VPackReadFeature.collectDefaults();
    static final int DEFAULT_VPACK_GENERATOR_FEATURE_FLAGS = VPackWriteFeature.collectDefaults();

    private final VPackReadConstraints vpackReadConstraints;
    private final VPackWriteConstraints vpackWriteConstraints;
    private final VPackAttributeNameCodec attributeNameCodec;

    public VPackFactory() {
        super(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(), DEFAULT_VPACK_PARSER_FEATURE_FLAGS,
                DEFAULT_VPACK_GENERATOR_FEATURE_FLAGS);
        vpackReadConstraints = VPackReadConstraints.defaults();
        vpackWriteConstraints = VPackWriteConstraints.defaults();
        attributeNameCodec = null;
    }

    protected VPackFactory(VPackFactoryBuilder builder) {
        super(builder);
        vpackReadConstraints = builder.readConstraints();
        vpackWriteConstraints = builder.writeConstraints();
        attributeNameCodec = builder.nameCodec();
    }

    protected VPackFactory(VPackFactory source) {
        super(source);
        vpackReadConstraints = source.vpackReadConstraints;
        vpackWriteConstraints = source.vpackWriteConstraints;
        attributeNameCodec = source.attributeNameCodec;
    }

    public static VPackFactoryBuilder builder() {
        return new VPackFactoryBuilder();
    }

    @Override
    public VPackFactoryBuilder rebuild() {
        return new VPackFactoryBuilder(this);
    }

    @Override
    public VPackFactory copy() {
        return new VPackFactory(this);
    }

    @Override
    public TokenStreamFactory snapshot() {
        return this;
    }

    @Serial
    protected Object readResolve() {
        return new VPackFactory(this);
    }

    public VPackReadConstraints vpackReadConstraints() {
        return vpackReadConstraints;
    }

    public VPackWriteConstraints vpackWriteConstraints() {
        return vpackWriteConstraints;
    }

    public VPackAttributeNameCodec attributeNameCodec() {
        return attributeNameCodec;
    }

    @SuppressWarnings("unused") // Public format-feature query used by callers.
    public boolean isEnabled(VPackReadFeature feature) {
        return feature.enabledIn(_formatReadFeatures);
    }

    @SuppressWarnings("unused") // Public format-feature query used by callers.
    public boolean isEnabled(VPackWriteFeature feature) {
        return feature.enabledIn(_formatWriteFeatures);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public boolean canParseAsync() {
        return false;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
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

    @Override
    protected VPackParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            InputStream input) {
        ioCtxt = _effectiveReadContext(readCtxt, ioCtxt);
        int streamFeatures = readCtxt.getStreamReadFeatures(_streamReadFeatures);
        int formatFeatures = readCtxt.getFormatReadFeatures(_formatReadFeatures);
        return new VPackParser(readCtxt, ioCtxt,
                streamFeatures, formatFeatures,
                vpackReadConstraints, attributeNameCodec,
                _byteSymbolCanonicalizer.makeChildOrPlaceholder(_factoryFeatures),
                VPackRootReader.forInputStream(input, vpackReadConstraints, 0L,
                        ioCtxt.streamReadConstraints().getMaxDocumentLength(),
                        VPackRecyclerPageSupplier.forRead(ioCtxt)));
    }

    @Override
    protected VPackParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            byte[] data, int offset, int len) {
        ioCtxt = _effectiveReadContext(readCtxt, ioCtxt);
        ioCtxt.streamReadConstraints().validateDocumentLength(len);
        int streamFeatures = readCtxt.getStreamReadFeatures(_streamReadFeatures);
        int formatFeatures = readCtxt.getFormatReadFeatures(_formatReadFeatures);
        return new VPackParser(readCtxt, ioCtxt,
                streamFeatures, formatFeatures,
                vpackReadConstraints, attributeNameCodec,
                _byteSymbolCanonicalizer.makeChildOrPlaceholder(_factoryFeatures),
                VPackRootReader.forByteArray(data, offset, len, vpackReadConstraints,
                        0L, ioCtxt.streamReadConstraints().getMaxDocumentLength()));
    }

    @Override
    protected VPackParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            DataInput input) {
        ioCtxt = _effectiveReadContext(readCtxt, ioCtxt);
        int streamFeatures = readCtxt.getStreamReadFeatures(_streamReadFeatures);
        int formatFeatures = readCtxt.getFormatReadFeatures(_formatReadFeatures);
        return new VPackParser(readCtxt, ioCtxt,
                streamFeatures, formatFeatures,
                vpackReadConstraints, attributeNameCodec,
                _byteSymbolCanonicalizer.makeChildOrPlaceholder(_factoryFeatures),
                VPackRootReader.forDataInput(input, vpackReadConstraints, 0L,
                        ioCtxt.streamReadConstraints().getMaxDocumentLength(),
                        VPackRecyclerPageSupplier.forRead(ioCtxt)));
    }

    /**
     * ObjectReadContext is allowed to replace the factory's stream constraints.
     * IOContext is the source of truth used by ParserBase, so preserve all of
     * its ownership/content metadata while changing only the effective read
     * constraints. The original context relinquishes its shared recycler.
     */
    private IOContext _effectiveReadContext(ObjectReadContext readCtxt, IOContext ioCtxt) {
        StreamReadConstraints effective = readCtxt.streamReadConstraints();
        // ObjectReadContext.empty() (and its no-op Base implementation) exposes
        // the process default as a sentinel; it does not override a factory
        // constraint configured by the caller. Databind contexts expose their
        // actual mapper/factory constraint object instead.
        if (effective == StreamReadConstraints.defaults()
                && ioCtxt.streamReadConstraints() != StreamReadConstraints.defaults()) {
            effective = ioCtxt.streamReadConstraints();
        }
        if (effective == null || effective == ioCtxt.streamReadConstraints()) {
            return ioCtxt;
        }
        IOContext result = new IOContext(effective, ioCtxt.streamWriteConstraints(),
                ioCtxt.errorReportConfiguration(), ioCtxt.bufferRecycler(),
                ioCtxt.contentReference(), ioCtxt.isResourceManaged(), ioCtxt.getEncoding());
        ioCtxt.markBufferRecyclerReleased();
        return result;
    }

    @Override
    protected JsonGenerator _createGenerator(ObjectWriteContext writeCtxt,
            IOContext ioCtxt, java.io.OutputStream out) {
        return new VPackGenerator(writeCtxt, ioCtxt,
                writeCtxt.getStreamWriteFeatures(_streamWriteFeatures),
                writeCtxt.getFormatWriteFeatures(_formatWriteFeatures), out,
                vpackWriteConstraints, attributeNameCodec);
    }

    @Override
    protected OutputStream _createDataOutputWrapper(DataOutput out) {
        return new VPackDataOutputStream(out);
    }

    @Override
    public JsonGenerator createGenerator(ObjectWriteContext writeCtxt,
            DataOutput out) throws JacksonException {
        IOContext ioCtxt = _createContext(_createContentReference(out), false,
                JsonEncoding.UTF8);
        OutputStream wrapped = _createDataOutputWrapper(out);
        return _decorate(_createGenerator(writeCtxt, ioCtxt,
                _decorate(ioCtxt, wrapped)));
    }

    @Override
    public JsonGenerator createGenerator(ObjectWriteContext writeCtxt,
            File f, JsonEncoding enc) throws JacksonException {
        OutputStream out = _fileOutputStream(f);
        IOContext ioCtxt = _createContext(_createContentReference(f), true, enc);
        return _decorate(_createGenerator(writeCtxt, ioCtxt,
                _decorate(ioCtxt, out)));
    }

    @Override
    public JsonGenerator createGenerator(ObjectWriteContext writeCtxt,
            Path p, JsonEncoding enc) throws JacksonException {
        OutputStream out = _pathOutputStream(p);
        IOContext ioCtxt = _createContext(_createContentReference(p), true, enc);
        return _decorate(_createGenerator(writeCtxt, ioCtxt,
                _decorate(ioCtxt, out)));
    }

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
