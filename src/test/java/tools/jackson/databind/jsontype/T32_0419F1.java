package tools.jackson.databind.jsontype;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.jsontype.impl.SimpleNameIdResolver;
import tools.jackson.databind.exc.InvalidTypeIdException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0419F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] FRUIT_APPLE = VPackWireFixtureTest.hex(
            "14 2b 44 6e 61 6d 65 4b 41 70 70 6c 65 2d 41 2d 44 61 79 "
          + "49 73 65 65 64 43 6f 75 6e 74 28 10 44 74 79 70 65 45 61 70 70 6c 65 03");
private static final byte[] FRUIT_WRAPPER = VPackWireFixtureTest.hex(
            "14 34 45 66 72 75 69 74 14 2b 44 6e 61 6d 65 4b 41 70 70 6c 65 2d 41 2d 44 61 79 "
          + "49 73 65 65 64 43 6f 75 6e 74 28 10 44 74 79 70 65 45 61 70 70 6c 65 03 01");
private static final byte[] FRUIT_LIST = VPackWireFixtureTest.hex(
            "13 5f 14 2b 44 6e 61 6d 65 4b 41 70 70 6c 65 2d 41 2d 44 61 79 "
          + "49 73 65 65 64 43 6f 75 6e 74 28 10 44 74 79 70 65 45 61 70 70 6c 65 03 "
          + "14 31 44 6e 61 6d 65 4f 4d 61 6e 64 61 72 69 6e 20 4f 72 61 6e 67 65 "
          + "45 63 6f 6c 6f 72 46 6f 72 61 6e 67 65 44 74 79 70 65 46 6f 72 61 6e 67 65 03 02");
private static final byte[] INNER_A = VPackWireFixtureTest.hex(
            "14 17 45 40 74 79 70 65 4d 49 6e 6e 65 72 53 75 62 34 30 36 31 41 01");
private static final byte[] MINIMAL_INNER_A = VPackWireFixtureTest.hex(
            "0b 28 01 42 40 63 60 2e 54 33 32 5f 30 34 31 39" +
                "46 31 24 4d 69 6e 69 6d 61 6c 49 6e 6e 65 72 53" +
                "75 62 34 30 36 31 41 03");
private static final byte[] BASIC_A = VPackWireFixtureTest.hex(
            "14 17 45 40 74 79 70 65 4d 42 61 73 69 63 53 75 62 34 30 36 31 41 01");
private static final byte[] MIXED_A = VPackWireFixtureTest.hex(
            "14 27 45 40 74 79 70 65 5d 4d 69 78 65 64 53 75 62 34 30 36 31 41 46 6f 72 53 65 61 6c 65 64 43 6c 61 73 73 65 73 01");
private static final byte[] MIXED_MINIMAL_A = VPackWireFixtureTest.hex(
            "0b 38 01 42 40 63 70 2e 54 33 32 5f 30 34 31 39" +
                "46 31 24 4d 69 78 65 64 4d 69 6e 69 6d 61 6c 53" +
                "75 62 34 30 36 31 41 46 6f 72 53 65 61 6c 65 64" +
                "43 6c 61 73 73 65 73 03");
private static final byte[] NEW_OBJECT = VPackWireFixtureTest.hex(
            "14 2f 45 63 68 69 6c 64 14 26 45 40 74 79 70 65 4b 4d 65 72 67 65 43 68 69 6c 64 41 "
          + "44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] NEW_OBJECT_CASE_INSENSITIVE = VPackWireFixtureTest.hex(
            "14 2f 45 63 68 69 6c 64 14 26 45 40 74 79 70 65 4b 6d 65 72 67 65 63 68 69 6c 64 61 "
          + "44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] UNKNOWN_OBJECT = VPackWireFixtureTest.hex(
            "14 31 45 63 68 69 6c 64 14 28 45 40 74 79 70 65 4d 55 6e 6b 6e 6f 77 6e 43 68 69 6c 64 41 "
          + "44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] ALIAS = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 13 14 42 61 62 14 0e 42 6e 6d 43 42 6f 62 41 41 28 11 02 02 01");
private static final byte[] DUPLICATE = VPackWireFixtureTest.hex(
            "14 2b 45 40 74 79 70 65 61 44 75 70 6c 69 63 61 74 65 53 75 62 43 6c 61 73 73 46 6f 72 53 65 61 6c 65 64 43 6c 61 73 73 65 73 01");

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testInnerClass().
    void testInnerClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(new InnerSub4061A()), Map.class);
        assertEquals(Map.of("@type", "InnerSub4061A"), encoded);
        InnerSuper4061 bean = MAPPER.readValue(INNER_A, InnerSuper4061.class);
        assertInstanceOf(InnerSub4061A.class, bean);
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testMinimalInnerClass().
    void testMinimalInnerClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new MinimalInnerSub4061A()), Map.class);
        assertEquals(".T32_0419F1$MinimalInnerSub4061A", encoded.get("@c"));
        MinimalInnerSuper4061 bean = MAPPER.readValue(MINIMAL_INNER_A, MinimalInnerSuper4061.class);
        assertInstanceOf(MinimalInnerSub4061A.class, bean);
        assertNotNull(bean);
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testBasicClass().
    void testBasicClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(new BasicSub4061A()), Map.class);
        assertEquals(Map.of("@type", "BasicSub4061A"), encoded);
        BasicSuper4061 bean = MAPPER.readValue(BASIC_A, BasicSuper4061.class);
        assertInstanceOf(BasicSub4061A.class, bean);
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testMixedClass().
    void testMixedClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new MixedSub4061AForSealedClasses()), Map.class);
        assertEquals(Map.of("@type", "MixedSub4061AForSealedClasses"), encoded);
        MixedSuper4061 bean = MAPPER.readValue(MIXED_A, MixedSuper4061.class);
        assertInstanceOf(MixedSub4061AForSealedClasses.class, bean);
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testMixedMinimalClass().
    void testMixedMinimalClassVpack() throws Exception {
        Map<?, ?> encoded = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new MixedMinimalSub4061AForSealedClasses()), Map.class);
        assertEquals(".T32_0419F1$MixedMinimalSub4061AForSealedClasses", encoded.get("@c"));
        MixedMinimalSuper4061 bean = MAPPER.readValue(MIXED_MINIMAL_A, MixedMinimalSuper4061.class);
        assertInstanceOf(MixedMinimalSub4061AForSealedClasses.class, bean);
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testPolymorphicNewObject().
    void testPolymorphicNewObjectVpack() throws Exception {
        Root root = MAPPER.readValue(NEW_OBJECT, Root.class);
        assertInstanceOf(MergeChildA.class, root.child);
        assertEquals("I'm child A", ((MergeChildA) root.child).name);
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testPolymorphicNewObjectCaseInsensitive().
    void testPolymorphicNewObjectCaseInsensitiveVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES).build();
        Root root = mapper.readValue(NEW_OBJECT_CASE_INSENSITIVE, Root.class);
        assertInstanceOf(MergeChildA.class, root.child);
        assertEquals("I'm child A", ((MergeChildA) root.child).name);
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testPolymorphicNewObjectUnknownTypeId().
    void testPolymorphicNewObjectUnknownTypeIdVpack() {
        assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(UNKNOWN_OBJECT, Root.class));
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testAliasWithPolymorphic().
    void testAliasWithPolymorphicVpack() throws Exception {
        PolyWrapperForAlias value = MAPPER.readValue(ALIAS, PolyWrapperForAlias.class);
        assertNotNull(value.value);
        AliasBean bean = (AliasBean) value.value;
        assertEquals("Bob", bean.name);
        assertEquals(17, bean._a);
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testGetMechanism().
    void testGetMechanismVpack() {
        DeserializationConfig config = MAPPER.deserializationConfig();
        JavaType javaType = config.constructType(InnerSub4061B.class);
        List<NamedType> namedTypes = List.of(new NamedType(InnerSub4061A.class),
                new NamedType(InnerSub4061B.class));
        SimpleNameIdResolver resolver = SimpleNameIdResolver.construct(
                config, javaType, namedTypes, false, true);
        assertEquals(JsonTypeInfo.Id.SIMPLE_NAME, resolver.getMechanism());
    }

    // Provenance: SealedTypesWithJsonTypeInfoSimpleClassName4061Test#testDuplicateNameLastOneWins().
    void testDuplicateNameLastOneWinsVpack() throws Exception {
        VPackSealedTypes.DuplicateSuperClass bean = MAPPER.readValue(DUPLICATE, VPackSealedTypes.DuplicateSuperClass.class);
        assertInstanceOf(tools.jackson.dataformat.velocypack.RelocatedDuplicateSubClassForSealedClasses.class, bean);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type", visible = true)
    static abstract sealed class Fruit permits Apple, Orange {
        public String name;
        Fruit() { }
        Fruit(String name) { this.name = name; }
    }
@JsonTypeName("apple")
    static final class Apple extends Fruit {
        public int seedCount;
        public String type;
        Apple() { }
        Apple(String name, int count) { super(name); seedCount = count; type = "apple"; }
    }
@JsonTypeName("orange")
    static final class Orange extends Fruit {
        public String color;
        public String type;
        Orange() { }
        Orange(String name, String color) { super(name); this.color = color; type = "orange"; }
    }
static class FruitWrapper {
        public Fruit fruit;
        FruitWrapper() { }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    static sealed class InnerSuper4061 permits InnerSub4061A, InnerSub4061B { }
static final class InnerSub4061A extends InnerSuper4061 { }
static final class InnerSub4061B extends InnerSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    static sealed class MinimalInnerSuper4061
            permits MinimalInnerSub4061A, MinimalInnerSub4061B { }
static final class MinimalInnerSub4061A extends MinimalInnerSuper4061 { }
static final class MinimalInnerSub4061B extends MinimalInnerSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    static sealed class BasicSuper4061 permits BasicSub4061A, BasicSub4061B { }
static final class BasicSub4061A extends BasicSuper4061 { }
static final class BasicSub4061B extends BasicSuper4061 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    static sealed class MixedSuper4061
            permits MixedSub4061AForSealedClasses, MixedSub4061BForSealedClasses { }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    static sealed class MixedMinimalSuper4061
            permits MixedMinimalSub4061AForSealedClasses, MixedMinimalSub4061BForSealedClasses { }
static class Root { @JsonProperty("child") @JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME) public MergeChild child; }
@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    static abstract sealed class MergeChild permits MergeChildA, MergeChildB { }
static final class MergeChildA extends MergeChild { public String name; }
static final class MergeChildB extends MergeChild { public String code; }
static class PolyWrapperForAlias {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        @com.fasterxml.jackson.annotation.JsonSubTypes(@com.fasterxml.jackson.annotation.JsonSubTypes.Type(
                value = AliasBean.class, name = "ab"))
        public Object value;
    }
static class AliasBean {
        @JsonAlias({"nm", "Name"}) public String name;
        int _a;
        @JsonCreator AliasBean(@JsonProperty("a") @JsonAlias("A") int a) { _a = a; }
    }
final static class MixedSub4061AForSealedClasses
        extends T32_0419F1.MixedSuper4061 { }
final static class MixedSub4061BForSealedClasses
        extends T32_0419F1.MixedSuper4061 { }
final static class MixedMinimalSub4061AForSealedClasses
        extends T32_0419F1.MixedMinimalSuper4061 { }
final static class MixedMinimalSub4061BForSealedClasses
        extends T32_0419F1.MixedMinimalSuper4061 { }

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
