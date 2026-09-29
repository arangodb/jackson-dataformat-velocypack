package tools.jackson.databind.deser.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.introspect.NopAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0193Fixture {
private static final byte[] SIMPLE = VPackWireFixtureTest.hex(
            "14 09 41 78 31 41 79 32 02");
private static final byte[] SIMPLE_WRAPPER = VPackWireFixtureTest.hex(
            "14 1c 42 78 79 14 16 41 78 31 43 66 6f 6f 33 41 79 32 "
          + "43 62 61 72 43 61 62 63 04 01");
private static final byte[] SIMPLE_WITH_UNKNOWN = VPackWireFixtureTest.hex(
            "14 0c 41 78 31 41 79 32 41 7a 34 03");
private static final byte[] MULTI_ACCESS = VPackWireFixtureTest.hex(
            "14 0d 41 63 33 41 61 32 41 62 20 f7 03");
private static final byte[] MULTI_ACCESS_WITH_IGNORED = VPackWireFixtureTest.hex(
            "14 0d 41 63 33 41 64 35 41 62 20 f7 03");
private static final byte[] VALUE_13 = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 28 0d 01");
private static final byte[] VALUE_1 = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 31 01");
private static final byte[] X_1 = VPackWireFixtureTest.hex(
            "14 06 41 78 31 01");
private static final byte[] X_3 = VPackWireFixtureTest.hex(
            "14 06 41 78 33 01");
private static final byte[] ANY_SETTER = VPackWireFixtureTest.hex(
            "14 1e 45 65 78 74 72 61 33 46 66 6f 6f 62 61 72 01 41 78 31 "
          + "44 6e 61 6d 65 43 62 6f 62 04");
private final ObjectMapper mapper = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

    void testSimple() throws Exception {
        Object value = mapper.readValue(SIMPLE, ValueClassXY.class);
        assertInstanceOf(ValueClassXY.class, value);
        ValueClassXY xy = (ValueClassXY) value;
        assertEquals(2, xy.x);
        assertEquals(3, xy.y);
    }

    void testSimpleWrapper() throws Exception {
        ValueClassXYWrapper wrapper = mapper.readValue(SIMPLE_WRAPPER,
                ValueClassXYWrapper.class);
        assertEquals(2, wrapper.xy.x);
        assertEquals(3, wrapper.xy.y);
    }

    void testSimpleWithIgnores() throws Exception {
        UnrecognizedPropertyException exception = assertThrows(
                UnrecognizedPropertyException.class,
                () -> mapper.readValue(SIMPLE_WITH_UNKNOWN, ValueClassXY.class));
        assertEquals("z", exception.getPropertyName());

        ObjectMapper ignorantMapper = VPackMapper.builder()
                .withConfigOverride(SimpleBuilderXY.class,
                        override -> override.setIgnorals(
                                JsonIgnoreProperties.Value.forIgnoreUnknown(true)))
                .build();
        ValueClassXY xy = ignorantMapper.readValue(SIMPLE_WITH_UNKNOWN,
                ValueClassXY.class);
        assertEquals(2, xy.x);
        assertEquals(3, xy.y);
    }

    void testMultiAccess() throws Exception {
        ValueClassABC value = mapper.readValue(MULTI_ACCESS, ValueClassABC.class);
        assertNotNull(value);
        assertEquals(2, value.a);
        assertEquals(-9, value.b);
        assertEquals(3, value.c);

        value = mapper.readValue(MULTI_ACCESS_WITH_IGNORED, ValueClassABC.class);
        assertNotNull(value);
        assertEquals(0, value.a);
        assertEquals(-9, value.b);
        assertEquals(3, value.c);
    }

    void testImmutable() throws Exception {
        ValueImmutable value = mapper.readValue(VALUE_13, ValueImmutable.class);
        assertEquals(13, value.value);
    }

    void testCustomWith() throws Exception {
        ValueFoo value = mapper.readValue(VALUE_1, ValueFoo.class);
        assertEquals(1, value.value);
    }

    void testBuilderMethodReturnMoreGeneral() throws Exception {
        ValueInterface value = mapper.readValue(X_1, ValueInterface.class);
        assertEquals(2, value.getX());
    }

    void testBuilderMethodReturnMoreSpecific() throws Exception {
        ValueInterface2 value = mapper.readValue(X_1, ValueInterface2.class);
        assertEquals(2, value.getX());
    }

    void testSelfBuilder777() throws Exception {
        SelfBuilder777 result = mapper.readValue(X_3, SelfBuilder777.class);
        assertNotNull(result);
        assertEquals(3, result.x);
    }

    void testWithAnySetter822() throws Exception {
        ValueClass822 value = mapper.readValue(ANY_SETTER, ValueClass822.class);
        assertEquals(1, value.x);
        assertNotNull(value.stuff);
        assertEquals(3, value.stuff.size());
        assertEquals(Integer.valueOf(3), value.stuff.get("extra"));
        assertEquals("bob", value.stuff.get("name"));
        Object object = value.stuff.get("foobar");
        assertNotNull(object);
        assertInstanceOf(List.class, object);
        assertEquals(List.of(), object);
    }

    void testPOJOConfigResolution1557() throws Exception {
        ObjectMapper configured = VPackMapper.builder()
                .addModule(new NopModule1557()).build();
        ValueFoo value = configured.readValue(VALUE_1, ValueFoo.class);
        assertEquals(1, value.value);
    }

    void testPrivateInnerBuilder() throws Exception {
        Value2354 result = mapper.readValue(VALUE_13, Value2354.class);
        assertEquals(13, result.value());
    }
@JsonDeserialize(builder = SimpleBuilderXY.class)
    static class ValueClassXY {
        final int x;
        final int y;

        protected ValueClassXY(int x, int y) {
            this.x = x + 1;
            this.y = y + 1;
        }
    }
static class ValueClassXYWrapper {
        @JsonIgnoreProperties(ignoreUnknown = true, value = { "abc", "def" })
        public ValueClassXY xy;
    }
static class SimpleBuilderXY {
        public int x;
        public int y;

        public SimpleBuilderXY withX(int value) {
            x = value;
            return this;
        }

        public SimpleBuilderXY withY(int value) {
            y = value;
            return this;
        }

        public ValueClassXY build() {
            return new ValueClassXY(x, y);
        }
    }
@JsonDeserialize(builder = BuildABC.class)
    static class ValueClassABC {
        final int a;
        final int b;
        final int c;

        protected ValueClassABC(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
@JsonIgnoreProperties({ "d" })
    static class BuildABC {
        public int a;
        private int b;
        private int c;

        @JsonProperty("b")
        public BuildABC assignB(int value) {
            b = value;
            return this;
        }

        @JsonSetter("c")
        public void c(int value) {
            c = value;
        }

        public ValueClassABC build() {
            return new ValueClassABC(a, b, c);
        }
    }
@JsonDeserialize(builder = BuildImmutable.class)
    static class ValueImmutable {
        final int value;

        protected ValueImmutable(int value) {
            this.value = value;
        }
    }
static class BuildImmutable {
        private final int value;

        private BuildImmutable() {
            this(0);
        }

        private BuildImmutable(int value) {
            this.value = value;
        }

        public BuildImmutable withValue(int value) {
            return new BuildImmutable(value);
        }

        public ValueImmutable build() {
            return new ValueImmutable(value);
        }
    }
@JsonDeserialize(builder = BuildFoo.class)
    static class ValueFoo {
        final int value;

        protected ValueFoo(int value) {
            this.value = value;
        }
    }
@JsonPOJOBuilder(withPrefix = "foo", buildMethodName = "construct")
    static class BuildFoo {
        private int value;

        public BuildFoo fooValue(int value) {
            this.value = value;
            return this;
        }

        public ValueFoo construct() {
            return new ValueFoo(value);
        }
    }
@JsonDeserialize(builder = ValueInterfaceBuilder.class)
    interface ValueInterface {
        int getX();
    }
@JsonDeserialize(builder = ValueInterface2Builder.class)
    interface ValueInterface2 {
        int getX();
    }
static class ValueInterfaceImpl implements ValueInterface {
        final int x;

        protected ValueInterfaceImpl(int value) {
            x = value + 1;
        }

        @Override
        public int getX() {
            return x;
        }
    }
static class ValueInterface2Impl implements ValueInterface2 {
        final int x;

        protected ValueInterface2Impl(int value) {
            x = value + 1;
        }

        @Override
        public int getX() {
            return x;
        }
    }
static class ValueInterfaceBuilder {
        public int x;

        public ValueInterfaceBuilder withX(int value) {
            x = value;
            return this;
        }

        public ValueInterface build() {
            return new ValueInterfaceImpl(x);
        }
    }
static class ValueInterface2Builder {
        public int x;

        public ValueInterface2Builder withX(int value) {
            x = value;
            return this;
        }

        public ValueInterface2Impl build() {
            return new ValueInterface2Impl(x);
        }
    }
@JsonDeserialize(builder = SelfBuilder777.class)
    @JsonPOJOBuilder(buildMethodName = "", withPrefix = "with")
    static class SelfBuilder777 {
        public int x;

        public SelfBuilder777 withX(int value) {
            x = value;
            return this;
        }
    }
@JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    static class ValueBuilder822 {
        public int x;
        private Map<String, Object> stuff = new HashMap<>();

        @JsonCreator
        public ValueBuilder822(@JsonProperty("x") int value) {
            x = value;
        }

        @JsonAnySetter
        public void addStuff(String key, Object value) {
            stuff.put(key, value);
        }

        public ValueClass822 build() {
            return new ValueClass822(x, stuff);
        }
    }
@JsonDeserialize(builder = ValueBuilder822.class)
    static class ValueClass822 {
        public int x;
        public Map<String, Object> stuff;

        public ValueClass822(int x, Map<String, Object> stuff) {
            this.x = x;
            this.stuff = stuff;
        }
    }
static class NopModule1557 extends JacksonModule {
        @Override
        public String getModuleName() {
            return "NopModule";
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public void setupModule(SetupContext context) {
            context.insertAnnotationIntrospector(new NopAnnotationIntrospector() {
                private static final long serialVersionUID = 1L;

                @Override
                public Version version() {
                    return Version.unknownVersion();
                }
            });
        }
    }
@JsonDeserialize(builder = Value2354.Value2354Builder.class)
    static class Value2354 {
        private final int value;

        protected Value2354(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        private static class Value2354Builder {
            private int value;

            public Value2354Builder withValue(int value) {
                this.value = value;
                return this;
            }

            private Value2354 build() {
                return new Value2354(value);
            }
        }
    }

    void __invoke_testSimple() throws Exception {
        try {
            testSimple();
        } finally {
        }
    }


    void __invoke_testSimpleWrapper() throws Exception {
        try {
            testSimpleWrapper();
        } finally {
        }
    }


    void __invoke_testSimpleWithIgnores() throws Exception {
        try {
            testSimpleWithIgnores();
        } finally {
        }
    }


    void __invoke_testMultiAccess() throws Exception {
        try {
            testMultiAccess();
        } finally {
        }
    }


    void __invoke_testImmutable() throws Exception {
        try {
            testImmutable();
        } finally {
        }
    }


    void __invoke_testCustomWith() throws Exception {
        try {
            testCustomWith();
        } finally {
        }
    }


    void __invoke_testBuilderMethodReturnMoreGeneral() throws Exception {
        try {
            testBuilderMethodReturnMoreGeneral();
        } finally {
        }
    }


    void __invoke_testBuilderMethodReturnMoreSpecific() throws Exception {
        try {
            testBuilderMethodReturnMoreSpecific();
        } finally {
        }
    }


    void __invoke_testSelfBuilder777() throws Exception {
        try {
            testSelfBuilder777();
        } finally {
        }
    }


    void __invoke_testWithAnySetter822() throws Exception {
        try {
            testWithAnySetter822();
        } finally {
        }
    }


    void __invoke_testPOJOConfigResolution1557() throws Exception {
        try {
            testPOJOConfigResolution1557();
        } finally {
        }
    }


    void __invoke_testPrivateInnerBuilder() throws Exception {
        try {
            testPrivateInnerBuilder();
        } finally {
        }
    }

}
