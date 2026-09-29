package tools.jackson.databind.jsontype;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonTypeIdResolver;
import tools.jackson.databind.jsontype.TypeIdResolver;
import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0411F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] PROP_TYPE_X = VPackWireFixtureTest.hex(
            "0b 26 01 47 77 72 61 70 70 65 64 "
          + "0b 1a 02 44 74 79 70 65 45 74 79 70 65 58 "
          + "45 76 61 6c 75 65 43 78 79 7a 03 0e 03");
private static final byte[] PROP_DEFAULT = VPackWireFixtureTest.hex(
            "0b 1a 01 47 77 72 61 70 70 65 64 "
          + "0b 0e 01 45 76 61 6c 75 65 43 78 79 7a 03 03");
private static final byte[] ARRAY_TYPE_X = VPackWireFixtureTest.hex(
            "0b 25 01 47 77 72 61 70 70 65 64 "
          + "06 19 02 45 74 79 70 65 58 0b 0e 01 45 76 61 6c 75 65 "
          + "43 78 79 7a 03 03 09 03");
private static final byte[] OBJECT_TYPE_X = VPackWireFixtureTest.hex(
            "0b 24 01 47 77 72 61 70 70 65 64 "
          + "0b 18 01 45 74 79 70 65 58 0b 0e 01 45 76 61 6c 75 65 "
          + "43 78 79 7a 03 03 03");

    // Provenance: ExistingPropertyTest#test3251WithExistingProperty().
    void test3251WithExistingPropertyVpack() throws Exception {
        GenericWrapperWithExisting3251<?> wrapper = new GenericWrapperWithExisting3251<>(123.5);
        GenericWrapperWithExisting3251<?> result = MAPPER.readValue(
                MAPPER.writeValueAsBytes(wrapper), GenericWrapperWithExisting3251.class);
        assertEquals(123.5, result.getValue());
        assertEquals(Double.class, result.getValue().getClass());
    }

    // Provenance: ExistingPropertyTest#test3251WithNewProperty().
    void test3251WithNewPropertyVpack() throws Exception {
        GenericWrapperWithNew3251<?> wrapper = new GenericWrapperWithNew3251<>(123.5);
        GenericWrapperWithNew3251<?> result = MAPPER.readValue(
                MAPPER.writeValueAsBytes(wrapper), GenericWrapperWithNew3251.class);
        assertEquals(123.5, result.getValue());
        assertEquals(Double.class, result.getValue().getClass());
    }

    // Provenance: ExistingPropertyTest#testDeserializationNull().
    void testDeserializationNullVpack() throws Exception {
        Shape3271 result = MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 0a 01 44 74 79 70 65 18 03"), Shape3271.class);
        assertNull(result.getType());
    }
private static void assertBytes(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual, "actual=" + hex(actual));
    }
private static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte value : bytes) {
            if (result.length() > 0) result.append(' ');
            result.append(String.format("%02x", value & 0xff));
        }
        return result.toString();
    }
static class Wrapper4407Prop {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
                defaultImpl = Default4407.class, property = "type")
        @JsonTypeIdResolver(Resolver4407TypeX.class)
        public Base4407 wrapped;
        Wrapper4407Prop() { }
        Wrapper4407Prop(String value) { wrapped = new Impl4407(value); }
    }
static class Wrapper4407PropNull {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
                defaultImpl = Default4407.class, property = "type")
        @JsonTypeIdResolver(Resolver4407Null.class)
        public Base4407 wrapped;
        Wrapper4407PropNull() { }
        Wrapper4407PropNull(String value) { wrapped = new Impl4407(value); }
    }
static class Wrapper4407WrapperArray {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY,
                defaultImpl = Default4407.class)
        @JsonTypeIdResolver(Resolver4407TypeX.class)
        public Base4407 wrapped;
        Wrapper4407WrapperArray() { }
        Wrapper4407WrapperArray(String value) { wrapped = new Impl4407(value); }
    }
static class Wrapper4407WrapperArrayNull {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY,
                defaultImpl = Default4407.class)
        @JsonTypeIdResolver(Resolver4407Null.class)
        public Base4407 wrapped;
        Wrapper4407WrapperArrayNull(String value) { wrapped = new Impl4407(value); }
    }
static class Wrapper4407WrapperObject {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT,
                defaultImpl = Default4407.class)
        @JsonTypeIdResolver(Resolver4407TypeX.class)
        public Base4407 wrapped;
        Wrapper4407WrapperObject() { }
        Wrapper4407WrapperObject(String value) { wrapped = new Impl4407(value); }
    }
static class Wrapper4407WrapperObjectNull {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT,
                defaultImpl = Default4407.class)
        @JsonTypeIdResolver(Resolver4407Null.class)
        public Base4407 wrapped;
        Wrapper4407WrapperObjectNull(String value) { wrapped = new Impl4407(value); }
    }
@JsonSubTypes(@JsonSubTypes.Type(value = Impl4407.class))
    static class Base4407 { }
static class Impl4407 extends Base4407 {
        public String value;
        Impl4407() { }
        Impl4407(String value) { this.value = value; }
    }
static class Default4407 extends Base4407 {
        public String value;
        Default4407() { }
        Default4407(String value) { this.value = value; }
    }
static abstract class Resolver4407Base implements TypeIdResolver {
        private final String id;
        Resolver4407Base(String id) { this.id = id; }
        @Override public void init(JavaType baseType) { }
        @Override public String idFromValue(DatabindContext ctxt, Object value) { return id; }
        @Override public String idFromValueAndType(DatabindContext ctxt, Object value,
                Class<?> suggestedType) { return idFromValue(ctxt, value); }
        @Override public String idFromBaseType(DatabindContext ctxt) { return "default"; }
        @Override public JavaType typeFromId(DatabindContext ctxt, String value) {
            return value.equals(id) ? ctxt.constructType(Impl4407.class) : null;
        }
        @Override public String getDescForKnownTypeIds() { return null; }
        @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.CUSTOM; }
    }
public static class Resolver4407TypeX extends Resolver4407Base {
        public Resolver4407TypeX() { super("typeX"); }
    }
public static class Resolver4407Null extends Resolver4407Base {
        public Resolver4407Null() { super(null); }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeIdResolver(CustomResolver.class)
    static abstract class CustomBean { }
static class CustomBeanImpl extends CustomBean {
        public int x;
        CustomBeanImpl() { }
        CustomBeanImpl(int x) { this.x = x; }
    }
static class CustomResolver implements TypeIdResolver {
        static List<JavaType> initTypes;
        @Override public void init(JavaType baseType) { initTypes.add(baseType); }
        @Override public String idFromValue(DatabindContext ctxt, Object value) { return "*"; }
        @Override public String idFromValueAndType(DatabindContext ctxt, Object value,
                Class<?> suggestedType) { return idFromValue(ctxt, value); }
        @Override public String idFromBaseType(DatabindContext ctxt) { return "xxx"; }
        @Override public JavaType typeFromId(DatabindContext ctxt, String id) {
            return "*".equals(id) ? ctxt.constructType(CustomBeanImpl.class) : null;
        }
        @Override public String getDescForKnownTypeIds() { return null; }
        @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.CUSTOM; }
    }
static class ExtBean { }
static class ExtBeanImpl extends ExtBean {
        public int y;
        ExtBeanImpl() { }
        ExtBeanImpl(int y) { this.y = y; }
    }
static class ExtBeanWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonTypeIdResolver(ExtResolver.class)
        public ExtBean value;
    }
static class ExtResolver implements TypeIdResolver {
        @Override public void init(JavaType baseType) { }
        @Override public String idFromValue(DatabindContext ctxt, Object value) { return "*"; }
        @Override public String idFromValueAndType(DatabindContext ctxt, Object value,
                Class<?> suggestedType) { return "*"; }
        @Override public String idFromBaseType(DatabindContext ctxt) { return "xxx"; }
        @Override public JavaType typeFromId(DatabindContext ctxt, String id) {
            return "*".equals(id) ? ctxt.constructType(ExtBeanImpl.class) : null;
        }
        @Override public String getDescForKnownTypeIds() { return null; }
        @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.CUSTOM; }
    }
static class Top1270 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
                property = "type")
        @JsonTypeIdResolver(Resolver1270.class)
        public Base1270<?> b;
    }
static class Base1270<O extends Poly1Base> {
        public O options;
        public String val;
    }
static abstract class Poly1Base { }
static class Poly1 extends Poly1Base { public String val; }
static class Resolver1270 implements TypeIdResolver {
        @Override public void init(JavaType baseType) { }
        @Override public String idFromValue(DatabindContext ctxt, Object value) { return "poly1"; }
        @Override public String idFromValueAndType(DatabindContext ctxt, Object value,
                Class<?> suggestedType) { return "poly1"; }
        @Override public String idFromBaseType(DatabindContext ctxt) { return null; }
        @Override public JavaType typeFromId(DatabindContext ctxt, String id) {
            return "poly1".equals(id)
                    ? ctxt.getTypeFactory().constructType(new TypeReference<Base1270<Poly1>>() { })
                    : null;
        }
        @Override public String getDescForKnownTypeIds() { return null; }
        @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.CUSTOM; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type_alias")
    static class GenericWrapperWithNew3251<T> {
        private final T value;
        @JsonCreator GenericWrapperWithNew3251(@JsonProperty("value") T value) { this.value = value; }
        public T getValue() { return value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "fieldType", visible = true, defaultImpl = GenericWrapperWithExisting3251.class)
    static class GenericWrapperWithExisting3251<T> {
        public String fieldType;
        private final T value;
        @JsonCreator GenericWrapperWithExisting3251(@JsonProperty("value") T value) { this.value = value; }
        public T getValue() { return value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            visible = true, property = "type", defaultImpl = DefaultShape3271.class)
    @JsonSubTypes(@JsonSubTypes.Type(value = Square3271.class, name = "square"))
    static abstract class Shape3271 {
        public String type;
        public String getType() { return type; }
    }
static class Square3271 extends Shape3271 { }
static class DefaultShape3271 extends Shape3271 { }

    void __invoke_test3251WithExistingPropertyVpack() throws Exception {
        try {
            test3251WithExistingPropertyVpack();
        } finally {
        }
    }


    void __invoke_test3251WithNewPropertyVpack() throws Exception {
        try {
            test3251WithNewPropertyVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationNullVpack() throws Exception {
        try {
            testDeserializationNullVpack();
        } finally {
        }
    }

}
