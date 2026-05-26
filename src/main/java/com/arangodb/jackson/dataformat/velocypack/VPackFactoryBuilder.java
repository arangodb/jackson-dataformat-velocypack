package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.base.DecorableTSFactory.DecorableTSFBuilder;

/**
 * {@link tools.jackson.core.TSFBuilder}
 * implementation for constructing {@link VPackFactory}
 * instances.
 */
public class VPackFactoryBuilder extends DecorableTSFBuilder<VPackFactory, VPackFactoryBuilder>
{
    /*
    /**********************************************************
    /* Life cycle
    /**********************************************************
     */

    protected VPackFactoryBuilder() {
        super(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                VPackFactory.DEFAULT_VPACK_PARSER_FEATURE_FLAGS,
                VPackFactory.DEFAULT_VPACK_GENERATOR_FEATURE_FLAGS);
    }

    public VPackFactoryBuilder(VPackFactory base) {
        super(base);
    }

    @Override
    public VPackFactory build() {
        return new VPackFactory(this);
    }

    /*
    /**********************************************************
    /* Configuration: on/off features
    /**********************************************************
     */

    // // // Parser features

    public VPackFactoryBuilder enable(VPackReadFeature f) {
        _formatReadFeatures |= f.getMask();
        return _this();
    }

    public VPackFactoryBuilder enable(VPackReadFeature first, VPackReadFeature... other) {
        _formatReadFeatures |= first.getMask();
        for (VPackReadFeature f : other) {
            _formatReadFeatures |= f.getMask();
        }
        return _this();
    }

    public VPackFactoryBuilder disable(VPackReadFeature f) {
        _formatReadFeatures &= ~f.getMask();
        return _this();
    }

    public VPackFactoryBuilder disable(VPackReadFeature first, VPackReadFeature... other) {
        _formatReadFeatures &= ~first.getMask();
        for (VPackReadFeature f : other) {
            _formatReadFeatures &= ~f.getMask();
        }
        return _this();
    }

    public VPackFactoryBuilder configure(VPackReadFeature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    // // // Generator features

    public VPackFactoryBuilder enable(VPackWriteFeature f) {
        _formatWriteFeatures |= f.getMask();
        return _this();
    }

    public VPackFactoryBuilder enable(VPackWriteFeature first, VPackWriteFeature... other) {
        _formatWriteFeatures |= first.getMask();
        for (VPackWriteFeature f : other) {
            _formatWriteFeatures |= f.getMask();
        }
        return _this();
    }

    public VPackFactoryBuilder disable(VPackWriteFeature f) {
        _formatWriteFeatures &= ~f.getMask();
        return _this();
    }

    public VPackFactoryBuilder disable(VPackWriteFeature first, VPackWriteFeature... other) {
        _formatWriteFeatures &= ~first.getMask();
        for (VPackWriteFeature f : other) {
            _formatWriteFeatures &= ~f.getMask();
        }
        return _this();
    }

    public VPackFactoryBuilder configure(VPackWriteFeature f, boolean state) {
        return state ? enable(f) : disable(f);
    }
}
