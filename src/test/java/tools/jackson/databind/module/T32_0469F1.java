package tools.jackson.databind.module;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.module.SimpleSerializers;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0469F1 {
private static final byte[] ENUM_B = VPackWireFixtureTest.hex("41 62");
private static final byte[] ENUM_KEYS = VPackWireFixtureTest.hex(
            "0b 0f 01 43 72 65 64 13 07 41 61 41 62 02 03");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NESTED_EMPTY_OBJECT = VPackWireFixtureTest.hex(
            "02 03 0a");
private static final byte[] RECURSIVE_MAP = VPackWireFixtureTest.hex(
            "0b 09 01 41 78 02 03 33 03");
private static final byte[] NESTED_MAP = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 61 61 61 03");
private static final byte[] CHAR_SEQUENCE = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] POLYMORPHIC_MAP = VPackWireFixtureTest.hex(
            "0b 18 01 47 73 6f 6d 65 4d 61 70 0b 0c 01 43 46 4f 4f 43 62 61 72 03 03");
private static final byte[] MODIFIER_VALUE = VPackWireFixtureTest.hex(
            "4c 52 45 50 4c 41 43 45 4d 45 4e 54 53");
private static final byte[] MODIFIER_KEYS = VPackWireFixtureTest.hex(
            "0b 18 01 4c 52 45 50 4c 61 63 65 4d 45 4e 54 53 46 66 6f 6f 62 61 72 03");

    void testCollectionDefaultingVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addAbstractTypeMapping(Collection.class, List.class);
        module.addAbstractTypeMapping(List.class, LinkedList.class);
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        Collection<?> result = mapper.readValue(EMPTY_ARRAY, Collection.class);
        assertEquals(LinkedList.class, result.getClass());
    }

    void testMapDefaultingBasicVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addAbstractTypeMapping(Map.class, TreeMap.class);
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        Map<?, ?> result = mapper.readValue(EMPTY_OBJECT, Map.class);
        assertEquals(TreeMap.class, result.getClass());
    }

    void testDefaultingRecursiveVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addAbstractTypeMapping(Map.class, TreeMap.class);
        module.addAbstractTypeMapping(List.class, LinkedList.class);
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Object result = mapper.readValue(NESTED_EMPTY_OBJECT, Object.class);
        assertEquals(LinkedList.class, result.getClass());
        assertEquals(TreeMap.class, ((List<?>) result).get(0).getClass());

        result = mapper.readValue(RECURSIVE_MAP, Object.class);
        assertEquals(TreeMap.class, result.getClass());
        Object value = ((Map<?, ?>) result).get("x");
        assertEquals(LinkedList.class, value.getClass());
        assertEquals(List.of(3), value);
    }

    void testInterfaceDefaultingVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addAbstractTypeMapping(CharSequence.class, MyString.class);
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        CharSequence result = mapper.readValue(CHAR_SEQUENCE, CharSequence.class);
        assertEquals(MyString.class, result.getClass());
        assertEquals("abc", ((MyString) result).value);

        module = new SimpleModule();
        module.addAbstractTypeMapping(Abstract.class, AbstractImpl.class);
        mapper = VPackMapper.builder().addModule(module).build();
        assertNotNull(mapper.readValue(EMPTY_OBJECT, Abstract.class));
    }

    void testAbstractMappingsFromTwoModulesVpack() throws Exception {
        SimpleModule first = new SimpleModule("module1");
        first.addAbstractTypeMapping(Datatype1.class, SimpleDatatype1.class);
        SimpleModule second = new SimpleModule("module2");
        second.addAbstractTypeMapping(Datatype2.class, SimpleDatatype2.class);
        ObjectMapper mapper = VPackMapper.builder().addModules(first, second).build();

        Datatype1 value1 = mapper.readValue(NESTED_MAP, Datatype1.class);
        Datatype2 value2 = mapper.readValue(NESTED_MAP, Datatype2.class);
        assertEquals("aaa", value1.getValue());
        assertEquals("aaa", value2.getValue());
    }
enum SimpleEnum { A, B }
static class SimpleEnumSerializer extends StdScalarSerializer<SimpleEnum> {
        SimpleEnumSerializer() { super(SimpleEnum.class); }

        @Override
        public void serialize(SimpleEnum value, JsonGenerator generator,
                SerializationContext provider) throws JacksonException {
            generator.writeString(value.name().toLowerCase());
        }
    }
interface Base { String getText(); }
static class Impl1 implements Base {
        @Override public String getText() { return "1"; }
    }
static class Impl2 extends Impl1 {
        @Override public String getText() { return "2"; }
    }
static class BaseSerializer extends StdScalarSerializer<Base> {
        BaseSerializer() { super(Base.class); }

        @Override
        public void serialize(Base value, JsonGenerator generator,
                SerializationContext provider) {
            generator.writeString("Base:" + value.getText());
        }
    }
static class MyString implements CharSequence {
        final String value;
        MyString(String value) { this.value = value; }
        @Override public char charAt(int index) { return value.charAt(index); }
        @Override public int length() { return value.length(); }
        @Override public CharSequence subSequence(int start, int end) { return this; }
    }
interface Abstract { int getValue(); }
static class AbstractImpl implements Abstract {
        @Override public int getValue() { return 3; }
    }
interface Datatype1 { String getValue(); }
interface Datatype2 { String getValue(); }
static class SimpleDatatype1 implements Datatype1 {
        private final String value;
        @JsonCreator
        SimpleDatatype1(@JsonProperty("value") String value) { this.value = value; }
        @Override public String getValue() { return value; }
    }
static class SimpleDatatype2 implements Datatype2 {
        private final String value;
        @JsonCreator
        SimpleDatatype2(@JsonProperty("value") String value) { this.value = value; }
        @Override public String getValue() { return value; }
    }
@JsonSerialize(using = TestEnumSerializer.class, keyUsing = TestEnumKeySerializer.class)
    @JsonDeserialize(using = TestEnumDeserializer.class, keyUsing = TestEnumKeyDeserializer.class)
    enum TestEnumMixin { }
enum TestEnum {
        RED("red"), GREEN("green");
        private final String code;
        TestEnum(String code) { this.code = code; }
        static TestEnum lookup(String value) {
            for (TestEnum item : values()) {
                if (item.code.equals(value)) return item;
            }
            throw new IllegalArgumentException(value);
        }
        String code() { return code; }
    }
static class TestEnumSerializer extends ValueSerializer<TestEnum> {
        @Override public void serialize(TestEnum value, JsonGenerator generator,
                SerializationContext ctxt) { generator.writeString(value.code()); }
        @Override public Class<TestEnum> handledType() { return TestEnum.class; }
    }
static class TestEnumDeserializer extends ValueDeserializer<TestEnum> {
        @Override public TestEnum deserialize(JsonParser parser, DeserializationContext ctxt) {
            return TestEnum.lookup(parser.getString());
        }
    }
static class TestEnumKeyDeserializer extends KeyDeserializer {
        @Override public Object deserializeKey(String key, DeserializationContext ctxt) {
            return TestEnum.lookup(key);
        }
    }
static class TestEnumKeySerializer extends ValueSerializer<TestEnum> {
        @Override public void serialize(TestEnum value, JsonGenerator generator,
                SerializationContext ctxt) { generator.writeName(value.code()); }
        @Override public Class<TestEnum> handledType() { return TestEnum.class; }
    }
static class TestEnumModule extends SimpleModule {
        TestEnumModule() { super(Version.unknownVersion()); }
        @Override public void setupModule(SetupContext context) {
            context.setMixIn(TestEnum.class, TestEnumMixin.class);
            SimpleSerializers serializers = new SimpleSerializers();
            serializers.addSerializer(new TestEnumKeySerializer());
            context.addKeySerializers(serializers);
        }
    }
enum KeyEnum { replacements, rootDirectory, licenseString }
enum SuperTypeEnum { FOO }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type", defaultImpl = SuperType.class)
    static class SuperType { public Map<SuperTypeEnum, String> someMap; }

    void __invoke_testCollectionDefaultingVpack() throws Exception {
        try {
            testCollectionDefaultingVpack();
        } finally {
        }
    }


    void __invoke_testMapDefaultingBasicVpack() throws Exception {
        try {
            testMapDefaultingBasicVpack();
        } finally {
        }
    }


    void __invoke_testDefaultingRecursiveVpack() throws Exception {
        try {
            testDefaultingRecursiveVpack();
        } finally {
        }
    }


    void __invoke_testInterfaceDefaultingVpack() throws Exception {
        try {
            testInterfaceDefaultingVpack();
        } finally {
        }
    }


    void __invoke_testAbstractMappingsFromTwoModulesVpack() throws Exception {
        try {
            testAbstractMappingsFromTwoModulesVpack();
        } finally {
        }
    }

}
