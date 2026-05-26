package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.cfg.MapperBuilderState;

import java.io.Serial;

/**
 * Specialized {@link ObjectMapper} to use with VelocyPack format backend.
 */
public class VPackMapper extends ObjectMapper
{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Base implementation for "Vanilla" {@link ObjectMapper}, used with
     * VelocyPack backend.
     */
    public static class Builder extends MapperBuilder<VPackMapper, Builder>
    {
        public Builder(VPackFactory f) {
            super(f);
        }

        protected Builder(StateImpl state) {
            super(state);
        }

        @Override
        public VPackMapper build() {
            return new VPackMapper(this);
        }

        @Override
        protected MapperBuilderState _saveState() {
            return new StateImpl(this);
        }

        /*
        /******************************************************************
        /* Format features
        /******************************************************************
         */

        public Builder enable(VPackReadFeature... features) {
            for (VPackReadFeature f : features) {
                _formatReadFeatures |= f.getMask();
            }
            return this;
        }

        public Builder disable(VPackReadFeature... features) {
            for (VPackReadFeature f : features) {
                _formatReadFeatures &= ~f.getMask();
            }
            return this;
        }

        public Builder configure(VPackReadFeature feature, boolean state)
        {
            if (state) {
                _formatReadFeatures |= feature.getMask();
            } else {
                _formatReadFeatures &= ~feature.getMask();
            }
            return this;
        }

        public Builder enable(VPackWriteFeature... features) {
            for (VPackWriteFeature f : features) {
                _formatWriteFeatures |= f.getMask();
            }
            return this;
        }

        public Builder disable(VPackWriteFeature... features) {
            for (VPackWriteFeature f : features) {
                _formatWriteFeatures &= ~f.getMask();
            }
            return this;
        }

        public Builder configure(VPackWriteFeature feature, boolean state)
        {
            if (state) {
                _formatWriteFeatures |= feature.getMask();
            } else {
                _formatWriteFeatures &= ~feature.getMask();
            }
            return this;
        }

        protected static class StateImpl extends MapperBuilderState
            implements java.io.Serializable // important!
        {
            @Serial
            private static final long serialVersionUID = 3L;

            public StateImpl(Builder src) {
                super(src);
            }

            // We also need actual instance of state as base class can not implement logic
            // for reinstating mapper (via mapper builder) from state.
            @Serial
            @Override
            protected Object readResolve() {
                return new Builder(this).build();
            }
        }
    }

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public VPackMapper() {
        this(new VPackFactory());
    }

    public VPackMapper(VPackFactory f) {
        this(new Builder(f));
    }

    public VPackMapper(Builder b) {
        super(b);
    }

    public static Builder builder() {
        return new Builder(new VPackFactory());
    }

    public static Builder builder(VPackFactory streamFactory) {
        return new Builder(streamFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder rebuild() {
        return new Builder((Builder.StateImpl) _savedBuilderState);
    }

    /*
    /**********************************************************************
    /* Life-cycle, shared "vanilla" (default configuration) instance
    /**********************************************************************
     */

    /**
     * Accessor method for getting globally shared "default" {@link VPackMapper}
     * instance: one that has default configuration, no modules registered, no
     * config overrides. Usable mostly when dealing "untyped" or Tree-style
     * content reading and writing.
     */
    public static VPackMapper shared() {
        return SharedWrapper.wrapped();
    }

    /*
    /**********************************************************************
    /* Basic accessor overrides
    /**********************************************************************
     */

    @Override
    public VPackFactory tokenStreamFactory() {
        return (VPackFactory) _streamFactory;
    }

    /*
    /**********************************************************************
    /* Format-specific
    /**********************************************************************
     */

    public boolean isEnabled(VPackReadFeature f) {
        return _deserializationConfig.hasFormatFeature(f);
    }

    public boolean isEnabled(VPackWriteFeature f) {
        return _serializationConfig.hasFormatFeature(f);
    }

    /*
    /**********************************************************************
    /* Helper class(es)
    /**********************************************************************
     */

    /**
     * Helper class to contain dynamically constructed "shared" instance of
     * mapper, should one be needed via {@link #shared}.
     */
    private final static class SharedWrapper {
        private final static VPackMapper MAPPER = VPackMapper.builder().build();

        public static VPackMapper wrapped() { return MAPPER; }
    }
}
