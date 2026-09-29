package tools.jackson.databind.jsontype;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0421F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: SealedTypesWithTypedDeserializationTest#testSimpleClassAsProperty().
    void testSimpleClassAsPropertyVpack() throws Exception {
        Animal value = MAPPER.readValue(classObject(Cat.class.getName(),
                pair("furColor", string("tabby")), pair("name", string("Garfield"))),
                Animal.class);
        Cat cat = assertInstanceOf(Cat.class, value);
        assertEquals("Garfield", cat.name);
        assertEquals("tabby", cat.furColor);
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testTypeAsWrapper().
    void testTypeAsWrapperVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addMixIn(Animal.class, TypeWithWrapper.class).build();
        Animal value = mapper.readValue(wrapperObject(minimalClassId(Dog.class),
                pair("name", string("Scooby")), pair("boneCount", integer(6))), Animal.class);
        Dog dog = assertInstanceOf(Dog.class, value);
        assertEquals("Scooby", dog.name);
        assertEquals(6, dog.boneCount);
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testTypeAsArray().
    void testTypeAsArrayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addMixIn(Animal.class, TypeWithArray.class).build();
        Animal value = mapper.readValue(wrapperArray(Dog.class.getName(),
                pair("name", string("Martti")), pair("boneCount", integer(11))), Animal.class);
        Dog dog = assertInstanceOf(Dog.class, value);
        assertEquals("Martti", dog.name);
        assertEquals(11, dog.boneCount);
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testListAsArray().
    void testListAsArrayVpack() throws Exception {
        byte[] fixture = compactArray(
                classObject(Cat.class.getName(), pair("name", string("Hello")), pair("furColor", string("white"))),
                classObject(Dog.class.getName(), pair("boneCount", integer(1)), pair("name", string("Bob"))),
                classObject(Fish.class.getName()), nullValue());
        List<Animal> animals = MAPPER.readValue(fixture,
                MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Animal.class));
        assertEquals(4, animals.size());
        Cat cat = assertInstanceOf(Cat.class, animals.get(0));
        assertEquals("Hello", cat.name);
        assertEquals("white", cat.furColor);
        Dog dog = assertInstanceOf(Dog.class, animals.get(1));
        assertEquals("Bob", dog.name);
        assertEquals(1, dog.boneCount);
        assertInstanceOf(Fish.class, animals.get(2));
        assertNull(animals.get(3));
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testCagedAnimal().
    void testCagedAnimalVpack() throws Exception {
        AnimalContainer value = MAPPER.readValue(compactObject(pair("animal",
                classObject(Cat.class.getName(), pair("name", string("Nilson")),
                        pair("furColor", string("black"))))), AnimalContainer.class);
        assertNotNull(value);
        Cat cat = assertInstanceOf(Cat.class, value.animal);
        assertEquals("Nilson", cat.name);
        assertEquals("black", cat.furColor);
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testIssue506WithDate().
    void testIssue506WithDateVpack() throws Exception {
        Issue506DateBean input = new Issue506DateBean();
        input.date = new java.util.Date(1234L);
        Issue506DateBean output = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Issue506DateBean.class);
        assertEquals(input.date, output.date);
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testIssue506WithNumber().
    void testIssue506WithNumberVpack() throws Exception {
        Issue506NumberBean input = new Issue506NumberBean();
        input.number = Long.valueOf(4567L);
        Issue506NumberBean output = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Issue506NumberBean.class);
        assertEquals(input.number, output.number);
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testIntAsTypeId1751Array().
    void testIntAsTypeId1751ArrayVpack() throws Exception {
        Issue1751ArrBase numeric = MAPPER.readValue(wrapperArray(integer(0), emptyObject()), Issue1751ArrBase.class);
        Issue1751ArrBase textual = MAPPER.readValue(wrapperArray(string("0"), emptyObject()), Issue1751ArrBase.class);
        assertInstanceOf(Issue1751ArrImpl.class, numeric);
        assertInstanceOf(Issue1751ArrImpl.class, textual);
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testIntAsTypeId1751Prop().
    void testIntAsTypeId1751PropVpack() throws Exception {
        Issue1751PropBase textual = MAPPER.readValue(compactObject(pair("type", string("1"))), Issue1751PropBase.class);
        Issue1751PropBase numeric = MAPPER.readValue(compactObject(pair("type", integer(1))), Issue1751PropBase.class);
        assertInstanceOf(Issue1751PropImpl.class, textual);
        assertInstanceOf(Issue1751PropImpl.class, numeric);
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testTypeAsArrayWithNullableType().
    void testTypeAsArrayWithNullableTypeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addMixIn(Animal.class, TypeWithArray.class).build();
        assertNull(mapper.readValue(wrapperArray(Fish.class.getName()), Animal.class));
    }

    // Provenance: SealedTypesWithTypedDeserializationTest#testTypeAsArrayWithCustomDeserializer().
    void testTypeAsArrayWithCustomDeserializerVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addMixIn(Animal.class, TypeWithArray.class).build();
        Animal value = mapper.readValue(wrapperArray(NullAnimal.class.getName()), Animal.class);
        assertInstanceOf(NullAnimal.class, value);
        assertNull(value.name);
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
        return ".T32_0421F0$" + type.getSimpleName();
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

    void __invoke_testSimpleClassAsPropertyVpack() throws Exception {
        try {
            testSimpleClassAsPropertyVpack();
        } finally {
        }
    }


    void __invoke_testTypeAsWrapperVpack() throws Exception {
        try {
            testTypeAsWrapperVpack();
        } finally {
        }
    }


    void __invoke_testTypeAsArrayVpack() throws Exception {
        try {
            testTypeAsArrayVpack();
        } finally {
        }
    }


    void __invoke_testListAsArrayVpack() throws Exception {
        try {
            testListAsArrayVpack();
        } finally {
        }
    }


    void __invoke_testCagedAnimalVpack() throws Exception {
        try {
            testCagedAnimalVpack();
        } finally {
        }
    }


    void __invoke_testIssue506WithDateVpack() throws Exception {
        try {
            testIssue506WithDateVpack();
        } finally {
        }
    }


    void __invoke_testIssue506WithNumberVpack() throws Exception {
        try {
            testIssue506WithNumberVpack();
        } finally {
        }
    }


    void __invoke_testIntAsTypeId1751ArrayVpack() throws Exception {
        try {
            testIntAsTypeId1751ArrayVpack();
        } finally {
        }
    }


    void __invoke_testIntAsTypeId1751PropVpack() throws Exception {
        try {
            testIntAsTypeId1751PropVpack();
        } finally {
        }
    }


    void __invoke_testTypeAsArrayWithNullableTypeVpack() throws Exception {
        try {
            testTypeAsArrayWithNullableTypeVpack();
        } finally {
        }
    }


    void __invoke_testTypeAsArrayWithCustomDeserializerVpack() throws Exception {
        try {
            testTypeAsArrayWithCustomDeserializerVpack();
        } finally {
        }
    }

}
