package tools.jackson.dataformat.velocypack;

import java.io.Serial;
import java.io.Serializable;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import tools.jackson.databind.deser.std.StdScalarDeserializer;

/**
 * Databind support for the two native values owned by the VPack format.
 *
 * <p>The registrations are deliberately limited to {@link VPackDate} and
 * {@link VPackSpecialValue}. In particular, this module does not change the
 * handling of {@link Long}, Java date types, or other enums.</p>
 */
public class VPackModule extends SimpleModule {
    @Serial
    private static final long serialVersionUID = 1L;

    public VPackModule() {
        super("VPackModule", PackageVersion.VERSION);
        addSerializer(VPackDate.class, new VPackDateSerializer());
        addDeserializer(VPackDate.class, new VPackDateDeserializer());
        addSerializer(VPackSpecialValue.class, new VPackSpecialSerializer());
        addDeserializer(VPackSpecialValue.class, new VPackSpecialDeserializer());
    }

    /* The databind serializer/deserializer base classes are intentionally not
     * serializable. Recreate this stateless module instead of serializing its
     * handler instances or losing native support during mapper restoration. */
    @Serial
    @SuppressWarnings("SameReturnValue") // Serialization hook always selects the singleton form.
    private Object writeReplace() {
        return SerializedForm.INSTANCE;
    }

    private static final class SerializedForm implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private static final SerializedForm INSTANCE = new SerializedForm();

        @Serial
        private Object readResolve() {
            return new VPackModule();
        }
    }

    private static IllegalStateException incompatibleGenerator(JsonGenerator generator,
            String type) {
        return new IllegalStateException(type + " requires a VPackGenerator, got "
                + generator.getClass().getName());
    }

    private static class VPackDateSerializer extends StdScalarSerializer<VPackDate>
            implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        VPackDateSerializer() {
            super(VPackDate.class);
        }

        @Override
        public void serialize(VPackDate value, JsonGenerator generator,
                SerializationContext ctxt) throws JacksonException {
            if (!(generator instanceof VPackGenerator vpackGenerator)) {
                throw incompatibleGenerator(generator, "VPackDate serialization");
            }
            vpackGenerator.writeVPackDate(value.epochMillis());
        }
    }

    private static class VPackSpecialSerializer extends StdScalarSerializer<VPackSpecialValue>
            implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        VPackSpecialSerializer() {
            super(VPackSpecialValue.class);
        }

        @Override
        public void serialize(VPackSpecialValue value, JsonGenerator generator,
                SerializationContext ctxt) throws JacksonException {
            if (!(generator instanceof VPackGenerator vpackGenerator)) {
                throw incompatibleGenerator(generator, "VPackSpecialValue serialization");
            }
            vpackGenerator.writeVPackSpecial(value);
        }
    }

    private static class VPackDateDeserializer extends StdScalarDeserializer<VPackDate>
            implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        VPackDateDeserializer() {
            super(VPackDate.class);
        }

        @Override
        public VPackDate deserialize(JsonParser parser, DeserializationContext ctxt)
                throws JacksonException {
            if (!(parser instanceof VPackParser vpackParser)) {
                return ctxt.reportInputMismatch(VPackDate.class,
                        "VPackDate deserialization requires a VPackParser, got %s",
                        parser.getClass().getName());
            }
            if (vpackParser.currentVPackType() != VPackType.DATE) {
                return ctxt.reportInputMismatch(VPackDate.class,
                        "VPackDate requires physical VPack DATE marker, got %s",
                        vpackParser.currentVPackType());
            }
            return new VPackDate(vpackParser.getLongValue());
        }
    }

    private static class VPackSpecialDeserializer
            extends StdScalarDeserializer<VPackSpecialValue> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        VPackSpecialDeserializer() {
            super(VPackSpecialValue.class);
        }

        @Override
        public VPackSpecialValue deserialize(JsonParser parser, DeserializationContext ctxt)
                throws JacksonException {
            if (!(parser instanceof VPackParser vpackParser)) {
                return ctxt.reportInputMismatch(VPackSpecialValue.class,
                        "VPackSpecialValue deserialization requires a VPackParser, got %s",
                        parser.getClass().getName());
            }
            VPackType type = vpackParser.currentVPackType();
            if (type != VPackType.MIN_KEY && type != VPackType.MAX_KEY) {
                return ctxt.reportInputMismatch(VPackSpecialValue.class,
                        "VPackSpecialValue requires physical VPack MIN_KEY or MAX_KEY marker, got %s",
                        type);
            }
            Object embedded = vpackParser.getEmbeddedObject();
            if (!(embedded instanceof VPackSpecialValue special)) {
                return ctxt.reportInputMismatch(VPackSpecialValue.class,
                        "VPack special marker did not expose a VPackSpecialValue, got %s",
                        embedded == null ? "null" : embedded.getClass().getName());
            }
            return special;
        }
    }
}
