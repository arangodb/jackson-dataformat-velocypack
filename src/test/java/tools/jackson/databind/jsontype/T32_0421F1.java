package tools.jackson.databind.jsontype;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.core.JsonParser;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0421F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: SealedTypesWithTypedSerializationTest#testSimpleClassAsProperty().
    void testSerializationSimpleClassAsPropertyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addMixIn(Animal.class, DefaultClassProperty.class).build();
        Map<?, ?> encoded = mapper.readValue(mapper.writeValueAsBytes(new Cat("Beelzebub", "tabby")), Map.class);
        assertEquals(3, encoded.size());
        assertEquals("Beelzebub", encoded.get("name"));
        assertEquals("tabby", encoded.get("furColor"));
        assertEquals(Cat.class.getName(), encoded.get("@class"));
    }
private static byte[] classObject(String className, byte[]... properties) {
        byte[][] all = new byte[properties.length + 1][];
        all[0] = pair("@classy", string(className));
        System.arraycopy(properties, 0, all, 1, properties.length);
        return compactObject(all);
    }
private static byte[] wrapperObject(String key, byte[]... properties) {
        return compactObject(pair(key, compactObject(properties)));
    }
private static byte[] wrapperArray(String typeId, byte[]... properties) {
        if (properties.length == 0) {
            return compactArray(string(typeId));
        }
        return wrapperArray(string(typeId), compactObject(properties));
    }
private static byte[] wrapperArray(byte[] typeId, byte[] value) {
        return compactArray(typeId, value);
    }
private static byte[] compactObject(byte[]... properties) {
        return compact(0x14, properties, properties.length);
    }
private static byte[] compactArray(byte[]... values) {
        return compact(0x13, values, values.length);
    }
private static byte[] compact(int marker, byte[][] values, int count) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (byte[] value : values) {
            if (value != null) body.writeBytes(value);
        }
        int lengthWidth = 1;
        int countWidth = reverseVarint(count).length;
        byte[] length;
        do {
            int totalLength = 1 + lengthWidth + body.size() + countWidth;
            length = forwardVarint(totalLength);
            if (length.length == lengthWidth) break;
            lengthWidth = length.length;
        } while (true);
        byte[] result = new byte[1 + length.length + body.size() + reverseVarint(count).length];
        int offset = 0;
        result[offset++] = (byte) marker;
        System.arraycopy(length, 0, result, offset, length.length);
        offset += length.length;
        byte[] bytes = body.toByteArray();
        System.arraycopy(bytes, 0, result, offset, bytes.length);
        offset += bytes.length;
        byte[] reverse = reverseVarint(count);
        System.arraycopy(reverse, 0, result, offset, reverse.length);
        return result;
    }
private static byte[] pair(String name, byte[] value) {
        byte[] key = name.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(0x40 + key.length);
        result.writeBytes(key);
        result.writeBytes(value);
        return result.toByteArray();
    }
private static byte[] string(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 126) throw new IllegalArgumentException("fixture string too long");
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(0x40 + bytes.length);
        result.writeBytes(bytes);
        return result.toByteArray();
    }
private static byte[] integer(int value) {
        if (value >= 0 && value <= 9) return new byte[] { (byte) (0x30 + value) };
        if (value >= 0 && value <= 255) return new byte[] { 0x28, (byte) value };
        throw new IllegalArgumentException("fixture integer out of range");
    }
private static byte[] nullValue() { return new byte[] { 0x18 }; }
private static byte[] emptyObject() { return new byte[] { 0x0a }; }
private static String minimalClassId(Class<?> type) {
        return ".T32_0421F1$" + type.getSimpleName();
    }
private static byte[] forwardVarint(int value) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        do {
            int group = value & 0x7f;
            value >>>= 7;
            result.write(value == 0 ? group : group | 0x80);
        } while (value != 0);
        return result.toByteArray();
    }
private static byte[] reverseVarint(int value) {
        byte[] forward = forwardVarint(value);
        byte[] result = new byte[forward.length];
        for (int i = 0; i < forward.length; ++i) {
            int source = forward[forward.length - 1 - i] & 0x7f;
            result[i] = (byte) (source | (i > 0 ? 0x80 : 0));
        }
        return result;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@classy")
    public static sealed abstract class Animal permits Dog, Cat, Fish, NullAnimal {
        public String name;
        protected Animal(String name) { this.name = name; }
    }
@JsonTypeName("doggie")
    static final class Dog extends Animal {
        public int boneCount;
        private Dog() { super(null); }
        Dog(String name, int count) { super(name); boneCount = count; }
        @JsonCreator Dog(@JsonProperty("name") String name) { super(name); }
        public void setBoneCount(int value) { boneCount = value; }
    }
@JsonTypeName("kitty")
    static final class Cat extends Animal {
        public String furColor;
        @JsonCreator Cat(@JsonProperty("furColor") String color) { super(null); furColor = color; }
        public void setName(String value) { name = value; }
        Cat(String name, String color) { super(name); furColor = color; }
    }
@JsonTypeName("fishy")
    static final class Fish extends Animal {
        Fish() { super(null); }
    }
@JsonDeserialize(using = NullAnimalDeserializer.class)
    static final class NullAnimal extends Animal {
        NullAnimal() { super(null); }
        static final NullAnimal NULL_INSTANCE = new NullAnimal();
    }
static class NullAnimalDeserializer extends ValueDeserializer<NullAnimal> {
        @Override public NullAnimal getNullValue(DeserializationContext context) { return NullAnimal.NULL_INSTANCE; }
        @Override public NullAnimal deserialize(JsonParser parser, DeserializationContext context) {
            throw new UnsupportedOperationException();
        }
    }
static class AnimalContainer { public Animal animal; }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    interface TypeWithWrapper { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    interface DefaultClassProperty { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    interface TypeWithArray { }
static class Issue506DateBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type2")
        public java.util.Date date;
    }
static class Issue506NumberBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type3")
        @JsonSubTypes({ @JsonSubTypes.Type(Long.class), @JsonSubTypes.Type(Integer.class) })
        public Number number;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    static sealed interface Issue1751ArrBase permits Issue1751ArrImpl { }
@JsonTypeName("0") static final class Issue1751ArrImpl implements Issue1751ArrBase { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = Issue1751PropImpl.class, name = "1"))
    interface Issue1751PropBase { }
static class Issue1751PropImpl implements Issue1751PropBase { }
static class AnimalWrapper {
        public Animal animal;
        AnimalWrapper(Animal value) { animal = value; }
    }

    void __invoke_testSerializationSimpleClassAsPropertyVpack() throws Exception {
        try {
            testSerializationSimpleClassAsPropertyVpack();
        } finally {
        }
    }

}
