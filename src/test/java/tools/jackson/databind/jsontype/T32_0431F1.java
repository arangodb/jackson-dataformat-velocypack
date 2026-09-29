package tools.jackson.databind.jsontype;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0431F1 {
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

    // Provenance: TestVisibleTypeId#testVisibleWithProperty().
    void testVisibleWithPropertyVpack() throws Exception {
        byte[] encoded = MAPPER.writeValueAsBytes(new PropertyBean0431());
        Map<?, ?> serialized = MAPPER.readValue(encoded, Map.class);
        assertEquals(3, serialized.get("a"));
        assertEquals("BaseType", serialized.get("type"));

        PropertyBean0431 result = MAPPER.readValue(VISIBLE_PROPERTY_REVERSED,
                PropertyBean0431.class);
        assertEquals(7, result.a);
        assertEquals("BaseType", result.type);
    }

    // Provenance: TestVisibleTypeId#testVisibleWithWrapperArray().
    void testVisibleWithWrapperArrayVpack() throws Exception {
        List<?> serialized = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new WrapperArrayBean0431()), List.class);
        assertEquals(2, serialized.size());
        assertEquals("ArrayType", serialized.get(0));
        assertEquals(1, ((Map<?, ?>) serialized.get(1)).get("a"));

        WrapperArrayBean0431 result = MAPPER.readValue(VISIBLE_WRAPPER_ARRAY,
                WrapperArrayBean0431.class);
        assertEquals(1, result.a);
        assertEquals("ArrayType", result.type);
    }

    // Provenance: TestVisibleTypeId#testVisibleWithWrapperObject().
    void testVisibleWithWrapperObjectVpack() throws Exception {
        Map<?, ?> serialized = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new WrapperObjectBean0431()), Map.class);
        Map<?, ?> value = assertInstanceOf(Map.class, serialized.get("ObjectType"));
        assertEquals(2, value.get("a"));

        WrapperObjectBean0431 result = MAPPER.readValue(VISIBLE_WRAPPER_OBJECT,
                WrapperObjectBean0431.class);
        assertEquals("ObjectType", result.type);
        assertEquals(2, result.a);
    }

    // Provenance: TestVisibleTypeId#testTypeIdFromProperty().
    void testTypeIdFromPropertyVpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new TypeIdFromFieldProperty0431()), Map.class);
        assertEquals("SomeType", result.get("type"));
        assertEquals(3, result.get("a"));
        assertFalse(result.containsKey("@type"));
    }

    // Provenance: TestVisibleTypeId#testTypeIdFromArray().
    void testTypeIdFromArrayVpack() throws Exception {
        List<?> result = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new TypeIdFromFieldArray0431()), List.class);
        assertEquals(List.of("SomeType"), result.subList(0, 1));
        assertEquals(3, ((Map<?, ?>) result.get(1)).get("a"));
    }

    // Provenance: TestVisibleTypeId#testTypeIdFromObject().
    void testTypeIdFromObjectVpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new TypeIdFromMethodObject0431()), Map.class);
        Map<?, ?> value = assertInstanceOf(Map.class, result.get("SomeType"));
        assertEquals(3, value.get("a"));
    }

    // Provenance: TestVisibleTypeId#testTypeIdFromExternal().
    void testTypeIdFromExternalVpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new ExternalIdWrapper0431()), Map.class);
        Map<?, ?> bean = assertInstanceOf(Map.class, result.get("bean"));
        assertEquals(2, bean.get("a"));
        assertEquals("SomeType", result.get("type"));
    }

    // Provenance: TestVisibleTypeId#testIssue263().
    void testIssue263Vpack() throws Exception {
        Map<?, ?> serialized = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new I263Impl0431()), Map.class);
        assertEquals("bob", serialized.get("name"));
        assertEquals(41, serialized.get("age"));
        assertFalse(serialized.containsKey("@type"));

        I263Base0431 result = MAPPER.readValue(ISSUE_263, I263Base0431.class);
        I263Impl0431 implementation = assertInstanceOf(I263Impl0431.class, result);
        assertEquals(19, implementation.age);
    }

    // Provenance: TestVisibleTypeId#testVisibleTypeId408().
    void testVisibleTypeId408Vpack() throws Exception {
        Map<?, ?> serialized = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new ExternalBeanWithId0431(3)), Map.class);
        assertEquals("vbean", serialized.get("type"));
        assertEquals(3, ((Map<?, ?>) serialized.get("bean")).get("value"));

        ExternalBeanWithId0431 result = MAPPER.readValue(VISIBLE_EXTERNAL_408,
                ExternalBeanWithId0431.class);
        assertNotNull(result.bean);
        assertEquals(3, result.bean.value);
        assertEquals("vbean", result.type);
    }

    // Provenance: TestVisibleTypeId#testInvalidMultipleTypeIds().
    void testInvalidMultipleTypeIdsVpack() {
        InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new MultipleIds0431()));
        assertTrue(error.getMessage().toLowerCase(Locale.ROOT).contains("multiple type ids"));
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

    void __invoke_testVisibleWithPropertyVpack() throws Exception {
        try {
            testVisibleWithPropertyVpack();
        } finally {
        }
    }


    void __invoke_testVisibleWithWrapperArrayVpack() throws Exception {
        try {
            testVisibleWithWrapperArrayVpack();
        } finally {
        }
    }


    void __invoke_testVisibleWithWrapperObjectVpack() throws Exception {
        try {
            testVisibleWithWrapperObjectVpack();
        } finally {
        }
    }


    void __invoke_testTypeIdFromPropertyVpack() throws Exception {
        try {
            testTypeIdFromPropertyVpack();
        } finally {
        }
    }


    void __invoke_testTypeIdFromArrayVpack() throws Exception {
        try {
            testTypeIdFromArrayVpack();
        } finally {
        }
    }


    void __invoke_testTypeIdFromObjectVpack() throws Exception {
        try {
            testTypeIdFromObjectVpack();
        } finally {
        }
    }


    void __invoke_testTypeIdFromExternalVpack() throws Exception {
        try {
            testTypeIdFromExternalVpack();
        } finally {
        }
    }


    void __invoke_testIssue263Vpack() throws Exception {
        try {
            testIssue263Vpack();
        } finally {
        }
    }


    void __invoke_testVisibleTypeId408Vpack() throws Exception {
        try {
            testVisibleTypeId408Vpack();
        } finally {
        }
    }


    void __invoke_testInvalidMultipleTypeIdsVpack() throws Exception {
        try {
            testInvalidMultipleTypeIdsVpack();
        } finally {
        }
    }

}
