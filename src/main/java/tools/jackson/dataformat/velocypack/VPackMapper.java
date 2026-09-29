package tools.jackson.dataformat.velocypack;

import java.io.Serial;

import tools.jackson.core.Version;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.cfg.MapperBuilderState;

/**
 * Immutable databind mapper for VelocyPack, with native date and special-value support.
 *
 * <p>Example:
 * <pre>{@code
 * VPackMapper mapper = VPackMapper.builder().build();
 * byte[] bytes = mapper.writeValueAsBytes(new VPackDate(1234L));
 * VPackDate date = mapper.readValue(bytes, VPackDate.class);
 * }
 * </pre>
 */
public class VPackMapper extends ObjectMapper {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Builder for a VPackMapper and its immutable saved configuration. */
    /** Public feature methods are intentionally available to mapper callers. */
    @SuppressWarnings("unused")
    public static class Builder extends MapperBuilder<VPackMapper, Builder> {
        public Builder(VPackFactory factory) {
            super(factory);
            addModule(new VPackModule());
        }

        /**
         * Constructor used by {@link VPackMapper#rebuild()} and Java
         * serialization restoration.
         */
        @SuppressWarnings("exports")
        public Builder(StateImpl state) {
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

        public Builder enable(VPackReadFeature... features) {
            for (VPackReadFeature feature : features) {
                _formatReadFeatures |= feature.getMask();
            }
            return this;
        }

        public Builder disable(VPackReadFeature... features) {
            for (VPackReadFeature feature : features) {
                _formatReadFeatures &= ~feature.getMask();
            }
            return this;
        }

        public Builder configure(VPackReadFeature feature, boolean state) {
            if (state) {
                _formatReadFeatures |= feature.getMask();
            } else {
                _formatReadFeatures &= ~feature.getMask();
            }
            return this;
        }

        public Builder enable(VPackWriteFeature... features) {
            for (VPackWriteFeature feature : features) {
                _formatWriteFeatures |= feature.getMask();
            }
            return this;
        }

        public Builder disable(VPackWriteFeature... features) {
            for (VPackWriteFeature feature : features) {
                _formatWriteFeatures &= ~feature.getMask();
            }
            return this;
        }

        public Builder configure(VPackWriteFeature feature, boolean state) {
            if (state) {
                _formatWriteFeatures |= feature.getMask();
            } else {
                _formatWriteFeatures &= ~feature.getMask();
            }
            return this;
        }

        @SuppressWarnings("ClassEscapesItsScope") // MapperBuilderState preserves rebuild and serialization state.
        protected static class StateImpl extends MapperBuilderState {
            @Serial
            private static final long serialVersionUID = 1L;

            public StateImpl(Builder source) {
                super(source);
            }

            @Override
            @Serial
            protected Object readResolve() {
                return new Builder(this).build();
            }
        }
    }

    public VPackMapper() {
        this(new VPackFactory());
    }

    public VPackMapper(VPackFactory factory) {
        this(new Builder(factory));
    }

    public VPackMapper(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder(new VPackFactory());
    }

    public static Builder builder(VPackFactory factory) {
        return new Builder(factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder rebuild() {
        return new Builder((Builder.StateImpl) _savedBuilderState);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public VPackFactory tokenStreamFactory() {
        return (VPackFactory) _streamFactory;
    }

    @SuppressWarnings("unused") // Public format-feature query used by callers.
    public boolean isEnabled(VPackReadFeature feature) {
        return _deserializationConfig.hasFormatFeature(feature);
    }

    public boolean isEnabled(VPackWriteFeature feature) {
        return _serializationConfig.hasFormatFeature(feature);
    }
}
