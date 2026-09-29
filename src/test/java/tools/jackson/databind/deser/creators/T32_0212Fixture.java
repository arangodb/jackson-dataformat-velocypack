package tools.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0212Fixture {
private static final byte[] CONSTRUCTOR_AND_FACTORY = VPackWireFixtureTest.hex(
            "14 0d 41 61 43 78 79 7a 41 78 28 0c 02");
private static final byte[] CONSTRUCTOR_AND_PROPS = VPackWireFixtureTest.hex(
            "14 0d 41 61 41 31 41 62 32 41 63 1a 03");
private static final byte[] FACTORY_AND_PROPS = VPackWireFixtureTest.hex(
            "14 11 41 61 13 06 19 1a 19 03 41 62 32 41 63 3f 03");
private static final byte[] DEFERRED_CONSTRUCTOR_AND_PROPS = VPackWireFixtureTest.hex(
            "14 20 45 70 72 6f 70 42 43 2e 2e 2e 47 63 72 65 61 74 65 41 "
          + "13 04 31 01 45 70 72 6f 70 41 18 03");
private static final byte[] DEFERRED_FACTORY_AND_PROPS = VPackWireFixtureTest.hex(
            "14 11 44 70 72 6f 70 41 31 44 63 74 6f 72 41 32 02");
private static final byte[] FACTORY_RENAMED = VPackWireFixtureTest.hex(
            "14 12 45 6d 69 78 65 64 1b 00 00 00 00 00 80 34 40 01");
private static final byte[] MAP_WITH_CONSTRUCTOR = VPackWireFixtureTest.hex(
            "14 22 44 74 65 78 74 43 61 62 63 45 65 6e 74 72 79 1a "
          + "46 6e 75 6d 62 65 72 28 7b 42 78 79 42 79 78 04");
private static final byte[] MAP_WITH_FACTORY = VPackWireFixtureTest.hex(
            "14 0c 41 78 43 2e 2e 2e 41 62 1a 02");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: TestCreators#testConstructorAndFactoryCreator.
    void testConstructorAndFactoryCreator() throws Exception {
        CreatorBeanWithBoth bean = MAPPER.readValue(CONSTRUCTOR_AND_FACTORY,
                CreatorBeanWithBoth.class);
        assertEquals(13, bean.x);
        assertEquals("ctor:xyz", bean.a);
    }

    // Provenance: TestCreators#testConstructorAndProps.
    void testConstructorAndProps() throws Exception {
        ConstructorAndPropsBean bean = MAPPER.readValue(CONSTRUCTOR_AND_PROPS,
                ConstructorAndPropsBean.class);
        assertEquals(1, bean.a);
        assertEquals(2, bean.b);
        assertTrue(bean.c);
    }

    // Provenance: TestCreators#testDeferredConstructorAndProps.
    void testDeferredConstructorAndProps() throws Exception {
        DeferredConstructorAndPropsBean bean = MAPPER.readValue(
                DEFERRED_CONSTRUCTOR_AND_PROPS, DeferredConstructorAndPropsBean.class);
        assertEquals("...", bean.propB);
        assertNull(bean.propA);
        assertNotNull(bean.createA);
        assertEquals(1, bean.createA.length);
        assertEquals(1, bean.createA[0]);
    }

    // Provenance: TestCreators#testDeferredFactoryAndProps.
    void testDeferredFactoryAndProps() throws Exception {
        DeferredFactoryAndPropsBean bean = MAPPER.readValue(
                DEFERRED_FACTORY_AND_PROPS, DeferredFactoryAndPropsBean.class);
        assertEquals("1", bean.prop);
        assertEquals("2", bean.ctor);
    }

    // Provenance: TestCreators#testFactoryAndProps.
    void testFactoryAndProps() throws Exception {
        FactoryAndPropsBean bean = MAPPER.readValue(FACTORY_AND_PROPS,
                FactoryAndPropsBean.class);
        assertEquals(2, bean.arg2);
        assertEquals(-1, bean.arg3);
        assertNotNull(bean.arg1);
        assertEquals(3, bean.arg1.length);
        assertFalse(bean.arg1[0]);
        assertTrue(bean.arg1[1]);
        assertFalse(bean.arg1[2]);
    }

    // Provenance: TestCreators#testFactoryCreatorWithMixin.
    void testFactoryCreatorWithMixin() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(CreatorBeanWithBoth.class, MixIn.class)
                .build();
        CreatorBeanWithBoth bean = mapper.readValue(CONSTRUCTOR_AND_FACTORY,
                CreatorBeanWithBoth.class);
        assertEquals(11, bean.x);
        assertEquals("factory:xyz", bean.a);
    }

    // Provenance: TestCreators#testFactoryCreatorWithRenamingMixin.
    void testFactoryCreatorWithRenamingMixin() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(FactoryBean.class, FactoryBeanMixIn.class)
                .build();
        FactoryBean bean = mapper.readValue(FACTORY_RENAMED, FactoryBean.class);
        assertEquals(20.5, bean.d);
    }

    // Provenance: TestCreators#testLongFactory.
    void testLongFactory() throws Exception {
        LongFactoryBean bean = MAPPER.readValue(
                VPackWireFixtureTest.hex("2c 08 1a 99 be 1c"),
                LongFactoryBean.class);
        assertEquals(123456789000L, bean.value);
    }

    // Provenance: TestCreators#testMapWithConstructor.
    void testMapWithConstructor() throws Exception {
        MapWithCtor result = MAPPER.readValue(MAP_WITH_CONSTRUCTOR, MapWithCtor.class);
        assertEquals(Boolean.TRUE, result.get("entry"));
        assertEquals("yx", result.get("xy"));
        assertEquals(2, result.size());
        assertEquals("abc", result._text);
        assertEquals(123, result._number);
    }

    // Provenance: TestCreators#testMapWithFactory.
    void testMapWithFactory() throws Exception {
        MapWithFactory result = MAPPER.readValue(MAP_WITH_FACTORY, MapWithFactory.class);
        assertEquals("...", result.get("x"));
        assertEquals(1, result.size());
        assertEquals(Boolean.TRUE, result._b);
    }

    // Provenance: TestCreators#testMultipleCreators.
    void testMultipleCreators() throws Exception {
        MultiBean bean = MAPPER.readValue(VPackWireFixtureTest.hex("28 7b"), MultiBean.class);
        assertEquals(Integer.valueOf(123), bean.value);
        bean = MAPPER.readValue(VPackWireFixtureTest.hex("43 61 62 63"), MultiBean.class);
        assertEquals("abc", bean.value);
        bean = MAPPER.readValue(VPackWireFixtureTest.hex(
                "1b 00 00 00 00 00 00 d0 3f"), MultiBean.class);
        assertEquals(Double.valueOf(0.25), bean.value);
    }
static class CreatorBeanWithBoth {
        String a;
        int x;

        @JsonCreator
        protected CreatorBeanWithBoth(@JsonProperty("a") String paramA,
                @JsonProperty("x") int paramX) {
            a = "ctor:" + paramA;
            x = 1 + paramX;
        }

        private CreatorBeanWithBoth(String a, int x, boolean dummy) {
            this.a = a;
            this.x = x;
        }

        @JsonCreator
        public static CreatorBeanWithBoth bobTheBuilder(@JsonProperty("a") String paramA,
                @JsonProperty("x") int paramX) {
            return new CreatorBeanWithBoth("factory:" + paramA, paramX - 1, false);
        }
    }
abstract static class MixIn {
        @JsonIgnore
        private MixIn(String a, int x) { }
    }
static class FactoryBean {
        double d;

        private FactoryBean(double value, boolean dummy) { d = value; }

        @JsonCreator
        protected static FactoryBean createIt(@JsonProperty("f") double value) {
            return new FactoryBean(value, true);
        }
    }
static class FactoryBeanMixIn {
        static FactoryBean createIt(@JsonProperty("mixed") double xyz) { return null; }
    }
static class LongFactoryBean {
        long value;

        private LongFactoryBean(long v) { value = v; }

        @JsonCreator
        static protected LongFactoryBean valueOf(long v) {
            return new LongFactoryBean(v);
        }
    }
static class MultiBean {
        Object value;

        @JsonCreator public MultiBean(int v) { value = v; }
        @JsonCreator public MultiBean(double v) { value = v; }
        @JsonCreator public MultiBean(String v) { value = v; }
    }
static class ConstructorAndPropsBean {
        final int a, b;
        boolean c;

        @JsonCreator
        protected ConstructorAndPropsBean(@JsonProperty("a") int a,
                @JsonProperty("b") int b) {
            this.a = a;
            this.b = b;
        }

        public void setC(boolean value) { c = value; }
    }
static class FactoryAndPropsBean {
        boolean[] arg1;
        int arg2, arg3;

        @JsonCreator
        protected FactoryAndPropsBean(@JsonProperty("a") boolean[] arg) { arg1 = arg; }

        public void setB(int value) { arg2 = value; }
        public void setC(int value) { arg3 = value; }
    }
static class DeferredConstructorAndPropsBean {
        final int[] createA;
        String propA = "xyz";
        String propB;

        @JsonCreator
        public DeferredConstructorAndPropsBean(@JsonProperty("createA") int[] a) {
            createA = a;
        }

        public void setPropA(String a) { propA = a; }
        public void setPropB(String b) { propB = b; }
    }
static class DeferredFactoryAndPropsBean {
        String prop, ctor;

        @JsonCreator
        DeferredFactoryAndPropsBean(@JsonProperty("ctor") String str) { ctor = str; }

        public void setProp(String str) { prop = str; }
    }
@SuppressWarnings("serial")
    static class MapWithCtor extends java.util.HashMap<Object, Object> {
        final int _number;
        String _text = "initial";

        MapWithCtor() { this(-1, "default"); }

        @JsonCreator
        public MapWithCtor(@JsonProperty("number") int nr,
                @JsonProperty("text") String t) {
            _number = nr;
            _text = t;
        }
    }
@SuppressWarnings("serial")
    static class MapWithFactory extends java.util.TreeMap<Object, Object> {
        Boolean _b;

        private MapWithFactory(Boolean b) { _b = b; }

        @JsonCreator
        static MapWithFactory createIt(@JsonProperty("b") Boolean b) {
            return new MapWithFactory(b);
        }
    }

    void __invoke_testConstructorAndFactoryCreator() throws Exception {
        try {
            testConstructorAndFactoryCreator();
        } finally {
        }
    }


    void __invoke_testConstructorAndProps() throws Exception {
        try {
            testConstructorAndProps();
        } finally {
        }
    }


    void __invoke_testDeferredConstructorAndProps() throws Exception {
        try {
            testDeferredConstructorAndProps();
        } finally {
        }
    }


    void __invoke_testDeferredFactoryAndProps() throws Exception {
        try {
            testDeferredFactoryAndProps();
        } finally {
        }
    }


    void __invoke_testFactoryAndProps() throws Exception {
        try {
            testFactoryAndProps();
        } finally {
        }
    }


    void __invoke_testFactoryCreatorWithMixin() throws Exception {
        try {
            testFactoryCreatorWithMixin();
        } finally {
        }
    }


    void __invoke_testFactoryCreatorWithRenamingMixin() throws Exception {
        try {
            testFactoryCreatorWithRenamingMixin();
        } finally {
        }
    }


    void __invoke_testLongFactory() throws Exception {
        try {
            testLongFactory();
        } finally {
        }
    }


    void __invoke_testMapWithConstructor() throws Exception {
        try {
            testMapWithConstructor();
        } finally {
        }
    }


    void __invoke_testMapWithFactory() throws Exception {
        try {
            testMapWithFactory();
        } finally {
        }
    }


    void __invoke_testMultipleCreators() throws Exception {
        try {
            testMultipleCreators();
        } finally {
        }
    }

}
