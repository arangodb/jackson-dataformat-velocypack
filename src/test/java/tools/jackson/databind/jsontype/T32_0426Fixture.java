package tools.jackson.databind.jsontype;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0426Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] INTER_OBJECT = VPackWireFixtureTest.hex(
            "14 1b 44 74 79 70 65 44 6d 69 6e 65 44 62 6c 61 68 "
          + "13 09 41 61 41 62 41 63 03 02");
private static final byte[] INTER_STRING = VPackWireFixtureTest.hex(
            "47 61 2c 62 2c 63 2c 64");
private static final byte[] INTER_ARRAY = VPackWireFixtureTest.hex(
            "13 0b 41 61 41 62 41 63 41 64 04");
private static final byte[] INTER_ARRAY_SIZE_2 = VPackWireFixtureTest.hex(
            "13 07 41 61 41 62 02");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] UNKNOWN_PROPERTY = VPackWireFixtureTest.hex(
            "14 0e 48 77 68 61 74 65 76 65 72 28 0d 01");
private static final byte[] DEFAULT_A13 = VPackWireFixtureTest.hex(
            "14 07 41 61 28 0d 01");
private static final byte[] DEFAULT_A14_TYPE = VPackWireFixtureTest.hex(
            "14 14 41 61 28 0e 45 23 74 79 70 65 46 66 6f 6f 62 61 72 02");
private static final byte[] DEFAULT_TYPE_A15 = VPackWireFixtureTest.hex(
            "14 14 45 23 74 79 70 65 46 66 6f 6f 62 61 72 41 61 28 0f 02");
private static final byte[] DEFAULT_TYPE_ONLY = VPackWireFixtureTest.hex(
            "14 10 45 23 74 79 70 65 46 66 6f 6f 62 61 72 01");
private static final byte[] WRAPPED_DEFAULT = VPackWireFixtureTest.hex(
            "14 10 46 66 6f 6f 62 61 72 14 06 41 61 33 01 01");
private static final byte[] INVALID_TYPE_ID_511 = VPackWireFixtureTest.hex(
            "14 2d 44 6d 61 6e 79 13 25 "
          + "14 11 44 73 75 62 31 14 09 41 61 43 66 6f 6f 01 01 "
          + "14 11 44 73 75 62 32 14 09 41 62 43 62 61 72 01 01 02 01");
private static final byte[] UNKNOWN_CLASS = VPackWireFixtureTest.hex(
            "14 25 45 76 61 6c 75 65 14 1c 45 63 6c 61 7a 7a "
          + "52 63 6f 6d 2e 66 6f 6f 62 61 72 2e 4e 6f 74 68 69 6e 67 01 01");
private static final byte[] INCOMPATIBLE_DEFAULT = VPackWireFixtureTest.hex(
            "14 30 48 74 79 70 65 49 6e 66 6f 47 64 65 72 69 76 65 64 "
          + "44 6e 61 6d 65 44 4a 6f 68 6e 4b 64 65 73 63 72 69 70 74 69 6f 6e "
          + "45 4f 77 6e 65 72 03");
private static final byte[] MODULE_VALUE = VPackWireFixtureTest.hex(
            "14 07 41 61 28 7b 01");
private static final byte[] MODULE_TYPE_ONLY = DEFAULT_TYPE_ONLY;

    // Provenance: TestPolymorphicWithDefaultImpl#testDeserializationWithObject().
    void testDeserializationWithObjectVpack() throws Exception {
        Inter426 inter = MAPPER.readerFor(Inter426.class).readValue(INTER_OBJECT);
        assertInstanceOf(MyInter426.class, inter);
        assertFalse(inter instanceof LegacyInter426);
        assertEquals(Arrays.asList("a", "b", "c"), ((MyInter426) inter).blah);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testDeserializationWithString().
    void testDeserializationWithStringVpack() throws Exception {
        Inter426 inter = MAPPER.readerFor(Inter426.class).readValue(INTER_STRING);
        assertInstanceOf(LegacyInter426.class, inter);
        assertEquals(Arrays.asList("a", "b", "c", "d"), ((MyInter426) inter).blah);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testDeserializationWithArray().
    void testDeserializationWithArrayVpack() throws Exception {
        Inter426 inter = MAPPER.readerFor(Inter426.class).readValue(INTER_ARRAY);
        assertInstanceOf(LegacyInter426.class, inter);
        assertEquals(Arrays.asList("a", "b", "c", "d"), ((MyInter426) inter).blah);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testDeserializationWithArrayOfSize2().
    void testDeserializationWithArrayOfSize2Vpack() throws Exception {
        Inter426 inter = MAPPER.readerFor(Inter426.class).readValue(INTER_ARRAY_SIZE_2);
        assertInstanceOf(LegacyInter426.class, inter);
        assertEquals(Arrays.asList("a", "b"), ((MyInter426) inter).blah);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testDefaultAsVoid().
    void testDefaultAsVoidVpack() throws Exception {
        Object ob = MAPPER.readerFor(DefaultWithVoidAsDefault426.class)
                .readValue(EMPTY_OBJECT);
        assertNull(ob);
        ob = MAPPER.readerFor(DefaultWithVoidAsDefault426.class).readValue(UNKNOWN_PROPERTY);
        assertNull(ob);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testBadTypeAsNull().
    void testBadTypeAsNullVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(MysteryPolymorphic426.class)
                .without(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        assertNull(reader.readValue(EMPTY_OBJECT));
        assertNull(reader.readValue(UNKNOWN_PROPERTY));
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testInvalidTypeId511().
    void testInvalidTypeId511Vpack() throws Exception {
        ObjectMapper reader = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .build();
        Good426 goodResult = reader.readValue(INVALID_TYPE_ID_511, Good426.class);
        assertNotNull(goodResult);
        Bad426 badResult = reader.readValue(INVALID_TYPE_ID_511, Bad426.class);
        assertNotNull(badResult);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testDefaultImplWithObjectWrapper().
    void testDefaultImplWithObjectWrapperVpack() throws Exception {
        BaseFor656_426 value = MAPPER.readValue(WRAPPED_DEFAULT, BaseFor656_426.class);
        assertNotNull(value);
        assertEquals(ImplFor656_426.class, value.getClass());
        assertEquals(3, ((ImplFor656_426) value).a);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testUnknownClassAsSubtype().
    void testUnknownClassAsSubtypeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
                .build();
        BaseWrapper426 wrapper = mapper.readValue(UNKNOWN_CLASS, BaseWrapper426.class);
        assertNotNull(wrapper);
        assertNull(wrapper.value);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testIncompatibleDefaultImpl1565().
    void testIncompatibleDefaultImpl1565Vpack() throws Exception {
        CDerived1565_426 result = MAPPER.readValue(INCOMPATIBLE_DEFAULT, CDerived1565_426.class);
        assertNotNull(result);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testDefaultImplFromAnnotation().
    void testDefaultImplFromAnnotationVpack() throws Exception {
        SuperTypeWithDefault426 bean = MAPPER.readValue(DEFAULT_A13,
                SuperTypeWithDefault426.class);
        assertEquals(DefaultImpl426.class, bean.getClass());
        assertEquals(13, ((DefaultImpl426) bean).a);

        bean = MAPPER.readValue(DEFAULT_A14_TYPE, SuperTypeWithDefault426.class);
        assertEquals(DefaultImpl426.class, bean.getClass());
        assertEquals(14, ((DefaultImpl426) bean).a);

        bean = MAPPER.readValue(DEFAULT_TYPE_A15, SuperTypeWithDefault426.class);
        assertEquals(DefaultImpl426.class, bean.getClass());
        assertEquals(15, ((DefaultImpl426) bean).a);

        bean = MAPPER.readValue(DEFAULT_TYPE_ONLY, SuperTypeWithDefault426.class);
        assertEquals(DefaultImpl426.class, bean.getClass());
        assertEquals(0, ((DefaultImpl426) bean).a);
    }

    // Provenance: TestPolymorphicWithDefaultImpl#testDefaultImplViaModule().
    void testDefaultImplViaModuleVpack() throws Exception {
        assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(MODULE_VALUE, SuperTypeWithoutDefault426.class));

        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addAbstractTypeMapping(SuperTypeWithoutDefault426.class, DefaultImpl505_426.class);
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        SuperTypeWithoutDefault426 bean = mapper.readValue(MODULE_VALUE,
                SuperTypeWithoutDefault426.class);
        assertNotNull(bean);
        assertEquals(DefaultImpl505_426.class, bean.getClass());
        assertEquals(123, ((DefaultImpl505_426) bean).a);

        bean = mapper.readValue(MODULE_TYPE_ONLY, SuperTypeWithoutDefault426.class);
        assertEquals(DefaultImpl505_426.class, bean.getClass());
        assertEquals(0, ((DefaultImpl505_426) bean).a);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = LegacyInter426.class)
    @JsonSubTypes(@JsonSubTypes.Type(name = "mine", value = MyInter426.class))
    interface Inter426 { }
static class MyInter426 implements Inter426 {
        @JsonProperty("blah") public List<String> blah;
    }
static class LegacyInter426 extends MyInter426 {
        @JsonCreator
        LegacyInter426(Object value) {
            if (value instanceof List<?> list) {
                blah = new java.util.ArrayList<>();
                for (Object item : list) {
                    blah.add(item.toString());
                }
            } else if (value instanceof String string) {
                blah = Arrays.asList(string.split(","));
            } else {
                throw new IllegalArgumentException("Unknown type: " + value.getClass());
            }
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
    static class DefaultWithVoidAsDefault426 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    abstract static class MysteryPolymorphic426 { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonSubTypes(@JsonSubTypes.Type(name = "sub1", value = BadSub1_426.class))
    static class BadItem426 { }
static class BadSub1_426 extends BadItem426 { public String a; }
static class Good426 { public List<GoodItem426> many; }
static class Bad426 { public List<BadItem426> many; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonSubTypes({ @JsonSubTypes.Type(name = "sub1", value = GoodSub1_426.class),
            @JsonSubTypes.Type(name = "sub2", value = GoodSub2_426.class) })
    static class GoodItem426 { }
static class GoodSub1_426 extends GoodItem426 { public String a; }
static class GoodSub2_426 extends GoodItem426 { public String b; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT,
            defaultImpl = ImplFor656_426.class)
    abstract static class BaseFor656_426 { }
static class ImplFor656_426 extends BaseFor656_426 { public int a; }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "clazz")
    abstract static class BaseClass426 { }
static class BaseWrapper426 { public BaseClass426 value; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "typeInfo", defaultImpl = CBaseClass1565_426.class)
    @JsonSubTypes(@JsonSubTypes.Type(CDerived1565_426.class))
    interface CTestInterface1565_426 {
        String getName();
        void setName(String name);
        String getTypeInfo();
    }
static class CBaseClass1565_426 implements CTestInterface1565_426 {
        private String name;
        @Override public String getName() { return name; }
        @Override public void setName(String value) { name = value; }
        @Override public String getTypeInfo() { return "base"; }
    }
@JsonTypeName("derived")
    static class CDerived1565_426 extends CBaseClass1565_426 {
        public String description;
        @Override public String getTypeInfo() { return "derived"; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "#type", defaultImpl = DefaultImpl426.class)
    abstract static class SuperTypeWithDefault426 { }
static class DefaultImpl426 extends SuperTypeWithDefault426 { public int a; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "#type")
    abstract static class SuperTypeWithoutDefault426 { }
static class DefaultImpl505_426 extends SuperTypeWithoutDefault426 { public int a; }

    void __invoke_testDeserializationWithObjectVpack() throws Exception {
        try {
            testDeserializationWithObjectVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithStringVpack() throws Exception {
        try {
            testDeserializationWithStringVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithArrayVpack() throws Exception {
        try {
            testDeserializationWithArrayVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithArrayOfSize2Vpack() throws Exception {
        try {
            testDeserializationWithArrayOfSize2Vpack();
        } finally {
        }
    }


    void __invoke_testDefaultAsVoidVpack() throws Exception {
        try {
            testDefaultAsVoidVpack();
        } finally {
        }
    }


    void __invoke_testBadTypeAsNullVpack() throws Exception {
        try {
            testBadTypeAsNullVpack();
        } finally {
        }
    }


    void __invoke_testInvalidTypeId511Vpack() throws Exception {
        try {
            testInvalidTypeId511Vpack();
        } finally {
        }
    }


    void __invoke_testDefaultImplWithObjectWrapperVpack() throws Exception {
        try {
            testDefaultImplWithObjectWrapperVpack();
        } finally {
        }
    }


    void __invoke_testUnknownClassAsSubtypeVpack() throws Exception {
        try {
            testUnknownClassAsSubtypeVpack();
        } finally {
        }
    }


    void __invoke_testIncompatibleDefaultImpl1565Vpack() throws Exception {
        try {
            testIncompatibleDefaultImpl1565Vpack();
        } finally {
        }
    }


    void __invoke_testDefaultImplFromAnnotationVpack() throws Exception {
        try {
            testDefaultImplFromAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testDefaultImplViaModuleVpack() throws Exception {
        try {
            testDefaultImplViaModuleVpack();
        } finally {
        }
    }

}
