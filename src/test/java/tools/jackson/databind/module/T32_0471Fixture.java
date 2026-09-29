package tools.jackson.databind.module;

import java.lang.reflect.Type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.Version;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.module.SimpleDeserializers;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.Serializers;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.CollectionLikeType;
import tools.jackson.databind.type.MapLikeType;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.type.TypeModifier;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0471Fixture {
private static final byte[] MAP_FOR_DESERIALIZATION = VPackWireFixtureTest.hex(
            "14 07 41 61 28 0d 01");
private static final byte[] ARRAY_FOR_DESERIALIZATION = VPackWireFixtureTest.hex(
            "02 04 20 db");
private static final byte[] COLLECTION_SERIALIZATION = VPackWireFixtureTest.hex(
            "02 04 28 13");
private static final byte[] MAP_SERIALIZATION = VPackWireFixtureTest.hex(
            "0b 0c 01 41 78 45 78 78 78 3a 33 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EXPLICIT_NULL = VPackWireFixtureTest.hex(
            "14 09 44 6e 6f 64 65 18 01");
private static final byte[] EXPLICIT_VALUE = VPackWireFixtureTest.hex(
            "14 0a 44 6e 6f 64 65 28 2a 01");
private final ObjectMapper MY_TYPE_MAPPER = VPackMapper.builder()
            .typeFactory(TypeFactory.createDefaultInstance().withModifier(new MyTypeModifier()))
            .build();
private final ObjectMapper MAPPER_WITH_MODIFIER = VPackMapper.builder()
            .typeFactory(TypeFactory.createDefaultInstance().withModifier(new MyTypeModifier()))
            .addModule(new ModifierModule())
            .build();

    void testMapLikeTypeConstructionVpack() throws Exception {
        JavaType type = MY_TYPE_MAPPER.constructType(MyMapLikeType.class);
        assertTrue(type.isMapLikeType());
        JavaType param = ((MapLikeType) type).getKeyType();
        assertNotNull(param);
        assertSame(String.class, param.getRawClass());
        param = ((MapLikeType) type).getContentType();
        assertNotNull(param);
        assertSame(Integer.class, param.getRawClass());
    }

    void testMapLikeTypeViaParametricVpack() {
        JavaType type = MAPPER_WITH_MODIFIER.getTypeFactory().constructParametricType(
                MapMarker.class, String.class, Double.class);
        assertTrue(type.isMapLikeType());
        JavaType param = ((MapLikeType) type).getKeyType();
        assertNotNull(param);
        assertSame(String.class, param.getRawClass());
        param = ((MapLikeType) type).getContentType();
        assertNotNull(param);
        assertSame(Double.class, param.getRawClass());
    }

    void testTypeResolutionForRecursiveVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .typeFactory(TypeFactory.createDefaultInstance().withModifier(new MyTypeModifier()))
                .build();
        assertNotNull(mapper.readTree(EMPTY_OBJECT));
    }

    void testCollectionLikeTypeConstructionVpack() throws Exception {
        JavaType type = MY_TYPE_MAPPER.constructType(MyCollectionLikeType.class);
        assertTrue(type.isCollectionLikeType());
        JavaType param = ((CollectionLikeType) type).getContentType();
        assertNotNull(param);
        assertSame(Integer.class, param.getRawClass());
    }

    void testCollectionLikeSerializationVpack() throws Exception {
        assertArrayEquals(COLLECTION_SERIALIZATION,
                MAPPER_WITH_MODIFIER.writeValueAsBytes(new MyCollectionLikeType(19)));
    }

    void testMapLikeSerializationVpack() throws Exception {
        assertArrayEquals(MAP_SERIALIZATION,
                MAPPER_WITH_MODIFIER.writeValueAsBytes(new MyMapLikeType("x", 3)));
    }

    void testCollectionLikeDeserializationVpack() throws Exception {
        MyMapLikeType result = MAPPER_WITH_MODIFIER.readValue(MAP_FOR_DESERIALIZATION,
                MyMapLikeType.class);
        assertEquals("a", result.getKey());
        assertEquals(Integer.valueOf(13), result.getValue());
    }

    void testMapLikeDeserializationVpack() throws Exception {
        MyCollectionLikeType result = MAPPER_WITH_MODIFIER.readValue(ARRAY_FOR_DESERIALIZATION,
                MyCollectionLikeType.class);
        assertEquals(Integer.valueOf(-37), result.getValue());
    }
static class ModifierModule extends SimpleModule {
        ModifierModule() {
            super("test", Version.unknownVersion());
        }

        @Override
        public void setupModule(SetupContext context) {
            context.addSerializers(new Serializers.Base() {
                @Override
                public ValueSerializer<?> findMapLikeSerializer(SerializationConfig config,
                        MapLikeType type, tools.jackson.databind.BeanDescription.Supplier beanDesc,
                        JsonFormat.Value format, ValueSerializer<Object> keySerializer,
                        tools.jackson.databind.jsontype.TypeSerializer elementTypeSerializer,
                        ValueSerializer<Object> elementValueSerializer) {
                    if (MapMarker.class.isAssignableFrom(type.getRawClass())) {
                        return new MyMapSerializer(keySerializer, elementValueSerializer);
                    }
                    return null;
                }

                @Override
                public ValueSerializer<?> findCollectionLikeSerializer(SerializationConfig config,
                        CollectionLikeType type, tools.jackson.databind.BeanDescription.Supplier beanDesc,
                        JsonFormat.Value format,
                        tools.jackson.databind.jsontype.TypeSerializer elementTypeSerializer,
                        ValueSerializer<Object> elementValueSerializer) {
                    if (CollectionMarker.class.isAssignableFrom(type.getRawClass())) {
                        return new MyCollectionSerializer();
                    }
                    return null;
                }
            });
            context.addDeserializers(new SimpleDeserializers() {
                @Override
                public ValueDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type,
                        DeserializationConfig config, tools.jackson.databind.BeanDescription.Supplier beanDesc,
                        tools.jackson.databind.jsontype.TypeDeserializer elementTypeDeserializer,
                        ValueDeserializer<?> elementDeserializer) {
                    if (CollectionMarker.class.isAssignableFrom(type.getRawClass())) {
                        return new MyCollectionDeserializer();
                    }
                    return null;
                }

                @Override
                public ValueDeserializer<?> findMapLikeDeserializer(MapLikeType type,
                        DeserializationConfig config, tools.jackson.databind.BeanDescription.Supplier beanDesc,
                        tools.jackson.databind.KeyDeserializer keyDeserializer,
                        tools.jackson.databind.jsontype.TypeDeserializer elementTypeDeserializer,
                        ValueDeserializer<?> elementDeserializer) {
                    if (MapMarker.class.isAssignableFrom(type.getRawClass())) {
                        return new MyMapDeserializer();
                    }
                    return null;
                }
            });
        }
    }
static class XxxSerializer extends StdSerializer<Object> {
        XxxSerializer() { super(Object.class); }

        @Override
        public void serialize(Object value, JsonGenerator g, SerializationContext provider) {
            g.writeString("xxx:" + value);
        }
    }
interface MapMarker<K, V> {
        K getKey();
        V getValue();
    }
interface CollectionMarker<V> {
        V getValue();
    }
@JsonSerialize(contentUsing = XxxSerializer.class)
    static class MyMapLikeType implements MapMarker<String, Integer> {
        public String key;
        public int value;

        public MyMapLikeType() { }

        MyMapLikeType(String k, int v) {
            key = k;
            value = v;
        }

        @Override
        public String getKey() { return key; }

        @Override
        public Integer getValue() { return value; }
    }
static class MyCollectionLikeType implements CollectionMarker<Integer> {
        public int value;

        public MyCollectionLikeType() { }

        MyCollectionLikeType(int v) {
            value = v;
        }

        @Override
        public Integer getValue() { return value; }
    }
static class MyMapSerializer extends StdSerializer<MapMarker<?, ?>> {
        protected final ValueSerializer<Object> keySerializer;
        protected final ValueSerializer<Object> valueSerializer;

        MyMapSerializer(ValueSerializer<Object> keySer, ValueSerializer<Object> valueSer) {
            super(MapMarker.class);
            keySerializer = keySer;
            valueSerializer = valueSer;
        }

        @Override
        public void serialize(MapMarker<?, ?> value, JsonGenerator g, SerializationContext provider) {
            g.writeStartObject();
            if (keySerializer == null) {
                g.writeName((String) value.getKey());
            } else {
                keySerializer.serialize(value.getKey(), g, provider);
            }
            if (valueSerializer == null) {
                g.writeNumber(((Number) value.getValue()).intValue());
            } else {
                valueSerializer.serialize(value.getValue(), g, provider);
            }
            g.writeEndObject();
        }
    }
static class MyMapDeserializer extends ValueDeserializer<MapMarker<?, ?>> {
        @Override
        public MapMarker<?, ?> deserialize(JsonParser p, DeserializationContext ctxt) {
            if (p.currentToken() != JsonToken.START_OBJECT) {
                throw new StreamReadException(p, "Wrong token: " + p.currentToken());
            }
            if (p.nextToken() != JsonToken.PROPERTY_NAME) {
                throw new StreamReadException(p, "Wrong token: " + p.currentToken());
            }
            String key = p.currentName();
            if (p.nextToken() != JsonToken.VALUE_NUMBER_INT) {
                throw new StreamReadException(p, "Wrong token: " + p.currentToken());
            }
            int value = p.getIntValue();
            if (p.nextToken() != JsonToken.END_OBJECT) {
                throw new StreamReadException(p, "Wrong token: " + p.currentToken());
            }
            return new MyMapLikeType(key, value);
        }
    }
static class MyCollectionSerializer extends StdSerializer<MyCollectionLikeType> {
        MyCollectionSerializer() { super(MyCollectionLikeType.class); }

        @Override
        public void serialize(MyCollectionLikeType value, JsonGenerator g,
                SerializationContext provider) {
            g.writeStartArray();
            g.writeNumber(value.value);
            g.writeEndArray();
        }
    }
static class MyCollectionDeserializer extends ValueDeserializer<MyCollectionLikeType> {
        @Override
        public MyCollectionLikeType deserialize(JsonParser p, DeserializationContext ctxt) {
            if (p.currentToken() != JsonToken.START_ARRAY) {
                throw new StreamReadException(p, "Wrong token: " + p.currentToken());
            }
            if (p.nextToken() != JsonToken.VALUE_NUMBER_INT) {
                throw new StreamReadException(p, "Wrong token: " + p.currentToken());
            }
            int value = p.getIntValue();
            if (p.nextToken() != JsonToken.END_ARRAY) {
                throw new StreamReadException(p, "Wrong token: " + p.currentToken());
            }
            return new MyCollectionLikeType(value);
        }
    }
static class MyTypeModifier extends TypeModifier {
        @Override
        public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings,
                TypeFactory typeFactory) {
            if (!type.isContainerType()) {
                Class<?> raw = type.getRawClass();
                if (raw == MapMarker.class) {
                    return MapLikeType.upgradeFrom(type, type.containedType(0),
                            type.containedType(1));
                }
                if (raw == CollectionMarker.class) {
                    return CollectionLikeType.upgradeFrom(type, type.containedType(0));
                }
            }
            return type;
        }
    }
static class CreatorBean5861 {
        public final JsonNode node;

        @JsonCreator
        CreatorBean5861(@JsonProperty("node") JsonNode n) {
            node = n;
        }
    }

    void __invoke_testMapLikeTypeConstructionVpack() throws Exception {
        try {
            testMapLikeTypeConstructionVpack();
        } finally {
        }
    }


    void __invoke_testMapLikeTypeViaParametricVpack() throws Exception {
        try {
            testMapLikeTypeViaParametricVpack();
        } finally {
        }
    }


    void __invoke_testTypeResolutionForRecursiveVpack() throws Exception {
        try {
            testTypeResolutionForRecursiveVpack();
        } finally {
        }
    }


    void __invoke_testCollectionLikeTypeConstructionVpack() throws Exception {
        try {
            testCollectionLikeTypeConstructionVpack();
        } finally {
        }
    }


    void __invoke_testCollectionLikeSerializationVpack() throws Exception {
        try {
            testCollectionLikeSerializationVpack();
        } finally {
        }
    }


    void __invoke_testMapLikeSerializationVpack() throws Exception {
        try {
            testMapLikeSerializationVpack();
        } finally {
        }
    }


    void __invoke_testCollectionLikeDeserializationVpack() throws Exception {
        try {
            testCollectionLikeDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testMapLikeDeserializationVpack() throws Exception {
        try {
            testMapLikeDeserializationVpack();
        } finally {
        }
    }

}
