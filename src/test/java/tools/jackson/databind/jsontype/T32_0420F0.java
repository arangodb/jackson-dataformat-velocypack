package tools.jackson.databind.jsontype;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.NamedType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0420F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] PROPERTY_SUB_C = VPackWireFixtureTest.hex(
            "0b 28 01 45 76 61 6c 75 65 0b 1e 02 45 40 74 79" +
                "70 65 4f 54 33 32 5f 30 34 32 30 46 30 24 53 75" +
                "62 43 41 63 31 03 19 03");
private static final byte[] NAMED_TYPE_B = VPackWireFixtureTest.hex(
            "0b 15 02 45 40 74 79 70 65 45 54 79 70 65 42 41 62 28 0d 03 0f");
private static final byte[] NAMED_TYPE_D = VPackWireFixtureTest.hex(
            "0b 14 02 45 40 74 79 70 65 45 54 79 70 65 44 41 64 3c 03 0f");
private static final byte[] NON_NAMED_SUB_C = VPackWireFixtureTest.hex(
            "0b 1e 02 45 40 74 79 70 65 4f 54 33 32 5f 30 34" +
                "32 30 46 30 24 53 75 62 43 41 63 31 03 19");
private static final byte[] ERROR_UNKNOWN_TYPE = VPackWireFixtureTest.hex(
            "0b 0b 01 44 74 79 70 65 41 7a 03");
private static final byte[] ATOMIC_IMPL_X = VPackWireFixtureTest.hex(
            "0b 19 01 45 76 61 6c 75 65 0b 0f 02 44 74 79 70 65 41 78 41 78 33 03 0a 03");
private static final byte[] ISSUE_1125_IMPL = VPackWireFixtureTest.hex(
            "0b 34 01 45 76 61 6c 75 65 0b 2a 04 45 40 74 79" +
                "70 65 53 54 33 32 5f 30 34 32 30 46 30 24 49 6d" +
                "70 6c 31 31 32 35 41 61 31 41 62 32 41 63 33 03" +
                "1d 20 23 03");
private static final byte[] ISSUE_1125_DEFAULT = VPackWireFixtureTest.hex(
            "0b 1b 01 45 76 61 6c 75 65 0b 11 03 41 61 33 41 62 35 43 64 65 66 39 03 06 09 03");
private static final byte[] TYPED_DUMMY_IMPL = VPackWireFixtureTest.hex(
            "06 41 02 74 74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f" +
                "6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f 6e 74" +
                "79 70 65 2e 54 33 32 5f 30 34 32 30 46 30 24 44" +
                "75 6d 6d 79 49 6d 70 6c 0b 07 01 41 78 33 03 03" +
                "38");
private static final byte[] DISALLOWED_CLASS = VPackWireFixtureTest.hex(
            "0b 4a 01 45 76 61 6c 75 65 06 40 02 72 74 6f 6f" +
                "6c 73 2e 6a 61 63 6b 73 6f 6e 2e 64 61 74 61 62" +
                "69 6e 64 2e 6a 73 6f 6e 74 79 70 65 2e 54 33 32" +
                "5f 30 34 32 30 46 30 24 54 68 65 42 6f 6d 62 0b" +
                "08 01 41 61 28 0d 03 03 36 03");

    // Provenance: SealedTypesWithSubtypesTest#testPropertyWithSubtypes().
    void testPropertyWithSubtypesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        PropertyBean result = mapper.readValue(PROPERTY_SUB_C, PropertyBean.class);
        assertInstanceOf(SubC.class, result.value);
    }

    // Provenance: SealedTypesWithSubtypesTest#testSubtypesViaModule().
    void testSubtypesViaModuleVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addModule(new SimpleModule()).build();
        PropertyBean result = mapper.readValue(PROPERTY_SUB_C, PropertyBean.class);
        assertInstanceOf(SubC.class, result.value);
    }

    // Provenance: SealedTypesWithSubtypesTest#testSerialization().
    void testSerializationVpack() throws Exception {
        Map<?, ?> named = MAPPER.readValue(MAPPER.writeValueAsBytes(new SubB()), Map.class);
        assertEquals("TypeB", named.get("@type"));
        assertEquals(1, named.get("b"));

        ObjectMapper mapper = VPackMapper.builder()
                .registerSubtypes(new NamedType(SubB.class, "typeB")).build();
        Map<?, ?> overridden = mapper.readValue(mapper.writeValueAsBytes(new SubB()), Map.class);
        assertEquals("typeB", overridden.get("@type"));
        assertEquals("T32_0420F0$SubD",
                MAPPER.readValue(MAPPER.writeValueAsBytes(new SubD()), Map.class).get("@type"));
    }

    // Provenance: SealedTypesWithSubtypesTest#testDeserializationNonNamed().
    void testDeserializationNonNamedVpack() throws Exception {
        SuperType bean = MAPPER.readValue(NON_NAMED_SUB_C, SuperType.class);
        assertEquals(SubC.class, bean.getClass());
        assertEquals(1, ((SubC) bean).c);
    }

    // Provenance: SealedTypesWithSubtypesTest#testDeserializatioNamed().
    void testDeserializatioNamedVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .registerSubtypes(new NamedType(SubD.class, "TypeD")).build();
        SuperType b = mapper.readValue(NAMED_TYPE_B, SuperType.class);
        assertEquals(SubB.class, b.getClass());
        assertEquals(13, ((SubB) b).b);
        SuperType d = mapper.readValue(NAMED_TYPE_D, SuperType.class);
        assertEquals(SubD.class, d.getClass());
        assertEquals(-4, ((SubD) d).d);
    }

    // Provenance: SealedTypesWithSubtypesTest#testEmptyBean().
    void testEmptyBeanVpack() throws Exception {
        ObjectMapper enabled = VPackMapper.builder()
                .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS).build();
        Map<?, ?> first = enabled.readValue(enabled.writeValueAsBytes(new EmptyBean()), Map.class);
        assertEquals(EmptyBean.class.getName().substring(EmptyBean.class.getName().lastIndexOf('.') + 1),
                first.get("@type"));

        ObjectMapper defaults = VPackMapper.builder()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("").build(), DefaultTyping.NON_FINAL)
                .build();
        Object decoded = defaults.readValue(defaults.writeValueAsBytes(new EmptyNonFinal()), Object.class);
        assertInstanceOf(EmptyNonFinal.class, decoded);
    }

    // Provenance: SealedTypesWithSubtypesTest#testErrorMessage().
    void testErrorMessageVpack() {
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(ERROR_UNKNOWN_TYPE, BaseX.class));
        assertTrue(e.getMessage().contains("Could not resolve type id 'z' as a subtype of"));
        assertTrue(e.getMessage().contains("known type ids = [x, y]"));
    }

    // Provenance: SealedTypesWithSubtypesTest#testViaAtomic().
    void testViaAtomicVpack() throws Exception {
        AtomicWrapper output = MAPPER.readValue(ATOMIC_IMPL_X, AtomicWrapper.class);
        assertNotNull(output);
        assertEquals(ImplX.class, output.value.getClass());
        assertEquals(3, ((ImplX) output.value).x);
    }

    // Provenance: SealedTypesWithSubtypesTest#testSubclassLimits().
    void testSubclassLimitsVpack() {
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(DISALLOWED_CLASS, DateWrapper.class));
        assertTrue(e.getMessage().contains("Not a subtype"));
        assertTrue(e.getMessage().contains(TheBomb.class.getName()));
    }

    // Provenance: SealedTypesWithSubtypesTest#testIssue1125NonDefault().
    void testIssue1125NonDefaultVpack() throws Exception {
        Issue1125Wrapper result = MAPPER.readValue(ISSUE_1125_IMPL, Issue1125Wrapper.class);
        Impl1125 impl = assertInstanceOf(Impl1125.class, result.value);
        assertEquals(1, impl.a);
        assertEquals(2, impl.b);
        assertEquals(3, impl.c);
    }

    // Provenance: SealedTypesWithSubtypesTest#testIssue1125WithDefault().
    void testIssue1125WithDefaultVpack() throws Exception {
        Issue1125Wrapper result = MAPPER.readValue(ISSUE_1125_DEFAULT, Issue1125Wrapper.class);
        Default1125 impl = assertInstanceOf(Default1125.class, result.value);
        assertEquals(3, impl.a);
        assertEquals(5, impl.b);
        assertEquals(9, impl.def);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    static abstract sealed class SuperType permits SubB, SubC, SubD { }
@JsonTypeName("TypeB")
    static final class SubB extends SuperType { public int b = 1; }
static final class SubC extends SuperType { public int c = 2; }
static final class SubD extends SuperType { public int d; }
static class PropertyBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
        public SuperType value;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    static abstract sealed class BaseX permits ImplX, ImplY { }
@JsonTypeName("x") static final class ImplX extends BaseX { public int x; }
@JsonTypeName("y") static final class ImplY extends BaseX { public int y; }
static class AtomicWrapper { public BaseX value; }
static class EmptyNonFinal { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    static abstract sealed class BaseBean permits EmptyBean { }
static final class EmptyBean extends BaseBean { }
static class DateWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public java.util.Date value;
    }
static class TheBomb { public int a; public TheBomb() { throw new Error("Ka-boom!"); } }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = Default1125.class)
    static sealed class Base1125 permits Interm1125 { public int a; }
static sealed class Interm1125 extends Base1125 permits Impl1125, Default1125 { public int b; }
static final class Impl1125 extends Interm1125 { public int c; }
static final class Default1125 extends Interm1125 { public int def; }
static class Issue1125Wrapper { public Base1125 value; }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@classy")
    static abstract sealed class DummyBase permits DummyImpl { protected DummyBase(boolean ignored) { } }
static final class DummyImpl extends DummyBase { public int x; public DummyImpl() { super(true); } }

    void __invoke_testPropertyWithSubtypesVpack() throws Exception {
        try {
            testPropertyWithSubtypesVpack();
        } finally {
        }
    }


    void __invoke_testSubtypesViaModuleVpack() throws Exception {
        try {
            testSubtypesViaModuleVpack();
        } finally {
        }
    }


    void __invoke_testSerializationVpack() throws Exception {
        try {
            testSerializationVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationNonNamedVpack() throws Exception {
        try {
            testDeserializationNonNamedVpack();
        } finally {
        }
    }


    void __invoke_testDeserializatioNamedVpack() throws Exception {
        try {
            testDeserializatioNamedVpack();
        } finally {
        }
    }


    void __invoke_testEmptyBeanVpack() throws Exception {
        try {
            testEmptyBeanVpack();
        } finally {
        }
    }


    void __invoke_testErrorMessageVpack() throws Exception {
        try {
            testErrorMessageVpack();
        } finally {
        }
    }


    void __invoke_testViaAtomicVpack() throws Exception {
        try {
            testViaAtomicVpack();
        } finally {
        }
    }


    void __invoke_testSubclassLimitsVpack() throws Exception {
        try {
            testSubclassLimitsVpack();
        } finally {
        }
    }


    void __invoke_testIssue1125NonDefaultVpack() throws Exception {
        try {
            testIssue1125NonDefaultVpack();
        } finally {
        }
    }


    void __invoke_testIssue1125WithDefaultVpack() throws Exception {
        try {
            testIssue1125WithDefaultVpack();
        } finally {
        }
    }

}
