package tools.jackson.databind;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import tools.jackson.core.JsonParser;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.HandlerInstantiator;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.jsontype.TypeIdResolver;
import tools.jackson.databind.jsontype.TypeResolverBuilder;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.annotation.JsonTypeIdResolver;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0126F0 {

    void boundsWithByteArrayInputVpack() throws Exception {
        VPackMapper mapper = new VPackMapper();
        ObjectReader reader = mapper.reader();
        JavaType stringType = mapper.constructType(String.class);
        ByteBackedCreation[] creators = {
            (data, offset, length) -> mapper.createParser(data, offset, length),
            (data, offset, length) -> reader.createParser(data, offset, length),
            (data, offset, length) -> mapper.readTree(data, offset, length),
            (data, offset, length) -> mapper.readValue(data, offset, length, Object.class),
            (data, offset, length) -> reader.readValue(data, offset, length),
            (data, offset, length) -> mapper.readValue(data, offset, length, stringType)
        };
        for (ByteBackedCreation creator : creators) {
            byte[] data = new byte[10];
            assertInvalidByteRange(creator, data, -1, 1);
            assertInvalidByteRange(creator, data, 4, -1);
            assertInvalidByteRange(creator, data, 4, -6);
            assertInvalidByteRange(creator, data, 9, 5);
            assertInvalidByteRange(creator, data, Integer.MAX_VALUE, 4);
            assertInvalidByteRange(creator, data, Integer.MAX_VALUE, Integer.MAX_VALUE);
            assertThrows(IllegalArgumentException.class,
                    () -> creator.call(null, 0, 3));
        }
    }
private static void assertInvalidByteRange(ByteBackedCreation creator,
            byte[] data, int offset, int length) {
        assertThrows(StreamReadException.class,
                () -> creator.call(data, offset, length));
    }
private static DeserializationContext exposedContext(VPackMapper mapper, JsonParser parser) {
        return new ExposedObjectReader(mapper, mapper.deserializationConfig()).context(parser);
    }
private static class ExposedObjectReader extends ObjectReader {
        ExposedObjectReader(tools.jackson.databind.ObjectMapper mapper,
                DeserializationConfig config) {
            super(mapper, config);
        }

        DeserializationContext context(JsonParser parser) {
            return _deserializationContext(parser);
        }
    }
@FunctionalInterface
    private interface ByteBackedCreation {
        void call(byte[] data, int offset, int length) throws Exception;
    }
static class Bean4934 {
        public String value;
    }
@JsonDeserialize(using = MyBeanDeserializer.class)
    @JsonSerialize(using = MyBeanSerializer.class)
    static class MyBean {
        public String value;

        public MyBean() { }
        public MyBean(String value) { this.value = value; }
    }
@SuppressWarnings("serial")
    @JsonDeserialize(keyUsing = MyKeyDeserializer.class)
    static class MyMap extends java.util.HashMap<String, String> { }
@JsonTypeInfo(use = Id.CUSTOM, include = As.WRAPPER_ARRAY)
    @JsonTypeIdResolver(TestCustomIdResolver.class)
    static class TypeIdBean {
        public int x;

        public TypeIdBean() { }
        public TypeIdBean(int x) { this.x = x; }
    }
static class TypeIdBeanWrapper {
        public TypeIdBean bean;

        public TypeIdBeanWrapper() { }
        public TypeIdBeanWrapper(TypeIdBean bean) { this.bean = bean; }
    }
static class MyBeanDeserializer extends ValueDeserializer<MyBean> {
        private final String prefix;

        MyBeanDeserializer(String prefix) { this.prefix = prefix; }

        @Override
        public MyBean deserialize(JsonParser parser, DeserializationContext ctxt) {
            return new MyBean(prefix + parser.getString());
        }
    }
static class MyKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return "KEY";
        }
    }
static class MyBeanSerializer extends ValueSerializer<MyBean> {
        private final String prefix;

        MyBeanSerializer(String prefix) { this.prefix = prefix; }

        @Override
        public void serialize(MyBean value, tools.jackson.core.JsonGenerator generator,
                tools.jackson.databind.SerializationContext ctxt) {
            generator.writeString(prefix + value.value);
        }
    }
static class TestCustomIdResolver extends TypeIdResolverBase {
        private static final long serialVersionUID = 1L;
        private final String id;

        TestCustomIdResolver(String id) { this.id = id; }

        @Override
        public Id getMechanism() { return Id.CUSTOM; }

        @Override
        public String idFromValue(tools.jackson.databind.DatabindContext ctxt, Object value) {
            return value.getClass() == TypeIdBean.class ? id : "unknown";
        }

        @Override
        public String idFromValueAndType(tools.jackson.databind.DatabindContext ctxt,
                Object value, Class<?> type) {
            return idFromValue(ctxt, value);
        }

        @Override
        public JavaType typeFromId(tools.jackson.databind.DatabindContext ctxt, String value) {
            return id.equals(value) ? ctxt.constructType(TypeIdBean.class) : null;
        }

        @Override
        public String idFromBaseType(tools.jackson.databind.DatabindContext ctxt) {
            return "xxx";
        }
    }
static class MyInstantiator extends HandlerInstantiator {
        private final String prefix;

        MyInstantiator(String prefix) { this.prefix = prefix; }

        @Override
        public ValueDeserializer<?> deserializerInstance(DeserializationConfig config,
                Annotated annotated, Class<?> deserClass) {
            return deserClass == MyBeanDeserializer.class ? new MyBeanDeserializer(prefix) : null;
        }

        @Override
        public KeyDeserializer keyDeserializerInstance(DeserializationConfig config,
                Annotated annotated, Class<?> keyDeserClass) {
            return keyDeserClass == MyKeyDeserializer.class ? new MyKeyDeserializer() : null;
        }

        @Override
        public ValueSerializer<?> serializerInstance(SerializationConfig config,
                Annotated annotated, Class<?> serClass) {
            return serClass == MyBeanSerializer.class ? new MyBeanSerializer(prefix) : null;
        }

        @Override
        public TypeIdResolver typeIdResolverInstance(MapperConfig<?> config,
                Annotated annotated, Class<?> resolverClass) {
            return resolverClass == TestCustomIdResolver.class
                    ? new TestCustomIdResolver("!!!") : null;
        }

        @Override
        public TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> config,
                Annotated annotated, Class<?> builderClass) {
            return null;
        }
    }

    void __invoke_boundsWithByteArrayInputVpack() throws Exception {
        try {
            boundsWithByteArrayInputVpack();
        } finally {
        }
    }

}
