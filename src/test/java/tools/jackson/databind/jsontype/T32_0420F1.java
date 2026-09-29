package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0420F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] PROPERTY_SUB_C = VPackWireFixtureTest.hex(
            "0b 28 01 45 76 61 6c 75 65 0b 1e 02 45 40 74 79" +
                "70 65 4f 54 33 32 5f 30 34 32 30 46 31 24 53 75" +
                "62 43 41 63 31 03 19 03");
private static final byte[] NAMED_TYPE_B = VPackWireFixtureTest.hex(
            "0b 15 02 45 40 74 79 70 65 45 54 79 70 65 42 41 62 28 0d 03 0f");
private static final byte[] NAMED_TYPE_D = VPackWireFixtureTest.hex(
            "0b 14 02 45 40 74 79 70 65 45 54 79 70 65 44 41 64 3c 03 0f");
private static final byte[] NON_NAMED_SUB_C = VPackWireFixtureTest.hex(
            "0b 1e 02 45 40 74 79 70 65 4f 54 33 32 5f 30 34" +
                "32 30 46 31 24 53 75 62 43 41 63 31 03 19");
private static final byte[] ERROR_UNKNOWN_TYPE = VPackWireFixtureTest.hex(
            "0b 0b 01 44 74 79 70 65 41 7a 03");
private static final byte[] ATOMIC_IMPL_X = VPackWireFixtureTest.hex(
            "0b 19 01 45 76 61 6c 75 65 0b 0f 02 44 74 79 70 65 41 78 41 78 33 03 0a 03");
private static final byte[] ISSUE_1125_IMPL = VPackWireFixtureTest.hex(
            "0b 34 01 45 76 61 6c 75 65 0b 2a 04 45 40 74 79" +
                "70 65 53 54 33 32 5f 30 34 32 30 46 31 24 49 6d" +
                "70 6c 31 31 32 35 41 61 31 41 62 32 41 63 33 03" +
                "1d 20 23 03");
private static final byte[] ISSUE_1125_DEFAULT = VPackWireFixtureTest.hex(
            "0b 1b 01 45 76 61 6c 75 65 0b 11 03 41 61 33 41 62 35 43 64 65 66 39 03 06 09 03");
private static final byte[] TYPED_DUMMY_IMPL = VPackWireFixtureTest.hex(
            "06 41 02 74 74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f" +
                "6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f 6e 74" +
                "79 70 65 2e 54 33 32 5f 30 34 32 30 46 31 24 44" +
                "75 6d 6d 79 49 6d 70 6c 0b 07 01 41 78 33 03 03" +
                "38");
private static final byte[] DISALLOWED_CLASS = VPackWireFixtureTest.hex(
            "0b 4a 01 45 76 61 6c 75 65 06 40 02 72 74 6f 6f" +
                "6c 73 2e 6a 61 63 6b 73 6f 6e 2e 64 61 74 61 62" +
                "69 6e 64 2e 6a 73 6f 6e 74 79 70 65 2e 54 33 32" +
                "5f 30 34 32 30 46 31 24 54 68 65 42 6f 6d 62 0b" +
                "08 01 41 61 28 0d 03 03 36 03");

    // Provenance: SealedTypesWithTypedDeserializationTest#testAbstractEmptyBaseClass().
    void testAbstractEmptyBaseClassVpack() throws Exception {
        DummyBase result = MAPPER.readValue(TYPED_DUMMY_IMPL, DummyBase.class);
        DummyImpl impl = assertInstanceOf(DummyImpl.class, result);
        assertEquals(3, impl.x);
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

    void __invoke_testAbstractEmptyBaseClassVpack() throws Exception {
        try {
            testAbstractEmptyBaseClassVpack();
        } finally {
        }
    }

}
