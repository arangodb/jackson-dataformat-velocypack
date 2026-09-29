package tools.jackson.databind.mixins;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0464F1 {
private static final byte[] A_VALUE = VPackWireFixtureTest.hex(
            "14 0b 41 61 45 76 61 6c 75 65 01");
private static final byte[] TWO_VALUES = VPackWireFixtureTest.hex(
            "14 09 41 61 33 41 62 1a 02");
private static final byte[] CREATOR_PROPERTIES = VPackWireFixtureTest.hex(
            "14 17 46 76 61 6c 75 65 30 29 c8 01"
          + "46 76 61 6c 75 65 31 29 15 03 02");
private static final byte[] HASH_CODE = VPackWireFixtureTest.hex(
            "14 0e 48 68 61 73 68 43 6f 64 65 28 0d 01");
private static final byte[] QUESTION = VPackWireFixtureTest.hex("41 3f");
private static final byte[] STRING = VPackWireFixtureTest.hex(
            "46 73 74 72 69 6e 67");
private static final byte[] A = VPackWireFixtureTest.hex("41 61");
private static final ObjectMapper DEFAULT = new VPackMapper();

    // Provenance: TestMixinDeserForCreators#testForConstructor().
    void testForConstructorVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(PrivateConstructorBean.class, PrivateConstructorMixIn.class)
                .build();
        PrivateConstructorBean result = mapper.readValue(QUESTION,
                PrivateConstructorBean.class);
        assertEquals("?...", result.a);
    }

    // Provenance: TestMixinDeserForCreators#testForFactoryAndCtor().
    void testForFactoryAndCtorVpack() throws Exception {
        BaseCreatorBean result = DEFAULT.readValue(STRING, BaseCreatorBean.class);
        assertEquals("string...", result.a);

        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(BaseCreatorBean.class, CreatorMixIn.class)
                .build();
        result = mapper.readValue(STRING, BaseCreatorBean.class);
        assertEquals("stringX", result.a);
    }

    // Provenance: TestMixinDeserForCreators#testFactoryDelegateMixIn().
    void testFactoryDelegateMixInVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(StringWrapper.class, StringWrapperMixIn.class)
                .build();
        StringWrapper result = mapper.readValue(A, StringWrapper.class);
        assertEquals("a", result.value);
    }

    // Provenance: TestMixinDeserForCreators#testFactoryPropertyMixin().
    void testFactoryPropertyMixinVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(PairBean.class, PairMixIn.class)
                .build();
        PairBean result = mapper.readValue(CREATOR_PROPERTIES, PairBean.class);
        assertEquals(456, result.x);
        assertEquals(789, result.y);
    }
static class BaseClass {
        @JsonProperty
        public String a;

        public void setA(String value) { a = "XXX" + value; }
    }
@JsonAutoDetect(setterVisibility = JsonAutoDetect.Visibility.ANY,
            fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class LeafClass extends BaseClass { }
@JsonAutoDetect(setterVisibility = JsonAutoDetect.Visibility.NONE,
            fieldVisibility = JsonAutoDetect.Visibility.NONE)
    interface VisibilityMixIn { }
interface HashCodeMixIn {
        @JsonProperty
        int hashCode();
    }
static class BeanWithoutHashCode { }
static class BeanWithHashCode {
        @Override public int hashCode() { return 13; }
    }
static class PrivateConstructorBean {
        protected String a;
        private PrivateConstructorBean(String value) { a = value + "..."; }
    }
static class PrivateConstructorMixIn {
        @JsonCreator PrivateConstructorMixIn(String value) { }
    }
static class BaseCreatorBean {
        protected String a;

        public BaseCreatorBean(String value) { a = value + "..."; }

        private BaseCreatorBean(String value, boolean factory) { a = value; }

        public static BaseCreatorBean create(String value) {
            return new BaseCreatorBean(value + "X", true);
        }
    }
static class CreatorMixIn {
        @JsonIgnore CreatorMixIn(String value) { }

        @JsonCreator
        static BaseCreatorBean create(String value) { return null; }
    }
static class StringWrapper {
        String value;
        private StringWrapper(String value, boolean ignored) { this.value = value; }
        @SuppressWarnings("unused")
        private static StringWrapper create(String value) {
            return new StringWrapper(value, false);
        }
    }
abstract static class StringWrapperMixIn {
        @JsonCreator static StringWrapper create(String value) { return null; }
    }
static class PairBean {
        final int x, y;

        private PairBean(int x, int y) { this.x = x; this.y = y; }

        static PairBean create(Object x, Object y) {
            return new PairBean(((Number) x).intValue(), ((Number) y).intValue());
        }
    }
static class PairMixIn {
        @JsonCreator
        static PairBean create(@JsonProperty("value0") Object value0,
                @JsonProperty("value1") Object value1) {
            return null;
        }
    }
static class AnySetterBean {
        protected HashMap<String, Object> values = new HashMap<>();
        public AnySetterBean() { }
        protected void addValue(String key, Object value) { values.put(key, value); }
    }
interface AnySetterMixIn {
        @com.fasterxml.jackson.annotation.JsonAnySetter
        void addValue(String key, Object value);
    }

    void __invoke_testForConstructorVpack() throws Exception {
        try {
            testForConstructorVpack();
        } finally {
        }
    }


    void __invoke_testForFactoryAndCtorVpack() throws Exception {
        try {
            testForFactoryAndCtorVpack();
        } finally {
        }
    }


    void __invoke_testFactoryDelegateMixInVpack() throws Exception {
        try {
            testFactoryDelegateMixInVpack();
        } finally {
        }
    }


    void __invoke_testFactoryPropertyMixinVpack() throws Exception {
        try {
            testFactoryPropertyMixinVpack();
        } finally {
        }
    }

}
