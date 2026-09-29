package tools.jackson.databind.module;

import java.util.List;
import java.util.Map;

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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0469F0 {
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

    void testSimpleEnumSerializerVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addSerializer(new SimpleEnumSerializer());
        ObjectMapper mapper = VPackMapper.builder().addModules(module).build();
        assertArrayEquals(ENUM_B, mapper.writeValueAsBytes(SimpleEnum.B));
    }

    void testSimpleInterfaceSerializerVpack() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addSerializer(new BaseSerializer());
        ObjectMapper mapper = VPackMapper.builder().addModules(List.of(module)).build();
        assertArrayEquals(VPackWireFixtureTest.hex("46 42 61 73 65 3a 31"),
                mapper.writeValueAsBytes(new Impl1()));
        assertArrayEquals(VPackWireFixtureTest.hex("46 42 61 73 65 3a 32"),
                mapper.writeValueAsBytes(new Impl2()));
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

    void __invoke_testSimpleEnumSerializerVpack() throws Exception {
        try {
            testSimpleEnumSerializerVpack();
        } finally {
        }
    }


    void __invoke_testSimpleInterfaceSerializerVpack() throws Exception {
        try {
            testSimpleInterfaceSerializerVpack();
        } finally {
        }
    }

}
