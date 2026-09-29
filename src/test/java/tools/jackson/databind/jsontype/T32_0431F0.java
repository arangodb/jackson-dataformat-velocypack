package tools.jackson.databind.jsontype;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0431F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper ID_MAPPER = VPackMapper.builder(VPackFactory.builder()
            .attributeNameCodec(new VPackAttributeNameCodec() {
                @Override
                public String decode(BigInteger id) {
                    return BigInteger.ONE.equals(id) ? "1" : null;
                }

                @Override
                public BigInteger encode(String name) {
                    return "1".equals(name) ? BigInteger.ONE : null;
                }
            }).build()).build();
private static final byte[] VISIBLE_PROPERTY_REVERSED = VPackWireFixtureTest.hex(
            "14 14 41 61 37 44 74 79 70 65 48 42 61 73 65 54 79 70 65 02");
private static final byte[] VISIBLE_WRAPPER_ARRAY = VPackWireFixtureTest.hex(
            "13 13 49 41 72 72 61 79 54 79 70 65 14 06 41 61 31 01 02");
private static final byte[] VISIBLE_WRAPPER_OBJECT = VPackWireFixtureTest.hex(
            "14 14 4a 4f 62 6a 65 63 74 54 79 70 65 14 06 41 61 32 01 01");
private static final byte[] ISSUE_263 = VPackWireFixtureTest.hex(
            "14 12 43 61 67 65 28 13 44 6e 61 6d 65 43 62 6f 62 02");
private static final byte[] VISIBLE_EXTERNAL_408 = VPackWireFixtureTest.hex(
            "14 1d 44 62 65 61 6e 14 0a 45 76 61 6c 75 65 33 01 "
          + "44 74 79 70 65 45 76 62 65 61 6e 02");

    // Provenance: TestTypedSerialization#testTypeAsWrapper().
    void testTypeAsWrapperVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Animal0431.class, TypeWithWrapper0431.class).build();
        Map<?, ?> result = MAPPER.readValue(mapper.writeValueAsBytes(
                new Cat0431("Venla", "black")), Map.class);

        assertEquals(1, result.size());
        Map<?, ?> cat = assertInstanceOf(Map.class,
                result.get(".T32_0431F0$Cat0431"));
        assertEquals(2, cat.size());
        assertEquals("Venla", cat.get("name"));
        assertEquals("black", cat.get("furColor"));
    }

    // Provenance: TestTypedSerialization#testTypedMaps().
    void testTypedMapsVpack() throws Exception {
        Map<Long, Collection<Super0431>> input = new HashMap<>();
        input.put(1L, List.of(new A0431()));
        TypeReference<Map<Long, Collection<Super0431>>> type = new TypeReference<>() { };

        ObjectWriter writer = ID_MAPPER.writerFor(type);
        Map<?, ?> result = ID_MAPPER.readValue(writer.writeValueAsBytes(input), Map.class);
        List<?> values = assertInstanceOf(List.class, result.get("1"));
        Map<?, ?> typed = assertInstanceOf(Map.class, values.get(0));
        assertEquals(A0431.class.getName(), typed.get("@class"));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    interface TypeWithWrapper0431 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    static abstract class Animal0431 {
        public String name;
        Animal0431() { }
        Animal0431(String name) { this.name = name; }
    }
static class Cat0431 extends Animal0431 {
        public String furColor;
        Cat0431() { }
        Cat0431(String name, String furColor) {
            super(name);
            this.furColor = furColor;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    static abstract class Super0431 { }
static class A0431 extends Super0431 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type", visible = true)
    @JsonTypeName("BaseType")
    static class PropertyBean0431 {
        public int a = 3;
        protected String type;
        public void setType(String value) { type = value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "type", visible = true)
    @JsonTypeName("ArrayType")
    static class WrapperArrayBean0431 {
        public int a = 1;
        protected String type;
        public void setType(String value) { type = value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT,
            property = "type", visible = true)
    @JsonTypeName("ObjectType")
    static class WrapperObjectBean0431 {
        public int a = 2;
        protected String type;
        public void setType(String value) { type = value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    static class TypeIdFromFieldProperty0431 {
        public int a = 3;
        @JsonTypeId
        public String type = "SomeType";
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "type")
    static class TypeIdFromFieldArray0431 {
        public int a = 3;
        @JsonTypeId
        public String type = "SomeType";
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT,
            property = "type")
    static class TypeIdFromMethodObject0431 {
        public int a = 3;
        @JsonTypeId
        public String getType() { return "SomeType"; }
    }
static class ExternalIdWrapper0431 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type", visible = true)
        public ExternalIdBean0431 bean = new ExternalIdBean0431();
    }
@JsonTypeName("SomeType")
    static class ExternalIdBean0431 {
        public int a = 2;
        protected String type;
        public void setType(String value) { type = value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name")
    @JsonSubTypes(@JsonSubTypes.Type(value = I263Impl0431.class))
    static abstract class I263Base0431 {
        @JsonTypeId
        public abstract String getName();
    }
@JsonTypeName("bob")
    static class I263Impl0431 extends I263Base0431 {
        @Override
        public String getName() { return "bob"; }
        public int age = 41;
    }
static class ExternalBeanWithId0431 {
        protected String type;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type", visible = true)
        public ValueBean0431 bean;
        ExternalBeanWithId0431() { }
        ExternalBeanWithId0431(int value) { bean = new ValueBean0431(value); }
        public void setType(String value) { type = value; }
    }
@JsonTypeName("vbean")
    static class ValueBean0431 {
        public int value;
        ValueBean0431() { }
        ValueBean0431(int value) { this.value = value; }
    }
static class MultipleIds0431 {
        @JsonTypeId
        public String type1 = "type1";
        @JsonTypeId
        public String getType2() { return "type2"; }
    }

    void __invoke_testTypeAsWrapperVpack() throws Exception {
        try {
            testTypeAsWrapperVpack();
        } finally {
        }
    }


    void __invoke_testTypedMapsVpack() throws Exception {
        try {
            testTypedMapsVpack();
        } finally {
        }
    }

}
