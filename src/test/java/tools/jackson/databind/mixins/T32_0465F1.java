package tools.jackson.databind.mixins;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0465F1 {
private static final byte[] FIELD_INHERITANCE = VPackWireFixtureTest.hex(
            "14 11 42 69 64 28 2a 44 6e 61 6d 65 43 42 6f 62 02");
private static final byte[] METHOD_INHERITANCE = VPackWireFixtureTest.hex(
            "14 12 42 69 64 28 0d 44 6e 61 6d 65 44 42 69 6c 6c 02");
private static final byte[] CITY = VPackWireFixtureTest.hex(
            "14 10 44 63 69 74 79 47 53 65 61 74 74 6c 65 01");
private static final byte[] A_ABC = VPackWireFixtureTest.hex(
            "14 09 41 61 43 61 62 63 01");
private static final byte[] A_ABC_C_C = VPackWireFixtureTest.hex(
            "14 0d 41 61 43 61 62 63 41 63 41 63 02");
private static final byte[] A_XYZ_C_C2 = VPackWireFixtureTest.hex(
            "14 0e 41 61 43 78 79 7a 41 63 42 63 32 02");
private static final byte[] C_C2 = VPackWireFixtureTest.hex(
            "14 08 41 63 42 63 32 01");
private static final byte[] A_42 = VPackWireFixtureTest.hex(
            "14 07 41 61 28 2a 01");
private static final byte[] B_42 = VPackWireFixtureTest.hex(
            "14 07 41 62 28 2a 01");
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: TestMixinMerging#testDisappearingMixins515().
    void testDisappearingMixins515Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(tools.jackson.databind.MapperFeature.INFER_PROPERTY_MUTATORS)
                .disable(tools.jackson.databind.MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
                .changeDefaultVisibility(vc -> vc
                        .withVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.FIELD,
                                JsonAutoDetect.Visibility.NONE)
                        .withVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.GETTER,
                                JsonAutoDetect.Visibility.NONE)
                        .withVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.IS_GETTER,
                                JsonAutoDetect.Visibility.NONE))
                .addMixIn(Person.class, PersonMixin.class)
                .build();

        assertEquals(MAPPER.readTree(CITY),
                MAPPER.readTree(mapper.writeValueAsBytes(new PersonImpl())));
    }
static class Beano {
        public int ido = 42;
        public String nameo = "Bob";
    }
static class BeanoMixinSuper {
        @JsonProperty("name")
        public String nameo;
    }
static class BeanoMixinSub extends BeanoMixinSuper {
        @JsonProperty("id")
        public int ido;
    }
static class Beano2 {
        public int getIdo() { return 13; }
        public String getNameo() { return "Bill"; }
    }
static abstract class BeanoMixinSuper2 extends Beano2 {
        @Override
        @JsonProperty("name")
        public abstract String getNameo();
    }
static abstract class BeanoMixinSub2 extends BeanoMixinSuper2 {
        @Override
        @JsonProperty("id")
        public abstract int getIdo();
    }
interface Contact { String getCity(); }
static class ContactImpl implements Contact {
        @Override public String getCity() { return "Seattle"; }
    }
static class ContactMixin implements Contact {
        @Override
        @JsonProperty
        public String getCity() { return null; }
    }
interface Person extends Contact { }
static class PersonImpl extends ContactImpl implements Person { }
static class PersonMixin extends ContactMixin implements Person { }
@JsonInclude(JsonInclude.Include.ALWAYS)
    static class BaseClass {
        protected String _a, _b;
        protected String _c = "c";

        protected BaseClass() { }
        public BaseClass(String a) { _a = a; }
        public String getA() { return _a; }
        @JsonProperty public String getB() { return _b; }
        @JsonProperty public String getC() { return _c; }
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class LeafClass extends BaseClass {
        public LeafClass() { super(null); }
        public LeafClass(String a) { super(a); }
    }
@JsonInclude(JsonInclude.Include.NON_NULL)
    interface NonNullMixIn { }
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            fieldVisibility = JsonAutoDetect.Visibility.NONE)
    interface NoAutoDetectMixIn { }
static class Bean3035 {
        public int getA() { return 42; }
    }
static class Rename3035MixIn extends Bean3035 {
        @JsonProperty("b")
        @Override public int getA() { return super.getA(); }
    }

    void __invoke_testDisappearingMixins515Vpack() throws Exception {
        try {
            testDisappearingMixins515Vpack();
        } finally {
        }
    }

}
