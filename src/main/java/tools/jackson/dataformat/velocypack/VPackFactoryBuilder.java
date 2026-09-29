package tools.jackson.dataformat.velocypack;

import java.util.Objects;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.base.DecorableTSFactory.DecorableTSFBuilder;

/** Builder for immutable {@link VPackFactory} instances. */
public class VPackFactoryBuilder
        extends DecorableTSFBuilder<VPackFactory, VPackFactoryBuilder> {
    private VPackReadConstraints vpackReadConstraints;
    private VPackWriteConstraints vpackWriteConstraints;
    private VPackAttributeNameCodec attributeNameCodec;

    protected VPackFactoryBuilder() {
        super(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(), VPackReadFeature.collectDefaults(),
                VPackWriteFeature.collectDefaults());
        vpackReadConstraints = VPackReadConstraints.defaults();
        vpackWriteConstraints = VPackWriteConstraints.defaults();
    }

    protected VPackFactoryBuilder(VPackFactory base) {
        super(base);
        vpackReadConstraints = base.vpackReadConstraints();
        vpackWriteConstraints = base.vpackWriteConstraints();
        attributeNameCodec = base.attributeNameCodec();
    }

    public VPackFactoryBuilder enable(VPackReadFeature feature) {
        _formatReadFeatures |= feature.getMask();
        return _this();
    }

    public VPackFactoryBuilder disable(VPackReadFeature feature) {
        _formatReadFeatures &= ~feature.getMask();
        return _this();
    }

    @SuppressWarnings("unused") // Public fluent builder API.
    public VPackFactoryBuilder configure(VPackReadFeature feature, boolean state) {
        return state ? enable(feature) : disable(feature);
    }

    public VPackFactoryBuilder enable(VPackWriteFeature feature) {
        _formatWriteFeatures |= feature.getMask();
        return _this();
    }

    public VPackFactoryBuilder disable(VPackWriteFeature feature) {
        _formatWriteFeatures &= ~feature.getMask();
        return _this();
    }

    @SuppressWarnings("unused") // Public fluent builder API.
    public VPackFactoryBuilder configure(VPackWriteFeature feature, boolean state) {
        return state ? enable(feature) : disable(feature);
    }

    public VPackFactoryBuilder attributeNameCodec(VPackAttributeNameCodec codec) {
        attributeNameCodec = codec;
        return _this();
    }

    public VPackFactoryBuilder vpackReadConstraints(VPackReadConstraints constraints) {
        vpackReadConstraints = Objects.requireNonNull(constraints, "constraints");
        return _this();
    }

    public VPackFactoryBuilder vpackWriteConstraints(VPackWriteConstraints constraints) {
        vpackWriteConstraints = Objects.requireNonNull(constraints, "constraints");
        return _this();
    }

    VPackReadConstraints readConstraints() {
        return vpackReadConstraints;
    }

    VPackWriteConstraints writeConstraints() {
        return vpackWriteConstraints;
    }

    VPackAttributeNameCodec nameCodec() {
        return attributeNameCodec;
    }

    @Override
    public VPackFactory build() {
        return new VPackFactory(this);
    }
}
