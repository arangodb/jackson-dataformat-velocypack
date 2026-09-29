package tools.jackson.databind.jsontype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.jsontype.impl.SimpleNameIdResolver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0414F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] INNER = VPackWireFixtureTest.hex(
            "0b 18 01 45 40 74 79 70 65 4d 49 6e 6e 65 72 53 75 62 34 30 36 31 41 03");
private static final byte[] MINIMAL_INNER = VPackWireFixtureTest.hex(
            "0b 28 01 42 40 63 60 2e 54 33 32 5f 30 34 31 34" +
                "46 30 24 4d 69 6e 69 6d 61 6c 49 6e 6e 65 72 53" +
                "75 62 34 30 36 31 41 03");
private static final byte[] BASIC = VPackWireFixtureTest.hex(
            "0b 18 01 45 40 74 79 70 65 4d 42 61 73 69 63 53 75 62 34 30 36 31 41 03");
private static final byte[] MIXED = VPackWireFixtureTest.hex(
            "0b 18 01 45 40 74 79 70 65 4d 4d 69 78 65 64 53 75 62 34 30 36 31 41 03");
private static final byte[] MIXED_MINIMAL = VPackWireFixtureTest.hex(
            "0b 28 01 42 40 63 60 2e 54 33 32 5f 30 34 31 34" +
                "46 30 24 4d 69 78 65 64 4d 69 6e 69 6d 61 6c 53" +
                "75 62 34 30 36 31 41 03");
private static final byte[] MERGE_CHILD = VPackWireFixtureTest.hex(
            "14 2f 45 63 68 69 6c 64 14 26 45 40 74 79 70 65 4b 4d 65 72 67 65 43 68 69 6c 64 41 44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] MERGE_CHILD_CASE_INSENSITIVE = VPackWireFixtureTest.hex(
            "14 2f 45 63 68 69 6c 64 14 26 45 40 74 79 70 65 4b 6d 65 72 67 65 63 68 69 6c 64 61 44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] MERGE_CHILD_UNKNOWN = VPackWireFixtureTest.hex(
            "14 31 45 63 68 69 6c 64 14 28 45 40 74 79 70 65 4d 55 6e 6b 6e 6f 77 6e 43 68 69 6c 64 41 44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] ALIAS_POLYMORPHIC = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 13 14 42 61 62 14 0e 42 6e 6d 43 42 6f 62 41 41 28 11 02 02 01");
private static final byte[] NO_TYPE_INFO = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 14 07 41 76 28 2a 01 01" );

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testInnerClass().
    void testInnerClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(new InnerSub4061A()), Map.class);
        assertEquals(Map.of("@type", "InnerSub4061A"), encoded);

        InnerSuper4061 bean = MAPPER.readValue(INNER, InnerSuper4061.class);
        assertInstanceOf(InnerSub4061A.class, bean);
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testMinimalInnerClass().
    void testMinimalInnerClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(new MinimalInnerSub4061A()), Map.class);
        assertEquals(".T32_0414F0$MinimalInnerSub4061A", encoded.get("@c"));

        MinimalInnerSuper4061 bean = MAPPER.readValue(MINIMAL_INNER, MinimalInnerSuper4061.class);
        assertInstanceOf(MinimalInnerSub4061A.class, bean);
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testBasicClass().
    void testBasicClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(new BasicSub4061A()), Map.class);
        assertEquals(Map.of("@type", "BasicSub4061A"), encoded);

        BasicSuper4061 bean = MAPPER.readValue(BASIC, BasicSuper4061.class);
        assertInstanceOf(BasicSub4061A.class, bean);
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testMixedClass().
    void testMixedClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(new MixedSub4061A()), Map.class);
        assertEquals(Map.of("@type", "MixedSub4061A"), encoded);

        MixedSuper4061 bean = MAPPER.readValue(MIXED, MixedSuper4061.class);
        assertInstanceOf(MixedSub4061A.class, bean);
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testMixedMinimalClass().
    void testMixedMinimalClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(new MixedMinimalSub4061A()), Map.class);
        assertEquals(".T32_0414F0$MixedMinimalSub4061A", encoded.get("@c"));

        MixedMinimalSuper4061 bean = MAPPER.readValue(MIXED_MINIMAL, MixedMinimalSuper4061.class);
        assertInstanceOf(MixedMinimalSub4061A.class, bean);
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testPolymorphicNewObject().
    void testPolymorphicNewObjectVpack() throws Exception {
        Root4061 root = MAPPER.readValue(MERGE_CHILD, Root4061.class);
        assertInstanceOf(MergeChildA4061.class, root.child);
        assertEquals("I'm child A", ((MergeChildA4061) root.child).name);
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testPolymorphicNewObjectCaseInsensitive().
    void testPolymorphicNewObjectCaseInsensitiveVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES).build();
        Root4061 root = mapper.readValue(MERGE_CHILD_CASE_INSENSITIVE, Root4061.class);
        assertInstanceOf(MergeChildA4061.class, root.child);
        assertEquals("I'm child A", ((MergeChildA4061) root.child).name);
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testPolymorphicNewObjectUnknownTypeId().
    void testPolymorphicNewObjectUnknownTypeIdVpack() {
        InvalidTypeIdException failure = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(MERGE_CHILD_UNKNOWN, Root4061.class));
        assertTrue(failure.getMessage().contains("UnknownChildA"));
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testAliasWithPolymorphic().
    void testAliasWithPolymorphicVpack() throws Exception {
        PolyWrapperForAlias4061 value = MAPPER.readValue(ALIAS_POLYMORPHIC,
                PolyWrapperForAlias4061.class);

        assertNotNull(value.value);
        AliasBean4061 bean = (AliasBean4061) value.value;
        assertEquals("Bob", bean.name);
        assertEquals(17, bean._a);
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testGetMechanism().
    void testGetMechanismVpack() {
        JavaType javaType = MAPPER.deserializationConfig().constructType(InnerSub4061B.class);
        List<NamedType> namedTypes = new ArrayList<>();
        namedTypes.add(new NamedType(InnerSub4061A.class));
        namedTypes.add(new NamedType(InnerSub4061B.class));

        SimpleNameIdResolver idResolver = SimpleNameIdResolver.construct(
                MAPPER.deserializationConfig(), javaType, namedTypes, false, true);

        assertEquals(JsonTypeInfo.Id.SIMPLE_NAME, idResolver.getMechanism());
    }

    // Provenance: JsonTypeInfoSimpleClassName4061Test#testDuplicateNameLastOneWins().
    void testDuplicateNameLastOneWinsVpack() throws Exception {
        DuplicateSuper4061 bean = MAPPER.readValue(
                VPackWireFixtureTest.hex("0b 1c 01 45 40 74 79 70 65 51 44 75 70 6c 69 63 61 74 65 53 75 62 43 6c 61 73 73 03"),
                DuplicateSuper4061.class);
        assertInstanceOf(DuplicateSubClassB4061.class, bean);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = InnerSub4061A.class),
            @JsonSubTypes.Type(value = InnerSub4061B.class)
    })
    static class InnerSuper4061 { }
static class InnerSub4061A extends InnerSuper4061 { }
static class InnerSub4061B extends InnerSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MinimalInnerSub4061A.class),
            @JsonSubTypes.Type(value = MinimalInnerSub4061B.class)
    })
    static class MinimalInnerSuper4061 { }
static class MinimalInnerSub4061A extends MinimalInnerSuper4061 { }
static class MinimalInnerSub4061B extends MinimalInnerSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MixedSub4061A.class),
            @JsonSubTypes.Type(value = MixedSub4061B.class)
    })
    static class MixedSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MixedMinimalSub4061A.class),
            @JsonSubTypes.Type(value = MixedMinimalSub4061B.class)
    })
    static class MixedMinimalSuper4061 { }
static class Root4061 {
        public MergeChild4061 child;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MergeChildA4061.class, name = "MergeChildA"),
            @JsonSubTypes.Type(value = MergeChildB4061.class, name = "MergeChildB")
    })
    static abstract class MergeChild4061 { }
static class MergeChildA4061 extends MergeChild4061 {
        public String name;
    }
static class MergeChildB4061 extends MergeChild4061 {
        public String code;
    }
static class PolyWrapperForAlias4061 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        @JsonSubTypes(@JsonSubTypes.Type(value = AliasBean4061.class, name = "ab"))
        public Object value;
    }
static class AliasBean4061 {
        @JsonAlias({ "nm", "Name" })
        public String name;
        int _a;

        @JsonCreator
        public AliasBean4061(@JsonProperty("a") @JsonAlias("A") int a) {
            _a = a;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DuplicateSubClassA4061.class, name = "DuplicateSubClass"),
            @JsonSubTypes.Type(value = DuplicateSubClassB4061.class, name = "DuplicateSubClass")
    })
    static class DuplicateSuper4061 { }
static class DuplicateSubClassA4061 extends DuplicateSuper4061 { }
static class DuplicateSubClassB4061 extends DuplicateSuper4061 { }
static class Value1654UsingCustomSerDeserUntyped4061 {
        @JsonDeserialize(using = Value1654Deserializer4061.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public Value1654_4061 value;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    static class Value1654_4061 {
        public int x;

        protected Value1654_4061() { }

        Value1654_4061(int x) {
            this.x = x;
        }
    }
static class Value1654Deserializer4061 extends ValueDeserializer<Value1654_4061> {
        @Override
        public Value1654_4061 deserialize(JsonParser p, DeserializationContext ctxt)
                throws JacksonException {
            JsonNode node = ctxt.readTree(p);
            if (!node.has("v")) {
                ctxt.reportInputMismatch(Value1654_4061.class,
                        "Bad VPack input (no 'v'): " + node);
            }
            return new Value1654_4061(node.path("v").intValue());
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BasicSub4061A.class),
        @JsonSubTypes.Type(value = BasicSub4061B.class)
})
static class BasicSuper4061 {
}
static class BasicSub4061A extends BasicSuper4061 { }
static class BasicSub4061B extends BasicSuper4061 { }
static class MixedSub4061A extends T32_0414F0.MixedSuper4061 { }
static class MixedSub4061B extends T32_0414F0.MixedSuper4061 { }
static class MixedMinimalSub4061A extends T32_0414F0.MixedMinimalSuper4061 { }
static class MixedMinimalSub4061B extends T32_0414F0.MixedMinimalSuper4061 { }

    void __invoke_testInnerClassVpack() throws Exception {
        try {
            testInnerClassVpack();
        } finally {
        }
    }


    void __invoke_testMinimalInnerClassVpack() throws Exception {
        try {
            testMinimalInnerClassVpack();
        } finally {
        }
    }


    void __invoke_testBasicClassVpack() throws Exception {
        try {
            testBasicClassVpack();
        } finally {
        }
    }


    void __invoke_testMixedClassVpack() throws Exception {
        try {
            testMixedClassVpack();
        } finally {
        }
    }


    void __invoke_testMixedMinimalClassVpack() throws Exception {
        try {
            testMixedMinimalClassVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicNewObjectVpack() throws Exception {
        try {
            testPolymorphicNewObjectVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicNewObjectCaseInsensitiveVpack() throws Exception {
        try {
            testPolymorphicNewObjectCaseInsensitiveVpack();
        } finally {
        }
    }


    void __invoke_testPolymorphicNewObjectUnknownTypeIdVpack() throws Exception {
        try {
            testPolymorphicNewObjectUnknownTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testAliasWithPolymorphicVpack() throws Exception {
        try {
            testAliasWithPolymorphicVpack();
        } finally {
        }
    }


    void __invoke_testGetMechanismVpack() throws Exception {
        try {
            testGetMechanismVpack();
        } finally {
        }
    }


    void __invoke_testDuplicateNameLastOneWinsVpack() throws Exception {
        try {
            testDuplicateNameLastOneWinsVpack();
        } finally {
        }
    }

}
